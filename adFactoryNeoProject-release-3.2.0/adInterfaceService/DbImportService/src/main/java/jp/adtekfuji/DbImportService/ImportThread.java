/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.DbImportService;

import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import static jp.adtekfuji.DbImportService.Constants.DB_HOST_NAME;
import static jp.adtekfuji.DbImportService.Constants.DB_PORT;
import static jp.adtekfuji.DbImportService.Constants.DB_SID;
import static jp.adtekfuji.DbImportService.Constants.DB_USER_NAME;
import static jp.adtekfuji.DbImportService.Constants.DB_USER_PASS;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.DbConnectorOrcl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * インポートスレッド
 * 
 * @author s-heya
 */
public class ImportThread extends Thread  {
    private static final Logger logger = LogManager.getLogger();
    private final String resourceName;

    /**
     * コンストラクタ
     * 
     * @param resourceName 
     */
    public ImportThread(String resourceName) {
        this.resourceName = resourceName;
    }
    
    /**
     * タスクを実行する。
     * 
     */
    @Override
    public void run() {
        DbConnectorOrcl dbConnector = new DbConnectorOrcl();

        final String FILE_EXT = ".tsv";
        final String DELIMITER = "\t";
        final String LINE_SEPARATOR = "\r\n";

        try {
            logger.info("ImportThread start.");
            
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adInterface.properties");
            Properties properties = AdProperty.getProperties();
            String host = properties.getProperty(DB_HOST_NAME);
            String port = properties.getProperty(DB_PORT);
            String user = properties.getProperty(DB_USER_NAME);
            String pass = properties.getProperty(DB_USER_PASS); 
            String sid  = properties.getProperty(DB_SID);

            dbConnector.openDB(host, port, user, pass, sid);
            ResultSet resultSet = dbConnector.execQuery(this.createQuery());

            if (Objects.nonNull(resultSet)) {
                int rows = 0;

                String path = System.getenv("ADFACTORY_HOME") + File.separator + "temp";
                String filePath = path + File.separator + resourceName + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + FILE_EXT;
                
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF8"))) {

                    while (resultSet.next()) {
                        StringBuilder sb = new StringBuilder();

                        // 組織識別名
                        String organizationIdentify = resultSet.getString(Constants.COL_SOSHIKI_SKB_NAME);

                        // 組織名
                        String organizationName = resultSet.getString(Constants.COL_SOSHIKI_NAME);
                        if (StringUtils.isEmpty(organizationName)) {
                            organizationName = organizationIdentify;
                        }

                        // 親組織名
                        String parentName = resultSet.getString(Constants.COL_DEPT_NAME);
                        if (Objects.isNull(parentName)) {
                            parentName = "";
                        }
                        
                        sb.append(organizationIdentify);
                        sb.append(DELIMITER);
                        sb.append(organizationName);
                        sb.append(DELIMITER);
                        sb.append(parentName);
                        sb.append(LINE_SEPARATOR);
                        
                        writer.append(sb.toString());
                        rows++;
                    }
                }
                
                logger.info("Number of records in HIN_TRACE_M_ADF_WORKER: " + rows);
                
                if (rows > 0) {
                    OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
                    organizationInfoFacade.importFile(filePath);
                }
                
                File file = new File(filePath);
                file.delete();
            }
           
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            dbConnector.closeDB();
            logger.info("ImportThread end.");
        }
    }

    /**
     * クエリー文字列を生成する。
     * 
     * @return 
     */
    private String createQuery(){
        return "SELECT SOSHIKI_SKB_NAME, SOSHIKI_NAME, DEPT_NAME FROM HIN_TRACE_M_ADF_WORKER";
    }
}
