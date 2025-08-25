/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model.entity;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task")
public class BpmnTask extends BpmnNode implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public BpmnTask() {
    }
    
    public BpmnTask(String id, String name) {
        super(id, name);
    }
    
    public BpmnTask cost(BpmnCost cost) {
        this.setCost(cost);
        return this;
    }
    
    @Override
    public String toString() {
        return "BpmnTask{" + super.toString() + '}';
    }
    
}
