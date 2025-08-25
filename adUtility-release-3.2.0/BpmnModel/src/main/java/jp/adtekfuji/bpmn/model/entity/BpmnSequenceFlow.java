/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model.entity;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sequenceFlow")
public class BpmnSequenceFlow extends BpmnEdge implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlAttribute(required = true)
    private String sourceRef;
    @XmlAttribute
    private String targetRef;

    public BpmnSequenceFlow() {
    }

    public BpmnSequenceFlow(String id, String name, BpmnNode sourceNode, BpmnNode targetNode) {
        super(id, name, sourceNode, targetNode);
        this.sourceRef = sourceNode.getId();
        this.targetRef = targetNode.getId();
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    @Override
    public void setSourceNode(BpmnNode sourceNode) {
        super.setSourceNode(sourceNode);
        this.sourceRef = sourceNode.getId();
    }

    @Override
    public void setTargetNode(BpmnNode targetNode) {
        super.setTargetNode(targetNode);
        this.targetRef = targetNode.getId();
    }

    @Override
    public String toString() {
        return "BpmnSequenceFlow{" + super.toString() + ", sourceRef=" + sourceRef + ", targetRef=" + targetRef + '}';
    }

}
