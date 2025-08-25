/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

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
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.ConUnitTemplateAssociateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplateEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unittemplate.UnitTemplatePropertyEntity;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ユニットテンプレートテストパッケージ
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.14.Fri
 */
public class UnitTemplateEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static UnitTemplateEntityFacadeREST unitTemplateRest = null;

    public UnitTemplateEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryForFujiServerPU");
        em = emf.createEntityManager();
        unitTemplateRest = new UnitTemplateEntityFacadeREST();
        unitTemplateRest.setEntityManager(em);
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
    public void testUnitTemplateEntityFacadeREST() throws Exception {
        System.out.println("testUnitTemplateEntityFacadeREST");
        final long TACT_TIME = 3600000L;
        final String UNIT_TEMPLATE_URI = "unittemplate/";
        // TODO:標準サーバとの連携が取れないのでユニットの階層だけで登録確認
//        //工程順の用意
//        UnitTemplateEntity work1 = new UnitTemplateEntity(0L, 0L, "workflow1", "rev1", null, 0L, null, null);
//        UnitTemplateEntity work2 = new UnitTemplateEntity(1L, 0L, "workflow2", "rev1", null, 0L, null, null);

        UnitTemplateEntity childTemplate1 = new UnitTemplateEntity(0L, "childTemplate1", "sample0", null, null, null);
        UnitTemplateEntity childTemplate2 = new UnitTemplateEntity(0L, "childTemplate2", "sample1", null, null, null);
        UnitTemplateEntity childTemplate3 = new UnitTemplateEntity(0L, "childTemplate3", "sample2", null, null, null);

        tx.begin();
        em.persist(childTemplate1);
        em.persist(childTemplate2);
        em.persist(childTemplate3);
        tx.commit();

        //ユニットテンプレートの用意
        UnitTemplateEntity unittemplate = new UnitTemplateEntity(3L, "unittemplate1", "sample3", null, null, null);
        UnitTemplatePropertyEntity prop1 = new UnitTemplatePropertyEntity(3L, "prop1", "prop1", "prop1", 1);
        UnitTemplatePropertyEntity prop2 = new UnitTemplatePropertyEntity(3L, "prop2", "prop2", "prop2", 2);
        UnitTemplatePropertyEntity prop3 = new UnitTemplatePropertyEntity(3L, "prop3", "prop3", "prop3", 3);
        List<UnitTemplatePropertyEntity> props = new ArrayList<>();
        props.addAll(Arrays.asList(prop1, prop2, prop3));
        unittemplate.setUnitTemplatePropertyCollection(props);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDatetime1 = sdf.parse("1970-01-01 00:00:00+00");
        Date endDatetime1 = new Date(startDatetime1.getTime() + TACT_TIME);
        Date startDatetime2 = endDatetime1;
        Date endDatetime2 = new Date(startDatetime2.getTime() + TACT_TIME);
        Date startDatetime3 = endDatetime2;
        Date endDatetime3 = new Date(startDatetime3.getTime() + TACT_TIME);
        Date startDatetime4 = endDatetime3;
        Date endDatetime4 = new Date(startDatetime4.getTime() + TACT_TIME);

        ConUnitTemplateAssociateEntity conUnitTemplate1 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate1.getUnitTemplateId(), 0, startDatetime1, endDatetime1);
        ConUnitTemplateAssociateEntity conUnitTemplate2 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate2.getUnitTemplateId(), 1, startDatetime2, endDatetime2);
        ConUnitTemplateAssociateEntity conUnitTemplate3 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate3.getUnitTemplateId(), 2, startDatetime3, endDatetime3);
        unittemplate.setConUnitTemplateAssociateCollection(Arrays.asList(conUnitTemplate1, conUnitTemplate2, conUnitTemplate3));

        // ユニットテンプレート登録処理
        tx.begin();
        ResponseEntity rs = (ResponseEntity) unitTemplateRest.add(unittemplate).getEntity();
        tx.commit();

        UnitTemplateEntity result = unitTemplateRest.findWithDatails(Long.parseLong(rs.getUri().substring(UNIT_TEMPLATE_URI.length())));
        assertThat(result.getUnitTemplateName(), is("unittemplate1"));
        assertThat(result.getConUnitTemplateAssociateCollection().size(), is(3));

        UnitTemplateEntity childTemplate4 = new UnitTemplateEntity(0L, "childTemplate4", "sample4", null, null, null);
        tx.begin();
        em.persist(childTemplate4);
        tx.commit();

        ConUnitTemplateAssociateEntity conUnitTemplate4 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate1.getUnitTemplateId(), 0, startDatetime1, endDatetime1);
        ConUnitTemplateAssociateEntity conUnitTemplate5 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate2.getUnitTemplateId(), 1, startDatetime2, endDatetime2);
        ConUnitTemplateAssociateEntity conUnitTemplate6 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate3.getUnitTemplateId(), 2, startDatetime3, endDatetime3);
        ConUnitTemplateAssociateEntity conUnitTemplate7 = new ConUnitTemplateAssociateEntity(null, null, null, childTemplate4.getUnitTemplateId(), 3, startDatetime4, endDatetime4);
        result.setConUnitTemplateAssociateCollection(Arrays.asList(conUnitTemplate4, conUnitTemplate5, conUnitTemplate6, conUnitTemplate7));
        result.setUnitTemplateName("unittemplate2");

        // 更新処理
        tx.begin();
        unitTemplateRest.update(result);
        tx.commit();
        UnitTemplateEntity result2 = unitTemplateRest.findWithDatails(result.getUnitTemplateId());
        assertThat(result2.getUnitTemplateName(), is("unittemplate2"));
        assertThat(result2.getConUnitTemplateAssociateCollection().size(), is(4));

        // コピー処理
        tx.begin();
        ResponseEntity rs2 = (ResponseEntity) unitTemplateRest.copy(result.getUnitTemplateId()).getEntity();
        tx.commit();

        UnitTemplateEntity result3 = unitTemplateRest.findWithDatails(Long.parseLong(rs.getUri().substring(UNIT_TEMPLATE_URI.length())));
        assertThat(result3.getUnitTemplateName(), is("unittemplate2-copy"));
        assertThat(result3.getConUnitTemplateAssociateCollection().size(), is(4));

    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        UnitTemplateEntity unittemplate1 = new UnitTemplateEntity(3L, "unittemplate1", "sample3", null, null, null);
        tx.begin();
        response = unitTemplateRest.add(unittemplate1);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        UnitTemplateEntity unittemplate2 = new UnitTemplateEntity(3L, "unittemplate1", "sample3", null, null, null);
        tx.begin();
        response = unitTemplateRest.add(unittemplate2);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        UnitTemplateEntity unittemplate3 = new UnitTemplateEntity(2L, "unittemplate1", "sample3", null, null, null);
        tx.begin();
        response = unitTemplateRest.add(unittemplate3);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        tx.begin();
        response = unitTemplateRest.remove(unittemplate1.getUnitTemplateId());
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

}
