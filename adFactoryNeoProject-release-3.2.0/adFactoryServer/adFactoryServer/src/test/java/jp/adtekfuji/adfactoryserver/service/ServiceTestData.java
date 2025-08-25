/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import adtekfuji.utility.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.scene.paint.Color;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.kanban.ProductInfoEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.entity.operation.OperateAppEnum;
import jp.adtekfuji.adFactory.entity.operation.OperationTypeEnum;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.DisposalClassEnum;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.ProductEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.IndirectWorkOperationEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationAddInfoEntity;
import jp.adtekfuji.adfactoryserver.entity.operation.OperationEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowHierarchyEntity;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalFlowModel;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * サービステスト用データ
 *
 * @author nar-nakamura
 */
public class ServiceTestData {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;

    private static OrganizationEntityFacadeREST organizationRest = null;
    private static OperationEntityFacadeREST operationEntityFacadeREST = null;
    private static EquipmentEntityFacadeREST equipmentRest = null;

    private static BreaktimeEntityFacadeREST breaktimeRest = null;
    private static HolidayEntityFacadeREST holidayRest = null;

    private static HierarchyEntityFacadeREST hierarchyRest = null;

    private static WorkEntityFacadeREST workRest = null;
    private static WorkHierarchyEntityFacadeREST workHierarchyRest = null;

    private static WorkflowEntityFacadeREST workflowRest = null;
    private static WorkflowHierarchyEntityFacadeREST workflowHierarchyRest = null;

    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static WorkKanbanWorkingEntityFacadeREST workKanbanWorkingRest = null;

    private static DirectActualEntityFacadeREST directActualEntityFacadeREST = null;
    private static ActualResultEntityFacadeREST actualResultRest = null;

    private static KanbanReportEntityFacadeREST kanbanReportRest = null;
    private static KanbanHierarchyEntityFacadeREST kanbanHierarchyRest = null;
    private static KanbanEntityFacadeREST kanbanRest = null;

    private static EquipmentTypeEntityFacadeREST equipmentTypeRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;
    private static PartsEntityFacadeREST partsRest = null;
    private static DsItemFacade dsItemFacade = null;
    private static RoleEntityFacadeREST roleRest = null;


    private static ApprovalFlowModel approvalFlowModel = null;

    // 親設備
    public static final String EQUIPMENT_IDENT_P0 = "adProduct_P0";
    public static final String EQUIPMENT_IDENT_P1 = "adProduct_P1";
    public static final String EQUIPMENT_IDENT_P2 = "adProduct_P2";
    public static final String EQUIPMENT_IDENT_P3 = "adProduct_P3";
    private static final List<String> parentEquipmentIdents = Arrays.asList(EQUIPMENT_IDENT_P1, EQUIPMENT_IDENT_P2, EQUIPMENT_IDENT_P3);
    // 設備
    public static final String EQUIPMENT_IDENT_1_1 = "adProduct1_1";
    public static final String EQUIPMENT_IDENT_1_2 = "adProduct1_2";
    public static final String EQUIPMENT_IDENT_1_3 = "adProduct1_3";
    public static final String EQUIPMENT_IDENT_2_1 = "adProduct2_1";
    public static final String EQUIPMENT_IDENT_2_2 = "adProduct2_2";
    public static final String EQUIPMENT_IDENT_2_3 = "adProduct2_3";
    public static final String EQUIPMENT_IDENT_3_1 = "adProduct3_1";
    public static final String EQUIPMENT_IDENT_3_2 = "adProduct3_2";
    public static final String EQUIPMENT_IDENT_3_3 = "adProduct3_3";

    // 親組織
    public static final String ORGANIZATION_IDENT_P1 = "worker_P1";
    public static final String ORGANIZATION_IDENT_P2 = "worker_P2";
    public static final String ORGANIZATION_IDENT_P3 = "worker_P3";
    // 組織
    public static final String ORGANIZATION_IDENT_1_1 = "worker1_1";
    public static final String ORGANIZATION_IDENT_1_2 = "worker1_2";
    public static final String ORGANIZATION_IDENT_1_3 = "worker1_3";
    public static final String ORGANIZATION_IDENT_2_1 = "worker2_1";
    public static final String ORGANIZATION_IDENT_2_2 = "worker2_2";
    public static final String ORGANIZATION_IDENT_2_3 = "worker2_3";
    public static final String ORGANIZATION_IDENT_3_1 = "worker3_1";
    public static final String ORGANIZATION_IDENT_3_2 = "worker3_2";
    public static final String ORGANIZATION_IDENT_3_3 = "worker3_3";

    // 工程
    public static final String WORK_NAME_1 = "工程①";
    public static final String WORK_NAME_2 = "工程②";
    public static final String WORK_NAME_3 = "工程③";
    public static final String WORK_NAME_4 = "工程④";
    public static final String WORK_NAME_5 = "工程⑤";
    public static final String WORK_NAME_6 = "工程⑥";
    // 同時作業禁止工程
    public static final String WORK_NAME_SW1 = "工程SW①";
    public static final String WORK_NAME_SW2 = "工程SW②";
    public static final String WORK_NAME_SW3 = "工程SW③";

    // 工程順
    public static final String WORKFLOW_NAME_1 = "工程順①";
    public static final String WORKFLOW_NAME_2 = "工程順②";
    public static final String WORKFLOW_NAME_3 = "工程順③";
    // 同時作業禁止工程の工程順
    public static final String WORKFLOW_NAME_SW1 = "工程順SW①";

    // モデル名
    public static final String MODEL_NAME_1 = "モデル①";

    private final List<BreakTimeInfoEntity> breaktimes = new LinkedList();
    private final List<HolidayEntity> holidays = new LinkedList();

    private final List<EquipmentEntity> equipments = new LinkedList();
    private final List<OrganizationEntity> organizations = new LinkedList();
    private final List<OperationEntity> operationEntities = new LinkedList<>();

    private final List<WorkHierarchyEntity> workHierarchies = new LinkedList();
    private final List<WorkflowHierarchyEntity> workflowHierarchies = new LinkedList();
    private final List<KanbanHierarchyEntity> kanbanHierarchies = new LinkedList();

    private final List<KanbanEntity> kanbans = new LinkedList();
    private final List<WorkEntity> works = new LinkedList();


    public ServiceTestData() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        roleRest = new RoleEntityFacadeREST();
        roleRest.setEntityManager(em);
        
        equipmentTypeRest = new EquipmentTypeEntityFacadeREST();
        equipmentTypeRest.setEntityManager(em);

        equipmentRest = new EquipmentEntityFacadeREST();
        equipmentRest.setEntityManager(em);
        equipmentRest.setEquipmentTypeRest(equipmentTypeRest);
        equipmentRest.setAuthRest(authRest);
        equipmentRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setAuthRest(authRest);
        organizationRest.setEquipmentRest(equipmentRest);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());
        organizationRest.setRoleRest(roleRest);

        breaktimeRest = new BreaktimeEntityFacadeREST();
        breaktimeRest.setEntityManager(em);

        holidayRest = new HolidayEntityFacadeREST();
        holidayRest.setEntityManager(em);

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);

        workHierarchyRest = new WorkHierarchyEntityFacadeREST();
        workHierarchyRest.setHierarchyRest(hierarchyRest);

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);
        workflowRest.setWorkEntityFacadeREST(workRest);
        workflowRest.setHierarchyEntityFacadeREST(hierarchyRest);

        workRest.setWorkflowEntityFacadeREST(workflowRest);

        workflowHierarchyRest = new WorkflowHierarchyEntityFacadeREST();
        workflowHierarchyRest.setHierarchyRest(hierarchyRest);

        workKanbanRest = new WorkKanbanEntityFacadeREST();
        workKanbanRest.setEntityManager(em);
        workKanbanRest.setEquipmentRest(equipmentRest);
        workKanbanRest.setOrganizationRest(organizationRest);

        workKanbanWorkingRest = new WorkKanbanWorkingEntityFacadeREST();
        workKanbanWorkingRest.setEntityManager(em);

        directActualEntityFacadeREST = new DirectActualEntityFacadeREST();
        directActualEntityFacadeREST.setEntityManager(em);

        operationEntityFacadeREST = new OperationEntityFacadeREST();
        operationEntityFacadeREST.setEntityManager(em);

        actualResultRest = new ActualResultEntityFacadeREST();
        actualResultRest.setEntityManager(em);
        actualResultRest.setEquipmentRest(equipmentRest);
        actualResultRest.setOrganizationRest(organizationRest);

        kanbanReportRest = new KanbanReportEntityFacadeREST();
        kanbanReportRest.setEntityManager(em);

        kanbanHierarchyRest = new KanbanHierarchyEntityFacadeREST();
        kanbanHierarchyRest.setEntityManager(em);
        kanbanHierarchyRest.setAuthRest(authRest);
        kanbanHierarchyRest.setKanbanFacadeRest(kanbanRest);
        kanbanHierarchyRest.setWorkflowFacadeRest(workflowRest);

        partsRest = new PartsEntityFacadeREST();
        partsRest.setEntityManager(em);

        kanbanRest = new KanbanEntityFacadeREST();
        kanbanRest.setEntityManager(em);
        kanbanRest.setWorkflowRest(workflowRest);
        kanbanRest.setWorkKandanREST(workKanbanRest);
        kanbanRest.setWorkRest(workRest);
        kanbanRest.setEquipmentRest(equipmentRest);
        kanbanRest.setOrganizationRest(organizationRest);
        kanbanRest.setKanbanHierarchyEntityFacadeRest(kanbanHierarchyRest);
        kanbanRest.setActualResultRest(actualResultRest);
        kanbanRest.setBreaktimeRest(breaktimeRest);
        kanbanRest.setWorkKanbanWorkingRest(workKanbanWorkingRest);
        kanbanRest.setHolidayEntityFacadeREST(holidayRest);
        kanbanRest.setPartsEntityFacadeREST(partsRest);
        kanbanRest.setKanbanReportEntityFacedeREST(kanbanReportRest);
        kanbanRest.setDirectActualEntityFacadeREST(directActualEntityFacadeREST);
        kanbanRest.setOperationEntityFacadeREST(operationEntityFacadeREST);

        workflowRest.setKanbanEntityFacadeREST(kanbanRest);

        approvalFlowModel = new ApprovalFlowModel();
        approvalFlowModel.setEntityManager(em);
        approvalFlowModel.setWorkflowEntityFacadeREST(workflowRest);

        directActualEntityFacadeREST.setEntityManager(em);
        
        dsItemFacade = new DsItemFacade();
        dsItemFacade.setEntityManager(em);
        kanbanRest.setDsItemFacade(dsItemFacade);

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

    /**
     * エンティティマネージャを取得する。
     *
     * @return エンティティマネージャ
     */
    public static EntityManager getEntityManager() {
        return em;
    }

    /**
     * トランザクションを取得する。
     *
     * @return トランザクション
     */
    public static EntityTransaction getTransaction() {
        return tx;
    }

    /**
     * 組織情報RESTを取得する。
     *
     * @return 組織情報REST
     */
    public static OrganizationEntityFacadeREST getOrganizationRest() {
        return organizationRest;
    }

    /**
     * 設備情報RESTを取得する。
     *
     * @return 設備情報REST
     */
    public static EquipmentEntityFacadeREST getEquipmentRest() {
        return equipmentRest;
    }

    /**
     * 休憩時間情報RESTを取得する。
     *
     * @return 休憩時間情報REST
     */
    public static BreaktimeEntityFacadeREST getBreaktimeRest() {
        return breaktimeRest;
    }

    /**
     * 休日情報RESTを取得する。
     *
     * @return 休日情報REST
     */
    public static HolidayEntityFacadeREST getHolidayRest() {
        return holidayRest;
    }

    /**
     * 工程情報RESTを取得する。
     *
     * @return 工程情報REST
     */
    public static WorkEntityFacadeREST getWorkREST() {
        return workRest;
    }

    /**
     * 工程階層情報RESTを取得する。
     *
     * @return 工程階層情報REST
     */
    public static WorkHierarchyEntityFacadeREST getWorkHierarchyRest() {
        return workHierarchyRest;
    }

    /**
     * 工程順情報RESTを取得する。
     *
     * @return 工程順情報REST
     */
    public static WorkflowEntityFacadeREST getWorkflowRest() {
        return workflowRest;
    }

    /**
     * 工程順階層情報RESTを取得する。
     *
     * @return 工程順階層情報REST
     */
    public static WorkflowHierarchyEntityFacadeREST getWorkflowHierarchyRest() {
        return workflowHierarchyRest;
    }

    /**
     * 工程カンバン情報RESTを取得する。
     *
     * @return 工程カンバン情報REST
     */
    public static WorkKanbanEntityFacadeREST getWorkKanbanRest() {
        return workKanbanRest;
    }

    /**
     * 工程カンバン作業情報RESTを取得する。
     *
     * @return 工程カンバン作業情報REST
     */
    public static WorkKanbanWorkingEntityFacadeREST getWorkKanbanWorkingRest() {
        return workKanbanWorkingRest;
    }

    /**
     * 工程実績情報RESTを取得する。
     *
     * @return 工程実績情報REST
     */
    public static ActualResultEntityFacadeREST getActualResultRest() {
        return actualResultRest;
    }

    /**
     * カンバン階層情報RESTを取得する。
     *
     * @return カンバン階層情報REST
     */
    public static KanbanHierarchyEntityFacadeREST getKanbanHierarchyRest() {
        return kanbanHierarchyRest;
    }


    public static DirectActualEntityFacadeREST getDirectActualEntityFacadeREST() { return directActualEntityFacadeREST; }

    /**
     * カンバン情報RESTを取得する。
     *
     * @return カンバン情報REST
     */
    public static KanbanEntityFacadeREST getKanbanRest() {
        return kanbanRest;
    }

    /**
     * 階層情報RESTを取得する。
     *
     * @return 階層情報REST
     */
    public static HierarchyEntityFacadeREST getHierarchyRest() {
        return hierarchyRest;
    }

    /**
     * 完成品情報RESTを取得する。
     *
     * @return 完成品情報REST
     */
    public static PartsEntityFacadeREST getPartsRest() {
        return partsRest;
    }

    /**
     * カンバン帳票情報RESTを取得する。
     *
     * @return カンバン帳票情報REST
     */
    public static KanbanReportEntityFacadeREST getKanbanReportRest() {
        return kanbanReportRest;
    }

    /**
     * 承認フローモデルを取得する。
     *
     * @return 承認フローモデル
     */
    public static ApprovalFlowModel getApprovalFlowModel() {
        return approvalFlowModel;
    }

    /**
     * 休憩時間一覧を取得する。
     *
     * @return 休憩時間一覧
     */
    public List<BreakTimeInfoEntity> getBreaktimes() {
        return breaktimes;
    }

    /**
     * 休日一覧を取得する。
     *
     * @return 休日一覧
     */
    public List<HolidayEntity> getHolidays() {
        return holidays;
    }

    /**
     * ラインの設備IDを取得する。
     *
     * @param index (0 ～ 2)
     * @return ラインの設備ID
     */
    public Long getLineEquipmentId(int index) {
        return equipments.stream().filter(p -> p.getEquipmentIdentify().equals(parentEquipmentIdents.get(index)))
                .findFirst().get()
                .getEquipmentId();
    }

    /**
     * ラインの子設備一覧を取得する。
     *
     * @param index (0 ～ 2)
     * @return ラインの子設備一覧
     */
    public List<EquipmentEntity> getLineEquipments(int index) {
        Long parentId = this.getLineEquipmentId(index);
        return equipments.stream()
                .filter(p -> p.getParentEquipmentId().equals(parentId))
                .collect(Collectors.toList());
    }

    public List<KanbanEntity> getKanbanList(String workflowName, Integer workflowRev) {
        return kanbans.stream()
                .filter(p -> p.getWorkflowName().equals(workflowName) && p.getWorkflowRev().equals(workflowRev))
                .collect(Collectors.toList());
    }

    public List<EquipmentEntity> getEquipments() {
        return equipments;
    }

    public List<OrganizationEntity> getOrganizations() {
        return organizations;
    }

    public List<WorkHierarchyEntity> getWorkHierarchies() {
        return workHierarchies;
    }

    public List<WorkflowHierarchyEntity> getWorkflowHierarchies() {
        return workflowHierarchies;
    }

    public List<KanbanHierarchyEntity> getKanbanHierarchies() {
        return kanbanHierarchies;
    }

    public List<WorkEntity> getWorks() {
        return works;
    }

    /**
     * テスト用のデータを作成する。
     *
     * @throws Exception 
     */
    public void createTestData() throws Exception {
        breaktimes.clear();
        holidays.clear();

        organizations.clear();
        equipments.clear();

        workHierarchies.clear();
        workflowHierarchies.clear();
        kanbanHierarchies.clear();

        kanbans.clear();
        works.clear();
        operationEntities.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // 休憩時間
        BreaktimeEntity breaktime1 = this.createBreaktime("昼休憩", datetimeFormat.parse("1970/01/01 12:00:00"), datetimeFormat.parse("1970/01/01 13:00:00"));
        List<Long> breaktimeIds = Arrays.asList(breaktime1.getBreaktimeId());

        // 休日
        this.createHoliday("土曜日", dateFormat.parse("2019/04/06"));
        this.createHoliday("日曜日", dateFormat.parse("2019/04/07"));
        this.createHoliday("土曜日", dateFormat.parse("2019/04/13"));
        this.createHoliday("日曜日", dateFormat.parse("2019/04/14"));
        this.createHoliday("土曜日", dateFormat.parse("2019/04/20"));
        this.createHoliday("日曜日", dateFormat.parse("2019/04/21"));
        this.createHoliday("土曜日", dateFormat.parse("2019/04/27"));
        this.createHoliday("日曜日", dateFormat.parse("2019/04/28"));

        // 親組織
        OrganizationEntity parentWorker1 = this.createOrganization(0L, "親組織①", ORGANIZATION_IDENT_P1, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity parentWorker2 = this.createOrganization(0L, "親組織②", ORGANIZATION_IDENT_P2, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity parentWorker3 = this.createOrganization(0L, "親組織③", ORGANIZATION_IDENT_P3, AuthorityEnum.WORKER, breaktimeIds);

        // 組織
        OrganizationEntity worker1_1 = this.createOrganization(parentWorker1.getOrganizationId(), "作業者①-1", ORGANIZATION_IDENT_1_1, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker1_2 = this.createOrganization(parentWorker1.getOrganizationId(), "作業者①-2", ORGANIZATION_IDENT_1_2, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker1_3 = this.createOrganization(parentWorker1.getOrganizationId(), "作業者①-3", ORGANIZATION_IDENT_1_3, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker2_1 = this.createOrganization(parentWorker2.getOrganizationId(), "作業者②-1", ORGANIZATION_IDENT_2_1, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker2_2 = this.createOrganization(parentWorker2.getOrganizationId(), "作業者②-2", ORGANIZATION_IDENT_2_2, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker2_3 = this.createOrganization(parentWorker2.getOrganizationId(), "作業者②-3", ORGANIZATION_IDENT_2_3, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker3_1 = this.createOrganization(parentWorker3.getOrganizationId(), "作業者③-1", ORGANIZATION_IDENT_3_1, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker3_2 = this.createOrganization(parentWorker3.getOrganizationId(), "作業者③-2", ORGANIZATION_IDENT_3_2, AuthorityEnum.WORKER, breaktimeIds);
        OrganizationEntity worker3_3 = this.createOrganization(parentWorker3.getOrganizationId(), "作業者③-3", ORGANIZATION_IDENT_3_3, AuthorityEnum.WORKER, breaktimeIds);

        // 親設備
        EquipmentEntity parentEquip0 = this.createEquipment(0L, "親設備0", EQUIPMENT_IDENT_P0, null);
        EquipmentEntity parentEquip1 = this.createEquipment(0L, "親設備①", EQUIPMENT_IDENT_P1, null);
        EquipmentEntity parentEquip2 = this.createEquipment(0L, "親設備②", EQUIPMENT_IDENT_P2, null);
        EquipmentEntity parentEquip3 = this.createEquipment(0L, "親設備③", EQUIPMENT_IDENT_P3, null);

        // 設備
        Long terminalEquipmentTypeId = equipmentRest.getEquipmentType(EquipmentTypeEnum.TERMINAL).getEquipmentTypeId();// 作業者端末の設備種別ID
        EquipmentEntity adPro1_1 = this.createEquipment(parentEquip1.getEquipmentId(), "作業者端末①-1", EQUIPMENT_IDENT_1_1, terminalEquipmentTypeId);
        EquipmentEntity adPro1_2 = this.createEquipment(parentEquip1.getEquipmentId(), "作業者端末①-2", EQUIPMENT_IDENT_1_2, terminalEquipmentTypeId);
        EquipmentEntity adPro1_3 = this.createEquipment(parentEquip1.getEquipmentId(), "作業者端末①-3", EQUIPMENT_IDENT_1_3, terminalEquipmentTypeId);
        EquipmentEntity adPro2_1 = this.createEquipment(parentEquip2.getEquipmentId(), "作業者端末②-1", EQUIPMENT_IDENT_2_1, terminalEquipmentTypeId);
        EquipmentEntity adPro2_2 = this.createEquipment(parentEquip2.getEquipmentId(), "作業者端末②-2", EQUIPMENT_IDENT_2_2, terminalEquipmentTypeId);
        EquipmentEntity adPro2_3 = this.createEquipment(parentEquip2.getEquipmentId(), "作業者端末②-3", EQUIPMENT_IDENT_2_3, terminalEquipmentTypeId);
        EquipmentEntity adPro3_1 = this.createEquipment(parentEquip3.getEquipmentId(), "作業者端末③-1", EQUIPMENT_IDENT_3_1, terminalEquipmentTypeId);
        EquipmentEntity adPro3_2 = this.createEquipment(parentEquip3.getEquipmentId(), "作業者端末③-2", EQUIPMENT_IDENT_3_2, terminalEquipmentTypeId);
        EquipmentEntity adPro3_3 = this.createEquipment(parentEquip3.getEquipmentId(), "作業者端末③-3", EQUIPMENT_IDENT_3_3, terminalEquipmentTypeId);

        // 工程階層
        WorkHierarchyEntity workHierarchy = this.createWorkHierarchy(0L, "工程階層①");

        // 工程順階層
        WorkflowHierarchyEntity workflowHierarchy = this.createWorkflowHierarchy(0L, "工程順階層①");

        // カンバン階層
        KanbanHierarchyEntity kanbanHierarchy = this.createKanbanHierarchy(0L, "カンバン階層①");

        // 操作情報
        IndirectWorkOperationEntity indirectWorkOperationEntity = new IndirectWorkOperationEntity();
        indirectWorkOperationEntity.setDoIndirect(Boolean.TRUE);
        indirectWorkOperationEntity.setIndirectWorkId(1L);
        indirectWorkOperationEntity.setReason("間接作業");
        OperationAddInfoEntity operationAddInfo = new OperationAddInfoEntity();
        operationAddInfo.setIndirectWork(indirectWorkOperationEntity);
        this.createOperation(new Date(), parentEquip1.getEquipmentId(), parentWorker1.getOrganizationId(), OperateAppEnum.ADPRODUCT, OperationTypeEnum.INDIRECT_WORK, operationAddInfo);
    }

    /**
     * テスト用のカンバンを作成する。
     *
     * @return カンバン情報
     * @throws Exception 
     */
    public KanbanEntity createTestKanban() throws Exception {
        OrganizationEntity parentWorker1 = organizations.stream().filter(p -> ORGANIZATION_IDENT_P1.equals(p.getOrganizationIdentify())).findFirst().get();
        EquipmentEntity parentEquip1 = equipments.stream().filter(p -> EQUIPMENT_IDENT_P1.equals(p.getEquipmentIdentify())).findFirst().get();

        OrganizationEntity Worker1 = organizations.stream().filter(p -> ORGANIZATION_IDENT_1_1.equals(p.getOrganizationIdentify())).findFirst().get();

        // 工程階層
        WorkHierarchyEntity workHierarchy = workHierarchies.get(0);
        // 工程順階層
        WorkflowHierarchyEntity workflowHierarchy = workflowHierarchies.get(0);
        // カンバン階層
        KanbanHierarchyEntity kanbanHierarchy = kanbanHierarchies.get(0);

        // 工程
        WorkEntity work1 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_1, 1, true, false);
        WorkEntity work2 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_2, 1, true, false);
        WorkEntity work3 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_3, 1, true, false);
        WorkEntity work4 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_4, 1, true, false);
        WorkEntity work5 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_5, 1, true, false);
        WorkEntity work6 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_6, 1, true, false);

        List<WorkEntity> works1 = Arrays.asList(work1, work2, work3, work4, work5, work6);

        // 工程順
        WorkflowEntity workflow1 = this.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), WORKFLOW_NAME_1, 1, MODEL_NAME_1, works1, parentWorker1.getOrganizationIdentify(), parentEquip1.getEquipmentIdentify());

        // カンバン
        String createrIdentify = Worker1.getOrganizationIdentify();
        return createKanban("カンバン①-1", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
    }

    /**
     * テスト用のカンバンを作成する。
     *
     * @param isCreateProducts 製品情報を登録する？ (true:する, false:しない)
     * @throws Exception 
     */
    public void createTestKanbans(boolean isCreateProducts) throws Exception {
        OrganizationEntity parentWorker1 = organizations.stream().filter(p -> ORGANIZATION_IDENT_P1.equals(p.getOrganizationIdentify())).findFirst().get();
        OrganizationEntity parentWorker2 = organizations.stream().filter(p -> ORGANIZATION_IDENT_P2.equals(p.getOrganizationIdentify())).findFirst().get();
        OrganizationEntity parentWorker3 = organizations.stream().filter(p -> ORGANIZATION_IDENT_P3.equals(p.getOrganizationIdentify())).findFirst().get();

        EquipmentEntity parentEquip1 = equipments.stream().filter(p -> EQUIPMENT_IDENT_P1.equals(p.getEquipmentIdentify())).findFirst().get();
        EquipmentEntity parentEquip2 = equipments.stream().filter(p -> EQUIPMENT_IDENT_P2.equals(p.getEquipmentIdentify())).findFirst().get();
        EquipmentEntity parentEquip3 = equipments.stream().filter(p -> EQUIPMENT_IDENT_P3.equals(p.getEquipmentIdentify())).findFirst().get();

        OrganizationEntity worker1 = organizations.stream().filter(p -> ORGANIZATION_IDENT_1_1.equals(p.getOrganizationIdentify())).findFirst().get();

        // 工程階層
        WorkHierarchyEntity workHierarchy = workHierarchies.get(0);
        // 工程順階層
        WorkflowHierarchyEntity workflowHierarchy = workflowHierarchies.get(0);
        // カンバン階層
        KanbanHierarchyEntity kanbanHierarchy = kanbanHierarchies.get(0);

        // 工程
        WorkEntity work1 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_1, 1, true, false);
        WorkEntity work2 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_2, 1, true, false);
        WorkEntity work3 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_3, 1, true, false);
        WorkEntity work4 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_4, 1, true, false);
        WorkEntity work5 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_5, 1, true, false);
        WorkEntity work6 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_6, 1, true, false);

        List<WorkEntity> works1 = Arrays.asList(work1, work2, work3, work4, work5, work6);

        // 工程順
        WorkflowEntity workflow1 = this.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), WORKFLOW_NAME_1, 1, MODEL_NAME_1, works1, parentWorker1.getOrganizationIdentify(), parentEquip1.getEquipmentIdentify());
        WorkflowEntity workflow2 = this.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), WORKFLOW_NAME_2, 1, MODEL_NAME_1, works1, parentWorker2.getOrganizationIdentify(), parentEquip2.getEquipmentIdentify());
        WorkflowEntity workflow3 = this.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), WORKFLOW_NAME_3, 1, MODEL_NAME_1, works1, parentWorker3.getOrganizationIdentify(), parentEquip3.getEquipmentIdentify());

        // カンバン
        String createrIdentify = worker1.getOrganizationIdentify();
        KanbanEntity kanban1_1 = createKanban("カンバン①-1", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban1_2 = createKanban("カンバン①-2", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban1_3 = createKanban("カンバン①-3", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban1_4 = createKanban("カンバン①-4", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban1_5 = createKanban("カンバン①-5", workflow1.getWorkflowName(), workflow1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban2_1 = createKanban("カンバン②-1", workflow2.getWorkflowName(), workflow2.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban2_2 = createKanban("カンバン②-2", workflow2.getWorkflowName(), workflow2.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban2_3 = createKanban("カンバン②-3", workflow2.getWorkflowName(), workflow2.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban2_4 = createKanban("カンバン②-4", workflow2.getWorkflowName(), workflow2.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban2_5 = createKanban("カンバン②-5", workflow2.getWorkflowName(), workflow2.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban3_1 = createKanban("カンバン③-1", workflow3.getWorkflowName(), workflow3.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban3_2 = createKanban("カンバン③-2", workflow3.getWorkflowName(), workflow3.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban3_3 = createKanban("カンバン③-3", workflow3.getWorkflowName(), workflow3.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban3_4 = createKanban("カンバン③-4", workflow3.getWorkflowName(), workflow3.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
        KanbanEntity kanban3_5 = createKanban("カンバン③-5", workflow3.getWorkflowName(), workflow3.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);

        if (isCreateProducts) {
            // 製品情報を追加するカンバン
            List<KanbanEntity> targetKanbans = Arrays.asList(
                    kanban1_1, kanban1_2, kanban1_3, kanban1_4, kanban1_5,
                    kanban2_1, kanban2_2, kanban2_3, kanban2_4, kanban2_5,
                    kanban3_1, kanban3_2, kanban3_3, kanban3_4, kanban3_5);

            // 製品情報を10件づつ登録する。
            for (KanbanEntity kanban : targetKanbans) {
                List<ProductEntity> products = new LinkedList();
                for (int i = 0; i < 10; i++) {
                    int orderNum = i + 1;
                    String uniqueId = String.format("P%012d%04d", kanban.getKanbanId(), orderNum);
                    ProductEntity product = new ProductEntity(uniqueId, kanban.getKanbanId(), orderNum);
                    products.add(product);
                }
                kanban.setProducts(products);

                tx.begin();
                kanbanRest.update(kanban, null);
                tx.commit();
            }
        }
    }

    /**
     * 同時作業禁止工程テスト用のカンバンを作成する。
     *
     * @return カンバン情報
     * @throws Exception 
     */
    public KanbanEntity createSyncWorkTestKanban() throws Exception {
        OrganizationEntity parentWorker1 = organizations.stream().filter(p -> ORGANIZATION_IDENT_P1.equals(p.getOrganizationIdentify())).findFirst().get();
        EquipmentEntity parentEquip1 = equipments.stream().filter(p -> EQUIPMENT_IDENT_P1.equals(p.getEquipmentIdentify())).findFirst().get();

        OrganizationEntity worker1 = organizations.stream().filter(p -> ORGANIZATION_IDENT_1_1.equals(p.getOrganizationIdentify())).findFirst().get();

        // 工程階層
        WorkHierarchyEntity workHierarchy = workHierarchies.get(0);;
        // 工程順階層
        WorkflowHierarchyEntity workflowHierarchy = workflowHierarchies.get(0);
        // カンバン階層
        KanbanHierarchyEntity kanbanHierarchy = kanbanHierarchies.get(0);

        // 同時作業禁止工程
        WorkEntity workSw1 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_SW1, 1, true, true);
        WorkEntity workSw2 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_SW2, 1, true, true);
        WorkEntity workSw3 = this.createWorkInfo(workHierarchy.getWorkHierarchyId(), WORK_NAME_SW3, 1, true, true);

        List<WorkEntity> worksSw1 = Arrays.asList(workSw1, workSw2, workSw3);

        // 同時作業禁止工程の工程順
        WorkflowEntity workflowSw1 = this.createWorkflowInfo(workflowHierarchy.getWorkflowHierarchyId(), WORKFLOW_NAME_SW1, 1, MODEL_NAME_1, worksSw1, parentWorker1.getOrganizationIdentify(), parentEquip1.getEquipmentIdentify());

        // 同時作業禁止工程のカンバン
        String createrIdentify = worker1.getOrganizationIdentify();
        return createKanban("カンバン①-1", workflowSw1.getWorkflowName(), workflowSw1.getWorkflowRev(), kanbanHierarchy.getHierarchyName(), createrIdentify);
    }

    /**
     * テスト用の組織マスタを作成する。
     * 
     * @return 組織マスタ
     * @throws Exception
     */
    public List<OrganizationEntity> createTestOrganizations() throws Exception {
        tx = em.getTransaction();

        // 親組織
        OrganizationEntity organization0 = new OrganizationEntity(0L, "parson0", "identname0", AuthorityEnum.WORKER, null, null, null, null, null);
        tx.begin();
        organizationRest.add(organization0, null);
        tx.commit();

        // 組織
        OrganizationEntity organization1 = new OrganizationEntity(organization0.getOrganizationId(), "parson1", "10101", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity organization2 = new OrganizationEntity(organization0.getOrganizationId(), "parson2", "10202", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity organization3 = new OrganizationEntity(organization0.getOrganizationId(), "parson3", "10303", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        organizationRest.add(organization1, null);
        organizationRest.add(organization2, null);
        organizationRest.add(organization3, null);
        tx.commit();
        
        return Arrays.asList(organization1, organization2, organization3);
    }

    /**
     * 休憩情報を作成する。
     *
     * @param name
     * @param starttime
     * @param endtime
     * @return
     * @throws Exception 
     */
    private BreaktimeEntity createBreaktime(String name, Date starttime, Date endtime) throws Exception {
        BreaktimeEntity entity = new BreaktimeEntity(name, starttime, endtime);

        tx.begin();
        breaktimeRest.add(entity, null);
        tx.commit();

        BreakTimeInfoEntity breaktime = new BreakTimeInfoEntity(entity.getBreaktimeId(), entity.getBreaktimeName(), entity.getStarttime(), entity.getEndtime());
        breaktimes.add(breaktime);

        return entity;
    }

    /**
     * 休日情報を作成する。
     *
     * @param name 名前
     * @param date 日付
     * @return 休日情報
     */
    private HolidayEntity createHoliday(String name, Date date) throws Exception {
        HolidayEntity entity = new HolidayEntity();
        entity.setHolidayName(name);
        entity.setHolidayDate(date);

        tx.begin();
        Response response = holidayRest.add(entity, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        holidays.add(entity);

        return entity;
    }

    /**
     * 組織情報を作成する。
     *
     * @param parentId 親組織ID
     * @param organizationName 組織名
     * @param organizationIdentify 組織識別名
     * @param authorityType 役割
     * @return 組織情報
     * @throws Exception 
     */
    private OrganizationEntity createOrganization(Long parentId, String organizationName, String organizationIdentify, AuthorityEnum authorityType, List<Long> breaktimeIds) throws Exception {
        OrganizationEntity entity = new OrganizationEntity(parentId, organizationName, organizationIdentify, authorityType, null, null, null, null, null);
        if (Objects.nonNull(breaktimeIds) && !breaktimeIds.isEmpty()) {
            entity.setBreaktimeCollection(breaktimeIds);
        }

        tx.begin();
        Response response = organizationRest.add(entity, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        organizations.add(entity);

        return entity;
    }

    /**
     * 設備情報を作成する。
     *
     * @param parentId 親設備ID
     * @param equipmentName 設備名
     * @param equipmentIdentify 設備管理名
     * @param equipmentTypeId 設備種別ID
     * @return 設備情報
     * @throws Exception 
     */
    private EquipmentEntity createEquipment(Long parentId, String equipmentName, String equipmentIdentify, Long equipmentTypeId) throws Exception {
        EquipmentEntity entity = new EquipmentEntity(parentId, equipmentName, equipmentIdentify, null, null, null);

        tx.begin();
        Response response = equipmentRest.add(entity, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        equipments.add(entity);

        return entity;
    }

    /**
     * 工程階層情報を作成する。
     *
     * @param parentId 親階層ID
     * @param hierarchyName 工程階層名
     * @return 工程階層情報
     * @throws Exception 
     */
    private WorkHierarchyEntity createWorkHierarchy(Long parentId, String hierarchyName) throws Exception {
        WorkHierarchyEntity entity = new WorkHierarchyEntity();
        entity.setHierarchyName(hierarchyName);
        entity.setParentHierarchyId(parentId);

        tx.begin();
        Response response = workHierarchyRest.add(entity, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        workHierarchies.add(entity);

        return entity;
    }

    /**
     * 工程情報を作成する。
     *
     * @param workHierarchyId 工程階層ID
     * @param workName 工程名
     * @param workRev 工程版数
     * @param isTraceability トレーサビリティ情報を作成する？
     * @param isSyncWork
     * @return 工程情報
     */
    public WorkEntity createWorkInfo(Long workHierarchyId, String workName, int workRev, boolean isTraceability, boolean isSyncWork) throws Exception {
        WorkEntity work = new WorkEntity();
        work.setParentId(workHierarchyId);// 工程階層ID
        work.setWorkName(workName);// 工程名
        work.setWorkRev(workRev);// 版数

        // 作業番号 (enableDailyReport=true の場合のみ)
        work.setWorkNumber(null);

        work.setTaktTime(60 * 60 * 1000);
        work.setContent(String.format("%sの作業を実施する。", workName));
        work.setContentType(ContentTypeEnum.STRING);
        Color backColor = Color.web("#FFFFFF");
        Color fontColor = Color.web("#000000");
        work.setBackColor(StringUtils.colorToRGBCode(backColor));
        work.setFontColor(StringUtils.colorToRGBCode(fontColor));
        work.setUpdateDatetime(new Date());
        work.setUseParts(null);

        // プロパティ
//        List<AddInfoEntity> props = new LinkedList();
        // 追加情報
        List<AddInfoEntity> addInfos = new LinkedList();

        int propOrder = 1;
        for (int i = 1; i <= 3; i++) {
            AddInfoEntity prop = new AddInfoEntity();
            prop.setKey(String.format("stringProp%d_name", i));
            prop.setType(CustomPropertyTypeEnum.TYPE_STRING);
            prop.setVal(String.format("stringProp%d_value", i));
            prop.setDisp(propOrder);

            addInfos.add(prop);
            propOrder++;
        }

        if (isSyncWork) {
            // 同時作業禁止
            AddInfoEntity prop = new AddInfoEntity();
            prop.setKey(Constants.DISABLE_SYNC_WORK);
            prop.setType(CustomPropertyTypeEnum.TYPE_STRING);
            prop.setVal(Constants.YES);
            prop.setDisp(propOrder);

            addInfos.add(prop);
            propOrder++;
        }

// TODO: [v2対応] 削除
//        work.setPropertyCollection(props);
        // 追加情報一覧をJSON文字列に変換して工程の追加情報にセットする。
        String jsonAddInfos = JsonUtils.objectsToJson(addInfos);
        work.setWorkAddInfo(jsonAddInfos);

        // トレーサビリティ
        List<CheckInfoEntity> checkInfos = new LinkedList();
        if (isTraceability) {
            List<CheckInfoEntity> traceProps = new LinkedList();

            CheckInfoEntity traceProp1 = new CheckInfoEntity();
            traceProp1.setKey("trace_WORK_name");
            traceProp1.setType(CustomPropertyTypeEnum.TYPE_STRING);
            traceProp1.setVal("trace_WORK_value");
            traceProp1.setDisp(propOrder);
            traceProp1.setPage(1);
            traceProp1.setCat(WorkPropertyCategoryEnum.WORK);

            traceProps.add(traceProp1);
            propOrder++;

            checkInfos.addAll(traceProps);
        }

        // 検査情報一覧をJSON文字列に変換して工程の検査情報にセットする。
        String jsonCheckInfos = JsonUtils.objectsToJson(checkInfos);
        work.setWorkCheckInfo(jsonCheckInfos);

        tx.begin();
        Response response = workRest.add(work, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        works.add(work);

        return work;
    }

    /**
     * 工程順階層情報を作成する。
     *
     * @param parentId 親階層ID
     * @param hierarchyName 工程順階層名
     * @return 工程順階層情報
     * @throws Exception 
     */
    private WorkflowHierarchyEntity createWorkflowHierarchy(Long parentId, String hierarchyName) throws Exception {
        WorkflowHierarchyEntity entity = new WorkflowHierarchyEntity();
        entity.setHierarchyName(hierarchyName);
        entity.setParentHierarchyId(parentId);

        tx.begin();
        Response response = workflowHierarchyRest.add(entity, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        workflowHierarchies.add(entity);

        return entity;
    }

    /**
     * テスト用の工程順情報を作成する。(直列工程のみ)
     *
     * @param workflowHierarchyId 工程順階層ID
     * @param workflowName 工程順名
     * @param workflowRev 工程順版数
     * @param modelName モデル名
     * @param works 工程
     * @param organizationIdentify 組織
     * @param equipmentIdentify 設備
     * @return 工程順情報
     * @throws Exception 
     */
    public WorkflowEntity createWorkflowInfo(Long workflowHierarchyId, String workflowName, int workflowRev, String modelName, List<WorkEntity> works, String organizationIdentify, String equipmentIdentify) throws Exception {
        WorkflowEntity workflow = new WorkflowEntity();
        workflow.setParentId(workflowHierarchyId);// 工程順階層ID
        workflow.setWorkflowName(workflowName);// 工程順名
        workflow.setWorkflowRev(workflowRev);// 版数

        workflow.setModelName(modelName);// モデル名

        // 作業番号 (enableDailyReport=true の場合のみ)
        workflow.setWorkflowNumber(null);

        // 作業時間枠
        SimpleDateFormat workTimeFormat = new SimpleDateFormat("HH:mm");
        workflow.setOpenTime(workTimeFormat.parse("09:00"));
        workflow.setCloseTime(workTimeFormat.parse("17:00"));

        // 作業順序
        workflow.setSchedulePolicy(SchedulePolicyEnum.PriorityParallel);

        // 帳票テンプレートパス
        String ledgerPath = "";
        workflow.setLedgerPath(ledgerPath);

        workflow.setUpdatePersonId(0L);
        workflow.setUpdateDatetime(new Date());

        StringBuilder sb = new StringBuilder()
            .append("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>")
            .append("<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\">")
            .append("<process isExecutable=\"true\">")
            .append("<startEvent id=\"start_id\" name=\"start\"/>")
            .append("<endEvent id=\"end_id\" name=\"end\"/>");

        for (WorkEntity work : works) {
            sb.append("<task id=\"").append(work.getWorkId()).append("\" name=\"").append(work.getWorkName()).append("\"/>");
        }

        sb.append("<sequenceFlow sourceRef=\"start_id\"");

        for (WorkEntity work : works) {
            String ref = String.valueOf(work.getWorkId());
            String id = String.format("id_%d", work.getWorkId());
            sb.append(" targetRef=\"").append(ref).append("\" id=\"").append(id).append("\" name=\"\"/>");
            sb.append("<sequenceFlow sourceRef=\"").append(ref).append("\"");
        }

        sb.append(" targetRef=\"end_id\" id=\"").append("").append("\" name=\"\"/>");
        sb.append("</process>");
        sb.append("</definitions>");

        workflow.setWorkflowDiaglam(sb.toString());

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        OrganizationEntity parentWorker = organizations.stream().filter(p -> p.getOrganizationIdentify().equals(organizationIdentify)).findFirst().get();
        EquipmentEntity parentOrg = equipments.stream().filter(p -> p.getEquipmentIdentify().equals(equipmentIdentify)).findFirst().get();

        Date workStartDate = datetimeFormat.parse("1970/01/01 00:00:00");
        Date workCompDate;

        Calendar cal = Calendar.getInstance();
        cal.setTime(workStartDate);

        List<ConWorkflowWorkEntity> conWorks = new LinkedList();
        int workflowOrder = 1;
        for (WorkEntity work : works) {
            cal.add(Calendar.MILLISECOND, work.getTaktTime());
            workCompDate = cal.getTime();

            ConWorkflowWorkEntity conWork = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, null, work.getWorkId(), false, workflowOrder, workStartDate, workCompDate);
            conWork.setEquipmentCollection(Arrays.asList(parentOrg.getEquipmentId()));
            conWork.setOrganizationCollection(Arrays.asList(parentWorker.getOrganizationId()));
            conWorks.add(conWork);

            workStartDate = workCompDate;
            workflowOrder++;
        }

        workflow.setConWorkflowWorkCollection(conWorks);

        tx.begin();
        Response response = workflowRest.add(workflow, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        return workflow;
    }

    /**
     * カンバン階層情報を作成する。
     *
     * @param parentId 親階層ID
     * @param hierarchyName カンバン階層名
     * @return カンバン階層情報
     * @throws Exception 
     */
    public KanbanHierarchyEntity createKanbanHierarchy(Long parentId, String hierarchyName) throws Exception {
        KanbanHierarchyEntity entity = new KanbanHierarchyEntity();
        entity.setHierarchyName(hierarchyName);
        entity.setParentId(parentId);

        tx.begin();
        Response response = kanbanHierarchyRest.add(entity, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        kanbanHierarchies.add(entity);

        return entity;
    }

    /**
     * カンバン情報を作成する。
     *
     * @param kanbanName カンバン名
     * @param workflowName 工程順名
     * @param workflowRev 工程順の版数
     * @param hierarchyName カンバン階層名
     * @param createrIdentify 作成者の組織識別名
     * @return カンバン情報
     * @throws Exception 
     */
    public KanbanEntity createKanban(String kanbanName, String workflowName, Integer workflowRev, String hierarchyName, String createrIdentify) throws Exception {
        tx.begin();
        Response response = kanbanRest.createKanban(kanbanName, workflowName, hierarchyName, createrIdentify, workflowRev, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        KanbanEntity kanban = kanbanRest.findByName(kanbanName, workflowName, workflowRev, null);
        kanbans.add(kanban);

        return kanban;
    }

    /**
     * 操作エンティティを作成.
     *
     * @param operateDatetime 操作日時
     * @param equipmentId 設備ID
     * @param organizationId 組織ID
     * @param operateApp 操作アプリ
     * @param operationType 操作種別
     * @param addInfo 追加項目
     * @return 操作エンティティ
     */
    public OperationEntity createOperation(Date operateDatetime, Long equipmentId, Long organizationId, OperateAppEnum operateApp, OperationTypeEnum operationType, OperationAddInfoEntity addInfo) {
//        createOperation(Date operateDatetime, Long equipmentId, Long organizationId, OperateAppEnum operateApp, OperationTypeEnum operationType, OperationAddInfoEntity addInfo) {
        OperationEntity entity = new OperationEntity(operateDatetime, equipmentId, organizationId, operateApp, operationType, addInfo);

        tx.begin();
        Response response = operationEntityFacadeREST.add(entity);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();

        operationEntities.add(entity);
        return entity;
    }

    /**
     * 指定したカンバンの工程を全て開始・完了して、カンバンを完了させる。
     *
     * @param kanban カンバン
     * @param startDate 最初の工程の開始日時
     * @param transactionId トランザクションID
     * @return 次のトランザクションID
     * @throws Exception 
     */
    public Long compKanban(KanbanEntity kanban, Date startDate, Long transactionId) throws Exception {
        ActualProductReportResult repResult;

        Calendar workCal = Calendar.getInstance();
        workCal.setTime(startDate);

        Date workingDate = workCal.getTime();

        List<WorkKanbanEntity> workKanbans = kanban.getWorkKanbanCollection();
        Collections.sort(workKanbans, ((a, b) -> a.getWorkKanbanOrder().compareTo(b.getWorkKanbanOrder())));

        for (WorkKanbanEntity workKanban : workKanbans) {
            Long equipmentId = workKanban.getEquipmentCollection().get(0);
            EquipmentEntity adPro = equipments.stream().filter(p -> equipmentId.equals(p.getEquipmentId())).findFirst().get();

            Long organozationId = workKanban.getOrganizationCollection().get(0);
            OrganizationEntity worker = organizations.stream().filter(p -> organozationId.equals(p.getOrganizationId())).findFirst().get();

            // 製品情報
            List<ProductInfoEntity> actualProducts = null;
            if (Objects.nonNull(kanban.getProducts()) && kanban.getProducts().size() > 0) {
                actualProducts = new LinkedList();
                for (ProductEntity product : kanban.getProducts()) {
                    ProductInfoEntity actualProduct = new ProductInfoEntity(product.getProductId(), product.getUniqueId(), product.getFkKanbanId(), product.getCompDatetime(), DisposalClassEnum.A.toString(), product.getDefectType(), product.getOrderNum());
                    actualProducts.add(actualProduct);
                }
            }

            // 開始
            repResult = this.report(transactionId, workKanban, adPro, worker, workingDate, KanbanStatusEnum.WORKING, false, true, null);
            transactionId = repResult.getNextTransactionID();

            // 完了
            workCal.add(Calendar.MINUTE, 1);
            workingDate = workCal.getTime();
            repResult = this.report(transactionId, workKanban, adPro, worker, workingDate, KanbanStatusEnum.COMPLETION, false, true, actualProducts);
            transactionId = repResult.getNextTransactionID();

            workCal.add(Calendar.MINUTE, 1);
            workingDate = workCal.getTime();
        }

        return transactionId;
    }

    /**
     * 指定した工程カンバンを開始・完了する。
     *
     * @param kanban カンバン
     * @param workKanban 工程カンバン
     * @param startDate 開始日時
     * @param transactionId トランザクションID
     * @param adPro 作業者端末
     * @return 次のトランザクションID
     * @throws Exception 
     */
    public Long compWorkKanban(KanbanEntity kanban, WorkKanbanEntity workKanban, Date startDate, Long transactionId, EquipmentEntity adPro) throws Exception {
        ActualProductReportResult repResult;

        Calendar workCal = Calendar.getInstance();
        workCal.setTime(startDate);

        Date workingDate = workCal.getTime();

        Long organozationId = workKanban.getOrganizationCollection().get(0);
        OrganizationEntity worker = organizations.stream().filter(p -> organozationId.equals(p.getOrganizationId())).findFirst().get();

        // 製品情報
        List<ProductInfoEntity> actualProducts = null;
        if (Objects.nonNull(kanban.getProducts()) && kanban.getProducts().size() > 0) {
            actualProducts = new LinkedList();
            for (ProductEntity product : kanban.getProducts()) {
                ProductInfoEntity actualProduct = new ProductInfoEntity(product.getProductId(), product.getUniqueId(), product.getFkKanbanId(), product.getCompDatetime(), DisposalClassEnum.A.toString(), product.getDefectType(), product.getOrderNum());
                actualProducts.add(actualProduct);
            }
        }

        // 開始
        repResult = this.report(transactionId, workKanban, adPro, worker, workingDate, KanbanStatusEnum.WORKING, false, true, null);
        transactionId = repResult.getNextTransactionID();

        // 完了
        workCal.add(Calendar.MINUTE, 1);
        workingDate = workCal.getTime();
        repResult = this.report(transactionId, workKanban, adPro, worker, workingDate, KanbanStatusEnum.COMPLETION, false, true, actualProducts);
        transactionId = repResult.getNextTransactionID();

        return transactionId;
    }

    /**
     * 実績登録と結果チェックを行なう。
     *
     * @param transactionId トランザクションID
     * @param workKanban 工程カンバン
     * @param adPro 作業者端末(設備)
     * @param worker 作業者(組織)
     * @param workingDate 実施日時
     * @param workStatus 工程カンバンステータス
     * @param isRework やり直し作業？ (true:やり直し作業, false:通常作業)
     * @param isCheck 結果をチェックする？
     * @return 処理後のトランザクションID
     */
    public ActualProductReportResult report(Long transactionId, WorkKanbanEntity workKanban, EquipmentEntity adPro, OrganizationEntity worker, Date workingDate, KanbanStatusEnum workStatus, boolean isRework, boolean isCheck) {
        return this.report(transactionId, workKanban, adPro, worker, workingDate, workStatus, isRework, isCheck, null);
    }

    /**
     * 実績登録と結果チェックを行なう。
     *
     * @param transactionId トランザクションID
     * @param workKanban 工程カンバン
     * @param adPro 作業者端末(設備)
     * @param worker 作業者(組織)
     * @param workingDate 実施日時
     * @param workStatus 工程カンバンステータス
     * @param isRework やり直し作業？ (true:やり直し作業, false:通常作業)
     * @param isCheck 結果をチェックする？
     * @param products 製品情報
     * @return 処理後のトランザクションID
     */
    public ActualProductReportResult report(Long transactionId, WorkKanbanEntity workKanban, EquipmentEntity adPro, OrganizationEntity worker, Date workingDate, KanbanStatusEnum workStatus, boolean isRework, boolean isCheck, List<ProductInfoEntity> products) {
        ActualProductReportEntity actual = new ActualProductReportEntity(
                transactionId, workKanban.getKanbanId(), workKanban.getWorkKanbanId(),
                adPro.getEquipmentId(), worker.getOrganizationId(), workingDate, workStatus, null, null);
        actual.setRework(isRework);
        actual.setProducts(products);

        tx.begin();
        ActualProductReportResult actualResult = kanbanRest.report(actual, false, null);
        tx.commit();

        // 結果: 作業開始の実績が登録され成功が返る。(transactionId は進む)
        if (isCheck) {
            assertThat(actualResult.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        }
        transactionId++;
        assertThat(actualResult.getNextTransactionID(), is(transactionId));

        return actualResult;
    }
    
    /**
     * 2019/12/12 １行程のみのやり直し対応
     * 実績登録と結果チェックを行なう。
     *
     * @param transactionId トランザクションID
     * @param workKanban 工程カンバン
     * @param adPro 作業者端末(設備)
     * @param worker 作業者(組織)
     * @param workingDate 実施日時
     * @param workStatus 工程カンバンステータス
     * @param isRework やり直し作業？ (true:やり直し作業, false:通常作業)
     * @param isCheck 結果をチェックする？
     * @param isLaterRework 後続のやり直し作業要否 (true:やり直す, false:やり直さない)
     * @return 処理後のトランザクションID
     */
    public ActualProductReportResult report(Long transactionId, WorkKanbanEntity workKanban, EquipmentEntity adPro, OrganizationEntity worker, Date workingDate, KanbanStatusEnum workStatus, boolean isRework, boolean isCheck, boolean isLaterRework) {
        return this.report(transactionId, workKanban, adPro, worker, workingDate, workStatus, isRework, isCheck, isLaterRework, false);
    }

    /**
     * 実績登録と結果チェックを行なう。
     *
     * @param transactionId トランザクションID
     * @param workKanban 工程カンバン
     * @param adPro 作業者端末(設備)
     * @param worker 作業者(組織)
     * @param workingDate 実施日時
     * @param workStatus 工程カンバンステータス
     * @param isRework やり直し作業？ (true:やり直し作業, false:通常作業)
     * @param isCheck 結果をチェックする？
     * @param isLaterRework 後続のやり直し作業要否 (true:やり直す, false:やり直さない)
     * @param isWorkSupport 同一作業者端末での応援者フラグ (true:同一作業者端末での応援、false:同一作業者端末での応援以外)
     * @return 処理後のトランザクションID
     */
    public ActualProductReportResult report(Long transactionId, WorkKanbanEntity workKanban, EquipmentEntity adPro, OrganizationEntity worker, Date workingDate, KanbanStatusEnum workStatus, boolean isRework, boolean isCheck, boolean isLaterRework, boolean isWorkSupport) {
        ActualProductReportEntity actual = new ActualProductReportEntity(
                transactionId, workKanban.getKanbanId(), workKanban.getWorkKanbanId(),
                adPro.getEquipmentId(), worker.getOrganizationId(), workingDate, workStatus, null, null);
        actual.setRework(isRework);
        actual.setProducts(null);
        actual.setLaterRework(isLaterRework);
        actual.setWorkSupport(isWorkSupport);

        tx.begin();
        ActualProductReportResult actualResult = kanbanRest.report(actual, false, null);
        tx.commit();

        // 結果: 作業開始の実績が登録され成功が返る。(transactionId は進む)
        if (isCheck) {
            assertThat(actualResult.getResultType(), is(ServerErrorTypeEnum.SUCCESS));
        }
        transactionId++;
        assertThat(actualResult.getNextTransactionID(), is(transactionId));

        return actualResult;
    }

    /**
     * 工程カンバンの内容をチェックする。
     *
     * @param workKanban 工程カンバン
     * @param status 工程カンバンステータス
     * @param actualStartDate 開始実績日時
     * @param actualCompDate 完了実績日時
     * @param reworkNum やり直し回数
     */
    public void checkWorkKanban(WorkKanbanEntity workKanban, KanbanStatusEnum status, Date actualStartDate, Date actualCompDate, Integer reworkNum) {
        assertThat(workKanban.getWorkStatus(), is(status));
        if (Objects.isNull(actualStartDate)) {
            assertThat(workKanban.getActualStartTime(), is(nullValue()));
        } else {
            assertThat(workKanban.getActualStartTime(), is(actualStartDate));
        }
        if (Objects.isNull(actualStartDate)) {
            assertThat(workKanban.getActualCompTime(), is(nullValue()));
        } else {
            assertThat(workKanban.getActualCompTime(), is(actualCompDate));
        }
        if (Objects.isNull(reworkNum)) {
            assertThat(workKanban.getReworkNum(), is(nullValue()));
        } else {
            assertThat(workKanban.getReworkNum(), is(reworkNum));
        }
    }

    /**
     * 工程の標準作業時間を変更する。
     *
     * ※．工程数と標準作業時間の件数は合わせること。
     *
     * @param workflow 工程順
     * @param standardStartTimes 標準開始時間一覧
     * @param standardEndTimes 標準終了時間一覧
     * @return
     * @throws Exception 
     */
    public WorkflowEntity updateConWorkflowWork(WorkflowEntity workflow, List<Date> standardStartTimes, List<Date> standardEndTimes) throws Exception {
        List<ConWorkflowWorkEntity> conWorks = workflow.getConWorkflowWorkCollection();
        for (int i = 0; i < conWorks.size(); i++) {
            ConWorkflowWorkEntity conWork = conWorks.get(i);
            conWork.setStandardStartTime(standardStartTimes.get(i));
            conWork.setStandardEndTime(standardEndTimes.get(i));
        }
        return workflow;
    }

    /**
     * テスト用 品番マスタ情報RESTを取得する。
     * 
     * @return 
     */
    public static DsItemFacade getDsItemFacade() {
        return dsItemFacade;
    }
    
    
}
