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
import jp.adtekfuji.adfactoryserver.entity.master.LabelEntity;
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
 * ラベルマスタファサードのユニットテスト
 * 
 * @author s-heya
 */
public class LabelEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static LabelEntityFacadeREST rest = null;

    /**
     * コンストラクタ
     */
    public LabelEntityFacadeRESTTest() {
    }

    /**
     * テストクラスを初期化する。
     */
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        rest = new LabelEntityFacadeREST();
        rest.setEntityManager(em);
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
     * テストを終了化する。
     */
    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }

    /**
     * ラベルマスタの追加・取得をテストする。
     * 
     * @throws Exception 
     */
    @Test
    public void test() throws Exception {
        System.out.println("test");

        LabelEntity label1 = new LabelEntity("label1", "#000000", "#FFFFFF", 5);
        LabelEntity label2 = new LabelEntity("label2", "#000000", "#FFFFFF", 4);
        LabelEntity label3 = new LabelEntity("label3", "#000000", "#FFFFFF", 3);
        LabelEntity label4 = new LabelEntity("label4", "#000000", "#FFFFFF", 2);
        LabelEntity label5 = new LabelEntity("label5", "#000000", "#FFFFFF", 1);
        LabelEntity label6 = new LabelEntity("label6", "#000000", "#FFFFFF", 1);

        List<LabelEntity> labels = Arrays.asList(label1, label2, label3, label4, label5, label6);

        // 追加
        tx.begin();
        rest.add(label1, null);
        rest.add(label2, null);
        rest.add(label3, null);
        rest.add(label4, null);
        rest.add(label5, null);
        rest.add(label6, null);
        tx.commit();
        em.clear();

        // 全件取得
        List<LabelEntity> list = rest.findRange(null, null, null);
        assertThat(list, hasSize(labels.size()));
        for (LabelEntity label : labels) {
            long count = list.stream().filter(p -> p.getLabelName().equals(label.getLabelName())).count();
            assertThat(count, is(1L));
        }

        // 取得
        LabelEntity label = rest.findByName(label3.getLabelName(), null);
        assertThat(label, is(label3));
        em.detach(label);

        // 更新
        label.setLabelName(label.getLabelName() + "-更新");
        label.setFontColor("#333333");
        label.setBackColor("#AAAAAA");
        tx.begin();
        rest.update(label, null);
        tx.commit();
        em.clear();
        
        // 取得
        LabelEntity updatedLabel = rest.find(label3.getLabelId(), null);
        assertThat(updatedLabel, is(label));

        // 削除
        tx.begin();
        rest.remove(label3.getLabelId(), null);
        tx.commit();
        em.clear();
        
        LabelEntity removedLabel = rest.find(label3.getLabelId(), null);
        assertThat(removedLabel, nullValue());
    }
}
