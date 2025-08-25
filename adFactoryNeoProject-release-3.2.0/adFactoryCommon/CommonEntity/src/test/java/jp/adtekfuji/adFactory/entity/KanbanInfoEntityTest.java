/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
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
public class KanbanInfoEntityTest {

    public KanbanInfoEntityTest() {
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

        KanbanInfoEntity entity = new KanbanInfoEntity(1L, 2L, "name1", "subName");
        entity.setFkWorkflowId(3L);
        entity.setWorkflowName("workflow");
        entity.setStartDatetime(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime());
        entity.setCompDatetime(new GregorianCalendar(2015, 11, 30, 14, 47, 6).getTime());
        entity.setFkUpdatePersonId(4L);
        entity.setUpdateDatetime(new GregorianCalendar(2015, 0, 30, 14, 47, 6).getTime());
        entity.setKanbanStatus(KanbanStatusEnum.PLANNED);
        entity.setFkInterruptReasonId(5L);
        entity.setFkDelayReasonId(6L);

        List<KanbanPropertyInfoEntity> knbanPros = new ArrayList<>();
        KanbanPropertyInfoEntity kanbanPro = new KanbanPropertyInfoEntity(1L, 2L, "name2", CustomPropertyTypeEnum.TYPE_STRING, "value", 3);
        knbanPros.add(kanbanPro);
        entity.setPropertyCollection(knbanPros);
        List<WorkKanbanInfoEntity> wKanbans = new ArrayList<>();
        WorkKanbanInfoEntity wKanban = new WorkKanbanInfoEntity(1L, 2L, 3L, 4L, 5L, "work1");
        wKanbans.add(wKanban);
        entity.setWorkKanbanCollection(wKanbans);
        List<WorkKanbanInfoEntity> sepaWKanbans = new ArrayList<>();
        WorkKanbanInfoEntity sepaWKanban = new WorkKanbanInfoEntity(7L, 8L, 9L, 10L, 11L, "work2");
        sepaWKanbans.add(sepaWKanban);
        entity.setSeparateworkKanbanCollection(sepaWKanbans);

        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"kanban"};
        String[] expectedNode2 = {
            "kanbanId", "parentId", "kanbanName", "kanbanSubname", "fkWorkflowId", "workflowName",
            "startDatetime", "compDatetime", "fkUpdatePersonId", "updateDatetime",
            "kanbanStatus", "fkInterruptReasonId", "fkDelayReasonId",
            "workKanbans", "separateworkKanbans", "productionType"
        };
        String[] expectedValue2 = {
            "1", "2", "name1", "subName", "3", "workflow",
            "2015-11-30T14:47:06+09:00", "2015-12-30T14:47:06+09:00", "4", "2015-01-30T14:47:06+09:00",
            "PLANNED", "5", "6",
            "12345work1", "7891011work2", "0"
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

        KanbanInfoEntity entity1 = new KanbanInfoEntity(1L, 2L, "name1", "subName1");
        KanbanInfoEntity entity2 = new KanbanInfoEntity(7L, 8L, "name2", "subName2");
        assertThat(entity1.getKanbanId(), is(1l));
        assertThat(entity1.getParentId(), is(2L));
        assertThat(entity1.getKanbanName(), is("name1"));
        assertThat(entity1.getKanbanSubname(), is("subName1"));
        assertThat(entity2.getKanbanId(), is(7L));
        assertThat(entity2.getParentId(), is(8L));
        assertThat(entity2.getKanbanName(), is("name2"));
        assertThat(entity2.getKanbanSubname(), is("subName2"));

        entity2.kanbanIdProperty().bind(entity1.kanbanIdProperty());
        entity2.parentIdProperty().bind(entity1.parentIdProperty());
        entity2.kanbanNameProperty().bind(entity1.kanbanNameProperty());
        entity2.kanbanSubnameProperty().bind(entity1.kanbanSubnameProperty());
        entity1.setKanbanId(3L);
        entity1.setParentId(4L);
        entity1.setKanbanName("name3");
        entity1.setKanbanSubname("subName3");
        assertThat(entity1.getKanbanId(), is(3l));
        assertThat(entity1.getParentId(), is(4L));
        assertThat(entity1.getKanbanName(), is("name3"));
        assertThat(entity1.getKanbanSubname(), is("subName3"));
        assertThat(entity2.getKanbanId(), is(3l));
        assertThat(entity2.getParentId(), is(4L));
        assertThat(entity2.getKanbanName(), is("name3"));
        assertThat(entity2.getKanbanSubname(), is("subName3"));
    }
}
