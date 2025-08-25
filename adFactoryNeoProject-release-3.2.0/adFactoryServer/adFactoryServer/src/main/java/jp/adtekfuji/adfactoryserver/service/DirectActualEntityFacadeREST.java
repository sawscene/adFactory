package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.directwork.ActualAddInfoEntity;
import jp.adtekfuji.adFactory.entity.directwork.WorkReportWorkNumEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.common.SystemConfig;
import jp.adtekfuji.adfactoryserver.entity.directwork.DirectActualEntity;
import jp.adtekfuji.adfactoryserver.entity.view.WorkReportEntity;
import jp.adtekfuji.adfactoryserver.entity.view.WorkReportListEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 直接工数編集REST
 */
@Singleton
@Path("direct-actual")
public class DirectActualEntityFacadeREST extends AbstractFacade<DirectActualEntity> {

    private final Logger logger = LogManager.getLogger();


    boolean isWorkReportWorkNumVisible = SystemConfig.getInstance().isWorkReportWorkNumVisible();;


    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    public DirectActualEntityFacadeREST() {
        super(DirectActualEntity.class);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @ExecutionTimeLogging
    public void add(DirectActualEntity entity) {
        logger.info("add: {}", entity);
        if (Objects.isNull(entity.getClassKey())) {
            entity.setClassKey("");
        }
        super.create(entity);
        this.em.flush();
    }

    static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

    /**
     * 作業日報が更新対象かどうか判定する
     *
     * @param a 作業日報情報
     * @param b 作業日報情報
     * @return 更新対象の場合はtrue
     */
    private static boolean isWorkReportUpdateTarget(WorkReportEntity a, WorkReportEntity b) {
        return
            Objects.equals(a.getWorkType(), b.getWorkType()) &&                // 作業区分
            Objects.equals(a.getOrganizationId(), b.getOrganizationId()) &&    // 組織ID
            StringUtils.equals(a.getWorkDate(), b.getWorkDate()) &&            // 日付
            StringUtils.equals(a.getKanbanName(), b.getKanbanName()) &&        // カンバン名
            Objects.equals(a.getWorkflowId(), b.getWorkflowId()) &&            // 工程順ID
            Objects.equals(a.getWorkId(), b.getWorkId()) &&                    // 工程ID
            Objects.equals(a.getClassKey(), b.getClassKey());                  // 分類キー
    }

    /**
     * カンバン集計日報の直接工数の更新
     *
     * @param workReportListEntity 更新後の日報
     * @param authId             更新者
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("kanban")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity updateKanbanActual(WorkReportListEntity workReportListEntity, @QueryParam("authId") Long authId) {
        logger.info("updateKanbanActual, authId={}", authId);

        if (Objects.isNull(workReportListEntity) || Objects.isNull(workReportListEntity.getWorkReportEntityList())) {
            logger.info("not Found Data");
            return ResponseEntity.success();
        }

        final List<WorkReportEntity> workReportEntities = workReportListEntity.getWorkReportEntityList().stream()
                .filter(entity -> entity.getWorkType() == 0 || entity.getWorkType() == 3)
                .filter(entity -> Objects.nonNull(entity.getWorkDate()))
                .filter(entity -> Objects.nonNull(entity.getOrganizationId()))
                .collect(toList());

        if (workReportEntities.isEmpty()) {
            logger.info("not Found Data");
            return ResponseEntity.success();
        }

        final String fromDate = workReportEntities.stream().map(WorkReportEntity::getWorkDate).min(Comparator.naturalOrder()).get();
        final String toDate = workReportEntities.stream().map(WorkReportEntity::getWorkDate).max(Comparator.naturalOrder()).get();
        final List<Long> organizationIds = workReportEntities.stream().map(WorkReportEntity::getOrganizationId).collect(toList());

        try (Connection connection = this.em.unwrap(Connection.class)) {
            java.sql.Array idArray = connection.createArrayOf("integer", organizationIds.toArray());

            // 条件に従い、日報を取得
            TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyKanban2", WorkReportEntity.class);
            query.setParameter(1, fromDate);
            query.setParameter(2, toDate);
            query.setParameter(3, idArray);
            final List<WorkReportEntity> oldWorkReportList = query.getResultList();

            // 追加
            for (WorkReportEntity newWorkReport: workReportEntities) {
                if (oldWorkReportList.stream().anyMatch(item -> isWorkReportUpdateTarget(item, newWorkReport))) {
                    continue;
                }
                ActualAddInfoEntity newActualAddInfoEntity = null;
                if (!StringUtils.isEmpty(newWorkReport.getWorkReprotAddInfo())) {
                    newActualAddInfoEntity = JsonUtils.jsonToObject(newWorkReport.getWorkReprotAddInfo(), ActualAddInfoEntity.class);
                }
                if (newActualAddInfoEntity == null || !newActualAddInfoEntity.isManuallyAdded() || newActualAddInfoEntity.isRemove()) {
                    continue;
                }
                Date day;
                try {
                    day = sf.parse(newWorkReport.getWorkDate());
                } catch (ParseException ex) {
                    logger.fatal(ex, ex);
                    return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
                }
                DirectActualEntity directActualEntity = new DirectActualEntity(
                        newWorkReport.getWorkType(),
                        day,
                        newWorkReport.getOrganizationId(),
                        newWorkReport.getWorkId(),
                        newWorkReport.getWorkName(),
                        newWorkReport.getOrderNumber(),
                        newWorkReport.getWorkTime().intValue(),
                        newWorkReport.getWorkflowId(),
                        newWorkReport.getKanbanName(),
                        newWorkReport.getModelName(),
                        0,
                        newWorkReport.getWorkTypeOrder(),
                        newWorkReport.getProductionNumber(),
                        newWorkReport.getClassKey(),
                        authId);
                directActualEntity.setActualAddInfo(newWorkReport.getWorkReprotAddInfo());
                add(directActualEntity);
                logger.info("add DirectActual {}",  newWorkReport);
            }

            for (WorkReportEntity oldWorkReport : oldWorkReportList) {
                List<WorkReportEntity> editEntities = workReportEntities.stream()
                        .filter(item -> isWorkReportUpdateTarget(item, oldWorkReport))
                        .collect(toList());

                if (editEntities.isEmpty()) {
                    // 同じデータが見つからない(クライアント側の実装が間違っている)
                    logger.fatal("Error!! Not Found Same Item {}", oldWorkReport);
                    continue;
                }

                // 削除
                ActualAddInfoEntity editActualAddInfoEntity = null;
                if (!StringUtils.isEmpty(editEntities.get(0).getWorkReprotAddInfo())) {
                    editActualAddInfoEntity = JsonUtils.jsonToObject(editEntities.get(0).getWorkReprotAddInfo(), ActualAddInfoEntity.class);
                }
                if (editActualAddInfoEntity != null && editActualAddInfoEntity.isRemove()) {
                    logger.info("remove DirectActual {}", oldWorkReport);

                    TypedQuery<DirectActualEntity> query2 = this.em.createNamedQuery("DirectActualEntity.removeDirectActual", DirectActualEntity.class);
                    query2.setParameter(1, oldWorkReport.getWorkType());
                    query2.setParameter(2, oldWorkReport.getWorkDate());
                    query2.setParameter(3, oldWorkReport.getKanbanName());
                    query2.setParameter(4, oldWorkReport.getWorkflowId());
                    query2.setParameter(5, oldWorkReport.getWorkId());
                    query2.setParameter(6, oldWorkReport.getOrganizationId());
                    query2.setParameter(7, oldWorkReport.getClassKey());
                    query2.executeUpdate();

                    continue;
                }

                long workingTime = editEntities.stream().mapToLong(WorkReportEntity::getWorkTime).sum();
                if (workingTime != oldWorkReport.getWorkTime()) {
                    Date day;
                    try {
                        day = sf.parse(oldWorkReport.getWorkDate());
                    } catch (ParseException ex) {
                        logger.fatal(ex, ex);
                        return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    }

                    // 差分を登録
                    DirectActualEntity directActualEntity = new DirectActualEntity(
                            oldWorkReport.getWorkType(),
                            day,
                            oldWorkReport.getOrganizationId(),
                            oldWorkReport.getWorkId(),
                            oldWorkReport.getWorkName(),
                            oldWorkReport.getOrderNumber(),
                            (int) (workingTime - oldWorkReport.getWorkTime()),
                            oldWorkReport.getWorkflowId(),
                            oldWorkReport.getKanbanName(),
                            oldWorkReport.getModelName(),
                            0,
                            oldWorkReport.getWorkTypeOrder(),
                            oldWorkReport.getProductionNumber(),
                            oldWorkReport.getClassKey(),
                            authId);
                    add(directActualEntity);
                    logger.info("update DirectActual new={}, {}", workingTime, oldWorkReport);
                }

                if (!isWorkReportWorkNumVisible) {
                    continue;
                }

                // 製造番号表示対応(浜井様特殊)
                
                List<WorkReportWorkNumEntity> reports = editEntities.stream()
                        .map(WorkReportEntity::getWorkReprotAddInfo)
                        .filter(Objects::nonNull)
                        .map(item -> JsonUtils.jsonToObject(item, ActualAddInfoEntity.class))
                        .filter(Objects::nonNull)
                        .map(ActualAddInfoEntity::getWorkReportWorkNum)
                        .collect(Collectors.toList());
                
                if (reports.isEmpty()) {
                    continue;
                }
                
                List<String> selectedNo = formatControlNumber(reports.stream()
                        .map(WorkReportWorkNumEntity::getSelectedControlNo)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(joining(",")));

                if (Objects.isNull(oldWorkReport.getDirectActualId())) {
                    continue;
                }

                DirectActualEntity directActualEntity = find(oldWorkReport.getDirectActualId());
                if (Objects.isNull(directActualEntity)) {
                    continue;
                }

                List<String> oldSelectedNo = null;
                ActualAddInfoEntity actualAddInfoEntity = null;
                WorkReportWorkNumEntity workReportWorkNum = null;
                if (!StringUtils.isEmpty(directActualEntity.getActualAddInfo())) {
                    actualAddInfoEntity = JsonUtils.jsonToObject(directActualEntity.getActualAddInfo(), ActualAddInfoEntity.class);
                    if (Objects.nonNull(actualAddInfoEntity)) {
                        workReportWorkNum = actualAddInfoEntity.getWorkReportWorkNum();
                        if (Objects.nonNull(workReportWorkNum)) {
                            oldSelectedNo = workReportWorkNum.getSelectedControlNo();
                        }
                    }
                }

                //if ((Objects.isNull(selectedNo) || selectedNo.isEmpty()) && (Objects.isNull(oldSelectedNo) || oldSelectedNo.isEmpty())) {
                //    continue;
                //}

                //if (Objects.equals(selectedNo, oldSelectedNo)) {
                //    continue;
                //}

                if (Objects.isNull(actualAddInfoEntity)) {
                    actualAddInfoEntity = new ActualAddInfoEntity();
                }

                if (Objects.isNull(workReportWorkNum)) {
                    workReportWorkNum = new WorkReportWorkNumEntity();
                }
                
                WorkReportWorkNumEntity report = reports.get(0);
                workReportWorkNum.setResources(report.getResources());
                workReportWorkNum.setFinalNum(report.getFinalNum());
                workReportWorkNum.setDefectNum(report.getDefectNum());
                workReportWorkNum.setStopTime(report.getStopTime());
                
                workReportWorkNum.setSelectedControlNo(selectedNo);
                actualAddInfoEntity.setWorkReportWorkNum(workReportWorkNum);
                directActualEntity.setActualAddInfo(JsonUtils.objectToJson(actualAddInfoEntity));
                edit(directActualEntity);
                em.flush();
            }

            return ResponseEntity.success();
        } catch (Exception ex) {
            logger.fatal(ex,ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 製番毎の集計日報の直接工数の更新
     *
     * @param workReportListEntity 更新後の日報
     * @param authId             更新者
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("production")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity updateProductionActual(WorkReportListEntity workReportListEntity, @QueryParam("authId") Long authId) {
        logger.info("updateProductionActual, authId={}", authId);

        if (Objects.isNull(workReportListEntity) || Objects.isNull(workReportListEntity.getWorkReportEntityList())) {
            logger.info("not Found Data");
            return ResponseEntity.success();
        }

        final List<WorkReportEntity> workReportEntities
                = workReportListEntity.getWorkReportEntityList()
                .stream()
                .filter(entity -> entity.getWorkType() == 0)
                .filter(entity -> Objects.nonNull(entity.getWorkDate()))
                .filter(entity -> Objects.nonNull(entity.getOrganizationId()))
                .collect(toList());

        if (workReportEntities.isEmpty()) {
            logger.info("not Found Data");
            return ResponseEntity.success();
        }

        final String fromDate = workReportEntities.stream().map(WorkReportEntity::getWorkDate).min(Comparator.naturalOrder()).get();
        final String toDate = workReportEntities.stream().map(WorkReportEntity::getWorkDate).max(Comparator.naturalOrder()).get();
        final List<Long> organizationIds = workReportEntities.stream().map(WorkReportEntity::getOrganizationId).collect(toList());

        try (Connection connection = this.em.unwrap(Connection.class)) {
            java.sql.Array idArray = connection.createArrayOf("integer", organizationIds.toArray());
            // オーダ毎の日報取得
            TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyProduct2", WorkReportEntity.class);
            query.setParameter(1, fromDate);
            query.setParameter(2, toDate);
            query.setParameter(3, idArray);
            List<WorkReportEntity> oldWorkReportList = query.getResultList();

            for (WorkReportEntity oldWorkReport : oldWorkReportList) {
                final long workingTime
                        = workReportEntities
                        .stream()
                        .filter(item -> Objects.equals(item.getModelName(), oldWorkReport.getModelName()))  // モデル名
                        .filter(item -> Objects.equals(item.getWorkflowId(), oldWorkReport.getWorkflowId())) // 工程順
                        .filter(item -> Objects.equals(item.getWorkId(), oldWorkReport.getWorkId())) // 工程
                        .filter(item -> Objects.equals(item.getProductionNumber(), oldWorkReport.getProductionNumber())) // 製造番号
                        .filter(item -> Objects.equals(item.getWorkDate(), oldWorkReport.getWorkDate())) // 実施日
                        .filter(item -> Objects.equals(item.getOrganizationIdentify(), oldWorkReport.getOrganizationIdentify())) // 組織
                        .mapToLong(WorkReportEntity::getWorkTime)
                        .sum();

                if (workingTime != oldWorkReport.getWorkTime()) {
                    Date day;
                    try {
                        day = sf.parse(oldWorkReport.getWorkDate());
                    } catch (ParseException ex) {
                        logger.fatal(ex, ex);
                        return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    }

                    // 差分を登録
                    DirectActualEntity directActualEntity = new DirectActualEntity(
                            oldWorkReport.getWorkType(),
                            day,
                            oldWorkReport.getOrganizationId(),
                            oldWorkReport.getWorkId(),
                            oldWorkReport.getWorkName(),
                            oldWorkReport.getOrderNumber(),
                            (int) (workingTime - oldWorkReport.getWorkTime()),
                            oldWorkReport.getWorkflowId(),
                            oldWorkReport.getKanbanName(),
                            oldWorkReport.getModelName(),
                            0,
                            oldWorkReport.getWorkTypeOrder(),
                            oldWorkReport.getProductionNumber(),
                            oldWorkReport.getClassKey(),
                            authId);
                    add(directActualEntity);
                    logger.info("update DirectActual new={}, {}", workingTime, oldWorkReport);
                }
            }
            return ResponseEntity.success();
        } catch (Exception ex) {
            logger.fatal(ex,ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * オーダ毎の集計日報の直接工数の更新
     *
     * @param workReportListEntity 変更後のエンティティ
     * @param authId             更新者
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("order")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public ResponseEntity updateOrderActual(WorkReportListEntity workReportListEntity, @QueryParam("authId") Long authId) {
        logger.info("updateOrderActual, authId={}", authId);

        if (Objects.isNull(workReportListEntity) || Objects.isNull(workReportListEntity.getWorkReportEntityList())) {
            logger.info("not Found Data");
            return ResponseEntity.success();
        }

        final List<WorkReportEntity> workReportEntities
                = workReportListEntity.getWorkReportEntityList()
                .stream()
                .filter(entity -> entity.getWorkType() == 0)
                .filter(entity -> Objects.nonNull(entity.getWorkDate()))
                .filter(entity -> Objects.nonNull(entity.getOrganizationId()))
                .collect(toList());

        if (workReportEntities.isEmpty()) {
            logger.info("not Found Data");
            return ResponseEntity.success();
        }

        final String fromDate = workReportEntities.stream().map(WorkReportEntity::getWorkDate).min(Comparator.naturalOrder()).get();
        final String toDate = workReportEntities.stream().map(WorkReportEntity::getWorkDate).max(Comparator.naturalOrder()).get();
        final List<Long> organizationIds = workReportEntities.stream().map(WorkReportEntity::getOrganizationId).collect(toList());

        try (Connection connection = this.em.unwrap(Connection.class)) {
            java.sql.Array idArray = connection.createArrayOf("integer", organizationIds.toArray());
            // オーダ毎の日報取得
            TypedQuery<WorkReportEntity> query = this.em.createNamedQuery("DirectActualEntity.findDirectWorkDailyOrder2", WorkReportEntity.class);
            query.setParameter(1, fromDate);
            query.setParameter(2, toDate);
            query.setParameter(3, idArray);
            List<WorkReportEntity> oldWorkReportList = query.getResultList();

            for (WorkReportEntity oldWorkReport : oldWorkReportList) {
                final long workingTime
                        = workReportEntities
                        .stream()
                        .filter(item -> Objects.equals(item.getWorkDate(), oldWorkReport.getWorkDate())) // 実施日
                        .filter(item -> Objects.equals(item.getOrganizationIdentify(), oldWorkReport.getOrganizationIdentify())) // 組織
                        .filter(item -> Objects.equals(item.getWorkId(), oldWorkReport.getWorkId())) // 工程
                        .filter(item -> Objects.equals(item.getOrderNumber(), oldWorkReport.getOrderNumber())) // オーダー
                        .filter(item -> Objects.equals(item.getWorkflowId(), oldWorkReport.getWorkflowId())) // 工程順
                        .filter(item -> Objects.equals(item.getModelName(), oldWorkReport.getModelName()))  // モデル名
                        .mapToLong(WorkReportEntity::getWorkTime)
                        .sum();

                if (workingTime != oldWorkReport.getWorkTime()) {
                    Date day;
                    try {
                        day = sf.parse(oldWorkReport.getWorkDate());
                    } catch (ParseException ex) {
                        logger.fatal(ex, ex);
                        return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
                    }
                    // 差分を登録
                    DirectActualEntity directActualEntity = new DirectActualEntity(
                            oldWorkReport.getWorkType(),
                            day,
                            oldWorkReport.getOrganizationId(),
                            oldWorkReport.getWorkId(),
                            oldWorkReport.getWorkName(),
                            oldWorkReport.getOrderNumber(),
                            (int) (workingTime - oldWorkReport.getWorkTime()),
                            oldWorkReport.getWorkflowId(),
                            oldWorkReport.getKanbanName(),
                            oldWorkReport.getModelName(),
                            0,
                            oldWorkReport.getWorkTypeOrder(),
                            oldWorkReport.getProductionNumber(),
                            oldWorkReport.getClassKey(),
                            authId);
                    add(directActualEntity);
                    logger.info("update DirectActual new={}, {}", workingTime, oldWorkReport);
                }
            }
            return ResponseEntity.success();
        } catch (Exception ex) {
            logger.fatal(ex,ex);
            return ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL);
        }
    }

    /**
     * 製造番号のフォーマットを整形
     *
     * @param src 整形元
     * @return 整形した製造番号
     */
    static public List<String> formatControlNumber(String src) {
        if (StringUtils.isEmpty(src)) {
            return null;
        }

        List<List<Integer>> progList
                = Stream.of(src.replace(" ", "").split(","))
                .filter(str -> !StringUtils.isEmpty(str))
                .map(str -> str.split("-"))
                .map(strList -> Stream.of(strList).map(Integer::parseInt).collect(toList()))
                .map(strList -> strList.size() >= 2
                        ? Arrays.asList(Math.min(strList.get(0), strList.get(1)), Math.max(strList.get(0), strList.get(1)))
                        : Arrays.asList(strList.get(0), strList.get(0)))
                .sorted(Comparator.comparing(item -> item.get(0)))
                .collect(toList());

        List<List<Integer>> range = new ArrayList<>();
        range.add(progList.get(0));
        progList.forEach(item -> {
            List<Integer> last = range.get(range.size() - 1);
            if (last.get(1) + 1 >= item.get(0)) {
                last.set(1, Math.max(item.get(1), last.get(1)));
            } else {
                range.add(item);
            }
        });

        return range.stream()
                .map(item -> Objects.equals(item.get(0), item.get(1))
                        ? String.valueOf(item.get(0))
                        : (String.valueOf(item.get(0)) + '-' + String.valueOf(item.get(1))))
                .collect(Collectors.toList());
    }

    /**
     * 桁数を計算
     * @param src 計測
     * @return 桁数
     */
    static public int calculateDigit(String src) {
        if (StringUtils.isEmpty(src)) {
            return 0;
        }

        return Stream.of(src.replace(" ", "").split(",|-"))
                .filter(str -> !StringUtils.isEmpty(str))
                .mapToInt(String::length)
                .max()
                .orElse(0);

    }


}
