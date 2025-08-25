/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
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
public class KanbanPropertyInfoEntityTest {

    public KanbanPropertyInfoEntityTest() {
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

        KanbanPropertyInfoEntity entity = new KanbanPropertyInfoEntity(1L, 2L, "name", CustomPropertyTypeEnum.TYPE_STRING, "value", 3);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"kanbanProperty"};
        String[] expectedNode2 = {"kanbannPropertyId", "fkKanbanId", "kanbanPropertyName", "kanbanPropertyType", "kanbanPropertyValue", "kanbanPropertyOrder"};
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

        KanbanPropertyInfoEntity kanbanProp1 = new KanbanPropertyInfoEntity(1L, 1L, "name1", CustomPropertyTypeEnum.TYPE_BOOLEAN, "test1", 1);
        KanbanPropertyInfoEntity kanbanProp2 = new KanbanPropertyInfoEntity(2L, 2L, "name2", CustomPropertyTypeEnum.TYPE_INTEGER, "test2", 2);
        assertThat(kanbanProp1.getKanbanPropId(), is(1l));
        assertThat(kanbanProp1.getFkKanbanId(), is(1l));
        assertThat(kanbanProp1.getKanbanPropertyName(), is("name1"));
        assertThat(kanbanProp1.getKanbanPropertyType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));
        assertThat(kanbanProp1.getKanbanPropertyValue(), is("test1"));
        assertThat(kanbanProp1.getKanbanPropertyOrder(), is(1));
        assertThat(kanbanProp2.getKanbanPropId(), is(2l));
        assertThat(kanbanProp2.getFkKanbanId(), is(2l));
        assertThat(kanbanProp2.getKanbanPropertyName(), is("name2"));
        assertThat(kanbanProp2.getKanbanPropertyType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
        assertThat(kanbanProp2.getKanbanPropertyValue(), is("test2"));
        assertThat(kanbanProp2.getKanbanPropertyOrder(), is(2));

        kanbanProp2.kanbanPropIdProperty().bind(kanbanProp1.kanbanPropIdProperty());
        kanbanProp2.fkMasterIdProperty().bind(kanbanProp1.fkMasterIdProperty());
        kanbanProp2.kanbanPropNameProperty().bind(kanbanProp1.kanbanPropNameProperty());
        kanbanProp2.kanbanPropTypeProperty().bind(kanbanProp1.kanbanPropTypeProperty());
        kanbanProp2.kanbanPropValueProperty().bind(kanbanProp1.kanbanPropValueProperty());
        kanbanProp2.kanbanPropOrderProperty().bind(kanbanProp1.kanbanPropOrderProperty());
        kanbanProp1.setKanbanPropId(3L);
        kanbanProp1.setFkKanbanId(3L);
        kanbanProp1.setKanbanPropertyName("name3");
        kanbanProp1.setKanbanPropertyType(CustomPropertyTypeEnum.TYPE_STRING);
        kanbanProp1.setKanbanPropertyValue("test3");
        kanbanProp1.setKanbanPropertyOrder(3);
        assertThat(kanbanProp1.getKanbanPropId(), is(3l));
        assertThat(kanbanProp1.getFkKanbanId(), is(3l));
        assertThat(kanbanProp1.getKanbanPropertyName(), is("name3"));
        assertThat(kanbanProp1.getKanbanPropertyType(), is(CustomPropertyTypeEnum.TYPE_STRING));
        assertThat(kanbanProp1.getKanbanPropertyValue(), is("test3"));
        assertThat(kanbanProp1.getKanbanPropertyOrder(), is(3));
        assertThat(kanbanProp2.getKanbanPropId(), is(3l));
        assertThat(kanbanProp2.getFkKanbanId(), is(3l));
        assertThat(kanbanProp2.getKanbanPropertyName(), is("name3"));
        assertThat(kanbanProp2.getKanbanPropertyType(), is(CustomPropertyTypeEnum.TYPE_STRING));
        assertThat(kanbanProp2.getKanbanPropertyValue(), is("test3"));
        assertThat(kanbanProp2.getKanbanPropertyOrder(), is(3));
    }
}
