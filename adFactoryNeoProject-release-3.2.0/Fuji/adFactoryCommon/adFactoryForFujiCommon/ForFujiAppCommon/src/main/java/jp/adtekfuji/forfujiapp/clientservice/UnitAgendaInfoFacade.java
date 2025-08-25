/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice;

import adtekfuji.rest.RestClient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * スケジュール生成用REST
 *
 * @author e-mori
 * @version Fver
 * @since 2016.11.30.thr
 */
public class UnitAgendaInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient = new RestClient();

    private final static String PATH_UNIT_AGENDA = RestConstants.PATH_UNIT + RestConstants.PATH_AGENDA;
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public UnitAgendaInfoFacade() {
        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    /**
     * 指定したユニットと月に該当する生産ユニットの予定を取得する。
     *
     * @param id ユニットid
     * @param month 取得する月
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public CustomAgendaEntity getUnitAgenda(Long id, Date month) throws UnsupportedEncodingException {
        logger.debug("find:{}", id);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNIT_AGENDA);
        path.append(RestConstants.PATH_KANBAN);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_ID, id));
        path.append(RestConstants.QUERY_AND);
        path.append(String.format(RestConstants.QUERY_DATE, URLEncoder.encode(DATE_FORMAT.format(month), "UTF-8")));
        return (CustomAgendaEntity) restClient.find(path.toString(), CustomAgendaEntity.class);
    }

    /**
     * 月内に該当する作業者の予定を取得
     *
     * @param id 作業者id
     * @param month 取得する月
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public CustomAgendaEntity getOrganizationAgenda(Long id, Date month) throws UnsupportedEncodingException {
        logger.debug("find:{}", id);
        StringBuilder path = new StringBuilder();
        path.append(PATH_UNIT_AGENDA);
        path.append(RestConstants.PATH_ORGANIZATION);
        path.append(RestConstants.QUERY_PATH);
        path.append(String.format(RestConstants.QUERY_ID, id));
        path.append(RestConstants.QUERY_AND);
        path.append(String.format(RestConstants.QUERY_DATE, URLEncoder.encode(DATE_FORMAT.format(month), "UTF-8")));
        return (CustomAgendaEntity) restClient.find(path.toString(), CustomAgendaEntity.class);
    }

}
