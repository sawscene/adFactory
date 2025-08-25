/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class EquipmentEntityFacadeRESTTest {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static EquipmentEntityFacadeREST rest = null;
    private static EquipmentTypeEntityFacadeREST equipmentTypeRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;

    public EquipmentEntityFacadeRESTTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();

        equipmentTypeRest = new EquipmentTypeEntityFacadeREST();
        equipmentTypeRest.setEntityManager(em);


        authRest = new AccessHierarchyEntityFacadeREST();
        authRest.setEntityManager(em);

        rest = new EquipmentEntityFacadeREST();
        rest.setEntityManager(em);
        rest.setAuthRest(authRest);
        rest.setEquipmentTypeRest(equipmentTypeRest);
        rest.setAdInterfaceClientFacade(new MockAdIntefaceClientFacade());

        LicenseManager.setupTest();
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
        em.clear();
    }

    @Test
    public void testTree() throws Exception {
        System.out.println("testTree");

        Long loginUserId = null;

        Response response;
        ResponseEntity responseEntity;
        Long updatePersonId = 678L;
        Date updateDateTime = new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime();

        // 親設備を追加する。
        Map<String, EquipmentEntity> expected = new HashMap();
        EquipmentEntity line1 = new EquipmentEntity(0L, "nameLine1", "identLine1", null, updatePersonId, updateDateTime);
        EquipmentEntity line2 = new EquipmentEntity(0L, "nameLine2", "identLine2", null, updatePersonId, updateDateTime);
        EquipmentEntity line3 = new EquipmentEntity(0L, "nameLine3", "identLine3", null, updatePersonId, updateDateTime);

        tx.begin();
        rest.add(line1, null);
        rest.add(line2, null);
        rest.add(line3, null);
        tx.commit();

        expected.put("identLine1", line1);
        expected.put("identLine2", line2);
        expected.put("identLine3", line3);

        // 設備一覧を取得する。
        List<EquipmentEntity> lines;
        lines = rest.findTreeRange(null, loginUserId, null, null, false, null);
        assertThat(lines.size(), is(3));

        int equipNo = 0;
        for (EquipmentEntity line : lines) {
            assertThat(line, is(expected.get(line.getEquipmentIdentify())));

            // 子設備を追加する。
            equipNo++;
            String name = new StringBuilder("nameEquip").append(equipNo).toString();
            String ident = new StringBuilder("identEquip").append(equipNo).toString();
            EquipmentEntity equip1 = new EquipmentEntity(line.getEquipmentId(), name, ident, 0L, updatePersonId, updateDateTime);

            tx.begin();
            rest.add(equip1, null);
            tx.commit();

            expected.put(ident, equip1);

            // 子設備を取得する。
            List<EquipmentEntity> equips = rest.findTreeRange(line.getEquipmentId(), loginUserId, null, null, false, null);
            assertThat(equips.size(), is(1));
            for (EquipmentEntity equip : equips) {
                assertThat(equip, is(expected.get(equip.getEquipmentIdentify())));
            }
        }

        // 親設備の設備識別名を変更する。
        line1 = rest.findByName("identLine1", loginUserId, null);
        line1.setEquipmentIdentify("identLine11");
        expected.remove("identLine1");
        expected.put("identLine11", line1);

        tx.begin();
        rest.update(line1, null);
        tx.commit();

        line1 = rest.findByName("identLine11", loginUserId, null);
        assertThat(line1.getEquipmentIdentify(), is("identLine11"));

        // 子設備の設備識別名を変更する。
        EquipmentEntity equip1 = rest.findByName("identEquip1", loginUserId, null);
        equip1.setEquipmentIdentify("identEquip11");
        expected.remove("identEquip1");
        expected.put("identEquip11", equip1);

        tx.begin();
        rest.update(equip1, null);
        tx.commit();

        equip1 = rest.findByName("identEquip11", loginUserId, null);
        assertThat(equip1.getEquipmentIdentify(), is("identEquip11"));

        // 設備一覧を取得する。
        lines = rest.findTreeRange(null, loginUserId, null, null, null, null);
        assertThat(lines.size(), is(3));

        for (EquipmentEntity line : lines) {
            assertThat(line, is(expected.get(line.getEquipmentIdentify())));

            // 子設備を取得する。
            List<EquipmentEntity> equips = rest.findTreeRange(line.getEquipmentId(), loginUserId, null, null, null, null);
            assertThat(equips.size(), is(1));
            for (EquipmentEntity equip : equips) {
                assertThat(equip, is(expected.get(equip.getEquipmentIdentify())));
            }
        }

        // 子設備をコピーする。
        EquipmentEntity equip3 = expected.get("identEquip3");

        tx.begin();
        rest.copy(equip3.getEquipmentId(), null);
        tx.commit();

        EquipmentEntity copy = rest.findByName("identEquip3-copy", loginUserId, null);
        assertThat(copy.getEquipmentIdentify(), is("identEquip3-copy"));
        assertThat(copy.getEquipmentTypeId(), is(equip3.getEquipmentTypeId()));
        assertThat(copy.getParentEquipmentId(), is(equip3.getParentEquipmentId()));

        // 子設備を削除する。
        tx.begin();
        rest.remove(copy.getEquipmentId(), null);
        tx.commit();

        copy = rest.findByName("identEquip3-copy", loginUserId, null);
        assertThat(copy.getEquipmentId(), nullValue());

        // 子設備を持つ親設備を削除する。
        tx.begin();
        response = rest.remove(line3.getEquipmentId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.EXIST_HIERARCHY_DELETE));

        // 子設備を削除する。
        tx.begin();
        rest.remove(equip3.getEquipmentId(), null);
        tx.commit();

        copy = rest.findByName("identEquip3", loginUserId, null);
        assertThat(copy.getEquipmentId(), nullValue());

        // 子設備を持たない親設備を削除する。
        tx.begin();
        rest.remove(line3.getEquipmentId(), null);
        tx.commit();

        copy = rest.findByName("identLine3", loginUserId, null);
        assertThat(copy.getEquipmentId(), nullValue());
    }

    @Test
    public void testError() throws Exception {
        System.out.println("testError");

        Response response;
        ResponseEntity responseEntity;

        EquipmentEntity line1 = new EquipmentEntity(0L, "名前", "識別名", null, null, null);

        tx.begin();
        response = rest.add(line1, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(201));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));

        EquipmentEntity line2 = new EquipmentEntity(0L, "名前", "識別名", 0L, null, null);

        tx.begin();
        response = rest.add(line2, null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(500));
        assertThat(responseEntity.isSuccess(), is(false));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.IDENTNAME_OVERLAP));

        tx.begin();
        response = rest.remove(line1.getEquipmentId(), null);
        responseEntity = (ResponseEntity) response.getEntity();
        tx.commit();
        assertThat(response.getStatus(), is(200));
        assertThat(responseEntity.isSuccess(), is(true));
        assertThat(responseEntity.getErrorType(), is(ServerErrorTypeEnum.SUCCESS));
    }

    @Test
    public void testProperty() throws Exception {
        System.out.println("testProperty");

        Date updateDateTime = new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime();

        EquipmentEntity tablet1 = new EquipmentEntity(0L, "nameTablet1", "identTablet1", 0L, 1L, updateDateTime);

        tx.begin();
        rest.add(tablet1, null);
        tx.commit();

        EquipmentEntity result = rest.find(tablet1.getEquipmentId(), null);
        System.out.println(result);

        assertThat(result.getEquipmentIdentify(), is(tablet1.getEquipmentIdentify()));
        assertThat(result.getEquipmentName(), is(tablet1.getEquipmentName()));
        assertThat(result.getEquipmentTypeId(), is(tablet1.getEquipmentTypeId()));
        assertThat(result.getUpdatePersonId(), is(tablet1.getUpdatePersonId()));
        assertThat(result.getUpdateDatetime(), is(tablet1.getUpdateDatetime()));

        tablet1.setEquipmentName("nameTablet1-1");

        tx.begin();
        rest.update(tablet1, null);
        tx.commit();

        result = rest.find(tablet1.getEquipmentId(), null);
        System.out.println(result);

        assertThat(result.getEquipmentIdentify(), is(tablet1.getEquipmentIdentify()));
        assertThat(result.getEquipmentName(), is(tablet1.getEquipmentName()));
        assertThat(result.getEquipmentTypeId(), is(tablet1.getEquipmentTypeId()));
        assertThat(result.getUpdatePersonId(), is(tablet1.getUpdatePersonId()));
        assertThat(result.getUpdateDatetime(), is(tablet1.getUpdateDatetime()));

        // 設備を削除する。
        tx.begin();
        rest.remove(tablet1.getEquipmentId(), null);
        tx.commit();
    }

    @Test
    public void testLogin() throws Exception {
        System.out.println("testLogin");

        tx.begin();
        EquipmentTypeEntity terminal = equipmentTypeRest.findType(EquipmentTypeEnum.TERMINAL);
        EquipmentTypeEntity monitor = equipmentTypeRest.findType(EquipmentTypeEnum.MONITOR);
        tx.commit();

        //設備登録なし.
        tx.begin();
        EquipmentLoginResult result = rest.login(null, EquipmentLoginRequest.identNameType(EquipmentTypeEnum.TERMINAL, "identTablet2"), null);
        tx.commit();

        assertThat(result.getIsSuccess(), is(false));
        assertThat(result.getErrorType(), is(ServerErrorTypeEnum.NOT_PERMIT_EQUIPMENT));

        //設備登録.
        EquipmentEntity tablet2 = new EquipmentEntity(0L, "nameTablet2", "identTablet2", terminal.getEquipmentTypeId(), 1L, null);
        EquipmentEntity tablet3 = new EquipmentEntity(0L, "nameTablet3", "identTablet3", monitor.getEquipmentTypeId(), 1L, null);
        tablet3.setIpv4Address("111.222.333.444");

        tx.begin();
        rest.add(tablet2, null);
        rest.add(tablet3, null);
        tx.commit();

        // 設備識別名で設備ログインする。
        result = rest.login(null, EquipmentLoginRequest.identNameType(EquipmentTypeEnum.TERMINAL, "identTablet2"), null);
        assertThat(result.getIsSuccess(), is(true));
        assertThat(result.getEquipmentId(), is(tablet2.getEquipmentId()));

        // IPアドレスで設備ログインする。
        result = rest.login(null, EquipmentLoginRequest.ip4AddressType(EquipmentTypeEnum.MONITOR, "111.222.333.444"), null);
        assertThat(result.getIsSuccess(), is(true));
        assertThat(result.getEquipmentId(), is(tablet3.getEquipmentId()));

        //設備削除.
        tx.begin();
        rest.remove(tablet2.getEquipmentId(), null);
        tx.commit();
    }

    @Test
    public void testGetEquipmentPerpetuity() throws Exception {
        System.out.println("testGetEquipmentPerpetuity");

        EquipmentEntity floor1 = new EquipmentEntity(0L, "nameFloor1", "identFloor1", null, null, null);

        tx.begin();
        rest.add(floor1, null);
        tx.commit();

        EquipmentEntity line1 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine1", "identLine1", null, null, null);
        EquipmentEntity line2 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine2", "identLine2", null, null, null);
        EquipmentEntity line3 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine3", "identLine3", null, null, null);

        tx.begin();
        rest.add(line1, null);
        rest.add(line2, null);
        rest.add(line3, null);
        tx.commit();

        EquipmentEntity equip1 = new EquipmentEntity(line1.getEquipmentId(), "nameEquip1", "identEquip1", null, null, null);
        EquipmentEntity equip2 = new EquipmentEntity(line1.getEquipmentId(), "nameEquip2", "identEquip2", null, null, null);
        EquipmentEntity equip3 = new EquipmentEntity(line1.getEquipmentId(), "nameEquip3", "identEquip3", null, null, null);
        EquipmentEntity equip4 = new EquipmentEntity(line2.getEquipmentId(), "nameEquip4", "identEquip4", null, null, null);
        EquipmentEntity equip5 = new EquipmentEntity(line2.getEquipmentId(), "nameEquip5", "identEquip5", null, null, null);
        EquipmentEntity equip6 = new EquipmentEntity(line2.getEquipmentId(), "nameEquip6", "identEquip6", null, null, null);
        EquipmentEntity equip7 = new EquipmentEntity(line3.getEquipmentId(), "nameEquip7", "identEquip7", null, null, null);
        EquipmentEntity equip8 = new EquipmentEntity(line3.getEquipmentId(), "nameEquip8", "identEquip8", null, null, null);
        EquipmentEntity equip9 = new EquipmentEntity(line3.getEquipmentId(), "nameEquip9", "identEquip9", null, null, null);

        tx.begin();
        rest.add(equip1, null);
        rest.add(equip2, null);
        rest.add(equip3, null);
        rest.add(equip4, null);
        rest.add(equip5, null);
        rest.add(equip6, null);
        rest.add(equip7, null);
        rest.add(equip8, null);
        rest.add(equip9, null);
        tx.commit();

        List<Long> list;
        list = rest.getEquipmentPerpetuity(floor1.getEquipmentId());
        assertThat(list, is(hasSize(13)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line1.getEquipmentId(), line2.getEquipmentId(), line3.getEquipmentId(),
                equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId(), equip4.getEquipmentId(), equip5.getEquipmentId(),
                equip6.getEquipmentId(), equip7.getEquipmentId(), equip8.getEquipmentId(), equip9.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(line1.getEquipmentId());
        assertThat(list, is(hasSize(5)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line1.getEquipmentId(), equip1.getEquipmentId(), equip2.getEquipmentId(), equip3.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(line2.getEquipmentId());
        assertThat(list, is(hasSize(5)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line2.getEquipmentId(), equip4.getEquipmentId(), equip5.getEquipmentId(), equip6.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(line3.getEquipmentId());
        assertThat(list, is(hasSize(5)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line3.getEquipmentId(), equip7.getEquipmentId(), equip8.getEquipmentId(), equip9.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip1.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line1.getEquipmentId(), equip1.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip2.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line1.getEquipmentId(), equip2.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip3.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line1.getEquipmentId(), equip3.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip4.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line2.getEquipmentId(), equip4.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip5.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line2.getEquipmentId(), equip5.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip6.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line2.getEquipmentId(), equip6.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip7.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line3.getEquipmentId(), equip7.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip8.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line3.getEquipmentId(), equip8.getEquipmentId())));

        list = rest.getEquipmentPerpetuity(equip9.getEquipmentId());
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(floor1.getEquipmentId(), line3.getEquipmentId(), equip9.getEquipmentId())));
    }

    @Test
    public void testSearch() throws Exception {
        System.out.println("testSearch");

        tx.begin();
        EquipmentTypeEntity terminal = equipmentTypeRest.findType(EquipmentTypeEnum.TERMINAL);
        EquipmentTypeEntity monitor = equipmentTypeRest.findType(EquipmentTypeEnum.MONITOR);
        tx.commit();
        
        EquipmentEntity floor1 = new EquipmentEntity(0L, "nameFloor1", "identFloor1", null, null, null);

        tx.begin();
        rest.add(floor1, null);
        tx.commit();

        EquipmentEntity line1 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine1", "identLine1", null, null, null);
        EquipmentEntity line2 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine2", "identLine2", null, null, null);

        tx.begin();
        rest.add(line1, null);
        rest.add(line2, null);
        tx.commit();

        EquipmentEntity equip1 = new EquipmentEntity(line1.getEquipmentId(), "nameEquip1", "identEquip1", terminal.getEquipmentTypeId(), null, null);
        equip1.setIpv4Address("192.168.0.1");

        EquipmentEntity equip2 = new EquipmentEntity(line1.getEquipmentId(), "nameEquip2", "identEquip2", terminal.getEquipmentTypeId(), null, null);
        equip2.setIpv4Address("192.168.0.2");

        EquipmentEntity equip3 = new EquipmentEntity(line1.getEquipmentId(), "nameEquip3", "identEquip3", terminal.getEquipmentTypeId(), null, null);
        equip3.setIpv4Address("192.168.0.3");

        EquipmentEntity equip4 = new EquipmentEntity(line2.getEquipmentId(), "nameEquip4", "identEquip4", terminal.getEquipmentTypeId(), null, null);

        EquipmentEntity equip5 = new EquipmentEntity(line2.getEquipmentId(), "nameEquip5", "identEquip5", monitor.getEquipmentTypeId(), null, null);

        EquipmentEntity equip6 = new EquipmentEntity(line2.getEquipmentId(), "nameEquip6", "identEquip6", monitor.getEquipmentTypeId(), null, null);
        equip6.setIpv4Address("192.168.0.2");

        tx.begin();
        rest.add(equip1, null);
        rest.add(equip2, null);
        rest.add(equip3, null);
        rest.add(equip4, null);
        rest.add(equip5, null);
        rest.add(equip6, null);
        tx.commit();

        EquipmentSearchCondition condition;
        List<EquipmentEntity> list;

        // 設備名で検索する。
        condition = new EquipmentSearchCondition("equip1", null, null, null, null);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(hasItems(equip1)));

        condition.setMatch(true);// 完全一致
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(0)));

        condition.setEquipmentName("nameEquip1");
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(hasItems(equip1)));

        // 設備識別指名で検索する。
        condition = new EquipmentSearchCondition(null, "equip2", null, null, null);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(hasItems(equip2)));

        condition.setMatch(true);// 完全一致
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(0)));

        condition.setEquipmentIdentName("identEquip2");
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(hasItems(equip2)));

        // 親設備の設備名で検索する。
        condition = new EquipmentSearchCondition(null, null, "line1", null, null);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(equip1, equip2, equip3)));

        condition.setMatch(true);// 完全一致
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(0)));

        condition.setParentName("nameLine1");
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(equip1, equip2, equip3)));

        // 親設備の設備識別名で検索する。
        condition = new EquipmentSearchCondition(null, null, null, "line2", null);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(equip4, equip5, equip6)));

        condition.setMatch(true);// 完全一致
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(0)));

        condition.setParentIdentName("identLine2");
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(equip4, equip5, equip6)));

        // 設備種別で検索する。
        condition = new EquipmentSearchCondition(null, null, null, null, EquipmentTypeEnum.TERMINAL);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(4)));
        assertThat(list, is(hasItems(equip1, equip2, equip3, equip4)));

        condition.setEquipmentType(EquipmentTypeEnum.MONITOR);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(2)));
        assertThat(list, is(hasItems(equip5, equip6)));

        // IPv4アドレスで検索する。
        condition = new EquipmentSearchCondition(null, null, null, null, null);
        condition.setIpv4Address("192.168.0.1");
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(hasItems(equip1)));

        condition.setIpv4Address("192.168.0.2");
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(2)));
        assertThat(list, is(hasItems(equip2, equip6)));

        condition.setEquipmentType(EquipmentTypeEnum.TERMINAL);
        list = rest.searchEquipment(condition, null, null, null);
        assertThat(list, is(hasSize(1)));
        assertThat(list, is(hasItems(equip2)));
    }

    @Test
    public void testFindNames() throws Exception {
        // findNamesでは測定機器による絞り込みを行うため取得
        tx.begin();
        EquipmentTypeEntity measure = equipmentTypeRest.findType(EquipmentTypeEnum.MEASURE);
        tx.commit();

        // IDはcommitするまで確定しないので一つずつ登録する
        EquipmentEntity floor1 = new EquipmentEntity(0L, "nameFloor1", "identFloor1", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(floor1, null);
        tx.commit();

        EquipmentEntity line1 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine1", "identLine1", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(line1, null);
        tx.commit();

        EquipmentEntity line2 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine2", "identLine2", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(line2, null);
        tx.commit();

        EquipmentEntity line3 = new EquipmentEntity(floor1.getEquipmentId(), "nameLine3", "identLine3", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(line3, null);
        tx.commit();

        EquipmentEntity line1a = new EquipmentEntity(floor1.getEquipmentId(), "nameLine1a", "identLine1a", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(line1a, null);
        tx.commit();

        EquipmentEntity line1b = new EquipmentEntity(floor1.getEquipmentId(), "nameLine1b", "identLine1b", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(line1b, null);
        tx.commit();

        EquipmentEntity line1c = new EquipmentEntity(floor1.getEquipmentId(), "nameLine1c", "identLine1c", measure.getEquipmentTypeId(), null, null);
        tx.begin();
        rest.add(line1c, null);
        tx.commit();

        List<EquipmentEntity> list = rest.findByNames(Arrays.asList("identLine1", "identLine2", "identLine3"), EquipmentTypeEnum.MEASURE);
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(hasItems(line1, line2, line3)));

        list = rest.findByNames(Arrays.asList("identLine11", "identLine12", "identLine13"), EquipmentTypeEnum.MEASURE);
        assertThat(list, is(hasSize(0)));

        list = rest.findByNames(Arrays.asList("identLine1", "identLine3", "identLine5"), EquipmentTypeEnum.MEASURE);
        assertThat(list, is(hasSize(2)));
        assertThat(list, is(hasItems(line1, line3)));

        // 辞書順となることを確認
        list = rest.findByNames(Arrays.asList("identLine3", "identLine1a", "identLine1"), EquipmentTypeEnum.MEASURE);
        assertThat(list, is(hasSize(3)));
        assertThat(list, is(contains(line1, line1a, line3)));
        assertThat(list, not(contains(line3, line1, line1a)));

        // 子階層も取得されることを確認
        list = rest.findByNames(Arrays.asList("identFloor1"), EquipmentTypeEnum.MEASURE);
        assertThat(list, is(hasSize(7)));
        assertThat(list, is(hasItems(floor1, line1, line2, line3, line1a, line1b, line1c)));
    }
}
