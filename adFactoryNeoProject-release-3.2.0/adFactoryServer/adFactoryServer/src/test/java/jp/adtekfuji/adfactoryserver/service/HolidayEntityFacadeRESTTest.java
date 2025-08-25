/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import static java.lang.Integer.parseInt;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.search.HolidaySearchCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.holiday.HolidayEntity;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
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
public class HolidayEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static HolidayEntityFacadeREST rest = null;

    public HolidayEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        rest = new HolidayEntityFacadeREST();
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
    public void testHolidayEntityFacadeREST() throws Exception {
        System.out.println("testHolidayEntityFacadeREST");

        //テストデータ作成
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        HolidayEntity holiday1 = new HolidayEntity();
        holiday1.setHolidayName("所定休日");
        holiday1.setHolidayDate(df.parse("2018/08/04"));
        HolidayEntity holiday2 = new HolidayEntity();
        holiday2.setHolidayName("所定休日");
        holiday2.setHolidayDate(df.parse("2018/08/05"));
        HolidayEntity holiday3 = new HolidayEntity();
        holiday3.setHolidayName("所定休日");
        holiday3.setHolidayDate(df.parse("2018/08/11"));
        HolidayEntity holiday4 = new HolidayEntity();
        holiday4.setHolidayName("所定休日");
        holiday4.setHolidayDate(df.parse("2018/08/12"));
        HolidayEntity holiday5 = new HolidayEntity();
        holiday5.setHolidayName("夏季休暇");
        holiday5.setHolidayDate(df.parse("2018/08/13"));
        HolidayEntity holiday6 = new HolidayEntity();
        holiday6.setHolidayName("夏季休暇");
        holiday6.setHolidayDate(df.parse("2018/08/14"));
        HolidayEntity holiday7 = new HolidayEntity();
        holiday7.setHolidayName("所定休日");
        holiday7.setHolidayDate(df.parse("2018/08/17"));
        HolidayEntity holiday8 = new HolidayEntity();
        holiday8.setHolidayName("所定休日");
        holiday8.setHolidayDate(df.parse("2018/08/18"));
        HolidayEntity holiday9 = new HolidayEntity();
        // 追加
        tx.begin();
        rest.add(holiday1, null);
        rest.add(holiday2, null);
        rest.add(holiday3, null);
        rest.add(holiday4, null);
        rest.add(holiday5, null);
        rest.add(holiday6, null);
        rest.add(holiday7, null);
        rest.add(holiday8, null);
        tx.commit();

        // 休日情報を取得する(find)
        HolidayEntity result1 = rest.find(holiday1.getHolidayId(), null);
        assertThat(holiday1, is(result1));
        HolidayEntity result2 = rest.find(holiday2.getHolidayId(), null);
        assertThat(holiday2, is(result2));
        HolidayEntity result3 = rest.find(holiday3.getHolidayId(), null);
        assertThat(holiday3, is(result3));
        HolidayEntity result4 = rest.find(holiday4.getHolidayId(), null);
        assertThat(holiday4, is(result4));
        HolidayEntity result5 = rest.find(holiday5.getHolidayId(), null);
        assertThat(holiday5, is(result5));
        HolidayEntity result6 = rest.find(holiday6.getHolidayId(), null);
        assertThat(holiday6, is(result6));
        HolidayEntity result7 = rest.find(holiday7.getHolidayId(), null);
        assertThat(holiday7, is(result7));
        HolidayEntity result8 = rest.find(holiday8.getHolidayId(), null);
        assertThat(holiday8, is(result8));

        // 件数取得(countAll)
        int count = parseInt(rest.countAll(1L));
        assertThat(count, is(8));

        // 全て取得(findRange)
        List<HolidayEntity> findAllResults = rest.findRange(null, null, null);
        assertThat(findAllResults.size(), is(count));
        assertThat(findAllResults.contains(holiday1), is(true));
        assertThat(findAllResults.contains(holiday2), is(true));
        assertThat(findAllResults.contains(holiday3), is(true));
        assertThat(findAllResults.contains(holiday4), is(true));
        assertThat(findAllResults.contains(holiday5), is(true));
        assertThat(findAllResults.contains(holiday6), is(true));
        assertThat(findAllResults.contains(holiday7), is(true));
        assertThat(findAllResults.contains(holiday8), is(true));

        // 範囲取得(findRange)
        List<HolidayEntity> findRangeResults1 = rest.findRange(0, 3, null);
        assertThat(findRangeResults1, is(findAllResults.subList(0, 4)));
        List<HolidayEntity> findRangeResults2 = rest.findRange(4, 7, null);
        assertThat(findRangeResults2, is(findAllResults.subList(4, 8)));

        // 検索条件作成
        HolidaySearchCondition condition = new HolidaySearchCondition();
        condition.setFromDate(df.parse("2018/08/05"));
        condition.setToDate(df.parse("2018/08/17"));

        // 条件を指定して休日情報の件数を取得（countHoliday）
        int searchCount = parseInt(rest.countHoliday(condition, null));
        assertThat(searchCount, is(6));

        // 条件を指定して休日情報一覧を取得（searchHoliday）
        List<HolidayEntity> searchResults = rest.searchHoliday(condition, null);
        assertThat(searchResults.size(), is(searchCount));
        assertThat(searchResults.contains(holiday1), is(false));// 条件範囲外
        assertThat(searchResults.contains(holiday2), is(true));
        assertThat(searchResults.contains(holiday3), is(true));
        assertThat(searchResults.contains(holiday4), is(true));
        assertThat(searchResults.contains(holiday5), is(true));
        assertThat(searchResults.contains(holiday6), is(true));
        assertThat(searchResults.contains(holiday7), is(true));
        assertThat(searchResults.contains(holiday8), is(false));// 条件範囲外

        //条件を指定して休日情報一覧を範囲取得（searchHolidayRange）
        List<HolidayEntity> searchRangeResults1 = rest.searchHolidayRange(condition, 0, 3, null);
        assertThat(searchRangeResults1, is(searchResults.subList(0, 4)));
        List<HolidayEntity> searchRangeResults2 = rest.searchHolidayRange(condition, 4, 5, null);
        assertThat(searchRangeResults2, is(searchResults.subList(4, 6)));

        // 更新情報作成
        long updateId = holiday3.getHolidayId();
        String newName = "休日";
        Date newDate = df.parse("2018/08/15");
        holiday3.setHolidayName(newName);
        holiday3.setHolidayDate(newDate);

        //休日情報を更新（update）
        tx.begin();
        Response response = rest.update(holiday3, null);
        ResponseEntity responseEntity = (ResponseEntity) response.getEntity();
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
        tx.commit();
        HolidayEntity updateHoliday = rest.find(updateId, null);
        assertThat(updateHoliday.getHolidayName(), is(newName));
        assertThat(updateHoliday.getHolidayDate(), is(newDate));
        
        //休日情報更新（update）の排他制御を確認
        assertThat(updateHoliday.getVerInfo(), is(2));  //排他用バージョン１up確認
        //holiday3の休日ID、任意の休日の名称、任意の休日の日付をholiday9に設定し、
        //排他用バージョンに1を設定
        //（２回目となる今回のupdateで排他用バージョン1のままとみなし排他確認する）
        holiday9.setHolidayId(holiday3.getHolidayId());
        holiday9.setHolidayName("半休");
        holiday9.setHolidayDate(df.parse("2018/08/16"));
        holiday9.setVerInfo(1);                
        //エンティティの排他用バージョンが1で休日情報をholiday9で更新（update）し、
        //排他チェックに引っかかる状態で更新　→　DIFFERENT_VER_INFO
        tx.begin();
        Response response2 = rest.update(holiday9, null);
        ResponseEntity responseEntity2 = (ResponseEntity) response2.getEntity();
        assertThat(responseEntity2.getErrorType(), is(ServerErrorTypeEnum.DIFFERENT_VER_INFO));
        tx.commit();
        HolidayEntity updateHoliday2 = rest.find(updateId, null);
        assertThat(updateHoliday2.getHolidayName(), is(newName));//更新なしを確認
        assertThat(updateHoliday2.getHolidayDate(), is(newDate));//更新なしを確認
        
        // 削除（remove）
        long deleteId = holiday6.getHolidayId();
        tx.begin();
        rest.remove(deleteId, null);
        tx.commit();
        HolidayEntity deleteHoliday = rest.find(deleteId, null);
        assertThat(deleteHoliday.getHolidayId(), nullValue());

        // 削除 （複数：remove(List<Long>, Long)）
        List<Long> deleteIds = Arrays.asList(holiday3.getHolidayId(), holiday4.getHolidayId(), holiday5.getHolidayId());
        tx.begin();
        rest.remove(deleteIds, null);
        tx.commit();
        findAllResults = rest.findRange(null, null, null);
        assertThat(findAllResults.contains(holiday1), is(true));
        assertThat(findAllResults.contains(holiday2), is(true));
        assertThat(findAllResults.contains(holiday3), is(false));// 削除済
        assertThat(findAllResults.contains(holiday4), is(false));// 削除済
        assertThat(findAllResults.contains(holiday5), is(false));// 削除済
        assertThat(findAllResults.contains(holiday6), is(false));// 削除済
        assertThat(findAllResults.contains(holiday7), is(true));
        assertThat(findAllResults.contains(holiday8), is(true));
    }
}
