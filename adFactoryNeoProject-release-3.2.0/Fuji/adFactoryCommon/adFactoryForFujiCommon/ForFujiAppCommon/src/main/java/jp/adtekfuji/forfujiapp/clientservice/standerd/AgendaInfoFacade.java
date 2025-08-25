/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.clientservice.standerd;

import adtekfuji.rest.RestClient;
import java.text.SimpleDateFormat;
import jp.adtekfuji.adFactory.entity.agenda.AgendaEntity;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.common.RestConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 実績Facade
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.19.Wed
 */
public class AgendaInfoFacade {

    private final Logger logger = LogManager.getLogger();

    private final RestClient restClient = new RestClient();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public AgendaInfoFacade() {
        restClient.setUriBase(new AdFactoryForFujiClientAppConfig().getAdFactoryForFujiServerAddress());
    }

    public AgendaInfoFacade(String uriBase) {
        restClient.setUriBase(uriBase);
    }

    /**
     * カンバンのスケジュール情報を取得
     *
     * @param kanbanId カンバンID
     * @param dateString 作業日
     * @return 予定データ
     * @throws Exception
     */
    public AgendaEntity findByKanban(Long kanbanId, String dateString) throws Exception {
        try {
            logger.info("findByKanban:{},{}", kanbanId, dateString);
            StringBuilder path = new StringBuilder();
            path.append(RestConstants.PATH_AGENDA);
            path.append(RestConstants.PATH_KANBAN);
            path.append(RestConstants.QUERY_PATH);
            path.append(String.format(RestConstants.QUERY_ID, kanbanId.toString()));
            path.append(RestConstants.QUERY_AND);
            path.append(String.format(RestConstants.QUERY_DATE, dateString));
            return (AgendaEntity) restClient.find(path.toString(), AgendaEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 作業者のスケジュール情報を取得
     *
     * @param organizationId 作業者ID
     * @param dateString 作業日
     * @return 予定データ
     * @throws Exception
     */
    public AgendaEntity findByOrganization(Long organizationId, String dateString) throws Exception {
        try {
            logger.info("findByOrganization:{},{}", organizationId, dateString);
            StringBuilder path = new StringBuilder();
            path.append(RestConstants.PATH_AGENDA);
            path.append(RestConstants.PATH_ORGANIZATION);
            path.append(RestConstants.QUERY_PATH);
            path.append(String.format(RestConstants.QUERY_ID, organizationId.toString()));
            path.append(RestConstants.QUERY_AND);
            path.append(String.format(RestConstants.QUERY_DATE, dateString));
            return (AgendaEntity) restClient.find(path.toString(), AgendaEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
