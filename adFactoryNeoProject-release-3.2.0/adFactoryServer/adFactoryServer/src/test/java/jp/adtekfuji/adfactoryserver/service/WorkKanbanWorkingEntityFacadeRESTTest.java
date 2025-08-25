/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanWorkingEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class WorkKanbanWorkingEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static WorkKanbanWorkingEntityFacadeREST workKanbanWorkingRest = null;

    public WorkKanbanWorkingEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        workKanbanWorkingRest = new WorkKanbanWorkingEntityFacadeREST();
        workKanbanWorkingRest.setEntityManager(em);
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
     * Test of updateWorking method, of class WorkKanbanWorkingEntityFacadeREST.
     */
    @Test
    public void testUpdateWorking() {
        System.out.println("updateWorking");

        WorkKanbanWorkingEntity working1 = new WorkKanbanWorkingEntity(100L, 201L, 301L, false);
        WorkKanbanWorkingEntity working2 = new WorkKanbanWorkingEntity(100L, 202L, 302L,true);
        WorkKanbanWorkingEntity working3 = new WorkKanbanWorkingEntity(100L, 203L, 303L,false);
        WorkKanbanWorkingEntity working4 = new WorkKanbanWorkingEntity(101L, 204L, 304L,true);
        long result = 0;

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.PLANNED, working1, false);
        tx.commit();
        assertThat(result, is(0L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working1, false);
        tx.commit();
        assertThat(result, is(1L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.COMPLETION, working1, false);
        tx.commit();
        assertThat(result, is(0L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working1, false);
        tx.commit();
        assertThat(result, is(1L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working2, false);
        tx.commit();
        assertThat(result, is(2L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working3, false);
        tx.commit();
        assertThat(result, is(3L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working4, false);
        tx.commit();
        assertThat(result, is(1L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.SUSPEND, working1, false);
        tx.commit();
        assertThat(result, is(2L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.COMPLETION, working2, false);
        tx.commit();
        assertThat(result, is(1L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.COMPLETION, working3, false);
        tx.commit();
        assertThat(result, is(0L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.COMPLETION, working4, false);
        tx.commit();
        assertThat(result, is(0L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working1, false);
        tx.commit();
        assertThat(result, is(1L));

        tx.begin();
        result = workKanbanWorkingRest.updateWorking(KanbanStatusEnum.WORKING, working1, false);
        tx.commit();
        assertThat(result, is(1L));

        tx.begin();
        workKanbanWorkingRest.deleteWorking(working1.getWorkKanbanId());
        tx.commit();
        List<WorkKanbanWorkingEntity> entities = workKanbanWorkingRest.getWorking(working1.getWorkKanbanId());
        assertThat(entities.size(), is(0));
    }
}
