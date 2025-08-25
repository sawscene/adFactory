/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.workflow.KanbanPropertyTemplateInfoEntity;
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
public class KanbanPropertyTemplateInfoEntityTest {

    public KanbanPropertyTemplateInfoEntityTest() {
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

        KanbanPropertyTemplateInfoEntity entity = new KanbanPropertyTemplateInfoEntity(1L, 2L, "name", CustomPropertyTypeEnum.TYPE_STRING, "value", 3);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"kanbanPropertyTemplate"};
        String[] expectedNode2 = {"kanbanPropTemplateId", "fkMasterId", "propertyName", "propertyType", "propInitialValue", "propertyOrder"};
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

        KanbanPropertyTemplateInfoEntity kanbanT1 = new KanbanPropertyTemplateInfoEntity(1L, 1L, "name1", CustomPropertyTypeEnum.TYPE_STRING, "value1", 1);
        KanbanPropertyTemplateInfoEntity kanbanT2 = new KanbanPropertyTemplateInfoEntity(2L, 2L, "name2", CustomPropertyTypeEnum.TYPE_BOOLEAN, "Value2", 1);
        assertThat(kanbanT1.getKanbanPropId(), is(1l));
        assertThat(kanbanT1.getFkMasterId(), is(1l));
        assertThat(kanbanT1.getKanbanPropName(), is("name1"));
        assertThat(kanbanT1.getKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_STRING));
        assertThat(kanbanT2.getKanbanPropId(), is(2l));
        assertThat(kanbanT2.getFkMasterId(), is(2l));
        assertThat(kanbanT2.getKanbanPropName(), is("name2"));
        assertThat(kanbanT2.getKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_BOOLEAN));

        kanbanT2.kanbanPropIdProperty().bind(kanbanT1.kanbanPropIdProperty());
        kanbanT2.fkMasterIdProperty().bind(kanbanT1.fkMasterIdProperty());
        kanbanT2.kanbanPropNameProperty().bind(kanbanT1.kanbanPropNameProperty());
        kanbanT2.kanbanPropTypeProperty().bind(kanbanT1.kanbanPropTypeProperty());
        kanbanT1.setKanbanPropId(3L);
        kanbanT1.setFkMasterId(3L);
        kanbanT1.setKanbanPropName("name3");
        kanbanT1.setKanbanPropType(CustomPropertyTypeEnum.TYPE_INTEGER);
        assertThat(kanbanT1.getKanbanPropId(), is(3l));
        assertThat(kanbanT1.getFkMasterId(), is(3l));
        assertThat(kanbanT1.getKanbanPropName(), is("name3"));
        assertThat(kanbanT1.getKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
        assertThat(kanbanT2.getKanbanPropId(), is(3l));
        assertThat(kanbanT2.getFkMasterId(), is(3l));
        assertThat(kanbanT2.getKanbanPropName(), is("name3"));
        assertThat(kanbanT2.getKanbanPropType(), is(CustomPropertyTypeEnum.TYPE_INTEGER));
    }
}
