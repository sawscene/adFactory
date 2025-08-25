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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jp.adtekfuji.adFactory.adinterface.command.SummaryReportNoticeCommand;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.*;
import jp.adtekfuji.adFactory.enumerate.AggregateUnitEnum;
import jp.adtekfuji.adFactory.enumerate.CategoryEnum;
import jp.adtekfuji.adFactory.enumerate.SendFrequencyEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.entity.summaryreport.*;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



@Singleton
@Path("summary-report")
public class SummaryReportFacadeREST {

    final static double TARGET_VALUE_CONVERT_TO_MIRISEC = 60*1000;

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    @EJB
    private OrganizationEntityFacadeREST organizationRest;


    @EJB
    private AdIntefaceClientFacade adIntefaceFacade;

    @EJB
    private WorkflowEntityFacadeREST workflowEntityFacadeREST;


    private final Logger logger = LogManager.getLogger();


    /**
     * テストメール送信
     * @param index 送信インデックス
     * @param authId 実施者
     * @return 結果
     */
    @Lock(LockType.READ)
    @POST
    @Path("/send-mail/{id}")
    @ExecutionTimeLogging
    public Response sendMail(@PathParam("id") int index, @QueryParam("authId") Long authId) {
        logger.info("sendMail : {}", index);
        SummaryReportNoticeCommand command = new SummaryReportNoticeCommand(SummaryReportNoticeCommand.COMMAND.SEND_MAIL);
        command.setConfig(JsonUtils.objectToJson(new SummaryReportNoticeCommand.SendMailConfig(index)));
        this.adIntefaceFacade.noticeSummaryReport(command);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    /**
     * 
     * @param authId
     * @return 
     */
    @Lock(LockType.READ)
    @PUT
    @Path("reload-config")
    @ExecutionTimeLogging
    public Response loadSummaryReportConfig(@QueryParam("authId") Long authId) {
        SummaryReportNoticeCommand command = new SummaryReportNoticeCommand(SummaryReportNoticeCommand.COMMAND.LOAD_CONFIG);
        this.adIntefaceFacade.noticeSummaryReport(command);
        return Response.ok().entity(ResponseEntity.success()).build();
    }

    static <T> Optional<SummaryReportEntityElement> createSummaryReportEntities(CategoryEnum elementType, Supplier<Optional<List<T>>> supplier) {
        return supplier.get()
                .map(JsonUtils::objectsToJson)
                .map(elem->new SummaryReportEntityElement(elementType, elem));
    }

    static final SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    @Lock(LockType.READ)
    @POST
    @Path("calculate")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public SummaryReportEntity calculate(SummaryReportConfigEntity entity, @QueryParam("authId") Long authId) {
        logger.info("calculate {} ", entity);

        if (Objects.isNull(entity)) {
            return new SummaryReportEntity();
        }

        Date toDate;
        try {
            toDate = sf.parse(entity.getSendDate());
        } catch (ParseException ex) {
            logger.error(ex);
            return new SummaryReportEntity();
        }

        // 解析期間を算出
        final Tuple<Date, Date> frequency = calcFrequency(toDate, entity.getSendFrequency());

        // 期間内に完了した工程カンバンの情報
        final Optional<List<WorkResultEntity>> optActualWorkKanbanWorkInfoEntities = getActualWorkKanbanWorkInfo(entity.getAggregateUnit(), entity.getItemName(), frequency.getLeft(),  frequency.getRight());
//        if (!optActualWorkKanbanWorkInfoEntities.isPresent()) {
//            return new SummaryReportEntity();
//        }
        final List<WorkResultEntity> workKanbanWorkInfoEntities = optActualWorkKanbanWorkInfoEntities.orElse(new ArrayList<>());

        // 期間内に終了した工程カンバンを工程カンバン毎にまとめた物
        final List<WorkResultEntity> workKanbanWorkTimeEntities = calculateWorkKanbanWorkTime(workKanbanWorkInfoEntities);

        // 期間内に完了したカンバンの情報
        final Optional<List<WorkResultEntity>> optActualKanbanWorkInfoEntities = getActualKanbanWorkInfo(entity.getAggregateUnit(), entity.getItemName(), frequency.getLeft(), frequency.getRight());
        final List<WorkResultEntity> kanbanWorkInfoEntities = optActualKanbanWorkInfoEntities.orElse(new ArrayList<>());

        // 期間内に終了したカンバンを工程カンバン毎にまとめた物
        final List<WorkResultEntity> kanbanWorkTimeEntities = calculateWorkKanbanWorkTime(kanbanWorkInfoEntities);

        final Optional<List<String>> optWorkNameList = getWorkNameList(entity.getAggregateUnit(), entity.getItemName());
        if (!optWorkNameList.isPresent()) {
            return new SummaryReportEntity();
        }
        final List<String> workNameLis = optWorkNameList.get();

        SummaryReportEntity ret = new SummaryReportEntity();
        ret.title = entity.getTitle();
        ret.period = String.format("%s - %s", sf.format(frequency.getLeft()), sf.format(frequency.getRight()));
        ret.aggregateUnit = entity.getAggregateUnit().getValue();
        ret.itemName = entity.getItemName();

        ret.summaryReportEntityElements = entity
                .getSummaryReportElementEntities()
                .stream()
                .map(summaryReportElementEntity -> {
                    final CategoryEnum type = summaryReportElementEntity.getElementType();
                    switch (type) {
                        // 製品の生産数
                        case NUMBER_OF_PRODUCTS_PRODUCED:
                            return createSummaryReportEntities(type, () -> calculateProductNum(entity.getAggregateUnit(), entity.getItemName(), summaryReportElementEntity, frequency.getLeft(), frequency.getRight()));
                        // 工程の生産数
                        case NUMBER_OF_PROCESSES_PRODUCED:
                            return createSummaryReportEntities(type, () -> calculateWorkNum(entity.getAggregateUnit(), entity.getItemName(), workNameLis, summaryReportElementEntity, frequency.getLeft(), frequency.getRight()));

                        // 製品の平均作業時間
                        case AVERAGE_PRODUCT_WORKING_HOURS:
                            return createSummaryReportEntities(type, () -> calculateAverageOfProduct(summaryReportElementEntity, kanbanWorkTimeEntities));
                        // 工程の平均作業時間
                        case WORK_AVERAGE_WORK_TIME:
                            return createSummaryReportEntities(type, () -> calculateAverageOfWork(summaryReportElementEntity, workNameLis, workKanbanWorkTimeEntities));

                        // ライン全体の稼働率
                        case OVERALL_LINE_UTILIZATION:
                            return createSummaryReportEntities(type, () -> calculateSumProductOfProduct(summaryReportElementEntity,workKanbanWorkTimeEntities));
                        // 作業者毎の稼働率
                        case OPERATING_RATE_PER_WORKER:
                            return createSummaryReportEntities(type, () -> calculateSumProductOfWorker(summaryReportElementEntity, workKanbanWorkTimeEntities));

                        // 工程内作業のバラツキ
                        case IN_PROCESS_WORK_VARIATION:
                            return createSummaryReportEntities(type, () -> calculateWorkVariation(workKanbanWorkTimeEntities));
                        // 作業者間のバラツキ(工程内)
                        case VARIATION_AMONG_WORKERS:
                            return createSummaryReportEntities(type, () -> calculateWorkWorkerVariation(workKanbanWorkTimeEntities));
                        // 設備間のバラツキ(工程内)
                        case VARIATION_IN_EQUIPMENT_COMPLETION:
                            return createSummaryReportEntities(type, () -> calculateWorkEquipmentVariation(workKanbanWorkTimeEntities));

                        // ラインバランス
                        case LINE_BALANCE:
                            return createSummaryReportEntities(type, () -> calculateLineBalance(kanbanWorkTimeEntities));
                        // 工程間待ち時間
                        case INTER_PROCESS_WAITING_TIME:
                            return createSummaryReportEntities(type, () -> calculateWaitTime(kanbanWorkTimeEntities));

                        // 遅延ランキング
                        case DELAY_RANKING:
                            return createSummaryReportEntities(type, () -> calculateDelayWorkTime(workKanbanWorkInfoEntities));
                        // 中断ランキング
                        case INTERRUPT_RANKING:
                            return createSummaryReportEntities(type, () -> calculateInterruptTime(workKanbanWorkInfoEntities));
                        //呼出ランキング
                        case CALL_RANKING:
                            return createSummaryReportEntities(type, () -> calculateCallCount(entity.getAggregateUnit(), entity.getItemName(), frequency.getLeft(), frequency.getRight()));
                        default:
                            return Optional.empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(l -> (SummaryReportEntityElement) l)
                .collect(toList());

        return ret;
    }

    /**
     * 工程カンバンの作業時間算出
     * @param workResultEntities
     * @return
     */
    static List<WorkResultEntity> calculateWorkKanbanWorkTime(List<WorkResultEntity> workResultEntities)
    {
        return new ArrayList<>(
                workResultEntities.stream()
                .collect(groupingBy(
                        entity->entity.workKanbanId,
                        collectingAndThen(
                                toList(),
                                entities-> {
                                    WorkResultEntity ret = entities.get(0).copy();
                                    ret.workTime
                                            = entities
                                            .stream()
                                            .mapToLong(entity->entity.workTime)
                                            .sum();
                                    return ret;
                                }
                        )))
                .values());
    }

    /**
     * 工程待ち時間算出
     * @param workResultEntities 待ち時間
     * @return 待ち時間リスト
     */
    Optional<List<InterProcessWaitingTimeEntity>> calculateWaitTime(List<WorkResultEntity> workResultEntities)
    {
        // 工程順Id取得
        final List<Long> workflowId
                = workResultEntities
                .stream()
                .map(entity->entity.workflowId)
                .distinct()
                .collect(toList());

        // 工程ダイアグラムを解析
        final List<WorkflowEntity> workflowEntities = workflowEntityFacadeREST.find(workflowId, null);
        List<Map<String, WorkflowDiaglam>> diagonales
                = workflowEntities
                .stream()
                .map(WorkflowEntity::getWorkflowDiaglam)
                .map(SummaryReportFacadeREST::analyzeWorkflowDialog)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        if (diagonales.size() != workflowId.size()) {
            return Optional.empty();
        }

        List<InterProcessWaitingTimeEntity> ret = new ArrayList<>();
        for (Map<String, WorkflowDiaglam> diagonal : diagonales) {

            WorkflowDiaglam workflowDiaglam = diagonal.get("start_id");
            if (Objects.isNull(workflowDiaglam)) {
                return Optional.empty();
            }

            // 解析開始
            WaitTimeVisitor visitor = new WaitTimeVisitor(workResultEntities);
            workflowDiaglam.accept(visitor);

            ret.addAll(visitor.result);
        }

        return Optional.of(ret);
    }


    /**
     * ラインバランスを算出
     * @param workResultEntities 実績データ
     * @return ライバランス結果
     */
    Optional<List<LineBalanceEntity>> calculateLineBalance(List<WorkResultEntity> workResultEntities)
    {
        // 工程順IDを取得
        final Map<Long, String> workflowIdMap
                = workResultEntities
                .stream()
                .collect(toMap(entity->entity.workflowId, entity -> entity.workflowName, (a,b)->a));
//                .map(entity->entity.workflowId)
//                .distinct()
//                .collect(toList());

        // 工程順ダイアグラムを解析
        final List<WorkflowEntity> workflowEntities = workflowEntityFacadeREST.find(new ArrayList<>(workflowIdMap.keySet()), null);
        List<Map<String, WorkflowDiaglam>> diagonales
                = workflowEntities
                .stream()
                .map(WorkflowEntity::getWorkflowDiaglam)
                .map(SummaryReportFacadeREST::analyzeWorkflowDialog)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        // 正しくデータが作成できなかった場合は終了
        if (diagonales.size() != workflowIdMap.size()) {
            return Optional.empty();
        }

        List<LineBalanceEntity> ret = new ArrayList<>();
        for (WorkflowEntity entity : workflowEntities) {
            Optional<Map<String, WorkflowDiaglam>> optDiaglam = analyzeWorkflowDialog(entity.getWorkflowDiaglam());
            if (!optDiaglam.isPresent()) {
                return Optional.empty();
            }
            WorkflowDiaglam workflowDiaglam = optDiaglam.get().get("start_id");

            // 解析
            CriticalPathVisitor visitor = new CriticalPathVisitor(workResultEntities);
            workflowDiaglam.accept(visitor);

            final Double planTime = visitor.planWorkTime > 0.001 ? visitor.planWorkTime : null;

            // ラインバランス率
            final double lineBalanceRate = visitor.actualWorkTime * 100 / (visitor.maxTime * visitor.criticalPath.size());

            ret.add(new LineBalanceEntity(entity.getWorkflowName(), visitor.criticalPath, visitor.actualWorkTime, planTime, lineBalanceRate));
        }

        return Optional.of(ret);
    }

    /**
     * 待ち時間を解析
     * @param prev 前の工程
     * @param now 次の工程
     * @return 解析結果
     */
    static Optional<InterProcessWaitingTimeEntity> analyzeWaitTime(List<WorkResultEntity> prev, List<WorkResultEntity> now)
    {
        if (prev.isEmpty() || now.isEmpty()) {
            return Optional.empty();
        }

        List<Long> waitTimes = new ArrayList<>();
        Map<Long, List<WorkResultEntity>> prevMap
                = prev
                .stream()
                .collect(groupingBy(item->item.kanbanId));

        Map<Long, List<WorkResultEntity>> nowMap
                = now
                .stream()
                .collect(groupingBy(item->item.kanbanId));

        prevMap.entrySet()
                .forEach(entity->{
                    Long kanbanId = entity.getKey();
                    List<WorkResultEntity> nowTime = nowMap.getOrDefault(kanbanId, new ArrayList<>());
                    List<WorkResultEntity> prevTime = entity.getValue();
                    if (nowTime.size()==1 && prevTime.size()==1) {
                        final Date prevEndDate = prev.get(0).endTime;
                        final Date nowStartDate = prev.get(0).startTime;
                        // やり直しがあった場合は集計対象外
                        waitTimes.add(nowTime.get(0).startTime.getTime() - prevTime.get(0).endTime.getTime());
                    }
                });

        final List<Long> sortedWaitTime = waitTimes.stream().sorted().collect(toList());
        final int num = sortedWaitTime.size();
        InterProcessWaitingTimeEntity ret = new InterProcessWaitingTimeEntity();
        ret.fromWork = prev.get(0).workName;
        ret.toWork = now.get(0).workName;
        ret.waitTimeMax = sortedWaitTime.get(num-1).doubleValue();
        ret.waitTimeMin = sortedWaitTime.get(0).doubleValue();
        ret.waitTimeMedian = sortedWaitTime.get(num/2).doubleValue();
        ret.waitTimeQ1 = sortedWaitTime.get(num/4).doubleValue();
        ret.waitTimeQ3 = sortedWaitTime.get(num*3/4).doubleValue();

        final double lower = ret.waitTimeQ1 - (ret.waitTimeMedian-ret.waitTimeQ1) * 1.5;
        final double upper = ret.waitTimeQ3 + (ret.waitTimeQ3-ret.waitTimeMedian) * 1.5;
        final List<Long> data = sortedWaitTime.stream()
                .filter(workTime -> workTime >= lower)
                .filter(workTime -> workTime <= upper)
                .collect(toList());

        ret.waitTimeAverage = data.stream().mapToDouble(workTIme -> workTIme).average().orElse(0);
        ret.waitTimeDiv = data.stream().mapToDouble(workTimes -> workTimes * workTimes).average().orElse(0) - ret.waitTimeAverage * ret.waitTimeAverage;
        return Optional.of(ret);
    }

    interface WorkflowDiaglamVisitor {
        void visit(ParallelNodeWorkflowDiaglam element);
        void visit(StartNodeWorkflowDiaglam element);
        void visit(EndNodeWorkflowDiaglam element);
        void visit(NodeWorkflowDiaglam element);
    }

    // 待ち時間解析用Visitor
    static class WaitTimeVisitor implements WorkflowDiaglamVisitor {
        final Map<Long, List<WorkResultEntity>> workResultEntitiesMap;
        List<Long> prevWorks = new ArrayList<>();
        List<InterProcessWaitingTimeEntity> result = new ArrayList<>();

        // カンバン、 工程
        String endName;

        WaitTimeVisitor(final List<WorkResultEntity> workResultEntities) {
            this.endName = "end";
            this.workResultEntitiesMap
                    = workResultEntities
                    .stream()
                    .collect(groupingBy(entity -> entity.workId, collectingAndThen(
                            toList(),
                            list -> list.stream().sorted(Comparator.comparing(l -> l.endTime)).collect(toList())
                    )));
        }

        private WaitTimeVisitor(String endName, List<Long> prevWorks, Map<Long, List<WorkResultEntity>> workResultEntitiesMap) {
            this.endName = endName;
            this.prevWorks = prevWorks;
            this.workResultEntitiesMap = workResultEntitiesMap;
        }

        public void visit(ParallelNodeWorkflowDiaglam element)
        {
            // 完了
            if (StringUtils.equals(this.endName, element.name)) {
                return;
            }

            if (element.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            List<Long> nextWorkIds = new ArrayList<>();
            // 開始
            for (WorkflowDiaglam workflowDiaglam : element.backwardWorkflowDiaglams) {
                WaitTimeVisitor visitor = new WaitTimeVisitor(element.pairDiaglam.name, this.prevWorks, this.workResultEntitiesMap);
                workflowDiaglam.accept(visitor);
                result.addAll(visitor.result);
                nextWorkIds.addAll(visitor.prevWorks);
            }

            if (element.pairDiaglam.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            this.prevWorks = nextWorkIds;
            element.pairDiaglam.backwardWorkflowDiaglams.get(0).accept(this);
        }

        public void visit(StartNodeWorkflowDiaglam element)
        {
            if (element.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            this.endName = "end";
            element.backwardWorkflowDiaglams.get(0).accept(this);
        }

        public void visit(EndNodeWorkflowDiaglam element)
        {
        }

        public void visit(NodeWorkflowDiaglam element)
        {
            final List<WorkResultEntity> now = workResultEntitiesMap.computeIfAbsent(element.id, i -> new ArrayList<>());
            for (Long prevItem : this.prevWorks) {
                final List<WorkResultEntity> prev = workResultEntitiesMap.computeIfAbsent(prevItem, i->new ArrayList<>());
                analyzeWaitTime(prev, now).ifPresent(result::add);
            }

            if (element.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            this.prevWorks = Collections.singletonList(element.id);
            element.backwardWorkflowDiaglams.get(0).accept(this);
        }
    }

    // クリティカルパス解析用 visitor
    static class CriticalPathVisitor implements WorkflowDiaglamVisitor {
        public List<String> criticalPath = new ArrayList<>();
        Map<Long, WorkResultEntity> workResultEntitiesMap;
        String endName;
        double actualWorkTime = 0;
        double planWorkTime = 0;
        double maxTime = 0;

        List<WorkflowDiaglam> path = new ArrayList<>();

        CriticalPathVisitor(List<WorkResultEntity> workResultEntities) {
            this.endName = "end";
            this.workResultEntitiesMap
                    = workResultEntities
                    .stream()
                    .collect(groupingBy(entity->entity.workId,
                            collectingAndThen(
                                    toList(),
                                    item -> {
                                        List<WorkResultEntity> works
                                                = item
                                                .stream()
                                                .sorted(Comparator.comparing(work -> work.workTime))
                                                .collect(toList());

                                        final double median = works.get(works.size() / 2).workTime;
                                        final double q1 = works.get(works.size() / 4).workTime;
                                        final double q3 = works.get(works.size() * 3 / 4).workTime;

                                        final double lower = q1 - (median - q1) * 1.5;
                                        final double upper = q3 + (q3 - median) * 1.5;
                                        final List<Double> data
                                                = works.stream()
                                                .map(work -> (double) work.workTime)
                                                .filter(workTime -> workTime >= lower)
                                                .filter(workTime -> workTime <= upper)
                                                .collect(toList());
                                        WorkResultEntity ret = item.get(0).copy();
                                        ret.workTime = (long) data.stream().mapToDouble(workTIme -> workTIme).average().orElse(0);
                                        return ret;
                                    })));
        }

        private CriticalPathVisitor(String endName, Map<Long, WorkResultEntity> workResultEntitiesMap) {
            this.endName = endName;
            this.workResultEntitiesMap = workResultEntitiesMap;
        }


        @Override
        public void visit(ParallelNodeWorkflowDiaglam element)
        {
            if (StringUtils.equals(this.endName, element.name)) {
                // 完了
                return;
            }

            if (element.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            double actualWorkTime = -1;
            double planWorkTime = 0;
            double maxTime = 0;
            List<String> criticalPath = new ArrayList<>();

            // 開始
            for (WorkflowDiaglam workflowDiaglam : element.backwardWorkflowDiaglams) {
                CriticalPathVisitor visitor = new CriticalPathVisitor(element.pairDiaglam.name, this.workResultEntitiesMap);
                workflowDiaglam.accept(visitor);
                if (actualWorkTime < visitor.actualWorkTime) {
                    actualWorkTime = visitor.actualWorkTime;
                    planWorkTime = visitor.planWorkTime;
                    criticalPath = visitor.criticalPath;
                    maxTime = visitor.maxTime;
                }
            }

            this.actualWorkTime += actualWorkTime;
            this.planWorkTime += planWorkTime;
            this.criticalPath.addAll(criticalPath);
            this.maxTime = Math.max(this.maxTime, maxTime);

            if (element.pairDiaglam.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            element.pairDiaglam.backwardWorkflowDiaglams.get(0).accept(this);
        }

        @Override
        public void visit(StartNodeWorkflowDiaglam element)
        {
            if (element.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            this.endName = "end";
            element.backwardWorkflowDiaglams.get(0).accept(this);

        }

        @Override
        public void visit(EndNodeWorkflowDiaglam element)
        {
        }

        @Override
        public void visit(NodeWorkflowDiaglam element)
        {
            final WorkResultEntity workResultEntity = workResultEntitiesMap.get(element.id);
            if (Objects.isNull(workResultEntity)) {
                return;
            }

            this.criticalPath.add(element.name);
            this.actualWorkTime += workResultEntity.workTime;
            this.planWorkTime += workResultEntity.tactTime;
            this.maxTime = Math.max(this.maxTime, workResultEntity.workTime);

            if (element.backwardWorkflowDiaglams.isEmpty()) {
                return;
            }

            element.backwardWorkflowDiaglams.get(0).accept(this);
        }
    }

    // 工程順ダイアログクラス
    static abstract class WorkflowDiaglam {
        public List<WorkflowDiaglam> forwardWorkflowDiaglams = new ArrayList<>();
        public List<WorkflowDiaglam> backwardWorkflowDiaglams = new ArrayList<>();

        final public String name;
        public long id;

        public WorkflowDiaglam(String name)
        {
            this.name = name;
        }

        public abstract void accept(WorkflowDiaglamVisitor visitor);
    }

    static class NodeWorkflowDiaglam extends WorkflowDiaglam {

        NodeWorkflowDiaglam(String name, String id) {
            super(name);
            this.id = StringUtils.parseLong(id);
        }

        @Override
        public void accept(WorkflowDiaglamVisitor visitor) { visitor.visit(this); }

    }

    static class ParallelNodeWorkflowDiaglam extends WorkflowDiaglam {
        public ParallelNodeWorkflowDiaglam pairDiaglam;

        public ParallelNodeWorkflowDiaglam(String name) {
            super(name);
        }

        @Override
        public void accept(WorkflowDiaglamVisitor visitor) { visitor.visit(this); }


    }

    static class StartNodeWorkflowDiaglam extends WorkflowDiaglam {

        StartNodeWorkflowDiaglam(String name) {
            super(name);
        }
        @Override
        public void accept(WorkflowDiaglamVisitor visitor) { visitor.visit(this); }

    }

    static class EndNodeWorkflowDiaglam extends WorkflowDiaglam {
        EndNodeWorkflowDiaglam(String name) {
            super(name);
        }
        @Override
        public void accept(WorkflowDiaglamVisitor visitor) { visitor.visit(this); }

    }

    /**
     * 工程順ダイアグラム解析
     * @param xmlDiaglam
     * @return
     */
    static Optional<Map<String, WorkflowDiaglam>> analyzeWorkflowDialog(String xmlDiaglam){

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder;
        try {
            docbuilder = dbfactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        ByteArrayInputStream instream = null;
        try {
            instream = new ByteArrayInputStream(xmlDiaglam.getBytes("MS932"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = docbuilder.parse(instream);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        Element processNode = document.getDocumentElement();
        Node node = processNode.getFirstChild();
        NodeList nodeList = node.getChildNodes();

        // 情報の取得
        Map<String, List<Node>> elementMap = new HashMap<>();
        for (int n=0; n<nodeList.getLength(); ++n) {
            Node element = nodeList.item(n);
            // ノード毎に分類
            elementMap.computeIfAbsent(element.getNodeName(), name->new ArrayList<>()).add(element);
        }

        Map<String, WorkflowDiaglam> workflowDiaglamMap = new HashMap<>();

        // 並列ノード
        List<Node> parallelGateways = elementMap.computeIfAbsent("parallelGateway", name->new ArrayList<>());
        for (Node parallelNode : parallelGateways) {
            String own = parallelNode.getAttributes().getNamedItem("id").getNodeValue();
            String pair = parallelNode.getAttributes().getNamedItem("pair").getNodeValue();
            int owni = workflowDiaglamMap.containsKey(own) ? 1 : -1;
            int pairi = workflowDiaglamMap.containsKey(pair) ? 1 : -1;

            // 対となる物が見つからない場合は異常
            if (owni*pairi <= 0) {
                return Optional.empty();
            }

            // 対が見つかった場合は追加
            if (owni == -1) {
                ParallelNodeWorkflowDiaglam ownNode  = new ParallelNodeWorkflowDiaglam(own);
                ParallelNodeWorkflowDiaglam pairNode = new  ParallelNodeWorkflowDiaglam(pair);

                ownNode.pairDiaglam = pairNode;
                pairNode.pairDiaglam = ownNode;

                workflowDiaglamMap.put(own, ownNode);
                workflowDiaglamMap.put(pair, pairNode);
            }
        }

        final String encoding = System.getProperty("file.encoding");
        // 要素ノードを追加
        elementMap
                .computeIfAbsent("task", name -> new ArrayList<>())
                .forEach(element -> {
                    final String name;
                    final String id;
                    try {
                        name = new String(element.getAttributes().getNamedItem("name").getNodeValue().getBytes(), encoding);
                        id = new String(element.getAttributes().getNamedItem("id").getNodeValue().getBytes(), encoding);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return;
                    }
                    workflowDiaglamMap.put(
                            element.getAttributes().getNamedItem("id").getNodeValue(),
                            new NodeWorkflowDiaglam(
                                    name,
                                    id));
                });

        // 開始ノード
        List<Node> startEvents = elementMap.computeIfAbsent("startEvent", name->new ArrayList<>());
        if (startEvents.size() != 1) {
            return Optional.empty();
        }
        startEvents.forEach(element->workflowDiaglamMap.put(element.getAttributes().getNamedItem("id").getNodeValue(), new StartNodeWorkflowDiaglam(element.getAttributes().getNamedItem("name").getNodeValue())));

        // 終了ノード
        List<Node> endEvents = elementMap.computeIfAbsent("endEvent", name->new ArrayList<>());
        if (endEvents.size() != 1) {
            return Optional.empty();
        }
        endEvents.forEach(element->workflowDiaglamMap.put(element.getAttributes().getNamedItem("id").getNodeValue(), new EndNodeWorkflowDiaglam(element.getAttributes().getNamedItem("name").getNodeValue())));

        // ノードの前後関係を構築
        for (Node elem : elementMap.computeIfAbsent("sequenceFlow", name->new ArrayList<>())) {
            WorkflowDiaglam source = workflowDiaglamMap.get(elem.getAttributes().getNamedItem("sourceRef").getNodeValue());
            WorkflowDiaglam target = workflowDiaglamMap.get(elem.getAttributes().getNamedItem("targetRef").getNodeValue());
            if (Objects.isNull(source) || Objects.isNull(target)) {
                return Optional.empty();
            }

            source.backwardWorkflowDiaglams.add(target);
            target.forwardWorkflowDiaglams.add(source);
        }

        return Optional.of(workflowDiaglamMap);
    }

    /**
     * 製品平均作業時間
     * @param aggregateUnit
     * @param itemName
     * @param entity
     * @param from
     * @param to
     * @return
     */
    private Optional<List<AverageProductWorkingHourEntity>> calculateAverageOfProduct(SummaryReportConfigElementEntity entity, List<WorkResultEntity> workResultEntities) {
        // 製品の平均時間
        final OptionalDouble actual = calculateActualAverageOfProduct(workResultEntities);


        // 閾値
        final Double threshold = StringUtils.isEmpty(entity.getThreshold()) ? null : Double.valueOf(entity.getThreshold());
//        if (Objects.nonNull(entity.getTargetValue())) {
//            return Optional.of(Collections.singletonList(new AverageProductWorkingHourEntity(actual.getAsDouble(), Double.valueOf(entity.getTargetValue()), threshold, entity.getWarningBackColor())));
//        }

        // 基準値が指定されている場合
        if (!StringUtils.isEmpty(entity.getTargetValue())) {
            final double planVal = Double.parseDouble(entity.getTargetValue()) * TARGET_VALUE_CONVERT_TO_MIRISEC;
            return Optional.of(Collections.singletonList(new AverageProductWorkingHourEntity(actual.orElse(0.), planVal, threshold, entity.getWarningBackColor())));
        }

        // 計画値
        double plan = calculatePlanAverageOfProduct(workResultEntities).orElse(0.);
        if (plan > 0.) {
            return Optional.of(Collections.singletonList(new AverageProductWorkingHourEntity(actual.orElse(0.), plan, threshold, entity.getWarningBackColor())));
        }

        if (!actual.isPresent()) {
            return Optional.empty();
        }
        
         return Optional.of(Collections.singletonList(new AverageProductWorkingHourEntity(actual.getAsDouble(), 0., threshold, entity.getWarningBackColor())));
    }

    /**
     * 製品実平均時間算出
     * @param workResultEntities
     * @return
     */
    static OptionalDouble calculateActualAverageOfProduct(List<WorkResultEntity> workResultEntities)
    {
        return workResultEntities
                .stream()
                .collect(groupingBy(
                        entity -> entity.kanbanId,
                        collectingAndThen(
                                toList(),
                                list -> list
                                        .stream()
                                        .mapToDouble(entity -> entity.workTime)
                                        .sum())))
                .values()
                .stream()
                .mapToDouble(l -> l)
                .average();
    }

    /**
     * 製品標準作業時間
     * @param workResultEntities
     * @return
     */
    static OptionalDouble calculatePlanAverageOfProduct(List<WorkResultEntity> workResultEntities)
    {
        return workResultEntities
                .stream()
                .collect(groupingBy(entity->entity.workflowId,
                        groupingBy(entity->entity.workId,
                                collectingAndThen(
                                        toList(),
                                        list->list.stream().mapToDouble(l->l.tactTime).average().orElse(0.)
                                ))))
                .values()
                .stream()
                .mapToDouble(l->l.values().stream().mapToDouble(item->item).sum())
                .average();
    }

    /**
     * 工程の平均作業時間を算出
     * @param entity
     * @param workResultEntities
     * @return
     */
    static Optional<List<WorkAverageWorkTimeEntity>> calculateAverageOfWork(SummaryReportConfigElementEntity entity, List<String> workNameList, List<WorkResultEntity> workResultEntities)
    {
        // 閾値
        final Double threshold = StringUtils.isEmpty(entity.getThreshold()) ? null : Double.valueOf(entity.getThreshold());
        // 平均値を計算
        final Map<String, Double> averageMap
                = workResultEntities
                .stream()
                .collect(groupingBy(
                        item->item.workName,
                        collectingAndThen(
                                toList(),
                                list -> list
                                        .stream()
                                        .mapToDouble(l->l.workTime)
                                        .average()
                                        .orElse(0.))));
//        if (averageMap.isEmpty()) {
//            return Optional.empty();
//        }

        // 基準値が指定されている場合
        if (!StringUtils.isEmpty(entity.getTargetValue())) {
            final double planVal = Double.parseDouble(entity.getTargetValue()) * TARGET_VALUE_CONVERT_TO_MIRISEC;
            return Optional.of(
                    workNameList
                    .stream()
                    .map(workName -> new WorkAverageWorkTimeEntity(workName, averageMap.getOrDefault(workName, 0.), planVal, threshold, entity.getWarningBackColor()))
                    .collect(toList()));
        }

        final Map<String, Double> planMap
                = workResultEntities
                .stream()
                .collect(groupingBy(
                        item->item.workName,
                        collectingAndThen(
                                toList(),
                                list->list.stream().mapToDouble(l->l.tactTime).average().orElse(0))));

        List<WorkAverageWorkTimeEntity> ret
                = workNameList
                .stream()
                .map(workName -> new WorkAverageWorkTimeEntity(workName, averageMap.getOrDefault(workName, 0.), planMap.getOrDefault(workName, 0.), threshold, entity.getWarningBackColor()))
                .filter(item -> item.actualWorkTime > 0 || item.planWorkTime > 0)
                .collect(toList());

        if (ret.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ret);
    }

    /**
     * ライン全体の稼働時間
     * @param entity
     * @param workResultEntities
     * @return
     */
   static Optional<List<OverallLineUtilizationEntity>> calculateSumProductOfProduct(SummaryReportConfigElementEntity entity, List<WorkResultEntity> workResultEntities)
    {
        // 基準値
        final Double targetValue = !StringUtils.isEmpty(entity.getTargetValue()) ? Double.parseDouble(entity.getTargetValue())*TARGET_VALUE_CONVERT_TO_MIRISEC : null;
        // 閾値
        final Double threshold = !StringUtils.isEmpty(entity.getThreshold()) ? Double.valueOf(entity.getThreshold()) : null;
        // ライン全体の稼働時間
        final double lineWorkTime
                = workResultEntities
                .stream()
                .mapToDouble(item -> item.workTime)
                .sum();

        if (lineWorkTime <= 0) {
            return Optional.empty();
        }

        return Optional.of(
                Collections.singletonList(
                        new OverallLineUtilizationEntity(lineWorkTime, targetValue, threshold, entity.getWarningBackColor())));
    }

    /**
     * 作業者毎の稼働時間
     * @param entity
     * @param workResultEntities
     * @return
     */
    static Optional<List<OperatingRatePerWorkerEntity>> calculateSumProductOfWorker(SummaryReportConfigElementEntity entity, List<WorkResultEntity> workResultEntities)
    {
        // 基準値
        final Double targetValue = !StringUtils.isEmpty(entity.getTargetValue()) ? Double.valueOf(entity.getTargetValue())*TARGET_VALUE_CONVERT_TO_MIRISEC : null;
        // 閾値
        final Double threshold = !StringUtils.isEmpty(entity.getThreshold()) ? Double.valueOf(entity.getThreshold()) : null;
        // 作業者毎の稼働時間
        return Optional.of(
                new ArrayList<>(
                        workResultEntities
                                .stream()
                                .collect(groupingBy(
                                        item -> item.organizationIdentify,
                                        collectingAndThen(
                                                toList(),
                                                items -> {
                                                    final double workTime = items.stream().mapToDouble(item -> item.workTime).sum();
                                                    return new OperatingRatePerWorkerEntity(items.get(0).organizationIdentify, items.get(0).organizationName, workTime, targetValue, threshold, entity.getWarningBackColor());
                                                })))
                                .values()));
    }


    // 作業のバラツキ
    static Optional<List<VariationEntity>> calculateWorkVariation(List<WorkResultEntity> workResultEntities)
    {
        return Optional.of(
                new ArrayList<>(
                        workResultEntities
                                .stream()
                                .collect(groupingBy(
                                        item -> item.workId,
                                        collectingAndThen(
                                                toList(),
                                                item -> {
                                                    List<WorkResultEntity> works
                                                            = item
                                                            .stream()
                                                            .sorted(Comparator.comparing(work -> work.workTime))
                                                            .collect(toList());

                                                    final double median = works.get(works.size() / 2).workTime;
                                                    final double q1 = works.get(works.size() / 4).workTime;
                                                    final double q3 = works.get(works.size() * 3 / 4).workTime;

                                                    final double lower = q1 - (median - q1) * 1.5;
                                                    final double upper = q3 + (q3 - median) * 1.5;
                                                    final List<Double> data
                                                            = works.stream()
                                                            .map(work -> (double) work.workTime)
                                                            .filter(workTime -> workTime >= lower)
                                                            .filter(workTime -> workTime <= upper)
                                                            .collect(toList());

                                                    final double average = data.stream().mapToDouble(workTIme -> workTIme).average().orElse(0);
                                                    final double distributed = Math.sqrt(data.stream().mapToDouble(workTimes -> workTimes * workTimes).average().orElse(0) - average * average);
                                                    return new VariationEntity(item.get(0).workName, median, q1, q3, average, distributed);
                                                })))
                                .values()));
    }

    // 作業者間のバラツキ
    static Optional<List<VariationEntity>> calculateWorkWorkerVariation(List<WorkResultEntity> workResultEntities)
    {
        return Optional.of(
                new ArrayList<>(
                        workResultEntities
                                .stream()
                                .collect(groupingBy(
                                                l -> l.workId,
                                                collectingAndThen(
                                                        toList(),
                                                        item -> {
                                                            List<WorkResultEntity> works
                                                                    = item.stream()
                                                                    .sorted(Comparator.comparing(work -> work.workTime))
                                                                    .collect(toList());

                                                            final long q1 = works.get(works.size() / 4).workTime;
                                                            final long q3 = works.get(works.size() * 3 / 4).workTime;

                                                            final double lower = q1 - q1 * 1.5;
                                                            final double upper = q3 + q3 * 1.5;
                                                            return works.stream()
                                                                    .filter(work -> work.workTime >= lower)
                                                                    .filter(work -> work.workTime <= upper)
                                                                    .collect(groupingBy(
                                                                            work -> work.organizationId,
                                                                            collectingAndThen(
                                                                                    toList(),
                                                                                    workerWorks ->
                                                                                            workerWorks
                                                                                                    .stream()
                                                                                                    .mapToDouble(workerWork -> workerWork.workTime)
                                                                                                    .average()
                                                                                                    .orElse(0))))
                                                                    .values()
                                                                    .stream()
                                                                    .sorted()
                                                                    .collect(collectingAndThen(
                                                                            toList(),
                                                                            data -> {
                                                                                final double median_ = data.get(data.size() / 2);
                                                                                final double q1_ = data.get(data.size() / 4);
                                                                                final double q3_ = data.get(data.size() * 3 / 4);

                                                                                final double average_ = data.stream().mapToDouble(e -> e).average().orElse(0);
                                                                                final double distributed_ = Math.sqrt(data.stream().mapToDouble(workTimes -> workTimes * workTimes).average().orElse(0) - average_ * average_);
                                                                                return new VariationEntity(item.get(0).workName, median_, q1_, q3_, average_, distributed_);
                                                                            }));
                                                        })))
                                .values()));
    }

    // 設備間のバラツキ
    static Optional<List<VariationEntity>> calculateWorkEquipmentVariation(List<WorkResultEntity> workResultEntities)
    {
        return Optional.of(
                new ArrayList<>(workResultEntities
                        .stream()
                        .collect(groupingBy(
                                        l -> l.workId,
                                        collectingAndThen(
                                                toList(),
                                                item -> {
                                                    List<WorkResultEntity> works
                                                            = item.stream()
                                                            .sorted(Comparator.comparing(work -> work.workTime))
                                                            .collect(toList());

                                                    final long q1 = works.get(works.size() / 4).workTime;
                                                    final long q3 = works.get(works.size() * 3 / 4).workTime;

                                                    final double lower = q1 - q1 * 1.5;
                                                    final double upper = q3 + q3 * 1.5;
                                                    return works.stream()
                                                            .filter(work -> work.workTime >= lower)
                                                            .filter(work -> work.workTime <= upper)
                                                            .collect(groupingBy(
                                                                    work -> work.equipmentId,
                                                                    collectingAndThen(
                                                                            toList(),
                                                                            workerWorks ->
                                                                                    workerWorks
                                                                                            .stream()
                                                                                            .mapToDouble(workerWork -> workerWork.workTime)
                                                                                            .average()
                                                                                            .orElse(0))))
                                                            .values()
                                                            .stream()
                                                            .sorted()
                                                            .collect(collectingAndThen(
                                                                    toList(),
                                                                    data -> {
                                                                        final double median_ = data.get(data.size() / 2);
                                                                        final double q1_ = data.get(data.size() / 4);
                                                                        final double q3_ = data.get(data.size() * 3 / 4);

                                                                        final double average_ = data.stream().mapToDouble(e -> e).average().orElse(0);
                                                                        final double distributed_ = Math.sqrt(data.stream().mapToDouble(workTimes -> workTimes * workTimes).average().orElse(0) - average_ * average_);
                                                                        return new VariationEntity(item.get(0).workName, median_, q1_, q3_, average_, distributed_);
                                                                    }));
                                                })
                                )
                        )
                        .values()));
    }


    /**
     * 生産数情報計算
     * @param aggregateUnit
     * @param itemName
     * @param entity
     * @param from
     * @param to
     * @return
     */
    Optional<List<NumberOfProductsProducedEntity>> calculateProductNum(AggregateUnitEnum aggregateUnit, String itemName, SummaryReportConfigElementEntity entity, Date from, Date to)
    {
        // 実生産数
        final Optional<Long> optActualProductNum = calculateActualProductNum(aggregateUnit, itemName, from, to);
        final Double actualProductNum = optActualProductNum.orElse((long)0).doubleValue();

        // 閾値
        final Double threshold = StringUtils.isEmpty(entity.getThreshold()) ? null : Double.valueOf(entity.getThreshold());

        // 計画数指定している場合
        if (!StringUtils.isEmpty(entity.getTargetValue())) {
            final Double planProductNum = Double.valueOf(entity.getTargetValue());
            return Optional.of(
                    Collections.singletonList(
                            new NumberOfProductsProducedEntity(actualProductNum, planProductNum, threshold, entity.getWarningBackColor())));
        }

        // 計画生産数
        final Optional<Long> optPlanProductNum = getPlanProductNum(aggregateUnit, itemName, from, to);
        if (!optPlanProductNum.isPresent() && !optActualProductNum.isPresent()) {
            return Optional.empty();
        }

        final Double planProductNum = optPlanProductNum.map(Long::doubleValue).orElse(0.);

        if (actualProductNum<=0 && planProductNum <= 0) {
            return Optional.empty();
        }

        return Optional.of(Collections.singletonList(new NumberOfProductsProducedEntity(actualProductNum, planProductNum, threshold, entity.getWarningBackColor())));
    }


    /**
     * 生産数を算出
     * @param aggregateUnit 集計単位
     * @param itemName 項目
     * @param from 集計開始日時
     * @param to 集計完了日時
     * @return 実生産数
     */
    private Optional<Long> calculateActualProductNum(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        final String actualProductNumByModel = "SELECT COUNT(*) FROM trn_kanban tk WHERE tk.model_name = ?1 AND tk.actual_comp_datetime BETWEEN ?2 AND ?3 AND tk.kanban_status = 'COMPLETION'";
        final String actualProductNumByWorkflow = "SELECT COUNT(*) FROM trn_kanban tk JOIN mst_workflow mw ON mw.workflow_id = tk.workflow_id WHERE mw.workflow_name = ?1 AND tk.actual_comp_datetime BETWEEN ?2 AND ?3 AND tk.kanban_status = 'COMPLETION'";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? actualProductNumByWorkflow
                : actualProductNumByModel;

        final Query query = em
                .createNativeQuery(sql)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        final Long productNum = (Long) query.getSingleResult();
        if (Objects.isNull(productNum)) {
            return Optional.empty();
        }

        // 実績値
        return Optional.of(productNum);
    }

    /**
     * 計画数を取得
     * @param aggregateUnit 集計単位
     * @param itemName 項目
     * @param from 集計開始日時
     * @param to 集計完了日時
     * @return 計画生産数
     */
    private Optional<Long> getPlanProductNum(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        final String planProductNumByModel = "SELECT COUNT(*) FROM trn_kanban tk WHERE tk.model_name = ?1 AND tk.comp_datetime BETWEEN ?2 AND ?3";
        final String planProductNumByWorkflow = "SELECT COUNT(*) FROM trn_kanban tk JOIN mst_workflow mw ON mw.workflow_id = tk.workflow_id WHERE mw.workflow_name = ?1 AND tk.comp_datetime BETWEEN ?2 AND ?3";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? planProductNumByWorkflow
                : planProductNumByModel;

        final Query query = em
                .createNativeQuery(sql)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        Long planProductNum = (Long) query.getSingleResult();

        if (Objects.isNull(planProductNum) || planProductNum<=0) {
            return Optional.empty();
        }

        // 計画数
        return Optional.of(planProductNum);
    }

    /**
     * 工程生産数
     * @param aggregateUnit
     * @param itemName
     * @param entity
     * @param from
     * @param to
     * @return
     */
    private Optional<List<NumberOfProcessProducedEntity>> calculateWorkNum(AggregateUnitEnum aggregateUnit, String itemName, List<String> workNameList, SummaryReportConfigElementEntity entity, Date from, Date to) {
        // 実績値
        Optional<List<WorkProductNumEntity>> optActualWorkNum = calculateActualWorkNum(aggregateUnit, itemName, from, to);
        final List<WorkProductNumEntity> actualWorkNum = optActualWorkNum.orElse(new ArrayList<>());
        // 実績のMap化
        final Map<String, Double> actualWorkMap
                = actualWorkNum
                .stream()
                .filter(work -> Objects.nonNull(work.number))
                .filter(work -> work.number > 0)
                .collect(toMap(work -> work.workName, work -> work.number.doubleValue()));

        // 閾値
        final Double threshold = StringUtils.isEmpty(entity.getThreshold()) ? null : Double.valueOf(entity.getThreshold());

        // 計画値が設定された場合
        if (!StringUtils.isEmpty(entity.getTargetValue())) {
            final Double planWorkNum = Double.parseDouble(entity.getTargetValue());
            return Optional.of(
                    workNameList
                            .stream()
                            .map(workName -> new NumberOfProcessProducedEntity(workName, actualWorkMap.getOrDefault(workName, 0.), planWorkNum, threshold, entity.getWarningBackColor()))
                            .collect(toList()));
        }

        // 計画取得
        Optional<List<WorkPlanNumEntity>> planWorkNum = calculatePlanWorkNum(aggregateUnit, itemName, from, to);
        if (!planWorkNum.isPresent()) {
            List<NumberOfProcessProducedEntity> ret
                    = workNameList
                            .stream()
                            .map(workName -> new NumberOfProcessProducedEntity(workName, actualWorkMap.getOrDefault(workName, 0.), null, threshold, entity.getWarningBackColor()))
                            .filter(item -> item.actualProducedNumber > 0)
                            .collect(toList());
            if (ret.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(ret);
        }

        // 計画のMap化
        final Map<String, Double> planWorkMap
                = planWorkNum.get()
                .stream()
                .filter(work-> Objects.nonNull(work.number))
                .filter(work-> work.number > 0)
                .collect(toMap(work -> work.workName, work->work.number.doubleValue()));

        List<NumberOfProcessProducedEntity> ret
                = workNameList
                .stream()
                .filter(workName -> actualWorkMap.containsKey(workName) || planWorkMap.containsKey(workName))
                .map(workName -> new NumberOfProcessProducedEntity(workName, actualWorkMap.getOrDefault(workName, 0.), planWorkMap.getOrDefault(workName, 0.), threshold, entity.getWarningBackColor()))
                .filter(item -> item.actualProducedNumber > 0 || item.planProducedNumber > 0)
                .collect(toList());

        if (ret.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ret);
    }

    /**
     * 実作業の工程数
     * @param aggregateUnit 集計単位
     * @param itemName 項目
     * @param from 集計開始日時
     * @param to 集計完了日時
     * @return 工程毎の生産数
     */
    private Optional<List<WorkProductNumEntity>> calculateActualWorkNum(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        final String actualWorkNumByModel = "SELECT mw.work_name, COUNT(*) number FROM trn_kanban tk JOIN trn_work_kanban twk ON twk.kanban_id = tk.kanban_id JOIN mst_work mw ON mw.work_id = twk.work_id WHERE tk.model_name = ?1 AND twk.actual_comp_datetime BETWEEN ?2 AND ?3 AND twk.work_status = 'COMPLETION' GROUP BY mw.work_name";
        final String actualWorkNumByWorkflow = "SELECT mw.work_name, COUNT(*) number FROM trn_kanban tk JOIN trn_work_kanban twk ON twk.kanban_id = tk.kanban_id JOIN mst_workflow mwf ON mwf.workflow_id = twk.workflow_id RIGHT JOIN mst_work mw ON mw.work_id = twk.work_id WHERE mwf.workflow_name = ?1 AND twk.actual_comp_datetime BETWEEN ?2 AND ?3 AND twk.work_status = 'COMPLETION' GROUP BY mw.work_name";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? actualWorkNumByWorkflow
                : actualWorkNumByModel;

        final Query query = em
                .createNativeQuery(sql, WorkProductNumEntity.class)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        // 実績値
        final List<WorkProductNumEntity> kanbanEntities = query.getResultList();

        return Optional.ofNullable(kanbanEntities);
    }

    /**
     * 工程生産数
     * @param aggregateUnit
     * @param itemName
     * @param from
     * @param to
     * @return
     */
    private Optional<List<WorkPlanNumEntity>> calculatePlanWorkNum(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        final String planWorkNumByModel = "SELECT mw.work_name, COUNT(*) number FROM trn_kanban tk JOIN trn_work_kanban twk ON twk.kanban_id = tk.kanban_id JOIN mst_work mw ON mw.work_id = twk.work_id WHERE tk.model_name = ?1 AND twk.comp_datetime BETWEEN ?2 AND ?3 GROUP BY mw.work_name";
        final String planWorkNumByWorkflow = "SELECT mw.work_name, COUNT(*) number FROM trn_kanban tk JOIN trn_work_kanban twk ON twk.kanban_id = tk.kanban_id JOIN mst_workflow mwf ON mwf.workflow_id = twk.workflow_id JOIN mst_work mw ON mw.work_id = twk.work_id WHERE mwf.workflow_name = ?1 AND twk.comp_datetime BETWEEN ?2 AND ?3 GROUP BY mw.work_name";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? planWorkNumByWorkflow
                : planWorkNumByModel;

        final Query query = em
                .createNativeQuery(sql, WorkPlanNumEntity.class)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        // 実績値
        final List<WorkPlanNumEntity> workEntities = query.getResultList();

        if (Objects.isNull(workEntities) || workEntities.isEmpty() ) {
            return Optional.empty();
        }

        return Optional.of(workEntities);
    }

    // 工程時間
    static abstract class WorkTime {
        public WorkResultEntity entity;
        public final Date date;

        WorkTime(WorkResultEntity entity, Date date) {
            this.entity = entity;
            this.date = date;
        }

        abstract List<WorkTime> updateList(List<WorkTime> data);

        Date getDate() {
            return date;
        }

        WorkResultEntity creatWorkResult(Date startTime, Date endTime, Long workNum) {
            WorkResultEntity ret = entity.copy();
            ret.startTime = startTime;
            ret.endTime = endTime;
            ret.workNum = workNum;
            ret.workTime = (endTime.getTime() - startTime.getTime()) / workNum;
            return ret;
        }
    }

    // 工程開始時間
    static class WorkStartTime extends WorkTime {
        WorkStartTime(WorkResultEntity entity) {
            super(entity, entity.startTime);
        }

        @Override
        List<WorkTime> updateList(List<WorkTime> data) {
            data.add(this);
            return data;
        }
    }

    // 工程完了時間
    static class WorkEndTime extends WorkTime {
        WorkEndTime(WorkResultEntity entity) {
            super(entity, entity.endTime);
        }

        @Override
        List<WorkTime> updateList(List<WorkTime> data) {
            data.removeIf(l -> Objects.equals(l.entity.actualId, this.entity.actualId));
            return data;
        }
    }

    static Stream<WorkTime> createWorkTime(WorkResultEntity entity) {
        return Stream.of(new WorkStartTime(entity), new WorkEndTime(entity));
    }

    /**
     * 実作業時間算出
     * @param aggregateUnit 集計単位
     * @param itemName 項目
     * @param from 集計開始日時
     * @param to 集計完了日時
     * @return 実作業時間
     */
    private Optional<List<WorkResultEntity>> getActualKanbanWorkInfo(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        final String targetKanbanIdByModel = "SELECT tk.kanban_id FROM trn_kanban tk WHERE tk.actual_comp_datetime BETWEEN ?2 AND ?3  AND model_name = ?1  AND tk.kanban_status = 'COMPLETION'  AND tk.actual_start_datetime IS NOT NULL";
        final String targetKanbanIdByWorkflow = "SELECT tk.kanban_id FROM trn_kanban tk  JOIN mst_workflow mwf ON tk.workflow_id = mwf.workflow_id WHERE tk.actual_comp_datetime BETWEEN ?2 AND ?3  AND mwf.workflow_name = ?1  AND tk.kanban_status = 'COMPLETION'  AND tk.actual_start_datetime IS NOT NULL";

        // ターゲットのカンバンIDを取得
        final String targeIdsSql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? targetKanbanIdByWorkflow
                : targetKanbanIdByModel;

        final Query targetIdsQuery = em
                .createNativeQuery(targeIdsSql)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        final List<Long> targetIds = targetIdsQuery.getResultList();
        if (targetIds.isEmpty()) {
            return Optional.empty();
        }

        // 計算対象のデータを取得
        final String actualKanbanWorkTimeByModel = "SELECT tar_p.actual_id, tk.model_name item, tk.kanban_id, tk.workflow_id, mwf.workflow_name, tar_p.work_kanban_id, tar_p.work_id, mw.work_name, mw.takt_time, tar_p.implement_datetime st, ac.et, ac.oi, mo.organization_identify, ac.ei, me.equipment_identify, ac.ir, ac.dr FROM (SELECT kanban_id ki, tar.pair_id sti, implement_datetime et, organization_id oi, equipment_id ei, tar.interrupt_reason ir, tar.delay_reason dr FROM trn_actual_result tar WHERE tar.implement_datetime BETWEEN ?1 AND ?2 AND tar.actual_status IN ('COMPLETION', 'SUSPEND')  AND tar.pair_id IS NOT NULL) ac  JOIN trn_kanban tk ON tk.kanban_id = ac.ki  JOIN trn_actual_result tar_p ON tar_p.actual_id = ac.sti  JOIN mst_equipment me on tar_p.equipment_id = me.equipment_id JOIN mst_work mw ON tar_p.work_id = mw.work_id JOIN mst_organization mo ON ac.oi = mo.organization_id JOIN mst_workflow mwf ON mwf.workflow_id = tk.workflow_id";
        final String periodByModel = "SELECT 1 id, min(tk.actual_start_datetime) sd, max(tk.actual_comp_datetime) ed  FROM trn_kanban tk  WHERE tk.actual_comp_datetime BETWEEN ?2 AND ?3  AND model_name = ?1  AND tk.kanban_status = 'COMPLETION'  AND tk.actual_start_datetime IS NOT NULL";

        final String actualKanbanWorkTypeByWorkflow = "SELECT tar_p.actual_id, mwf2.workflow_name item, tk.kanban_id, tk.workflow_id, mwf2.workflow_name, tar_p.work_kanban_id, tar_p.work_id, mw.work_name, mw.takt_time, tar_p.implement_datetime st, ac.et, ac.oi, ac.ei, mo.organization_identify, ac.ei, me.equipment_identify, ac.ir, ac.dr FROM (SELECT kanban_id ki, tar.pair_id sti, implement_datetime et, organization_id oi, equipment_id ei, tar.interrupt_reason ir, tar.delay_reason dr FROM trn_actual_result tar WHERE tar.implement_datetime BETWEEN ?1 AND ?2 AND tar.actual_status IN ('COMPLETION', 'SUSPEND')  AND tar.pair_id IS NOT NULL) ac  JOIN trn_kanban tk ON tk.kanban_id = ac.ki  JOIN mst_workflow mwf2 ON tk.workflow_id = mwf2.workflow_id JOIN trn_actual_result tar_p ON tar_p.actual_id = ac.sti  JOIN mst_equipment me on tar_p.equipment_id = me.equipment_id JOIN mst_work mw ON tar_p.work_id = mw.work_id JOIN mst_organization mo ON ac.oi = mo.organization_id";
        final String periodByWorkflow = "SELECT 1 id, min(tk.actual_start_datetime) sd, max(tk.actual_comp_datetime) ed  FROM trn_kanban tk  JOIN mst_workflow mwf ON tk.workflow_id = mwf.workflow_id  WHERE tk.actual_comp_datetime BETWEEN ?2 AND ?3  AND mwf.workflow_name = ?1  AND tk.kanban_status = 'COMPLETION'  AND tk.actual_start_datetime IS NOT NULL";

        final String periodSql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? periodByWorkflow
                : periodByModel;

        final Query periodQuery = em
                .createNativeQuery(periodSql, PeriodEntity.class)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        final List<PeriodEntity> periodEntities = periodQuery.getResultList();

        if (periodEntities.isEmpty()) {
            return Optional.empty();
        }

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? actualKanbanWorkTypeByWorkflow
                : actualKanbanWorkTimeByModel;

        final Query query = em
                .createNativeQuery(sql, WorkResultEntity.class)
                .setParameter(1, periodEntities.get(0).startDate)
                .setParameter(2, periodEntities.get(0).endDate);

        final List<WorkResultEntity> workResultEntities = query.getResultList();

        if (workResultEntities.isEmpty()) {
            return Optional.empty();
        }


        final Set<Long> idSet = new HashSet<>(targetIds);
        return Optional.of(calculateActualWorkTime(workResultEntities)
                .stream()
                .filter(entity->idSet.contains(entity.kanbanId))
                .collect(toList()));
    }


    /**
     * 実作業時間算出
     * @param aggregateUnit 集計単位
     * @param itemName 項目
     * @param from 集計開始日時
     * @param to 集計完了日時
     * @return 実作業時間
     */
    private Optional<List<WorkResultEntity>> getActualWorkKanbanWorkInfo(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        // 計算対象のデータを取得
        final String actualWorkTimeByModel = "SELECT tar_p.actual_id, tk.model_name item, tk.kanban_id, tk.workflow_id, mwf.workflow_name, tar_p.work_kanban_id, tar_p.work_id, mw.work_name, twk.takt_time, tar_p.implement_datetime st, ac.et, ac.oi, mo.organization_identify, mo.organization_name, ac.ei, me.equipment_identify, ac.ir, ac.dr  FROM (SELECT kanban_id ki, tar.pair_id sti, implement_datetime et, organization_id oi, equipment_id ei, tar.interrupt_reason ir, tar.delay_reason dr FROM trn_actual_result tar WHERE tar.implement_datetime BETWEEN ?1 AND ?2 AND tar.actual_status IN ('COMPLETION', 'SUSPEND')  AND tar.pair_id IS NOT NULL) ac  JOIN trn_kanban tk ON tk.kanban_id = ac.ki  JOIN trn_actual_result tar_p ON tar_p.actual_id = ac.sti  JOIN mst_equipment me on tar_p.equipment_id = me.equipment_id JOIN mst_work mw ON tar_p.work_id = mw.work_id JOIN mst_organization mo ON ac.oi = mo.organization_id JOIN mst_workflow mwf ON mwf.workflow_id = tk.workflow_id JOIN trn_work_kanban twk ON twk.work_kanban_id = tar_p.work_kanban_id";
        final String actualWorkTypeByWorkflow = "SELECT tar_p.actual_id, mwf2.workflow_name item, tk.kanban_id, tk.workflow_id, mwf2.workflow_name, tar_p.work_kanban_id, tar_p.work_id, mw.work_name, twk.takt_time, tar_p.implement_datetime st, ac.et, ac.oi, ac.ei, mo.organization_identify, mo.organization_name, ac.ei, me.equipment_identify, ac.ir, ac.dr FROM (SELECT kanban_id ki, tar.pair_id sti, implement_datetime et, organization_id oi, equipment_id ei, tar.interrupt_reason ir, tar.delay_reason dr FROM trn_actual_result tar WHERE tar.implement_datetime BETWEEN ?1 AND ?2 AND tar.actual_status IN ('COMPLETION', 'SUSPEND')  AND tar.pair_id IS NOT NULL) ac  JOIN trn_kanban tk ON tk.kanban_id = ac.ki  JOIN mst_workflow mwf2 ON tk.workflow_id = mwf2.workflow_id JOIN trn_actual_result tar_p ON tar_p.actual_id = ac.sti  JOIN mst_equipment me on tar_p.equipment_id = me.equipment_id JOIN mst_work mw ON tar_p.work_id = mw.work_id JOIN mst_organization mo ON ac.oi = mo.organization_id JOIN trn_work_kanban twk ON twk.work_kanban_id = tar_p.work_kanban_id";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? actualWorkTypeByWorkflow
                : actualWorkTimeByModel;

        final Query query = em
                .createNativeQuery(sql, WorkResultEntity.class)
                .setParameter(1, from)
                .setParameter(2, to);

        final List<WorkResultEntity> workResultEntities = query.getResultList();

        if (workResultEntities.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(calculateActualWorkTime(workResultEntities)
                .stream()
                .filter(entity->StringUtils.equals(itemName, entity.item))
                .collect(toList()));
    }



    /**
     * 実績時間を算出 (並列作業の計算)
     * @param workResultEntities 実績データ
     * @return 作業時間
     */
    static List<WorkResultEntity> calculateActualWorkTime(List<WorkResultEntity> workResultEntities) {
        return workResultEntities
                .stream()
                .collect(groupingBy(
                        entity -> entity.organizationId,
                        collectingAndThen(
                                toList(),
                                SummaryReportFacadeREST::calculateActualWorkTimeImpl)))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(toList());
    }


    /**
     * 工程毎の実績を計算
     * @param workResultEntities 実績情報
     * @return 工程毎の実績
     */
    static List<WorkResultEntity> calculateActualWorkTimeImpl(List<WorkResultEntity> workResultEntities) {
        final List<WorkTime> workTimes
                = workResultEntities
                .stream()
                .flatMap(SummaryReportFacadeREST::createWorkTime)
                .sorted(Comparator.comparing(WorkTime::getDate))
                .collect(toList());

        List<WorkResultEntity> ret = new ArrayList<>();

        List<WorkTime> works = new LinkedList<>();
        for (int n = 0; n < workTimes.size(); ++n) {
            WorkTime nowWork = workTimes.get(n);
            final long workNum = works.size();
            if (workNum > 0) {
                WorkTime prevWork = workTimes.get(n - 1);
                ret.addAll(
                        works.stream()
                                .map(entity->entity.creatWorkResult(prevWork.getDate(), nowWork.getDate(), workNum))
                                .collect(toList()));
            }
            works = nowWork.updateList(works);
        }
        return ret;
    }

    /**
     * 遅延時間計算
     * @param workResultEntities
     * @return
     */
    static Optional<List<TimeRankingElementEntity>> calculateDelayWorkTime(List<WorkResultEntity> workResultEntities) {
        return Optional.of(
                new ArrayList<>(
                workResultEntities.stream()
                .collect(groupingBy(
                        entity->entity.workKanbanId,
                        collectingAndThen(
                                toList(),
                                SummaryReportFacadeREST::calculateDelayWorkTimeImpl
                        )
                ))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(entity->Objects.nonNull(entity.delayReason))
                .collect(groupingBy(
                        entity->entity.delayReason,
                        collectingAndThen(
                                toList(),
                                entities ->  {
                                    final Double delayTime
                                            = entities
                                            .stream()
                                            .mapToDouble(entity->(double)(entity.endTime.getTime()-entity.startTime.getTime())/ entity.workNum)
                                            .sum();

                                    final String delayReason
                                            = entities
                                            .stream()
                                            .map(entity->entity.delayReason)
                                            .filter(Objects::nonNull)
                                            .findFirst()
                                            .orElse(null);

                                    return new TimeRankingElementEntity(delayReason, delayTime);
                                }
                        )))
                .values()));
    }

    /**
     * 中断時間実装
     * @param workResultEntities
     * @return
     */
    static List<WorkResultEntity> calculateDelayWorkTimeImpl(List<WorkResultEntity> workResultEntities) {

        if (workResultEntities.isEmpty()) {
            return new ArrayList<>();
        }

        final List<WorkTime> workTimes
                = workResultEntities
                .stream()
                .flatMap(SummaryReportFacadeREST::createWorkTime)
                .sorted(Comparator.comparing(WorkTime::getDate))
                .collect(toList());

        List<WorkResultEntity> ret = new ArrayList<>();
        double remainingTime = workResultEntities.get(0).tactTime;

        List<WorkTime> works = new LinkedList<>();
        for (int n = 0; n < workTimes.size(); ++n) {
            WorkTime nowWork = workTimes.get(n);
            final long workNum = works.size();
            if (workNum > 0) {
                WorkTime prevWork = workTimes.get(n - 1);
                final double revWorkNum = works.stream().mapToDouble(work->1/((double)work.entity.workNum)).sum();
                final double workTime = ((double)nowWork.getDate().getTime() - (double)prevWork.getDate().getTime()) * revWorkNum;
                remainingTime -= workTime;
                if (remainingTime < 0) {
                    final Date startDate = new Date(nowWork.getDate().getTime() + (long)(remainingTime/revWorkNum));
                    ret.addAll(works.stream().map(work->work.creatWorkResult(startDate, nowWork.getDate(), work.entity.workNum)).collect(toList()));
                    remainingTime = 0;
                }
            }
            works = nowWork.updateList(works);
        }

        return ret;
    }

    /**
     * 中断時間計算
     * @param workResultEntities
     * @return
     */
    static Optional<List<TimeRankingElementEntity>> calculateInterruptTime(List<WorkResultEntity> workResultEntities) {
        return Optional.of(
                new ArrayList<>(
                        workResultEntities.stream()
                                .collect(groupingBy(
                                        entity->entity.workKanbanId,
                                        collectingAndThen(
                                                toList(),
                                                SummaryReportFacadeREST::calculateInterruptTimeImpl
                                        )
                                ))
                                .values()
                                .stream()
                                .flatMap(Collection::stream)
                                .filter(entity->Objects.nonNull(entity.interruptReason))
                                .collect(groupingBy(
                                        entity->entity.interruptReason,
                                        collectingAndThen(
                                                toList(),
                                                entities ->  {
                                                    Double delayTime
                                                            = entities
                                                            .stream()
                                                            .mapToDouble(entity->(double)(entity.endTime.getTime()-entity.startTime.getTime())/ entity.workNum)
                                                            .sum();
                                                    return new TimeRankingElementEntity(entities.get(0).interruptReason, delayTime);
                                                }
                                        )))
                                .values()));
    }

    /**
     * 遅延時間計算実装
     * @param workResultEntities
     * @return
     */
    static List<WorkResultEntity> calculateInterruptTimeImpl(List<WorkResultEntity> workResultEntities) {
        if (workResultEntities.isEmpty()) {
            return new ArrayList<>();
        }

        final List<WorkTime> workTimes
                = workResultEntities
                .stream()
                .flatMap(SummaryReportFacadeREST::createWorkTime)
                .sorted(Comparator.comparing(WorkTime::getDate))
                .collect(toList());

        List<WorkResultEntity> ret = new ArrayList<>();

        String interruptReason = null;
        List<WorkTime> works = new LinkedList<>();
        for (int n = 0; n < workTimes.size(); ++n) {
            WorkTime nowWork = workTimes.get(n);
            works = nowWork.updateList(works);
            if (works.isEmpty()) {
                interruptReason = nowWork.entity.interruptReason;
            } else {
                if (Objects.nonNull(interruptReason)) {
                    WorkTime prevWork = workTimes.get(n - 1);
                    ret.add(prevWork.creatWorkResult(prevWork.getDate(), nowWork.getDate(), (long)1));
                }
                interruptReason = null;
            }

        }

        return ret;

    }


    /**
     *
     * @param aggregateUnit
     * @param itemName
     * @param from
     * @param to
     * @return
     */
    Optional<List<CountRankingElementEntity>> calculateCallCount(AggregateUnitEnum aggregateUnit, String itemName, Date from, Date to) {
        final String callNumByModel = "WITH top AS (SELECT tope.operation_id, tope.operate_datetime, add_info.pair_id, add_info.reason, add_info.work_kanban_id  FROM trn_operation tope,  jsonb_to_record(tope.add_info -> 'call') AS add_info(pair_id bigint, reason text, work_kanban_id bigint)) SELECT top2.reason reason, COUNT(top2.reason) count FROM top  JOIN top top2 ON top2.operation_id = top.pair_id  JOIN trn_work_kanban twk ON twk.work_kanban_id = top.work_kanban_id  JOIN trn_kanban tk on tk.kanban_id = twk.kanban_id WHERE tk.model_name = ?1  AND (top2.operate_datetime BETWEEN ?2 AND ?3) GROUP BY top2.reason";
        final String callNumByWorkflow = "WITH top AS (SELECT tope.operation_id, tope.operate_datetime, add_info.pair_id, add_info.reason, add_info.work_kanban_id  FROM trn_operation tope,  jsonb_to_record(tope.add_info -> 'call') AS add_info(pair_id bigint, reason text, work_kanban_id bigint)) SELECT top2.reason reason, COUNT(top2.reason) count FROM top  JOIN top top2 ON top2.operation_id = top.pair_id  JOIN trn_work_kanban twk ON twk.work_kanban_id = top.work_kanban_id  JOIN trn_kanban tk on tk.kanban_id = twk.kanban_id  JOIN mst_workflow mw ON mw.workflow_id = tk.workflow_id WHERE mw.workflow_name = ?1  AND (top2.operate_datetime BETWEEN ?2 AND ?3) GROUP BY top2.reason";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? callNumByWorkflow
                : callNumByModel;

        final Query query = em
                .createNativeQuery(sql, CallRankingEntity.class)
                .setParameter(1, itemName)
                .setParameter(2, from)
                .setParameter(3, to);

        // 実績値
        final List<CallRankingEntity> result = query.getResultList();

        if (Objects.isNull(result)) {
            logger.fatal("calculateCallCount Error!!");
            return Optional.empty();
        }

        return Optional.of(
                result.stream()
                        .map(element->new CountRankingElementEntity(element.reason, element.count))
                        .collect(toList()));
    }

    /**
     *
     * @param aggregateUnit
     * @param itemName
     * @return
     */
    Optional<List<String>> getWorkNameList(AggregateUnitEnum aggregateUnit, String itemName) {
        final String workNameByModel = "SELECT distinct(mw.work_name) FROM (SELECT distinct(tk.workflow_id) wi FROM trn_kanban tk WHERE tk.model_name = ?1) wi JOIN mst_workflow mwf ON mwf.workflow_id = wi.wi JOIN con_workflow_work cww ON cww.workflow_id = wi JOIN mst_work mw ON mw.work_id = cww.work_id WHERE mwf.remove_flag = FALSE";
        final String workNameByWorkflow = "SELECT distinct(mw.work_name) FROM (SELECT mwf.workflow_id  FROM mst_workflow mwf  WHERE mwf.remove_flag = FALSE AND mwf.workflow_name = ?1) wi  JOIN con_workflow_work cww  ON cww.workflow_id = wi.workflow_id  JOIN mst_work mw ON mw.work_id = cww.work_id";

        final String sql = AggregateUnitEnum.ORDER_PROCESSES.equals(aggregateUnit)
                ? workNameByWorkflow
                : workNameByModel;

        final Query query = em
                .createNativeQuery(sql)
                .setParameter(1, itemName);

        // 実績値
        final List<String> result = query.getResultList();
        if (Objects.isNull(result) || result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);
    }


        /**
         * 解析期間の確認
         * @param toDate 最終日時
         * @param sendFrequencyEnum 頻度
         * @return 期間 (開始日時, 完了日時)
         */
    static Tuple<Date, Date> calcFrequency(Date toDate, SendFrequencyEnum sendFrequencyEnum)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);

        if (SendFrequencyEnum.EVERYDAY.equals(sendFrequencyEnum)) {
            // 日
            cal.add(Calendar.DATE, -1);
        } else if (SendFrequencyEnum.MONTHLY.equals(sendFrequencyEnum)) {
            // 月
            cal.add(Calendar.MONTH, -1);
        } else {
            // 週
            cal.add(Calendar.DATE, -7);
        }
        return new Tuple<>(cal.getTime(), toDate);
    }
}
