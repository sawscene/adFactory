/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model.approval;

import adtekfuji.utility.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.scene.paint.Color;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.CheckInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalDataTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.HierarchyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkKbnEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalFlowEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalOrderEntity;
import jp.adtekfuji.adfactoryserver.entity.approval.ApprovalRouteEntity;
import jp.adtekfuji.adfactoryserver.entity.master.HierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.master.RoleAuthorityEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.ConWorkflowWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.service.*;
import jp.adtekfuji.adfactoryserver.utility.JsonUtils;
import jp.adtekfuji.adfactoryserver.utility.TestUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 *
 * @author nar-nakamura
 */
public class ApprovalFlowModelTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static ApprovalFlowModel model = null;

    private static ApprovalModel approvalModel = null;
    private static RoleEntityFacadeREST roleRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;

    public ApprovalFlowModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        approvalModel = new ApprovalModel();
        approvalModel.setEntityManager(em);

        roleRest = new RoleEntityFacadeREST();
        roleRest.setEntityManager(em);

        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setRoleRest(roleRest);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);
        workRest.setApprovalModel(approvalModel);

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);
        workflowRest.setApprovalModel(approvalModel);
        workflowRest.setWorkEntityFacadeREST(workRest);
        workflowRest.setHierarchyEntityFacadeREST(hierarchyRest);

        model = new ApprovalFlowModel();
        model.setEntityManager(em);
        model.setApprovalModel(approvalModel);
        model.setWorkflowEntityFacadeREST(workflowRest);

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

    /**
     * 承認ルートの登録・更新・削除
     *
     * @throws Exception 
     */
    @Test
    public void testApprovalRoute() throws Exception {
        System.out.println("testApprovalRoute");

        String routeName1 = "承認ルート①";
        ApprovalRouteEntity route1 = new ApprovalRouteEntity();
        route1.setRouteName(routeName1);

        String routeName2 = "承認ルート②";
        ApprovalRouteEntity route2 = new ApprovalRouteEntity();
        route2.setRouteName(routeName2);

        String routeName3 = "承認ルート③";
        ApprovalRouteEntity route3 = new ApprovalRouteEntity();
        route3.setRouteName(routeName3);

        List<ApprovalRouteEntity> routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);

        ResponseEntity response;

        // add
        tx.begin();

        response = model.addApprovalRoute(route1, null);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUriId(), is(not(0)));

        response = model.addApprovalRoute(route2, null);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUriId(), is(not(0)));

        response = model.addApprovalRoute(route3, null);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUriId(), is(not(0)));

        tx.commit();
        em.clear();

        long count = model.countAllApprovalRoute(null);
        assertThat(count, is(3L));

        ApprovalRouteEntity retRoute1 = model.findApprovalRoute(route1.getRouteId(), null);
        assertThat(retRoute1, is(route1));
        assertThat(retRoute1.getRouteName(), is(route1.getRouteName()));
        assertThat(retRoute1.getApprovalOrders(), hasSize(0));

        ApprovalRouteEntity retRoute2 = model.findApprovalRoute(route2.getRouteId(), null);
        assertThat(retRoute2, is(route2));
        assertThat(retRoute2.getRouteName(), is(route2.getRouteName()));
        assertThat(retRoute2.getApprovalOrders(), hasSize(0));

        ApprovalRouteEntity retRoute3 = model.findApprovalRoute(route3.getRouteId(), null);
        assertThat(retRoute3, is(route3));
        assertThat(retRoute3.getRouteName(), is(route3.getRouteName()));
        assertThat(retRoute3.getApprovalOrders(), hasSize(0));

        // add(名前重複)
        ApprovalRouteEntity errrorRoute = new ApprovalRouteEntity();
        errrorRoute.setRouteName(routeName1);

        tx.begin();
        response = model.addApprovalRoute(errrorRoute, null);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
        tx.commit();
        em.clear();

        // update
        List<ApprovalOrderEntity> routeOrders1 = new ArrayList();
        for (int i = 1; i <= 5; i++) {
            ApprovalOrderEntity approvalOrder = new ApprovalOrderEntity();
            approvalOrder.setRouteId(route1.getRouteId());
            approvalOrder.setApprovalOrder(i);
            approvalOrder.setOrganizationId(1000L + i);

            if (i < 5) {
                approvalOrder.setApprovalFinal(false);
            } else {
                approvalOrder.setApprovalFinal(true);
            }

            routeOrders1.add(approvalOrder);
        }

        route1.setRouteName(routeName1 + "a");
        route1.setApprovalOrders(routeOrders1);

        tx.begin();
        response = model.updateApprovalRoute(route1, null);
        assertThat(response.isSuccess(), is(true));
        tx.commit();
        em.clear();

        retRoute1 = model.findApprovalRoute(route1.getRouteId(), null);
        assertThat(retRoute1, is(route1));
        assertThat(retRoute1.getRouteName(), is(route1.getRouteName()));
        assertThat(retRoute1.getApprovalOrders(), hasSize(route1.getApprovalOrders().size()));

        retRoute2 = model.findApprovalRoute(route2.getRouteId(), null);
        assertThat(retRoute2, is(route2));
        assertThat(retRoute2.getRouteName(), is(route2.getRouteName()));
        assertThat(retRoute2.getApprovalOrders(), hasSize(0));

        retRoute3 = model.findApprovalRoute(route3.getRouteId(), null);
        assertThat(retRoute3, is(route3));
        assertThat(retRoute3.getRouteName(), is(route3.getRouteName()));
        assertThat(retRoute3.getApprovalOrders(), hasSize(0));

        // 2回目update(排他用バージョン違い)
        tx.begin();
        response = model.updateApprovalRoute(route1, null);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getErrorType(), is(ServerErrorTypeEnum.DIFFERENT_VER_INFO));
        tx.commit();
        em.clear();

        // 2回目update(OK)
        routes.remove(route1);

        route1 = retRoute1;
        routes.add(route1);

        routeOrders1.clear();
        for (int i = 1; i <= 10; i++) {
            ApprovalOrderEntity approvalOrder = new ApprovalOrderEntity();
            approvalOrder.setRouteId(route1.getRouteId());
            approvalOrder.setApprovalOrder(i);
            approvalOrder.setOrganizationId(1100L + i);

            if (i < 10) {
                approvalOrder.setApprovalFinal(false);
            } else {
                approvalOrder.setApprovalFinal(true);
            }

            routeOrders1.add(approvalOrder);
        }

        route1.setRouteName(routeName1 + "b");
        route1.setApprovalOrders(routeOrders1);

        tx.begin();
        response = model.updateApprovalRoute(route1, null);
        assertThat(response.isSuccess(), is(true));
        tx.commit();
        em.clear();

        retRoute1 = model.findApprovalRoute(route1.getRouteId(), null);
        assertThat(retRoute1, is(route1));
        assertThat(retRoute1.getRouteName(), is(route1.getRouteName()));
        assertThat(retRoute1.getApprovalOrders(), hasSize(route1.getApprovalOrders().size()));

        retRoute2 = model.findApprovalRoute(route2.getRouteId(), null);
        assertThat(retRoute2, is(route2));
        assertThat(retRoute2.getRouteName(), is(route2.getRouteName()));
        assertThat(retRoute2.getApprovalOrders(), hasSize(0));

        retRoute3 = model.findApprovalRoute(route3.getRouteId(), null);
        assertThat(retRoute3, is(route3));
        assertThat(retRoute3.getRouteName(), is(route3.getRouteName()));
        assertThat(retRoute3.getApprovalOrders(), hasSize(0));

        // copy
        tx.begin();
        response = model.copyApprovalRoute(route1.getRouteId(), null);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUriId(), is(not(0)));
        tx.commit();
        em.clear();

        ApprovalRouteEntity copyRoute1 = model.findApprovalRoute(response.getUriId(), null);
        assertThat(copyRoute1.getRouteName(), is(route1.getRouteName() + "-copy"));

        List<ApprovalRouteEntity> retRoutes = model.findApprovalRouteRange(null, null, null);
        assertThat(retRoutes.size(), is(4));

        ApprovalRouteEntity route4 = null;
        for (ApprovalRouteEntity retRoute : retRoutes) {
            Optional<ApprovalRouteEntity> opt = routes.stream()
                    .filter(p -> Objects.equals(p.getRouteId(), retRoute.getRouteId()))
                    .findFirst();
            if (opt.isPresent()) {
                ApprovalRouteEntity route = opt.get();
                assertThat(retRoute.getRouteName(), is(route.getRouteName()));
                if (Objects.isNull(route.getApprovalOrders())) {
                    assertThat(retRoute.getApprovalOrders().size(), is(0));
                } else {
                    assertThat(retRoute.getApprovalOrders().size(), is(route.getApprovalOrders().size()));
                }
            } else {
                assertThat(retRoute.getRouteName(), is(route1.getRouteName() + "-copy"));
                assertThat(retRoute.getApprovalOrders(), hasSize(route1.getApprovalOrders().size()));
                route4 = retRoute;
            }
        }
        assertThat(route4, notNullValue());
        routes.add(route4);

        // 2回目copy
        tx.begin();
        response = model.copyApprovalRoute(route1.getRouteId(), null);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUriId(), is(not(0)));
        tx.commit();
        em.clear();

        ApprovalRouteEntity copyRoute2 = model.findApprovalRoute(response.getUriId(), null);
        assertThat(copyRoute2.getRouteName(), is(route1.getRouteName() + "-copy-copy"));

        retRoutes = model.findApprovalRouteRange(null, null, null);
        assertThat(retRoutes.size(), is(5));

        ApprovalRouteEntity route5 = null;
        for (ApprovalRouteEntity retRoute : retRoutes) {
            Optional<ApprovalRouteEntity> opt = routes.stream()
                    .filter(p -> Objects.equals(p.getRouteId(), retRoute.getRouteId()))
                    .findFirst();
            if (opt.isPresent()) {
                ApprovalRouteEntity route = opt.get();
                assertThat(retRoute.getRouteName(), is(route.getRouteName()));
                if (Objects.isNull(route.getApprovalOrders())) {
                    assertThat(retRoute.getApprovalOrders().size(), is(0));
                } else {
                    assertThat(retRoute.getApprovalOrders(), hasSize(route.getApprovalOrders().size()));
                }
            } else {
                assertThat(retRoute.getRouteName(), is(route1.getRouteName() + "-copy-copy"));
                assertThat(retRoute.getApprovalOrders(), hasSize(route1.getApprovalOrders().size()));
                route5 = retRoute;
            }
        }
        assertThat(route5, notNullValue());
        routes.add(route5);

        // delete
        for (ApprovalRouteEntity retRoute : retRoutes) {
            tx.begin();
            response = model.removeApprovalRoute(retRoute.getRouteId(), null);
            assertThat(response.isSuccess(), is(true));
            tx.commit();
            em.clear();
        }

        count = model.countAllApprovalRoute(null);
        retRoutes = model.findApprovalRouteRange(null, null, null);
        assertThat(count, is(0L));
        assertThat(retRoutes.size(), is(0));
    }

    /**
     * 工程の承認テスト
     *
     * @throws Exception 
     */
    @Test
    public void testApproveWork() throws Exception {
        System.out.println("testApproveWork");

        ResponseEntity response;

        // 申請者の役割(承認権限なし)
        RoleAuthorityEntity requestorRole = this.createRole("申請者", false);
        // 承認者の役割(承認権限あり)
        RoleAuthorityEntity approverRole = this.createRole("承認者", true);

        // 申請者を登録する。
        List<OrganizationEntity> requestors = this.createOrganization("申請者", "requestor", 3, requestorRole.getRoleId());
        // 承認者を登録する。
        List<OrganizationEntity> approvers = this.createOrganization("承認者", "approver", 5, approverRole.getRoleId());

        // 承認ルートを登録する。
        ApprovalRouteEntity route = this.createApprovalRoute("承認ルート①", approvers);

        // 工程を作成する。
        List<WorkEntity> works = this.createWork("工程", 1);
        WorkEntity work = works.get(0);

        tx.begin();

        OrganizationEntity requestor1 = requestors.get(0);
        OrganizationEntity requestor2 = requestors.get(1);

        // 申請情報
        ApprovalEntity approval = new ApprovalEntity();
        approval.setDataType(ApprovalDataTypeEnum.WORK);
        approval.setNewData(work.getWorkId());
        approval.setRouteId(route.getRouteId());
        approval.setRequestorId(requestor1.getOrganizationId());
        approval.setRequestDatetime(new Date());
        approval.setComment("申請ダイアログで入力したコメント");

        WorkEntity targetWork;
        ApprovalEntity workApproval;

        // 申請する。
        response = model.apply(approval, requestor1.getOrganizationId());
        assertThat(response.isSuccess(), is(true));

        // 申請した工程を取得する。
        targetWork = workRest.find(work.getWorkId(), null, null, null);
        assertThat(targetWork.getApprovalState(), is(ApprovalStatusEnum.APPLY));
        assertThat(targetWork.getApprovalId(), is(notNullValue()));

        // 申請情報を取得する。
        workApproval = approvalModel.findApproval(targetWork.getApprovalId());
        assertThat(workApproval, is(notNullValue()));

        // 申請取消する。
        ApprovalEntity cancelApproval = new ApprovalEntity();
        cancelApproval.setApprovalId(workApproval.getApprovalId());
        cancelApproval.setComment("申請取消ダイアログで入力したコメント");

        // 申請者以外が申請取消
        response = model.cancelApply(cancelApproval, requestor2.getOrganizationId());
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));

        // 申請者が申請取消
        response = model.cancelApply(cancelApproval, requestor1.getOrganizationId());
        assertThat(response.isSuccess(), is(true));

        workApproval = approvalModel.findApproval(targetWork.getApprovalId());
        assertThat(workApproval, is(notNullValue()));

        targetWork = workRest.find(work.getWorkId(), null, null, null);
        assertThat(targetWork.getApprovalState(), is(ApprovalStatusEnum.CANCEL_APPLY));

        for (ApprovalOrderEntity rejectOrder : route.getApprovalOrders()) {
            int count = 1;
            if (rejectOrder.getApprovalFinal()) {
                count = 2;
            }

            for (int i = 0; i < count; i++) {
                // 申請する。
                response = model.apply(approval, requestor1.getOrganizationId());
                assertThat(response.isSuccess(), is(true));

                // 申請した工程を取得する。
                targetWork = workRest.find(work.getWorkId(), null, null, null);
                assertThat(targetWork.getApprovalState(), is(ApprovalStatusEnum.APPLY));
                assertThat(targetWork.getApprovalId(), is(notNullValue()));

                // 申請情報を取得する。
                workApproval = approvalModel.findApproval(targetWork.getApprovalId());
                assertThat(workApproval, is(notNullValue()));
                assertThat(workApproval.getApprovalState(), is(ApprovalStatusEnum.APPLY));

                // 該当承認者で却下する。
                boolean isReject = false;
                for (ApprovalOrderEntity approvalOrder : route.getApprovalOrders()) {
                    ApprovalFlowEntity approvalFlow = new ApprovalFlowEntity();
                    approvalFlow.setApprovalId(targetWork.getApprovalId());
                    approvalFlow.setApproverId(approvalOrder.getOrganizationId());
                    approvalFlow.setApprovalDatetime(new Date());
                    approvalFlow.setComment(new StringBuilder("承認画面で入力したコメント").append(approvalOrder.getApprovalOrder()).toString());

                    if (Objects.equals(approvalOrder.getOrganizationId(), rejectOrder.getOrganizationId())) {
                        if (i == 0) {
                            // 却下する。
                            response = model.reject(approvalFlow, approvalOrder.getOrganizationId());
                            assertThat(response.isSuccess(), is(true));
                            isReject = true;
                        } else {
                            // 最終承認する。
                            response = model.finalApprove(approvalFlow, approvalOrder.getOrganizationId());
                            assertThat(response.isSuccess(), is(true));
                        }
                    } else {
                        // 承認する。
                        response = model.approve(approvalFlow, approvalOrder.getOrganizationId());
                        if (isReject) {
                            // 承認待ちの承認者ではないため承認できない。
                            assertThat(response.isSuccess(), is(false));
                            assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));
                        } else {
                            assertThat(response.isSuccess(), is(true));

                            for (OrganizationEntity cancelApprover : approvers) {
                                // 承認取消する。
                                response = model.cancelApprove(approvalFlow, cancelApprover.getOrganizationId());

                                if (Objects.equals(cancelApprover.getOrganizationId(), approvalOrder.getOrganizationId())) {
                                    assertThat(response.isSuccess(), is(true));

                                    // 再度承認する。
                                    response = model.approve(approvalFlow, approvalOrder.getOrganizationId());
                                    assertThat(response.isSuccess(), is(true));
                                } else {
                                    // 前の承認者ではないため承認取消できない。
                                    assertThat(response.isSuccess(), is(false));
                                    assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));
                                }
                            }
                        }

                        // 却下する。
                        response = model.reject(approvalFlow, approvalOrder.getOrganizationId());
                        // 承認待ちの承認者ではないため却下できない。
                        assertThat(response.isSuccess(), is(false));
                        assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));
                    }
                }

                targetWork = workRest.find(work.getWorkId(), null, null, null);
                assertThat(targetWork, is(notNullValue()));

                workApproval = approvalModel.findApproval(targetWork.getApprovalId());
                assertThat(workApproval, is(notNullValue()));

                if (i == 0) {
                    assertThat(targetWork.getApprovalState(), is(ApprovalStatusEnum.REJECT));
                    assertThat(workApproval.getApprovalState(), is(ApprovalStatusEnum.REJECT));
                } else {
                    assertThat(targetWork.getApprovalState(), is(ApprovalStatusEnum.FINAL_APPROVE));
                    assertThat(workApproval.getApprovalState(), is(ApprovalStatusEnum.FINAL_APPROVE));
                }
            }
        }

        tx.commit();
    }

    /**
     * 工程順の承認テスト
     *
     * @throws Exception 
     */
    @Test
    public void testApproveWorkflow() throws Exception {
        System.out.println("testApproveWork");

        ResponseEntity response;

        // 申請者の役割(承認権限なし)
        RoleAuthorityEntity requestorRole = this.createRole("申請者", false);
        // 承認者の役割(承認権限あり)
        RoleAuthorityEntity approverRole = this.createRole("承認者", true);

        // 申請者を登録する。
        List<OrganizationEntity> requestors = this.createOrganization("申請者", "requestor", 3, requestorRole.getRoleId());
        // 承認者を登録する。
        List<OrganizationEntity> approvers = this.createOrganization("承認者", "approver", 5, approverRole.getRoleId());

        // 承認ルートを登録する。
        ApprovalRouteEntity route = this.createApprovalRoute("承認ルート①", approvers);

        // 工程順階層を作成する。
        HierarchyEntity workflowHierarchy = this.createHierarchy(HierarchyTypeEnum.WORKFLOW, 0L, "工程順階層①");

        // 工程順を作成する。
        WorkflowEntity workflow = this.createWorkflow(workflowHierarchy.getHierarchyId(), "工程順①");

        tx.begin();

        OrganizationEntity requestor1 = requestors.get(0);
        OrganizationEntity requestor2 = requestors.get(1);

        // 申請情報
        ApprovalEntity approval = new ApprovalEntity();
        approval.setDataType(ApprovalDataTypeEnum.WORKFLOW);
        approval.setNewData(workflow.getWorkflowId());
        approval.setRouteId(route.getRouteId());
        approval.setRequestorId(requestor1.getOrganizationId());
        approval.setRequestDatetime(new Date());
        approval.setComment("申請ダイアログで入力したコメント");

        WorkflowEntity targetWorkflow;
        ApprovalEntity workflowApproval;

        // 申請する。
        response = model.apply(approval, requestor1.getOrganizationId());
        assertThat(response.isSuccess(), is(true));

        // 申請した工程順を取得する。
        targetWorkflow = workflowRest.find(workflow.getWorkflowId(), null, null);
        assertThat(targetWorkflow.getApprovalState(), is(ApprovalStatusEnum.APPLY));
        assertThat(targetWorkflow.getApprovalId(), is(notNullValue()));

        // 申請情報を取得する。
        workflowApproval = approvalModel.findApproval(targetWorkflow.getApprovalId());
        assertThat(workflowApproval, is(notNullValue()));

        // 申請取消する。
        ApprovalEntity cancelApproval = new ApprovalEntity();
        cancelApproval.setApprovalId(workflowApproval.getApprovalId());
        cancelApproval.setComment("申請取消ダイアログで入力したコメント");

        // 申請者以外が申請取消
        response = model.cancelApply(cancelApproval, requestor2.getOrganizationId());
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));

        // 申請者が申請取消
        response = model.cancelApply(cancelApproval, requestor1.getOrganizationId());
        assertThat(response.isSuccess(), is(true));

        workflowApproval = approvalModel.findApproval(targetWorkflow.getApprovalId());
        assertThat(workflowApproval, is(notNullValue()));

        targetWorkflow = workflowRest.find(workflow.getWorkflowId(), null, null);
        assertThat(targetWorkflow.getApprovalState(), is(ApprovalStatusEnum.CANCEL_APPLY));

        for (ApprovalOrderEntity rejectOrder : route.getApprovalOrders()) {
            int count = 1;
            if (rejectOrder.getApprovalFinal()) {
                count = 2;
            }

            for (int i = 0; i < count; i++) {
                // 申請する。
                response = model.apply(approval, requestor1.getOrganizationId());
                assertThat(response.isSuccess(), is(true));

                // 申請した工程順を取得する。
                targetWorkflow = workflowRest.find(workflow.getWorkflowId(), null, null);
                assertThat(targetWorkflow.getApprovalState(), is(ApprovalStatusEnum.APPLY));
                assertThat(targetWorkflow.getApprovalId(), is(notNullValue()));

                // 申請情報を取得する。
                workflowApproval = approvalModel.findApproval(targetWorkflow.getApprovalId());
                assertThat(workflowApproval, is(notNullValue()));
                assertThat(workflowApproval.getApprovalState(), is(ApprovalStatusEnum.APPLY));

                // 該当承認者で却下する。
                boolean isReject = false;
                for (ApprovalOrderEntity approvalOrder : route.getApprovalOrders()) {
                    ApprovalFlowEntity approvalFlow = new ApprovalFlowEntity();
                    approvalFlow.setApprovalId(targetWorkflow.getApprovalId());
                    approvalFlow.setApproverId(approvalOrder.getOrganizationId());
                    approvalFlow.setApprovalDatetime(new Date());
                    approvalFlow.setComment(new StringBuilder("承認画面で入力したコメント").append(approvalOrder.getApprovalOrder()).toString());

                    if (Objects.equals(approvalOrder.getOrganizationId(), rejectOrder.getOrganizationId())) {
                        if (i == 0) {
                            // 却下する。
                            response = model.reject(approvalFlow, approvalOrder.getOrganizationId());
                            assertThat(response.isSuccess(), is(true));
                            isReject = true;
                        } else {
                            // 最終承認する。
                            response = model.finalApprove(approvalFlow, approvalOrder.getOrganizationId());
                            assertThat(response.isSuccess(), is(true));
                        }
                    } else {
                        // 承認する。
                        response = model.approve(approvalFlow, approvalOrder.getOrganizationId());
                        if (isReject) {
                            // 承認待ちの承認者ではないため承認できない。
                            assertThat(response.isSuccess(), is(false));
                            assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));
                        } else {
                            assertThat(response.isSuccess(), is(true));

                            for (OrganizationEntity cancelApprover : approvers) {
                                // 承認取消する。
                                response = model.cancelApprove(approvalFlow, cancelApprover.getOrganizationId());

                                if (Objects.equals(cancelApprover.getOrganizationId(), approvalOrder.getOrganizationId())) {
                                    assertThat(response.isSuccess(), is(true));

                                    // 再度承認する。
                                    response = model.approve(approvalFlow, approvalOrder.getOrganizationId());
                                    assertThat(response.isSuccess(), is(true));
                                } else {
                                    // 前の承認者ではないため承認取消できない。
                                    assertThat(response.isSuccess(), is(false));
                                    assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));
                                }
                            }
                        }

                        // 却下する。
                        response = model.reject(approvalFlow, approvalOrder.getOrganizationId());
                        // 承認待ちの承認者ではないため却下できない。
                        assertThat(response.isSuccess(), is(false));
                        assertThat(response.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_ORGANIZATION));
                    }
                }

                targetWorkflow = workflowRest.find(workflow.getWorkflowId(), null, null);
                assertThat(targetWorkflow, is(notNullValue()));

                workflowApproval = approvalModel.findApproval(targetWorkflow.getApprovalId());
                assertThat(workflowApproval, is(notNullValue()));

                if (i == 0) {
                    assertThat(targetWorkflow.getApprovalState(), is(ApprovalStatusEnum.REJECT));
                    assertThat(workflowApproval.getApprovalState(), is(ApprovalStatusEnum.REJECT));
                } else {
                    assertThat(targetWorkflow.getApprovalState(), is(ApprovalStatusEnum.FINAL_APPROVE));
                    assertThat(workflowApproval.getApprovalState(), is(ApprovalStatusEnum.FINAL_APPROVE));
                }
            }
        }

        tx.commit();
    }

    /**
     * 役割を作成する。
     *
     * @param name 役割名
     * @param approve 承認権限
     * @return 役割
     * @throws Exception 
     */
    private RoleAuthorityEntity createRole(String name, boolean approve) throws Exception {
        RoleAuthorityEntity role = new RoleAuthorityEntity();
        role.setRoleName(name);
        role.setApprove(approve);

        role.setKanbanReference(true);
        role.setKanbanCreate(true);
        role.setResourceReference(true);
        role.setResourceEdit(true);

        role.setActualDel(false);
        role.setLineManage(false);
        role.setActualOutput(false);
        role.setAccessEdit(false);

        tx.begin();
        roleRest.add(role, null);
        tx.commit();
        em.clear();

        return role;
    }

    /**
     * 組織を作成する。
     *
     * @param name 組織名のベース(組織名は name①, name② のようになる)
     * @param ident 組織識別名のベース(組織識別名は ident1, ident2 のようになる)
     * @param count 作成する件数
     * @param roleId 役割ID
     * @return 組織一覧
     * @throws Exception 
     */
    private List<OrganizationEntity> createOrganization(String name, String ident, int count, long roleId) throws Exception {
        List<OrganizationEntity> organizations = new LinkedList();

        int number = (int)'①';
        for (int i = 1; i <= count; i++) {
            char c = (char)number;
            String organizationName = new StringBuilder(name).append(c).toString();
            String organizationIdentify = new StringBuilder(ident).append(i).toString();
            String mailAddress = new StringBuilder("dummyTest").append(i).append("@adtek-fuji.co.jp").toString();
            OrganizationEntity organization = new OrganizationEntity(0L, organizationName, organizationIdentify, AuthorityEnum.WORKER, null, null, mailAddress, null, null);
            organization.setRoleCollection(Arrays.asList(roleId));

            tx.begin();
            organizationRest.add(organization, null);
            tx.commit();
            em.clear();

            organizations.add(organization);
            number++;
        }

        return organizations;
    }

    /**
     * 承認ルートを作成する。
     *
     * @param routeName ルート名
     * @param approvers 承認者一覧
     * @return 承認ルート
     */
    private ApprovalRouteEntity createApprovalRoute(String routeName, List<OrganizationEntity> approvers) {
        ApprovalRouteEntity route = new ApprovalRouteEntity();
        route.setRouteName(routeName);

        ResponseEntity response;

        tx.begin();
        response = model.addApprovalRoute(route, null);
        assertThat(response.isSuccess(), is(true));
        tx.commit();
        em.clear();

        List<ApprovalOrderEntity> routeOrders = new ArrayList();

        int orderNum = 1;
        for (OrganizationEntity approver : approvers) {
            ApprovalOrderEntity approvalOrder = new ApprovalOrderEntity();
            approvalOrder.setRouteId(route.getRouteId());
            approvalOrder.setApprovalOrder(orderNum);
            approvalOrder.setOrganizationId(approver.getOrganizationId());

            if (orderNum < approvers.size()) {
                approvalOrder.setApprovalFinal(false);
            } else {
                approvalOrder.setApprovalFinal(true);
            }

            routeOrders.add(approvalOrder);
            orderNum++;
        }

        route.setApprovalOrders(routeOrders);

        tx.begin();
        response = model.updateApprovalRoute(route, null);
        assertThat(response.isSuccess(), is(true));
        tx.commit();
        em.clear();

        return route;
    }

    /**
     * 階層を作成する。
     *
     * @param type 階層種別
     * @param parentId 親階層ID
     * @param name 階層名
     * @return 階層
     * @throws Exception 
     */
    private HierarchyEntity createHierarchy(HierarchyTypeEnum type, Long parentId, String name) throws Exception {
        HierarchyEntity hierarchy = new HierarchyEntity();
        hierarchy.setHierarchyType(type);
        hierarchy.setHierarchyName(name);
        hierarchy.setParentHierarchyId(parentId);

        tx.begin();
        Response response = hierarchyRest.add(hierarchy, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.isSuccess(), is(true));
        tx.commit();
        em.clear();

        return hierarchy;
    }

    /**
     * 工程を作成する。
     *
     * @param name 工程名のベース(工程名は name①, name② のようになる)
     * @param count 作成する件数
     * @return 工程一覧
     * @throws Exception 
     */
    private List<WorkEntity> createWork(String name, int count) throws Exception {
        List<WorkEntity> works = new LinkedList();

        int number = (int)'①';
        for (int i = 1; i <= count; i++) {
            char c = (char)number;
            String workName = new StringBuilder(name).append(c).toString();

            WorkEntity work = new WorkEntity();
            work.setParentId(0L);// 工程階層ID
            work.setWorkName(workName);// 工程名
            work.setWorkRev(1);// 版数
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

            // 追加情報
            List<AddInfoEntity> addInfos = new LinkedList();

            int propOrder = 1;
            for (int num = 1; num <= 3; num++) {
                AddInfoEntity addInfo = new AddInfoEntity();
                addInfo.setKey(new StringBuilder("追加情報").append(num).toString());
                addInfo.setType(CustomPropertyTypeEnum.TYPE_STRING);
                addInfo.setVal(new StringBuilder("追加情報").append(num).append("の値").toString());
                addInfo.setDisp(propOrder);

                addInfos.add(addInfo);
                propOrder++;
            }

            // 追加情報一覧をJSON文字列に変換して工程の追加情報にセットする。
            String jsonAddInfos = JsonUtils.objectsToJson(addInfos);
            work.setWorkAddInfo(jsonAddInfos);

            // トレーサビリティ
            List<CheckInfoEntity> checkInfos = new LinkedList();

            for (int num = 1; num <= 3; num++) {
                CheckInfoEntity checkInfo = new CheckInfoEntity();
                checkInfo.setKey(new StringBuilder("検査").append(num).toString());
                checkInfo.setType(CustomPropertyTypeEnum.TYPE_STRING);
                checkInfo.setVal(new StringBuilder("検査").append(num).append("の値").toString());
                checkInfo.setDisp(propOrder);
                checkInfo.setPage(1);
                checkInfo.setCat(WorkPropertyCategoryEnum.WORK);

                checkInfos.add(checkInfo);
                propOrder++;
            }

            // 検査情報一覧をJSON文字列に変換して工程の検査情報にセットする。
            String jsonCheckInfos = JsonUtils.objectsToJson(checkInfos);
            work.setWorkCheckInfo(jsonCheckInfos);

            tx.begin();
            Response response = workRest.add(work, null);
            ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
            assertThat(responseEntity.isSuccess(), is(true));
            tx.commit();
            em.clear();

            works.add(work);

            number++;
        }

        return works;
    }

    /**
     * 工程順を作成する。
     *
     * @param parentId 親階層ID
     * @param name 工程順名
     * @return 工程順
     * @throws Exception 
     */
    private WorkflowEntity createWorkflow(long parentId, String name) throws Exception {
        WorkflowEntity workflow = new WorkflowEntity();

        // 工程を新規作成する。
        List<WorkEntity> works = this.createWork("工程", 5);

        workflow.setParentId(parentId);

        workflow.setWorkflowName(name);
        workflow.setWorkflowRev(1);

        workflow.setModelName("モデル①");
        workflow.setWorkflowNumber(null);

        // 作業時間枠
        SimpleDateFormat workTimeFormat = new SimpleDateFormat("HH:mm");
        workflow.setOpenTime(workTimeFormat.parse("09:00"));
        workflow.setCloseTime(workTimeFormat.parse("17:00"));

        // 作業順序
        workflow.setSchedulePolicy(SchedulePolicyEnum.PriorityParallel);

        workflow.setLedgerPath(null);

        workflow.setUpdatePersonId(0L);
        workflow.setUpdateDatetime(new Date());

        // 工程順ダイアグラム
        String diaglam = TestUtils.createWorkflowDiaglam(works);
        workflow.setWorkflowDiaglam(diaglam);

        // 追加情報
        List<AddInfoEntity> addInfos = new LinkedList();

        int propOrder = 1;
        for (int num = 1; num <= 3; num++) {
            AddInfoEntity addInfo = new AddInfoEntity();
            addInfo.setKey(new StringBuilder("追加情報").append(num).toString());
            addInfo.setType(CustomPropertyTypeEnum.TYPE_STRING);
            addInfo.setVal(new StringBuilder("追加情報").append(num).append("の値").toString());
            addInfo.setDisp(propOrder);

            addInfos.add(addInfo);
            propOrder++;
        }

        // 追加情報一覧をJSON文字列に変換して工程順の追加情報にセットする。
        String jsonAddInfos = JsonUtils.objectsToJson(addInfos);
        workflow.setWorkflowAddInfo(jsonAddInfos);

        // サービス情報
        workflow.setServiceInfo(new StringBuilder(name).append("のサービス情報").toString());

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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
        em.clear();

        return workflow;
    }
}
