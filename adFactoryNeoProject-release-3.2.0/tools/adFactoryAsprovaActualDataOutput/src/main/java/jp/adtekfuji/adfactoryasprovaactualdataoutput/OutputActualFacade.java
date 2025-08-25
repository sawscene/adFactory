/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryasprovaactualdataoutput;

import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import adtekfuji.utility.Tuple;
import io.vavr.control.Either;
import java.io.BufferedWriter;
import jp.adtekfuji.adfactoryasprovaactualdataoutput.mail.MailProperty;
import jp.adtekfuji.adfactoryasprovaactualdataoutput.mail.MailUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * 実績情報出力
 * 
 * @author koga
 */
public class OutputActualFacade {

    private final static Logger logger = LogManager.getLogger();
    private final static String FILE_NAME = "result_";
    private final static String FILE_NAME2 = "adFactoryReport_work_";
    private final static String FILE_EXT_CSV = ".csv";
    private final static String DELIMITER = ",";
    private final static String LINE_SEPARATOR = "\r\n";
    private final static String CHAR_CODE = "Shift_JIS";
    private final static List<String> CSV_HEADER = Arrays.asList("作業コード", "実績開始日時", "実績終了日時");

    private final static String SQL_URL = "jdbc:postgresql://%s:15432/adFactoryDB2";
    private final static String SQL_USER = "postgres";
    private final static String SQL_PASSWORD = "@dtek1977";

    // 実勢情報取得項目
    private final static String KANBAN_ID = "kanban_id";
    private final static String ACTUAL_ID = "actual_id";
    private final static String WORK_CODE = "work_code";
    private final static String KANBAN_NAME = "kanban_name";
    private final static String WORKFLOW_NAME = "workflow_name";
    private final static String WORK_NAME = "work_name";
    private final static String ACTUAL_START_DATETIME = "actual_start_datetime";
    private final static String ACTUAL_COMP_DATETIME = "actual_comp_datetime";
    private final static String IMPLEMENT_DATETIME = "implement_datetime";

    // エラーメッセージ（エラー内容）
    private final static String ERROR_DB_ACCESS = "データベース接続に失敗しました。";
    private final static String ERROR_SQL_EXEC = "SQL構文の解析異常しました。";
    private final static String ERROR_CREATE_ACTUAL_DATA = "実績情報の作成に失敗しました。";
    private final static String ERROR_FOLDER_ACCESS = "フォルダーパスにアクセスできません。";
    private final static String ERROR_CREATE_CSV = "実績情報ファイル（CSV）の生成に失敗しました。";
    private final static String ERROR_SAVE_SETTINGS = "設定保存に失敗しました。";
    private final static String ERROR_WORKNAME_NOTFOUND = "下記製造オーダーに開始工程名または終了工程名を含む工程名が見つかりません。";
    // エラーメッセージ（対処方法）
    private final static String DEAL_CONFIRM_SERVER = "adFactoryサーバーアドレス設定を確認してください。";
    private final static String DEAL_CONFIRM_PROCESS_NAME = "開始工程名／終了工程名設定を確認してください。";
    private final static String DEAL_CONFIRM_INTERVAL = "データ取り込み間隔設定を確認してください。";
    private final static String DEAL_CONFIRM_FOLDER = "フォルダーパス設定 および フォルダーの共有設定を確認してください。";
    private final static String DEAL_REBOOT_SERVER = "adFactoryサーバー側に問題がある可能性がありますので、サーバー再起動後に実行してください。";

    private final static String ACTUAL_ID_DEFAULT = "0";

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/M/d H:mm");
    private final OutputActualInfo info;

    public enum SEARCH_TYPE {
        INTERVAL_TIME,       // 時間で指定（未使用）
        FROM_TO_SEARCH,      // 日時範囲で指定
        FROM_LAST_SEARCHED   // 前回実行時から現在を指定
    }

    public enum OUTPUT_TYPE {
        KANBAN_WORK_CODE,       // カンバンのプロパティ情報に「作業コード」が設定されている実績情報
        PROCESS_WORK_CODE       // 工程のプロパティ情報に「作業コード」が設定されている実績情報
    }

    /**
     * コンストラクタ
     * @param info 実績出力設定
     */
    public OutputActualFacade(OutputActualInfo info) {
        this.info = info;
    }

    /**
     * 実績情報ファイル（CSV）を作成し、フォルダーパスに格納する。
     *
     * @param searchType 検索タイプ
     * @param outputType 出力タイプ
     * @return 報告数
     */
    public int output(SEARCH_TYPE searchType, OUTPUT_TYPE outputType) {
        logger.info("Starting OutputActualFacade::output : {}", searchType, outputType);
        int result = -1;
        Date fromDate;
        Date toDate;

        try {
            // データ取り込み間隔を設定
            if (SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
                fromDate = info.getFromSearchDatetime();
                toDate = info.getToSearchDatetime();
            } else {
                if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                    fromDate = info.lastSearchedDatetimeProperty().get();
                } else {
                    fromDate = info.workLastSearchedDatetimeProperty().get();
                }
                toDate = new Date();
            }

            // 実績情報ファイル（CSV）を作成して、フォルダーパスに格納
            final Either<List<String>, Integer> ret = implOutput(searchType, outputType, fromDate, toDate);

            // 正常
            if (ret.isRight()) {
                logger.info("Starting OutputActualFacade::output : success");
                return ret.get();
            }

            // 異常発生
            sendErrorMail(outputType, fromDate, toDate, ret.getLeft());
            return -1;
        } catch(Exception e) {
            logger.fatal(e.getMessage());
        }
        return result;
    }

    /**
     * 実績情報ファイル（CSV）を作成して、フォルダーパスに格納する。
     *
     * @param searchType 検索タイプ
     * @param outputType 出力タイプ
     * @param fromDate 検索開始日時（FROM）
     * @param toDate 検索範囲日時（TO）
     * @return 報告数
     */
    private Either<List<String>, Integer> implOutput(SEARCH_TYPE searchType, OUTPUT_TYPE outputType, Date fromDate, Date toDate) {
        // エラーメッセージ一覧
        List<String> errorMessages = new ArrayList<>();
        // 開始工程名リスト
        List<String> startWorkNames = Arrays.asList(info.getStartWorkName().split(DELIMITER));
        // 終了工程名リスト
        List<String> endWorkNames = Arrays.asList(info.getEndWorkName().split(DELIMITER));

        // SQL文生成
        String sql = createGetActualResultQuery(searchType, outputType, fromDate, toDate);
        // DB接続
        Either<String, Optional<DataTable>> optActualResult = SQLUtil.connect(String.format(SQL_URL, info.getAdFactoryAddress()), SQL_USER, SQL_PASSWORD,
                conn -> {
                    Statement stmt = conn.createStatement();
                    // DBからデータ受信
                    return DataTable.create(stmt.executeQuery(sql));
                });

        if (optActualResult.isLeft()) {
            errorMessages.add(ERROR_DB_ACCESS);
            errorMessages.add(DEAL_CONFIRM_SERVER);
            errorMessages.add(LINE_SEPARATOR);
            errorMessages.add(optActualResult.getLeft());
            return Either.left(errorMessages);
        }
        if (!optActualResult.get().isPresent()) {
            logger.error("Actual result data error");
            errorMessages.add(ERROR_SQL_EXEC);
            if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                errorMessages.add(DEAL_CONFIRM_PROCESS_NAME);
            }
            errorMessages.add(DEAL_CONFIRM_INTERVAL);
            return Either.left(errorMessages);
        }
        // 取得結果をMapへ変換
        List<Map<String, String>> dataList = optActualResult.get().get().toMapList();

        // 実績情報を編集
        Either<String, Tuple<List<Map<String, String>>, List<String>>> actualResultList;
        if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
            actualResultList = this.createActualResultList(dataList, startWorkNames, endWorkNames, searchType);
        } else {
            actualResultList = this.createWorkActualResultList(dataList, searchType);
        }

        if(actualResultList.isLeft()) {
            errorMessages.add(ERROR_CREATE_ACTUAL_DATA);
            if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                errorMessages.add(DEAL_CONFIRM_PROCESS_NAME);
            }
            errorMessages.add(DEAL_CONFIRM_INTERVAL);
            errorMessages.add(actualResultList.getLeft());
            return Either.left(errorMessages);
        }

        if (!actualResultList.get().getLeft().isEmpty()) {
            // フォルダーパス＋CSVファイル名を取得
            String fullFileName = this.getFullFileName(outputType);
            if (StringUtils.isEmpty(fullFileName)) {
                errorMessages.add(ERROR_FOLDER_ACCESS);
                errorMessages.add(DEAL_CONFIRM_FOLDER);
                return Either.left(errorMessages);
            }
            // CSVファイルを生成
            Boolean isCreateFile = this.createCsv(actualResultList.get().getLeft(), fullFileName);
            if (!isCreateFile) {
                errorMessages.add(ERROR_CREATE_CSV);
                errorMessages.add(DEAL_REBOOT_SERVER);
                return Either.left(errorMessages);
            }
        }

        if (!SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            // 現在日時を保持
            if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                info.setLastSearchedDatetime(new Date());
            } else {
                info.setWorkLastSearchedDatetime(new Date());
            }

            // 取得時のactual_id最大値を保持
            OptionalInt optMaxActualId
                    = dataList
                    .stream()
                    .map(l -> l.getOrDefault(ACTUAL_ID, ACTUAL_ID_DEFAULT))
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::parseInt)
                    .max();

            if(optMaxActualId.isPresent()) {
                logger.info("update last ActualId : {}", optMaxActualId.getAsInt());
                // 取得したactual_idが保持しているactual_idを超える場合、前回検索実績IDを更新
                if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                    if(Integer.parseInt(info.getLastSearchedActualId()) < optMaxActualId.getAsInt()){
                        info.setLastSearchedActualId(String.valueOf(optMaxActualId.getAsInt()));
                    }
                } else {
                    if(Integer.parseInt(info.getWorkLastSearchedActualId()) < optMaxActualId.getAsInt()){
                        info.setWorkLastSearchedActualId(String.valueOf(optMaxActualId.getAsInt()));
                    }
                }
            }
        }

        if (!info.save()) {
            errorMessages.add(ERROR_SAVE_SETTINGS);
            errorMessages.add(DEAL_REBOOT_SERVER);
            return Either.left(errorMessages);
        }

        List<String> error = actualResultList.get().getRight();
        if (!error.isEmpty()) {
            errorMessages.add(ERROR_CREATE_ACTUAL_DATA);
            if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                errorMessages.add(DEAL_CONFIRM_PROCESS_NAME);
            }
            errorMessages.add(DEAL_CONFIRM_INTERVAL);
            errorMessages.add(String.join(LINE_SEPARATOR, error));
            return Either.left(errorMessages);
        }

        return Either.right(actualResultList.get().getLeft().size());
    }

    /**
     * SQL文を生成
     *
     * @param searchType 検索タイプ
     * @param outputType 出力タイプ
     * @param fromDate 検索範囲日時（FROM）
     * @param toDate 検索範囲日時（TO）
     * @return SQL文
     */
    private String createGetActualResultQuery(SEARCH_TYPE searchType, OUTPUT_TYPE outputType, Date fromDate, Date toDate) {

        boolean isNotChangedSearchedDatetime;
        String strLastSearchedActualId;
        if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
            isNotChangedSearchedDatetime = info.isNotChangedSearchedDatetime();
            strLastSearchedActualId = info.getLastSearchedActualId();
        } else {
            isNotChangedSearchedDatetime = info.isNotChangedWorkSearchedDatetime();
            strLastSearchedActualId = info.getWorkLastSearchedActualId();
        }

        StringBuilder sql1 = new StringBuilder();
        if (SEARCH_TYPE.FROM_LAST_SEARCHED.equals(searchType)
                && isNotChangedSearchedDatetime
                && !ACTUAL_ID_DEFAULT.equals(strLastSearchedActualId)) {
            // 検索タイプが「前回実行時から現在を指定」
            // かつプロパティーロード時と変更が無い
            // かつ前回検索実績IDが初期値ではない場合
            sql1.append("actual_id BETWEEN ");
            sql1.append(Integer.parseInt(strLastSearchedActualId) + 1);
            sql1.append(" AND ( ");
            sql1.append("SELECT ");
            sql1.append("last_value ");
            sql1.append("FROM ");
            sql1.append("trn_actual_result_actual_id_seq ");
            sql1.append(") ");
        } else {
            sql1.append("implement_datetime BETWEEN '");
            sql1.append(sdf.format(fromDate));
            sql1.append("' AND '");
            sql1.append(sdf.format(toDate));
            sql1.append("' ");
        }

        StringBuilder sql2 = new StringBuilder();
        if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
            // カンバンのプロパティ情報に「作業コード」が含まれる作業実績の出力
            sql2.append("WITH TAR AS ( ");
            sql2.append("SELECT DISTINCT ");
            sql2.append("(kanban_id) kanban_id ");
            sql2.append("FROM ");
            sql2.append("trn_actual_result ");
            sql2.append("WHERE ");
            sql2.append(sql1.toString());
            sql2.append("AND (rework_num IS NULL OR rework_num = 0) ");
            sql2.append(") ");
            sql2.append("SELECT ");
            sql2.append("tar1.kanban_id ");
            sql2.append(", x.val AS work_code ");
            sql2.append(", tk.kanban_name ");
            sql2.append(", mwf.workflow_name ");
            sql2.append(", mw.work_name ");
            sql2.append(", twk.actual_start_datetime ");
            sql2.append(", twk.actual_comp_datetime ");
            sql2.append(", tar2.implement_datetime ");
            sql2.append(", tar2.actual_id ");
            sql2.append("FROM ");
            sql2.append("TAR tar1 ");
            sql2.append("JOIN trn_kanban tk ");
            sql2.append("ON tk.kanban_id = tar1.kanban_id ");
            sql2.append("JOIN jsonb_to_recordset(tk.kanban_add_info) as x(key TEXT, val TEXT) ");
            sql2.append("ON x.key = '作業コード' ");
            sql2.append("JOIN trn_work_kanban twk ");
            sql2.append("ON tar1.kanban_id = twk.kanban_id ");
            sql2.append("JOIN mst_workflow mwf ");
            sql2.append("ON twk.workflow_id = mwf.workflow_id ");
            sql2.append("JOIN mst_work mw ");
            sql2.append("ON twk.work_id = mw.work_id ");
            sql2.append("LEFT JOIN trn_actual_result tar2 ");
            sql2.append("ON tar2.work_kanban_id = twk.work_kanban_id ");
        } else {
            // 工程のプロパティ情報に「作業コード」が含まれる作業実績の出力
            sql2.append("WITH TAR AS( ");
            sql2.append("SELECT DISTINCT ");
            sql2.append("(work_id) work_id ");
            sql2.append("FROM ");
            sql2.append("trn_actual_result ");
            sql2.append("WHERE ");
            sql2.append(sql1.toString());
            sql2.append(") ");
            sql2.append("SELECT ");
            sql2.append("x.val AS work_code ");
            sql2.append(",MIN(implement_datetime) AS actual_start_datetime ");
            sql2.append(",MAX( ");
            sql2.append("CASE ");
            sql2.append("WHEN tar2.actual_status = 'COMPLETION' ");
            sql2.append("THEN implement_datetime ");
            sql2.append("ELSE NULL ");
            sql2.append("END ");
            sql2.append(")AS actual_comp_datetime ");
            sql2.append(",MAX(tar2.actual_id) AS actual_id ");
            sql2.append("FROM ");
            sql2.append("TAR tar1 ");
            sql2.append("JOIN trn_work_kanban twk ");
            sql2.append("ON tar1.work_id = twk.work_id ");
            sql2.append("JOIN jsonb_to_recordset(twk.work_kanban_add_info) as x(key TEXT, val TEXT) ");
            sql2.append("ON x.key = '作業コード' ");
            sql2.append("LEFT JOIN trn_actual_result tar2 ");
            sql2.append("ON tar2.work_kanban_id = twk.work_kanban_id ");
            sql2.append("WHERE ");
            sql2.append("tar2.rework_num IS NULL OR tar2.rework_num = 0 ");
            sql2.append("GROUP BY ");
            sql2.append("x.val ");
            sql2.append(",twk.work_kanban_id ");
        }

        return sql2.toString();
    }

    /**
     * 実績情報を編集する。
     *
     * @param actualResultList 実績情報リスト
     * @param startWorkNames 開始工程名リスト
     * @param endWorkNames 終了工程名リスト
     * @param searchType 検索タイプ
     * @return 編集後の実績情報リスト
     */
    private Either<String, Tuple<List<Map<String, String>>, List<String>>> createActualResultList(
            List<Map<String, String>> actualResultList,
            List<String> startWorkNames,
            List<String> endWorkNames,
            SEARCH_TYPE searchType) {

        List<Map<String, String>> retList = new ArrayList<>();
        List<String> errorMessage = new ArrayList<>();
        try{
            // DBから取得した実績情報をkanban_idでグループ化
            Map<String, List<Map<String, String>>> kanbanIdGroup
                    = actualResultList
                    .stream()
                    .collect(groupingBy(map -> map.get(KANBAN_ID)));

            for(Map.Entry<String, List<Map<String, String>>> entry : kanbanIdGroup.entrySet()) {
                // 開始工程の実績情報を取得
                List<Map<String, String>> startWorkData = this.getWorkNameData(entry.getValue(), startWorkNames);
                // 終了工程の実績情報を取得
                List<Map<String, String>> endWorkData = this.getWorkNameData(entry.getValue(), endWorkNames);

                if (startWorkData.isEmpty() || endWorkData.isEmpty()) {
                    // 開始工程名または終了工程名が存在しない場合はエラー通知
                    errorMessage.add(this.createErrorMessage(entry.getValue().get(0)));
                    continue;
                }

                Map<String, String> outputData = new HashMap<>(entry.getValue().get(0));
                // 開始日時を取得
                Optional<Date> startDate = this.getStartDatetime(startWorkData);
                startDate.ifPresent(date -> {
                    outputData.replace(ACTUAL_START_DATETIME, sdf3.format(date));
                });

                // 終了日時を取得
                Date endDate = this.getEndDatetime(endWorkData);
                if (Objects.nonNull(endDate)) {
                    if (this.isOutputWork(searchType, endWorkData, info.getLastSearchedActualId())) {
                        // 終了日時を更新
                        outputData.replace(ACTUAL_COMP_DATETIME, sdf3.format(endDate));
                        retList.add(outputData);
                    }
                } else {
                    if (this.isOutputNoEnddateWork(searchType, startWorkData, info.getLastSearchedActualId())) {
                        outputData.replace(ACTUAL_COMP_DATETIME, "");
                        retList.add(outputData);
                    }
                }
            }

            return Either.right(new Tuple<>(retList, errorMessage));
        } catch (Exception e) {
            logger.fatal(e, e);
            return Either.left(e.getMessage());
        }
    }

    /**
     * 実績情報を出力するか判定する。
     *
     * @param searchType 検索タイプ
     * @param workData 工程実績リスト
     * @param lastSearchedActualId 前回検索実績ID
     * @return ture:出力する, false:出力しない
     */
    private boolean isOutputWork(SEARCH_TYPE searchType, List<Map<String, String>> workData, String lastSearchedActualId) {

        if (!SEARCH_TYPE.FROM_LAST_SEARCHED.equals(searchType)
                || ACTUAL_ID_DEFAULT.equals(lastSearchedActualId)) {
            // 検索タイプが「前回実行時から現在」でない または 前回検索実績IDが初期値ではない場合
            return true;
        }

        // 最終実績IDを取得
        OptionalInt optEndActualId
                = workData
                .stream()
                .map(l -> l.getOrDefault(ACTUAL_ID, ACTUAL_ID_DEFAULT))
                .filter(Objects::nonNull)
                .mapToInt(Integer::parseInt)
                .max();

        if (optEndActualId.isPresent()) {
            // 工程の実績IDが前回検索実績IDよりも小さい場合（前回出力している場合）は出力しない
            if (optEndActualId.getAsInt() < Integer.parseInt(lastSearchedActualId)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 終了日時が無い実績情報を出力するか判定する。
     *
     * @param searchType 検索タイプ
     * @param workData 工程実績リスト
     * @param lastSearchedActualId 前回検索実績ID
     * @return ture:出力する, false:出力しない
     */
    private boolean isOutputNoEnddateWork(SEARCH_TYPE searchType, List<Map<String, String>> workData, String lastSearchedActualId) {
        if (SEARCH_TYPE.FROM_LAST_SEARCHED.equals(searchType)
                && !ACTUAL_ID_DEFAULT.equals(lastSearchedActualId)) {
            // 検索タイプが「前回実行時から現在」かつ前回検索実績IDが初期値ではない場合

            // 開始工程の実績IDを取得
            OptionalInt optStartActualId = workData
                    .stream()
                    .map(l -> l.getOrDefault(ACTUAL_ID, ACTUAL_ID_DEFAULT))
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::parseInt)
                    .min();
            if (optStartActualId.isPresent()) {
                // 開始工程の実績IDが前回検索実績IDよりも小さい場合（前回出力している場合）は出力しない
                if(optStartActualId.getAsInt() < Integer.parseInt(lastSearchedActualId)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 工程名が含まれる実績情報を取得する。
     *
     * @param entry 実績情報リスト
     * @param workNames 工程名リスト
     * @return retData 工程名が含まれる実績情報リスト
     */
    private List<Map<String, String>> getWorkNameData(List<Map<String, String>> actualData, List<String> workNames) {
        List<Map<String, String>> retData = new ArrayList<>();
        for (String workName : workNames) {
            // 工程名が含まれる実績情報を取得
            retData.addAll(actualData
                    .stream()
                    .filter(val -> val.get(WORK_NAME).contains(workName))
                    .collect(toList()));
        }
        return retData.stream().distinct().collect(toList());
    }

    /**
     * 最も早い開始日時を返す。
     *
     * @param workData 工程実績情報リスト
     * @return 日付
     */
    private Optional<Date> getStartDatetime(List<Map<String, String>> workData) {
        return workData
                .stream()
                .map(val -> val.get(IMPLEMENT_DATETIME))
                .filter(Objects::nonNull)
                .map(this::strToDate)
                .filter(Objects::nonNull)
                .sorted()
                .findFirst();
    }

    /**
     * 最も遅い完了日時を返す。
     *
     * @param workData 工程実績情報リスト
     * @return 日付
     */
    private Date getEndDatetime(List<Map<String, String>> workData) {
        Date retDate = null;

        // 完了日時有無を判定
        boolean isEndDatetime
                = workData
                .stream()
                .map(val -> val.get(ACTUAL_COMP_DATETIME))
                .anyMatch(Objects::isNull);
        if (isEndDatetime) {
            // 完了していない終了工程があればNULLを返す
            return retDate;
        }

        // 完了日時を取得
        Optional<Date> optEndDatetime = workData
                .stream()
                .map(val -> val.get(ACTUAL_COMP_DATETIME))
                .map(this::strToDate)
                .sorted(Comparator.reverseOrder())
                .findFirst();
        if (optEndDatetime.isPresent()) {
            retDate = optEndDatetime.get();
        }
        return retDate;
    }

    /**
     * 文字列を日付に変換する。
     *
     * @param strDate 日付文字列
     * @return 日付
     */
    private Date strToDate(String strDate) {
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            logger.error(e, e);
            return null;
        }
    }

    /**
     * エラーメッセージを生成する。
     *
     * @param errorMessage エラーメッセージ
     * @param map 実績情報
     * @return エラーメッセージ
     */
    private String createErrorMessage(Map<String, String> map) throws Exception {
        final String kanbanName = map.getOrDefault(KANBAN_NAME, "");
        final String workflowName = map.getOrDefault(WORKFLOW_NAME, "");
        
        StringBuilder sb = new StringBuilder();
        sb.append(LINE_SEPARATOR);
        sb.append("・カンバン名：").append(kanbanName);
        sb.append("、工程順名：").append(workflowName);
        
        return sb.toString();
    }

    /**
     * ファイル名を取得する。
     *
     * @return ファイルパス＋ファイル名
     */
    private String getFullFileName(OUTPUT_TYPE outputType) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        File dir = new File(info.getFolderPath());
        if(!dir.exists()){
            logger.error("Folder access error.");
            return "";
        }
        // ファイル名を生成
        StringBuilder sb = new StringBuilder();
        sb.append(info.getFolderPath());
        sb.append(File.separator);
        if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
            sb.append(FILE_NAME);
        } else {
            sb.append(FILE_NAME2);
        }
        sb.append(df.format(new Date()));
        sb.append(FILE_EXT_CSV);

        return sb.toString();
    }
    
    /**
     * 作業実績ファイル（CSV）を作成する。
     *
     * @param actualResultList 実績情報リスト
     */
    private boolean createCsv(List<Map<String, String>> actualResultList, String fullFileName) {
        try (PrintWriter csvWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fullFileName),CHAR_CODE)))) {
            // ヘッダー
            csvWriter.print(StringUtils.join(CSV_HEADER, DELIMITER));
            csvWriter.print(LINE_SEPARATOR);
            // 実績情報
            for(Map<String, String> map : actualResultList) {
                csvWriter.print(map.get(WORK_CODE));
                csvWriter.print(DELIMITER);
                csvWriter.print(map.get(ACTUAL_START_DATETIME));
                csvWriter.print(DELIMITER);
                csvWriter.print(map.get(ACTUAL_COMP_DATETIME));
                
                csvWriter.print(LINE_SEPARATOR);
            }
            csvWriter.close();
            return true;
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
            return false;
        }
    }
    
    /**
     * エラー発生通知メールを送信する。
     *
     * @param outputType 出力タイプ
     * @param fromDate 日時指定の先頭
     * @param toDate   日時指定の末尾
     * @param messages メッセージリスト
     */
    private void sendErrorMail(OUTPUT_TYPE outputType, Date fromDate, Date toDate, List<String> messages) {
        try {
            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setUser(info.getErrorMailUser());
            prop.setPassword(info.getErrorMailPassword());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);

            String from = info.getErrorMailFrom();
            String to = info.getErrorMailTo();
            String subject = "【adFactory ASPROVA連携】 実績情報出力エラー";

            StringBuilder content = new StringBuilder();
            if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                content.append("adFactory カンバンに「作業コード」が含まれる実績情報出力でエラーが発生しました。");
            } else {
                content.append("adFactory 工程に「作業コード」が含まれる実績情報出力でエラーが発生しました。");
            }
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append("エラー発生日時： ");
            content.append(sdf2.format(new Date()));
            content.append(LINE_SEPARATOR);
            content.append("【実績出力設定】");
            content.append(LINE_SEPARATOR);
            content.append("adFactoryサーバーアドレス： ");
            content.append(info.getAdFactoryAddress());
            content.append(LINE_SEPARATOR);
            content.append("データ取り込み間隔： ");
            content.append(sdf2.format(fromDate));
            content.append(" ～ ");
            content.append(sdf2.format(toDate));
            content.append(LINE_SEPARATOR);
            if (OUTPUT_TYPE.KANBAN_WORK_CODE.equals(outputType)) {
                content.append("開始工程名： ");
                content.append(info.getStartWorkName());
                content.append(LINE_SEPARATOR);
                content.append("終了工程名： ");
                content.append(info.getEndWorkName());
                content.append(LINE_SEPARATOR);
            }
            content.append("フォルダーパス： ");
            content.append(info.getFolderPath());
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append("エラー詳細： ");
            content.append(LINE_SEPARATOR);
            content.append(createMessage(messages));

            MailUtils mail = new MailUtils(prop);
            mail.send(from, to, subject, content.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * メールメッセージ生成
     *
     * @param messages メッセージリスト
     * @return メッセージ
     */
    private String createMessage(List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for(String msg : messages) {
            sb.append(msg);
            sb.append(LINE_SEPARATOR);
        }
        return sb.toString();
    }

    /**
     * 工程に作業コードが含まれる実績情報を編集する。
     *
     * @param actualResultList 実績情報リスト
     * @param serchType 検索タイプ
     * @return 編集後の実績情報リスト
     */
    private Either<String, Tuple<List<Map<String, String>>, List<String>>> createWorkActualResultList(
            List<Map<String, String>> actualResultList,
            SEARCH_TYPE searchType) {

        List<Map<String, String>> retList = new ArrayList<>();
        try{
            for (Map<String, String> actualResult: actualResultList) {
                Map<String, String> outputData = new HashMap<>(actualResult);

                // 開始日時を取得
                Date startDate = null;
                if (Objects.nonNull(actualResult.get(ACTUAL_START_DATETIME))) {
                    startDate = this.strToDate(actualResult.get(ACTUAL_START_DATETIME));
                }
                if (Objects.isNull(startDate)) {
                    continue;
                }
                outputData.replace(ACTUAL_START_DATETIME, sdf3.format(startDate));

                // 完了日時を取得
                Date endDate = null;
                if (Objects.nonNull(actualResult.get(ACTUAL_COMP_DATETIME))) {
                    endDate = this.strToDate(actualResult.get(ACTUAL_COMP_DATETIME));
                }
                List<Map<String, String>> workList = new ArrayList<>();
                workList.add(actualResult);
                if (Objects.nonNull(endDate)) {
                    if (this.isOutputWork(searchType, workList, info.getWorkLastSearchedActualId())) {
                        // 終了日時を更新
                        outputData.replace(ACTUAL_COMP_DATETIME, sdf3.format(endDate));
                        retList.add(outputData);
                    }
                } else {
                    if (this.isOutputNoEnddateWork(searchType, workList, info.getWorkLastSearchedActualId())) {
                        outputData.replace(ACTUAL_COMP_DATETIME, "");
                        retList.add(outputData);
                    }
                }
            }
            return Either.right(new Tuple<>(retList, new ArrayList<>()));
        } catch (Exception e) {
            logger.fatal(e, e);
            return Either.left(e.getMessage());
        }
    }
}
