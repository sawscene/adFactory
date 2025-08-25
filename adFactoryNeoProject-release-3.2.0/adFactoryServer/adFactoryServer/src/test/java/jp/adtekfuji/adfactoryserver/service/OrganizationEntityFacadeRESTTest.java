/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.PasswordEncoder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginRequest;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.WorkCategoryEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class OrganizationEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static EquipmentEntityFacadeREST equipmentRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static KanbanEntityFacadeREST kanbanRest = null;
    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static RoleEntityFacadeREST roleRest = null;
    private static WorkCategoryEntityFacedeREST workCategoryRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static KanbanHierarchyEntityFacadeREST kanbanHierarchyRest = null;
    private static IndirectWorkEntityFacadeREST indirectWorkRest = null;

    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public OrganizationEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        roleRest = new RoleEntityFacadeREST();
        roleRest.setEntityManager(em);

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        equipmentRest = new EquipmentEntityFacadeREST();
        equipmentRest.setEntityManager(em);
        equipmentRest.setAuthRest(authRest);
        equipmentRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        indirectWorkRest = new IndirectWorkEntityFacadeREST();
        indirectWorkRest.setEntityManager(em);
        
        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setAuthRest(authRest);
        organizationRest.setEquipmentRest(equipmentRest);
        organizationRest.setRoleRest(roleRest);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());
        organizationRest.setIindirectWorkRest(indirectWorkRest);

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

        workflowRest.setKanbanEntityFacadeREST(kanbanRest);

        kanbanHierarchyRest = new KanbanHierarchyEntityFacadeREST();
        kanbanHierarchyRest.setEntityManager(em);
        kanbanHierarchyRest.setAuthRest(authRest);
        kanbanHierarchyRest.setKanbanFacadeRest(kanbanRest);
        kanbanHierarchyRest.setWorkflowFacadeRest(workflowRest);

        workCategoryRest = new WorkCategoryEntityFacedeREST();
        workCategoryRest.setEntityManager(em);

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
    }

    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    @Test
    public void testTree() throws Exception {
        System.out.println("testTree");

        Long loginUserId = null;

        Response response;
        ResponseEntity responseEntity;
        Long updatePersonId = 678L;
        Date updateDateTime = new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime();

        // 親組織を追加する。
        Map<String, OrganizationEntity> expected = new HashMap();
        OrganizationEntity team1 = new OrganizationEntity(0L, "nameTeam1", "identTeam1", AuthorityEnum.SYSTEM_ADMIN, "", "password", "mail", updatePersonId, updateDateTime);
        OrganizationEntity team2 = new OrganizationEntity(0L, "nameTeam2", "identTeam2", AuthorityEnum.ADMINISTRATOR, "", "password", "mail", updatePersonId, updateDateTime);
        OrganizationEntity team3 = new OrganizationEntity(0L, "nameTeam3", "identTeam3", AuthorityEnum.WORKER, "", "password", "mail", updatePersonId, updateDateTime);

        tx.begin();
        organizationRest.add(team1, null);
        organizationRest.add(team2, null);
        organizationRest.add(team3, null);
        tx.commit();

        expected.put("identTeam1", team1);
        expected.put("identTeam2", team2);
        expected.put("identTeam3", team3);

        // 組織一覧を取得する。
        List<OrganizationEntity> teams;
        teams = organizationRest.findTreeRange(null, loginUserId, null, null, null);
        assertThat(teams.size(), is(3));

        int personNo = 0;
        for (OrganizationEntity line : teams) {
            assertThat(line, is(expected.get(line.getOrganizationIdentify())));

            // 子組織を追加する。
            personNo++;
            String name = new StringBuilder("namePerson").append(personNo).toString();
            String ident = new StringBuilder("identPerson").append(personNo).toString();
            OrganizationEntity person1 = new OrganizationEntity(line.getOrganizationId(), name, ident, AuthorityEnum.WORKER, "", "password", "mail", updatePersonId, updateDateTime);

            tx.begin();
            organizationRest.add(person1, null);
            tx.commit();

            expected.put(ident, person1);

            // 子組織を取得する。
            List<OrganizationEntity> persons = organizationRest.findTreeRange(line.getOrganizationId(), loginUserId, null, null, null);
            assertThat(persons.size(), is(1));
            for (OrganizationEntity person : persons) {
                assertThat(person, is(expected.get(person.getOrganizationIdentify())));
            }
        }

        // 親組織の組織識別名を変更する。
        tx.begin();
        team1 = organizationRest.findByName("identTeam1", loginUserId, null);
        team1.setOrganizationIdentify("identTeam11");
        expected.remove(team1, null);
        expected.put("identTeam11", team1);

        organizationRest.update(team1, null);
        tx.commit();

        team1 = organizationRest.findByName("identTeam11", loginUserId, null);
        assertThat(team1.getOrganizationIdentify(), is("identTeam11"));

        // 子組織の組織識別名を変更する。
        OrganizationEntity person1 = organizationRest.findByName("identPerson1", loginUserId, null);
        person1.setOrganizationIdentify("identPerson11");
        expected.remove(person1, null);
        expected.put("identPerson11", person1);

        tx.begin();
        organizationRest.update(person1, null);
        tx.commit();

        person1 = organizationRest.findByName("identPerson11", loginUserId, null);
        assertThat(person1.getOrganizationIdentify(), is("identPerson11"));

        // 組織一覧を取得する。
        teams = organizationRest.findTreeRange(null, loginUserId, null, null, null);
        assertThat(teams.size(), is(3));

        for (OrganizationEntity line : teams) {
            assertThat(line, is(expected.get(line.getOrganizationIdentify())));
 
            // 子組織を取得する。
            List<OrganizationEntity> persons = organizationRest.findTreeRange(line.getOrganizationId(), loginUserId, null, null, null);
            assertThat(persons.size(), is(1));
            for (OrganizationEntity person : persons) {
                assertThat(person, is(expected.get(person.getOrganizationIdentify())));
            }
        }

        // 子組織をコピーする。
        OrganizationEntity person3 = expected.get("identPerson3");

        tx.begin();
        organizationRest.copy(person3.getOrganizationId(), null);
        tx.commit();

        OrganizationEntity copy = organizationRest.findByName("identPerson3-copy", loginUserId, null);
        assertThat(copy.getOrganizationIdentify(), is("identPerson3-copy"));
        assertThat(copy.getParentOrganizationId(), is(person3.getParentOrganizationId()));

        // 子組織を削除する。
        tx.begin();
        organizationRest.remove(copy.getOrganizationId(), null);
        tx.commit();

        copy = organizationRest.findByName("identPerson3-copy", loginUserId, null);
        assertThat(copy.getOrganizationId(), nullValue());

        // 子組織を持つ親組織を削除する。
        tx.begin();
        response = organizationRest.remove(team3.getOrganizationId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE));

        // 子組織を削除する。
        tx.begin();
        organizationRest.remove(person3.getOrganizationId(), null);
        tx.commit();

        copy = organizationRest.findByName("identPerson3", loginUserId, null);
        assertThat(copy.getOrganizationId(), nullValue());

        // 子組織を持たない親組織を削除する。
        tx.begin();
        organizationRest.remove(team3.getOrganizationId(), null);
        tx.commit();

        copy = organizationRest.findByName("identTeam3", loginUserId, null);
        assertThat(copy.getOrganizationId(), nullValue());
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        OrganizationEntity parson1 = new OrganizationEntity(0L, "名前", "識別名", AuthorityEnum.WORKER, "", "password", "mail", 0L, null);

        tx.begin();
        response = organizationRest.add(parson1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        OrganizationEntity parson2 = new OrganizationEntity(0L, "名前", "識別名", AuthorityEnum.WORKER, "", "password", "mail", 0L, null);

        tx.begin();
        response = organizationRest.add(parson2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        tx.begin();
        response = organizationRest.remove(parson1.getOrganizationId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

    @Test
    public void testProperty() throws Exception {
        System.out.println("testProperty");

        Date updateDateTime = new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime();

        OrganizationEntity user1 = new OrganizationEntity(0L, "nameUser1", "identUser1", AuthorityEnum.WORKER, "", "password", "mail", 0L, updateDateTime);

        tx.begin();
        organizationRest.add(user1, null);
        tx.commit();

        OrganizationEntity result = organizationRest.find(user1.getOrganizationId(), null);
        System.out.println(result);

        assertThat(result.getOrganizationIdentify(), is(user1.getOrganizationIdentify()));
        assertThat(result.getOrganizationName(), is(user1.getOrganizationName()));
        assertThat(result.getUpdatePersonId(), is(user1.getUpdatePersonId()));
        assertThat(result.getUpdateDatetime(), is(user1.getUpdateDatetime()));

        user1.setOrganizationName("nameUser1-1");

        tx.begin();
        organizationRest.update(user1, null);
        tx.commit();

        result = organizationRest.find(user1.getOrganizationId(), null);
        System.out.println(result);

        assertThat(result.getOrganizationIdentify(), is(user1.getOrganizationIdentify()));
        assertThat(result.getOrganizationName(), is(user1.getOrganizationName()));
        assertThat(result.getUpdatePersonId(), is(user1.getUpdatePersonId()));
        assertThat(result.getUpdateDatetime(), is(user1.getUpdateDatetime()));
    }

    @Test
    public void testLogin() throws Exception {
        System.out.println("testLogin");
        PasswordEncoder encoder = new PasswordEncoder();

        //組織登録なし.
        tx.begin();
        OrganizationLoginResult result = organizationRest.login(OrganizationLoginRequest.passwordType("identUser2", encoder.encode("password")), false, null);
        tx.commit();
        assertThat(result.getIsSuccess(), is(false));
        assertThat(result.getErrorType(), is(ServerErrorTypeEnum.NOT_LOGINID_ORGANIZATION));

        //組織登録.
        OrganizationEntity user2 = new OrganizationEntity(0L, "nameUser2", "identUser2", AuthorityEnum.WORKER, "", encoder.encode("password"), "mail", null, null);
        tx.begin();
        organizationRest.add(user2, null);
        tx.commit();
        tx.begin();
        result = organizationRest.login(OrganizationLoginRequest.passwordType("identUser2", encoder.encode("xxxxxxxxx")), false, null);
        tx.commit();
        assertThat(result.getIsSuccess(), is(false));
        assertThat(result.getErrorType(), is(ServerErrorTypeEnum.NOT_AUTH_ORGANIZATION));
        tx.begin();
        result = organizationRest.login(OrganizationLoginRequest.passwordType("identUser2", encoder.encode("password")), false, null);
        tx.commit();
        assertThat(result.getIsSuccess(), is(true));
        assertThat(result.getOrganizationId(), is(not(0)));
    }

    @Test
    public void testFindAtWorkKanbanByEquipment() throws Exception {
        System.out.println("testFindAtWorkKanbanByEquipment");

        Long loginUserId = null;

        Date updateDt = df.parse("2019/10/01 08:00:00");

        List<OrganizationEntity> organizations = organizationRest.findByEquipmentId(5L, loginUserId, null, null, null);
        assertThat(organizations, is(empty()));

        // 親設備
        EquipmentEntity equip0 = new EquipmentEntity(0L, "nameEquip0", "identEquip0", null, 0L, updateDt);

        tx.begin();
        equipmentRest.add(equip0, null);
        tx.commit();

        // 設備
        EquipmentEntity equip1 = new EquipmentEntity(equip0.getEquipmentId(), "nameEquip1", "identEquip1", null, 0L, updateDt);
        EquipmentEntity equip2 = new EquipmentEntity(equip0.getEquipmentId(), "nameEquip2", "identEquip2", null, 0L, updateDt);

        tx.begin();
        equipmentRest.add(equip1, null);
        equipmentRest.add(equip2, null);
        tx.commit();

        // 親組織
        OrganizationEntity organization0 = new OrganizationEntity(0L, "nameParson0", "identPerson0", AuthorityEnum.WORKER, null, null, null, 0L, updateDt);

        tx.begin();
        organizationRest.add(organization0, null);
        tx.commit();

        // 組織
        OrganizationEntity organization1 = new OrganizationEntity(organization0.getOrganizationId(), "nameParson1", "identPerson1", AuthorityEnum.WORKER, null, null, null, 0L, updateDt);
        OrganizationEntity organization2 = new OrganizationEntity(organization0.getOrganizationId(), "nameParson2", "identPerson2", AuthorityEnum.WORKER, null, null, null, 0L, updateDt);

        tx.begin();
        organizationRest.add(organization1, null);
        organizationRest.add(organization2, null);
        tx.commit();

        // 工程階層
        HierarchyEntity workTree1 = new HierarchyEntity(HierarchyTypeEnum.WORK, 0L, "workTree1");

        tx.begin();
        hierarchyRest.add(workTree1, null);
        tx.commit();

        // 工程
        WorkEntity work1 = new WorkEntity(workTree1.getHierarchyId(), "work1", 1, 0, "work1", ContentTypeEnum.STRING, 0L, updateDt, null, null);
        WorkEntity work2 = new WorkEntity(workTree1.getHierarchyId(), "work2", 1, 0, "work2", ContentTypeEnum.STRING, 0L, updateDt, null, null);
        WorkEntity work3 = new WorkEntity(workTree1.getHierarchyId(), "work3", 1, 0, "work3", ContentTypeEnum.STRING, 0L, updateDt, null, null);
        WorkEntity work4 = new WorkEntity(workTree1.getHierarchyId(), "work4", 1, 0, "work4", ContentTypeEnum.STRING, 0L, updateDt, null, null);

        tx.begin();
        workRest.add(work1, null);
        workRest.add(work2, null);
        workRest.add(work3, null);
        workRest.add(work4, null);
        tx.commit();

        // 工程順階層
        HierarchyEntity workflowTree1 = new HierarchyEntity(HierarchyTypeEnum.WORKFLOW, 0L, "workflowTree1");

        tx.begin();
        hierarchyRest.add(workflowTree1, null);
        tx.commit();

        // 工程順
        String diaglam1 = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
                .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
                .append("<process isExecutable=\"true\">")
                .append("<startEvent id=\"start_id\" name=\"start\"/>")
                .append("<endEvent id=\"end_id\" name=\"end\"/>")
                .append("<task id=\"1\" name=\"work1\"/>")
                .append("<task id=\"2\" name=\"work2\"/>")
                .append("<task id=\"3\" name=\"work3\"/>")
                .append("<task id=\"4\" name=\"work4\"/>")
                .append("<sequenceFlow sourceRef=\"start_id\" targetRef=\"1\" id=\"start_id\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"1\" targetRef=\"2\" id=\"1\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"2\" targetRef=\"3\" id=\"2\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"3\" targetRef=\"4\" id=\"3\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"4\" targetRef=\"end_id\" id=\"4\" name=\"\"/>")
                .append("</process>")
                .append("</definitions>")
                .toString();

        WorkflowEntity workflow1 = new WorkflowEntity(workflowTree1.getHierarchyId(), "workflow1", "rev1", diaglam1, 0L, updateDt, null);

        ConWorkflowWorkEntity conWork1 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work1.getWorkId(), false, 10001, null, null);
        ConWorkflowWorkEntity conWork2 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work2.getWorkId(), false, 20001, null, null);
        ConWorkflowWorkEntity conWork3 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work3.getWorkId(), false, 30001, null, null);
        ConWorkflowWorkEntity conWork4 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work4.getWorkId(), false, 40001, null, null);

        workflow1.setConWorkflowWorkCollection(Arrays.asList(conWork1, conWork2, conWork3, conWork4));

        tx.begin();
        workflowRest.add(workflow1, null);
        tx.commit();

        KanbanHierarchyEntity kanbanTree1 = new KanbanHierarchyEntity(0L, "kanbanTree1");

        tx.begin();
        kanbanHierarchyRest.add(kanbanTree1, null);
        tx.commit();

        List<KanbanEntity> kanbans = new LinkedList();
        KanbanEntity kanban1 = new KanbanEntity(kanbanTree1.getKanbanHierarchyId(), "kanban1", null, workflow1.getWorkflowId(), workflow1.getWorkflowName(), null, null, 0L, updateDt, null, null, null);
        KanbanEntity kanban2 = new KanbanEntity(kanbanTree1.getKanbanHierarchyId(), "kanban2", null, workflow1.getWorkflowId(), workflow1.getWorkflowName(), null, null, 0L, updateDt, null, null, null);
        KanbanEntity kanban3 = new KanbanEntity(kanbanTree1.getKanbanHierarchyId(), "kanban3", null, workflow1.getWorkflowId(), workflow1.getWorkflowName(), null, null, 0L, updateDt, null, null, null);
        KanbanEntity kanban4 = new KanbanEntity(kanbanTree1.getKanbanHierarchyId(), "kanban4", null, workflow1.getWorkflowId(), workflow1.getWorkflowName(), null, null, 0L, updateDt, null, null, null);

        kanbans.add(kanban1);
        kanbans.add(kanban2);
        kanbans.add(kanban3);
        kanbans.add(kanban4);

        for (KanbanEntity kanban : kanbans) {
            tx.begin();
            kanbanRest.add(kanban, null);
            tx.commit();

            List<WorkKanbanEntity> workKanbans = workKanbanRest.findByKanbanId(kanban.getKanbanId(), null, null, null);

            WorkKanbanEntity workKanban1 = workKanbans.stream().filter(p -> work1.getWorkId().equals(p.getWorkId())).findFirst().get();
            workKanban1.setWorkStatus(KanbanStatusEnum.PLANNING);
            workKanban1.setImplementFlag(true);
            workKanban1.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId()));
            workKanban1.setOrganizationCollection(Arrays.asList(organization0.getOrganizationId()));

            WorkKanbanEntity workKanban2 = workKanbans.stream().filter(p -> work2.getWorkId().equals(p.getWorkId())).findFirst().get();
            workKanban2.setWorkStatus(KanbanStatusEnum.PLANNED);
            workKanban2.setImplementFlag(true);
            workKanban2.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId()));
            workKanban2.setOrganizationCollection(Arrays.asList(organization0.getOrganizationId()));

            WorkKanbanEntity workKanban3 = workKanbans.stream().filter(p -> work3.getWorkId().equals(p.getWorkId())).findFirst().get();
            workKanban3.setWorkStatus(KanbanStatusEnum.SUSPEND);
            workKanban3.setImplementFlag(true);
            workKanban3.setEquipmentCollection(Arrays.asList(equip2.getEquipmentId()));
            workKanban3.setOrganizationCollection(Arrays.asList(organization0.getOrganizationId()));

            WorkKanbanEntity workKanban4 = workKanbans.stream().filter(p -> work4.getWorkId().equals(p.getWorkId())).findFirst().get();
            workKanban4.setWorkStatus(KanbanStatusEnum.WORKING);
            workKanban4.setImplementFlag(true);
            workKanban4.setEquipmentCollection(Arrays.asList(equip2.getEquipmentId()));
            workKanban4.setOrganizationCollection(Arrays.asList(organization0.getOrganizationId()));

            tx.begin();
            workKanbanRest.update(workKanban1, null);
            workKanbanRest.update(workKanban2, null);
            workKanbanRest.update(workKanban3, null);
            workKanbanRest.update(workKanban4, null);
            tx.commit();
        }

        // カンバンステータスを変更する。
        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);
        kanban1.setKanbanStatus(KanbanStatusEnum.PLANNING);

        kanban2 = kanbanRest.find(kanban1.getKanbanId(), null);
        kanban2.setKanbanStatus(KanbanStatusEnum.PLANNED);

        kanban3 = kanbanRest.find(kanban1.getKanbanId(), null);
        kanban3.setKanbanStatus(KanbanStatusEnum.SUSPEND);

        kanban4 = kanbanRest.find(kanban1.getKanbanId(), null);
        kanban4.setKanbanStatus(KanbanStatusEnum.WORKING);

        tx.begin();
        kanbanRest.update(kanban1, null);
        kanbanRest.update(kanban2, null);
        kanbanRest.update(kanban3, null);
        kanbanRest.update(kanban4, null);
        tx.commit();

        // 組織識別名順
        List<OrganizationEntity> orgs = Arrays.asList(organization0, organization1, organization2);
        orgs.sort(Comparator.comparing(p -> p.getOrganizationIdentify()));

        organizations = organizationRest.findByEquipmentId(equip0.getEquipmentId(), loginUserId, null, null, null);
        assertThat(organizations, is(hasSize(3)));
        assertThat(organizations.get(0), is(orgs.get(0)));
        assertThat(organizations.get(1), is(orgs.get(1)));
        assertThat(organizations.get(2), is(orgs.get(2)));

        String count = organizationRest.countByEquipmentId(equip0.getEquipmentId(), null);
        assertThat(Long.parseLong(count), is(3L));

        organizations = organizationRest.findByEquipmentId(equip0.getEquipmentId(), loginUserId, 0, 0, null);
        assertThat(organizations, is(hasSize(1)));
        assertThat(organizations.get(0), is(orgs.get(0)));

        organizations = organizationRest.findByEquipmentId(equip0.getEquipmentId(), loginUserId, 1, 2, null);
        assertThat(organizations, is(hasSize(2)));
        assertThat(organizations.get(0), is(orgs.get(1)));
        assertThat(organizations.get(1), is(orgs.get(2)));
    }

    @Test
    public void testGetOrganizationPerpetuity() throws Exception {
        System.out.println("testGetOrganizationsPerpetuity");

        OrganizationEntity top = new OrganizationEntity(0L, "top", "top", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        organizationRest.add(top, null);
        tx.commit();

        OrganizationEntity team1 = new OrganizationEntity(top.getOrganizationId(), "team1", "team1", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity team2 = new OrganizationEntity(top.getOrganizationId(), "team2", "team2", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity team3 = new OrganizationEntity(top.getOrganizationId(), "team3", "team3", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        organizationRest.add(team1, null);
        organizationRest.add(team2, null);
        organizationRest.add(team3, null);
        tx.commit();

        OrganizationEntity parson1 = new OrganizationEntity(team1.getOrganizationId(), "parson1", "parson1", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson2 = new OrganizationEntity(team1.getOrganizationId(), "parson2", "parson2", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson3 = new OrganizationEntity(team1.getOrganizationId(), "parson3", "parson3", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson4 = new OrganizationEntity(team2.getOrganizationId(), "parson4", "parson4", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson5 = new OrganizationEntity(team2.getOrganizationId(), "parson5", "parson5", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson6 = new OrganizationEntity(team2.getOrganizationId(), "parson6", "parson6", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson7 = new OrganizationEntity(team3.getOrganizationId(), "parson7", "parson7", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson8 = new OrganizationEntity(team3.getOrganizationId(), "parson8", "parson8", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity parson9 = new OrganizationEntity(team3.getOrganizationId(), "parson9", "parson9", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        organizationRest.add(parson1, null);
        organizationRest.add(parson2, null);
        organizationRest.add(parson3, null);
        organizationRest.add(parson4, null);
        organizationRest.add(parson5, null);
        organizationRest.add(parson6, null);
        organizationRest.add(parson7, null);
        organizationRest.add(parson8, null);
        organizationRest.add(parson9, null);
        tx.commit();

        List<Long> list;
        list = organizationRest.getOrganizationPerpetuity(top.getOrganizationId());
        assertThat(list, is(hasSize(13)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team1.getOrganizationId(), team2.getOrganizationId(), team3.getOrganizationId(),
                parson1.getOrganizationId(), parson2.getOrganizationId(), parson3.getOrganizationId(), parson4.getOrganizationId(), parson5.getOrganizationId(),
                parson6.getOrganizationId(), parson7.getOrganizationId(), parson8.getOrganizationId(), parson9.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(team1.getOrganizationId());
        assertThat(list, is(hasSize(5)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team1.getOrganizationId(), parson1.getOrganizationId(), parson2.getOrganizationId(), parson3.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(team2.getOrganizationId());
        assertThat(list, is(hasSize(5)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team2.getOrganizationId(), parson4.getOrganizationId(), parson5.getOrganizationId(), parson6.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(team3.getOrganizationId());
        assertThat(list, is(hasSize(5)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team3.getOrganizationId(), parson7.getOrganizationId(), parson8.getOrganizationId(), parson9.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson1.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team1.getOrganizationId(), parson1.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson2.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team1.getOrganizationId(), parson2.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson3.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team1.getOrganizationId(), parson3.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson4.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team2.getOrganizationId(), parson4.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson5.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team2.getOrganizationId(), parson5.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson6.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team2.getOrganizationId(), parson6.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson7.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team3.getOrganizationId(), parson7.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson8.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team3.getOrganizationId(), parson8.getOrganizationId())));

        list = organizationRest.getOrganizationPerpetuity(parson9.getOrganizationId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(top.getOrganizationId(), team3.getOrganizationId(), parson9.getOrganizationId())));
    }

    /**
     * 作業区分関連付けのテスト
     *
     * @throws Exception 
     */
    @Test
    public void testOrganizationWorkCategory() throws Exception {
        System.out.println("testOrganizationWorkCategory");

        Long loginUserId = null;

        // 追加
        WorkCategoryEntity category1 = new WorkCategoryEntity("category1");
        WorkCategoryEntity category2 = new WorkCategoryEntity("category2");
        WorkCategoryEntity category3 = new WorkCategoryEntity("category3");

        tx.begin();
        workCategoryRest.add(category1, null);
        workCategoryRest.add(category2, null);
        workCategoryRest.add(category3, null);
        tx.commit();

        OrganizationEntity team1 = new OrganizationEntity(0L, "team1", "team1", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity team2 = new OrganizationEntity(0L, "team2", "team2", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity team3 = new OrganizationEntity(0L, "team3", "team3", AuthorityEnum.WORKER, null, null, null, null, null);

        List<Long> team1categories = Arrays.asList(category1.getWorkCategoryId());
        team1.setWorkCategoryCollection(team1categories);

        List<Long> team3categories = Arrays.asList(category2.getWorkCategoryId(), category3.getWorkCategoryId());
        team3.setWorkCategoryCollection(team3categories);

        tx.begin();
        organizationRest.add(team1, null);
        organizationRest.add(team2, null);
        organizationRest.add(team3, null);
        tx.commit();

        OrganizationEntity team = organizationRest.findByName("team1", loginUserId, null);
        assertThat(team.getWorkCategoryCollection(), is(hasSize(team1categories.size())));
        assertThat(team.getWorkCategoryCollection(), is(hasItems(category1.getWorkCategoryId())));

        team = organizationRest.findByName("team2", loginUserId, null);
        assertThat(team.getWorkCategoryCollection(), is(hasSize(0)));

        team = organizationRest.findByName("team3", loginUserId, null);
        assertThat(team.getWorkCategoryCollection(), is(hasSize(team3categories.size())));
        assertThat(team.getWorkCategoryCollection(), is(hasItems(category2.getWorkCategoryId(), category3.getWorkCategoryId())));

        // 更新
        List<Long> categories = Arrays.asList(category2.getWorkCategoryId());
        team.setWorkCategoryCollection(categories);

        tx.begin();
        organizationRest.update(team, null);
        tx.commit();

        team = organizationRest.findByName("team3", loginUserId, null);
        assertThat(team.getWorkCategoryCollection(), is(hasSize(categories.size())));
        assertThat(team.getWorkCategoryCollection(), is(hasItems(category2.getWorkCategoryId())));

        team.getWorkCategoryCollection().clear();

        tx.begin();
        organizationRest.update(team, null);
        tx.commit();

        team = organizationRest.findByName("team3", loginUserId, null);
        assertThat(team.getWorkCategoryCollection(), is(hasSize(0)));
    }
}
