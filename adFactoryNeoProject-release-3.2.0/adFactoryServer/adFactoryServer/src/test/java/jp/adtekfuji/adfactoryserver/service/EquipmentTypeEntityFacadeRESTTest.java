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
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import org.junit.After;
import org.junit.AfterClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author z-hideo
 */
public class EquipmentTypeEntityFacadeRESTTest {
    
    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;
    private static EntityTransaction tx = null;
    private static EquipmentTypeEntityFacadeREST equipmentTypeRest = null;
    private static AccessHierarchyEntityFacadeREST authRest = null;
    
    public EquipmentTypeEntityFacadeRESTTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test_adFactoryServerPU");
        em = emf.createEntityManager();
               
        equipmentTypeRest = new EquipmentTypeEntityFacadeREST();
        equipmentTypeRest.setEntityManager(em);
        
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

    /**
     * Test of findType method, of class EquipmentTypeEntityFacadeREST.
     */
    @Test
    public void testEquipmentTypeEntityFacadeREST() throws Exception {
        System.out.println("findType");
        
        tx.begin();
        EquipmentTypeEntity terminal = equipmentTypeRest.findType(EquipmentTypeEnum.TERMINAL);
        EquipmentTypeEntity monitor = equipmentTypeRest.findType(EquipmentTypeEnum.MONITOR);
        EquipmentTypeEntity manufacture = equipmentTypeRest.findType(EquipmentTypeEnum.MANUFACTURE);
        EquipmentTypeEntity measure = equipmentTypeRest.findType(EquipmentTypeEnum.MEASURE);
        EquipmentTypeEntity lite = equipmentTypeRest.findType(EquipmentTypeEnum.LITE);
        EquipmentTypeEntity reporter = equipmentTypeRest.findType(EquipmentTypeEnum.REPORTER);
        tx.commit();
        
        // 作業者端末
        assertThat(terminal.getName(), is(EquipmentTypeEnum.TERMINAL));
        assertThat(terminal.getEquipmentTypeId(), is(1L));

        // 進捗モニター
        assertThat(monitor.getName(), is(EquipmentTypeEnum.MONITOR));
        assertThat(monitor.getEquipmentTypeId(), is(2L));
        
        // 製造設備
        assertThat(manufacture.getName(), is(EquipmentTypeEnum.MANUFACTURE));
        assertThat(manufacture.getEquipmentTypeId(), is(3L));
        
        // 測定機器
        assertThat(measure.getName(), is(EquipmentTypeEnum.MEASURE));
        assertThat(measure.getEquipmentTypeId(), is(4L));
        
        // Lite
        assertThat(lite.getName(), is(EquipmentTypeEnum.LITE));
        assertThat(lite.getEquipmentTypeId(), is(5L));

        // Reporter
        assertThat(reporter.getName(), is(EquipmentTypeEnum.REPORTER));
        assertThat(reporter.getEquipmentTypeId(), is(6L));

        List<EquipmentTypeEntity> list = equipmentTypeRest.findAll(null);
        assertThat(list, is(hasSize(6)));
        assertThat(list, is(hasItems(terminal, monitor, manufacture, measure, lite, reporter)));
    }
}
