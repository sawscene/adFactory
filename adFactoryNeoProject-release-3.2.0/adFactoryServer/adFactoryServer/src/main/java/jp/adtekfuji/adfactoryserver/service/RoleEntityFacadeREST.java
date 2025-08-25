/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.RoleAuthorityEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 役割情報REST：役割情報を操作するためのクラス
 *
 * @author ke.yokoi
 */
@Stateless
@Path("role")
public class RoleEntityFacadeREST extends AbstractFacade<RoleAuthorityEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public RoleEntityFacadeREST() {
        super(RoleAuthorityEntity.class);
    }

    /**
     * 役割情報を登録する。
     *
     * @param entity 役割情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(RoleAuthorityEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add:{}", entity);
        try {
            // 重複確認
            TypedQuery<Long> query = this.em.createNamedQuery("RoleAuthorityEntity.checkAddByRoleName", Long.class);
            query.setParameter("roleName", entity.getRoleName());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            //役割更新.
            super.create(entity);
            this.em.flush();

            URI uri = new URI("role/" + entity.getRoleId().toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 役割情報を更新する。
     *
     * @param entity 役割情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(RoleAuthorityEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update:{}", entity);
        try {
            // 排他用バージョンを確認する。
            RoleAuthorityEntity target = super.find(entity.getRoleId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 重複確認
            TypedQuery<Long> query = this.em.createNamedQuery("RoleAuthorityEntity.checkUpdateByRoleName", Long.class);
            query.setParameter("roleName", entity.getRoleName());
            query.setParameter("roleId", entity.getRoleId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 役割情報を更新する。
            super.edit(entity);

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したIDの役割情報を削除する。
     *
     * @param id 役割ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove:{}", id);
        try {
            //組織・役割関連付け削除.
            Query query = this.em.createNamedQuery("ConOrganizationRoleEntity.removeByRoleId");
            query.setParameter("roleId", id);
            query.executeUpdate();
            //役割削除.
            super.remove(super.find(id));
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したIDの役割情報を取得する。
     *
     * @param id 役割ID
     * @param authId 認証ID
     * @return 役割情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public RoleAuthorityEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find:{}", id);
        RoleAuthorityEntity role = super.find(id);
        if (Objects.isNull(role)) {
            role = new RoleAuthorityEntity();
        }
        return role;
    }

    /**
     * 役割情報一覧を取得する。
     *
     * @param authId 認証ID
     * @return 役割情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<RoleAuthorityEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll");
        return super.findAll();
    }

    /**
     * 役割情報一覧を範囲指定して取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の役割情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<RoleAuthorityEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange:{},{}", from, to);
        List<RoleAuthorityEntity> roleEntities = super.findRange(from, to);
        return roleEntities;
    }

    /**
     * 役割情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("countREST");
        return String.valueOf(super.count());
    }

    /**
     *
     * 基本の役割を返します
     * 
     * @return 役割
     */
    @Lock(LockType.READ)
    public RoleAuthorityEntity getDefualtRole() {
        // リソース編集権限のある役割を取得する。
        RoleAuthorityEntity role = null;
        try {
            TypedQuery<RoleAuthorityEntity> query = this.em.createNamedQuery("RoleAuthorityEntity.findResourceEdit", RoleAuthorityEntity.class);
            query.setMaxResults(1);

            role = query.getSingleResult();
        } catch (NoResultException ex) {
            // 該当なし
        }

        // 存在しない場合は新規作成する。
        if (Objects.isNull(role)) {
            logger.info("create defualt role");
            role = new RoleAuthorityEntity(null, DEFAULT_ROLE);

            role.setActualDel(false);
            role.setResourceEdit(true);
            role.setKanbanCreate(true);
            role.setLineManage(true);
            role.setActualOutput(true);
            role.setKanbanReference(true);
            role.setResourceReference(true);
            role.setAccessEdit(true);
            role.setWorkflowEdit(true);
            role.setWorkflowReference(true);

            try {
                this.add(role, null);
            } catch (URISyntaxException ex) {
                logger.fatal(ex, ex);
            }
        }
        return role;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
