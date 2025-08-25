/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnEdge;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnExclusiveGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnGatewayNode;
import jp.adtekfuji.bpmn.model.entity.BpmnInclusiveGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnProcess;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import jp.adtekfuji.bpmn.model.entity.BpmnTerminateEndEvent;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * BpmnModeler
 *
 * @author ke.yokoi
 */
public class BpmnModeler implements BpmnModel {

    private BpmnDocument bpmn;
    private final DirectedSparseMultigraph<BpmnNode, BpmnEdge> graph;
    private static final int uuidLength = 8;

    private BpmnModeler() {
        graph = new DirectedSparseMultigraph<>();
    }

    /**
     * get workflow engine.
     *
     * @return
     */
    public static BpmnModel getModeler() {
        return new BpmnModeler();
    }

    /**
     * create initial workflow.
     *
     * @param start adding start node
     * @param end adding end node
     * @return true/false
     */
    @Override
    public boolean createModel(BpmnStartEvent start, BpmnEndEvent end) {
        this.bpmn = initBpmn();
        this.graph.addVertex(start);
        this.graph.addVertex(end);
        addFlow(start, end);
        return true;
    }

    /**
     * create copied workflow.
     *
     * @param bpmn
     * @return true/false
     */
    @Override
    public boolean createModel(BpmnDocument bpmn) {
        this.bpmn = bpmn;
        updateFlow(bpmn);
        return true;
    }

    /**
     * connecting node between front node and rear node.
     *
     * @param frontNode
     * @param node
     * @return true/false
     */
    @Override
    public boolean addNextNode(BpmnNode frontNode, BpmnNode node) {
        //check.
        if (false == graph.containsVertex(frontNode) || graph.containsVertex(node)) {
            return false;
        }
        List<BpmnEdge> edges = new LinkedList<>(graph.getOutEdges(frontNode));
        if (edges.size() != 1) {
            return false;
        }
        BpmnEdge edge = edges.get(0);
        BpmnNode rearNode = graph.getDest(edge);
        //remove edge.
        if (false == graph.removeEdge(edge)) {
            return false;
        }
        //adding node and connect edge.
        graph.addVertex(node);
        addFlow(frontNode, node);
        addFlow(node, rearNode);
        return true;
    }

    /**
     * connecting parallel gateway between front node and rear node.
     *
     * @param frontNode
     * @param gateway1
     * @param gateway2
     * @return true/false
     */
    @Override
    public boolean addNextNode(BpmnNode frontNode, BpmnParallelGateway gateway1, BpmnParallelGateway gateway2) {
        //check.
        if (false == graph.containsVertex(frontNode) || graph.containsVertex(gateway1) || graph.containsVertex(gateway2)) {
            return false;
        }
        List<BpmnEdge> edges = new LinkedList<>(graph.getOutEdges(frontNode));
        if (edges.size() != 1) {
            return false;
        }
        BpmnEdge edge = edges.get(0);
        BpmnNode rearNode = graph.getDest(edge);
        //remove edge.
        graph.removeEdge(edge);
        //adding node and connect edge.
        graph.addVertex(gateway1);
        graph.addVertex(gateway2);
        addFlow(frontNode, gateway1);
        addFlow(gateway1, gateway2);
        addFlow(gateway2, rearNode);
        return true;
    }

    /**
     * additional parallel process.
     *
     * @param gateway
     * @param node
     * @return true/false
     */
    @Override
    public boolean addParallelNode(BpmnParallelGateway gateway, BpmnNode node) {
        //check.
        if (false == graph.containsVertex(gateway) || graph.containsVertex(node)) {
            return false;
        }
        //remove edge.
        List<BpmnNode> nodes = new LinkedList(graph.getSuccessors(gateway));
        if (nodes.size() == 1 && nodes.get(0) == gateway.getPairedGateway()) {
            graph.removeEdge(graph.findEdge(gateway, gateway.getPairedGateway()));
        }
        //adding node and connect edge.
        graph.addVertex(node);
        addFlow(gateway, node);
        addFlow(node, gateway.getPairedGateway());
        return true;
    }

    /**
     * move node between front node and rear node.
     *
     * @param frontNode
     * @param node
     * @return true/false
     */
    @Override
    public boolean moveNextNode(BpmnNode frontNode, BpmnNode node) {
        //check.
        if (!graph.containsVertex(frontNode) || !graph.containsVertex(node)) {
            return false;
        }

        List<BpmnEdge> frontOutEdges = new LinkedList<>(graph.getOutEdges(frontNode));
        List<BpmnEdge> nodeOutEdges = new LinkedList<>(graph.getOutEdges(node));
        List<BpmnEdge> nodeInEdges = new LinkedList<>(graph.getInEdges(node));
        if ((frontOutEdges.size() != 1) || (nodeOutEdges.size() != 1) || (nodeInEdges.size() != 1)) {
            return false;
        }

        BpmnEdge nodeInEdge = nodeInEdges.get(0);
        BpmnEdge nodeOutEdge = nodeOutEdges.get(0);
        BpmnEdge frontOutEdge = frontOutEdges.get(0);

        //remove edge.
        graph.removeEdge(nodeInEdge);
        graph.removeEdge(nodeOutEdge);
        graph.removeEdge(frontOutEdge);

        //reconnect edge.
        addFlow(nodeInEdge.getSourceNode(), nodeOutEdge.getTargetNode());
        addFlow(nodeOutEdge.getSourceNode(), frontOutEdge.getTargetNode());
        addFlow(frontOutEdge.getSourceNode(), node);

        return true;
    }

    /**
     * move parallel gateway between front node and rear node.
     *
     * @param frontNode
     * @param gateway1
     * @param gateway2
     * @return true/false
     */
    @Override
    public boolean moveNextNode(BpmnNode frontNode, BpmnParallelGateway gateway1, BpmnParallelGateway gateway2) {
        //check.
        if (!graph.containsVertex(frontNode) || !graph.containsVertex(gateway1) || !graph.containsVertex(gateway2)) {
            return false;
        }

        List<BpmnEdge> frontOutEdges = new LinkedList<>(graph.getOutEdges(frontNode));
        List<BpmnEdge> gatewayOutEdges = new LinkedList<>(graph.getOutEdges(gateway2));
        List<BpmnEdge> gatewayInEdges = new LinkedList<>(graph.getInEdges(gateway1));
        if ((frontOutEdges.size() != 1) || (gatewayOutEdges.size() != 1) || (gatewayInEdges.size() != 1)) {
            return false;
        }

        BpmnEdge gatewayInEdge = gatewayInEdges.get(0);
        BpmnEdge gatewayOutEdge = gatewayOutEdges.get(0);
        BpmnEdge frontOutEdge = frontOutEdges.get(0);

        //remove edge.
        graph.removeEdge(gatewayInEdge);
        graph.removeEdge(gatewayOutEdge);
        graph.removeEdge(frontOutEdge);

        //reconnect edge.
        addFlow(gatewayInEdge.getSourceNode(), gatewayOutEdge.getTargetNode());
        addFlow(gatewayOutEdge.getSourceNode(), frontOutEdge.getTargetNode());
        addFlow(frontOutEdge.getSourceNode(), gateway1);

        return true;
    }

    /**
     * move to parallel process.
     *
     * @param gateway
     * @param node
     * @return true/false
     */
    @Override
    public boolean moveParallelNode(BpmnParallelGateway gateway, BpmnNode node) {
        //check.
        if (!graph.containsVertex(gateway) || !graph.containsVertex(node)) {
            return false;
        }

        List<BpmnEdge> nodeOutEdges = new LinkedList<>(graph.getOutEdges(node));
        List<BpmnEdge> nodeInEdges = new LinkedList<>(graph.getInEdges(node));
        if ((nodeOutEdges.size() != 1) || (nodeInEdges.size() != 1)) {
            return false;
        }

        BpmnEdge nodeInEdge = nodeInEdges.get(0);
        BpmnEdge nodeOutEdge = nodeOutEdges.get(0);

        //remove edge.
        List<BpmnNode> nodes = new LinkedList(graph.getSuccessors(gateway));
        if (nodes.size() == 1 && nodes.get(0) == gateway.getPairedGateway()) {
            graph.removeEdge(graph.findEdge(gateway, gateway.getPairedGateway()));
        }
        graph.removeEdge(nodeInEdge);
        graph.removeEdge(nodeOutEdge);

        //reconnect edge.
        addFlow(nodeInEdge.getSourceNode(), nodeOutEdge.getTargetNode());
        addFlow(nodeOutEdge.getSourceNode(), gateway.getPairedGateway());
        addFlow(gateway, node);

        return true;
    }

    /**
     * move to parallel process.
     *
     * @param frontGateway
     * @param gateway1
     * @param gateway2
     * @return true/false
     */
    @Override
    public boolean moveParallelNode(BpmnParallelGateway frontGateway, BpmnParallelGateway gateway1, BpmnParallelGateway gateway2) {
        //check.
        if (!graph.containsVertex(frontGateway)
                || !graph.containsVertex(gateway1) || !graph.containsVertex(gateway2)) {
            return false;
        }

        List<BpmnEdge> gatewayOutEdges = new LinkedList<>(graph.getOutEdges(gateway2));
        List<BpmnEdge> gatewayInEdges = new LinkedList<>(graph.getInEdges(gateway1));
        if ((gatewayOutEdges.size() != 1) || (gatewayInEdges.size() != 1)) {
            return false;
        }

        BpmnEdge gatewayInEdge = gatewayInEdges.get(0);
        BpmnEdge gatewayOutEdge = gatewayOutEdges.get(0);

        //remove edge.
        List<BpmnNode> nodes = new LinkedList(graph.getSuccessors(frontGateway));
        if (nodes.size() == 1 && nodes.get(0) == frontGateway.getPairedGateway()) {
            graph.removeEdge(graph.findEdge(frontGateway, frontGateway.getPairedGateway()));
        }
        graph.removeEdge(gatewayInEdge);
        graph.removeEdge(gatewayOutEdge);

        //reconnect edge.
        addFlow(gatewayInEdge.getSourceNode(), gatewayOutEdge.getTargetNode());
        addFlow(gatewayOutEdge.getSourceNode(), frontGateway.getPairedGateway());
        addFlow(frontGateway, gateway1);

        return true;
    }

    /**
     * remove node. connection with the before and after it will do
     * automatically.
     *
     * @param node
     * @return true/false
     */
    @Override
    public boolean removeNode(BpmnNode node) {
        //check.
        if (false == graph.containsVertex(node)) {
            return false;
        }
        List<BpmnEdge> inEdges = new LinkedList(graph.getInEdges(node));
        List<BpmnEdge> outEdges = new LinkedList(graph.getOutEdges(node));
        if (inEdges.size() != 1 || outEdges.size() != 1) {
            return false;
        }
        // BpmnParallelGatewayとBpmnParallelGatewayとの結合を可能にした s-heya 2016/10/14
        //List<BpmnNode> neighbors = new LinkedList(graph.getNeighbors(node));
        //boolean connect = false;
        //for (BpmnNode neighbor : neighbors) {
        //    if (!(neighbor instanceof BpmnParallelGateway)) {
        //        connect = true;
        //    }
        //}
        //connect edge.
        BpmnEdge inEdge = inEdges.get(0);
        BpmnEdge outEdge = outEdges.get(0);

        boolean connect = true;
        if (graph.getSource(inEdge) instanceof BpmnGatewayNode && graph.getDest(outEdge) instanceof BpmnGatewayNode) {
            if (((BpmnGatewayNode) graph.getSource(inEdge)).getPairedId() == graph.getDest(outEdge).getId()) {
                // ペアの場合は結合しない
                connect = false;
            }
        }

        if (connect) {
            addFlow(graph.getSource(inEdge), graph.getDest(outEdge));
        }
        //remove.
        graph.removeEdge(inEdge);
        graph.removeEdge(outEdge);
        graph.removeVertex(node);
        return true;
    }

    /**
     * remove parallel gateway. connection with the before and after it will do
     * automatically.
     *
     * @param gateway1
     * @param gateway2
     * @return true/false
     */
    @Override
    public boolean removeNode(BpmnParallelGateway gateway1, BpmnParallelGateway gateway2) {
        //check.
        if (false == graph.containsVertex(gateway1) || false == graph.containsVertex(gateway2)) {
            return false;
        }
        List<BpmnEdge> inEdges = new LinkedList(graph.getInEdges(gateway1));
        List<BpmnEdge> outEdges = new LinkedList(graph.getOutEdges(gateway2));
        if (inEdges.size() != 1 && outEdges.size() != 1) {
            return false;
        }
        //connect edge.
        BpmnEdge inEdge = inEdges.get(0);
        BpmnEdge outEdge = outEdges.get(0);

        boolean connect = true;
        if (graph.getSource(inEdge) instanceof BpmnGatewayNode && graph.getDest(outEdge) instanceof BpmnGatewayNode) {
            if (((BpmnGatewayNode) graph.getSource(inEdge)).getPairedId() == graph.getDest(outEdge).getId()) {
                // ペアの場合は結合しない
                connect = false;
            }
        }

        if (connect) {
            addFlow(graph.getSource(inEdge), graph.getDest(outEdge));
        }
        //remove.
        graph.removeEdge(inEdge);
        graph.removeEdge(outEdge);
        graph.removeVertex(gateway1);
        graph.removeVertex(gateway2);
        return true;
    }

    /**
     *
     * @param sourceNode
     * @param targetNode
     * @return
     */
    private boolean addFlow(BpmnNode sourceNode, BpmnNode targetNode) {
        BpmnSequenceFlow flow = new BpmnSequenceFlow(RandomStringUtils.randomAlphanumeric(uuidLength), "", sourceNode, targetNode);
        return graph.addEdge(flow, flow.getSourceNode(), flow.getTargetNode(), EdgeType.DIRECTED);
    }

    /**
     *
     * @return
     */
    private BpmnDocument initBpmn() {
        BpmnDocument bpmnDoc = new BpmnDocument();
        BpmnProcess process = new BpmnProcess();
        bpmnDoc.setProcess(process);
        return bpmnDoc;
    }

    /**
     *
     * @param bpmn
     * @return
     */
    private boolean updateFlow(BpmnDocument bpmn) {
        Map<String, BpmnNode> nodeMap = new HashMap<>();
        BpmnProcess process = bpmn.getProcess();
        for (BpmnStartEvent node : process.getStartEventCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnEndEvent node : process.getEndEventCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnTerminateEndEvent node : process.getTerminateEndEventCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnTask node : process.getTaskCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnParallelGateway node : process.getParallelGatewayCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnExclusiveGateway node : process.getExclusiveGatewayCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnInclusiveGateway node : process.getInclusiveGatewayCollection()) {
            graph.addVertex(node);
            nodeMap.put(node.getId(), node);
        }
        for (BpmnSequenceFlow flow : process.getSequenceFlowCollection()) {
            // source と target が並列の開始終了ペアの場合は無視する。
            boolean isExistGateway = false;
            for (BpmnParallelGateway gateway : process.getParallelGatewayCollection()) {
                if (gateway.getId() == flow.getSourceRef()
                        && gateway.getPairedId() == flow.getTargetRef()) {
                    isExistGateway = true;
                    break;
                }
            }

            if (isExistGateway) {
                continue;
            }

            BpmnNode source = nodeMap.get(flow.getSourceRef());
            BpmnNode target = nodeMap.get(flow.getTargetRef());
            graph.addEdge(flow, source, target);
        }
        return true;
    }

    /**
     * get bpmn document.
     *
     * @return bpmn document
     */
    @Override
    public BpmnDocument getBpmnDefinitions() {
        List<BpmnNode> nodes = new LinkedList(graph.getVertices());
        List<BpmnEdge> edges = new LinkedList(graph.getEdges());

        List<BpmnStartEvent> startEventCollection = new LinkedList<>();
        List<BpmnEndEvent> endEventCollection = new LinkedList<>();
        List<BpmnTerminateEndEvent> terminateEndEventCollection = new LinkedList<>();
        List<BpmnTask> taskCollection = new LinkedList<>();
        List<BpmnParallelGateway> parallelGatewayCollection = new LinkedList<>();
        List<BpmnExclusiveGateway> exclusiveGatewayCollection = new LinkedList<>();
        List<BpmnInclusiveGateway> inclusiveGatewayCollection = new LinkedList<>();
        List<BpmnSequenceFlow> sequenceFlowCollection = new LinkedList<>();

        for (BpmnNode node : nodes) {
            if (node instanceof BpmnStartEvent) {
                startEventCollection.add((BpmnStartEvent) node);
            } else if (node instanceof BpmnEndEvent) {
                endEventCollection.add((BpmnEndEvent) node);
            } else if (node instanceof BpmnTerminateEndEvent) {
                terminateEndEventCollection.add((BpmnTerminateEndEvent) node);
            } else if (node instanceof BpmnTask) {
                taskCollection.add((BpmnTask) node);
            } else if (node instanceof BpmnParallelGateway) {
                BpmnParallelGateway parallel = (BpmnParallelGateway) node;
                if (parallel.getPairedGateway() == null) {
                    for (BpmnNode gateway : nodes) {
                        if (gateway instanceof BpmnParallelGateway) {
                            BpmnParallelGateway pair = (BpmnParallelGateway) gateway;
                            if (pair.getId().equals(parallel.getPairedId())) {
                                parallel.setPairedGateway(pair);
                                pair.setPairedGateway(parallel);
                                break;
                            }
                        }
                    }
                }
                parallelGatewayCollection.add(parallel);
            } else if (node instanceof BpmnExclusiveGateway) {
                exclusiveGatewayCollection.add((BpmnExclusiveGateway) node);
            } else if (node instanceof BpmnInclusiveGateway) {
                inclusiveGatewayCollection.add((BpmnInclusiveGateway) node);
            }
        }
        for (BpmnEdge edge : edges) {
            if (edge instanceof BpmnSequenceFlow) {
                BpmnSequenceFlow sequenceFlow = (BpmnSequenceFlow) edge;

                // source と target が並列の開始終了ペアの場合は無視する。
                boolean isExistGateway = false;
                for (BpmnParallelGateway gateway : parallelGatewayCollection) {
                    if (gateway.getId() == sequenceFlow.getSourceRef()
                            && gateway.getPairedId() == sequenceFlow.getTargetRef()) {
                        isExistGateway = true;
                        break;
                    }
                }

                if (isExistGateway) {
                    continue;
                }

                sequenceFlow.setSourceNode(graph.getSource(edge));
                sequenceFlow.setTargetNode(graph.getDest(edge));
                sequenceFlowCollection.add(sequenceFlow);
            }
        }
        BpmnProcess process = bpmn.getProcess();
        process.setStartEventCollection(startEventCollection);
        process.setEndEventCollection(endEventCollection);
        process.setTerminateEndEventCollection(terminateEndEventCollection);
        process.setTaskCollection(taskCollection);
        process.setParallelGatewayCollection(parallelGatewayCollection);
        process.setExclusiveGatewayCollection(exclusiveGatewayCollection);
        process.setInclusiveGatewayCollection(inclusiveGatewayCollection);
        process.setSequenceFlowCollection(sequenceFlowCollection);
        return bpmn;
    }

    /**
     *
     * @return
     */
    public DirectedGraph getGraph() {
        return graph;
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
