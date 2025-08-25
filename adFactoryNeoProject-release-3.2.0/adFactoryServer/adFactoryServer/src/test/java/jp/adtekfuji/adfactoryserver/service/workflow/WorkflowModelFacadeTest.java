/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service.workflow;

import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowModelFacade;
import jp.adtekfuji.adfactoryserver.service.workflow.WorkflowInteface;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.service.AccessHierarchyEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.DatabaseControll;
import jp.adtekfuji.adfactoryserver.service.HierarchyEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.KanbanEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.WorkEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.WorkKanbanEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.WorkKanbanWorkingEntityFacadeREST;
import jp.adtekfuji.adfactoryserver.service.WorkflowEntityFacadeREST;
import jp.adtekfuji.bpmn.model.BpmnModel;
import jp.adtekfuji.bpmn.model.BpmnModeler;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class WorkflowModelFacadeTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;

    private static KanbanEntityFacadeREST kanbanRest = null;
    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static WorkKanbanWorkingEntityFacadeREST workKanbanWorkingRest = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;

    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final Map<String, WorkEntity> works = new HashMap<>();
    private final Map<String, BpmnTask> tasks = new HashMap<>();
    private final Map<String, WorkKanbanEntity> workKanbans = new HashMap<>();
    private KanbanEntity kanban = null;
    private WorkflowEntity workflow = null;

    public WorkflowModelFacadeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);
        hierarchyRest.setAuthRest(authRest);

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);
        workflowRest.setWorkEntityFacadeREST(workRest);
        workflowRest.setHierarchyEntityFacadeREST(hierarchyRest);

        workRest.setWorkflowEntityFacadeREST(workflowRest);

        workKanbanRest = new WorkKanbanEntityFacadeREST();
        workKanbanRest.setEntityManager(em);

        workKanbanWorkingRest = new WorkKanbanWorkingEntityFacadeREST();
        workKanbanWorkingRest.setEntityManager(em);

        kanbanRest = new KanbanEntityFacadeREST();
        kanbanRest.setEntityManager(em);
        kanbanRest.setWorkflowRest(workflowRest);
        kanbanRest.setWorkKandanREST(workKanbanRest);
        kanbanRest.setWorkRest(workRest);
        kanbanRest.setWorkKanbanWorkingRest(workKanbanWorkingRest);

        workflowRest.setKanbanEntityFacadeREST(kanbanRest);
    }

    @AfterClass
    public static void tearDownClass() {
        if (Objects.nonNull(em)) {
            em.close();
        }
        if (Objects.nonNull(emf)) {
            emf.close();
        }
    }

    @Before
    public void setUp() {
        tx = em.getTransaction();
    }

    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    private void createData() throws Exception {
        // 工程階層を作成する。
        HierarchyEntity workHierarchy = new HierarchyEntity(HierarchyTypeEnum.WORK, 0L, "tree");

        tx.begin();
        hierarchyRest.add(workHierarchy, null);
        tx.commit();

        //工程作成.
        for (int loop = 1; loop <= 40; loop++) {
            WorkEntity work = new WorkEntity(workHierarchy.getHierarchyId(), "work" + loop, 1, 0, "work" + loop, ContentTypeEnum.STRING, null, null, null, null);
            works.put("work" + loop, work);
            tx.begin();
            workRest.add(work, null);
            tx.commit();
            BpmnTask task = new BpmnTask(String.valueOf(work.getWorkId()), work.getWorkName());
            tasks.put("work" + loop, task);
        }

        //BPMNモデル作成.
        BpmnModel modeler = BpmnModeler.getModeler();
        BpmnStartEvent start = new BpmnStartEvent("start", "start");
        BpmnEndEvent end = new BpmnEndEvent("end", "end");
        modeler.createModel(start, end);
        modeler.addNextNode(start, tasks.get("work1"));
        modeler.addNextNode(tasks.get("work1"), tasks.get("work2"));
        modeler.addNextNode(tasks.get("work2"), tasks.get("work3"));
        modeler.addNextNode(tasks.get("work3"), tasks.get("work4"));
        modeler.addNextNode(tasks.get("work4"), tasks.get("work5"));
        modeler.addNextNode(tasks.get("work5"), tasks.get("work6"));
        BpmnParallelGateway parallelS1 = new BpmnParallelGateway("S1", "S1", null);
        BpmnParallelGateway parallelE1 = new BpmnParallelGateway("E10", "E10", parallelS1);
        modeler.addNextNode(tasks.get("work6"), parallelS1, parallelE1);
        modeler.addParallelNode(parallelS1, tasks.get("work7"));
        modeler.addParallelNode(parallelS1, tasks.get("work8"));
        modeler.addParallelNode(parallelS1, tasks.get("work9"));
        modeler.addParallelNode(parallelS1, tasks.get("work10"));
        modeler.addParallelNode(parallelS1, tasks.get("work11"));
        modeler.addParallelNode(parallelS1, tasks.get("work12"));
        modeler.addNextNode(tasks.get("work7"), tasks.get("work13"));
        modeler.addNextNode(tasks.get("work13"), tasks.get("work14"));
        modeler.addNextNode(tasks.get("work14"), tasks.get("work15"));
        modeler.addNextNode(tasks.get("work15"), tasks.get("work16"));
        modeler.addNextNode(tasks.get("work16"), tasks.get("work17"));
        modeler.addNextNode(tasks.get("work17"), tasks.get("work18"));
        modeler.addNextNode(tasks.get("work8"), tasks.get("work19"));
        modeler.addNextNode(tasks.get("work19"), tasks.get("work20"));
        modeler.addNextNode(tasks.get("work9"), tasks.get("work21"));
        modeler.addNextNode(tasks.get("work21"), tasks.get("work22"));
        modeler.addNextNode(tasks.get("work22"), tasks.get("work23"));
        BpmnParallelGateway parallelS2 = new BpmnParallelGateway("S2", "S2", null);
        BpmnParallelGateway parallelE2 = new BpmnParallelGateway("E20", "E20", parallelS2);
        modeler.addNextNode(parallelE1, parallelS2, parallelE2);
        modeler.addParallelNode(parallelS2, tasks.get("work24"));
        modeler.addParallelNode(parallelS2, tasks.get("work25"));
        modeler.addParallelNode(parallelS2, tasks.get("work26"));
        BpmnParallelGateway parallelS3 = new BpmnParallelGateway("S3", "S3", null);
        BpmnParallelGateway parallelE3 = new BpmnParallelGateway("E30", "E30", parallelS3);
        modeler.addNextNode(tasks.get("work24"), parallelS3, parallelE3);
        modeler.addParallelNode(parallelS3, tasks.get("work27"));
        modeler.addParallelNode(parallelS3, tasks.get("work28"));
        modeler.addParallelNode(parallelS3, tasks.get("work29"));
        modeler.addNextNode(tasks.get("work27"), tasks.get("work30"));
        modeler.addNextNode(parallelE2, tasks.get("work31"));
        modeler.addNextNode(tasks.get("work31"), tasks.get("work32"));
        modeler.addNextNode(tasks.get("work32"), tasks.get("work33"));
        BpmnParallelGateway parallelS4 = new BpmnParallelGateway("S4", "S4", null);
        BpmnParallelGateway parallelE4 = new BpmnParallelGateway("E40", "E40", parallelS4);
        modeler.addNextNode(tasks.get("work33"), parallelS4, parallelE4);
        modeler.addParallelNode(parallelS4, tasks.get("work34"));
        modeler.addParallelNode(parallelS4, tasks.get("work35"));
        modeler.addParallelNode(parallelS4, tasks.get("work36"));
        modeler.addParallelNode(parallelS4, tasks.get("work37"));
        modeler.addParallelNode(parallelS4, tasks.get("work38"));
        modeler.addParallelNode(parallelS4, tasks.get("work39"));
        modeler.addNextNode(parallelE4, tasks.get("work40"));

        // 工程順階層を作成する。
        HierarchyEntity workflowHierarchy = new HierarchyEntity(HierarchyTypeEnum.WORKFLOW, 0L, "tree");

        tx.begin();
        hierarchyRest.add(workflowHierarchy, null);
        tx.commit();

        // 工程順を作成する。
        workflow = new WorkflowEntity(workflowHierarchy.getHierarchyId(), "workflow", "rev", modeler.getBpmnDefinitions().marshal(), null, null, null);

        // 工程順工程関連付けを作成する。
        List<ConWorkflowWorkEntity> workCollection = new ArrayList<>();
        int cnt = 1;
        for (Map.Entry<String, WorkEntity> e : works.entrySet()) {
            ConWorkflowWorkEntity conwork = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK ,null, e.getValue().getWorkId(), false, cnt++, null, null);
            workCollection.add(conwork);
        }

        workflow.setConWorkflowWorkCollection(workCollection);

        tx.begin();
        workflowRest.add(workflow, null);
        tx.commit();

        // カンバンを作成する。
        kanban = new KanbanEntity(0L, "kanban", "sub", workflow.getWorkflowId(), workflow.getWorkflowName(), df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:10:00"), null, null, KanbanStatusEnum.PLANNING, null, null);

        tx.begin();
        kanbanRest.add(kanban, null);
        tx.commit();

        kanban = kanbanRest.find(kanban.getKanbanId(), null);
        for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
            workKanbans.put(workKanban.getWorkName(), workKanban);
        }

        WorkKanbanEntity workKanban3 = workKanbans.get("work3");
        workKanban3.setSkipFlag(true);
        WorkKanbanEntity workKanban4 = workKanbans.get("work4");
        workKanban4.setSkipFlag(true);
        WorkKanbanEntity workKanban9 = workKanbans.get("work9");
        workKanban9.setSkipFlag(true);
        WorkKanbanEntity workKanban15 = workKanbans.get("work15");
        workKanban15.setSkipFlag(true);
        WorkKanbanEntity workKanban34 = workKanbans.get("work34");
        workKanban34.setSkipFlag(true);
        WorkKanbanEntity workKanban35 = workKanbans.get("work35");
        workKanban35.setSkipFlag(true);
        WorkKanbanEntity workKanban36 = workKanbans.get("work36");
        workKanban36.setSkipFlag(true);
        WorkKanbanEntity workKanban37 = workKanbans.get("work37");
        workKanban37.setSkipFlag(true);
        WorkKanbanEntity workKanban38 = workKanbans.get("work38");
        workKanban38.setSkipFlag(true);
        WorkKanbanEntity workKanban39 = workKanbans.get("work39");
        workKanban39.setSkipFlag(true);

        tx.begin();
        workKanbanRest.update(workKanban3, null);
        workKanbanRest.update(workKanban4, null);
        workKanbanRest.update(workKanban9, null);
        workKanbanRest.update(workKanban15, null);
        workKanbanRest.update(workKanban34, null);
        workKanbanRest.update(workKanban35, null);
        workKanbanRest.update(workKanban36, null);
        workKanbanRest.update(workKanban37, null);
        workKanbanRest.update(workKanban38, null);
        workKanbanRest.update(workKanban39, null);
        tx.commit();
    }

    @Test
    public void testExecuteWorkflow() throws Exception {
        System.out.println("executeWorkflow");
        Method updateStatusMethod = KanbanEntityFacadeREST.class.getDeclaredMethod("updateStatus", KanbanEntity.class);
        updateStatusMethod.setAccessible(true);

        createData();

        WorkflowInteface instance = WorkflowModelFacade.createInstance(workKanbanRest, kanban.getWorkKanbanCollection(), workflow.getWorkflowDiaglam());

        kanban = kanbanRest.find(kanban.getKanbanId(), null).clone();
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));

        //開始.
        kanban.setKanbanStatus(KanbanStatusEnum.PLANNED);
        tx.begin();
        kanbanRest.update(kanban, null);
        //instance.executeWorkflow(null);
        tx.commit();
        assertThat(workKanbans.get("work1").getImplementFlag(), is(true));

        kanban = kanbanRest.find(kanban.getKanbanId(), null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));

        //工程を進める.
        workKanbans.get("work1").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work1"), null);
        instance.executeWorkflow(works.get("work1").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work2").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work2").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work2"), null);
        instance.executeWorkflow(works.get("work2").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work3").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work4").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work5").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work5").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work5"), null);
        instance.executeWorkflow(works.get("work5").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work6").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work6").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work6"), null);
        instance.executeWorkflow(works.get("work6").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work7").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work8").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work9").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work21").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work10").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work11").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work12").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work7").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work8").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work21").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work10").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work11").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work12").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work7"), null);
        workKanbanRest.update(workKanbans.get("work8"), null);
        workKanbanRest.update(workKanbans.get("work21"), null);
        workKanbanRest.update(workKanbans.get("work10"), null);
        workKanbanRest.update(workKanbans.get("work11"), null);
        workKanbanRest.update(workKanbans.get("work12"), null);
        instance.executeWorkflow(works.get("work7").getWorkId(), null);
        instance.executeWorkflow(works.get("work8").getWorkId(), null);
        instance.executeWorkflow(works.get("work21").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work13").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work19").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work22").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work13").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work19").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work22").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work13"), null);
        workKanbanRest.update(workKanbans.get("work19"), null);
        workKanbanRest.update(workKanbans.get("work22"), null);
        instance.executeWorkflow(works.get("work13").getWorkId(), null);
        instance.executeWorkflow(works.get("work19").getWorkId(), null);
        instance.executeWorkflow(works.get("work22").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work14").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work20").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work23").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work14").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work20").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work23").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work14"), null);
        workKanbanRest.update(workKanbans.get("work20"), null);
        workKanbanRest.update(workKanbans.get("work23"), null);
        instance.executeWorkflow(works.get("work14").getWorkId(), null);
        instance.executeWorkflow(works.get("work20").getWorkId(), null);
        instance.executeWorkflow(works.get("work23").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work16").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work16").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work16"), null);
        instance.executeWorkflow(works.get("work16").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work17").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work17").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work17"), null);
        instance.executeWorkflow(works.get("work17").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work18").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work24").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work25").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work26").getImplementFlag(), is(false));

        //工程を進める.
        workKanbans.get("work18").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work18"), null);
        instance.executeWorkflow(works.get("work18").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work24").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work25").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work26").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work30").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work31").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work32").getImplementFlag(), is(false));

        //工程を進める.
        workKanbans.get("work24").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work25").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work26").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work24"), null);
        workKanbanRest.update(workKanbans.get("work25"), null);
        workKanbanRest.update(workKanbans.get("work26"), null);
        instance.executeWorkflow(works.get("work24").getWorkId(), null);
        instance.executeWorkflow(works.get("work25").getWorkId(), null);
        instance.executeWorkflow(works.get("work26").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work27").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work28").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work29").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work30").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work31").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work32").getImplementFlag(), is(false));

        //工程を進める.
        workKanbans.get("work27").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work28").setWorkStatus(KanbanStatusEnum.COMPLETION);
        workKanbans.get("work29").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work27"), null);
        workKanbanRest.update(workKanbans.get("work28"), null);
        workKanbanRest.update(workKanbans.get("work29"), null);
        instance.executeWorkflow(works.get("work27").getWorkId(), null);
        instance.executeWorkflow(works.get("work28").getWorkId(), null);
        instance.executeWorkflow(works.get("work29").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work30").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work31").getImplementFlag(), is(false));
        assertThat(workKanbans.get("work32").getImplementFlag(), is(false));

        //工程を進める.
        workKanbans.get("work30").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work30"), null);
        instance.executeWorkflow(works.get("work30").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work31").getImplementFlag(), is(true));
        assertThat(workKanbans.get("work32").getImplementFlag(), is(false));

        //工程を進める.
        workKanbans.get("work31").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work31"), null);
        instance.executeWorkflow(works.get("work31").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work32").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work32").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work32"), null);
        instance.executeWorkflow(works.get("work32").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work33").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work33").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work33"), null);
        instance.executeWorkflow(works.get("work33").getWorkId(), null);
        tx.commit();
        assertThat(workKanbans.get("work40").getImplementFlag(), is(true));

        //工程を進める.
        workKanbans.get("work40").setWorkStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        workKanbanRest.update(workKanbans.get("work40"), null);
        instance.executeWorkflow(works.get("work40").getWorkId(), null);
        tx.commit();

        tx.begin();
        KanbanStatusEnum status = (KanbanStatusEnum) updateStatusMethod.invoke(kanbanRest, kanban);
        tx.commit();
        assertThat(status, is(KanbanStatusEnum.COMPLETION));
    }
}
