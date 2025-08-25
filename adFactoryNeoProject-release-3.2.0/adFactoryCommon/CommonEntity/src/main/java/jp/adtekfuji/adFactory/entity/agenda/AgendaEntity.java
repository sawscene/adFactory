/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.agenda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 予実情報.
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "agenda")
public class AgendaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String title1;
    @XmlElement()
    private String title2;
    @XmlElement()
    private String fontColor;
    @XmlElement()
    private String backgraundColor;
    @XmlElement()
    private Boolean isBlink;
    @XmlElement()
    private Long delayTimeMillisec;
    @XmlElementWrapper(name = "planConcurrents")
    @XmlElement(name = "planConcurrent")
    private List<AgendaConcurrentEntity> planCollection = new ArrayList<>();
    @XmlElementWrapper(name = "actualConcurrents")
    @XmlElement(name = "actualConcurrent")
    private List<AgendaConcurrentEntity> actualCollection = new ArrayList<>();

    public AgendaEntity() {
    }

    public AgendaEntity(String title1, String title2, String fontColor, String backgraundColor) {
        this.title1 = title1;
        this.title2 = title2;
        this.fontColor = fontColor;
        this.backgraundColor = backgraundColor;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackgraundColor() {
        return backgraundColor;
    }

    public void setBackgraundColor(String backgraundColor) {
        this.backgraundColor = backgraundColor;
    }

    public Boolean getIsBlink() {
        return isBlink;
    }

    public void setIsBlink(Boolean isBlink) {
        this.isBlink = isBlink;
    }

    public Long getDelayTimeMillisec() {
        return delayTimeMillisec;
    }

    public void setDelayTimeMillisec(Long delayTimeMillisec) {
        this.delayTimeMillisec = delayTimeMillisec;
    }

    public List<AgendaConcurrentEntity> getPlanCollection() {
        return planCollection;
    }

    public void setPlanCollection(List<AgendaConcurrentEntity> planCollection) {
        this.planCollection = planCollection;
    }

    public AgendaEntity addPlan(AgendaConcurrentEntity concurrent) {
        this.planCollection.add(concurrent);
        return this;
    }

    public AgendaEntity addPlans(AgendaConcurrentEntity... concurrents) {
        this.planCollection.addAll(Arrays.asList(concurrents));
        return this;
    }

    public AgendaEntity addAllPlans(List<AgendaConcurrentEntity> concurrents) {
        this.planCollection.addAll(concurrents);
        return this;
    }

    public List<AgendaConcurrentEntity> getActualCollection() {
        return actualCollection;
    }

    public void setActualCollection(List<AgendaConcurrentEntity> actualCollection) {
        this.actualCollection = actualCollection;
    }

    public AgendaEntity addActual(AgendaConcurrentEntity concurrent) {
        this.actualCollection.add(concurrent);
        return this;
    }

    public AgendaEntity addActuals(AgendaConcurrentEntity... concurrents) {
        this.actualCollection.addAll(Arrays.asList(concurrents));
        return this;
    }

    public AgendaEntity addAllActuals(List<AgendaConcurrentEntity> concurrents) {
        this.actualCollection.addAll(concurrents);
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final AgendaEntity other = (AgendaEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return "AgendaEntity{" + "title1=" + title1 + ", title2=" + title2 + ", fontColor=" + fontColor + ", backgraundColor=" + backgraundColor + ", isBlink=" + isBlink + '}';
    }

}
