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
import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author e-mori
 */
public class KanbanHierarchyInfoEntityTest {

    public KanbanHierarchyInfoEntityTest() {
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

        KanbanHierarchyInfoEntity entity = new KanbanHierarchyInfoEntity(1L, "name");
        entity.setParentId(2L);
        List<KanbanInfoEntity> kanbans = new ArrayList<>();
        KanbanInfoEntity entity1 = new KanbanInfoEntity(1L, 1L, "kanban", "subKanban");
        kanbans.add(entity1);        
        entity.setKanbanCollection(kanbans);

        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"kanbanHierarchy"};
        String[] expectedNode2 = {"kanbanHierarchyId", "parentId", "hierarchyName", "kanbans", "partitionFlag"};
        String[] expectedValue2 = {"1", "2", "name", "11kanbansubKanban0", "false"};

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
    @Ignore
    public void testBindProperty() throws ConfigurationException, IOException {
        System.out.println("testBindProperty");

        KanbanHierarchyInfoEntity hierarchy1 = new KanbanHierarchyInfoEntity(1L, "name1");
        KanbanHierarchyInfoEntity hierarchy2 = new KanbanHierarchyInfoEntity(2L, "name2");
        assertThat(hierarchy1.getKanbanHierarchyId(), is(1l));
        assertThat(hierarchy1.getHierarchyName(), is("name1"));
        assertThat(hierarchy2.getKanbanHierarchyId(), is(2l));
        assertThat(hierarchy2.getHierarchyName(), is("name2"));

        hierarchy2.kanbanHierarchyIdProperty().bind(hierarchy1.kanbanHierarchyIdProperty());
        hierarchy2.hierarchyNameProperty().bind(hierarchy1.hierarchyNameProperty());
        hierarchy1.setKanbanHierarchyId(3L);
        hierarchy1.setHierarchyName("name3");
        assertThat(hierarchy1.getKanbanHierarchyId(), is(3l));
        assertThat(hierarchy1.getHierarchyName(), is("name3"));
        assertThat(hierarchy2.getKanbanHierarchyId(), is(3l));
        assertThat(hierarchy2.getHierarchyName(), is("name3"));
    }
    
}
