/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanHierarchyEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ke.yokoi
 */
public class KanbanHierarchyEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static KanbanHierarchyEntityFacadeREST rest = null;
    private static WorkflowEntityFacadeREST workflowRest = null;
    private static WorkKanbanEntityFacadeREST workKanbanRest = null;
    private static KanbanEntityFacadeREST kanbanRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;
    private static OrganizationEntityFacadeREST organizationEntityFacadeREST = null;

    public KanbanHierarchyEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        workflowRest = new WorkflowEntityFacadeREST();
        workflowRest.setEntityManager(em);

        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        workKanbanRest = new WorkKanbanEntityFacadeREST();
        workKanbanRest.setEntityManager(em);

        kanbanRest = new KanbanEntityFacadeREST();
        kanbanRest.setEntityManager(em);
        kanbanRest.setWorkflowRest(workflowRest);
        kanbanRest.setWorkKandanREST(workKanbanRest);

        organizationEntityFacadeREST = new OrganizationEntityFacadeREST();
        organizationEntityFacadeREST.setEntityManager(em);

        rest = new KanbanHierarchyEntityFacadeREST();
        rest.setEntityManager(em);
        rest.setAuthRest(authRest);
        rest.setKanbanFacadeRest(kanbanRest);
        rest.setWorkflowFacadeRest(workflowRest);
        rest.setOrganizationFacade(organizationEntityFacadeREST);
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
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Response response;
        ResponseEntity responseEntity;

        // 親階層を追加する。
        KanbanHierarchyEntity tree1 = new KanbanHierarchyEntity(0L, "tree1");
        KanbanHierarchyEntity tree2 = new KanbanHierarchyEntity(0L, "tree2");
        KanbanHierarchyEntity tree3 = new KanbanHierarchyEntity(0L, "tree3");

        // 階層一覧
        List<KanbanHierarchyEntity> srcTrees = Arrays.asList(tree1, tree2, tree3);

        tx.begin();
        rest.add(tree1, null);
        rest.add(tree2, null);
        rest.add(tree3, null);
        tx.commit();

        // 階層一覧を取得する。
        List<KanbanHierarchyEntity> trees = rest.findTreeRange(null, loginUserId, null, null, null, null);
        assertThat(trees.size(), is(3));

        // 登録した階層がすべて取得できたか確認する。
        for (KanbanHierarchyEntity src : srcTrees) {
            long count = trees.stream().filter(p -> p.getHierarchyName().equals(src.getHierarchyName())).count();
            assertThat(count, is(1L));
        }

        // 工程順を追加する。
        WorkflowEntity workflow1 = new WorkflowEntity(0L, "workflow1", "rev1", null, null, df.parse("2019/10/01 08:00:00"), null);

        tx.begin();
        workflowRest.add(workflow1, null);
        tx.commit();

        List<Long> childIds = new LinkedList();
        for (KanbanHierarchyEntity tree : trees) {
            // 子階層を追加する。
            String id = new StringBuilder("child").append(tree.getKanbanHierarchyId()).toString();
            KanbanHierarchyEntity srcChild = new KanbanHierarchyEntity(tree.getKanbanHierarchyId(), id);

            tx.begin();
            rest.add(srcChild, null);
            tx.commit();

            KanbanHierarchyEntity child = rest.findHierarchyByName(id, null, null);

            childIds.add(child.getKanbanHierarchyId());

// TODO: [v2対応] カンバン対応後に修正する。
//            // 子階層にカンバンを追加する。
//            KanbanEntity kanban1 = new KanbanEntity(child.getKanbanHierarchyId(), id + "kanban1", null, workflow1.getWorkflowId(), workflow1.getWorkflowName(), df.parse("2019/10/01 08:00:00"), df.parse("2019/10/01 09:00:00"), null, df.parse("2019/10/01 00:00:00"), KanbanStatusEnum.PLANNING, null, null);
//            KanbanEntity kanban2 = new KanbanEntity(child.getKanbanHierarchyId(), id + "kanban2", null, workflow1.getWorkflowId(), workflow1.getWorkflowName(), df.parse("2019/10/01 08:00:00"), df.parse("2015/10/01 09:00:00"), null, df.parse("2019/10/01 00:00:00"), KanbanStatusEnum.PLANNING, null, null);
//
//            tx.begin();
//            kanbanRest.add(kanban1, null);
//            kanbanRest.add(kanban2, null);
//            tx.commit();
//
//            // 子階層の階層一覧を取得する。
//            List<KanbanHierarchyEntity> childs = rest.findTreeRange(tree.getKanbanHierarchyId(), loginUserId, null, null, null, null);
//            assertThat(childs.size(), is(1));
//
//            // カンバンを含む階層が取得できたか確認する。
//            KanbanHierarchyEntity c = childs.get(0);
//            assertThat(c.getHierarchyName(), is(srcChild.getHierarchyName()));
//            assertThat(c.getKanbanCollection().size(), is(2));
        }

// TODO: [v2対応] カンバン対応後に修正する。
//        // 子要素(カンバン)を持つ階層を削除する。
//        tx.begin();
//        response = rest.remove(childIds.get(0), null);
//        responseEntity = (ResponseEntity) response.getEntity();
//        tx.commit();
//        assertThat(response.getStatus(), is(500));
//        assertThat(responseEntity.isSuccess(), is(false));
//        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_CHILD_DELETE));

        // 子階層を持つ階層を削除する。
        tx.begin();
        response = rest.remove(trees.get(0).getKanbanHierarchyId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE));

        List<KanbanHierarchyEntity> childs = rest.findTreeRange(trees.get(0).getKanbanHierarchyId(), null, null, null, null, null);

        // 子階層を削除する。
        tx.begin();
        for (KanbanHierarchyEntity child : childs) {
            rest.remove(child.getKanbanHierarchyId(), null);
        }
        tx.commit();

        // 子階層を持たない階層を削除する。
        tx.begin();
        response = rest.remove(trees.get(0).getKanbanHierarchyId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(responseEntity.isSuccess(), is(true));
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        KanbanHierarchyEntity kanban1 = new KanbanHierarchyEntity(0L, "kanban1");

        tx.begin();
        response = rest.add(kanban1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        KanbanHierarchyEntity kanban2 = new KanbanHierarchyEntity(0L, "kanban1");

        tx.begin();
        response = rest.add(kanban2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
    }
}
