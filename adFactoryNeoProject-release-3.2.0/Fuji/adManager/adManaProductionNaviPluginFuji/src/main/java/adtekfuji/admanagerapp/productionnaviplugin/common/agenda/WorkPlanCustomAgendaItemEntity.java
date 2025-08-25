/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common.agenda;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.agenda.AgendaItemEntity;
import jp.adtekfuji.adFactory.entity.agenda.KanbanTopicInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットの予実情報アイテム
 *
 * @author (TST)min
 * @version 1.8.3
 * @since 2018/09/28
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "customAgendaItem")
public class WorkPlanCustomAgendaItemEntity {

    private final String defaultFrameColor = "lightgray";

    private final Logger logger = LogManager.getLogger();
    private static final SimpleDateFormat dateDataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    @XmlElement()
    private String title1;
    @XmlElement()
    private String title2;
    @XmlElement()
    private String title3;
    @XmlElement()
    private String title4;
    @XmlElement()
    private String title5;
    @XmlElement()
    private String workName;
    @XmlElement()
    private Integer taktTime;
    @XmlElement()
    private Date startTime;
    @XmlElement()
    private Date endTime;
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
    @XmlElement()
    private Long workKanbanId;
    @XmlElement()
    private Long organizationId;
    @XmlElement()
    private KanbanStatusEnum workKanbanStatus;

    /**
     * コンストラクタ
     */
    public WorkPlanCustomAgendaItemEntity() {
    }

    /**
     * コンストラクタ
     * 
     * @param item 
     */
    public WorkPlanCustomAgendaItemEntity(AgendaItemEntity item) {
        this.title1 = item.getTitle1();
        this.title2 = item.getTitle2();
        this.title3 = item.getTitle3();
        this.taktTime = item.getTaktTime();
        this.startTime = item.getStartTime();
        this.endTime = item.getEndTIme();
        this.fontColor = item.getFontColor();
        this.backgraundColor = item.getBackgraundColor();
        this.isBlink = item.getIsBlink();
    }

    /**
     * コンストラクタ
     * 
     * @param title1
     * @param title2
     * @param title3
     * @param taktTime
     * @param startTime
     * @param endTIme
     * @param fontColor
     * @param backgraundColor
     * @param frameColor
     * @param isBlink 
     */
    public WorkPlanCustomAgendaItemEntity(String title1, String title2, String title3, Integer taktTime, Date startTime, Date endTIme, String fontColor, String backgraundColor, String frameColor, Boolean isBlink) {
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.taktTime = taktTime;
        this.startTime = startTime;
        this.endTime = endTIme;
        this.fontColor = fontColor;
        this.backgraundColor = backgraundColor;
        this.frameColor = frameColor;
        this.isBlink = isBlink;
    }

    /**
     * 作業計画の計画
     * 
     * @param item
     * @param workKanban 
     * @param workSeparate 
     * @param statuses 
     */
    public void createKanbanPlanData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity status, long scheduleCount) {
        this.kanbanId = item.getKanbanId();
        this.workKanbanId = item.getWorkKanbanId();
        this.workKanbanStatus = item.getWorkKanbanStatus();
        this.title1 = item.getKanbanName();
        this.title2 = Objects.isNull(item.getModelName()) ? "" : item.getModelName();
        this.title3 = item.getWorkflowName();
        this.title4 = Objects.isNull(item.getOrganizationName()) ? "" : item.getOrganizationName();
        
        StringBuilder buff = new StringBuilder();
        if(scheduleCount > 0){
            buff.append("予定あり：").append(scheduleCount).append("件");
        }else{
            buff.append("予定なし");
        }
        this.title5 = buff.toString();
        
        this.workName = item.getWorkName();
        this.startTime = item.getPlanStartTime();
        this.endTime = item.getPlanEndTime();
        this.fontColor = status.getFontColor();
        this.backgraundColor = status.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
    }

    /**
     * 作業計画の実績
     * 
     * @param item
     * @param workKanban 
     * @param workSeparate 
     * @param statuses 
     */
    public void createKanbanActualData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity status) {
        this.kanbanId = item.getKanbanId();
        this.title1 = item.getWorkName();
        this.title2 = item.getOrganizationName();
        this.startTime = item.getActualStartTime();
        this.endTime = item.getActualEndTime();
        this.fontColor = status.getFontColor();
        this.backgraundColor = status.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
    }

    /**
     * 作業者管理の計画
     * 
     * @param item
     * @param displayedStatusInfoEntity 
     */
    public void createOrganizationPlanData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity status, List<ScheduleInfoEntity> scheduleData) {
        this.kanbanId = item.getKanbanId();
        this.workKanbanId = item.getWorkKanbanId();
        this.organizationId = item.getOrganizationId();
        this.workKanbanStatus = item.getWorkKanbanStatus();
        this.title1 = item.getKanbanName();
        this.title2 = item.getModelName();
        this.title3 = item.getWorkflowName();
        //予定件数取得
        if(Objects.nonNull(scheduleData) && scheduleData.size() > 0){
            this.title4 = scheduleData.get(0).getScheduleName();
            this.title5 = dateDataFormat.format(scheduleData.get(0).getScheduleFromDate()) 
                            + "-" + dateDataFormat.format(scheduleData.get(0).getScheduleToDate());

            if(scheduleData.size() > 1 ){
                this.title4 = this.title4.concat("...");
                this.title5 = this.title5.concat("...");
            }
        }
        this.workName = item.getWorkName();
        this.startTime = item.getPlanStartTime();
        this.endTime = item.getPlanEndTime();
        this.fontColor = status.getFontColor();
        this.backgraundColor = status.getBackColor();
        this.frameColor = defaultFrameColor;
        this.isBlink = false;
    }

    /**
     * 作業者管理の実績
     * 
     * @param item
     * @param displayedStatusInfoEntity 
     */

    /**
     * 作業者管理の実績
     * @param item
     * @param workKanban
     * @param displayedStatusInfoEntity
     */
    public void createOrganizationActualData(KanbanTopicInfoEntity item, DisplayedStatusInfoEntity status) {
        this.kanbanId = item.getKanbanId();
        this.title1 = item.getWorkflowName();
        this.title2 = item.getWorkName();
        this.startTime = item.getActualStartTime();
        this.endTime = item.getActualEndTime();
        this.fontColor = status.getFontColor();
        this.backgraundColor = status.getBackColor();
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

    public String getTitle4() {
        return title4;
    }

    public void setTitle4(String title4) {
        this.title4 = title4;
    }
    
    public String getTitle5() {
        return title5;
    }

    public void setTitle5(String title5) {
        this.title5 = title5;
    }
    
    public String getWorkName() {
        return workName;
    }
    
    public void setWorkName(String workName) {
        this.workName = workName;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Long getWorkKanbanId() {
        return workKanbanId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }
    
    public KanbanStatusEnum getWorkKanbanStatus() {
        return workKanbanStatus;
    }
    
    public void setWorkKanbanStatus(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
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
        return "UnitAgendaItemEntity{" + "title1=" + title1 + ", title2=" + title2 + ", title3=" + title3 + ", taktTime=" + taktTime + ", startTime=" + startTime + ", endTIme=" + endTime + ", fontColor=" + fontColor + ", backgraundColor=" + backgraundColor + ", frameColor=" + frameColor + ", isBlink=" + isBlink + '}';
    }
}
