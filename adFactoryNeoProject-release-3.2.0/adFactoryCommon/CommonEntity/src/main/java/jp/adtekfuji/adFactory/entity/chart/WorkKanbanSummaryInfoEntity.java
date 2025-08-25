/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.chart;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 工程カンバン集計情報
 *
 * @author kentarou.suzuki
 */
@XmlRootElement(name = "workKanbanSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkKanbanSummaryInfoEntity implements Serializable {

    /**
     * カンバンID
     */
    @XmlElement
    private Long kanbanId;

    /**
     * カンバン名
     */
    @XmlElement
    private String kanbanName;

    /**
     * 工程順ID
     */
    @XmlElement
    private String workflowId;

    /**
     * 工程順名
     */
    @XmlElement
    private String workflowName;

    /**
     * 工程ID
     */
    @XmlElement
    private String workId;

    /**
     * 工程名
     */
    @XmlElement
    private String workName;

    /**
     * 作業時間[ms]
     */
    @XmlElement
    private Integer workTimes;

    /**
     * 開始日時(実績)
     */
    @XmlElement
    private Date actualStartTime;

    /**
     * 完了日時(実績)
     */
    @XmlElement
    private Date actualEndTime;

    /**
     * ロット数量
     */
    @XmlElement
    private Long LotQuantity;

    /**
     * 工程の版数
     */
    @XmlElement
    private Integer workRev;

    /**
     * コンストラクタ
     */
    public WorkKanbanSummaryInfoEntity() {
    }

    /**
     * カンバンIDを取得する。
     * 
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     * 
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * カンバン名を取得する。
     * 
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     * 
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * 工程順IDを取得する。
     * 
     * @return 工程順ID
     */
    public String getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     * 
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 工程順名を取得する。
     * 
     * @return 工程順名
     */
    public String getWorkflowName() {
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     * 
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 工程IDを取得する。
     * 
     * @return 工程ID
     */
    public String getWorkId() {
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     * 
     * @param workId 工程ID
     */
    public void setWorkId(String workId) {
        this.workId = workId;
    }

    /**
     * 工程名を取得する。
     * 
     * @return 工程名
     */
    public String getWorkName() {
        return this.workName;
    }

    /**
     * 工程名を設定する。
     * 
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        this.workName = workName;
    }

    /**
     * 作業時間[ms]を取得する。
     * 
     * @return 作業時間[ms]
     */
    public Integer getWorkTimes() {
        return this.workTimes;
    }

    /**
     * 作業時間[ms]を設定する。
     * 
     * @param workTimes 作業時間[ms]
     */
    public void setWorkTimes(Integer workTimes) {
        this.workTimes = workTimes;
    }

    /**
     * 開始日時(実績)を取得する。
     * 
     * @return 開始日時(実績)
     */
    public Date getActualStartTime() {
        return this.actualStartTime;
    }

    /**
     * 開始日時(実績)を設定する。
     * 
     * @param actualStartTime 開始日時(実績)
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * 完了日時(実績)を取得する。
     * 
     * @return 完了日時(実績)
     */
    public Date getActualEndTime() {
        return this.actualEndTime;
    }

    /**
     * 完了日時(実績)を設定する。
     * 
     * @param actualEndTime 完了日時(実績)
     */
    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    /**
     * ロット数量を取得する。
     * 
     * @return ロット数量
     */
    public Long getLotQuantity() {
        return this.LotQuantity;
    }

    /**
     * ロット数量を設定する。
     * 
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Long lotQuantity) {
        this.LotQuantity = lotQuantity;
    }

    /**
     * 工程の版数を取得する。
     *
     * @return 工程の版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 工程の版数を設定する。
     *
     * @param workRev 工程の版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
    }

    /**
     * 表示名を取得する。
     *
     * @return 表示名(工程名 : 版数)
     */
    public String getDisplayWorkName() {
        StringBuilder name = new StringBuilder(this.workName);
        if (Objects.nonNull(this.workRev)) {
            name.append(" : ").append(this.workRev);
        }
        return name.toString();
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "WorkKanbanSummaryInfoEntity{" +
                "kanbanId=" + kanbanId +
                ", kanbanName='" + kanbanName + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", workflowName='" + workflowName + '\'' +
                ", workId='" + workId + '\'' +
                ", workName='" + workName + '\'' +
                ", workTimes=" + workTimes +
                ", actualStartTime=" + actualStartTime +
                ", actualEndTime=" + actualEndTime +
                ", LotQuantity=" + LotQuantity +
                '}';
    }
}
