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
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;
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
public class WorkKanbanInfoEntityTest {

    public WorkKanbanInfoEntityTest() {
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

        WorkKanbanInfoEntity entity = new WorkKanbanInfoEntity(1l, 2l, 3l, 4l, 5l, "work");
        entity.setSeparateWorkFlag(true);
        entity.setImplementFlag(true);
        entity.setSkipFlag(true);
        entity.setStartDatetime(new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime());
        entity.setCompDatetime(new GregorianCalendar(2015, 11, 30, 14, 47, 6).getTime());
        entity.setTaktTime(100);
        entity.setFkUpdatePersonId(6l);
        entity.setUpdateDatetime(new GregorianCalendar(2015, 0, 30, 14, 47, 6).getTime());
        entity.setWorkStatus(KanbanStatusEnum.PLANNED);
        entity.setFkInterruptReasonId(7l);
        entity.setFkDelayReasonId(8l);
        List<WorkKanbanPropertyInfoEntity> propertyCollection = new ArrayList<>();
        WorkKanbanPropertyInfoEntity entity1 = new WorkKanbanPropertyInfoEntity(1L, 2L, "name", CustomPropertyTypeEnum.TYPE_STRING, "value", 3);
        propertyCollection.add(entity1);
        entity.setPropertyCollection(propertyCollection);
        List<Long> equipments = new ArrayList<>();
        equipments.add(1l);
        entity.setEquipmentCollection(equipments);
        List<Long> organizations = new ArrayList<>();
        organizations.add(1l);
        entity.setOrganizationCollection(organizations);

        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"workKanban"};
        String[] expectedNode2 = {
            "workKanbanId", "parentId", "fkKanbanId", "fkWorkflowId", "fkWorkId", "workName",
            "separateWorkFlag", "implementFlag", "skipFlag", "startDatetime", "compDatetime", "taktTime",
            "fkUpdatePersonId", "updateDatetime", "workStatus",
            "fkInterruptReasonId", "fkDelayReasonId", "equipments", "organizations"
        };
        String[] expectedValue2 = {
            "1", "2", "3", "4", "5", "work",
            "true", "true", "true", "2015-11-30T14:47:06+09:00", "2015-12-30T14:47:06+09:00", "100",
            "6", "2015-01-30T14:47:06+09:00", "PLANNED",
            "7", "8", "1", "1"
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

        WorkKanbanInfoEntity entity1 = new WorkKanbanInfoEntity(1l, 2l, 3l, 4l, 5l, "work1");
        WorkKanbanInfoEntity entity2 = new WorkKanbanInfoEntity(6l, 7l, 8l, 9l, 10l, "work2");

        assertThat(entity1.getWorkKanbanId(), is(1l));
        assertThat(entity1.getParentId(), is(2l));
        assertThat(entity1.getFkKanbanId(), is(3l));
        assertThat(entity1.getFkWorkflowId(), is(4L));
        assertThat(entity1.getFkWorkId(), is(5l));
        assertThat(entity1.getWorkName(), is("work1"));
        assertThat(entity2.getWorkKanbanId(), is(6l));
        assertThat(entity2.getParentId(), is(7l));
        assertThat(entity2.getFkKanbanId(), is(8l));
        assertThat(entity2.getFkWorkflowId(), is(9l));
        assertThat(entity2.getFkWorkId(), is(10l));
        assertThat(entity2.getWorkName(), is("work2"));

        entity2.workKanbanIdProperty().bind(entity1.workKanbanIdProperty());
        entity2.parentIdProperty().bind(entity1.parentIdProperty());
        entity2.fkKanbanIdProperty().bind(entity1.fkKanbanIdProperty());
        entity2.fkWorkflowIdProperty().bind(entity1.fkWorkflowIdProperty());
        entity2.fkWorkIdProperty().bind(entity1.fkWorkIdProperty());
        entity2.workNameProperty().bind(entity1.workNameProperty());
        entity1.setWorkKanbanId(11l);
        entity1.setParentId(12l);
        entity1.setFkKanbanId(13l);
        entity1.setFkWorkflowId(14l);
        entity1.setFkWorkId(15l);
        entity1.setWorkName("work3");
        assertThat(entity1.getWorkKanbanId(), is(11l));
        assertThat(entity1.getParentId(), is(12l));
        assertThat(entity1.getFkKanbanId(), is(13l));
        assertThat(entity1.getFkWorkflowId(), is(14l));
        assertThat(entity1.getFkWorkId(), is(15l));
        assertThat(entity1.getWorkName(), is("work3"));
        assertThat(entity2.getWorkKanbanId(), is(11l));
        assertThat(entity2.getParentId(), is(12l));
        assertThat(entity2.getFkKanbanId(), is(13l));
        assertThat(entity2.getFkWorkflowId(), is(14l));
        assertThat(entity2.getFkWorkId(), is(15l));
        assertThat(entity2.getWorkName(), is("work3"));
    }
}
