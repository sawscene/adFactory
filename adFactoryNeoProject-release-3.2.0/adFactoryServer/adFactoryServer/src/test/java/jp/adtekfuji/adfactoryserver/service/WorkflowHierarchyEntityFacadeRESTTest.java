/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowHierarchyEntity;
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
public class WorkflowHierarchyEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static WorkflowHierarchyEntityFacadeREST workflowHierarchyRest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;
    private static OrganizationEntityFacadeREST organizationEntityFacadeREST = null;


    public WorkflowHierarchyEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);
        workflowRest.setHierarchyEntityFacadeREST(hierarchyRest);

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        organizationEntityFacadeREST = new OrganizationEntityFacadeREST();
        organizationEntityFacadeREST.setEntityManager(em);

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);
        hierarchyRest.setAuthRest(authRest);
        hierarchyRest.setOrganizationFacade(organizationEntityFacadeREST);

        workflowHierarchyRest = new WorkflowHierarchyEntityFacadeREST();
        workflowHierarchyRest.setEntityManager(em);
        workflowHierarchyRest.setHierarchyRest(hierarchyRest);

        // 承認機能ライセンス無効
        TestUtils.setOptionLicense(LicenseOptionType.ApprovalOption, false);
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
    public void testTree() throws Exception {
        System.out.println("testTree");

        Long loginUserId = null;

        Response response;
        ResponseEntity responseEntity;

        // 親階層を追加する。
        WorkflowHierarchyEntity tree1 = new WorkflowHierarchyEntity(0L, "tree1");
        WorkflowHierarchyEntity tree2 = new WorkflowHierarchyEntity(0L, "tree2");
        WorkflowHierarchyEntity tree3 = new WorkflowHierarchyEntity(0L, "tree3");

        // 階層一覧
        List<WorkflowHierarchyEntity> srcTrees = Arrays.asList(tree1, tree2, tree3);

        tx.begin();
        workflowHierarchyRest.add(tree1, null);
        workflowHierarchyRest.add(tree2, null);
        workflowHierarchyRest.add(tree3, null);
        tx.commit();

        // 階層一覧を取得する。
        List<WorkflowHierarchyEntity> trees = workflowHierarchyRest.findTreeRange(null, loginUserId, null, null, null, null, null);
        assertThat(trees.size(), is(3));

        // 登録した階層がすべて取得できたか確認する。
        for (WorkflowHierarchyEntity src : srcTrees) {
            long count = trees.stream().filter(p -> p.getHierarchyName().equals(src.getHierarchyName())).count();
            assertThat(count, is(1L));
        }

        List<Long> childIds = new LinkedList();
        for (WorkflowHierarchyEntity tree : trees) {
            // 子階層を追加する。
            String id = new StringBuilder("child").append(tree.getWorkflowHierarchyId()).toString();
            WorkflowHierarchyEntity srcChild = new WorkflowHierarchyEntity(tree.getWorkflowHierarchyId(), id);

            tx.begin();
            workflowHierarchyRest.add(srcChild, null);
            tx.commit();

            WorkflowHierarchyEntity child = workflowHierarchyRest.findHierarchyByName(id, null);

            childIds.add(child.getHierarchyId());

            // 子階層に工程順を追加する。
            WorkflowEntity workflow1 = new WorkflowEntity(child.getWorkflowHierarchyId(), id + "workflow1", null, null, null, null, null);
            WorkflowEntity workflow2 = new WorkflowEntity(child.getWorkflowHierarchyId(), id + "workflow2", null, null, null, null, null);

            tx.begin();
            workflowRest.add(workflow1, null);
            workflowRest.add(workflow2, null);
            tx.commit();

            // 子階層の階層一覧を取得する。
            List<WorkflowHierarchyEntity> childs = workflowHierarchyRest.findTreeRange(tree.getWorkflowHierarchyId(), loginUserId, null, null, null, null, null);
            assertThat(childs.size(), is(1));

            // 工程を含む階層が取得できたか確認する。
            WorkflowHierarchyEntity c = childs.get(0);
            assertThat(c.getHierarchyName(), is(srcChild.getHierarchyName()));
            assertThat(c.getWorkflowCollection().size(), is(2));
        }

        // 子要素(工程順)を持つ階層を削除する。
        tx.begin();
        response = workflowHierarchyRest.remove(childIds.get(0), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_CHILD_DELETE));

        // 子階層を持つ階層を削除する。
        tx.begin();
        response = workflowHierarchyRest.remove(trees.get(0).getWorkflowHierarchyId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE));
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        WorkflowHierarchyEntity tree1 = new WorkflowHierarchyEntity(0L, "tree");

        tx.begin();
        response = workflowHierarchyRest.add(tree1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        WorkflowHierarchyEntity tree2 = new WorkflowHierarchyEntity(0L, "tree");

        tx.begin();
        response = workflowHierarchyRest.add(tree2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
    }
}
