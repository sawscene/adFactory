/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryfujiactualdataoutput.service;

import adtekfuji.property.AdProperty;
import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import com.sun.jersey.api.client.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 実績情報取得用RESTクラス
 *
 * @author ke.yokoi
 */
public class ActualResultInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String ACTUAL_PATH = "/adFactoryServer/rest/actual";
    private final static String SEARCH_PATH = "/search";
    private final static String REPORTOUT_PATH = "/reportout";
    private final static String RANGE_PATH = "/range";

    private final int maxRetry;
    private final long numInterval;

    /**
     * コンストラクタ
     *
     * @param serverAddress サーバーアドレス
     */
    public ActualResultInfoFacade(String serverAddress) {
        Properties properties = AdProperty.getProperties();
        String timeout = properties.getProperty("timeout", "180000");
        this.maxRetry = Integer.parseInt(properties.getProperty("maxRetry", "3"));
        this.numInterval = Long.parseLong(properties.getProperty("numInterval", "3"));

        RestClientProperty property = new RestClientProperty(serverAddress);
        property.setTimeout(Integer.parseInt(timeout));

        this.restClient = new RestClient(property);
    }

    /**
     * 条件を指定して、実績出力情報一覧を取得する。
     *
     * @param condition 検索条件
     * @return 実績出力情報一覧 (エラー発生時はnull)
     */
    public List<ReportOutInfoEntity> reportOutSearch(ReportOutSearchCondition condition) {
        try {
            logger.info("reportOutSearch start: {}", condition);

            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(REPORTOUT_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            List<ReportOutInfoEntity> list = new ArrayList<>();

            int retry = 0;
            while (true) {
                try {
                    List<ReportOutInfoEntity> entities = this.restClient.put(sb.toString(), condition, new GenericType<List<ReportOutInfoEntity>>() {
                    });
                    list.addAll(entities);
                    break;

                } catch (Exception ex1) {
                    if (retry > this.maxRetry) {
                        throw ex1;
                    }

                    try {
                        logger.info("Retry reportOutSearch: " + retry);

                        list.clear();
                        retry++;

                        TimeUnit.SECONDS.sleep(this.numInterval);
                    } catch (Exception ex2) {
                    }
                }
            }

            return list;
        } finally {
            logger.info("reportOutSearch end.");
        }
    }
}
