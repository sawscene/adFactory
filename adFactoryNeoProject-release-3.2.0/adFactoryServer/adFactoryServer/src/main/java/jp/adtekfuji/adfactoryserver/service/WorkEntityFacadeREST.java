/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
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
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXB;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.entity.work.TraceOptionEntity;
import jp.adtekfuji.adFactory.entity.work.TraceSettingEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.ListWrapper;
import jp.adtekfuji.adfactoryserver.entity.ObjectParam;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ConHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkSectionEntity;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalModel;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程情報REST
 *
 * @author ke.yokoi
 */
@Stateless
@Path("work")
public class WorkEntityFacadeREST extends AbstractFacade<WorkEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @Inject
    private ApprovalModel approvalModel;

    @EJB
    private EquipmentEntityFacadeREST equipmentEntityFacadeREST;

    @EJB
    private WorkflowEntityFacadeREST workflowEntityFacadeREST;

    private final Logger logger = LogManager.getLogger();

    /**
     * コンストラクタ
     */
    public WorkEntityFacadeREST() {
        super(WorkEntity.class);
    }

    /**
     * 工程IDを指定して、工程情報を取得する。(基本情報のみ)
     *
     * @param id 工程ID
     * @return 工程情報
     */
    @Lock(LockType.READ)
    public WorkEntity findBasicInfo(Long id) {
        WorkEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new WorkEntity();
        }

        // 工程が属する階層の階層IDを取得する。
        Long parentId = this.findParentId(id);
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        return entity;
    }

    public WorkEntity find(Long id, Boolean withDevice, Boolean isGetLatestRev, Long authId) {
        return find(id, withDevice, null, isGetLatestRev, authId);
    }
    
    /**
     * 工程IDを指定して、工程情報を取得する。
     *
     * @param id 工程ID
     * @param withDevice デバイス情報を取得するか (true:取得する, false:取得しない)
     * @param kanbanId カンバンID (作業パラメーター反映のため, 任意)
     * @param isGetLatestRev 最新版数を取得する？ (true:取得する, false:取得しない)
     * @param authId 認証ID
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @ExecutionTimeLogging
    public WorkEntity find(@PathParam("id") Long id, @QueryParam("withDevice") Boolean withDevice,
            @QueryParam("kanbanId") Long kanbanId, @QueryParam("getlatestrev") Boolean isGetLatestRev,
            @QueryParam("authId") Long authId) {
        logger.info("find: id={}, withDevice={}, kanbanId={}, isGetLatestRev={}, authId={}", id, withDevice, kanbanId, isGetLatestRev, authId);

        WorkEntity entity = super.find(id);
        if (Objects.isNull(entity)) {
            return new WorkEntity();
        }
        
        // 工程が属する階層の階層IDを取得する。
        Long parentId = this.findParentId(id);
        if (Objects.nonNull(parentId)) {
            entity.setParentId(parentId);
        }

        // 工程セクション情報を取得してセットする。
        entity.setWorkSectionCollection(this.getWorkSections(id));

        if (Objects.nonNull(withDevice) && withDevice) {
            // 工程の検査情報のJSON文字列を検査情報一覧に変換する。
            List<CheckInfoEntity> checkInfos = JsonUtils.jsonToObjects(entity.getWorkCheckInfo(), CheckInfoEntity[].class);

            List<WorkPropertyCategoryEnum> categories = Arrays.asList(WorkPropertyCategoryEnum.MEASURE, WorkPropertyCategoryEnum.WORK, WorkPropertyCategoryEnum.INSPECTION);
            final Set<String> equipmentIdentifiers = new HashSet<>();
            for (CheckInfoEntity prop : checkInfos) {
                if (!categories.contains(prop.getCat())) {
                    continue;
                }

                final String option = prop.getOpt();
                if (Objects.nonNull(option)) {
                    // オプションはトレーサビリティのオプションボタンを押して初めて値が入る
                    // オプションボタンを押してないとき当然管理番号のチェックも入ってないからこのトレーサビリティの設備情報は不要
                    final TraceSettingEntity settingEntity = JAXB.unmarshal(new ByteArrayInputStream(option.getBytes(StandardCharsets.UTF_8)), TraceSettingEntity.class);

                    for (TraceOptionEntity optionEntity : settingEntity.getTraceOptions()) {
                        if (optionEntity.getKey().equals("REFERENCE_NUMBER")) {
                            equipmentIdentifiers.addAll(optionEntity.getValues());
                        }
                    }
                }
            }

            final List<EquipmentEntity> equips = this.equipmentEntityFacadeREST.findByNames(new ArrayList(equipmentIdentifiers), EquipmentTypeEnum.MEASURE, EquipmentTypeEnum.MANUFACTURE);

            entity.setDeviceCollection(equips);
        }

        if (Objects.nonNull(kanbanId)) {
            this.workflowEntityFacadeREST.applyWorkParameters(entity, kanbanId);
        }

        if (Objects.nonNull(isGetLatestRev) && isGetLatestRev) {
            // 最新版数 (削除済・未承認は除く)
            int latestRev = this.getLatestRev(entity.getWorkName(), true, true);
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
     * 指定した工程名の工程情報を取得する。
     *
     * @param name 工程名
     * @param rev 工程の版番
     * @param isGetLatestRev 最新版数を取得する？ (true:取得する, false:取得しない)
     * @param userId ユーザーID (組織ID)
     * @param authId 認証ID
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("name")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public WorkEntity findByName(@QueryParam("name") String name, @QueryParam("rev") Integer rev,
             @QueryParam("getlatestrev") Boolean isGetLatestRev, @QueryParam("user") Long userId, @QueryParam("authId") Long authId) {
        logger.info("findByName: name={}, rev={}, getlatestrev={}, userId={}, authId={}", name, rev, isGetLatestRev, userId, authId);

        TypedQuery<WorkEntity> query;
        if (Objects.nonNull(rev)) {
            // 工程名・版数を指定して、工程情報を取得する。(削除済は対象外)
            query = this.em.createNamedQuery("WorkEntity.findByNameAndRev", WorkEntity.class);
            query.setParameter("workRev", rev);
        } else {
            // 工程名を指定して、最終承認済の最新版数の工程を取得する。(削除済は対象外)
            query = this.em.createNamedQuery("WorkEntity.findLatestRevByName", WorkEntity.class);
        }

        query.setParameter("workName", name);

        try {
            WorkEntity entity = query.getSingleResult();

            // 工程が属する階層の階層IDを取得する。
            Long parentId = this.findParentId(entity.getWorkId());
            if (Objects.nonNull(parentId)) {
                entity.setParentId(parentId);
            }

            // 工程セクション情報を取得してセットする。
            entity.setWorkSectionCollection(this.getWorkSections(entity.getWorkId()));

            if (Objects.nonNull(isGetLatestRev) && isGetLatestRev) {
                // 最新版数 (削除済・未承認は除く)
                int latestRev = this.getLatestRev(entity.getWorkName(), true, true);
                entity.setLatestRev(latestRev);
            }

            // 承認機能ライセンスが有効な場合は申請情報も取得する。
            boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
            if (approvalLicense && Objects.nonNull(entity.getApprovalId())) {
                ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
                entity.setApproval(approval);
            }

            return entity;
        } catch (NoResultException ex) {
            logger.fatal(ex);
            return new WorkEntity();
        }
    }

    /**
     * 名前リストにて全ての最新の工程を取得する
     * @param param 工程リスト
     * @param authId
     * @return
     */
    @POST
    @Path("/name/all")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkEntity> findAllByName(ListWrapper<String> param, @QueryParam("authId") Long authId)
    {
        logger.info("findByName: name={}, authId={}", param, authId);
        try {
            if (param.getList().isEmpty()) {
                return new ArrayList<>();
            }

            java.sql.Array tagNamesArray = this.em.unwrap(Connection.class).createArrayOf("text", param.getList().toArray());

            TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findAllLatestRevByName", WorkEntity.class);
            query.setParameter(1, tagNamesArray);
            return query.getResultList();


        } catch (Exception ex) {
            logger.fatal(ex,ex);
            return new ArrayList<>();
        }
    }

    /**
     * 工程情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param authId 認証ID
     * @return 工程情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("range")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkEntity> findRange(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findRange: from={}, to={}, authId={}", from, to, authId);

        List<WorkEntity> entities;
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            entities = super.findRange(from, to);
        } else {
            entities = super.findAll();
        }

        // 承認機能ライセンス
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());

        for (WorkEntity entity : entities) {
            // 工程が属する階層の階層IDを取得する。
            Long parentId = this.findParentId(entity.getWorkId());
            if (Objects.nonNull(parentId)) {
                entity.setParentId(parentId);
            }

            // 工程セクション情報を取得してセットする。
            entity.setWorkSectionCollection(this.getWorkSections(entity.getWorkId()));

            // 承認機能ライセンスが有効な場合は申請情報も取得する。
            if (approvalLicense && Objects.nonNull(entity.getApprovalId())) {
                ApprovalEntity approval = this.approvalModel.findApproval(entity.getApprovalId());
                entity.setApproval(approval);
            }
        }

        return entities;
    }

    /**
     * 工程情報の件数を取得する。
     *
     * @param authId 認証ID
     * @return 工程情報の件数
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
     * 工程情報を登録する。
     *
     * @param entity 工程情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     * @throws URISyntaxException
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(WorkEntity entity, @QueryParam("authId") Long authId) throws URISyntaxException {
        ResponseEntity response = this.addWork(entity, false, authId);
        if (response.isSuccess()) {
            WorkEntity newEntity = (WorkEntity) response.getUserData();

            // 作成した情報を元に、戻り値のURIを作成する。
            URI uri = new URI(new StringBuilder("work/").append(newEntity.getWorkId()).toString());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 工程情報を登録する。
     *
     * @param entity 工程情報
     * @param isLite adFactory Lite か？
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity addWork(WorkEntity entity, boolean isLite, Long authId) {
        logger.info("addWork: {}, isLite={}, authId={}", entity, isLite, authId);
        try {
            // 版数が未指定の場合は「1」とする。
            if (Objects.isNull(entity.getWorkRev())) {
                entity.setWorkRev(1);
            }

            // 工程名の重複を確認する。(削除済も含む)
            TypedQuery<Long> query = this.em.createNamedQuery("WorkEntity.checkAddByWorkName", Long.class);
            query.setParameter("workName", entity.getWorkName());
            query.setParameter("workRev", entity.getWorkRev());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
            }

            if (isLite) {
                entity.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
            } else {
                // 承認機能ライセンスが有効な場合は承認状態を「未承認」に、無効な場合は「最終承認済」にする。
                boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
                if (approvalLicense) {
                    entity.setApprovalState(ApprovalStatusEnum.UNAPPROVED);
                } else {
                    entity.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
                }
            }

            // 工程情報を登録する。
            super.create(entity);
            this.em.flush();

            // 工程の階層関連付け情報を登録する。
            this.addHierarchy(entity);
            // 工程セクション情報を登録する。
            this.addWorkSections(entity);

            // FTPサーバーのデータフォルダに、ドキュメント用のフォルダを作成する。
            FileManager fileManager = FileManager.getInstance();
            fileManager.createDirectory(FileManager.Data.Manual, entity.getWorkId().toString());

            return ResponseEntity.success().userData(entity);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 工程情報をコピーする。
     *
     * @param id 工程ID
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
        logger.info("copy: {}, authId={}", id, authId);

        WorkEntity entity = this.find(id, false, false, authId);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        boolean isFind = true;
        StringBuilder name = new StringBuilder(entity.getWorkName())
                .append(SUFFIX_COPY);
        while (isFind) {
            // 工程名の重複を確認する。(削除済も含む)
            TypedQuery<Long> checkQuery = this.em.createNamedQuery("WorkEntity.checkAddByWorkName", Long.class);
            checkQuery.setParameter("workName", name.toString());
            checkQuery.setParameter("workRev", 1);
            if (checkQuery.getSingleResult() > 0) {
                name.append(SUFFIX_COPY);
                continue;
            }
            isFind = false;
        }

        WorkEntity newEntity = new WorkEntity(entity);
        newEntity.setWorkName(name.toString());
        newEntity.setWorkRev(1);// 版数

        // 新規追加する。
        this.add(newEntity, authId);

        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
        if (approvalLicense) {
            newEntity.setLatestRev(0);
        } else {
            newEntity.setLatestRev(1);
        }

        // ドキュメントファイルを複製する。
        if (Objects.nonNull(entity.getWorkSectionCollection())) {
            FileManager fileManager = FileManager.getInstance();
            for (WorkSectionEntity section : newEntity.getWorkSectionCollection()) {
                if (section.hasDocument()) {
                    // コピー元のファイル
                    StringBuilder source = new StringBuilder();
                    source.append(entity.getWorkId());
                    source.append(File.separator);
                    source.append(section.getPhysicalName());

                    // コピー先のファイル
                    StringBuilder target = new StringBuilder();
                    target.append(newEntity.getWorkId());
                    target.append(File.separator);
                    target.append(section.getPhysicalName());

                    fileManager.copy(FileManager.Data.Manual, source.toString(), target.toString());
                }
            }
        }

        // 作成した情報を元に、戻り値のURIを作成する。
        URI uri = new URI(new StringBuilder("work/").append(newEntity.getWorkId()).toString());
        return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
    }

    /**
     * 工程情報を更新する。
     *
     * @param entity 工程情報
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response update(WorkEntity entity, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.updateWork(entity, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 工程情報を更新する。
     *
     * @param entity 工程情報
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity updateWork(WorkEntity entity, Long authId) {
        logger.info("updateWork: {}, authId={}", entity, authId);
        try {
            // 排他用バージョンを確認する。
            WorkEntity target = super.find(entity.getWorkId());
            if (!target.getVerInfo().equals(entity.getVerInfo())) {
                // 現在の排他用バージョンと異なる場合、排他用バージョンが異なる事を通知する。
                return ResponseEntity.failed(ServerErrorTypeEnum.DIFFERENT_VER_INFO);
            }

            // 版数がnullの場合は「1」とする。(「版数」実装前のデータ)
            if (Objects.isNull(entity.getWorkRev())) {
                entity.setWorkRev(1);
            }

            // 工程名の重複を確認する。
            TypedQuery<Long> query = this.em.createNamedQuery("WorkEntity.checkUpdateByWorkName", Long.class);
            query.setParameter("workId", entity.getWorkId());
            query.setParameter("workName", entity.getWorkName());
            query.setParameter("workRev", entity.getWorkRev());
            if (query.getSingleResult() > 0) {
                // 該当するものがあった場合、重複を通知する。
                return ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
            }

            // 楽観的ロックをかける。
            this.em.lock(target, LockModeType.OPTIMISTIC);

            // 工程情報を更新する。
            super.edit(entity);

            // 工程の階層関連付け情報を更新する。
            this.registHierarchy(entity);

            // 工程セクション情報を更新する。
            this.removeWorkSections(entity.getWorkId(), entity.getWorkSectionCollection());
            this.addWorkSections(entity);

            return ResponseEntity.success().userData(entity);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 指定した工程IDの工程情報を削除する。
     *
     * @param id 工程ID
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + エラー原因(ServerErrorTypeEnum)
     */
    @DELETE
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        ResponseEntity response = this.removeWork(id, false, authId);
        if (response.isSuccess()) {
            return Response.ok().entity(response).build();
        } else {
            return Response.serverError().entity(response).build();
        }
    }

    /**
     * 指定した工程IDの工程情報を削除する。
     *
     * @param id 工程ID
     * @param isLite dFactory Lite か？
     * @param authId 認証ID
     * @return 結果
     */
    public ResponseEntity removeWork(Long id, boolean isLite, Long authId) {
        logger.info("removeWork: {}, isLite={}, authId={}", id, isLite, authId);

        // 工程IDを指定して、工程情報を取得する。
        WorkEntity entity = super.find(id);
        if (Objects.isNull(entity.getWorkId())) {
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

        FileManager fileManager = FileManager.getInstance();

        // 工程IDを指定して、工程順工程関連付け情報の件数を取得する。
        TypedQuery<Long> queryWorkflow = this.em.createNamedQuery("WorkEntity.countWorkflowWorkAssociation", Long.class);
        queryWorkflow.setParameter("workId", id);
        Long numWorkflow = queryWorkflow.getSingleResult();

        // 工程IDを指定して、工程カンバン情報の件数を取得する。
        TypedQuery<Long> queryKanban = this.em.createNamedQuery("WorkEntity.countKanbanAssociation", Long.class);
        queryKanban.setParameter("workId", id);
        Long numKanban = queryKanban.getSingleResult();

        // 関連付けが無い場合、完全に削除する。
        if (numWorkflow == 0 && numKanban == 0) {
            logger.info("remove-real:{}", id);

            // 工程セクション情報を削除する。
            this.removeWorkSections(id, null);
            // ドキュメントフォルダを削除する。
            fileManager.remove(FileManager.Data.Manual, id.toString());

            // 工程の階層関連付け情報を削除する。
            this.removeHierarchy(id);

            // 工程情報をを削除する。
            super.remove(entity);
            return ResponseEntity.success();
        }

        // 関連付けがある場合、削除フラグで論理削除する。

        // ドキュメントファイルを削除する。
        if (Objects.nonNull(entity.getWorkSectionCollection())) {
            for (WorkSectionEntity section : entity.getWorkSectionCollection()) {
                if (section.hasDocument()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(id);
                    sb.append(File.separator);
                    sb.append(section.getPhysicalName());
                    fileManager.remove(FileManager.Data.Manual, sb.toString());
                }
            }
        }

        // ドキュメントフォルダを削除する。
        fileManager.remove(FileManager.Data.Manual, id.toString());

        // 削除済の名称に変更する。
        boolean isFind = true;
        int num = 1;
        String baseName = new StringBuilder(entity.getWorkName())
                .append(SUFFIX_REMOVE)
                .toString();
        String name = new StringBuilder(baseName)
                .append(num)
                .toString();
        while (isFind) {
            // 工程名の重複を確認する。
            TypedQuery<Long> checkQuery = this.em.createNamedQuery("WorkEntity.checkUpdateByWorkName", Long.class);
            checkQuery.setParameter("workId", id);
            checkQuery.setParameter("workName", name);
            if (Objects.nonNull(entity.getWorkRev())) {
                checkQuery.setParameter("workRev", entity.getWorkRev());
            } else {
                checkQuery.setParameter("workRev", 1);
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

        entity.setWorkName(name);
        entity.setRemoveFlag(true);

        // 工程情報を更新する。
        super.edit(entity);

        // 工程の階層関連付け情報を削除する。
        this.removeHierarchy(id);

        return ResponseEntity.success();
    }

    /**
     * 工程順に属する工程の個数を取得する。
     *
     * @param workflowId 工程順ID
     * @param authId 認証ID
     * @return 工程の個数
     */
    @Lock(LockType.READ)
    @GET
    @Path("workflow/count")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String countByWorlflowId(@QueryParam("workflowId") Long workflowId, @QueryParam("authId") Long authId) {
        logger.info("countByWorlflowId: workflowId={}, authId={}", workflowId, authId);
        try {
            // 工程順IDを指定して、工程順に属する工程の件数を取得する。
            TypedQuery<Long> query = this.em.createNamedQuery("WorkEntity.countByWorkflowId", Long.class);
            query.setParameter("workflowId", workflowId);

            return String.valueOf(query.getSingleResult());
        } catch (NoResultException ex) {
            logger.fatal(ex, ex);
            return null;
        } finally {
            logger.info("countByWorlflowId end:{}", workflowId);
        }
    }

    /**
     * 工程順に属する工程情報一覧を取得する。
     *
     * @param workflowId 工程順ID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 工程情報一覧
     */
    @Lock(LockType.READ)
    @GET
    @Path("workflow")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkEntity> findByWorlflowId(@QueryParam("workflowId") Long workflowId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findByWorlflowId: workflowId={}, from={}, to={}, authId={}", workflowId, from, to, authId);
        try {
            TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findByWorkflowId", WorkEntity.class);
            query.setParameter("workflowId", workflowId);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (NoResultException ex) {
            logger.fatal(ex, ex);
            return null;
        } finally {
            logger.info("findByWorlflowId end:{}", workflowId);
        }
    }
   
    /**
     * データベースから工程セクション情報を取得する。
     *
     * @param id
     * @return
     */
    @Lock(LockType.READ)
    public List<WorkSectionEntity> getWorkSections(Long id) {
        // 工程IDを指定して、工程セクション情報を取得する。
        TypedQuery<WorkSectionEntity> query = this.em.createNamedQuery("WorkSectionEntity.findByWorkId", WorkSectionEntity.class);
        query.setParameter("workId", id);
        return query.getResultList();
    }

    /**
     * データベースから工程セクション情報を削除する。
     *
     * @param id 工程ID
     * @param newSections 置き換える工程シート情報
     */
    private void removeWorkSections(Long id, List<WorkSectionEntity> newSections) {
        // ドキュメントファイルを削除する。
        FileManager fileManager = FileManager.getInstance();
        List<String> fileNames = fileManager.listFileName(FileManager.Data.Manual, id.toString());
        for (String fileName : fileNames) {
            // 使用するドキュメントファイルは削除しない
            if (Objects.nonNull(newSections) && newSections.stream().filter(o -> Objects.equals(o.getPhysicalName(), fileName)).count() > 0) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(id);
            sb.append(File.separator);
            sb.append(fileName);
            fileManager.remove(FileManager.Data.Manual, sb.toString());
        }

        // 工程IDを指定して、工程セクション情報を削除する。
        Query query = this.em.createNamedQuery("WorkSectionEntity.removeByWorkId");
        query.setParameter("workId", id);
        query.executeUpdate();
    }

    /**
     * データベースに工程セクション情報を登録する。
     *
     * @param entity 工程情報
     */
    private void addWorkSections(WorkEntity entity) {
        if (Objects.nonNull(entity.getWorkSectionCollection())) {
            for (WorkSectionEntity section : entity.getWorkSectionCollection()) {
                section.setWorkId(entity.getWorkId());
                this.em.persist(section);
            }
        }
    }

    /**
     * 工程IDを指定して、工程が属する階層の階層IDを取得する。
     *
     * @param id 工程ID
     * @return 工程階層ID
     */
    @Lock(LockType.READ)
    private Long findParentId(Long id) {
        TypedQuery<Long> query = this.em.createNamedQuery("ConHierarchyEntity.findHierarchyId", Long.class);
        query.setParameter("hierarchyType", HierarchyTypeEnum.WORK);
        query.setParameter("workWorkflowId", id);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            // 親階層が設定されていない。(論理削除済の工程等)
            return null;
        }
    }

    /**
     * 指定したIDの工程の階層関連付け情報を削除する。
     *
     * @param id 工程ID
     */
    private void removeHierarchy(Long id) {
        // 階層種別と工程・工程順IDを指定して、階層関連付け情報を削除する。
        Query query = this.em.createNamedQuery("ConHierarchyEntity.removeByTypeAndMstId");
        query.setParameter("hierarchyType", HierarchyTypeEnum.WORK);
        query.setParameter("workWorkflowId", id);

        query.executeUpdate();
    }

    /**
     * 工程の階層関連付け情報を登録する。
     *
     * @param entity 工程情報
     */
    private void addHierarchy(WorkEntity entity) {
        ConHierarchyEntity hierarchy = new ConHierarchyEntity(entity.getParentId(), entity.getWorkId(), HierarchyTypeEnum.WORK);
        this.em.persist(hierarchy);
    }

    /**
     * 工程の階層関連付け情報を登録または更新する。
     *
     * @param entity 工程情報
     * @return
     */
    private boolean registHierarchy(WorkEntity entity) {
        boolean result = false;

        // 階層種別と工程・工程順IDを指定して、階層関連付け情報の件数を取得する。
        TypedQuery<Long> countQuery = this.em.createNamedQuery("ConHierarchyEntity.countByTypeAndMstId", Long.class);
        countQuery.setParameter("hierarchyType", HierarchyTypeEnum.WORK);
        countQuery.setParameter("workWorkflowId", entity.getWorkId());

        Long count = countQuery.getSingleResult();
        if (count == 0) {
            // 工程の階層関連付け情報を新規登録する。
            ConHierarchyEntity hierarchy = new ConHierarchyEntity(entity.getParentId(), entity.getWorkId(), HierarchyTypeEnum.WORK);
            this.em.persist(hierarchy);
            result = true;
        } else {
            // 工程の階層関連付け情報を更新する。
            Query updateQuery = this.em.createNamedQuery("ConHierarchyEntity.updateHierarchyId");
            updateQuery.setParameter("hierarchyId", entity.getParentId());
            updateQuery.setParameter("hierarchyType", HierarchyTypeEnum.WORK);
            updateQuery.setParameter("workWorkflowId", entity.getWorkId());

            int updateCount = updateQuery.executeUpdate();
            if (updateCount == 1) {
                result = true;
            }
        }

        return result;
    }

   /**
     * 工程情報の一括登録
     *
     * @param param 工程情報
     * @param isUpdateTaktTime タクトタイムをアップデートするか
     * @param authId 認証ID
     * @return 成功：200/失敗：500 + 工程名=>工程ID
     * @throws URISyntaxException
     */
    @POST
    @Path("all")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response addAll(ObjectParam<WorkEntity> param, @QueryParam("isUpdateTaktTime") Boolean isUpdateTaktTime, @QueryParam("authId") Long authId) throws URISyntaxException {
        logger.info("addAll: {}, authId={}", param, authId);
        boolean isShift = Boolean.parseBoolean(FileManager.getInstance().getSystemProperties().getProperty("work_reschedule_isShift", "true"));
        List<WorkEntity> works = param.getList();

        if (works.stream()
                .map(WorkEntity::getWorkName)
                .anyMatch(StringUtils::isEmpty)) {
            // データに不備があるため失敗
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        try {
            Map<String, String> resultMap = new HashMap<>();

            for (WorkEntity work : works) {
                ResponseEntity res;
                WorkEntity currentWork = null;
                if (Objects.nonNull(work.getParentId())) {
                    // 工程を追加
                    res = (ResponseEntity)this.add(work, authId).getEntity();
                } else {
                    // 既に存在する
                    currentWork = this.findByName(work.getWorkName(), null, true, null, authId);
                    if (Objects.isNull(currentWork.getWorkId())) {
                        continue;
                    }
                    work.setParentId(currentWork.getParentId());
                    res = ResponseEntity.failed(ServerErrorTypeEnum.IDENTNAME_OVERLAP);
                }


                Long workId;
                if (ServerErrorTypeEnum.SUCCESS.equals(res.getErrorType())) {
                    // 追加に成功
                    workId = Long.parseLong(res.getUri().substring(res.getUri().lastIndexOf("/") + 1));
                } else if (ServerErrorTypeEnum.IDENTNAME_OVERLAP.equals(res.getErrorType())) {
                    // 既に存在するので更新する
                    if (Objects.isNull(currentWork)) {
                        currentWork = this.findByName(work.getWorkName(),null, true, null, authId);
                    }
                    em.clear();

                    Integer oldTactTime = currentWork.getTaktTime();
                    work.setWorkId(currentWork.getWorkId());
                    work.setWorkRev(currentWork.getWorkRev());
                    work.setVerInfo(currentWork.getVerInfo());
                    work.setApprovalState(currentWork.getApprovalState());

                    if (Objects.isNull(work.getContent())) {
                        work.setContent(currentWork.getContent());
                    }
                    if (Objects.isNull(work.getContentType())) {
                        work.setContentType(currentWork.getContentType());
                    }
                    if (Objects.isNull(work.getFontColor())) {
                        work.setFontColor(currentWork.getFontColor());
                    }
                    if (Objects.isNull(work.getBackColor())) {
                        work.setBackColor(currentWork.getBackColor());
                    }
                    if (Objects.isNull(work.getUseParts())) {
                        work.setUseParts(currentWork.getUseParts());
                    }
                    if (Objects.isNull(work.getWorkNumber())) {
                        work.setWorkNumber(currentWork.getWorkNumber());
                    }
                    if (Objects.isNull(work.getWorkCheckInfo())) {
                        work.setWorkCheckInfo(currentWork.getWorkCheckInfo());
                    }
                    if (Objects.isNull(work.getWorkAddInfo())) {
                        work.setWorkAddInfo(currentWork.getWorkAddInfo());
                    }
                    if (Objects.isNull(work.getServiceInfo())) {
                        work.setServiceInfo(currentWork.getServiceInfo());
                    }
                    if (Objects.isNull(work.getDisplayItems())) {
                        work.setDisplayItems(currentWork.getDisplayItems());
                    }

                    if (Objects.isNull(work.getWorkSectionCollection())) {
                        work.setWorkSectionCollection(currentWork.getWorkSectionCollection());
                    }

                    if (Objects.isNull(work.getDeviceCollection())) {
                        work.setDeviceCollection(currentWork.getDeviceCollection());
                    }

                    // 更新
                    ResponseEntity updateRet = (ResponseEntity)this.update(work, authId).getEntity();
                    if (!ServerErrorTypeEnum.SUCCESS.equals(updateRet.getErrorType())) {
                        // 更新に失敗
                        logger.fatal(String.format("update failed [%s]", work.getWorkName()));
                        continue;
                    }

                    if (isUpdateTaktTime) {
                        if (Objects.nonNull(oldTactTime) && !Objects.equals(oldTactTime, work.getTaktTime())) {
                            // タクトタイムの更新がTRUE かつ タクトタイムの変更がある場合、タクトタイムを更新する
                            workflowEntityFacadeREST.updateTime(work, isShift, authId);
                        }
                    }

                    workId = work.getWorkId();
                } else {
                    // 追加に失敗
                    logger.error(String.format("add failed [%s]", work.getWorkName()));
                    continue;
                }
                // 処理に成功したら 工程名 => 工程ID で結果に格納
                resultMap.put(work.getWorkName(), workId.toString());
            }

            // 結果から戻り値を作成する
            ResponseEntity res = ResponseEntity.success();
            res.setUri(JsonUtils.mapToJson(resultMap));
            return Response.ok().entity(res).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        } finally {
            logger.info("addAll end");
        }
    }

    /**
     * 工程を改訂する。(コピーして新しい版数を付与)
     *
     * @param id 工程順ID
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

        WorkEntity entity = this.find(id, false, false, authId);
        if (Objects.isNull(entity)) {
            return Response.serverError().entity(ResponseWorkEntity.failed(ServerErrorTypeEnum.NOTFOUND_UPDATE)).build();
        }

        // 新しい版数 (最新版数 +1)
        int workRev = this.getLatestRev(entity.getWorkName(), false, false) + 1;
        if (workRev > Constants.WORKFLOW_REV_LIMIT) {
            // 版数の最大値オーバー
            return Response.serverError().entity(ResponseWorkEntity.failed(ServerErrorTypeEnum.OVER_MAX_VALUE)).build();
        }

        // 工程順をコピーして、新しい版数で追加する。
        WorkEntity newEntity = new WorkEntity(entity);
        newEntity.setWorkRev(workRev);

        // 新規追加する。
        this.add(newEntity, authId);

        // 最終承認済の最新版数
        int latestRev = this.getLatestRev(entity.getWorkName(), true, true);
        newEntity.setLatestRev(latestRev);

        // ドキュメントファイルを複製する。
        if (Objects.nonNull(entity.getWorkSectionCollection())) {
            FileManager fileManager = FileManager.getInstance();
            for (WorkSectionEntity section : newEntity.getWorkSectionCollection()) {
                if (section.hasDocument()) {
                    // コピー元のファイル
                    StringBuilder source = new StringBuilder();
                    source.append(entity.getWorkId());
                    source.append(File.separator);
                    source.append(section.getPhysicalName());

                    // コピー先のファイル
                    StringBuilder target = new StringBuilder();
                    target.append(newEntity.getWorkId());
                    target.append(File.separator);
                    target.append(section.getPhysicalName());

                    fileManager.copy(FileManager.Data.Manual, source.toString(), target.toString());
                }
            }
        }

        // 作成した情報を元に、戻り値のURIを作成する。
        URI uri = new URI("work/" + newEntity.getWorkId().toString());

        ResponseWorkEntity res = ResponseWorkEntity.success().uri(uri);
        res.setValue(newEntity);

        return Response.created(uri).entity(res).build();
    }

    /**
     * 工程の最新版数を取得する。
     *
     * @param workName 工程名
     * @param isNotRemove 削除済は対象外？
     * @param approve 承認済のみ取得(true:承認済のみ, false:全て)　※承認機能ライセンスが無効な場合は、trueでも全てが対象
     * @return 最新版数
     */
    @Lock(LockType.READ)
    public int getLatestRev(String workName, boolean isNotRemove, boolean approve) {
        TypedQuery<Integer> query;
        boolean approvalLicense = LicenseManager.getInstance().isLicenceOption(LicenseOptionType.ApprovalOption.getName());
        if (approve && approvalLicense) {
            if (isNotRemove) {
                // 指定した工程名の最大の版数を取得 ※.削除済・未承認は対象外
                query = this.em.createNamedQuery("WorkEntity.findLatestRevApproveNotRemove", Integer.class);
            } else {
                // 指定した工程名の最大の版数を取得 ※.削除済も対象、未承認は対象外
                query = this.em.createNamedQuery("WorkEntity.findLatestRevApprove", Integer.class);
            }
        } else {
            if (isNotRemove) {
                // 指定した工程名の最大の版数を取得 ※.削除済は対象外
                query = this.em.createNamedQuery("WorkEntity.findLatestRevNotRemove", Integer.class);
            } else {
                // 指定した工程名の最大の版数を取得 ※.削除済も対象
                query = this.em.createNamedQuery("WorkEntity.findLatestRev", Integer.class);
            }
        }

        query.setParameter("workName", workName);
        Integer latestRev = query.getSingleResult();
        if (Objects.isNull(latestRev)) {
            latestRev = 0;
        }
        return latestRev;
    }


    /**
     * 工程IDを指定して、該当する工程を使用している工程順の件数があるかどうかを取得する。
     *
     * @param id 工程ID
     * @param authId 認証ID
     * @return 使用している工程順があるか (1:ある, 0:ない)
     */
    @GET
    @Path("{id}/exist/assigned-workflow")
    @Produces("text/plain")
    @ExecutionTimeLogging
    public String existAssignedWorkflow(@PathParam("id") Long id, @QueryParam("authId") Long authId) {
        logger.info("existAssignedWorkflow: id={}, authId={}", id, authId);
        Long count = this.countWorkflowAssociation(id);
        if (count > 0) {
            return "1";
        } else {
            return "0";
        }
    }


    /**
     * 工程IDを指定して、該当する工程を使用している工程順の件数を取得する。
     *
     * @param workId 工程ID
     * @return 使用している工程順の件数
     */
    private Long countWorkflowAssociation(Long workId) {
        TypedQuery<Long> query = this.em.createNamedQuery("WorkEntity.countWorkflowWorkAssociation", Long.class);
        query.setParameter("workId", workId);
        return query.getSingleResult();
    }


    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void setWorkflowEntityFacadeREST(WorkflowEntityFacadeREST workflowRest) {
        this.workflowEntityFacadeREST = workflowRest;
    }

    public void setApprovalModel(ApprovalModel approvalModel) {
        this.approvalModel = approvalModel;
    }

    
    /**
     * 工程情報を取得する。
     * 
     * @param ids 工程ID
     * @return 工程情報
     */
    public List<WorkEntity> find(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findByWorkIds", WorkEntity.class);
        query.setParameter("workIds", ids);
        return query.getResultList();
    }
}
