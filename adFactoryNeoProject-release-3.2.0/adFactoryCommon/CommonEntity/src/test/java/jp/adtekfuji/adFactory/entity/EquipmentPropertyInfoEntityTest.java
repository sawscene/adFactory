/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author e-mori
 */
public class EquipmentPropertyInfoEntityTest {

    public EquipmentPropertyInfoEntityTest() {
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

        EquipmentPropertyInfoEntity entity = new EquipmentPropertyInfoEntity(1L, 2L, "name");
        entity.setEquipmentPropType(CustomPropertyTypeEnum.TYPE_STRING);
        entity.setEquipmentPropValue("value");
        entity.setEquipmentPropOrder(3);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"equipmentProperty"};
        String[] expectedNode2 = {"equipmentPropId", "fkMasterId", "equipmentPropName", "equipmentPropType", "equipmentPropValue", "equipmentPropOrder", "updatePersonId", "updateDateTime"};
        String[] expectedValue2 = {"1", "2", "name", "TYPE_STRING", "value", "3"};

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

        EquipmentPropertyInfoEntity equipmentProp1 = new EquipmentPropertyInfoEntity(1L, 1L, "name1");
        equipmentProp1.setEquipmentPropType(CustomPropertyTypeEnum.TYPE_STRING);
        equipmentProp1.setEquipmentPropValue("value1");
        EquipmentPropertyInfoEntity equipmentProp2 = new EquipmentPropertyInfoEntity(2L, 2L, "name2");
        equipmentProp2.setEquipmentPropType(CustomPropertyTypeEnum.TYPE_INTEGER);
        equipmentProp2.setEquipmentPropValue("value2");
        assertThat(equipmentProp1.getEquipmentPropId(), is(1l));
        assertThat(equipmentProp1.getFkMasterId(), is(1l));
        assertThat(equipmentProp1.getEquipmentPropName(), is("name1"));
        assertThat(equipmentProp1.getEquipmentPropType(), is(CustomPropertyTypeEnum.TYPE_STRING));
        assertThat(equipmentProp1.getEquipmentPropValue(), is("value1"));
        assertThat(equipmentProp2.getEquipmentPropId(), is(2l));
        assertThat(equipmentProp2.getFkMasterId(), is(2l));
        assertThat(equipmentProp2.getEquipmentPropName(), is("name2"));
        assertThat(equipmentProp2.getEquipmentPropType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
        assertThat(equipmentProp2.getEquipmentPropValue(), is("value2"));

        equipmentProp2.equipmentPropIdProperty().bind(equipmentProp1.equipmentPropIdProperty());
        equipmentProp2.fkMasterIdProperty().bind(equipmentProp1.fkMasterIdProperty());
        equipmentProp2.equipmentPropNameProperty().bind(equipmentProp1.equipmentPropNameProperty());
        equipmentProp2.equipmentPropTypeProperty().bind(equipmentProp1.equipmentPropTypeProperty());
        equipmentProp2.equipmentPropTypeProperty().bind(equipmentProp1.equipmentPropTypeProperty());
        equipmentProp2.equipmentPropValueProperty().bind(equipmentProp1.equipmentPropValueProperty());
        equipmentProp1.setEquipmentPropId(3L);
        equipmentProp1.setFkMasterId(3L);
        equipmentProp1.setEquipmentPropName("name3");
        equipmentProp1.setEquipmentPropType(CustomPropertyTypeEnum.TYPE_BOOLEAN);
        equipmentProp1.setEquipmentPropValue("value3");
        assertThat(equipmentProp1.getEquipmentPropId(), is(3l));
        assertThat(equipmentProp1.getFkMasterId(), is(3l));
        assertThat(equipmentProp1.getEquipmentPropName(), is("name3"));
        assertThat(equipmentProp1.getEquipmentPropType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));
        assertThat(equipmentProp1.getEquipmentPropValue(), is("value3"));
        assertThat(equipmentProp2.getEquipmentPropId(), is(3l));
        assertThat(equipmentProp2.getFkMasterId(), is(3l));
        assertThat(equipmentProp2.getEquipmentPropName(), is("name3"));
        assertThat(equipmentProp2.getEquipmentPropType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));
        assertThat(equipmentProp2.getEquipmentPropValue(), is("value3"));
    }
}
