/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import java.io.File;
import java.util.TimerTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * インポートタスク
 * 
 * @author 14-0282
 */
public class ImportTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger();

    private ServiceStatus status = ServiceStatus.SYNC_STOP;
    private final WarehouseScheduler scheduler;
    private final WarehouseConfig config;
    private final String settingRoot; // 設定フォルダーパス
    private final String root; // 読込フォルダーパス
    private final String path;

     /**
     * コンストラクタ
     * 
     * @param scheduler スケジューラー
     */
    public ImportTask(WarehouseScheduler scheduler) {
        this.scheduler = scheduler;
        this.path = null;
        this.config = WarehouseConfig.getInstance();
        String importDir = this.config.getImportPath();

        if (importDir.endsWith(File.separator)) {
            this.root = importDir;
            this.settingRoot = importDir;
        } else {
            this.root = importDir + File.separator;
            this.settingRoot = importDir + File.separator;
        }
    }

    /**
     * コンストラクタ
     * 
     * @param scheduler スケジューラー
     * @param path 読込フォルダーパス
     */
    public ImportTask(WarehouseScheduler scheduler, String path) {
        this.scheduler = scheduler;
        this.path = path;
        this.config = WarehouseConfig.getInstance();
        String importDir = this.config.getImportPath();

        if (importDir.endsWith(File.separator)) {
            this.root = importDir;
            this.settingRoot = importDir;
        } else {
            this.root = importDir + File.separator;
            this.settingRoot = importDir + File.separator;
        }
    }

     /**
     * ステータスを取得する。
     * 
     * @return ステータス
     */
    public synchronized ServiceStatus getStatus() {
        return status;
    }

    /**
     * ステータスを設定する。
     * 
     * @param status ステータス
     */
    private synchronized void setStatus(ServiceStatus status) {
        this.status = status;
    }

    /**
     * インポート処理を実行する。
     */
    @Override
    public void run() {
        logger.info("ImportTask start.");
        long startTime = System.currentTimeMillis();

        try {
            setStatus(ServiceStatus.SYNC_RUNNNING);

            // インポートファイル配置パス設定
            String fileRoot = this.root;
            if (!StringUtils.isEmpty(this.path)) {
                File fol = new File(this.path);
                if (!fol.exists() || !fol.isDirectory()) {
                    logger.fatal("Dose not exist import folder.");
                    return;
                }
                
                if (this.path.endsWith(File.separator)) {
                    fileRoot = this.path;
                } else {
                    fileRoot = this.path + File.separator;
                }
            }

            // 棚マスタのインポート
            new ImportMstLocation(fileRoot).importData();

            // 部品マスタのインポート
            ImportMstParts importMstParts = new ImportMstParts(fileRoot, settingRoot);
            importMstParts.importData();

            // 保管方法のインポート
            ImportMstStock stock = new ImportMstStock(fileRoot, settingRoot);
            stock.importData();

            // 部品構成マスタのインポート
            ImportMstBom importMstBom = new ImportMstBom(fileRoot, settingRoot);
            importMstBom.importData();

            // 在庫情報のインポート
            new ImportInStock(fileRoot, settingRoot).importData();

            // 納入情報のインポート
            ImportReqStoreIn importReqStoreIn = new ImportReqStoreIn(fileRoot, settingRoot, "storein_setting.xml", "req_storein.csv");
            importReqStoreIn.importData();

            // 作業指示情報のインポート
            ImportReqStoreIn importOrder = new ImportReqStoreIn(fileRoot, settingRoot, "order_setting.xml", "order.csv");
            importOrder.importData();

            // 支給品リストのインポート
            ImportSupplies importSupplies = new ImportSupplies(fileRoot, settingRoot);
            importSupplies.importData();

            // 出庫情報のインポート
            ImportReqWithDraw importReqWithDraw = new ImportReqWithDraw(fileRoot, settingRoot, "withdraw_setting.xml", "req_withdraw.csv");
            importReqWithDraw.importData();

            //scheduler.stopSchedule();
            //scheduler.startSchedule();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            setStatus(ServiceStatus.SYNC_STOP);
            
            long stopTime = System.currentTimeMillis();
            logger.info("ImportTask end: {}[ms] ", (stopTime - startTime));
        }
    }
}
