/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.JAXB;
import javax.xml.transform.dom.DOMSource;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author ke.yokoi
 */
public class BpmnXmlSerializeTest {

    public BpmnXmlSerializeTest() {
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
    public void testXML() throws Exception {
        System.out.println("testXML");

        // データの準備.
        List<BpmnStartEvent> startEventCollection = new ArrayList<>();
        List<BpmnEndEvent> endEventCollection = new ArrayList<>();
        List<BpmnTerminateEndEvent> terminateEndEventCollection = new ArrayList<>();
        List<BpmnTask> taskCollection = new ArrayList<>();
        List<BpmnParallelGateway> parallelGatewayCollection = new ArrayList<>();
        List<BpmnExclusiveGateway> exclusiveGatewayCollection = new ArrayList<>();
        List<BpmnInclusiveGateway> inclusiveGatewayCollection = new ArrayList<>();
        List<BpmnSequenceFlow> sequenceFlowCollection = new ArrayList<>();

        BpmnStartEvent start = new BpmnStartEvent("start_id", "start");
        BpmnEndEvent end = new BpmnEndEvent("end_id", "end");
        BpmnTerminateEndEvent terminateEndEvent1 = new BpmnTerminateEndEvent("terminateEndEvent1_id", "terminateEndEvent1");
        BpmnTerminateEndEvent terminateEndEvent2 = new BpmnTerminateEndEvent("terminateEndEvent2_id", "terminateEndEvent2");
        BpmnTask task1 = new BpmnTask("task1_id", "task1");
        BpmnTask task2 = new BpmnTask("task2_id", "task2");
        BpmnTask task3 = new BpmnTask("task3_id", "task3");
        BpmnParallelGateway parralelGateway1 = new BpmnParallelGateway("parralelGateway1_id", "parralelGateway1", null);
        BpmnParallelGateway parralelGateway2 = new BpmnParallelGateway("parralelGateway2_id", "parralelGateway2", parralelGateway1);
        BpmnExclusiveGateway exclusiveGateway1 = new BpmnExclusiveGateway("exclusiveGateway1_id", "exclusiveGateway1", null);
        BpmnExclusiveGateway exclusiveGateway2 = new BpmnExclusiveGateway("exclusiveGateway2_id", "exclusiveGateway2", exclusiveGateway1);
        BpmnInclusiveGateway inclusiveGateway1 = new BpmnInclusiveGateway("inclusiveGateway1_id", "inclusiveGateway1", null);
        BpmnInclusiveGateway inclusiveGateway2 = new BpmnInclusiveGateway("inclusiveGateway2_id", "inclusiveGateway2", inclusiveGateway1);
        BpmnSequenceFlow sequenceFlow1 = new BpmnSequenceFlow("sequenceFlow1_id", "sequenceFlow1", start, task1);
        BpmnSequenceFlow sequenceFlow2 = new BpmnSequenceFlow("sequenceFlow2_id", "sequenceFlow2", task1, task2);
        BpmnSequenceFlow sequenceFlow3 = new BpmnSequenceFlow("sequenceFlow3_id", "sequenceFlow3", task2, task3);
        BpmnSequenceFlow sequenceFlow4 = new BpmnSequenceFlow("sequenceFlow4_id", "sequenceFlow4", task3, end);

        startEventCollection.add(start);
        endEventCollection.add(end);
        terminateEndEventCollection.addAll(Arrays.asList(terminateEndEvent1, terminateEndEvent2));
        taskCollection.addAll(Arrays.asList(task1, task2, task3));
        parallelGatewayCollection.addAll(Arrays.asList(parralelGateway1, parralelGateway2));
        exclusiveGatewayCollection.addAll(Arrays.asList(exclusiveGateway1, exclusiveGateway2));
        inclusiveGatewayCollection.addAll(Arrays.asList(inclusiveGateway1, inclusiveGateway2));
        sequenceFlowCollection.addAll(Arrays.asList(sequenceFlow1, sequenceFlow2, sequenceFlow3, sequenceFlow4));

        BpmnProcess process = new BpmnProcess();
        process.setId("process-id");
        process.setStartEventCollection(startEventCollection);
        process.setEndEventCollection(endEventCollection);
        process.setTerminateEndEventCollection(terminateEndEventCollection);
        process.setTaskCollection(taskCollection);
        process.setParallelGatewayCollection(parallelGatewayCollection);
        process.setExclusiveGatewayCollection(exclusiveGatewayCollection);
        process.setInclusiveGatewayCollection(inclusiveGatewayCollection);
        process.setSequenceFlowCollection(sequenceFlowCollection);
        BpmnDocument definitions = new BpmnDocument();
        definitions.setName("definitions-name");
        definitions.setProcess(process);

        // オブジェクトをXMLに.
        Document xml = EntityToXml.getXml(definitions);

        // 各ノードの名前や属性の確認.
        String[] expectedNode1 = {"definitions"};
        String[] expectedAttributeName1 = {"name", "targetNamespace"};
        String[] expectedAttributeValue1 = {"definitions-name", "http://www.adtek-fuji.co.jp/adfactory"};
        String[] expectedNode2 = {"process"};
        String[] expectedAttributeName2 = {"id", "isExecutable"};
        String[] expectedAttributeValue2 = {"process-id", "true"};
        String[] expectedNode3 = {
            "startEvent", "endEvent", "terminateEndEvent", "terminateEndEvent",
            "task", "task", "task", "parallelGateway", "parallelGateway", "exclusiveGateway", "exclusiveGateway", "inclusiveGatewy", "inclusiveGatewy",
            "sequenceFlow", "sequenceFlow", "sequenceFlow", "sequenceFlow"};
        String[] expectedAttributeName3 = {
            "id", "name", "id", "name", //start,end
            "id", "name", "id", "name", //terminateEndEvent1,terminateEndEvent2
            "id", "name", "id", "name", "id", "name", //task1,task2,task3
            "id", "name", "pair", "id", "name", "pair",//parralelGateway1,parralelGateway2
            "id", "name", "pair", "id", "name", "pair", //exclusiveGateway1,exclusiveGateway2
            "id", "name", "pair", "id", "name", "pair", //inclusiveGateway1,inclusiveGateway2
            "id", "name", "sourceRef", "targetRef", "id", "name", "sourceRef", "targetRef", "id", "name", "sourceRef", "targetRef", "id", "name", "sourceRef", "targetRef"};
        String[] expectedAttributeValue3 = {
            "start_id", "start", "end_id", "end", //start,end
            "terminateEndEvent1_id", "terminateEndEvent1", "terminateEndEvent2_id", "terminateEndEvent2", //terminateEndEvent1,terminateEndEvent2
            "task1_id", "task1", "task2_id", "task2", "task3_id", "task3", //task1,task2,task3
            "parralelGateway1_id", "parralelGateway1", "parralelGateway2_id", "parralelGateway2_id", "parralelGateway2", "parralelGateway1_id", //parralelGateway1,parralelGateway2
            "exclusiveGateway1_id", "exclusiveGateway1", "exclusiveGateway2_id", "exclusiveGateway2_id", "exclusiveGateway2", "exclusiveGateway1_id",//exclusiveGateway1,exclusiveGateway2
            "inclusiveGateway1_id", "inclusiveGateway1", "inclusiveGateway2_id", "inclusiveGateway2_id", "inclusiveGateway2", "inclusiveGateway1_id", //inclusiveGateway1,inclusiveGateway2
            "sequenceFlow1_id", "sequenceFlow1", "start_id", "task1_id", "sequenceFlow2_id", "sequenceFlow2", "task1_id", "task2_id",
            "sequenceFlow3_id", "sequenceFlow3", "task2_id", "task3_id", "sequenceFlow4_id", "sequenceFlow4", "task3_id", "end_id"};

        int level1 = 0;
        int level2 = 0;
        int level3 = 0;
        int cnt = 0;
        for (Node node1 = xml.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
            if (node1 instanceof Element) {
                System.out.println(node1.getNodeName() + ":" + node1.getTextContent());
                assertThat(node1.getNodeName(), is(expectedNode1[level1]));
                for (int loop = 0; loop < node1.getAttributes().getLength(); loop++) {
                    Node attribute = node1.getAttributes().item(loop);
                    System.out.println("\t" + attribute);
                    assertThat(attribute.getNodeName(), is(expectedAttributeName1[cnt]));
                    assertThat(attribute.getNodeValue(), is(expectedAttributeValue1[cnt++]));
                }
                level1++;
            }
            cnt = 0;
            level2 = 0;
            for (Node node2 = node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2 instanceof Element) {
                    System.out.println(node2.getNodeName() + ":" + node2.getTextContent());
                    assertThat(node2.getNodeName(), is(expectedNode2[level2]));
                    for (int loop = 0; loop < node2.getAttributes().getLength(); loop++) {
                        Node attribute = node2.getAttributes().item(loop);
                        System.out.println("\t" + attribute);
                        assertThat(attribute.getNodeName(), is(expectedAttributeName2[cnt]));
                        assertThat(attribute.getNodeValue(), is(expectedAttributeValue2[cnt++]));
                    }
                    level2++;
                }
                level3 = 0;
                cnt = 0;
                for (Node node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                    if (node3 instanceof Element) {
                        System.out.println(node3.getNodeName() + ":" + node3.getTextContent());
                        assertThat(node3.getNodeName(), is(expectedNode3[level3]));
                        for (int loop = 0; loop < node3.getAttributes().getLength(); loop++) {
                            Node attribute = node3.getAttributes().item(loop);
                            System.out.println("\t" + attribute);
                            assertThat(attribute.getNodeName(), is(expectedAttributeName3[cnt]));
                            assertThat(attribute.getNodeValue(), is(expectedAttributeValue3[cnt++]));
                        }
                        level3++;
                    }
                }
            }
        }

        // XMLをオブジェクトに.
        BpmnDocument definitions2 = JAXB.unmarshal(new DOMSource(xml), BpmnDocument.class);
        assertThat(definitions2, is(definitions));
        assertThat(definitions2.getProcess(), is(process));
        assertThat(definitions2.getProcess().getStartEventCollection(), is(startEventCollection));
        assertThat(definitions2.getProcess().getEndEventCollection(), is(endEventCollection));
        assertThat(definitions2.getProcess().getTerminateEndEventCollection(), is(terminateEndEventCollection));
        assertThat(definitions2.getProcess().getTaskCollection(), is(taskCollection));
        assertThat(definitions2.getProcess().getParallelGatewayCollection(), is(parallelGatewayCollection));
        assertThat(definitions2.getProcess().getExclusiveGatewayCollection(), is(exclusiveGatewayCollection));
        assertThat(definitions2.getProcess().getInclusiveGatewayCollection(), is(inclusiveGatewayCollection));
        assertThat(definitions2.getProcess().getSequenceFlowCollection(), is(sequenceFlowCollection));
    }

}
