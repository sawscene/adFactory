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
@XmlRootElement(name = "parallelGateway")
public class BpmnParallelGateway extends BpmnGatewayNode implements Serializable {

    private static final long serialVersionUID = 1L;

    public BpmnParallelGateway() {
    }

    public BpmnParallelGateway(String id, String name, BpmnParallelGateway pairedGateway) {
        super(id, name, pairedGateway);
    }

    @Override
    public String toString() {
        return "BpmnParallelGateway{" + super.toString() + '}';
    }

}
