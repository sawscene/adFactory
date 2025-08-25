/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adFactory.entity.search.KanbanTopicSearchCondition;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.adfactoryserver.entity.actual.ActualResultEntity;
import jp.adtekfuji.adfactoryserver.entity.agenda.AbstractTopicEntity;
import jp.adtekfuji.adfactoryserver.entity.agenda.DatetimeConcurrent;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.master.DisplayedStatusEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class AgendaFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static AgendaFacadeREST agendaRest = null;
    private static KanbanEntityFacadeREST kanbanRest = null;
    private static ActualResultEntityFacadeREST actualResultRest = null;
    private static OrganizationEntityFacadeREST organizationRest = null;
    private static WorkEntityFacadeREST workREST = null;
    private static Map<StatusPatternEnum, DisplayedStatusEntity> statuses = new HashMap<>();
    private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public AgendaFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        agendaRest = new AgendaFacadeREST();
        agendaRest.setEntityManager(em);
//        workREST = new WorkEntityFacadeREST();
//        workREST.setEntityManager(em);
//        organizationRest = new OrganizationEntityFacadeREST();
//        organizationRest.setEntityManager(em);
//        actualResultRest = new ActualResultEntityFacadeREST();
//        actualResultRest.setEntityManager(em);
//        agendaRest.setOrganizationRest(organizationRest);
//        agendaRest.setWorkRest(workREST);
//        agendaRest.setActualResultRest(actualResultRest);
//
//        statuses.put(StatusPatternEnum.PLAN_NORMAL, new DisplayedStatusEntity(StatusPatternEnum.PLAN_NORMAL, null, null, null));
//        statuses.put(StatusPatternEnum.PLAN_DELAYSTART, new DisplayedStatusEntity(StatusPatternEnum.PLAN_DELAYSTART, null, null, null));
//        statuses.put(StatusPatternEnum.WORK_NORMAL, new DisplayedStatusEntity(StatusPatternEnum.WORK_NORMAL, null, null, null));
//        statuses.put(StatusPatternEnum.WORK_DELAYSTART, new DisplayedStatusEntity(StatusPatternEnum.WORK_DELAYSTART, null, null, null));
//        statuses.put(StatusPatternEnum.WORK_DELAYCOMP, new DisplayedStatusEntity(StatusPatternEnum.WORK_DELAYCOMP, null, null, null));
//        statuses.put(StatusPatternEnum.SUSPEND_NORMAL, new DisplayedStatusEntity(StatusPatternEnum.SUSPEND_NORMAL, null, null, null));
//        statuses.put(StatusPatternEnum.INTERRUPT_NORMAL, new DisplayedStatusEntity(StatusPatternEnum.INTERRUPT_NORMAL, null, null, null));
//        statuses.put(StatusPatternEnum.COMP_NORMAL, new DisplayedStatusEntity(StatusPatternEnum.COMP_NORMAL, null, null, null));
//        statuses.put(StatusPatternEnum.COMP_DELAYCOMP, new DisplayedStatusEntity(StatusPatternEnum.COMP_DELAYCOMP, null, null, null));
//        agendaRest.setStatuses(statuses);

        LicenseManager.setupTest();
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

    @Ignore
    @Test
    public void test() throws Exception {
        // TODO: [v2対応] テストを実装する。
    }
}
