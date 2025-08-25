/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.common.agenda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ユニットの予実情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.11.24.thr
 */
public class CustomAgendaEntity {

    private static final long serialVersionUID = 1L;

    private String title1;
    private String fontColor;
    private String backgraundColor;
    private Boolean isBlink;

    private List<CustomAgendaConcurrentEntity> planCollection = new ArrayList<>();
    private List<CustomAgendaConcurrentEntity> actualCollection = new ArrayList<>();
    private Long unitId;
    private Long organizationId;

    public CustomAgendaEntity() {

    }

    public CustomAgendaEntity(String title1, String fontColor, String backgraundColor) {
        this.title1 = title1;
        this.fontColor = fontColor;
        this.backgraundColor = backgraundColor;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
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

    public List<CustomAgendaConcurrentEntity> getPlanCollection() {
        return planCollection;
    }

    public void setPlanCollection(List<CustomAgendaConcurrentEntity> planCollection) {
        this.planCollection = planCollection;
    }

    public CustomAgendaEntity addPlan(CustomAgendaConcurrentEntity concurrent) {
        this.planCollection.add(concurrent);
        return this;
    }

    public CustomAgendaEntity addPlans(CustomAgendaConcurrentEntity... concurrents) {
        this.planCollection.addAll(Arrays.asList(concurrents));
        return this;
    }

    public CustomAgendaEntity addAllPlans(List<CustomAgendaConcurrentEntity> concurrents) {
        this.planCollection.addAll(concurrents);
        return this;
    }

    public List<CustomAgendaConcurrentEntity> getActualCollection() {
        return actualCollection;
    }

    public void setActualCollection(List<CustomAgendaConcurrentEntity> actualCollection) {
        this.actualCollection = actualCollection;
    }

    public CustomAgendaEntity addActual(CustomAgendaConcurrentEntity concurrent) {
        this.actualCollection.add(concurrent);
        return this;
    }

    public CustomAgendaEntity addActuals(CustomAgendaConcurrentEntity... concurrents) {
        this.actualCollection.addAll(Arrays.asList(concurrents));
        return this;
    }

    public CustomAgendaEntity addAllActuals(List<CustomAgendaConcurrentEntity> concurrents) {
        this.actualCollection.addAll(concurrents);
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
