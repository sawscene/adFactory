package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.OperationSerachCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectActualEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.*;
import jp.adtekfuji.adfactoryserver.model.FileManager;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
@Path("operation")
public class OperationEntityFacadeREST extends AbstractFacade<OperationEntity> {

    private final Logger logger = LogManager.getLogger();

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private BreaktimeEntityFacadeREST breaktimeEntityFacadeREST;

    @EJB
    public IndirectWorkEntityFacadeREST indirectWorkEntityFacadeREST;

    @EJB
    private IndirectActualEntityFacadeREST indirectActualEntityFacadeREST;

    @EJB
    private EquipmentEntityFacadeREST equipmentRest;

    @EJB
    private ActualResultEntityFacadeREST actualResultRest;

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * コンストラクタ
     */
    public OperationEntityFacadeREST() {
        super(OperationEntity.class);
    }

    public OperationEntityFacadeREST(Class<OperationEntity> entityClass) {
        super(entityClass);
    }

    /**
     * EntityManager を取得する。
     *
     * @return EntityManager
     */
    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * 作業者操作実績を追加する
     *
     * @param entity 作業者操作実績情報
     * @return 処理結果
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response add(OperationEntity entity) {
        logger.info("add:{}", entity);
        try {
            if (Objects.isNull(entity.getOperateDatetime())) {
                return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.INVALID_ARGUMENT)).build();
            }

            super.create(entity);

            return Response.ok().entity(ResponseEntity.success().errorType(ServerErrorTypeEnum.SUCCESS)).build();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().entity(ResponseEntity.failed(ServerErrorTypeEnum.SERVER_FETAL)).build();
        }
    }

    /**
     *  操作開始を登録
     * @param entity
     * @param authId
     * @return
     */
    @Lock(LockType.READ)
    @PUT
    @Path("start")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response registerStart(OperationEntity entity, @QueryParam("authId") Long authId) {
        logger.info("registerStart :{}", entity);


        Date date = entity.getOperateDatetime();
        if (Objects.isNull(date)) {
            logger.fatal("date parse Error");
            return Response.serverError().build();
        }

        Properties properties = FileManager.getInstance().getSystemProperties();
        final boolean enableMultiIndirectWork = Boolean.parseBoolean(properties.getProperty("enableMultiIndirectWork", "false"));

        if (!enableMultiIndirectWork) {

            // 既に作業中の場合、許可しない
            ActualSearchCondition actualCondition = new ActualSearchCondition()
                    .organizationList(Collections.singletonList(entity.getOrganizationId()))
                    .resultDailyEnum(ActualResultDailyEnum.ALL);

            ActualResultEntity lastActual = this.actualResultRest.findLastActualResult(actualCondition, null);
            if (Objects.nonNull(lastActual)
                    && (lastActual.getActualStatus() == KanbanStatusEnum.WORKING || lastActual.getActualStatus() == KanbanStatusEnum.OTHER)) {
                logger.warn("Not allowed to working: " + lastActual.toString());
                return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION).userData(lastActual.getEquipmentName())).build();
            }

            // 実施済みの間接作業があった場合は中断にする。
            Response response = completeIndirectWork(entity.getOperateDatetime(), entity.getOrganizationId(), authId);
            if (!((ResponseEntity)response.getEntity()).isSuccess()) {
                return response;
            }
        }


        // 間接作業は中断中の工程がある場合、工程IDを残す
        if ((OperateAppEnum.ADPRODUCT.equals(entity.getOperateApp()) || OperateAppEnum.ADPRODUCTWEB.equals(entity.getOperateApp())) && OperationTypeEnum.INDIRECT_WORK.equals(entity.getOperationType())) {
            final List<ActualResultEntity> suspendingActualResult = this.actualResultRest.findSuspendingActualResult(entity.getEquipmentId(), entity.getOrganizationId());
            if (Objects.nonNull(suspendingActualResult) && !suspendingActualResult.isEmpty()) {
                OperationAddInfoEntity opeAddInfo = entity.getAddInfo();
                if (Objects.isNull(opeAddInfo)) {
                    opeAddInfo = new OperationAddInfoEntity();
                }
                IndirectWorkOperationEntity indirectWorkOperation = opeAddInfo.getIndirectWork();
                if (Objects.isNull(indirectWorkOperation)) {
                    indirectWorkOperation = new IndirectWorkOperationEntity();
                    opeAddInfo.setIndirectWork(indirectWorkOperation);
                }

                final List<Long> suspendingActualIds = suspendingActualResult.stream().map(ActualResultEntity::getActualId).collect(toList());
                indirectWorkOperation.setSuspendActualResultId(suspendingActualIds);
                entity.setAddInfo(opeAddInfo);
            }
        }

        IndirectWorkOperationEntity indirectEntity = entity.getAddInfo().getIndirectWork();
        if (Objects.nonNull(indirectEntity)) {
            final IndirectWorkEntity indirectWorkEntity = indirectWorkEntityFacadeREST.find(indirectEntity.getIndirectWorkId(), authId);
            if (Objects.isNull(indirectWorkEntity)) {
                return Response.serverError().build();
            }
        }

        super.create(entity);
        this.em.flush();
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 間接作業を実施中出会った場合間接作業を中止する
     * @param datetime         実施日時
     * @param organizationId   設備ID
     * @param equipmentId      組織ID
     * @param operateApp       アプリ種
     * @param authId           設備IDThe authentication ID
     * @return ture:成功, false:失敗
     */
    public Response completeIndirectWork(Date datetime, Long organizationId, Long authId) {
        OperationSerachCondition operationCondition = new OperationSerachCondition(null, organizationId, OperateAppEnum.UNKNOWN, OperationTypeEnum.INDIRECT_WORK);
        OperationEntity lastOperation = this.getLastOperation(operationCondition, authId);
        if (Objects.nonNull(lastOperation)
                && Objects.nonNull(lastOperation.getAddInfo())
                && Objects.nonNull(lastOperation.getAddInfo().getIndirectWork())
                && Objects.nonNull(lastOperation.getAddInfo().getIndirectWork().getDoIndirect())
                && lastOperation.getAddInfo().getIndirectWork().getDoIndirect()) {
            logger.warn("Not allowed to working: " + lastOperation);

            OperationAddInfoEntity operationAddInfoEntity = lastOperation.getAddInfo();
            IndirectWorkOperationEntity indirectWorkOperationEntity = operationAddInfoEntity.getIndirectWork();
            indirectWorkOperationEntity.setDoIndirect(false);
            indirectWorkOperationEntity.setReason(null);

            operationAddInfoEntity.setIndirectWork(indirectWorkOperationEntity);
            OperationEntity operationEntity
                    = new OperationEntity(datetime, lastOperation.getEquipmentId(), organizationId, lastOperation.getOperateApp(), lastOperation.getOperationType(), operationAddInfoEntity);
            return this.registerComp(operationEntity, authId);
        }
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 操作を完了する
     *
     * @param operation 作業者操作実績
     * @param authId 実行者ID
     * @return 処理結果
     */
    @Lock(LockType.READ)
    @PUT
    @Path("comp")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public Response registerComp(OperationEntity operation, @QueryParam("authId") Long authId) {
        logger.info("registerComp start: {}", operation);

        try {
            Date date = operation.getOperateDatetime();
            if (Objects.isNull(date)) {
                logger.fatal("date parse Error");
                return Response.serverError().build();
            }

            // 最後の実績を取得
            OperationSerachCondition condition = new OperationSerachCondition(operation.getEquipmentId(), operation.getOrganizationId(), operation.getOperateApp(), operation.getOperationType());
            if (OperateAppEnum.ADPRODUCTLITE.equals(operation.getOperateApp())) {
                condition.setEquipmentId(null);
            }

            OperationEntity lastEntity = getLastOperation(condition);

            OperationAddInfoEntity addInfoEntity = operation.getAddInfo();
            if (Objects.isNull(addInfoEntity)) {
                addInfoEntity = new OperationAddInfoEntity();
            }

            switch (operation.getOperationType()) {
                // 呼出
                case CALL: {
                        CallOperationEntity opAddInfo = addInfoEntity.getCallOperationEntity();
                        if (Objects.isNull(opAddInfo)) {
                            opAddInfo = new CallOperationEntity();
                        }

                        if (Objects.nonNull(lastEntity)
                                && lastEntity.getAddInfo().getCallOperationEntity().getCall()) {
                            opAddInfo.setPairId(lastEntity.getOperationId());
                        }
                        addInfoEntity.setCallOperationEntity(opAddInfo);
                    }
                    break;
                // 間接作業
                case INDIRECT_WORK: {
                        if (Objects.isNull(lastEntity)) {
                            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKINGWORK)).build();
                        }

                        OperationAddInfoEntity lastOpAddInfoEntity = lastEntity.getAddInfo();
                        if (Objects.isNull(lastOpAddInfoEntity)) {
                            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKINGWORK)).build();
                        }

                        IndirectWorkOperationEntity lastOpAddInfo = lastOpAddInfoEntity.getIndirectWork();
                        if (Objects.isNull(lastOpAddInfo)) {
                            return Response.ok().entity(ResponseEntity.failed(ServerErrorTypeEnum.NOTFOUND_WORKINGWORK)).build();
                        }

                        if (Objects.isNull(lastOpAddInfo.getDoIndirect()) || !lastOpAddInfo.getDoIndirect()) {
                            // 既に間接作業が完了されている
                            logger.warn("The work has already been completed: {}", lastEntity);
                            return Response.ok().entity(ResponseEntity.success()).build();
                        }

                        IndirectWorkOperationEntity opAddInfo = addInfoEntity.getIndirectWork();
                        if (Objects.isNull(opAddInfo)) {
                            opAddInfo = new IndirectWorkOperationEntity();
                        }

                        // 実績登録
                        opAddInfo.setDoIndirect(false);
                        opAddInfo.setPairId(lastEntity.getOperationId());
                        addInfoEntity.setIndirectWork(opAddInfo);
                        registerIndicateActual(lastEntity, operation, authId);
                    }
                    break;
                default:
                    break;
            }

            operation.setAddInfo(addInfoEntity);

            super.create(operation);
            this.em.flush();
            return Response.ok().entity(ResponseEntity.success()).build();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return Response.serverError().build();

        } finally {
            logger.info("registerComp end.");
        }
    }

    Tuple<Date, Date> calculateOneDate(Date date) {
        final Date fromDate = adtekfuji.utility.DateUtils.getBeginningOfDate(date);
        final Date toDate = adtekfuji.utility.DateUtils.getEndOfDate(date);
        return new Tuple<>(fromDate, toDate);
    }

    /**
     * 間接作業実績を登録する。
     *
     * @param from 作業者操作実績
     * @param to 作業者操作実績
     * @param authId 実行者ID
     * @throws Exception
     */
    void registerIndicateActual(OperationEntity from, OperationEntity to, Long authId) throws Exception {
        Long workTime = this.calculateWorkTime(from.getOperateDatetime(), to.getOperateDatetime(), from.getOrganizationId());

        OperationAddInfoEntity addInfoEntity = from.getAddInfo();
        if (Objects.isNull(addInfoEntity)) {
            return;
        }

        IndirectWorkOperationEntity indirectWorkOperationEntity = addInfoEntity.getIndirectWork();
        if (Objects.isNull(indirectWorkOperationEntity)) {
            return;
        }

        Long indirectWorkId = indirectWorkOperationEntity.getIndirectWorkId();
        if (Objects.isNull(indirectWorkId)) {
            return;
        }

        // 検索対象の日にち
        Tuple<Date, Date> oneDate = calculateOneDate(to.getOperateDatetime());

        // 実績作業日時を設定
        TypedQuery<IndirectActualEntity> query = this.em.createNamedQuery("IndirectActualEntity.findByIndirectWorkId", IndirectActualEntity.class);
        query.setParameter("indirectWorkId", indirectWorkId);
        query.setParameter("organizationId", from.getOrganizationId());
        query.setParameter("fromDate", oneDate.getLeft());
        query.setParameter("toDate", oneDate.getRight());
        query.setMaxResults(1);

        List<IndirectActualEntity> list = query.getResultList();
        if (!list.isEmpty()) {
            IndirectActualEntity indirectActual = list.get(0);
            indirectActual.setProductionNum(indirectWorkOperationEntity.getProductionNum());
            indirectActual.setWorkTime(indirectActual.getWorkTime() + workTime.intValue());
            this.indirectActualEntityFacadeREST.update(indirectActual, authId);
            return;
        }

        IndirectActualEntity newIndirectActual = new IndirectActualEntity(null, indirectWorkId, from.getOperateDatetime(), 0, from.getOrganizationId(), workTime.intValue());
        newIndirectActual.setProductionNum(indirectWorkOperationEntity.getProductionNum());

        this.indirectActualEntityFacadeREST.add(newIndirectActual, authId);
    }

    /**
     * 休憩を引いた作業時間を計算
     * @param from 開始時間
     * @param to 終了時間
     * @param organizationId 組織ID
     * @return 作業時間
     */
    private long calculateWorkTime(Date from, Date to, Long organizationId) {
        // 休憩時間を取得
        List<BreaktimeEntity> breakTimeEntities =  breaktimeEntityFacadeREST.findByOrganizationId(organizationId);
        if (Objects.isNull(breakTimeEntities)) {
            breakTimeEntities = new ArrayList<>();
        }

        // 作業開始時間
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(from);

        // 作業終了時間
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(to);

        // 作業(開始, 完了)
        Tuple<Calendar, Calendar> workEntity = new Tuple<>(startTime, endTime);

        // 作業(開始 - 完了) 間に発生した休憩時間
        List<Tuple<Calendar, Calendar>> breakTimeActualEntities
                = breakTimeEntities
                .stream()
                .map(OperationEntityFacadeREST::convertBreakTimePair)
                .map(breakEntity -> convertBreakTimeDatePair(workEntity, breakEntity))
                .flatMap(Collection::stream)
                .collect(toList());

        // 休憩時間を削除
        List<Tuple<Calendar, Calendar>> workEntities = Collections.singletonList(workEntity);
        for (Tuple<Calendar, Calendar> breakTimeActualEntity : breakTimeActualEntities) {
            workEntities = removeBreakTime(workEntities, breakTimeActualEntity);
        }

        // 作業時間を計算(msec)
        return workEntities
                .stream()
                .mapToLong(item -> item.getRight().getTime().getTime() -  item.getLeft().getTime().getTime())
                .sum();
    }

    /**
     * 最後の操作実績情報を取得する。
     *
     * @param condition 検索条件
     * @return 操作実績情報
     */
    private OperationEntity getLastOperation(OperationSerachCondition condition) {
        String sql = "Select o FROM OperationEntity o";

        String where = Stream.of(
                        Objects.isNull(condition.getEquipmentId()) ? "" : ("o.equipmentId = " + condition.getEquipmentId()),
                        Objects.isNull(condition.getOrganizationId()) ? "" : ("o.organizationId = " + condition.getOrganizationId()),
                        Objects.isNull(condition.getOperateApp()) || OperateAppEnum.UNKNOWN.equals(condition.getOperateApp()) ? "" : ("o.operateApp = '" + condition.getOperateApp().getName() + "'"),
                        Objects.isNull(condition.getOperationType()) ? "" : ("o.operationType = '" + condition.getOperationType().getName() + "'"))
                .filter(o -> !StringUtils.isEmpty(o))
                .collect(joining(" AND "));

        if (!StringUtils.isEmpty(where)) {
            sql += (" WHERE " + where);
        }

        sql += " ORDER BY o.operationId DESC";

        TypedQuery<OperationEntity> query = em.createQuery(sql, OperationEntity.class);
        query.setMaxResults(1);
        List<OperationEntity> list = query.getResultList();

        if (list.isEmpty()) {
            // logger.fatal("Error!! not find Pair");
            return null;
        }

        em.clear();
        return list.get(0);
    }

    /**
     * 最後の操作実績情報を取得する。
     *
     * @param condition 検索条件
     * @param authId 組織ID
     * @return 操作実績情報
     */
    @Lock(LockType.READ)
    @PUT
    @Path("last")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public OperationEntity getLastOperation(OperationSerachCondition condition, @QueryParam("authId") Long authId) {
        logger.info("start getLastOperation");

        try {
            OperationEntity operation = this.getLastOperation(condition);
            if (Objects.isNull(operation)) {
                return null;
            }

            if (OperationTypeEnum.INDIRECT_WORK.equals(condition.getOperationType())) {
                if (Objects.nonNull(operation.getAddInfo())
                    && Objects.nonNull(operation.getAddInfo().getIndirectWork())
                    && Objects.nonNull(operation.getAddInfo().getIndirectWork().getDoIndirect())
                    && operation.getAddInfo().getIndirectWork().getDoIndirect()) {

                    // 現時点の作業時間を計算
                    long workTime = calculateWorkTime(operation.getOperateDatetime(), new Date(), operation.getOrganizationId());
                    operation.setWorkTime(workTime);
                }
            }

            logger.info("end getLastOperation {}", operation);
            return operation;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 最後の操作実績情報を取得する。
     *
     * @param condition 検索条件
     * @param authId 組織ID
     * @return 操作実績情報一覧
     */
    @Lock(LockType.READ)
    @PUT
    @Path("last/list")
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<OperationEntity> getLastOperationList(OperationSerachCondition condition, @QueryParam("authId") Long authId) {
        logger.info("getLastOperationList: {}, authId={}", condition, authId);
        List<OperationEntity> resultList = new ArrayList<>();

        if (Objects.isNull(condition.getOrganizationCollection()) && condition.getOrganizationCollection().isEmpty()) {
            return resultList;
        }

        for (Long organizationId : condition.getOrganizationCollection()) {
            condition.setOrganizationId(organizationId);

            OperationEntity operation = this.getLastOperation(condition, authId);

            if (Objects.nonNull(operation)) {
                resultList.add(operation);
            }
        }

        return resultList;
    }

    /**
     * 日にちを変換
     */
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    static Date parseDate(String dateText) {
        if(StringUtils.isEmpty(dateText)) {
            return null;
        }

        try {
            return dateFormat.parse(dateText);
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * 休憩時間の開始と完了をペアにする
     * @param entity
     * @return
     */
    static Tuple<Calendar, Calendar> convertBreakTimePair(BreaktimeEntity entity) {
        Calendar s = Calendar.getInstance();
        s.setTime(entity.getStarttime());
        Calendar e = Calendar.getInstance();
        e.setTime(entity.getEndtime());
        return new Tuple<>(s, e);
    }

    /**
     * 実休憩日時をペアにする
     * @param workTimePair
     * @param breakTime
     * @return
     */
    static List<Tuple<Calendar, Calendar>> convertBreakTimeDatePair(Tuple<Calendar, Calendar> workTimePair, Tuple<Calendar, Calendar> breakTime) {
        final Calendar s = workTimePair.getLeft();
        Calendar d = (Calendar) s.clone();
        final Calendar e = workTimePair.getRight();

        List<Tuple<Calendar, Calendar>> ret = new ArrayList<>();
        for(;d.before(e); d.add(Calendar.DATE, 1)) {
            final int year = d.get(Calendar.YEAR);
            final int month = d.get(Calendar.MONTH);
            final int date = d.get(Calendar.DATE);

            Calendar breakStart = (Calendar) breakTime.getLeft().clone();
            breakStart.set(Calendar.YEAR, year);
            breakStart.set(Calendar.MONTH, month);
            breakStart.set(Calendar.DATE, date);

            Calendar breakEnd = (Calendar) breakTime.getRight().clone();
            breakEnd.set(Calendar.YEAR, year);
            breakEnd.set(Calendar.MONTH, month);
            breakEnd.set(Calendar.DATE, date);
            ret.add(new Tuple<>(breakStart, breakEnd));
        }
        return ret;
    }

    /**
     * 稼働時間から休憩時間を削除
     * @param workEntities
     * @param breakTimeEntity
     * @return
     */
    List<Tuple<Calendar, Calendar>> removeBreakTime(List<Tuple<Calendar, Calendar>> workEntities, Tuple<Calendar, Calendar> breakTimeEntity) {
        final Calendar breakStart = breakTimeEntity.getLeft();
        final Calendar breakEnd = breakTimeEntity.getRight();

        List<Tuple<Calendar, Calendar>> ret = new ArrayList<>();
        workEntities
            .forEach(entity-> {
                Calendar start = entity.getLeft();
                Calendar end = entity.getRight();

                if (start.after(breakEnd) || end.before(breakStart)) {
                    ret.add(entity);
                    return;
                }

                if (breakStart.after(start)) {
                    ret.add(new Tuple<>(start, breakStart));
                }

                if (breakEnd.before(end)) {
                    ret.add(new Tuple<>(breakEnd, end));
                }
            });
        return ret;
    }

}
