/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.text.SimpleDateFormat;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.master.BreaktimeEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nar-nakamura
 */
public class BreaktimeEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static BreaktimeEntityFacadeREST rest = null;

    public BreaktimeEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        rest = new BreaktimeEntityFacadeREST();
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

        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        BreaktimeEntity breaktime1 = new BreaktimeEntity("昼休憩", datetimeFormat.parse("1970/01/01 12:00:00"), datetimeFormat.parse("1970/01/01 13:00:00"));

        Response restRes;
        ResponseEntity res;

        // add
        tx.begin();
        restRes = rest.add(breaktime1, null);
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        tx.commit();

        em.clear();
        BreaktimeEntity result1 = rest.find(breaktime1.getBreaktimeId(), null);
        assertThat(breaktime1, is(result1));

        // update
        breaktime1.setEndtime(datetimeFormat.parse("1970/01/01 14:00:00"));

        tx.begin();
        restRes = rest.update(breaktime1, null);
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        tx.commit();

        em.clear();
        result1 = rest.find(breaktime1.getBreaktimeId(), null);
        breaktime1.setVerInfo(2);// updateで排他バージョンが更新されているはず
        assertThat(result1, is(breaktime1));

        // update 排他バージョンエラー
        BreaktimeEntity breaktime1a = new BreaktimeEntity("休憩①", datetimeFormat.parse("1970/01/01 10:00:00"), datetimeFormat.parse("1970/01/01 11:00:00"));
        breaktime1a.setBreaktimeId(breaktime1.getBreaktimeId());
        breaktime1a.setVerInfo(1);// 古い排他バージョンをセット

        tx.begin();
        restRes = rest.update(breaktime1a, null);
        res = (ResponseEntity) restRes.getEntity();
        assertThat(res.getErrorType(), is(ServerErrorTypeEnum.DIFFERENT_VER_INFO));// 排他バージョンのエラー
        tx.commit();

        em.clear();
        result1 = rest.find(breaktime1.getBreaktimeId(), null);
        assertThat(result1, is(breaktime1));
    }
}
