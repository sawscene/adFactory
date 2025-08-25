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
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.ReasonCategoryEntity;
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
 * 理由区分RESTのテスト
 * 
 * @author HN)y-harada
 */
public class ReasonCategoryEntityFacedeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static ReasonCategoryEntityFacadeREST rest = null;
    private static ReasonMasterEntityFacadeREST reasonRest = null;

    /**
     * コンストラクタ
     */
    public ReasonCategoryEntityFacedeRESTTest() {
    }

    /**
     * テストクラスを初期化する。
     */
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        
        reasonRest = new ReasonMasterEntityFacadeREST();
        reasonRest.setEntityManager(em);

        rest = new ReasonCategoryEntityFacadeREST();
        rest.setEntityManager(em);
        rest.setReasonMasterEntityFacadeREST(reasonRest);
    }

    /**
     * テストクラスを終了する。
     */
    @AfterClass
    public static void tearDownClass() {
        if (Objects.nonNull(em)) {
            em.close();
        }
        if (Objects.nonNull(emf)) {
            emf.close();
        }
    }

    /**
     * テストを初期化する。
     */
    @Before
    public void setUp() {
        tx = em.getTransaction();
    }


    /**
     * テストを終了する。
     */
    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    /**
     * 追加・更新・削除・取得をテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        System.out.println("test");

        // 理由区分情報を登録する。
        ReasonCategoryEntity srcReason11 = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_CALL, "呼び出し①");
        ReasonCategoryEntity srcReason21 = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_DELAY, "遅延①");
        ReasonCategoryEntity srcReason31 = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_INTERRUPT, "中断①");
        ReasonCategoryEntity srcReason41 = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_DEFECT, "不良区分①");
        ReasonCategoryEntity srcReason42 = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_DEFECT, "不良区分②");
        ReasonCategoryEntity srcReason43 = new ReasonCategoryEntity(ReasonTypeEnum.TYPE_DEFECT, "不良区分③");

        // 理由区分情報一覧
        List<ReasonCategoryEntity> srcReasons = Arrays.asList(srcReason11, srcReason21, srcReason31, srcReason41, srcReason42, srcReason43);
        
        // 不良理由区分情報一覧
        List<ReasonCategoryEntity> defectReasons = srcReasons.stream()
                .filter(p -> ReasonTypeEnum.TYPE_DEFECT.equals(p.getReasonType())).collect(Collectors.toList());

        tx.begin();
        rest.add(srcReason11, null);
        rest.add(srcReason21, null);
        rest.add(srcReason31, null);
        rest.add(srcReason41, null);
        rest.add(srcReason42, null);
        rest.add(srcReason43, null);
        tx.commit();
        em.clear();

        // 不良理由区分を全件取得する。
        List<ReasonCategoryEntity> reasons = rest.findByType(ReasonTypeEnum.TYPE_DEFECT, null);
        // 取得した不良理由区分の件数を確認する。
        assertThat(reasons, hasSize(defectReasons.size()));
        
        // 登録した不良理由区分がすべて取得できたか確認する。
        for (ReasonCategoryEntity src : defectReasons) {
            long count = reasons.stream().filter(p -> p.getReasonCategoryName().equals(src.getReasonCategoryName())).count();
            assertThat(count, is(1L));
        }
        
        // 呼び出し理由区分を全件取得する。
        List<ReasonCategoryEntity> callReasons = rest.findByType(ReasonTypeEnum.TYPE_CALL, null);
        // 遅延理由区分を全件取得する。
        List<ReasonCategoryEntity> delayReasons = rest.findByType(ReasonTypeEnum.TYPE_DELAY, null);
        // 中断理由区分を全件取得する。
        List<ReasonCategoryEntity> interruptReasons = rest.findByType(ReasonTypeEnum.TYPE_INTERRUPT, null);
        // 取得した理由区分の件数を確認する。
        assertThat(reasons.size() + callReasons.size() + delayReasons.size() + interruptReasons.size(), is(srcReasons.size()));
        
        // 理由区分情報を更新する。
        ReasonCategoryEntity srcUpdateReason = reasons.get(0);
        long updateId = srcUpdateReason.getReasonCategoryId();
        srcUpdateReason.setReasonCategoryName(srcUpdateReason.getReasonCategoryName()+ "-更新");

        tx.begin();
        rest.update(srcUpdateReason, null);
        tx.commit();
        em.clear();
        
        // 更新した理由区分情報を取得する。
        ReasonCategoryEntity updateReason1 = rest.find(updateId, null);
        assertThat(updateReason1 , is(srcUpdateReason));
        
        // 更新した理由区分情報を区分名から取得する。
        ReasonCategoryEntity updateReason2 = rest.findName(srcUpdateReason.getReasonCategoryName(), srcUpdateReason.getReasonType(), null);
        assertThat(updateReason2, is(srcUpdateReason));

        // 理由区分情報を削除する。
        tx.begin();
        rest.remove(updateId, null);
        tx.commit();
        em.clear();
        // 削除した理由区分情報を取得する。
        ReasonCategoryEntity removeReason = rest.find(updateId, null);
        assertThat(removeReason, nullValue());// 削除済のためnullが返る。
        
    }
}
