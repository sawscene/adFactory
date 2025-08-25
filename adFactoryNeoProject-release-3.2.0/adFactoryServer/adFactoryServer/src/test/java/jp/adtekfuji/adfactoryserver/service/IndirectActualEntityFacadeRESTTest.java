/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adFactory.entity.search.IndirectActualSearchCondition;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectActualEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.WorkCategoryEntity;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 *
 * @author nar-nakamura
 */
public class IndirectActualEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static IndirectActualEntityFacadeREST rest = null;
    private static WorkCategoryEntityFacedeREST workCategoryRest = null;
    private static IndirectWorkEntityFacadeREST indirectWorkRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;

    public IndirectActualEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        indirectWorkRest = new IndirectWorkEntityFacadeREST();
        indirectWorkRest.setEntityManager(em);

        workCategoryRest = new WorkCategoryEntityFacedeREST(em, indirectWorkRest);

        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        rest = new IndirectActualEntityFacadeREST();
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
    public void setUp() {
        tx = em.getTransaction();
    }

    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    @Test
    public void testIndirectActualEntityFacadeRESTTest() throws Exception {
        System.out.println("testIndirectActualEntityFacadeRESTTest");

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        WorkCategoryEntity cat1 = new WorkCategoryEntity("category1");

        tx.begin();
        workCategoryRest.add(cat1, null);
        tx.commit();

        IndirectWorkEntity work1 = new IndirectWorkEntity("IW-001", "work1", cat1.getWorkCategoryId());
        IndirectWorkEntity work2 = new IndirectWorkEntity("IW-002", "work2", cat1.getWorkCategoryId());

        tx.begin();
        indirectWorkRest.add(work1, null);
        indirectWorkRest.add(work2, null);
        tx.commit();

        OrganizationEntity person1 = new OrganizationEntity(0L, "parson1", "identname1", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity person2 = new OrganizationEntity(0L, "parson2", "identname2", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        organizationRest.add(person1, null);
        organizationRest.add(person2, null);
        tx.commit();

        IndirectActualEntity act1 = new IndirectActualEntity(null, work1.getIndirectWorkId(), df.parse("2019/11/05 09:00:00"), 0L, person1.getOrganizationId(), 10 * 60 * 1000);
        IndirectActualEntity act2 = new IndirectActualEntity(null, work1.getIndirectWorkId(), df.parse("2019/11/05 09:10:00"), 0L, person1.getOrganizationId(), 10 * 60 * 1000);
        IndirectActualEntity act3 = new IndirectActualEntity(null, work1.getIndirectWorkId(), df.parse("2019/11/05 09:20:00"), 0L, person1.getOrganizationId(), 10 * 60 * 1000);
        IndirectActualEntity act4 = new IndirectActualEntity(null, work2.getIndirectWorkId(), df.parse("2019/11/05 09:30:00"), 0L, person1.getOrganizationId(), 10 * 60 * 1000);
        IndirectActualEntity act5 = new IndirectActualEntity(null, work2.getIndirectWorkId(), df.parse("2019/11/05 09:40:00"), 0L, person1.getOrganizationId(), 10 * 60 * 1000);

        // add
        tx.begin();
        rest.add(act1, null);
        rest.add(act2, null);
        rest.add(act3, null);
        rest.add(act4, null);
        rest.add(act5, null);
        tx.commit();
        em.clear();

        IndirectActualEntity actuals1 = rest.find(act1.getIndirectActualId(), null);
        assertThat(act1, is(actuals1));

        IndirectActualEntity actuals2 = rest.find(act2.getIndirectActualId(), null);
        assertThat(act2, is(actuals2));

        IndirectActualEntity actuals3 = rest.find(act3.getIndirectActualId(), null);
        assertThat(act3, is(actuals3));

        IndirectActualEntity actuals4 = rest.find(act4.getIndirectActualId(), null);
        assertThat(act4, is(actuals4));

        IndirectActualEntity actuals5 = rest.find(act5.getIndirectActualId(), null);
        assertThat(act5, is(actuals5));

        List<IndirectActualEntity> acts = Arrays.asList(act1, act2, act3, act4, act5);

        int count;
        List<IndirectActualEntity> actuals;

        // countAll
        count = Integer.parseInt(rest.countAll(null));
        assertThat(count, is(acts.size()));

        // findAll
        actuals = rest.findAll(null);
        assertThat(actuals, hasSize(acts.size()));
        // findRange
        List<IndirectActualEntity> rangeActuals = rest.findRange(null, null, null);
        assertThat(rangeActuals, hasSize(acts.size()));

        // search
        IndirectActualSearchCondition condition = new IndirectActualSearchCondition();
        condition.setFromDate(df.parse("2019/11/05 09:10:00"));
        condition.setToDate(df.parse("2019/11/05 09:30:00"));
        condition.setOrganizationCollection(Arrays.asList(person1.getOrganizationId()));

        count = Integer.parseInt(rest.countIndirectActual(condition, null));
        assertThat(count, is(3));

        List<IndirectActualEntity> condActuals = rest.searchIndirectActual(condition, null, null, null);
        assertThat(condActuals, hasSize(3));

        // update
        act1.setIndirectWorkId(work2.getIndirectWorkId());
        act2.setImplementDatetime(df.parse("2019/11/05 10:10:00"));
        act3.setOrganizationId(person2.getOrganizationId());
        act4.setWorkTime(5 * 60 * 1000);

        tx.begin();
        rest.update(act1, null);
        rest.update(act2, null);
        rest.update(act3, null);
        rest.update(act4, null);
        rest.update(act5, null);
        tx.commit();
        em.clear();

        actuals1 = rest.find(act1.getIndirectActualId(), null);
        assertThat(act1, is(actuals1));

        actuals2 = rest.find(act2.getIndirectActualId(), null);
        assertThat(act2, is(actuals2));

        actuals3 = rest.find(act3.getIndirectActualId(), null);
        assertThat(act3, is(actuals3));

        actuals4 = rest.find(act4.getIndirectActualId(), null);
        assertThat(act4, is(actuals4));

        actuals5 = rest.find(act5.getIndirectActualId(), null);
        assertThat(act5, is(actuals5));

        // search
        count = Integer.parseInt(rest.countIndirectActual(condition, null));
        assertThat(count, is(1));

        condActuals = rest.searchIndirectActual(condition, null, null, null);
        assertThat(condActuals, hasSize(1));

        // delete
        for (IndirectActualEntity act : acts) {
            tx.begin();
            rest.remove(act.getIndirectActualId(), null);
            tx.commit();
            em.clear();
        }

        actuals = rest.findAll(null);
        assertThat(actuals, hasSize(0));
    }
}
