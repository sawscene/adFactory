/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.adtekfuji.bpmn.model.entity.BpmnCost;
import jp.adtekfuji.bpmn.model.entity.BpmnEdge;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author ke.yokoi
 */
public class BpmnAlgorithm {

    private BpmnAlgorithm() {
    }

    /**
     * 最短経路探索.
     *
     * @param model
     * @param node1
     * @param node2
     * @return
     */
    public static List<BpmnNode> getShortestPath(final BpmnModel model, final BpmnNode node1, final BpmnNode node2) {

        BpmnModeler modeler = (BpmnModeler) model;
        Transformer<BpmnEdge, Long> wtTransformer = new Transformer<BpmnEdge, Long>() {
            @Override
            public Long transform(BpmnEdge edge) {
                if (edge.getCost() == null || edge.getCost().getWeight() == null) {
                    return 0L;
                }
                return edge.getCost().getWeight().longValue();
            }
        };
        DijkstraShortestPath<BpmnNode, BpmnEdge> alg = new DijkstraShortestPath(modeler.getGraph(), wtTransformer);
        List<BpmnNode> nodes = new ArrayList<>();
        for (BpmnEdge edge : alg.getPath(node1, node2)) {
            nodes.add(edge.getSourceNode());
        }
        return nodes;
    }

    /**
     * 最長経路探索.（コストを逆数にして最短経路探索）
     *
     * @param model
     * @param node1
     * @param node2
     * @return
     */
    public static List<BpmnNode> getLongestPath(final BpmnModel model, final BpmnNode node1, final BpmnNode node2) {

        BpmnModeler modeler = (BpmnModeler) model;
        Transformer<BpmnEdge, Double> wtTransformer = new Transformer<BpmnEdge, Double>() {
            @Override
            public Double transform(BpmnEdge edge) {
                if (edge.getCost() == null || edge.getCost().getWeight() == null) {
                    return (double) (1.0 / Long.MAX_VALUE);
                }
                return (double) (1.0 / (edge.getCost().getWeight().longValue()));
            }
        };
        DijkstraShortestPath<BpmnNode, BpmnEdge> alg = new DijkstraShortestPath(modeler.getGraph(), wtTransformer);
        List<BpmnNode> nodes = new ArrayList<>();
        for (BpmnEdge edge : alg.getPath(node1, node2)) {
            nodes.add(edge.getSourceNode());
        }
        return nodes;
    }

    /**
     * 最大フロー取得.
     *
     * @param model
     * @param cost need capacity
     * @param node1
     * @param node2
     * @return
     */
    public static int getMaxFlow(final BpmnModel model, final BpmnCost cost, final BpmnNode node1, final BpmnNode node2) {

        BpmnModeler modeler = (BpmnModeler) model;
        Transformer<BpmnEdge, Double> capTransformer = new Transformer<BpmnEdge, Double>() {
            @Override
            public Double transform(BpmnEdge edge) {
                if (edge.getCost() == null || edge.getCost().getCapacity() == null) {
                    return Double.MAX_VALUE;
                }
                return edge.getCost().getCapacity().doubleValue();
            }
        };
        Map<BpmnEdge, Double> edgeFlowMap = new HashMap<>();
        Factory<BpmnEdge> edgeFactory = new Factory<BpmnEdge>() {
            @Override
            public BpmnEdge create() {
                return new BpmnEdge(RandomStringUtils.randomAlphanumeric(8), null, new BpmnTask().cost(cost), null) {
                };
            }
        };
        EdmondsKarpMaxFlow<BpmnNode, BpmnEdge> alg = new EdmondsKarpMaxFlow(
                modeler.getGraph(), node1, node2, capTransformer, edgeFlowMap, edgeFactory);
        alg.evaluate();
        return alg.getMaxFlow();
    }

}
