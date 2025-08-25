
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.DbImportService;

import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.HolidayInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.property.AdProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Timer;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import static jp.adtekfuji.DbImportService.Constants.ENABLE_IMPORT;
import static jp.adtekfuji.DbImportService.Constants.POLLING_TIME;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Oracleインポートサービス
 *
 * @author ka.makihara
 */
public class DbImportServicePlugin extends Thread implements AdInterfaceServiceInterface {

    private static final Logger logger = LogManager.getLogger();
    private static final String SERVICE_NAME = "DBImportService";
    private static final long REST_RANGE_NUM = 20;
    private static final long HOLIDAY_RANGE_NUM = 100;

    private final Timer timer = new Timer();
    private DbImportTask importTask;
    private final boolean enableImport;
    private final long pollingTime;
    private final boolean ignoreSameKanban;

    public DbImportServicePlugin() throws IOException {
        this.ignoreSameKanban = false;
        AdProperty.load("adInterface.properties");
        final Properties properties = AdProperty.getProperties();

        // DBからテーブルを読み込む時のDB接続情報、項目名をプロパティファイルより読み込む
        Map<String, String> map = new HashMap<String, String>() {
            {
                put(Constants.ENABLE_IMPORT,          "false");
                put(Constants.POLLING_TIME,           "60");
                put(Constants.DB_HOST_NAME,           "localhost");
                put(Constants.DB_PORT,                "1521");
                put(Constants.DB_USER_NAME,           "adtek");
                put(Constants.DB_USER_PASS,           "adtek");
                put(Constants.DB_SID,                 "orcl");
                put(Constants.DB_KANBAN_TABLE,        "table1");
                put(Constants.DB_ITEM_SEIBAN,         "SEIBAN");          // 製番
                put(Constants.DB_ITEM_GOKI,           "GOKI");            // 号機
                put(Constants.DB_ITEM_KANBAN_NAME,    "KANBAN_NAME");     // カンバン名称
                put(Constants.DB_ITEM_VIEW_NAME,      "VIEW_NAME");       // 表示名称
                put(Constants.DB_ITEM_LINE,           "LINE");            // 製造ライン
                put(Constants.DB_ITEM_SEIBAN_KBN,     "SEIBAN_KBN");      // 製番区分
                put(Constants.DB_ITEM_KISYU_ID,       "KISYU_ID");        // 機種ID
                put(Constants.DB_ITEM_KEIKAKU_BI,     "KEIKAKU_BI");      // 作業計画日時
                put(Constants.DB_ITEM_KANBAN_STS,     "KANBAN_STS");      // 取り込みステータス
                put(Constants.DB_ITEM_STS_DETAILS,    "STS_DETAILS");     // ステータス詳細
                put(Constants.DB_ITEM_SOCKET_STS,     "SOCKET_STS");      // ステータス詳細
                put(Constants.DB_ITEM_DATA_TOROKU_BI, "DATA_TOROKU_BI");  // データ登録日
                put(Constants.DB_ITEM_DATA_KOSHIN_BI, "DATA_KOSHIN_BI");  // データ更新日
            }
        };

        map.entrySet().stream().filter((entry) -> (!properties.containsKey(entry.getKey()))).map((entry) -> {
            properties.setProperty(entry.getKey(), entry.getValue());
            return entry;
        }).forEach((_item) -> {
            store();
        });

        this.enableImport = Boolean.valueOf(properties.getProperty(ENABLE_IMPORT));
        this.pollingTime = 1000L * Long.valueOf(properties.getProperty(POLLING_TIME));
    }

    @Override
    public void run() {

        if (!enableImport) {
            return;
        }

        try {
            importTask = new DbImportTask(ignoreSameKanban);
        } catch (IOException ex) {
            logger.fatal("Can't start DbImportTask:"+ex.toString());
        }
        timer.schedule(importTask, pollingTime, pollingTime);
    }

    @Override
    public void startService() throws Exception {
        if (enableImport) {
            logger.info("started DBimport service.");

            if (!enableAutoImport()) {
                return;
            }

            final CashManager cm = CashManager.getInstance();
            this.createCashOrganization(cm);
            this.createCashHoliday(cm);

            super.start();
        }
        else{
            logger.info("disabled DBimport service.");
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
        if( enableImport ){
            logger.info("stopped DBimport service.");

            if (Objects.nonNull(timer)) {
                timer.cancel();
            }
            super.join();
        }
        else{
            logger.info("disabled DBimport service.");
        }
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    private boolean enableAutoImport() {
        return enableImport;
    }
    
    @Override
    public void noticeActualCommand(Object cmd){
    }

    /**
     * キャッシュに組織情報を読み込む。
     */
    private void createCashOrganization(CashManager cashManager) {
        logger.info("createCashOrganization");

        if (cashManager.isExist(OrganizationInfoEntity.class)) {
            logger.info("OrganizationInfoEntity exist.");
            return;
        }

        cashManager.setNewCashList(OrganizationInfoEntity.class);
        cashManager.clearList(OrganizationInfoEntity.class);

        OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
        Long organizationCount = organizationInfoFacade.count();
        for (long count = 0; count < organizationCount; count += REST_RANGE_NUM) {
            List<OrganizationInfoEntity> entitys = organizationInfoFacade.findRange(count, count + REST_RANGE_NUM - 1);
            entitys.stream().forEach((entity) -> {
                cashManager.setItem(OrganizationInfoEntity.class, entity.getOrganizationId(), entity);
            });
        }

        logger.info("createCashOrganization end.");
    }

    /**
     * キャッシュに休日情報を読み込む。
     */
    private void createCashHoliday(CashManager cashManager) {
        logger.info("createCashHoliday");

        if (cashManager.isExist(HolidayInfoEntity.class)) {
            logger.info("HolidayInfoEntity exist.");
            return;
        }

        cashManager.setNewCashList(HolidayInfoEntity.class);
        cashManager.clearList(HolidayInfoEntity.class);

        HolidayInfoFacade holidayInfoFacade = new HolidayInfoFacade();
        long holidayCount = holidayInfoFacade.count();
        for (long count = 0; count <= holidayCount; count += HOLIDAY_RANGE_NUM) {
            List<HolidayInfoEntity> entities = holidayInfoFacade.findRange(count, count + HOLIDAY_RANGE_NUM - 1);
            entities.stream().forEach((entity) -> {
                cashManager.setItem(HolidayInfoEntity.class, entity.getHolidayId(), entity);
            });
        }

        logger.info("createCashHoliday end.");
    }

    /**
     * コマンド(実績通知コマンド以外)を受信した。
     * 
     * @param command 
     */
    @Override
    public void notice(Object command) {
        if (command instanceof JsonNode) {
            JsonNode node = (JsonNode) command;
            String cmd = node.get("CMD").asText().toLowerCase();
            List<String> params = this.getParams(node, "PARAM");
            
            if ("organization".equals(cmd) && "update".equals(params.get(0).toLowerCase())) {
                // 組織マスタを更新
                ImportThread thread = new ImportThread(cmd);
                thread.start();
            }
        }
    }
    
    /**
     * 
     * 
     * @param node
     * @param name
     * @return 
     */
    private List<String> getParams(JsonNode node, String name) {
        List<String> params = new ArrayList<>();

        JsonNode jsonNode = node.get(name);
        if( Objects.isNull(jsonNode) ){
            return params;
        }

        Iterator<JsonNode> it = jsonNode.elements();
        it.forEachRemaining(o -> {
            // ダブルクォーテーションを取り除く
            params.add(o.toString().replace("\"",""));
        });
        
        return params;
    }
}
