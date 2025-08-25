/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.WorkCategoryEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * WorkCategoryEntityFacedeREST JUnitテスト クラス
 * 
 * @author s-heya
 */
public class WorkCategoryEntityFacedeRESTTest {
    
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static WorkCategoryEntityFacedeREST rest = null;
    private static IndirectWorkEntityFacadeREST indirectWorkREST = null;

    public WorkCategoryEntityFacedeRESTTest() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        indirectWorkREST = new IndirectWorkEntityFacadeREST();
        indirectWorkREST.setEntityManager(em);
        rest = new WorkCategoryEntityFacedeREST(em, indirectWorkREST);
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * 基本テスト
     */
    @Test
    public void testBasic() throws Exception {
        System.out.println("testBasic");

        WorkCategoryEntity _category1 = new WorkCategoryEntity("category1");
        WorkCategoryEntity _category2 = new WorkCategoryEntity("category2");

        tx.begin();
        rest.add(_category1, null);
        rest.add(_category2, null);
        tx.commit();
        em.clear();
        
        WorkCategoryEntity category1 = rest.findName(_category1.getWorkCategoryName(), null);
        assertThat(category1.getWorkCategoryName(), is(category1.getWorkCategoryName()));
        WorkCategoryEntity category2 = rest.findName(_category2.getWorkCategoryName(), null);
        assertThat(_category2.getWorkCategoryName(), is(category2.getWorkCategoryName()));

        List<WorkCategoryEntity> categories = rest.findRange(null, null, null);
        assertThat(categories.contains(category1), is(true));
        assertThat(categories.contains(category2), is(true));
                
        IndirectWorkEntity _indirectWor1 = new IndirectWorkEntity("1", "work1", category1.getWorkCategoryId());
        IndirectWorkEntity _indirectWor2 = new IndirectWorkEntity("2", "work2", category1.getWorkCategoryId());
        IndirectWorkEntity _indirectWor3 = new IndirectWorkEntity("3", "work3", category1.getWorkCategoryId());
        IndirectWorkEntity _indirectWor4 = new IndirectWorkEntity("4", "work4", category2.getWorkCategoryId());

        // add
        tx.begin();
        indirectWorkREST.add(_indirectWor1, null);
        indirectWorkREST.add(_indirectWor2, null);
        indirectWorkREST.add(_indirectWor3, null);
        indirectWorkREST.add(_indirectWor4, null);
        tx.commit();
        em.clear();

        List<IndirectWorkEntity> list1 = indirectWorkREST.findCategory(Arrays.asList(category2.getWorkCategoryId()), 0L);
        assertThat(list1.size(), is(1));

        List<IndirectWorkEntity> list2 = indirectWorkREST.findCategory(Arrays.asList(category1.getWorkCategoryId(), category2.getWorkCategoryId()), 0L);
        assertThat(list2.size(), is(4));

        // update
        category1.setWorkCategoryName("category");
        tx.begin();
        rest.update(category1, null);
        tx.commit();
        em.clear();

        category1 = rest.find(category1.getWorkCategoryId(), null);
        assertThat(category1.getWorkCategoryName(), is("category"));

        // delete
        tx.begin();
        rest.remove(category2.getWorkCategoryId(), null);
        tx.commit();
        em.clear();

        categories = rest.findRange(null, null, null);
        assertThat(categories.contains(category2), is(true));
        
        for (IndirectWorkEntity indirectWor : list1) {
            tx.begin();
            indirectWorkREST.remove(indirectWor.getIndirectWorkId(), null);
            tx.commit();
            em.clear();
        }
        
        list1 = indirectWorkREST.findCategory(Arrays.asList(category2.getWorkCategoryId()), 0L);
        assertThat(list1.size(), is(0));

        tx.begin();
        rest.remove(category2.getWorkCategoryId(), null);
        tx.commit();
        em.clear();

        categories = rest.findRange(null, null, null);
        assertThat(categories.contains(category1), is(true));
        assertThat(categories.contains(category2), is(false));
    }
}
