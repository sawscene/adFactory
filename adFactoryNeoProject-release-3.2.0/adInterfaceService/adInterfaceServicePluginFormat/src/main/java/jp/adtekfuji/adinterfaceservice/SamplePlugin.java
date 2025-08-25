/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class SamplePlugin implements AdInterfaceServiceInterface {

    private static final Logger logger = LogManager.getLogger();

    public SamplePlugin() throws Exception {
    }

    @Override
    public void startService() throws Exception {
        logger.info("adInterface SamplePlugin start...");
    }

    @Override
    public void stopService() throws Exception {
        logger.info("adInterface SamplePlugin stoed...");
    }

    /**
     * サービス名を取得する。
     *
     * @return
     */
    @Override
    public String getServiceName() {
        return "adInterface SamplePlugin";
    }
}
