/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jp.adtekfuji.adfactoryforfujiserver.entity.accessfuji.AccessHierarchyFujiEntity;
import jp.adtekfuji.forfujiapp.entity.accessfuji.AccessHierarchyFujiTypeEnum;

/**
 *
 * @author 
 */
public class AccessHierarchyFujiEntityFacadeRESTTest {
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static AccessHierarchyFujiEntityFacadeREST rest = null;

    public AccessHierarchyFujiEntityFacadeRESTTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryForFujiServerPU");
        em = emf.createEntityManager();
        rest = new AccessHierarchyFujiEntityFacadeREST();
        rest.setEntityManager(em);
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
    public void setUp() throws URISyntaxException {
        tx = em.getTransaction();
    }

    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    @Test
    public void testAccessHierarchyEntityFacadeREST() throws Exception {
        AccessHierarchyFujiEntity access1 = new AccessHierarchyFujiEntity();
        access1.setTypeId(AccessHierarchyFujiTypeEnum.UnitHierarchy);
        access1.setFkHierarchyId(1L);
        access1.setFkOrganizationId(1L);
        AccessHierarchyFujiEntity access2 = new AccessHierarchyFujiEntity();
        access2.setTypeId(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy);
        access2.setFkHierarchyId(2L);
        access2.setFkOrganizationId(2L);
        AccessHierarchyFujiEntity access3 = new AccessHierarchyFujiEntity();
        access3.setTypeId(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy);
        access3.setFkHierarchyId(2L);
        access3.setFkOrganizationId(3L);
        AccessHierarchyFujiEntity access4 = new AccessHierarchyFujiEntity();
        access4.setTypeId(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy);
        access4.setFkHierarchyId(2L);
        access4.setFkOrganizationId(4L);
        
        // 追加
        tx.begin();
        rest.add(access1);
        rest.add(access2);
        rest.add(access3);
        rest.add(access4);
        tx.commit();
        
        // 取得
        List<AccessHierarchyFujiEntity> find1 = rest.find(AccessHierarchyFujiTypeEnum.UnitHierarchy, 1L);
        assertThat(find1.contains(access1), is(true));
        List<AccessHierarchyFujiEntity> find2 = rest.find(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, 2L);
        assertThat(find2.contains(access2), is(true));
        assertThat(find2.contains(access3), is(true));
        assertThat(find2.contains(access4), is(true));
        
        // 件数取得
        Long count1 = rest.getHierarchyCount(AccessHierarchyFujiTypeEnum.UnitHierarchy, 1L);
        assertThat(count1, is(1L));
        Long count2 = rest.getHierarchyCount(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, 2L);
        assertThat(count2, is(3L));
        
        // 範囲取得
        find1 = rest.getHierarchyRange(AccessHierarchyFujiTypeEnum.UnitHierarchy, 1L,0,count1.intValue());
        assertThat(find1.contains(access1), is(true));
        find2 = rest.getHierarchyRange(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, 2L,0,count2.intValue());
        assertThat(find2.contains(access2), is(true));
        assertThat(find2.contains(access3), is(true));
        assertThat(find2.contains(access4), is(true));
        
        // 削除
        tx.begin();
        rest.remove(AccessHierarchyFujiTypeEnum.UnitHierarchy, 1L, Arrays.asList(1L));
        rest.remove(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, 2L, Arrays.asList(2L,3L,4L));
        tx.commit();
        find1 = rest.find(AccessHierarchyFujiTypeEnum.UnitHierarchy, 1L);
        assertThat(find1.size(), is(0));
        find2 = rest.find(AccessHierarchyFujiTypeEnum.UnitTemplateHierarchy, 2L);
        assertThat(find2.size(), is(0));
    }
    
    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        AccessHierarchyFujiEntity access1 = new AccessHierarchyFujiEntity();
        access1.setTypeId(AccessHierarchyFujiTypeEnum.UnitHierarchy);
        access1.setFkHierarchyId(1L);
        access1.setFkOrganizationId(1L);
        tx.begin();
        response = rest.add(access1);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        AccessHierarchyFujiEntity access2 = new AccessHierarchyFujiEntity();
        access2.setTypeId(AccessHierarchyFujiTypeEnum.UnitHierarchy);
        access2.setFkHierarchyId(1L);
        access2.setFkOrganizationId(1L);
        tx.begin();
        response = rest.add(access2);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));
        
        tx.begin();
        response = rest.remove(AccessHierarchyFujiTypeEnum.UnitHierarchy, 1L, Arrays.asList(1L));
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }
}
