/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.Comparator;
import java.util.List;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.entity.actual.DefectReasonEntity;
import jp.adtekfuji.adfactoryserver.model.DefectReasonManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 不良内容 REST
 *
 * @author nar-nakamura
 */
@Singleton
@Path("/defect")
public class DefectFacadeREST {

    private final Logger logger = LogManager.getLogger();

    private final DefectReasonManager defectReasonManager = DefectReasonManager.getInstance();

    /**
     * コンストラクタ
     */
    public DefectFacadeREST() {
    }

    /**
     * 不良内容の件数を取得する。
     *
     * @param authId 認証ID
     * @return 不良内容の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("count");
        try {
            return String.valueOf(defectReasonManager.getDefectReasons().size());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 不良内容一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の不良内容一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DefectReasonEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange:{},{}", from, to);
        try {
            List<DefectReasonEntity> entities = defectReasonManager.getDefectReasons();
            entities.sort(Comparator.comparing(entity -> entity.getDefectOrder()));

            int toIndex = to + 1;
            int lastIndex = entities.size();
            if (toIndex > lastIndex) {
                toIndex = lastIndex;
            }
            return entities.subList(from, toIndex);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
