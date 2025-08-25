/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model.entity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BpmnGatewayNode extends BpmnNode {

    private static final long serialVersionUID = 1L;
    @XmlAttribute(required = true, name = "pair")
    private String pairedId;
    @XmlTransient
    private BpmnGatewayNode pairedGateway;

    public BpmnGatewayNode() {
    }

    public BpmnGatewayNode(String id, String name, BpmnGatewayNode pairedGateway) {
        super(id, name);
        if (pairedGateway != null) {
            this.setPairedGateway(pairedGateway);
            pairedGateway.setPairedGateway(this);
        }
    }

    public BpmnGatewayNode getPairedGateway() {
        return pairedGateway;
    }

    public void setPairedGateway(BpmnGatewayNode pairedGateway) {
        this.pairedGateway = pairedGateway;
        pairedId = pairedGateway.getId();
    }

    public String getPairedId() {
        return pairedId;
    }
    
    @Override
    public String toString() {
        return super.toString() + ", pairedId=" + pairedId;
    }

}
