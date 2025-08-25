/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "process")
public class BpmnProcess implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlAttribute
    private String id;
    @XmlAttribute
    private Boolean isExecutable = true;
    @XmlElement(name = "startEvent")
    private List<BpmnStartEvent> startEventCollection = new ArrayList<>();
    @XmlElement(name = "endEvent")
    private List<BpmnEndEvent> endEventCollection = new ArrayList<>();
    @XmlElement(name = "terminateEndEvent")
    private List<BpmnTerminateEndEvent> terminateEndEventCollection = new ArrayList<>();
    @XmlElement(name = "task")
    private List<BpmnTask> taskCollection = new ArrayList<>();
    @XmlElement(name = "parallelGateway")
    private List<BpmnParallelGateway> parallelGatewayCollection = new ArrayList<>();
    @XmlElement(name = "exclusiveGateway")
    private List<BpmnExclusiveGateway> exclusiveGatewayCollection = new ArrayList<>();
    @XmlElement(name = "inclusiveGatewy")
    private List<BpmnInclusiveGateway> inclusiveGatewayCollection = new ArrayList<>();
    @XmlElement(name = "sequenceFlow")
    private List<BpmnSequenceFlow> sequenceFlowCollection = new ArrayList<>();

    public BpmnProcess() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsExecutable() {
        return isExecutable;
    }

    public void setIsExecutable(Boolean isExecutable) {
        this.isExecutable = isExecutable;
    }

    public List<BpmnStartEvent> getStartEventCollection() {
        return startEventCollection;
    }

    public void setStartEventCollection(List<BpmnStartEvent> startEventCollection) {
        this.startEventCollection = startEventCollection;
    }

    public List<BpmnEndEvent> getEndEventCollection() {
        return endEventCollection;
    }

    public void setEndEventCollection(List<BpmnEndEvent> endEventCollection) {
        this.endEventCollection = endEventCollection;
    }

    public List<BpmnTerminateEndEvent> getTerminateEndEventCollection() {
        return terminateEndEventCollection;
    }

    public void setTerminateEndEventCollection(List<BpmnTerminateEndEvent> terminateEndEventCollection) {
        this.terminateEndEventCollection = terminateEndEventCollection;
    }

    public List<BpmnTask> getTaskCollection() {
        return taskCollection;
    }

    public void setTaskCollection(List<BpmnTask> taskCollection) {
        this.taskCollection = taskCollection;
    }

    public List<BpmnParallelGateway> getParallelGatewayCollection() {
        return parallelGatewayCollection;
    }

    public void setParallelGatewayCollection(List<BpmnParallelGateway> parallelGatewayCollection) {
        this.parallelGatewayCollection = parallelGatewayCollection;
    }

    public List<BpmnExclusiveGateway> getExclusiveGatewayCollection() {
        return exclusiveGatewayCollection;
    }

    public void setExclusiveGatewayCollection(List<BpmnExclusiveGateway> exclusiveGatewayCollection) {
        this.exclusiveGatewayCollection = exclusiveGatewayCollection;
    }

    public List<BpmnInclusiveGateway> getInclusiveGatewayCollection() {
        return inclusiveGatewayCollection;
    }

    public void setInclusiveGatewayCollection(List<BpmnInclusiveGateway> inclusiveGatewayCollection) {
        this.inclusiveGatewayCollection = inclusiveGatewayCollection;
    }

    public List<BpmnSequenceFlow> getSequenceFlowCollection() {
        return sequenceFlowCollection;
    }

    public void setSequenceFlowCollection(List<BpmnSequenceFlow> sequenceFlowCollection) {
        this.sequenceFlowCollection = sequenceFlowCollection;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BpmnProcess other = (BpmnProcess) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BpmnProcess{" + "id=" + id + ", isExecutable=" + isExecutable + '}';
    }

}
