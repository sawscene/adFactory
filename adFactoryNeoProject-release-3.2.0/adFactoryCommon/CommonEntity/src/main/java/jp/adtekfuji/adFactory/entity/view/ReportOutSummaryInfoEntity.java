/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.view;

import adtekfuji.utility.StringTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;

/**
 * 工程実績出力情報
 *
 * @author shizuka.hirano
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reportOutSummary")
public class ReportOutSummaryInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workKanbanIdProperty;
    private StringProperty kanbanNameProperty;
    private StringProperty workflowNameProperty;
    private StringProperty workNameProperty;
    private IntegerProperty workflowRevProperty;
    private ObjectProperty<Date> startDatetimeProperty;
    private ObjectProperty<Date> compDatetimeProperty;
    private StringProperty taktTimeProperty;
    private StringProperty sumTimesProperty;
    private StringProperty interruptTimesProperty;
    private ObjectProperty<KanbanStatusEnum> workStatusProperty;
    private StringProperty fkDelayReasonProperty;
    private StringProperty productionNumberProperty;
    private StringProperty modelNameProperty;

    private Long workKanbanId;      // 工程カンバンID
    private String kanbanName;      // カンバン名
    private String workflowName;    // 工程順名
    private Integer workflowRev;    // 工程順版数
    private String workName;        // 工程名
    private Date startDatetime;     // 開始日時
    private Date compDatetime;      // 完了日時
    private Integer taktTime;       // タクトタイム[ms]
    private Long sumTimes;          // 作業累計時間[ms]
    private Integer interruptTimes; // 中断時間[ms]
    private KanbanStatusEnum workStatus;// 工程ステータス
    private String delayReason;     // 遅延理由
    private String productionNumber;// 製造番号
    private String modelName;       // モデル名
    private Map<String, Integer> interruptReasonTimes; // 中断理由
    private Map<String, Integer> compCnt; // 完了数
    private Map<String, String> actualAddInfo; // 追加情報
    private List<ActualPropertyEntity> propertyCollection = null; // 追加情報
    private String organizationName; // 組織名
    private String equipmentName;// 設備名


    /**
     * コンストラクタ
     */
    public ReportOutSummaryInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKanban 工程カンバン
     */
    public ReportOutSummaryInfoEntity(WorkKanbanInfoEntity workKanban) {
        this.workKanbanId = workKanban.getWorkKanbanId();
        this.kanbanName = workKanban.getKanbanName();
        this.workflowName = workKanban.getWorkflowName();
        this.workName = workKanban.getWorkName();
        this.taktTime = workKanban.getTaktTime();
        this.sumTimes = workKanban.getSumTimes();
        this.workStatus = workKanban.getWorkStatus();
        this.startDatetime = workKanban.getActualStartTime();
        this.compDatetime = workKanban.getActualCompTime();
    }

    /**
     * 工程カンバンIDプロパティを取得する。
     *
     * @return 工程カンバンIDプロパティ
     */
    public LongProperty workKanbanIdProperty() {
        if (Objects.isNull(this.workKanbanIdProperty)) {
            this.workKanbanIdProperty = new SimpleLongProperty(this.workKanbanId);
        }
        return this.workKanbanIdProperty;
    }

    /**
     * カンバン名プロパティを取得する。
     *
     * @return カンバン名
     */
    public StringProperty kanbanNameProperty() {
        if (Objects.isNull(this.kanbanNameProperty)) {
            this.kanbanNameProperty = new SimpleStringProperty(this.kanbanName);
        }
        return this.kanbanNameProperty;
    }

    /**
     * 工程順名プロパティを取得する。
     *
     * @return 工程順名
     */
    public StringProperty workflowNameProperty() {
        if (Objects.isNull(this.workflowNameProperty)) {
            this.workflowNameProperty = new SimpleStringProperty(this.workflowName);
        }
        return this.workflowNameProperty;
    }

    /**
     * 工程順の版数プロパティを取得する。
     *
     * @return 版数
     */
    public IntegerProperty workflowRevProperty() {
        if (Objects.isNull(this.workflowRevProperty) && Objects.nonNull(this.workflowRev)) {
            this.workflowRevProperty = new SimpleIntegerProperty(this.workflowRev);
        }
        return this.workflowRevProperty;
    }

    /**
     * 工程名プロパティを取得する。
     *
     * @return 工程名
     */
    public StringProperty workNameProperty() {
        if (Objects.isNull(this.workNameProperty)) {
            this.workNameProperty = new SimpleStringProperty(this.workName);
        }
        return this.workNameProperty;
    }

    /**
     * 開始予定日時プロパティを取得する。
     *
     * @return 開始予定日時
     */
    public ObjectProperty<Date> startDatetimeProperty() {
        if (Objects.isNull(this.startDatetimeProperty)) {
            this.startDatetimeProperty = new SimpleObjectProperty<>(this.startDatetime);
        }
        return this.startDatetimeProperty;
    }

    /**
     * 完了予定日時プロパティを取得する。
     *
     * @return 完了予定日時
     */
    public ObjectProperty<Date> compDatetimeProperty() {
        if (Objects.isNull(this.compDatetimeProperty)) {
            this.compDatetimeProperty = new SimpleObjectProperty<>(this.compDatetime);
        }
        return this.compDatetimeProperty;
    }

    /**
     * タクトタイム[ms]プロパティを取得する。
     *
     * @return タクトタイム[ms]
     */
    public StringProperty taktTimeProperty() {
        if (Objects.isNull(this.taktTimeProperty)) {
            this.taktTimeProperty = new SimpleStringProperty(StringTime.convertMillisToStringTime(this.taktTime));
        }
        return this.taktTimeProperty;
    }

    /**
     * 作業累計時間[ms]プロパティを取得する。
     *
     * @return 作業累計時間[ms]
     */
    public StringProperty sumTimesProperty() {
        if (Objects.isNull(this.sumTimesProperty)) {
            this.sumTimesProperty = new SimpleStringProperty(StringTime.convertMillisToStringTime(this.sumTimes));
        }
        return this.sumTimesProperty;
    }

    /**
     * 作業累計時間[ms]プロパティを取得する。
     *
     * @return 作業累計時間[ms]
     */
    public StringProperty InterruptTimesProperty() {
        if (Objects.isNull(this.interruptTimesProperty)) {
            this.interruptTimesProperty = new SimpleStringProperty(StringTime.convertMillisToStringTime(this.interruptTimes));
        }
        return this.interruptTimesProperty;
    }

    /**
     * 工程ステータスプロパティを取得する。
     *
     * @return 工程ステータス
     */
    public ObjectProperty<KanbanStatusEnum> workStatusProperty() {
        if (Objects.isNull(this.workStatusProperty)) {
            this.workStatusProperty = new SimpleObjectProperty<>(this.workStatus);
        }
        return this.workStatusProperty;
    }

    /**
     * 遅延理由プロパティを取得する。
     *
     * @return 遅延理由
     */
    public StringProperty fkDelayReasonProperty() {
        if (Objects.isNull(this.fkDelayReasonProperty)) {
            this.fkDelayReasonProperty = new SimpleStringProperty(this.delayReason);
        }
        return this.fkDelayReasonProperty;
    }

    /**
     * 製造番号プロパティを取得する
     *
     * @return 製造番号
     */
    public StringProperty productionNumberProperty() {
        if (Objects.isNull(this.productionNumberProperty) && Objects.nonNull(this.productionNumber)) {
            this.productionNumberProperty = new SimpleStringProperty(this.productionNumber);
        }
        return this.productionNumberProperty;
    }

    /**
     * モデル名プロパティを取得する。
     *
     * @return モデル名
     */
    public StringProperty modelNameProperty() {
        if (Objects.isNull(this.modelNameProperty)) {
            this.modelNameProperty = new SimpleStringProperty(this.modelName);
        }
        return this.modelNameProperty;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getWorkKanbanId() {
        if (Objects.nonNull(this.workKanbanIdProperty)) {
            return this.workKanbanIdProperty.get();
        }
        return this.workKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param workKanbanId 工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        if (Objects.nonNull(this.workKanbanIdProperty)) {
            this.workKanbanIdProperty.set(workKanbanId);
        } else {
            this.workKanbanId = workKanbanId;
        }
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        if (Objects.nonNull(this.kanbanNameProperty)) {
            return this.kanbanNameProperty.get();
        }
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        if (Objects.nonNull(this.kanbanNameProperty)) {
            this.kanbanNameProperty.set(kanbanName);
        } else {
            this.kanbanName = kanbanName;
        }
    }

    /**
     * 工程順名を取得する。
     *
     * @return 工程順名
     */
    public String getWorkflowName() {
        if (Objects.nonNull(this.workflowNameProperty)) {
            return this.workflowNameProperty.get();
        }
        return this.workflowName;
    }

    /**
     * 工程順名を設定する。
     *
     * @param workflowName 工程順名
     */
    public void setWorkflowName(String workflowName) {
        if (Objects.nonNull(this.workflowNameProperty)) {
            this.workflowNameProperty.set(workflowName);
        } else {
            this.workflowName = workflowName;
        }
    }

    /**
     * 工程順の版数を取得する。
     *
     * @return 工程順の版数
     */
    public Integer getWorkflowRev() {
        if (Objects.nonNull(this.workflowRevProperty)) {
            return this.workflowRevProperty.get();
        }
        return this.workflowRev;
    }

    /**
     * 工程順の版数を設定する。
     *
     * @param workflowRev 工程順の版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        if (Objects.nonNull(this.workflowRevProperty)) {
            this.workflowRevProperty.set(workflowRev);
        } else {
            this.workflowRev = workflowRev;
        }
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        if (Objects.nonNull(this.workNameProperty)) {
            return this.workNameProperty.get();
        }
        return workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        if (Objects.nonNull(this.workNameProperty)) {
            this.workNameProperty.set(workName);
        } else {
            this.workName = workName;
        }
    }

    /**
     * 開始予定日時を取得する。
     *
     * @return 開始予定日時
     */
    public Date getStartDatetime() {
        if (Objects.nonNull(this.startDatetimeProperty)) {
            return this.startDatetimeProperty.get();
        }
        return this.startDatetime;
    }

    /**
     * 開始予定日時を設定する。
     *
     * @param startDatetime 開始予定日時
     */
    public void setStartDatetime(Date startDatetime) {
        if (Objects.nonNull(this.startDatetimeProperty)) {
            this.startDatetimeProperty.set(startDatetime);
        } else {
            this.startDatetime = startDatetime;
        }
    }

    /**
     * 完了予定日時を取得する。
     *
     * @return 完了予定日時
     */
    public Date getCompDatetime() {
        if (Objects.nonNull(this.compDatetimeProperty)) {
            return this.compDatetimeProperty.get();
        }
        return this.compDatetime;
    }

    /**
     * 完了予定日時を設定する。
     *
     * @param compDatetime 完了予定日時
     */
    public void setCompDatetime(Date compDatetime) {
        if (Objects.nonNull(this.compDatetimeProperty)) {
            this.compDatetimeProperty.set(compDatetime);
        } else {
            this.compDatetime = compDatetime;
        }
    }

    /**
     * タクトタイム[ms]を取得する。
     *
     * @return タクトタイム[ms]
     */
    public Integer getTaktTime() {
        if (Objects.nonNull(this.taktTimeProperty)) {
            return Long.valueOf(StringTime.convertStringTimeToMillis(this.taktTimeProperty.get())).intValue();
        }
        return this.taktTime;
    }

    /**
     * タクトタイム[ms]を設定する。
     *
     * @param taktTime タクトタイム[ms]
     */
    public void setTaktTime(Integer taktTime) {
        if (Objects.nonNull(this.taktTimeProperty)) {
            this.taktTimeProperty.set(StringTime.convertMillisToStringTime(taktTime));
        } else {
            this.taktTime = taktTime;
        }
    }

    /**
     * 作業累計時間[ms]を取得する。
     *
     * @return 作業累計時間[ms]
     */
    public Long getSumTimes() {
        if (Objects.nonNull(this.sumTimesProperty)) {
            return StringTime.convertStringTimeToMillis(this.sumTimesProperty.get());
        }
        return this.sumTimes;
    }

    /**
     * 作業累計時間[ms]を設定する。
     *
     * @param value 作業累計時間[ms]
     */
    public void setSumTimes(Long value) {
        if (Objects.nonNull(this.sumTimesProperty)) {
            this.taktTimeProperty.set(StringTime.convertMillisToStringTime(value));
        } else {
            this.sumTimes = value;
        }
    }

    /**
     * 中断時間[ms]を取得する。
     *
     * @return 中断時間[ms]
     */
    public Integer getInterruptTimes() {
        if (Objects.nonNull(this.interruptTimesProperty)) {
            return Long.valueOf(StringTime.convertStringTimeToMillis(this.interruptTimesProperty.get())).intValue();
        }
        return this.interruptTimes;
    }

    /**
     * 中断時間[ms]を設定する。
     *
     * @param value 中断時間[ms]
     */
    public void setInterruptTimes(Integer value) {
        if (Objects.nonNull(this.interruptTimes)) {
            this.interruptTimesProperty.set(StringTime.convertMillisToStringTime(value));
        } else {
            this.interruptTimes = value;
        }
    }

    /**
     * 工程ステータスを取得する。
     *
     * @return 工程ステータス
     */
    public KanbanStatusEnum getWorkStatus() {
        if (Objects.nonNull(this.workStatusProperty)) {
            return this.workStatusProperty.get();
        }
        return this.workStatus;
    }

    /**
     * 工程ステータスを設定する。
     *
     * @param workStatus 工程ステータス
     */
    public void setWorkStatus(KanbanStatusEnum workStatus) {
        if (Objects.nonNull(this.workStatusProperty)) {
            this.workStatusProperty.set(workStatus);
        } else {
            this.workStatus = workStatus;
        }
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        if (Objects.nonNull(this.fkDelayReasonProperty)) {
            return this.fkDelayReasonProperty.get();
        }
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        if (Objects.nonNull(this.fkDelayReasonProperty)) {
            this.fkDelayReasonProperty.set(delayReason);
        } else {
            this.delayReason = delayReason;
        }
    }

    /**
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNumber() {
        if (Objects.nonNull(this.productionNumberProperty)) {
            return this.productionNumberProperty.get();
        }
        return this.productionNumber;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNumber 製造番号
     */
    public void setProductionNumber(String productionNumber) {
        if (Objects.nonNull(this.productionNumberProperty)) {
            this.productionNumberProperty.set(productionNumber);
        } else {
            this.productionNumber = productionNumber;
        }
    }

    /**
     * モデル名を取得する。
     *
     * @return モデル名
     */
    public String getModelName() {
        if (Objects.nonNull(this.modelNameProperty)) {
            return this.modelNameProperty.get();
        }
        return this.modelName;
    }

    /**
     * モデル名を設定する。
     *
     * @param modelName モデル名
     */
    public void setModelName(String modelName) {
        if (Objects.nonNull(this.modelNameProperty)) {
            this.modelNameProperty.set(modelName);
        } else {
            this.modelName = modelName;
        }
    }

    public Map<String, Integer> getInterruptReasonTimes() {
        return this.interruptReasonTimes;
    }

    public void setInterruptReasonTimes(Map<String, Integer> interruptReasonTimes) {
        this.interruptReasonTimes = interruptReasonTimes;
    }

    /**
     * 項目名を指定して、中断時間の値を取得する。
     *
     * @param name 中断時間の項目名
     * @return 中断時間の値
     */
    public Integer getPropertyValue(String name) {
        if (Objects.nonNull(this.getInterruptReasonTimes())) {
            if (Objects.nonNull(getInterruptReasonTimes().get(name))) {
                return getInterruptReasonTimes().get(name);
            } else {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 完了数を取得する。 miyake
     *
     * @return 完了数
     */
    public Map<String, Integer> getCompCnt() {
        
        return this.compCnt;
    }

    /**
     * 完了数を設定する。 miyake
     *
     * @param 完了数
     */
    public void setCompCnt(Map<String, Integer> compCnt) {
            this.compCnt = compCnt;
    }

    /**
     * 追加情報マップを取得する。 miyake
     *
     * return 追加情報マップ
     */
    public Map<String, String> GetActualAddInfo() {
        return this.actualAddInfo;
    }
    
    /**
     * 追加情報マップを設定する。 miyake
     *
     * @param 追加情報マップ
     */
    public void setActualAddInfo(Map<String, String> addInfo) {
        this.actualAddInfo = addInfo;
    }

    /**
     * 組織名を取得する。
     * 
     * @return 組織名 
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * 組織名を設定する。
     * 
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * 設備名を取得する。
     * 
     * @return 設備名
     */
    public String getEquipmentName() {
        return equipmentName;
    }

    /**
     * 設備名を設定する。
     * 
     * @param equipmentName 設備名 
     */
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.workKanbanId);
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
        final ReportOutSummaryInfoEntity other = (ReportOutSummaryInfoEntity) obj;
        if (!Objects.equals(this.workKanbanId, other.workKanbanId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanInfoEntity{")
                .append("workKanbanId=").append(this.workKanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", workName=").append(this.workName)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", taktTime=").append(this.taktTime)
                .append(", sumTimes=").append(this.sumTimes)
                .append(", workStatus=").append(this.workStatus)
                .append(", delayReason=").append(this.delayReason)
                .append(", productionNumber=").append(this.productionNumber)
                .append(", modelName=").append(this.modelName)
                .append(", interruptReasonTimes=").append(this.interruptReasonTimes)
                .append(", compCnt=").append(this.compCnt)              //miyake
                .append(", actualAddInfo=").append(this.actualAddInfo)  //miyake
                .append("}")
                .toString();
    }
 
}
