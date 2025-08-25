/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryfujiactualdataoutput;

import adtekfuji.clientservice.BreaktimeInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.property.AdProperty;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.BreaktimeUtil;
import jp.adtekfuji.adfactoryfujiactualdataoutput.mail.MailProperty;
import jp.adtekfuji.adfactoryfujiactualdataoutput.mail.MailUtils;
import jp.adtekfuji.adfactoryfujiactualdataoutput.service.ActualResultInfoFacade;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class OutputActualFacade {

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
        FTP_TRANSFER;// FTP転送
    }

    private final static Logger logger = LogManager.getLogger();
    private final static String TEMP_PATH = "temp";
    private final static String WORK_CENTER = "ワークセンター";
    private final static String WORK_CLASS = "作業区分";
    private final static String ORDER_NUMBER = "オーダー番号";
    private final static String RECEIVED_NUMBER = "受注番号";
    private final static String SERIAL_NUMBER = "シリアル";
    private final static String MANAGEMENT_CLASS = "管理区分";
    private final static String KOUSU_COLLAB = "工数連携";
    private final static String KOUSU_COLLAB_YES = "YES";
    private final static String KOUSU_COLLAB_NO = "NO";
    private final static String DELIMITER = "\t";
    private final static String LINE_SEPARATOR = "\r\n";
    private final static String FINAL_ROW = "END";
    private final static String FILE_EXT_TSV = ".tsv";// 出力ファイルの拡張子
    private final static String NO_COMPLETION = "完了なし";// 着手のみで完了のない作業の場合に、作業時間に出力する文字列

    private final OutputActualInfo info;
    private final Map<String, Writer> writers = new HashMap<>();
    private final List<String> files = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param info 実績出力設定情報
     */
    public OutputActualFacade(OutputActualInfo info) {
        this.info = info;
    }

    /**
     * 工数実績ファイルを作成して、FTP転送する。
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

            // tempフォルダのtsvファイルを削除する。
            nowTask = OutputActualTaskEnum.DELETE_FILE;
            this.cleanupTempFolder();

            // 工数連携対象の実績出力情報一覧を取得する。
            nowTask = OutputActualTaskEnum.GET_ACTUAL_DATA;
            List<ReportOutInfoEntity> actuals = getActualData(searchType, fromDate, toDate);
            if (Objects.isNull(actuals)) {
                return result;
            }

            if (actuals.isEmpty()) {
                result = 0;
                return result;
            }

            // 重複時間の削除.
            nowTask = OutputActualTaskEnum.ACTUAL_DATA_PROC;
            actuals = this.deleteOverlapTime(actuals);

            boolean isOutput = false;
            try {
                //実績データ書き込み
                nowTask = OutputActualTaskEnum.CREATE_FILE;
                for (ReportOutInfoEntity actual : actuals) {
                    String workCenter = actual.getPropertyValue(WORK_CENTER).get();
                    Writer writer = getWriter(workCenter);
                    writeActualData(writer, actual);
                }

                isOutput = true;
            } finally {
                //クローズ.
                for (Map.Entry<String, Writer> e : writers.entrySet()) {
                    Writer writer = e.getValue();
                    writer.append(FINAL_ROW);
                    writer.flush();
                    writer.close();
                }
            }

            // 工数実績ファイルをFTP転送する。
            nowTask = OutputActualTaskEnum.FTP_TRANSFER;
            if (isOutput) {
                result = this.fileToFtp();
                logger.info("comp actual data output");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            // エラー発生通知メールを送信する。
            this.sendErrorMail(fromDate, toDate, ex, nowTask);
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

        StringBuilder sb = new StringBuilder();
        sb.append(info.getAdFactoryAddress());
        sb.append("/adFactoryServer/rest");
        Map<Long, ReportOutInfoEntity> workingActuals = new HashMap<>();                         // 作業中の実績.
        Date lastDatetime = new Date();                                                         // 直前の実績日時.
        BreaktimeInfoFacade breaktimeFacade = new BreaktimeInfoFacade(sb.toString());           // 休憩時間REST.
        List<BreakTimeInfoEntity> breaktimeCollection = new ArrayList<>();                      // 休憩時間リスト.
        OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade(sb.toString());  // 作業者REST.
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
            for (ReportOutInfoEntity actual : entry.getValue().stream().sorted(Comparator.comparing(a -> a.getImplementDatetime())).collect(Collectors.toList())) {

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
                    // 開始実績の場合、対になる実績(完了, 中断, 中止)がない場合、「完了なし」を出力するために作業時間をnullにしておく。
                    long pairCount = entry.getValue().stream().filter(a -> !a.getActualId().equals(actual.getActualId())
                            && a.getFkWorkKanbanId().equals(actual.getFkWorkKanbanId())
                            && (a.getImplementDatetime().equals(actual.getImplementDatetime())
                                    || a.getImplementDatetime().after(actual.getImplementDatetime()))
                            && endStatusList.contains(a.getActualStatus())).count();
                    if (pairCount == 0) {
                        actual.setWorkingTime(null);
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
     * tempフォルダのtsvファイルを削除する。
     *
     * @throws IOException
     */
    private void cleanupTempFolder() throws IOException {
        Path path = Paths.get(TEMP_PATH);
        Files.createDirectories(path);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*" + FILE_EXT_TSV)) {
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

        Properties properties = AdProperty.getProperties();
        int numData = Integer.parseInt(properties.getProperty("numData", "20"));

        List<ReportOutInfoEntity> result = null;

        ActualResultInfoFacade facade = new ActualResultInfoFacade(info.getAdFactoryAddress());

        ReportOutSearchCondition reportOutSearchCondition = new ReportOutSearchCondition()
                .fromDate(fromDate).toDate(toDate)
                .statusList(Arrays.asList(
                        KanbanStatusEnum.WORKING,
                        KanbanStatusEnum.COMPLETION,
                        KanbanStatusEnum.INTERRUPT,
                        KanbanStatusEnum.SUSPEND));

        // 実績出力情報一覧を取得する。
        List<ReportOutInfoEntity> actuals = facade.reportOutSearch(reportOutSearchCondition);
        if (Objects.isNull(actuals)) {
            return result;
        }

        List<ReportOutInfoEntity> noSettingActuals = new ArrayList();// 工数連携の設定が無い実績のリスト
        List<ReportOutInfoEntity> errorSettingActuals = new ArrayList();// 工数連携の値が不正な実績のリスト

        // 工数連携対象の実績に絞り込む。
        result = actuals.stream().filter(actual -> {
            List<StringProperty> propertyValues = actual.getPropertyValues(KOUSU_COLLAB);
            if (propertyValues.isEmpty()) {
                // プロパティに工数連携の設定が無いので、設定無しリストに追加する。
                // 大量にエラーが通知されるため除外。
                //noSettingActuals.add(actual);
                return false;
            } else {
                // 工数連携対象かチェックする。(全ての工数連携がYESか)
                boolean isAllYes = propertyValues.stream().allMatch(propertyValue -> {
                    return KOUSU_COLLAB_YES.equals(convertAlphabetHalf(propertyValue.get()).toUpperCase());
                });

                if (!isAllYes) {
                    // 工数連携の設定にYES/NO以外が入っているか、nullまたは空白になっている場合、設定不正リストに追加。
                    for (StringProperty propertyValue : propertyValues) {
                        String prop = convertAlphabetHalf(propertyValue.get());
                        if (prop.isEmpty() || (!KOUSU_COLLAB_YES.equals(prop.toUpperCase()) && !KOUSU_COLLAB_NO.equals(prop.toUpperCase()))) {
                            errorSettingActuals.add(actual);
                            break;
                        }
                    }
                }
                return isAllYes;
            }
        }).collect(Collectors.toList());

        // 工数連携の設定に異常があった場合、警告メールを送信する。
        if (!noSettingActuals.isEmpty() || !errorSettingActuals.isEmpty()) {
            this.sendWarnMail(fromDate, toDate, noSettingActuals, errorSettingActuals);
        }

        return result;
    }

    /**
     *
     * @param workCenter
     * @return
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Writer getWriter(String workCenter) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        if (!writers.containsKey(workCenter)) {
            String data = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String path = workCenter + "_" + data + FILE_EXT_TSV;
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEMP_PATH + File.separator + path), "UTF8"));
            writers.put(workCenter, writer);
            files.add(path);
            logger.info("make tsv file:{}", path);
            //カラム書き込み.
            writeColumn(writer);
        }
        return writers.get(workCenter);
    }

    /**
     *
     * @param writer
     * @throws IOException
     */
    private void writeColumn(Writer writer) throws IOException {
        final List<String> columns = Arrays.asList("作業日", "職番", "WC", "作業区分", "受注番号／オーダ番号", "シリアル", "作業時間", "管理区分", "備考");
        int num = 0;
        StringBuilder sb = new StringBuilder();
        for (String column : columns) {
            if (num != 0) {
                sb.append(DELIMITER);
            }
            sb.append(column);
            num++;
        }
        sb.append(LINE_SEPARATOR);
        writer.append(sb.toString());
    }

    /**
     *
     * @param writer
     * @param actual
     * @throws IOException
     */
    private void writeActualData(Writer writer, ReportOutInfoEntity actual) throws IOException {
        String date = new SimpleDateFormat("yyyy/MM/dd").format(actual.getImplementDatetime());
        String name = actual.getOrganizationIdentName();
        String workCenter = actual.getPropertyValue(WORK_CENTER).get();
        String workClass = actual.getPropertyValue(WORK_CLASS).get();
        String orderNumber = actual.getPropertyValue(ORDER_NUMBER).get();
        String receivedNumber = actual.getPropertyValue(RECEIVED_NUMBER).get();
        String serialNumber = actual.getPropertyValue(SERIAL_NUMBER).get();
        String managmentClass = actual.getPropertyValue(MANAGEMENT_CLASS).get();

        // シリアルは下7桁を出力する。
        if (Objects.nonNull(serialNumber) && serialNumber.length() > 7) {
            serialNumber = serialNumber.substring(serialNumber.length() - 7);
        }

        // 着手のみの作業(リスト作成時に作業時間がnullになっている)の場合は「完了なし」の文字を出力する。
        String workTime;
        if (Objects.nonNull(actual.getWorkingTime())) {
            workTime = String.valueOf((int) Math.round(actual.getWorkingTime().doubleValue() / 60.0 / 1000.0));
        } else {
            workTime = NO_COMPLETION;
        }

        //String note = "";
        //if (actual.getActualStatus() != KanbanStatusEnum.INTERRUPT && Objects.nonNull(actual.getDelayReason())) {
        //    note = actual.getDelayReason();
        //}
        //if (actual.getActualStatus() == KanbanStatusEnum.INTERRUPT && Objects.nonNull(actual.getInterruptReason())) {
        //    note = actual.getInterruptReason();
        //}
        logger.info("writeActualData:{},{},{},{},{},{},{},{}", date, name, workCenter, workClass, orderNumber, serialNumber, managmentClass, workTime);

        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(DELIMITER);
        sb.append(name);
        sb.append(DELIMITER);
        sb.append(Objects.nonNull(workCenter) ? workCenter : "");
        sb.append(DELIMITER);
        sb.append(Objects.nonNull(workClass) ? workClass : "");
        sb.append(DELIMITER);
        if (Objects.nonNull(orderNumber)) {
            sb.append(orderNumber);
        } else if (Objects.nonNull(receivedNumber)) {
            sb.append(receivedNumber);
        } else {
            sb.append("");
        }
        sb.append(DELIMITER);
        sb.append(Objects.nonNull(serialNumber) ? serialNumber : "");
        sb.append(DELIMITER);
        sb.append(workTime);
        sb.append(DELIMITER);
        sb.append(Objects.nonNull(managmentClass) ? managmentClass : "");
        sb.append(DELIMITER);
        sb.append("");
        sb.append(LINE_SEPARATOR);
        writer.append(sb.toString());
    }

    /**
     * 工数実績ファイルをFTP転送する。
     *
     * @return
     * @throws Exception
     */
    private int fileToFtp() throws Exception {
        int result = -1;
        FTPClient ftpClient = new FTPClient();
        try {
            logger.info("connect ftp:{},{},{}", info.getFtpAddress(), info.getFtpPort(), info.getFtpUser());
            ftpClient.connect(info.getFtpAddress(), info.getFtpPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new Exception(String.format("Failed to connect to ftp server.(%d)", reply));
            }
            if (ftpClient.login(info.getFtpUser(), info.getFtpPassword()) == false) {
                throw new Exception("Login to ftp server failed.");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            for (String path : files) {
                try (InputStream inputStream = new FileInputStream(TEMP_PATH + File.separator + path)) {
                    logger.info("file to ftp:{}", info.getFtpUploadPath() + path);
                    ftpClient.storeFile(info.getFtpUploadPath() + path, inputStream);
                }
            }

            result = files.size();
        } catch (IOException ex) {
            throw ex;
        } finally {
            ftpClient.logout();
            ftpClient.disconnect();
            logger.info("disconnect ftp:{},{}", info.getFtpAddress(), info.getFtpPort());
        }
        return result;
    }

    /**
     * エラー発生通知メールを送信する。
     *
     * @param fromDate 日時指定の先頭
     * @param toDate 日時指定の末尾
     * @param exception 発生したエラー
     */
    private void sendErrorMail(Date fromDate, Date toDate, Exception exception, OutputActualTaskEnum nowTask) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
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
                case FTP_TRANSFER:
                    content.append("　実績ファイルのアップロードに失敗しました。");
                    content.append(LINE_SEPARATOR);
                    content.append("　FTPサーバーを確認してください。");
                    break;
                default:
                    break;
            }
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append(exception.getMessage());

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
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);

            prop.setUser(info.getErrorMailUser());
            prop.setPassword(info.getErrorMailPassword());

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
                content.append("(カンバン階層 / カンバン / 工程順階層> / 工程順 / 工程 / 組織階層)");
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
                content.append("(カンバン階層 / カンバン / 工程順階層 / 工程順 / 工程 / 組織階層)");
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
     * 文字列中の全角英文字を半角英文字に変換する。
     *
     * @param value 文字列
     * @return 文字列中の全角英文字を半角英文字に変換した文字列
     */
    private String convertAlphabetHalf(String value) {
        if (Objects.isNull(value)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++ ) {
            char c = value.charAt(i);
            if (c >= 'ａ' && c <= 'ｚ') {
                c = (char)(c - 'ａ' + 'a');
            } else if (c >= 'Ａ' && c <= 'Ｚ') {
                c = (char)(c - 'Ａ' + 'A');
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
