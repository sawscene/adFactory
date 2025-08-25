/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model.approval;

import java.util.Arrays;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalFlowEntity;

/**
 * 
 * @author nar-nakamura
 */
public class ApprovalModel {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * 申請IDを指定して、申請情報を取得する。
     *
     * @param approvalId 申請ID
     * @return 申請情報
     */
    public ApprovalEntity findApproval(long approvalId) {
        try {
            ApprovalEntity approval = this.em.find(ApprovalEntity.class, approvalId);

            // 承認フロー情報一覧
            List<ApprovalFlowEntity> approvalFlows = this.findApprovalFlowByApprovalId(Arrays.asList(approvalId));
            approval.setApprovalFlows(approvalFlows);

            return approval;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 申請IDを指定して、申請情報を削除する。
     *
     * @param approvalId 申請ID
     */
    public void removeApproval(long approvalId) {

        // 承認フロー情報一覧を削除する。
        this.removeApprovalFlowByApprovalId(approvalId);

        // 申請情報を削除する。
        Query query = this.em.createNamedQuery("ApprovalEntity.remove");
        query.setParameter("approvalId", approvalId);
        query.executeUpdate();
    }

    /**
     * 申請IDを指定して、承認フロー情報一覧を取得する。
     *
     * @param approvalIds 申請ID一覧
     * @return 承認フロー情報一覧
     */
    private List<ApprovalFlowEntity> findApprovalFlowByApprovalId(List<Long> approvalIds) {
        TypedQuery<ApprovalFlowEntity> query = this.em.createNamedQuery("ApprovalFlowEntity.findByApprovalId", ApprovalFlowEntity.class);
        query.setParameter("approvalIds", approvalIds);
        return query.getResultList();
    }

    /**
     * 申請IDを指定して、承認フロー情報を削除する。
     *
     * @param approvalId 申請ID
     */
    public void removeApprovalFlowByApprovalId(long approvalId) {
        Query query = this.em.createNamedQuery("ApprovalFlowEntity.removeByApprovalId");
        query.setParameter("approvalId", approvalId);
        query.executeUpdate();
    }
}
