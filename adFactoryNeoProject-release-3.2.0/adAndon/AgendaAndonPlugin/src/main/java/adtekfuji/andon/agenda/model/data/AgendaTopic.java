/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.Date;
import java.util.Objects;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 計画実績トピック
 *
 * @author s-heya
 */
public class AgendaTopic {
    private Long kanbanId;
    private Long workKanbanId;
    private Long organizationId;
    private String title1;
    private String title2;
    private String title3;
    private KanbanStatusEnum kanbanStatus;
    private KanbanStatusEnum workKanbanStatus;
    private Integer taktTime;
    private Date planStartTime;
    private Date planEndTime;
    private Date actualStartTime;
    private Date actualEndTime;
    private String fontColor;
    private String backColor;
    private String backColor2 = "#80808"; // 進捗バーの実施前の色
    private boolean blink;
    private Long sumTimes;
    private int row = -1;
    private Double progress=null;
    private KanbanStatusEnum actualStatus;
    private Boolean isIndirectData = false;
    private String reason;

    public AgendaTopic(Long kanbanId, Long workKanbanId, Long organizationId, String title1, String title2, String title3, KanbanStatusEnum kanbanStatus, KanbanStatusEnum workKanbanStatus, Integer taktTime, Date planStartTime, Date planEndTime, Date actualStartTime, Date actualEndTime, String fontColor, String backColor, Long sumTimes, Boolean isIndirectData) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.organizationId = organizationId;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.kanbanStatus = kanbanStatus;
        this.workKanbanStatus = workKanbanStatus;
        this.taktTime = taktTime;
        this.planStartTime = planStartTime;
        this.planEndTime = planEndTime;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.sumTimes = sumTimes;
        this.isIndirectData = isIndirectData;
    }
    
    /**
     * コンストラクタ
     * 
     * @param kanbanId カンバンID
     * @param workKanbanId 工程カンバンID
     * @param title1 
     * @param title2
     * @param title3
     * @param kanbanStatus カンバンステータス
     * @param workKanbanStatus 工程カンバンステータス
     * @param taktTime タクトタイム
     * @param planStartTime 予定開始時間
     * @param planEndTime 予定完了時間
     * @param actualStartTime 実績開始時間
     * @param actualEndTime 実績完了時間
     * @param fontColor フォント色
     * @param backColor 背景色
     * @param sumTimes 作業時間
     * @param dispOrder  
     * @param isIndirectData 間接作業データかどうか
     */
    public AgendaTopic(Long kanbanId, Long workKanbanId, Long organizationId, String title1, String title2, String title3, KanbanStatusEnum kanbanStatus, KanbanStatusEnum workKanbanStatus, Integer taktTime, Date planStartTime, Date planEndTime, Date actualStartTime, Date actualEndTime, String fontColor, String backColor, Long sumTimes, Integer dispOrder, Boolean isIndirectData) {
        this.kanbanId = kanbanId;
        this.workKanbanId = workKanbanId;
        this.organizationId = organizationId;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.kanbanStatus = kanbanStatus;
        this.workKanbanStatus = workKanbanStatus;
        this.taktTime = taktTime;
        this.planStartTime = planStartTime;
        this.planEndTime = planEndTime;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
        this.fontColor = fontColor;
        this.backColor = backColor;
        this.sumTimes = sumTimes;
        this.row = Objects.nonNull(dispOrder) ? (dispOrder % 10000) - 1 : -1;
        this.progress=null;
        this.isIndirectData = isIndirectData;
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

    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
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

    public KanbanStatusEnum getKanbanStatus() {
        return kanbanStatus;
    }

    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        this.kanbanStatus = kanbanStatus;
    }

    public KanbanStatusEnum getWorkKanbanStatus() {
        return workKanbanStatus;
    }

    public void setWorkKanbanStatus(KanbanStatusEnum workKanbanStatus) {
        this.workKanbanStatus = workKanbanStatus;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public boolean isBlink() {
        return blink;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

    public Long getSumTimes() {
        return sumTimes;
    }

    public void setSumTimes(Long sumTimes) {
        this.sumTimes = sumTimes;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * 行番号を取得する。
     * 
     * @return 行番号
     */
    public Integer getRow() {
        return row;
    }

    /**
     * 行番号を設定する。
     * 
     * @param row 行番号
     */
    public void setRow(int row) {
        this.row = row;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getBackColor2() {
        return backColor2;
    }

    public void setBackColor2(String backColor2) {
        this.backColor2 = backColor2;
    }

    /**
     * 実績ステータスを取得する。
     * 
     * @return 実績ステータス 
     */
    public KanbanStatusEnum getActualStatus() {
        return actualStatus;
    }

    /**
     * 実績ステータスを設定する。
     * 
     * @param actualStatus 実績ステータス
     */
    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    /**
     * 実績を持っているかどうかを返す。
     *
     * @return
     */
    public boolean hasActual() {
        return Objects.nonNull(this.getActualStartTime());
    }

    /**
     * 間接作業データかどうかを取得する。
     * 
     * @return true: 間接作業データ、false: 直接作業データ(カンバン)
     */
    public Boolean getIsIndirectData() {
        return isIndirectData;
    }

    /**
     * 間接作業データかどうかを設定する。
     * 
     * @param isIndirectData true: 間接作業データ、false: 直接作業データ(カンバン)
     */
    public void setIsIndirectData(Boolean isIndirectData) {
        this.isIndirectData = isIndirectData;
    }

    /**
     * 理由を取得する。
     * 
     * @return 理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 理由を設定する。
     * 
     * @param reason 理由 
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.kanbanId);
        hash = 17 * hash + Objects.hashCode(this.workKanbanId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgendaTopic other = (AgendaTopic) obj;
        if (!Objects.equals(this.kanbanId, other.kanbanId)) {
            return false;
        }
        return Objects.equals(this.workKanbanId, other.workKanbanId);
    }

    @Override
    public String toString() {
        return "AgendaTopic{" + "title1=" + title1 + ", title2=" + title2 + ", title3=" + title3 + ", kanbanStatus=" + kanbanStatus + ", workKanbanStatus=" + workKanbanStatus +
                ", taktTime=" + taktTime + ", planStartTime=" + planStartTime + ", planEndTime=" + planEndTime + ", actualStartTime=" + actualStartTime + ", actualEndTime=" + actualEndTime +
                ", fontColor=" + fontColor + ", backColor=" + backColor + ", isBlink=" + blink + ", row=" + row + ", isIndirectData=" + isIndirectData +'}';
    }
}
