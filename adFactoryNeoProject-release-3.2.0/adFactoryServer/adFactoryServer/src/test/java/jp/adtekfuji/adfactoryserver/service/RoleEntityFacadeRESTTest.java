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
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.RoleAuthorityEntity;
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
 * 役割情報RESTのテストクラス
 * 
 * @author z-okado
 */
public class RoleEntityFacadeRESTTest {
    
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static RoleEntityFacadeREST rest = null;
    private static RoleAuthorityEntity entity1 = null;
    private static RoleAuthorityEntity entity2 = null;
    private static RoleAuthorityEntity entity3 = null;
    private static RoleAuthorityEntity entity4 = null;
    private static RoleAuthorityEntity entity5 = null;

    private Response restRes;
    private ResponseEntity res;
    
    /**
     * コンストラクタ
     */
    public RoleEntityFacadeRESTTest(){
    }
    
    /**
     * 最初の設定
     */
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        rest = new RoleEntityFacadeREST();
        rest.setEntityManager(em);
        
        entity1 = new RoleAuthorityEntity(1L, "role1");
        entity2 = new RoleAuthorityEntity(2L, "role2");
        entity3 = new RoleAuthorityEntity(3L, "role3");
        entity4 = new RoleAuthorityEntity(4L, "role4");
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
    public void setUp() {
        tx = em.getTransaction();
    }

    /**
     * DBの状態をリセットをする
     */
    @After
    public void tearDown() {
        DatabaseControll.reset(em, tx);
    }
    
    /**
     * 情報取得APIのテスト
     * @throws Exception 
     */
    @Test
    public void testRoleEntityFacadeRESTTest() throws Exception{
        
        //テストデータを作成
        entity1.setRoleName("role1");
        entity2.setRoleName("role2");
        entity3.setRoleName("role3");        
        tx.begin();
        rest.add(entity1, null);
        rest.add(entity2, null);
        rest.add(entity3, null);
        tx.commit();
        em.clear(); 
        
        //情報取得APIのテスト(find)
        RoleAuthorityEntity role = rest.find(entity1.getRoleId(), null);
        assertTrue("エンティティの一致", role.equals(entity1));
        assertThat(role.getRoleName(), is("role1"));
        
        //一覧取得APIのテスト(findAll)
        List<RoleAuthorityEntity> roles = rest.findAll(0L);
        assertThat(roles.size(), is(3));        
        assertTrue("エンティティの一致(entity1)", roles.stream().filter(e -> e.getRoleName().equals("role1")).allMatch(e -> e.equals(entity1)));
        assertTrue("エンティティの一致(entity2)", roles.stream().filter(e -> e.getRoleName().equals("role2")).allMatch(e -> e.equals(entity2)));
        assertTrue("エンティティの一致(entity3)", roles.stream().filter(e -> e.getRoleName().equals("role3")).allMatch(e -> e.equals(entity3)));
    
        //範囲を指定しての一覧取得APIのテスト(findRange)
        List<RoleAuthorityEntity> roles2 = rest.findRange(2, 3, null);
        assertThat(roles2.size(), is(1));        
        assertTrue("エンティティの一致(entity2)", roles2.stream().filter(e -> e.getRoleName().equals("role2")).allMatch(e -> e.equals(entity2)));

        //役割の件数取得APIのテスト(countAll)
        String rolesCount = rest.countAll(0L);
        assertThat(rolesCount, is("3"));
        
        //作成APIのテスト(add)
        tx.begin();
        restRes = rest.add(entity4, null);
        tx.commit();
        em.clear();        
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));        
        RoleAuthorityEntity role2 = rest.find(entity4.getRoleId(), null);
        assertTrue("エンティティの一致", role2.equals(entity4));
        assertThat(role2.getRoleName(), is("role4"));
        
        //更新APIのテスト(update)
        entity1.setRoleName("role5");        
        tx.begin();
        restRes = rest.update(entity1, 0L);
        tx.commit();
        em.clear();        
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        RoleAuthorityEntity role3 = rest.find(entity1.getRoleId(), null);
        assertThat(role3.getRoleName(), is("role5"));
        //更新API（update）の排他制御のテスト
        assertThat(role3.getVerInfo(), is(2));  //排他用バージョン１up確認
        entity5 = new RoleAuthorityEntity(entity1.getRoleId(), "role5_1");
        entity5.setVerInfo(1); //排他用バージョンに1を設定、２回目となる今回のupdateで排他用バージョン1のままとみなし排他確認
        tx.begin();
        restRes = rest.update(entity5, 0L);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.DIFFERENT_VER_INFO));//排他成功
        RoleAuthorityEntity role5 = rest.find(entity5.getRoleId(), null);
        assertThat(role5.getRoleName(), is("role5"));//更新されていないことを確認
        
        //削除APIのテスト(remove)
        Long target = entity2.getRoleId();
        tx.begin();
        restRes = rest.remove(entity2.getRoleId(), null);
        tx.commit();
        em.clear();
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        RoleAuthorityEntity role4 = rest.find(target, null);
        assertNull(role4.getRoleId());
    }
}
