/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adquery;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Application.Parameters;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import jp.adtekfuji.adFactory.entity.view.ReportOutSummaryInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adquery.common.Constants;
import jp.adtekfuji.adquery.entity.ReportOutSearchResult;
import jp.adtekfuji.adquery.utils.IniFile;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * データ抽出
 * 
 * @author s-heya
 */
public class DataQuery {
    private static final Logger logger = LogManager.getLogger();
    private static final String BUNDLE_NAME = "jp.adtekfuji.adquery.locale.locale";
    private final Parameters parameters;    //exe実行時に渡される引数

    private final Integer DEBUG_TRACE = 0;  //デバッグトレース（1：出力する、0：出力しない）

    private final String srvName;           // サーバー名
    private String fromDate;                // 抽出期間（From）
    private String toDate;                  // 抽出期間（To）
    private final String equipment;         // 設備
    private final String organization;      // 組織
    private final String work;              // 工程順
    private final String model;             // モデル名
    private final String statusParam;       // ステータス
    private final String type;              // 出力ファイル形式
    private final String dest;              // 出力先パス
    private final String host;              // ホスト名
    private final String port;              // ポート名
    private final String dbName;            // データベース名（サービス名）
    private final String user;              // ユーザ名
    private final String pass;              // パスワード
    private boolean clear;                   // 出力時データ削除
    private final String outDataCol;        // 出力データ列
    private final int maxIn;                // 最大取得件数
    private final int maxOut;               // 最大出力件数
    private final String size;              // 検索数
    
    private boolean argumentJudge = false;  // 引数判定
    private int fetchSize;                  // 実績データの取得サイズ
    private File destFile;                  // csvファイル選択パス

    private static final String SEARCH_DATE_FORMAT = "yyyyMMddHHmmss";
    private static final String OUT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final String SJIS_ENCODE = "Shift-JIS";
    private static final String SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";
    private static final String CHARSET = "UTF-8";
    private static final int RANGE_IDS = 100;
    private static final String REST_PATH = "/adFactoryServer/rest";
    private static final int COLUMN_NUM = 13;                           // 基本列数
    private static final String COL_ALL = "ALL";
    
    private ActualResultInfoFacade actualResultFacade = null;
    private WorkKanbanInfoFacade workKanbanFacade = null;

    private Long reportCount = 0L;
    private StringBuilder headerSb = new StringBuilder();               // ヘッダ行
    private StringBuilder rowSb = new StringBuilder();                  // データ行

    private Set<String> addInfos = new LinkedHashSet<>();               // プロパティ名リスト
    private Map<String, String> addInfoMap = new LinkedHashMap<>();     // 追加情報マップ
    private Map<String, Integer> compCntMap = new LinkedHashMap<>();    // 完了数
    private List<KanbanStatusEnum> statusList = new ArrayList<>();      // ステータスリスト
    private final Set<String> reasonSet = new HashSet<>();
    private final ReportOutSearchResult reportData = new ReportOutSearchResult();
    
    /**
     * コンストラクタ
     * 
     * @param parameters パラメータ
     */
    public DataQuery(Parameters parameters) {

        this.parameters = parameters;

        logger.info("Parameters: {}", String.join(" ", parameters.getRaw()));
        
        //サーバー名（REST通信のため、サーバー名にSRV_ADD_PATHを付加する）
        String srvParam = parameters.getNamed().get("svr");
        if (StringUtils.isEmpty(srvParam)) {
            //サーバー名が指定されていない場合はlocalhostを初期値とする
            srvParam = "https://localhost";
        }
        this.srvName = srvParam + REST_PATH;
        // 設備
        this.equipment = parameters.getNamed().get("eq");
        // 組織
        this.organization = parameters.getNamed().get("org");
        // 工程順
        this.work = parameters.getNamed().get("work");
        // モデル名
        this.model = parameters.getNamed().get("model");
        // ステータス
        this.statusParam = parameters.getNamed().get("status");
        // 出力ファイル形式
        this.type = StringUtils.isEmpty(parameters.getNamed().get("type")) ? "csv" : parameters.getNamed().get("type");
        // 出力先パス
        this.dest = StringUtils.isEmpty(parameters.getNamed().get("dest")) ? System.getProperty("user.dir") +"\\adfactory_report.csv" : parameters.getNamed().get("dest");
        // ホスト名
        this.host = parameters.getNamed().get("host");
        // ポート名
        this.port = parameters.getNamed().get("port");
        // データベース名（サービス名）
        this.dbName = parameters.getNamed().get("db");
        // ユーザ名
        this.user = parameters.getNamed().get("user");
        // パスワード
        this.pass = parameters.getNamed().get("pass");
        // 出力時データ削除
        this.clear = "1".equals(parameters.getNamed().get("clear"));
        // 出力データ列（追加情報）
        this.outDataCol = parameters.getNamed().get("col");
        // 最大取得件数
        this.maxIn = StringUtils.isEmpty(parameters.getNamed().get("max")) ? -1 : Integer.valueOf(parameters.getNamed().get("max"));
        // 最大出力件数 (初期値: 10万件)
        this.maxOut = StringUtils.isEmpty(parameters.getNamed().get("lines")) ? 100000 : Integer.valueOf(parameters.getNamed().get("lines"));
        
        // 検索数
        this.size = parameters.getNamed().get("size");
        if (!StringUtils.isEmpty(this.size)) {
            this.fetchSize = Integer.valueOf(this.size);
            if (0 >= this.fetchSize) {
                fetchSize = Integer.valueOf(Constants.REPORT_OUT_SEARCH_MAX_DEFAULT);
            }
        } else {
            fetchSize = Integer.valueOf(Constants.REPORT_OUT_SEARCH_MAX_DEFAULT);
        }

        // 抽出期間（From）
        this.fromDate = parameters.getNamed().get("from");
        // 抽出期間（To）
        this.toDate = parameters.getNamed().get("to");
        
        try {
            // 日付フォーマットチェック用
            if (!StringUtils.isEmpty(this.fromDate)) {
                LocalDateTime fromDateTime = LocalDateTime.parse(this.fromDate, DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
            }

            if (!StringUtils.isEmpty(this.toDate)) {
                LocalDateTime toDateTime = LocalDateTime.parse(this.toDate, DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
            }

            if (!StringUtils.isEmpty(this.statusParam)) {
                String values[] = this.statusParam.split(",");
                for (String value : values) {
                    KanbanStatusEnum status = KanbanStatusEnum.valueOf(value);
                    if (Objects.isNull(status)) {
                        logger.error("Invalid boot parameters: --status=" + this.statusParam);
                        return;
                    }
                    statusList.add(status);
                }
            }
           
            int startPos = 0;
            int endPos;
            String cutRowData = "";
            String searchData = this.outDataCol;

            if (!cutRowData.equals(searchData) && Objects.nonNull(searchData)) {
                while(true){
                    endPos = searchData.indexOf(",", startPos);

                    if ( 0 >= endPos) {
                        addInfos.add(searchData.substring(startPos));
                        break;
                    }

                    cutRowData = searchData.substring(startPos, endPos);
                    addInfos.add(cutRowData);
                    startPos = endPos + 1;
                }
            }

            if (this.type.equals("db")) {
            } else {
                File dir = new File(this.dest).getParentFile();
                if (Objects.nonNull(dir) && !dir.exists()) {
                    dir.mkdirs();
                }

                this.destFile = new File(this.dest);
            }
 
        } catch (Exception ex) {
            // 起動パラメータ不正
            logger.fatal(ex, ex);
            return;
        }
        
        argumentJudge = true;   //引数正常
    }
    
    /**
     * 引数判定の結果を取得する。
     *
     * @return 引数判定結果
     */
    public boolean getArgumentJudge() {
        logger.info("getArgumentJudge():" + this.argumentJudge);
        return this.argumentJudge;
    }
    
    /**
     * 表示用の工程順名を取得する。
     *
     * @param workflowName 工程順名
     * @param workflowRev 工程順の版数
     * @return 表示用の工程順名
     */
    private String formatWorkflowName(String workflowName, Integer workflowRev) {
        return String.format("%s : %d", workflowName, workflowRev);
    }

    /**
     * 実績データを抽出する。
     */
    public void searchActuals() {
        logger.info("searchActual start.");

        boolean isCancel = false;
        
        try {
            // 検索条件作成
            final ReportOutSearchCondition condition = this.createSearchCondition();
            logger.info("ReportOutSearchCondition: {}", condition);
            
            Task task = new Task<Long>() {
                @Override
                protected Long call() throws Exception {
                    // 実績出力情報の件数を取得する
                    return actualResultFacade.reportOutSearchCount(condition);
                }
            
                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        reportCount = this.getValue();
                        logger.info("Report count: " + reportCount);

                        if (Objects.isNull(reportCount)) {
                            // サーバー接続失敗
                            logger.error("Could not connect to server.");
                            System.exit(4);
                        }
                        
                        if (reportCount > 0 && maxIn != 0) {
                            int count = 0 > maxIn ? reportCount.intValue() : (reportCount > maxIn ? maxIn : reportCount.intValue());
                            logger.info("Fetch count: " + count);
                            fetchActuals(condition, count, 0);
                        } else {
                            saveConfig(condition);
                            
                            logger.info("Successful termination.");                             
                            System.exit(0);
                        }
                        
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        System.exit(0);
                    }
                }
                
                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        System.exit(0);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        System.exit(0);
                    } finally {
						//
                    }
                }
            };
            
            logger.info("thread_start");
            new Thread(task).start();            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
            System.exit(0);
        } finally {
            if (isCancel) {
                //
                System.exit(0);
            }
        }
        
    }
                   
     /**
     * 実績出力情報の検索条件を作成する。
     *
     * @return 実績出力情報の検索条件
     */
    private ReportOutSearchCondition createSearchCondition() throws IOException {
        logger.info("createReportOutSearchCondition start.");
        
        // 実施日時でソート
        ReportOutSearchCondition condition = new ReportOutSearchCondition().sortType(ReportOutSearchCondition.ReportOutSortEnum.ACTUAL_ID);

        // 現在日時取得
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
        logger.info("Current date:" + currentDate);
        
        // サーバー名
        if (!StringUtils.isEmpty(this.srvName)) {
            actualResultFacade = new ActualResultInfoFacade(this.srvName);
            workKanbanFacade = new WorkKanbanInfoFacade(this.srvName);
        }
        
        // 設備
        if (!StringUtils.isEmpty(this.equipment)) {
            EquipmentInfoEntity equip = new EquipmentInfoFacade(this.srvName).findName(URLEncoder.encode(this.equipment, CHARSET));
            condition.setEquipmentIdCollection(Arrays.asList(equip.getEquipmentId()));
        }
        
        // 組織
        if (!StringUtils.isEmpty(this.organization)) {
            OrganizationInfoEntity org = new OrganizationInfoFacade(this.srvName).findName(URLEncoder.encode(this.organization, CHARSET));
            condition.setOrganizationIdCollection(Arrays.asList(org.getOrganizationId()));
        }

        // 工程順
        if (!StringUtils.isEmpty(this.work)) {
            Map<Long, String> choiceDatas = new LinkedHashMap();
            WorkflowInfoFacade workFacade = new WorkflowInfoFacade(this.srvName);
            List<WorkflowInfoEntity> workEntity = workFacade.getWorkflowRange(null,null);
           
            for (long flowCnt = 0; flowCnt < workEntity.size(); flowCnt++) {
                WorkflowInfoEntity workIdflow = workEntity.get((int)flowCnt);
                String flowName = workIdflow.getWorkflowName();
                
                if (!StringUtils.isEmpty(flowName)) {
                    int delPos = flowName.lastIndexOf("-del");
                    
                    if (0>=delPos) {
                        //削除済み工程順でないときは、指定した工程順名と一致するか確認する。
                        if (flowName.equals(this.work)) {
                            choiceDatas.put(workIdflow.getWorkflowId(), formatWorkflowName(URLEncoder.encode(this.work, CHARSET), workIdflow.getWorkflowRev()));
                        }
                    } else {
                        //削除済み工程順のときは、"-del"の前を切り出して、指定した工程順名と一致するか確認する、
                        String delName = flowName.substring(0, delPos);
                        if (delName.equals(this.work)) {
                            choiceDatas.put(workIdflow.getWorkflowId(), formatWorkflowName(URLEncoder.encode(this.work, CHARSET), workIdflow.getWorkflowRev()));
                        }
                    }
                }
            }
                  
            condition.setWorkflowIdCollection(new ArrayList(choiceDatas.keySet()));
        }

        // モデル名
        condition.setModelName(this.model);

        // ステータス
        if (!statusList.isEmpty()) {
            condition.setActualStatusCollection(statusList);
        }
        
        // 抽出期間（From）
        if (!StringUtils.isEmpty(this.fromDate)) {
            LocalDateTime dateTime = LocalDateTime.parse(this.fromDate, DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
            Date date = Date.from(ZonedDateTime.of(dateTime, ZoneId.systemDefault()).toInstant());
            condition.setFromDate(date);
        }

        // 抽出期間（To）
        if (!StringUtils.isEmpty(this.toDate)) {
            LocalDateTime dateTime = LocalDateTime.parse(this.toDate, DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
            Date date = Date.from(ZonedDateTime.of(dateTime, ZoneId.systemDefault()).toInstant());
            condition.setToDate(date);
        }
        
        // 抽出期間（From） or 抽出期間（To）が未設定のとき、最後に出力した時間から現在時間までを期間とする.
        if (StringUtils.isEmpty(this.fromDate) || StringUtils.isEmpty(this.toDate)) {
            String from = currentDate;
            String to = currentDate;
            
            // 抽出日時(from)をiniファイルから取得した値に差し替える.
            String iniFilePath = System.getProperty("user.dir") + "\\conf\\adQuery.ini";
            File file = new File(iniFilePath);
            if (file.exists()) {
                IniFile iniFile = new IniFile(iniFilePath);
                from = iniFile.getString("Date", "fromDate", currentDate);
            }
            
            // 抽出日時(from)をiniファイルから取得した値 or 現在日時とする.
            LocalDateTime fromLocalDate = LocalDateTime.parse(from, DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
            Date fromDateParam = Date.from(ZonedDateTime.of(fromLocalDate, ZoneId.systemDefault()).toInstant());
            condition.setFromDate(fromDateParam);
            this.fromDate = from;
            
            // 抽出日時(to)を現在日時とする.
            LocalDateTime toLocalDate = LocalDateTime.parse(to, DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT));
            Date toDateParam = Date.from(ZonedDateTime.of(toLocalDate, ZoneId.systemDefault()).toInstant());
            condition.setToDate(toDateParam);
            this.toDate = to;
        }
        
        return condition;
    }
   
    /**
     * 実績データをCSV形式のテキストに整形する。
     * 
     * @param from
     * @param result 
     */
    private void formatToCSV(long from, ReportOutSearchResult result){
        logger.info("formatToCSV start.");

        String headerBase = "カンバン名,工程順名,工程名,作業者,設備名,実績ステータス,中断理由,遅延理由,実施時間,作業時間,不良理由,不良数,製造番号,標準作業時間,完了数,シリアル番号,モデル名";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ResourceBundle rb = LocaleUtils.load(BUNDLE_NAME);
        
        if (COL_ALL.equalsIgnoreCase(this.outDataCol)) {
            if (from == 0) {
                // ヘッダー行を再初期化
                this.initHeader(headerBase);
            }
            
            for (int row = 0; row < result.getActuals().size(); row++) { 
                ReportOutInfoEntity reportOut = result.getActuals().get(row);
                if (Objects.nonNull(reportOut.getPropertyCollection())) {
                    this.addInfos.addAll(reportOut.getPropertyCollection().stream()
                            .map(o -> o.getActualPropName()).collect(Collectors.toSet()));
                }
            }
        } else if (from == 0) {
            this.headerSb.append(headerBase);
        }
        
        List<ReportOutInfoEntity> actuals = result.getActuals();
        for (ReportOutInfoEntity actual : actuals) {
            // カンバン名
            this.rowSb.append(actual.getKanbanName());
            this.rowSb.append(SEPARATOR);
            // 工程順名
            this.rowSb.append(actual.getWorkflowName());
            this.rowSb.append(":");
            this.rowSb.append(actual.getWorkflowRev());
            this.rowSb.append(SEPARATOR);
            // 工程名
            this.rowSb.append(actual.getWorkName());
            this.rowSb.append(SEPARATOR);
            // 作業者
            this.rowSb.append(actual.getOrganizationName());
            this.rowSb.append(SEPARATOR);
            // 設備名
            this.rowSb.append(actual.getEquipmentName());
            this.rowSb.append(SEPARATOR);
            // 実績ステータス
            this.rowSb.append(rb.getString(actual.getActualStatus().getResourceKey()));
            this.rowSb.append(SEPARATOR);
            // 中断理由
            this.rowSb.append(StringUtils.isEmpty(actual.getInterruptReason()) ? "" : actual.getInterruptReason());
            this.rowSb.append(SEPARATOR);
            // 遅延理由
            this.rowSb.append(StringUtils.isEmpty(actual.getDelayReason()) ? "" : actual.getDelayReason());
            this.rowSb.append(SEPARATOR);
            // 実施時間
            this.rowSb.append(formatter.format(actual.getImplementDatetime()));
            this.rowSb.append(SEPARATOR);
            // 作業時間
            this.rowSb.append(Objects.isNull(actual.getWorkingTime()) ? "" : actual.getWorkingTime() / 1000);
            this.rowSb.append(SEPARATOR);
            // 不良理由
            this.rowSb.append(StringUtils.isEmpty(actual.getDefectReason()) ? "" : actual.getDefectReason());
            this.rowSb.append(SEPARATOR);
            // 不良数
            this.rowSb.append(Objects.isNull(actual.getDefectNum()) ? "" : actual.getDefectNum());
            this.rowSb.append(SEPARATOR);
            // 製造番号
            this.rowSb.append(StringUtils.isEmpty(actual.getProductionNumber()) ? "" : actual.getProductionNumber());
            this.rowSb.append(SEPARATOR);
            // 標準作業時間
            this.rowSb.append(actual.getTaktTime() / 1000);
            this.rowSb.append(SEPARATOR);
            // 完了数
            this.rowSb.append(actual.getCompNum());
            this.rowSb.append(SEPARATOR);
            // シリアル番号
            this.rowSb.append(StringUtils.isEmpty(actual.getSerialNo()) ? "" : actual.getSerialNo());
            this.rowSb.append(SEPARATOR);
            // モデル名
            this.rowSb.append(StringUtils.isEmpty(actual.getModelName()) ? "" : actual.getModelName());

            // 追加情報
            if (Objects.nonNull(actual.getPropertyCollection())) {
                this.addInfoMap = actual.getPropertyCollection().stream()
                        .collect(Collectors.toMap(
                                o -> o.getActualPropName(), 
                                o -> Objects.nonNull(o.getActualPropValue()) ? o.getActualPropValue() : "",
                                (a, b) -> {
                                    logger.warn("Duplicate tag : {}, {}", a, b);
                                    return b; // 重複時は後勝ち
                                }
                        ));
                
                this.addInfos.forEach(propertyName -> {
                    this.rowSb.append(SEPARATOR);
                    if (this.addInfoMap.containsKey(propertyName) && Objects.nonNull(this.addInfoMap.get(propertyName))) {
                        // 改行コードを変換 (adManagerの実績出力と同様)
                        String value = toString(this.addInfoMap.get(propertyName))
                                .replaceAll("\n", "\\\\n")
                                .replaceAll("\r", "\\\\r");
                        this.rowSb.append(value);
                    } else {
                        this.rowSb.append("");
                    }
                });
            }

            this.rowSb.append(LINE_SEPARATOR);            
        }

        logger.info("exportCsv end.");
    }

    /**
     * 実績データをCSV形式のテキストに整形する。
     * 
     * @param from
     * @param result 
     */
    private void formatToCSV2(long from, ReportOutSearchResult result){
        logger.info("formatToCSV2 start");

        final String headerBase = "カンバン名,工程順名,工程名,ステータス,標準作業時間,作業時間,中断時間,開始時間,完了時間,遅延理由,製造番号,モデル名,完了数";

        if (0 == from) {
            this.clear = true; // 追記書き込みは不可
            this.reasonSet.clear();

            if (COL_ALL.equalsIgnoreCase(this.outDataCol)) {
                final Set<String> tags = new HashSet<>();
 
                result.getWorkActuals().forEach(reportOut ->{
                    if (Objects.nonNull(reportOut.GetActualAddInfo())) {
                        tags.addAll(reportOut.GetActualAddInfo().keySet().stream().map(item -> item).collect(Collectors.toSet()));
                    }
                    
                    if (Objects.nonNull(reportOut.getInterruptReasonTimes())) {
                        reasonSet.addAll(reportOut.getInterruptReasonTimes().keySet());
                    }
                });
                
                this.addInfos = tags.stream().sorted().collect(Collectors.toSet());
                
            } else {
                result.getWorkActuals().forEach(reportOut -> {
                    if (Objects.nonNull(reportOut.getInterruptReasonTimes())) {
                        reasonSet.addAll(reportOut.getInterruptReasonTimes().keySet());
                    }
                });
            }
            
            this.headerSb.append(headerBase);
            
            reasonSet.forEach(reason -> {
                this.headerSb.append(SEPARATOR);
                this.headerSb.append(reason);
            });
            
            this.addInfos.forEach(tag -> {
                this.headerSb.append(SEPARATOR);
                this.headerSb.append(tag);
            });
        }

        result.getWorkActuals().forEach(reportOut -> { 

            // 指定された工程ステータスに含まれる実績データを出力
            boolean output = statusList.isEmpty() ? true : statusList.contains(reportOut.getWorkStatus());
            
            if (output) {
                 // カンバン名
                rowSb.append(reportOut.getKanbanName());
                rowSb.append(SEPARATOR);

                // 工程順名
                rowSb.append(formatWorkflowName(reportOut.getWorkflowName(), reportOut.getWorkflowRev()));
                rowSb.append(SEPARATOR);

                // 工程名
                StringProperty workNameProperty = reportOut.workNameProperty();

                // 工程名称に@が含まれていたら、@の後ろを工程名として切り出す.
                String workNamePropertyName = "";
                if (!StringUtils.isEmpty(workNameProperty.get())) {
                    int splitPos = workNameProperty.get().lastIndexOf("@");

                    if (0 < splitPos) {
                        splitPos = splitPos + 1;
                        workNamePropertyName = workNameProperty.get().substring(splitPos);
                    } else {
                        workNamePropertyName = workNameProperty.get();
                    }
                }

                rowSb.append(workNamePropertyName);
                rowSb.append(SEPARATOR);

                // 工程ステータス
                String statusResourceKey = reportOut.getWorkStatus().getResourceKey();

                ResourceBundle rb = LocaleUtils.load(BUNDLE_NAME);
                if (Objects.nonNull(rb)) {
                    String statusName = rb.getString(statusResourceKey);
                    rowSb.append(statusName);
                    rowSb.append(SEPARATOR);
                } else {
                    rowSb.append(reportOut.getWorkStatus());
                    rowSb.append(SEPARATOR);
                }

                // 標準作業時間
                int standardTime = 0;
                if (Objects.nonNull(reportOut.getTaktTime())) {
                    standardTime = reportOut.getTaktTime() / 1000;
                }
                rowSb.append(standardTime);
                rowSb.append(SEPARATOR);

                // 作業時間（累計）
                long workTime = 0;
                if (Objects.nonNull(reportOut.getSumTimes())) {
                    workTime = reportOut.getSumTimes() / 1000;
                }
                rowSb.append(workTime);
                rowSb.append(SEPARATOR);

                // 中断時間
                int interrruptTime = 0;
                if (Objects.nonNull(reportOut.getInterruptTimes())) {
                    interrruptTime = reportOut.getInterruptTimes() / 1000;
                }
                rowSb.append(interrruptTime);
                rowSb.append(SEPARATOR);

                //開始時間
                if (Objects.nonNull(reportOut.getStartDatetime())) {
                    Date startDate = reportOut.getStartDatetime();
                    SimpleDateFormat sdfStart = new SimpleDateFormat(OUT_DATE_FORMAT);
                    rowSb.append(sdfStart.format(startDate));
                    rowSb.append(SEPARATOR);  
                } else {
                    rowSb.append(SEPARATOR); 
                }

                // 完了時間
                if (Objects.nonNull(reportOut.getCompDatetime())) {
                    Date compDate = reportOut.getCompDatetime();
                    SimpleDateFormat sdfComp = new SimpleDateFormat(OUT_DATE_FORMAT);
                    rowSb.append(sdfComp.format(compDate));
                    rowSb.append(SEPARATOR);  
                } else {
                    rowSb.append(SEPARATOR); 
                }      

                // 遅延理由
                if (Objects.nonNull(reportOut.getDelayReason())) {
                    rowSb.append(reportOut.getDelayReason());
                    rowSb.append(SEPARATOR);
                } else {
                    rowSb.append(SEPARATOR);
                }

                // 製造番号
                if (Objects.nonNull(reportOut.getProductionNumber())) {
                    rowSb.append(reportOut.getProductionNumber());
                    rowSb.append(SEPARATOR);
                } else {
                    rowSb.append(SEPARATOR);
                }

                // モデル名
                if (Objects.nonNull(reportOut.getModelName())) {
                    rowSb.append(reportOut.getModelName());
                    rowSb.append(SEPARATOR);
                } else {
                    rowSb.append(SEPARATOR);
                }

                // 完了数
                compCntMap.clear();
                Boolean compMatch = false;

                if (Objects.nonNull(reportOut.getCompCnt())) {
                    Set<Map.Entry<String, Integer>> compCntItems = reportOut.getCompCnt().entrySet();

                    compCntItems.stream().forEach(item -> {                                            
                        compCntMap.put(item.getKey(), item.getValue());       //工程名,完了数
                    });

                    for (Map.Entry<String, Integer> compList : compCntMap.entrySet()) {
                        String compWorkKey;    //完了数の工程名（Key名）
                        String compWorkName;   //完了数の工程名
                        compWorkKey = compList.getKey();

                        //工程名称に@が含まれていたら、@の後ろを工程名として切り出す.                        
                        int splitWorkPos = compWorkKey.lastIndexOf("@");
                        if (0 < splitWorkPos) {
                            compWorkName = compWorkKey.substring(splitWorkPos);
                        } else {
                            compWorkName = compWorkKey;
                        }

                        if (!StringUtils.isEmpty(compWorkName)) {
                            if (compWorkName.equals(workNamePropertyName)) {
                                rowSb.append(compList.getValue());
                                rowSb.append(SEPARATOR);
                                compMatch = true;
                                break;
                            }
                        }
                    }
                }

                if (false == compMatch) {
                    rowSb.append("0");
                    rowSb.append(SEPARATOR);
                }                

                // 中断時間
                if (Objects.nonNull(reportOut.getInterruptReasonTimes())) {
                    reasonSet.forEach(reason -> {
                        if (reportOut.getInterruptReasonTimes().containsKey(reason)) {
                            rowSb.append(reportOut.getInterruptReasonTimes().get(reason));
                        } else {
                            rowSb.append("0");
                        }
                        rowSb.append(SEPARATOR);
                    });
                } else {
                    reasonSet.forEach(reason -> {
                        rowSb.append("0");
                        rowSb.append(SEPARATOR);
                    });
                }

                // 追加情報
                if (Objects.nonNull(reportOut.GetActualAddInfo())) {
                    addInfos.forEach(tag -> {
                        if (reportOut.GetActualAddInfo().containsKey(tag) 
                                && !StringUtils.isEmpty(reportOut.GetActualAddInfo().get(tag))) {
                            // 改行コードを変換 (adManagerの実績出力と同様)
                            String value = toString(reportOut.GetActualAddInfo().get(tag))
                                    .replaceAll("\n", "\\\\n")
                                    .replaceAll("\r", "\\\\r");
                            rowSb.append(value);
                        }
                        rowSb.append(SEPARATOR);
                    });
                } else {
                    addInfos.forEach(tag -> {
                        rowSb.append(SEPARATOR);
                    });
                    
                }

                rowSb.append(LINE_SEPARATOR);
            }
        });
    }
    
    /**
     * テキストをファイルに書き出す。
     */
    private void writeFile() {
        logger.info("writeFile strat.");
        
        if (Objects.isNull(this.destFile)) {
            logger.error("Unknown error.");
            return;
        }

        if (!this.clear && this.destFile.exists() && this.destFile.length() > 0) {
            
            File file = new File(this.destFile.getPath()+ ".temp");
            long count = 0;

            try {
                Files.copy(this.destFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                long lineCount = Files.lines(this.destFile.toPath(), Charset.forName(SJIS_ENCODE)).count() - 1;
                count = lineCount + this.reportCount - this.maxOut;
                
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
            
            // ヘッダー行の書き換え
            try (BufferedReader reader =  new BufferedReader(new InputStreamReader(new FileInputStream(file), SJIS_ENCODE));
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.destFile), SJIS_ENCODE)));
                LineNumberReader lnReader = new LineNumberReader(reader)) {

                String line = lnReader.readLine(); // ヘッダー行を読み捨てる
                writer.println(headerSb.toString());
                
                while (count > 0) {
                    line = lnReader.readLine();
                    count--;
                }

                while ((line = lnReader.readLine()) != null) {
                    writer.println(line);
                }

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
            
            file.delete();
        }

        boolean append = !clear;
        if (!this.destFile.exists() || 0 == this.destFile.length()) {
            append = false;
        }
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile, append), SJIS_ENCODE))) {
          
            if (!append) {
                headerSb.append(LINE_SEPARATOR);
                writer.write(headerSb.toString());
            }

            writer.write(rowSb.toString());
            writer.close();

        } catch (Exception ex) {
            logger.fatal(ex, ex);                              
            System.exit(0);
        }
    }
    
    /**
     * ヘッダー行を初期化する。
     * 
     * @param headerBase 
     */
    private void initHeader(String headerBase) {
        this.headerSb = new StringBuilder();
        this.headerSb.append(headerBase);
        this.addInfos.clear();

        if (this.clear
            || !this.destFile.exists()
            || this.destFile.length() <= 0) {
            return;
        }
       
        String[] columnBase = headerBase.split(SEPARATOR);

        // 前回出力時のCSVファイルからタグを読み込む
        try (BufferedReader reader = new BufferedReader(new FileReader(this.destFile))) {
            String line = reader.readLine();
            String[] columns = line.split(SEPARATOR);
            for (int i = columnBase.length; i < columns.length; i++) {
                this.addInfos.add(columns[i]);
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);                              
        }
    }
    
    private void saveConfig(ReportOutSearchCondition condition) {
        try {
            Date nextTime = DateUtils.addSeconds(condition.getToDate(), 1);

            String path = System.getProperty("user.dir") + "\\conf\\";
            String iniFilePath = path + "adQuery.ini";
        
            // INIファイルに最終取得日時を保存する
            File file = new File(iniFilePath);
            if (!file.exists()) {
                File fileDir = new File(path);
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }

                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                try (Writer fileWriter = new BufferedWriter(osw)) {
                    fileWriter.write("[Date]\n");
                    fileWriter.write("fromDate=20230101101030\n");
                }
            }

            LocalDateTime ldt = LocalDateTime.ofInstant(nextTime.toInstant(), ZoneId.systemDefault());
            String nextTimeStr = DateTimeFormatter.ofPattern(SEARCH_DATE_FORMAT).format(ldt);

            IniFile iniFile = new IniFile(iniFilePath);
            iniFile.SetString("Date", "fromDate", nextTimeStr);
            iniFile.write();
            logger.info("Next time: " + nextTimeStr);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 実績出力情報を取得して、リスト表示を更新する。
     *
     * @param condition 実績データの検索条件
     * @param count 実績データの件数
     * @param from 実績データの抽出範囲
     */
    private void fetchActuals(ReportOutSearchCondition condition, int count, long from) {
        logger.info("fetchActuals start: condition={}, count={}, from={}", condition, count, from);
        
        try {
            Task task = new Task<ReportOutSearchResult>() {
                @Override
                protected ReportOutSearchResult call() throws Exception {
                    return fetchThread(condition, count, from);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    boolean isEnd = true;
                    
                    try {
                        ReportOutSearchResult result = this.getValue();
                        
                        // 検索結果を出力
                        if (type.equals("csv")) {
                            formatToCSV(from, result);
                        } else if (type.equals("csv2")) {
                            reportData.getActuals().addAll(result.getActuals());
                            reportData.getWorkActuals().addAll(result.getWorkActuals());
                        }
                        
                        long form = result.getActualsTo() + 1;
                        
                        if (form < count) {
                            // 継続
                            fetchActuals(condition, count, form);
                            isEnd = false;
                        } else {
                            if (type.equals("csv2")) {
                                formatToCSV2(0, reportData);
                            } else {
                                addInfos.forEach(tag -> {
                                    headerSb.append(SEPARATOR);
                                    headerSb.append(tag);
                                });

                            }

                            // 全件取得完了
                            writeFile();
                            saveConfig(condition);

                            logger.info("Successful termination.");                            
                            System.exit(0);
                        }
                        
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        System.exit(0);
                    } finally {
                        if (isEnd) {
                            //
                            System.exit(0);
                        }
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        System.exit(0);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                        System.exit(0);
                    } finally {
                        //
                        System.exit(0);
                    }
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //
            System.exit(0);
        }
    }
    
    /**
     * 実績データを取得する。
     * 
     * @param condition 検索条件
     * @param count
     * @param from 
     * @return 実績データ
     */
    private ReportOutSearchResult fetchThread(ReportOutSearchCondition condition, int count, long from) {
        logger.info("fetchThread start.");

        ReportOutSearchResult result = new ReportOutSearchResult();

        List<ReportOutInfoEntity> reportData = new ArrayList<>();
        List<ReportOutSummaryInfoEntity> workReportData = new ArrayList<>();
        
        long to = from + fetchSize - 1;
        if (to > (count - 1)) {
            to = count - 1;
        }

        List<ReportOutInfoEntity> actuals = actualResultFacade.reportOutSearch(condition, from, to);

        // 実績プロパティ一覧の取得により、追加情報(JSON)からの変換を行なう。
        for (ReportOutInfoEntity actual : actuals) {
            actual.getPropertyCollection();
        }

        reportData.addAll(actuals);
        result.setActuals(reportData);
        result.setActualsTo(to);

        if (!type.equals("csv2")) {
            return result;
        }
        
        // 一覧に含まれる工程カンバン
        List<Long> ids = new ArrayList<>();
        reportData.stream().forEach((entity) -> {
            if (ids.indexOf(entity.getFkWorkKanbanId()) < 0) {
                ids.add(entity.getFkWorkKanbanId());
            }
        });

        List<WorkKanbanInfoEntity> workKanbanEntities = new ArrayList();
        for (int fromIndex = 0; fromIndex < ids.size(); fromIndex += RANGE_IDS) {
            int toIndex = fromIndex + RANGE_IDS;
            if (toIndex >= ids.size()) {
                toIndex = ids.size();
            }
            List<Long> findIds = ids.subList(fromIndex, toIndex);
            List<WorkKanbanInfoEntity> workKanbans = workKanbanFacade.find(findIds);
            workKanbanEntities.addAll(workKanbans);
        }
        
        // 工程実績情報
        workKanbanEntities.stream()
            .forEach(workKanban -> {
                ReportOutSummaryInfoEntity workReport = new ReportOutSummaryInfoEntity(workKanban);

                Map<String, String> _addInfoMap = new LinkedHashMap<>();
                _addInfoMap.clear();

                List<ReportOutInfoEntity> list = reportData.stream()
                        .filter(o -> o.getFkKanbanId().equals(workKanban.getFkKanbanId()) && o.getFkWorkId().equals(workKanban.getFkWorkId()))
                        .sorted(Comparator.comparing(ReportOutInfoEntity::getImplementDatetime).reversed())
                        .collect(Collectors.toList());

                if (list.size() > 0) {                        
                    workReport.setKanbanName(list.get(0).getKanbanName());
                    workReport.setWorkflowName(list.get(0).getWorkflowName());
                    workReport.setWorkflowRev(list.get(0).getWorkflowRev());
                    workReport.setWorkName(list.get(0).getWorkName());
                    workReport.setProductionNumber(list.get(0).getProductionNumber());
                    workReport.setModelName(list.get(0).getModelName());
                    
                    workReport.setOrganizationName(list.get(0).getOrganizationName());
                    workReport.setEquipmentName(list.get(0).getEquipmentName());

                    // 完了数
                    List<ReportOutInfoEntity> compCntList = new ArrayList<>();
                    compCntList.addAll(list.stream()
                            .filter(o -> (!StringUtils.isEmpty(o.getWorkName())))
                            .collect(Collectors.toList()));

                    if (compCntList.size() > 0) {
                        // 工程毎に完了数を取得する
                        Map<String, Integer> compTims = compCntList.stream()
                                .collect(Collectors.groupingBy(ReportOutInfoEntity::getWorkName,
                                        Collectors.summingInt(ReportOutInfoEntity::getCompNum)));
                        workReport.setCompCnt(compTims);
                    }

                    // 追加情報
                    List<ReportOutInfoEntity> addInfoList = new ArrayList<>();
                    addInfoList.addAll(list);

                    if (addInfoList.size() > 0) {
                        addInfoList.stream().forEach((resultEntity) -> {
                            resultEntity.getPropertyCollection().stream().forEach((propertyEntity) -> {
                                _addInfoMap.put(propertyEntity.getActualPropName(), propertyEntity.getActualPropValue());
                            });
                        });

                        workReport.setActualAddInfo(_addInfoMap);
                    }

                    Optional<ReportOutInfoEntity> delayReason = list.stream().filter(report -> Objects.nonNull(report.getDelayReason())).findFirst(); 
                    if (delayReason.isPresent()) {
                        workReport.setDelayReason(delayReason.get().getDelayReason());
                    }

                    // 中断理由
                    List<ReportOutInfoEntity> interruptReasonsList = new ArrayList<>();
                    interruptReasonsList.addAll(list.stream()
                            .filter(o -> (!StringUtils.isEmpty(o.getInterruptReason()) && KanbanStatusEnum.WORKING.equals(o.getActualStatus())))
                            .collect(Collectors.toList()));

                    if (interruptReasonsList.size() > 0) {
                        // 中断理由毎に中断時間を取得する
                        Map<String, Integer> interruptTims = interruptReasonsList.stream()
                                .collect(Collectors.groupingBy(ReportOutInfoEntity::getInterruptReason,
                                        Collectors.summingInt(ReportOutInfoEntity::getNonWorkTime)));
                        workReport.setInterruptReasonTimes(interruptTims);

                        // 中断時間合計
                        int totalInterruptTime = interruptReasonsList.stream()
                                .mapToInt(ReportOutInfoEntity::getNonWorkTime)
                                .sum();

                        workReport.setInterruptTimes(totalInterruptTime);
                    }
                }

                // 表示条件
                if (Objects.nonNull(workReport)) {
                    workReportData.add(workReport);
                }
            });
        
        result.setWorkActuals(workReportData);

        return result;
    }

    /**
     * 文字列に変換する
     * 
     * @param value オブジェクト
     * @return 文字列
     */
    private String toString(Object value) {
        return Objects.nonNull(value) ? Objects.toString(value) : "";
    }
}
