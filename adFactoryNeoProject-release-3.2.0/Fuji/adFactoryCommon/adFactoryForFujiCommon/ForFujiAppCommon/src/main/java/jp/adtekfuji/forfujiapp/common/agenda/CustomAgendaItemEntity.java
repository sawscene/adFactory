/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.common.agenda;

import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.agenda.AgendaItemEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;

/**
 * ユニットの予実情報アイテム
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.11.24.thr
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customAgendaItem")
public class CustomAgendaItemEntity {

    private final String defaultFrameColor = "lightgray";

    @XmlElement()
    private String title1;
    @XmlElement()
    private String title2;
    @XmlElement()
    private String title3;
    @XmlElement()
    private Integer taktTime;
    @XmlElement()
    private Date startTime;
    @XmlElement()
    private Date endTIme;
    @XmlElement()
    private String fontColor;
    @XmlElement()
    private String backgraundColor;
    @XmlElement()
    private String frameColor;
    @XmlElement()
    private Boolean isBlink;
    @XmlElement()
    private Long kanbanId;

    public CustomAgendaItemEntity() {
    }

    public CustomAgendaItemEntity(AgendaItemEntity item) {
        this.title1 = item.getTitle1();
        this.title2 = item.getTitle2();
        this.title3 = item.getTitle3();
        this.taktTime = item.getTaktTime();
        this.startTime = item.getStartTime();
        this.endTIme = item.getEndTIme();
        this.fontColor = item.getFontColor();
        this.backgraundColor = item.getBackgraundColor();
        this.isBlink = item.getIsBlink();
    }

    public CustomAgendaItemEntity(String title1, String title2, String title3, Integer taktTime, Date startTime, Date endTIme, String fontColor, String backgraundColor, String frameColor, Boolean isBlink) {
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.taktTime = taktTime;
        this.startTime = startTime;
        this.endTIme = endTIme;
        this.fontColor = fontColor;
        this.backgraundColor = backgraundColor;
        this.frameColor = frameColor;
        this.isBlink = isBlink;
    }

    public void createKanbanPlanData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity displayedStatusInfoEntity) {
        this.kanbanId = item.getKanbanId();
        this.title1 = item.getKanbanName();
        this.title2 = item.getWorkflowName();
        this.startTime = item.getPlanStartTime();
        this.endTIme = item.getPlanEndTime();
        this.fontColor = displayedStatusInfoEntity.getFontColor();
        this.backgraundColor = displayedStatusInfoEntity.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
    }

    public void createKanbanActualData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity displayedStatusInfoEntity) {
        this.kanbanId = item.getKanbanId();
        this.title1 = item.getWorkName();
        this.title2 = item.getOrganizationName();
        this.taktTime = 0;
        this.startTime = item.getActualStartTime();
        this.endTIme = item.getActualEndTime();
        this.fontColor = displayedStatusInfoEntity.getFontColor();
        this.backgraundColor = displayedStatusInfoEntity.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
    }

    public void createOrganizationPlanData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity displayedStatusInfoEntity) {
        this.kanbanId = item.getKanbanId();
        this.title1 = item.getWorkflowName();
        this.title2 = item.getWorkName();
        this.startTime = item.getPlanStartTime();
        this.endTIme = item.getPlanEndTime();
        this.fontColor = displayedStatusInfoEntity.getFontColor();
        this.backgraundColor = displayedStatusInfoEntity.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
    }

    public void createOrganizationActualData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity displayedStatusInfoEntity) {
        this.kanbanId = item.getKanbanId();
        this.title1 = item.getWorkflowName();
        this.title2 = item.getWorkName();
        this.startTime = item.getActualStartTime();
        this.endTIme = item.getActualEndTime();
        this.fontColor = displayedStatusInfoEntity.getFontColor();
        this.backgraundColor = displayedStatusInfoEntity.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
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

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTIme() {
        return endTIme;
    }

    public void setEndTIme(Date endTIme) {
        this.endTIme = endTIme;
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

    public String getFrameColor() {
        return frameColor;
    }

    public void setFrameColor(String frameColor) {
        this.frameColor = frameColor;
    }

    public Boolean getIsBlink() {
        return isBlink;
    }

    public void setIsBlink(Boolean isBlink) {
        this.isBlink = isBlink;
    }

    public Long getKanbanId() {
        return kanbanId;
    }

    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
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
        final AgendaItemEntity other = (AgendaItemEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return "UnitAgendaItemEntity{" + "title1=" + title1 + ", title2=" + title2 + ", title3=" + title3 + ", taktTime=" + taktTime + ", startTime=" + startTime + ", endTIme=" + endTIme + ", fontColor=" + fontColor + ", backgraundColor=" + backgraundColor + ", frameColor=" + frameColor + ", isBlink=" + isBlink + '}';
    }
}
