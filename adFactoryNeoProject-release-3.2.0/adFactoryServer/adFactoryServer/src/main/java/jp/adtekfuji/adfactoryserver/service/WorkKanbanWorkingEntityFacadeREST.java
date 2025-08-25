/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.List;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanWorkingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程カンバン作業中情報REST
 *
 * @author ke.yokoi
 */
@Singleton
public class WorkKanbanWorkingEntityFacadeREST extends AbstractFacade<WorkKanbanWorkingEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public WorkKanbanWorkingEntityFacadeREST() {
        super(WorkKanbanWorkingEntity.class);
    }

    /**
     * 作業中リストを更新する。
     *
     * @param status 工程ステータス
     * @param entity 工程カンバン作業中情報
     * @param byLite adProduct Liteによる更新
     * @return 
     */
    public long updateWorking(KanbanStatusEnum status, WorkKanbanWorkingEntity entity, Boolean byLite) {
        // 作業開始なら追加し、それ以外なら抜く
        if (status == KanbanStatusEnum.WORKING) {
            this.pushWorking(entity);
        } else {
            this.pullWorking(entity);
        }

        this.em.flush();

        // 作業中の工程数を返す
        TypedQuery<Long> countQuery = this.em.createNamedQuery("WorkKanbanWorkingEntity.countByWorkKanbanId", Long.class);
        countQuery.setParameter("workKanbanId", entity.getWorkKanbanId());

        return countQuery.getSingleResult();
    }

    /**
     * 応援者の人数を取得する
     * @param workKanbanId　カンバンID
     * @return 応援者の人数
     */
    public long getSupporterNumber(Long workKanbanId) {
        // 作業中の工程数を返す
        TypedQuery<Long> countQuery = this.em.createNamedQuery("WorkKanbanWorkingEntity.countSupporterByWorkKanbanID", Long.class);
        countQuery.setParameter("workKanbanId", workKanbanId);
        countQuery.setParameter("supporterFlag", true);

        return countQuery.getSingleResult();
    }

    /**
     * 
     * @param working 
     */
    private void pushWorking(WorkKanbanWorkingEntity working) {
        logger.info("pushWorking: {}", working);
        super.create(working);
    }

    /**
     * 工程カンバン作業中をプルする。
     * 
     * @param working 工程カンバン作業中情報
     */
    //private void pullWorking(WorkKanbanWorkingEntity working) {
    //    try {
    //        logger.info("pullWorking: {}", working);
    //
    //        TypedQuery<WorkKanbanWorkingEntity> query = this.em.createNamedQuery("WorkKanbanWorkingEntity.find", WorkKanbanWorkingEntity.class);
    //        query.setParameter("workKanbanId", working.getWorkKanbanId());
    //        query.setParameter("equipmentId", working.getEquipmentId());
    //
    //        // 作業中にadProductが強制終了すると、複数のデータが残るため、リストで処理する
    //        List<WorkKanbanWorkingEntity> list = query.getResultList();
    //        for (WorkKanbanWorkingEntity entity : list) {
    //            super.remove(entity);
    //        }
    //    } finally {
    //        logger.info("pullWorking end.");
    //    }
    //}
    
    /**
     * 工程カンバン作業中をプルする。
     * 
     * @param working 工程カンバン作業中情報
     */
    private void pullWorking(WorkKanbanWorkingEntity working) {
        try {
            logger.info("pullWorkingByLite: {}", working);

            TypedQuery<WorkKanbanWorkingEntity> query = this.em.createNamedQuery("WorkKanbanWorkingEntity.findByOrganizationId", WorkKanbanWorkingEntity.class);
            query.setParameter("workKanbanId", working.getWorkKanbanId());
            query.setParameter("organizationId", working.getOrganizationId());

            // 作業中にadProductが強制終了すると、複数のデータが残るため、リストで処理する
            List<WorkKanbanWorkingEntity> list = query.getResultList();
            for (WorkKanbanWorkingEntity entity : list) {
                super.remove(entity);
            }
        } finally {
            logger.info("pullWorkingByLite end.");
        }
    }
    
    /**
     * 作業中の個数を返す
     * 
     * @param workKanbanId
     * @return 
     */
    @Lock(LockType.READ)
    public Long countWorking(Long workKanbanId) {
        // 工程カンバンIDを指定して、工程カンバン作業中情報の件数を取得する。
        TypedQuery<Long> countQuery = em.createNamedQuery("WorkKanbanWorkingEntity.countByWorkKanbanId", Long.class);
        countQuery.setParameter("workKanbanId", workKanbanId);
        return countQuery.getSingleResult();
    }

    /**
     * 工程カンバンIDを指定して、工程カンバン作業中情報を取得する。
     *
     * @param workKanbanId 工程カンバンID
     * @return 、工程カンバン作業中情報
     */
    @Lock(LockType.READ)
    public List<WorkKanbanWorkingEntity> getWorking(Long workKanbanId) {
        TypedQuery<WorkKanbanWorkingEntity> query = this.em.createNamedQuery("WorkKanbanWorkingEntity.findByWorkKanbanId", WorkKanbanWorkingEntity.class);
        query.setParameter("workKanbanId", workKanbanId);
        return query.getResultList();
    }

    /**
     * 工程カンバンIDを指定して、工程カンバン作業中情報を削除する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void deleteWorking(Long workKanbanId) {
        Query query = this.em.createNamedQuery("WorkKanbanWorkingEntity.removeByWorkKanbanId");
        query.setParameter("workKanbanId", workKanbanId);
        query.executeUpdate();
    }

    /**
     * 工程カンバンID一覧を指定して、工程カンバン作業中情報を削除する。
     *
     * @param workKanbanIds 工程カンバンID一覧
     */
    public void deleteWorkings(List<Long> workKanbanIds) {
        Query query = this.em.createNamedQuery("WorkKanbanWorkingEntity.removeByWorkKanbanIds");
        query.setParameter("workKanbanIds", workKanbanIds);
        query.executeUpdate();
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
