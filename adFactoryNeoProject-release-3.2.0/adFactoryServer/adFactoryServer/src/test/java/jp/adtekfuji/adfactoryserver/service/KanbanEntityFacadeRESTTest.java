/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.DefectInfoEntity;
import jp.adtekfuji.adFactory.entity.actual.DefectReasonEntity;
import jp.adtekfuji.adFactory.entity.actual.DefectSerialEntity;
import jp.adtekfuji.adFactory.entity.dsKanban.DsActual;
import jp.adtekfuji.adFactory.entity.dsKanban.DsKanban;
import jp.adtekfuji.adFactory.entity.dsKanban.DsKanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.dsKanban.DsKanbanProperty;
import jp.adtekfuji.adFactory.entity.dsKanban.DsPickup;
import jp.adtekfuji.adFactory.entity.job.OrderInfoEntity;
import jp.adtekfuji.adFactory.entity.job.WorkComment;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportPropertyEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.kanban.ApprovalEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanCreateCondition;
import jp.adtekfuji.adFactory.entity.kanban.PartsInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PlanChangeCondition;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.entity.search.KanbanSearchCondition;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.dsKanban.MstDsItem;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanPropertyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.PartsEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ProductEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.TestUtils;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.time.DateUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class KanbanEntityFacadeRESTTest {

    private static ServiceTestData serviceTestData = null;

    private static EntityManager em = null;
    private static EntityTransaction tx = null;

    private static KanbanEntityFacadeREST kanbanRest = null;
    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static EquipmentEntityFacadeREST equipmentRest = null;
    private static KanbanHierarchyEntityFacadeREST kanbanHierarchyRest = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static PartsEntityFacadeREST partsRest = null;
    private static ActualResultEntityFacadeREST actualResultRest = null;
    private static KanbanReportEntityFacadeREST kanbanReportRest = null;
    private static DirectActualEntityFacadeREST directActualEntityFacadeREST = null;
    private static DsItemFacade dsItemFacade = null;

    private final List<Long> workflows = new ArrayList<>();
    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private KanbanEntity kanban1;
    private KanbanEntity kanban2;
    private KanbanEntity kanban3;
    private KanbanEntity kanban4;
    private KanbanEntity kanban5;
    private KanbanEntity kanban6;
    private KanbanEntity kanban7;
    private KanbanEntity kanban8;
    private KanbanEntity kanban9;
    private KanbanEntity kanban10;
    private KanbanEntity kanban11;
    private KanbanEntity kanban12;
    private KanbanEntity kanban13;
    private WorkflowEntity workflow1;
    private WorkflowEntity workflow2;
    private EquipmentEntity equip1;
    private EquipmentEntity equip2;
    private EquipmentEntity equip3;
    private OrganizationEntity organization1;
    private OrganizationEntity organization2;
    private OrganizationEntity organization3;

    public KanbanEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        serviceTestData = new ServiceTestData();
        ServiceTestData.setUpClass();

        em = ServiceTestData.getEntityManager();

        equipmentRest = ServiceTestData.getEquipmentRest();
        organizationRest = ServiceTestData.getOrganizationRest();
        workRest = ServiceTestData.getWorkREST();
        workflowRest = ServiceTestData.getWorkflowRest();
        kanbanHierarchyRest = ServiceTestData.getKanbanHierarchyRest();
        kanbanRest = ServiceTestData.getKanbanRest();
        workKanbanRest = ServiceTestData.getWorkKanbanRest();
        hierarchyRest = ServiceTestData.getHierarchyRest();
        partsRest = ServiceTestData.getPartsRest();
        actualResultRest = ServiceTestData.getActualResultRest();
        kanbanReportRest = ServiceTestData.getKanbanReportRest();
        directActualEntityFacadeREST = ServiceTestData.getDirectActualEntityFacadeREST();
        dsItemFacade = ServiceTestData.getDsItemFacade();

        LicenseManager.setupTest();

        // 承認機能ライセンス無効
        TestUtils.setOptionLicense(LicenseOptionType.ApprovalOption, false);
    }

    @AfterClass
    public static void tearDownClass() {
        ServiceTestData.tearDownClass();
    }

    @Before
    public void setUp() {
        if (Objects.nonNull(serviceTestData)) {
            serviceTestData.setUp();
        }
        tx = ServiceTestData.getTransaction();
    }

    @After
    public void tearDown() {
        if (Objects.nonNull(serviceTestData)) {
            serviceTestData.tearDown();
        }
    }

    /**
     * テスト用のデータを作成する。
     *
     * @throws URISyntaxException
     * @throws ParseException
     */
    private void createData() throws URISyntaxException, ParseException {
        // 親設備・組織
        EquipmentEntity equip0 = new EquipmentEntity(0L, "equip0", "identname0", null, null, null);
        OrganizationEntity organization0 = new OrganizationEntity(0L, "parson0", "identname0", AuthorityEnum.WORKER, null, null, null, null, null);
        tx.begin();
        equipmentRest.add(equip0, null);
        organizationRest.add(organization0, null);
        tx.commit();

        // 設備・組織
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

        // 工程階層
        HierarchyEntity workTree1 = new HierarchyEntity(HierarchyTypeEnum.WORK , 0L, "工程階層①");
        tx.begin();
        hierarchyRest.add(workTree1, null);
        tx.commit();

        // 工程
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

        work1 = workRest.findByName(work1.getWorkName(), 1, null, null, null);
        work2 = workRest.findByName(work2.getWorkName(), 1, null, null, null);
        work3 = workRest.findByName(work3.getWorkName(), 1, null, null, null);
        work4 = workRest.findByName(work4.getWorkName(), 1, null, null, null);
        work5 = workRest.findByName(work5.getWorkName(), 1, null, null, null);
        work6 = workRest.findByName(work6.getWorkName(), 1, null, null, null);

        // 工程順階層
        HierarchyEntity workflowTree1 = new HierarchyEntity(HierarchyTypeEnum.WORKFLOW , 0L, "工程順階層①");
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
                .append("<task id=\"id1\" name=\"work1\"/>")
                .append("<task id=\"id2\" name=\"work2\"/>")
                .append("<task id=\"id3\" name=\"work3\"/>")
                .append("<task id=\"id4\" name=\"work4\"/>")
                .append("<task id=\"id5\" name=\"work5\"/>")
                .append("<task id=\"id6\" name=\"work6\"/>")
                .append("<sequenceFlow sourceRef=\"start_id\" targetRef=\"id1\" id=\"kUQNj8TT\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"id1\" targetRef=\"id2\" id=\"twyDpKyC\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"id2\" targetRef=\"id3\" id=\"6mcAMmeP\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"id3\" targetRef=\"id4\" id=\"D5tPPfXE\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"id4\" targetRef=\"id5\" id=\"k6s367y1\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"id5\" targetRef=\"id6\" id=\"kKU3bDR3\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"id6\" targetRef=\"end_id\" id=\"OyZ9Koou\" name=\"\"/>")
                .append("</process>")
                .append("</definitions>").toString();

        diaglam1 = diaglam1.replace("id1", String.valueOf(work1.getWorkId()))
                .replace("id2", String.valueOf(work2.getWorkId()))
                .replace("id3", String.valueOf(work3.getWorkId()))
                .replace("id4", String.valueOf(work4.getWorkId()))
                .replace("id5", String.valueOf(work5.getWorkId()))
                .replace("id6", String.valueOf(work6.getWorkId()));

        workflow1 = new WorkflowEntity(workflowTree1.getHierarchyId(), "workflow1", "rev1", diaglam1, null, df.parse("2015/11/18 08:10:00"), null);
        ConWorkflowWorkEntity conwork11 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work1.getWorkId(), false, 1, null, null);
        conwork11.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId()));
        conwork11.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId()));
        ConWorkflowWorkEntity conwork12 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work2.getWorkId(), false, 2, null, null);
        conwork12.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId()));
        conwork12.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId()));
        ConWorkflowWorkEntity conwork13 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work3.getWorkId(), false, 3, null, null);
        conwork13.setEquipmentCollection(Arrays.asList(equip3.getEquipmentId()));
        conwork13.setOrganizationCollection(Arrays.asList(organization3.getOrganizationId()));
        workflow1.setConWorkflowWorkCollection(Arrays.asList(conwork11, conwork12, conwork13));

        List<AddInfoEntity> addInfos1 = new LinkedList();
        addInfos1.add(new AddInfoEntity("prop1", CustomPropertyTypeEnum.TYPE_STRING, "value1", 1, null));
        addInfos1.add(new AddInfoEntity("prop2", CustomPropertyTypeEnum.TYPE_STRING, "value2", 2, null));
        workflow1.setWorkflowAddInfo(JsonUtils.objectsToJson(addInfos1));

        workflow2 = new WorkflowEntity(workflowTree1.getHierarchyId(), "workflow2", "rev1", diaglam1, null, df.parse("2015/11/18 08:10:00"), null);
        ConWorkflowWorkEntity conwork21 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work4.getWorkId(), false, 1, null, null);
        conwork21.setEquipmentCollection(Arrays.asList(equip2.getEquipmentId()));
        conwork21.setOrganizationCollection(Arrays.asList(organization2.getOrganizationId()));
        ConWorkflowWorkEntity conwork22 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work5.getWorkId(), false, 2, null, null);
        conwork22.setEquipmentCollection(Arrays.asList(equip2.getEquipmentId()));
        conwork22.setOrganizationCollection(Arrays.asList(organization2.getOrganizationId()));
        ConWorkflowWorkEntity conwork23 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work6.getWorkId(), false, 3, null, null);
        conwork23.setEquipmentCollection(Arrays.asList(equip3.getEquipmentId()));
        conwork23.setOrganizationCollection(Arrays.asList(organization3.getOrganizationId()));
        workflow2.setConWorkflowWorkCollection(Arrays.asList(conwork21, conwork22, conwork23));

        List<AddInfoEntity> addInfos2 = new LinkedList();
        addInfos2.add(new AddInfoEntity("prop3", CustomPropertyTypeEnum.TYPE_STRING, "value3", 1, null));
        addInfos2.add(new AddInfoEntity("prop4", CustomPropertyTypeEnum.TYPE_STRING, "value4", 2, null));
        workflow2.setWorkflowAddInfo(JsonUtils.objectsToJson(addInfos2));

        tx.begin();
        workflowRest.add(workflow1, null);
        workflowRest.add(workflow2, null);
        tx.commit();

        workflows.clear();
        workflows.add(workflow1.getWorkflowId());
        workflows.add(workflow2.getWorkflowId());

        // カンバン
        KanbanEntity kanban01 = new KanbanEntity(1L, "kanban1", "sub1", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:10:00"), 200L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.PLANNED, null, null);
        KanbanEntity kanban02 = new KanbanEntity(1L, "kanban2", "sub2", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/18 08:10:00"), df.parse("2015/11/18 08:20:00"), 201L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.PLANNED, null, null);
        KanbanEntity kanban03 = new KanbanEntity(1L, "kanban3", "sub3", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/18 08:20:00"), df.parse("2015/11/18 08:30:00"), 201L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.WORKING, null, null);
        KanbanEntity kanban04 = new KanbanEntity(1L, "kanban4", "sub4", workflow1.getWorkflowId(), "workflow2", df.parse("2015/11/18 08:30:00"), df.parse("2015/11/18 08:40:00"), 202L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.WORKING, null, null);
        KanbanEntity kanban05 = new KanbanEntity(1L, "kanban5", "sub5", workflow2.getWorkflowId(), "workflow2", df.parse("2015/11/18 08:40:00"), df.parse("2015/11/18 08:50:00"), 202L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.WORKING, null, null);
        KanbanEntity kanban06 = new KanbanEntity(1L, "kanban6", "sub6", workflow2.getWorkflowId(), "workflow3", df.parse("2015/11/18 08:50:00"), df.parse("2015/11/18 09:00:00"), 202L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.WORKING, null, null);
        KanbanEntity kanban07 = new KanbanEntity(1L, "kanban7", "sub7", workflow2.getWorkflowId(), "workflow3", df.parse("2015/11/18 09:00:00"), df.parse("2015/11/18 09:10:00"), 203L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.SUSPEND, null, null);
        KanbanEntity kanban08 = new KanbanEntity(1L, "kanban8", "sub8", workflow2.getWorkflowId(), "workflow3", df.parse("2015/11/18 09:10:00"), df.parse("2015/11/18 09:20:00"), 203L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.SUSPEND, null, null);
        KanbanEntity kanban09 = new KanbanEntity(1L, "kanban9", "sub9", workflow2.getWorkflowId(), "workflow3", df.parse("2015/11/18 09:20:00"), df.parse("2015/11/18 09:30:00"), 204L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.SUSPEND, null, null);
        KanbanEntity kanban010 = new KanbanEntity(1L, "kanban10", "sub10", workflow2.getWorkflowId(), "workflow3", df.parse("2015/11/18 09:30:00"), df.parse("2015/11/18 09:40:00"), 204L, df.parse("2015/11/18 08:10:00"), KanbanStatusEnum.COMPLETION, null, null);
        KanbanEntity kanban012 = new KanbanEntity(1L, "kanban12", "sub12", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/20 08:00:00"), df.parse("2015/11/20 08:10:00"), 200L, df.parse("2015/11/20 08:10:00"), KanbanStatusEnum.PLANNING, null, null);
        KanbanEntity kanban013 = new KanbanEntity(1L, "kanban13", "sub13", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/20 08:00:00"), df.parse("2015/11/20 08:10:00"), 200L, df.parse("2015/11/20 08:10:00"), KanbanStatusEnum.INTERRUPT, null, null);

        tx.begin();
        kanbanRest.add(kanban01, null);
        kanbanRest.add(kanban02, null);
        kanbanRest.add(kanban03, null);
        kanbanRest.add(kanban04, null);
        kanbanRest.add(kanban05, null);
        kanbanRest.add(kanban06, null);
        kanbanRest.add(kanban07, null);
        kanbanRest.add(kanban08, null);
        kanbanRest.add(kanban09, null);
        kanbanRest.add(kanban010, null);
        kanbanRest.add(kanban012, null);
        kanbanRest.add(kanban013, null);
        tx.commit();

        kanban01 = kanbanRest.find(kanban01.getKanbanId(), null);
        this.detach(kanban01);

        kanban01.setKanbanStatus(KanbanStatusEnum.PLANNED);
        
        ProductEntity product1 = new ProductEntity("A01234567", kanban01.getKanbanId(), 1);
        ProductEntity product2 = new ProductEntity("B01234567", kanban01.getKanbanId(), 2);
        kanban01.setProducts(Arrays.asList(product1, product2));
        kanban01.setLotQuantity(kanban01.getProducts().size());

        tx.begin();
        kanbanRest.update(kanban01, null);
        tx.commit();

        kanban1 = kanbanRest.find(kanban01.getKanbanId(), null);
        kanban2 = kanbanRest.find(kanban02.getKanbanId(), null);
        kanban3 = kanbanRest.find(kanban03.getKanbanId(), null);
        kanban4 = kanbanRest.find(kanban04.getKanbanId(), null);
        kanban5 = kanbanRest.find(kanban05.getKanbanId(), null);
        kanban6 = kanbanRest.find(kanban06.getKanbanId(), null);
        kanban7 = kanbanRest.find(kanban07.getKanbanId(), null);
        kanban8 = kanbanRest.find(kanban08.getKanbanId(), null);
        kanban9 = kanbanRest.find(kanban09.getKanbanId(), null);
        kanban10 = kanbanRest.find(kanban010.getKanbanId(), null);
        kanban12 = kanbanRest.find(kanban012.getKanbanId(), null);
        kanban13 = kanbanRest.find(kanban013.getKanbanId(), null);

        tx.begin();
        kanban1.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanban2.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanban3.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban4.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban5.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban6.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban7.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban8.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban9.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban10.setKanbanStatus(KanbanStatusEnum.COMPLETION);
        kanban12.setKanbanStatus(KanbanStatusEnum.PLANNING);
        kanban13.setKanbanStatus(KanbanStatusEnum.INTERRUPT);
        tx.commit();

        tx.begin();
        kanbanRest.createKanban("kanban11", "workflow1", "kanbanHierarchy1", "identname1", 1, null);
        tx.commit();

        kanban11 = kanbanRest.findByName("kanban11", "workflow1", 1, null);
    }

    /**
     * 検索のテスト
     *
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        System.out.println("testSearch");

        createData();

        List<KanbanEntity> kanbans;
        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition(), null, null, null);
        assertThat(kanbans, is(hasSize(13)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().kanbanName("kanban5"), null, null, null);
        assertThat(kanbans, is(hasSize(1)));
        assertThat(kanbans, is(hasItems(kanban5)));
        assertEquals(kanbans.get(0).getWorkflowName(), kanbans.get(0).getWorkflow().getWorkflowName());

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().kanbanName("anba"), null, null, null);
        assertThat(kanbans, is(hasSize(13)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().kanbanName("kanban11"), null, null, null);
        assertThat(kanbans, is(hasSize(1)));
        assertThat(kanbans, is(hasItems(kanban11)));
        assertEquals(kanbans.get(0).getWorkflowName(), kanbans.get(0).getWorkflow().getWorkflowName());

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().workflowId(workflow1.getWorkflowId()), null, null, null);
        assertThat(kanbans, is(hasSize(7)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban11)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().workflowIdList(workflows), null, null, null);
        assertThat(kanbans, is(hasSize(13)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban5, kanban6, kanban7, kanban8, kanban9, kanban10, kanban11)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")), null, null, null);
        assertThat(kanbans, is(hasSize(11)));
        assertThat(kanbans, is(hasItems(kanban2, kanban3, kanban4, kanban5, kanban6, kanban7, kanban8, kanban9, kanban10)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().toDate(df.parse("2015/11/18 08:42:00")), null, null, null);
        assertThat(kanbans, is(hasSize(5)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban5)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")).toDate(df.parse("2015/11/18 08:42:00")), null, null, null);
        assertThat(kanbans, is(hasSize(4)));
        assertThat(kanbans, is(hasItems(kanban2, kanban3, kanban4, kanban5)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")).toDate(df.parse("2015/11/18 08:42:00")).statusList(Arrays.asList(KanbanStatusEnum.WORKING)), null, null, null);
        assertThat(kanbans, is(hasSize(3)));
        assertThat(kanbans, is(hasItems(kanban3, kanban4, kanban5)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().equipmentList(Arrays.asList(equip1.getEquipmentId())).equipmentIdWithParent(Boolean.TRUE), null, null, null);
        assertThat(kanbans, is(hasSize(7)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban11)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().organizationList(Arrays.asList(organization2.getOrganizationId())).organizationIdWithParent(Boolean.TRUE), null, null, null);
        assertThat(kanbans, is(hasSize(6)));
        assertThat(kanbans, is(hasItems(kanban5, kanban6, kanban7, kanban8, kanban9, kanban10)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().equipmentNameList(Arrays.asList("equip1")), null, null, null);
        assertThat(kanbans, is(hasSize(7)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban11, kanban12, kanban13)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().organizationNameList(Arrays.asList("parson1")), null, null, null);
        assertThat(kanbans, is(hasSize(7)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban11, kanban12, kanban13)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().hierarchyId(1L), null, null, null);
        assertThat(kanbans, is(hasSize(12)));
        assertThat(kanbans, is(hasItems(kanban1, kanban2, kanban3, kanban4, kanban5, kanban6, kanban7, kanban8, kanban9, kanban10, kanban12, kanban13)));
        kanbans.forEach(kanban -> assertEquals(kanban.getWorkflowName(), kanban.getWorkflow().getWorkflowName()));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")).toDate(df.parse("2015/11/18 08:42:00"))
                .fromActualDate(df.parse("2015/11/18 08:12:00")).toActualDate(df.parse("2015/11/18 08:42:00")), null, null, null);
        assertThat(kanbans, is(hasSize(0)));

    }

    /**
     * 検索(分割)のテスト
     *
     * @throws Exception
     */
    @Test
    public void testSearchRange() throws Exception {
        System.out.println("testSearchRange");

        createData();

        List<KanbanEntity> kanbans;
        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")), 0, 2, null);
        assertThat(kanbans, is(hasSize(3)));
        assertThat(kanbans, is(hasItems(kanban2, kanban3, kanban4)));

        kanbans = kanbanRest.searchKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")), 3, 100, null);
        assertThat(kanbans, is(hasSize(8)));
        assertThat(kanbans, is(hasItems(kanban5, kanban6, kanban7, kanban8, kanban9, kanban10)));
    }

    /**
     * 件数取得のテスト
     *
     * @throws Exception
     */
    @Test
    public void testCountSearch() throws Exception {
        System.out.println("testCountSearch");

        createData();

        String num;
        num = kanbanRest.countKanban(new KanbanSearchCondition().fromDate(df.parse("2015/11/18 08:12:00")), null);
        assertThat(Integer.parseInt(num), is(11));

        num = kanbanRest.countKanban(new KanbanSearchCondition().toDate(df.parse("2015/11/18 08:42:00")), null);
        assertThat(Integer.parseInt(num), is(5));
    }

    /**
     * カンバンステータス更新のテスト
     *
     * @throws Exception
     */
    @Test
    public void updateStatus() throws Exception {
        System.out.println("updateStatus");

        this.createData();

        // カンバン情報を取得する。
        KanbanEntity kanban = kanbanRest.findByName("kanban1", workflow1.getWorkflowName(), null, null);

        // カンバンステータスを更新する。
        kanban.setKanbanStatus(KanbanStatusEnum.COMPLETION);
  
        tx.begin();
        Response response = kanbanRest.update(kanban, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));

        // カンバン情報を再取得して、カンバンステータスが更新されている事を確認する。
        kanban = kanbanRest.findByName("kanban1", workflow1.getWorkflowName(), null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
    }

    /**
     * エラーのテスト
     *
     * @throws Exception
     */
    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        createData();

        KanbanEntity kanban11 = new KanbanEntity(0L, "kanban11", "sub11", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:10:00"), 200L, null, KanbanStatusEnum.PLANNED, null, null);
        tx.begin();
        response = kanbanRest.add(kanban11, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        KanbanEntity kanban22 = new KanbanEntity(0L, "kanban11", "sub11", workflow1.getWorkflowId(), "workflow1", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:10:00"), 200L, null, KanbanStatusEnum.PLANNED, null, null);
        tx.begin();
        response = kanbanRest.add(kanban22, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        KanbanEntity kanban33 = new KanbanEntity(0L, "kanban11", "sub11", workflow2.getWorkflowId(), "workflow2", df.parse("2015/11/18 08:00:00"), df.parse("2015/11/18 08:10:00"), 200L, null, KanbanStatusEnum.PLANNED, null, null);
        tx.begin();
        response = kanbanRest.add(kanban33, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

    /**
     * 工程順取得のテスト
     *
     * @throws Exception
     */
    @Test
    public void testGetWorkflowEntity() throws Exception {
        System.out.println("getWorkflowEntity");

        Method getWorkflowEntityMethod = KanbanEntityFacadeREST.class.getDeclaredMethod("getWorkflowEntity", Long.class);
        getWorkflowEntityMethod.setAccessible(true);

        WorkflowEntity intWorkflow1 = new WorkflowEntity(0L, "workflow1", "rev1", null, null, df.parse("2015/11/18 08:42:00"), null);
        tx.begin();
        workflowRest.add(intWorkflow1, null);
        tx.commit();

        WorkflowEntity retWorkflow;
        retWorkflow = (WorkflowEntity) getWorkflowEntityMethod.invoke(kanbanRest, intWorkflow1.getWorkflowId());
        assertThat(retWorkflow, is(intWorkflow1));

        WorkflowEntity intWorkflow2 = workflowRest.find(intWorkflow1.getWorkflowId(), false, null);
        intWorkflow2.setUpdateDatetime(df.parse("2015/11/18 09:42:00"));
        tx.begin();
        workflowRest.update(intWorkflow2, null);
        tx.commit();

        retWorkflow = (WorkflowEntity) getWorkflowEntityMethod.invoke(kanbanRest, intWorkflow2.getWorkflowId());
        assertThat(retWorkflow, is(intWorkflow2));
    }

    /**
     * 日時比較のテスト
     *
     * @throws Exception
     */
    @Test
    public void testDateCompare() throws Exception {
        System.out.println("testDateCompare");

        Date date1 = df.parse("2015/11/18 08:42:00");
        Date date2 = df.parse("2015/11/18 08:42:00");
        assertThat(date1.after(date2), is(false));
        assertThat(date1.before(date2), is(false));

        Date date3 = df.parse("2015/11/18 08:42:00");
        Date date4 = df.parse("2015/11/18 09:42:00");
        assertThat(date3.after(date4), is(false));
        assertThat(date3.before(date4), is(true));

        Date date5 = df.parse("2015/11/18 08:42:00");
        Date date6 = df.parse("2015/11/18 07:42:00");
        assertThat(date5.after(date6), is(true));
        assertThat(date5.before(date6), is(false));
    }

    /**
     * カンバンプロパティ更新のテスト
     *
     * @throws Exception
     */
    @Test
    public void updateProperty() throws Exception {
        System.out.println("updateProperty");

        Response response;
        ResponseEntity responseEntity;

        this.createData();

        // プロパティ値を更新する。
        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(kanban1.getKanbanAddInfo(), AddInfoEntity[].class);
        AddInfoEntity addInfo = addInfos.get(1);
        KanbanPropertyEntity testEntity = new KanbanPropertyEntity(kanban1.getKanbanId(), addInfo.getKey(), addInfo.getType().name(), addInfo.getVal(), addInfo.getDisp());
        testEntity.setKanbanPropertyValue("SN0001");

        String key = addInfo.getKey();

        tx.begin();
        response = kanbanRest.updateProperty(testEntity, null);
        tx.commit();

        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        // カンバンを再取得して、プロパティ値がされている事を確認する。
        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);

        addInfos = JsonUtils.jsonToObjects(kanban1.getKanbanAddInfo(), AddInfoEntity[].class);
        addInfo = addInfos.get(1);
        assertThat(addInfo.getKey(), is(key));
        assertThat(addInfo.getVal(), is("SN0001"));

        // 無効なカンバンIDを指定して、エラー(NOTFOUND_UPDATE)になる事を確認する。
        testEntity.setFkKanbanId(1000L);
        tx.begin();
        response = kanbanRest.updateProperty(testEntity, null);
        tx.rollback();

        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.NOTFOUND_UPDATE));

        // 未登録のプロパティ名を指定して、追加情報が登録される事を確認する。
        testEntity.setFkKanbanId(kanban1.getKanbanId());
        testEntity.setKanbanPropertyName("AddProp");
        testEntity.setKanbanPropertyType(String.valueOf(CustomPropertyTypeEnum.TYPE_STRING));
        testEntity.setKanbanPropertyValue("YES");
        tx.begin();
        response = kanbanRest.updateProperty(testEntity, null);
        tx.commit();

        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        
        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);

        addInfos = JsonUtils.jsonToObjects(kanban1.getKanbanAddInfo(), AddInfoEntity[].class);
        addInfo = addInfos.get(2);
        assertThat(addInfo.getKey(), is("AddProp"));
        assertThat(addInfo.getVal(), is("YES"));
    }

    /**
     * 工程実績登録のテスト
     *
     * @throws Exception
     */
    @Test
    public void testActualReport() throws Exception {
        System.out.println("testActualReport");

        createData();

        // 工程を作成する。
        WorkEntity testWork1 = new WorkEntity(0L, "工程①", 1, 60000, null, ContentTypeEnum.STRING, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null, null);
        WorkEntity testWork2 = new WorkEntity(0L, "工程②", 1, 90000, null, ContentTypeEnum.STRING, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null, null);

        tx.begin();
        em.persist(testWork1);
        em.persist(testWork2);
        tx.commit();

        // 工程順を作成する。
        WorkflowEntity testWorkflow1 = new WorkflowEntity(0L, "工程順①", null, null, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startDatetime1 = sdf.parse("1970-01-01 00:00:00+00");
        Date endDatetime1 = new Date(startDatetime1.getTime() + testWork1.getTaktTime());
        Date startDatetime2 = endDatetime1;
        Date endDatetime2 = new Date(startDatetime2.getTime() + testWork2.getTaktTime());

        ConWorkflowWorkEntity conTestWork1 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, testWork1.getWorkId(), false, 10001, startDatetime1, endDatetime1);
        conTestWork1.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId()));
        conTestWork1.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId(), organization2.getOrganizationId()));

        ConWorkflowWorkEntity conTestWork2 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, testWork2.getWorkId(), false, 20001, startDatetime2, endDatetime2);
        conTestWork2.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId()));
        conTestWork2.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId(), organization2.getOrganizationId()));

        testWorkflow1.setConWorkflowWorkCollection(Arrays.asList(conTestWork1, conTestWork2));

        String diaglam1 = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
                .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
                .append("<process isExecutable=\"true\">")
                .append("<startEvent id=\"start_id\" name=\"start\"/>")
                .append("<endEvent id=\"end_id\" name=\"end\"/>")
                .append("<task id=\"1\"/>")
                .append("<task id=\"2\"/>")
                .append("<sequenceFlow sourceRef=\"2\" targetRef=\"end_id\" id=\"A2SLSQfD\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"start_id\" targetRef=\"1\" id=\"cO6r5XRD\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"1\" targetRef=\"2\" id=\"bCYrairP\" name=\"\"/>")
                .append("</process>")
                .append("</definitions>")
                .toString();

        testWorkflow1.setWorkflowDiaglam(diaglam1);

        tx.begin();
        workflowRest.add(testWorkflow1, null);
        tx.commit();

        // カンバン階層を作成する。
        KanbanHierarchyEntity kanbanHierarchy = new KanbanHierarchyEntity(0L, "カンバン階層①");

        tx.begin();
        kanbanHierarchyRest.add(kanbanHierarchy, null);
        tx.commit();

        long hierarchyId1 = kanbanHierarchy.getKanbanHierarchyId();

        // カンバンを作成する。
        KanbanEntity testKanban1 = new KanbanEntity(hierarchyId1, "カンバン①", null, testWorkflow1.getWorkflowId(), testWorkflow1.getWorkflowName(),
                startDatetime1, endDatetime2, organization1.getOrganizationId(), df.parse("2018/06/01 08:00:00"), KanbanStatusEnum.PLANNING, null, null);
        KanbanEntity testKanban2 = new KanbanEntity(hierarchyId1, "カンバン②", null, testWorkflow1.getWorkflowId(), testWorkflow1.getWorkflowName(),
                startDatetime1, endDatetime2, organization1.getOrganizationId(), df.parse("2018/06/01 08:00:00"), KanbanStatusEnum.PLANNING, null, null);
        KanbanEntity testKanban3 = new KanbanEntity(hierarchyId1, "カンバン③", null, testWorkflow1.getWorkflowId(), testWorkflow1.getWorkflowName(),
                startDatetime1, endDatetime2, organization1.getOrganizationId(), df.parse("2018/06/01 08:00:00"), KanbanStatusEnum.PLANNING, null, null);

        tx.begin();
        kanbanRest.add(testKanban1, null);
        kanbanRest.add(testKanban2, null);
        kanbanRest.add(testKanban3, null);
        tx.commit();

        // カンバンステータスを変更する。
        testKanban1.setKanbanStatus(KanbanStatusEnum.PLANNED);
        testKanban2.setKanbanStatus(KanbanStatusEnum.PLANNED);
        testKanban3.setKanbanStatus(KanbanStatusEnum.PLANNED);

        tx.begin();
        kanbanRest.update(kanban1, null);
        kanbanRest.update(kanban2, null);
        kanbanRest.update(kanban3, null);
        tx.commit();

        WorkKanbanEntity workKanban11 = testKanban1.getWorkKanbanCollection().get(0);
        WorkKanbanEntity workKanban12 = testKanban1.getWorkKanbanCollection().get(1);

        WorkKanbanEntity workKanban21 = testKanban2.getWorkKanbanCollection().get(0);
        WorkKanbanEntity workKanban22 = testKanban2.getWorkKanbanCollection().get(1);

        WorkKanbanEntity workKanban31 = testKanban3.getWorkKanbanCollection().get(0);
        WorkKanbanEntity workKanban32 = testKanban3.getWorkKanbanCollection().get(1);

        long transactionId = 0;

        // 組織① が 設備① で、作業(カンバン① - 工程①)を開始する。
        ActualProductReportEntity actual1 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:01"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result1 = kanbanRest.report(actual1, false, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result1.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result1.getNextTransactionID(), is(transactionId));

        // 同じ transactionId で再度開始する。
        tx.begin();
        ActualProductReportResult result1a = kanbanRest.report(actual1, false, null);
        tx.commit();

        // 結果：　受取済の transactionId のため何もせずに成功が返る。(transactionId はそのまま)
        assertThat(result1a.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result1a.getNextTransactionID(), is(transactionId));

        // 組織① が 設備② で、作業(カンバン① - 工程①)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual2 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip2.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:02"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result2 = kanbanRest.report(actual2, true, null);
        tx.commit();

        // 結果：　「他端末で作業中の組織」のエラーが返る。(transactionId は進む)
        transactionId++;
        assertThat(result2.getResultType(), is(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION));
        assertThat(result2.getNextTransactionID(), is(transactionId));

        // 組織① が 設備② で、作業(カンバン① - 工程①)を開始する。(二重作業チェックなし)
        ActualProductReportEntity actual3 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip2.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:03"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result3 = kanbanRest.report(actual3, false, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result3.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result3.getNextTransactionID(), is(transactionId));

        // 組織① が 設備① で、作業(カンバン① - 工程①)を完了する。
        ActualProductReportEntity actual4 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:04"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result4 = kanbanRest.report(actual4, false, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result4.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result4.getNextTransactionID(), is(transactionId));

        // 組織① が 設備② で、作業(カンバン① - 工程①)を完了する。
        ActualProductReportEntity actual5 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip2.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:05"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result5 = kanbanRest.report(actual5, false, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result5.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result5.getNextTransactionID(), is(transactionId));

        // 組織① が 設備① で、作業(カンバン① - 工程②)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual6 = new ActualProductReportEntity(
                transactionId, workKanban12.getKanbanId(), workKanban12.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:06"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result6 = kanbanRest.report(actual6, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result6.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result6.getNextTransactionID(), is(transactionId));

        // 組織① が 設備② で、作業(カンバン② - 工程①)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual7 = new ActualProductReportEntity(
                transactionId, workKanban21.getKanbanId(), workKanban21.getWorkKanbanId(),
                equip2.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:07"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result7 = kanbanRest.report(actual7, true, null);
        tx.commit();

        // 結果：　「他端末で作業中の組織」のエラーが返る。(transactionId は進む)
        transactionId++;
        assertThat(result7.getResultType(), is(ServerErrorTypeEnum.ALREADY_WORKING_ORGANIZATION));
        assertThat(result7.getNextTransactionID(), is(transactionId));

        // 組織① が 設備② で、作業(カンバン② - 工程①)を開始する。(二重作業チェックなし)
        ActualProductReportEntity actual8 = new ActualProductReportEntity(
                transactionId, workKanban21.getKanbanId(), workKanban21.getWorkKanbanId(),
                equip2.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:08"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result8 = kanbanRest.report(actual8, false, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result8.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result8.getNextTransactionID(), is(transactionId));

        // 組織① が 設備① で、作業(カンバン① - 工程②)を中断する。
        ActualProductReportEntity actual9 = new ActualProductReportEntity(
                transactionId, workKanban12.getKanbanId(), workKanban12.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:09"), KanbanStatusEnum.SUSPEND, null, null);

        tx.begin();
        ActualProductReportResult result9 = kanbanRest.report(actual9, false, null);
        tx.commit();

        // 結果：　作業中断の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result9.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result9.getNextTransactionID(), is(transactionId));

        // 組織① が 設備② で、作業(カンバン② - 工程①)を中断する。
        ActualProductReportEntity actual10 = new ActualProductReportEntity(
                transactionId, workKanban21.getKanbanId(), workKanban21.getWorkKanbanId(),
                equip2.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:10"), KanbanStatusEnum.SUSPEND, null, null);

        tx.begin();
        ActualProductReportResult result10 = kanbanRest.report(actual10, false, null);
        tx.commit();

        // 結果：　作業中断の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result10.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result10.getNextTransactionID(), is(transactionId));

        // 組織① が 設備① で、中断した作業(カンバン① - 工程②)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual11 = new ActualProductReportEntity(
                transactionId, workKanban12.getKanbanId(), workKanban12.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:11"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result11 = kanbanRest.report(actual11, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result11.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result11.getNextTransactionID(), is(transactionId));

        // 組織② が 設備② で、作業(カンバン② - 工程①)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual12 = new ActualProductReportEntity(
                transactionId, workKanban21.getKanbanId(), workKanban21.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:12"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result12 = kanbanRest.report(actual12, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result12.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result12.getNextTransactionID(), is(transactionId));

        // 組織① が 設備① で、作業(カンバン① - 工程②)を完了する。
        ActualProductReportEntity actual13 = new ActualProductReportEntity(
                transactionId, workKanban12.getKanbanId(), workKanban12.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:13"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result13 = kanbanRest.report(actual13, true, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result13.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result13.getNextTransactionID(), is(transactionId));
        // 完了カンバンは移動しない。
        assertThat(testKanban1.getParentId(), is(kanbanHierarchy.getKanbanHierarchyId()));

        // 完了カンバンの自動移動テスト１ (移動先フォルダがない場合)
        // カンバン階層①の完了カンバン自動移動を有効にする。
        kanbanHierarchy.setPartitionFlag(true);

        tx.begin();
        kanbanHierarchyRest.update(kanbanHierarchy, null);
        tx.commit();

        // 組織② が 設備② で、作業(カンバン② - 工程①)を完了する。
        ActualProductReportEntity actual14 = new ActualProductReportEntity(
                transactionId, workKanban21.getKanbanId(), workKanban21.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:14"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result14 = kanbanRest.report(actual14, true, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result14.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result14.getNextTransactionID(), is(transactionId));

        // 組織② が 設備② で、作業(カンバン② - 工程②)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual15 = new ActualProductReportEntity(
                transactionId, workKanban22.getKanbanId(), workKanban22.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:15"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result15 = kanbanRest.report(actual15, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result15.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result15.getNextTransactionID(), is(transactionId));

        // 組織② が 設備② で、作業(カンバン② - 工程②)を完了する。
        ActualProductReportEntity actual16 = new ActualProductReportEntity(
                transactionId, workKanban22.getKanbanId(), workKanban22.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:16"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result16 = kanbanRest.report(actual16, true, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result16.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result16.getNextTransactionID(), is(transactionId));
        // 完了カンバンは移動する。
        KanbanHierarchyEntity compHierarchy2 = kanbanHierarchyRest.find(testKanban2.getParentId(), null);
        assertThat(compHierarchy2.getHierarchyName(), is("カンバン階層①-CLOSE-2018/06"));

        // 完了カンバンの自動移動テスト２ (移動先フォルダが既にある場合)
        // 組織② が 設備② で、作業(カンバン③ - 工程①)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual17 = new ActualProductReportEntity(
                transactionId, workKanban31.getKanbanId(), workKanban31.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:17"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result17 = kanbanRest.report(actual17, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result17.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result17.getNextTransactionID(), is(transactionId));

        // 組織② が 設備② で、作業(カンバン③ - 工程①)を完了する。
        ActualProductReportEntity actual18 = new ActualProductReportEntity(
                transactionId, workKanban31.getKanbanId(), workKanban31.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:18"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result18 = kanbanRest.report(actual18, true, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result18.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result18.getNextTransactionID(), is(transactionId));

        // 組織② が 設備② で、作業(カンバン③ - 工程②)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual19 = new ActualProductReportEntity(
                transactionId, workKanban32.getKanbanId(), workKanban32.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:19"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result19 = kanbanRest.report(actual19, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result19.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result19.getNextTransactionID(), is(transactionId));

        // 組織② が 設備② で、作業(カンバン③ - 工程②)を完了する。
        ActualProductReportEntity actual20 = new ActualProductReportEntity(
                transactionId, workKanban32.getKanbanId(), workKanban32.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:00:20"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result20 = kanbanRest.report(actual20, true, null);
        tx.commit();

        // 結果：　作業完了の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result20.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result20.getNextTransactionID(), is(transactionId));
        // 完了カンバンは移動する。
        KanbanHierarchyEntity compHierarchy3 = kanbanHierarchyRest.find(testKanban3.getParentId(), null);
        assertThat(compHierarchy3.getHierarchyName(), is("カンバン階層①-CLOSE-2018/06"));
    }

    /**
     * updateStatus のテスト
     * 
     * @throws Exception 
     */
    @Test
    public void testUpdateStatus() throws Exception {
        System.out.println("updateStatus(List<Long>, String)");
        
        this.createData();
        
        long transactionId = 0L;
        
        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);       // 計画済
        kanban3 = kanbanRest.find(kanban3.getKanbanId(), null);       // 作業中
        kanban7 = kanbanRest.find(kanban7.getKanbanId(), null);       // 一時中断
        kanban10 = kanbanRest.find(kanban10.getKanbanId(), null);     // 完了
        kanban12 = kanbanRest.find(kanban12.getKanbanId(), null);     // 計画中
        kanban13 = kanbanRest.find(kanban13.getKanbanId(), null);     // 中止

        // カンバンIDリストを取得
        List<Long> kanbanIds = new ArrayList<>();
        kanbanIds.add(kanban12.getKanbanId());  // 計画中
        kanbanIds.add(kanban1.getKanbanId());   // 計画済
        kanbanIds.add(kanban3.getKanbanId());   // 作業中
        kanbanIds.add(kanban7.getKanbanId());   // 一時中断
        kanbanIds.add(kanban10.getKanbanId());  // 完了
        kanbanIds.add(kanban13.getKanbanId());  // 中止

        
        // カンバンのステータスを計画中に変更
        tx.begin();
        Response response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.PLANNING.name(), true, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));
        assertThat(kanban1.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));
        assertThat(kanban3.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));
        assertThat(kanban7.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));
        assertThat(kanban10.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));
        assertThat(kanban13.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));

        
        // カンバンのステータスを計画済に変更
        kanban12.setKanbanStatus(KanbanStatusEnum.PLANNING);
        kanban1.setKanbanStatus(KanbanStatusEnum.PLANNING);
        kanban3.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban7.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban10.setKanbanStatus(KanbanStatusEnum.COMPLETION);
        kanban13.setKanbanStatus(KanbanStatusEnum.INTERRUPT);

        // 作業開始
        WorkKanbanEntity workKanban = kanban3.getWorkKanbanCollection().get(0);
        ActualProductReportEntity actual1 = new ActualProductReportEntity(transactionId, workKanban.getKanbanId(), workKanban.getWorkKanbanId(), 
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:01"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result1 = kanbanRest.report(actual1, false, null);
        tx.commit();
        
        transactionId++;
        assertThat(result1.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result1.getNextTransactionID(), is(transactionId));
        
        // 計画済に変更
        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.PLANNED.name(), false, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.NOT_SOME_UPDATED));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban1.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban3.getKanbanStatus(), is(KanbanStatusEnum.WORKING)); // 作業中の工程があるため、作業中のまま
        assertThat(kanban7.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban10.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban13.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));

        
        // 強制的に計画済に変更
        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.PLANNED.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban1.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban3.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban7.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban10.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kanban13.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        
        workKanban = workKanbanRest.find(workKanban.getWorkKanbanId(), null);
        assertThat(workKanban.getWorkStatus(), is(KanbanStatusEnum.SUSPEND));
        
        // 一時中断
        ActualProductReportEntity actual2 = new ActualProductReportEntity(transactionId, workKanban.getKanbanId(), workKanban.getWorkKanbanId(), 
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:10:00"), KanbanStatusEnum.SUSPEND, null, null);

        tx.begin();
        ActualProductReportResult result2 = kanbanRest.report(actual2, false, null);
        tx.commit();

        transactionId++;
        assertThat(result2.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result2.getNextTransactionID(), is(transactionId));

        // キャンセルされているため、一時中断しても状態は変わらず
        assertThat(workKanban.getWorkStatus(), is(KanbanStatusEnum.SUSPEND));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        tx.begin();
        kanban1.getWorkKanbanCollection().get(0).setStartDatetime(df.parse("2015/11/18 08:00:00"));
        workKanbanRest.update(kanban1.getWorkKanbanCollection().get(0), null);

        kanban3.getWorkKanbanCollection().get(0).setStartDatetime(df.parse("2015/11/18 08:00:00"));
        workKanbanRest.update(kanban3.getWorkKanbanCollection().get(0), null);

        kanban12.getWorkKanbanCollection().get(0).setStartDatetime(df.parse("2015/11/18 08:00:00"));
        workKanbanRest.update(kanban12.getWorkKanbanCollection().get(0), null);

        kanban13.getWorkKanbanCollection().get(0).setStartDatetime(df.parse("2015/11/18 08:00:00"));
        workKanbanRest.update(kanban13.getWorkKanbanCollection().get(0), null);

        tx.commit();


        // 工程の実施フラグを検証
        List<KanbanEntity> kanbans = kanbanRest.searchProduct(0, 20, this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), sdf.format(new Date()), null, null, null, null, null, null);
        assertThat(kanbans, is(hasSize(4)));

        assertEquals(kanbans.get(0).getKanbanId(), kanban1.getKanbanId());
        assertEquals(kanbans.get(1).getKanbanId(), kanban3.getKanbanId());
        assertEquals(kanbans.get(2).getKanbanId(), kanban12.getKanbanId());
        assertEquals(kanbans.get(3).getKanbanId(), kanban13.getKanbanId());


        //assertThat(kanbans, is(hasItems(kanban1, kanban3, kanban12, kanban13)));

        
        // カンバンのステータスを完了に変更
        kanban12.setKanbanStatus(KanbanStatusEnum.PLANNING);
        kanban1.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanban3.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban7.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban10.setKanbanStatus(KanbanStatusEnum.COMPLETION);
        kanban13.setKanbanStatus(KanbanStatusEnum.INTERRUPT);

        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.COMPLETION.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.NOT_SOME_UPDATED));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
        assertThat(kanban1.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
        assertThat(kanban3.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION)); // 変更可
        assertThat(kanban7.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION)); // 変更可
        assertThat(kanban10.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
        assertThat(kanban13.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));

        
        // カンバンのステータスを中止に変更
        kanban12.setKanbanStatus(KanbanStatusEnum.PLANNING);
        kanban1.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanban3.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban7.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban10.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban13.setKanbanStatus(KanbanStatusEnum.SUSPEND);
//        kanban10.setKanbanStatus(KanbanStatusEnum.COMPLETION);
//        kanban13.setKanbanStatus(KanbanStatusEnum.INTERRUPT);


        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.INTERRUPT.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.INTERRUPT)); // 変更可
        assertThat(kanban1.getKanbanStatus(), is(KanbanStatusEnum.INTERRUPT)); // 変更可
        assertThat(kanban3.getKanbanStatus(), is(KanbanStatusEnum.INTERRUPT)); // 変更可
        assertThat(kanban7.getKanbanStatus(), is(KanbanStatusEnum.INTERRUPT)); // 変更可
//        assertThat(kanban10.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
//        assertThat(kanban13.getKanbanStatus(), is(KanbanStatusEnum.INTERRUPT));

        // カンバンのステータスを中止に変更1
        kanban12.setKanbanStatus(KanbanStatusEnum.COMPLETION);
        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.INTERRUPT.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.NOT_SOME_UPDATED));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));

        // カンバンのステータスを中止に変更2
        kanban12.setKanbanStatus(KanbanStatusEnum.INTERRUPT);
        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.INTERRUPT.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.NOT_SOME_UPDATED));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.INTERRUPT));


        // カンバンのステータスを中止に変更2
        kanban12.setKanbanStatus(KanbanStatusEnum.DEFECT);
        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.INTERRUPT.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.NOT_SOME_UPDATED));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.DEFECT));

        // カンバンのステータスを計画中に変更
        kanban12.setKanbanStatus(KanbanStatusEnum.PLANNING);
        kanban1.setKanbanStatus(KanbanStatusEnum.PLANNED);
        kanban3.setKanbanStatus(KanbanStatusEnum.WORKING);
        kanban7.setKanbanStatus(KanbanStatusEnum.SUSPEND);
        kanban10.setKanbanStatus(KanbanStatusEnum.COMPLETION);
        kanban13.setKanbanStatus(KanbanStatusEnum.INTERRUPT);

        tx.begin();
        response = kanbanRest.updateStatus(kanbanIds, KanbanStatusEnum.PLANNING.name(), true, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(kanban12.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));
        assertThat(kanban1.getKanbanStatus(), is(KanbanStatusEnum.PLANNING)); // 変更可
        assertThat(kanban3.getKanbanStatus(), is(KanbanStatusEnum.PLANNING)); // 変更可
        assertThat(kanban7.getKanbanStatus(), is(KanbanStatusEnum.PLANNING)); // 変更可
        assertThat(kanban10.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));// 変更可
        assertThat(kanban13.getKanbanStatus(), is(KanbanStatusEnum.PLANNING));// 変更可

        // 計画中に変更されたため、計画済になっている
        assertThat(workKanban.getWorkStatus(), is(KanbanStatusEnum.PLANNED));

        // 工程の実施フラグを検証
        kanbans = kanbanRest.searchProduct(0, 20, this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), sdf.format(new Date()), null, null, null, null, null, null);
        assertThat(kanbans, is(hasSize(0)));
    }

    /**
     * findProduct のテスト
     *
     * @throws Exception
     */
    @Test
    public void testFindProduct() throws Exception {
        this.createData();

        long transactionId = 0L;

        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);       // 計画済
        kanban3 = kanbanRest.find(kanban3.getKanbanId(), null);       // 作業中
        kanban7 = kanbanRest.find(kanban7.getKanbanId(), null);       // 一時中断
        kanban10 = kanbanRest.find(kanban10.getKanbanId(), null);     // 完了
        kanban12 = kanbanRest.find(kanban12.getKanbanId(), null);     // 計画中
        kanban13 = kanbanRest.find(kanban13.getKanbanId(), null);     // 中止


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        List<KanbanEntity> kanbans;
        String count;

        tx.begin();
        kanban1.getWorkKanbanCollection().get(0).setStartDatetime(df.parse("2015/11/18 08:00:00"));
        workKanbanRest.update(kanban1.getWorkKanbanCollection().get(0), null);
        tx.commit();

        // 応援 = false の場合
        count =  kanbanRest.countProduct(this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), null, false, null);
        assertThat(Integer.valueOf(count), is(2));
         
        kanbans = kanbanRest.searchProduct(0, 20, this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), sdf.format(new Date()), null, null, null, false, null, null);
        assertThat(kanbans, is(hasSize(1)));
        assertEquals(kanbans.get(0).getKanbanId(), kanban1.getKanbanId());

        // 応援 = true の場合
        count =  kanbanRest.countProduct(this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), null, true, null);
        assertThat(Integer.valueOf(count), is(0));

        kanbans = kanbanRest.searchProduct(0, 20, this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), sdf.format(new Date()), null, null, null, true, null, null);
        assertThat(kanbans, is(hasSize(0)));


        // 作業開始
        WorkKanbanEntity workKanban = kanban1.getWorkKanbanCollection().get(0);
        ActualProductReportEntity actual1 = new ActualProductReportEntity(transactionId, workKanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:00:01"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result1 = kanbanRest.report(actual1, false, null);
        tx.commit();

        transactionId++;
        assertThat(result1.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result1.getNextTransactionID(), is(transactionId));

        // 応援 = true の場合
        count =  kanbanRest.countProduct(this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), null, true, null);
        assertThat(Integer.valueOf(count), is(1));

        workKanban.setStartDatetime(df.parse("2015/11/18 08:00:00"));
        tx.begin();
        workKanbanRest.update(workKanban, null);
        tx.commit();
        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);

        kanbans = kanbanRest.searchProduct(0, 20, this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), sdf.format(new Date()), null, null, null, true, null, null);
        assertThat(kanbans, is(hasSize(1)));
        assertEquals(kanbans.get(0).getKanbanId(), kanban1.getKanbanId());

        ActualProductReportEntity actual2 = new ActualProductReportEntity(transactionId, workKanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:10:00"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result2 = kanbanRest.report(actual2, false, null);
        tx.commit();

        transactionId++;
        assertThat(result2.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result2.getNextTransactionID(), is(transactionId));

        // 応援 = true の場合
        kanbans = kanbanRest.searchProduct(0, 20, this.equip1.getEquipmentId(), this.organization1.getOrganizationId(), sdf.format(new Date()), null, null, null, true, null, null);
        assertThat(kanbans, is(hasSize(0)));
    }

    /**
     * オブジェクトを永続化コンテキストの管理下から外す。
     *
     * @param kanban
     */
    private void detach(KanbanEntity kanban) {
        em.detach(kanban);
        for (WorkKanbanEntity workKanban : kanban.getWorkKanbanCollection()) {
            em.detach(workKanban);
        }

        if (Objects.nonNull(kanban.getProducts())) {
            for (ProductEntity product : kanban.getProducts()) {
                em.detach(product);
            }
        }
    }

    /**
     * 工程カンバンやり直しのテスト
     *
     * @throws Exception
     */
    @Test
    public void testRework() throws Exception {
        System.out.println("testReportRework");

        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = serviceTestData.createTestKanban();

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // 計画変更で工程カンバンの開始・完了計画日時を設定する。
        PlanChangeCondition condition = new PlanChangeCondition();
        condition.setStartDatetime(datetimeFormat.parse("2019/04/10 10:00:00"));
        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        OrganizationEntity worker1 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_1, null, null);
        OrganizationEntity worker2 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_2, null, null);
        OrganizationEntity worker3 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_3, null, null);

        EquipmentEntity adPro1 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_1, null, null);
        EquipmentEntity adPro2 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_2, null, null);
        EquipmentEntity adPro3 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_3, null, null);

        WorkKanbanEntity workKanban1 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_1)).findFirst().get();
        WorkKanbanEntity workKanban2 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_2)).findFirst().get();
        WorkKanbanEntity workKanban3 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_3)).findFirst().get();
        WorkKanbanEntity workKanban4 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_4)).findFirst().get();
        WorkKanbanEntity workKanban5 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_5)).findFirst().get();
        WorkKanbanEntity workKanban6 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_6)).findFirst().get();

        Long transactionId = 1L;
        ActualProductReportResult repResult;

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程①)を開始する。
        Date workingDate1 = datetimeFormat.parse("2019/04/11 10:00:00");
        repResult = serviceTestData.report(transactionId, workKanban1, adPro1, worker1, workingDate1, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban1 = ServiceTestData.getWorkKanbanRest().find(workKanban1.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.WORKING, workingDate1, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程①)を完了する。
        Date compDate1 = datetimeFormat.parse("2019/04/11 11:00:00");
        repResult = serviceTestData.report(transactionId, workKanban1, adPro1, worker1, compDate1, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban1 = ServiceTestData.getWorkKanbanRest().find(workKanban1.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程②)を開始する。
        Date workingDate2 = datetimeFormat.parse("2019/04/11 11:00:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro1, worker1, workingDate2, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.WORKING, workingDate2, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程②)を完了する。
        Date compDate2 = datetimeFormat.parse("2019/04/11 12:00:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro1, worker1, compDate2, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.COMPLETION, workingDate2, compDate2, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程③)を開始する。
        Date workingDate3 = datetimeFormat.parse("2019/04/11 13:00:00");
        repResult = serviceTestData.report(transactionId, workKanban3, adPro1, worker1, workingDate3, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban3 = ServiceTestData.getWorkKanbanRest().find(workKanban3.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.WORKING, workingDate3, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程③)を完了する。
        Date compDate3 = datetimeFormat.parse("2019/04/11 14:00:00");
        repResult = serviceTestData.report(transactionId, workKanban3, adPro1, worker1, compDate3, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban3 = ServiceTestData.getWorkKanbanRest().find(workKanban3.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.COMPLETION, workingDate3, compDate3, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程④)を開始する。
        Date workingDate4 = datetimeFormat.parse("2019/04/11 14:00:00");
        repResult = serviceTestData.report(transactionId, workKanban4, adPro1, worker1, workingDate4, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban4 = ServiceTestData.getWorkKanbanRest().find(workKanban4.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.WORKING, workingDate4, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程④)を完了する。
        Date compDate4 = datetimeFormat.parse("2019/04/11 15:00:00");
        repResult = serviceTestData.report(transactionId, workKanban4, adPro1, worker1, compDate4, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban4 = ServiceTestData.getWorkKanbanRest().find(workKanban4.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.COMPLETION, workingDate4, compDate4, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程⑤)を開始する。
        Date workingDate5 = datetimeFormat.parse("2019/04/11 16:00:00");
        repResult = serviceTestData.report(transactionId, workKanban5, adPro1, worker1, workingDate5, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban5 = ServiceTestData.getWorkKanbanRest().find(workKanban5.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.WORKING, workingDate5, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程②)のやり直しを開始する。(作業のやり直し)
        Date workingDate2r = datetimeFormat.parse("2019/04/11 16:10:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro2, worker2, workingDate2r, KanbanStatusEnum.WORKING, true, true, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.WORKING, workingDate2r, null, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程②)のやり直しを完了する。(作業のやり直し)
        Date compDate2r = datetimeFormat.parse("2019/04/11 16:40:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro2, worker2, compDate2r, KanbanStatusEnum.COMPLETION, true, true, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        // ※．完了している後工程が全て「計画済」に戻る。
        // 工程①は、前の工程なので更新なし。
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
        // 工程②は、やり直した工程。
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.COMPLETION, workingDate2r, compDate2r, 1);
        // 工程③は、やり直し以降の完了工程なので計画済に戻り、開始・完了実績日時がnullになる。
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.PLANNED, null, null, 1);
        // 工程④は、やり直し以降の完了工程なので計画済に戻り、開始・完了実績日時がnullになる。
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.PLANNED, null, null, 1);
        // 工程⑤は、やり直し以降の完了工程だが、作業中なので更新なし。
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.WORKING, workingDate5, null, null);
        // 工程⑥は、やり直し以降の完了工程だが、計画済なので更新なし。
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.PLANNED, null, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程⑤)を完了する。
        Date compDate5 = datetimeFormat.parse("2019/04/11 17:00:00");
        repResult = serviceTestData.report(transactionId, workKanban5, adPro1, worker1, compDate5, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban5 = ServiceTestData.getWorkKanbanRest().find(workKanban5.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.COMPLETION, workingDate5, compDate5, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程③)を開始する。(やり直し後工程で計画済に戻っているので通常作業)
        Date workingDate3r = datetimeFormat.parse("2019/04/15 09:00:00");
        repResult = serviceTestData.report(transactionId, workKanban3, adPro2, worker2, workingDate3r, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban3 = ServiceTestData.getWorkKanbanRest().find(workKanban3.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.WORKING, workingDate3r, null, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程③)を中断する。(やり直し後工程で計画済に戻っているので通常作業)
        Date suspendDate3 = datetimeFormat.parse("2019/04/15 09:10:00");
        repResult = serviceTestData.report(transactionId, workKanban3, adPro2, worker2, suspendDate3, KanbanStatusEnum.SUSPEND, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban3 = ServiceTestData.getWorkKanbanRest().find(workKanban3.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.SUSPEND, workingDate3r, null, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));// 中断中: 工程③

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程④)を開始する。(やり直し後工程で計画済に戻っているので通常作業)
        Date workingDate4r = datetimeFormat.parse("2019/04/15 09:30:00");
        repResult = serviceTestData.report(transactionId, workKanban4, adPro2, worker2, workingDate4r, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban4 = ServiceTestData.getWorkKanbanRest().find(workKanban4.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.WORKING, workingDate4r, null, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));// 作業中: 工程③

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程④)を完了する。(やり直し後工程で計画済に戻っているので通常作業)
        Date compDate4r = datetimeFormat.parse("2019/04/15 10:00:00");
        repResult = serviceTestData.report(transactionId, workKanban4, adPro2, worker2, compDate4r, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban4 = ServiceTestData.getWorkKanbanRest().find(workKanban4.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.COMPLETION, workingDate4r, compDate4r, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));// 中断中: 工程③

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程⑥)を開始する。
        Date workingDate6 = datetimeFormat.parse("2019/04/15 10:40:00");
        repResult = serviceTestData.report(transactionId, workKanban6, adPro1, worker1, workingDate6, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban6 = ServiceTestData.getWorkKanbanRest().find(workKanban6.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.WORKING, workingDate6, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));// 作業中: 工程③

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程⑥)を中断する。
        Date suspendDate6 = datetimeFormat.parse("2019/04/15 10:50:00");
        repResult = serviceTestData.report(transactionId, workKanban6, adPro1, worker1, suspendDate6, KanbanStatusEnum.SUSPEND, false, true);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban6 = ServiceTestData.getWorkKanbanRest().find(workKanban6.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.SUSPEND, workingDate6, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));// 中断中: 工程③, 工程⑥

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程②)のやり直しを開始する。(作業のやり直し)
        Date workingDate2r_2 = datetimeFormat.parse("2019/04/15 11:00:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro3, worker3, workingDate2r_2, KanbanStatusEnum.WORKING, true, true, true);
        transactionId = repResult.getNextTransactionID();
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.WORKING, workingDate2r_2, null, 2);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));// 作業中: 工程③, 工程⑥

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程②)のやり直しを中断する。(作業のやり直し)
        Date suspendDate2r = datetimeFormat.parse("2019/04/15 11:10:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro3, worker3, suspendDate2r, KanbanStatusEnum.SUSPEND, true, true, true);
        transactionId = repResult.getNextTransactionID();
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        // ※．完了している後工程が全て「計画済」に戻る。
        // 工程①は、前の工程なので更新なし。
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
        // 工程②は、やり直した工程。
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.SUSPEND, workingDate2r_2, null, 2);
        // 工程③は、やり直し以降の工程だが、中断中なので更新なし。
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.SUSPEND, workingDate3r, null, 1);
        // 工程④は、やり直し以降の完了工程なので計画済に戻り、開始・完了実績日時がnullになる。
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.PLANNED, null, null, 2);
        // 工程⑤は、やり直し以降の完了工程なので計画済に戻り、開始・完了実績日時がnullになる。
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.PLANNED, null, null, 1);
        // 工程⑥は、やり直し以降の工程だが、中断中なので更新なし。
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.SUSPEND, workingDate6, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程②)を再開する。
        Date workingDate2r_3 = datetimeFormat.parse("2019/04/15 11:40:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro3, worker3, workingDate2r_3, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.WORKING, workingDate2r_2, null, 2);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程②)を完了する。
        Date compDate2r_2 = datetimeFormat.parse("2019/04/15 12:00:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro3, worker3, compDate2r_2, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.COMPLETION, workingDate2r_2, compDate2r_2, 2);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));// 中断中: 工程③, 工程⑥

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程③)を再開する。
        Date workingDate3r_2 = datetimeFormat.parse("2019/04/15 13:00:00");
        repResult = serviceTestData.report(transactionId, workKanban3, adPro2, worker2, workingDate3r_2, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban3 = ServiceTestData.getWorkKanbanRest().find(workKanban3.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.WORKING, workingDate3r, null, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));// 中断中: 工程⑥

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程③)を完了する。
        Date compDate3r = datetimeFormat.parse("2019/04/15 13:20:00");
        repResult = serviceTestData.report(transactionId, workKanban3, adPro2, worker2, compDate3r, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban3 = ServiceTestData.getWorkKanbanRest().find(workKanban3.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.COMPLETION, workingDate3r, compDate3r, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));// 中断中: 工程⑥

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程⑥)を再開する。
        Date workingDate6_2 = datetimeFormat.parse("2019/04/15 13:30:00");
        repResult = serviceTestData.report(transactionId, workKanban6, adPro1, worker1, workingDate6_2, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban6 = ServiceTestData.getWorkKanbanRest().find(workKanban6.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.WORKING, workingDate6, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程⑥)を完了する。
        Date compDate6_2 = datetimeFormat.parse("2019/04/15 13:50:00");
        repResult = serviceTestData.report(transactionId, workKanban6, adPro1, worker1, compDate6_2, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban6 = ServiceTestData.getWorkKanbanRest().find(workKanban6.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.COMPLETION, workingDate6, compDate6_2, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程④)を開始する。(やり直し後工程で計画済に戻っているので通常作業)
        Date workingDate4r_2 = datetimeFormat.parse("2019/04/15 14:00:00");
        repResult = serviceTestData.report(transactionId, workKanban4, adPro3, worker3, workingDate4r_2, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban4 = ServiceTestData.getWorkKanbanRest().find(workKanban4.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.WORKING, workingDate4r_2, null, 2);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程④)を完了する。(やり直し後工程で計画済に戻っているので通常作業)
        Date compDate4r_2 = datetimeFormat.parse("2019/04/15 15:00:00");
        repResult = serviceTestData.report(transactionId, workKanban4, adPro3, worker3, compDate4r_2, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban4 = ServiceTestData.getWorkKanbanRest().find(workKanban4.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.COMPLETION, workingDate4r_2, compDate4r_2, 2);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程⑤)を開始する。(やり直し後工程で計画済に戻っているので通常作業)
        Date workingDate5r = datetimeFormat.parse("2019/04/15 15:00:00");
        repResult = serviceTestData.report(transactionId, workKanban5, adPro3, worker3, workingDate5r, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban5 = ServiceTestData.getWorkKanbanRest().find(workKanban5.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.WORKING, workingDate5r, null, 1);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者③ が 作業者端末③ で、作業(カンバン① - 工程⑤)を完了する。(やり直し後工程で計画済に戻っているので通常作業)
        Date compDate5r = datetimeFormat.parse("2019/04/15 11:30:00");
        repResult = serviceTestData.report(transactionId, workKanban5, adPro3, worker3, compDate5r, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban5 = ServiceTestData.getWorkKanbanRest().find(workKanban5.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        // ※．やり直し後工程は通常作業のため、他工程の更新なし。
        // 工程①は、更新なし。
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
        // 工程②は、更新なし。
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.COMPLETION, workingDate2r_2, compDate2r_2, 2);
        // 工程③は、更新なし。
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.COMPLETION, workingDate3r, compDate3r, 1);
        // 工程④は、やり直した工程。
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.COMPLETION, workingDate4r_2, compDate4r_2, 2);
        // 工程⑤は、更新なし。
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.COMPLETION, workingDate5r, compDate5r, 1);
        // 工程⑥は、更新なし。
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.COMPLETION, workingDate6, compDate6_2, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));

        // 2019/12/19 １行程のみの作業のやり直し 後続をやり直さない場合のテストを実施する
        // 作業者② が 作業者端末② で、作業(カンバン① - 工程②)のやり直しを開始する。(作業のやり直し)
        Date workingDate2r_5 = datetimeFormat.parse("2019/04/16 16:10:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro2, worker2, workingDate2r_5, KanbanStatusEnum.WORKING, true, true, false);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.WORKING, workingDate2r_5, null, 3);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

        // 作業者② が 作業者端末② で、作業(カンバン① - 工程②)のやり直しを完了する。(作業のやり直し)
        Date compDate2r_3 = datetimeFormat.parse("2019/04/16 16:40:00");
        repResult = serviceTestData.report(transactionId, workKanban2, adPro2, worker2, compDate2r_3, KanbanStatusEnum.COMPLETION, true, true, false);
        transactionId = repResult.getNextTransactionID();
        // 再取得して状態をチェックする。
        workKanban2 = ServiceTestData.getWorkKanbanRest().find(workKanban2.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        // ※．やり直し後の工程が「計画済」に戻らないこと。

        // ※．やり直し後工程は通常作業のため、他工程の更新なし。
        // 工程①は、更新なし。
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
        // 工程②は、やり直した工程。
        serviceTestData.checkWorkKanban(workKanban2, KanbanStatusEnum.COMPLETION, workingDate2r_5, compDate2r_3, 3);
        // 工程③は、更新なし。
        serviceTestData.checkWorkKanban(workKanban3, KanbanStatusEnum.COMPLETION, workingDate3r, compDate3r, 1);
        // 工程④は、更新なし。
        serviceTestData.checkWorkKanban(workKanban4, KanbanStatusEnum.COMPLETION, workingDate4r_2, compDate4r_2, 2);
        // 工程⑤は、更新なし。
        serviceTestData.checkWorkKanban(workKanban5, KanbanStatusEnum.COMPLETION, workingDate5r, compDate5r, 1);
        // 工程⑥は、更新なし。
        serviceTestData.checkWorkKanban(workKanban6, KanbanStatusEnum.COMPLETION, workingDate6, compDate6_2, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
    }

    /**
     * 同時作業禁止工程のテスト
     *
     * @throws Exception
     */
    @Test
    public void testReportSyncWork() throws Exception {
        System.out.println("testReportSyncWork");

        serviceTestData.createTestData();

        // 同時作業禁止工程のカンバンを作成する。
        KanbanEntity kanban = serviceTestData.createSyncWorkTestKanban();

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        OrganizationEntity worker1 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_1, null, null);
        OrganizationEntity worker2 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_2, null, null);

        EquipmentEntity adPro1 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_1, null, null);
        EquipmentEntity adPro2 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_2, null, null);

        Long transactionId = 1L;
        ActualProductReportResult repResult;

        Date workingDate = datetimeFormat.parse("2019/04/11 10:00:00");
        Calendar cal = Calendar.getInstance();
        cal.setTime(workingDate);

        for (int i = 0; i < kanban.getWorkKanbanCollection().size(); i++) {
            boolean isLast = false;
            if (i >= kanban.getWorkKanbanCollection().size() - 1) {
                isLast = true;
            }

            WorkKanbanEntity workKanban = kanban.getWorkKanbanCollection().get(i);

            // 作業者① が 作業者端末① で、作業を開始する。
            Date workingDate1 = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro1, worker1, workingDate1, KanbanStatusEnum.WORKING, false, true);
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.WORKING, workingDate1, null, null);
            assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

            // 作業者② が 作業者端末② で、作業を開始する。(作業中のため開始できない)
            Date workingDate1a = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro2, worker2, workingDate1a, KanbanStatusEnum.WORKING, false, false);
            assertThat(repResult.getResultType(), is(ServerErrorTypeEnum.THERE_WORKING_NON_START));// 作業中のため作業不可
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.WORKING, workingDate1, null, null);
            assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

            cal.add(Calendar.MINUTE, 10);

            // 作業者① が 作業者端末① で、作業を中断する。
            Date suspendDate1 = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro1, worker1, suspendDate1, KanbanStatusEnum.SUSPEND, false, true);
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.SUSPEND, workingDate1, null, null);
            assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.SUSPEND));

            cal.add(Calendar.MINUTE, 10);

            // 作業者② が 作業者端末② で、作業を開始する。(中断中のため開始できる)
            Date workingDate1b = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro2, worker2, workingDate1b, KanbanStatusEnum.WORKING, false, true);
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.WORKING, workingDate1, null, null);
            assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

            // 作業者① が 作業者端末① で、作業を開始する。(作業中のため開始できない)
            Date workingDate1c = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro1, worker1, workingDate1c, KanbanStatusEnum.WORKING, false, false);
            assertThat(repResult.getResultType(), is(ServerErrorTypeEnum.THERE_WORKING_NON_START));// 作業中のため作業不可
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.WORKING, workingDate1, null, null);
            assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));

            cal.add(Calendar.MINUTE, 10);

            // 作業者② が 作業者端末② で、作業を完了する。
            Date compDate1 = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro2, worker2, compDate1, KanbanStatusEnum.COMPLETION, false, true);
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
            if (isLast) {
                assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
            } else {
                assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));
            }

            cal.add(Calendar.MINUTE, 10);

            // 作業者① が 作業者端末① で、作業を開始する。(作業完了のため開始できない)
            Date workingDate1d = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro1, worker1, workingDate1d, KanbanStatusEnum.WORKING, false, false);
            assertThat(repResult.getResultType(), is(ServerErrorTypeEnum.THERE_COMPLETED_NON_START));// 作業完了済みのため作業不可
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
            if (isLast) {
                assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
            } else {
                assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));
            }

            // 作業者② が 作業者端末② で、作業を開始する。(作業完了のため開始できない)
            Date workingDate1e = cal.getTime();
            repResult = serviceTestData.report(transactionId, workKanban, adPro2, worker2, workingDate1e, KanbanStatusEnum.WORKING, false, false);
            assertThat(repResult.getResultType(), is(ServerErrorTypeEnum.THERE_COMPLETED_NON_START));// 作業完了済みのため作業不可
            transactionId = repResult.getNextTransactionID();
            workKanban = ServiceTestData.getWorkKanbanRest().find(workKanban.getWorkKanbanId(), null);
            kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
            serviceTestData.checkWorkKanban(workKanban, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
            if (isLast) {
                assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.COMPLETION));
            } else {
                assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));
            }

            cal.add(Calendar.MINUTE, 10);
        }
    }

    /**
     * カンバンの計画時間を変更するテスト
     *
     * @throws Exception
     */
    @Test
    public void testPlanChange() throws Exception {
        System.out.println("testPlanChange");

        // ※.計画変更で中断時間を設定後、再度計画変更で別の中断時間を設定した場合、
        //  前に設定した中断時間は消える仕様になっているので注意。
        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = serviceTestData.createTestKanban();

        // 計画変更で開始時間を設定する。(最終工程の完了時刻が工程順の作業時間枠の終了時間と同じ)
        this.testPlanChangeBaseTime(kanban);

        // 計画変更で中断時間を設定する。(休憩後の工程を中断)
        this.testPlanChangeAfterBreaktime1(kanban);

        // 計画変更で中断時間を設定する。(休憩前の工程を中断)
        this.testPlanChangeBeforeBreaktime1(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を工程の開始時間から中断)
        this.testPlanChangeBeforeBreaktime2(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を工程の完了時間まで中断)
        this.testPlanChangeBeforeBreaktime3(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を工程の開始から完了時間まで中断)
        this.testPlanChangeBeforeBreaktime4(kanban);

        // 計画変更で中断時間を設定する。(休憩前の工程を休憩の途中まで中断)
        this.testPlanChangeBreaktime1(kanban);
        // 計画変更で中断時間を設定する。(休憩中の中断)
        this.testPlanChangeBreaktime2(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を休憩をまたいで中断)
        this.testPlanChangeBreaktime3(kanban);
        // 計画変更で中断時間を設定する。(休憩中から休憩後の工程を中断)
        this.testPlanChangeBreaktime4(kanban);

        // 計画変更で開始時間を設定する。(休日またぎ)
        this.testPlanChangeBaseTimeHoliday(kanban);

        // 計画変更で中断時間を設定する。(休憩後の工程を中断)(休日またぎ)
        this.testPlanChangeAfterBreaktime1Holiday(kanban);

        // 計画変更で中断時間を設定する。(休憩前の工程を中断)(休日またぎ)
        this.testPlanChangeBeforeBreaktime1Holiday(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を工程の開始時間から中断)(休日またぎ)
        this.testPlanChangeBeforeBreaktime2Holiday(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を工程の完了時間まで中断)(休日またぎ)
        this.testPlanChangeBeforeBreaktime3Holiday(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を工程の開始から完了時間まで中断)(休日またぎ)
        this.testPlanChangeBeforeBreaktime4Holiday(kanban);

        // 計画変更で中断時間を設定する。(休憩前の工程を休憩の途中まで中断)(休日またぎ)
        this.testPlanChangeBreaktime1Holiday(kanban);
        // 計画変更で中断時間を設定する。(休憩中の中断)(休日またぎ)
        this.testPlanChangeBreaktime2Holiday(kanban);
        // 計画変更で中断時間を設定する。(休憩前の工程を休憩をまたいで中断)(休日またぎ)
        this.testPlanChangeBreaktime3Holiday(kanban);
        // 計画変更で中断時間を設定する。(休憩中から休憩後の工程を中断)(休日またぎ)
        this.testPlanChangeBreaktime4Holiday(kanban);
    }

    /**
     * 計画変更で開始時間を設定する。(最終工程の完了時刻が工程順の作業時間枠の終了時間と同じ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBaseTime(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(baseTime);
        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/10 17:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩後の工程を中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeAfterBreaktime1(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 15:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 15:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:30:00",// 工程⑤
                "2019/04/11 09:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime1(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 11:10:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 11:40:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 13:30:00",// 工程③
                "2019/04/10 14:30:00",// 工程④
                "2019/04/10 15:30:00",// 工程⑤
                "2019/04/10 16:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 13:30:00",// 工程②
                "2019/04/10 14:30:00",// 工程③
                "2019/04/10 15:30:00",// 工程④
                "2019/04/10 16:30:00",// 工程⑤
                "2019/04/11 09:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を工程の開始時間から中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime2(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 11:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 11:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 13:30:00",// 工程③
                "2019/04/10 14:30:00",// 工程④
                "2019/04/10 15:30:00",// 工程⑤
                "2019/04/10 16:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 13:30:00",// 工程②
                "2019/04/10 14:30:00",// 工程③
                "2019/04/10 15:30:00",// 工程④
                "2019/04/10 16:30:00",// 工程⑤
                "2019/04/11 09:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を工程の完了時間まで中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime3(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 11:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 12:00:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 13:30:00",// 工程③
                "2019/04/10 14:30:00",// 工程④
                "2019/04/10 15:30:00",// 工程⑤
                "2019/04/10 16:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 13:30:00",// 工程②
                "2019/04/10 14:30:00",// 工程③
                "2019/04/10 15:30:00",// 工程④
                "2019/04/10 16:30:00",// 工程⑤
                "2019/04/11 09:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を工程の開始から完了時間まで中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime4(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 11:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 12:00:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/11 09:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 14:00:00",// 工程②
                "2019/04/10 15:00:00",// 工程③
                "2019/04/10 16:00:00",// 工程④
                "2019/04/10 17:00:00",// 工程⑤
                "2019/04/11 10:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を休憩の途中まで中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime1(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 11:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 12:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 13:30:00",// 工程③
                "2019/04/10 14:30:00",// 工程④
                "2019/04/10 15:30:00",// 工程⑤
                "2019/04/10 16:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 13:30:00",// 工程②
                "2019/04/10 14:30:00",// 工程③
                "2019/04/10 15:30:00",// 工程④
                "2019/04/10 16:30:00",// 工程⑤
                "2019/04/11 09:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩中の中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime2(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 12:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 13:00:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:00:00",// 工程④
                "2019/04/10 15:00:00",// 工程⑤
                "2019/04/10 16:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/10 17:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を休憩をまたいで中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime3(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 11:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 13:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 14:00:00",// 工程③
                "2019/04/10 15:00:00",// 工程④
                "2019/04/10 16:00:00",// 工程⑤
                "2019/04/11 09:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 14:00:00",// 工程②
                "2019/04/10 15:00:00",// 工程③
                "2019/04/10 16:00:00",// 工程④
                "2019/04/10 17:00:00",// 工程⑤
                "2019/04/11 10:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩中から休憩後の工程を中断)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime4(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/10 10:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/10 12:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/10 13:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/10 10:00:00",// 工程①
                "2019/04/10 11:00:00",// 工程②
                "2019/04/10 12:00:00",// 工程③
                "2019/04/10 14:30:00",// 工程④
                "2019/04/10 15:30:00",// 工程⑤
                "2019/04/10 16:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/10 11:00:00",// 工程①
                "2019/04/10 12:00:00",// 工程②
                "2019/04/10 14:30:00",// 工程③
                "2019/04/10 15:30:00",// 工程④
                "2019/04/10 16:30:00",// 工程⑤
                "2019/04/11 09:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で開始時間を設定する。(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBaseTimeHoliday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(baseTime);
        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:00:00",// 工程③
                "2019/04/12 15:00:00",// 工程④
                "2019/04/12 16:00:00",// 工程⑤
                "2019/04/15 09:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 12:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:00:00",// 工程④
                "2019/04/12 17:00:00",// 工程⑤
                "2019/04/15 10:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩後の工程を中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeAfterBreaktime1Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 15:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 15:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:00:00",// 工程③
                "2019/04/12 15:00:00",// 工程④
                "2019/04/12 16:30:00",// 工程⑤
                "2019/04/15 09:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 12:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:30:00",// 工程④
                "2019/04/15 09:30:00",// 工程⑤
                "2019/04/15 10:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime1Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 11:10:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 11:40:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 13:30:00",// 工程②
                "2019/04/12 14:30:00",// 工程③
                "2019/04/12 15:30:00",// 工程④
                "2019/04/12 16:30:00",// 工程⑤
                "2019/04/15 09:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 13:30:00",// 工程①
                "2019/04/12 14:30:00",// 工程②
                "2019/04/12 15:30:00",// 工程③
                "2019/04/12 16:30:00",// 工程④
                "2019/04/15 09:30:00",// 工程⑤
                "2019/04/15 10:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を工程の開始時間から中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime2Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 11:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 11:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 13:30:00",// 工程②
                "2019/04/12 14:30:00",// 工程③
                "2019/04/12 15:30:00",// 工程④
                "2019/04/12 16:30:00",// 工程⑤
                "2019/04/15 09:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 13:30:00",// 工程①
                "2019/04/12 14:30:00",// 工程②
                "2019/04/12 15:30:00",// 工程③
                "2019/04/12 16:30:00",// 工程④
                "2019/04/15 09:30:00",// 工程⑤
                "2019/04/15 10:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を工程の完了時間まで中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime3Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 11:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 12:00:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 13:30:00",// 工程②
                "2019/04/12 14:30:00",// 工程③
                "2019/04/12 15:30:00",// 工程④
                "2019/04/12 16:30:00",// 工程⑤
                "2019/04/15 09:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 13:30:00",// 工程①
                "2019/04/12 14:30:00",// 工程②
                "2019/04/12 15:30:00",// 工程③
                "2019/04/12 16:30:00",// 工程④
                "2019/04/15 09:30:00",// 工程⑤
                "2019/04/15 10:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を工程の開始から完了時間まで中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBeforeBreaktime4Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 11:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 12:00:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:00:00",// 工程④
                "2019/04/15 09:00:00",// 工程⑤
                "2019/04/15 10:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 14:00:00",// 工程①
                "2019/04/12 15:00:00",// 工程②
                "2019/04/12 16:00:00",// 工程③
                "2019/04/12 17:00:00",// 工程④
                "2019/04/15 10:00:00",// 工程⑤
                "2019/04/15 11:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を休憩の途中まで中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime1Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 11:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 12:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 13:30:00",// 工程②
                "2019/04/12 14:30:00",// 工程③
                "2019/04/12 15:30:00",// 工程④
                "2019/04/12 16:30:00",// 工程⑤
                "2019/04/15 09:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 13:30:00",// 工程①
                "2019/04/12 14:30:00",// 工程②
                "2019/04/12 15:30:00",// 工程③
                "2019/04/12 16:30:00",// 工程④
                "2019/04/15 09:30:00",// 工程⑤
                "2019/04/15 10:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩中の中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime2Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 12:00:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 13:00:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:00:00",// 工程③
                "2019/04/12 15:00:00",// 工程④
                "2019/04/12 16:00:00",// 工程⑤
                "2019/04/15 09:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 12:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:00:00",// 工程④
                "2019/04/12 17:00:00",// 工程⑤
                "2019/04/15 10:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩前の工程を休憩をまたいで中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime3Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 11:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 13:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 14:00:00",// 工程②
                "2019/04/12 15:00:00",// 工程③
                "2019/04/12 16:00:00",// 工程④
                "2019/04/15 09:00:00",// 工程⑤
                "2019/04/15 10:00:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 14:00:00",// 工程①
                "2019/04/12 15:00:00",// 工程②
                "2019/04/12 16:00:00",// 工程③
                "2019/04/12 17:00:00",// 工程④
                "2019/04/15 10:00:00",// 工程⑤
                "2019/04/15 11:00:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * 計画変更で中断時間を設定する。(休憩中から休憩後の工程を中断)(休日またぎ)
     *
     * @param kanban
     * @throws Exception
     */
    private void testPlanChangeBreaktime4Holiday(KanbanEntity kanban) throws Exception {
        PlanChangeCondition condition = new PlanChangeCondition();

        Date baseTime = TestUtils.parseDatetime("2019/04/12 11:00:00");

        condition.setStartDatetime(null);
        condition.setInterruptFromTime(TestUtils.parseDatetime("2019/04/12 12:30:00"));
        condition.setInterruptToTime(TestUtils.parseDatetime("2019/04/12 13:30:00"));

        Response response = ServiceTestData.getKanbanRest().updatePlan(condition, Arrays.asList(kanban.getKanbanId()), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        // 計画時間変更後の各工程の開始日時
        List<String> startDates = Arrays.asList(
                "2019/04/12 11:00:00",// 工程①
                "2019/04/12 12:00:00",// 工程②
                "2019/04/12 14:30:00",// 工程③
                "2019/04/12 15:30:00",// 工程④
                "2019/04/12 16:30:00",// 工程⑤
                "2019/04/15 09:30:00");// 工程⑥

        // 計画時間変更後の各工程の完了日時
        List<String> compDates = Arrays.asList(
                "2019/04/12 12:00:00",// 工程①
                "2019/04/12 14:30:00",// 工程②
                "2019/04/12 15:30:00",// 工程③
                "2019/04/12 16:30:00",// 工程④
                "2019/04/15 09:30:00",// 工程⑤
                "2019/04/15 10:30:00");// 工程⑥

        // カンバン・工程カンバンの開始・完了日時をチェックする。
        TestUtils.checkPlanDate(kanban, startDates, compDates, baseTime);
    }

    /**
     * ロット流しカンバンの工程実績登録のテスト(シリアル番号あり)
     * (カンバン登録・工数集計・不良品管理)
     *
     * @throws Exception 
     */
    @Ignore// TODO: 要テストコード修正
    @Test
    public void testLotSerialActualReport() throws Exception {
        System.out.println("testLotSerialActualReport");
        this.testLotActualReport(true);
    }

    /**
     * ロット流しカンバンの工程実績登録のテスト(シリアル番号なし)
     * (カンバン登録・工数集計・不良品管理)
     *
     * @throws Exception 
     */
    @Ignore// TODO: 要テストコード修正
    @Test
    public void testLotActualReport() throws Exception {
        System.out.println("testLotActualReport");
        this.testLotActualReport(false);
    }

    /**
     * ロット流しカンバンの工程実績登録のテスト
     * (カンバン登録・工数集計・不良品管理)
     *
     * @throws Exception 
     */
    private void testLotActualReport(boolean isExistSerial) throws Exception {

        this.createData();

        List<DefectReasonEntity> defectReasons = this.createDefectReasons();

        // 工程を作成する。
        WorkEntity testWork1 = new WorkEntity(0L, "工程①", 1, 60000, null, ContentTypeEnum.STRING, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null, null);
        WorkEntity testWork2 = new WorkEntity(0L, "工程②", 1, 90000, null, ContentTypeEnum.STRING, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null, null);
        WorkEntity testWorkS1 = new WorkEntity(0L, "S工程①", 1, 60000, null, ContentTypeEnum.STRING, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null, null);
        WorkEntity testWorkS2 = new WorkEntity(0L, "S工程②", 1, 90000, null, ContentTypeEnum.STRING, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null, null);


        // トレーサビリティ情報
        List<CheckInfoEntity> checkInfos = new LinkedList();

        for (int i = 1; i <= 3; i++) {
            CheckInfoEntity traceProp = new CheckInfoEntity();
            traceProp.setKey(new StringBuilder("trace_WORK").append(i).append("_name").toString());
            traceProp.setType(CustomPropertyTypeEnum.TYPE_STRING);
            traceProp.setVal(new StringBuilder("trace_WORK").append(i).append("_value").toString());
            traceProp.setDisp(i);
            traceProp.setPage(1);
            traceProp.setCat(WorkPropertyCategoryEnum.WORK);

            checkInfos.add(traceProp);
        }

        // 検査情報一覧をJSON文字列に変換して工程の検査情報にセットする。
        String jsonCheckInfos = JsonUtils.objectsToJson(checkInfos);
        testWork1.setWorkCheckInfo(jsonCheckInfos);

        em.clear();
        tx.begin();
        em.persist(testWork1);
        em.persist(testWork2);
        em.persist(testWorkS1);
        em.persist(testWorkS2);
        tx.commit();

        // 工程順を作成する。
        WorkflowEntity testWorkflow1 = new WorkflowEntity(0L, "工程順①", null, null, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null);
        WorkflowEntity testWorkflow2 = new WorkflowEntity(0L, "H24Gﾍｯﾄﾞ 標準-サブAssy", null, null, organization1.getOrganizationId(), df.parse("2018/07/01 08:00:00"), null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startDatetime1 = sdf.parse("1970-01-01 00:00:00+00");
        Date endDatetime1 = new Date(startDatetime1.getTime() + testWork1.getTaktTime());
        Date startDatetime2 = endDatetime1;
        Date endDatetime2 = new Date(startDatetime2.getTime() + testWork2.getTaktTime());

        ConWorkflowWorkEntity conTestWork1_1 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, testWork1.getWorkId(), false, 10001, startDatetime1, endDatetime1);
        conTestWork1_1.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId()));
        conTestWork1_1.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId(), organization2.getOrganizationId()));

        ConWorkflowWorkEntity conTestWork1_2 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, testWork2.getWorkId(), false, 20001, startDatetime2, endDatetime2);
        conTestWork1_2.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId()));
        conTestWork1_2.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId(), organization2.getOrganizationId()));

        ConWorkflowWorkEntity conTestWork2_1 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, testWorkS1.getWorkId(), false, 10001, startDatetime1, endDatetime1);
        conTestWork2_1.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId()));
        conTestWork2_1.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId(), organization2.getOrganizationId()));

        ConWorkflowWorkEntity conTestWork2_2 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, testWorkS2.getWorkId(), false, 20001, startDatetime2, endDatetime2);
        conTestWork2_2.setEquipmentCollection(Arrays.asList(equip1.getEquipmentId(), equip2.getEquipmentId()));
        conTestWork2_2.setOrganizationCollection(Arrays.asList(organization1.getOrganizationId(), organization2.getOrganizationId()));

        testWorkflow1.setConWorkflowWorkCollection(Arrays.asList(conTestWork1_1, conTestWork1_2));
        testWorkflow2.setConWorkflowWorkCollection(Arrays.asList(conTestWork2_1, conTestWork2_2));

        String diaglam1 = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
                .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
                .append("<process isExecutable=\"true\">")
                .append("<startEvent id=\"start_id\" name=\"start\"/>")
                .append("<endEvent id=\"end_id\" name=\"end\"/>")
                .append("<task id=\"1\" name=\"工程①\"/>")
                .append("<task id=\"2\" name=\"工程②\"/>")
                .append("<sequenceFlow sourceRef=\"start_id\" targetRef=\"1\" id=\"AkTY9U54\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"1\" targetRef=\"2\" id=\"vhyQRtzF\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"2\" targetRef=\"end_id\" id=\"phWmgLdw\" name=\"\"/>")
                .append("</process>")
                .append("</definitions>")
                .toString();

        String diaglam2 = new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
                .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
                .append("<process isExecutable=\"true\">")
                .append("<startEvent id=\"start_id\" name=\"start\"/>")
                .append("<endEvent id=\"end_id\" name=\"end\"/>")
                .append("<task id=\"1\" name=\"S工程①\"/>")
                .append("<task id=\"2\" name=\"S工程②\"/>")
                .append("<sequenceFlow sourceRef=\"start_id\" targetRef=\"1\" id=\"AkTY9U54\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"1\" targetRef=\"2\" id=\"vhyQRtzF\" name=\"\"/>")
                .append("<sequenceFlow sourceRef=\"2\" targetRef=\"end_id\" id=\"phWmgLdw\" name=\"\"/>")
                .append("</process>")
                .append("</definitions>")
                .toString();

        testWorkflow1.setWorkflowDiaglam(diaglam1);
        testWorkflow2.setWorkflowDiaglam(diaglam2);

        em.clear();
        tx.begin();
        workflowRest.add(testWorkflow1, null);
        workflowRest.add(testWorkflow2, null);
        tx.commit();

        // カンバン階層を作成する。
        KanbanHierarchyEntity kanbanHierarchy = new KanbanHierarchyEntity(0L, "カンバン階層①");

        em.clear();
        tx.begin();
        kanbanHierarchyRest.add(kanbanHierarchy, null);
        tx.commit();

        long hierarchyId1 = kanbanHierarchy.getKanbanHierarchyId();

        List<List<String>> snList = new LinkedList();
        snList.add(Arrays.asList("sn-①", "sn-②", "sn-③", "sn-④", "sn-⑤"));
        snList.add(Arrays.asList("sn-⑥"));
        snList.add(Arrays.asList("sn-⑦", "sn-⑧", "sn-⑨"));

        Long lotQuantity = snList.stream()
                .flatMap(p -> p.stream())
                .count();

        // カンバンを作成する。
        KanbanCreateCondition condition = new KanbanCreateCondition(
                "カンバン①",
                testWorkflow1.getWorkflowId(),
                hierarchyId1,
                organization1.getOrganizationIdentify(),
                true,
                lotQuantity.intValue(),
                startDatetime1,
                null,
                2
        );

        em.clear();
        tx.begin();
        Response response = kanbanRest.createKanban(condition, null);
        ResponseEntity res = (ResponseEntity) response.getEntity();
        tx.commit();
        em.clear();
        assertThat(res.isSuccess(), is(true));

        // 作成したカンバンのカンバンIDを取得する。
        int pos = res.getUri().lastIndexOf("/");
        String idStr = res.getUri().substring(pos + 1);

        Long kanbanId = Long.valueOf(idStr);

        KanbanEntity testKanban1 = kanbanRest.find(kanbanId, null);

        String service = "els";

        List<String> porders = new LinkedList();

        List<OrderInfoEntity> orderInfos = new LinkedList();
        for (int i = 0; i < 3; i++) {
            int num = i + 1;

            String porder = String.format("P001-%05d", num);
            porders.add(porder);

            OrderInfoEntity orderInfo = new OrderInfoEntity();
            orderInfo.setPorder(porder);// 注番
            orderInfo.setHinmei(String.format("ﾌﾞﾋﾝ-%03d", num));// 品名
            orderInfo.setKikakuKatasiki("H24Gﾍｯﾄﾞ 標準");// 規格・型式
            orderInfo.setKbumoName("サブAssy");// 部門名
            orderInfo.setSyanaiZuban(String.format("ZN%03d", num));// 図番
            orderInfo.setSyanaiComment("X1000001～X1000100");// 社内コメント
            orderInfo.setKban("10");// 工程番号
            orderInfo.setKvol(100);// 計画指示数
            orderInfo.setLvol(snList.get(i).size());// 指示数
            orderInfo.setDefect(0);// 不良数
            orderInfo.setRem(null);// 残り台数

            if (isExistSerial) {
                orderInfo.setSn(snList.get(i));// シリアル番号
            } else {
                orderInfo.setSn(null);// シリアル番号なし
            }

            orderInfos.add(orderInfo);
        }

        ServiceInfoEntity serviceInfo1 = new ServiceInfoEntity();
        serviceInfo1.setService(service);
        serviceInfo1.setJob(orderInfos);

        testKanban1.setServiceInfo(JsonUtils.objectsToJson(Arrays.asList(serviceInfo1)));

        // カンバンステータスを変更する。
        testKanban1.setKanbanStatus(KanbanStatusEnum.PLANNED);

        em.clear();
        tx.begin();
        response = kanbanRest.update(testKanban1, null);
        res = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(res.isSuccess(), is(true));

        HashMap<String, Long> serialKanbanMap = new LinkedHashMap();
        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎のカンバン情報が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    KanbanEntity kan = kanbanRest.findByName(serialNo, testWorkflow2.getWorkflowName(), testWorkflow2.getWorkflowRev(), null);
                    assertThat(kan.getKanbanId(), is(notNullValue()));
                    assertThat(kan.getKanbanSubname(), is(porders.get(i)));

                    serialKanbanMap.put(serialNo, kan.getKanbanId());
                }
            }
        }

        WorkKanbanEntity workKanban11 = testKanban1.getWorkKanbanCollection().get(0);
        WorkKanbanEntity workKanban12 = testKanban1.getWorkKanbanCollection().get(1);

        long transactionId = 1;
        List<ActualResultEntity> actualResults;
        ActualResultEntity actualResult;

        // 組織① が 設備① で、作業(カンバン① - 工程①)を開始する。
        ActualProductReportEntity actual1 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:01:00"), KanbanStatusEnum.WORKING, null, null);

        // 工程実績を登録する。
        em.clear();
        tx.begin();
        ActualProductReportResult result1 = kanbanRest.report(actual1, false, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result1.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result1.getNextTransactionID(), is(transactionId));

        // 工程実績が登録されている。
        actualResults = actualResultRest.find(Arrays.asList(actual1.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(1));

        actualResult = actualResults.get(0);
        this.checkActualResult(actualResult, actual1);

        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎の実績が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    actualResults = actualResultRest.find(Arrays.asList(serialKanbanMap.get(serialNo)), null, null);
                    assertThat(actualResults.size(), is(1));

                    actualResult = actualResults.get(0);
                    this.checkActualResult(actualResult, actual1);
                }
            }
        }

        // 組織① が 設備① で、作業(カンバン① - 工程①)を中断する。
        ActualProductReportEntity actual2 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:02:00"), KanbanStatusEnum.SUSPEND, null, null);

        // 実績通知の生産実績情報に完成品情報をセットする。
        List<PartsInfoEntity> partsInfos2 = new LinkedList();

        for (int i = 0; i < snList.size(); i++) {
            PartsInfoEntity partsInfo = new PartsInfoEntity();
            partsInfo.setPartsId(porders.get(i));
            partsInfo.setSerialNoInfo(JsonUtils.objectsToJson(snList.get(i)));

            partsInfos2.add(partsInfo);
        }

        actual2.setParts(JsonUtils.objectsToJson(partsInfos2));

        // 実績通知に検査結果をセットする。
        List<ActualProductReportPropertyEntity> props2 = new LinkedList();
        for (CheckInfoEntity checkInfo : checkInfos) {
            ActualProductReportPropertyEntity prop = new ActualProductReportPropertyEntity(
                    checkInfo.getKey(),
                    checkInfo.getType().getResourceKey(),
                    checkInfo.getVal(),
                    checkInfo.getDisp(),
                    null
            );
            props2.add(prop);
        }

        actual2.setPropertyCollection(props2);

        // 工程実績を登録する。
        em.clear();
        tx.begin();
        ActualProductReportResult result2 = kanbanRest.report(actual2, false, null);
        tx.commit();

        // 結果：　作業中断の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result2.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result2.getNextTransactionID(), is(transactionId));

        // 工程実績が登録されている。
        actualResults = actualResultRest.find(Arrays.asList(actual1.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(2));

        // 最後の実績の内容をチェックする。
        actualResult = actualResults.stream()
                .max(Comparator.comparing(entity -> entity.getActualId()))
                .get();
        this.checkActualResult(actualResult, actual2);

        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎の実績が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    actualResults = actualResultRest.find(Arrays.asList(serialKanbanMap.get(serialNo)), null, null);
                    assertThat(actualResults.size(), is(2));

                    // 最後の実績の内容をチェックする。
                    actualResult = actualResults.stream()
                            .max(Comparator.comparing(entity -> entity.getActualId()))
                            .get();
                    this.checkActualResult(actualResult, actual2);
                }
            }
        }

        // 実績通知の生産実績情報に完成品情報をセットしたが、中断なので完成品情報が登録されていない。
        for (int i = 0; i < snList.size(); i++) {
            PartsEntity parts = partsRest.findParts(porders.get(i), null);
            assertThat(parts.getPartsId(), is(nullValue()));
        }

        // 組織① が 設備① で、中断した作業(カンバン① - 工程①)を開始する。(二重作業チェックあり)
        ActualProductReportEntity actual3 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:03:00"), KanbanStatusEnum.WORKING, null, null);

        // 工程実績を登録する。
        em.clear();
        tx.begin();
        ActualProductReportResult result3 = kanbanRest.report(actual3, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result3.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result3.getNextTransactionID(), is(transactionId));

        // 工程実績が登録されている。
        actualResults = actualResultRest.find(Arrays.asList(actual3.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(3));

        // 最後の実績の内容をチェックする。
        actualResult = actualResults.stream()
                .max(Comparator.comparing(entity -> entity.getActualId()))
                .get();
        this.checkActualResult(actualResult, actual3);

        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎の実績が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    actualResults = actualResultRest.find(Arrays.asList(serialKanbanMap.get(serialNo)), null, null);
                    assertThat(actualResults.size(), is(3));

                    // 最後の実績の内容をチェックする。
                    actualResult = actualResults.stream()
                            .max(Comparator.comparing(entity -> entity.getActualId()))
                            .get();
                    this.checkActualResult(actualResult, actual3);
                }
            }
        }

        // 組織① が 設備① で、作業(カンバン① - 工程①)を完了する。
        ActualProductReportEntity actual4 = new ActualProductReportEntity(
                transactionId, workKanban11.getKanbanId(), workKanban11.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2018/06/01 08:04:00"), KanbanStatusEnum.COMPLETION, null, null);

        // 実績通知の生産実績情報に完成品情報をセットする。
        List<PartsInfoEntity> partsInfos4 = new LinkedList();

        for (int i = 0; i < snList.size(); i++) {
            PartsInfoEntity partsInfo = new PartsInfoEntity();
            partsInfo.setPartsId(porders.get(i));
            partsInfo.setSerialNoInfo(JsonUtils.objectsToJson(snList.get(i)));

            partsInfos4.add(partsInfo);
        }

        actual4.setParts(JsonUtils.objectsToJson(partsInfos4));

        // 実績通知に検査結果をセットする。
        List<ActualProductReportPropertyEntity> props4 = new LinkedList();
        for (CheckInfoEntity checkInfo : checkInfos) {
            ActualProductReportPropertyEntity prop = new ActualProductReportPropertyEntity(
                    checkInfo.getKey(),
                    checkInfo.getType().getResourceKey(),
                    checkInfo.getVal(),
                    checkInfo.getDisp(),
                    null
            );
            props4.add(prop);
        }

        actual4.setPropertyCollection(props4);

        // 工程実績を登録する。
        em.clear();
        tx.begin();
        ActualProductReportResult result4 = kanbanRest.report(actual4, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result4.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result4.getNextTransactionID(), is(transactionId));

        // 工程実績が登録されている。
        actualResults = actualResultRest.find(Arrays.asList(actual4.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(4));

        // 最後の実績の内容をチェックする。
        actualResult = actualResults.stream()
                .max(Comparator.comparing(entity -> entity.getActualId()))
                .get();
        this.checkActualResult(actualResult, actual4);

        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎の実績が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    actualResults = actualResultRest.find(Arrays.asList(serialKanbanMap.get(serialNo)), null, null);
                    assertThat(actualResults.size(), is(4));

                    // 最後の実績の内容をチェックする。
                    actualResult = actualResults.stream()
                            .max(Comparator.comparing(entity -> entity.getActualId()))
                            .get();
                    this.checkActualResult(actualResult, actual4);
                }
            }
        }

        // 実績通知の生産実績情報に完成品情報をセットしていたので、完成品情報が登録されている。
        for (int i = 0; i < snList.size(); i++) {
            PartsEntity parts = partsRest.findParts(porders.get(i), null);

            assertThat(parts.getPartsId(), is(partsInfos4.get(i).getPartsId()));
            assertThat(parts.getSerialNoInfo(), is(partsInfos4.get(i).getSerialNoInfo()));
            assertThat(parts.getWorkKanbanId(), is(actual4.getWorkKanbanId()));
            assertThat(parts.getCompDatetime(), is(actual4.getReportDatetime()));
        }

        // 組織② が 設備② で、作業(カンバン① - 工程②)を開始する。
        ActualProductReportEntity actual5 = new ActualProductReportEntity(
                transactionId, workKanban12.getKanbanId(), workKanban12.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:05:00"), KanbanStatusEnum.WORKING, null, null);

        // 工程実績を登録する。
        em.clear();
        tx.begin();
        ActualProductReportResult result5 = kanbanRest.report(actual5, false, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result5.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result5.getNextTransactionID(), is(transactionId));

        // 工程実績が登録されている。
        actualResults = actualResultRest.find(Arrays.asList(actual5.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(5));

        // 最後の実績の内容をチェックする。
        actualResult = actualResults.stream()
                .max(Comparator.comparing(entity -> entity.getActualId()))
                .get();
        this.checkActualResult(actualResult, actual5);

        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎の実績が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    actualResults = actualResultRest.find(Arrays.asList(serialKanbanMap.get(serialNo)), null, null);
                    assertThat(actualResults.size(), is(5));

                    // 最後の実績の内容をチェックする。
                    actualResult = actualResults.stream()
                            .max(Comparator.comparing(entity -> entity.getActualId()))
                            .get();
                    this.checkActualResult(actualResult, actual5);
                }
            }
        }

        // 組織② が 設備② で、作業(カンバン① - 工程②)を完了する。
        ActualProductReportEntity actual6 = new ActualProductReportEntity(
                transactionId, workKanban12.getKanbanId(), workKanban12.getWorkKanbanId(),
                equip2.getEquipmentId(), organization2.getOrganizationId(), df.parse("2018/06/01 08:06:00"), KanbanStatusEnum.COMPLETION, null, null);

        // 実績通知の追加情報に検査結果(使用部品)をセットする。
        List<ActualProductReportPropertyEntity> props6 = new LinkedList();
        ActualProductReportPropertyEntity prop6_1 = new ActualProductReportPropertyEntity(
                new StringBuilder("TAG_")
                        .append(workKanban12.getWorkKanbanOrder())
                        .append("_PARTSID")
                        .toString(),
                CustomPropertyTypeEnum.TYPE_TRACE.name(),
                porders.get(0),
                0,
                null
        );
        props6.add(prop6_1);

        actual6.setPropertyCollection(props6);

        // 実績通知の不良品情報に情報をセットする。
        Map<String, DefectSerialEntity> defSnMap = new HashMap();

        // 注番1
        OrderInfoEntity defectOrder6_1 = orderInfos.get(0);

        DefectReasonEntity defectReason6_1;
        int defectNum6_1;
        List<DefectSerialEntity> defectSerials6_1;

        if (isExistSerial) {
            DefectSerialEntity defectSerial6_1_1 = new DefectSerialEntity();
            defectSerial6_1_1.setSerialNo(defectOrder6_1.getSn().get(3));// sn-④
            defectSerial6_1_1.setDefectReason(defectReasons.get(0));
            defSnMap.put(defectSerial6_1_1.getSerialNo(), defectSerial6_1_1);

            DefectSerialEntity defectSerial6_1_2 = new DefectSerialEntity();
            defectSerial6_1_2.setSerialNo(defectOrder6_1.getSn().get(4));// sn-⑤
            defectSerial6_1_2.setDefectReason(defectReasons.get(1));
            defSnMap.put(defectSerial6_1_2.getSerialNo(), defectSerial6_1_2);

            defectSerials6_1 = new LinkedList();
            defectSerials6_1.add(defectSerial6_1_1);
            defectSerials6_1.add(defectSerial6_1_2);

            defectReason6_1 = null;
            defectNum6_1 = defectSerials6_1.size();
        } else {
            defectReason6_1 = defectReasons.get(0);
            defectNum6_1 = 2;
            defectSerials6_1 = null;
        }

        DefectInfoEntity defectInfo6_1 = new DefectInfoEntity();
        defectInfo6_1.setPorder(defectOrder6_1.getPorder());
        defectInfo6_1.setDefectReason(defectReason6_1);
        defectInfo6_1.setDefectNum(defectNum6_1);
        defectInfo6_1.setDefectSerials(defectSerials6_1);

        // 注番2
        OrderInfoEntity defectOrder6_2 = orderInfos.get(1);

        DefectReasonEntity defectReason6_2;
        int defectNum6_2;
        List<DefectSerialEntity> defectSerials6_2;

        if (isExistSerial) {
            DefectSerialEntity defectSerial6_2_1 = new DefectSerialEntity();
            defectSerial6_2_1.setSerialNo(defectOrder6_2.getSn().get(0));// sn-⑥
            defectSerial6_2_1.setDefectReason(defectReasons.get(2));
            defSnMap.put(defectSerial6_2_1.getSerialNo(), defectSerial6_2_1);

            defectSerials6_2 = new LinkedList();
            defectSerials6_2.add(defectSerial6_2_1);

            defectReason6_2 = null;
            defectNum6_2 = defectSerials6_2.size();
        } else {
            defectReason6_2 = defectReasons.get(2);
            defectNum6_2 = 1;
            defectSerials6_2 = null;
        }

        DefectInfoEntity defectInfo6_2 = new DefectInfoEntity();
        defectInfo6_2.setPorder(defectOrder6_2.getPorder());
        defectInfo6_2.setDefectReason(defectReason6_2);
        defectInfo6_2.setDefectNum(defectNum6_2);
        defectInfo6_2.setDefectSerials(defectSerials6_2);

        List<DefectInfoEntity> defectInfos6 = new LinkedList();
        defectInfos6.add(defectInfo6_1);
        defectInfos6.add(defectInfo6_2);

        actual6.setDefects(JsonUtils.objectsToJson(defectInfos6));

        // 工程実績を登録する。
        em.clear();
        tx.begin();
        ActualProductReportResult result6 = kanbanRest.report(actual6, true, null);
        tx.commit();

        // 結果：　作業開始の実績が登録され成功が返る。(transactionId は進む)
        transactionId++;
        assertThat(result6.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        assertThat(result6.getNextTransactionID(), is(transactionId));

        // 工程実績が登録されている。
        actualResults = actualResultRest.find(Arrays.asList(actual6.getKanbanId()), null, null);
        if (isExistSerial) {
            assertThat(actualResults.size(), is(6));
        } else {
            // シリアル番号なしの場合、不良実績が登録されている。
            assertThat(actualResults.size(), is(6 + defectInfos6.size()));

            List<ActualResultEntity> defectActuals = actualResults.stream()
                    .filter(p -> Objects.equals(p.getActualStatus(), KanbanStatusEnum.DEFECT))
                    .collect(Collectors.toList());

            this.checkDefectActualResult(defectActuals, actual6, defectInfos6);
        }

        // 不良実績を除外して、最後の実績の内容をチェックする。
        actualResult = actualResults.stream()
                .filter(p -> !Objects.equals(p.getActualStatus(), KanbanStatusEnum.DEFECT))
                .max(Comparator.comparing(entity -> entity.getActualId()))
                .get();
        this.checkActualResult(actualResult, actual6);

        if (isExistSerial) {
            // シリアル番号ありのロット流しカンバンなので、シリアル番号毎の実績が登録されている。
            for (int i = 0; i < snList.size(); i++) {
                for (String serialNo : snList.get(i)) {
                    actualResults = actualResultRest.find(Arrays.asList(serialKanbanMap.get(serialNo)), null, null);

                    if (defSnMap.keySet().contains(serialNo)) {
                        // 不良シリアルは不良実績が登録されている。
                        assertThat(actualResults.size(), is(7));

                        actualResult = actualResults.stream()
                                .filter(p -> Objects.equals(p.getActualStatus(), KanbanStatusEnum.DEFECT))
                                .findFirst()
                                .get();
                        this.checkDefectSerialActualResult(actualResult, actual6, defSnMap.get(serialNo));
                    } else {
                        assertThat(actualResults.size(), is(6));
                    }

                    // 不良実績を除外して、最後の実績の内容をチェックする。
                    actualResult = actualResults.stream()
                            .filter(p -> !Objects.equals(p.getActualStatus(), KanbanStatusEnum.DEFECT))
                            .max(Comparator.comparing(entity -> entity.getActualId()))
                            .get();
                    this.checkActualResult(actualResult, actual6);
                }
            }
        }

        // 実績通知の追加情報に検査結果(使用部品)をセットしていたので、完成品情報が削除されている。
        PartsEntity usedParts6 = partsRest.findParts(porders.get(0), null);
        assertThat(usedParts6.getPartsId(), is(porders.get(0)));
        assertThat(usedParts6.getRemoveFlag(), is(true));

        // 未使用部品の完成品情報は削除されていない。
        for (int i = 1; i < snList.size(); i++) {
            PartsEntity parts = partsRest.findParts(porders.get(i), null);

            assertThat(parts.getPartsId(), is(partsInfos4.get(i).getPartsId()));
            assertThat(parts.getSerialNoInfo(), is(partsInfos4.get(i).getSerialNoInfo()));
            assertThat(parts.getWorkKanbanId(), is(actual4.getWorkKanbanId()));
            assertThat(parts.getCompDatetime(), is(actual4.getReportDatetime()));
            assertThat(parts.getRemoveFlag(), is(false));
        }

        // 実績通知に不良品情報をセットしていたので、補充カンバンが登録されている。
        this.checkReplenishmentKanban(testKanban1, orderInfos, defectInfos6);
    }

    /**
     * 工程実績の内容が実績通知と合っているかチェックする。
     *
     * @param actualResult 工程実績
     * @param actual 実績通知
     */
    private void checkActualResult(ActualResultEntity actualResult, ActualProductReportEntity actual) {
        assertThat(actualResult.getActualStatus(), is(actual.getStatus()));
        assertThat(actualResult.getEquipmentId(), is(actual.getEquipmentId()));
        assertThat(actualResult.getOrganizationId(), is(actual.getOrganizationId()));
        assertThat(actualResult.getImplementDatetime(), is(actual.getReportDatetime()));
    }

    /**
     * 工程実績の内容が不良品情報と合っているかチェックする。
     *
     * @param actualResult 不良実績
     * @param actual 実績通知
     * @param defectSerial シリアル番号毎の不良情報
     */
    private void checkDefectSerialActualResult(ActualResultEntity actualResult, ActualProductReportEntity actual, DefectSerialEntity defectSerial) {
        assertThat(actualResult.getActualStatus(), is(KanbanStatusEnum.DEFECT));// ステータス: 不良
        assertThat(actualResult.getEquipmentId(), is(actual.getEquipmentId()));// 設備ID: 実績通知と同じ
        assertThat(actualResult.getOrganizationId(), is(actual.getOrganizationId()));// 組織ID: 実績通知と同じ
        assertThat(actualResult.getImplementDatetime(), is(actual.getReportDatetime()));// 実施日時: 実績通知と同じ

        assertThat(actualResult.getDefectReason(), is(defectSerial.getDefectReason().getDefectValue()));// 不良理由: 不良理由の値
        assertThat(actualResult.getDefectNum(), is(1));// 不良数: 1

        // 不良実績の追加情報のJSON文字列を追加情報一覧に変換する。
        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(actualResult.getActualAddInfo(), AddInfoEntity[].class);

        // シリアル番号毎の不良実績には、追加情報に不良情報なし。
        List<AddInfoEntity> defectAddInfos = addInfos.stream()
                .filter(p -> Objects.equals(p.getType(), KanbanStatusEnum.DEFECT))
                .collect(Collectors.toList());
        assertThat(defectAddInfos.size(), is(0));
    }

    /**
     * 工程実績の内容が不良品情報と合っているかチェックする。
     *
     * @param actualResults 不良実績一覧
     * @param actual 実績通知
     * @param defectInfos 不良品情報一覧
     */
    private void checkDefectActualResult(List<ActualResultEntity> actualResults, ActualProductReportEntity actual, List<DefectInfoEntity> defectInfos) {
        // 注文番号のチェックリスト
        List<String> porders = defectInfos.stream()
                .map(p -> p.getPorder())
                .collect(Collectors.toList());

        for (ActualResultEntity actualResult : actualResults) {
            assertThat(actualResult.getActualStatus(), is(KanbanStatusEnum.DEFECT));// ステータス: 不良
            assertThat(actualResult.getEquipmentId(), is(actual.getEquipmentId()));// 設備ID: 実績通知と同じ
            assertThat(actualResult.getOrganizationId(), is(actual.getOrganizationId()));// 組織ID: 実績通知と同じ
            assertThat(actualResult.getImplementDatetime(), is(actual.getReportDatetime()));// 実施日時: 実績通知と同じ

            // 不良実績の追加情報のJSON文字列を追加情報一覧に変換する。
            List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(actualResult.getActualAddInfo(), AddInfoEntity[].class);

            // 追加情報に不良情報が登録されている。
            List<AddInfoEntity> defectAddInfos = addInfos.stream()
                    .filter(p -> Objects.equals(p.getType(), CustomPropertyTypeEnum.TYPE_DEFECT))
                    .collect(Collectors.toList());
            assertThat(defectAddInfos.size(), is(1));

            AddInfoEntity defectAddInfo = defectAddInfos.get(0);
            assertThat(defectAddInfo.getKey(), is("defect"));// 追加情報のキー

            // 不良品情報(追加情報の値)のJSON文字列を不良品情報に変換する。
            DefectInfoEntity actualDefectInfo = JsonUtils.jsonToObject(defectAddInfo.getVal(), DefectInfoEntity.class);

            // 元の不良品情報一覧から、該当する注文番号の情報を取得する。
            DefectInfoEntity defectInfo = defectInfos.stream()
                    .filter(p -> Objects.equals(p.getPorder(), actualDefectInfo.getPorder()))
                    .findFirst()
                    .get();

            assertThat(actualDefectInfo.getDefectReason(), is(defectInfo.getDefectReason()));// 不良理由
            assertThat(actualDefectInfo.getDefectNum(), is(defectInfo.getDefectNum()));// 不良数
            assertThat(actualDefectInfo.getDefectSerials(), is(nullValue()));// 不良情報一覧

            assertThat(actualResult.getDefectReason(), is(defectInfo.getDefectReason().getDefectValue()));// 不良理由: 不良理由の値
            assertThat(actualResult.getDefectNum(), is(defectInfo.getDefectNum()));// 不良数: 不良情報の不良数合計

            porders.remove(defectInfo.getPorder());// チェックリストから削除する。
        }

        assertThat(porders.size(), is(0));// 全てチェックしたら0件になっている。
    }

    /**
     * 補充カンバンをチェックする。
     *
     * @param baseKanban 元のカンバン
     * @param baseOrderInfos 注番情報一覧
     * @param defectInfos 不良品情報一覧
     * @throws Exception 
     */
    private void checkReplenishmentKanban(KanbanEntity baseKanban, List<OrderInfoEntity> baseOrderInfos, List<DefectInfoEntity> defectInfos) throws Exception {
        // 補充カンバンのカンバン名
        String kanbanName = new StringBuilder(baseKanban.getKanbanName()).append("-1").toString();

        int lotQuantity = defectInfos.stream()
                .mapToInt(p -> p.getDefectNum())
                .sum();

        KanbanEntity kan = kanbanRest.findByName(kanbanName, baseKanban.getWorkflowName(), baseKanban.getWorkflowRev(), null);
        assertThat(kan.getKanbanId(), is(notNullValue()));

        assertThat(kan.getKanbanStatus(), is(KanbanStatusEnum.PLANNED));
        assertThat(kan.getProductionType(), is(2));
        assertThat(kan.getLotQuantity(), is(lotQuantity));

        List<ServiceInfoEntity> serviceInfos = JsonUtils.jsonToObjects(kan.getServiceInfo(), ServiceInfoEntity[].class);
        assertThat(serviceInfos.size(), is(1));

        ServiceInfoEntity serviceInfo = serviceInfos.get(0);
        assertThat(serviceInfo.getService(), is("els"));

        List<LinkedHashMap<String, Object>> list = (List<LinkedHashMap<String, Object>>) serviceInfo.getJob();
        List<OrderInfoEntity> orderInfos = kanbanRest.castOrderInfoList(serviceInfo.getJob());
        for (LinkedHashMap<String, Object> item : list) {
            OrderInfoEntity orderInfo = new OrderInfoEntity(item);
            orderInfos.add(orderInfo);
        }

        for (DefectInfoEntity defectInfo : defectInfos) {
            OrderInfoEntity baseOrderInfo = baseOrderInfos.stream()
                    .filter(p -> Objects.equals(p.getPorder(), defectInfo.getPorder()))
                    .findFirst()
                    .get();

            OrderInfoEntity orderInfo = orderInfos.stream()
                    .filter(p -> Objects.equals(p.getPorder(), defectInfo.getPorder()))
                    .findFirst()
                    .get();

            assertThat(orderInfo.getHinmei(), is(baseOrderInfo.getHinmei()));// 品名: 元カンバンと同じ
            assertThat(orderInfo.getKikakuKatasiki(), is(baseOrderInfo.getKikakuKatasiki()));// 規格・型式: 元カンバンと同じ
            assertThat(orderInfo.getSyanaiZuban(), is(baseOrderInfo.getSyanaiZuban()));// 図番: 元カンバンと同じ
            assertThat(orderInfo.getSyanaiComment(), is(baseOrderInfo.getSyanaiComment()));// 社内コメント: 元カンバンと同じ
            assertThat(orderInfo.getKban(), is(baseOrderInfo.getKban()));// 工程番号: 元カンバンと同じ
            assertThat(orderInfo.getKvol(), is(baseOrderInfo.getKvol()));// 計画指示数: 元カンバンと同じ
            assertThat(orderInfo.getLvol(), is(defectInfo.getDefectNum()));// 指示数: 不良数を設定
            assertThat(orderInfo.getDefect(), is(0));// 不良数: 0
            assertThat(orderInfo.getRem(), is(0));// 残り台数: 0

            orderInfo.getSn();// シリアル番号: 不良品のシリアル番号
        }

        // 元のカンバン・補充カンバンの工程実績一覧を取得する。
        List<ActualResultEntity> baseActuals = actualResultRest.find(Arrays.asList(baseKanban.getKanbanId()), null, null);
        // 補充カンバンの工程実績一覧を取得する。
        List<ActualResultEntity> actuals = actualResultRest.find(Arrays.asList(kan.getKanbanId()), null, null);

        // 作業可能な工程カンバンが存在するかチェックする。
        List<KanbanStatusEnum> workableStatus = Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.SUSPEND);
        boolean isWorkable = kan.getWorkKanbanCollection().stream()
                .anyMatch(p -> p.getImplementFlag() && workableStatus.contains(p.getWorkStatus()));
        assertThat(isWorkable, is(true));

        for (WorkKanbanEntity baseWkan : baseKanban.getWorkKanbanCollection()) {
            // 元の工程カンバンの最後のトレーサビリティ情報が引き継がれているかチェックする。
            if (Objects.nonNull(baseActuals)) {
                ComparatorChain comparator = new ComparatorChain();
                comparator.addComparator(new BeanComparator("implementDatetime", new NullComparator()));
                comparator.addComparator(new BeanComparator("actualId", new NullComparator()));

                // 元の工程カンバンの最後の完了実績を取得する。
                Optional<ActualResultEntity> baseActOpt = baseActuals.stream()
                        .filter(p -> Objects.equals(p.getWorkId(), baseWkan.getWorkId())
                                && Objects.equals(p.getActualStatus(), KanbanStatusEnum.COMPLETION))
                        .sorted(comparator.reversed())
                        .findFirst();

                if (baseActOpt.isPresent()) {
                    ActualResultEntity baseAct = baseActOpt.get();

                    List<ActualResultEntity> workActuals = actuals.stream()
                            .filter(p -> Objects.equals(p.getWorkId(), baseWkan.getWorkId())
                                    && Objects.equals(p.getActualStatus(), KanbanStatusEnum.COMPLETION))
                            .collect(Collectors.toList());

                    assertThat(workActuals.size(), is(1));

                    ActualResultEntity workAct = workActuals.get(0);
                    assertThat(workAct.getWorkflowId(), is(baseAct.getWorkflowId()));
                    assertThat(workAct.getIsSeparateWork(), is(baseAct.getIsSeparateWork()));
                    assertThat(workAct.getWorkId(), is(baseAct.getWorkId()));

                    assertThat(workAct.getEquipmentId(), is(baseAct.getEquipmentId()));
                    assertThat(workAct.getOrganizationId(), is(baseAct.getOrganizationId()));

                    assertThat(workAct.getActualStatus(), is(baseAct.getActualStatus()));
                    assertThat(workAct.getActualAddInfo(), is(baseAct.getActualAddInfo()));
                    assertThat(workAct.getServiceInfo(), is(baseAct.getServiceInfo()));

                    assertThat(workAct.getWorkingTime(), is(0));
                    assertThat(workAct.getInterruptReason(), is(nullValue()));
                    assertThat(workAct.getDelayReason(), is(nullValue()));
                    assertThat(workAct.getDefectReason(), is(nullValue()));
                }
            }
        }
    }

    /**
     * 不良内容一覧を作成する。
     *
     * @return 不良内容一覧
     */
    private List<DefectReasonEntity> createDefectReasons() {
        List<DefectReasonEntity> reasons = new LinkedList();

        DefectReasonEntity reason1 = new DefectReasonEntity();
        reason1.setDefectId(1L);
        reason1.setDefectOrder(1L);
        reason1.setDefectType("A");// 種類
        reason1.setDefectClass("その他");// 分類
        reason1.setDefectValue("不良A1");// 内容
        reasons.add(reason1);

        DefectReasonEntity reason2 = new DefectReasonEntity();
        reason1.setDefectId(2L);
        reason1.setDefectOrder(2L);
        reason1.setDefectType("A");// 種類
        reason1.setDefectClass("その他");// 分類
        reason1.setDefectValue("不良A2");// 内容
        reasons.add(reason2);

        DefectReasonEntity reason3 = new DefectReasonEntity();
        reason1.setDefectId(3L);
        reason1.setDefectOrder(3L);
        reason1.setDefectType("B");// 種類
        reason1.setDefectClass("その他");// 分類
        reason1.setDefectValue("不良B1");// 内容
        reasons.add(reason3);

        return reasons;
    }
    
    /**
     * 応援のテスト
     * 
     * @throws Exception
     * @author s-heya
     */
    @Test
    public void testWorkSupport() throws Exception {
        System.out.println("testReportRework");

        serviceTestData.createTestData();

        // テスト用のカンバンを作成する。
        KanbanEntity kanban = serviceTestData.createTestKanban();

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        OrganizationEntity worker1 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_1, null, null);
        OrganizationEntity worker2 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_2, null, null);
        OrganizationEntity worker3 = ServiceTestData.getOrganizationRest().findByName(ServiceTestData.ORGANIZATION_IDENT_1_3, null, null);

        EquipmentEntity adPro1 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_1, null, null);
        EquipmentEntity adPro2 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_2, null, null);
        EquipmentEntity adPro3 = ServiceTestData.getEquipmentRest().findByName(ServiceTestData.EQUIPMENT_IDENT_1_3, null, null);

        WorkKanbanEntity workKanban1 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_1)).findFirst().get();
        WorkKanbanEntity workKanban2 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_2)).findFirst().get();
        WorkKanbanEntity workKanban3 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_3)).findFirst().get();
        WorkKanbanEntity workKanban4 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_4)).findFirst().get();
        WorkKanbanEntity workKanban5 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_5)).findFirst().get();
        WorkKanbanEntity workKanban6 = kanban.getWorkKanbanCollection().stream().filter(p -> p.getWorkName().equals(ServiceTestData.WORK_NAME_6)).findFirst().get();

        Long transactionId = 1L;
        ActualProductReportResult repResult;
        List<ActualResultEntity> actualResults;

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程①)を開始する。同一作業者端末での応援者フラグ=OFF
        Date workingDate1 = datetimeFormat.parse("2019/04/11 10:00:00");
        repResult = serviceTestData.report(transactionId, workKanban1, adPro1, worker1, workingDate1, KanbanStatusEnum.WORKING, false, true);
        transactionId = repResult.getNextTransactionID();
        workKanban1 = ServiceTestData.getWorkKanbanRest().find(workKanban1.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.WORKING, workingDate1, null, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));
        
        // 作業者② が 作業者端末① で、作業(カンバン① - 工程①)を開始する。同一作業者端末での応援者フラグ=OFF
        repResult = serviceTestData.report(transactionId, workKanban1, adPro1, worker2, workingDate1, KanbanStatusEnum.WORKING, false, true, false, false);
        transactionId = repResult.getNextTransactionID();

        actualResults = actualResultRest.find(Arrays.asList(kanban.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(2));

        // 作業者② が 作業者端末① で、作業(カンバン① - 工程①)を完了する。同一作業者端末での応援者フラグ=ON
        Date compDate1 = datetimeFormat.parse("2019/04/11 11:00:00");
        repResult = serviceTestData.report(transactionId, workKanban1, adPro1, worker2, compDate1, KanbanStatusEnum.SUSPEND, false, true, false, true);
        transactionId = repResult.getNextTransactionID();

        // 作業者① が 作業者端末① で、作業(カンバン① - 工程①)を完了する。同一作業者端末での応援者フラグ=OFF
        repResult = serviceTestData.report(transactionId, workKanban1, adPro1, worker1, compDate1, KanbanStatusEnum.COMPLETION, false, true);
        transactionId = repResult.getNextTransactionID();

        workKanban1 = ServiceTestData.getWorkKanbanRest().find(workKanban1.getWorkKanbanId(), null);
        kanban = ServiceTestData.getKanbanRest().find(kanban.getKanbanId(), null);
        serviceTestData.checkWorkKanban(workKanban1, KanbanStatusEnum.COMPLETION, workingDate1, compDate1, null);
        assertThat(kanban.getKanbanStatus(), is(KanbanStatusEnum.WORKING));
        
        actualResults = actualResultRest.find(Arrays.asList(kanban.getKanbanId()), null, null);
        assertThat(actualResults.size(), is(4));
    }

    /**
     * カンバンIDを指定して、承認情報を追加・更新する。
     * 
     * @throws Exception 
     */
    @Test
    public void testUpdateApproval() throws Exception {
        System.out.println("testUpdateApproval");

        KanbanEntity kanban = new KanbanEntity("testUpdateApproval", null, null, null, null, KanbanStatusEnum.PLANNING);

        tx.begin();
        Response response = kanbanRest.add(kanban, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();
        em.clear();

        // 承認1を追加する。
        ApprovalEntity approval1 = new ApprovalEntity();
        approval1.setOrder(1);
        approval1.setApprove(true);
        approval1.setApprover("富士一郎");
        approval1.setReason(null);

        response = kanbanRest.updateApproval(kanban.getKanbanId(), approval1, kanban.getVerInfo(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        kanban = kanbanRest.findBasicInfo(kanban.getKanbanId());

        List<ApprovalEntity> resultApprovals = JsonUtils.jsonToObjects(kanban.getApproval(), ApprovalEntity[].class);
        assertThat(resultApprovals.size(), is(1));

        assertThat(resultApprovals.get(0), is(approval1));

        // 承認2を追加する。
        ApprovalEntity approval2 = new ApprovalEntity();
        approval2.setOrder(2);
        approval2.setApprove(false);
        approval2.setApprover("富士アドテッ君");
        approval2.setReason("不備があるため");

        response = kanbanRest.updateApproval(kanban.getKanbanId(), approval2, kanban.getVerInfo(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        kanban = kanbanRest.findBasicInfo(kanban.getKanbanId());

        resultApprovals = JsonUtils.jsonToObjects(kanban.getApproval(), ApprovalEntity[].class);
        assertThat(resultApprovals.size(), is(2));

        resultApprovals.sort(Comparator.comparing(item -> item.getOrder()));

        assertThat(resultApprovals.get(0), is(approval1));
        assertThat(resultApprovals.get(1), is(approval2));

        // 承認2を更新する。
        approval2.setApprove(true);
        approval2.setReason(null);

        response = kanbanRest.updateApproval(kanban.getKanbanId(), approval2, kanban.getVerInfo(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));

        kanban = kanbanRest.findBasicInfo(kanban.getKanbanId());

        resultApprovals = JsonUtils.jsonToObjects(kanban.getApproval(), ApprovalEntity[].class);
        assertThat(resultApprovals.size(), is(2));

        resultApprovals.sort(Comparator.comparing(item -> item.getOrder()));

        assertThat(resultApprovals.get(0), is(approval1));
        assertThat(resultApprovals.get(1), is(approval2));

        // 承認2を無効な排他バージョンで更新する。
        ApprovalEntity approval2a = new ApprovalEntity();
        approval2a.setOrder(approval2.getOrder());
        approval2a.setApprove(false);
        approval2a.setApprover("富士二郎");
        approval2a.setReason("無効な更新");

        response = kanbanRest.updateApproval(kanban.getKanbanId(), approval2a, kanban.getVerInfo() - 1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.DIFFERENT_VER_INFO));

        kanban = kanbanRest.findBasicInfo(kanban.getKanbanId());

        resultApprovals = JsonUtils.jsonToObjects(kanban.getApproval(), ApprovalEntity[].class);
        assertThat(resultApprovals.size(), is(2));

        resultApprovals.sort(Comparator.comparing(item -> item.getOrder()));

        assertThat(resultApprovals.get(0), is(approval1));
        assertThat(resultApprovals.get(1), is(approval2));
    }
    
    /**
     * ラベル情報の更新をテストする。
     * 
     * @throws Exception
     * @author s-heya
     */
    @Test
    public void updateLabel() throws Exception {
        System.out.println("updateLabel");

        this.createData();
        
        kanban1 = kanbanRest.find(kanban1.getKanbanId(), null); 
        List<Long> labelIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 20L);

        Response response = kanbanRest.updateLabel(kanban1.getKanbanId(), labelIds, kanban1.getVerInfo(), null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        
        //kanban1 = kanbanRest.find(kanban1.getKanbanId(), null);
        List<Long> afterIds = JsonUtils.jsonToObjects(kanban1.getKanbanLabel(), Long[].class);
        assertThat(labelIds, is(afterIds));
    }
    
    /**
     * 
     * @throws Exception 
     */
    //@Test
    public void createDsKanban1() throws Exception {
        createData();
        
        String bom = " [\n" +
                        "  {\n" +
                        "    \"key\": \"1\",\n" +
                        "    \"ic\": \"457873-0431\",\n" +
                        "    \"in\": \"MOTOR ASSY, STEPPING\",\n" +
                        "    \"qt\": 3,\n" +
                        "    \"pr\": \"\",\n" +
                        "    \"lo\": \"H876\",\n" +
                        "    \"pn\": \"作業者\",\n" +
                        "    \"ct\": \"2024/7/21 14:23\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"key\": \"2\",\n" +
                        "    \"ic\": \"057713-8060\",\n" +
                        "    \"in\": \"CASE, LWR\",\n" +
                        "    \"qt\": 1,\n" +
                        "    \"pr\": \"ラベル\",\n" +
                        "    \"lo\": \"\",\n" +
                        "    \"pn\": null,\n" +
                        "    \"ct\": null\n" +
                        "  }\n" +
                        "]";

        MstDsItem dsItem = new MstDsItem(1, "287700-4902", "H869メータ", "140L HI", "101", "102", bom, workflow1.getWorkflowId(), workflow2.getWorkflowId());

        tx.begin();
        Response response1 = dsItemFacade.add(dsItem, null);
        tx.commit();
       
        DsKanbanCreateCondition condition = new DsKanbanCreateCondition();
        condition.setCategory(1);
        condition.setProductNo("287700-4902");
        condition.setPackageCode("DS");
        condition.setQuantity(3);
        condition.setQrCode("287700-4902");
        
        tx.begin();
        Response response2 = kanbanRest.createDsKanban(condition, organization1.getOrganizationId());
        tx.commit();

        KanbanHierarchyEntity kanbanHierarchy = kanbanHierarchyRest.findHierarchyByName("補給生産", null, null);
        
        KanbanSearchCondition search = new  KanbanSearchCondition();
        search.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
        search.setHierarchyId(kanbanHierarchy.getKanbanHierarchyId());

        List<KanbanEntity> kanbans  = kanbanRest.searchKanban(search, null, null, null);
        assertThat(kanbans.size(), is(1));
        assertThat(kanbans.get(0).getKanbanName(), is(startsWith("287700-4902")));
        assertThat(kanbans.get(0).getServiceInfo(), notNullValue());

        KanbanEntity kanban = kanbanRest.find(kanbans.get(0).getKanbanId());
        assertThat(kanban.getSeparateWorkKanbanCount(), is(4L));
        
        tx.begin();
        Response response = kanbanRest.updateDsProperty(new DsKanbanProperty(1, kanban.getKanbanId(), "DUEDATE", "2024/10/02"), organization2.getOrganizationId());
        tx.commit();

        assertThat(response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertTrue(DateUtils.isSameDay(kanban.getCompDatetime(), DateUtils.parseDate("2024/10/2", "yyyy/MM/dd")));

        tx.begin();
        response = kanbanRest.updateDsProperty(new DsKanbanProperty(2, kanban.getKanbanId(), "NOTE", "部品入荷待ち"), organization2.getOrganizationId());
        tx.commit();

        assertThat(response.getStatus(), is(HttpURLConnection.HTTP_OK));
       
        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), AddInfoEntity[].class);
        assertThat(addInfos, hasItems(hasProperty("val", is("部品入荷待ち"))));

        KanbanEntity inspectKanban = kanbanRest.findByName(kanbans.get(0).getKanbanName(), workflow2.getWorkflowName(), null, null);
        assertThat(inspectKanban.getKanbanId(), notNullValue());

        // 部品集荷の工程情報
        WorkKanbanEntity workKanban = workKanbanRest.findByWorkName(kanban.getKanbanId(), "部品集荷", null);
        assertThat(workKanban.getKanbanId(), notNullValue());

        DsPickup dsPickup = DsPickup.lookup(workKanban.getServiceInfo());
        assertThat(dsPickup.getProductNo(), is("287700-4902"));
        assertThat(dsPickup.getPartsList().size(), is(2));
       
        // 作業開始
        ActualProductReportEntity actual1 = new ActualProductReportEntity(
                0L, kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:00:00"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result1 = kanbanRest.report(actual1, false, null);
        tx.commit();

        kanban = kanbanRest.find(kanbans.get(0).getKanbanId());
        DsKanban dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        DsActual actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), nullValue());
        assertThat(actual.getWorkTime(), nullValue());

        // 作業中断
        ActualProductReportEntity actual2 = new ActualProductReportEntity(
                result1.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:10:00"), KanbanStatusEnum.SUSPEND, null, null);
        
        // 部品集荷情報
        dsPickup.getPartsList().get(0).setCompDateTime("2024/10/07 08:05:00");
        ServiceInfoEntity dsPickupInfo = new ServiceInfoEntity();
        dsPickupInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSPICKUP);
        dsPickupInfo.setJob(dsPickup);
        actual2.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsPickupInfo)));

        tx.begin();
        ActualProductReportResult result2 = kanbanRest.report(actual2, false, null);
        tx.commit();
        
        // 工程情報
        kanban = kanbanRest.find(kanbans.get(0).getKanbanId());
        dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), nullValue());
        assertThat(actual.getWorkTime(), is(600L));

        // 部品集荷情報
        workKanban = workKanbanRest.findByWorkName(kanban.getKanbanId(), "部品集荷", null);
        dsPickup = DsPickup.lookup(workKanban.getServiceInfo());
        assertThat(dsPickup.getPartsList().get(0).getCompDateTime(), is("2024/10/07 08:05:00"));

        // 作業再開
        ActualProductReportEntity actual3 = new ActualProductReportEntity(
                result2.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 09:00:00"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result4 = kanbanRest.report(actual3, false, null);
        tx.commit();

        // 作業完了
        ActualProductReportEntity actual4 = new ActualProductReportEntity(
                result4.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 9:50:00"), KanbanStatusEnum.COMPLETION, null, null);

        dsPickup.getPartsList().get(1).setCompDateTime("2024/10/07 09:15:00");
        dsPickupInfo.setService(ServiceInfoEntity.SERVICE_INFO_DSPICKUP);
        dsPickupInfo.setJob(dsPickup);
        actual4.setServiceInfo(JsonUtils.objectToJson(Arrays.asList(dsPickupInfo)));    

        tx.begin();
        ActualProductReportResult result5 = kanbanRest.report(actual4, false, null);
        tx.commit();
        
        // 工程情報
        dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), is("2024/10/07 09:50"));
        assertThat(actual.getWorkTime(), is(3600L));
        
        // 部品集荷情報
        workKanban = workKanbanRest.findByWorkName(kanban.getKanbanId(), "部品集荷", null);
        dsPickup = DsPickup.lookup(workKanban.getServiceInfo());
        assertThat(dsPickup.getPartsList().get(0).getCompDateTime(), is("2024/10/07 08:05:00"));
        assertThat(dsPickup.getPartsList().get(1).getCompDateTime(), is("2024/10/07 09:15:00"));
    }

    /**
     * 
     * @throws Exception 
     */
    //@Test
    public void createDsKanban2() throws Exception {
        createData();
        
        MstDsItem dsItem = new MstDsItem(2, "287700-4902", "H869メータ", "140L HI", null, null, null, null, workflow2.getWorkflowId());

        tx.begin();
        Response response1 = dsItemFacade.add(dsItem, null);
        tx.commit();
       
        DsKanbanCreateCondition condition = new DsKanbanCreateCondition();
        condition.setCategory(2);
        condition.setProductNo("2877004902");
        condition.setSerial("103");
        condition.setQuantity(3);
        condition.setQrCode("287700-4902");
        
        tx.begin();
        Response response2 = kanbanRest.createDsKanban(condition, organization1.getOrganizationId());
        tx.commit();
        
        KanbanHierarchyEntity kanbanHierarchy = kanbanHierarchyRest.findHierarchyByName("検査", null, null);
        
        KanbanSearchCondition search = new  KanbanSearchCondition();
        search.setKanbanStatusCollection(Arrays.asList(KanbanStatusEnum.PLANNED, KanbanStatusEnum.WORKING, KanbanStatusEnum.SUSPEND));
        search.setHierarchyId(kanbanHierarchy.getKanbanHierarchyId());

        List<KanbanEntity> kanbans  = kanbanRest.searchKanban(search, null, null, null);
        assertThat(kanbans.size(), is(1));
        assertThat(kanbans.get(0).getKanbanName(), is(startsWith("287700-4902")));
        assertThat(kanbans.get(0).getServiceInfo(), notNullValue());
        
        KanbanEntity kanban = kanbanRest.find(kanbans.get(0).getKanbanId());
        assertThat(kanban.getSeparateWorkKanbanCount(), is(4L));

        tx.begin();
        Response response = kanbanRest.updateDsProperty(new DsKanbanProperty(2, kanban.getKanbanId(), "INVENTORYDATE", "2024/9/27"), organization2.getOrganizationId());
        tx.commit();

        assertThat(response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertTrue(DateUtils.isSameDay(kanban.getStartDatetime(), DateUtils.parseDate("2024/9/27", "yyyy/MM/dd")));

        tx.begin();
        response = kanbanRest.updateDsProperty(new DsKanbanProperty(2, kanban.getKanbanId(), "NOTE", "部品入荷待ち"), organization2.getOrganizationId());
        tx.commit();
        assertThat(response.getStatus(), is(HttpURLConnection.HTTP_OK));
        
        tx.begin();
        response = kanbanRest.updateDsProperty(new DsKanbanProperty(2, kanban.getKanbanId(), "EVENT", "1A"), organization2.getOrganizationId());
        tx.commit();
        assertThat(response.getStatus(), is(HttpURLConnection.HTTP_OK));
        
        List<AddInfoEntity> addInfos = JsonUtils.jsonToObjects(kanban.getKanbanAddInfo(), AddInfoEntity[].class);
        
        assertThat(addInfos, hasItems(
            hasProperty("key", is("NOTE")),
            hasProperty("key", is("EVENT"))
        ));

        WorkKanbanEntity workKanban = kanban.getWorkKanbanCollection().get(0);

        // 作業開始
        ActualProductReportEntity actual1 = new ActualProductReportEntity(
                0L, kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:00:00"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result1 = kanbanRest.report(actual1, false, null);
        tx.commit();

        kanban = kanbanRest.find(kanbans.get(0).getKanbanId());
        DsKanban dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        DsActual actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), nullValue());
        assertThat(actual.getWorkTime(), nullValue());

        // 作業完了
        ActualProductReportEntity actual2 = new ActualProductReportEntity(
                result1.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:10:00"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result2 = kanbanRest.report(actual2, false, null);
        tx.commit();
        
        // 工程情報
        dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), nullValue());
        assertThat(actual.getWorkTime(), is(600L));

        workKanban = kanban.getWorkKanbanCollection().get(1);

        // 作業開始
        ActualProductReportEntity actual3 = new ActualProductReportEntity(
                result2.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:15:00"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result3 = kanbanRest.report(actual3, false, null);
        tx.commit();

        // 作業完了
        ActualProductReportEntity actual4 = new ActualProductReportEntity(
                result3.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:30:00"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result4 = kanbanRest.report(actual4, false, null);
        tx.commit();
        
        // 工程情報
        kanban = kanbanRest.find(kanban.getKanbanId());
        dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), nullValue());
        assertThat(actual.getWorkTime(), is(1500L));

        workKanban = kanban.getWorkKanbanCollection().get(2);

        // 作業開始
        ActualProductReportEntity actual5 = new ActualProductReportEntity(
                result4.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:30:00"), KanbanStatusEnum.WORKING, null, null);

        tx.begin();
        ActualProductReportResult result5 = kanbanRest.report(actual5, false, null);
        tx.commit();

        // 作業完了
        ActualProductReportEntity actual6 = new ActualProductReportEntity(
                result5.getNextTransactionID(), kanban.getKanbanId(), workKanban.getWorkKanbanId(),
                equip1.getEquipmentId(), organization1.getOrganizationId(), df.parse("2024/10/07 08:40:00"), KanbanStatusEnum.COMPLETION, null, null);

        tx.begin();
        ActualProductReportResult result6 = kanbanRest.report(actual6, false, null);
        tx.commit();
        
        // 工程情報
        kanban = kanbanRest.find(kanban.getKanbanId());
        dsKanban = DsKanban.lookup(kanban.getServiceInfo());
        actual = dsKanban.getActuals().get(0);
        assertThat(actual.getPersonName(), is(organization1.getOrganizationName()));
        assertThat(actual.getStartDateTime(), is("2024/10/07 08:00"));
        assertThat(actual.getCompDateTime(), is("2024/10/07 08:40"));
        assertThat(actual.getWorkTime(), is(2100L));
    }

    /**
     * 作業コメント機能のテスト
     * 
     * @throws Exception 
     */
    @Test
    public void testWorkComment() throws Exception {
        createData();

        WorkComment comment1 = new WorkComment();
        comment1.setData("コメント1");
        comment1.setDate(df.format(new Date()));
        comment1.setName(organization1.getOrganizationName());
        comment1.setOrgId(organization1.getOrganizationId());
        comment1.setWork("工程1");
        
        WorkComment comment2 = new WorkComment();
        comment2.setData("コメント2");
        comment2.setDate(df.format(new Date()));
        comment2.setName(organization2.getOrganizationName());
        comment2.setOrgId(organization2.getOrganizationId());
        comment2.setWork("工程2");
        
        WorkComment comment3 = new WorkComment();
        comment3.setType(WorkComment.Type.Image);
        comment3.setData("dummy.png");
        comment3.setDate(df.format(new Date()));
        comment3.setName(organization2.getOrganizationName());
        comment3.setOrgId(organization2.getOrganizationId());
        comment3.setWork("工程2");

        tx.begin();
        Response res1 = kanbanRest.addWorkComment(comment1, kanban1.getKanbanId(), organization1.getOrganizationId());
        tx.commit();

        ResponseEntity response1 = (ResponseEntity) res1.getEntity();
        assertThat(response1.getUri(), is("1"));
        
        tx.begin();
        Response res2 = kanbanRest.addWorkComment(comment2, kanban1.getKanbanId(), organization2.getOrganizationId());
        tx.commit();

        ResponseEntity response2 = (ResponseEntity) res2.getEntity();
        assertThat(response2.getUri(), is("2"));

        tx.begin();
        Response res5 = kanbanRest.addWorkComment(comment3, kanban1.getKanbanId(), organization2.getOrganizationId());
        tx.commit();

        ResponseEntity response5 = (ResponseEntity) res5.getEntity();
        assertThat(response5.getUri(), is("3"));

        // 他者が追加した作業コメントを削除しようとした場合、NOT_PERMITTED_EDIT_RESOURCE を返す
        tx.begin();
        Response res3 = kanbanRest.removeWorkComment(kanban1.getKanbanId(), 1L, organization2.getOrganizationId());
        tx.commit();

        ResponseEntity response3 = (ResponseEntity) res3.getEntity();
        assertThat(res3.getStatus(), is(200));
        assertThat(response3.isSuccess(), is(false));
        assertThat(response3.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMITTED_EDIT_RESOURCE));

        tx.begin();
        Response res4 = kanbanRest.removeWorkComment(kanban1.getKanbanId(), 1L, organization1.getOrganizationId());
        tx.commit();

        ResponseEntity response4 = (ResponseEntity) res4.getEntity();
        assertThat(res4.getStatus(), is(200));
        assertThat(response4.isSuccess(), is(true));
    }
}
