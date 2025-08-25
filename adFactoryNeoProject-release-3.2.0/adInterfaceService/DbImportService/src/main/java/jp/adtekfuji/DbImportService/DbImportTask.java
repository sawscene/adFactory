/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.DbImportService;

import adtekfuji.clientservice.KanbanHierarchyInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimerTask;
import static jp.adtekfuji.DbImportService.Constants.DB_HOST_NAME;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_GOKI;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_KANBAN_NAME;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_KANBAN_STS;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_KEIKAKU_BI;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_KISYU_ID;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_LINE;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_SEIBAN;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_SEIBAN_KBN;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_STS_DETAILS;
import static jp.adtekfuji.DbImportService.Constants.DB_ITEM_VIEW_NAME;
import static jp.adtekfuji.DbImportService.Constants.DB_KANBAN_TABLE;
import static jp.adtekfuji.DbImportService.Constants.DB_PORT;
import static jp.adtekfuji.DbImportService.Constants.DB_SID;
import static jp.adtekfuji.DbImportService.Constants.DB_USER_NAME;
import static jp.adtekfuji.DbImportService.Constants.DB_USER_PASS;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.DbConnectorOrcl;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.WorkKanbanTimeReplaceUtils;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.WorkPlanWorkflowProcess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 読み込みと登録を行うタスク
 *
 * @author ka.makihara 2019/09/25
 */
public class DbImportTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger();
    private final ResourceBundle rb = ResourceBundle.getBundle("locale.locale");

    private DbConnectorOrcl    dbAccess;

    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final KanbanHierarchyInfoFacade kanbanHierarchyInfoFacade = new KanbanHierarchyInfoFacade();
    private final WorkflowInfoFacade workflowInfoFacade = new WorkflowInfoFacade();
    /**
     * コンストラクタ
     * 
     * @param defaultPath
     * @param importFormatInfo 
     */
    DbImportTask() throws IOException {
        this(false);
    }

    /**
     * コンストラクタ
     * 
     * @param ignoreSameKanban 
     */
    DbImportTask(boolean ignoreSameKanban) throws IOException {
        //プロパティ読み込み.
        AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
        AdProperty.load("adInterface.properties");
    }

    /**
     * 指定されたプロパティ値の取得
     * @param name
     * @return 
     */
    private String getProp(String name){
        final Properties properties = AdProperty.getProperties();
        return properties.getProperty(name);
    }

    /**
     * DBアクセス・select のクエリー文字列の生成
     * @return 
     */
    private String createSelectQuery(){
        //"select カンバン名称, 表示名称, 製番,背番号,号機,定格,日別のシーケンス,組立てライン,仕込_注文区分,機種ID, 計画日時,取り込みステータス from TABLE1"
        final Properties properties = AdProperty.getProperties();
        return new StringBuilder()
                .append("SELECT ")
                .append(properties.getProperty(DB_ITEM_SEIBAN)).append(",")        //製番
                .append(properties.getProperty(DB_ITEM_GOKI)).append(",")          //号機
                .append(properties.getProperty(DB_ITEM_KANBAN_NAME)).append(",")   //カンバン名称
                .append(properties.getProperty(DB_ITEM_VIEW_NAME)).append(",")     //表示名称
                .append(properties.getProperty(DB_ITEM_LINE)).append(",")          //製造ライン
                .append(properties.getProperty(DB_ITEM_SEIBAN_KBN)).append(",")    //製番区分
                .append(properties.getProperty(DB_ITEM_KISYU_ID)).append(",")      //機種ID
                .append(properties.getProperty(DB_ITEM_KEIKAKU_BI)).append(",")    //カンバンの作業計画日時
                .append(properties.getProperty(DB_ITEM_KANBAN_STS))                 //カンバンの取り込みステータス
                .append(" FROM ")
                .append(properties.getProperty(DB_KANBAN_TABLE))
                .append(" WHERE ")
                .append(properties.getProperty(DB_ITEM_KANBAN_STS)).append(" IS NULL")
                .append(" OR ")
                .append(properties.getProperty(DB_ITEM_KANBAN_STS)).append(" = 0")
                .toString();
    }
    
    /**
     * DBアクセス・取り込みステータスを更新するためのクエリー文字列生成
     * @return 
     */
    private String createUpdateQuery(){
        //"UPDATE table1 SET KANBAN_STS = ?"
        return new StringBuilder()
                .append("UPDATE ")
                .append(getProp(DB_KANBAN_TABLE))
                .append(" SET ")
                .append( getProp(DB_ITEM_KANBAN_STS) )
                .append(" = ?")
                .toString();
    }
    private String createStatusDetailQuery(String kanbanName){
        //"UPDATE table1 SET STATUS_DETAIL = ?, STS_DETAILS=? WHERE kanbanName=カンバン名"
        return new StringBuilder()
                .append("UPDATE ")
                .append(getProp(DB_KANBAN_TABLE))
                .append(" SET ")
                .append(getProp(DB_ITEM_KANBAN_STS))
                .append(" = ?,")
                .append( getProp(DB_ITEM_STS_DETAILS) )
                .append(" = ?")
                .append(" WHERE ")
                .append( getProp(DB_ITEM_KANBAN_NAME))
                .append(" = '").append(kanbanName).append("'")
                .toString();
    }

    /**
     * 工程順名情報の取得
     * @param workflowName
     * @param rev
     * @return WorkflowInfoEntity
     * @throws UnsupportedEncodingException 
     */
    private WorkflowInfoEntity findWorkflow(final String workflowName, final String rev) throws UnsupportedEncodingException {
        WorkflowInfoEntity workflow;
        if (Objects.nonNull(rev) && !rev.isEmpty()) {
            workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, "UTF-8"), Integer.valueOf(rev));
        } else {
            workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, "UTF-8"));
        }
        return workflow;
    }

    /**
     * カンバン階層
     * @param hierarchyName
     * @return  カンバン階層ID 
     * @throws java.io.UnsupportedEncodingException 
     */
    public Long getkanbanHierarchyID(String hierarchyName) throws UnsupportedEncodingException
    {
        KanbanHierarchyInfoEntity kanbanHierarchy = kanbanHierarchyInfoFacade.findHierarchyName(URLEncoder.encode(hierarchyName, "UTF-8"));

        return kanbanHierarchy.getKanbanHierarchyId();
    }

    /**
     * 
     * @param workflowName
     * @return
     * @throws UnsupportedEncodingException 
     */
    public Long getWorkflowID(String workflowName) throws UnsupportedEncodingException{
        WorkflowInfoEntity workflow;

        workflow = findWorkflow(workflowName, null);
        return workflow.getWorkflowId();
    }

    /**
     * カンバンステータスを指定した値に変更する
     * @param kanban
     * @param st
     * @return boolean(ステータス変更OK:true)
     * @throws Exception 
     */
    public boolean setKanbanStatus(KanbanInfoEntity kanban, KanbanStatusEnum st) throws Exception
    {
        KanbanStatusEnum kanbanStatus;

        if( Objects.isNull(kanban.getKanbanId()) ){
            return false;
        }
        kanbanStatus = kanban.getKanbanStatus();

        // カンバンステータスが「中止(Suspend)」，「その他(Other)」，「完了(Completion)」の場合は更新しない
        //      ※．KanbanStatusEnumは、「中止(Suspend)」が INTERRUPT で、「一時中断(Interrupt)」が SUSPEND なので注意。
        if (kanbanStatus.equals(KanbanStatusEnum.INTERRUPT) ||
            kanbanStatus.equals(KanbanStatusEnum.OTHER)     ||
            kanbanStatus.equals(KanbanStatusEnum.COMPLETION) ) {
            // 更新できないカンバンのためスキップ
            return false;
        }
        if (!kanbanStatus.equals(KanbanStatusEnum.PLANNING)) {
            kanban.setKanbanStatus(KanbanStatusEnum.PLANNING);
            ResponseEntity updateStatusRes = kanbanInfoFacade.update(kanban);
            if (Objects.isNull(updateStatusRes) || !updateStatusRes.isSuccess()) {
                // 更新できないカンバンのためスキップ (ステータス変更できない状態だった)
                return false;
            }
        }
        kanban.setKanbanStatus(st);

        return  true;
    }

    /**
     * 工程順名を取得する。
     * 
     * @param rset
     * @return
     * @throws SQLException 
     */
    private String getWorkflowName(ResultSet rset) throws SQLException {
        // "<組立ライン>␣<製番区分>␣<機種ID>"
        String line  = rset.getString(getProp(DB_ITEM_LINE));
        String seiban = rset.getString(getProp(DB_ITEM_SEIBAN_KBN));
        String kisyuId = rset.getString(getProp(DB_ITEM_KISYU_ID));
        String workflowName = line + " " + seiban + " " + kisyuId;
        return workflowName;
    }

    /**
     * 文字列を日時に変換する。
     *
     * @param value 文字列
     * @return 日時
     */
    public static Date stringToDateTime(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.parse(value);
        } catch (ParseException e) {
            return new Date();
        }
    }
    
    /**
     * カンバンを生成する
     * @param rset
     * @return
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws Exception 
     */
    private KanbanInfoEntity createKanban(ResultSet rset) throws SQLException, UnsupportedEncodingException, Exception
    {
        KanbanInfoEntity kanban;

        String kanbanName = rset.getString( getProp(DB_ITEM_KANBAN_NAME) ); //カンバン名称
        String dateStr    = rset.getString( getProp(DB_ITEM_KEIKAKU_BI));   //計画日時
        String hierarchy  = rset.getString( getProp(DB_ITEM_LINE));         //階層名(組立てライン)
        String subName    = rset.getString( getProp(DB_ITEM_VIEW_NAME) );   //表示名称
        String kisyuId    = rset.getString( getProp(DB_ITEM_KISYU_ID) );    //機種ID

        // カンバン階層
        Long kanbanHierarchyId = getkanbanHierarchyID(hierarchy);

        // 工程順の確認
        //  "組立てライン","機種ID","仕込_注文" から工程順名を作る
        String workflowName = getWorkflowName(rset);
        Long workflowId = getWorkflowID(workflowName);

        // 開始予定日時
        Date startDatetime = stringToDateTime(dateStr);

        List<KanbanInfoEntity> kanbans;

        KanbanSearchCondition condition = new KanbanSearchCondition()
            .kanbanName(kanbanName)
            .workflowId(workflowId);

        //カンバンを検索する
        kanbans = kanbanInfoFacade.findSearch(condition);
        if( kanbans.isEmpty() ){
            //該当するカンバンは登録されていない
            kanban = new KanbanInfoEntity();
        }
        else{
            kanban = kanbans.get(0);
        }

        // 登録済のカンバンの場合は、カンバンステータスを一旦「計画中(Planning)」に戻す。
        if( Objects.nonNull(kanban.getKanbanId()) ){
            if( setKanbanStatus(kanban,KanbanStatusEnum.PLANNING) == false ){
                //ステータスが「計画中」に変更できない
                return kanban;
            }
        }

        kanban.setKanbanName(kanbanName);
        kanban.setParentId(kanbanHierarchyId);
        kanban.setFkWorkflowId(workflowId);
        // 開始予定日時
        if (Objects.nonNull(startDatetime)) {
            kanban.setStartDatetime(startDatetime);
        }
        kanban.setModelName(kisyuId);       //モデル名
        kanban.setKanbanSubname(subName);   //カンバンサブ名
                                            // adProductで、カンバンサブ名が設定されている場合はこれを表示する
        return kanban;
    }

    /**
     * 追加情報の設定
     * @param kanban
     * @param rset
     * @return
     * @throws SQLException 
     */
    public KanbanInfoEntity setKanbanProp(KanbanInfoEntity kanban,ResultSet rset) throws SQLException
    {
        String seiban    = rset.getString(getProp(DB_ITEM_SEIBAN));    //製番
        String goki      = rset.getString(getProp(DB_ITEM_GOKI));      //号機
        String seibanKbn = rset.getString(getProp(DB_ITEM_SEIBAN_KBN));//製番区分
        String kisyu     = rset.getString(getProp(DB_ITEM_KISYU_ID));  //機種ID
        Long kanbanID    = kanban.getKanbanId();

        //既に登録されている追加情報の数
        int nn = kanban.getPropertyCollection().size();
        Long cnt = Long.valueOf(nn);

        ///追加情報
        List<KanbanPropertyInfoEntity> props = new ArrayList<>();

        /*
        props.add( new KanbanPropertyInfoEntity(cnt+1,kanbanID,getProp(DB_ITEM_SEIBAN),    CustomPropertyTypeEnum.TYPE_STRING,seiban,   nn+1) );//製番
        props.add( new KanbanPropertyInfoEntity(cnt+2,kanbanID,getProp(DB_ITEM_GOKI),      CustomPropertyTypeEnum.TYPE_STRING,goki,     nn+2) );//号機
        props.add( new KanbanPropertyInfoEntity(cnt+3,kanbanID,getProp(DB_ITEM_SEIBAN_KBN),CustomPropertyTypeEnum.TYPE_STRING,seibanKbn,nn+3) );//製番区分
        props.add( new KanbanPropertyInfoEntity(cnt+4,kanbanID,getProp(DB_ITEM_KISYU_ID),  CustomPropertyTypeEnum.TYPE_STRING,kisyu,    nn+4) );//機種ID
        */

        // 追加情報の項目名はローマ字より日本語表記の方が良いと思うが
        // データ定義が無いのでとりあえず、直書きする
        props.add( new KanbanPropertyInfoEntity(cnt+1,kanbanID,"製番",    CustomPropertyTypeEnum.TYPE_STRING,seiban,   nn+1) );//製番
        props.add( new KanbanPropertyInfoEntity(cnt+2,kanbanID,"号機",    CustomPropertyTypeEnum.TYPE_STRING,goki,     nn+2) );//号機
        props.add( new KanbanPropertyInfoEntity(cnt+3,kanbanID,"製番区分",CustomPropertyTypeEnum.TYPE_STRING,seibanKbn,nn+3) );//製番区分
        props.add( new KanbanPropertyInfoEntity(cnt+4,kanbanID,"機種ID",  CustomPropertyTypeEnum.TYPE_STRING,kisyu,    nn+4) );//機種ID

        kanban.setPropertyCollection(props);

        return kanban;
    }

    private void updateDbStatus(DbConnectorOrcl db, String kanbanName, int status, String detail) throws SQLException {

        try (PreparedStatement ps = db.getPreparedStatement(createStatusDetailQuery(kanbanName))) {
            ps.setInt(1, status);
            ps.setString(2, detail);
            if (ps.executeUpdate() > 0){
                logger.info("DB update fail");
            }
        }
    }

    /**
     * タイマータスクにより生産計画の読み込みを実行する。
     */
    @Override
    public void run() {
        try {
            logger.info("DbImportTask start.");

            //プロパティ読み込み.
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adInterface.properties");
            String host = getProp(DB_HOST_NAME);
            String port = getProp(DB_PORT);
            String user = getProp(DB_USER_NAME);
            String pass = getProp(DB_USER_PASS); 
            String sid  = getProp(DB_SID);

            dbAccess = new DbConnectorOrcl();
            dbAccess.openDB(host, port, user, pass, sid);

            ResultSet rset = dbAccess.execQuery(createSelectQuery());

            if (Objects.nonNull(rset)) {
                while (rset.next()) {
                    //Long skip =  rset.getLong( getProp(DB_ITEM_KANBAN_STS) );
                    //if( skip == 1 ){
                    //    //取り込み済みのカンバンはスキップする
                    //    //前回エラーであったカンバン(登録)は状態が修正されている可能性があるので
                    //    //スキップせず、再登録を試みる
                    //    logger.debug("kanban::" +  rset.getString(getProp(DB_ITEM_KANBAN_NAME)) + "  skip");
                    //    continue;
                    //} 

                    KanbanInfoEntity kanban = createKanban(rset);

                    if (Objects.isNull(kanban.getFkWorkflowId())) {
                        //工程順が登録されていない
                        logger.info("add kanban::" + "undefined workflow");
                        updateDbStatus(dbAccess, kanban.getKanbanName(), -1 ,"Undefined Workflow:" + getWorkflowName(rset));
                        continue;
                    }

                    // 新規作成分は一旦登録
                    if (Objects.isNull(kanban.getKanbanId())) {
                        if (Objects.isNull(kanban.getParentId())) {
                            // 階層が登録されていない
                            logger.info("add kanban::" + "undefined hierarchy");
                            updateDbStatus(dbAccess,kanban.getKanbanName(), -1, "Undefined Hierarchy:" + rset.getString(getProp(DB_ITEM_LINE)));
                            continue;
                        }
                       
                        // カンバン追加
                        ResponseEntity createRes = kanbanInfoFacade.regist(kanban);
                        String kanbanName = rset.getString(getProp(DB_ITEM_KANBAN_NAME));
                        if (Objects.nonNull(createRes) && createRes.isSuccess()) {
                            // 追加成功
                            logger.info("add kanban::" + kanbanName);
                        } else {
                            // 追加失敗
                            logger.info("fail kanban::" + kanbanName + ":" + createRes.toString());
                            updateDbStatus(dbAccess, kanbanName, -1, "add fail:" + createRes.getErrorCode().toString());
                            continue;
                        }
                        kanban = kanbanInfoFacade.findURI(createRes.getUri());
                   }
                   else if (Objects.nonNull(kanban.getStartDatetime())) {
                       
                       
                       //新規登録でない場合は再スケジュール
                       List<WorkKanbanInfoEntity> work = kanban.getWorkKanbanCollection();
                       if( Objects.isNull(work) ){
                           logger.info("kanabn:" + kanban.getKanbanName() + "workflow empty");
                            updateDbStatus(dbAccess, kanban.getKanbanName(), -1, "workflow empty");
                            continue;
                       }
                       List<BreakTimeInfoEntity> breakTimes = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes( work );
                       List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                       String workflowName = getWorkflowName(rset);
                       WorkflowInfoEntity workflow = workflowInfoFacade.findName(URLEncoder.encode(workflowName, "UTF-8"));
                       WorkPlanWorkflowProcess workflowProcess = new WorkPlanWorkflowProcess(workflow);
                       workflowProcess.setBaseTime(kanban, breakTimes, kanban.getStartDatetime(), holidays);
                   }

                    // 工程カンバンをオーダー順にソートする。
                    kanban.getWorkKanbanCollection().sort(Comparator.comparing(p -> p.getWorkKanbanOrder()));

                    //追加情報をセットする
                    kanban = setKanbanProp(kanban,rset);

                    //setKanbanStatus(kanban,KanbanStatusEnum.PLANNED);
                    //生成時は「計画中」にする、その後コマンドで「計画済み」にセットする
                    //  (開始時にカンバンの選択リストに表示されないようにするため)
                    setKanbanStatus(kanban,KanbanStatusEnum.PLANNING);

                    ResponseEntity updateRes = kanbanInfoFacade.update(kanban);
                    if (Objects.nonNull(updateRes) && updateRes.isSuccess()) {
                        // 更新成功
                        logger.info("Kanban:" + kanban.getKanbanName() + " update success");
                        updateDbStatus(dbAccess, kanban.getKanbanName(), 1, "");
                    }
                    else{
                        logger.info("Kanban:" + kanban.getKanbanName() + " update fail");
                        updateDbStatus(dbAccess, kanban.getKanbanName(), -1, "kanbanUpdateFail:" + kanban.getKanbanName());
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            dbAccess.closeDB();
            logger.info("DbImportTask end.");
        }
    }

    /**
     * 結果を結合する。同じ文字列をキーとしている場合、値を加算する。値が異なる場合、新しく追加する。
     *
     * @param v1
     * @param v2
     * @return
     */
    static public Map<String, Integer> concatResult(Map<String, Integer> v1, Map<String, Integer> v2) {

        if (Objects.isNull(v1) && Objects.isNull(v2)) {
            return null;
        } else if (Objects.isNull(v1)) {
            return v2;
        } else if (Objects.isNull(v2)) {
            return v1;
        }

        Map<String, Integer> ret = new HashMap(v1);

        for (Map.Entry<String, Integer> v : v2.entrySet()) {
            if (ret.containsKey(v.getKey())) {
                ret.put(v.getKey(), ret.get(v.getKey()) + v.getValue());
            } else {
                ret.put(v.getKey(), v.getValue());
            }
        }

        return ret;
    }
}
