/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.common;

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
public class UnitAgendaEntity {

    private String title1;
    private String fontColor;
    private String backgraundColor;
    private Boolean isBlink;
    private List<UnitAgendaConcurrentEntity> planCollection = new ArrayList<>();
    private List<UnitAgendaConcurrentEntity> actualCollection = new ArrayList<>();
    private Long unitId;

    public UnitAgendaEntity() {

    }

    public UnitAgendaEntity(String title1, String fontColor, String backgraundColor) {
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

    public List<UnitAgendaConcurrentEntity> getPlanCollection() {
        return planCollection;
    }

    public void setPlanCollection(List<UnitAgendaConcurrentEntity> planCollection) {
        this.planCollection = planCollection;
    }

    public UnitAgendaEntity addPlan(UnitAgendaConcurrentEntity concurrent) {
        this.planCollection.add(concurrent);
        return this;
    }

    public UnitAgendaEntity addPlans(UnitAgendaConcurrentEntity... concurrents) {
        this.planCollection.addAll(Arrays.asList(concurrents));
        return this;
    }

    public UnitAgendaEntity addAllPlans(List<UnitAgendaConcurrentEntity> concurrents) {
        this.planCollection.addAll(concurrents);
        return this;
    }

    public List<UnitAgendaConcurrentEntity> getActualCollection() {
        return actualCollection;
    }

    public void setActualCollection(List<UnitAgendaConcurrentEntity> actualCollection) {
        this.actualCollection = actualCollection;
    }

    public UnitAgendaEntity addActual(UnitAgendaConcurrentEntity concurrent) {
        this.actualCollection.add(concurrent);
        return this;
    }

    public UnitAgendaEntity addActuals(UnitAgendaConcurrentEntity... concurrents) {
        this.actualCollection.addAll(Arrays.asList(concurrents));
        return this;
    }

    public UnitAgendaEntity addAllActuals(List<UnitAgendaConcurrentEntity> concurrents) {
        this.actualCollection.addAll(concurrents);
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
