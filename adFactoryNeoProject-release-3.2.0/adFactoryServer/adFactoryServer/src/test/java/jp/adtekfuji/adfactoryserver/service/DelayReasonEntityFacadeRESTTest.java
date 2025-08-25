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
import jp.adtekfuji.adfactoryserver.entity.master.DelayReasonEntity;
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
public class DelayReasonEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static DelayReasonEntityFacadeREST rest = null;
    private static ReasonMasterEntityFacadeREST reasonRest = null;

    public DelayReasonEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        reasonRest = new ReasonMasterEntityFacadeREST();
        reasonRest.setEntityManager(em);

        rest = new DelayReasonEntityFacadeREST();
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

        // 遅延理由を登録する。
        DelayReasonEntity srcReason1 = new DelayReasonEntity("遅延①","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        DelayReasonEntity srcReason2 = new DelayReasonEntity("遅延②","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        DelayReasonEntity srcReason3 = new DelayReasonEntity("遅延③","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);

        // 遅延理由一覧
        List<DelayReasonEntity> srcReasons = Arrays.asList(srcReason1, srcReason2, srcReason3);

        tx.begin();
        rest.add(srcReason1, null);
        rest.add(srcReason2, null);
        rest.add(srcReason3, null);
        tx.commit();
        em.clear();

        // 遅延理由を全件取得する。
        List<DelayReasonEntity> reasons = rest.findAll(null);
        // 取得した遅延理由の件数を確認する。
        assertThat(reasons, hasSize(srcReasons.size()));
        // 登録した遅延理由がすべて取得できたか確認する。
        for (DelayReasonEntity src : srcReasons) {
            long count = reasons.stream().filter(p -> p.getDelayReason().equals(src.getDelayReason())).count();
            assertThat(count, is(1L));
        }

        // 遅延理由を更新する。
        DelayReasonEntity srcUpdateReason = reasons.get(0);
        long reasonId = srcUpdateReason.getDelayId();
        srcUpdateReason.setReason(srcUpdateReason.getReason() + "-更新");
        srcUpdateReason.setFontColor("#333333");
        srcUpdateReason.setBackColor("#AAAAAA");
        srcUpdateReason.setLightPattern(LightPatternEnum.BLINK);
        srcUpdateReason.setReasonOrder(99L);
        tx.begin();
        rest.update(srcUpdateReason, null);
        tx.commit();
        em.clear();
        // 更新した遅延理由を取得する。
        DelayReasonEntity updateReason = rest.find(reasonId, null);
        assertThat(updateReason, is(srcUpdateReason));

        // 遅延理由を削除する。
        tx.begin();
        rest.remove(reasonId, null);
        tx.commit();
        em.clear();
        // 削除した遅延理由を取得する。
        DelayReasonEntity removeReason = rest.find(reasonId, null);
        assertThat(removeReason, nullValue());// 削除済のためnullが返る。
    }
}
