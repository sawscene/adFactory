/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ActualResultDailyEnum;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationAddInfoEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
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
public class ActualResultEntityFacadeRESTTest {

    private static ServiceTestData serviceTestData = null;

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;

    private static ActualResultEntityFacadeREST actualRest = null;
    private static KanbanHierarchyEntityFacadeREST kanbanHierarchyRest = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static WorkflowHierarchyEntityFacadeREST workflowHierarchyRest = null;
    private static WorkHierarchyEntityFacadeREST workHierarchyRest = null;
    private static KanbanEntityFacadeREST kanbanRest = null;
    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static EquipmentEntityFacadeREST equipmentRest = null;
    private static OperationEntityFacadeREST operationEntityFacadeREST = null;
    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private ActualResultEntity actual1;
    private ActualResultEntity actual2;
    private ActualResultEntity actual3;
    private ActualResultEntity actual4;
    private ActualResultEntity actual5;
    private ActualResultEntity actual6;
    private ActualResultEntity actual7;
    private ActualResultEntity actual8;
    private ActualResultEntity actual9;
    private ActualResultEntity actual10;
    private EquipmentEntity equip1;
    private EquipmentEntity equip2;
    private EquipmentEntity equip3;
    private EquipmentEntity equip4;
    private EquipmentEntity equip5;
    private EquipmentEntity equip6;
    private EquipmentEntity equip7;
    private EquipmentEntity equip8;
    private EquipmentEntity equip9;
    private EquipmentEntity equip10;
    private OrganizationEntity organization1;
    private OrganizationEntity organization2;
    private OrganizationEntity organization3;
    private OrganizationEntity organization4;
    private OrganizationEntity organization5;
    private OrganizationEntity organization6;
    private OrganizationEntity organization7;
    private OrganizationEntity organization8;
    private OrganizationEntity organization9;
    private OrganizationEntity organization10;
    private WorkflowEntity workflow1;
    private WorkflowEntity workflow2;
    private WorkflowEntity workflow3;
    private WorkflowEntity workflow4;
    private KanbanEntity kanban1;
    private KanbanEntity kanban2;
    private KanbanEntity kanban3;
    private KanbanEntity kanban4;

    public ActualResultEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        serviceTestData = new ServiceTestData();
        ServiceTestData.setUpClass();

        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        equipmentRest = new EquipmentEntityFacadeREST();
        equipmentRest.setEntityManager(em);
        equipmentRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setEquipmentRest(equipmentRest);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);

        workflowHierarchyRest = new WorkflowHierarchyEntityFacadeREST();
        workflowHierarchyRest.setHierarchyRest(hierarchyRest);
        
        workHierarchyRest = new WorkHierarchyEntityFacadeREST();
        workHierarchyRest.setHierarchyRest(hierarchyRest);

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);
        workflowRest.setWorkEntityFacadeREST(workRest);
        workflowRest.setHierarchyEntityFacadeREST(hierarchyRest);

        workRest.setWorkflowEntityFacadeREST(workflowRest);

        workKanbanRest = new WorkKanbanEntityFacadeREST();
        workKanbanRest.setEntityManager(em);
        workKanbanRest.setEquipmentRest(equipmentRest);
        workKanbanRest.setOrganizationRest(organizationRest);

        kanbanRest = new KanbanEntityFacadeREST();
        kanbanRest.setEntityManager(em);
        kanbanRest.setWorkflowRest(workflowRest);
        kanbanRest.setWorkKandanREST(workKanbanRest);
        kanbanRest.setWorkRest(workRest);
        kanbanRest.setOrganizationRest(organizationRest);
        kanbanRest.setEquipmentRest(equipmentRest);
        kanbanRest.setActualResultRest(actualRest);

        workflowRest.setKanbanEntityFacadeREST(kanbanRest);

        operationEntityFacadeREST = new OperationEntityFacadeREST();
        operationEntityFacadeREST.setEntityManager(em);
        kanbanRest.setOperationEntityFacadeREST(operationEntityFacadeREST);

        kanbanHierarchyRest = new KanbanHierarchyEntityFacadeREST();
        kanbanHierarchyRest.setEntityManager(em);
        kanbanHierarchyRest.setKanbanFacadeRest(kanbanRest);
        kanbanHierarchyRest.setWorkflowFacadeRest(workflowRest);

        actualRest = new ActualResultEntityFacadeREST();
        actualRest.setEntityManager(em);
        actualRest.setKanbanHierarchyRest(kanbanHierarchyRest);
        actualRest.setWorkflowHierarchyRest(workflowHierarchyRest);
        actualRest.setWorkHierarchyRest(workHierarchyRest);
        actualRest.setKanbanRest(kanbanRest);
        actualRest.setWorkKandanRest(workKanbanRest);
        actualRest.setWorkflowRest(workflowRest);
        actualRest.setWorkRest(workRest);
        actualRest.setOrganizationRest(organizationRest);
        actualRest.setEquipmentRest(equipmentRest);

        LicenseManager.setupTest();
    }

    @AfterClass
    public static void tearDownClass() {
        ServiceTestData.tearDownClass();

        if (Objects.nonNull(em)) {
            em.close();
        }
        if (Objects.nonNull(emf)) {
            emf.close();
        }
    }

    @Before
    public void setUp() throws Exception {
        if (Objects.nonNull(serviceTestData)) {
            serviceTestData.setUp();
        }
        tx = ServiceTestData.getTransaction();

        tx = em.getTransaction();
    }

    @After
    public void tearDown() {
        if (Objects.nonNull(serviceTestData)) {
            serviceTestData.tearDown();
        }

        DatabaseControll.reset(em, tx);
    }

    private void createData() throws Exception {
        equip1 = new EquipmentEntity(0L, "equip1", "identname1", null, null, null);
        equip2 = new EquipmentEntity(0L, "equip2", "identname2", null, null, null);
        equip3 = new EquipmentEntity(0L, "equip3", "identname3", null, null, null);
        equip4 = new EquipmentEntity(0L, "equip4", "identname4", null, null, null);
        equip5 = new EquipmentEntity(0L, "equip5", "identname5", null, null, null);
        equip6 = new EquipmentEntity(0L, "equip6", "identname6", null, null, null);
        equip7 = new EquipmentEntity(0L, "equip7", "identname7", null, null, null);
        equip8 = new EquipmentEntity(0L, "equip8", "identname8", null, null, null);
        equip9 = new EquipmentEntity(0L, "equip9", "identname9", null, null, null);
        equip10 = new EquipmentEntity(0L, "equip10", "identname10", null, null, null);
        organization1 = new OrganizationEntity(0L, "parson1", "identname1", AuthorityEnum.WORKER, null, null, null, null, null);
        organization2 = new OrganizationEntity(0L, "parson2", "identname2", AuthorityEnum.WORKER, null, null, null, null, null);
        organization3 = new OrganizationEntity(0L, "parson3", "identname3", AuthorityEnum.WORKER, null, null, null, null, null);
        organization4 = new OrganizationEntity(0L, "parson4", "identname4", AuthorityEnum.WORKER, null, null, null, null, null);
        organization5 = new OrganizationEntity(0L, "parson5", "identname5", AuthorityEnum.WORKER, null, null, null, null, null);
        organization6 = new OrganizationEntity(0L, "parson6", "identname6", AuthorityEnum.WORKER, null, null, null, null, null);
        organization7 = new OrganizationEntity(0L, "parson7", "identname7", AuthorityEnum.WORKER, null, null, null, null, null);
        organization8 = new OrganizationEntity(0L, "parson8", "identname8", AuthorityEnum.WORKER, null, null, null, null, null);
        organization9 = new OrganizationEntity(0L, "parson9", "identname9", AuthorityEnum.WORKER, null, null, null, null, null);
        organization10 = new OrganizationEntity(0L, "parson10", "identname10", AuthorityEnum.WORKER, null, null, null, null, null);
        tx.begin();
        equipmentRest.add(equip1, null);
        equipmentRest.add(equip2, null);
        equipmentRest.add(equip3, null);
        equipmentRest.add(equip4, null);
        equipmentRest.add(equip5, null);
        equipmentRest.add(equip6, null);
        equipmentRest.add(equip7, null);
        equipmentRest.add(equip8, null);
        equipmentRest.add(equip9, null);
        equipmentRest.add(equip10, null);
        organizationRest.add(organization1, null);
        organizationRest.add(organization2, null);
        organizationRest.add(organization3, null);
        organizationRest.add(organization4, null);
        organizationRest.add(organization5, null);
        organizationRest.add(organization6, null);
        organizationRest.add(organization7, null);
        organizationRest.add(organization8, null);
        organizationRest.add(organization9, null);
        organizationRest.add(organization10, null);
        tx.commit();

        workflow1 = new WorkflowEntity(0L, "workflow1", "1", "workflowDiagram1", 0L, df.parse("2015/11/17 07:00:00"), "ledget1");
        workflow2 = new WorkflowEntity(0L, "workflow2", "1", "workflowDiagram1", 0L, df.parse("2015/11/17 07:10:00"), "ledget1");
        workflow3 = new WorkflowEntity(0L, "workflow3", "1", "workflowDiagram1", 0L, df.parse("2015/11/17 07:20:00"), "ledget1");
        workflow4 = new WorkflowEntity(0L, "workflow4", "1", "workflowDiagram1", 0L, df.parse("2015/11/17 07:30:00"), "ledget1");
        tx.begin();
        workflowRest.add(workflow1, null);
        workflowRest.add(workflow2, null);
        workflowRest.add(workflow3, null);
        workflowRest.add(workflow4, null);
        tx.commit();

        kanban1 = new KanbanEntity("kanban1", workflow1.getWorkflowId(), workflow1.getWorkflowName(), 0L, df.parse("2015/11/17 08:00:00"), KanbanStatusEnum.PLANNED);
        kanban1.setModelName("model1");
        kanban2 = new KanbanEntity("kanban1", workflow2.getWorkflowId(), workflow2.getWorkflowName(), 0L, df.parse("2015/11/17 08:10:00"), KanbanStatusEnum.PLANNED);
        kanban2.setModelName("model2");
        kanban3 = new KanbanEntity("kanban1", workflow3.getWorkflowId(), workflow3.getWorkflowName(), 0L, df.parse("2015/11/17 08:20:00"), KanbanStatusEnum.PLANNED);
        kanban3.setModelName("model11");
        kanban4 = new KanbanEntity("kanban1", workflow4.getWorkflowId(), workflow4.getWorkflowName(), 0L, df.parse("2015/11/17 08:30:00"), KanbanStatusEnum.PLANNED);
        kanban4.setModelName("model12");
        tx.begin();
        kanbanRest.add(kanban1, null);
        kanbanRest.add(kanban2, null);
        kanbanRest.add(kanban3, null);
        kanbanRest.add(kanban4, null);
        tx.commit();

        actual1 = new ActualResultEntity(kanban1.getKanbanId(), 200L, df.parse("2015/11/18 08:00:00"), 0L, equip1.getEquipmentId(), organization1.getOrganizationId(), 500L, 600L, KanbanStatusEnum.PLANNED, "", "", 0, null, null, 0, false);
        actual1.setKanbanName(kanban1.getKanbanName());
        actual1.setEquipmentName(equip1.getEquipmentName());
        actual1.setOrganizationName(organization1.getOrganizationName());

        AddInfoEntity addInfo1 = new AddInfoEntity("key1", CustomPropertyTypeEnum.TYPE_STRING, "val1", 1, null);
        AddInfoEntity addInfo2 = new AddInfoEntity("key2", CustomPropertyTypeEnum.TYPE_STRING, "val2", 2, null);
        AddInfoEntity addInfo3 = new AddInfoEntity("key3", CustomPropertyTypeEnum.TYPE_TRACE, "val3", 3, null);
        AddInfoEntity addInfo4 = new AddInfoEntity("key4", CustomPropertyTypeEnum.TYPE_TRACE, "val4", 4, null);
        AddInfoEntity addInfo5 = new AddInfoEntity("key5", CustomPropertyTypeEnum.TYPE_STRING, "val5", 5, null);

        List<AddInfoEntity> addInfos = Arrays.asList(addInfo1, addInfo2, addInfo3, addInfo4, addInfo5);
        String actualAddInfo = JsonUtils.objectsToJson(addInfos);

        actual1.setActualAddInfo(actualAddInfo);

        actual2 = new ActualResultEntity(kanban1.getKanbanId(), 201L, df.parse("2015/11/18 08:10:00"), 0L, equip2.getEquipmentId(), organization2.getOrganizationId(), 501L, 601L, KanbanStatusEnum.PLANNED, "", "", 0, null, null, 0, false);
        actual2.setKanbanName(kanban1.getKanbanName());
        actual2.setEquipmentName(equip2.getEquipmentName());
        actual2.setOrganizationName(organization2.getOrganizationName());

        actual3 = new ActualResultEntity(kanban2.getKanbanId(), 202L, df.parse("2015/11/18 08:20:00"), 0L, equip3.getEquipmentId(), organization3.getOrganizationId(), 502L, 602L, KanbanStatusEnum.WORKING, "", "", 0, null, null,0, false);
        actual3.setKanbanName(kanban2.getKanbanName());
        actual3.setEquipmentName(equip3.getEquipmentName());
        actual3.setOrganizationName(organization3.getOrganizationName());

        actual4 = new ActualResultEntity(kanban2.getKanbanId(), 203L, df.parse("2015/11/18 08:30:00"), 0L, equip4.getEquipmentId(), organization4.getOrganizationId(), 503L, 603L, KanbanStatusEnum.WORKING, "", "", 0, null, null,0, false);
        actual4.setKanbanName(kanban2.getKanbanName());
        actual4.setEquipmentName(equip4.getEquipmentName());
        actual4.setOrganizationName(organization4.getOrganizationName());

        actual5 = new ActualResultEntity(kanban3.getKanbanId(), 204L, df.parse("2015/11/18 08:40:00"), 0L, equip5.getEquipmentId(), organization5.getOrganizationId(), 504L, 604L, KanbanStatusEnum.SUSPEND, "", "", 0, null, null,0, false);
        actual5.setKanbanName(kanban3.getKanbanName());
        actual5.setEquipmentName(equip5.getEquipmentName());
        actual5.setOrganizationName(organization5.getOrganizationName());

        actual6 = new ActualResultEntity(kanban3.getKanbanId(), 205L, df.parse("2015/11/18 08:50:00"), 0L, equip6.getEquipmentId(), organization6.getOrganizationId(), 505L, 605L, KanbanStatusEnum.SUSPEND, "", "", 0, null, null,0, false);
        actual6.setKanbanName(kanban3.getKanbanName());
        actual6.setEquipmentName(equip6.getEquipmentName());
        actual6.setOrganizationName(organization6.getOrganizationName());

        actual7 = new ActualResultEntity(kanban3.getKanbanId(), 206L, df.parse("2015/11/18 09:00:00"), 0L, equip7.getEquipmentId(), organization7.getOrganizationId(), 506L, 606L, KanbanStatusEnum.INTERRUPT, "", "", 0, null, null,0, false);
        actual7.setKanbanName(kanban3.getKanbanName());
        actual7.setEquipmentName(equip7.getEquipmentName());
        actual7.setOrganizationName(organization7.getOrganizationName());

        actual8 = new ActualResultEntity(kanban3.getKanbanId(), 207L, df.parse("2015/11/18 09:10:00"), 0L, equip8.getEquipmentId(), organization8.getOrganizationId(), 507L, 607L, KanbanStatusEnum.INTERRUPT, "", "", 0, null, null,0, false);
        actual8.setKanbanName(kanban3.getKanbanName());
        actual8.setEquipmentName(equip8.getEquipmentName());
        actual8.setOrganizationName(organization8.getOrganizationName());

        actual9 = new ActualResultEntity(kanban3.getKanbanId(), 208L, df.parse("2015/11/18 09:20:00"), 0L, equip9.getEquipmentId(), organization9.getOrganizationId(), 508L, 608L, KanbanStatusEnum.COMPLETION, "", "", 0, null, null,0, false);
        actual9.setKanbanName(kanban3.getKanbanName());
        actual9.setEquipmentName(equip9.getEquipmentName());
        actual9.setOrganizationName(organization9.getOrganizationName());

        actual10 = new ActualResultEntity(kanban4.getKanbanId(), 209L, df.parse("2015/11/18 09:30:00"), 0L, equip10.getEquipmentId(), organization10.getOrganizationId(), 509L, 609L, KanbanStatusEnum.COMPLETION, "", "", 0, null, null,0, false);
        actual10.setKanbanName(kanban4.getKanbanName());
        actual10.setEquipmentName(equip10.getEquipmentName());
        actual10.setOrganizationName(organization10.getOrganizationName());

        tx.begin();
        actualRest.add(actual1);
        actualRest.add(actual2);
        actualRest.add(actual3);
        actualRest.add(actual4);
        actualRest.add(actual5);
        actualRest.add(actual6);
        actualRest.add(actual7);
        actualRest.add(actual8);
        actualRest.add(actual9);
        actualRest.add(actual10);
        tx.commit();
    }

    @Test
    public void testSearch() throws Exception {
        System.out.println("testSearch");

        createData();

        List<ActualResultEntity> actruals;
        actruals = actualRest.searchActualResult(new ActualSearchCondition(700L, null, null, null, null, null), null, null ,null);
        assertThat(actruals, is(empty()));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, null, null), null, null ,null);
        assertThat(actruals, is(hasSize(10)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(kanban1.getKanbanId(), null, null, null, null, null), null, null ,null);
        assertThat(actruals, is(hasSize(2)));
        assertThat(actruals, is(hasItems(actual1, actual2)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, null, null).statusList(Arrays.asList(KanbanStatusEnum.WORKING)), null, null ,null);
        assertThat(actruals, is(hasSize(2)));
        assertThat(actruals, is(hasItems(actual3, actual4)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(kanban1.getKanbanId(), null, null, 501L, null, null), null, null ,null);
        assertThat(actruals, is(hasSize(1)));
        assertThat(actruals, is(hasItems(actual2)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")), null, null ,null);
        assertThat(actruals, is(hasSize(5)));
        assertThat(actruals, is(hasItems(actual2, actual3, actual4, actual5, actual6)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(kanban3.getKanbanId(), null, null, 505L, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")).statusList(Arrays.asList(KanbanStatusEnum.SUSPEND)), null, null ,null);
        assertThat(actruals, is(hasSize(1)));
        assertThat(actruals, is(hasItems(actual6)));
    }

    @Test
    public void testSearchRange() throws Exception {
        System.out.println("testCountSearch");

        createData();

        List<ActualResultEntity> actruals;
        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")), 0, 1, null);
        assertThat(actruals, is(hasSize(2)));
        assertThat(actruals, is(hasItems(actual2, actual3)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")), 1, 3, null);
        assertThat(actruals, is(hasSize(3)));
        assertThat(actruals, is(hasItems(actual3, actual4, actual5)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")), 0, 100, null);
        assertThat(actruals, is(hasSize(5)));
        assertThat(actruals, is(hasItems(actual2, actual3, actual4, actual5, actual6)));
    }

    @Test
    public void testCountSearch() throws Exception {
        System.out.println("testCountSearch");

        createData();

        String cnt;
        cnt = actualRest.countActualResult(new ActualSearchCondition(700L, null, null, null, null, null), null);
        assertThat(Integer.parseInt(cnt), is(0));

        cnt = actualRest.countActualResult(new ActualSearchCondition(null, null, null, null, null, null), null);
        assertThat(Integer.parseInt(cnt), is(10));

        cnt = actualRest.countActualResult(new ActualSearchCondition(kanban1.getKanbanId(), null, null, null, null, null), null);
        assertThat(Integer.parseInt(cnt), is(2));

        cnt = actualRest.countActualResult(new ActualSearchCondition(null, null, null, null, null, null).statusList(Arrays.asList(KanbanStatusEnum.WORKING)), null);
        assertThat(Integer.parseInt(cnt), is(2));

        cnt = actualRest.countActualResult(new ActualSearchCondition(kanban1.getKanbanId(), null, null, 501L, null, null), null);
        assertThat(Integer.parseInt(cnt), is(1));

        cnt = actualRest.countActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")), null);
        assertThat(Integer.parseInt(cnt), is(5));

        cnt = actualRest.countActualResult(new ActualSearchCondition(kanban3.getKanbanId(), null, null, 505L, df.parse("2015/11/18 08:08:00"), df.parse("2015/11/18 08:53:32")).statusList(Arrays.asList(KanbanStatusEnum.SUSPEND)), null);
        assertThat(Integer.parseInt(cnt), is(1));
    }

    @Test
    public void testSearchByEquipment() throws Exception {
        System.out.println("testSearchByEquipment");

        createData();

        List<ActualResultEntity> actuals;
        actuals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, null, null)
                .equipmentList(Arrays.asList(equip4.getEquipmentId(), equip6.getEquipmentId(), equip2.getEquipmentId())), null, null, null);
        assertThat(actuals, is(hasSize(3)));
        assertThat(actuals, is(hasItems(actual4, actual6, actual2)));

        actuals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:20:00"), df.parse("2015/11/18 08:53:32"))
                .equipmentList(Arrays.asList(equip4.getEquipmentId(), equip6.getEquipmentId(), equip2.getEquipmentId())), null, null, null);
        assertThat(actuals, is(hasSize(2)));
        assertThat(actuals, is(hasItems(actual4, actual6)));

        actuals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, null, null)
                .equipmentNameList(Arrays.asList("equip5")), null, null, null);
        assertThat(actuals, is(hasSize(1)));
        assertThat(actuals, is(hasItems(actual5)));
    }

    @Test
    public void testSearchByOrganization() throws Exception {
        System.out.println("testSearchByOrganization");

        createData();

        List<ActualResultEntity> actruals;
        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, null, null)
                .organizationList(Arrays.asList(organization4.getOrganizationId(), organization6.getOrganizationId(), organization2.getOrganizationId())), null, null, null);
        assertThat(actruals, is(hasSize(3)));
        assertThat(actruals, is(hasItems(actual4, actual6, actual2)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, df.parse("2015/11/18 08:20:00"), df.parse("2015/11/18 08:53:32"))
                .organizationList(Arrays.asList(organization4.getOrganizationId(), organization6.getOrganizationId(), organization2.getOrganizationId())), null, null, null);
        assertThat(actruals, is(hasSize(2)));
        assertThat(actruals, is(hasItems(actual4, actual6)));

        actruals = actualRest.searchActualResult(new ActualSearchCondition(null, null, null, null, null, null)
                .organizationNameList(Arrays.asList("parson3")), null, null, null);
        assertThat(actruals, is(hasSize(1)));
        assertThat(actruals, is(hasItems(actual3)));
    }

    @Test
    public void testActualReport() throws Exception {
        System.out.println("testActualReport");

        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = serviceTestData.createTestKanban();

        OrganizationEntity worker = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_1, null, null);
        EquipmentEntity adPro = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_1, null, null);

        WorkKanbanEntity workKanban = kanban.getWorkKanbanCollection().stream()
                .filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_1)).findFirst().get();

        ActualProductReportResult result;

        result = serviceTestData.report(0L, workKanban, adPro, worker, df.parse("2019/04/11 10:00:00"), KanbanStatusEnum.PLANNING, false, true);
        assertThat(result.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result.getNextTransactionID(), is(1L));

        result = serviceTestData.report(1L, workKanban, adPro, worker, df.parse("2019/04/11 10:00:00"), KanbanStatusEnum.PLANNED, false, true);
        assertThat(result.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result.getNextTransactionID(), is(2L));

        result = serviceTestData.report(2L, workKanban, adPro, worker, df.parse("2019/04/11 10:00:00"), KanbanStatusEnum.WORKING, false, true);
        assertThat(result.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result.getNextTransactionID(), is(3L));

        // 再送
        result = serviceTestData.report(2L, workKanban, adPro, worker, df.parse("2019/04/11 10:00:00"), KanbanStatusEnum.WORKING, false, true);
        assertThat(result.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result.getNextTransactionID(), is(3L));
    }

    @Test
    public void testGetFirstActualResult() throws Exception {
        System.out.println("testGetFirstActualResult");

        createData();

        ActualSearchCondition condition;
        ActualResultEntity result;

        condition = new ActualSearchCondition(null, null, null, null, null, null);
        condition.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.WORKING));
        condition.setResultDailyEnum(ActualResultDailyEnum.ALL);
        result = actualRest.findFirstActualResult(condition, null);
        assertThat(result, is(actual3));
    }

    @Test
    public void testGetLastActualResult() throws Exception {
        System.out.println("testGetLastActualResult");

        createData();

        ActualSearchCondition condition;
        ActualResultEntity result;

        condition = new ActualSearchCondition(null, null, null, null, null, null);
        condition.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.WORKING));
        condition.setResultDailyEnum(ActualResultDailyEnum.ALL);
        result = actualRest.findLastActualResult(condition, null);
        assertThat(result, is(actual4));
    }

    /**
     * 設備名と時間から実績のカンバンIDを検索する
     * 
     * @throws Exception
     */
    @Test
    public void testFindKanban() throws Exception {
        System.out.println("testFindKanban");

        createData();

        // モデル名なし
        List<Long> result;

        result = actualRest.findKanban(Arrays.asList(equip1.getEquipmentId()), df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:30:00"));
        assertThat(result, is(hasSize(1)));
        assertThat(result, is(hasItems(kanban1.getKanbanId())));

        result = actualRest.findKanban(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId()), df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:29:00"));
        assertThat(result, is(hasSize(3)));
        assertThat(result, is(hasItems(kanban1.getKanbanId(), kanban2.getKanbanId())));
    }

    /**
     * 設備名と時間とモデル名から実績のカンバンIDを検索する
     *
     * @throws Exception
     */
    @Test
    public void testFindKanbanByModelName() throws Exception {
        System.out.println("testFindKanbanByModelName");

        createData();

        // モデル名あり
        List<Long> result;

        // equip1, equip2は同じカンバンに割当たっているので注意
        // 設備・時間でのカンバンとモデル名が一致
        result = actualRest.findKanban(Arrays.asList(equip1.getEquipmentId()), "model1", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:30:00"));
        assertThat(result, is(hasSize(1)));
        assertThat(result, is(hasItems(kanban1.getKanbanId())));

        // 設備・時間で絞り込んだがモデル名が不一致
        result = actualRest.findKanban(Arrays.asList(equip1.getEquipmentId()), "model2", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:30:00"));
        assertThat(result, is(hasSize(0)));

        // 設備・時間で絞り込んだ複数のカンバンからモデル名の一致したものを取り出す
        result = actualRest.findKanban(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId()), "model1", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:19:00"));
        assertThat(result, is(hasSize(2)));
        assertThat(result, is(hasItems(kanban1.getKanbanId())));

        // 正規表現での取り出し
        result = actualRest.findKanban(
                Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId(), equip5.getEquipmentId(), equip6.getEquipmentId(), equip7.getEquipmentId(), equip8.getEquipmentId(), equip9.getEquipmentId(), equip10.getEquipmentId()),
                ".*ode.*",
                df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 12:00:00"));
        assertThat(result, is(hasSize(10)));
        assertThat(result, is(hasItems(kanban1.getKanbanId(), kanban2.getKanbanId(), kanban3.getKanbanId(), kanban4.getKanbanId())));

        // 正規表現その2
        result = actualRest.findKanban(
                Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId(), equip5.getEquipmentId(), equip6.getEquipmentId(), equip7.getEquipmentId(), equip8.getEquipmentId(), equip9.getEquipmentId(), equip10.getEquipmentId()),
                "model1.*",
                df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 12:00:00"));
        assertThat(result, is(hasSize(8)));
        assertThat(result, is(hasItems(kanban1.getKanbanId(), kanban3.getKanbanId(), kanban4.getKanbanId())));

        // 何もないならすべてが対象
        result = actualRest.findKanban(
                Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId(), equip5.getEquipmentId(), equip6.getEquipmentId(), equip7.getEquipmentId(), equip8.getEquipmentId(), equip9.getEquipmentId(), equip10.getEquipmentId()),
                "",
                df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 12:00:00"));
        assertThat(result, is(hasSize(10)));
        assertThat(result, is(hasItems(kanban1.getKanbanId(), kanban2.getKanbanId(), kanban3.getKanbanId(), kanban4.getKanbanId())));

        // nullの場合もすべてが対象
        result = actualRest.findKanban(
                Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId(), equip5.getEquipmentId(), equip6.getEquipmentId(), equip7.getEquipmentId(), equip8.getEquipmentId(), equip9.getEquipmentId(), equip10.getEquipmentId()),
                null,
                df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 12:00:00"));
        assertThat(result, is(hasSize(10)));
        assertThat(result, is(hasItems(kanban1.getKanbanId(), kanban2.getKanbanId(), kanban3.getKanbanId(), kanban4.getKanbanId())));
    }

    /**
     * 工程実績IDを指定して、品質データを更新する。
     */
    @Test
    public void testUpdateAddInfo() throws Exception {
        System.out.println("testFindKanbanByModelName");

        this.createData();

        // 工程実績情報を取得する。
        ActualSearchCondition condition = new ActualSearchCondition(kanban1.getKanbanId(), null, null, null, null, null);

        List<ActualResultEntity> actruals = actualRest.searchActualResult(condition, null, null ,null);
        assertThat(actruals, is(hasSize(2)));
        assertThat(actruals, is(hasItems(actual1, actual2)));

        List<AddInfoEntity> actualAddInfos = JsonUtils.jsonToObjects(actual1.getActualAddInfo(), AddInfoEntity[].class);
        assertThat(actualAddInfos, is(hasSize(5)));

        List<AddInfoEntity> traceInfos = actualAddInfos.stream()
                .filter(p -> Objects.equals(p.getType(), CustomPropertyTypeEnum.TYPE_TRACE))
                .collect(Collectors.toList());
        assertThat(traceInfos, is(hasSize(2)));

        // 品質データを更新する。
        traceInfos.get(1).setVal("newValue");

        String newActualAddInfos = JsonUtils.objectsToJson(actualAddInfos);

        tx.begin();
        Response response = actualRest.updateAddInfo(actual2.getActualId(), newActualAddInfos, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();
        em.clear();

        // 工程実績情報を再取得して、品質データが更新されていることを確認する。
        List<ActualResultEntity> resultActruals = actualRest.searchActualResult(condition, null, null ,null);
        assertThat(resultActruals, is(hasSize(2)));
        assertThat(resultActruals, is(hasItems(actual1, actual2)));

        List<AddInfoEntity> resultActualAddInfos = JsonUtils.jsonToObjects(actual1.getActualAddInfo(), AddInfoEntity[].class);
        assertThat(resultActualAddInfos, is(hasSize(5)));

        List<AddInfoEntity> resultTraceInfos = actualAddInfos.stream()
                .filter(p -> Objects.equals(p.getType(), CustomPropertyTypeEnum.TYPE_TRACE))
                .collect(Collectors.toList());
        assertThat(traceInfos, is(hasSize(2)));

        assertThat(resultTraceInfos.get(0), is(traceInfos.get(0)));
        assertThat(resultTraceInfos.get(1), is(traceInfos.get(1)));
    }
}
