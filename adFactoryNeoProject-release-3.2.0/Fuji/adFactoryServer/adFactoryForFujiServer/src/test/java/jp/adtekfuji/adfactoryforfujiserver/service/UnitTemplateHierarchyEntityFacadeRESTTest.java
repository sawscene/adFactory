/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateHierarchyEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ユニットテンプレートテストパッケージ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.12.Mon
 */
public class UnitTemplateHierarchyEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static UnitTemplateHierarchyEntityFacadeREST unitTemplateHierarchyRest = null;
    private static UnitTemplateEntityFacadeREST unitTemplateRest = null;
    private static AccessHierarchyFujiEntityFacadeREST authRest = null;

    public UnitTemplateHierarchyEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryForFujiServerPU");
        em = emf.createEntityManager();
        authRest = new AccessHierarchyFujiEntityFacadeREST();
        authRest.setEntityManager(em);
        unitTemplateRest = new UnitTemplateEntityFacadeREST();
        unitTemplateRest.setEntityManager(em);
        unitTemplateHierarchyRest = new UnitTemplateHierarchyEntityFacadeREST();
        unitTemplateHierarchyRest.setEntityManager(em);
        unitTemplateHierarchyRest.setUnitTemplateEntityFacadeREST(unitTemplateRest);
        unitTemplateHierarchyRest.setAuthRest(authRest);
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
        Response response;
        ResponseEntity responseEntity;

        // 親追加.
        Map<String, UnitTemplateHierarchyEntity> expected = new HashMap<>();
        UnitTemplateHierarchyEntity tree1 = new UnitTemplateHierarchyEntity(0L, "tree1");
        UnitTemplateHierarchyEntity tree2 = new UnitTemplateHierarchyEntity(0L, "tree2");
        UnitTemplateHierarchyEntity tree3 = new UnitTemplateHierarchyEntity(0L, "tree3");
        tx.begin();
        unitTemplateHierarchyRest.add(tree1);
        unitTemplateHierarchyRest.add(tree2);
        unitTemplateHierarchyRest.add(tree3);
        tx.commit();
        expected.put("tree1", tree1);
        expected.put("tree2", tree2);
        expected.put("tree3", tree3);
        // 親確認.
        List<UnitTemplateHierarchyEntity> trees = new ArrayList<>();
        trees = unitTemplateHierarchyRest.findTree(null);
        assertThat(trees.size(), is(3));
        for (UnitTemplateHierarchyEntity tree : trees) {
            assertThat(tree, is(expected.get(tree.getHierarchyName())));
            // 子追加.
            String id = "child" + tree.getUnitTemplateHierarchyId();
            UnitTemplateHierarchyEntity child = new UnitTemplateHierarchyEntity(tree.getUnitTemplateHierarchyId(), id);
            tx.begin();
            unitTemplateHierarchyRest.add(child);
            tx.commit();
            expected.put(id, child);
            // ユニットテンプレート追加.
            UnitTemplateEntity unittemplate1 = new UnitTemplateEntity(child.getUnitTemplateHierarchyId(), id + "unittemplate1", null, null, null, null);
            UnitTemplateEntity unittemplate2 = new UnitTemplateEntity(child.getUnitTemplateHierarchyId(), id + "unittemplate2", null, null, null, null);
            tx.begin();
            unitTemplateRest.add(unittemplate1);
            unitTemplateRest.add(unittemplate2);
            tx.commit();
            // 子確認.
            List<UnitTemplateHierarchyEntity> childs = unitTemplateHierarchyRest.findTree(tree.getUnitTemplateHierarchyId());
            assertThat(childs.size(), is(1));
            for (UnitTemplateHierarchyEntity c : childs) {
                assertThat(c, is(expected.get(c.getHierarchyName())));
                assertThat(c.getUnitTemplateCollection().size(), is(2));
            }
        }

        // 子要素を含んだ子階層削除
        tx.begin();
        response = unitTemplateHierarchyRest.remove(expected.get("child" + tree1.getUnitTemplateHierarchyId()).getUnitTemplateHierarchyId());
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_CHILD_DELETE));

        // 子階層を含んだ親階層削除
        tx.begin();
        response = unitTemplateHierarchyRest.remove(tree1.getUnitTemplateHierarchyId());
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

        UnitTemplateHierarchyEntity tree1 = new UnitTemplateHierarchyEntity(0L, "tree");
        tx.begin();
        response = unitTemplateHierarchyRest.add(tree1);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        UnitTemplateHierarchyEntity tree2 = new UnitTemplateHierarchyEntity(0L, "tree");
        tx.begin();
        response = unitTemplateHierarchyRest.add(tree2);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
    }

}
