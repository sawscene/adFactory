/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.object.ObjectEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * モノ情報取得用REST：モノ情報を操作するためのクラス
 *
 * @author e-mori
 * @version 設計・製造ソリューション展(2016)
 * @since 2016.06.06.Mon
 */
@Singleton
@Path("object")
public class ObjectEntityFacadeREST extends AbstractFacade<ObjectEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;
    private final Logger logger = LogManager.getLogger();

    /**
     * モノRESTクラスが使用するエンティティを継承したクラスに登録します。
     *
     */
    public ObjectEntityFacadeREST() {
        super(ObjectEntity.class);
    }

    /**
     * モノ情報を新しく追加する
     *
     * @param entity 新しいモノ情報
     * @param authId 認証ID
     * @return 成功：200 + 追加したモノのURI/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(ObjectEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("add: {}, authId={}", entity, authId);
        try {
            //モノIDの重複確認.
            TypedQuery<Long> query = em.createNamedQuery("ObjectEntity.checkByPK", Long.class);
            query.setParameter("objectId", entity.getObjectId());
            query.setParameter("objectTypeId", entity.getObjectTypeId());
            if (query.getSingleResult() > 0) {
                //該当するものがあった場合、登録しようとしているものが重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }
            //モノ名の重複確認.
            TypedQuery<Long> query2 = em.createNamedQuery("ObjectEntity.checkAddByObjectName", Long.class);
            query2.setParameter("objectTypeId", entity.getObjectTypeId());
            query2.setParameter("objectName", entity.getObjectName());
            if (query2.getSingleResult() > 0) {
                //該当するものがあった場合、登録しようとしているものが重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            //作成.作成日、削除フラグを追加して登録する
//            entity.setUpdateDatetime(new Date());// TODO: [v2対応] update_datetime 削除のため削除
            entity.setRemoveFlag(false);
            super.create(entity);
            em.flush();
            //作成したモノ情報をもとに戻り値のURIを作成する
            URI uri = new URI("object/" + entity.getObjectId());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 既存のモノ情報を更新する
     *
     * @param id 更新対象のモノID
     * @param typeId 更新対象のモノ種別ID
     * @param entity 更新する情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(@QueryParam("id") String id, @QueryParam("typeid") Long typeId, ObjectEntity entity, @QueryParam("authId") Long authId) {
        logger.info("update: id={}, typeId={}, {}, authId={}", id, typeId, entity, authId);
        try {
            // 更新対象のモノ情報がDBに登録されているか確認する。
            TypedQuery<ObjectEntity> query = em.createNamedQuery("ObjectEntity.findByPK", ObjectEntity.class);
            query.setParameter("objectId", id);
            query.setParameter("objectTypeId", typeId);
            ObjectEntity target = query.getSingleResult();
            if (Objects.isNull(target.getObjectId())) {
                // 該当するものがなかった場合、更新できないことを通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }
            //モノ名の重複確認.
            TypedQuery<Long> query2 = em.createNamedQuery("ObjectEntity.checkUpdateByObjectName", Long.class);
            query2.setParameter("objectId", id);
            query2.setParameter("objectTypeId", typeId);
            query2.setParameter("objectName", entity.getObjectName());
            if (query2.getSingleResult() > 0) {
                //該当するものがあった場合、登録しようとしているものが重複していることを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NAME_OVERLAP)).build();
            }

            // 編集日時、削除フラグを更新する。
//            entity.setUpdateDatetime(new Date());// TODO: [v2対応] update_datetime 削除のため削除
            entity.setRemoveFlag(false);

            if (id.equals(entity.getObjectId()) && typeId.equals(entity.getObjectTypeId())) {
                super.edit(entity);
                em.flush();
            } else {
                // PKが変更されている場合、元のデータを削除して、新たに登録する。
                TypedQuery<Long> checkQuery = em.createNamedQuery("ObjectEntity.checkByPK", Long.class);
                checkQuery.setParameter("objectId", entity.getObjectId());
                checkQuery.setParameter("objectTypeId", entity.getObjectTypeId());
                if (checkQuery.getSingleResult() > 0) {
                    // 変更後のPKが既に登録されていた場合、更新できないことを通知する。
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
                }
                super.remove(target);
                em.flush();
                super.create(entity);
                em.flush();
            }
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定されたIDの情報を複製する
     *
     * @param id 複製するモノ情報のモノID
     * @param typeId 複製するモノ情報のモノ種別ID
     * @param authId 認証ID
     * @return 成功：200 + 複製したモノタイプのURI/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException 戻り値のURIが,文字列を URI 参照として解析できなかった場合例外を発生します
     */
    @POST
    @Path("copy")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response copy(@QueryParam("id") String id, @QueryParam("typeid") Long typeId, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("copy: id={}, typeId={}, authId={}", id, typeId, authId);
        //複製するモノ情報がDBに登録されているか確認する
        TypedQuery<ObjectEntity> query = em.createNamedQuery("ObjectEntity.findByPK", ObjectEntity.class);
        query.setParameter("objectId", id);
        query.setParameter("objectTypeId", typeId);
        try {
            ObjectEntity entity = query.getSingleResult();
            if (Objects.isNull(entity.getObjectId())) {
                //該当するものがなかった場合、コピーできないことを通知する
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }
            //コピーのため改変した名前が登録されていないことを確認し、モノ情報を複製する
            TypedQuery<Long> checkQuery = em.createNamedQuery("ObjectEntity.checkByPK", Long.class);
            checkQuery.setParameter("objectTypeId", typeId);
            boolean isFind = true;
            String copyId = entity.getObjectId() + SUFFIX_COPY;
            while (isFind) {
                checkQuery.setParameter("objectId", copyId);
                if (checkQuery.getSingleResult() > 0) {
                    copyId += SUFFIX_COPY;
                    continue;
                }
                isFind = false;
            }
            //モノタイプ名前の重複確認.
            TypedQuery<Long> checkQuery2 = em.createNamedQuery("ObjectEntity.checkAddByObjectName", Long.class);
            checkQuery2.setParameter("objectTypeId", typeId);
            String copyName = entity.getObjectName() + SUFFIX_COPY;
            isFind = true;
            while (isFind) {
                checkQuery2.setParameter("objectName", copyName);
                if (checkQuery2.getSingleResult() > 0) {
                    copyName += SUFFIX_COPY;
                    continue;
                }
                isFind = false;
            }

            ObjectEntity newEntity = new ObjectEntity(entity);
            //複製したモノ情報に新しい名前を付け登録する
            newEntity.setObjectId(copyId);
            newEntity.setObjectName(copyName);
            return add(newEntity, authId);
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定されたIDを削除する
     *
     * @param id 削除するモノ情報のモノID
     * @param typeId 削除するモノ情報のモノ種別ID
     * @param authId 認証ID
     * @return 成功：200 + 追加したモノのURI/失敗：500
     */
    @DELETE
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("id") String id, @QueryParam("typeid") Long typeId, @QueryParam("authId") Long authId) {
        /**
         * 影響範囲が現在ないので削除時の判定処理は実装しない
         */
        logger.info("remove-real: id={}, typeId={}, authId={}", id, typeId, authId);
        //削除するモノ情報がDBに存在するか確認する
        TypedQuery<ObjectEntity> query = em.createNamedQuery("ObjectEntity.findByPK", ObjectEntity.class);
        query.setParameter("objectId", id);
        query.setParameter("objectTypeId", typeId);
        try {
            ObjectEntity target = query.getSingleResult();
            if (Objects.nonNull(target)) {
                //削除
                super.remove(target);
            }
            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 指定されたPKの情報を検索する
     *
     * @param id 検索するモノ情報のモノID
     * @param typeId 検索するモノ情報のモノ種別ID
     * @param authId 認証ID
     * @return 発見したモノ情報
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ObjectEntity find(@QueryParam("id") String id, @QueryParam("typeid") Long typeId, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, typeId={}, authId={}", id, typeId);
        
        TypedQuery<ObjectEntity> query = em.createNamedQuery("ObjectEntity.findByPK", ObjectEntity.class);
        query.setParameter("objectId", id);
        query.setParameter("objectTypeId", typeId);
        try {
            ObjectEntity entity = query.getSingleResult();
            if (Objects.isNull(entity)) {
                //検索した情報が存在しない場合は空のエンティティを返す
                return new ObjectEntity();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex);
            return new ObjectEntity();
        }
    }

    /**
     * 指定された名前の情報を検索する
     *
     * @param name 検索するモノ情報の名前
     * @param authId 認証ID
     * @return 発見したモノ情報のリスト
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ObjectEntity> findByName(@QueryParam("name") String name, @QueryParam("authId") Long authId) {
        logger.info("find: name={}, authId={}", name, authId);

        TypedQuery<ObjectEntity> query = em.createNamedQuery("ObjectEntity.findByObjectName", ObjectEntity.class);
        query.setParameter("objectName", name);
        try {
            List<ObjectEntity> entity = query.getResultList();
            if (Objects.isNull(entity)) {
                //検索した情報が存在しない場合は空のリストを返す
                return new ArrayList<>();
            }
            return entity;
        } catch (Exception ex) {
            logger.fatal(ex);
            return new ArrayList<>();
        }
    }

    /**
     * モノ情報一覧を取得する
     *
     * @param authId 認証ID
     * @return モノ情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ObjectEntity> findAll(@QueryParam("authId") Long authId) {
        logger.info("findAll: authId={}", authId);
        List<ObjectEntity> entities = super.findAll();
        return entities;
    }

    /**
     * 指定した範囲のモノ情報一覧を取得する
     *
     * @param from 指定範囲_開始
     * @param to 指定範囲_終了
     * @param authId 認証ID
     * @return 指定された範囲のモノ情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ObjectEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);
        List<ObjectEntity> entities = super.findRange(from, to);
        return entities;
    }

    /**
     * 登録されているモノ情報の数を取得する
     *
     * @param authId 認証ID
     * @return 登録数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countALL(@QueryParam("authId") Long authId) {
        logger.info("count: authId={}", authId);
        return String.valueOf(super.count());
    }

    /**
     * 指定されたモノタイプに一致するモノ情報の数を取得する
     *
     * @param typeId モノタイプID
     * @param authId 認証ID
     * @return 検索に該当したモノ情報の数
     */
    @Lock(LockType.READ)
    @GET
    @Path("type/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countByType(@QueryParam("id") Long typeId, @QueryParam("authId") Long authId) {
        logger.info("countByType: typeId={}, authId={}", typeId, authId);
        return String.valueOf(findByTypeId(typeId, null, null).size());
    }

    /**
     * 指定されたモノタイプに一致するモノ情報を範囲指定し取得する
     *
     * @param typeId モノタイプID
     * @param from 指定範囲_開始
     * @param to 指定範囲_終了
     * @param authId 認証ID
     * @return 検索に該当するモノ情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("type/range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ObjectEntity> findByType(@QueryParam("id") Long typeId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findByType: typeId={}, from={}, to={}, authId={}", typeId, from, to, authId);
        return findByTypeId(typeId, from, to);
    }

    /**
     * モノタイプIDに一致するモノ情報の取得
     *
     * @param typeId モノタイプID
     * @param from 指定範囲_開始
     * @param to 指定範囲_終了
     * @return 該当するモノ情報一覧
     */
    @Lock(LockType.READ)
    private List<ObjectEntity> findByTypeId(Long typeId, Integer from, Integer to) {
        TypedQuery<ObjectEntity> query = em.createNamedQuery("ObjectEntity.findByObjectTypeId", ObjectEntity.class);
        query.setParameter("objectTypeId", typeId);
        //範囲の指定がある場合(from~to)クエリに取得範囲を設定する
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            query.setMaxResults(to - from + 1);
            query.setFirstResult(from);
        }

        //モノタイプIDに一致するモノ情報の検索結果を返す
        return query.getResultList();
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
}
