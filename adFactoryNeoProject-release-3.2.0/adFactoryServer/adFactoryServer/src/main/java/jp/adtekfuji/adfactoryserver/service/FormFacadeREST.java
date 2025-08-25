package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXB;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.form.FormCondition;
import jp.adtekfuji.adFactory.entity.form.FormInfoEntity;
import jp.adtekfuji.adFactory.entity.form.FormTagEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.work.TraceOptionEntity;
import jp.adtekfuji.adFactory.entity.work.TraceSettingEntity;
import jp.adtekfuji.adFactory.enumerate.*;
import jp.adtekfuji.adfactoryserver.entity.ErrorResultEntity;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualAditionEntity;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import jp.adtekfuji.adfactoryserver.entity.form.DelayWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.form.WorkScheduleEntity;
import jp.adtekfuji.adfactoryserver.entity.form.WorkflowScheduleEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkSectionEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.ActrualResultRuntimeData;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Singleton
@Path("form")
public class FormFacadeREST {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    @EJB
    private KanbanEntityFacadeREST kanbanEntityFacadeREST;

    @EJB
    private ActualResultEntityFacadeREST actualResultEntityFacadeREST;

    @EJB
    private WorkKanbanEntityFacadeREST workKanbanEntityFacadeREST;

    @EJB
    private WorkflowEntityFacadeREST workflowEntityFacadeREST;

    @EJB
    private WorkEntityFacadeREST workEntityFacadeREST;

    @EJB
    private OrganizationEntityFacadeREST organizationEntityFacadeREST;

    @EJB
    private EquipmentEntityFacadeREST equipmentEntityFacadeREST;

    static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");

    static final String folderName = "adFactoryReport";


    /**
     * 対象のフォルダにある工程カンバンを探す
     *
     * @param workflowId 工程順ID
     * @param workId     工程ID
     * @return 工程カンバン一覧
     */
    public List<WorkKanbanEntity> findWorkKanban(Long workflowId, Long workId) {
        try {

            String sql = "SELECT mwk.* FROM trn_work_kanban mwk WHERE mwk.workflow_id = ?1 AND mwk.work_id = ?2 AND EXISTS(SELECT * FROM mst_kanban_hierarchy mkh JOIN con_kanban_hierarchy ckh ON mkh.hierarchy_name = ?3 AND ckh.kanban_hierarchy_id = mkh.kanban_hierarchy_id AND mwk.kanban_id = ckh.kanban_id)";
            Query query = em.createNativeQuery(sql, WorkKanbanEntity.class);
            query.setParameter(1, workflowId);
            query.setParameter(2, workId);
            query.setParameter(3, folderName);
            return (List<WorkKanbanEntity>) query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }


    /**
     * 実績報告
     *
     * @param report         エンティティ
     * @param actualResultId 実績ID
     * @param workflowId     工程順ID
     * @param workId         工程ID
     * @param authId         承認
     * @return 実績報告結果
     */
    @POST
    @Path("report")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult report(ActualProductReportEntity report, @QueryParam("id") Long actualResultId, @QueryParam("workflowId") Long workflowId, @QueryParam("workId") Long workId, @QueryParam("authId") Long authId) {
        logger.info("report: {}, id={}, workflowId={}, workId={}, authId={}", report, actualResultId, workflowId, workId, authId);
        try {
            // トランザクションIDチェック
            ActrualResultRuntimeData runtimeData = ActrualResultRuntimeData.getInstance();
            if (!runtimeData.checkTransactionId(report)) {
                //すでに実績を受け取っているので無視する.
                logger.info("Invalid report: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.SUCCESS, runtimeData.getNextTransactionId(report));
            }

            // 引数が不正
            if (Objects.isNull(workflowId) || Objects.isNull(workId)) {
                logger.info("Invalid report: {}", report);
                return new ActualProductReportResult(ServerErrorTypeEnum.INVALID_ARGUMENT, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
            }

            ActualResultEntity entity = null;

            // 実績がある場合
            if (Objects.nonNull(actualResultId)) {
                entity = actualResultEntityFacadeREST.find(actualResultId);
                if (Objects.isNull(entity)) {
                    logger.fatal("is Null");
                    return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_ACTUAL_RESULT, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
                }

                if (!Objects.equals(entity.getWorkId(), workId)
                ) {
                    logger.fatal("not Match entity ={}", entity);
                    return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_ACTUAL_RESULT, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
                }

                report.setKanbanId(entity.getKanbanId());
                report.setWorkKanbanId(entity.getWorkKanbanId());
            } else {
                //実績がない場合
                List<WorkKanbanEntity> workKanbanEntities = findWorkKanban(workflowId, workId);

                if (workKanbanEntities.isEmpty()) {
                    // 工程順情報取得
                    WorkflowEntity workflowEntity = null;
                    try {
                        workflowEntity = this.workflowEntityFacadeREST.find(workflowId);
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    if (Objects.isNull(workflowEntity)) {
                        logger.fatal("not found workflow : {}", workflowId);
                        return new ActualProductReportResult(ServerErrorTypeEnum.NOTFOUND_WORKFLOW, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
                    }

                    // 組織情報
                    OrganizationEntity organizationEntity = null;
                    try {
                        organizationEntity = this.organizationEntityFacadeREST.find(report.getOrganizationId());
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                    if (Objects.isNull(organizationEntity)) {
                        logger.fatal("not found organization : {}", report.getOrganizationId());
                    }

                    // カンバン作成
                    String kanbanName = sf.format(new Date()) + workflowEntity.getWorkflowId();
                    Response response = this.kanbanEntityFacadeREST.createKanban(kanbanName, workflowEntity.getWorkflowName(), folderName, organizationEntity.getOrganizationIdentify(), workflowEntity.getWorkflowRev(), authId);
                    ResponseEntity res = (ResponseEntity) response.getEntity();
                    if (!res.isSuccess()) {
                        logger.fatal("cant create Kanban");
                        return new ActualProductReportResult(res.getErrorType(), ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
                    }

                    // 工程カンバン再取得
                    workKanbanEntities = findWorkKanban(workflowId, workId);
                    if (workKanbanEntities.isEmpty()) {
                        return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
                    }
                }

                WorkKanbanEntity workKanbanEntity
                        = workKanbanEntities
                        .stream()
                        .sorted(Comparator.comparing(WorkKanbanEntity::getWorkKanbanId).reversed())
                        .collect(toList())
                        .get(0);
                report.setKanbanId(workKanbanEntity.getKanbanId());
                report.setWorkKanbanId(workKanbanEntity.getWorkKanbanId());
            }

            KanbanEntity kanban = this.kanbanEntityFacadeREST.find(report.getKanbanId());
            WorkflowEntity workflow = this.workflowEntityFacadeREST.find(workflowId);
            kanban.setWorkflowName(String.format("%s : %d", workflow.getWorkflowName(), workflow.getWorkflowRev()));

            WorkKanbanEntity workKanban = this.workKanbanEntityFacadeREST.find(report.getWorkKanbanId());
            WorkEntity work = this.workEntityFacadeREST.find(workId);
            workKanban.setWorkName(String.format("%s : %d", work.getWorkName(), work.getWorkRev()));

            ErrorResultEntity updateResult = this.kanbanEntityFacadeREST.updateActualResult(kanban, workKanban, report, null, actualResultId, 0, report.getInterruptReason(), false, kanban.getServiceInfo(), 0, null, null, null);
            em.flush();
            em.clear();

            ActualResultEntity register = (ActualResultEntity) updateResult.getValue();
            if (Objects.nonNull(entity) && Objects.nonNull(updateResult.getValue())) {
                entity = actualResultEntityFacadeREST.find(actualResultId);
                entity.setRemoveFlag(true);
                List<AddInfoEntity> addInfoEntities = JsonUtils.jsonToObjects(entity.getActualAddInfo(), AddInfoEntity[].class);
                addInfoEntities.add(new AddInfoEntity("@forward_report_id@", CustomPropertyTypeEnum.TYPE_INTEGER, Long.toString(register.getActualId()), 0, null));
                entity.setActualAddInfo(JsonUtils.objectsToJson(addInfoEntities));
                em.flush();
                em.clear();
            }

            // 工程実績
            TypedQuery<ActualResultEntity> actualResultQuery = this.em.createNamedQuery("ActualResultEntity.searchFirstById", ActualResultEntity.class);
            actualResultQuery.setParameter(1, register.getActualId());
            ActualResultEntity actualResultEntities = actualResultQuery.getSingleResult();
            List<AddInfoEntity> addInfoEntities = JsonUtils.jsonToObjects(actualResultEntities.getActualAddInfo(), AddInfoEntity[].class);
            Optional<AddInfoEntity> optLast = addInfoEntities.stream().filter(info -> "@last_actual_id@".equals(info.getKey())).findFirst();
            if (optLast.isPresent()) {
                optLast.ifPresent(last -> last.setVal(String.valueOf(register.getActualId())));
            } else {
                AddInfoEntity info = new AddInfoEntity("@last_actual_id@", CustomPropertyTypeEnum.TYPE_INTEGER, Long.toString(register.getActualId()), 0, null);
                addInfoEntities.add(info);
            }
            actualResultEntities.setActualAddInfo(JsonUtils.objectToJson(addInfoEntities));
            em.flush();
            em.clear();

            return new ActualProductReportResult(updateResult.getErrorType(), runtimeData.forwardTransactionId(report));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
        } finally {
            logger.info("reportReport end.");
        }
    }


    /**
     * 工程実績を登録する。(DB登録ファイルあり)
     *
     * @param inputStreams   マルチパートリクエストボディ
     * @param actualResultId 実績ID
     * @param authId         認証ID
     * @param dataType       データ形式
     * @return 工程実績登録結果
     */
    @POST
    @Path("report/multipart")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ActualProductReportResult reportMultiPart(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("id") Long actualResultId, @QueryParam("workflowId") Long workflowId, @QueryParam("workId") Long workId, @QueryParam("authId") Long authId, @QueryParam("dataType") DataTypeEnum dataType) {
        logger.info("reportMultiPart: id={}, workflowId={}, workId={}, authId={}", actualResultId, workflowId, workId, authId);
        ActualProductReportEntity report = null;
        try {
            // 1番目(ActualProductReportEntity)
            InputStreamReader isr = new InputStreamReader(inputStreams.get(0), StandardCharsets.UTF_8);
            if (DataTypeEnum.JSON.equals(dataType)) {
                // JSON
                Stream<String> stream = new BufferedReader(isr).lines();
                String json = stream.collect(joining());
                report = JsonUtils.jsonToObject(json, ActualProductReportEntity.class);
            } else {
                // JSON以外
                report = JAXB.unmarshal(isr, ActualProductReportEntity.class);
            }

            for (int count = 2; count <= inputStreams.size(); count++) {
                // 2番目以降(ファイルデータ)
                InputStream inputStream = inputStreams.get(count - 1);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                byte[] buff = new byte[1024];
                int len;

                // ファイル情報抽出
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    while ((len = bis.read(buff)) != -1) {
                        byteArrayOutputStream.write(buff, 0, len);
                    }
                    byte[] fileByte = byteArrayOutputStream.toByteArray();

                    // ファイルデータを格納
                    if (Objects.nonNull(report.getAditions().get(count - 2))) {
                        report.getAditions().get(count - 2).setRawData(fileByte);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ActualProductReportResult(ServerErrorTypeEnum.SERVER_FETAL, ActrualResultRuntimeData.getInstance().forwardTransactionId(report));
        } finally {
            logger.info("reportMultiPart end.");
        }

        try {
            return this.report(report, actualResultId, workflowId, workId, authId);
        } finally {
            logger.info("reportMultiPart end.");
        }
    }


    @POST
    @Path("remove")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response remove(@QueryParam("id") List<Long> actualResultIds, @QueryParam("authId") Long authId) {
        try {
            if (Objects.isNull(actualResultIds) || actualResultIds.isEmpty()) {
                return Response.ok().entity(ResponseEntity.success()).build();
            }

            List<ActualResultEntity> actualResultEntities = this.actualResultEntityFacadeREST.find(actualResultIds, authId);
            actualResultEntities.forEach(l -> l.setRemoveFlag(true));
            em.flush();
            em.clear();

            // 履歴が無い場合、カンバンを削除する
            actualResultEntities
                    .stream()
                    .map(ActualResultEntity::getKanbanId)
                    .distinct()
                    .filter(id -> {
                        ActualSearchCondition condition = new ActualSearchCondition().kanbanId(id);
                        condition.setCheckRemoveFlag(false);
                        long count = Long.parseLong(this.actualResultEntityFacadeREST.countActualResult(condition, authId));
                        return count == 0;
                    })
                    .forEach(id -> this.kanbanEntityFacadeREST.removeForced(id, authId));

            return Response.ok().entity(ResponseEntity.success()).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }


    /**
     * 工程順に属する工程情報一覧を取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate       範囲の先頭
     * @param toDate         範囲の末尾
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("report/search/workHistory")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> searchWorkHistory(@QueryParam("workflowId") Long workflowId, @QueryParam("workId") Long workId, @QueryParam("equipmentId") Long equipmentId, @QueryParam("limit") Integer limit, @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("authId") Long authId) {
        logger.info("searchWorkHistory: workflowId={}, workId={}, equipmentId={}, from={}, to={}, authId={}", workflowId, workId, equipmentId, fromDate, toDate, authId);
        try {
            final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");

            int index = 0;
            List<Function<Query, Query>> parameterSetter = new ArrayList<>();

            String sql = "WITH kanban_id AS (WITH RECURSIVE kanban_hierarchy AS (SELECT mkh.kanban_hierarchy_id id FROM mst_kanban_hierarchy mkh WHERE mkh.hierarchy_name = '" + folderName + "' UNION DISTINCT SELECT mkh2.child_id AS kanban_hierarch_id FROM tre_kanban_hierarchy mkh2, kanban_hierarchy WHERE mkh2.parent_id = kanban_hierarchy.id) SELECT ckh.kanban_id FROM kanban_hierarchy JOIN con_kanban_hierarchy ckh ON ckh.kanban_hierarchy_id = kanban_hierarchy.id) SELECT * FROM trn_actual_result tar";
            List<String> actualResultCondition = new ArrayList<>();
            actualResultCondition.add("EXISTS(SELECT * FROM kanban_id WHERE kanban_id.kanban_id = tar.kanban_id)");
            actualResultCondition.add("tar.remove_flag = FALSE");

            // 開始日時
            if (!StringUtils.isEmpty(fromDate)) {
                final Date date = sf.parse(fromDate);
                final int num = ++index;
                actualResultCondition.add("tar.implement_datetime >= " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, date));
            }

            // 完了日時
            if (!StringUtils.isEmpty(toDate)) {
                final Date date = sf.parse(toDate);
                final int num = ++index;
                actualResultCondition.add("tar.implement_datetime <= " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, date));
            }

            // 設備
            if (Objects.nonNull(equipmentId)) {
                final int num = ++index;
                actualResultCondition.add("equipment_id = ?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, equipmentId));
            }

            // 工程
            if (Objects.nonNull(workId)) {
                final int num = ++index;
                actualResultCondition.add("work_id = ?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, workId));
            }

            // 工程順
            if (Objects.nonNull(workflowId)) {
                final int num = ++index;
                actualResultCondition.add("EXISTS(SELECT * FROM mst_workflow mw WHERE mw.remove_flag = FALSE AND mw.workflow_id = tar.workflow_id AND EXISTS(SELECT * FROM mst_workflow mw2 WHERE mw2.workflow_name = mw.workflow_name AND mw2.workflow_id = ?" + num + "))");
                parameterSetter.add((query) -> query.setParameter(num, workflowId));
            }

            sql += " WHERE " + String.join(" AND ", actualResultCondition);
            sql += " ORDER BY implement_datetime DESC";

            // 表示数
            if (Objects.nonNull(limit)) {
                final int num = ++index;
                sql += " LIMIT( ?" + num + " )";
                parameterSetter.add((query) -> query.setParameter(num, limit));
            }

            Query query = em.createNativeQuery(sql, ActualResultEntity.class);
            for (Function<Query, Query> setter : parameterSetter) {
                query = setter.apply(query);
            }

            List<ActualResultEntity> ret = query.getResultList();

            // 実施時刻の昇順でソート
            return ret.stream().sorted(Comparator.comparing(ActualResultEntity::getImplementDatetime)).collect(toList());
//
//            TypedQuery<ActualResultEntity> query = this.em.createNamedQuery("ActualResultEntity.searchHistory", ActualResultEntity.class);
//            query.setParameter(1, workflowId);
//            query.setParameter(2, workId);
//            query.setParameter(3, equipmentId);
//
//            if (Objects.nonNull(limit)) {
//                query.setMaxResults(limit);
//                query.setFirstResult(0);
//            }

//            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("searchHistory end:{}", workflowId);
        }
    }

    /**
     * 工程順に属する工程情報一覧を取得する。
     *
     * @param workflowId 工程順ID
     * @param fromDate       範囲の先頭
     * @param toDate         範囲の末尾
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("report/search/workflowHistory")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<ActualResultEntity> searchWorkflowHistory(@QueryParam("workflowId") Long workflowId, @QueryParam("equipmentId") Long equipmentId, @QueryParam("limit") Integer limit, @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("authId") Long authId) {
        logger.info("workflowHistory: workflowId={}, equipmentId={}, from={}, to={}, authId={}", workflowId, equipmentId, fromDate, toDate, authId);
        try {
            final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            int index = 0;
            List<Function<Query, Query>> parameterSetter = new ArrayList<>();

            String sql = "SELECT tar.* FROM trn_actual_result tar";

            List<String> actualResultCondition = new ArrayList<>();
            if (Objects.nonNull(limit)) {
                List<String> subActualResultCondition = new ArrayList<>();
                sql += " JOIN (SELECT tar2.implement_datetime FROM trn_actual_result tar2 JOIN con_kanban_hierarchy ckh2 ON tar2.kanban_id = ckh2.kanban_id JOIN mst_kanban_hierarchy mkh2 ON ckh2.kanban_hierarchy_id = mkh2.kanban_hierarchy_id AND mkh2.hierarchy_name = '" + folderName + "'";
                sql += " JOIN jsonb_to_recordset(tar2.actual_add_info) as X(key TEXT, val TEXT) ON X.key = '@last_actual_id@' JOIN trn_actual_result tar3 ON tar3.actual_id = CAST(X.val AS INT) AND tar3.remove_flag = FALSE";

                if (Objects.nonNull(fromDate)) {
                    final Date date = sf.parse(fromDate);
                    final int num = ++index;
                    subActualResultCondition.add("tar2.implement_datetime >= ?"+num);
                    parameterSetter.add((query) -> query.setParameter(num, date));
                }

                if (Objects.nonNull(toDate)) {
                    final Date date = sf.parse(toDate);
                    final int num = ++index;
                    subActualResultCondition.add("tar2.implement_datetime <= ?"+num);
                    parameterSetter.add((query) -> query.setParameter(num, date));
                }

                if (Objects.nonNull(equipmentId)) {
                    final int num = ++index;
                    subActualResultCondition.add("tar2.equipment_id = ?"+num);
                    parameterSetter.add((query) -> query.setParameter(num, equipmentId));
                }

                if (Objects.nonNull(workflowId)) {
                    final int num = ++index;
                    subActualResultCondition.add("tar2.workflow_id = ?"+num);
                    parameterSetter.add((query) -> query.setParameter(num, workflowId));
                }

                subActualResultCondition.add("tar2.pair_id IS NULL");

                sql += " WHERE " + String.join(" AND ", subActualResultCondition);
                sql += " GROUP BY tar2.implement_datetime, tar2.equipment_id, tar2.organization_id ORDER BY tar2.implement_datetime DESC LIMIT "+ limit +") subq ON tar.implement_datetime = subq.implement_datetime";
            } else {
                if (Objects.nonNull(fromDate)) {
                    final Date date = sf.parse(fromDate);
                    final int num = ++index;
                    actualResultCondition.add("tar.implement_datetime >= ?"+num);
                    parameterSetter.add((query) -> query.setParameter(num, date));
                }

                if (Objects.nonNull(toDate)) {
                    final Date date = sf.parse(toDate);
                    final int num = ++index;
                    actualResultCondition.add("tar.implement_datetime <= ?"+num);
                    parameterSetter.add((query) -> query.setParameter(num, date));
                }
            }

            if (Objects.nonNull(equipmentId)) {
                final int num = ++index;
                actualResultCondition.add("equipment_id = ?"+num);
                parameterSetter.add((query) -> query.setParameter(num, equipmentId));
            }

            if (Objects.nonNull(workflowId)) {
                final int num = ++index;
                actualResultCondition.add("workflow_id = ?"+num);
                parameterSetter.add((query) -> query.setParameter(num, workflowId));
            }

            actualResultCondition.add("pair_id IS NULL");

            sql += " JOIN con_kanban_hierarchy ckh ON tar.kanban_id = ckh.kanban_id JOIN mst_kanban_hierarchy mkh ON ckh.kanban_hierarchy_id = mkh.kanban_hierarchy_id AND mkh.hierarchy_name = '" + folderName + "'";
            sql += " WHERE " + String.join(" AND ", actualResultCondition);

            Query query = em.createNativeQuery(sql, ActualResultEntity.class);
            for (Function<Query, Query> setter : parameterSetter) {
                query = setter.apply(query);
            }

            List<ActualResultEntity> ret = query.getResultList();

            TypedQuery<ActualResultEntity> actualResultQuery = this.em.createNamedQuery("ActualResultEntity.searchLastById", ActualResultEntity.class);
            return ret
                    .stream()
                    .map(actualResultEntity -> {
                        try {
                            actualResultQuery.setParameter(1, actualResultEntity.getActualId());
                            ActualResultEntity last = actualResultQuery.getSingleResult();
                            if (last.getRemoveFlag()) {
                                logger.info("removeFlat", actualResultEntity.getActualId());
                                return null;
                            }
                            last.setCreateDatetime(actualResultEntity.getImplementDatetime());
                            last.setCreateOrganizationName(actualResultEntity.getOrganizationName());
                            return last;
                        } catch (Exception ex) {
                            actualResultEntity.setCreateDatetime(actualResultEntity.getImplementDatetime());
                            actualResultEntity.setCreateOrganizationName(actualResultEntity.getOrganizationName());
                            return actualResultEntity;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(ActualResultEntity::getImplementDatetime))
                    .collect(toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("searchHistory end:{}", workflowId);
        }
    }

    /**
     * 工程順に属する工程情報一覧を取得する。
     *
     * @param workflowId 工程順ID
     * @param from       範囲の先頭
     * @param to         範囲の末尾
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("workSchedule")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkScheduleEntity> findWorkSchedule(@QueryParam("workflowId") Long workflowId, @QueryParam("equipmentId") Long equipmentId, @QueryParam("organizationId") Long organizationId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findWorkSchedule: workflowId={}, equipmentId={}, organizationId={}, from={}, to={}, authId={}", workflowId, equipmentId, organizationId, from, to, authId);
        try {
            TypedQuery<ConWorkflowWorkEntity> conWorkflowWorkQuery = this.em.createNamedQuery("ConWorkflowWorkEntity.findByKbnAndWorkIdAndWorkflowId", ConWorkflowWorkEntity.class);
            conWorkflowWorkQuery.setParameter(1, WorkKbnEnum.BASE_WORK.getId());
            conWorkflowWorkQuery.setParameter(3, workflowId);

            List<WorkScheduleEntity> workScheduleEntities;
            if (Objects.nonNull(equipmentId) && equipmentId > 0) {
                // 設備IDが指定
                TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findWorkList", WorkEntity.class);
                query.setParameter(1, equipmentId);
                query.setParameter(2, organizationId);
                query.setParameter(3, workflowId);
                if (Objects.nonNull(from) && Objects.nonNull(to)) {
                    query.setMaxResults(to - from + 1);
                    query.setFirstResult(from);
                }
                List<WorkEntity> workEntities = query.getResultList();
                workScheduleEntities
                        = workEntities
                        .parallelStream()
                        .map(workEntity -> {
                            WorkScheduleEntity workScheduleEntity = new WorkScheduleEntity(workEntity);

                            // 前回の実施
                            try {
                                List<ActualResultEntity> actualResultEntities = getLastActualResult(workflowId, workEntity.getWorkId(), equipmentId, 1);
                                if (!actualResultEntities.isEmpty()) {
                                    workScheduleEntity.setLastDatetime(actualResultEntities.getFirst().getImplementDatetime());
                                }
                            } catch (Exception ex) {
                                logger.error(ex, ex);
                            }

                            // 次回の実施予定
                            try {
                                conWorkflowWorkQuery.setParameter(2, workEntity.getWorkId());
                                ConWorkflowWorkEntity conWorkflowWorkEntity = conWorkflowWorkQuery.getSingleResult();
                                conWorkflowWorkEntity
                                        .getSchedule()
                                        .parallelStream()
                                        .map(schedule -> schedule.getNextSchedule(workScheduleEntity.getLastDatetime()))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .sorted()
                                        .findFirst()
                                        .ifPresent(workScheduleEntity::setNextDatetime);
                            } catch (Exception ex) {
                                logger.error(ex, ex);
                            }
                            return workScheduleEntity;
                        })
                        .collect(toList());
            } else {
                // 設備IDが指定
                TypedQuery<WorkEntity> query = this.em.createNamedQuery("WorkEntity.findWorkListNonEq", WorkEntity.class);
                query.setParameter(1, organizationId);
                query.setParameter(2, workflowId);
                if (Objects.nonNull(from) && Objects.nonNull(to)) {
                    query.setMaxResults(to - from + 1);
                    query.setFirstResult(from);
                }
                List<WorkEntity> workEntities = query.getResultList();
                workScheduleEntities
                        = workEntities
                        .parallelStream()
                        .map(workEntity -> {
                            WorkScheduleEntity workScheduleEntity = new WorkScheduleEntity(workEntity);
                            // 前回の実施
                            try {
                                List<ActualResultEntity> actualResultEntities = getLastActualResult(workflowId, workEntity.getWorkId(), null, 1);
                                if (!actualResultEntities.isEmpty()) {
                                    workScheduleEntity.setLastDatetime(actualResultEntities.getFirst().getImplementDatetime());
                                }
                            } catch (Exception ex) {
                                logger.error(ex, ex);
                            }

                            // 次回の実施予定
                            try {
                                conWorkflowWorkQuery.setParameter(2, workEntity.getWorkId());
                                ConWorkflowWorkEntity conWorkflowWorkEntity = conWorkflowWorkQuery.getSingleResult();
                                conWorkflowWorkEntity
                                        .getSchedule()
                                        .parallelStream()
                                        .map(schedule -> schedule.getNextSchedule(workScheduleEntity.getLastDatetime()))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .sorted()
                                        .findFirst()
                                        .ifPresent(workScheduleEntity::setNextDatetime);
                            } catch (Exception ex) {
                                logger.error(ex, ex);
                            }
                            return workScheduleEntity;
                        })
                        .collect(toList());
            }

            workScheduleEntities
                    .parallelStream()
                    .forEach(
                    workEntity -> {
                        List<WorkSectionEntity> workSectionEntities = this.workEntityFacadeREST.getWorkSections(workEntity.getWorkId());
                        workEntity.setWorkSectionCollection(workSectionEntities);

                        // 工程の検査情報のJSON文字列を検査情報一覧に変換する。
                        List<CheckInfoEntity> checkInfos = JsonUtils.jsonToObjects(workEntity.getWorkCheckInfo(), CheckInfoEntity[].class);

                        final Set<String> equipmentIdentifiers
                                = checkInfos
                                .parallelStream()
                                .filter(prop -> categories.contains(prop.getCat()))
                                .map(CheckInfoEntity::getOpt)
                                .filter(Objects::nonNull)
                                .map(option -> JAXB.unmarshal(new ByteArrayInputStream(option.getBytes(StandardCharsets.UTF_8)), TraceSettingEntity.class))
                                .map(TraceSettingEntity::getTraceOptions)
                                .flatMap(Collection::stream)
                                .filter(optionEntity -> "REFERENCE_NUMBER".equals(optionEntity.getKey()))
                                .map(TraceOptionEntity::getValues)
                                .flatMap(Collection::stream)
                                .collect(toSet());

                        final List<EquipmentEntity> equips = equipmentEntityFacadeREST.findByNames(new ArrayList<>(equipmentIdentifiers), EquipmentTypeEnum.MEASURE, EquipmentTypeEnum.MANUFACTURE);
                        workEntity.setDeviceCollection(equips);
                    });
            return workScheduleEntities;
        } catch (NoResultException ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        } finally {
            logger.info("findWorkList end:{}", workflowId);
        }
    }

    /**
     * 設備の子孫を取得する
     * @param parentIds 設備親ID
     * @param equipmentParentMap 設備親IDマップ
     * @return 子孫
     */
    public Map<Long, EquipmentEntity> getEquipmentDescendants(Set<Long> parentIds, Map<Long, List<EquipmentEntity>> equipmentParentMap)
    {
        // 子エレメント
        Map<Long, EquipmentEntity> equipmentEntities
                = parentIds
                .stream()
                .map(equipmentParentMap::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(equipment -> !parentIds.contains(equipment.getEquipmentId()))
                .collect(toMap(EquipmentEntity::getEquipmentId, Function.identity(), (a, b) -> a));

        if (equipmentEntities.size() == 0) {
            return new HashMap<>();
        }

        // 孫ID群
        Set<Long> childrenIds
                = equipmentEntities
                .values()
                .stream()
                .map(EquipmentEntity::getEquipmentId)
                .collect(toSet());

        return Stream.of(
                equipmentEntities,
                getEquipmentDescendants(childrenIds, equipmentParentMap)
                ).flatMap(m -> m.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    }


    /**
     * 工程順に属する工程情報一覧を取得する。
     *
     * @param workflowIds 工程順ID
     * @param from       範囲の先頭
     * @param to         範囲の末尾
     * @return 工程情報
     */
    static final Set<WorkPropertyCategoryEnum> categories = new HashSet<>(Arrays.asList(WorkPropertyCategoryEnum.MEASURE, WorkPropertyCategoryEnum.WORK, WorkPropertyCategoryEnum.INSPECTION));
    @Lock(LockType.READ)
    @GET
    @Path("workflowSchedule")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowScheduleEntity> findWorkflowSchedule(@QueryParam("whId") Long workflowHierarchyId, @QueryParam("wfId") List<Long> workflowIds, @QueryParam("isWf") Boolean isLatestWorkflow, @QueryParam("eId") List<Long> equipmentIds, @QueryParam("type") final List<String> types, @QueryParam("oId") Long organizationId, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        logger.info("findWorkflowSchedule: workflowId={}, equipmentId={}, organizationId={}, from={}, to={}, authId={}", workflowIds, equipmentIds, organizationId, from, to, authId);
        try {
            Set<Long> equipmentTypeId
                    = types.stream()
                    .map(type -> equipmentEntityFacadeREST.getEquipmentType(EquipmentTypeEnum.getEnum(type)))
                    .map(EquipmentTypeEntity::getEquipmentTypeId)
                    .collect(toSet());

            // 工程順Idマップを作成
            Map<Long, WorkflowEntity> workflowEntityMap
                    = workflowIds.isEmpty() || workflowIds.contains(0L)
                    ? this.findWorkflow(workflowHierarchyId, organizationId, isLatestWorkflow, null, null, authId).stream().collect(toMap(WorkflowEntity::getWorkflowId, Function.identity(), (a, b) -> a))
                    : workflowEntityFacadeREST.find(workflowIds, authId).stream().collect(toMap(WorkflowEntity::getWorkflowId, Function.identity(), (a, b) -> a));

            List<EquipmentEntity> equipmentEntities = equipmentEntityFacadeREST.findAll().stream().filter(equipment -> !equipment.getRemoveFlag()).toList();
            Map<Long, EquipmentEntity> equipmentEntityMap = equipmentEntities.stream().collect(toMap(EquipmentEntity::getEquipmentId, Function.identity(), (a, b) -> a));
            Map<Long, List<EquipmentEntity>> equipmentParentMap = equipmentEntities.stream().collect(groupingBy(EquipmentEntity::getParentEquipmentId));

            // 工程順Id 工程Id の工程工程順マップを作成
            Map<Long, List<ConWorkflowWorkEntity>> workflowConWorkflowWorkEntityMap
                    = workflowEntityMap
                    .values()
                    .stream()
                    .map(WorkflowEntity::getWorkflowId)
                    .map(id -> workflowEntityFacadeREST.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, id, false))
                    .filter(Objects::nonNull)
                    .map(entities -> entities
                            .stream()
                            .filter(conWorkflowEntity -> !conWorkflowEntity.getSkipFlag())
                            .collect(toList()))
                    .filter(entities -> !entities.isEmpty())
                    .collect(toMap(entities -> entities.getFirst().getWorkflowId(), Function.identity(), (a, b) -> a));

            List<Long> workEntities
                    = workflowConWorkflowWorkEntityMap
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(ConWorkflowWorkEntity::getWorkId)
                    .distinct()
                    .collect(toList());

            // 工程Idの工程マップを作成
            Map<Long, WorkEntity> workEntityMap
                    = workEntities.isEmpty()
                    ? new HashMap<>()
                    : workEntityFacadeREST
                    .find(workEntities)
                    .stream()
                    .peek(workEntity -> {
                        List<String> equipmentIdentifiers
                                = JsonUtils.jsonToObjects(workEntity.getWorkCheckInfo(), CheckInfoEntity[].class)
                                .stream()
                                .filter(prop -> categories.contains(prop.getCat()))
                                .map(CheckInfoEntity::getOpt)
                                .filter(Objects::nonNull)
                                .map(option -> JAXB.unmarshal(new ByteArrayInputStream(option.getBytes(StandardCharsets.UTF_8)), TraceSettingEntity.class))
                                .map(TraceSettingEntity::getTraceOptions)
                                .flatMap(Collection::stream)
                                .filter(traceOption -> StringUtils.equals("REFERENCE_NUMBER", traceOption.getKey()))
                                .map(TraceOptionEntity::getValues)
                                .flatMap(Collection::stream)
                                .distinct()
                                .toList();
                        final List<EquipmentEntity> equips = equipmentEntityFacadeREST.findByNames(new ArrayList<>(equipmentIdentifiers), EquipmentTypeEnum.MEASURE, EquipmentTypeEnum.MANUFACTURE);
                        workEntity.setDeviceCollection(equips);
                    })
                    .collect(toMap(WorkEntity::getWorkId, Function.identity(), (a, b) -> a));

            workEntityMap
                    .values()
                    .forEach(workEntity -> {
                        List<WorkSectionEntity> workSectionEntities = this.workEntityFacadeREST.getWorkSections(workEntity.getWorkId());
                        workEntity.setWorkSectionCollection(workSectionEntities);
                    });

            // 組織の祖先を取得
            final Set<Long> organizationIdSet
                    = organizationEntityFacadeREST.findOrganizationAncestors(Collections.singletonList(organizationId), authId)
                    .stream()
                    .map(OrganizationEntity::getOrganizationId)
                    .collect(toSet());

            return workflowConWorkflowWorkEntityMap
                    .entrySet()
                    .stream()
                    .parallel()
                    .flatMap(workflowConWorkflowWorkEntrySet -> {
                        WorkflowEntity workflow = workflowEntityMap.get(workflowConWorkflowWorkEntrySet.getKey());
                        return workflowConWorkflowWorkEntrySet
                                .getValue()
                                .parallelStream()
                                .filter(conWorkflowWorkEntity -> conWorkflowWorkEntity
                                        .getOrganizationCollection()
                                        .stream()
                                        .anyMatch(organizationIdSet::contains))
                                .flatMap(conWorkflowWorkEntity -> {
                                    // 設備を設定する
                                    if (!conWorkflowWorkEntity.getEquipmentCollection().isEmpty()) {
                                        // 工程順工程に設定されている設備情報を取得する。
                                        Set<Long> equipmentIdSet = new HashSet<>(conWorkflowWorkEntity.getEquipmentCollection());
                                        // 設備子孫を取得
                                        Map<Long, EquipmentEntity> equipmentDescendant = getEquipmentDescendants(equipmentIdSet, equipmentParentMap);
                                        // 設定要素を設定
                                        equipmentIdSet.forEach(id -> equipmentDescendant.put(id, equipmentEntityMap.get(id)));

                                        if (equipmentIds.contains(0L)) {
                                            // ************************* 設備未指定の場合
                                            List<EquipmentEntity> targetEquipments = new ArrayList<>(equipmentDescendant.values());
                                            return targetEquipments
                                                    .stream()
                                                    .filter(equipmentEntity -> equipmentTypeId.contains(equipmentEntity.getEquipmentTypeId()))
                                                    .map(equipmentEntity -> new Tuple<>(equipmentEntity, conWorkflowWorkEntity));
                                        } else {
                                            // *********************** 設備指定の場合
                                            // 工程順工程に設定されている設備の子孫を取得する。
                                            Set<Long> selectedDescendantIds
                                                    = equipmentIds
                                                    .stream()
                                                    .filter(equipmentDescendant::containsKey)
                                                    .collect(toSet());

                                            List<EquipmentEntity> targetEquipments
                                                    = new ArrayList<>(getEquipmentDescendants(selectedDescendantIds, equipmentParentMap).values());
                                            targetEquipments.addAll(
                                                    selectedDescendantIds
                                                            .stream()
                                                            .map(equipmentEntityMap::get)
                                                            .toList());
                                            return targetEquipments
                                                    .stream()
                                                    .filter(equipmentEntity -> equipmentTypeId.contains(equipmentEntity.getEquipmentTypeId()))
                                                    .map(equipmentEntity -> new Tuple<>(equipmentEntity, conWorkflowWorkEntity));
                                        }
                                    } else if (equipmentIds.contains(0L)){
                                        EquipmentEntity equipment = new EquipmentEntity();
                                        equipment.setEquipmentId(0L);
                                        return Stream.of(new Tuple<>(equipment, conWorkflowWorkEntity));
                                    }
                                    return Stream.of(new Tuple<EquipmentEntity, ConWorkflowWorkEntity>(null, null));
                                })
                                .filter(l -> Objects.nonNull(l.getLeft()))
                                // 設備毎にグループ化
                                .collect(groupingBy(entry -> entry.getLeft().getEquipmentId()))
                                .values()
                                .parallelStream()
                                .map(tuples -> {
                                    // 戻り値の生成
                                    EquipmentEntity equipment = tuples.getFirst().getLeft();
                                    List<ConWorkflowWorkEntity> conWorkflowWorkEntities
                                            = tuples
                                            .stream()
                                            .map(Tuple::getRight)
                                            .toList();

                                    WorkflowScheduleEntity workflowSchedule = new WorkflowScheduleEntity(workflow);
                                    // 設備情報
                                    workflowSchedule.setEquipment(equipment);

                                    Map<Long, Date> workIdImplementDateMap
                                            = getLastActualResult(workflow.getWorkflowId(), null, equipment.getEquipmentId(), null)
                                            .stream()
                                            .collect(toMap(ActualResultEntity::getWorkId, ActualResultEntity::getImplementDatetime));

                                    // 前回のスケジュールを設定
                                    conWorkflowWorkEntities
                                            .stream()
                                            .map(ConWorkflowWorkEntity::getWorkId)
                                            .filter(workIdImplementDateMap::containsKey)
                                            .map(workIdImplementDateMap::get)
                                            .max(Comparator.naturalOrder())
                                            .ifPresent(workflowSchedule::setLastDatetime);

                                    // 次回のスケジュールを設定
                                    conWorkflowWorkEntities
                                            .stream()
                                            .flatMap(conWorkflowWorkEntity -> {
                                                Date implementDate = workIdImplementDateMap.get(conWorkflowWorkEntity.getWorkId());
                                                return conWorkflowWorkEntity
                                                        .getSchedule()
                                                        .stream()
                                                        .map(scheduleConditionInfoEntity -> scheduleConditionInfoEntity.getNextSchedule(implementDate));
                                            })
                                            .filter(Optional::isPresent)
                                            .map(Optional::get)
                                            .min(Comparator.naturalOrder())
                                            .ifPresent(workflowSchedule::setNextDatetime);

                                    // 未実施のレポートがあるか？
                                    workflowSchedule.setHasInProgressReports(this.hasInProgressReports(workflow.getWorkflowId(), null, equipment.getEquipmentId()));

                                    // 必要な工程情報を設定
                                    workflowSchedule.setWorks(
                                            conWorkflowWorkEntities
                                                    .stream()
                                                    .sorted(Comparator.comparing(ConWorkflowWorkEntity::getWorkflowOrder))
                                                    .map(ConWorkflowWorkEntity::getWorkId)
                                                    .map(workEntityMap::get)
                                                    .filter(Objects::nonNull)
                                                    .map(WorkEntity::clone)
                                                    .peek(work -> {
                                                        if (Objects.isNull(work.getDeviceCollection())) {
                                                            work.setDeviceCollection(Collections.singletonList(equipment));
                                                        } else {
                                                            work.getDeviceCollection().add(equipment);
                                                        }
                                                    })
                                                    .collect(toList()));

                                    return workflowSchedule;
                                });
                    })
                    .collect(toList());
        } catch (NoResultException ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }


    /**
     * 遅延している工程一覧を取得する。
     *
     * @param workflowId 工程順ID
     * @param equipmentId 設備ID
     * @param authId 認証ID
     * @return 工程情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("delayWorkIds")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<DelayWorkEntity> getDelayWorkIds(@QueryParam("workflowId") Long workflowId, @QueryParam("equipmentId") Long equipmentId, @QueryParam("authId") Long authId) {
        Date now = new Date();
        Map<Long, Date> workIdImplementDateMap
                = getLastActualResult(workflowId, null, equipmentId, null)
                .stream()
                .collect(toMap(ActualResultEntity::getWorkId, ActualResultEntity::getImplementDatetime));

        return workflowEntityFacadeREST.getConWorkflowWorks(WorkKbnEnum.BASE_WORK, workflowId, false)
                .stream()
                .map(conWorkflowWorkEntity -> {
                    Long workId = conWorkflowWorkEntity.getWorkId();
                    Stream<Date> nextImplementDate;
                    if (workIdImplementDateMap.containsKey(workId)) {
                        Date implementDate = workIdImplementDateMap.get(workId);
                        nextImplementDate = conWorkflowWorkEntity
                                .getSchedule()
                                .stream()
                                .map(scheduleConditionInfoEntity -> scheduleConditionInfoEntity.getNextSchedule(implementDate))
                                .filter(Optional::isPresent)
                                .map(Optional::get);
                    } else {
                        nextImplementDate = conWorkflowWorkEntity
                                .getSchedule()
                                .stream()
                                .map(scheduleConditionInfoEntity -> scheduleConditionInfoEntity.getNextSchedule(scheduleConditionInfoEntity.getStartDate()))
                                .filter(Optional::isPresent)
                                .map(Optional::get);
                    }

                    List<Date> nextDate = nextImplementDate.filter(date -> date.before(now)).collect(toList());
                    if (!nextDate.isEmpty()) {
                        Optional<Date> next = nextDate.stream().min(Comparator.comparing(date -> date));
                        return new DelayWorkEntity(conWorkflowWorkEntity.getWorkId(), next.get());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }


    /**
     * 工程順一覧を取得
     *
     * @param hierarchyId    階層ID
     * @param organizationId 組織ID
     * @param isLatest       最新のみ取得
     * @param from           開始
     * @param to             完了
     * @param authId         認証
     * @return 工程順
     */
    @Lock(LockType.READ)
    @GET
    @Path("workflow")
    @Consumes({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<WorkflowEntity> findWorkflow(@QueryParam("hierarchyId") Long hierarchyId, @QueryParam("organizationId") Long organizationId, @QueryParam("isLatest") Boolean isLatest, @QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("authId") Long authId) {
        try {
            TypedQuery<WorkflowEntity> query = isLatest
                    ? this.em.createNamedQuery("WorkflowEntity.findLatestWorkflowList", WorkflowEntity.class)
                    : this.em.createNamedQuery("WorkflowEntity.findWorkflowList", WorkflowEntity.class);
            query.setParameter(1, organizationId);
            query.setParameter(2, hierarchyId);
            query.setParameter(3, organizationId);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                query.setMaxResults(to - from + 1);
                query.setFirstResult(from);
            }

            return query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 製造設備の子孫一覧を取得する
     *
     * @param ids 組織ID
     * @param authId 承認ID
     * @return 設備情報
     */
    @Lock(LockType.READ)
    @GET
    @Path("equipment")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentEntity> findEquipmentDescendants(@QueryParam("id") final List<Long> ids, @QueryParam("type") final List<String> types, @QueryParam("authId") Long authId) {
        logger.info("findEquipment: ids={}, authId={}", ids, authId);

        if (ids.isEmpty() || types.isEmpty()) {
            return new ArrayList<>();
        }

        try (Connection connection = this.em.unwrap(Connection.class)) {
            java.sql.Array idArray = connection.createArrayOf("integer", ids.toArray());
            java.sql.Array typeArray = connection.createArrayOf("text", types.toArray());
            final Query query = em
                    .createNamedQuery("EquipmentEntity.findDescendantsByIds", EquipmentEntity.class)
                    .setParameter(1, idArray)
                    .setParameter(2, typeArray);

            return (List<EquipmentEntity>) query.getResultList();
        } catch (SQLException ex) {

            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }




    /**
     * 帳票取得
     *
     * @param condition 条件
     * @param authId    承認ID
     * @return 帳票
     */
    @Lock(LockType.READ)
    @PUT
    @Path("template")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<FormInfoEntity> findTemplate(FormCondition condition, @QueryParam("authId") Long authId) {

        try {
            List<WorkflowEntity> workflowEntities = workflowEntityFacadeREST.find(condition.getWorkflowIds(), authId);
            return workflowEntities
                    .stream()
                    .flatMap(entity ->
                            Stream.of(entity.getLedgerPath().split("\\|"))
                                    .map(item -> {
                                        final String fileName = new File(item).getName();
                                        return new FormInfoEntity(FormInfoEntity.FormCategoryEnum.WORKFLOW, entity.getWorkflowId(), fileName.substring(0, fileName.lastIndexOf('.')), item);
                                    }))
                    .collect(toList());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }


    /**
     * タグリスト取得
     *
     * @param entity タグリスト
     * @return タグ結果一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("tag")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public FormTagEntity getFormTag(FormInfoEntity entity, @QueryParam("authId") Long authID) {

        FormTagEntity formTagEntity = new FormTagEntity();

        try {
            // 工程順
            WorkflowEntity workflowEntity = workflowEntityFacadeREST.find(entity.getId());
            OrganizationEntity workflowUpdater = organizationEntityFacadeREST.find(workflowEntity.getUpdatePersonId());
            formTagEntity.put("TAG_WORKFLOW_NAME", workflowEntity.getWorkflowName()); // 工程順名
            formTagEntity.put("TAG_WORKFLOW_REVISION", workflowEntity.getWorkflowRevision()); // 工程順版数
            formTagEntity.put("TAG_WORKFLOW_UPDATER", workflowUpdater.getOrganizationName()); // 工程順更新者
            formTagEntity.put("TAG_WORKFLOW_UPDATE_DATE", workflowEntity.getUpdateDatetime()); // 工程順更新日

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        try {
            // 工程実績
            TypedQuery<ActualResultEntity> actualResultQuery = this.em.createNamedQuery("ActualResultEntity.searchHistoryByDate", ActualResultEntity.class);
            actualResultQuery.setParameter(1, entity.getFromDate(), TemporalType.TIMESTAMP);
            actualResultQuery.setParameter(2, entity.getToDate(), TemporalType.TIMESTAMP);
            actualResultQuery.setParameter(3, entity.getId());
            List<ActualResultEntity> actualResultEntities = actualResultQuery.getResultList();
            // 工程実績を工程ごとにグループ化
            Map<Long, List<ActualResultEntity>> actualResultByWorkMap
                    = actualResultEntities
                    .stream()
                    .collect(groupingBy(ActualResultEntity::getWorkId));

            // 工程実績付加情報取得
            Map<Long, List<ActualAditionEntity>> ActualAditionEntityMap = new HashMap<>();
            if (!actualResultEntities.isEmpty()) {
                try {
                    TypedQuery<ActualAditionEntity> actualAdditionEntityTypedQuery = this.em.createNamedQuery("ActualAditionEntity.findByActualIDWithoutImage", ActualAditionEntity.class);
                    actualAdditionEntityTypedQuery.setParameter("actualID", actualResultEntities.stream().map(ActualResultEntity::getActualId).collect(toList()));
                    List<ActualAditionEntity> additionEntities = actualAdditionEntityTypedQuery.getResultList();
                    ActualAditionEntityMap = additionEntities.stream().collect(groupingBy(ActualAditionEntity::getActualId));
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }

            // タグのプレフィックスは工程ごとに採番する
            final String TAG_ED = "TAG_ED(";
            for (List<ActualResultEntity> actualResults : actualResultByWorkMap.values()) {
                for (int index = 0; index < actualResults.size(); ++index) {
                    final String prefix = String.format("$%d.", index + 1);

                    final ActualResultEntity actualResultEntity = actualResults.get(index);
                    formTagEntity.put(prefix + "TAG_HIS_DATETIME", actualResultEntity.getImplementDatetime()); // 日付
                    formTagEntity.put(prefix + "TAG_HIS_ORGANIZATION", actualResultEntity.getOrganizationName()); // 作業者
                    formTagEntity.put(prefix + "TAG_HIS_EQUIPMENT", actualResultEntity.getEquipmentName()); // 作業設備

                    // 品質トレサ情報設定
                    final String actualAddInfo = actualResultEntity.getActualAddInfo();
                    Stream.of(JsonUtils.jsonToObjects(actualAddInfo, ActualPropertyEntity[].class))
                            .flatMap(Collection::stream)
                            .filter(item -> CustomPropertyTypeEnum.TYPE_TRACE.equals(item.getActualPropType()))
                            .forEach(item -> {
                                if (item.getActualPropName().startsWith(TAG_ED)) {
                                    formTagEntity.put(item.getActualPropName().replace(TAG_ED, TAG_ED + prefix), item.getActualPropValue());
                                } else {
                                    formTagEntity.put(prefix + item.getActualPropName(), item.getActualPropValue());
                                }
                            });

                    // 工程実績付加情報
                    ActualAditionEntityMap
                            .computeIfAbsent(actualResultEntity.getActualId(), key -> new ArrayList<>())
                            .forEach(item -> formTagEntity.put(prefix + item.getTag(), item.getActualAditionId()));
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return formTagEntity;
    }


    final static FileManager fileManager = FileManager.getInstance();

    /**
     * 帳票アップロード
     *
     * @param inputStreams 帳票ファイル
     * @param authId       w承認ID
     * @return 結果
     */
    @POST
    @Path(value = "uploadLedgerFile")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response uploadLedgerFile(@FormDataParam("file") ArrayList<InputStream> inputStreams, @QueryParam("authId") Long authId) {
        logger.info("uploadLedgerFile: start");

        final String fileName = "report-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        File inputFile = new File(fileManager.getLocalePath(FileManager.Data.Import, fileName));

        // ファイル取込
        logger.info("importFile: " + inputFile);
        BufferedInputStream bf = new BufferedInputStream(inputStreams.get(0));
        byte[] buff = new byte[1024];
        try (OutputStream out = Files.newOutputStream(inputFile.toPath())) {
            int len;
            while ((len = bf.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
            out.flush();

            File outputFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, fileName + ".pdf"));

            // excel -> pdf変換
            ProcessBuilder pb = new ProcessBuilder(fileManager.getLocalePath("/bin/xlsx2pdf.bat"), inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
            Process p = pb.start();
            p.waitFor();

            // ファイルが存在しない
            if (!outputFile.exists()) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
            }

            URI uri = new URI("/data/report/" + outputFile.getName());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     * 最後に実施した実績
     * @param workflowId 工程順ID
     * @param workId 工程ID
     * @param equipmentId 設備ID
     * @return 工程実績
     */
    private List<ActualResultEntity> getLastActualResult(Long workflowId, Long workId, Long equipmentId, Integer limit) {
        return getLastActualResult(workflowId, workId, equipmentId, "tar.actual_status = 'COMPLETION'", limit);
    }

    /**
     * 指定された条件に基づいて、実績結果の最後のリストを取得します。
     *
     * @param workflowId ワークフローID（0またはnullの場合は無視されます）
     * @param workId ワークID（0またはnullの場合は無視されます）
     * @param equipmentId 設備ID（0またはnullの場合は無視されます。0の場合はequipment_idが0のデータを対象とします）
     * @param statusCondition 実績ステータスに対する条件（必須）
     * @param limit 結果リストの最大件数（nullの場合、制限なし）
     * @return 条件に一致するActualResultEntityのリスト（エラー発生時は空リストを返します）
     */
    private List<ActualResultEntity> getLastActualResult(Long workflowId, Long workId, Long equipmentId, String statusCondition, Integer limit) {
        try {
            int index = 0;
            List<Function<Query, Query>> parameterSetter = new ArrayList<>();

            String sql = "SELECT DISTINCT ON (tar.work_kanban_id, tar.equipment_id) tar.* FROM trn_actual_result tar JOIN con_kanban_hierarchy ckh ON ckh.kanban_id = tar.kanban_id  JOIN mst_kanban_hierarchy mkh ON ckh.kanban_hierarchy_id = mkh.kanban_hierarchy_id AND mkh.hierarchy_name = '" + folderName + "'";
            List<String> actualResultCondition = new ArrayList<>();

            actualResultCondition.add("tar.remove_flag = false");
            actualResultCondition.add(statusCondition);
//            actualResultCondition.add("tar.actual_status = 'COMPLETION'");

            // 工程順
            if (Objects.nonNull(workflowId) && workflowId !=0 ) {
                final int num = ++index;
                actualResultCondition.add("tar.workflow_id = " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, workflowId));
            }

            // 工程
            if (Objects.nonNull(workId) && workId!=0) {
                final int num = ++index;
                actualResultCondition.add("tar.work_id = " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, workId));
            }

            // 設備
            if (Objects.nonNull(equipmentId) && equipmentId!=0) {
                final int num = ++index;
                actualResultCondition.add("tar.equipment_id = " + "?" + (num));
                parameterSetter.add((query) -> query.setParameter(num, equipmentId));
            } else if (equipmentId == 0){
                actualResultCondition.add("tar.equipment_id = 0");
            }

            sql += " WHERE " + String.join(" AND ", actualResultCondition);
            sql += " ORDER BY tar.work_kanban_id, tar.equipment_id, tar.implement_datetime DESC";

            Query query = em.createNativeQuery(sql, ActualResultEntity.class);
            for (Function<Query, Query> setter : parameterSetter) {
                query = setter.apply(query);
            }

            if (Objects.nonNull(limit)) {
                query.setMaxResults(limit);
            }

            return (List<ActualResultEntity>) query.getResultList();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定されたワークフローID、作業ID、装置IDに関連する進行中のレポートが存在するかを確認します。
     * @param workflowId ワークフローID
     * @param workId 作業ID
     * @param equipmentId 装置ID
     * @return 進行中のレポートが存在する場合はtrue、それ以外の場合はfalse
     */
    private Boolean hasInProgressReports(Long workflowId, Long workId, Long equipmentId) {
        return !getLastActualResult(workflowId, workId, equipmentId, "tar.actual_status <> 'COMPLETION'", 1).isEmpty();
    }

    /**
     * 画像ファイルアップロード
     *
     * @param inputStreams 帳票ファイル
     * @param authId       w承認ID
     * @return 結果
     */
    @POST
    @Path(value = "uploadImageFile")
    @Consumes("multipart/form-data")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response uploadImageFile(List<InputStream> inputStreams, @QueryParam("id") final Long workId, @QueryParam("authId") Long authId) {
        logger.info("uploadImageFile: start");

        final File dir = new File(fileManager.getLocalePath(FileManager.Data.REPORT, String.format("%d", workId)));
        if (!dir.exists() && !dir.mkdir()) {
            logger.fatal("Fail mkDir");
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }

        final String fileName = String.format("%d", workId)+ File.separator + "report-" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        File outputFile = new File(fileManager.getLocalePath(FileManager.Data.REPORT, fileName));

        // ファイル取込
        BufferedInputStream bf = new BufferedInputStream(inputStreams.get(0));
        byte[] buff = new byte[1024];
        try (OutputStream out = Files.newOutputStream(outputFile.toPath())) {
            int len;
            while ((len = bf.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
            out.flush();
            URI uri = new URI("/data/report/" + String.format("%d", workId) + "/" + outputFile.getName());
            return Response.created(uri).entity(ResponseEntity.success().uri(uri)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

}
