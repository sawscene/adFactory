/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitHierarchyEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ユニット階層テストパッケージ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.17.Mon
 */
public class UnitHierarchyEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static UnitHierarchyEntityFacadeREST unitHierarchyRest = null;
    private static AccessHierarchyFujiEntityFacadeREST authRest = null;

    public UnitHierarchyEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryForFujiServerPU");
        em = emf.createEntityManager();
        authRest = new AccessHierarchyFujiEntityFacadeREST();
        authRest.setEntityManager(em);
        unitHierarchyRest = new UnitHierarchyEntityFacadeREST();
        unitHierarchyRest.setEntityManager(em);
        unitHierarchyRest.setAuthRest(authRest);
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
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        // 作成
        UnitHierarchyEntity unit1 = new UnitHierarchyEntity(0L, "unit1");
        tx.begin();
        response = unitHierarchyRest.add(unit1);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        // 階層が重複しているためエラー
        UnitHierarchyEntity unit2 = new UnitHierarchyEntity(0L, "unit1");
        tx.begin();
        response = unitHierarchyRest.add(unit2);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        // 別階層に登録するため名前重複でも成功
        UnitHierarchyEntity unit3 = new UnitHierarchyEntity(1L, "unit1");
        tx.begin();
        response = unitHierarchyRest.add(unit3);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        // 更新
        unit1.setHierarchyName("update unit");
        tx.begin();
        response = unitHierarchyRest.update(unit1);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        // 階層が重複しているためエラー
        unit3.setHierarchyName("update unit");
        tx.begin();
        response = unitHierarchyRest.add(unit3);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
    }

}
