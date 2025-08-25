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
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkHierarchyEntity;
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
public class WorkHierarchyEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static HierarchyEntityFacadeREST hierarchyRest = null;
    private static WorkHierarchyEntityFacadeREST workHierarchyRest = null;
    private static WorkEntityFacadeREST workRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;
    private static OrganizationEntityFacadeREST organizationEntityFacadeREST = null;

    public WorkHierarchyEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        workRest = new WorkEntityFacadeREST();
        workRest.setEntityManager(em);

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        organizationEntityFacadeREST = new OrganizationEntityFacadeREST();
        organizationEntityFacadeREST.setEntityManager(em);

        hierarchyRest = new HierarchyEntityFacadeREST();
        hierarchyRest.setEntityManager(em);
        hierarchyRest.setAuthRest(authRest);
        hierarchyRest.setOrganizationFacade(organizationEntityFacadeREST);

        workHierarchyRest = new WorkHierarchyEntityFacadeREST();
        workHierarchyRest.setEntityManager(em);
        workHierarchyRest.setHierarchyRest(hierarchyRest);

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
        WorkHierarchyEntity tree1 = new WorkHierarchyEntity(0L, "tree1");
        WorkHierarchyEntity tree2 = new WorkHierarchyEntity(0L, "tree2");
        WorkHierarchyEntity tree3 = new WorkHierarchyEntity(0L, "tree3");

        // 階層一覧
        List<WorkHierarchyEntity> srcTrees = Arrays.asList(tree1, tree2, tree3);

        tx.begin();
        workHierarchyRest.add(tree1, null);
        workHierarchyRest.add(tree2, null);
        workHierarchyRest.add(tree3, null);
        tx.commit();

        // 階層一覧を取得する。
        List<WorkHierarchyEntity> trees = workHierarchyRest.findTreeRange(null, loginUserId, null, null, null, null, null);
        assertThat(trees.size(), is(3));

        // 登録した階層がすべて取得できたか確認する。
        for (WorkHierarchyEntity src : srcTrees) {
            long count = trees.stream().filter(p -> p.getHierarchyName().equals(src.getHierarchyName())).count();
            assertThat(count, is(1L));
        }

        List<Long> childIds = new LinkedList();
        for (WorkHierarchyEntity tree : trees) {
            // 子階層を追加する。
            String id = new StringBuilder("child").append(tree.getWorkHierarchyId()).toString();
            WorkHierarchyEntity srcChild = new WorkHierarchyEntity(tree.getWorkHierarchyId(), id);

            tx.begin();
            workHierarchyRest.add(srcChild, null);
            tx.commit();

            WorkHierarchyEntity child = workHierarchyRest.findHierarchyByName(id, null);

            childIds.add(child.getHierarchyId());

            // 子階層に工程を追加する。
            WorkEntity work1 = new WorkEntity(child.getWorkHierarchyId(), id + "work1", 1, null, null, null, null, null, null, null);
            WorkEntity work2 = new WorkEntity(child.getWorkHierarchyId(), id + "work2", 1, null, null, null, null, null, null, null);

            tx.begin();
            workRest.add(work1, null);
            workRest.add(work2, null);
            tx.commit();

            // 子階層の階層一覧を取得する。
            List<WorkHierarchyEntity> childs = workHierarchyRest.findTreeRange(tree.getWorkHierarchyId(), loginUserId, null, null, null, null, null);
            assertThat(childs.size(), is(1));

            // 工程を含む階層が取得できたか確認する。
            WorkHierarchyEntity c = childs.get(0);
            assertThat(c.getHierarchyName(), is(srcChild.getHierarchyName()));
            assertThat(c.getWorkCollection().size(), is(2));
        }

        // 子要素(工程)を持つ階層を削除する。
        tx.begin();
        response = workHierarchyRest.remove(childIds.get(0), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_CHILD_DELETE));

        // 子階層を持つ階層を削除する。
        tx.begin();
        response = workHierarchyRest.remove(trees.get(0).getWorkHierarchyId(), null);
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

        WorkHierarchyEntity line1 = new WorkHierarchyEntity(0L, "tree");

        tx.begin();
        response = workHierarchyRest.add(line1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        WorkHierarchyEntity line2 = new WorkHierarchyEntity(0L, "tree");

        tx.begin();
        response = workHierarchyRest.add(line2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
    }
}
