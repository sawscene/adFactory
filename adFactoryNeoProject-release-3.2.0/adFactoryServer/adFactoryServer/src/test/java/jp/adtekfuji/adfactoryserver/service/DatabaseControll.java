/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 *
 * @author ke.yokoi
 */
public class DatabaseControll {

    public static void reset(EntityManager em, EntityTransaction tx) {
        tx.begin();
        //実績
        em.createQuery("DELETE FROM ActualResultEntity").executeUpdate();// 工程実績
        //カンバン
        em.createQuery("DELETE FROM ConKanbanHierarchyEntity").executeUpdate();// カンバン階層関連付け
        em.createQuery("DELETE FROM ConWorkkanbanEquipmentEntity").executeUpdate();// 工程カンバン・設備関連付け
        em.createQuery("DELETE FROM ConWorkkanbanOrganizationEntity").executeUpdate();// 工程カンバン・組織関連付け
        em.createQuery("DELETE FROM KanbanHierarchyEntity").executeUpdate();// カンバン階層マスタ
        em.createQuery("DELETE FROM TreeKanbanHierarchyEntity").executeUpdate();// カンバン階層
        em.createQuery("DELETE FROM WorkKanbanEntity").executeUpdate();// 工程カンバン
        em.createQuery("DELETE FROM KanbanEntity").executeUpdate();// カンバン
        //階層
        em.createQuery("DELETE FROM HierarchyEntity").executeUpdate();// 階層マスタ
        em.createQuery("DELETE FROM ConHierarchyEntity").executeUpdate();// 階層関連付け
        //工程順
        em.createQuery("DELETE FROM ConWorkEquipmentEntity").executeUpdate();// 工程・設備関連付け
        em.createQuery("DELETE FROM ConWorkOrganizationEntity").executeUpdate();// 工程・組織関連付け
        em.createQuery("DELETE FROM WorkflowEntity").executeUpdate();// 工程順マスタ
        //工程
        em.createQuery("DELETE FROM WorkEntity").executeUpdate();// 工程マスタ
        //設備
        // 設備種別EquipmentTypeEntityについては起動後に一度きり作成されるためリセットしない
//        em.createQuery("DELETE FROM EquipmentTypeEntity").executeUpdate();// 設備種別マスタ
        em.createQuery("DELETE FROM EquipmentEntity").executeUpdate();// 設備マスタ
        //組織
        em.createQuery("DELETE FROM AuthenticationInfoEntity").executeUpdate();// 認証情報
        em.createQuery("DELETE FROM ConOrganizationBreaktimeEntity").executeUpdate();// 組織・休憩関連付け
        em.createQuery("DELETE FROM ConOrganizationRoleEntity").executeUpdate();// 組織・役割関連付け
        em.createQuery("DELETE FROM ConOrganizationWorkCategoryEntity").executeUpdate();// 組織・作業区分関連付け
        em.createQuery("DELETE FROM OrganizationEntity").executeUpdate();// 組織マスタ
        //その他
        em.createQuery("DELETE FROM BreaktimeEntity").executeUpdate();// 休憩マスタ設定項目
        em.createQuery("DELETE FROM DisplayedStatusEntity").executeUpdate();// ステータス表示マスタ
        em.createQuery("DELETE FROM RoleAuthorityEntity").executeUpdate();// 役割権限マスタプロパティ
        em.createQuery("DELETE FROM HolidayEntity").executeUpdate();// 休日情報
        em.createQuery("DELETE FROM IndirectWorkEntity").executeUpdate();// 間接作業マスタ
        em.createQuery("DELETE FROM ObjectEntity").executeUpdate();// モノマスタ
        em.createQuery("DELETE FROM ObjectTypeEntity").executeUpdate();// モノ種別マスタ
        em.createQuery("DELETE FROM ReasonMasterEntity").executeUpdate();// 理由マスタ
        em.createQuery("DELETE FROM ScheduleEntity").executeUpdate();// 予定情報
        em.createQuery("DELETE FROM WorkCategoryEntity").executeUpdate();// 作業区分マスタ
        em.createQuery("DELETE FROM WorkSectionEntity").executeUpdate();// 工程セクション
        em.createQuery("DELETE FROM AccessHierarchyEntity").executeUpdate();// 階層アクセス権
        em.createQuery("DELETE FROM ProdResultEntity").executeUpdate();// 生産実績
        em.createQuery("DELETE FROM ProductEntity").executeUpdate();// 製品
        em.createQuery("DELETE FROM WorkKanbanWorkingEntity").executeUpdate();// 工程カンバン作業中リスト
        em.createQuery("DELETE FROM IndirectActualEntity").executeUpdate();// 間接工数実績情報
        em.createQuery("DELETE FROM PartsEntity").executeUpdate();// 完成品情報
        em.createQuery("DELETE FROM KanbanReportEntity").executeUpdate();// カンバン帳票情報
        em.createQuery("DELETE FROM RoleAuthorityEntity").executeUpdate(); // 役割権限
        // 承認
        em.createQuery("DELETE FROM ApprovalRouteEntity").executeUpdate(); // 承認ルート
        em.createQuery("DELETE FROM ApprovalOrderEntity").executeUpdate(); // 承認順
        em.createQuery("DELETE FROM ApprovalEntity").executeUpdate(); // 申請
        em.createQuery("DELETE FROM ApprovalFlowEntity").executeUpdate(); // 承認フロー
        // NFT特殊
        em.createQuery("DELETE FROM AssemblyPartsEntity").executeUpdate(); // 使用部品
        tx.commit();
    }
}
