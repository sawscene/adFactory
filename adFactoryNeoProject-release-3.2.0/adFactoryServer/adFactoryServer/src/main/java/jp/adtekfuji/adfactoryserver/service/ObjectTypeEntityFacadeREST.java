/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
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
import jp.adtekfuji.adfactoryserver.entity.object.ObjectTypeEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * モノタイプ取得用REST:モノタイプ情報を操作するためのクラス
 *
 * @author e-mori
 * @version 設計・製造ソリューション展(2016)
 * @since 2016.06.06.Mon
 */
@Stateless
@Path("object-type")
public class ObjectTypeEntityFacadeREST extends AbstractFacade<ObjectTypeEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    @EJB
    private ObjectEntityFacadeREST objectEntityFacadeREST;
    private final Logger logger = LogManager.getLogger();

    /**
     * モノタイプRESTクラスが使用するエンティティを継承したクラスに登録します。
     *
     */
    public ObjectTypeEntityFacadeREST() {
        super(ObjectTypeEntity.class);
    }

    /**
     * 新しいモノタイプを追加する
     *
     * @param entity 追加するモノタイプ
     * @param authId 認証ID
     * @return 成功：200 + 追加したモノタイプのURI/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(ObjectTypeEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            //モノタイプIDの重複確認.
            TypedQuery<Long> query1 = em.createNamedQuery("ObjectTypeEntity.checkAddByObjectTypeId", Long.class);
            query1.setParameter("objectTypeId", entity.getObjectTypeId());
            if (query1.getSingleResult() > 0) {
                //該当するものがあった場合、登録しようとしているものが重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
            //モノタイプ名前の重複確認.
            TypedQuery<Long> query2 = em.createNamedQuery("ObjectTypeEntity.checkAddByObjectTypeName", Long.class);
            query2.setParameter("objectTypeName", entity.getObjectTypeName());
            if (query2.getSingleResult() > 0) {
                //該当するものがあった場合、登録しようとしているものが重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            //作成.
            super.create(entity);
            em.flush();
            //作成したモノ種別情報をもとに戻り値のURIを作成する
            URI uri = new URI("objecttype/" + entity.getObjectTypeId());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 既存のモノタイプを更新する
     *
     * @param entity 更新するモノタイプの情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(ObjectTypeEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: {}, authId={}", entity, authId);
        try {
            // 更新対象のモノ種別情報がDBに登録されているか確認する。
            TypedQuery<ObjectTypeEntity> query = em.createNamedQuery("ObjectTypeEntity.findByObjectTypeId", ObjectTypeEntity.class);
            query.setParameter("objectTypeId", entity.getObjectTypeId());
            ObjectTypeEntity target = query.getSingleResult();
            if (Objects.isNull(target.getObjectTypeId())) {
                // 該当するものがなかった場合、更新できないことを通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            // 排他用バージョンを確認する。
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            //モノ種別名の重複確認.
            TypedQuery<Long> query2 = em.createNamedQuery("ObjectTypeEntity.checkUpdateByObjectTypeName", Long.class);
            query2.setParameter("objectTypeId", entity.getObjectTypeId());
            query2.setParameter("objectTypeName", entity.getObjectTypeName());
            if (query2.getSingleResult() > 0) {
                //該当するものがあった場合、登録しようとしているものが重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            super.edit(entity);
            em.flush();
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定したIDのモノタイプを削除する
     *
     * @param id 削除するモノタイプのID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("remove: id={}, authId={}", id, authId);

        //IDに該当するモノタイプを利用しているモノ情報の数を取得する
        long count = Long.parseLong(objectEntityFacadeREST.countByType(id, authId));
        if (!(count > 0)) {
            logger.info("remove-real:{}", id);
            //削除するモノタイプ情報がDBに存在するか確認する
            ObjectTypeEntity target = super.find(id);
            if (Objects.nonNull(target)) {
                //削除
                super.remove(target);
            }
            return Response.ok().entity(ResponseEntity.success()).build();
        }
        //モノ情報に使用されているため削除できないことを通知する
        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.EXIST_CHILD_DELETE)).build();
    }

    /**
     * 指定したIDの情報を検索する
     *
     * @param id 検索するモノタイプのID
     * @param authId 認証ID
     * @return 検索に該当するモノタイプ情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public ObjectTypeEntity find(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, authId={}", id, authId);

        ObjectTypeEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            //検索した情報が存在しない場合は空のエンティティを返す
            return new ObjectTypeEntity();
        }
        return entity;
    }

    /**
     * 指定した名前の情報を検索する
     *
     * @param name 検索するモノタイプの名前
     * @param authId 認証ID
     * @return 検索に該当するモノタイプ情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ObjectTypeEntity findByName(@QueryParam("name") String name, @QueryParam("authId") Long authId) {
        logger.info("find: name={}, authId={}", name, authId);

        TypedQuery<ObjectTypeEntity> query = em.createNamedQuery("ObjectTypeEntity.findByObjectTypeName", ObjectTypeEntity.class);
        query.setParameter("objectTypeName", name);
        try {
            ObjectTypeEntity entity = query.getSingleResult();
            if (Objects.isNull(entity)) {
                //検索した情報が存在しない場合は空のエンティティを返す
                return new ObjectTypeEntity();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex);
            return new ObjectTypeEntity();
        }
    }

    /**
     * 登録されているモノタイプの一覧を取得
     *
     * @param authId 認証ID
     * @return モノタイプ一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ObjectTypeEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);

        List<ObjectTypeEntity> entities = super.findAll();
        entities.sort(Comparator.comparing(entity -> entity.getObjectTypeName()));
        return entities;
    }

    /**
     * 指定した範囲の情報を取得する
     *
     * @param from 指定範囲_開始
     * @param to 指定範囲_終了
     * @param authId 認証ID
     * @return 範囲に該当したモノタイプの一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ObjectTypeEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);

        List<ObjectTypeEntity> entities = super.findRange(from, to);
        entities.sort(Comparator.comparing(entity -> entity.getObjectTypeName()));
        return entities;
    }

    /**
     * 登録されているモノタイプの一覧を取得する
     *
     * @param authId 認証ID
     * @return 個数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("count: authId={}", authId);
        return String.valueOf(super.count());
    }

    /**
     * エンティティマネージャーを渡します
     *
     * @return エンティティマネージャー
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager value) {
        this.em = value;
    }

    public void setObjectRest(ObjectEntityFacadeREST value) {
        this.objectEntityFacadeREST = value;
    }
}
