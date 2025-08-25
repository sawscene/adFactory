/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
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
public class WorkKanbanPropertyInfoEntityTest {

    public WorkKanbanPropertyInfoEntityTest() {
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

        WorkKanbanPropertyInfoEntity entity = new WorkKanbanPropertyInfoEntity(1L, 2L, "name", CustomPropertyTypeEnum.TYPE_STRING, "value", 3);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"workKanbanProperty"};
        String[] expectedNode2 = {"workKanbannPropertyId", "fkWorkKanbanId", "workKanbanPropName", "workKanbanPropType", "workKanbanPropValue", "workKanbanPropOrder", "updatePersonId", "updateDateTime"};
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

        WorkKanbanPropertyInfoEntity workKanbanProp1 = new WorkKanbanPropertyInfoEntity(1L, 1L, "name1", CustomPropertyTypeEnum.TYPE_STRING, "value1", 1);
        WorkKanbanPropertyInfoEntity workKanbanProp2 = new WorkKanbanPropertyInfoEntity(2L, 2L, "name2", CustomPropertyTypeEnum.TYPE_INTEGER, "value2", 2);
        assertThat(workKanbanProp1.getWorkKanbanPropId(), is(1l));
        assertThat(workKanbanProp1.getFkMasterId(), is(1l));
        assertThat(workKanbanProp1.getWorkKanbanPropName(), is("name1"));
        assertThat(workKanbanProp1.getWorkKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_STRING));
        assertThat(workKanbanProp1.getWorkKanbanPropValue(), is("value1"));
        assertThat(workKanbanProp2.getWorkKanbanPropId(), is(2l));
        assertThat(workKanbanProp2.getFkMasterId(), is(2l));
        assertThat(workKanbanProp2.getWorkKanbanPropName(), is("name2"));
        assertThat(workKanbanProp2.getWorkKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
        assertThat(workKanbanProp2.getWorkKanbanPropValue(), is("value2"));

        workKanbanProp2.workKanbanPropIdProperty().bind(workKanbanProp1.workKanbanPropIdProperty());
        workKanbanProp2.fkMasterIdProperty().bind(workKanbanProp1.fkMasterIdProperty());
        workKanbanProp2.workKanbanPropNameProperty().bind(workKanbanProp1.workKanbanPropNameProperty());
        workKanbanProp2.workKanbanPropTypeProperty().bind(workKanbanProp1.workKanbanPropTypeProperty());
        workKanbanProp2.workKanbanPropValueProperty().bind(workKanbanProp1.workKanbanPropValueProperty());
        workKanbanProp1.setWorkKanbanPropId(3L);
        workKanbanProp1.setFkMasterId(3L);
        workKanbanProp1.setWorkKanbanPropName("name3");
        workKanbanProp1.setWorkKanbanPropType(CustomPropertyTypeEnum.TYPE_BOOLEAN);
        workKanbanProp1.setWorkKanbanPropValue("value3");
        assertThat(workKanbanProp1.getWorkKanbanPropId(), is(3l));
        assertThat(workKanbanProp1.getFkMasterId(), is(3l));
        assertThat(workKanbanProp1.getWorkKanbanPropName(), is("name3"));
        assertThat(workKanbanProp1.getWorkKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));
        assertThat(workKanbanProp2.getWorkKanbanPropValue(), is("value3"));
        assertThat(workKanbanProp2.getWorkKanbanPropId(), is(3l));
        assertThat(workKanbanProp2.getFkMasterId(), is(3l));
        assertThat(workKanbanProp2.getWorkKanbanPropName(), is("name3"));
        assertThat(workKanbanProp2.getWorkKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));
        assertThat(workKanbanProp2.getWorkKanbanPropValue(), is("value3"));
    }
}
