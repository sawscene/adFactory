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
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.SchedulePolicyEnum;
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
public class WorkflowInfoEntityTest {

    public WorkflowInfoEntityTest() {
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

        WorkflowInfoEntity entity = new WorkflowInfoEntity(1L, 2L, "workflowName", "revision", "diaglam", 3L, new GregorianCalendar(2015, 10, 30, 14, 47, 6).getTime(), "path");
        entity.setSchedulePolicy(SchedulePolicyEnum.PrioritySerial);
        Document xml = EntityToXml.getXml(entity);

        String[] expectedNode1 = {"workflow"};
        String[] expectedNode2 = {"workflowId", "parentId", "workflowName", "workflowRev", "workflowRevision", "workflowDiaglam", "fkUpdatePersonId", "updateDatetime", "ledgerPath", "schedulePolicy"};
        String[] expectedValue2 = {"1", "2", "workflowName", "1", "revision", "diaglam", "3", "2015-11-30T14:47:06+09:00", "path", "PrioritySerial"};

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

        WorkflowInfoEntity work1 = new WorkflowInfoEntity(1L, 0L, "workflowName1", "1", "1", 1L, new GregorianCalendar(2015, 10, 1, 0, 0, 0).getTime(), "1");
        WorkflowInfoEntity work2 = new WorkflowInfoEntity(2L, 0L, "workflowName2", "2", "2", 2L, new GregorianCalendar(2015, 10, 1, 1, 1, 1).getTime(), "2");
        assertThat(work1.getWorkflowId(), is(1l));
        assertThat(work1.getWorkflowName(), is("workflowName1"));
        assertThat(work1.getWorkflowRevision(), is("1"));
        assertThat(work1.getWorkflowDiaglam(), is("1"));
        assertThat(work1.getFkUpdatePersonId(), is(1L));
        assertThat(work1.getUpdateDatetime(), is(new GregorianCalendar(2015, 10, 1, 0, 0, 0).getTime()));
        assertThat(work1.getLedgerPath(), is("1"));
        assertThat(work2.getWorkflowId(), is(2l));
        assertThat(work2.getWorkflowName(), is("workflowName2"));
        assertThat(work2.getWorkflowRevision(), is("2"));
        assertThat(work2.getWorkflowDiaglam(), is("2"));
        assertThat(work2.getFkUpdatePersonId(), is(2L));
        assertThat(work2.getUpdateDatetime(), is(new GregorianCalendar(2015, 10, 1, 1, 1, 1).getTime()));
        assertThat(work2.getLedgerPath(), is("2"));

        work2.workflowIdProperty().bind(work1.workflowIdProperty());
        work2.workflowNameProperty().bind(work1.workflowNameProperty());
        work2.workflowRevisionProperty().bind(work1.workflowRevisionProperty());
        work2.workflowDiaglamProperty().bind(work1.workflowDiaglamProperty());
        work2.fkUpdatePersonIdProperty().bind(work1.fkUpdatePersonIdProperty());
        work2.updateDatetimeProperty().bind(work1.updateDatetimeProperty());
        work2.ledgerPathProperty().bind(work1.ledgerPathProperty());

        work1.setWorkflowId(3L);
        work1.setWorkflowName("workflowName3");
        work1.setWorkflowRevision("3");
        work1.setWorkflowDiaglam("3");
        work1.setFkUpdatePersonId(3L);
        work1.setUpdateDatetime(new GregorianCalendar(2015, 10, 1, 3, 3, 3).getTime());
        work1.setLedgerPath("3");
        assertThat(work1.getWorkflowId(), is(3l));
        assertThat(work1.getWorkflowName(), is("workflowName3"));
        assertThat(work1.getWorkflowRevision(), is("3"));
        assertThat(work1.getWorkflowDiaglam(), is("3"));
        assertThat(work1.getFkUpdatePersonId(), is(3L));
        assertThat(work1.getUpdateDatetime(), is(new GregorianCalendar(2015, 10, 1, 3, 3, 3).getTime()));
        assertThat(work1.getLedgerPath(), is("3"));
        assertThat(work2.getWorkflowId(), is(3l));
        assertThat(work2.getWorkflowName(), is("workflowName3"));
        assertThat(work2.getWorkflowRevision(), is("3"));
        assertThat(work2.getWorkflowDiaglam(), is("3"));
        assertThat(work2.getFkUpdatePersonId(), is(3L));
        assertThat(work2.getUpdateDatetime(), is(new GregorianCalendar(2015, 10, 1, 3, 3, 3).getTime()));
        assertThat(work2.getLedgerPath(), is("3"));
    }
}
