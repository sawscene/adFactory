/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import jp.adtekfuji.bpmn.model.entity.BpmnEdge;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ke.yokoi
 */
public class JungTest {

    public JungTest() {
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
    public void testJung() throws Exception {
        System.out.println("testJung");

        UndirectedSparseMultigraph<BpmnNode, BpmnEdge> graph = new UndirectedSparseMultigraph<>();
        BpmnStartEvent start = new BpmnStartEvent("start", "start");
        BpmnTask task1 = new BpmnTask("task1", "task1");
        BpmnTask task2 = new BpmnTask("task2", "task2");
        BpmnTask task3 = new BpmnTask("task3", "task3");
        BpmnEndEvent end = new BpmnEndEvent("end", "end");
        BpmnSequenceFlow flow1 = new BpmnSequenceFlow("flow1", "flow1", start, task1);
        BpmnSequenceFlow flow2 = new BpmnSequenceFlow("flow2", "flow2", task1, task2);
        BpmnSequenceFlow flow3 = new BpmnSequenceFlow("flow3", "flow3", task2, task3);
        BpmnSequenceFlow flow4 = new BpmnSequenceFlow("flow4", "flow4", task3, end);

        graph.addVertex(start);
        graph.addVertex(task1);
        graph.addVertex(task2);
        graph.addVertex(task3);
        graph.addVertex(end);
        graph.addEdge(flow1, flow1.getSourceNode(), flow1.getTargetNode());
        graph.addEdge(flow2, flow2.getSourceNode(), flow2.getTargetNode());
        graph.addEdge(flow3, flow3.getSourceNode(), flow3.getTargetNode());
        graph.addEdge(flow4, flow4.getSourceNode(), flow4.getTargetNode());

        System.out.println("Graph G = " + graph.toString());

    }

}
