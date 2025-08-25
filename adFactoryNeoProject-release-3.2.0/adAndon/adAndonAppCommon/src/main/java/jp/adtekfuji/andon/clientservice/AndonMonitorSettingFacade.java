/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.clientservice;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import java.io.StringWriter;
import jakarta.xml.bind.JAXB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ設定情報REST API
 *
 * @author e-mori
 */
public class AndonMonitorSettingFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String CONFIG_PATH = "/monitor/%d/line/config";

    public AndonMonitorSettingFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 進捗モニタ設定情報を取得する。
     *
     * @param monitorId
     * @param cls
     * @return
     */
    public Object getLineSetting(Long monitorId, Class cls) {
        logger.debug("getLineSetting:{}", monitorId);
        try {
            String path = String.format(CONFIG_PATH, monitorId);
            return restClient.find(path, cls);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 進捗モニタ設定情報を設定する。
     *
     * @param monitorId
     * @param setting
     */
    public void setLineSetting(Long monitorId, Object setting) {
        logger.debug("setLineSetting:{}", monitorId);
        try {
            String path = String.format(CONFIG_PATH, monitorId);

            try (StringWriter sw = new StringWriter()) {
                JAXB.marshal(setting, sw);
                restClient.put(path, sw.toString(), String.class);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
