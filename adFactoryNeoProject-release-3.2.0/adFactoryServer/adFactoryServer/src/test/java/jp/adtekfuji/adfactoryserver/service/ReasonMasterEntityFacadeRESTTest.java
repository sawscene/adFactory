/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonCategoryEntity;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonMasterEntity;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nar-nakamura
 */
public class ReasonMasterEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static ReasonMasterEntityFacadeREST rest = null;
    private static ReasonCategoryEntityFacadeREST reasonCategoryRest = null;
    
    public ReasonMasterEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        reasonCategoryRest = new ReasonCategoryEntityFacadeREST();
        reasonCategoryRest.setEntityManager(em);
        
        rest = new ReasonMasterEntityFacadeREST();
        rest.setEntityManager(em);
        rest.setReasonCategoryEntityFacadeREST(reasonCategoryRest);
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

        // 理由情報を登録する。
        ReasonMasterEntity srcReason11 = new ReasonMasterEntity(ReasonTypeEnum.TYPE_CALL, "呼び出し①","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        ReasonMasterEntity srcReason12 = new ReasonMasterEntity(ReasonTypeEnum.TYPE_CALL, "呼び出し②","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        ReasonMasterEntity srcReason13 = new ReasonMasterEntity(ReasonTypeEnum.TYPE_CALL, "呼び出し③","#000000", "#FFFFFF", LightPatternEnum.BLINK);
        ReasonMasterEntity srcReason21 = new ReasonMasterEntity(ReasonTypeEnum.TYPE_DELAY, "遅延①","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);
        ReasonMasterEntity srcReason31 = new ReasonMasterEntity(ReasonTypeEnum.TYPE_INTERRUPT, "中断①","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);

        // 理由情報一覧
        List<ReasonMasterEntity> srcReasons = Arrays.asList(srcReason11, srcReason12, srcReason13, srcReason21, srcReason31);
        // 呼び出し理由情報一覧
        List<ReasonMasterEntity> callReasons = srcReasons.stream()
                .filter(p -> ReasonTypeEnum.TYPE_CALL.equals(p.getReasonType())).collect(Collectors.toList());

        tx.begin();
        rest.add(srcReason11, null);
        rest.add(srcReason12, null);
        rest.add(srcReason13, null);
        rest.add(srcReason21, null);
        rest.add(srcReason31, null);
        tx.commit();
        em.clear();

        // 理由情報を全件取得する。
        List<ReasonMasterEntity> reasons = rest.findRange(null, null, null);
        // 取得した理由情報の件数を確認する。
        assertThat(reasons, hasSize(srcReasons.size()));
        // 登録した理由情報がすべて取得できたか確認する。
        for (ReasonMasterEntity src : srcReasons) {
            long count = reasons.stream().filter(p -> p.getReason().equals(src.getReason())).count();
            assertThat(count, is(1L));
        }

        // 呼び出し理由情報を取得する。
        reasons = rest.findByType(ReasonTypeEnum.TYPE_CALL, null, null, null);
        // 取得した呼び出し理由情報の件数を確認する。
        assertThat(reasons, hasSize(callReasons.size()));
        // 呼び出し理由情報がすべて取得できたか確認する。
        for (ReasonMasterEntity src : callReasons) {
            long count = reasons.stream().filter(p -> p.getReason().equals(src.getReason())).count();
            assertThat(count, is(1L));
        }

        // 理由情報を更新する。
        ReasonMasterEntity srcUpdateReason = reasons.get(0);
        long reasonId = srcUpdateReason.getReasonId();
        srcUpdateReason.setReason(srcUpdateReason.getReason() + "-更新");
        srcUpdateReason.setFontColor("#333333");
        srcUpdateReason.setBackColor("#AAAAAA");
        srcUpdateReason.setLightPattern(LightPatternEnum.BLINK);
        srcUpdateReason.setReasonOrder(99L);
        tx.begin();
        rest.update(srcUpdateReason, null);
        tx.commit();
        em.clear();
        // 更新した理由情報を取得する。
        ReasonMasterEntity updateReason = rest.find(reasonId, null);
        assertThat(updateReason, is(srcUpdateReason));

        // 理由情報を削除する。
        tx.begin();
        rest.remove(reasonId, null);
        tx.commit();
        em.clear();
        // 削除した理由情報を取得する。
        ReasonMasterEntity removeReason = rest.find(reasonId, null);
        assertThat(removeReason, nullValue());// 削除済のためnullが返る。
    }

    /**
     * findAllByCategoryのテストメソッド
     * 
     * @throws Exception 
     */
    @Test
    public void testFindAllByCategory() throws Exception {
        System.out.println("testFindAllByCategory");

        ReasonCategoryEntity reasonCategory = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_DEFECT, "不良区分①");
        ReasonMasterEntity reason = new ReasonMasterEntity(ReasonTypeEnum.TYPE_DEFECT, "不良①","#000000", "#FFFFFF", LightPatternEnum.LIGHTING);

        tx.begin();
        // 区分登録
        reasonCategoryRest.add(reasonCategory, null);

        ReasonCategoryEntity findCategory = reasonCategoryRest.findName(reasonCategory.getReasonCategoryName(), reasonCategory.getReasonType(), null);
        reason.setReasonCategoryId(findCategory.getReasonCategoryId());
        // 上記区分に属する理由を登録
        rest.add(reason, null);

        tx.commit();
        em.clear();

        // 区分IDで検索
        List<ReasonMasterEntity> reasonsGetById = rest.findByCategoryId(Arrays.asList(findCategory.getReasonCategoryId()), null);
        assertThat(reasonsGetById, hasItem(reason));
        // 区分IDで検索(失敗)
        reasonsGetById = rest.findByCategoryId(Arrays.asList(9999L), null);
        assertTrue(reasonsGetById.isEmpty());

        // 区分名で検索
        List<ReasonMasterEntity> reasonsGetByName = rest.findByCategoryName(reasonCategory.getReasonCategoryName(), reasonCategory.getReasonType(), null);
        assertThat(reasonsGetByName, hasItem(reason));
        // 区分名で検索(失敗)
        reasonsGetByName = rest.findByCategoryName("存在しない不良区分名", reasonCategory.getReasonType(), null);
        assertTrue(reasonsGetByName.isEmpty());

        // 引数なしで検索
        List<ReasonMasterEntity> reasonsGetByNull = rest.findByCategoryId(null, null);
        assertTrue(reasonsGetByNull.isEmpty());
        reasonsGetByNull = rest.findByCategoryName(null, null, null);
        assertTrue(reasonsGetByNull.isEmpty());

    }
}
