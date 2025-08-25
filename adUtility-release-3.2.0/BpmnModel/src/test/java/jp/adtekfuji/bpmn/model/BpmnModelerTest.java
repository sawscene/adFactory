/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnExclusiveGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnInclusiveGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import jp.adtekfuji.bpmn.model.entity.BpmnTerminateEndEvent;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class BpmnModelerTest {

    public BpmnModelerTest() {
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

    @Test
    public void testInitialCreateAndXmlOutput() throws Exception {
        System.out.println("testInitialCreateAndXmlOutput");

        //initial.
        BpmnModel workflow = BpmnModeler.getModeler();
        BpmnStartEvent start = new BpmnStartEvent("start_id", "start");
        BpmnEndEvent end = new BpmnEndEvent("end_id", "end");
        workflow.createModel(start, end);

        //adding task.
        BpmnTask task1 = new BpmnTask("task1_id", "task1");
        BpmnTask task2 = new BpmnTask("task2_id", "task2");
        workflow.addNextNode(start, task1);
        workflow.addNextNode(task1, task2);
        //addint parallel gateway.
        BpmnParallelGateway parallel1 = new BpmnParallelGateway("parallel1_id", "parallel1", null);
        BpmnParallelGateway parallel2 = new BpmnParallelGateway("parallel2_id", "parallel2", parallel1);
        workflow.addNextNode(task2, parallel1, parallel2);
        BpmnTask task3 = new BpmnTask("task3_id", "task3");
        BpmnTask task4 = new BpmnTask("task4_id", "task4");
        BpmnTask task5 = new BpmnTask("task5_id", "task5");
        BpmnTask task6 = new BpmnTask("task6_id", "task6");
        BpmnTask task7 = new BpmnTask("task7_id", "task7");
        workflow.addParallelNode(parallel1, task3);
        workflow.addParallelNode(parallel1, task4);
        workflow.addParallelNode(parallel1, task5);
        workflow.addParallelNode(parallel1, task6);
        workflow.addParallelNode(parallel1, task7);

        //check.
        System.out.println(workflow);
        BpmnDocument bpmn = workflow.getBpmnDefinitions();
        System.out.println(bpmn.marshal());
        BpmnProcess process = bpmn.getProcess();
        List<BpmnStartEvent> startEventCollection = process.getStartEventCollection();
        List<BpmnEndEvent> endEventCollection = process.getEndEventCollection();
        List<BpmnTerminateEndEvent> terminateEndEventCollection = process.getTerminateEndEventCollection();
        List<BpmnTask> taskCollection = process.getTaskCollection();
        List<BpmnParallelGateway> parallelGatewayCollection = process.getParallelGatewayCollection();
        List<BpmnExclusiveGateway> exclusiveGatewayCollection = process.getExclusiveGatewayCollection();
        List<BpmnInclusiveGateway> inclusiveGatewayCollection = process.getInclusiveGatewayCollection();
        List<BpmnSequenceFlow> sequenceFlowCollection = process.getSequenceFlowCollection();
        assertThat(startEventCollection.size(), is(1));
        assertThat(endEventCollection.size(), is(1));
        assertThat(terminateEndEventCollection.size(), is(0));
        assertThat(taskCollection.size(), is(7));
        assertThat(parallelGatewayCollection.size(), is(2));
        assertThat(exclusiveGatewayCollection.size(), is(0));
        assertThat(inclusiveGatewayCollection.size(), is(0));
        assertThat(sequenceFlowCollection.size(), is(14));

        byte[] fileContentBytes = bpmn.marshal().getBytes();
        Files.write(Paths.get(getClass().getResource("/bpmnTest1.xml").toURI()), fileContentBytes);
    }

    @Test
    public void testXmlToObject() throws Exception {
        System.out.println("testXmlToObject");

        //load xml file at resource.
        byte[] fileContentBytes = Files.readAllBytes(Paths.get(getClass().getResource("/bpmnTest1.xml").toURI()));
        String xml = new String(fileContentBytes, StandardCharsets.UTF_8);

        //xml to object.
        BpmnDocument bpmn = BpmnDocument.unmarshal(xml);
        BpmnModel workflow = BpmnModeler.getModeler();
        workflow.createModel(bpmn);

        //check.
        System.out.println(workflow);
        BpmnDocument bpmn2 = workflow.getBpmnDefinitions();
        System.out.println(bpmn2.marshal());
        BpmnProcess process = bpmn2.getProcess();

        assertThat(process.getStartEventCollection().size(), is(1));
        assertThat(process.getEndEventCollection().size(), is(1));
        assertThat(process.getTerminateEndEventCollection().size(), is(0));
        assertThat(process.getTaskCollection().size(), is(7));
        assertThat(process.getParallelGatewayCollection().size(), is(2));
        assertThat(process.getExclusiveGatewayCollection().size(), is(0));
        assertThat(process.getInclusiveGatewayCollection().size(), is(0));
        assertThat(process.getSequenceFlowCollection().size(), is(14));
    }

}
