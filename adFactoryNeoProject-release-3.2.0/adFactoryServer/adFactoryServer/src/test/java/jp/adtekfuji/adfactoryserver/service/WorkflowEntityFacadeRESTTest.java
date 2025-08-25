/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adfactoryserver.common.Constants;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.response.ResponseWorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.model.approval.ApprovalModel;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.TestUtils;
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
public class WorkflowEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static ApprovalModel approvalModel = null;

    public WorkflowEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        approvalModel = new ApprovalModel();
        approvalModel.setEntityManager(em);

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);
        workRest.setApprovalModel(approvalModel);

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);
        workflowRest.setWorkEntityFacadeREST(workRest);
        workflowRest.setHierarchyEntityFacadeREST(hierarchyRest);
        workflowRest.setApprovalModel(approvalModel);

        // 承認機能ライセンス有効
        TestUtils.setOptionLicense(LicenseOptionType.ApprovalOption, true);
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

    @Test
    public void testWorkflowEntityFacadeREST() throws Exception {
        System.out.println("testWorkflowEntityFacadeREST");

        //組織の用意
        OrganizationEntity person1 = new OrganizationEntity(0L, "namePerson1", "identPerson1", null, null, null, null, null, null);
        OrganizationEntity person2 = new OrganizationEntity(0L, "namePerson2", "identPerson2", null, null, null, null, null, null);

        //設備の用意
        EquipmentEntity equip1 = new EquipmentEntity(0L, "nameEquip1", "identEquip1", null, null, null);
        EquipmentEntity equip2 = new EquipmentEntity(0L, "nameEquip2", "identEquip2", null, null, null);

        //工程の用意
        WorkEntity work1 = new WorkEntity(0L, "work1", 1, 60000, null, ContentTypeEnum.STRING, null, null, null, null);
        WorkEntity work2 = new WorkEntity(0L, "work2", 1, 90000, null, ContentTypeEnum.STRING, null, null, null, null);
        WorkEntity work3 = new WorkEntity(0L, "work3", 1, 120000, null, ContentTypeEnum.STRING, null, null, null, null);
        WorkEntity work4 = new WorkEntity(0L, "work4", 1, 150000, null, ContentTypeEnum.STRING, null, null, null, null);
        WorkEntity workEx1 = new WorkEntity(0L, "workEx1", 1, 60000, null, ContentTypeEnum.STRING, null, null, null, null);
        WorkEntity workEx2 = new WorkEntity(0L, "workEx2", 1, 120000, null, ContentTypeEnum.STRING, null, null, null, null);

        // 申請状態: 最終承認済
        work1.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
        work2.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
        work3.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
        work4.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
        workEx1.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);
        workEx2.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);

        tx.begin();
        em.persist(person1);
        em.persist(person2);
        em.persist(equip1);
        em.persist(equip2);
        em.persist(work1);
        em.persist(work2);
        em.persist(work3);
        em.persist(work4);
        em.persist(workEx1);
        em.persist(workEx2);
        tx.commit();

        //工程順
        WorkflowEntity workflow1 = new WorkflowEntity(0L, "workflow", "rev1", null, 0L, null, null);

        AddInfoEntity addInfo1 = new AddInfoEntity("propName1", CustomPropertyTypeEnum.TYPE_STRING, "propValue1", 1, null);
        AddInfoEntity addInfo2 = new AddInfoEntity("propName2", CustomPropertyTypeEnum.TYPE_STRING, "propValue2", 2, null);
        AddInfoEntity addInfo3 = new AddInfoEntity("propName3", CustomPropertyTypeEnum.TYPE_STRING, "propValue3", 3, null);
        List<AddInfoEntity> addInfos = new LinkedList();
        addInfos.addAll(Arrays.asList(addInfo1, addInfo2, addInfo3));

        // 追加情報一覧をJSON文字列に変換して工程順の追加情報にセットする。
        String jsonProps = JsonUtils.objectsToJson(addInfos);
        workflow1.setWorkflowAddInfo(jsonProps);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDatetime1 = sdf.parse("1970-01-01 00:00:00+00");
        Date endDatetime1 = new Date(startDatetime1.getTime() + work1.getTaktTime());
        Date startDatetime2 = endDatetime1;
        Date endDatetime2 = new Date(startDatetime2.getTime() + work2.getTaktTime());
        Date startDatetime3 = endDatetime2;
        Date endDatetime3 = new Date(startDatetime3.getTime() + work3.getTaktTime());
        Date startDatetime4 = endDatetime3;
        Date endDatetime4 = new Date(startDatetime4.getTime() + work4.getTaktTime());

        Date startDatetimeEx1 = endDatetime4;
        Date endDatetimeEx1 = new Date(startDatetimeEx1.getTime() + workEx1.getTaktTime());
        Date startDatetimeEx2 = endDatetimeEx1;
        Date endDatetimeEx2 = new Date(startDatetimeEx2.getTime() + workEx2.getTaktTime());

        ConWorkflowWorkEntity conWork1 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, work1.getWorkId(), false, 10001, startDatetime1, endDatetime1);
        ConWorkflowWorkEntity conWork2 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, work2.getWorkId(), false, 20001, startDatetime2, endDatetime2);
        ConWorkflowWorkEntity conWork3 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, work3.getWorkId(), false, 20002, startDatetime3, endDatetime3);
        ConWorkflowWorkEntity conWork4 = new ConWorkflowWorkEntity(WorkKbnEnum.BASE_WORK, 0L, work4.getWorkId(), false, 30001, startDatetime4, endDatetime4);
        workflow1.setConWorkflowWorkCollection(Arrays.asList(conWork1, conWork2, conWork3, conWork4));

        ConWorkflowWorkEntity conWorkEx1 = new ConWorkflowWorkEntity(WorkKbnEnum.ADDITIONAL_WORK, 0L, workEx1.getWorkId(), false, 1, startDatetimeEx1, endDatetimeEx1);
        ConWorkflowWorkEntity conWorkEx2 = new ConWorkflowWorkEntity(WorkKbnEnum.ADDITIONAL_WORK, 0L, workEx2.getWorkId(), false, 2, startDatetimeEx2, endDatetimeEx2);
        workflow1.setConWorkflowSeparateworkCollection(Arrays.asList(conWorkEx1, conWorkEx2));

        workflow1.setWorkflowDiaglam("<?xml version=\"1.0\" encoding=\"Shift_JIS\" standalone=\"yes\"?>"
                + "<definitions targetNamespace=\"http://www.adtek-fuji.co.jp/adfactory\"><process isExecutable=\"true\">"
                + "<startEvent id=\"start_id\" name=\"start\"/>"
                + "<endEvent id=\"end_id\" name=\"end\"/>"
                + "<task id=\"1\" name=\"work1\"/>"
                + "<task id=\"2\" name=\"work2\"/>"
                + "<task id=\"3\" name=\"work3\"/>"
                + "<task id=\"4\" name=\"work4\"/>"
                + "<parallelGateway pair=\"parallelId_2\" id=\"parallelId_1\" name=\"parallelId_1\"/>"
                + "<parallelGateway pair=\"parallelId_1\" id=\"parallelId_2\" name=\"parallelId_2\"/>"
                + "<sequenceFlow sourceRef=\"4\" targetRef=\"parallelId_2\" id=\"VJkr0R7Z\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"1\" targetRef=\"parallelId_1\" id=\"TadehUtK\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"3\" targetRef=\"end_id\" id=\"y7zMPaZt\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"parallelId_1\" targetRef=\"2\" id=\"veLoR9wE\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"start_id\" targetRef=\"1\" id=\"9mOWZC32\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"parallelId_1\" targetRef=\"4\" id=\"10xHrI7V\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"2\" targetRef=\"parallelId_2\" id=\"7S0ToqoF\" name=\"\"/>"
                + "<sequenceFlow sourceRef=\"parallelId_2\" targetRef=\"3\" id=\"mf2Zl7tP\" name=\"\"/>"
                + "</process></definitions>");

        tx.begin();
        workflowRest.add(workflow1, null);
        tx.commit();

        tx.begin();
        work2.setTaktTime(80000);
        em.persist(work1);
        tx.commit();

        tx.begin();
        // 作業時間の再計算
        List<WorkflowEntity> workflows = workflowRest.updateTime(work2, true, null);
        tx.commit();

        // 申請状態: 最終承認済
        workflow1.setApprovalState(ApprovalStatusEnum.FINAL_APPROVE);

        tx.begin();
        workflowRest.update(workflow1, null);
        tx.commit();
        em.clear();

        assertThat(workflows.size(), is(1));
        WorkflowEntity workflow = workflows.get(0);
        assertThat(workflow.getConWorkflowWorkCollection().size(), is(4));

        // 工程順の改訂テスト
        this.testRevise();
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        WorkflowEntity workflow1 = new WorkflowEntity(0L, "workflow", "rev1", null, 0L, null, null);
        tx.begin();
        response = workflowRest.add(workflow1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        WorkflowEntity workflow2 = new WorkflowEntity(0L, "workflow", "rev1", null, 0L, null, null);
        tx.begin();
        response = workflowRest.add(workflow2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        tx.begin();
        response = workflowRest.remove(workflow1.getWorkflowId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

    /**
     * 工程順の改訂テスト
     *
     * @throws Exception 
     */
    private void testRevise() throws Exception {
        System.out.println("revise");

        Long loginUserId = null;

        WorkflowEntity targetWorkflow = workflowRest.findByName("workflow", 1, true, loginUserId, null);

        int workflowRev = targetWorkflow.getLatestRev();
        int testNum = 11;// 改訂テスト回数 (最後の1回前で最大値の登録を、最後に最大値オーバーのテストを実施するので、3以上を設定する)
        for (int i = 0; i < (testNum + 1); i++) {
            if (i == (testNum - 1)) {
                // 最後の1回前は、版数の最大値の登録確認のため、1小さい版数の工程順を登録
                workflowRev = Constants.WORKFLOW_REV_LIMIT - 1;
                WorkflowEntity workflowLast = new WorkflowEntity(targetWorkflow);
                workflowLast.setWorkflowRev(workflowRev);
                tx.begin();
                workflowRest.add(workflowLast, null);
                tx.commit();
            }

            workflowRev++;

            tx.begin();
            Response res = workflowRest.revise(targetWorkflow.getWorkflowId(), null);
            tx.commit();
            ResponseWorkflowEntity resWf = (ResponseWorkflowEntity) res.getEntity();
            if (workflowRev < 1000) {
                // 版数の最大値までのチェック
                assertThat(resWf.isSuccess(), is(true));

                WorkflowEntity newWorkflow = resWf.getValue();

                // 工程順名
                assertThat(newWorkflow.getWorkflowName(), is(targetWorkflow.getWorkflowName()));
                // 版数
                assertThat(newWorkflow.getWorkflowRev(), is(workflowRev));
                // 工程順ダイアグラム
                assertThat(newWorkflow.getWorkflowDiaglam(), is(targetWorkflow.getWorkflowDiaglam()));
                // 帳票テンプレートパス
                assertThat(newWorkflow.getLedgerPath(), is(targetWorkflow.getLedgerPath()));
                // 作業番号
                assertThat(newWorkflow.getWorkflowNumber(), is(targetWorkflow.getWorkflowNumber()));

                // 工程順の追加情報のJSON文字列を追加情報一覧に変換する。
                List<AddInfoEntity> targetProps = JsonUtils.jsonToObjects(targetWorkflow.getWorkflowAddInfo(), AddInfoEntity[].class);
                List<AddInfoEntity> newProps = JsonUtils.jsonToObjects(newWorkflow.getWorkflowAddInfo(), AddInfoEntity[].class);

                assertThat(newProps.size(), is(targetProps.size()));
                for (AddInfoEntity prop : newProps) {
                    Optional<AddInfoEntity> opt = targetProps.stream().filter(p -> p.getKey().equals(prop.getKey())).findFirst();
                    assertThat(opt.isPresent(), is(true));

                    AddInfoEntity targetProp = opt.get();
                    assertThat(prop.getDisp(), is(targetProp.getDisp()));
                    assertThat(prop.getType(), is(targetProp.getType()));
                    assertThat(prop.getVal(), is(targetProp.getVal()));
                }

                // 工程の関連付け情報一覧
                assertThat(newWorkflow.getConWorkflowWorkCollection().size(), is(targetWorkflow.getConWorkflowWorkCollection().size()));
                for (ConWorkflowWorkEntity work : newWorkflow.getConWorkflowWorkCollection()) {
                    Optional<ConWorkflowWorkEntity> opt = targetWorkflow.getConWorkflowWorkCollection().stream().filter(p -> p.getWorkId().equals(work.getWorkId())).findFirst();
                    assertThat(opt.isPresent(), is(true));

                    ConWorkflowWorkEntity targetWork = opt.get();
                    assertThat(work.getWorkflowOrder(), is(targetWork.getWorkflowOrder()));
                    assertThat(work.getSkipFlag(), is(targetWork.getSkipFlag()));
                    assertThat(work.getWorkName(), is(targetWork.getWorkName()));
                    assertThat(work.getStandardStartTime(), is(targetWork.getStandardStartTime()));
                    assertThat(work.getStandardEndTime(), is(targetWork.getStandardEndTime()));
                    assertThat(work.getEquipmentCollection(), is(targetWork.getEquipmentCollection()));
                    assertThat(work.getOrganizationCollection(), is(targetWork.getOrganizationCollection()));
                }

                // 追加工程の関連付け情報一覧
                assertThat(newWorkflow.getConWorkflowSeparateworkCollection().size(), is(targetWorkflow.getConWorkflowSeparateworkCollection().size()));
                for (ConWorkflowWorkEntity work : newWorkflow.getConWorkflowSeparateworkCollection()) {
                    Optional<ConWorkflowWorkEntity> opt = targetWorkflow.getConWorkflowSeparateworkCollection().stream().filter(p -> p.getWorkId().equals(work.getWorkId())).findFirst();
                    assertThat(opt.isPresent(), is(true));

                    ConWorkflowWorkEntity targetWork = opt.get();
                    assertThat(work.getWorkflowOrder(), is(targetWork.getWorkflowOrder()));
                    assertThat(work.getSkipFlag(), is(targetWork.getSkipFlag()));
                    assertThat(work.getWorkName(), is(targetWork.getWorkName()));
                    assertThat(work.getStandardStartTime(), is(targetWork.getStandardStartTime()));
                    assertThat(work.getStandardEndTime(), is(targetWork.getStandardEndTime()));
                    assertThat(work.getEquipmentCollection(), is(targetWork.getEquipmentCollection()));
                    assertThat(work.getOrganizationCollection(), is(targetWork.getOrganizationCollection()));
                }
            } else {
                // 版数の最大値オーバーのチェック
                assertThat(resWf.isSuccess(), is(false));
                assertThat(resWf.getErrorType(), is(ServerErrorTypeEnum.OVER_MAX_VALUE));
            }
        }
    }
}
