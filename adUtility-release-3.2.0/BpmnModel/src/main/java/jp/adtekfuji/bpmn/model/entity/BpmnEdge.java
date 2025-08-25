/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BpmnEdge implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlAttribute(required = true)
    private String id;
    @XmlAttribute
    private String name;
    @XmlTransient
    private BpmnNode sourceNode;
    @XmlTransient
    private BpmnNode targetNode;

    public BpmnEdge() {
    }

    public BpmnEdge(String id, String name, BpmnNode sourceNode, BpmnNode targetNode) {
        this.id = id;
        this.name = name;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BpmnNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(BpmnNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public BpmnNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(BpmnNode targetNode) {
        this.targetNode = targetNode;
    }

    public BpmnCost getCost() {
        return sourceNode.getCost();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final BpmnEdge other = (BpmnEdge) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BpmnEdge{" + "id=" + id + ", name=" + name + ", sourceNode=" + sourceNode + ", targetNode=" + targetNode + '}';
    }

}
