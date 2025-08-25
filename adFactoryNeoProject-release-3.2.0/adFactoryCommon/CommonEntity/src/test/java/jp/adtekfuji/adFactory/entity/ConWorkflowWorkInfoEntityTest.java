/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.IOException;
import java.util.GregorianCalendar;
import javax.naming.ConfigurationException;
import static jp.adtekfuji.adFactory.entity.EntityToXml.CR;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author ta.ito
 */
public class ConWorkflowWorkInfoEntityTest {

    public ConWorkflowWorkInfoEntityTest() {
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

        ConWorkflowWorkInfoEntity entity = new ConWorkflowWorkInfoEntity(1L, 2L, 3L, true, 4);
        entity.setStandardStartTime(new GregorianCalendar(2015, 10, 11, 9, 47, 6).getTime());
        entity.setStandardEndTime(new GregorianCalendar(2015, 10, 12, 9, 47, 6).getTime());
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"conWorkflowWork"};
        String[] expectedNode2 = {"workKbn", "associationId", "fkWorkflowId", "fkWorkId", "skipFlag", "workflowOrder", "standardStartTime", "standardEndTime", "standardStartDay"};
        String[] expectedValue2 = {"0", "1", "2", "3", "true", "4", "2015-11-11T09:47:06+09:00", "2015-11-12T09:47:06+09:00", "0"};

        Integer level1 = 0;
        Integer level2;
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

        ConWorkflowWorkInfoEntity workflowWork1 = new ConWorkflowWorkInfoEntity(1L, 1L, 1L, true, 1);
        workflowWork1.setStandardStartTime(new GregorianCalendar(2015, 0, 1, 0, 0, 0).getTime());
        workflowWork1.setStandardEndTime(new GregorianCalendar(2015, 0, 2, 0, 0, 0).getTime());
        ConWorkflowWorkInfoEntity workflowWork2 = new ConWorkflowWorkInfoEntity(2L, 2L, 2L, true, 2);
        workflowWork2.setStandardStartTime(new GregorianCalendar(2015, 1, 1, 0, 0, 0).getTime());
        workflowWork2.setStandardEndTime(new GregorianCalendar(2015, 1, 2, 0, 0, 0).getTime());

        assertThat(workflowWork1.getAssociationId(), is(1l));
        assertThat(workflowWork1.getFkWorkflowId(), is(1l));
        assertThat(workflowWork1.getFkWorkId(), is(1l));
        assertThat(workflowWork1.getSkipFlag(), is(true));
        assertThat(workflowWork1.getWorkflowOrder(), is(1));
        assertThat(workflowWork1.getStandardStartTime(), is(new GregorianCalendar(2015, 0, 1, 0, 0, 0).getTime()));
        assertThat(workflowWork1.getStandardEndTime(), is(new GregorianCalendar(2015, 0, 2, 0, 0, 0).getTime()));
        assertThat(workflowWork2.getAssociationId(), is(2l));
        assertThat(workflowWork2.getFkWorkflowId(), is(2l));
        assertThat(workflowWork2.getFkWorkId(), is(2l));
        assertThat(workflowWork2.getSkipFlag(), is(true));
        assertThat(workflowWork2.getWorkflowOrder(), is(2));
        assertThat(workflowWork2.getStandardStartTime(), is(new GregorianCalendar(2015, 1, 1, 0, 0, 0).getTime()));
        assertThat(workflowWork2.getStandardEndTime(), is(new GregorianCalendar(2015, 1, 2, 0, 0, 0).getTime()));

        workflowWork2.associationIdProperty().bind(workflowWork1.associationIdProperty());
        workflowWork2.fkWorkflowIdProperty().bind(workflowWork1.fkWorkflowIdProperty());
        workflowWork2.fkWorkIdProperty().bind(workflowWork1.fkWorkIdProperty());
        workflowWork2.skipFlagProperty().bind(workflowWork1.skipFlagProperty());
        workflowWork2.workflowOrderProperty().bind(workflowWork1.workflowOrderProperty());
        workflowWork2.standardStartTimeProperty().bind(workflowWork1.standardStartTimeProperty());
        workflowWork2.standardEndTimeProperty().bind(workflowWork1.standardEndTimeProperty());
        workflowWork1.setAssociationId(3L);
        workflowWork1.setFkWorkflowId(3L);
        workflowWork1.setFkWorkId(3L);
        workflowWork1.setSkipFlag(false);
        workflowWork1.setWorkflowOrder(3);
        workflowWork1.setStandardStartTime(new GregorianCalendar(2015, 2, 1, 0, 0, 0).getTime());
        workflowWork1.setStandardEndTime(new GregorianCalendar(2015, 2, 2, 0, 0, 0).getTime());

        assertThat(workflowWork1.getAssociationId(), is(3l));
        assertThat(workflowWork1.getFkWorkflowId(), is(3l));
        assertThat(workflowWork1.getFkWorkId(), is(3l));
        assertThat(workflowWork1.getSkipFlag(), is(false));
        assertThat(workflowWork1.getWorkflowOrder(), is(3));
        assertThat(workflowWork1.getStandardStartTime(), is(new GregorianCalendar(2015, 2, 1, 0, 0, 0).getTime()));
        assertThat(workflowWork1.getStandardEndTime(), is(new GregorianCalendar(2015, 2, 2, 0, 0, 0).getTime()));
        assertThat(workflowWork2.getAssociationId(), is(3l));
        assertThat(workflowWork2.getFkWorkflowId(), is(3l));
        assertThat(workflowWork2.getFkWorkId(), is(3l));
        assertThat(workflowWork2.getSkipFlag(), is(false));
        assertThat(workflowWork2.getWorkflowOrder(), is(3));
        assertThat(workflowWork2.getStandardStartTime(), is(new GregorianCalendar(2015, 2, 1, 0, 0, 0).getTime()));
        assertThat(workflowWork2.getStandardEndTime(), is(new GregorianCalendar(2015, 2, 2, 0, 0, 0).getTime()));
    }
}
