/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model.approval;

import adtekfuji.utility.StringUtils;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBException;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.approval.ApprovalHistoryInfo;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.common.ServiceConfig;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalFlowEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalOrderEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalRouteEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.RoleAuthorityEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.system.TroubleReportConfig;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.service.WorkEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.WorkflowEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.mail.MailProperty;
import jp.adtekfuji.adfactoryserver.service.mail.MailUtils;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.LocaleUtils;
import jp.adtekfuji.adfactoryserver.utility.RoleUtils;
import jp.adtekfuji.adinterfaceservicecommon.plugin.htmlmail.SimpleHtmlBuilder;
import jp.adtekfuji.bpmn.model.BpmnModel;
import jp.adtekfuji.bpmn.model.BpmnModeler;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 承認フローモデル
 *
 * @author nar-nakamura
 */
@Singleton
public class ApprovalFlowModel {

    /**
     * コピー時の接尾語
     */
    private static final String SUFFIX_COPY = "-copy";

    /**
     * 送信メール文字コードのデフォルト値
     */
    private static final String SMTP_CHARSET = StandardCharsets.UTF_8.toString();

    /**
     * DOCTYPE
     */
    private static final String DPCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

    /**
     * 空白行
     */
    private static final String BRANK_HEIGHT = "height:40px;";

    /**
     * 中央揃え（配置）
     */
    private static final String CENTER_STYLE = "align: center;";

    /**
     * リンクの書式
     */
    private static final String LINK_STYLE = "line-height:3; display: block; color: #ffffff; text-align: center; font-weight: bold; text-decoration: none; mso-border-alt:none;";

    /**
     * ボタンの書式
     */
    private static final String BUTTON_STYLE = "height: 50px; background-color: #0070c0;";

    /**
     * ボタンの横幅
     */
    private static final String BUTTON_SIZE = "width: 350px";

    /**
     * テーブルCSS
     */
    private static final String TABLE_STYLE = "border-collapse:collapse; padding: 10px 10px 10px 10px;";

    /**
     * 項目CSS
     */
    private static final String ITEM_STYLE = "padding: 10px; min-width: 150px; min-height:40px; width: 30%;";

    /**
     * 値CSS
     */
    private static final String VALUE_STYLE = "padding: 10px; min-width: 200px; min-height:40px; width: 70%;";

    /**
     * メールリンク
     */
    private static final String PATH = "/adFactoryServer/approval?id=%s&organization=%s";

    /**
     * 通知区分
     */
    private enum NotificationCategoryEnum {
        /**
         * 申請通知
         */
        REQUEST,
        /**
         * 申請取消通知
         */
        REQUEST_CANCEL,
        /**
         * 承認通知
         */
        APPROVAL,
        /**
         * 却下通知
         */
        REMAND,
        /**
         * 承認取消通知
         */
        APPROVAL_CANCEL;
    }

    private final Logger logger = LogManager.getLogger();

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @Inject
    private ApprovalModel approvalModel;

    @EJB
    private WorkEntityFacadeREST workRest;
    @EJB
    private WorkflowEntityFacadeREST workflowRest;

    /**
     * EntityManager を設定する。
     *
     * @param em EntityManager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     *
     * @param approvalModel
     */
    public void setApprovalModel(ApprovalModel approvalModel) {
        this.approvalModel = approvalModel;
    }

    /**
     *
     * @param workflowRest
     */
    public void setWorkflowEntityFacadeREST(WorkflowEntityFacadeREST workflowRest) {
        this.workflowRest = workflowRest;
    }

    /**
     * 承認ルート情報を登録する。
     *
     * @param entity 承認ルート情報
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity addApprovalRoute(ApprovalRouteEntity entity, Long authId) {
        logger.info("addApprovalRoute: {}, authId={}", entity, authId);
        try {
            // 承認ルート名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("ApprovalRouteEntity.checkAddByName", Long.class);
            query.setParameter("routeName", entity.getRouteName());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
            }

            this.em.persist(entity);
            this.em.flush();

            URI uri = new URI(new StringBuilder("approval-route/").append(entity.getRouteId()).toString());
            return ResponseEntity.success().uri(uri);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ルートIDを指定して、承認ルート情報をコピーする。(承認順情報一覧もコピー)
     *
     * @param id ルートID
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity copyApprovalRoute(Long id, Long authId) {
        logger.info("copyApprovalRoute: id={}, authId={}", id, authId);
        try {
            // 承認ルート情報を取得する。
            ApprovalRouteEntity entity = this.findApprovalRoute(id, authId);
            if (Objects.isNull(entity)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE);
            }

            boolean isFind = true;
            StringBuilder name = new StringBuilder(entity.getRouteName())
                    .append(SUFFIX_COPY);
            while (isFind) {
                // 承認ルート名の重複を確認する。(削除済も含む)
                TypedQuery<Long> checkQuery = em.createNamedQuery("ApprovalRouteEntity.checkAddByName", Long.class);
                checkQuery.setParameter("routeName", name.toString());
                if (checkQuery.getSingleResult() > 0) {
                    name.append(SUFFIX_COPY);
                    continue;
                }
                isFind = false;
            }

            ApprovalRouteEntity newEntity = new ApprovalRouteEntity();
            newEntity.setRouteName(name.toString());

            this.em.persist(newEntity);
            this.em.flush();

            long routeId = newEntity.getRouteId();

            // 承認順情報をコピーする。
            if (Objects.nonNull(entity.getApprovalOrders())) {
                List<ApprovalOrderEntity> approvalOrders = new ArrayList();

                for (ApprovalOrderEntity approvalOrder : entity.getApprovalOrders()) {
                    ApprovalOrderEntity newApprovalOrder = new ApprovalOrderEntity(routeId, approvalOrder);

                    this.em.persist(newApprovalOrder);

                    approvalOrders.add(newApprovalOrder);
                }

                this.em.flush();

                newEntity.setApprovalOrders(approvalOrders);
            }

            URI uri = new URI(new StringBuilder("approval-route/").append(newEntity.getRouteId()).toString());
            return ResponseEntity.success().uri(uri);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 承認ルート情報を更新する。(承認順情報一覧も更新)
     *
     * @param entity 承認ルート情報
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity updateApprovalRoute(ApprovalRouteEntity entity, Long authId) {
        logger.info("updateApprovalRoute: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            ApprovalRouteEntity target = this.em.find(ApprovalRouteEntity.class, entity.getRouteId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO);
            }

            // 承認ルート名の重複を確認する。(削除済も含む)
            TypedQuery<Long> query = this.em.createNamedQuery("ApprovalRouteEntity.checkUpdateByName", Long.class);
            query.setParameter("routeName", entity.getRouteName());
            query.setParameter("routeId", entity.getRouteId());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            this.em.merge(entity);

            // 承認順情報一覧
            this.removeApprovalOrderByRouteId(entity.getRouteId());

            if (Objects.nonNull(entity.getApprovalOrders()) && !entity.getApprovalOrders().isEmpty()) {
                for (ApprovalOrderEntity approvalOrder : entity.getApprovalOrders()) {
                    this.em.persist(approvalOrder);
                }

                this.em.flush();
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ルートIDを指定して、承認ルート情報を削除する。(承認順情報も削除)
     *
     * @param id ルートID
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity removeApprovalRoute(Long id, Long authId) {
        logger.info("removeApprovalRoute: id={}, authId={}", id, authId);
        try {
            // 承認順情報を削除する。
            this.removeApprovalOrderByRouteId(id);

            // 承認ルート情報を削除する。
            this.em.remove(this.em.find(ApprovalRouteEntity.class, id));

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * ルートIDを指定して、承認ルート情報を取得する。(承認順情報も取得)
     *
     * @param id ルートID
     * @param authId 認証ID
     * @return 承認ルート情報
     */
    public ApprovalRouteEntity findApprovalRoute(Long id, Long authId) {
        logger.info("findApprovalRoute: id={}, authId={}", id, authId);
        try {
            ApprovalRouteEntity approvalRoute = this.em.find(ApprovalRouteEntity.class, id);
            if (Objects.nonNull(approvalRoute)) {
                // 承認順情報一覧
                List<ApprovalOrderEntity> approvalOrders = this.findApprovalOrderByRouteId(Arrays.asList(id));
                approvalRoute.setApprovalOrders(approvalOrders);
            }

            return approvalRoute;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 承認ルート情報一覧を取得する。(承認順情報も取得)
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 承認ルート情報一覧
     */
    public List<ApprovalRouteEntity> findApprovalRouteRange(Integer from, Integer to, Long authId) {
        logger.info("findApprovalRouteRange: from={}, to={}, authId={}", from, to, authId);
        try {
            TypedQuery<ApprovalRouteEntity> query = this.em.createNamedQuery("ApprovalRouteEntity.findAll", ApprovalRouteEntity.class);

            boolean isAll = true;
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
                isAll = false;
            }

            List<ApprovalRouteEntity> approvalRoutes = query.getResultList();

            // 承認順情報一覧を取得する。
            List<ApprovalOrderEntity> approvalOrders;
            if (isAll) {
                approvalOrders = this.findAllApprovalOrder();
            } else {
                List<Long> routeIds = approvalRoutes.stream()
                        .map(p -> p.getRouteId())
                        .collect(Collectors.toList());
                approvalOrders = this.findApprovalOrderByRouteId(routeIds);
            }

            // 承認順情報をルートIDでマッピングする。
            Map<Long, List<ApprovalOrderEntity>> routeOrdersMap = approvalOrders.stream()
                    .collect(Collectors.groupingBy(ApprovalOrderEntity::getRouteId));

            // 承認順情報を承認ルート情報にセットする。
            if (!routeOrdersMap.isEmpty()) {
                for (ApprovalRouteEntity approvalRoute : approvalRoutes) {
                    List<ApprovalOrderEntity> routeOrders;
                    if (routeOrdersMap.containsKey(approvalRoute.getRouteId())) {
                        routeOrders = routeOrdersMap.get(approvalRoute.getRouteId());
                    } else {
                        routeOrders = new ArrayList();
                    }

                    approvalRoute.setApprovalOrders(routeOrders);
                }
            }

            return approvalRoutes;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 承認ルート情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 件数
     */
    public long countAllApprovalRoute(Long authId) {
        logger.info("countAllApprovalRoute: authId={}", authId);
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("ApprovalRouteEntity.countAll", Long.class);

            return query.getSingleResult();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 承認順情報をすべて取得する。
     *
     * @return 承認順情報一覧
     */
    private List<ApprovalOrderEntity> findAllApprovalOrder() {
        TypedQuery<ApprovalOrderEntity> query = this.em.createNamedQuery("ApprovalOrderEntity.findAll", ApprovalOrderEntity.class);
        return query.getResultList();
    }

    /**
     * 承認ルートIDを指定して、承認順情報一覧を取得する。
     *
     * @param routeId ルートID一覧
     * @return 承認順情報一覧
     */
    private List<ApprovalOrderEntity> findApprovalOrderByRouteId(List<Long> routeIds) {
        TypedQuery<ApprovalOrderEntity> query = this.em.createNamedQuery("ApprovalOrderEntity.findByRouteId", ApprovalOrderEntity.class);
        query.setParameter("routeIds", routeIds);
        return query.getResultList();
    }

    /**
     * 承認ルートIDを指定して、承認順情報を削除する。
     *
     * @param routeId ルートID
     */
    private void removeApprovalOrderByRouteId(long routeId) {
        Query query = this.em.createNamedQuery("ApprovalOrderEntity.removeByRouteId");
        query.setParameter("routeId", routeId);
        query.executeUpdate();
    }

    /**
     * 申請IDを指定して、申請情報を取得する。
     *
     * @param approvalId 申請ID
     * @return 申請情報
     */
    public ApprovalEntity findApproval(long approvalId) {
        return this.approvalModel.findApproval(approvalId);
    }

    /**
     * 工程・工程順の変更を申請する。
     *
     * @param entity 申請情報(申請)
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity apply(ApprovalEntity entity, Long authId) {
        logger.info("apply: {}, authId={}", entity, authId);

        if (Objects.isNull(entity.getDataType())
                || Objects.isNull(entity.getNewData())
                || Objects.isNull(entity.getRouteId())
                || Objects.isNull(entity.getRequestorId())
                || Objects.isNull(entity.getRequestDatetime())
                || Objects.isNull(authId)) {
            return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
        }

        // 組織を取得する。
        OrganizationEntity requestor = this.findOrganization(authId);
        if (Objects.isNull(requestor)) {
            return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
        }

        // リソース編集権限をチェックする。
        boolean auth = RoleUtils.checkResourceEditRole(this.em, requestor);
        if (!auth) {
            return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
        }

        // 承認ルートを取得する。
        ApprovalRouteEntity approvalRoute = this.findApprovalRoute(entity.getRouteId(), null);
        if (Objects.isNull(approvalRoute)) {
            return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
        }

        switch (entity.getDataType()) {
            case WORK:
                return this.applyWork(entity, requestor, approvalRoute, authId);
            case WORKFLOW:
                return this.applyWorkflow(entity, requestor, approvalRoute, authId);
            case KANBAN:
            default:
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
        }
    }

    /**
     * 工程の変更を申請する。
     *
     * @param entity 申請情報(申請)
     * @param requestor 申請者(組織情報)
     * @param approvalRoute 承認ルート
     * @param authId 認証ID
     * @return 結果
     */
    private ResponseEntity applyWork(ApprovalEntity entity, OrganizationEntity requestor, ApprovalRouteEntity approvalRoute, Long authId) {
        try {
            // 工程を取得する。
            WorkEntity work = this.em.find(WorkEntity.class, entity.getNewData());
            if (Objects.isNull(work)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 対象の工程、または前の版数の工程を含む工程順が申請中の場合は申請不可。
            List<Long> checkWorkIds = new ArrayList();
            checkWorkIds.add(entity.getNewData());
            if (Objects.nonNull(entity.getOldData())) {
                checkWorkIds.add(entity.getOldData());
            }

            long applyCount = this.countApplyWorkflowByWorkId(checkWorkIds);
            if (applyCount > 0) {
                // 対象の工程を使用している工程順が申請中のため申請不可
                return ResponseEntity.failed(ServerErrorTypeEnum.RELATED_WORKFLOW_APPLYING);
            }

            // 楽観的ロックをかける。
            this.em.lock(work, LockModeType.OPTIMISTIC);

            Long oldWorkId = null;
            if (work.getWorkRev() > 1) {
                // 最終承認済の最新版数の工程IDを取得する。
                oldWorkId = this.findLatestRevWorkIdByName(work.getWorkName());
            }

            // 申請情報
            ApprovalEntity approval;

            boolean isAddApproval = Objects.isNull(work.getApprovalId());

            if (isAddApproval) {
                approval = new ApprovalEntity();
                approval.setDataType(entity.getDataType());
                approval.setNewData(entity.getNewData());
                approval.setOldData(oldWorkId);
            } else {
                approval = this.approvalModel.findApproval(work.getApprovalId());
            }

            approval.setRouteId(entity.getRouteId());
            approval.setRequestorId(requestor.getOrganizationId());
            approval.setRequestDatetime(entity.getRequestDatetime());
            approval.setApprovalState(ApprovalStatusEnum.APPLY);
            approval.setComment(entity.getComment());

            // 承認履歴に申請の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.apply"),
                    requestor.getOrganizationName(),
                    entity.getRequestDatetime(),
                    approvalRoute.getRouteName(),
                    0,
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            if (isAddApproval) {
                this.em.persist(approval);
                this.em.flush();

                work.setApprovalId(approval.getApprovalId());
            } else {
                this.em.merge(approval);

                // 古い承認フローを削除する。
                this.approvalModel.removeApprovalFlowByApprovalId(approval.getApprovalId());
            }

            // 新しい承認フローを作成する。
            List<ApprovalFlowEntity> approvalFlows = new LinkedList();
            for (ApprovalOrderEntity approvalOrder : approvalRoute.getApprovalOrders()) {
                ApprovalFlowEntity approvalFlow = new ApprovalFlowEntity();

                approvalFlow.setApprovalId(approval.getApprovalId());
                approvalFlow.setApprovalOrder(approvalOrder.getApprovalOrder());
                approvalFlow.setApprovalFinal(approvalOrder.getApprovalFinal());
                approvalFlow.setApproverId(approvalOrder.getOrganizationId());
                approvalFlow.setApprovalDatetime(null);
                approvalFlow.setApprovalState(ApprovalStatusEnum.UNAPPROVED);

                this.em.persist(approvalFlow);

                approvalFlows.add(approvalFlow);
            }

            approval.setApprovalFlows(approvalFlows);

            this.em.flush();
            this.em.clear();

            work.setApprovalState(ApprovalStatusEnum.APPLY);

            // 工程情報を更新する。
            this.em.merge(work);

            // 最初の承認者に変更申請メールを送信する。
            if (!sendRequestNotification(authId, approval, work, null, null)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程順の変更を申請する。
     *
     * @param entity 申請情報(申請)
     * @param requestor 申請者(組織情報)
     * @param approvalRoute 承認ルート
     * @param authId 認証ID
     * @return 結果
     */
    private ResponseEntity applyWorkflow(ApprovalEntity entity, OrganizationEntity requestor, ApprovalRouteEntity approvalRoute, Long authId) {
        try {
            // 工程順を取得する。
            WorkflowEntity workflow = this.em.find(WorkflowEntity.class, entity.getNewData());
            if (Objects.isNull(workflow)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 申請中の工程が含まれる場合は申請不可。
            long applyCount = this.countApplyWorkByWorkflowId(entity.getNewData());
            if (applyCount > 0) {
                // 対象の工程順で使用している工程が申請中のため申請不可
                return ResponseEntity.failed(ServerErrorTypeEnum.RELATED_WORK_APPLYING);
            }

            // 楽観的ロックをかける。
            this.em.lock(workflow, LockModeType.OPTIMISTIC);

            Long oldWorkId = null;
            if (workflow.getWorkflowRev() > 1) {
                // 最終承認済の最新版数の工程順IDを取得する。
                oldWorkId = this.findLatestRevWorkflowIdByName(workflow.getWorkflowName());
            }

            // 申請情報
            ApprovalEntity approval;

            boolean isAddApproval = Objects.isNull(workflow.getApprovalId());

            if (isAddApproval) {
                approval = new ApprovalEntity();
                approval.setDataType(entity.getDataType());
                approval.setNewData(entity.getNewData());
                approval.setOldData(oldWorkId);
            } else {
                approval = this.approvalModel.findApproval(workflow.getApprovalId());
            }

            approval.setRouteId(entity.getRouteId());
            approval.setRequestorId(requestor.getOrganizationId());
            approval.setRequestDatetime(entity.getRequestDatetime());
            approval.setApprovalState(ApprovalStatusEnum.APPLY);
            approval.setComment(entity.getComment());

            // 承認履歴に申請の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.apply"),
                    requestor.getOrganizationName(),
                    entity.getRequestDatetime(),
                    approvalRoute.getRouteName(),
                    0,
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            if (isAddApproval) {
                this.em.persist(approval);
                this.em.flush();

                workflow.setApprovalId(approval.getApprovalId());
            } else {
                this.em.merge(approval);

                // 古い承認フローを削除する。
                this.approvalModel.removeApprovalFlowByApprovalId(approval.getApprovalId());
            }

            // 新しい承認フローを作成する。
            List<ApprovalFlowEntity> approvalFlows = new LinkedList();
            for (ApprovalOrderEntity approvalOrder : approvalRoute.getApprovalOrders()) {
                ApprovalFlowEntity approvalFlow = new ApprovalFlowEntity();

                approvalFlow.setApprovalId(approval.getApprovalId());
                approvalFlow.setApprovalOrder(approvalOrder.getApprovalOrder());
                approvalFlow.setApprovalFinal(approvalOrder.getApprovalFinal());
                approvalFlow.setApproverId(approvalOrder.getOrganizationId());
                approvalFlow.setApprovalDatetime(null);
                approvalFlow.setApprovalState(ApprovalStatusEnum.UNAPPROVED);

                this.em.persist(approvalFlow);

                approvalFlows.add(approvalFlow);
            }

            approval.setApprovalFlows(approvalFlows);

            this.em.flush();
            this.em.clear();

            workflow.setApprovalState(ApprovalStatusEnum.APPLY);

            // 工程情報を更新する。
            this.em.merge(workflow);

            // 最初の承認者に変更申請メールを送信する。
            if (!sendRequestNotification(authId, approval, null, workflow, null)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程・工程順の変更申請を取り消す。
     *
     * @param entity 申請情報(申請取消)
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity cancelApply(ApprovalEntity entity, Long authId) {
        logger.info("cancelApply: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getApprovalId())
                    || Objects.isNull(authId)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 申請情報を取得する。
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            if (Objects.isNull(approval)) {
                // 申請情報がない。
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            if (!Objects.equals(approval.getRequestorId(), authId)) {
                // 申請者ではない。
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 組織を取得する。
            OrganizationEntity requestor = this.findOrganization(authId);
            if (Objects.isNull(requestor)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認ルートを取得する。
            ApprovalRouteEntity approvalRoute = this.findApprovalRoute(approval.getRouteId(), null);
            if (Objects.isNull(approvalRoute)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            approval.setApprovalState(ApprovalStatusEnum.CANCEL_APPLY);

            // 承認履歴に申請取消の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.cancel"),
                    requestor.getOrganizationName(),
                    new Date(),
                    approvalRoute.getRouteName(),
                    0,
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            this.em.merge(approval);

            // 工程・工程順の承認状態を更新する。
            Long id = this.updateApprovalStatus(approval, ApprovalStatusEnum.CANCEL_APPLY);
            if (Objects.isNull(id)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認した承認者、および承認待ちの承認者に変更申請取消メールを送信する。
            Boolean ret = false;
            if (approval.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                WorkEntity work = this.findWorkByApprovalId(entity.getApprovalId());
                ret = sendRequestCancelNotification(authId, approval, entity, work, null, null);
            } else if (approval.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                WorkflowEntity workflow = this.findWorkflowByApprovalId(entity.getApprovalId());
                ret = sendRequestCancelNotification(authId, approval, entity, null, workflow, null);
            }
            if (!ret) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程・工程順の変更を承認する。
     *
     * @param entity 承認フロー情報(承認)
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity approve(ApprovalFlowEntity entity, Long authId) {
        logger.info("approve: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getApprovalId())
                    || Objects.isNull(entity.getApproverId())
                    || Objects.isNull(entity.getApprovalDatetime())
                    || Objects.isNull(authId)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 組織を取得する。
            OrganizationEntity approver = this.findOrganization(authId);
            if (Objects.isNull(approver)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認権限をチェックする。
            boolean auth = RoleUtils.checkApproveRole(this.em, approver);
            if (!auth) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 申請情報を取得する。
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            if (Objects.isNull(approval)) {
                // 申請情報がない。
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認者の承認フロー情報を取得する。
            Optional<ApprovalFlowEntity> optFlow = approval.getApprovalFlows().stream()
                    .filter(p -> Objects.equals(p.getApproverId(), authId))
                    .findFirst();
            if (!optFlow.isPresent()) {
                // 承認フローにない。
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            ApprovalFlowEntity approvalFlow = optFlow.get();

            // 承認可能な状態かチェックする。
            boolean isPossible = approval.getApprovalFlows().stream()
                    .allMatch(p -> (p.getApprovalOrder() < approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.APPROVE))
                    || (p.getApprovalOrder() >= approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.UNAPPROVED)));
            if (!isPossible) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 承認ルートを取得する。
            ApprovalRouteEntity approvalRoute = this.findApprovalRoute(approval.getRouteId(), null);
            if (Objects.isNull(approvalRoute)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認履歴に申請取消の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.approve"),
                    approver.getOrganizationName(),
                    entity.getApprovalDatetime(),
                    approvalRoute.getRouteName(),
                    approvalFlow.getApprovalOrder(),
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            this.em.merge(approval);

            // 承認フローを更新する。
            approvalFlow.setApprovalDatetime(entity.getApprovalDatetime());
            approvalFlow.setApprovalState(ApprovalStatusEnum.APPROVE);
            approvalFlow.setComment(entity.getComment());

            this.em.merge(approvalFlow);

            // 次の承認者、または最終承認者に変更申請メールを送信する。
            Boolean ret = false;
            if (approval.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                WorkEntity work = this.findWorkByApprovalId(entity.getApprovalId());
                ret = sendRequestNotification(authId, approval, work, null, null);
            } else if (approval.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                WorkflowEntity workflow = this.findWorkflowByApprovalId(entity.getApprovalId());
                ret = sendRequestNotification(authId, approval, null, workflow, null);
            }
            if (!ret) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }
            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程・工程順の変更を最終承認する。
     *
     * @param entity 承認フロー情報
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity finalApprove(ApprovalFlowEntity entity, Long authId) {
        logger.info("finalApprove: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getApprovalId())
                    || Objects.isNull(entity.getApproverId())
                    || Objects.isNull(entity.getApprovalDatetime())
                    || Objects.isNull(authId)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 組織を取得する。
            OrganizationEntity approver = this.findOrganization(authId);
            if (Objects.isNull(approver)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認権限をチェックする。
            boolean auth = RoleUtils.checkApproveRole(this.em, approver);
            if (!auth) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 申請情報を取得する。
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            if (Objects.isNull(approval)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認者の承認フロー情報を取得する。
            Optional<ApprovalFlowEntity> optFlow = approval.getApprovalFlows().stream()
                    .filter(p -> Objects.equals(p.getApproverId(), authId))
                    .findFirst();
            if (!optFlow.isPresent()) {
                // 承認フローにない。
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            ApprovalFlowEntity approvalFlow = optFlow.get();

            // 承認可能な状態かチェックする。
            boolean isPossible = approval.getApprovalFlows().stream()
                    .allMatch(p -> (p.getApprovalOrder() < approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.APPROVE))
                    || (p.getApprovalOrder() >= approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.UNAPPROVED)));
            if (!isPossible) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 承認ルートを取得する。
            ApprovalRouteEntity approvalRoute = this.findApprovalRoute(approval.getRouteId(), null);
            if (Objects.isNull(approvalRoute)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            approval.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);

            // 承認履歴に申請取消の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.finalApprove"),
                    approver.getOrganizationName(),
                    approval.getRequestDatetime(),
                    approvalRoute.getRouteName(),
                    approvalFlow.getApprovalOrder(),
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            this.em.merge(approval);

            // 承認フローを更新する。
            approvalFlow.setApprovalFinal(true);
            approvalFlow.setApprovalDatetime(entity.getApprovalDatetime());
            approvalFlow.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
            approvalFlow.setComment(entity.getComment());

            this.em.merge(approvalFlow);

            // 工程・工程順の承認状態を更新する。
            Long id = this.updateApprovalStatus(approval, ApprovalStatusEnum.FINAL_APPROVE);
            if (Objects.isNull(id)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 工程の場合のみ、工程順に工程の変更を反映する。
            if (Objects.equals(approval.getDataType(), ApprovalDataTypeEnum.WORK)) {
                this.reflectWork(approval);
            }

            // 申請者、および全ての承認者に最終承認メールを送信する。
            Boolean ret = false;
            if (approval.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                WorkEntity work = this.findWorkByApprovalId(entity.getApprovalId());
                ret = sendApprovalNotification(authId, approval, work, null, null);
            } else if (approval.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                WorkflowEntity workflow = this.findWorkflowByApprovalId(entity.getApprovalId());
                ret = sendApprovalNotification(authId, approval, null, workflow, null);
            }

            if (!ret) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程・工程順の変更を却下する。
     *
     * @param entity 承認フロー情報
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity reject(ApprovalFlowEntity entity, Long authId) {
        logger.info("reject: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getApprovalId())
                    || Objects.isNull(entity.getApproverId())
                    || Objects.isNull(entity.getApprovalDatetime())
                    || Objects.isNull(authId)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 組織を取得する。
            OrganizationEntity approver = this.findOrganization(authId);
            if (Objects.isNull(approver)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認権限をチェックする。
            boolean auth = RoleUtils.checkApproveRole(this.em, approver);
            if (!auth) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 申請情報を取得する。
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            if (Objects.isNull(approval)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認者の承認フロー情報を取得する。
            Optional<ApprovalFlowEntity> optFlow = approval.getApprovalFlows().stream()
                    .filter(p -> Objects.equals(p.getApproverId(), authId))
                    .findFirst();
            if (!optFlow.isPresent()) {
                // 承認フローにない。
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            ApprovalFlowEntity approvalFlow = optFlow.get();

            // 承認可能な状態かチェックする。
            boolean isPossible = approval.getApprovalFlows().stream()
                    .allMatch(p -> (p.getApprovalOrder() < approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.APPROVE))
                    || (p.getApprovalOrder() >= approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.UNAPPROVED)));
            if (!isPossible) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 承認ルートを取得する。
            ApprovalRouteEntity approvalRoute = this.findApprovalRoute(approval.getRouteId(), null);
            if (Objects.isNull(approvalRoute)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            approval.setApprovalState(ApprovalStatusEnum.REJECT);

            // 承認履歴に申請取消の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.reject"),
                    approver.getOrganizationName(),
                    approval.getRequestDatetime(),
                    approvalRoute.getRouteName(),
                    approvalFlow.getApprovalOrder(),
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            this.em.merge(approval);

            // 承認フローを更新する。
            approvalFlow.setApprovalDatetime(entity.getApprovalDatetime());
            approvalFlow.setApprovalState(ApprovalStatusEnum.REJECT);
            approvalFlow.setComment(entity.getComment());

            this.em.merge(approvalFlow);

            // 工程・工程順の承認状態を更新する。
            Long id = this.updateApprovalStatus(approval, ApprovalStatusEnum.REJECT);
            if (Objects.isNull(id)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 申請者、および承認した承認者に却下メールを送信する。
            Boolean ret = false;
            if (approval.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                WorkEntity work = this.findWorkByApprovalId(entity.getApprovalId());
                ret = sendRemandNotification(authId, approval, work, null, null);
            } else if (approval.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                WorkflowEntity workflow = this.findWorkflowByApprovalId(entity.getApprovalId());
                ret = sendRemandNotification(authId, approval, null, workflow, null);
            }

            if (!ret) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程・工程順の変更の承認を取り消す。
     *
     * @param entity 承認フロー情報
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity cancelApprove(ApprovalFlowEntity entity, Long authId) {
        logger.info("cancelApprove: {}, authId={}", entity, authId);
        try {
            if (Objects.isNull(entity.getApprovalId())
                    || Objects.isNull(entity.getApproverId())
                    || Objects.isNull(entity.getApprovalDatetime())
                    || Objects.isNull(authId)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 組織を取得する。
            OrganizationEntity approver = this.findOrganization(authId);
            if (Objects.isNull(approver)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認権限をチェックする。
            boolean auth = RoleUtils.checkApproveRole(this.em, approver);
            if (!auth) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 申請情報を取得する。
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            if (Objects.isNull(approval)) {
                // 申請情報がない。
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認者の承認フロー情報を取得する。
            Optional<ApprovalFlowEntity> optFlow = approval.getApprovalFlows().stream()
                    .filter(p -> Objects.equals(p.getApproverId(), authId))
                    .findFirst();
            if (!optFlow.isPresent()) {
                // 承認フローにない。
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            ApprovalFlowEntity approvalFlow = optFlow.get();

            // 承認取消可能な状態かチェックする。
            boolean isPossible = approval.getApprovalFlows().stream()
                    .allMatch(p -> (p.getApprovalOrder() <= approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.APPROVE))
                    || (p.getApprovalOrder() > approvalFlow.getApprovalOrder() && Objects.equals(p.getApprovalState(), ApprovalStatusEnum.UNAPPROVED)));
            if (!isPossible) {
                return ResponseEntity.failed(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION);
            }

            // 承認ルートを取得する。
            ApprovalRouteEntity approvalRoute = this.findApprovalRoute(approval.getRouteId(), null);
            if (Objects.isNull(approvalRoute)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT);
            }

            // 承認履歴に申請取消の承認履歴を追加する。
            List<ApprovalHistoryInfo> historyInfos;
            if (StringUtils.isEmpty(approval.getApprovalHistory())) {
                historyInfos = new LinkedList();
            } else {
                historyInfos = JsonUtils.jsonToObjects(approval.getApprovalHistory(), ApprovalHistoryInfo[].class);
            }

            ApprovalHistoryInfo historyInfo = new ApprovalHistoryInfo(
                    LocaleUtils.getString("key.approval.cancelApprove"),
                    approver.getOrganizationName(),
                    entity.getApprovalDatetime(),
                    approvalRoute.getRouteName(),
                    approvalFlow.getApprovalOrder(),
                    entity.getComment());

            historyInfos.add(historyInfo);

            String history = JsonUtils.objectsToJson(historyInfos);

            approval.setApprovalHistory(history);

            // 申請情報を追加・更新する。
            this.em.merge(approval);

            // 承認フローを更新する。
            approvalFlow.setApprovalDatetime(entity.getApprovalDatetime());
            approvalFlow.setApprovalState(ApprovalStatusEnum.UNAPPROVED);
            approvalFlow.setComment(entity.getComment());

            this.em.merge(approvalFlow);

            // 承認待ちの承認者に承認取消メールを送信する。
            Boolean ret = false;
            if (approval.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                WorkEntity work = this.findWorkByApprovalId(entity.getApprovalId());
                ret = sendApprovalCancelNotification(authId, approval, work, null, null);
            } else if (approval.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                WorkflowEntity workflow = this.findWorkflowByApprovalId(entity.getApprovalId());
                ret = sendApprovalCancelNotification(authId, approval, null, workflow, null);
            }

            if (!ret) {
                return ResponseEntity.failed(ServerErrorTypeEnum.MAIL_AUTHENTICATION_FAILED);
            }

            return ResponseEntity.success();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 組織IDを指定して、組織情報を取得する。
     *
     * @param organizationId 組織ID
     * @return 組織情報
     */
    public OrganizationEntity findOrganization(long organizationId) {
        return this.em.find(OrganizationEntity.class, organizationId);
    }

    /**
     * 組織識別名を指定して、組織情報を取得する。
     *
     * @param orgIdent 組織識別名
     * @return 組織情報
     */
    public OrganizationEntity findOrganizationByIdentify(String orgIdent) {
        try {
            TypedQuery<OrganizationEntity> queryExist = em.createNamedQuery("OrganizationEntity.findByIdentNotRemove", OrganizationEntity.class);
            queryExist.setParameter("organizationIdentify", orgIdent);

            OrganizationEntity organization = queryExist.getSingleResult();
            em.detach(organization);
            organization.setPassword(null);

            return organization;
        } catch (Exception ex) {
            logger.fatal(ex);
            return null;
        }
    }

    /**
     * 組織情報一覧取得(組織ID)
     *
     * @param orgIds 組織ID一覧
     * @return 組織情報一覧
     */
    public List<OrganizationEntity> findOrganizationByIds(List<Long> orgIds) {
        if (orgIds.isEmpty()) {
            return new ArrayList();
        }

        try {
            TypedQuery<OrganizationEntity> query = em.createNamedQuery("OrganizationEntity.findByIdsNotRemove", OrganizationEntity.class);
            query.setParameter("organizationIds", orgIds);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex);
            return new ArrayList();
        }
    }

   /**
     * 設備情報一覧取得(組織ID)
     *
     * @param equipIds 設備ID一覧
     * @return 設備情報一覧
     */
    public List<EquipmentEntity> findEquipmentByIds(List<Long> equipIds) {
        if (equipIds.isEmpty()) {
            return new ArrayList();
        }

        try {
            TypedQuery<EquipmentEntity> query = em.createNamedQuery("EquipmentEntity.findByIdsNotRemove", EquipmentEntity.class);
            query.setParameter("equipmentIds", equipIds);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex);
            return new ArrayList();
        }
    }

    /**
     * 指定した組織にリソース編集権限があるかどうかを取得する。
     *
     * @param organization 組織情報
     * @return リソース編集権限(true:あり, false:なし)
     */
    private boolean checkResourceEditRole(OrganizationEntity organization) {
        if (Objects.equals(organization.getAuthorityType(), AuthorityEnum.SYSTEM_ADMIN)) {
            return true;
        }

        List<RoleAuthorityEntity> roles = this.findRoleByOrganizationId(organization.getOrganizationId());

        return roles.stream().anyMatch(p -> p.getResourceEdit());
    }

    /**
     * 指定した組織に承認権限があるかどうかを取得する。
     *
     * @param organization 組織情報
     * @return 承認権限(true:あり, false:なし)
     */
    private boolean checkApproveRole(OrganizationEntity organization) {
        if (Objects.equals(organization.getAuthorityType(), AuthorityEnum.SYSTEM_ADMIN)) {
            return true;
        }

        List<RoleAuthorityEntity> roles = this.findRoleByOrganizationId(organization.getOrganizationId());

        return roles.stream().anyMatch(p -> p.getApprove());
    }

    /**
     * 組織IDを指定して、役割権限情報一覧を取得する。
     *
     * @param organizationId 組織ID
     * @return 役割権限情報一覧
     */
    private List<RoleAuthorityEntity> findRoleByOrganizationId(long organizationId) {
        TypedQuery<RoleAuthorityEntity> query = this.em.createNamedQuery("RoleAuthorityEntity.findByOrganizationId", RoleAuthorityEntity.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    /**
     * 工程IDを指定して、工程情報を取得する。
     *
     * @param workId 工程ID
     * @return 工程情報
     */
    public WorkEntity findWork(long workId) {
        return this.workRest.find(workId, false, false, null);
    }

    /**
     * 工程順IDを指定して、工程順情報を取得する。
     *
     * @param workflowId 工程順ID
     * @return 工程順情報
     */
    public WorkflowEntity findWorkflow(long workflowId) {
        return this.workflowRest.find(workflowId, false, null);
    }

    /**
     * 工程名を指定して、最終承認済の最新版数の工程IDを取得する。※.削除済は対象外
     *
     * @param workName 工程名
     * @return 工程ID
     */
    private Long findLatestRevWorkIdByName(String workName) {
        TypedQuery<Long> query = this.em.createNamedQuery("WorkEntity.findWorkIdLatestRevByName", Long.class);
        query.setParameter("workName", workName);
        return query.getSingleResult();
    }

    /**
     * 工程順名を指定して、最終承認済の最新版数の工程順IDを取得する。※.削除済は対象外
     *
     * @param workflowName 工程順名
     * @return 工程順ID
     */
    private Long findLatestRevWorkflowIdByName(String workflowName) {
        TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.findWorkIdLatestRevByName", Long.class);
        query.setParameter("workflowName", workflowName);
        return query.getSingleResult();
    }

    /**
     * 申請IDを指定して、工程情報を取得する。
     *
     * @param approvalId 申請ID
     * @return 工程情報
     */
    public WorkEntity findWorkByApprovalId(long approvalId) {
        TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findByApprovalId", WorkEntity.class);
        query.setParameter("approvalId", approvalId);

        WorkEntity work = query.getSingleResult();

        // 申請情報
        ApprovalEntity approval = this.approvalModel.findApproval(approvalId);
        work.setApproval(approval);

        return work;
    }

    /**
     * 申請IDを指定して、工程順情報を取得する。
     *
     * @param approvalId 申請ID
     * @return 工程順情報
     */
    private WorkflowEntity findWorkflowByApprovalId(long approvalId) {
        TypedQuery<WorkflowEntity> query = this.em.createNamedQuery("WorkflowEntity.findByApprovalId", WorkflowEntity.class);
        query.setParameter("approvalId", approvalId);

        WorkflowEntity workflow = query.getSingleResult();

        // 申請情報
        ApprovalEntity approval = this.approvalModel.findApproval(approvalId);
        workflow.setApproval(approval);

        return workflow;
    }

    /**
     * 申請情報を指定して、工程・工程順の承認状態を更新する。
     *
     * @param approval 申請情報
     * @param approvalStatus 承認状態
     */
    private Long updateApprovalStatus(ApprovalEntity approval, ApprovalStatusEnum approvalStatus) {
        switch (approval.getDataType()) {
            case WORK:
                // 工程情報を更新する。
                return this.updateWorkApprovalStatus(approval.getApprovalId(), approvalStatus);
            case WORKFLOW:
                // 工程順情報を更新する。
                return this.updateWorkflowApprovalStatus(approval.getApprovalId(), approvalStatus);
            case KANBAN:
            default:
                return null;
        }
    }

    /**
     * 申請IDを指定して、工程の承認状態を更新する。
     *
     * @param approvalId 申請ID
     * @param approvalStatus 承認状態
     */
    private long updateWorkApprovalStatus(long approvalId, ApprovalStatusEnum approvalStatus) {
        // 工程を取得する。
        WorkEntity work = this.findWorkByApprovalId(approvalId);

        work.setApprovalId(approvalId);
        work.setApprovalState(approvalStatus);

        // 工程情報を更新する。
        this.em.merge(work);

        return work.getWorkId();
    }

    /**
     * 申請IDを指定して、工程順の申請状態を更新する。
     *
     * @param approvalId 申請ID
     * @param approvalStatus 承認状態
     */
    private long updateWorkflowApprovalStatus(long approvalId, ApprovalStatusEnum approvalStatus) {
        // 工程を取得する。
        WorkflowEntity workflow = this.findWorkflowByApprovalId(approvalId);

        workflow.setApprovalId(approvalId);
        workflow.setApprovalState(approvalStatus);

        // 工程情報を更新する。
        this.em.merge(workflow);

        return workflow.getWorkflowId();
    }

    /**
     * 前の版数の工程を使用している工程順に、最終承認された工程を反映する。
     *
     * @param approval 申請情報
     * @throws Exception
     */
    private void reflectWork(ApprovalEntity approval) throws Exception {
        if (Objects.isNull(approval.getOldData())) {
            return;
        }

        long oldWorkId = approval.getOldData();
        long newWorkId = approval.getNewData();

        // 最新版数の工程順のうち、前の版数の工程を使用している工程順を取得する。
        List<WorkflowEntity> workflows = this.findByWorkId(oldWorkId);
        if (Objects.isNull(workflows) || workflows.isEmpty()) {
            return;
        }

        for (WorkflowEntity workflow : workflows) {
            WorkflowEntity targetWorkflow;

            // 承認済の場合は改訂する。
            if (Objects.equals(workflow.getApprovalState(), ApprovalStatusEnum.FINAL_APPROVE)) {
                Response reviseRes = this.workflowRest.revise(workflow.getWorkflowId(), null);

                ResponseWorkflowEntity wfRes = (ResponseWorkflowEntity) reviseRes.getEntity();
                if (!wfRes.isSuccess()) {
                    // 改訂でエラー
                    throw new Exception();
                }

                targetWorkflow = wfRes.getValue();
            } else {
                targetWorkflow = workflow;
            }

            // 工程順ダイアグラムの工程IDを差し替える。
            String diaglam = this.replaceDiagramWork(targetWorkflow.getWorkflowDiaglam(), oldWorkId, newWorkId);

            // 工程順を更新する。
            targetWorkflow.setWorkflowDiaglam(diaglam);
            this.em.merge(targetWorkflow);

            // 工程順・工程関連付け情報の工程IDを更新する。
            this.replaceConWorkflowWork(targetWorkflow.getWorkflowId(), oldWorkId, newWorkId);
            // 工程・設備関連付け情報の工程IDを更新する。
            this.replaceConWorkEquipment(targetWorkflow.getWorkflowId(), oldWorkId, newWorkId);
            // 工程・組織関連付け情報の工程IDを更新する。
            this.replaceConWorkOrganization(targetWorkflow.getWorkflowId(), oldWorkId, newWorkId);

            this.em.flush();
            this.em.clear();
        }
    }

    /**
     * 工程IDを指定して、最新版数の工程順のうち、対象工程を使用している工程順一覧を取得する。
     *
     * @param workId 工程ID
     * @return 工程順一覧
     */
    private List<WorkflowEntity> findByWorkId(long workId) {
        TypedQuery<WorkflowEntity> query = this.em.createNamedQuery("WorkflowEntity.findByWorkId", WorkflowEntity.class);
        query.setParameter(1, workId);
        return query.getResultList();
    }

    /**
     * 工程順ダイアグラムの工程IDを、新しい工程IDに更新する。
     *
     * @param workflowDiaglam 工程順ダイアグラム
     * @param oldWorkId 工程ID
     * @param newWorkId 新しい工程ID
     * @return 工程順ダイアグラム
     * @throws JAXBException
     */
    private String replaceDiagramWork(String workflowDiaglam, long oldWorkId, long newWorkId) throws JAXBException {
        BpmnModel bpmnModel = BpmnModeler.getModeler();

        BpmnDocument bpmn = BpmnDocument.unmarshal(workflowDiaglam);
        bpmnModel.createModel(bpmn);

        BpmnProcess bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        String oldId = String.valueOf(oldWorkId);
        String newId = String.valueOf(newWorkId);

        for (BpmnTask task : bpmnProcess.getTaskCollection()) {
            if (StringUtils.equals(task.getId(), oldId)) {
                task.setId(newId);
            }
        }

        for (BpmnSequenceFlow flow : bpmnProcess.getSequenceFlowCollection()) {
            if (StringUtils.equals(flow.getSourceRef(), oldId)) {
                flow.setSourceRef(newId);
            }

            if (StringUtils.equals(flow.getTargetRef(), oldId)) {
                flow.setTargetRef(newId);
            }
        }

        return bpmn.marshal2();
    }

    /**
     * 工程順ID・工程IDと新しい工程IDを指定して、工程順・工程関連付け情報の工程IDを更新する。
     *
     * @param workflowId 工程順ID
     * @param oldWorkId 工程ID
     * @param newWorkId 新しい工程ID
     */
    private void replaceConWorkflowWork(long workflowId, long oldWorkId, long newWorkId) {
        Query query = this.em.createNamedQuery("ConWorkflowWorkEntity.updateWorkId");
        query.setParameter("workflowId", workflowId);
        query.setParameter("oldWorkId", oldWorkId);
        query.setParameter("newWorkId", newWorkId);
        query.executeUpdate();
    }

    /**
     * 工程順ID・工程IDと新しい工程IDを指定して、工程・設備関連付け情報の工程IDを更新する。
     *
     * @param workflowId 工程順ID
     * @param oldWorkId 工程ID
     * @param newWorkId 新しい工程ID
     */
    private void replaceConWorkEquipment(long workflowId, long oldWorkId, long newWorkId) {
        Query query = this.em.createNamedQuery("ConWorkEquipmentEntity.updateWorkId");
        query.setParameter("workflowId", workflowId);
        query.setParameter("oldWorkId", oldWorkId);
        query.setParameter("newWorkId", newWorkId);
        query.executeUpdate();
    }

    /**
     * 工程順ID・工程IDと新しい工程IDを指定して、工程・組織関連付け情報の工程IDを更新する。
     *
     * @param workflowId 工程順ID
     * @param oldWorkId 工程ID
     * @param newWorkId 新しい工程ID
     */
    private void replaceConWorkOrganization(long workflowId, long oldWorkId, long newWorkId) {
        Query query = this.em.createNamedQuery("ConWorkOrganizationEntity.updateWorkId");
        query.setParameter("workflowId", workflowId);
        query.setParameter("oldWorkId", oldWorkId);
        query.setParameter("newWorkId", newWorkId);
        query.executeUpdate();
    }

    /**
     * 申請通知メール送信
     *
     * @param authId 組織ID(操作者自身)
     * @param approval 申請情報
     * @param work 工程情報
     * @param workflow 工程順情報
     * @param kanban カンバン情報
     * @return 通知結果(true：通知成功/false：通知失敗)
     */
    private Boolean sendRequestNotification(Long authId, ApprovalEntity approval, WorkEntity work, WorkflowEntity workflow, KanbanEntity kanban) {
        return sendHtmlMail(NotificationCategoryEnum.REQUEST, authId, approval, null, work, workflow, kanban);
    }

    /**
     * 申請取消通知メール送信
     *
     * @param authId 組織ID(操作者自身)
     * @param approval 申請情報
     * @param approvalScreen 申請情報(画面入力値)
     * @param work 工程情報
     * @param workflow 工程順情報
     * @param kanban カンバン情報
     * @return 通知結果(true：通知成功/false：通知失敗)
     */
    private Boolean sendRequestCancelNotification(Long authId, ApprovalEntity approval, ApprovalEntity approvalScreen, WorkEntity work, WorkflowEntity workflow, KanbanEntity kanban) {
        return sendHtmlMail(NotificationCategoryEnum.REQUEST_CANCEL, authId, approval, approvalScreen, work, workflow, kanban);
    }

    /**
     * 却下通知メール送信
     *
     * @param authId 組織ID(操作者自身)
     * @param approval 申請情報
     * @param work 工程情報
     * @param workflow 工程順情報
     * @param kanban カンバン情報
     * @return 通知結果(true：通知成功/false：通知失敗)
     */
    private Boolean sendRemandNotification(Long authId, ApprovalEntity approval, WorkEntity work, WorkflowEntity workflow, KanbanEntity kanban) {
        return sendHtmlMail(NotificationCategoryEnum.REMAND, authId, approval, null, work, workflow, kanban);
    }

    /**
     * 承認取消通知メール送信
     *
     * @param authId 組織ID(操作者自身)
     * @param approval 申請情報
     * @param work 工程情報
     * @param workflow 工程順情報
     * @param kanban カンバン情報
     * @return 通知結果(true：通知成功/false：通知失敗)
     */
    private Boolean sendApprovalCancelNotification(Long authId, ApprovalEntity approval, WorkEntity work, WorkflowEntity workflow, KanbanEntity kanban) {
        return sendHtmlMail(NotificationCategoryEnum.APPROVAL_CANCEL, authId, approval, null, work, workflow, kanban);
    }

    /**
     * 承認通知メール送信
     *
     * @param authId 組織ID(操作者自身)
     * @param approval 申請情報
     * @param work 工程情報
     * @param workflow 工程順情報
     * @return 通知結果(true：通知成功/false：通知失敗)
     */
    private Boolean sendApprovalNotification(Long authId, ApprovalEntity approval, WorkEntity work, WorkflowEntity workflow, KanbanEntity kanban) {

        //操作者自身の組織情報取得
        OrganizationEntity opeOrgInfo = findOrganization(authId);
        //承認者が組織情報(操作者自身).組織IDと一致する承認フロー情報を取得
        Optional<ApprovalFlowEntity> approvalFlowOpt = approval.getApprovalFlows().stream().filter((info) -> (info.getApproverId().equals(opeOrgInfo.getOrganizationId()))).findFirst();
        if (!approvalFlowOpt.isPresent()) {
            return false;
        }
        ApprovalFlowEntity approvalFlow = approvalFlowOpt.get();

        if (approvalFlow.getApprovalFinal()) {
            return sendHtmlMail(NotificationCategoryEnum.APPROVAL, authId, approval, null, work, workflow, kanban);
        } else {
            return sendHtmlMail(NotificationCategoryEnum.REQUEST, authId, approval, null, work, workflow, kanban);
        }
    }

    /**
     * HTML形式のメールを送信する。
     *
     * @param type 通知区分
     * @param authId 組織ID(操作者自身)
     * @param approval 申請情報
     * @param approvalScreen 申請情報(画面入力値)
     * @param work 工程情報
     * @param workflow 工程順情報
     * @param kanban カンバン情報
     * @return 通知結果(true：通知成功/false：通知失敗)
     */
    private Boolean sendHtmlMail(NotificationCategoryEnum type, Long authId, ApprovalEntity approval, ApprovalEntity approvalScreen, WorkEntity work, WorkflowEntity workflow, KanbanEntity kanban) {
        Boolean result = true;

        try {
            if (Objects.isNull(authId)) {
                logger.fatal("The organization id was not found.");
                return false;
            }

            //申請者の組織情報取得
            OrganizationEntity reqOrgInfo = findOrganization(approval.getRequestorId());
            //操作者自身の組織情報取得
            OrganizationEntity opeOrgInfo = findOrganization(authId);

            if (Objects.isNull(reqOrgInfo) || Objects.isNull(opeOrgInfo)) {
                logger.fatal("The organization infomation was not found.");
                return false;
            }

            // 自動テスト実行時は以降の処理を行わない
            if (Objects.nonNull(reqOrgInfo.getMailAddress())
                    && reqOrgInfo.getMailAddress().contains("dummyTest") && reqOrgInfo.getMailAddress().contains("@adtek-fuji.co.jp")) {
                logger.info("Do not send emails for automated tests.");
                return true;
            }
            //メール設定情報の取得
            MailProperty mailPropertyInfo = getMailProperty();
            MailUtils mail = new MailUtils(mailPropertyInfo);

            //メール件名とメール本文(可変部)を作成
            String changedDataType = "";
            String changedDataName = "";
            int changedRevision = 0;

            //変更対象データの取得
            if (Objects.nonNull(work)) {
                changedDataType = LocaleUtils.getString("key.Process");
                changedDataName = work.getWorkName();
                changedRevision = work.getWorkRev();
            } else if (Objects.nonNull(workflow)) {
                changedDataType = LocaleUtils.getString("key.OrderProcesses");
                changedDataName = workflow.getWorkflowName();
                changedRevision = workflow.getWorkflowRev();
            } else if (Objects.nonNull(kanban)) {
                changedDataType = LocaleUtils.getString("key.Kanban");
                changedDataName = kanban.getKanbanName();
                changedRevision = kanban.getWorkflowRev();
            }

            StringBuilder subject = new StringBuilder();
            String guidance = "";
            String approvalKey = "";
            String approvalVal = "";
            String comment = "";

            switch (type) {
                case REQUEST:
                    subject.append(LocaleUtils.getString("approval.request"));
                    if (approval.getDataType().equals(ApprovalDataTypeEnum.WORK)) {
                        guidance = LocaleUtils.getString("approval.work.requestMsg");
                    } else if (approval.getDataType().equals(ApprovalDataTypeEnum.WORKFLOW)) {
                        guidance = LocaleUtils.getString("approval.workflow.requestMsg");
                    }
                    approvalKey = "";
                    approvalVal = "";
                    comment = Objects.isNull(approval.getComment()) ? "" : approval.getComment();
                    break;
                case REQUEST_CANCEL:
                    subject.append(LocaleUtils.getString("approval.requestCancel"));
                    guidance = LocaleUtils.getString("approval.requestCancelMsg");
                    approvalKey = "";
                    approvalVal = "";
                    comment = Objects.isNull(approvalScreen.getComment()) ? "" : approvalScreen.getComment();
                    break;
                case REMAND: {
                    subject.append(LocaleUtils.getString("approval.remand"));
                    guidance = LocaleUtils.getString("approval.remandMsg");
                    approvalKey = LocaleUtils.getString("approval.authorizer");
                    approvalVal = opeOrgInfo.getOrganizationName();
                    Optional<ApprovalFlowEntity> approvalFlowOpt = approval.getApprovalFlows().stream().filter((info) -> (info.getApproverId().equals(opeOrgInfo.getOrganizationId()))).findFirst();
                    if (approvalFlowOpt.isPresent()) {
                        comment = Objects.isNull(approvalFlowOpt.get().getComment()) ? "" : approvalFlowOpt.get().getComment();
                    }
                    break;
                }
                case APPROVAL_CANCEL: {
                    subject.append(LocaleUtils.getString("approval.approvalCancel"));
                    guidance = LocaleUtils.getString("approval.approvalCancelMsg");
                    approvalKey = LocaleUtils.getString("approval.authorizer");
                    approvalVal = opeOrgInfo.getOrganizationName();
                    Optional<ApprovalFlowEntity> approvalFlowOpt = approval.getApprovalFlows().stream().filter((info) -> (info.getApproverId().equals(opeOrgInfo.getOrganizationId()))).findFirst();
                    if (approvalFlowOpt.isPresent()) {
                        comment = Objects.isNull(approvalFlowOpt.get().getComment()) ? "" : approvalFlowOpt.get().getComment();
                    }
                    break;
                }
                case APPROVAL: {
                    subject.append(LocaleUtils.getString("approval.approval"));
                    guidance = LocaleUtils.getString("approval.approvalMsg");
                    approvalKey = LocaleUtils.getString("approval.finalAuthorizer");
                    approvalVal = opeOrgInfo.getOrganizationName();
                    Optional<ApprovalFlowEntity> approvalFlowOpt = approval.getApprovalFlows().stream().filter((info) -> (info.getApproverId().equals(opeOrgInfo.getOrganizationId()))).findFirst();
                    if (approvalFlowOpt.isPresent()) {
                        comment = Objects.isNull(approvalFlowOpt.get().getComment()) ? "" : approvalFlowOpt.get().getComment();
                    }
                    break;
                }
                default:
                    break;
            }
            subject.append(changedDataName).append(" : ").append(changedRevision);

            String fromAddress = mailPropertyInfo.getMailFrom();

            //申請者への通知メール送信
            String toRequestorAddress = reqOrgInfo.getMailAddress();
            if (Objects.nonNull(toRequestorAddress) && !StringUtils.isEmpty(toRequestorAddress)
                    && (type.equals(NotificationCategoryEnum.REMAND) || type.equals(NotificationCategoryEnum.APPROVAL))) {
                SimpleHtmlBuilder builder = new SimpleHtmlBuilder();
                builder.line(DPCTYPE)
                        .html().head().meta(StandardCharsets.UTF_8.toString())._head();
                builder.body().table(0, "");
                builder.tr().td().line(reqOrgInfo.getOrganizationName() + LocaleUtils.getString("key.HonorificTitle"))._tr();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                builder.tr().td().line(guidance)._tr();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                builder.table(1, TABLE_STYLE)
                        .tr()
                        .td(LocaleUtils.getString("approval.approvalId"), ITEM_STYLE)
                        .td(approval.getApprovalId().toString(), VALUE_STYLE)
                        ._tr()
                        .tr()
                        .td(LocaleUtils.getString("approval.applicant"), ITEM_STYLE)
                        .td(reqOrgInfo.getOrganizationName(), VALUE_STYLE)
                        ._tr()
                        .tr()
                        .td(changedDataType, ITEM_STYLE)
                        .td(changedDataName, VALUE_STYLE)
                        ._tr()
                        .tr()
                        .td(LocaleUtils.getString("approval.revision"), ITEM_STYLE)
                        .td(String.valueOf(changedRevision), VALUE_STYLE)
                        ._tr();

                if (!approvalKey.isEmpty()) {
                    builder.tr()
                            .td(approvalKey, ITEM_STYLE)
                            .td(approvalVal, VALUE_STYLE)
                            ._tr();
                }
                comment = comment.replaceAll("\\r\\n|\\n\\r|\\n|\r", "<br>");
                builder.tr().td(LocaleUtils.getString("approval.comment"), ITEM_STYLE)
                        .td(comment, VALUE_STYLE)._tr()
                        ._table();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                builder.tr().td(LocaleUtils.getString("approval.mailBody"))._tr();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                String address = FileManager.getInstance().getSystemProperties().getProperty("serverAddress", "http://localhost:8080");
                String linkURL = String.format(address + PATH, approval.getApprovalId(), reqOrgInfo.getOrganizationIdentify());
                SimpleHtmlBuilder buttonBuilder = new SimpleHtmlBuilder();
                buttonBuilder.center(CENTER_STYLE).link(LocaleUtils.getString("approval.contentConfirmation"), linkURL, LINK_STYLE)._center();
                builder.center(CENTER_STYLE).table(0, BUTTON_SIZE, 15).tr().td(buttonBuilder, BUTTON_STYLE)._tr()._table()._center();
                builder._tr()._table()._body()._html();

                ServerErrorTypeEnum ret = mail.send(fromAddress, toRequestorAddress, subject.toString(), builder.toString(), true);

                if (!ret.equals(ServerErrorTypeEnum.SUCCESS)) {
                    result = false;
                    logger.fatal("Failed to send the notification email to the applicant. ServerErrorType: {}", ret);
                    return result;
                }
            }

            //通知メール送信対象の組織情報一覧を作成
            Optional<ApprovalFlowEntity> opt;
            List<Long> organizationIds;
            List<OrganizationEntity> approvalOrgList = new ArrayList<>();
            //承認者への通知メール送信
            switch (type) {
                case REQUEST:
                    // 承認済でなく、かつ、承認順が最小の承認フロー情報を取得
                    opt = approval.getApprovalFlows().stream()
                            .filter(p -> !p.getApprovalState().equals(ApprovalStatusEnum.APPROVE))
                            .collect(Collectors.minBy(Comparator.comparing(ApprovalFlowEntity::getApprovalOrder)));
                    if (opt.isPresent()) {
                        OrganizationEntity target = findOrganization(opt.get().getApproverId());
                        if (Objects.nonNull(target) && Objects.nonNull(target.getMailAddress())
                                && !StringUtils.isEmpty(target.getMailAddress())) {
                            approvalOrgList.add(target);
                        }
                    } else {
                        logger.fatal("No approval flow information was found.");
                        return false;
                    }
                    break;
                case APPROVAL_CANCEL:
                    // 操作者の承認フロー情報を取得
                    opt = approval.getApprovalFlows().stream()
                            .filter(p -> p.getApproverId().equals(authId))
                            .findFirst();
                    if (opt.isPresent()) {
                        // 操作者の次の承認順の承認者の承認フロー情報を取得
                        Integer order = opt.get().getApprovalOrder() + 1;
                        Optional<ApprovalFlowEntity> optTarget = approval.getApprovalFlows().stream()
                                .filter(p -> p.getApprovalOrder().equals(order))
                                .findFirst();
                        if (optTarget.isPresent()) {
                            OrganizationEntity target = findOrganization(optTarget.get().getApproverId());
                            if (Objects.nonNull(target) && !StringUtils.isEmpty(target.getMailAddress())) {
                                approvalOrgList.add(target);
                            }
                        } else {
                            logger.fatal("No approval flow information was found.");
                            return false;
                        }
                    } else {
                        logger.fatal("No approval flow information was found.");
                        return false;
                    }
                    break;
                case REQUEST_CANCEL:
                    // 承認済みの承認者一覧
                    organizationIds = approval.getApprovalFlows().stream()
                            .filter(p -> p.getApprovalState().equals(ApprovalStatusEnum.APPROVE))
                            .map(a -> a.getApproverId())
                            .distinct()
                            .collect(Collectors.toList());

                    // 承認済でなく、かつ、承認順が最小の承認フロー情報を取得
                    opt = approval.getApprovalFlows().stream()
                            .filter(p -> !p.getApprovalState().equals(ApprovalStatusEnum.APPROVE))
                            .collect(Collectors.minBy(Comparator.comparing(ApprovalFlowEntity::getApprovalOrder)));
                    if (opt.isPresent()) {
                        organizationIds.add(opt.get().getApproverId());
                    }

                    if (!organizationIds.isEmpty()) {
                        approvalOrgList = findOrganizationByIds(organizationIds).stream()
                                .filter(p -> !StringUtils.isEmpty(p.getMailAddress()))
                                .collect(Collectors.toList());
                    } else {
                        logger.fatal("No approval flow information was found.");
                        return false;
                    }
                    break;
                case REMAND:
                    // 承認済みの承認者一覧
                    organizationIds = approval.getApprovalFlows().stream()
                            .filter(p -> p.getApprovalState().equals(ApprovalStatusEnum.APPROVE))
                            .map(a -> a.getApproverId())
                            .distinct()
                            .collect(Collectors.toList());

                    if (!organizationIds.isEmpty()) {
                        approvalOrgList = findOrganizationByIds(organizationIds).stream()
                                .filter(p -> !StringUtils.isEmpty(p.getMailAddress()))
                                .collect(Collectors.toList());
                    }
                    break;
                case APPROVAL:
                    organizationIds = approval.getApprovalFlows().stream()
                            .map(a -> a.getApproverId())
                            .collect(Collectors.toList());

                    if (!organizationIds.isEmpty()) {
                        approvalOrgList = findOrganizationByIds(organizationIds).stream()
                                .filter(p -> !StringUtils.isEmpty(p.getMailAddress()))
                                .collect(Collectors.toList());
                    }
                    break;
                default:
                    break;
            }

            //メール本文作成、送信
            for (OrganizationEntity orgInfo : approvalOrgList) {
                SimpleHtmlBuilder builder = new SimpleHtmlBuilder();
                builder.line(DPCTYPE)
                        .html().head().meta(StandardCharsets.UTF_8.toString())._head();
                builder.body().table(0, "");
                builder.tr().td().line(orgInfo.getOrganizationName() + LocaleUtils.getString("key.HonorificTitle"))._tr();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                builder.tr().td().line(guidance)._tr();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                builder.table(1, TABLE_STYLE)
                        .tr()
                        .td(LocaleUtils.getString("approval.approvalId"), ITEM_STYLE)
                        .td(approval.getApprovalId().toString(), VALUE_STYLE)
                        ._tr()
                        .tr()
                        .td(LocaleUtils.getString("approval.applicant"), ITEM_STYLE)
                        .td(reqOrgInfo.getOrganizationName(), VALUE_STYLE)
                        ._tr()
                        .tr()
                        .td(changedDataType, ITEM_STYLE)
                        .td(changedDataName, VALUE_STYLE)
                        ._tr()
                        .tr()
                        .td(LocaleUtils.getString("approval.revision"), ITEM_STYLE)
                        .td(String.valueOf(changedRevision), VALUE_STYLE)
                        ._tr();
                if (type.equals(NotificationCategoryEnum.REMAND) || type.equals(NotificationCategoryEnum.APPROVAL_CANCEL) || type.equals(NotificationCategoryEnum.APPROVAL)) {
                    if (!approvalKey.isEmpty()) {
                        builder.tr()
                                .td(approvalKey, ITEM_STYLE)
                                .td(approvalVal, VALUE_STYLE)
                                ._tr();
                    }
                }
                comment = comment.replaceAll("\\r\\n|\\n\\r|\\n|\r", "<br>");
                builder.tr().td(LocaleUtils.getString("approval.comment"), ITEM_STYLE)
                        .td(comment, VALUE_STYLE)._tr()
                        ._table();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                builder.tr().td(LocaleUtils.getString("approval.mailBody"))._tr();
                builder.tr().td("", BRANK_HEIGHT)._tr();
                String address = FileManager.getInstance().getSystemProperties().getProperty("serverAddress", "http://localhost");
                String linkURL = String.format(address + PATH, approval.getApprovalId(), orgInfo.getOrganizationIdentify());
                SimpleHtmlBuilder buttonBuilder = new SimpleHtmlBuilder();
                buttonBuilder.center(CENTER_STYLE).link(LocaleUtils.getString("approval.contentConfirmation"), linkURL, LINK_STYLE)._center();
                builder.center(CENTER_STYLE).table(0, BUTTON_SIZE, 15).tr().td(buttonBuilder, BUTTON_STYLE)._tr()._table()._center();
                builder._tr()._table()._body()._html();

                // 宛先
                List<String> toAddresses = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                toAddresses.forEach((s) -> sb.append(s).append(","));
                String toAddress = orgInfo.getMailAddress();

                ServerErrorTypeEnum ret = mail.send(fromAddress, toAddress, subject.toString(), builder.toString(), true);

                if (!ret.equals(ServerErrorTypeEnum.SUCCESS)) {
                    logger.fatal("Failed to send notification email to approver. ServerErrorType: {}", ret);
                    result = false;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            result = false;
        }
        return result;
    }

    /**
     * メール設定情報の取得
     *
     * @return メール設定情報(設定値取得不可の場合はnull)
     */
    private MailProperty getMailProperty() {

        // adFactoryServer設定
        ServiceConfig config = ServiceConfig.getInstance();
        String smtpServer = config.getSmtpServer();
        Integer smtpPort = config.getSmtpPort();
        String smtpUser = config.getSmtpUser();
        String smtpPassword = config.getSmtpPassword();

        // 障害レポート設定
        TroubleReportConfig props = TroubleReportConfig.getInstance();
        props.load();

        Integer mailTimeoutMs = props.getReportMailTimeout() * 1000;

        MailProperty prop = new MailProperty();
        prop.setHost(smtpServer);
        prop.setPort(smtpPort);
        prop.setUser(smtpUser);
        prop.setPassword(smtpPassword);
        prop.setIsEnableAuth(props.getReportMailAuth());
        prop.setIsEnableTLS(props.getReportMailTLS());
        prop.setConnectionTimeout(mailTimeoutMs);
        prop.setTimeout(mailTimeoutMs);
        prop.setCharset(SMTP_CHARSET);
        prop.setMailFrom(smtpUser);

        return prop;
    }

    /**
     * 工程IDを指定して、対象の工程を使用している工程順で、申請中の工程順の件数を取得する。
     *
     * @param workIds 工程ID
     * @return 件数
     */
    private long countApplyWorkflowByWorkId(List<Long> workIds) {
        TypedQuery<Long> query = this.em.createNamedQuery("ApprovalEntity.countApplyWorkflowByWorkId", Long.class);
        query.setParameter("workIds", workIds);
        return query.getSingleResult();
    }

    /**
     * 工程順IDを指定して、対象の工程順で使用している工程で、申請中の工程の件数を取得する。
     *
     * @param workflowId 工程順ID
     * @return 件数
     */
    private long countApplyWorkByWorkflowId(long workflowId) {
        TypedQuery<Long> query = this.em.createNamedQuery("ApprovalEntity.countApplyWorkByWorkflowId", Long.class);
        query.setParameter("workflowId", workflowId);
        return query.getSingleResult();
    }
}
