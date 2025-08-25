/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.warehouse;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import jp.adtekfuji.adfactoryserver.common.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * ラベルプリンター
 *
 * @author s-heya
 */
public class LabelPrinter {

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public LabelPrinter() {
    }
 
    /**
     * キット箱に貼るラベルを印刷する。
     * 
     * @param server
     * @param host
     * @param deliveryNo
     * @param unitNo
     * @param modelName
     * @param personNo
     * @param copies
     */
    public void printKitLabel(String server, String host, String deliveryNo, String unitNo, String modelName, String personNo, int copies) {
        try {
            Date now = new Date();
            
            StringBuilder command = new StringBuilder();
            command.append("/Format/Print?__format_archive_url=");
            command.append(URLEncoder.encode(host + "/adFactoryServer/deploy/smapri_kit.spfmtz", Constants.UTF_8));
            command.append("&__format_archive_update=update");
            command.append("&__format_id_number=1");
            command.append("&delivery_no=");
            command.append(URLEncoder.encode(deliveryNo, Constants.UTF_8));
            command.append("&unit_no=");
            command.append(URLEncoder.encode(unitNo, Constants.UTF_8));
            command.append("&model=");
            command.append(URLEncoder.encode(modelName, Constants.UTF_8));
            command.append("&qty=");
            command.append(0);
            command.append("&person=");
            command.append(URLEncoder.encode(personNo, Constants.UTF_8));
            command.append("&date=");
            command.append(URLEncoder.encode(new SimpleDateFormat("yy/MM/dd").format(now), Constants.UTF_8));
            command.append("&time=");
            command.append(URLEncoder.encode(new SimpleDateFormat("HH:mm").format(now), Constants.UTF_8));
            command.append("&copies=");
            command.append(copies);

            PrimeFaces.current().executeScript("sendRequest('" + server + command.toString() + "','ja');");
                    
            logger.info("printKitLabel: " + server + command.toString());

            //WebClient webClient = new WebClient(server);
            //ClientResponse response = webClient.requestGet(command.toString());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
