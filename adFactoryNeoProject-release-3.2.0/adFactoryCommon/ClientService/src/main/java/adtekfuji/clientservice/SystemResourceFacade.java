/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.List;
import java.util.stream.Stream;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.entity.system.SystemPropEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程情報取得用RESTクラス
 *
 * @author ta.ito
 */
public class SystemResourceFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String SYSTEM_PATH = "/system";
    private final static String OPTION_PATH = "/option";
    private final static String PROPERTY_PATH = "/property";
    private final static String LICENSE_NUM_PATH = "/licenseNum";

    private final static String QUERY_PATH = "?";
    private final static String KEY_PATH = "key=%s";

    public SystemResourceFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * ライセンスのオプション設定の取得
     *
     * @param opttionName
     * @return オプション設定
     */
    public SystemOptionEntity getLicenseOption(String opttionName) {
        logger.debug("getLicenseOption:{}", opttionName);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(SYSTEM_PATH).append(OPTION_PATH).append("/").append(opttionName);

            return (SystemOptionEntity) restClient.find(sb.toString(), SystemOptionEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * ライセンスのオプション設定の取得
     *
     * @return オプション設定
     */
    public List<SystemOptionEntity> getLicenseOptions() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(SYSTEM_PATH).append(OPTION_PATH);

            return (List<SystemOptionEntity>) restClient.findAll(sb.toString(), new GenericType<List<SystemOptionEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    public long[] getLicenseNum() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(SYSTEM_PATH);
            sb.append(LICENSE_NUM_PATH);

            ResultResponse result = (ResultResponse) restClient.find(sb.toString(), ResultResponse.class);
            String[] values = result.getResult().split(",");
            
            return Stream.of(values).mapToLong(Long::parseLong).toArray();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * システム設定を取得する。
     *
     * @return システム設定
     */
    public List<SystemPropEntity> getSystemProperties() {
        try {
            return (List<SystemPropEntity>) restClient.find(SYSTEM_PATH, new GenericType<List<SystemPropEntity>>() {});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * キーを指定して、システム設定のプロパティ値を取得する。
     *
     * @param key プロパティのキー
     * @return プロパティの値
     */
    public String getSystemProperty(String key) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(SYSTEM_PATH);
            sb.append(PROPERTY_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(KEY_PATH, key));

            return (String) restClient.find(sb.toString(), String.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
}
