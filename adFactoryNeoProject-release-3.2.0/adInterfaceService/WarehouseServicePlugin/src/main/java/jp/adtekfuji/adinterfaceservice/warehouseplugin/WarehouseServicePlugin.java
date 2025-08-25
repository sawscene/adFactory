/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.warehouseplugin;

import adtekfuji.plugin.PluginLoader;
import java.io.File;
import jp.adtekfuji.adFactory.adinterface.command.RequestCommand;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.mainapp.LocalePluginInterface;

/**
 * 倉庫案内サービスプラグイン
 * 
 * @author s-heya
 */
public class WarehouseServicePlugin implements AdInterfaceServiceInterface {

    public static final String WAREHOUSE_PROPERTY = "warehouse";
    
    private final WarehouseScheduler scheduler;

    /**
     * コンストラクタ
     * 
     * @throws Exception 
     */
    public WarehouseServicePlugin() throws Exception {
        // 言語ファイルプラグイン読み込み
        PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
        PluginLoader.load(LocalePluginInterface.class);

        // スケジューラーを生成
        this.scheduler = WarehouseScheduler.getInstance();
    }

    /**
     * 
     * @throws Exception 
     */
    @Override
    public void startService() throws Exception {
        this.scheduler.startService();
    }

    @Override
    public void stopService() throws Exception {
        this.scheduler.stopService();
    }

    /**
     * サービス名を取得する。
     *
     * @return
     */
    @Override
    public String getServiceName() {
        return "Warehouse";
    }
    /**
     * コマンドを受信した。
     *
     * @param command
     */
    @Override
    public Object request(RequestCommand command) {
        return RequestHandler.getInstance().handle(command.getMessage());
    }
}
