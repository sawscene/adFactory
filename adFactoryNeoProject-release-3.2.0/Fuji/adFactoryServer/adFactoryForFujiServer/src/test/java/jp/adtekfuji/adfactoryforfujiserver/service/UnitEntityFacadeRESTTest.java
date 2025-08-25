/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateAssociateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplatePropertyEntity;
import jp.adtekfuji.forfujiapp.entity.search.UnitSearchCondition;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 生産ユニットテストパッケージ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
public class UnitEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static UnitEntityFacadeREST unitRest = null;
    private static UnitTemplateEntityFacadeREST unitTemplateRest = null;
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final List<Long> unitTemplateEntitys = new ArrayList<>();
    private UnitTemplateEntity top;
    private UnitEntity unit;

    public UnitEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryForFujiServerPU");
        em = emf.createEntityManager();
        unitTemplateRest = new UnitTemplateEntityFacadeREST();
        unitTemplateRest.setEntityManager(em);

        unitRest = new UnitEntityFacadeREST();
        unitRest.setEntityManager(em);
        unitRest.setUnitTemplateEntityFacadeREST(unitTemplateRest);
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
    public void testWorkflowEntityFacadeREST() throws Exception {
        // サービスを２つ跨いで実装しているためテストケースが実装できない。
    }

    @Test
    public void testError() throws Exception {
        Response response;
        ResponseEntity responseEntity;
        Date updateDateTime = df.parse("1970-01-01 00:00:00+00");

        createData();

        UnitEntity unit1 = new UnitEntity(0L, "unit1", top.getUnitTemplateId(), top.getWorkflowDiaglam());
        unit1.setFkUpdatePersonId(0L);
        unit1.setUpdateDatetime(updateDateTime);
        unit1.setStartDatetime(new Date());
        unit1.setCompDatetime(new Date());
        tx.begin();
        response = unitRest.add(unit1);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        UnitEntity unit2 = new UnitEntity(0L, "unit1", top.getUnitTemplateId(), top.getWorkflowDiaglam());
        unit2.setStartDatetime(new Date());
        unit2.setCompDatetime(new Date());
        unit2.setFkUpdatePersonId(0L);
        unit2.setUpdateDatetime(updateDateTime);
        tx.begin();
        response = unitRest.add(unit2);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        UnitEntity unit3 = new UnitEntity(1L, "unit2", top.getUnitTemplateId(), top.getWorkflowDiaglam());
        unit3.setStartDatetime(new Date());
        unit3.setCompDatetime(new Date());
        unit3.setFkUpdatePersonId(0L);
        unit3.setUpdateDatetime(updateDateTime);
        tx.begin();
        response = unitRest.add(unit3);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

    /**
     * 検索用テスト
     *
     * @throws Exception
     */
    //@Test
    public void testSearch() throws Exception {
        System.out.println("testSearch");

        createData();

        String count;
        count = unitRest.countBasicSearch(new UnitSearchCondition().unitName("unit"));
        assertThat(Integer.parseInt(count), is(5));
        List<UnitEntity> units;
        units = unitRest.findBasicSearch(new UnitSearchCondition().unitName("unit"), 0, 30);
        assertThat(units, is(hasSize(5)));

        //String num1;
        //num1 = unitRest.countBasicSearch(new UnitSearchCondition().hierarchyId(0L));
        //assertThat(Integer.parseInt(num1), is(1));
        //List<UnitEntity> units1;
        //units1 = unitRest.findBasicSearch(new UnitSearchCondition().hierarchyId(0L), 0, 30);
        //assertThat(units1, is(hasSize(1)));

        //String num2;
        //num2 = unitRest.countBasicSearch(new UnitSearchCondition().unitTemplateId(top.getUnitTemplateId()));
        //assertThat(Integer.parseInt(num2), is(1));
        //List<UnitEntity> units2;
        //units2 = unitRest.findBasicSearch(new UnitSearchCondition().unitTemplateId(top.getUnitTemplateId()), 0, 30);
        //assertThat(units2, is(hasSize(1)));

        count = unitRest.countBasicSearch(new UnitSearchCondition().unitTemplateIdCollection(unitTemplateEntitys));
        assertThat(Integer.parseInt(count), is(5));
        units = unitRest.findBasicSearch(new UnitSearchCondition().unitTemplateIdCollection(unitTemplateEntitys), 0, 30);
        assertThat(units, is(hasSize(5)));

        count = unitRest.countBasicSearch(new UnitSearchCondition().fromDate(df.parse("1960-01-01 00:00:00")));
        assertThat(Integer.parseInt(count), is(5));
        units = unitRest.findBasicSearch(new UnitSearchCondition().fromDate(df.parse("1960-01-01 00:00:00")), 0, 30);
        assertThat(units, is(hasSize(5)));

        count = unitRest.countBasicSearch(new UnitSearchCondition().toDate(df.parse("1980-01-01 00:00:00")));
        assertThat(Integer.parseInt(count), is(0));
        units = unitRest.findBasicSearch(new UnitSearchCondition().toDate(df.parse("1980-01-01 00:00:00")), 0, 30);
        assertThat(units, is(hasSize(0)));

        count = unitRest.countBasicSearch(new UnitSearchCondition().unitTemplateIdCollection(unitTemplateEntitys));
        assertThat(Integer.parseInt(count), is(5));
        units  = unitRest.findSearchRange(new UnitSearchCondition().fromDate(df.parse("1960-01-01 00:00:00")).toDate(df.parse("1980-01-01 00:00:00")).unitTemplateIdCollection(unitTemplateEntitys), 0, 30);
        assertThat(units, is(hasSize(5)));
    }

    /**
     * ユニットテンプレート情報作成
     *
     * @throws ParseException
     * @throws URISyntaxException
     */
    private void createData() throws ParseException, URISyntaxException, Exception {
        System.out.println("testUniteEntityFacadeREST");
        final long TACT_TIME = 3600000L;
        Date startDatetime1 = df.parse("1970-01-01 00:00:00+00");
        Date endDatetime1 = new Date(startDatetime1.getTime() + TACT_TIME);
        Date startDatetime2 = endDatetime1;
        Date endDatetime2 = new Date(startDatetime2.getTime() + TACT_TIME);
        Date startDatetime3 = endDatetime2;
        Date endDatetime3 = new Date(startDatetime3.getTime() + TACT_TIME);

        UnitTemplateEntity childTemplate1 = new UnitTemplateEntity(0L, "childTemplate1", "sample0", 0L, 0L, startDatetime1);
        UnitTemplateEntity childTemplate2 = new UnitTemplateEntity(0L, "childTemplate2", "sample1", 0L, 0L, startDatetime1);
        UnitTemplateEntity childTemplate3 = new UnitTemplateEntity(0L, "childTemplate3", "sample2", 0L, 0L, startDatetime1);

        tx.begin();
        em.persist(childTemplate1);
        em.persist(childTemplate2);
        em.persist(childTemplate3);
        tx.commit();
        unitTemplateEntitys.clear();
        unitTemplateEntitys.add(childTemplate1.getUnitTemplateId());
        unitTemplateEntitys.add(childTemplate2.getUnitTemplateId());
        unitTemplateEntitys.add(childTemplate3.getUnitTemplateId());

        //ユニットテンプレートの用意
        UnitTemplateEntity unittemplate1 = new UnitTemplateEntity(3L, "unittemplate1", "sample3", 0L, 0L, startDatetime1);
        UnitTemplatePropertyEntity prop1 = new UnitTemplatePropertyEntity(3L, "prop1", "prop1", "prop1", 1);
        UnitTemplatePropertyEntity prop2 = new UnitTemplatePropertyEntity(3L, "prop2", "prop2", "prop2", 2);
        UnitTemplatePropertyEntity prop3 = new UnitTemplatePropertyEntity(3L, "prop3", "prop3", "prop3", 3);
        List<UnitTemplatePropertyEntity> props = new ArrayList<>();
        props.addAll(Arrays.asList(prop1, prop2, prop3));
        unittemplate1.setUnitTemplatePropertyCollection(props);

        ConUnitTemplateAssociateEntity conUnitTemplate1 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate1.getUnitTemplateId(), 0, startDatetime1, endDatetime1);
        ConUnitTemplateAssociateEntity conUnitTemplate2 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate2.getUnitTemplateId(), 1, startDatetime2, endDatetime2);
        ConUnitTemplateAssociateEntity conUnitTemplate3 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate3.getUnitTemplateId(), 2, startDatetime3, endDatetime3);
        unittemplate1.setConUnitTemplateAssociateCollection(Arrays.asList(conUnitTemplate1, conUnitTemplate2, conUnitTemplate3));

        tx.begin();
        unitTemplateRest.add(unittemplate1).getEntity();
        tx.commit();
        unitTemplateEntitys.add(unittemplate1.getUnitTemplateId());

        top = new UnitTemplateEntity(3L, "top", "sample3", null, 0L, startDatetime1);
        UnitTemplatePropertyEntity prop4 = new UnitTemplatePropertyEntity(3L, "prop1", "prop1", "prop1", 1);
        UnitTemplatePropertyEntity prop5 = new UnitTemplatePropertyEntity(3L, "prop2", "prop2", "prop2", 2);
        UnitTemplatePropertyEntity prop6 = new UnitTemplatePropertyEntity(3L, "prop3", "prop3", "prop3", 3);
        top.setUnitTemplatePropertyCollection(Arrays.asList(prop4, prop5, prop6));

        ConUnitTemplateAssociateEntity conUnitTemplate4 = new ConUnitTemplateAssociateEntity(null, null, null, unittemplate1.getUnitTemplateId(), 0, startDatetime3, endDatetime3);
        top.setConUnitTemplateAssociateCollection(Arrays.asList(conUnitTemplate4));

        tx.begin();
        unitTemplateRest.add(top).getEntity();
        tx.commit();
        unitTemplateEntitys.add(top.getUnitTemplateId());

        unit = new UnitEntity(0L, "unit", top.getUnitTemplateId(), unittemplate1.getWorkflowDiaglam());
        unit.setStartDatetime(new Date());
        unit.setCompDatetime(new Date());
        unit.setFkUpdatePersonId(0L);
        unit.setUpdateDatetime(startDatetime1);
        tx.begin();
        unitRest.add(unit);
        tx.commit();
    }
}
