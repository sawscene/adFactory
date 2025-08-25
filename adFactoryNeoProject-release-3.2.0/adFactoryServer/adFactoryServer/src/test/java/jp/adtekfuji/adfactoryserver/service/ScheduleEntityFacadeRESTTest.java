/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

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
import jp.adtekfuji.adFactory.entity.search.ScheduleSearchCondition;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adfactoryserver.entity.organization.OrganizationEntity;
import jp.adtekfuji.adfactoryserver.entity.schedule.ScheduleEntity;
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
public class ScheduleEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static ScheduleEntityFacadeREST rest = null;

    private static OrganizationEntityFacadeREST organizationRest = null;

    public ScheduleEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
        rest = new ScheduleEntityFacadeREST();
        rest.setEntityManager(em);

        organizationRest = new OrganizationEntityFacadeREST();
        organizationRest.setEntityManager(em);
        organizationRest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        rest.setOrganizationRest(organizationRest);
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
    public void testScheduleEntityFacadeREST() throws Exception {
        System.out.println("testScheduleEntityFacadeREST");

        OrganizationEntity organization1 = new OrganizationEntity(null, "所属①", "organization1", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity organization2 = new OrganizationEntity(null, "所属②", "organization2", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity organization3 = new OrganizationEntity(null, "所属③", "organization3", AuthorityEnum.WORKER, null, null, null, null, null);

        tx.begin();
        organizationRest.add(organization1, null);
        organizationRest.add(organization2, null);
        organizationRest.add(organization3, null);
        tx.commit();

        OrganizationEntity woker1 = new OrganizationEntity(null, "作業者①", "worker1", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity woker2 = new OrganizationEntity(null, "作業者②", "worker2", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity woker3 = new OrganizationEntity(null, "作業者③", "worker3", AuthorityEnum.WORKER, null, null, null, null, null);
        OrganizationEntity woker4 = new OrganizationEntity(null, "作業者④", "worker4", AuthorityEnum.WORKER, null, null, null, null, null);

        woker1.setParentOrganizationId(organization1.getOrganizationId());
        woker2.setParentOrganizationId(organization1.getOrganizationId());
        woker3.setParentOrganizationId(organization2.getOrganizationId());
        woker4.setParentOrganizationId(organization3.getOrganizationId());

        tx.begin();
        organizationRest.add(woker1, null);
        organizationRest.add(woker2, null);
        organizationRest.add(woker3, null);
        organizationRest.add(woker4, null);
        tx.commit();

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        ScheduleEntity schedule1 = new ScheduleEntity();
        schedule1.setScheduleName("①-1");
        schedule1.setScheduleFromDate(df.parse("2018/08/06 09:00:00"));
        schedule1.setScheduleToDate(df.parse("2018/08/06 17:00:00"));
        schedule1.setOrganizationId(woker1.getOrganizationId());

        ScheduleEntity schedule2 = new ScheduleEntity();
        schedule2.setScheduleName("①-2");
        schedule2.setScheduleFromDate(df.parse("2018/08/07 09:00:00"));
        schedule2.setScheduleToDate(df.parse("2018/08/07 12:00:00"));
        schedule2.setOrganizationId(woker1.getOrganizationId());

        ScheduleEntity schedule3 = new ScheduleEntity();
        schedule3.setScheduleName("①-3");
        schedule3.setScheduleFromDate(df.parse("2018/08/08 13:00:00"));
        schedule3.setScheduleToDate(df.parse("2018/08/08 17:00:00"));
        schedule3.setOrganizationId(woker1.getOrganizationId());

        ScheduleEntity schedule4 = new ScheduleEntity();
        schedule4.setScheduleName("①-4");
        schedule4.setScheduleFromDate(df.parse("2018/08/09 09:00:00"));
        schedule4.setScheduleToDate(df.parse("2018/08/09 10:00:00"));
        schedule4.setOrganizationId(woker1.getOrganizationId());

        ScheduleEntity schedule5 = new ScheduleEntity();
        schedule5.setScheduleName("①-5");
        schedule5.setScheduleFromDate(df.parse("2018/08/09 10:00:00"));
        schedule5.setScheduleToDate(df.parse("2018/08/09 11:00:00"));
        schedule5.setOrganizationId(woker1.getOrganizationId());

        ScheduleEntity schedule6 = new ScheduleEntity();
        schedule6.setScheduleName("①-6");
        schedule6.setScheduleFromDate(df.parse("2018/08/09 11:00:00"));
        schedule6.setScheduleToDate(df.parse("2018/08/09 12:00:00"));
        schedule6.setOrganizationId(woker1.getOrganizationId());

        ScheduleEntity schedule7 = new ScheduleEntity();
        schedule7.setScheduleName("②-1");
        schedule7.setScheduleFromDate(df.parse("2018/08/10 00:00:00"));
        schedule7.setScheduleToDate(df.parse("2018/08/10 23:59:59"));
        schedule7.setOrganizationId(woker2.getOrganizationId());

        ScheduleEntity schedule8 = new ScheduleEntity();
        schedule8.setScheduleName("②-2");
        schedule8.setScheduleFromDate(df.parse("2018/09/08 00:00:00"));
        schedule8.setScheduleToDate(df.parse("2018/09/08 23:59:59"));
        schedule8.setOrganizationId(woker2.getOrganizationId());

        ScheduleEntity schedule9 = new ScheduleEntity();
        schedule9.setScheduleName("③-1");
        schedule9.setScheduleFromDate(df.parse("2018/08/09 09:00:00"));
        schedule9.setScheduleToDate(df.parse("2018/08/09 10:00:00"));
        schedule9.setOrganizationId(woker3.getOrganizationId());

        ScheduleEntity schedule10 = new ScheduleEntity();
        schedule10.setScheduleName("④-1");
        schedule10.setScheduleFromDate(df.parse("2018/08/09 10:00:00"));
        schedule10.setScheduleToDate(df.parse("2018/08/09 11:00:00"));
        schedule10.setOrganizationId(woker4.getOrganizationId());

        // 追加
        tx.begin();
        rest.add(schedule1, null);
        rest.add(schedule2, null);
        rest.add(schedule3, null);
        rest.add(schedule4, null);
        rest.add(schedule5, null);
        rest.add(schedule6, null);
        rest.add(schedule7, null);
        rest.add(schedule8, null);
        rest.add(schedule9, null);
        rest.add(schedule10, null);
        tx.commit();

        // 取得
        ScheduleEntity result1 = rest.find(schedule1.getScheduleId(), null);
        assertThat(schedule1, is(result1));
        ScheduleEntity result2 = rest.find(schedule2.getScheduleId(), null);
        assertThat(schedule2, is(result2));
        ScheduleEntity result3 = rest.find(schedule3.getScheduleId(), null);
        assertThat(schedule3, is(result3));
        ScheduleEntity result4 = rest.find(schedule4.getScheduleId(), null);
        assertThat(schedule4, is(result4));
        ScheduleEntity result5 = rest.find(schedule5.getScheduleId(), null);
        assertThat(schedule5, is(result5));
        ScheduleEntity result6 = rest.find(schedule6.getScheduleId(), null);
        assertThat(schedule6, is(result6));
        ScheduleEntity result7 = rest.find(schedule7.getScheduleId(), null);
        assertThat(schedule7, is(result7));
        ScheduleEntity result8 = rest.find(schedule8.getScheduleId(), null);
        assertThat(schedule8, is(result8));
        ScheduleEntity result9 = rest.find(schedule9.getScheduleId(), null);
        assertThat(schedule9, is(result9));
        ScheduleEntity result10 = rest.find(schedule10.getScheduleId(), null);
        assertThat(schedule10, is(result10));

        // 件数取得
        int count = rest.count();
        assertThat(count, is(10));

        // 全て取得
        List<ScheduleEntity> findAllResults = rest.findRange(null, null, null);
        assertThat(findAllResults.size(), is(count));
        assertThat(findAllResults.contains(schedule1), is(true));
        assertThat(findAllResults.contains(schedule2), is(true));
        assertThat(findAllResults.contains(schedule3), is(true));
        assertThat(findAllResults.contains(schedule4), is(true));
        assertThat(findAllResults.contains(schedule5), is(true));
        assertThat(findAllResults.contains(schedule6), is(true));
        assertThat(findAllResults.contains(schedule7), is(true));
        assertThat(findAllResults.contains(schedule8), is(true));
        assertThat(findAllResults.contains(schedule9), is(true));
        assertThat(findAllResults.contains(schedule10), is(true));

        // 範囲取得
        List<ScheduleEntity> findRangeResults1 = rest.findRange(0, 3, null);
        assertThat(findRangeResults1, is(findAllResults.subList(0, 4)));
        List<ScheduleEntity> findRangeResults2 = rest.findRange(4, 7, null);
        assertThat(findRangeResults2, is(findAllResults.subList(4, 8)));
        List<ScheduleEntity> findRangeResults3 = rest.findRange(8, 9, null);
        assertThat(findRangeResults3, is(findAllResults.subList(8, 10)));

        // 条件
        ScheduleSearchCondition condition = new ScheduleSearchCondition();
        condition.setFromDate(df.parse("2018/08/07 00:00:00"));
        condition.setToDate(df.parse("2018/08/09 23:59:59"));
        condition.setOrganizationIdCollection(Arrays.asList(organization1.getOrganizationId(), organization3.getOrganizationId()));

        // 条件 件数取得
        int searchCount = Integer.parseInt(rest.countSchedule(condition, null));
        assertThat(searchCount, is(6));

        // 条件 取得
        List<ScheduleEntity> searchResults = rest.searchSchedule(condition, null);
        assertThat(searchResults.size(), is(searchCount));
        assertThat(searchResults.contains(schedule1), is(false));// 条件範囲外
        assertThat(searchResults.contains(schedule2), is(true));
        assertThat(searchResults.contains(schedule3), is(true));
        assertThat(searchResults.contains(schedule4), is(true));
        assertThat(searchResults.contains(schedule5), is(true));
        assertThat(searchResults.contains(schedule6), is(true));
        assertThat(searchResults.contains(schedule7), is(false));// 条件範囲外
        assertThat(searchResults.contains(schedule8), is(false));// 条件範囲外
        assertThat(searchResults.contains(schedule9), is(false));// 条件範囲外
        assertThat(searchResults.contains(schedule10), is(true));

        // 条件 範囲取得
        List<ScheduleEntity> searchRangeResults1 = rest.searchScheduleRange(condition, 0, 3, null);
        assertThat(searchRangeResults1, is(searchResults.subList(0, 4)));
        List<ScheduleEntity> searchRangeResults2 = rest.searchScheduleRange(condition, 4, 5, null);
        assertThat(searchRangeResults2, is(searchResults.subList(4, 6)));

        // 更新
        long updateId = schedule2.getScheduleId();
        String newName = "新しい③";
        Date newFromDate = df.parse("2018/09/03 00:00:00");
        Date newToDate = df.parse("2018/09/03 23:59:59");
        schedule2.setScheduleName(newName);
        schedule2.setScheduleFromDate(newFromDate);
        schedule2.setScheduleToDate(newToDate);

        tx.begin();
        rest.update(schedule2, null);
        tx.commit();
        ScheduleEntity updateSchedule = rest.find(updateId, null);
        assertThat(updateSchedule.getScheduleName(), is(newName));
        assertThat(updateSchedule.getScheduleFromDate(), is(newFromDate));
        assertThat(updateSchedule.getScheduleToDate(), is(newToDate));

        // 削除
        long deleteId = schedule7.getScheduleId();

        tx.begin();
        rest.remove(deleteId, null);
        tx.commit();
        ScheduleEntity deleteSchedule = rest.find(deleteId, null);
        assertThat(deleteSchedule.getScheduleId(), nullValue());

        // 削除 (複数)
        List<Long> deleteIds = Arrays.asList(schedule4.getScheduleId(), schedule5.getScheduleId(), schedule6.getScheduleId());

        tx.begin();
        rest.remove(deleteIds, null);
        tx.commit();

        findAllResults = rest.findRange(null, null, null);
        assertThat(findAllResults.contains(schedule1), is(true));
        assertThat(findAllResults.contains(schedule2), is(true));
        assertThat(findAllResults.contains(schedule3), is(true));
        assertThat(findAllResults.contains(schedule4), is(false));// 削除済
        assertThat(findAllResults.contains(schedule5), is(false));// 削除済
        assertThat(findAllResults.contains(schedule6), is(false));// 削除済
        assertThat(findAllResults.contains(schedule7), is(false));// 削除済
        assertThat(findAllResults.contains(schedule8), is(true));
        assertThat(findAllResults.contains(schedule9), is(true));
        assertThat(findAllResults.contains(schedule10), is(true));
    }
}
