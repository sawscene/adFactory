/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.kanban.KanbanExistCollection;
import jp.adtekfuji.adFactory.entity.kanban.KanbanExistEntity;
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
 * @author ta-ito
 */
public class KanbanExistEntityTest {

    public KanbanExistEntityTest() {
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

        KanbanExistCollection collection = new KanbanExistCollection();
        KanbanExistEntity entity = new KanbanExistEntity();
        entity.setKanbanId(1L);
        entity.setKanbanName("kanban1");
        entity.setKanbanSubname("subKanban1");
        entity.setFkWorkflowId(1L);
        List<KanbanExistEntity> list = new ArrayList<>();
        list.add(entity);
        collection.setKanbanExistCollection(list);

        Document xml = EntityToXml.getXml(collection);

        String[] expectedNode1 = {"kanbanExistCollection"};
        String[] expectedNode2 = {"KanbanExistEntities"};
        String[] expectedNode3 = {"KanbanExistEntity"};
        String[] expectedNode4 = {"kanbanId", "kanbanName", "kanbanSubname", "fkWorkflowId"};
        String[] expectedValue4 = {"1", "kanban1", "subKanban1", "1",};

        Integer level1 = 0;
        Integer level2 = 0;
        Integer level3 = 0;
        Integer level4 = 0;
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
                    level2++;
                }
                level3 = 0;
                for (Node node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    if (node3 instanceof Element) {
                        System.out.print(node3.getNodeName() + ":" + node3.getTextContent() + CR);
                        assertThat(node3.getNodeName(), is(expectedNode3[level3]));
                        level3++;
                    }
                    level4 = 0;
                    for (Node node4 = node3.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                        if (node4 instanceof Element) {
                            System.out.print(node3.getNodeName() + ":" + node4.getTextContent() + CR);
                            assertThat(node4.getNodeName(), is(expectedNode4[level4]));
                            assertThat(node4.getTextContent(), is(expectedValue4[level4]));
                            level4++;
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testBindProperty() throws ConfigurationException, IOException {
    }
}
