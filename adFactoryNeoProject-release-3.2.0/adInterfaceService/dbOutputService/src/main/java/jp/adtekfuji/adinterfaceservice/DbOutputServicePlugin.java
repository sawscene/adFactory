
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.WorkDetail;
import jp.adtekfuji.adFactory.adinterface.command.WorkResult;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.DbConnectorOrcl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サービスで実績出力をDBに行う
 *
 * @author ka.makihara
 */
public class DbOutputServicePlugin extends Thread implements AdInterfaceServiceInterface {

    private static final String SERVICE_NAME = "DBOutputService";

    private static final Logger logger = LogManager.getLogger();

    private final ActualResultInfoFacade actualResultEntityFacade = new ActualResultInfoFacade();

    private final LinkedList<ActualNoticeCommand> recvQueue = new LinkedList<>();

    private DbConnectorOrcl    dbAccess;
    private boolean execution = true;

    private static String dbHostName;
    private static String dbPort;
    private static String dbUserName;
    private static String dbUserPass;
    private static String dbSid;
    private static String dbActualTbl;
    private static String dbDetailTbl;
    private static boolean isDbOutput;

    public DbOutputServicePlugin() throws IOException {
        this.dbAccess = null;
        {
            AdProperty.load("adInterface.properties");
            final Properties properties = AdProperty.getProperties();

            // 初期設定
            Map<String, String> map = new HashMap<String, String>() {{
                    put(Constants.DB_HOST_NAME,            "localhost");
                    put(Constants.DB_PORT,                 "1521");
                    put(Constants.DB_USER_NAME,            "adtek");
                    put(Constants.DB_USER_PASS,            "adtek");
                    put(Constants.DB_SID,                  "orcl");
                    put(Constants.DB_ACTUAL_TABLE,         "HIN_TRACE_T_ADF_HISTORY");
                    put(Constants.DB_DETAIL_TABLE,         "HIN_TRACE_T_ADF_DETAIL");
                    put(Constants.DB_ITEM_LINE,            "LINE");
                    put(Constants.DB_ITEM_KANBAN_NAME,     "KANBAN_NAME");
                    put(Constants.DB_ITEM_KOUTEI_JUN_NAME, "KOTEI_JUN_NAME");
                    put(Constants.DB_ITEM_KOUTEI_NAME,     "KOTEI_NAME");
                    put(Constants.DB_ITEM_SOSHIKI_NAME,    "SOSHIKI_NAME");
                    put(Constants.DB_ITEM_SOSHIKI_SKB_NAME,"SOSHIKI_SKB_NAME");
                    put(Constants.DB_ITEM_SETSUBI_NAME,    "SETSUBI_NAME");
                    put(Constants.DB_ITEM_SETSUBI_MNG_NAME,"SETSUBI_MNG_NAME");
                    put(Constants.DB_ITEM_HISTORY_STS,     "HISTORY_STS");
                    put(Constants.DB_ITEM_CYUDAN_REASON,   "CYUDAN_REASON");
                    put(Constants.DB_ITEM_CHIEN_REASON,    "CHIEN_REASON");
                    put(Constants.DB_ITEM_JISSHI_BI,       "JISSHI_BI");
                    put(Constants.DB_ITEM_TACT_TIME,       "TACT_TIME");
                    put(Constants.DB_ITEM_WORK_TIME,       "WORK_TIME");
                    put(Constants.DB_ITEM_DATA_TOROKU_BI,  "DATA_TOROKU_BI");
                    put(Constants.DB_ITEM_DATA_KOSHIN_BI, "DATA_KOUSHIN_BI");
                    put(Constants.DB_OUTPUT_ENABLE,        "true");
                    put(Constants.DB_ITEM_ACTUAL_ID,       Constants.COL_ACTUAL_ID);
                }
            };
            
            map.entrySet().stream().filter((entry) -> (!properties.containsKey(entry.getKey()))).map((entry) -> {
                properties.setProperty(entry.getKey(), entry.getValue());
                return entry;
            }).forEach((_item) -> {
                store();
            });

            dbHostName = properties.getProperty(Constants.DB_HOST_NAME);
            dbPort     = properties.getProperty(Constants.DB_PORT);
            dbUserName = properties.getProperty(Constants.DB_USER_NAME);
            dbUserPass = properties.getProperty(Constants.DB_USER_PASS);
            dbActualTbl= properties.getProperty(Constants.DB_ACTUAL_TABLE);
            dbSid      = properties.getProperty(Constants.DB_SID);
            isDbOutput = properties.getProperty(Constants.DB_OUTPUT_ENABLE).equals("true");
            dbDetailTbl = properties.getProperty(Constants.DB_DETAIL_TABLE);
        }
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
     *  java.util.Date を sql.Dataに変換
     *    ※時分秒無し
     * @param dd
     * @return 
     */
    private java.sql.Date convSqlDate(Date dd){
        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        //cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.HOUR_OF_DAY,dd.getHours());
        //cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.MINUTE,dd.getMinutes());
        //cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,dd.getSeconds());

        return new java.sql.Date(cal.getTimeInMillis());
    }
    /**
     *  java.util.Date を sql.Timestampに変換
     *    ※時分秒を扱いたい場合はテーブル定義をTIMESTAMP型にする必要があります
     * @param dd
     * @return 
     */
    private java.sql.Timestamp convSqlTimestamp(Date dd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        cal.set(Calendar.MILLISECOND,0);

        return new java.sql.Timestamp(cal.getTimeInMillis());
    }
    /**
     * カンバンステータス(KanbanStatusEnum)値を文字へ変換する
     * @param  pairId
     * @param e
     * @return 
     */
    private String convKanbanStatusString(Long pairId, KanbanStatusEnum e){
        String status;
        switch(e){
        case PLANNING:   status="計画中";   break;
        case PLANNED:    status="計画済";   break;
        case WORKING:
            if( pairId == null ){
                //pairIdがnullなら最初、中断からの「開始」の場合はpairIdが設定されているはず
                status="開始";
            }
            else{
                status="作業中";
            }
            break;
        case SUSPEND:    status="一時中断"; break;
        case INTERRUPT:  status="中止";     break;
        case COMPLETION: status="完了";     break;
        case OTHER:      status="その他";   break;
        default:         status="未定義";   break;
       } 
        return status;
    }

    @Override
    public void run() {
        try {
            StringBuilder insertActualQuery = new StringBuilder();
            insertActualQuery.append("INSERT INTO ").append(dbActualTbl).append(" (");
            insertActualQuery.append(getProp(Constants.DB_ITEM_LINE)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_KANBAN_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_KOUTEI_JUN_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_KOUTEI_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_SOSHIKI_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_SOSHIKI_SKB_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_SETSUBI_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_SETSUBI_MNG_NAME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_HISTORY_STS)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_CYUDAN_REASON)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_CHIEN_REASON)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_JISSHI_BI)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_TACT_TIME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_WORK_TIME)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_DATA_TOROKU_BI)).append(",");
            insertActualQuery.append(getProp(Constants.DB_ITEM_ACTUAL_ID)).append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            StringBuilder insertDetailQuery = new StringBuilder();
            insertDetailQuery.append("INSERT INTO ").append(dbDetailTbl).append(" (");
            insertDetailQuery.append(Constants.COL_ACTUAL_ID).append(",");
            insertDetailQuery.append(Constants.COL_SUB_ID).append(",");
            insertDetailQuery.append(Constants.COL_TRACE_NAME).append(",");
            insertDetailQuery.append(Constants.COL_TRACE_TAG).append(",");
            insertDetailQuery.append(Constants.COL_TRACE_VALUE).append(",");
            insertDetailQuery.append(Constants.COL_DATA_TOROKU_BI).append(") VALUES (?,?,?,?,?,?)");

            while (execution) {
                try {
                    synchronized (recvQueue) {
                        if (recvQueue.isEmpty()) {
                            try {
                                recvQueue.wait();
                            } catch (InterruptedException ex) {
                                logger.fatal(ex, ex);
                            }
                        }

                        if (!recvQueue.isEmpty()) {
                            ActualNoticeCommand actualNotice = recvQueue.removeFirst();
                            logger.info("CMD: {}", actualNotice);

                            if (Objects.isNull(actualNotice.getWorkResult())) {
                                continue;
                            }

                            WorkResult workResult = actualNotice.getWorkResult();
                            Date now = new Date();

                            dbAccess = new DbConnectorOrcl();
                            dbAccess.openDB(dbHostName, dbPort, dbUserName, dbUserPass, dbSid);
                            dbAccess.setAutoCommit(false);

                            PreparedStatement ps = dbAccess.getPreparedStatement(insertActualQuery.toString());
                            ps.setString(1, workResult.getKanbanParentName());          // カンバン階層名(30文字)
                            ps.setString(2, workResult.getKanbanName());                // カンバン名(80文字)
                            ps.setString(3, workResult.getWorkflowName());              // 工程順名
                            ps.setString(4, workResult.getWorkName());                  // 工程名
                            ps.setString(5, workResult.getOrganizationName());          // 組織名
                            ps.setString(6, workResult.getOrganizationIdentName());     // 組織識別名
                            ps.setString(7, workResult.getEquipmentName());             // 設備名
                            ps.setString(8, workResult.getEquipmentIdentName());        // 設備識別名
                            ps.setString(9, convKanbanStatusString(workResult.getPairId(), actualNotice.getWorkKanbanStatus())); //ステータス
                            ps.setString(10, workResult.getInterruptReason());           // 中断理由(256文字)
                            ps.setString(11, workResult.getDelayReason());               // 遅延理由(256文字)
                            ps.setTimestamp(12, convSqlTimestamp(workResult.getImplementDatetime())); // 実施時間(Timestamp)
                            ps.setLong(13, workResult.getTaktTime() / 1000);             // タクトタイム(秒,5桁)
                            ps.setLong(14, workResult.getWorkingTime() / 1000);          // 作業時間(秒,5桁)
                            ps.setTimestamp(15, convSqlTimestamp(now));                  // データ登録日
                            ps.setLong(16, actualNotice.getActualId());                  // 実績ID
                            ps.executeUpdate();
                            
                            // 実績詳細
                            for (WorkDetail detail : workResult.getDetails()) {
                                PreparedStatement insertDetail = dbAccess.getPreparedStatement(insertDetailQuery.toString());
                                insertDetail.setLong(1, actualNotice.getActualId()); // 実績ID
                                insertDetail.setInt(2, detail.getOrder());          // 枝番
                                insertDetail.setString(3, detail.getName());        // 項目
                                insertDetail.setString(4, detail.getTag());         // タグ
                                insertDetail.setString(5, detail.getValue());       // 値
                                insertDetail.setTimestamp(6, convSqlTimestamp(now));// データ登録日
                                insertDetail.executeUpdate();
                            }

                            dbAccess.commit();
                            dbAccess.closeDB();
                            dbAccess = null;
                            
                            logger.info("Data output was successful.");
                        }
                    }

                } catch (SQLException ex) {
                    logger.fatal(ex);

                    if (Objects.nonNull(dbAccess)) {
                        dbAccess.rollback();
                        dbAccess.closeDB();
                        dbAccess = null;
                    }
    
                } catch (Exception ex) {
                    logger.fatal(ex);
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            if (Objects.nonNull(dbAccess)) {
                dbAccess.closeDB();
            }
        }
    }

    @Override
    public void startService() throws Exception {
        if( isDbOutput ){
            logger.info("started DbOutput service.");

            super.start();
        }
        else{
            logger.info("disabled DbOutput service.");
        }
    }

    private void store() {
        try {
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void stopService() throws Exception {
        if( isDbOutput ){
            logger.info("stopped DbOutput service.");

            execution = false;
            synchronized (recvQueue) {
                recvQueue.notify();
            }
            super.join();
        }
        else{
            logger.info("disabled DbOutput service.");
        }
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    /**
     * 実績通知コマンドを受信した。
     * 
     * @param cmd
     */
    @Override
    public void noticeActualCommand(Object cmd) {
        if (isDbOutput) {
               synchronized (recvQueue) {
                   ActualNoticeCommand command = (ActualNoticeCommand) cmd;
                   recvQueue.add(command);
                   recvQueue.notify();
            }
        }
    }
}
