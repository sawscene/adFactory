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
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adfactoryserver.entity.master.InterruptReasonEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 *
 * @author nar-nakamura
 */
public class InterruptReasonEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static InterruptReasonEntityFacadeREST rest = null;
    private static ReasonMasterEntityFacadeREST reasonRest = null;

    public InterruptReasonEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        reasonRest = new ReasonMasterEntityFacadeREST();
        reasonRest.setEntityManager(em);

        rest = new InterruptReasonEntityFacadeREST();
        rest.setReasonRest(reasonRest);
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
    public void test() throws Exception {
        System.out.println("test");

        // 中断理由を登録する。
        InterruptReasonEntity srcReason1 = new InterruptReasonEntity("中断①","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        InterruptReasonEntity srcReason2 = new InterruptReasonEntity("中断②","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        InterruptReasonEntity srcReason3 = new InterruptReasonEntity("中断③","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);

        // 中断理由一覧
        List<InterruptReasonEntity> srcReasons = Arrays.asList(srcReason1, srcReason2, srcReason3);

        tx.begin();
        rest.add(srcReason1, null);
        rest.add(srcReason2, null);
        rest.add(srcReason3, null);
        tx.commit();
        em.clear();

        // 中断理由を全件取得する。
        List<InterruptReasonEntity> reasons = rest.findAll(null);
        // 取得した中断理由の件数を確認する。
        assertThat(reasons, hasSize(srcReasons.size()));
        // 登録した中断理由がすべて取得できたか確認する。
        for (InterruptReasonEntity src : srcReasons) {
            long count = reasons.stream().filter(p -> p.getInterruptReason().equals(src.getInterruptReason())).count();
            assertThat(count, is(1L));
        }

        // 中断理由を更新する。
        InterruptReasonEntity srcUpdateReason = reasons.get(0);
        long reasonId = srcUpdateReason.getInterruptId();
        srcUpdateReason.setReason(srcUpdateReason.getReason() + "-更新");
        srcUpdateReason.setFontColor("#333333");
        srcUpdateReason.setBackColor("#AAAAAA");
        srcUpdateReason.setLightPattern(LightPatternEnum.BLINK);
        srcUpdateReason.setReasonOrder(99L);
        tx.begin();
        rest.update(srcUpdateReason, null);
        tx.commit();
        em.clear();
        // 更新した中断理由を取得する。
        InterruptReasonEntity updateReason = rest.find(reasonId, null);
        assertThat(updateReason, is(srcUpdateReason));

        // 中断理由を削除する。
        tx.begin();
        rest.remove(reasonId, null);
        tx.commit();
        em.clear();
        reasons = rest.findAll(null);
        // 削除した中断理由を取得する。
        InterruptReasonEntity removeReason = rest.find(reasonId, null);
        assertThat(removeReason, nullValue());// 削除済のためnullが返る。
    }
}
