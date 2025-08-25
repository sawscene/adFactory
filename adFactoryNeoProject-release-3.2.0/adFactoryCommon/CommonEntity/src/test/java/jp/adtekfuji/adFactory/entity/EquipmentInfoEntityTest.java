/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import adtekfuji.utility.DateUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentSettingInfoEntity;
import jp.adtekfuji.adFactory.enumerate.TermUnitEnum;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author e-mori
 */
public class EquipmentInfoEntityTest {

    public EquipmentInfoEntityTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * testXml
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testXml() throws Exception {
        System.out.println("testXml");

        EquipmentInfoEntity entity = new EquipmentInfoEntity(1L, "identify", "name", 0L);
        entity.setParentId(0L);
        entity.setUpdatePersonId(2L);
        entity.setUpdateDateTime(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime());
        List<EquipmentPropertyInfoEntity> propertyInfoEntitys = new ArrayList<>();
        propertyInfoEntitys.add(new EquipmentPropertyInfoEntity(1L, 2L, "proname"));
        entity.setPropertyInfoCollection(propertyInfoEntitys);
        List<EquipmentSettingInfoEntity> settingInfoEntitys = new ArrayList<>();
        settingInfoEntitys.add(new EquipmentSettingInfoEntity());
        entity.setSettingInfoCollection(settingInfoEntitys);

        entity.setCalFlag(false);
        entity.setCalNextDate(DateUtils.parse("2015/11/30 00:00:00"));
        entity.setCalLastDate(DateUtils.parse("2015/11/30 00:00:00"));
        entity.setCalTerm(4);
        entity.setCalTermUnit(TermUnitEnum.MONTHLY);
        entity.setCalPersonId(0L);
        entity.setCalWarningDays(10);

        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"equipment"};

        String[] expectedNode2 = {"equipmentId", "parentId", "equipmentIdentify", "equipmentName", "equipmentTypeId",
                "updatePersonId", "updateDatetime", "childCount", "licenseCount", //"equipmentPropertys", "equipmentSettings",
                "calFlag", "calNextDate", "calLastDate", "calWarningDays", "calTerm", "calTermUnit", "calPersonId",
                "ipv4Address", "workProgressFlag", "pluginName","localeFileInfos","liteCount","reporterCount"
        };

        String[] expectedValue2 = {"1", "0", "identify", "name", "0",
                "2", "2015-11-30T14:47:06+09:00", "0", "0", //"12proname", "",
                "false", "2015-11-30T00:00:00+09:00", "2015-11-30T00:00:00+09:00", "10", "4", "MONTHLY", "0",
                "", "false", "", "", "0", "0"
        };

        Integer level1 = 0;
        Integer level2 = 0;
        for (Node node1 = xml.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
            if (node1 instanceof Element) {
                System.out.print(node1.getNodeName() + ":" + node1.getTextContent() + CR);
                assertThat(node1.getNodeName(), is(expectedNode1[level1]));
                level1++;
            }
            level2 = 0;
            for (Node node2 = node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2 instanceof Element) {
                    System.out.print(node2.getNodeName() + ":" + node2.getTextContent() + CR);
                    assertThat(node2.getNodeName(), is(expectedNode2[level2]));
                    assertThat(node2.getTextContent(), is(expectedValue2[level2]));
                    level2++;
                }
            }
        }
    }

    @Test
    public void testBindProperty() throws ConfigurationException, IOException {
        System.out.println("testBindProperty");

        EquipmentInfoEntity equipment1 = new EquipmentInfoEntity(1L, "ident1", "name1", 1L);
        EquipmentInfoEntity equipment2 = new EquipmentInfoEntity(2L, "ident2", "name2", 2L);
        assertThat(equipment1.getEquipmentId(), is(1l));
        assertThat(equipment1.getEquipmentIdentify(), is("ident1"));
        assertThat(equipment1.getEquipmentName(), is("name1"));
        assertThat(equipment1.getEquipmentType(), is(1L));
        assertThat(equipment2.getEquipmentId(), is(2l));
        assertThat(equipment2.getEquipmentIdentify(), is("ident2"));
        assertThat(equipment2.getEquipmentName(), is("name2"));
        assertThat(equipment2.getEquipmentType(), is(2L));

        equipment2.equipmentIdProperty().bind(equipment1.equipmentIdProperty());
        equipment2.equipmentIdentifyProperty().bind(equipment1.equipmentIdentifyProperty());
        equipment2.equipmentNameProperty().bind(equipment1.equipmentNameProperty());
        equipment2.equipmentTypeIdProperty().bind(equipment1.equipmentTypeIdProperty());
        equipment1.setEquipmentId(3L);
        equipment1.setEquipmentIdentify("ident3");
        equipment1.setEquipmentName("name3");
        equipment1.setEquipmentType(3L);
        assertThat(equipment1.getEquipmentId(), is(3l));
        assertThat(equipment1.getEquipmentIdentify(), is("ident3"));
        assertThat(equipment1.getEquipmentName(), is("name3"));
        assertThat(equipment1.getEquipmentType(), is(3L));
        assertThat(equipment2.getEquipmentId(), is(3l));
        assertThat(equipment2.getEquipmentIdentify(), is("ident3"));
        assertThat(equipment2.getEquipmentName(), is("name3"));
        assertThat(equipment2.getEquipmentType(), is(3L));
    }
}
