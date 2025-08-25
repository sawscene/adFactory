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
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adfactoryserver.entity.master.DisplayedStatusEntity;
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
public class DisplayedStatusEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static DisplayedStatusEntityFacadeREST rest = null;

    public DisplayedStatusEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        rest = new DisplayedStatusEntityFacadeREST();
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
    public void test() throws Exception {
        System.out.println("test");

        int statusCount = 11;// DisplayedStatusEntityFacadeREST.createDisplayedStatusTable で登録した情報の件数

        // ステータス表示情報の件数を取得する。
        tx.begin();
        int count = Integer.valueOf(rest.countAll(null));
        tx.commit();
        em.clear();
        assertThat(count, is(statusCount));

        // ステータス表示情報を全件取得する。
        tx.begin();
        List<DisplayedStatusEntity> statuses = rest.findAll(null);
        tx.commit();
        em.clear();
        // 取得したステータス表示情報の件数を確認する。
        assertThat(statuses, hasSize(statusCount));

        // ステータス表示情報を更新する。
        DisplayedStatusEntity srcUpdateStatus = statuses.get(0);
        long statusId = srcUpdateStatus.getStatusId();
        srcUpdateStatus.setFontColor("#333333");
        srcUpdateStatus.setBackColor("#AAAAAA");
        srcUpdateStatus.setLightPattern(LightPatternEnum.BLINK);
        srcUpdateStatus.setNotationName(srcUpdateStatus.getNotationName() + "-更新");
        srcUpdateStatus.setMelodyPath(srcUpdateStatus.getMelodyPath() + "-更新");
        srcUpdateStatus.setMelodyRepeat(true);
        tx.begin();
        rest.update(srcUpdateStatus, null);
        tx.commit();
        em.clear();
        // 更新した理由を取得する。
        tx.begin();
        DisplayedStatusEntity updateStatus = rest.find(statusId, null);
        tx.commit();
        em.clear();
        assertThat(updateStatus, is(srcUpdateStatus));
    }
}
