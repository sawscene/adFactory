/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import static org.hamcrest.CoreMatchers.is;
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
public class WorkKanbanEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static KanbanEntityFacadeREST kanbanRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static EquipmentEntityFacadeREST equipmentRest = null;
    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static KanbanHierarchyEntityFacadeREST kanbanHierarchyRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;

    public WorkKanbanEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        equipmentRest = new EquipmentEntityFacadeREST();
        equipmentRest.setEntityManager(em);
        equipmentRest.setAuthRest(authRest);
        equipmentRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setAuthRest(authRest);
        organizationRest.setEquipmentRest(equipmentRest);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

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
        workKanbanRest.setEquipmentRest(equipmentRest);
        workKanbanRest.setOrganizationRest(organizationRest);

        kanbanRest = new KanbanEntityFacadeREST();
        kanbanRest.setEntityManager(em);
        kanbanRest.setWorkflowRest(workflowRest);
        kanbanRest.setWorkKandanREST(workKanbanRest);
        kanbanRest.setWorkRest(workRest);
        kanbanRest.setOrganizationRest(organizationRest);

        workflowRest.setKanbanEntityFacadeREST(kanbanRest);

        kanbanHierarchyRest = new KanbanHierarchyEntityFacadeREST();
        kanbanHierarchyRest.setEntityManager(em);
        kanbanHierarchyRest.setAuthRest(authRest);
        kanbanHierarchyRest.setKanbanFacadeRest(kanbanRest);
        kanbanHierarchyRest.setWorkflowFacadeRest(workflowRest);

        tx = em.getTransaction();

        LicenseManager.setupTest();
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

    private final List<Long> workflows = new ArrayList<>();
    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private KanbanEntity kanban1;
    private KanbanEntity kanban2;
    private WorkflowEntity workflow1;
    private WorkflowEntity workflow2;
    private EquipmentEntity equip1;
    private EquipmentEntity equip2;
    private EquipmentEntity equip3;
    private OrganizationEntity organization1;
    private OrganizationEntity organization2;
    private OrganizationEntity organization3;

    private void createData() throws URISyntaxException, ParseException {
        EquipmentEntity equip0 = new EquipmentEntity(0L, "equip0", "identname0", null, null, null);
        OrganizationEntity organization0 = new OrganizationEntity(0L, "parson0", "identname0", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        equipmentRest.add(equip0, null);
        organizationRest.add(organization0, null);
        tx.commit();

        equip1 = new EquipmentEntity(equip0.getEquipmentId(), "equip1", "identname1", null, null, null);
        equip2 = new EquipmentEntity(equip0.getEquipmentId(), "equip2", "identname2", null, null, null);
        equip3 = new EquipmentEntity(equip0.getEquipmentId(), "equip3", "identname3", null, null, null);
        organization1 = new OrganizationEntity(organization0.getOrganizationId(), "parson1", "identname1", AuthorityEnum.WORKER, null, null, null, null, null);
        organization2 = new OrganizationEntity(organization0.getOrganizationId(), "parson2", "identname2", AuthorityEnum.WORKER, null, null, null, null, null);
        organization3 = new OrganizationEntity(organization0.getOrganizationId(), "parson3", "identname3", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        equipmentRest.add(equip1, null);
        equipmentRest.add(equip2, null);
        equipmentRest.add(equip3, null);
        organizationRest.add(organization1, null);
        organizationRest.add(organization2, null);
        organizationRest.add(organization3, null);
        tx.commit();

        HierarchyEntity workTree1 = new HierarchyEntity(HierarchyTypeEnum.WORK, 0L, "tree1");

        tx.begin();
        hierarchyRest.add(workTree1, null);
        tx.commit();

        WorkEntity work1 = new WorkEntity(workTree1.getHierarchyId(), "work1", 1, 0, "work1", ContentTypeEnum.STRING, null, df.parse("2015/11/18 08:00:00"), null, null);
        WorkEntity work2 = new WorkEntity(workTree1.getHierarchyId(), "work2", 1, 0, "work2", ContentTypeEnum.STRING, null, df.parse("2015/11/18 08:00:00"), null, null);
        WorkEntity work3 = new WorkEntity(workTree1.getHierarchyId(), "work3", 1, 0, "work3", ContentTypeEnum.STRING, null, df.parse("2015/11/18 08:00:00"), null, null);
        WorkEntity work4 = new WorkEntity(workTree1.getHierarchyId(), "work4", 1, 0, "work4", ContentTypeEnum.STRING, null, df.parse("2015/11/18 08:00:00"), null, null);
        WorkEntity work5 = new WorkEntity(workTree1.getHierarchyId(), "work5", 1, 0, "work5", ContentTypeEnum.STRING, null, df.parse("2015/11/18 08:00:00"), null, null);
        WorkEntity work6 = new WorkEntity(workTree1.getHierarchyId(), "work6", 1, 0, "work6", ContentTypeEnum.STRING, null, df.parse("2015/11/18 08:00:00"), null, null);

        tx.begin();
        workRest.add(work1, null);
        workRest.add(work2, null);
        workRest.add(work3, null);
        workRest.add(work4, null);
        workRest.add(work5, null);
        workRest.add(work6, null);
        tx.commit();

        String diaglam1 = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
                .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
                .append("<process isExecutable=\"true\">")
                .append("<startEvent id=\"start_id\" name=\"start\"/>")
                .append("<endEvent id=\"end_id\" name=\"end\"/>")
                .append("<task id=\"1\" name=\"work1\"/>")
                .append("<task id=\"2\" name=\"work2\"/>")
                .append("<task id=\"3\" name=\"work3\"/>")
                .append("<sequenceFlow sourceRef=\"start_id\" targetRef=\"1\" id=\"kUQNj8TT\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"1\" targetRef=\"2\" id=\"twyDpKyC\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"2\" targetRef=\"3\" id=\"6mcAMmeP\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"3\" targetRef=\"end_id\" id=\"OyZ9Koou\" name=\"\"/>")
                .append("</process>")
                .append("</definitions>").toString();

        workflow1 = new WorkflowEntity(0L, "workflow1", "rev1", diaglam1, null, df.parse("2015/11/18 00:00:00"), null);
        ConWorkflowWorkEntity conwork11 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work1.getWorkId(), false, 1, df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:30:00"));
        conwork11.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId()));
        conwork11.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId()));
        ConWorkflowWorkEntity conwork12 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work2.getWorkId(), true, 2, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"));
        conwork12.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId()));
        conwork12.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId()));
        ConWorkflowWorkEntity conwork13 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work3.getWorkId(), false, 3, df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 10:30:00"));
        conwork13.setEquipmentCollection(Arrays.asList(equip3.getEquipmentId()));
        conwork13.setOrganizationCollection(Arrays.asList(organization3.getOrganizationId()));
        workflow1.setConWorkflowWorkCollection(Arrays.asList(conwork11, conwork12, conwork13));

        workflow2 = new WorkflowEntity(0L, "workflow2", "rev1", null, null, null, null);
        ConWorkflowWorkEntity conwork21 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work4.getWorkId(), false, 1, df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:30:00"));
        conwork21.setEquipmentCollection(Arrays.asList(equip2.getEquipmentId()));
        conwork21.setOrganizationCollection(Arrays.asList(organization2.getOrganizationId()));
        ConWorkflowWorkEntity conwork22 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work5.getWorkId(), true, 2, df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:30:00"));
        conwork22.setEquipmentCollection(Arrays.asList(equip2.getEquipmentId()));
        conwork22.setOrganizationCollection(Arrays.asList(organization2.getOrganizationId()));
        ConWorkflowWorkEntity conwork23 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work6.getWorkId(), false, 3, df.parse("2015/11/18 10:00:00"), df.parse("2015/11/18 10:30:00"));
        conwork23.setEquipmentCollection(Arrays.asList(equip3.getEquipmentId()));
        conwork23.setOrganizationCollection(Arrays.asList(organization3.getOrganizationId()));
        workflow2.setConWorkflowWorkCollection(Arrays.asList(conwork21, conwork22, conwork23));

        tx.begin();
        workflowRest.add(workflow1, null);
        workflowRest.add(workflow2, null);
        tx.commit();

        workflows.clear();
        workflows.add(workflow1.getWorkflowId());
        workflows.add(workflow2.getWorkflowId());

        kanban1 = new KanbanEntity(0L, "kanban1", "sub1", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:10:00"), 200L, null, KanbanStatusEnum.PLANNED, null, null);
        kanban2 = new KanbanEntity(0L, "kanban2", "sub2", workflow2.getWorkflowId(), "workflow2", df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:10:00"), 200L, null, KanbanStatusEnum.PLANNED, null, null);

        tx.begin();
        kanbanRest.add(kanban1, null);
        kanbanRest.add(kanban2, null);
        tx.commit();
    }

    @Test
    public void testSearch() throws Exception {
        System.out.println("testSearch");

        createData();

        Map<String, WorkKanbanEntity> works = new HashMap<>();
        for (WorkKanbanEntity w : workKanbanRest.findRange(null, null, null)) {
            works.put(w.getWorkName(), w);
        }

        List<WorkKanbanEntity> workKansans;
        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition(), null, null, null);
        assertThat(workKansans, is(hasSize(0)));// カンバンID, カンバン名, 日時範囲が全て指定なしの場合は検索されない。

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban"), null, null, null);
        assertThat(workKansans, is(hasSize(6)));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").workflowId(workflow1.getWorkflowId()), null, null, null);
        assertThat(workKansans, is(hasSize(3)));
        assertThat(workKansans, is(hasItems(works.get("work1"), works.get("work2"), works.get("work3"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").workflowIdList(workflows), null, null, null);
        assertThat(workKansans, is(hasSize(6)));
        assertThat(workKansans, is(hasItems(works.get("work1"), works.get("work2"), works.get("work3"), works.get("work4"), works.get("work5"), works.get("work6"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").skipFlag(true), null, null, null);
        assertThat(workKansans, is(hasSize(2)));
        assertThat(workKansans, is(hasItems(works.get("work2"), works.get("work5"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").equipmentList(Arrays.asList(equip1.getEquipmentId())).equipmentIdWithParent(Boolean.TRUE), null, null, null);
        assertThat(workKansans, is(hasSize(2)));
        assertThat(workKansans, is(hasItems(works.get("work1"), works.get("work2"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").organizationList(Arrays.asList(organization2.getOrganizationId())).organizationIdWithParent(Boolean.TRUE), null, null, null);
        assertThat(workKansans, is(hasSize(2)));
        assertThat(workKansans, is(hasItems(works.get("work4"), works.get("work5"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").skipFlag(true), null, null, null);
        assertThat(workKansans, is(hasSize(2)));
        assertThat(workKansans, is(hasItems(works.get("work2"), works.get("work5"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban").skipFlag(true).equipmentList(Arrays.asList(equip1.getEquipmentId())), null, null, null);
        assertThat(workKansans, is(hasSize(1)));
        assertThat(workKansans, is(hasItems(works.get("work2"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanName("kanban1"), null, null, null);
        assertThat(workKansans, is(hasSize(3)));
        assertThat(workKansans, is(hasItems(works.get("work1"), works.get("work2"), works.get("work3"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().kanbanId(kanban2.getKanbanId()), null, null, null);
        assertThat(workKansans, is(hasSize(3)));
        assertThat(workKansans, is(hasItems(works.get("work4"), works.get("work5"), works.get("work6"))));

        workKansans = workKanbanRest.searchWorkKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 09:30:00")).toDate(df.parse("2015/11/18 10:00:00")), null, null, null);
        assertThat(workKansans, is(hasSize(4)));
        assertThat(workKansans, is(hasItems(works.get("work2"), works.get("work3"), works.get("work5"), works.get("work6"))));
    }
}
