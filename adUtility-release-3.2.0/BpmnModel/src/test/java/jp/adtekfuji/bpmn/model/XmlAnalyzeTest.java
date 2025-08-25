/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import static org.hamcrest.core.Is.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.hasItems;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author ke.yokoi
 */
public class XmlAnalyzeTest {

    public XmlAnalyzeTest() {
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
    public void testXPath1() throws Exception {
        System.out.println("testXPath1");

        //XPath expression.
        Document xml = getDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//*/sequenceFlow[@sourceRef='task1_id']");
        NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);

        //check.
        List<String> targetRefs = Arrays.asList("task2_id");
        assertThat(nodes.getLength(), is(1));
        for (int nodeLoop = 0; nodeLoop < nodes.getLength(); nodeLoop++) {
            for (int attLoop = 0; attLoop < nodes.item(nodeLoop).getAttributes().getLength(); attLoop++) {
                Node node = nodes.item(nodeLoop).getAttributes().item(attLoop);
                if (node.getNodeName() == "targetRef") {
                    System.out.println(node.getNodeName() + ", " + node.getNodeValue());
                    assertThat(targetRefs.contains(node.getNodeValue()), is(true));
                }
            }
        }
    }

    @Test
    public void testXPath2() throws Exception {
        System.out.println("testXPath2");

        //XPath expression.
        Document xml = getDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//*/sequenceFlow[@sourceRef='parallel1_id']");
        NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);

        //check.
        List<String> targetRefs = Arrays.asList("task3_id", "task4_id", "task5_id", "task6_id", "task7_id", "parallel2_id");
        assertThat(nodes.getLength(), is(6));
        for (int nodeLoop = 0; nodeLoop < nodes.getLength(); nodeLoop++) {
            for (int attLoop = 0; attLoop < nodes.item(nodeLoop).getAttributes().getLength(); attLoop++) {
                Node node = nodes.item(nodeLoop).getAttributes().item(attLoop);
                if (node.getNodeName() == "targetRef") {
                    System.out.println(node.getNodeName() + ", " + node.getNodeValue());
                    assertThat(targetRefs.contains(node.getNodeValue()), is(true));
                }
            }
        }
    }

    @Test
    public void testXPath3() throws Exception {
        System.out.println("testXPath3");

        //XPath expression.
        Document xml = getDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//*/*[@id='task3_id']");
        NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
        //check.
        assertThat(nodes.getLength(), is(1));
        Node node = nodes.item(0);
        assertThat(node.getNodeName(), is("task"));
        assertThat(node.getAttributes().item(0).getNodeName(), is("id"));
        assertThat(node.getAttributes().item(0).getNodeValue(), is("task3_id"));
        assertThat(node.getAttributes().item(1).getNodeName(), is("name"));
        assertThat(node.getAttributes().item(1).getNodeValue(), is("task3"));
    }

    @Test
    public void testXPath4() throws Exception {
        System.out.println("testXPath4");

        //XPath expression.
        Document xml = getDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//*/*[@id='parallel2_id']");
        NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
        //check.
        assertThat(nodes.getLength(), is(1));
        Node node = nodes.item(0);
        assertThat(node.getNodeName(), is("parallelGateway"));
        assertThat(node.getAttributes().item(0).getNodeName(), is("id"));
        assertThat(node.getAttributes().item(0).getNodeValue(), is("parallel2_id"));
        assertThat(node.getAttributes().item(1).getNodeName(), is("name"));
        assertThat(node.getAttributes().item(1).getNodeValue(), is("parallel2"));
        assertThat(node.getAttributes().item(2).getNodeName(), is("pair"));
        assertThat(node.getAttributes().item(2).getNodeValue(), is("parallel1_id"));
    }

    private Document getDocument() throws Exception {
        //load xml file at resource.
        byte[] fileContentBytes = Files.readAllBytes(Paths.get(getClass().getResource("/xmlTest.xml").toURI()));
        //make xml document.
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
        ByteArrayInputStream instream = new ByteArrayInputStream(fileContentBytes);
        return docbuilder.parse(instream);
    }

    @Test
    public void getStartNode() throws Exception {
        System.out.println("getStartNode");

        byte[] fileContentBytes = Files.readAllBytes(Paths.get(getClass().getResource("/xmlTest.xml").toURI()));
        String xml = new String(fileContentBytes);

        BpmnModelUtility utility = new BpmnModelUtility(xml);
        BpmnStartEvent node = utility.getStartNode();
        //check
        assertThat(node, is(new BpmnStartEvent("start_id", "start")));
    }

    @Test
    public void getNextNode() throws Exception {
        System.out.println("getNextNode");

        byte[] fileContentBytes = Files.readAllBytes(Paths.get(getClass().getResource("/xmlTest.xml").toURI()));
        String xml = new String(fileContentBytes);

        BpmnModelUtility utility = new BpmnModelUtility(xml);
        List<BpmnNode> nodes = utility.getNextNode("parallel1_id");
        //check
        BpmnTask task3 = new BpmnTask("task3_id", "task3");
        BpmnTask task4 = new BpmnTask("task4_id", "task4");
        BpmnTask task5 = new BpmnTask("task5_id", "task5");
        BpmnTask task6 = new BpmnTask("task6_id", "task6");
        BpmnTask task7 = new BpmnTask("task7_id", "task7");
        BpmnParallelGateway parallel1 = new BpmnParallelGateway("parallel1_id", "parallel1_id", null);
        BpmnParallelGateway parallel2 = new BpmnParallelGateway("parallel2_id", "parallel2_id", parallel1);
        List<BpmnNode> actuals = Arrays.asList(task3, task4, task5, task6, task7, parallel2);
        assertThat(nodes, is(hasItems(actuals.toArray(new BpmnNode[actuals.size()]))));
    }

    @Test
    public void getForwardNode() throws Exception {
        System.out.println("getForwardNode");

        byte[] fileContentBytes = Files.readAllBytes(Paths.get(getClass().getResource("/xmlTest.xml").toURI()));
        String xml = new String(fileContentBytes);

        BpmnModelUtility utility = new BpmnModelUtility(xml);
        List<BpmnNode> nodes = utility.getForwardNode("parallel2_id");
        //check
        BpmnTask task3 = new BpmnTask("task3_id", "task3");
        BpmnTask task4 = new BpmnTask("task4_id", "task4");
        BpmnTask task5 = new BpmnTask("task5_id", "task5");
        BpmnTask task6 = new BpmnTask("task6_id", "task6");
        BpmnTask task7 = new BpmnTask("task7_id", "task7");
        BpmnParallelGateway parallel1 = new BpmnParallelGateway("parallel1_id", "parallel1_id", null);
        List<BpmnNode> actuals = Arrays.asList(task3, task4, task5, task6, task7, parallel1);
        assertThat(nodes, is(hasItems(actuals.toArray(new BpmnNode[actuals.size()]))));

    }
}
