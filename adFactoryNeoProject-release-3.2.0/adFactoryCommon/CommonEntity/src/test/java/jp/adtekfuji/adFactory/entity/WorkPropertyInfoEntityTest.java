/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
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
 * @author ta.ito
 */
public class WorkPropertyInfoEntityTest {

    public WorkPropertyInfoEntityTest() {
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

        WorkPropertyInfoEntity entity = new WorkPropertyInfoEntity(1L, 2L, "name", CustomPropertyTypeEnum.TYPE_STRING, "value", 3);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"workProperty"};
        String[] expectedNode2 = {"workPropId", "fkMasterId", "workPropName", "workPropType", "workPropValue", "workPropOrder", "updatePersonId", "updateDateTime"};
        String[] expectedValue2 = {"1", "2", "name", CustomPropertyTypeEnum.TYPE_STRING.toString(), "value", "3"};

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

        WorkPropertyInfoEntity workProp1 = new WorkPropertyInfoEntity(1L, 1L, "name1", CustomPropertyTypeEnum.TYPE_STRING, "value1", 1);
        WorkPropertyInfoEntity workProp2 = new WorkPropertyInfoEntity(2L, 2L, "name2", CustomPropertyTypeEnum.TYPE_BOOLEAN, "value2", 2);
        assertThat(workProp1.getWorkPropId(), is(1l));
        assertThat(workProp1.getFkMasterId(), is(1l));
        assertThat(workProp1.getWorkPropName(), is("name1"));
        assertThat(workProp1.getWorkPropType(), is(CustomPropertyTypeEnum.TYPE_STRING));
        assertThat(workProp1.getWorkPropValue(), is("value1"));
        assertThat(workProp1.getWorkPropOrder(), is(1));
        assertThat(workProp2.getWorkPropId(), is(2l));
        assertThat(workProp2.getFkMasterId(), is(2l));
        assertThat(workProp2.getWorkPropName(), is("name2"));
        assertThat(workProp2.getWorkPropType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));
        assertThat(workProp2.getWorkPropValue(), is("value2"));
        assertThat(workProp2.getWorkPropOrder(), is(2));

        workProp2.workPropIdProperty().bind(workProp1.workPropIdProperty());
        workProp2.fkMasterIdProperty().bind(workProp1.fkMasterIdProperty());
        workProp2.workPropNameProperty().bind(workProp1.workPropNameProperty());
        workProp2.workPropTypeProperty().bind(workProp1.workPropTypeProperty());
        workProp2.workPropValueProperty().bind(workProp1.workPropValueProperty());
        workProp2.workPropOrderProperty().bind(workProp1.workPropOrderProperty());
        workProp1.setWorkPropId(3L);
        workProp1.setFkMasterId(3L);
        workProp1.setWorkPropName("name3");
        workProp1.setWorkPropType(CustomPropertyTypeEnum.TYPE_INTEGER);
        workProp1.setWorkPropValue("value3");
        workProp1.setWorkPropOrder(3);
        assertThat(workProp1.getWorkPropId(), is(3l));
        assertThat(workProp1.getFkMasterId(), is(3l));
        assertThat(workProp1.getWorkPropName(), is("name3"));
        assertThat(workProp1.getWorkPropType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
        assertThat(workProp1.getWorkPropValue(), is("value3"));
        assertThat(workProp1.getWorkPropOrder(), is(3));
        assertThat(workProp2.getWorkPropId(), is(3l));
        assertThat(workProp2.getFkMasterId(), is(3l));
        assertThat(workProp2.getWorkPropName(), is("name3"));
        assertThat(workProp2.getWorkPropType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
        assertThat(workProp2.getWorkPropValue(), is("value3"));
        assertThat(workProp2.getWorkPropOrder(), is(3));
    }
}
