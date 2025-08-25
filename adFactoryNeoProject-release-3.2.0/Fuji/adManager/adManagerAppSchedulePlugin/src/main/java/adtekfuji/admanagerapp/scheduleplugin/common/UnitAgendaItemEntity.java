/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.agenda.AgendaConcurrentEntity;
import jp.adtekfuji.adFactory.entity.agenda.AgendaEntity;
import jp.adtekfuji.adFactory.entity.agenda.AgendaItemEntity;

/**
 * ユニットの予実情報アイテム
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.11.24.thr
 */
public class UnitAgendaItemEntity {

    private String title1;
    private String title2;
    private String title3;
    private Integer taktTime;
    private Date startTime;
    private Date endTIme;
    private String fontColor;
    private String backgraundColor;
    private String frameColor;
    private Boolean isBlink;

    private Long kanbanId;

    public UnitAgendaItemEntity() {
    }

    public UnitAgendaItemEntity(String title1, String title2, String title3, Integer taktTime, Date startTime, Date endTIme, String fontColor, String backgraundColor, String frameColor, Boolean isBlink) {
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

    public void createPlanData(AgendaEntity item, Long kanbanId) {
        this.title1 = item.getTitle1();
        this.title2 = item.getTitle2();
        Date fastDate = null;
        Date lastDate = null;
        for (AgendaConcurrentEntity ace : item.getPlanCollection()) {
            ace.getItemCollection().sort(Comparator.comparing((con) -> con.getStartTime()));
            for (AgendaItemEntity aie : ace.getItemCollection()) {
                if (Objects.isNull(fastDate) && Objects.isNull(lastDate)) {
                    fastDate = aie.getStartTime();
                    lastDate = aie.getEndTIme();
                } else {
                    if (fastDate.after(aie.getStartTime())) {
                        fastDate = aie.getStartTime();
                    }
                    if (lastDate.before(aie.getEndTIme())) {
                        lastDate = aie.getEndTIme();
                    }
                }
            }
        }
        this.taktTime = (int) (lastDate.getTime() - fastDate.getTime());
        this.startTime = fastDate;
        this.endTIme = lastDate;
        this.fontColor = item.getFontColor();
        this.backgraundColor = item.getTitle1();
        this.frameColor = item.getBackgraundColor();
        this.isBlink = item.getIsBlink();
        this.kanbanId = kanbanId;
    }

    public void createActualData(AgendaEntity item, Long kanbanId) {
        this.title1 = item.getTitle1();
        this.title2 = item.getTitle2();
        Date fastDate = null;
        Date lastDate = null;
        for (AgendaConcurrentEntity ace : item.getActualCollection()) {
            ace.getItemCollection().sort(Comparator.comparing((con) -> con.getStartTime()));
            for (AgendaItemEntity aie : ace.getItemCollection()) {
                if (Objects.isNull(fastDate) && Objects.isNull(lastDate)) {
                    fastDate = aie.getStartTime();
                    lastDate = aie.getEndTIme();
                } else {
                    if (fastDate.after(aie.getStartTime())) {
                        fastDate = aie.getStartTime();
                    }
                    if (lastDate.before(aie.getEndTIme())) {
                        lastDate = aie.getEndTIme();
                    }
                }
            }
        }
        this.taktTime = (int) (lastDate.getTime() - fastDate.getTime());
        this.startTime = fastDate;
        this.endTIme = lastDate;
        this.fontColor = item.getFontColor();
        this.backgraundColor = item.getTitle1();
        this.frameColor = item.getBackgraundColor();
        this.isBlink = item.getIsBlink();
        this.kanbanId = kanbanId;
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
