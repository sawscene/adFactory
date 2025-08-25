/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import adtekfuji.utility.ThreadUtils;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
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
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jp.adtekfuji.adFactory.entity.MessageEntity;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ResultResponse;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.ListWrapper;
import jp.adtekfuji.adfactoryserver.entity.ObjectParam;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.*;
import jp.adtekfuji.adfactoryserver.entity.lite.LiteWorkflow;
import jp.adtekfuji.adfactoryserver.entity.master.ConHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkSectionEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.*;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalModel;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowProcess;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.bpmn.model.BpmnModel;
import jp.adtekfuji.bpmn.model.BpmnModeler;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順情報REST：工程順情報を操作するためのクラス
 *
 * @author ke.yokoi
 */
@Singleton
@Path("workflow")
public class WorkflowEntityFacadeREST extends AbstractFacade<WorkflowEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @Inject
    private ApprovalModel approvalModel;

    @EJB
    private KanbanEntityFacadeREST kanbanEntityFacade;

    @EJB
    private HierarchyEntityFacadeREST hierarchyFacade;

    @EJB
    private WorkEntityFacadeREST workEntityFacade;

    @EJB
    private WorkKanbanEntityFacadeREST workKanbanEntityFacade;

    @EJB
    private ActualResultEntityFacadeREST actualResultRest;

    @EJB
    private OrganizationEntityFacadeREST organizationFacade;

    @EJB
    private WorkKanbanEntityFacadeREST workKanbanEntityFacadeREST;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public WorkflowEntityFacadeREST() {
        super(WorkflowEntity.class);
    }

    /**
     * 指定したIDの工程順情報を取得する。(基本情報のみ)
     *
     * @param id 工程順ID
     * @return 工程順情報
     */
    @Lock(LockType.READ)
    public WorkflowEntity findBasicInfo(Long id) {
        WorkflowEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new WorkflowEntity();
        }

        // 工程順が属する階層の階層IDを取得する。
        Long parentId = this.findParentId(id);
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        return entity;
    }

    /**
     * 工程順IDを指定して、工程順情報を取得する。
     *
     * @param id             工程順ID
     * @param isGetLatestRev 最新版数を取得する？ (true:取得する, false:取得しない)
     * @param authId         認証ID
     * @return 工程順情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkflowEntity find(@PathParam("id") Long id, @QueryParam("getlatestrev") Boolean isGetLatestRev, @QueryParam("authId") Long authId) {
        logger.info("find: id={}, getlatestrev={}, authId={}", id, isGetLatestRev, authId);

        WorkflowEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new WorkflowEntity();
        }

        // 工程順が属する階層の階層IDを取得する。
        Long parentId = this.findParentId(id);
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        // 通常工程の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
        entity.setConWorkflowWorkCollection(this.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, id, true));
        // 追加工程の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
        entity.setConWorkflowSeparateworkCollection(this.getConWorkflowWorks(WorkKbnEnum.ADDITIONAL_WORK, id, true));

        if (Objects.nonNull(isGetLatestRev) && isGetLatestRev) {
            // 最新版数 (削除済・未承認は除く)
            int latestRev = this.getLatestRev(entity.getWorkflowName(), true, true);
            entity.setLatestRev(latestRev);
        }

        // 承認機能ライセンスが有効な場合は申請情報も取得する。
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
        if (approvalLicense && Objects.nonNull(entity.getApprovalId())) {
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            entity.setApproval(approval);
        }

        return entity;
    }


    /**
     * id指定に工程順を検索する
     * @param ids 工程順ID群
     * @param authId 承認
     * @return 工程順
     */
    @Lock(LockType.READ)
    public List<WorkflowEntity> find(List<Long> ids, Long authId) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        TypedQuery<WorkflowEntity> query = this.em.createNamedQuery("WorkflowEntity.findByIds", WorkflowEntity.class);
        query.setParameter("workflowIds", ids);

        return query.getResultList();
    }


    /**
     * 指定した工程順名・版数の工程順情報を取得する。
     *
     * @param name           工程順名
     * @param rev            工程順の版番
     * @param isGetLatestRev 最新版数を取得する？ (true:取得する, false:取得しない)
     * @param userId         ユーザーID (組織ID)
     * @param authId         認証ID
     * @return 工程順情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkflowEntity findByName(@QueryParam("name") String name, @QueryParam("rev") Integer rev,
                                     @QueryParam("getlatestrev") Boolean isGetLatestRev, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("findByName: name={}, rev={}, getlatestrev={}, userId={}, authId={}", name, rev, isGetLatestRev, userId, authId);

        TypedQuery<WorkflowEntity> query;
        if (Objects.nonNull(rev)) {
            // 工程順名・版数を指定して、工程順情報を取得する。(削除済は対象外)
            query = this.em.createNamedQuery("WorkflowEntity.findByWorkflowName", WorkflowEntity.class);
            query.setParameter("workflowRev", rev);
        } else {
            // 工程順名を指定して、最終承認済の最新版数の工程順を取得する。(削除済は対象外)
            query = this.em.createNamedQuery("WorkflowEntity.findLatestRevByName", WorkflowEntity.class);
        }

        query.setParameter("workflowName", name);

        WorkflowEntity entity;
        try {
            entity = query.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new WorkflowEntity();
        }

        // 工程順が属する階層の階層IDを取得する。
        Long parentId = this.findParentId(entity.getWorkflowId());
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        // 対象階層からルートまでの階層アクセス権をチェックする。
        boolean isAccessible = hierarchyFacade.isHierarchyAccessible(HierarchyTypeEnum.WORKFLOW, parentId, userId);
        if (!isAccessible) {
            return new WorkflowEntity();
        }

        // 通常工程の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
        entity.setConWorkflowWorkCollection(this.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, entity.getWorkflowId(), true));
        // 追加工程の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
        entity.setConWorkflowSeparateworkCollection(this.getConWorkflowWorks(WorkKbnEnum.ADDITIONAL_WORK, entity.getWorkflowId(), true));

        if (Objects.nonNull(isGetLatestRev) && isGetLatestRev) {
            // 最新版数 (削除済・未承認は除く)
            int latestRev = this.getLatestRev(entity.getWorkflowName(), true, true);
            entity.setLatestRev(latestRev);
        }

        // 承認機能ライセンスが有効な場合は申請情報も取得する。
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
        if (approvalLicense && Objects.nonNull(entity.getApprovalId())) {
            ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
            entity.setApproval(approval);
        }

        return entity;
    }


    @Lock(LockType.READ)
    public List<WorkflowEntity> findByName(List<String> name, Long authId) {
        logger.info("findByName: name={}, authId={}", name, authId);

        if (name.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            TypedQuery<WorkflowEntity> query;
            // 工程順名を指定して、最終承認済の最新版数の工程順を取得する。(削除済は対象外)
            query = this.em.createNamedQuery("WorkflowEntity.findByNameList", WorkflowEntity.class);
            query.setParameter("workflowName", name);
            return query.getResultList();
        } catch (Exception ex) {
            logger.error(ex, ex);
            return new ArrayList<>();
        }
    }


    /**
     * 工程順情報一覧を範囲指定して取得する。
     *
     * @param from   範囲の先頭
     * @param to     範囲の末尾
     * @param authId 認証ID
     * @return 指定された範囲の工程順情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);

        List<WorkflowEntity> entities;
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            entities = super.findRange(from, to);
        } else {
            entities = super.findAll();
        }

        // 承認機能ライセンス
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());

        for (WorkflowEntity entity : entities) {
            // 工程順が属する階層の階層IDを取得する。
            Long parentId = this.findParentId(entity.getWorkflowId());
            if (Objects.nonNull(parentId)) {
                entity.setParentId(parentId);
            }

            // 通常工程の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
            entity.setConWorkflowWorkCollection(this.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, entity.getWorkflowId(), true));
            // 追加工程の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
            entity.setConWorkflowSeparateworkCollection(this.getConWorkflowWorks(WorkKbnEnum.ADDITIONAL_WORK, entity.getWorkflowId(), true));

            // 承認機能ライセンスが有効な場合は申請情報も取得する。
            if (approvalLicense && Objects.nonNull(entity.getApprovalId())) {
                ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
                entity.setApproval(approval);
            }
        }

        return entities;
    }

    /**
     * 工程順情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 工程順情報の件数
     */
    @Lock(LockType.READ)
    @GET
    @Path("count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countAll(@QueryParam("authId") Long authId) {
        logger.info("countAll: authId={}", authId);
        return String.valueOf(super.count());
    }

    /**
     * 工程順情報を登録する。
     *
     * @param entity 工程順情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(WorkflowEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        ResponseEntity response = this.addWorkflow(entity, false, authId);
        if (response.isSuccess()) {
            WorkflowEntity newEntity = (WorkflowEntity) response.getUserData();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("workflow/").append(newEntity.getWorkflowId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 工程順情報を登録する。
     *
     * @param entity 工程順情報
     * @param isLite adFactory Lite か？
     * @param authId 認証ID
     * @return 結果
     */
    private ResponseEntity addWorkflow(WorkflowEntity entity, boolean isLite, Long authId) {
        logger.info("add: {}, isLite={}, authId={}", entity, isLite, authId);
        try {
            // 版数が未指定の場合は「1」とする。
            if (Objects.isNull(entity.getWorkflowRev())) {
                entity.setWorkflowRev(1);
            }

            if (isLite) {
                entity.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
            } else {
                // 工程順名の重複を確認する。(削除済も含む)
                TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.checkAddByWorkflowName", Long.class);
                query.setParameter("workflowName", entity.getWorkflowName());
                query.setParameter("workflowRev", entity.getWorkflowRev());
                if (query.getSingleResult() > 0) {
                    // 該当するものがあった場合、重複を通知する。
                    return ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
                }

                // 承認機能ライセンスが有効な場合は承認状態を「未承認」に、無効な場合は「最終承認済」にする。
                boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
                if (approvalLicense) {
                    entity.setApprovalState(ApprovalStatusEnum.UNAPPROVED);
                } else {
                    entity.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
                }
            }

            // 工程順情報を登録する。
            super.create(entity);
            this.em.flush();

            // 工程順の階層関連付け情報を登録する。
            this.addHierarchy(entity);

            // 通常工程の工程の関連付け情報(工程・設備・組織)を登録する。
            this.addConWorkflowWorks(entity.getWorkflowId(), entity.getConWorkflowWorkCollection());
            // 追加工程の工程の関連付け情報(工程・設備・組織)を登録する。
            this.addConWorkflowWorks(entity.getWorkflowId(), entity.getConWorkflowSeparateworkCollection());
            this.em.flush();

            return ResponseEntity.success().userData(entity);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程順情報をコピーする。
     *
     * @param id     工程順ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Path("copy/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response copy(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("copy: id={}, authId={}", id, authId);

        WorkflowEntity entity = this.find(id, false, authId);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        boolean isFind = true;
        StringBuilder name = new StringBuilder(entity.getWorkflowName())
                .append(SUFFIX_COPY);
        while (isFind) {
            // 工程順名の重複を確認する。(削除済も含む)
            TypedQuery<Long> checkQuery = this.em.createNamedQuery("WorkflowEntity.checkAddByWorkflowName", Long.class);
            checkQuery.setParameter("workflowName", name.toString());
            checkQuery.setParameter("workflowRev", 1);
            if (checkQuery.getSingleResult() > 0) {
                name.append(SUFFIX_COPY);
                continue;
            }
            isFind = false;
        }

        WorkflowEntity newEntity = new WorkflowEntity(entity);
        newEntity.setWorkflowName(name.toString());
        newEntity.setWorkflowRev(1);// 版数

        // 新規追加する。
        this.add(newEntity, authId);

        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
        if (approvalLicense) {
            newEntity.setLatestRev(0);
        } else {
            newEntity.setLatestRev(1);
        }

        // 作成した情報を元に、戻り値のURIを作成する。
        URI uri = new URI(new StringBuilder("workflow/").append(newEntity.getWorkflowId()).toString());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * 工程順情報を更新する。
     *
     * @param entity 工程順情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(WorkflowEntity entity, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.updateWorkflow(entity, false, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 工程順情報を更新する。
     *
     * @param entity 工程順情報
     * @param isLite dFactory Lite か？
     * @param authId 認証ID
     * @return 結果
     */
    private ResponseEntity updateWorkflow(WorkflowEntity entity, boolean isLite, Long authId) {
        logger.info("updateWorkflow: {}, authId={}", entity, authId);
        try {
            WorkflowEntity target = super.find(entity.getWorkflowId());

            if (!isLite) {
                // 排他用バージョンを確認する。
                if (!target.getVerInfo().equals(entity.getVerInfo())) {
                    // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                    return ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO);
                }

                // 版数がnullの場合は「1」とする。(「版数」実装前のデータ)
                if (Objects.isNull(entity.getWorkflowRev())) {
                    entity.setWorkflowRev(1);
                }

                // 工程順名の重複を確認する。
                TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.checkUpdateByWorkflowName", Long.class);
                query.setParameter("workflowId", entity.getWorkflowId());
                query.setParameter("workflowName", entity.getWorkflowName());
                query.setParameter("workflowRev", entity.getWorkflowRev());
                if (query.getSingleResult() > 0) {
                    // 該当するものがあった場合、重複を通知する。
                    return ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
                }
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 工程順情報を更新する。
            super.edit(entity);

            // 工程順の階層関連付け情報を更新する。
            this.registHierarchy(entity);

            // 関連付け情報(工程・設備・組織)を削除する。
            this.removeConWorkfowWorks(entity.getWorkflowId());
            // 通常工程の関連付け情報(工程・設備・組織)を登録する。
            this.addConWorkflowWorks(entity.getWorkflowId(), entity.getConWorkflowWorkCollection());
            // 追加工程の関連付け情報(工程・設備・組織)を登録する。
            this.addConWorkflowWorks(entity.getWorkflowId(), entity.getConWorkflowSeparateworkCollection());
            this.em.flush();

            return ResponseEntity.success().userData(entity);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 指定した工程順IDの工程順情報を削除する。
     *
     * @param id     工程順ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.removeWorkflow(id, false, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 指定した工程順IDの工程順情報を削除する。
     *
     * @param id 工程順ID
     * @param isLite dFactory Lite か？
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity removeWorkflow(Long id, boolean isLite, Long authId) {
        logger.info("removeWorkflow: id={}, isLite={}, authId={}", id, isLite, authId);

        // 工程順IDを指定して、工程順情報を取得する。
        WorkflowEntity entity = super.find(id);
        if (Objects.isNull(entity.getWorkflowId())) {
            return ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_DELETE);
        }

        if (!isLite) {
            // 申請中の場合は削除不可。
            if (Objects.equals(entity.getApprovalState(), ApprovalStatusEnum.APPLY)) {
                return ResponseEntity.failed(ServerErrorTypeEnum.APPROVAL_APPLY_NON_DELETABLE);
            }

            // 申請情報を削除する。
            if (Objects.nonNull(entity.getApprovalId())) {
                this.approvalModel.removeApproval(entity.getApprovalId());
            }
        }

        // 工程順IDを指定して、カンバン情報の件数を取得する。
        Long numKanban = this.countKanbanAssociation(id);

        // 関連付けが無い場合、完全に削除する。
        if (numKanban == 0) {
            logger.info("remove-real:{}", id);

            List<WorkEntity> workEntities
                    = this.workEntityFacade
                    .findByWorlflowId(entity.getWorkflowId(), null, null, authId);

            // 工程順階層関連付け情報を削除する。
            this.removeHierarchy(id);

            // 関連付け情報(工程・設備・組織)を削除する。
            this.removeConWorkfowWorks(id);

            // 工程順情報をを削除する。
            super.remove(entity);

            workEntities
                    .stream()
                    .filter(WorkEntity::getRemoveFlag)
                    .map(WorkEntity::getWorkId)
                    .filter(workId -> {
                        // 工程IDを指定して、工程順工程関連付け情報の件数を取得する。
                        TypedQuery<Long> queryWorkflow = this.em.createNamedQuery("WorkEntity.countWorkflowWorkAssociation", Long.class);
                        queryWorkflow.setParameter("workId", id);
                        Long numWorkflow = queryWorkflow.getSingleResult();

                        // 工程IDを指定して、工程カンバン情報の件数を取得する。
                        TypedQuery<Long> queryKanban = this.em.createNamedQuery("WorkEntity.countKanbanAssociation", Long.class);
                        queryKanban.setParameter("workId", id);
                        Long numWorkKanban = queryKanban.getSingleResult();
                        return numWorkflow == 0 && numWorkKanban == 0;
                    })
                    .forEach(workId -> this.workEntityFacade.removeWork(workId, false, authId));
            return ResponseEntity.success();
        }

        // 関連付けがある場合、削除フラグで論理削除する。

        entity = this.find(id, false, authId);
        if (Objects.isNull(entity.getWorkflowId())) {
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }

        // 削除済の名称に変更する。
        boolean isFind = true;
        int num = 1;
        String baseName = new StringBuilder(entity.getWorkflowName())
                .append(SUFFIX_REMOVE)
                .toString();
        String name = new StringBuilder(baseName)
                .append(num)
                .toString();
        while (isFind) {
            // 工程順名の重複を確認する。
            TypedQuery<Long> checkQuery = this.em.createNamedQuery("WorkflowEntity.checkUpdateByWorkflowName", Long.class);
            checkQuery.setParameter("workflowId", id);
            checkQuery.setParameter("workflowName", name);
            if (Objects.nonNull(entity.getWorkflowRev())) {
                checkQuery.setParameter("workflowRev", entity.getWorkflowRev());
            } else {
                checkQuery.setParameter("workflowRev", 1);
            }
            if (checkQuery.getSingleResult() > 0) {
                num++;
                name = new StringBuilder(baseName)
                        .append(num)
                        .toString();
                continue;
            }
            isFind = false;
        }
        logger.info("remove-logic:{},{}", id, name);

        entity.setWorkflowName(name);
        entity.setRemoveFlag(true);

        // 工程順情報を更新する。
        super.edit(entity);

        // 工程順の階層関連付け情報を削除する。
        this.removeHierarchy(id);
        // ※．論理削除の場合は他の関連データは削除しない。

        return ResponseEntity.success();
    }

    /**
     * 工程情報から、その工程が含まれる工程順の作業時間を更新する。
     *
     * @param work    工程情報
     * @param isShift 並列工程の作業時間をシフトするかどうか
     * @param authId  認証ID
     * @return 更新された工程順を返す。
     */
    @PUT
    @Path("time")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowEntity> updateTime(WorkEntity work, @QueryParam("isShift") Boolean isShift, @QueryParam("authId") Long authId) {
        logger.info("updateTime: work={}, isShift={}, authId={}", work, isShift, authId);

        List<WorkflowEntity> workflows = new ArrayList<>();
        try {
            if (Objects.isNull(isShift)) {
                isShift = true;
            }

            // 更新対象となる工程順を抽出する。
            List<Long> workflowIds = this.findWorkflowIdByKbnAndWorkId(WorkKbnEnum.BASE_WORK, work.getWorkId());
            for (Long workflowId : workflowIds) {
                WorkflowEntity workflow = super.find(workflowId);
                if (Objects.nonNull(workflow)) {
                    workflow.setConWorkflowWorkCollection(this.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, workflow.getWorkflowId(), false));
                    workflows.add(workflow);
                }
            }

            for (WorkflowEntity workflow : workflows) {
                WorkflowProcess workflowProcess = new WorkflowProcess(workflow);
                workflowProcess.setOrganizationRest(this.organizationFacade);
                workflowProcess.updateTimetable(workflow, work);

                // 更新
                workflow.setUpdatePersonId(work.getUpdatePersonId());
                workflow.setUpdateDatetime(work.getUpdateDatetime());
                super.edit(workflow);

                for (ConWorkflowWorkEntity workflowWork : workflow.getConWorkflowWorkCollection()) {
                    this.em.persist(workflowWork);
                }
                this.em.flush();
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("updateTime end.");
        }

        return workflows;
    }
    
    /**
     * 工程を使用した最新版の工程順を取得する
     * 
     * @param workName 工程名
     * @param authId 認証ID
     * @return 工程順一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("work-name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowEntity> findByWorkName(@QueryParam("workName") String workName, @QueryParam("authId") Long authId) {
        TypedQuery<WorkEntity> workQuery = this.em.createNamedQuery("WorkEntity.findByName", WorkEntity.class);
        workQuery.setParameter("workName", workName);
        List<WorkEntity> works = workQuery.getResultList();
        if (works.isEmpty()) {
            return new ArrayList<>();
        }
    
        List<Long> workIds = works.stream().map(o -> o.getWorkId()).collect(Collectors.toList());
        TypedQuery<WorkflowEntity> workflowQuery = this.em.createNamedQuery("WorkflowEntity.findLatestByWorkId", WorkflowEntity.class);
        workflowQuery.setParameter("workIds", workIds);
        List<WorkflowEntity> workflows = workflowQuery.getResultList();
        return workflows;
    }
    
    /**
     * 工程順の工程を更新する。
     * 
     * @param workflowIds 工程順ID
     * @param workId 工程ID
     * @param authId 認証ID
     * @return 工程順一覧
     */
    @PUT
    @Path("work")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateWork(@QueryParam("id") List<Long> workflowIds, @QueryParam("workId") Long workId, @QueryParam("authId") Long authId) {
        logger.info("updateWork: workflowIds={}, workId={}, authId={}", workflowIds, workId, authId);

        try {
            Date now = new Date();
            
            // 工程に関連した最新版数の工程順一覧を取得
            List<WorkflowEntity> workflows = this.find(workflowIds, authId);
            if (workflowIds.size() != workflows.size()) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKFLOW)).build();
            }

            WorkEntity work = this.workEntityFacade.find(workId);
            if (Objects.isNull(work)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORK)).build();
            }

            for (WorkflowEntity workflow : workflows) {
                
                WorkEntity oldWork = this.findWork(workflow.getWorkflowId(), work.getWorkName(), null);
                if (Objects.isNull(oldWork)) {
                    continue;
                }

                WorkflowEntity targetWorkflow;
                if (this.countKanbanAssociation(workflow.getWorkflowId()) > 0) {
                    // 工程順を改訂
                    ResponseWorkflowEntity response = (ResponseWorkflowEntity) this.revise(workflow.getWorkflowId(), null).getEntity();
                    if (!response.isSuccess()) {
                        throw new Exception();
                    }

                    targetWorkflow = response.getValue();
                } else {
                    targetWorkflow = workflow;
                }
    
                // 工程順ダイアグラムの工程IDを更新
                String diaglam = this.replaceDiagramTaskId(targetWorkflow.getWorkflowDiaglam(), String.valueOf(oldWork.getWorkId()), String.valueOf(workId), null);

                targetWorkflow.setWorkflowDiaglam(diaglam);
                targetWorkflow.setUpdatePersonId(authId);
                targetWorkflow.setUpdateDatetime(now);

                this.em.merge(targetWorkflow);
                
                // 工程順・工程関連付け情報の工程IDを更新
                this.replaceConWorkflowWork(targetWorkflow.getWorkflowId(), oldWork.getWorkId(), workId);
                // 工程・設備関連付け情報の工程IDを更新
                this.replaceConWorkEquipment(targetWorkflow.getWorkflowId(), oldWork.getWorkId(), workId);
                // 工程・組織関連付け情報の工程IDを更新
                this.replaceConWorkOrganization(targetWorkflow.getWorkflowId(), oldWork.getWorkId(), workId);

                this.em.flush();
            }

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.debug("updateWork end.");
        }
    }
 
    /**
     * 工程情報を取得する。
     * 
     * @param workflowId 工程順ID
     * @param workName 工程名
     * @param authId 認証ID
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("work")
    @Produces({"application/xml", "application/json"})
    public WorkEntity findWork(@QueryParam("id") Long workflowId, @QueryParam("workName") String workName, @QueryParam("authId") Long authId) {
        try {
            TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findByNameAndWorkflowId", WorkEntity.class);
            query.setParameter("workName", workName);
            query.setParameter("workflowId", workflowId);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
    
    /**
     * 工程順を作成する。
     *
     * @param entity 工程順情報
     * @return 作成された工程順情報
     * @throws Exception
     */
    public WorkflowEntity createWorkflow(WorkflowEntity entity) throws Exception {
        logger.info("createWorkflow:{}", entity);

        // 版数が未指定の場合は「1」とする。
        if (Objects.isNull(entity.getWorkflowRev())) {
            entity.setWorkflowRev(1);
        }

        // 工程順名の重複を確認する。(削除済も含む)
        TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.checkAddByWorkflowName", Long.class);
        query.setParameter("workflowName", entity.getWorkflowName());
        query.setParameter("workflowRev", entity.getWorkflowRev());
        if (query.getSingleResult() > 0) {
            return null;
        }

        // 工程順情報を登録する。
        super.create(entity);
        this.em.flush();

        // 工程順の階層関連付け情報を登録する。
        this.addHierarchy(entity);

        // 通常工程の工程の関連付け情報(工程・設備・組織)を登録する。
        this.addConWorkflowWorks(entity.getWorkflowId(), entity.getConWorkflowWorkCollection());
        // 追加工程の工程の関連付け情報(工程・設備・組織)を登録する。
        this.addConWorkflowWorks(entity.getWorkflowId(), entity.getConWorkflowSeparateworkCollection());
        this.em.flush();

        return entity;
    }

    /**
     * 工程順を改訂する。(コピーして新しい版数を付与)
     *
     * @param id     工程順ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Path("revision/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response revise(@PathParam("id") Long id, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("revise: id={}, authId={}", id, authId);

        WorkflowEntity entity = this.find(id, false, authId);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseWorkflowEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        // 新しい版数 (最新版数 +1)
        int workflowRev = this.getLatestRev(entity.getWorkflowName(), false, false) + 1;
        if (workflowRev > Constants.WORKFLOW_REV_LIMIT) {
            // 版数の最大値オーバー
            return Response.serverError().entity(ResponseWorkflowEntity.failed(ServerErrorTypeEnum.OVER_MAX_VALUE)).build();
        }

        // 工程順をコピーして、新しい版数で追加する。
        WorkflowEntity newEntity = new WorkflowEntity(entity);
        newEntity.setWorkflowRev(workflowRev);

        // 最終承認済の最新版数
        int latestRev = this.getLatestRev(entity.getWorkflowName(), true, true);
        newEntity.setLatestRev(latestRev);

        // 新規追加する。
        this.add(newEntity, authId);

        // 作成した情報を元に、戻り値のURIを作成する。
        URI uri = new URI("workflow/" + newEntity.getWorkflowId().toString());

        ResponseWorkflowEntity res = ResponseWorkflowEntity.success().uri(uri);
        res.setValue(newEntity);

        return Response.created(uri).entity(res).build();
    }

    /**
     * 工程順IDを指定して、工程順が属する階層の階層IDを取得する。
     *
     * @param id 工程ID
     * @return 工程階層ID
     */
    @Lock(LockType.READ)
    private Long findParentId(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("ConHierarchyEntity.findHierarchyId", Long.class);
        query.setParameter("hierarchyType", HierarchyTypeEnum.WORKFLOW);
        query.setParameter("workWorkflowId", id);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            // 親階層が設定されていない。(論理削除済の工程順等)
            return null;
        }
    }

    /**
     * 指定したIDの工程順の工程順階層関連付け情報を削除する。
     *
     * @param id 工程順ID
     */
    private void removeHierarchy(Long id) {
        // 階層種別と工程・工程順IDを指定して、階層関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConHierarchyEntity.removeByTypeAndMstId");
        query.setParameter("hierarchyType", HierarchyTypeEnum.WORKFLOW);
        query.setParameter("workWorkflowId", id);

        query.executeUpdate();
    }

    /**
     * 工程順情報の工程順階層関連付け情報を登録する。
     *
     * @param entity 工程順情報
     */
    private void addHierarchy(WorkflowEntity entity) {
        ConHierarchyEntity hierarchy = new ConHierarchyEntity(entity.getParentId(), entity.getWorkflowId(), HierarchyTypeEnum.WORKFLOW);
        this.em.persist(hierarchy);
    }

    /**
     * 工程順の階層関連付け情報を登録または更新する。
     *
     * @param entity 工程順情報
     * @return
     */
    private boolean registHierarchy(WorkflowEntity entity) {
        boolean result = false;

        // 階層種別と工程・工程順IDを指定して、階層関連付け情報の件数を取得する。
        TypedQuery<Long> countQuery = this.em.createNamedQuery("ConHierarchyEntity.countByTypeAndMstId", Long.class);
        countQuery.setParameter("hierarchyType", HierarchyTypeEnum.WORKFLOW);
        countQuery.setParameter("workWorkflowId", entity.getWorkflowId());

        Long count = countQuery.getSingleResult();
        if (count == 0) {
            // 工程順の階層関連付け情報を新規登録する。
            ConHierarchyEntity hierarchy = new ConHierarchyEntity(entity.getParentId(), entity.getWorkflowId(), HierarchyTypeEnum.WORKFLOW);
            this.em.persist(hierarchy);
            result = true;
        } else {
            // 工程の階層関連付け情報を更新する。
            Query updateQuery = this.em.createNamedQuery("ConHierarchyEntity.updateHierarchyId");
            updateQuery.setParameter("hierarchyId", entity.getParentId());
            updateQuery.setParameter("hierarchyType", HierarchyTypeEnum.WORKFLOW);
            updateQuery.setParameter("workWorkflowId", entity.getWorkflowId());

            int updateCount = updateQuery.executeUpdate();
            if (updateCount == 1) {
                result = true;
            }
        }

        return result;
    }

    /**
     * 指定したIDの工程順の工程関連付け情報一覧を取得する。(工程と設備・組織の関連付けを含む)
     *
     * @param workKbn    工程区分
     * @param id         工程順ID
     * @param needDetail 工程情報を取得する？
     * @return 工程関連付け情報一覧
     */
    @Lock(LockType.READ)
    public List<ConWorkflowWorkEntity> getConWorkflowWorks(WorkKbnEnum workKbn, Long id, boolean needDetail) {
        // 工程区分・工程順IDを指定して、工程順工程関連付け情報一覧を取得する。
        TypedQuery<ConWorkflowWorkEntity> query = this.em.createNamedQuery("ConWorkflowWorkEntity.findByKbnAndWorkflowId", ConWorkflowWorkEntity.class);
        query.setParameter("workKbn", workKbn);
        query.setParameter("workflowId", id);

        List<ConWorkflowWorkEntity> conWorks = query.getResultList();
        for (ConWorkflowWorkEntity conWork : conWorks) {
            if (needDetail) {
                // 工程IDを指定して、工程情報を取得する。(基本情報のみ)
                WorkEntity work = this.workEntityFacade.findBasicInfo(conWork.getWorkId());
                conWork.setWorkName(work.getWorkName());
                conWork.setWorkRev(work.getWorkRev());
            }

            // 関連付けされた設備情報一覧を取得してセットする。
            conWork.setEquipmentCollection(this.getWorkflowWorkEquiment(workKbn, id, conWork.getWorkId()));
            // 関連付けされた組織情報一覧を取得してセットする。
            conWork.setOrganizationCollection(this.getWorkflowWorkOrganiztion(workKbn, id, conWork.getWorkId()));
        }
        return conWorks;
    }

    /**
     * 工程区分・工程IDを指定して、工程順・工程関連付け情報一覧を取得する。
     *
     * @param workKbn 工程区分
     * @param workId  工程ID
     * @return 工程関連付け情報一覧
     */
    @Lock(LockType.READ)
    private List<Long> findWorkflowIdByKbnAndWorkId(WorkKbnEnum workKbn, Long workId) {
        // 工程区分・工程IDを指定して、工程順・工程関連付け情報一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkflowWorkEntity.findWorkflowIdByKbnAndWorkId", Long.class);
        query.setParameter("workKbn", workKbn);
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    /**
     * 工程区分・工程順ID・工程IDを指定して、通常工程に関連付されている設備ID一覧を取得する。
     *
     * @param workKbn    工程区分
     * @param workflowId 工程順ID
     * @param workId     工程ID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getWorkflowWorkEquiment(WorkKbnEnum workKbn, Long workflowId, Long workId) {
        // 工程・設備関連付け情報の設備ID一覧を取得する。
        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkEquipmentEntity.findEquipmentIdByKbnAndWorkId", Long.class);
        query.setParameter("workKbn", workKbn);
        query.setParameter("workflowId", workflowId);
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    /**
     * 工程区分・工程順ID・工程IDを指定して、通常工程に関連付されている組織ID一覧を取得する。
     *
     * @param workKbn    工程区分
     * @param workflowId 工程順ID
     * @param workId     工程ID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getWorkflowWorkOrganiztion(WorkKbnEnum workKbn, Long workflowId, Long workId) {
        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkOrganizationEntity.findOrganizationId", Long.class);
        query.setParameter("workKbn", workKbn);
        query.setParameter("workflowId", workflowId);
        query.setParameter("workId", workId);
        return query.getResultList();
    }

    /**
     * 工程順IDを指定して、工程順の関連付け情報(工程・設備・組織)を削除する。
     *
     * @param workflowId 工程順ID
     */
    private void removeConWorkfowWorks(Long workflowId) {
        // 工程・設備関連付け情報を削除する。
        Query query1 = this.em.createNamedQuery("ConWorkEquipmentEntity.removeByWorkflowId");
        query1.setParameter("workflowId", workflowId);
        query1.executeUpdate();

        // 工程・組織関連付け情報を削除する。
        Query query2 = this.em.createNamedQuery("ConWorkOrganizationEntity.removeByWorkflowId");
        query2.setParameter("workflowId", workflowId);
        query2.executeUpdate();

        // 工程順・工程関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConWorkflowWorkEntity.removeByWorkflowId");
        query.setParameter("workflowId", workflowId);
        query.executeUpdate();
    }

    /**
     * 工程の関連付け情報(工程・設備・組織)を登録する。
     *
     * @param workflowId      工程順ID
     * @param conWorkflowWork 工程順・工程関連付け情報一覧
     */
    private void addConWorkflowWorks(Long workflowId, List<ConWorkflowWorkEntity> conWorkflowWorks) {
        if (Objects.isNull(conWorkflowWorks) || conWorkflowWorks.isEmpty()) {
            return;
        }

        // 工程順・工程関連付け情報を登録する。
        for (ConWorkflowWorkEntity conWork : conWorkflowWorks) {
            conWork.setWorkflowId(workflowId);
            this.em.persist(conWork);

            // 工程・設備関連付け情報を登録する。
            if (Objects.nonNull(conWork.getEquipmentCollection())) {
                for (Long equipmentId : conWork.getEquipmentCollection()) {
                    ConWorkEquipmentEntity conEquipment = new ConWorkEquipmentEntity(conWork.getWorkKbn(), workflowId, conWork.getWorkId(), equipmentId);
                    this.em.persist(conEquipment);
                }
            }

            // 工程・組織関連付け情報を登録する。
            if (Objects.nonNull(conWork.getOrganizationCollection())) {
                for (Long organizationId : conWork.getOrganizationCollection()) {
                    ConWorkOrganizationEntity conOrganization = new ConWorkOrganizationEntity(conWork.getWorkKbn(), workflowId, conWork.getWorkId(), organizationId);
                    this.em.persist(conOrganization);
                }
            }
        }
    }

    /**
     * 工程順の最新版数を取得する。
     *
     * @param workflowName 工程順名
     * @param isNotRemove 削除済は対象外？
     * @param approve 承認済のみ取得(true:承認済のみ, false:全て)　※承認機能ライセンスが無効な場合は、trueでも全てが対象
     * @return 最新版数
     */
    @Lock(LockType.READ)
    public int getLatestRev(String workflowName, boolean isNotRemove, boolean approve) {
        TypedQuery<Integer> query;
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
        if (approve && approvalLicense) {
            if (isNotRemove) {
                // 指定した工程順名の最大の版数を取得 ※.削除済・未承認は対象外
                query = this.em.createNamedQuery("WorkflowEntity.findLatestRevApproveNotRemove", Integer.class);
            } else {
                // 指定した工程順名の最大の版数を取得 ※.削除済も対象、未承認は対象外
                query = this.em.createNamedQuery("WorkflowEntity.findLatestRevApprove", Integer.class);
            }
        } else {
            if (isNotRemove) {
                // 指定した工程順名の最大の版数を取得 ※.削除済は対象外
                query = this.em.createNamedQuery("WorkflowEntity.findLatestRevNotRemove", Integer.class);
            } else {
                // 指定した工程順名の最大の版数を取得 ※.削除済も対象
                query = this.em.createNamedQuery("WorkflowEntity.findLatestRev", Integer.class);
            }
        }

        query.setParameter("workflowName", workflowName);
        Integer latestRev = query.getSingleResult();
        if (Objects.isNull(latestRev)) {
            latestRev = 0;
        }
        return latestRev;
    }

    /**
     * 工程順名と版数で工程順IDを取得する。
     *
     * @param name 工程順名
     * @param rev  版数
     * @return 工程順ID
     */
    @Lock(LockType.READ)
    public Long findIdByName(String name, Integer rev) {
        logger.info("findIdByName: name={}, rev={}", name, rev);
        Long workflowId = null;
        try {
            TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.findIdByWorkflowName", Long.class);
            query.setParameter("workflowName", name);
            if (Objects.isNull(rev)) {
                // 版数指定なしの場合は最新版(削除済・未承認は除く)を返す。
                rev = this.getLatestRev(name, true, true);
            }
            query.setParameter("workflowRev", rev);

            workflowId = query.getSingleResult();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return workflowId;
    }

    /**
     * 工程順IDを指定して、該当する工程順を使用しているカンバンの件数があるかどうかを取得する。
     *
     * @param id     工程順ID
     * @param incompleteOnly 未完了のカンバンのみを対象とする
     * @param authId 認証ID
     * @return 使用しているカンバンがあるか (1:ある, 0:ない)
     */
    @GET
    @Lock(LockType.READ)
    @Path("{id}/exist/assigned-kanban")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String existAssignedKanban(@PathParam("id") Long id, @QueryParam("incompleteOnly") Boolean incompleteOnly,  @QueryParam("authId") Long authId) {
        logger.info("existAssignedKanban: id={}, incompleteOnly={}, authId={}", id, incompleteOnly, authId);
        long count = 0L;
    
        if (Objects.isNull(incompleteOnly) || !incompleteOnly) {
            count = this.countKanbanAssociation(id);
        } else {
            TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.countIncompleteKanban", Long.class);
            query.setParameter("workflowId", id);
            count = query.getSingleResult();
        }
        
        if (count > 0) {
            return "1";
        }

        return "0";
    }

    /**
     * 重複データ
     *
     * @param tagName タグ名
     * @param entity  重複リスト
     * @return データチェック結果情報に設定
     */
    static WorkflowDataCheckEntity createDataCheckResult(String tagName, List<WorkflowTagExtractEntity> entities) {
        final String workSeparator = System.lineSeparator() + "    ";
        final String sheetSeparator = System.lineSeparator() + "        ";

        final String message = entities.stream()
                .collect(Collectors.groupingBy(entity -> entity.getWorkName() + " : Rev." + entity.getWorkRev(),
                        Collectors.mapping(WorkflowTagExtractEntity::getSheetName, Collectors.toList())))
                .entrySet()
                .stream()
                .map(workEntry -> workEntry.getValue()
                        .stream()
                        .reduce(workEntry.getKey(), (result, sheetName) -> result + sheetSeparator + sheetName))
                .reduce("Tag : " + tagName, (result, work) -> result + workSeparator + work);

        return new WorkflowDataCheckEntity(WorkflowDateCheckErrorTypeEnum.TagDuplicate, message);
    }

    /**
     * データチェックを実施する
     *
     * @param ids    ワークフローID
     * @param authId 認証ID
     * @return データチェック異常情報
     */
    @GET
    @Lock(LockType.READ)
    @Path("check-workflow")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowDataCheckEntity> checkWorkflow(@QueryParam("id") List<Long> ids, @QueryParam("authId") Long authId) {
        logger.info("start checkWorkflow: ids={}, authId={}", ids, authId);

        List<WorkflowDataCheckEntity> ret = new ArrayList<>();

        try {
            // タグの重複チェック
            java.sql.Array idArray = this.em.unwrap(Connection.class).createArrayOf("integer", ids.toArray());

            final Query query = em
                    .createNamedQuery("WorkflowTagExtractEntity.findTag", WorkflowTagExtractEntity.class)
                    .setParameter(1, idArray);

            List<WorkflowTagExtractEntity> resultList = query.getResultList();
            ret.addAll(resultList.stream()
                            .collect(Collectors.groupingBy(WorkflowTagExtractEntity::getWorkflowId,
                                    Collectors.groupingBy(WorkflowTagExtractEntity::getTagName)))
                            .values()
                            .stream()
                            .flatMap(workflowEntity ->
                                    workflowEntity.entrySet()
                                            .stream()
                                            .filter(entry -> entry.getValue().size() >= 2)
                                            .map(entity -> createDataCheckResult(entity.getKey(), entity.getValue())))
                            .collect(Collectors.toList())
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            logger.info("end checkWorkflow");
        }
        return ret;
    }

    /**
     * 工程順IDを指定して、該当する工程順を使用しているカンバンの件数を取得する。
     *
     * @param workflowId 工程順ID
     * @return 使用しているカンバンの件数
     */
    @Lock(LockType.READ)
    public Long countKanbanAssociation(Long workflowId) {
        TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.countKanbanAssociation", Long.class);
        query.setParameter("workflowId", workflowId);
        return query.getSingleResult();
    }

    /**
     * [Lite] 工程順と工程をまとめて登録する。
     *
     * @param liteWorkflow [Lite] 工程順・工程情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path("lite")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response addLiteWorkflow(LiteWorkflow liteWorkflow, @QueryParam("authId") Long authId) {
        logger.info("addLiteWorkflow: {}, authId={}", liteWorkflow, authId);
        try {
            if (Objects.isNull(liteWorkflow.getWorkflow())
                    || Objects.isNull(liteWorkflow.getWorks())
                    || liteWorkflow.getWorks().isEmpty()) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            WorkflowEntity workflow = liteWorkflow.getWorkflow();

            // 工程順名の重複を確認する。(削除済も含む)
            TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.checkAddByWorkflowName", Long.class);
            query.setParameter("workflowName", workflow.getWorkflowName());
            query.setParameter("workflowRev", workflow.getWorkflowRev());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            Date now = new Date();

            SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date baseDate = datetimeFormat.parse("1970/01/01 00:00:00");;

            String diaglam = workflow.getWorkflowDiaglam();

            liteWorkflow.getWorks().sort(Comparator.comparing(entity -> entity.getWorkId()));

            // 工程順ダイアグラムの仮IDを頭に「n」を付けたものにする。
            for (WorkEntity work : liteWorkflow.getWorks()) {
                String tempId = new StringBuilder("n").append(work.getWorkId()).toString();
                diaglam = this.replaceDiagramTaskId(diaglam, String.valueOf(work.getWorkId()), tempId, null);
            }

            List<WorkEntity> newWorks = new LinkedList();
            List<ConWorkflowWorkEntity> conWorkflowWorks = new ArrayList(); // 工程順・工程関連付け情報一覧(通常工程)

            int workflowOrder = 10001;
            for (WorkEntity work : liteWorkflow.getWorks()) {
                String tempId = new StringBuilder("n").append(work.getWorkId()).toString();

                work.setWorkId(null);
                work.setUpdatePersonId(authId); // 更新者
                work.setUpdateDatetime(now); // 更新日時

                // 工程を登録する。
                ResponseEntity workRes = this.workEntityFacade.addWork(work, true, authId);
                if (!workRes.isSuccess()) {
                    return Response.serverError().entity(workRes).build();
                }

                WorkEntity newWork = (WorkEntity) workRes.getUserData();

                // 工程順ダイアグラムの仮IDを、正式な工程IDに差し替える。
                diaglam = this.replaceDiagramTaskId(diaglam, tempId, String.valueOf(newWork.getWorkId()), null);

                newWorks.add(newWork);

                // 工程順・工程関連付け情報を作成する。
                ConWorkflowWorkEntity conWork = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, newWork.getWorkId(), false, workflowOrder, baseDate, baseDate);
                conWorkflowWorks.add(conWork);

                if (Objects.equals(workflow.getSchedulePolicy(), SchedulePolicyEnum.PriorityParallel)) {
                    workflowOrder++;
                } else {
                    workflowOrder += 10000;
                }
            }

            // 帳票テンプレートパス
            if (Objects.isNull(workflow.getLedgerPath())) {
                workflow.setLedgerPath("");
            }

            // モデル名
            if (Objects.isNull(workflow.getModelName())) {
                workflow.setModelName("");
            }

            workflow.setWorkflowDiaglam(diaglam); // 工程順ダイアグラム
            workflow.setUpdatePersonId(authId); // 更新者
            workflow.setUpdateDatetime(now); // 更新日時

            workflow.setConWorkflowWorkCollection(conWorkflowWorks); // 工程順・工程関連付け情報一覧(通常工程)

            // 工程順を登録する。
            ResponseEntity workflowRes = this.addWorkflow(workflow, true, authId);
            if (workflowRes.isSuccess()) {
                WorkflowEntity newWorkflow = (WorkflowEntity) workflowRes.getUserData();

                liteWorkflow.setWorkflow(newWorkflow);
                liteWorkflow.setWorks(newWorks);

                // 作成した情報を元に、戻り値のURIを作成する。
                URI uri = new URI(new StringBuilder("workflow/").append(newWorkflow.getWorkflowId()).toString());
                return Response.created(uri).entity(ResponseEntity.success().uri(uri).userData(liteWorkflow)).build();
            } else {
                return Response.serverError().entity(workflowRes).build();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * [Lite] 工程順と工程をまとめて更新する。
     *
     * @param liteWorkflow [Lite] 工程順・工程情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Path("lite")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response updateLiteWorkflow(LiteWorkflow liteWorkflow, @QueryParam("authId") Long authId) {
        logger.info("updateLiteWorkflow: {}, authId={}", liteWorkflow, authId);
        try {
            if (Objects.isNull(liteWorkflow.getWorkflow())
                    || Objects.isNull(liteWorkflow.getWorks())
                    || liteWorkflow.getWorks().isEmpty()) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            WorkflowEntity workflow = liteWorkflow.getWorkflow();

            // 排他用バージョンを確認する。
            WorkflowEntity target = super.find(workflow.getWorkflowId());
            if (!target.getVerInfo().equals(workflow.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO)).build();
            }

            // 版数がnullの場合は「1」とする。(「版数」実装前のデータ)
            if (Objects.isNull(workflow.getWorkflowRev())) {
                workflow.setWorkflowRev(1);
            }

            // 工程順名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.checkUpdateByWorkflowName", Long.class);
            query.setParameter("workflowId", workflow.getWorkflowId());
            query.setParameter("workflowName", workflow.getWorkflowName());
            query.setParameter("workflowRev", workflow.getWorkflowRev());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP)).build();
            }

            Date now = new Date();

            SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date baseDate = datetimeFormat.parse("1970/01/01 00:00:00");;

            String diaglam = workflow.getWorkflowDiaglam();

            liteWorkflow.getWorks().sort(Comparator.comparing(entity -> entity.getWorkId()));

            List<String> workNames = new ArrayList(); // 工程名リスト

            // 工程順ダイアグラムの仮IDを頭に「n」を付けたものにする。
            for (WorkEntity work : liteWorkflow.getWorks()) {
                String tempId = new StringBuilder("n").append(work.getWorkId()).toString();
                diaglam = this.replaceDiagramTaskId(diaglam, String.valueOf(work.getWorkId()), tempId, null);

                workNames.add(work.getWorkName());
            }

            // 工程順IDを指定して、現在の工程順情報を取得する。
            WorkflowEntity oldWorkflow = super.find(workflow.getWorkflowId());

            // 現在の工程順に属する工程情報一覧を取得する。
            List<WorkEntity> oldWorks =  this.workEntityFacade.findByWorlflowId(workflow.getWorkflowId(), null, null, authId);

            Map<String, Long> updateMap = new HashMap(); // 更新対象マップ
            List<Long> removeWorkIds = new ArrayList(); // 削除対象リスト

            for (WorkEntity oldWork : oldWorks) {
                String workName = oldWork.getWorkName().replaceFirst(oldWorkflow.getWorkflowName(), workflow.getWorkflowName());

                if (workNames.contains(workName)) {
                    // 更新対象マップに追加する。
                    updateMap.put(workName, oldWork.getWorkId());
                } else {
                    // 削除対象リストに追加する。
                    removeWorkIds.add(oldWork.getWorkId());
                }
            }

            List<WorkEntity> newWorks = new LinkedList();
            List<ConWorkflowWorkEntity> conWorkflowWorks = new ArrayList(); // 工程順・工程関連付け情報一覧(通常工程)

            int workflowOrder = 10001;
            for (WorkEntity work : liteWorkflow.getWorks()) {
                String tempId = new StringBuilder("n").append(work.getWorkId()).toString();

                Long workId = null;
                if (updateMap.containsKey(work.getWorkName())) {
                    workId = updateMap.get(work.getWorkName());
                }

                work.setWorkId(workId);
                work.setUpdatePersonId(authId); // 更新者
                work.setUpdateDatetime(now); // 更新日時

                ResponseEntity workRes;
                if (Objects.nonNull(workId)) {
                    // 工程を更新する。
                    workRes = this.workEntityFacade.updateWork(work, authId);
                } else {
                    // 工程を登録する。
                    workRes = this.workEntityFacade.addWork(work, true, authId);
                }

                if (!workRes.isSuccess()) {
                    return Response.serverError().entity(workRes).build();
                }

                WorkEntity newWork = (WorkEntity) workRes.getUserData();

                // 工程順ダイアグラムの仮IDを、正式な工程IDに差し替える。
                diaglam = this.replaceDiagramTaskId(diaglam, tempId, String.valueOf(newWork.getWorkId()), null);

                newWorks.add(newWork);

                // 工程順・工程関連付け情報を作成する。
                ConWorkflowWorkEntity conWork = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, newWork.getWorkId(), false, workflowOrder, baseDate, baseDate);
                conWorkflowWorks.add(conWork);

                if (Objects.equals(workflow.getSchedulePolicy(), SchedulePolicyEnum.PriorityParallel)) {
                    workflowOrder++;
                } else {
                    workflowOrder += 10000;
                }
            }

            workflow.setWorkflowDiaglam(diaglam); // 工程順ダイアグラム
            workflow.setUpdatePersonId(authId); // 更新者
            workflow.setUpdateDatetime(now); // 更新日時

            workflow.setConWorkflowWorkCollection(conWorkflowWorks); // 工程順・工程関連付け情報一覧(通常工程)

            // 工程順を更新する。
            ResponseEntity workflowRes = this.updateWorkflow(workflow, true, authId);
            if (!workflowRes.isSuccess()) {
                return Response.serverError().entity(workflowRes).build();
            }

            // 不要になった工程を削除する。(工程順の更新より前に削除すると論理削除になるため、工程順の更新後に処理して物理削除処理されるようにする)
            for (Long workId : removeWorkIds) {
                ResponseEntity workRes = this.workEntityFacade.removeWork(workId, true, authId);
                if (!workRes.isSuccess()) {
                    return Response.serverError().entity(workRes).build();
                }
            }

            liteWorkflow.setWorkflow((WorkflowEntity) workflowRes.getUserData());
            liteWorkflow.setWorks(newWorks);

            return Response.ok().entity(ResponseEntity.success().userData(liteWorkflow)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * [Lite] 工程順と工程をまとめて削除する。
     *
     * @param id 工程順ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("lite/{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response removeLiteWorkflow(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("removeLiteWorkflow: id={}, authId={}", id, authId);
        try {
            // 通常工程の工程関連付け情報一覧を取得する。
            List<ConWorkflowWorkEntity> conWorkflowWorks = this.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, id, false);

            // 工程順を削除する前に、工程ID一覧を抽出しておく。
            List<Long> workIds = conWorkflowWorks.stream()
                    .map(p -> p.getWorkId())
                    .collect(Collectors.toList());;

            // 工程順を削除する。
            ResponseEntity workflowRes = this.removeWorkflow(id, true, authId);
            if (!workflowRes.isSuccess()) {
                return Response.serverError().entity(workflowRes).build();
            }

            // 工程を削除する。(工程順より前に削除すると論理削除になるため、工程順の削除後に処理して物理削除処理されるようにする)
            for (Long workId : workIds) {
                ResponseEntity workRes = this.workEntityFacade.removeWork(workId, true, authId);
                if (!workRes.isSuccess()) {
                    return Response.serverError().entity(workRes).build();
                }
            }

            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * [Lite] 工程順と工程をまとめてコピーする。
     *
     * @param id 工程順ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @POST
    @Path("lite/copy/{id}")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response copyLiteWorkflow(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("copyLiteWorkflow: id={}, authId={}", id, authId);
        try {
            // 工程順IDを指定して、現在の工程順情報を取得する。
            WorkflowEntity srcWorkflow = this.find(id, false, authId);
            if (Objects.isNull(srcWorkflow)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
            }

            boolean isFind = true;
            StringBuilder name = new StringBuilder(srcWorkflow.getWorkflowName())
                    .append(SUFFIX_COPY);
            while (isFind) {
                // 工程順名の重複を確認する。(削除済も含む)
                TypedQuery<Long> checkQuery = this.em.createNamedQuery("WorkflowEntity.checkAddByWorkflowName", Long.class);
                checkQuery.setParameter("workflowName", name.toString());
                checkQuery.setParameter("workflowRev", 1);
                if (checkQuery.getSingleResult() > 0) {
                    name.append(SUFFIX_COPY);
                    continue;
                }
                isFind = false;
            }

            // 現在の工程順に属する工程情報一覧を取得する。
            List<WorkEntity> srcWorks =  this.workEntityFacade.findByWorlflowId(id, null, null, authId);

            // 工程順をコピーする。
            WorkflowEntity workflow = new WorkflowEntity(srcWorkflow);

            workflow.setWorkflowId(null);
            workflow.setWorkflowName(name.toString());
            workflow.setWorkflowRev(1); // 版数
            workflow.setConWorkflowSeparateworkCollection(new ArrayList());

            Date now = new Date();

            String diaglam = workflow.getWorkflowDiaglam();

            // 工程順ダイアグラムの仮IDを頭に「n」を付けたものにする。
            for (WorkEntity work : srcWorks) {
                String tempId = new StringBuilder("n").append(work.getWorkId()).toString();
                diaglam = this.replaceDiagramTaskId(diaglam, String.valueOf(work.getWorkId()), tempId, null);
            }

            List<WorkEntity> newWorks = new LinkedList();

            for (WorkEntity srcWork : srcWorks) {
                // 工程が属する階層の階層IDを取得する。
                Long parentId = this.findParentId(srcWork.getWorkId());
                if (Objects.nonNull(parentId)) {
                    srcWork.setParentId(parentId);
                }

                srcWork.setWorkSectionCollection(new ArrayList());

                // 工程をコピーする。
                WorkEntity work = new WorkEntity(srcWork);
                String workName = work.getWorkName().replaceFirst(srcWorkflow.getWorkflowName(), workflow.getWorkflowName());

                work.setWorkId(null);
                work.setWorkName(workName); // 工程名
                work.setUpdatePersonId(authId); // 更新者
                work.setUpdateDatetime(now); // 更新日時

                // 工程を登録する。
                ResponseEntity workRes = this.workEntityFacade.addWork(work, true, authId);
                if (!workRes.isSuccess()) {
                    return Response.serverError().entity(workRes).build();
                }

                WorkEntity newWork = (WorkEntity) workRes.getUserData();

                // 工程順ダイアグラムの仮IDを、正式な工程IDに差し替える。
                String tempId = new StringBuilder("n").append(srcWork.getWorkId()).toString();
                diaglam = this.replaceDiagramTaskId(diaglam, tempId, String.valueOf(newWork.getWorkId()), workName);

                newWorks.add(newWork);

                // 工程順・工程関連付け情報の新規登録のため、工程順IDをnullにして、正式な工程IDをセットする。
                ConWorkflowWorkEntity conWork = workflow.getConWorkflowWorkCollection().stream()
                        .filter(p -> Objects.equals(p.getWorkId(), srcWork.getWorkId()))
                        .findFirst()
                        .get();

                conWork.setWorkflowId(null);
                conWork.setWorkId(newWork.getWorkId());
            }

            workflow.setWorkflowDiaglam(diaglam); // 工程順ダイアグラム
            workflow.setUpdatePersonId(authId); // 更新者
            workflow.setUpdateDatetime(now); // 更新日時

            // 工程順を登録する。
            ResponseEntity workflowRes = this.addWorkflow(workflow, true, authId);
            if (workflowRes.isSuccess()) {
                WorkflowEntity newWorkflow = (WorkflowEntity) workflowRes.getUserData();

                LiteWorkflow liteWorkflow = new LiteWorkflow();
                liteWorkflow.setWorkflow(newWorkflow);
                liteWorkflow.setWorks(newWorks);

                // 作成した情報を元に、戻り値のURIを作成する。
                URI uri = new URI(new StringBuilder("workflow/").append(newWorkflow.getWorkflowId()).toString());
                return Response.created(uri).entity(ResponseEntity.success().uri(uri).userData(liteWorkflow)).build();
            } else {
                return Response.serverError().entity(workflowRes).build();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 工程順ダイアグラムの工程IDを更新する。
     *
     * @param workflowDiaglam 工程順ダイアグラム
     * @param oldId ID
     * @param newId 新しいID
     * @param newName 新しい工程名 (null: 変更なし)
     * @return 工程順ダイアグラム
     * @throws JAXBException
     */
    private String replaceDiagramTaskId(String workflowDiaglam, String oldId, String newId, String newName) throws JAXBException {
        BpmnModel bpmnModel = BpmnModeler.getModeler();

        BpmnDocument bpmn = BpmnDocument.unmarshal(workflowDiaglam);
        bpmnModel.createModel(bpmn);

        BpmnProcess bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        for (BpmnTask task : bpmnProcess.getTaskCollection()) {
            if (StringUtils.equals(task.getId(), oldId)) {
                task.setId(newId);
                if (Objects.nonNull(newName)) {
                    task.setName(newName);
                }
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
     * 工程順名を指定して作業中の工程カンバン数を取得する
     * @param param 工程カンバン名
     * @return 作業中の工程カンバン数
     */
    @POST
    @Path("/workingWKCount")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public String getWorkingWorkKanbanCount(ListWrapper<String> param)
    {
        List<String> workflowName = param.getList();
        logger.info("getWorkingWorkKanbanCount : {}", workflowName);
        TypedQuery<Long> query = this.em.createNamedQuery("WorkflowEntity.countWorkingWorkKanbanByWorkflowName", Long.class);
        query.setParameter("workflowName", workflowName);
        Long ret = query.getSingleResult();
        return String.valueOf(ret);
    }

    /**
     * 工程順情報の一括登録
     *
     * @param param  工程順情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + 工程名=>工程ID
     * @throws URISyntaxException
     */
    @POST
    @Path("all")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response addAll(ObjectParam<WorkflowEntity> param, @QueryParam("importSuspend") Boolean importSuspend, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("addAll: {}, importSuspend={}, authId={}", param, importSuspend, authId);
        final String workflowCategory = "workflow";
        final String kanbanCategory = "kanban";
        final Date now = new Date();

        try {
            // ---------------- データチェック処理
            List<WorkflowEntity> workflows = param.getList();
            if (workflows.stream()
                    .map(WorkflowEntity::getWorkflowName)
                    .anyMatch(StringUtils::isEmpty)) {
                logger.info("fail inputFile");
                // データに不備があるため失敗
                ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
                message.setAddInfo(workflowCategory);
                ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
            }

            // 工程順名リスト
            final List<String> workflowNameList = param.getList()
                    .stream()
                    .map(WorkflowEntity::getWorkflowName)
                    .collect(Collectors.toList());

            // 既に登録済みのワークフロー
            Map<String, List<WorkflowEntity>> registeredWorkflowMap = this.findByName(workflowNameList, authId)
                    .stream()
                    .collect(Collectors.groupingBy(WorkflowEntity::getWorkflowName));

            // 最新のリビジョンの工程順
            Map<String, WorkflowEntity> latestRegisteredWorkflowMap = registeredWorkflowMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(WorkflowEntity::getWorkflowName, Function.identity(), (a, b) -> a.getWorkflowRev() > b.getWorkflowRev() ? a : b));

            // 承認中のワークフローがある場合は異常終了
            if (latestRegisteredWorkflowMap
                    .values()
                    .stream()
                    .anyMatch(workflow -> ApprovalStatusEnum.APPLY.equals(workflow.getApprovalState()))) {
                logger.error("認証中の工程があるため異常");
                ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                MessageEntity message = new MessageEntity("%s", "key.DeleteErrProtectedData");
                ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                // ToDo メッセージ登録
                message.setAddInfo(workflowCategory);
                return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
            }

            // 工程順Id->工程順名への変換用マップ
            Map<Long, WorkflowEntity> workflowIDMap = registeredWorkflowMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(WorkflowEntity::getWorkflowId, Function.identity()));

            // 工程順が使用されているカンバンリスト
            List<KanbanEntity> usedKanbanList
                    = new ArrayList<>(kanbanEntityFacade
                    .findByWorkflowName(workflowNameList, authId)
                    .stream()
                    .collect(Collectors.toMap(
                            kanbanEntity -> kanbanEntity.getKanbanName() + "#######" + workflowIDMap.get(kanbanEntity.getWorkflowId()).getWorkflowName(),
                            Function.identity(),
                            (a, b) -> workflowIDMap.get(a.getWorkflowId()).getWorkflowRev() > workflowIDMap.get(b.getWorkflowId()).getWorkflowRev() ? a : b))
                    .values());

            // カンバンで使用されている工程順のリスト
            Set<String> workflowListUsedKanban = usedKanbanList
                    .stream()
                    .map(entity -> {
                        WorkflowEntity workflowEntity = workflowIDMap.get(entity.getWorkflowId());
                        return workflowEntity.getWorkflowName() +":"+workflowEntity.getWorkflowRev();
                    })
                    .collect(Collectors.toSet());

            // 工程順を変更するカンバンのリスト
            List<KanbanEntity> targetKanbanList = usedKanbanList
                    .stream()
                    .filter(entity->!KanbanStatusEnum.COMPLETION.equals(entity.getKanbanStatus()))
                    .filter(entity->!KanbanStatusEnum.DEFECT.equals(entity.getKanbanStatus()))
//                    .filter(entity->Objects.nonNull(entity.getActualStartTime()) || Objects.nonNull(entity.getActualCompTime()))
                    .collect(Collectors.toList());

            // 未完了のカンバンの工程カンバンリスト
            List<WorkKanbanEntity> workKanbanEntityList = workKanbanEntityFacade.findByKanbanId(
                    targetKanbanList
                            .stream()
                            .map(KanbanEntity::getKanbanId)
                            .collect(Collectors.toList())
                    , authId);

            // 工程カンバンマップ
            Map<Long, Map<Boolean, List<WorkKanbanEntity>>> workKanbanMap = workKanbanEntityList
                    .stream()
                    .collect(Collectors.groupingBy(WorkKanbanEntity::getKanbanId,
                            Collectors.groupingBy(WorkKanbanEntity::getSeparateWorkFlag)));

            // 実績のある工程カンバン
            Map<Long, Map<Boolean, List<WorkKanbanEntity>>> workedWorkKanbanMap = workKanbanEntityList
                    .stream()
                    .filter(entity -> Objects.nonNull(entity.getActualStartTime()) || Objects.nonNull(entity.getActualCompTime()))
                    .collect(Collectors.groupingBy(WorkKanbanEntity::getKanbanId,
                            Collectors.groupingBy(WorkKanbanEntity::getSeparateWorkFlag)));

            // 工程Id->工程名への変換用マップ
            Map<Long, String> workIDMap = workEntityFacade.find(
                    workKanbanEntityList.stream()
                            .map(WorkKanbanEntity::getWorkId)
                            .collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(WorkEntity::getWorkId, WorkEntity::getWorkName));

            this.em.clear();

            // 承認機能
            final boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
            final ApprovalStatusEnum approvalState = approvalLicense ? ApprovalStatusEnum.UNAPPROVED : ApprovalStatusEnum.FINAL_APPROVE;

            // ------------------------------- ここから登録処理 ------------------------
//            Map<String, String> resultMap = new HashMap<>();
            List<ResultResponse> retList = new ArrayList<>();

            logger.info("addAll: start register workflow");
            // ================ 工程順の登録

            for (WorkflowEntity workflowEntity : param.getList()) {
                workflowEntity.setApprovalState(approvalState);
                final String workflowName = workflowEntity.getWorkflowName();

                if (!registeredWorkflowMap.containsKey(workflowName)) {
                    // 未登録な工程順を登録
                    ResponseEntity res = (ResponseEntity) this.add(workflowEntity, authId).getEntity();
                    if (!ServerErrorTypeEnum.SUCCESS.equals(res.getErrorType())) {
                        logger.error(String.format("add failed [%s]", workflowEntity.getWorkflowName()));
                        ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                        MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
                        ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                        return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                    }
                    ResultResponse ret = ResultResponse.success().errorType(ServerErrorTypeEnum.SUCCESS);
                    MessageEntity message = new MessageEntity();
                    message.setAddInfo(workflowCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                // 登録済みの工程順の登録
                WorkflowEntity current = latestRegisteredWorkflowMap.get(workflowName);
                if (workflowListUsedKanban.contains(current.getWorkflowName() + ":" + current.getWorkflowRev())) {
                    // 最新のリビジョンの工程順がカンバンで使用中の場合は版の改定を行う
                    ResponseWorkflowEntity reviseRet = (ResponseWorkflowEntity) this.revise(current.getWorkflowId(), authId).getEntity();
                    if (!ServerErrorTypeEnum.SUCCESS.equals(reviseRet.getErrorType())) {
                        // 改定に失敗
                        logger.error(String.format("revise failed [%s]", current.getWorkflowName()));
                        ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                        MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
                        message.setAddInfo(workflowCategory);
                        ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                        return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                    }
                    current = reviseRet.getValue();
                }

                // 最新情報に更新
                workflowEntity.setWorkflowId(current.getWorkflowId());
                workflowEntity.setWorkflowRev(current.getWorkflowRev());
                workflowEntity.setVerInfo(current.getVerInfo());

                // 工程順を更新
                ResponseEntity updateRet = (ResponseEntity) this.update(workflowEntity, authId).getEntity();
                if (!ServerErrorTypeEnum.SUCCESS.equals(updateRet.getErrorType())) {
                    // 更新に失敗
                    logger.error(String.format("update failed [%s]", workflowEntity.getWorkflowName()));
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    MessageEntity message = new MessageEntity("%s", "key.FaildToProcess");
                    message.setAddInfo(workflowCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                }

                ResultResponse ret = ResultResponse.success().errorType(ServerErrorTypeEnum.SUCCESS);
                MessageEntity message = new MessageEntity();
                message.setAddInfo(workflowCategory);
                ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                retList.add(ret);
            }
            logger.info("addAll: end register workflow");
            this.em.flush();
            this.em.clear();

            if (!importSuspend || targetKanbanList.isEmpty()) {
                // カンバンへの取り込みを実施しない場合はここで終了
                return Response.ok().entity(new GenericEntity<List<ResultResponse>>(retList){}).build();
            }

            latestRegisteredWorkflowMap = param.getList().stream().collect(Collectors.toMap(WorkflowEntity::getWorkflowName, Function.identity()));

            // カンバン階層マップを作成
            Map<Long, Long> historyMap = kanbanEntityFacade.findParentEntityByKanbanIds(
                            targetKanbanList
                                    .stream()
                                    .map(KanbanEntity::getKanbanId)
                                    .collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.toMap(ConKanbanHierarchyEntity::getKanbanId, ConKanbanHierarchyEntity::getKanbanHierarchyId));

            this.em.flush();
            this.em.clear();

            retList.clear();
            // ================ カンバンの登録
            logger.info("addAll: start register kanban {}", targetKanbanList.size());
            for (KanbanEntity kanbanEntity : targetKanbanList) {

                if (!kanbanEntity.getKanbanStatus().isKanbanUpdatableStatus) {
                    // ***************** カンバンのステータスが変更不可 -> THERE_START_NON_EDITABLE
                    logger.info("No Edit Status : kanbanName={}, kanbanStatus={}", kanbanEntity.getKanbanName(), kanbanEntity.getKanbanStatus());
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.THERE_START_NON_EDITABLE);
                    MessageEntity message = new MessageEntity("["+kanbanEntity.getKanbanName()+"] > カンバンが更新できないステータスです: %s", kanbanEntity.getKanbanStatus().getResourceKey());
                    message.setAddInfo(kanbanCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                if (workKanbanMap.computeIfAbsent(kanbanEntity.getKanbanId(), key->new HashMap<>()).values()
                        .stream()
                        .flatMap(Collection::stream)
                        .allMatch(l-> l.getSkipFlag() || KanbanStatusEnum.COMPLETION.equals(l.getKanbanStatus()))) {
                    logger.info("All WorkKanban Is Skip : kanbanName={}, kanbanStatus={}", kanbanEntity.getKanbanName(), kanbanEntity.getKanbanStatus());
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.THERE_START_NON_EDITABLE);
                    MessageEntity message = new MessageEntity("["+kanbanEntity.getKanbanName()+"] > カンバンが全て完了、又はスキップです: %s", kanbanEntity.getKanbanStatus().getResourceKey());
                    message.setAddInfo(kanbanCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                // 最新の工程順を取得
                final WorkflowEntity workflowEntity = latestRegisteredWorkflowMap.get(workflowIDMap.get(kanbanEntity.getWorkflowId()).getWorkflowName());

                // カンバンで使用する工程一覧
                Set<Long> workIds = workflowEntity.getConWorkflowWorkCollection()
                        .stream()
                        .map(ConWorkflowWorkEntity::getWorkId)
                        .collect(Collectors.toSet());

                // 実績がある工程が工程順に登録されているか?
                Optional<WorkKanbanEntity> optNotFoundWork =
                        workedWorkKanbanMap
                                .computeIfAbsent(kanbanEntity.getKanbanId(), key -> new HashMap<>())
                                .computeIfAbsent(Boolean.FALSE, key -> new ArrayList<>())
                                .stream()
                                .filter(workedKanbanEntity -> !workIds.contains(workedKanbanEntity.getWorkId()))
                                .findFirst();

                if (optNotFoundWork.isPresent()) {
                    logger.info("skip kanban name={}", kanbanEntity.getKanbanName());
                    MessageEntity message = new MessageEntity("[" + kanbanEntity.getKanbanName() + "] > %s [" + workIDMap.getOrDefault(optNotFoundWork.get().getWorkId(), String.valueOf(optNotFoundWork.get().getWorkId())) + "]", "key.import.skip.WorkNotRegistered");
                    message.setAddInfo(kanbanCategory);
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.NOTFOUND_WORK);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    retList.add(ret);
                    continue;
                }

                // カンバン作成
                final String tmpKanbanName = "__TEMP" + kanbanEntity.getKanbanName() + "__";
                final Long parentId = historyMap.get(kanbanEntity.getKanbanId());
                final Long workflowId = workflowEntity.getWorkflowId();
                Response response = null;
                if (kanbanEntity.getProductionType() == 1) {
                    KanbanCreateCondition condition = new KanbanCreateCondition(
                            tmpKanbanName,
                            workflowId,
                            parentId,
                            String.valueOf(kanbanEntity.getUpdatePersonId()),
                            true,
                            Objects.isNull(kanbanEntity.getLotQuantity()) ? 1 : kanbanEntity.getLotQuantity(),
                            Objects.isNull(kanbanEntity.getStartDatetime()) ? new Date() : kanbanEntity.getStartDatetime(),
                            null,
                            kanbanEntity.getProductionType());

                    response = kanbanEntityFacade.createKanban(condition, authId);
                } else {
                    // 一個流し生産 or ロット生産のカンバンを登録
                    KanbanEntity newKanban = new KanbanEntity();
                    newKanban.setKanbanName(tmpKanbanName);             // カンバン名
                    newKanban.setWorkflowId(workflowId);           // 工程順ID
                    newKanban.setParentId(parentId);                 // 階層ID
                    newKanban.setLotQuantity(Objects.isNull(kanbanEntity.getLotQuantity()) ? 1 : kanbanEntity.getLotQuantity());           // ロット数
                    newKanban.setStartDatetime(Objects.isNull(kanbanEntity.getStartDatetime()) ? new Date() : kanbanEntity.getStartDatetime());       // 開始日時
                    newKanban.setProductionType(kanbanEntity.getProductionType());     // 生産タイプ

                    try {
                        response = kanbanEntityFacade.add(newKanban, authId);
                    } catch (URISyntaxException ex) {
                        //////  ありあえない -> SERVER_FETAL
                        logger.info("ExceptionError : kanbanName={} ", tmpKanbanName);
                        logger.fatal(ex, ex);

                        ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                        MessageEntity message = new MessageEntity("["+tmpKanbanName+"]> %s", "key.FaildToProcess");
                        ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                        retList.add(ret);
                        continue;
                    }
                }
                ResponseEntity res = (ResponseEntity) response.getEntity();
                if (!res.isSuccess()) {
                    // 作成エラー
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    MessageEntity message = new MessageEntity("["+tmpKanbanName+"] > 一時カンバンの作成に失敗しました。既存の一時カンバンを削除して下さい。", "key.FaildToProcess");
                    message.setAddInfo(kanbanCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                }
                this.em.flush();

                // 作成した補充カンバンのカンバンIDを取得する。
                int pos = res.getUri().lastIndexOf("/");
                String idStr = res.getUri().substring(pos + 1);

                Long kanbanId = Long.valueOf(idStr);

                // 作成した補充カンバンを取得する。
                KanbanEntity kanban = kanbanEntityFacade.find(kanbanId, null);
                // サブカンバン名
                if (!StringUtils.isEmpty(kanban.getKanbanSubname())) {
                    kanban.setKanbanSubname(kanbanEntity.getKanbanSubname());
                }

                // 情報の移行
                kanban.setInterruptReasonId(kanbanEntity.getInterruptReasonId());  // 中断理由ID
                kanban.setDelayReasonId(kanbanEntity.getDelayReasonId()); // 遅延理由ID
                kanban.setActualStartTime(kanbanEntity.getActualStartTime()); // 開始日時
                kanban.setActualCompTime(kanbanEntity.getActualCompTime()); // 終了日時
                kanban.setModelName(kanbanEntity.getModelName());// モデル名
                kanban.setRepairNum(kanbanEntity.getRepairNum()); // 補修数
                kanban.setServiceInfo(kanbanEntity.getServiceInfo());// サービス情報
                kanban.setProductionNumber(kanbanEntity.getProductionNumber()); //製造番号
                kanban.setKanbanLabel(kanbanEntity.getKanbanLabel()); // ラベル
                kanban.setKanbanStatus(kanbanEntity.getKanbanStatus()); // カンバンステータス
                kanban.setUpdateDatetime(new Date()); // 更新日

                // 追加情報 (重複を削除)
                List<KanbanPropertyInfoEntity> kanbanPropertyInfoEntities
                        = new ArrayList<>(Stream.of(kanbanEntity, kanban)
                        .map(KanbanEntity::getKanbanAddInfo)
                        .map(item -> JsonUtils.jsonToObjects(item, KanbanPropertyInfoEntity[].class))
                        .map(ArrayList::new)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(KanbanPropertyInfoEntity::getKanbanPropertyName, Function.identity(), (oldVal, newVal) -> newVal, LinkedHashMap::new))
                        .values());
                kanban.setKanbanAddInfo(JsonUtils.objectsToJson(kanbanPropertyInfoEntities));


                // カンバンで使用されてい工程カンバンID一覧
                List<Long> workKanbanIds = Stream.of(kanban.getWorkKanbanCollection(), kanban.getSeparateworkKanbanCollection())
                        .flatMap(Collection::stream)
                        .map(WorkKanbanEntity::getWorkId)
                        .filter(workId -> !workIDMap.containsKey(workId))
                        .collect(Collectors.toList());

                // workIDMapの更新
                workEntityFacade.find(workKanbanIds)
                        .stream()
                        .peek(entity -> this.em.detach(entity))
                        .forEach(entry -> workIDMap.put(entry.getWorkId(), entry.getWorkName()));

                // 実績取得
                Map<Long, List<ActualResultEntity>> actualResultEntities = this.actualResultRest.find(Collections.singletonList(kanbanEntity.getKanbanId()), null, null)
                        .stream()
                        .collect(Collectors.groupingBy(ActualResultEntity::getWorkKanbanId));

                // 通常工程(非追加工程の場合)
                List<WorkKanbanEntity> workKanbanEntities = workKanbanMap
                        .computeIfAbsent(kanbanEntity.getKanbanId(), key -> new HashMap<>())
                        .computeIfAbsent(Boolean.FALSE, key -> new ArrayList<>());

                List<MessageEntity> msg = new ArrayList<>();
                // 工程カンバンの情報の移行
                for (WorkKanbanEntity workKanbanEntity : workKanbanEntities) {
                    // 同名工程 かつ シリアル番号が同じものを検索
                    Optional<WorkKanbanEntity> optWKan = kanban.getWorkKanbanCollection()
                            .stream()
                            .filter(workKanban -> StringUtils.equals(workIDMap.get(workKanban.getWorkId()), workIDMap.get(workKanbanEntity.getWorkId()))) // 工程名
                            .filter(workKanban -> Objects.equals(workKanban.getSerialNumber(), workKanbanEntity.getSerialNumber())) // シリアル番号
                            .findFirst();

                    optWKan.ifPresent(workKanban -> {
                        Set<Long> equipment = new HashSet<>(workKanbanEntityFacadeREST.getEquipmentCollection(workKanban.getWorkKanbanId()));
                        getEquipmentCollection(workKanbanEntity.getWorkKanbanId())
                                .stream()
                                .filter(equipmentId -> !equipment.contains(equipmentId))
                                .forEach(equipmentId -> {
                                    ConWorkkanbanEquipmentEntity con = new ConWorkkanbanEquipmentEntity(workKanban.getWorkKanbanId(), equipmentId);
                                    this.em.persist(con);
                                });

                        Set<Long> organization = new HashSet<>(workKanbanEntityFacadeREST.getOrganizationCollection(workKanban.getWorkKanbanId()));
                        getOrganizationCollection(workKanbanEntity.getWorkKanbanId())
                                .stream()
                                .filter(organizationId -> !organization.contains(organizationId))
                                .forEach(organizationId -> {
                                    ConWorkkanbanOrganizationEntity con = new ConWorkkanbanOrganizationEntity(workKanban.getWorkKanbanId(), organizationId);
                                    this.em.persist(con);
                                });

                        // 工程情報のコピー
                        copyWorkKanban(workKanbanEntity, workKanban);
                        // 実績情報を移動

                        List<ActualResultEntity> actualResultEntityList = actualResultEntities.computeIfAbsent(workKanbanEntity.getWorkKanbanId(), key -> new ArrayList<>());
                        actualResultEntityList.forEach(actualResultEntity -> {
                            actualResultEntity.setKanbanId(workKanban.getKanbanId());
                            actualResultEntity.setWorkflowId(workKanban.getWorkflowId());
                            actualResultEntity.setWorkKanbanId(workKanban.getWorkKanbanId());
                        });
                    });
                }

                // 追加工程カンバンの情報の移行
                // 移行元
                Map<String, List<WorkKanbanEntity>> oldWorkKanban = workKanbanMap
                        .computeIfAbsent(kanbanEntity.getKanbanId(), key -> new HashMap<>())
                        .computeIfAbsent(Boolean.TRUE, key -> new ArrayList<>())
                        .stream()
                        .collect(Collectors.groupingBy(entity -> workIDMap.get(entity.getWorkId())));

                // 移行先
                Map<String, List<WorkKanbanEntity>> newWorkKanban = kanban.getSeparateworkKanbanCollection()
                        .stream()
                        .collect(Collectors.groupingBy(entity -> workIDMap.get(entity.getWorkId())));

                for (Map.Entry<String, List<WorkKanbanEntity>> workKanbanEntry : oldWorkKanban.entrySet()) {
                    List<WorkKanbanEntity> toList = newWorkKanban.get(workKanbanEntry.getKey());
                    List<WorkKanbanEntity> fromList = workKanbanEntry.getValue();

                    if (Objects.isNull(toList) || toList.size() < fromList.size()) {
                        continue;
                    }
                    for (int n = 0; n < fromList.size(); ++n) {
                        WorkKanbanEntity from = fromList.get(n);
                        WorkKanbanEntity to = toList.get(n);

                        getEquipmentCollection(from.getWorkKanbanId())
                                .forEach(equipmentId-> {
                                    ConWorkkanbanEquipmentEntity con = new ConWorkkanbanEquipmentEntity(to.getWorkKanbanId(), equipmentId);
                                    this.em.persist(con);
                                });

                        getOrganizationCollection(from.getWorkKanbanId())
                                .forEach(organizationId-> {
                                    ConWorkkanbanOrganizationEntity con = new ConWorkkanbanOrganizationEntity(to.getWorkKanbanId(), organizationId);
                                    this.em.persist(con);
                                });

                        // 工程情報のコピー
                        copyWorkKanban(fromList.get(n), toList.get(n));

                        List<ActualResultEntity> actualResultEntityList = actualResultEntities.computeIfAbsent(from.getWorkKanbanId(), key -> new ArrayList<>());
                        actualResultEntityList.forEach(actualResultEntity -> {
                            actualResultEntity.setKanbanId(to.getKanbanId());
                            actualResultEntity.setWorkflowId(to.getWorkflowId());
                            actualResultEntity.setWorkKanbanId(to.getWorkKanbanId());
                        });
                    }
                }

                if(!kanbanEntityFacade.startWorkflow(kanban, workflowEntity, null)) {
                    this.kanbanEntityFacade.removeForced(kanban.getKanbanId(), authId);

                    // 作成エラー
                    logger.fatal("startWorkflow kanban fatal: kanbanName={}, workflowId={}", tmpKanbanName, workflowId);
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    MessageEntity message = new MessageEntity("[" + kanbanEntity.getKanbanName() + "] > カンバンの作成に失敗しました。");
                    message.setAddInfo(kanbanCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                }

                // カンバンを更新する。
                response = kanbanEntityFacade.update(kanban, authId);
                res = (ResponseEntity) response.getEntity();
                if (!res.isSuccess()) {
                    this.kanbanEntityFacade.removeForced(kanban.getKanbanId(), authId);
                    // 作成エラー
                    logger.fatal("update kanban fatal: kanbanName={}, workflowId={}", tmpKanbanName, workflowId);
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    MessageEntity message = new MessageEntity("[" + kanbanEntity.getKanbanName() + "] > カンバンの更新に失敗しました。");
                    message.setAddInfo(kanbanCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                }

                this.em.flush();
                this.em.clear();

                // 旧カンバンを削除
                response = this.kanbanEntityFacade.removeForced(kanbanEntity.getKanbanId(), authId);
                res = (ResponseEntity) response.getEntity();
                if (!res.isSuccess()) {
                    this.kanbanEntityFacade.removeForced(kanban.getKanbanId(), authId);
                    // 削除失敗
                    logger.fatal("update kanban fatal: kanbanName={}, workflowId={}", tmpKanbanName, workflowId);
                    ResultResponse ret = ResultResponse.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    MessageEntity message = new MessageEntity("[" + kanbanEntity.getKanbanName() + "] > カンバンの削除に失敗しました。");
                    message.setAddInfo(kanbanCategory);
                    ret.result(JsonUtils.objectsToJson(Collections.singletonList(message)));
                    return Response.serverError().entity(new GenericEntity<List<ResultResponse>>(Collections.singletonList(ret)){}).build();
                }

                // リネーム
                kanban = this.kanbanEntityFacade.find(kanban.getKanbanId(), authId);
                kanban.setKanbanName(kanbanEntity.getKanbanName());

                List<WorkKanbanEntity> workKanbanInfoEntityList
                        = Stream.of(kanban.getWorkKanbanCollection(), kanban.getSeparateworkKanbanCollection())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                Date startKanbanDate
                        = workKanbanInfoEntityList
                        .stream()
                        .map(WorkKanbanEntity::getStartDatetime)
                        .filter(Objects::nonNull)
                        .min(Comparator.comparing(Function.identity()))
                        .orElse(now);

                Date compKanbanDate
                        = workKanbanInfoEntityList
                        .stream()
                        .map(WorkKanbanEntity::getCompDatetime)
                        .filter(Objects::nonNull)
                        .max(Comparator.comparing(Function.identity()))
                        .orElse(startKanbanDate);

                if (startKanbanDate.after(compKanbanDate)) {
                    Date tmp = startKanbanDate;
                    startKanbanDate = compKanbanDate;
                    compKanbanDate = tmp;
                }

                kanban.setStartDatetime(startKanbanDate);
                kanban.setCompDatetime(compKanbanDate);

                this.em.merge(kanban);
                this.em.flush();

                ResultResponse ret = ResultResponse.success().errorType(ServerErrorTypeEnum.SUCCESS);
                MessageEntity message = new MessageEntity();
                message.setAddInfo(kanbanCategory);
                msg.add(0, message);
                ret.result(JsonUtils.objectsToJson(msg));
                retList.add(ret);
            }

            // 結果から戻り値を作成する
//            ResponseEntity res = ResponseEntity.success();
//            res.setUri(JsonUtils.mapToJson(resultMap));
//            return Response.ok().entity(res).build();

            return Response.ok().entity(new GenericEntity<List<ResultResponse>>(retList){}).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            this.em.flush();
            this.em.clear();
            logger.info("addAll end");
        }
    }

    /**
     * 工程順ダイアグラムの各IDを置き換える
     *
     * @param workflowDiaglam 工程順ダイアグラムXML
     * @param workIdMap 仮IDから実際IDへの工程マップ
     * @return IDの置き換わった工程順ダイアグラムXML
     * @throws Exception
     */
    private String relocateWorkflowDiagram(String workflowDiaglam, Map<Long, Long> workIdMap) throws Exception {
        BpmnModel bpmnModel = BpmnModeler.getModeler();

        BpmnDocument bpmn = BpmnDocument.unmarshal(workflowDiaglam);
        bpmnModel.createModel(bpmn);

        BpmnProcess bpmnProcess = bpmnModel.getBpmnDefinitions().getProcess();

        for (BpmnTask task : bpmnProcess.getTaskCollection()) {
            task.setId(workIdMap.get(Long.parseLong(task.getId())).toString());
        }

        for (BpmnSequenceFlow flow : bpmnProcess.getSequenceFlowCollection()) {
            String sourceRef = flow.getSourceRef();
            if (sourceRef.length() > 0 && sourceRef.charAt(0) >= '0' && sourceRef.charAt(0) <= '9') {
                flow.setSourceRef(workIdMap.get(Long.parseLong(sourceRef)).toString());
            }
            String targetRef = flow.getTargetRef();
            if (targetRef.length() > 0 && targetRef.charAt(0) >= '0' && targetRef.charAt(0) <= '9') {
                flow.setTargetRef(workIdMap.get(Long.parseLong(targetRef)).toString());
            }
        }

        return bpmn.marshal2();
    }

    /**
     * 工程順の各IDを置き換える
     *
     * @param workflow 工程順
     * @param workflowHierarchyIdMap 仮IDから実際IDへの工程順階層マップ
     * @param workflowIdMap 仮IDから実際IDへの工程順マップ
     * @param workIdMap 仮IDから実際IDへの工程マップ
     * @throws Exception
     */
    private void relocateWorkflow(WorkflowEntity workflow, Map<Long, Long> workflowHierarchyIdMap,
            Map<Long, Long> workflowIdMap, Map<Long, Long> workIdMap) throws Exception {

        workflow.setParentId(workflowHierarchyIdMap.get(workflow.getParentId()));
        workflow.setWorkflowId(workflowIdMap.get(workflow.getWorkflowId()));
        workflow.setWorkflowDiaglam(relocateWorkflowDiagram(workflow.getWorkflowDiaglam(), workIdMap));

        for (ConWorkflowWorkEntity con : workflow.getConWorkflowWorkCollection()) {
            con.setWorkflowId(workflowIdMap.get(con.getWorkflowId()));
            con.setWorkId(workIdMap.get(con.getWorkId()));
        }
    }

    /**
     * 工程の画像をコピーする
     *
     * @param workEntity 工程
     */
    private void copyWorkImages(WorkEntity workEntity) {
        for (WorkSectionEntity workSectionEntity: workEntity.getWorkSectionCollection()) {
            FileManager fileManager = FileManager.getInstance();

            // コピー元のファイル
            StringBuilder source = new StringBuilder();
            source.append(0);
            source.append(File.separator);
            source.append(workSectionEntity.getPhysicalName());

            // コピー先のファイル
            StringBuilder target = new StringBuilder();
            target.append(workEntity.getWorkId());
            target.append(File.separator);
            target.append(workSectionEntity.getPhysicalName());

            fileManager.copy(FileManager.Data.Manual, source.toString(), target.toString());
        }
    }

    /**
     * 作業パラメーターの画像をコピーする
     *
     * @param workParsEnt 作業パラメーター
     */
    private void copyWorkParameterImages(WorkParametersEntity workParsEnt) {
        WorkParameterEntity workParEnt = JsonUtils.jsonToObject(workParsEnt.getWorkParameter(), WorkParameterEntity.class);
        for (WorkParameterWorkEntity work : workParEnt.getWork()) {
            for (WorkParameterWorkSectionEntity workSection: work.getWorkSection()) {
                if (workSection.getPhysicalFileName() == null) {
                    continue;
                }
                FileManager fileManager = FileManager.getInstance();

                // コピー元のファイル
                StringBuilder source = new StringBuilder();
                source.append(0);
                source.append(File.separator);
                source.append(workSection.getPhysicalFileName());

                // コピー先のファイル
                StringBuilder target = new StringBuilder();
                target.append(work.getWorkId());
                target.append(File.separator);
                target.append(workSection.getPhysicalFileName());

                fileManager.copy(FileManager.Data.Manual, source.toString(), target.toString());
            }
        }
    }

    /**
     * 作業パラメーターの各IDを置き換える
     *
     * @param workParsEnt 作業パラメーター
     * @param workflowIdMap 仮IDから実際IDへの工程順マップ
     * @param workIdMap 仮IDから実際IDへの工程マップ
     */
    private void relocateWorkParametersEntity(WorkParametersEntity workParsEnt, Map<Long, Long> workflowIdMap, Map<Long, Long> workIdMap) {
        workParsEnt.setWorkflowId(workflowIdMap.get(workParsEnt.getWorkflowId()));

        WorkParameterEntity workParEnt = JsonUtils.jsonToObject(workParsEnt.getWorkParameter(), WorkParameterEntity.class);
        for (WorkParameterWorkEntity work : workParEnt.getWork()) {
            work.setWorkId(workIdMap.get(work.getWorkId()));
        }
        workParsEnt.setWorkParameter(JsonUtils.objectToJson(workParEnt));
    }

    /**
     * 一括インポート (Excel用)
     *
     * @param param インポート工程順
     * @param loginId ログインID (組織識別子名)
     * @return
     * @throws URISyntaxException
     */
    @POST
    @Path("import")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response importAll(ImportWorkflowEntity param, @QueryParam("loginId") String loginId) throws URISyntaxException {
        logger.info("importAll: {}, loginId={}", param, loginId);
        try {
            Response resp;
            ResponseEntity respEnt;

            Map<Long, Long> workflowIdMap = new HashMap<>();
            Map<Long, Long> workIdMap = new HashMap<>();
            Map<Long, Long> workflowHierarchyIdMap = new HashMap<>();
            Map<Long, Long> workHierarchyIdMap = new HashMap<>();

            if (StringUtils.isEmpty(loginId)) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            // 組織情報を取得
            OrganizationEntity organization = this.organizationFacade.findByName(loginId, null, null);
            if (Objects.isNull(organization.getOrganizationId())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_ORGANIZATION)).build();
            }
        
            // 工程階層を登録
            for (WorkHierarchyEntity workHierarchy : param.getWorkHierarchies()) {
                HierarchyEntity oldWorkHiererchy = hierarchyFacade.findHierarchyByName(HierarchyTypeEnum.WORK, workHierarchy.getHierarchyName());

                if (Objects.isNull(oldWorkHiererchy) || Objects.isNull(oldWorkHiererchy.getHierarchyId())) {
                    HierarchyEntity newWorkHierarchy = new HierarchyEntity(HierarchyTypeEnum.WORK, 0L, workHierarchy.getHierarchyName());
                    resp = hierarchyFacade.add(newWorkHierarchy, organization.getOrganizationId());
                    respEnt = (ResponseEntity) resp.getEntity();
                    if (!respEnt.isSuccess()) {
                        return resp;
                    }
                    workHierarchyIdMap.put(workHierarchy.getWorkHierarchyId(), newWorkHierarchy.getHierarchyId());

                } else {
                    // 階層にアクセスできるか
                    if (!hierarchyFacade.isHierarchyAccessible(HierarchyTypeEnum.WORK, oldWorkHiererchy.getHierarchyId(), organization.getOrganizationId())) {
                        return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOT_ACCESS_RESOURCE)).build();
                    }

                    workHierarchyIdMap.put(workHierarchy.getWorkHierarchyId(), oldWorkHiererchy.getHierarchyId());
                }
            }

            // 工程順階層を登録
            WorkflowHierarchyEntity workflowHierarchy = param.getWorkflowHierarchy();
            HierarchyEntity oldWorkflowHiererchy = this.hierarchyFacade.findHierarchyByName(HierarchyTypeEnum.WORKFLOW, workflowHierarchy.getHierarchyName());

            if (Objects.isNull(oldWorkflowHiererchy) || Objects.isNull(oldWorkflowHiererchy.getHierarchyId())) {
                HierarchyEntity newWorkflowHierarchy = new HierarchyEntity(HierarchyTypeEnum.WORKFLOW, 0L, workflowHierarchy.getHierarchyName());
                resp = this.hierarchyFacade.add(newWorkflowHierarchy, organization.getOrganizationId());
                respEnt = (ResponseEntity) resp.getEntity();
                if (!respEnt.isSuccess()) {
                    return resp;
                }
                workflowHierarchyIdMap.put(workflowHierarchy.getWorkflowHierarchyId(), newWorkflowHierarchy.getHierarchyId());

            } else {
                // 階層にアクセスできるか
                if (!this.hierarchyFacade.isHierarchyAccessible(HierarchyTypeEnum.WORKFLOW, oldWorkflowHiererchy.getHierarchyId(), organization.getOrganizationId())) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOT_ACCESS_RESOURCE)).build();
                }
                
                workflowHierarchyIdMap.put(workflowHierarchy.getWorkflowHierarchyId(), oldWorkflowHiererchy.getHierarchyId());
            }

            WorkflowEntity workflow = param.getWorkflow();
            WorkflowEntity oldWorkflow = this.findByName(workflow.getWorkflowName(), workflow.getWorkflowRev(), false, null, organization.getOrganizationId());

            if (Objects.nonNull(oldWorkflow) && Objects.nonNull(oldWorkflow.getWorkflowId())) {
                
                // 工程順を使用したカンバンが存在するか
                if (Integer.parseInt(this.existAssignedKanban(oldWorkflow.getWorkflowId(), false, null)) > 0) {
                    return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.LOCKED_RESOURCE)).build();
                }
                
                // 追加工程の関連付け情報
                List<ConWorkflowWorkEntity> list = oldWorkflow.getConWorkflowSeparateworkCollection().stream()
                        .map(o -> new ConWorkflowWorkEntity(o.getWorkKbn(), o))
                        .collect(Collectors.toList());

                // データの引き継ぎ
                workflow.setModelName(oldWorkflow.getModelName());
                workflow.setOpenTime(oldWorkflow.getOpenTime());
                workflow.setCloseTime(oldWorkflow.getCloseTime());
                workflow.setLedgerPath(oldWorkflow.getLedgerPath());
                workflow.setWorkflowAddInfo(oldWorkflow.getWorkflowAddInfo());
                workflow.setConWorkflowSeparateworkCollection(list);
                workflow.setVerInfo(oldWorkflow.getVerInfo());
                
                workflowIdMap.put(workflow.getWorkflowId(), oldWorkflow.getWorkflowId());
            } else {
                WorkflowEntity newWorkflow = new WorkflowEntity(workflow);
                newWorkflow.setParentId(workflowHierarchyIdMap.get(workflow.getParentId()));
                newWorkflow.setWorkflowDiaglam(null);
                newWorkflow.setConWorkflowWorkCollection(Collections.emptyList());
                resp = this.add(newWorkflow, organization.getOrganizationId());
                respEnt = (ResponseEntity) resp.getEntity();
                if (!respEnt.isSuccess()) {
                    return resp;
                }
                workflowIdMap.put(workflow.getWorkflowId(), newWorkflow.getWorkflowId());
            }

            // 工程登録
            Map<String, Long> workNameMap = new HashMap<>();
            for (WorkEntity work : param.getWorks()) {
                work.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
                workNameMap.put(work.getWorkName(), work.getWorkId());
            }

            // 工程階層ID をセットして、工程情報を追加
            param.getWorks().forEach(o -> o.setParentId(workHierarchyIdMap.get(o.getParentId())));
            resp = this.workEntityFacade.addAll(new ObjectParam<>(param.getWorks()), true, organization.getOrganizationId());

            param.getWorks().forEach(o -> this.copyWorkImages(o));

            String respIdMapJson = ((ResponseEntity) resp.getEntity()).getUri();
            Map<String, String> respIdMap = JsonUtils.jsonToMap(respIdMapJson);
            for (Map.Entry<String, String> it : respIdMap.entrySet()) {
                Long oldWorkId = workNameMap.get(it.getKey());
                workIdMap.put(oldWorkId, Long.parseLong(it.getValue()));
            }

            // 工程順登録
            this.relocateWorkflow(workflow, workflowHierarchyIdMap, workflowIdMap, workIdMap);
            workflow.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE); // 最終承認済

            resp = this.update(workflow, organization.getOrganizationId());
            respEnt = (ResponseEntity) resp.getEntity();
            if (!respEnt.isSuccess()) {
                return resp;
            }

            // 作業パラメーター登録
            Query query = em.createNamedQuery("WorkParametersEntity.deleteByWorkflowId");
            query.setParameter("workflowId", workflow.getWorkflowId());
            query.executeUpdate();

            for (WorkParametersEntity parameters : param.getWorkParameters()) {
                this.relocateWorkParametersEntity(parameters, workflowIdMap, workIdMap);
            }

            for (WorkParametersEntity parameters : param.getWorkParameters()) {
                this.copyWorkParameterImages(parameters);
            }

            for (WorkParametersEntity parameters : param.getWorkParameters()) {
                this.em.persist(parameters);
            }

            return resp;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();

        } finally {
            FileManager fileManager = FileManager.getInstance();
            if (param != null && param.getWorks() != null) {
                for (WorkEntity workEntity : param.getWorks()) {
                    if (workEntity.getWorkSectionCollection() == null) {
                        continue;
                    }
                    for (WorkSectionEntity workSectionEntity : workEntity.getWorkSectionCollection()) {
                        if (workSectionEntity.getPhysicalName() == null) {
                            continue;
                        }
                        StringBuilder source = new StringBuilder();
                        source.append(0);
                        source.append(File.separator);
                        source.append(workSectionEntity.getPhysicalName());

                        fileManager.remove(FileManager.Data.Manual, source.toString());
                    }
                }
            }
        }
        //return Response.ok().build();
    }

    /**
     * 作業パラメーターを反映する
     *
     * @param workEntity 工程
     * @param kanbanId カンバンID
     */
    @Lock(LockType.READ)
    public void applyWorkParameters(WorkEntity workEntity, Long kanbanId) {
        if (Objects.isNull(kanbanId)) {
            return;
        }
        KanbanEntity kanbanEntity = this.kanbanEntityFacade.findBasicInfo(kanbanId);
        if (Objects.isNull(kanbanEntity.getKanbanId())) {
            return;
        }
        Long workflowId = kanbanEntity.getWorkflowId();
        String modelName = kanbanEntity.getModelName();
        this.applyWorkParameters(workEntity, workflowId, modelName);
    }

    /**
     * 作業パラメーターを反映する
     *
     * @param workEntity 工程
     * @param workflowId 工程順ID
     * @param modelName モデル名 (品番)
     */
    @Lock(LockType.READ)
    public void applyWorkParameters(WorkEntity workEntity, Long workflowId, String modelName) {
        if (Objects.isNull(workflowId) || StringUtils.isEmpty(modelName) || ThreadUtils.isRunningUnderJUnit()) {
            return;
        }
        
        try {
            // 品番が部分一致する作業パラメータを抽出
            // PostgreSQL 独自の正規表現マッチ演算子「~*」を使用しています
            TypedQuery<WorkParametersEntity> query = this.em.createNamedQuery("WorkParametersEntity.findByItemNumber", WorkParametersEntity.class);
            query.setParameter(1, workflowId);
            query.setParameter(2, modelName);

            List<WorkParametersEntity> entities = query.getResultList();
            if (entities.isEmpty()) {
                logger.info("Not found work parameters: modelName={}", modelName);
                return;
            }

            Optional<WorkParametersEntity> opt = entities.stream()
                    .filter(o -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(o.getItemNumber(), modelName))
                    .findFirst();

            WorkParametersEntity workParameters = opt.isPresent() ? opt.get() : entities.get(0);
            
            logger.info("Apply work parameters: modelName={}, parameters={}", modelName, workParameters);
            
            WorkParameterEntity workParamEnt = JsonUtils.jsonToObject(workParameters.getWorkParameter(), WorkParameterEntity.class);
            this.applyWorkParameters(workEntity, workParamEnt);

        } catch (Exception ex) {
            logger.warn(ex);
        }
    }

    /**
     * 作業パラメーターを反映する
     *
     * @param workEntity 工程
     * @param workParameterEntity 作業パラメーター
     */
    @Lock(LockType.READ)
    private void applyWorkParameters(WorkEntity workEntity, WorkParameterEntity workParameterEntity) {
        for (WorkParameterWorkEntity wpWorkEnt : workParameterEntity.getWork()) {
            if (!Objects.equals(workEntity.getWorkId(), wpWorkEnt.getWorkId())) {
                continue;
            }

            Map<Integer, WorkSectionEntity> workSectionPageMap = new HashMap<>();
            for (WorkSectionEntity workSect: workEntity.getWorkSectionCollection()) {
                workSectionPageMap.put(workSect.getWorkSectionOrder(), workSect);
            }

            List<CheckInfoEntity> checkInfos = JsonUtils.jsonToObjects(workEntity.getWorkCheckInfo(), CheckInfoEntity[].class);
            Map<Integer, CheckInfoEntity> checkInfoDispMap = new HashMap<>();
            for (CheckInfoEntity checkInfo : checkInfos) {
                checkInfoDispMap.put(checkInfo.getDisp(), checkInfo);
            }

            if (wpWorkEnt.getTaktTime() != null) {
                workEntity.setTaktTime(wpWorkEnt.getTaktTime());
            }
            for (WorkParameterWorkSectionEntity wpWorkSectEnt : wpWorkEnt.getWorkSection()) {
                WorkSectionEntity workSectionEnt = workSectionPageMap.get(wpWorkSectEnt.getOrder());
                if (workSectionEnt != null) {
                    if (wpWorkSectEnt.getDocumentTitle() != null) {
                        workSectionEnt.setDocumentTitle(wpWorkSectEnt.getDocumentTitle());
                    }
                    if (wpWorkSectEnt.getFileName() != null) {
                        workSectionEnt.setFileName(wpWorkSectEnt.getFileName());
                    }
                    if (wpWorkSectEnt.getPhysicalFileName() != null) {
                        workSectionEnt.setPhysicalName(wpWorkSectEnt.getPhysicalFileName());
                    }
                }
                for (WorkParameterWorkCheckInfoEntity wpWorkCheckInfo: wpWorkSectEnt.getWorkCheckInfo()) {
                    CheckInfoEntity checkInfoEnt = checkInfoDispMap.get(wpWorkCheckInfo.getOrder());
                    if (checkInfoEnt == null) {
                        continue;
                    }
                    if (wpWorkCheckInfo.isHidden()) {
                        checkInfos.remove(checkInfoEnt);
                        checkInfoDispMap.remove(wpWorkCheckInfo.getOrder());
                        continue;
                    }
                    if (wpWorkCheckInfo.getKey() != null) {
                        checkInfoEnt.setKey(wpWorkCheckInfo.getKey());
                    }
                    if (wpWorkCheckInfo.getVal() != null) {
                        checkInfoEnt.setVal(wpWorkCheckInfo.getVal());
                    }
                    if (wpWorkCheckInfo.getMin() != null) {
                        checkInfoEnt.setMin(wpWorkCheckInfo.getMin());
                    }
                    if (wpWorkCheckInfo.getMax() != null) {
                        checkInfoEnt.setMax(wpWorkCheckInfo.getMax());
                    }
                }
            }
            workEntity.setWorkCheckInfo(JsonUtils.objectsToJson(checkInfos));
        }
    }

    static private void copyWorkKanban(WorkKanbanEntity from, WorkKanbanEntity to) {
        // startWorkflowのロジックを見直さないといけない
//        to.setImplementFlag(KanbanStatusEnum.COMPLETION.equals(from.getWorkStatus()) || KanbanStatusEnum.DEFECT.equals(from.getKanbanStatus())); // 実施フラグ
        to.setImplementFlag(from.getImplementFlag());
        to.setWorkStatus(from.getWorkStatus());
        to.setSkipFlag(from.getSkipFlag()); // スキップフラグ
        to.setStartDatetime(from.getStartDatetime()); // 開始予定日時
        to.setCompDatetime(from.getCompDatetime()); // 完了予定時間
        to.setTaktTime(from.getTaktTime()); // タクトタイム
        to.setSumTimes(from.getSumTimes()); //作業累積時間
        to.setWorkStatus(from.getWorkStatus()); // 工程ステータス
        to.setInterruptReasonId(from.getInterruptReasonId()); // 中断理由ID
        to.setDelayReasonId(from.getDelayReasonId()); // 遅延理由
        to.setSerialNumber(from.getSerialNumber()); // シリアル番号
        to.setActualStartTime(from.getActualStartTime()); // 開始日時
        to.setActualCompTime(from.getActualCompTime()); // 完了日時
        to.setActualNum1(from.getActualNum1()); // A品実施数
        to.setActualNum2(from.getActualNum2()); // B品実施数
        to.setActualNum3(from.getActualNum3()); // C品実施数
        to.setReworkNum(from.getReworkNum()); // やり直し回数
        to.setServiceInfo(from.getServiceInfo()); // サービス情報

        to.setNeedActualOutputFlag(from.getNeedActualOutputFlag()); // 要実績出力フラグ
        to.setActualOutputDatetime(from.getActualOutputDatetime()); // 実績出力日時
        to.setLastActualId(from.getLastActualId());

        // 追加情報
        List<AddInfoEntity> addInfoEntities
                = new ArrayList<>(Stream.of(from, to)
                .map(WorkKanbanEntity::getWorkKanbanAddInfo)
                .map(item -> JsonUtils.jsonToObjects(item, AddInfoEntity[].class))
                .map(ArrayList::new)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(AddInfoEntity::getKey, Function.identity(), (newVal, oldVal) -> newVal, LinkedHashMap::new))
                .values());

        to.setWorkKanbanAddInfo(JsonUtils.objectsToJson(addInfoEntities)); // 追加情報
    }

    @Override
    protected EntityManager getEntityManager () {
        return this.em;
    }

    public void setEntityManager (EntityManager em){
        this.em = em;
    }

    public void setWorkEntityFacadeREST (WorkEntityFacadeREST workEntityFacadeRest){
        this.workEntityFacade = workEntityFacadeRest;
    }

    public void setHierarchyEntityFacadeREST (HierarchyEntityFacadeREST hierarchyFacade){
        this.hierarchyFacade = hierarchyFacade;
    }

    public void setKanbanEntityFacadeREST (KanbanEntityFacadeREST kanbanEntityFacade){
        this.kanbanEntityFacade = kanbanEntityFacade;
    }

    public void setApprovalModel (ApprovalModel approvalModel){
        this.approvalModel = approvalModel;
    }

    /**
     * 工程カンバンIDを指定して、関連付けられた設備ID一覧を取得する。
     *
     * @param id 工程カンバンID
     * @return 設備ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getEquipmentCollection(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkkanbanEquipmentEntity.findEquipmentIdByWorkKanbanId", Long.class);
        query.setParameter("workKanbanId", id);
        return query.getResultList();
    }

    /**
     * 工程カンバンIDを指定して、関連付けられた組織ID一覧を取得する。
     *
     * @param id 工程カンバンID
     * @return 組織ID一覧
     */
    @Lock(LockType.READ)
    private List<Long> getOrganizationCollection(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("ConWorkkanbanOrganizationEntity.findOrganizationIdByWorkKanbanId", Long.class);
        query.setParameter("workKanbanId", id);
        return query.getResultList();
    }

    /**
     *
     */
    @Lock(LockType.READ)
    public List<WorkflowEntity> getReporterWorkflow() {
        try {
            TypedQuery<WorkflowEntity> query = this.em.createNamedQuery("WorkflowEntity.WorkflowEntity.findReporterWorkflow", WorkflowEntity.class);
            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

}
