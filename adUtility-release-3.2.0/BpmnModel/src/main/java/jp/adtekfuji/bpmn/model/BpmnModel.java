/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model;

import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.bpmn.model.entity.BpmnDocument;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnParallelGateway;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;

/**
 *
 * @author ke.yokoi
 */
public interface BpmnModel {

    public boolean createModel(BpmnStartEvent start, BpmnEndEvent end);

    public boolean createModel(BpmnDocument bpmn);

    public boolean addNextNode(BpmnNode frontNode, BpmnNode node);

    public boolean addNextNode(BpmnNode frontNode, BpmnParallelGateway gateway1, BpmnParallelGateway gateway2);

    public boolean addParallelNode(BpmnParallelGateway gateway1, BpmnNode node);

    public boolean moveNextNode(BpmnNode frontNode, BpmnNode node);

    public boolean moveNextNode(BpmnNode frontNode, BpmnParallelGateway gateway1, BpmnParallelGateway gateway2);

    public boolean moveParallelNode(BpmnParallelGateway gateway1, BpmnNode node);

    public boolean moveParallelNode(BpmnParallelGateway frontGateway, BpmnParallelGateway gateway1, BpmnParallelGateway gateway2);

    public boolean removeNode(BpmnNode node);

    public boolean removeNode(BpmnParallelGateway gateway1, BpmnParallelGateway gateway2);

    public BpmnDocument getBpmnDefinitions();

}
