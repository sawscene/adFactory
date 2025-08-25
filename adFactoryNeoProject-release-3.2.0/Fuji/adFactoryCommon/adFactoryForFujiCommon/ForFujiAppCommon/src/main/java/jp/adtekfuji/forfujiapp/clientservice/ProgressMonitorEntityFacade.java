/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.utility.StringUtils;
import com.sun.jersey.api.client.GenericType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorGraphInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorListInfoEntity;
import jp.adtekfuji.forfujiapp.entity.monitor.MonitorPanelInfoEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 進捗モニタ表示情報取得用REST
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.12.8.Thr
 */
public class ProgressMonitorEntityFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient = new RestClient();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProgressMonitorEntityFacade() {
        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    /**
     * 指定された生産ユニットの進捗モニタパネル表示の情報を取得
     *
     * @param conditin
     * @param titleOption タイトルに表示するユニットプロパティ名
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public List<MonitorPanelInfoEntity> getMonitorPanelEntity(UnitSearchCondition conditin, String titleOption) throws UnsupportedEncodingException {
        logger.debug("getMonitorPanelEntity:{}", conditin);
        if (StringUtils.isEmpty(titleOption)) {
            titleOption = "";
        }
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_MONITOR);
        path.append(RestConstants.PATH_PANEL);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_TITLE_OPTION, URLEncoder.encode(titleOption, "UTF-8")));
        return restClient.put(path.toString(), conditin, new GenericType<List<MonitorPanelInfoEntity>>() {});
    }

    /**
     * 指定された生産ユニットの進捗モニタグラフ表示の情報を取得
     *
     * @param conditin
     * @param titleOption タイトルに使用するユニットプロパティ名
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public List<MonitorGraphInfoEntity> getMonitorGraph(UnitSearchCondition conditin, String titleOption) throws UnsupportedEncodingException {
        logger.debug("getMonitorGraphEntity:{}", conditin);
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_MONITOR);
        path.append(RestConstants.PATH_GRAPH);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_TITLE_OPTION, URLEncoder.encode(titleOption, "UTF-8")));
        return restClient.put(path.toString(), conditin, new GenericType<List<MonitorGraphInfoEntity>>() {});
    }

    /**
     * 指定された生産ユニットの進捗モニタリスト表示の情報を取得
     *
     * @param conditin
     * @param mainTitle タイトルに表示するユニットプロパティ名
     * @param subTitle サブタイトルに表示するユニットプロパティ名
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public List<MonitorListInfoEntity> getMonitorListEntity(UnitSearchCondition conditin, String mainTitle, String subTitle) throws UnsupportedEncodingException {
        logger.debug("getMonitorListEntity:{}", conditin);
        if (StringUtils.isEmpty(mainTitle)) {
            mainTitle = "";
        }
        if (StringUtils.isEmpty(subTitle)) {
            subTitle = "";
        }
        StringBuilder path = new StringBuilder();
        path.append(RestConstants.PATH_MONITOR);
        path.append(RestConstants.PATH_LIST);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_TITLE_MAIN, URLEncoder.encode(mainTitle, "UTF-8")));
        path.append(RestConstants.QUERY_AND);
        path.append(String.format(RestConstants.QUERY_TITLE_SUB, URLEncoder.encode(subTitle, "UTF-8")));
        return restClient.put(path.toString(), conditin, new GenericType<List<MonitorListInfoEntity>>() {});
    }
}
