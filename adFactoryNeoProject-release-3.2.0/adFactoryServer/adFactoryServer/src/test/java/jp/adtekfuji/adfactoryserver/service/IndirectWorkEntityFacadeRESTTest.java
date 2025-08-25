/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.IndirectWorkEntity;
import jp.adtekfuji.adfactoryserver.entity.indirectwork.WorkCategoryEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * 間接作業情報取得用RESTのテストクラス
 * 
 * @author z-okado
 */
public class IndirectWorkEntityFacadeRESTTest {
    
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static IndirectWorkEntityFacadeREST IndirectWorkEntityRest = null;
    private static WorkCategoryEntityFacedeREST WorkCategoryEntityRest = null;
    private static IndirectWorkEntity entity1 = null;
    private static IndirectWorkEntity entity2 = null;
    private static IndirectWorkEntity entity3 = null;
    private static IndirectWorkEntity entity4 = null;
    private static IndirectWorkEntity entity5 = null;
    private static WorkCategoryEntity workCategory1 = null;
    private static WorkCategoryEntity workCategory2 = null;
    private static WorkCategoryEntity workCategory3 = null;
    private static WorkCategoryEntity workCategory4 = null;
    private Response restRes;
    private ResponseEntity res;
    
    /**
     * コンストラクタ
     */
    public IndirectWorkEntityFacadeRESTTest(){
    }
    
    /**
     * 最初の設定
     */    
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        IndirectWorkEntityRest = new IndirectWorkEntityFacadeREST();
        IndirectWorkEntityRest.setEntityManager(em);
        WorkCategoryEntityRest = new WorkCategoryEntityFacedeREST();
        WorkCategoryEntityRest.setEntityManager(em);
        
        workCategory1 = new WorkCategoryEntity("workCategory1");
        workCategory2 = new WorkCategoryEntity("workCategory2");
        workCategory3 = new WorkCategoryEntity("workCategory3");
        workCategory4 = new WorkCategoryEntity("workCategory4");
        
        entity1 = new IndirectWorkEntity("1", "workName1", 1L);
        entity2 = new IndirectWorkEntity("2", "workName2", 2L);
        entity3 = new IndirectWorkEntity("3", "workName3", 2L);
        entity4 = new IndirectWorkEntity("4", "workName4", 3L);
    }
        
    /**
     * リソース解放
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
     * トランザクションを取得
     */
    @Before
    public void setUp() throws Exception {
        tx = em.getTransaction();
    }

    /**
     * DBの状態をリセットをする
     */
    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
        em.clear();
    }

    /**
     * 間接作業情報のテスト
     * @throws Exception 
     */
    @Test
    public void IndirectWorkEntityFacadeRESTTest() throws Exception{

        //テストデータ作成(作業区分)
        workCategory1.setWorkCategoryId(1L);
        workCategory2.setWorkCategoryId(2L);
        workCategory3.setWorkCategoryId(3L);
        workCategory4.setWorkCategoryId(4L);
        tx.begin();
        restRes = WorkCategoryEntityRest.add(workCategory1, null);
        WorkCategoryEntityRest.add(workCategory2, null);
        WorkCategoryEntityRest.add(workCategory3, null);
        tx.commit();
        em.clear();
        
        //テストデータ作成(間接作業)
        entity1.setIndirectWorkId(1L);
        entity2.setIndirectWorkId(2L);
        entity3.setIndirectWorkId(3L);
        entity4.setIndirectWorkId(4L);
        entity1.setClassNumber("1");
        entity2.setClassNumber("2");
        entity3.setClassNumber("3");
        entity4.setClassNumber("4");
        entity1.setWorkName("workName1");
        entity2.setWorkName("workName2");
        entity3.setWorkName("workName3");
        entity4.setWorkName("workName4");
        entity1.setWorkCategoryId(workCategory1.getWorkCategoryId());
        entity2.setWorkCategoryId(workCategory2.getWorkCategoryId());
        entity3.setWorkCategoryId(workCategory2.getWorkCategoryId());
        entity4.setWorkCategoryId(workCategory3.getWorkCategoryId());
        tx.begin();
        IndirectWorkEntityRest.add(entity1, null);
        IndirectWorkEntityRest.add(entity2, null);
        restRes = IndirectWorkEntityRest.add(entity3, null);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        
        //間接作業情報取得のテスト(find)
        IndirectWorkEntity work = IndirectWorkEntityRest.find(entity1.getIndirectWorkId(), null);
        assertTrue("エンティティの一致", work.equals(entity1));
        assertThat(work.getWorkName(), is("workName1"));
        
        //間接作業情報取得(全件)のテスト(findAll)
        List<IndirectWorkEntity> works = IndirectWorkEntityRest.findAll(0L);
        assertThat(works.size(), is(3));
        assertTrue("エンティティの一致(entity1)", works.stream().filter(e -> e.getWorkName().equals("workName1")).allMatch(e -> e.equals(entity1)));
        assertTrue("エンティティの一致(entity2)", works.stream().filter(e -> e.getWorkName().equals("workName2")).allMatch(e -> e.equals(entity2)));
        assertTrue("エンティティの一致(entity3)", works.stream().filter(e -> e.getWorkName().equals("workName3")).allMatch(e -> e.equals(entity3)));
        
        //間接作業情報取得(範囲指定)のテスト(findRange)
        List<IndirectWorkEntity> works2 = IndirectWorkEntityRest.findRange(1, 2, null);
        assertThat(works2.size(), is(2));        
        assertTrue("エンティティの一致(entity1)", works2.stream().filter(e -> e.getWorkName().equals("workName1")).allMatch(e -> e.equals(entity1)));
        assertTrue("エンティティの一致(entity2)", works2.stream().filter(e -> e.getWorkName().equals("workName2")).allMatch(e -> e.equals(entity2)));
        
        //間接作業情報件数取得のテスト(countAll)
        String worksCount = IndirectWorkEntityRest.countAll(0L);
        assertThat(worksCount, is("3"));
        
        //指定した分類番号・作業番号の間接作業取得のテスト(findByWorkNumber)
        IndirectWorkEntity work3 = IndirectWorkEntityRest.findByWorkNumber("1", "1", 0L);
        assertTrue("エンティティの一致", work3.equals(entity1));
        assertThat(work3.getWorkName(), is("workName1"));
        
        //間接作業情報登録のテスト(add)
        tx.begin();
        restRes = IndirectWorkEntityRest.add(entity4, null);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        IndirectWorkEntity work4 = IndirectWorkEntityRest.find(entity4.getIndirectWorkId(), null);
        assertTrue("エンティティの一致", work4.equals(entity4));
        assertThat(work4.getWorkName(), is("workName4"));
                
        //間接作業情報更新のテスト(update)
        entity1.setWorkName("workName5");
        tx.begin();
        restRes = IndirectWorkEntityRest.update(entity1, 0L);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        IndirectWorkEntity work5 = IndirectWorkEntityRest.find(entity1.getIndirectWorkId(), null);
        assertThat(work5.getWorkName(), is("workName5"));
        //間接作業情報更新（update）の排他制御のテスト
        assertThat(work5.getVerInfo(), is(2));  //排他用バージョン１up確認
        entity5 = new IndirectWorkEntity(entity1.getWorkNumber(),
                                         "workName5_1",
                                         entity1.getWorkCategoryId());
        entity5.setIndirectWorkId(entity1.getIndirectWorkId());
        entity5.setVerInfo(1);
        tx.begin();
        restRes = IndirectWorkEntityRest.update(entity5, 0L);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.DIFFERENT_VER_INFO));
        IndirectWorkEntity work8 = IndirectWorkEntityRest.find(entity5.getIndirectWorkId(), null);
        assertThat(work8.getWorkName(), is("workName5"));//更新されていないことを確認
                
        //間接作業情報削除のテスト(remove)
        Long target = entity2.getIndirectWorkId();
        tx.begin();
        restRes = IndirectWorkEntityRest.remove(entity2.getIndirectWorkId(), null);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        IndirectWorkEntity work6 = IndirectWorkEntityRest.find(target, null);
        assertNull(work6);        
        
        //テストデータ作成(間接作業)
        entity1.setIndirectWorkId(1L);
        entity2.setIndirectWorkId(2L);
        entity4.setIndirectWorkId(4L);
        entity1.setClassNumber("1");
        entity2.setClassNumber("2");
        entity4.setClassNumber("4");
        entity1.setWorkName("workName1");
        entity2.setWorkName("workName2");
        entity4.setWorkName("workName4");
        entity1.setWorkCategoryId(workCategory1.getWorkCategoryId());
        entity2.setWorkCategoryId(workCategory2.getWorkCategoryId());
        entity4.setWorkCategoryId(workCategory3.getWorkCategoryId());
        tx.begin();
        IndirectWorkEntityRest.add(entity1, null);
        IndirectWorkEntityRest.add(entity2, null);
        tx.commit();
        em.clear();
        
        //作業区分取得のテスト(findCategory)
        List<Long> workCategoryIds = new ArrayList<>();
        workCategoryIds.add(entity1.getWorkCategoryId());
        workCategoryIds.add(entity2.getWorkCategoryId());
        // オーバーロードのメソッドを両方テストする
        // 引数二つ版
        List<IndirectWorkEntity> works7 = IndirectWorkEntityRest.findCategory(workCategoryIds, 0L);
        assertThat(works7.size(), is(3));
        assertTrue("エンティティの一致(entity1)", works7.stream().filter(e -> e.getWorkName().equals("workName1")).allMatch(e -> e.equals(entity1)));
        assertTrue("エンティティの一致(entity2)", works7.stream().filter(e -> e.getWorkName().equals("workName2")).allMatch(e -> e.equals(entity2)));
        assertTrue("エンティティの一致(entity3)", works7.stream().filter(e -> e.getWorkName().equals("workName3")).allMatch(e -> e.equals(entity3)));
        // 引数四つ版
        List<IndirectWorkEntity> works8 = IndirectWorkEntityRest.findCategory(workCategoryIds, 1, 2, 0L);
        assertThat(works8.size(), is(2));
        assertTrue("エンティティの一致(entity1)", works8.stream().filter(e -> e.getWorkName().equals("workName1")).allMatch(e -> e.equals(entity1)));
        assertTrue("エンティティの一致(entity2)", works8.stream().filter(e -> e.getWorkName().equals("workName2")).allMatch(e -> e.equals(entity2)));
        
        //作業区分件数取得のテスト(countCategory)
        List<Long> workCategoryIds2 = new ArrayList<>();
        workCategoryIds2.add(entity1.getWorkCategoryId());
        workCategoryIds2.add(entity2.getWorkCategoryId());
        String worksCount2 = IndirectWorkEntityRest.countCategory(workCategoryIds2, 0L);
        assertThat(worksCount2, is("3"));
    }
}
