/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.bpmn.model.entity;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Objects;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
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
@XmlRootElement(name = "definitions")
public class BpmnDocument implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String targetNamespace = "http://www.adtek-fuji.co.jp/adfactory";
    @XmlElement
    private BpmnProcess process;

    public BpmnDocument() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public BpmnProcess getProcess() {
        return process;
    }

    public void setProcess(BpmnProcess process) {
        this.process = process;
    }

    /**
     * BpmnDocument object to xml.
     *
     * @return
     * @throws JAXBException
     */
    public String marshal() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(getClass());
        Marshaller marshaller = context.createMarshaller();
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        //marshaller.setProperty(Marshaller.JAXB_ENCODING, "Shift_JIS"); // Java 8の場合
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // Java 21の場合
        marshaller.marshal(this, outstream);
        return outstream.toString();
    }
    
    /**
     * XMLに整列化する。
     * エンコーディングを指定すると工程名が化けてしまうため、エンコーディングを指定していない。
     * 
     * @return
     * @throws JAXBException 
     */
    public String marshal2() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(getClass());
        Marshaller marshaller = context.createMarshaller();
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        marshaller.marshal(this, outstream);
        return outstream.toString();
    }
    
    /**
     * xml to BpmnDocument object.
     *
     * @param xml
     * @return
     * @throws jakarta.xml.bind.JAXBException
     */
    public static BpmnDocument unmarshal(String xml) throws JAXBException {
        StringReader sr = new StringReader(xml);
        return JAXB.unmarshal(sr, BpmnDocument.class);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.name);
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
        final BpmnDocument other = (BpmnDocument) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BpmnDefinitions{" + "name=" + name + '}';
    }

}
