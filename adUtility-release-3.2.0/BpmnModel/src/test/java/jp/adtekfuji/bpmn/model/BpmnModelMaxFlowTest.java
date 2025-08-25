/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import java.util.List;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

/**
 *
 * @author ke.yokoi
 */
public class BpmnModelMaxFlowTest {

    BpmnModel workflow = BpmnModeler.getModeler();
    BpmnStartEvent start = new BpmnStartEvent("start_id", "start");
    BpmnEndEvent end = new BpmnEndEvent("end_id", "end");
    BpmnTask task1 = new BpmnTask("task1_id", "task1");
    BpmnTask task2 = new BpmnTask("task2_id", "task2");
    BpmnTask task3 = new BpmnTask("task3_id", "task3");
    BpmnTask task4 = new BpmnTask("task4_id", "task4");
    BpmnTask task5 = new BpmnTask("task5_id", "task5");
    BpmnTask task6 = new BpmnTask("task6_id", "task6");
    BpmnTask task7 = new BpmnTask("task7_id", "task7");
    BpmnTask task8 = new BpmnTask("task8_id", "task8");
    BpmnTask task9 = new BpmnTask("task9_id", "task9");
    BpmnParallelGateway parallel1 = new BpmnParallelGateway("parallel1_id", "parallel1", null);
    BpmnParallelGateway parallel2 = new BpmnParallelGateway("parallel2_id", "parallel2", parallel1);

    public BpmnModelMaxFlowTest() {
        task1.setCost(new MyBpmnCost(5L, 30L));
        task2.setCost(new MyBpmnCost(10L, 30L));
        task3.setCost(new MyBpmnCost(15L, 5L));
        task4.setCost(new MyBpmnCost(5L, 6L));
        task5.setCost(new MyBpmnCost(5L, 6L));
        task6.setCost(new MyBpmnCost(3L, 10L));
        task7.setCost(new MyBpmnCost(3L, 10L));
        task8.setCost(new MyBpmnCost(3L, 10L));
        task9.setCost(new MyBpmnCost(10L, 30L));
        workflow = BpmnModeler.getModeler();
        workflow.createModel(start, end);
        workflow.addNextNode(start, task1);
        workflow.addNextNode(task1, task2);
        workflow.addNextNode(task2, parallel1, parallel2);
        workflow.addParallelNode(parallel1, task3);
        workflow.addParallelNode(parallel1, task4);
        workflow.addNextNode(task4, task5);
        workflow.addParallelNode(parallel1, task6);
        workflow.addNextNode(task6, task7);
        workflow.addNextNode(task7, task8);
        workflow.addNextNode(parallel2, task9);
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
    public void testShortestDistance() throws Exception {
        System.out.println("testShortestDistance");

        List<BpmnNode> nodes;

        nodes = BpmnAlgorithm.getShortestPath(workflow, start, end);
        assertThat(nodes, is(hasSize(9)));
        assertThat(nodes, is(hasItems(start, task1, task2, parallel1, task6, task7, task8, parallel2, task9)));

        nodes = BpmnAlgorithm.getShortestPath(workflow, task4, end);
        assertThat(nodes, is(hasSize(4)));
        assertThat(nodes, is(hasItems(task4, task5, parallel2, task9)));
    }

    @Test
    public void testLongestDistance() throws Exception {
        System.out.println("testLongestDistance");

        List<BpmnNode> nodes;

        nodes = BpmnAlgorithm.getLongestPath(workflow, start, end);
        assertThat(nodes, is(hasSize(7)));
        assertThat(nodes, is(hasItems(start, task1, task2, parallel1, task3, parallel2, task9)));

        nodes = BpmnAlgorithm.getLongestPath(workflow, task4, end);
        assertThat(nodes, is(hasSize(4)));
        assertThat(nodes, is(hasItems(task4, task5, parallel2, task9)));
    }

    @Test
    public void testMaxFlow() throws Exception {
        System.out.println("testMaxFlow");

        int maxFlow = BpmnAlgorithm.getMaxFlow(workflow, new MyBpmnCost(1L, 1L), start, end);
        assertThat(maxFlow, is(21));

    }

}
