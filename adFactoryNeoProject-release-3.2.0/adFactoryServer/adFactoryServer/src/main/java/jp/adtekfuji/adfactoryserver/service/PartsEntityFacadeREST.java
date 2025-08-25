/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsRemoveCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.PartsEntity;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 完成品情報REST
 *
 * @author nar-nakamura
 */
@Singleton
@Path("parts")
public class PartsEntityFacadeREST extends AbstractFacade<PartsEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public PartsEntityFacadeREST() {
        super(PartsEntity.class);
    }

    /**
     * パーツIDを指定して、完成品情報を取得する。
     *
     * @param partsId パーツID
     * @param authId 認証ID
     * @return 完成品情報 (該当なしの場合、空の完成品情報)
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public PartsEntity findParts(@QueryParam("id") String partsId, @QueryParam("authId") Long authId) {
        logger.info("findParts: partsId={}, authId={}", partsId, authId);
        try {
            // パーツIDを指定して、完成品情報を取得する。(削除済も対象として、未削除・製造日が新しいものを優先)
            TypedQuery<PartsEntity> query = this.em.createNamedQuery("PartsEntity.findByPartsId", PartsEntity.class);
            query.setParameter("partsId", partsId);
            query.setMaxResults(1);

            return query.getSingleResult();
        } catch (NoResultException ex) {
            // 該当なしの場合、空の完成品情報を返す。
            return new PartsEntity();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 完成品情報を登録する。
     *
     * @param parts 完成品情報(JSON)
     * @param workKanbanId 製造元工程カンバンID
     * @param compDatetime 製造日
     * @return 結果 (成功:SUCCESS, 失敗:PARTS_REGIST_ERROR)
     */
    public ServerErrorTypeEnum registParts(String parts, Long workKanbanId, Date compDatetime) {
        logger.info("registParts: parts={}, workKanbanId={}, compDatetime={}", parts, workKanbanId, compDatetime);
        try {
            if (Objects.isNull(parts)) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            // 工程カンバンの完成品情報を削除する。
            this.deleteWorkKanbanParts(workKanbanId);

            List<PartsEntity> entities = JsonUtils.jsonToObjects(parts, PartsEntity[].class);
            for (PartsEntity entity : entities) {
                if (StringUtils.isEmpty(entity.getPartsId())) {
                    continue;
                }

                PartsEntity partsInfo = this.findParts(entity.getPartsId(), null);
                if (Objects.nonNull(partsInfo.getPartsId()) && !partsInfo.getRemoveFlag()) {
                    // 他の工程カンバンで登録されている。
                    return ServerErrorTypeEnum.PARTS_ADD_ERROR;
                }

                entity.setWorkKanbanId(workKanbanId);
                entity.setCompDatetime(compDatetime);

                this.add(entity);
            }
            return ServerErrorTypeEnum.SUCCESS;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ServerErrorTypeEnum.PARTS_ADD_ERROR;
        }
    }

    /**
     * 完成品情報を使用済にする。
     *
     * @param partsIds 部品ID一覧
     * @param workKanbanId 供給先工程カンバンID
     * @return 結果 (成功:SUCCESS, 失敗:PARTS_REMOVE_ERROR)
     */
    public ServerErrorTypeEnum removeParts(List<String> partsIds, Long workKanbanId) {
        logger.info("removeParts: partsIds={}, workKanbanId={}", partsIds, workKanbanId);
        try {
            if (Objects.isNull(partsIds) || partsIds.isEmpty()) {
                return ServerErrorTypeEnum.SUCCESS;
            }

            Query query = this.em.createNamedQuery("PartsEntity.removeParts");
            query.setParameter("destWorkKanbanId", workKanbanId);
            query.setParameter("partsIds", partsIds);

            query.executeUpdate();

            return ServerErrorTypeEnum.SUCCESS;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ServerErrorTypeEnum.PARTS_DEL_ERROR;
        }
    }

    /**
     * 完成品情報を登録する。
     *
     * @param entity 完成品情報
     */
    private void add(PartsEntity entity) {
        logger.info("add: {}", entity);

        // 完成品情報を登録する。
        entity.setRemoveFlag(false);
        super.create(entity);
        this.em.flush();
    }

    /**
     * 工程カンバンIDを指定して、完成品情報を削除する。
     *
     * @param workKanbanId 工程カンバンID
     */
    private void deleteWorkKanbanParts(Long workKanbanId) {
        Query query = this.em.createNamedQuery("PartsEntity.deleteByWorkKanbanId");
        query.setParameter("workKanbanId", workKanbanId);

        query.executeUpdate();
    }

    /**
     * カンバンIDを指定して、完成品情報を削除する。
     *
     * @param kanbanId カンバンID
     */
    public void deleteKanbanParts(Long kanbanId) {
        Query query = this.em.createNamedQuery("PartsEntity.deleteByKanbanId");
        query.setParameter("kanbanId", kanbanId);

        query.executeUpdate();
    }

    /**
     * パーツIDの一部の文字列から完成品情報を検索する。
     * 
     * @param keyword パーツIDの一部の文字列
     * @param authId 認証ID
     * @return 完成品情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("search")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<PartsEntity> searchParts(@QueryParam("keyword") String keyword, @QueryParam("authId") Long authId) {
        logger.info("searchParts: keyword={}, authId={}", keyword, authId);
        try {
            if (StringUtils.isEmpty(keyword)) {
                return new ArrayList<>();
            }
            
            TypedQuery<PartsEntity> query = this.em.createNamedQuery("PartsEntity.searchByPartsId", PartsEntity.class);
            query.setParameter("keyword", "%" + keyword + "%");

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * 完成品情報を削除する。
     * 
     * @param condition 完成品情報削除条件
     * @param authId 認証ID
     * @return 処理結果
     */
    @PUT
    @Path("remove")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response removeForced(PartsRemoveCondition condition, @QueryParam("authId") Long authId) {
        logger.info("removeParts: condition={}, authId={}", condition, authId);
        
        try {
            if (Objects.isNull(condition) 
                    || Objects.isNull(condition.getItems()) 
                    || condition.getItems().isEmpty()) {
                return Response.ok().entity(ResponseWorkKanbanEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }
            
            List<String> partsIds = condition.getItems().stream()
                    .map(o -> o.getPartsId())
                    .collect(Collectors.toList());
            
            Query query = this.em.createNamedQuery("PartsEntity.removeForced");
            query.setParameter("partsIds", partsIds);
            int count = query.executeUpdate();

            return Response.ok().entity(ResponseEntity.success().userData(count).errorType(ServerErrorTypeEnum.SUCCESS)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.ok().entity(ResponseWorkKanbanEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
