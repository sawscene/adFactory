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
@XmlRootElement(name = "terminateEndEvent")
public class BpmnTerminateEndEvent extends BpmnNode implements Serializable {

    private static final long serialVersionUID = 1L;

    public BpmnTerminateEndEvent() {
    }

    public BpmnTerminateEndEvent(String id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "BpmnTerminateEndEvent{" + super.toString() + '}';
    }

}
