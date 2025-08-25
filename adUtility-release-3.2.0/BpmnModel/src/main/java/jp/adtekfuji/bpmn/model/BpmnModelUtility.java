/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ke.yokoi
 */
public class BpmnModelUtility {

    private static final String SEARCH_START_NODE = "//*/startEvent";
    private static final String SEARCH_TARGET_REF = "//*/sequenceFlow[@targetRef='%s']";
    private static final String SEARCH_SOURCE_REF = "//*/sequenceFlow[@sourceRef='%s']";
    private static final String TARGET_REF = "targetRef";
    private static final String SOURCE_REF = "sourceRef";
    private static final String SEARCH_ID = "//*/*[@id='%s']";
    private static final String TASK_NODE = "task";
    private static final String NODE_ID = "id";
    private static final String NODE_NAME = "name";
    private static final String NODE_PAIR = "pair";
    private static final String PARALLEL_GATEWAY_NODE = "parallelGateway";
    private final Document xml;
    private final XPath xpath;

    private BpmnModelUtility() {
        xml = null;
        xpath = null;
    }

    /**
     * BpmnModelUtility
     *
     * @param xmlDiaglam xmlデータ
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public BpmnModelUtility(String xmlDiaglam) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
        ByteArrayInputStream instream = new ByteArrayInputStream(xmlDiaglam.getBytes());
        xml = docbuilder.parse(instream);
        xpath = XPathFactory.newInstance().newXPath();
    }

    /**
     * 開始ノードを取得。
     *
     * @return
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public BpmnStartEvent getStartNode() throws XPathExpressionException {
        XPathExpression searchRefPash = xpath.compile(SEARCH_START_NODE);
        NodeList nodes = (NodeList) searchRefPash.evaluate(xml, XPathConstants.NODESET);
        if (nodes.getLength() != 1) {
            return null;
        }
        return getStartTask(nodes.item(0));
    }

    /**
     * XMLデータから、指定したノードを参照元としたノードのコレクションを取得する。
     *
     * @param nodeId 参照元ノードID
     * @return 次ノードおよび次ゲートウェイの配列
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public List<BpmnNode> getNextNode(String nodeId) throws XPathExpressionException {
        return getSearchNode(SEARCH_SOURCE_REF, TARGET_REF, nodeId);
    }

    /**
     * XMLデータから、指定したノードを参照先としたノードのコレクションを取得する。
     *
     * @param nodeId 参照先ノードID
     * @return 次ノードおよび次ゲートウェイの配列
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public List<BpmnNode> getForwardNode(String nodeId) throws XPathExpressionException {
        return getSearchNode(SEARCH_TARGET_REF, SOURCE_REF, nodeId);
    }

    public List<BpmnNode> getSearchNode(String xpathRef, String name, String nodeId) throws XPathExpressionException {
        List<BpmnNode> nodeIds = new LinkedList<>();
        if (xml == null || xpath == null) {
            throw new RuntimeException("Ready to use the BpmnModelUtility is not equipped");
        }
        //参照先を検索.
        XPathExpression searchRefPath = xpath.compile(String.format(xpathRef, nodeId));
        NodeList nodes = (NodeList) searchRefPath.evaluate(xml, XPathConstants.NODESET);
        for (int nodeLoop = 0; nodeLoop < nodes.getLength(); nodeLoop++) {
            for (int attLoop = 0; attLoop < nodes.item(nodeLoop).getAttributes().getLength(); attLoop++) {
                Node node = nodes.item(nodeLoop).getAttributes().item(attLoop);
                if (node.getNodeName().equals(name)) {
                    //参照先の種別を判断.
                    XPathExpression searchTaskPash = xpath.compile(String.format(SEARCH_ID, node.getNodeValue()));
                    NodeList refs = (NodeList) searchTaskPash.evaluate(xml, XPathConstants.NODESET);
                    if (refs.getLength() != 1) {
                        //一つ以外はあり得ないぞ.
                        continue;
                    }
                    Node ref = refs.item(0);
                    switch (ref.getNodeName()) {
                        case TASK_NODE:
                            nodeIds.add(getBpmnTask(ref));
                            break;
                        case PARALLEL_GATEWAY_NODE:
                            nodeIds.add(getBpmnParallelGateway(ref));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return nodeIds;
    }

    private BpmnStartEvent getStartTask(Node node) {
        String id = "";
        String name = "";
        for (int attLoop2 = 0; attLoop2 < node.getAttributes().getLength(); attLoop2++) {
            Node att = node.getAttributes().item(attLoop2);
            if (att.getNodeName().equals(NODE_ID)) {
                id = att.getNodeValue();
            }
            if (att.getNodeName().equals(NODE_NAME)) {
                name = att.getNodeValue();
            }
        }
        return new BpmnStartEvent(id, name);
    }

    private BpmnTask getBpmnTask(Node node) {
        String id = "";
        String name = "";
        for (int attLoop2 = 0; attLoop2 < node.getAttributes().getLength(); attLoop2++) {
            Node att = node.getAttributes().item(attLoop2);
            if (att.getNodeName().equals(NODE_ID)) {
                id = att.getNodeValue();
            }
            if (att.getNodeName().equals(NODE_NAME)) {
                name = att.getNodeValue();
            }
        }
        return new BpmnTask(id, name);
    }

    private BpmnParallelGateway getBpmnParallelGateway(Node node) throws XPathExpressionException {
        String id = "";
        String name = "";
        BpmnParallelGateway pairGateway = null;
        for (int attLoop2 = 0; attLoop2 < node.getAttributes().getLength(); attLoop2++) {
            Node att = node.getAttributes().item(attLoop2);
            if (att.getNodeName().equals(NODE_ID)) {
                id = att.getNodeValue();
            }
            if (att.getNodeName().equals(NODE_NAME)) {
                name = att.getNodeValue();
            }
            if (att.getNodeName().equals(NODE_PAIR)) {
                pairGateway = getPairBpmnParallelGateway(att.getNodeValue());
            }
        }
        BpmnParallelGateway gateway = new BpmnParallelGateway(id, name, pairGateway);
        return gateway;
    }

    private BpmnParallelGateway getPairBpmnParallelGateway(String nodeName) throws XPathExpressionException {
        XPathExpression searchTaskPash = xpath.compile(String.format(SEARCH_ID, nodeName));
        NodeList nodes = (NodeList) searchTaskPash.evaluate(xml, XPathConstants.NODESET);
        if (nodes.getLength() != 1) {
            //一つ以外はあり得ないぞ.
            return null;
        }
        Node node = nodes.item(0);
        String id = "";
        String name = "";
        for (int attLoop2 = 0; attLoop2 < node.getAttributes().getLength(); attLoop2++) {
            Node att = node.getAttributes().item(attLoop2);
            if (att.getNodeName().equals(NODE_ID)) {
                id = att.getNodeValue();
            }
            if (att.getNodeName().equals(NODE_NAME)) {
                name = att.getNodeValue();
            }
        }
        BpmnParallelGateway pairGateway = new BpmnParallelGateway(id, name, null);
        return pairGateway;
    }

    /**
     * gateway は開始ゲートウェイ
     * 
     * @param gateway
     * @param pair
     * @return 
     */
    public boolean isStartGateway(BpmnParallelGateway gateway, BpmnParallelGateway pair) {
        String str1 = gateway.getId().replaceAll("[^0-9]+", ""); 
        String str2 = pair.getId().replaceAll("[^0-9]+", ""); 
        if (Integer.parseInt(str1) > Integer.parseInt(str2)) {
            return false;
        }
        return true;
    }
}
