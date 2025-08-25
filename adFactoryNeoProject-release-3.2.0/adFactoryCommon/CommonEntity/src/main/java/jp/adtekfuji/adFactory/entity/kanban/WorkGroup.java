/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 工程グループ
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workGroup")
public class WorkGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long qauntity;
    @XmlElement
    private Date startTime;

    public WorkGroup() {
    }

    public WorkGroup(Long qauntity, Date startTime) {
        this.qauntity = qauntity;
        this.startTime = startTime;
    }
    
    public Long getQauntity() {
        return qauntity;
    }

    public void setQauntity(Long qauntity) {
        this.qauntity = qauntity;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "WorkGroup{" + "qauntity=" + qauntity + ", startTime=" + startTime + '}';
    }
}
