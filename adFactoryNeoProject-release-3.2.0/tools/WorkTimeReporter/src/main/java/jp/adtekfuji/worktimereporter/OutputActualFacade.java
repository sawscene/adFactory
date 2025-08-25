/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimereporter;

import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import adtekfuji.utility.DirectoryUtils;
import adtekfuji.utility.FileUtils;
import adtekfuji.utility.NetworkFileUtil;
import adtekfuji.utility.StringUtils;
import io.vavr.control.Either;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.worktimereporter.mail.MailProperty;
import jp.adtekfuji.worktimereporter.mail.MailUtils;
import jp.adtekfuji.worktimereporter.service.ActualResultInfoFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class OutputActualFacade {
    public static final String TMP_READ_FILE_NAME_LIST_FILE_PREFIX = "TmpReadFileNameList_";
    public static final String READ_FILE_NAME_LIST_FILE_PREFIX = "ReadFileNameList_";

    public enum SEARCH_TYPE {

        INTERVAL_TIME,
        FROM_TO_SEARCH;
    }

    private enum OutputActualTaskEnum {
        INIT,
        DELETE_FILE,// ファイル削除
        GET_ACTUAL_DATA,// 実績データ取得
        ACTUAL_DATA_PROC,// 実績データ処理
        CREATE_FILE,// ファイル作成
        FILE_UPLOAD;// ファイルアップロード
    }

    private final static Logger logger = LogManager.getLogger();
    private final static String TEMP_PATH = "temp";
    public final static String WORK_CENTER = "ワークセンター";
    public final static String ORDER_NUMBER = "指図番号";
    public final static String KOUSU_COLLAB = "工数連携";
    public final static String KOUSU_COLLAB_YES = "YES";
    private final static String KOUSU_COLLAB_NO = "NO";
    private final static String LINE_SEPARATOR = "\r\n";
    private final static String ORGANIZATION_SUFFIX = "0101";
    private final static String FILE_EXT_CSV = ".csv";// 出力ファイルの拡張子
    private final static String HIERARCHY = "(カンバン階層 / カンバン / 工程順階層 / 工程順 / 工程 / 組織階層)";

    private final OutputActualInfo info;
    private final Map<String, Writer> writers = new HashMap<>();
    private final List<String> files = new ArrayList<>();
    private final List<String> filesUplodeFail = new ArrayList<>();

    final BreaktimeInfoFacade breaktimeFacade;
    final OrganizationInfoFacade organizationFacade;
    final ActualResultInfoFacade actualResultInfoFacade;

    /**
     * コンストラクタ
     *
     * @param info 実績出力設定情報
     */
    public OutputActualFacade(OutputActualInfo info) {
        this.info = info;
        StringBuilder sb = new StringBuilder();
        sb.append(info.getAdFactoryAddress());
        sb.append("/adFactoryServer/rest");
        breaktimeFacade = new BreaktimeInfoFacade(sb.toString());           // 休憩時間REST.
        organizationFacade = new OrganizationInfoFacade(sb.toString());  // 作業者REST.
        actualResultInfoFacade = new ActualResultInfoFacade(info.getAdFactoryAddress());
    }

    /**
     * 工数実績ファイルを作成して、共有フォルダーにアップロードする。
     *
     * @param searchType
     * @return 結果 (true:成功, false:失敗)
     */
    public int output(SEARCH_TYPE searchType) {
        int result = -1;
        Date fromDate = null;
        Date toDate = null;
        OutputActualTaskEnum nowTask = OutputActualTaskEnum.INIT;
        try {
            switch (searchType) {
                case INTERVAL_TIME:
                    Calendar from = Calendar.getInstance();
                    from.add(Calendar.HOUR, -info.getUptakeInterval());
                    Calendar to = Calendar.getInstance();
                    fromDate = from.getTime();
                    toDate = to.getTime();
                    break;
                case FROM_TO_SEARCH:
                    fromDate = info.getFromSearchDatetime();
                    toDate = info.getToSearchDatetime();
                    break;
                default:
                    return result;
            }

            logger.info("Starting OutputActualFacade::output : {}", searchType);
            logger.info("Intake period：" + fromDate + " ～ " + toDate);

            List<ReportOutInfoEntity> actuals = new ArrayList<>();

            // 工数連携対象の実績出力情報一覧を取得する。
            nowTask = OutputActualTaskEnum.GET_ACTUAL_DATA;
            List<ReportOutInfoEntity> directActuals = getActualData(searchType, fromDate, toDate);
            if (Objects.nonNull(directActuals)) {
                actuals.addAll(directActuals);
            }

            if (actuals.isEmpty()) {
                result = 0;
                return result;
            }

            // 重複時間の削除.
            nowTask = OutputActualTaskEnum.ACTUAL_DATA_PROC;
            actuals = this.deleteOverlapTime(actuals);

            // データをグループ化する
            actuals = this.groupActuals(actuals);

            // 工数0分のデータ削除
            actuals = this.removeTimeWorkingZeroMinute(actuals);

            try {
                // tempフォルダを削除して再作成する
                if (!DirectoryUtils.recreateDirectories(TEMP_PATH)) {
                    throw new IOException("temp folder create fail");
                }
                logger.info("Created new temp directory");

                //実績データ書き込み
                nowTask = OutputActualTaskEnum.CREATE_FILE;
                for (ReportOutInfoEntity actual : actuals) {
                    String workCenter = actual.getPropertyValue(WORK_CENTER).get();
                    Writer writer = getWriter(workCenter);
                    writeActualData(writer, actual);
                }

            } finally {
                //クローズ.
                for (Map.Entry<String, Writer> e : writers.entrySet()) {
                    Writer writer = e.getValue();
                    writer.flush();
                    writer.close();
                }
            }

            // 工数実績ファイルをフォルダーにアップロードする。
            nowTask = OutputActualTaskEnum.FILE_UPLOAD;
            result = this.uploadFiles();
            logger.info("comp actual data output");

            // tempフォルダのcsvファイルを削除する。
            nowTask = OutputActualTaskEnum.DELETE_FILE;
            this.cleanupTempFolder();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // エラー発生通知メールを送信する。
            this.sendErrorMail(fromDate, toDate, nowTask);
        }
        return result;
    }

    /**
     *
     * @param actuals
     * @return
     */
    private List<ReportOutInfoEntity> deleteOverlapTime(List<ReportOutInfoEntity> actuals) {

        // 組織識別名毎に実績を振り分け
        Map<String, List<ReportOutInfoEntity>> actualEachWorkers = new HashMap<>();
        List<ReportOutInfoEntity> tempActual;
        for (ReportOutInfoEntity actual : actuals) {
            String organizationIdentName = actual.getOrganizationIdentName();
            tempActual = actualEachWorkers.get(organizationIdentName);
            if (Objects.isNull(tempActual)) {
                tempActual = new ArrayList<>();
            }
            tempActual.add(actual);
            actualEachWorkers.put(organizationIdentName, tempActual);
        }

        Map<Long, ReportOutInfoEntity> workingActuals = new HashMap<>();                        // 作業中の実績.
        Date lastDatetime = new Date();                                                         // 直前の実績日時.
        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();                      // 休憩時間リスト.
        long diffTime = 0;                                                                      // 前の実績からの時間.
        int workingTime;

        // 作業者
        for (Map.Entry<String, List<ReportOutInfoEntity>> entry : actualEachWorkers.entrySet()) {

            // 作業者の休憩時間リスト作成.
            breaktimeCollection.clear();
            try {
                OrganizationInfoEntity worker = organizationFacade.findName(URLEncoder.encode(entry.getKey(), "UTF-8"));
                if (Objects.nonNull(worker.getBreakTimeInfoCollection())) {
                    worker.getBreakTimeInfoCollection().stream().forEach((breaktimeId) -> {
                        breaktimeCollection.add(breaktimeFacade.find(breaktimeId));
                    });
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }

            workingActuals.clear();
            // 実施時間毎
            for (ReportOutInfoEntity actual : entry.getValue().stream().sorted(Comparator.comparing(a -> a.getImplementDatetime())).collect(toList())) {

                // 作業中の実績がある場合は、時間を作業している数で割ってプラスする.
                if (!workingActuals.isEmpty()) {

                    for (Map.Entry<Long, ReportOutInfoEntity> workingEntry : workingActuals.entrySet()) {
                        diffTime = BreaktimeUtil.getDiffTime(breaktimeCollection, lastDatetime, actual.getImplementDatetime());
                        workingTime = Objects.nonNull(workingEntry.getValue().getWorkingTime()) ? workingEntry.getValue().getWorkingTime() : 0;
                        workingEntry.getValue().setWorkingTime((int) (workingTime + (diffTime / workingActuals.size())));
                    }
                }

                // 完了ステータスリスト
                List<KanbanStatusEnum> endStatusList = Arrays.asList(KanbanStatusEnum.COMPLETION, KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.SUSPEND);

                if (actual.getActualStatus() == KanbanStatusEnum.WORKING) {
                    if (isDirectActual(actual)) {
                        // 開始実績の場合、対になる実績(完了, 中断, 中止)がない場合、「完了なし」を出力するために作業時間をnullにしておく。
                        long pairCount = entry.getValue().stream().filter(a -> isDirectActual(a)
                                && !a.getActualId().equals(actual.getActualId())
                                && a.getFkWorkKanbanId().equals(actual.getFkWorkKanbanId())
                                && (a.getImplementDatetime().equals(actual.getImplementDatetime())
                                || a.getImplementDatetime().after(actual.getImplementDatetime()))
                                && endStatusList.contains(a.getActualStatus())).count();
                        if (pairCount == 0) {
                            actual.setWorkingTime(0);
                        }
                    }

                    workingActuals.put(actual.getFkWorkKanbanId(), actual);
                } else if (endStatusList.contains(actual.getActualStatus())) {
                    workingActuals.remove(actual.getFkWorkKanbanId());
                    actual.setWorkingTime(0);   // adFactoryの実績は完了、中断の実績に作業時間が付いているが、作業開始の実績に記録するようにしたので消す.
                } else {
                    continue;
                }

                lastDatetime = actual.getImplementDatetime();
            }
        }
        List<ReportOutInfoEntity> retActuals = new ArrayList<>();
        actualEachWorkers.entrySet().stream().forEach(entry -> {
            entry.getValue().stream().filter((actual -> {
                return (Objects.isNull(actual.getWorkingTime()) || actual.getWorkingTime() > 0);
            })).forEach(actual -> {
                retActuals.add(actual);
            });
        });
        return retActuals;
    }

    /**
     * 指図番号、社員番号、ワークセンターでグループ化
     *
     * @param actuals 実績データ
     * @return グループ化された実績データ
     */
    public List<ReportOutInfoEntity> groupActuals(List<ReportOutInfoEntity> actuals) {

        List<ReportOutInfoEntity> tempActuals = new ArrayList<>(actuals);
        return new ArrayList<>(
                tempActuals
                        .stream()
                        .collect(Collectors.groupingBy(
                                entity -> String.join(",",
                                        entity.getPropertyValue(ORDER_NUMBER).get(),
                                        entity.getOrganizationIdentName(),
                                        entity.getPropertyValue(WORK_CENTER).get()
                                ),
                                Collectors.reducing(
                                        (entity1, entity2) -> {
                                            entity1.setWorkingTime(entity1.getWorkingTime() + entity2.getWorkingTime());
                                            return entity1;
                                        }
                                )
                        ))
                        .values()
                        .stream()
                        .map(Optional::get)
                        .collect(Collectors.toList()));
    }

    /**
     * 実績データの工数が0分のデータを削除する
     *
     * @param actuals 実績データ
     * @return 工数0分以外の実績データ
     */
    public List<ReportOutInfoEntity> removeTimeWorkingZeroMinute(List<ReportOutInfoEntity> actuals) {

        List<ReportOutInfoEntity> workTimeZeroActuals = new ArrayList<>();
        List<ReportOutInfoEntity> tempActuals = new ArrayList<>();

        actuals.stream().forEach(entity -> {
            if (0 != millisecondsToMinutes(entity.getWorkingTime().doubleValue())) {
                tempActuals.add(entity);
            } else {
                workTimeZeroActuals.add(entity);
            }
        });

        if (!workTimeZeroActuals.isEmpty()) {
            noReportActualsOutputLog("工数連携の設定がNOの実績一覧", workTimeZeroActuals);
        }

        return tempActuals;
    }

    /**
     * 直接工数実績か判定
     *
     * @param actual
     */
    private static boolean isDirectActual(ReportOutInfoEntity actual) {
        return Objects.nonNull(actual.getActualId());
    }

    /**
     * tempフォルダのcsvファイルを削除する。
     *
     * @throws IOException
     */
    private void cleanupTempFolder() throws IOException {
        Path path = Paths.get(TEMP_PATH);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*" + FILE_EXT_CSV)) {
            for (Path deleteFilePath : ds) {
                Files.delete(deleteFilePath);
            }
        }
    }

    /**
     * 工数連携対象の実績出力情報一覧を取得する。
     *
     * @param searchType
     * @param fromDate
     * @param toDate
     * @return 工数連携対象の実績出力情報一覧 (エラー発生時はnull)
     */
    private List<ReportOutInfoEntity> getActualData(SEARCH_TYPE searchType, Date fromDate, Date toDate) {

        List<ReportOutInfoEntity> result = null;


        ReportOutSearchCondition reportOutSearchCondition = new ReportOutSearchCondition()
                .fromDate(fromDate).toDate(toDate)
                .statusList(Arrays.asList(
                        KanbanStatusEnum.WORKING,
                        KanbanStatusEnum.COMPLETION,
                        KanbanStatusEnum.INTERRUPT,
                        KanbanStatusEnum.SUSPEND));

        // 実績出力情報一覧を取得する。
        List<ReportOutInfoEntity> actuals = actualResultInfoFacade.reportOutSearch(reportOutSearchCondition);
        if (Objects.isNull(actuals)) {
            return result;
        }

        List<ReportOutInfoEntity> noSettingActuals = new ArrayList();// 工数連携の設定が無い実績のリスト
        List<ReportOutInfoEntity> errorSettingActuals = new ArrayList();// 工数連携の値が不正な実績のリスト
        List<ReportOutInfoEntity> collabNoSettingActuals = new ArrayList();// 工数連携の値がNOのリスト

        // 工数連携対象の実績に絞り込む。
        result = actuals.stream().filter(actual -> {
            List<StringProperty> propertyValues = actual.getPropertyValues(KOUSU_COLLAB);
            if (propertyValues.isEmpty()) {
                return false;
            } else {
                // 工数連携対象かチェックする。(全ての工数連携がYESか)
                boolean isAllYes = propertyValues.stream().allMatch(propertyValue -> {
                    return KOUSU_COLLAB_YES.equals(propertyValue.get());
                });

                if (!isAllYes) {
                    // 工数連携の設定にYES/NO以外が入っているか、nullまたは空白になっている場合、設定不正リストに追加。
                    for (StringProperty propertyValue : propertyValues) {
                        String prop = propertyValue.get();
                        if (prop.isEmpty() || (!KOUSU_COLLAB_YES.equals(prop) && !KOUSU_COLLAB_NO.equals(prop))) {
                            errorSettingActuals.add(actual);
                            break;
                        } // 工数連携の設定がNOであれば連携しないリストに追加
                        else if (KOUSU_COLLAB_NO.equals(prop)) {
                            collabNoSettingActuals.add(actual);
                            break;
                        }
                    }
                }
                return isAllYes;
            }
        }).collect(toList());

        // 工数連携の設定に異常があった場合、警告メールを送信する。
        if (!noSettingActuals.isEmpty() || !errorSettingActuals.isEmpty()) {
            this.sendWarnMail(fromDate, toDate, noSettingActuals, errorSettingActuals);
        }

        // 連携されなかった実績をログに出力
        if (!collabNoSettingActuals.isEmpty()) {
            noReportActualsOutputLog("工数連携の設定がNOの実績一覧", collabNoSettingActuals);
        }

        return result;
    }

    /**
     *
     * @param workCenter
     * @return
     * @throws IOException
     */
    private Writer getWriter(String workCenter) throws IOException {
        if (!writers.containsKey(workCenter)) {
            String data = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String path = workCenter + "_" + data + FILE_EXT_CSV;
            Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(TEMP_PATH + File.separator + path)), StandardCharsets.UTF_8));
            writers.put(workCenter, writer);
            files.add(path);
            logger.info("make csv file:{}", path);
        }
        return writers.get(workCenter);
    }

    /**
     *
     * @param writer
     * @param actual
     * @throws IOException
     */
    private void writeActualData(Writer writer, ReportOutInfoEntity actual) throws IOException {
        String orderNumber = actual.getPropertyValue(ORDER_NUMBER).get();
        String name = actual.getOrganizationIdentName() + ORGANIZATION_SUFFIX;
        String date = new SimpleDateFormat("yyyyMMdd").format(actual.getImplementDatetime());
        String workCenter = actual.getPropertyValue(WORK_CENTER).get();
        String workTime = String.valueOf(millisecondsToMinutes(actual.getWorkingTime().doubleValue()));

        logger.info("writeActualData:{},{},{},{},{}", orderNumber, name, date, workTime, workCenter);

        StringBuilder sb = new StringBuilder();
        sb.append(orderNumber).append(",");
        sb.append(name).append(",");
        sb.append(date).append(",");
        sb.append(workTime).append(",");
        sb.append(Objects.nonNull(workCenter) ? workCenter : "");
        sb.append(LINE_SEPARATOR);
        writer.append(sb.toString());
    }
    
    /**
     * 読み込み対象のファイル名をファイルに入力する
     * @param filePath
     * @param readFileNameList
     * @return 
     */
    private void readFileNameWrite(List<String> readFileNameList) {
        if (readFileNameList.isEmpty()) {
            return;
        }

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now = new Date();

            String readFileNameAddress = info.getReadFileNameAddress();
            if (StringUtils.isEmpty(readFileNameAddress)) {
                readFileNameAddress = System.getProperty("user.dir");
            }

            // 読み込んだファイル名を記憶するファイルを作成
            File readName = new File(readFileNameAddress + File.separator + TMP_READ_FILE_NAME_LIST_FILE_PREFIX + formatter.format(now) + ".txt");
            if (!readName.exists()) {
                readName.createNewFile();
            }

            try (FileWriter readNameFile = new FileWriter(readName, true);
                 PrintWriter pw = new PrintWriter(new BufferedWriter(readNameFile))) {
                for (String fileName : readFileNameList) {
                    pw.println(fileName + "," + formatter.format(now));
                }
            }

            Path fileName = Paths.get(readFileNameAddress + File.separator + READ_FILE_NAME_LIST_FILE_PREFIX + formatter.format(now) + ".txt");
            String result = FileUtils.renameFile(readName.toPath(), fileName);
            if (!FileUtils.SUCCESS.equals(result)) {
                throw new Exception(result);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工数実績ファイルをフォルダーにアップロードする。
     *
     * @return
     * @throws Exception
     */
    private int uploadFiles() throws Exception {
        List<String> readFileName = new ArrayList<>();
        Either<String, File> inFolderEither = NetworkFileUtil.connect(info.getFolderAddress(), info.getUserName(), info.getPassword());
        if (inFolderEither.isLeft()) {
            filesUplodeFail.addAll(files);
            throw new Exception(inFolderEither.getLeft());
        }
        File inFolder = inFolderEither.get();

        for (String path : files) {
            String sourceFilePath = TEMP_PATH + File.separator + path;
            String destinationFilePath = inFolder.getPath() + File.separator + path;

            logger.info("Copying file: {} to {}", sourceFilePath, destinationFilePath);

            try {
                Files.copy(Paths.get(sourceFilePath), Paths.get(destinationFilePath), StandardCopyOption.REPLACE_EXISTING);
                // 読み込んだファイル名を記憶する
                String fileName = path.substring(0, path.lastIndexOf("."));
                readFileName.add(fileName);
            } catch (IOException ex) {
                logger.fatal(ex, ex);
                logger.fatal("Copying file: {} to {} failed.", sourceFilePath, destinationFilePath);
                filesUplodeFail.add(path);
            }
        }

        readFileNameWrite(readFileName);

        if (!filesUplodeFail.isEmpty()) {
            throw new Exception("ファイルのアップロードに失敗しました。");
        }

        return files.size();
    }

    /**
     * エラー発生通知メールを送信する。
     *
     * @param fromDate 日時指定の先頭
     * @param toDate 日時指定の末尾
     * @param nowTask 現在のタスク
     */
    private void sendErrorMail(Date fromDate, Date toDate, OutputActualTaskEnum nowTask) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setUser(info.getErrorMailUser());
            prop.setPassword(info.getErrorMailPassword());
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);

            String from = info.getErrorMailFrom();
            String to = info.getErrorMailTo();
            String subject = "【adFactory 工数連携】 実績情報出力エラー";

            StringBuilder content = new StringBuilder();
            content.append("adFactory 工数連携の実績情報出力でエラーが発生しました。");
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append("データ取り込み間隔： ");
            content.append(dateFormatter.format(fromDate));
            content.append(" ～ ");
            content.append(dateFormatter.format(toDate));
            content.append(LINE_SEPARATOR);
            content.append("エラー発生日時: ");
            content.append(dateFormatter.format(new Date()));
            content.append(LINE_SEPARATOR);
            content.append("エラー詳細:");
            content.append(LINE_SEPARATOR);
            switch (nowTask) {
                case DELETE_FILE:
                    content.append("　テンポラリファイルを削除できません。");
                    content.append(LINE_SEPARATOR);
                    content.append("　手動で削除してください。");
                    content.append(LINE_SEPARATOR);
                    break;
                case GET_ACTUAL_DATA:
                    content.append("　実績データの取得に失敗しました。");
                    break;
                case ACTUAL_DATA_PROC:
                    content.append("　データ処理でエラーが発生しました。");
                    break;
                case CREATE_FILE:
                    content.append("　実績ファイルの出力に失敗しました。");
                    break;
                case FILE_UPLOAD:
                    content.append("　実績ファイルのアップロードに失敗しました。");
                    if (!filesUplodeFail.isEmpty()) {
                        content.append(LINE_SEPARATOR);
                        for (String file : filesUplodeFail) {
                            content.append("　" + file);
                        }
                    }
                    content.append(LINE_SEPARATOR);
                    content.append("　共有フォルダを確認してください。 ");
                    break;
                default:
                    break;
            }

            MailUtils mail = new MailUtils(prop);
            mail.send(from, to, subject, content.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 警告メールを送信する。
     *
     * @param fromDate 日時指定の先頭
     * @param toDate 日時指定の末尾
     * @param noSettingActuals 工数連携の設定が無い実績一覧
     * @param errorSettingActuals 工数連携の設定が異常な実績一覧
     */
    private void sendWarnMail(Date fromDate, Date toDate, List<ReportOutInfoEntity> noSettingActuals, List<ReportOutInfoEntity> errorSettingActuals) {
        logger.info("sendWarnMail:{},{},{},{}", fromDate, toDate, noSettingActuals, errorSettingActuals);
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setUser(info.getErrorMailUser());
            prop.setPassword(info.getErrorMailPassword());
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);

            String from = info.getErrorMailFrom();
            String to = info.getErrorMailTo();
            String subject = "【adFactory 工数連携】 実績情報出力 警告";

            StringBuilder content = new StringBuilder();
            content.append("adFactory 工数連携の設定に異常がありました。");
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append("データ取り込み間隔： ");
            content.append(dateFormatter.format(fromDate));
            content.append(" ～ ");
            content.append(dateFormatter.format(toDate));
            content.append(LINE_SEPARATOR);
            content.append("発生日時: ");
            content.append(dateFormatter.format(new Date()));
            content.append(LINE_SEPARATOR);
            content.append("エラー詳細:");
            content.append(LINE_SEPARATOR);

            if (Objects.nonNull(noSettingActuals) && !noSettingActuals.isEmpty()) {
                content.append(LINE_SEPARATOR);
                content.append("工数連携が設定されていません。");
                content.append(LINE_SEPARATOR);
                content.append(HIERARCHY);
                content.append(LINE_SEPARATOR);
                content.append(LINE_SEPARATOR);
                for (ReportOutInfoEntity actual : noSettingActuals) {
                    content.append("・");
                    content.append(actual.getKanbanParentName());// カンバン階層名
                    content.append(" / ");
                    content.append(actual.getKanbanName());// カンバン名
                    content.append(" / ");
                    content.append(actual.getWorkflowParentName());// 工程順階層名
                    content.append(" / ");
                    content.append(actual.getWorkflowName());// 工程順名
                    content.append(" / ");
                    content.append(actual.getWorkName());// 工程名
                    content.append(" / ");
                    content.append(actual.getOrganizationParentName());// 組織階層名
                    content.append(LINE_SEPARATOR);
                }
            }

            if (Objects.nonNull(errorSettingActuals) && !errorSettingActuals.isEmpty()) {
                content.append(LINE_SEPARATOR);
                content.append("工数連携の値が不正です。");
                content.append(LINE_SEPARATOR);
                content.append(HIERARCHY);
                content.append(LINE_SEPARATOR);
                content.append(LINE_SEPARATOR);
                for (ReportOutInfoEntity actual : errorSettingActuals) {
                    content.append("・");
                    content.append(actual.getKanbanParentName());// カンバン階層名
                    content.append(" / ");
                    content.append(actual.getKanbanName());// カンバン名
                    content.append(" / ");
                    content.append(actual.getWorkflowParentName());// 工程順階層名
                    content.append(" / ");
                    content.append(actual.getWorkflowName());// 工程順名
                    content.append(" / ");
                    content.append(actual.getWorkName());// 工程名
                    content.append(" / ");
                    content.append(actual.getOrganizationParentName());// 組織階層名
                    content.append(LINE_SEPARATOR);
                }
            }

            MailUtils mail = new MailUtils(prop);
            mail.send(from, to, subject, content.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 報告しない実績をログ出力する
     *
     * @param header ヘッダー
     * @param noReportActuals 報告しない実績一覧
     */
    //"工数連携の設定がNOの実績一覧
    private void noReportActualsOutputLog(String header, List<ReportOutInfoEntity> noReportActuals) {

        logger.info(header + "\n"
                + HIERARCHY
        );

        if (Objects.nonNull(noReportActuals) && !noReportActuals.isEmpty()) {
            for (ReportOutInfoEntity actual : noReportActuals) {
                logger.info("{} / {} / {} / {} / {} / {}",
                        actual.getKanbanParentName(),
                        actual.getKanbanName(),
                        actual.getWorkflowParentName(),
                        actual.getWorkflowName(),
                        actual.getWorkName(),
                        actual.getOrganizationParentName());
            }
        }
    }

    /**
     * ミリ秒を分に変換する
     *
     * @param value ms
     * @return 分に変換した値
     */
    public static int millisecondsToMinutes(double value) {
        return (int) Math.floor(value / (1000 * 60));
    }
}
