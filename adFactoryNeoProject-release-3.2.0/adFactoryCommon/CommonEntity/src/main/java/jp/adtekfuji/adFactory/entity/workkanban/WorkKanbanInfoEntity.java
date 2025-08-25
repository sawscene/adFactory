/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.workkanban;

import adtekfuji.utility.StringTime;

import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 工程カンバン情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workKanban")
public class WorkKanbanInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workKanbanIdProperty;
    private LongProperty parentIdProperty;
    private LongProperty fkKanbanIdProperty;
    private StringProperty kanbanNameProperty;
    private LongProperty fkWorkflowIdProperty;
    private StringProperty workflowNameProperty;
    private LongProperty fkWorkIdProperty;
    private StringProperty workNameProperty;
    private BooleanProperty separateWorkFlagProperty;
    private BooleanProperty implementFlagProperty;
    private BooleanProperty skipFlagProperty;
    private ObjectProperty<Date> startDatetimeProperty;
    private ObjectProperty<Date> compDatetimeProperty;
    private StringProperty taktTimeProperty;
    private StringProperty sumTimesProperty;
    private LongProperty fkUpdatePersonIdProperty;
    private ObjectProperty<Date> updateDatetimeProperty;
    private ObjectProperty<KanbanStatusEnum> workStatusProperty;
    private LongProperty fkInterruptReasonIdProperty;
    private LongProperty fkDelayReasonIdProperty;
    private IntegerProperty workKanbanOrderProperty;

    @XmlElement(required = true)
    private Long workKanbanId;// 工程カンバンID
    @XmlElement()
    private Long parentId;// 親ID
    @XmlElement()
    private Long fkKanbanId;// カンバンID
    @XmlElement()
    private String kanbanName;// カンバン名
    @XmlElement()
    private Long fkWorkflowId;// 工程順ID
    @XmlElement()
    private String workflowName;// 工程順名

    @XmlElement()
    private Long fkWorkId;// 工程ID
    @XmlElement()
    private String workName;// 工程名
    @XmlElement()
    private Boolean separateWorkFlag;// 追加工程フラグ
    @XmlElement()
    private Boolean implementFlag;// 実施フラグ
    @XmlElement()
    private Boolean skipFlag;// スキップフラグ
    @XmlElement()
    private Date startDatetime;// 開始予定日時
    @XmlElement()
    private Date compDatetime;// 完了予定日時
    @XmlElement()
    private Integer taktTime;// タクトタイム[ms]
    @XmlElement()
    private Long sumTimes;// 作業累計時間[ms]
    @XmlElement()
    private Long fkUpdatePersonId;// 更新者(組織ID)
    @XmlElement()
    private Date updateDatetime;// 更新日時
    @XmlElement()
    private KanbanStatusEnum workStatus;// 工程ステータス
    @XmlElement()
    private Long fkInterruptReasonId;// 中断理由ID
    @XmlElement()
    private Long fkDelayReasonId;// 遅延理由ID
    @XmlElement()
    private Integer workKanbanOrder;// 表示順
    @XmlElement()
    private Boolean needActualOutputFlag;// 要実績出力フラグ
    @XmlElement()
    private Date actualStartTime;// 開始日時(実績)
    @XmlElement()
    private Date actualCompTime;// 完了日時(実績)

    @XmlTransient
    private List<WorkKanbanPropertyInfoEntity> propertyCollection = null;// 工程カンバンプロパティ一覧

    @XmlElementWrapper(name = "equipments")
    @XmlElement(name = "equipment")
    private List<Long> equipmentCollection;// 設備ID一覧

    @XmlElementWrapper(name = "equipment_identifies")
    @XmlElement(name = "equipment_identify")
    private List<String> equipmentIdentifyCollection; // 設備識別名一覧

    @XmlElementWrapper(name = "organizations")
    @XmlElement(name = "organization")
    private List<Long> organizationCollection;// 組織ID一覧

    @XmlElementWrapper(name = "organization_identifies")
    @XmlElement(name = "organization_identify")
    private List<String> organizationIdentifyCollection; // 設備識別名一覧

    @XmlElement()
    private Integer serialNumber;// シリアル番号
    @XmlElement()
    private Boolean syncWork;// 同時作業フラグ
    @XmlElement()
    private Integer actualNum1;// A品実績数
    @XmlElement()
    private Integer actualNum2;// B品実績数
    @XmlElement()
    private Integer actualNum3;// C品実績数

    @XmlElement()
    private String workKanbanAddInfo;// 追加情報(JSON)
    @XmlElement()
    private String serviceInfo;// サービス情報(JSON)

    @XmlElement()
    private Long lastActualId;// 最終実績ID

    private Integer workflowRev;                    // 工程順版数
    /**
     * コンストラクタ
     */
    public WorkKanbanInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workKanbanId 工程カンバンID
     * @param parentId 親ID
     * @param fkKanbanId カンバンID
     * @param fkWorkflowId 工程順ID
     * @param fkWorkId 工程ID
     * @param workName 工程名
     */
    public WorkKanbanInfoEntity(Long workKanbanId, Long parentId, Long fkKanbanId, Long fkWorkflowId, Long fkWorkId, String workName) {
        this.workKanbanId = workKanbanId;
        this.parentId = parentId;
        this.fkKanbanId = fkKanbanId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.workName = workName;
    }

    /**
     * 工程カンバンプロパティ一覧プロパティを取得する。
     *
     * @return 工程カンバンプロパティ一覧
     */
    public LongProperty workKanbanIdProperty() {
        if (Objects.isNull(this.workKanbanIdProperty)) {
            this.workKanbanIdProperty = new SimpleLongProperty(this.workKanbanId);
        }
        return this.workKanbanIdProperty;
    }

    /**
     * 親IDプロパティを取得する。
     *
     * @return 親ID
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentIdProperty;
    }

    /**
     * カンバンIDプロパティを取得する。
     *
     * @return カンバンID
     */
    public LongProperty fkKanbanIdProperty() {
        if (Objects.isNull(this.fkKanbanIdProperty)) {
            this.fkKanbanIdProperty = new SimpleLongProperty(this.fkKanbanId);
        }
        return this.fkKanbanIdProperty;
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
     * 工程順IDプロパティを取得する。
     *
     * @return 工程順ID
     */
    public LongProperty fkWorkflowIdProperty() {
        if (Objects.isNull(this.fkWorkflowIdProperty)) {
            this.fkWorkflowIdProperty = new SimpleLongProperty(this.fkWorkflowId);
        }
        return this.fkWorkflowIdProperty;
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
     * 工程IDプロパティを取得する。
     *
     * @return 工程ID
     */
    public LongProperty fkWorkIdProperty() {
        if (Objects.isNull(this.fkWorkIdProperty)) {
            this.fkWorkIdProperty = new SimpleLongProperty(this.fkWorkId);
        }
        return this.fkWorkIdProperty;
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
     * 追加工程フラグプロパティを取得する。
     *
     * @return 追加工程フラグ
     */
    public BooleanProperty separateWorkFlagProperty() {
        if (Objects.isNull(this.separateWorkFlagProperty)) {
            this.separateWorkFlagProperty = new SimpleBooleanProperty(this.separateWorkFlag);
        }
        return this.separateWorkFlagProperty;
    }

    /**
     * 実施フラグプロパティを取得する。
     *
     * @return 実施フラグ
     */
    public BooleanProperty implementFlagProperty() {
        if (Objects.isNull(this.implementFlagProperty)) {
            this.implementFlagProperty = new SimpleBooleanProperty(this.implementFlag);
        }
        return this.implementFlagProperty;
    }

    /**
     * スキップフラグプロパティを取得する。
     *
     * @return スキップフラグ
     */
    public BooleanProperty skipFlagProperty() {
        if (Objects.isNull(this.skipFlagProperty)) {
            this.skipFlagProperty = new SimpleBooleanProperty(this.skipFlag);
        }
        return this.skipFlagProperty;
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
     * 更新者(組織ID)プロパティを取得する。
     *
     * @return 更新者(組織ID)
     */
    public LongProperty fkUpdatePersonIdProperty() {
        if (Objects.isNull(this.fkUpdatePersonIdProperty)) {
            this.fkUpdatePersonIdProperty = new SimpleLongProperty(this.fkUpdatePersonId);
        }
        return this.fkUpdatePersonIdProperty;
    }

    /**
     * 更新日時プロパティを取得する。
     *
     * @return 更新日時
     */
    public ObjectProperty<Date> updateDatetimeProperty() {
        if (Objects.isNull(this.updateDatetimeProperty)) {
            this.updateDatetimeProperty = new SimpleObjectProperty<>(this.updateDatetime);
        }
        return this.updateDatetimeProperty;
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
     * 中断理由IDプロパティを取得する。
     *
     * @return 中断理由ID
     */
    public LongProperty fkInterruptReasonIdProperty() {
        if (Objects.isNull(this.fkInterruptReasonIdProperty)) {
            this.fkInterruptReasonIdProperty = new SimpleLongProperty(this.fkInterruptReasonId);
        }
        return this.fkInterruptReasonIdProperty;
    }

    /**
     * 遅延理由IDプロパティを取得する。
     *
     * @return 遅延理由ID
     */
    public LongProperty fkDelayReasonIdProperty() {
        if (Objects.isNull(this.fkDelayReasonIdProperty)) {
            this.fkDelayReasonIdProperty = new SimpleLongProperty(this.fkDelayReasonId);
        }
        return this.fkDelayReasonIdProperty;
    }

    /**
     * 表示順プロパティを取得する。
     *
     * @return 表示順
     */
    public IntegerProperty workKanbanOrderProperty() {
        if (Objects.isNull(this.workKanbanOrderProperty)) {
            this.workKanbanOrderProperty = new SimpleIntegerProperty(this.workKanbanOrder);
        }
        return this.workKanbanOrderProperty;
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
     * 親IDを取得する。
     *
     * @return 親ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * 親IDを設定する。
     *
     * @param parentId 親ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getFkKanbanId() {
        if (Objects.nonNull(this.fkKanbanIdProperty)) {
            return this.fkKanbanIdProperty.get();
        }
        return this.fkKanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param fkKanbanId カンバンID
     */
    public void setFkKanbanId(Long fkKanbanId) {
        if (Objects.nonNull(this.fkKanbanIdProperty)) {
            this.fkKanbanIdProperty.set(fkKanbanId);
        } else {
            this.fkKanbanId = fkKanbanId;
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
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getFkWorkflowId() {
        if (Objects.nonNull(this.fkWorkflowIdProperty)) {
            return this.fkWorkflowIdProperty.get();
        }
        return this.fkWorkflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param fkWorkflowId 工程順ID
     */
    public void setFkWorkflowId(Long fkWorkflowId) {
        if (Objects.nonNull(this.fkWorkflowIdProperty)) {
            this.fkWorkflowIdProperty.set(fkWorkflowId);
        } else {
            this.fkWorkflowId = fkWorkflowId;
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
     * 工程順版数
      * @return 工程順版数
     */
    public Integer getWorkflowRev() {
        return workflowRev;
    }

    /**
     * 工程順版数
     * @param workflowRev 工程順版数
     */
    public void setWorkflowRev(Integer workflowRev) {
        this.workflowRev = workflowRev;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getFkWorkId() {
        if (Objects.nonNull(this.fkWorkIdProperty)) {
            return this.fkWorkIdProperty.get();
        }
        return fkWorkId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param fkWorkId 工程ID
     */
    public void setFkWorkId(Long fkWorkId) {
        if (Objects.nonNull(this.fkWorkIdProperty)) {
            this.fkWorkIdProperty.set(fkWorkId);
        } else {
            this.fkWorkId = fkWorkId;
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
     * 追加工程フラグを取得する。
     *
     * @return 追加工程フラグ
     */
    public Boolean getSeparateWorkFlag() {
        if (Objects.nonNull(this.separateWorkFlagProperty)) {
            return this.separateWorkFlagProperty.get();
        }
        return this.separateWorkFlag;
    }

    /**
     * 追加工程フラグを設定する。
     *
     * @param separateWorkFlag 追加工程フラグ
     */
    public void setSeparateWorkFlag(Boolean separateWorkFlag) {
        if (Objects.nonNull(this.separateWorkFlagProperty)) {
            this.separateWorkFlagProperty.set(separateWorkFlag);
        } else {
            this.separateWorkFlag = separateWorkFlag;
        }
    }

    /**
     * 実施フラグを取得する。
     *
     * @return 実施フラグ
     */
    public Boolean getImplementFlag() {
        if (Objects.nonNull(this.implementFlagProperty)) {
            return this.implementFlagProperty.get();
        }
        return this.implementFlag;
    }

    /**
     * 実施フラグを設定する。
     *
     * @param implementFlag 実施フラグ
     */
    public void setImplementFlag(Boolean implementFlag) {
        if (Objects.nonNull(this.implementFlagProperty)) {
            this.implementFlagProperty.set(implementFlag);
        } else {
            this.implementFlag = implementFlag;
        }
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public Boolean getSkipFlag() {
        if (Objects.nonNull(this.skipFlagProperty)) {
            return this.skipFlagProperty.get();
        }
        return this.skipFlag;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param skipFlag スキップフラグ
     */
    public void setSkipFlag(Boolean skipFlag) {
        if (Objects.nonNull(this.skipFlagProperty)) {
            this.skipFlagProperty.set(skipFlag);
        } else {
            this.skipFlag = skipFlag;
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
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getFkUpdatePersonId() {
        if (Objects.nonNull(this.fkUpdatePersonIdProperty)) {
            return this.fkUpdatePersonIdProperty.get();
        }
        return this.fkUpdatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param fkUpdatePersonId 更新者(組織ID)
     */
    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        if (Objects.nonNull(this.fkUpdatePersonIdProperty)) {
            this.fkUpdatePersonIdProperty.set(fkUpdatePersonId);
        } else {
            this.fkUpdatePersonId = fkUpdatePersonId;
        }
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDatetime() {
        if (Objects.nonNull(this.updateDatetimeProperty)) {
            return this.updateDatetimeProperty.get();
        }
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        if (Objects.nonNull(this.updateDatetimeProperty)) {
            this.updateDatetimeProperty.set(updateDatetime);
        } else {
            this.updateDatetime = updateDatetime;
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
     * 中断理由IDを取得する。
     *
     * @return 中断理由ID
     */
    public Long getFkInterruptReasonId() {
        if (Objects.nonNull(this.fkInterruptReasonIdProperty)) {
            return this.fkInterruptReasonIdProperty.get();
        }
        return this.fkInterruptReasonId;
    }

    /**
     * 中断理由IDを設定する。
     *
     * @param fkInterruptReasonId 中断理由ID
     */
    public void setFkInterruptReasonId(Long fkInterruptReasonId) {
        if (Objects.nonNull(this.fkInterruptReasonIdProperty)) {
            this.fkInterruptReasonIdProperty.set(fkInterruptReasonId);
        } else {
            this.fkInterruptReasonId = fkInterruptReasonId;
        }
    }

    /**
     * 遅延理由IDを取得する。
     *
     * @return 遅延理由ID
     */
    public Long getFkDelayReasonId() {
        if (Objects.nonNull(this.fkDelayReasonIdProperty)) {
            return this.fkDelayReasonIdProperty.get();
        }
        return this.fkDelayReasonId;
    }

    /**
     * 遅延理由IDを設定する。
     *
     * @param fkDelayReasonId 遅延理由ID
     */
    public void setFkDelayReasonId(Long fkDelayReasonId) {
        if (Objects.nonNull(this.fkDelayReasonIdProperty)) {
            this.fkDelayReasonIdProperty.set(fkDelayReasonId);
        } else {
            this.fkDelayReasonId = fkDelayReasonId;
        }
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getWorkKanbanOrder() {
        if (Objects.nonNull(this.workKanbanOrderProperty)) {
            return this.workKanbanOrderProperty.get();
        }
        return this.workKanbanOrder;
    }

    /**
     * 表示順を設定する。
     *
     * @param workKanbanOrder 表示順
     */
    public void setWorkKanbanOrder(Integer workKanbanOrder) {
        if (Objects.nonNull(this.workKanbanOrderProperty)) {
            this.workKanbanOrderProperty.set(workKanbanOrder);
        } else {
            this.workKanbanOrder = workKanbanOrder;
        }
    }

    public void addProperty(WorkKanbanPropertyInfoEntity workKanbanPropertyInfoEntity) {
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if(Objects.isNull(this.propertyCollection)){
            if (StringUtils.isEmpty(this.getWorkKanbanAddInfo())) {
                this.setPropertyCollection(new ArrayList<>());
            } else {
                // 変換した結果をエンティティにセットする
                this.setPropertyCollection(JsonUtils.jsonToObjects(this.getWorkKanbanAddInfo(), WorkKanbanPropertyInfoEntity[].class));
            }
        }
        this.propertyCollection.add(workKanbanPropertyInfoEntity);
    }


    /**
     * 工程カンバンプロパティ一覧を取得する。
     *
     * @return 工程カンバンプロパティ一覧
     */
    public List<WorkKanbanPropertyInfoEntity> getPropertyCollection() {
        
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if(Objects.isNull(this.propertyCollection)){
            // 変換した結果をエンティティにセットする
            this.setPropertyCollection(JsonUtils.jsonToObjects(this.getWorkKanbanAddInfo(), WorkKanbanPropertyInfoEntity[].class));
        }
        return this.propertyCollection;
    }

    /**
     * 工程カンバンプロパティ一覧を設定する。
     *
     * @param propertyCollection 工程カンバンプロパティ一覧
     */
    public void setPropertyCollection(List<WorkKanbanPropertyInfoEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    /**
     * 設備ID一覧を取得する。
     *
     * @return 設備ID一覧
     */
    public List<Long> getEquipmentCollection() {
        return this.equipmentCollection;
    }

    /**
     * 設備ID一覧を設定する。
     *
     * @param equipmentCollection 設備ID一覧
     */
    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    /**
     * 設備を追加
     * @param equipmentIdentify 設備識別名
     * @return
     */
    public void addEquipmentIdentify(String equipmentIdentify) {
        if (Objects.isNull(this.equipmentIdentifyCollection)) {
            this.equipmentIdentifyCollection = new ArrayList<>();
        }
        this.equipmentIdentifyCollection.add(equipmentIdentify);
    }

    /**
     * 組織識別名一覧を取得
     * @return　組織識別名一覧
     */
    public List<String> getEquipmentIdentifyCollection() { return this.equipmentIdentifyCollection; }

    /**
     * 組織識別名を設定
     * @param equipmentIdentifyCollection　組織識別名を設定
     */
    public void setEquipmentIdentifyCollection(List<String> equipmentIdentifyCollection) {
        this.equipmentIdentifyCollection = equipmentIdentifyCollection;
    }


    public void addOrganizationIdentify(String organizationIdentify) {
        if (Objects.isNull(this.organizationIdentifyCollection)) {
            this.organizationIdentifyCollection = new ArrayList<>();
        }
        this.organizationIdentifyCollection.add(organizationIdentify);
    }
    /**
     * 組織ID一覧を取得する。
     *
     * @return 組織ID一覧
     */
    public List<Long> getOrganizationCollection() {
        return this.organizationCollection;
    }

    /**
     * 組織ID一覧を設定する。
     *
     * @param organizationCollection 組織ID一覧
     */
    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    /**
     * 組織識別名一覧取得
     * @return 組織識別名一覧
     */
    public List<String> getOrganizationIdentifyCollection() { return this.organizationIdentifyCollection; }

    /**
     * 組織識別名一覧
     * @param organizationIdentifyCollection 組織識別名一覧
     */
    public void setOrganizationIdentifyCollection(List<String> organizationIdentifyCollection) {
        this.organizationIdentifyCollection = organizationIdentifyCollection;
    }


    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public Integer getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNumber シリアル番号
     */
    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 同時作業フラグを取得する。
     *
     * @return 同時作業フラグ
     */
    public Boolean isSyncWork() {
        return this.syncWork;
    }

    /**
     * 同時作業フラグを設定する。
     *
     * @param syncWork 同時作業フラグ
     */
    public void setSyncWork(Boolean syncWork) {
        this.syncWork = syncWork;
    }

    /**
     * A品実績数を取得する。
     *
     * @return A品実績数
     */
    public Integer getActualNum1() {
        return this.actualNum1;
    }

    /**
     * A品実績数を設定する。
     *
     * @param actualNum1 A品実績数
     */
    public void setActualNum1(Integer actualNum1) {
        this.actualNum1 = actualNum1;
    }

    /**
     * B品実績数を取得する。
     *
     * @return B品実績数
     */
    public Integer getActualNum2() {
        return this.actualNum2;
    }

    /**
     * B品実績数を設定する。
     *
     * @param actualNum2 B品実績数
     */
    public void setActualNum2(Integer actualNum2) {
        this.actualNum2 = actualNum2;
    }

    /**
     * C品実績数を取得する。
     *
     * @return C品実績数
     */
    public Integer getActualNum3() {
        return this.actualNum3;
    }

    /**
     * C品実績数を設定する。
     *
     * @param actualNum3 C品実績数
     */
    public void setActualNum3(Integer actualNum3) {
        this.actualNum3 = actualNum3;
    }

    /**
     * 追加情報を取得する。
     *
     * @return 追加情報
     */
    public String getWorkKanbanAddInfo() {
        return this.workKanbanAddInfo;
    }

    /**
     * 追加情報を設定する。
     *
     * @param workKanbanAddInfo 追加情報
     */
    public void setWorkKanbanAddInfo(String workKanbanAddInfo) {
        this.workKanbanAddInfo = workKanbanAddInfo;
    }

    /**
     * サービス情報を取得する。
     *
     * @return サービス情報
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報を設定する。
     *
     * @param serviceInfo サービス情報
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
    
    /**
     * 要実績出力フラグを取得する。
     *
     * @return 要実績出力フラグ
     */
    public Boolean getNeedActualOutputFlag() {
        return this.needActualOutputFlag;
    }

    /**
     * 要実績出力フラグを設定する。
     *
     * @param needActualOutputFlag 要実績出力フラグ
     */
    public void setNeedActualOutputFlag(Boolean needActualOutputFlag) {
        this.needActualOutputFlag = needActualOutputFlag;
    }
    
    /** 
     * 
     * @return the actualStartTime
     */
    public Date getActualStartTime() {
        return actualStartTime;
    }

    /**
     * @param actualStartTime the actualStartTime to set
     */
    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    /**
     * @return the actualCompTime
     */
    public Date getActualCompTime() {
        return actualCompTime;
    }

    /**
     * @param actualCompTime the actualCompTime to set
     */
    public void setActualCompTime(Date actualCompTime) {
        this.actualCompTime = actualCompTime;
    }

    /**
     * 最終実績IDを取得する。
     *
     * @return 最終実績ID
     */
    public Long getLastActualId() {
        return this.lastActualId;
    }

    /**
     * 最終実績IDを設定する。
     *
     * @param lastActualId 最終実績ID
     */
    public void setLastActualId(Long lastActualId) {
        this.lastActualId = lastActualId;
    }

    /**
     * 内部変数を更新する。
     */
    public void updateMember() {
        this.workKanbanId = getWorkKanbanId();
        this.fkKanbanId = getFkKanbanId();
        this.fkWorkflowId = getFkWorkflowId();
        this.fkWorkId = getFkWorkId();
        this.separateWorkFlag = getSeparateWorkFlag();
        this.implementFlag = getImplementFlag();
        this.skipFlag = getSkipFlag();
        this.startDatetime = getStartDatetime();
        this.compDatetime = getCompDatetime();
        this.taktTime = getTaktTime();
        this.fkUpdatePersonId = getFkUpdatePersonId();
        this.updateDatetime = getUpdateDatetime();
        this.workStatus = getWorkStatus();
        this.fkInterruptReasonId = getFkInterruptReasonId();
        this.fkDelayReasonId = getFkDelayReasonId();
        this.workKanbanOrder = getWorkKanbanOrder();
        this.sumTimes = getSumTimes();
    }

    /**
     * 工程情報から工程カンバン情報を作成する。
     *
     * @param entity 工程情報
     * @return 工程カンバン情報
     */
    public static WorkKanbanInfoEntity convertWorkToWorkKanban(WorkInfoEntity entity) {
        WorkKanbanInfoEntity convertEntity = new WorkKanbanInfoEntity();
        convertEntity.setFkWorkId(entity.getWorkId());
        convertEntity.setWorkName(entity.getWorkName());
        convertEntity.setSeparateWorkFlag(true);
        convertEntity.setSkipFlag(false);
        convertEntity.setTaktTime(entity.getTaktTime());
        convertEntity.setWorkStatus(KanbanStatusEnum.PLANNING);
        convertEntity.setStartDatetime(new Date());
        convertEntity.setCompDatetime(
                new Date(convertEntity.getStartDatetime().getTime() + convertEntity.getTaktTime()));
        convertEntity.setWorkKanbanOrder(0);
        return convertEntity;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.workKanbanId ^ (this.workKanbanId >>> 32));
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
        final WorkKanbanInfoEntity other = (WorkKanbanInfoEntity) obj;
        if (!Objects.equals(this.getWorkKanbanId(), other.getWorkKanbanId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkKanbanInfoEntity{")
                .append("workKanbanId=").append(this.workKanbanId)
                .append(", parentId=").append(this.parentId)
                .append(", fkKanbanId=").append(this.fkKanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", fkWorkflowId=").append(this.fkWorkflowId)
                .append(", workflowName=").append(this.workflowName)
                .append(", fkWorkId=").append(this.fkWorkId)
                .append(", workName=").append(this.workName)
                .append(", separateWorkFlag=").append(this.separateWorkFlag)
                .append(", implementFlag=").append(this.implementFlag)
                .append(", skipFlag=").append(this.skipFlag)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", taktTime=").append(this.taktTime)
                .append(", sumTimes=").append(this.sumTimes)
                .append(", fkUpdatePersonId=").append(this.fkUpdatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", workStatus=").append(this.workStatus)
                .append(", fkInterruptReasonId=").append(this.fkInterruptReasonId)
                .append(", fkDelayReasonId=").append(this.fkDelayReasonId)
                .append(", workKanbanOrder=").append(this.workKanbanOrder)
                .append(", serialNumber=").append(this.serialNumber)
                .append(", syncWork=").append(this.syncWork)
                .append(", actualNum1=").append(this.actualNum1)
                .append(", actualNum2=").append(this.actualNum2)
                .append(", actualNum3=").append(this.actualNum3)
                .append(", lastActualId=").append(this.lastActualId)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public WorkKanbanInfoEntity clone() {
        WorkKanbanInfoEntity entity = new WorkKanbanInfoEntity();

        entity.setKanbanName(getKanbanName());
        entity.setWorkName(getWorkName());
        entity.setSkipFlag(getSkipFlag());
        entity.setWorkflowName(getWorkflowName());
        entity.setStartDatetime(getStartDatetime());
        entity.setTaktTime(getTaktTime());

        entity.setEquipmentCollection(Objects.nonNull(getEquipmentCollection()) ? new ArrayList<>(getEquipmentCollection()) : null);
        entity.setOrganizationCollection(Objects.nonNull(getOrganizationCollection()) ? new ArrayList<>(getOrganizationCollection()) : null);

        //追加情報のコピー
        List<WorkKanbanPropertyInfoEntity> cloneProperties = new LinkedList<>();
        getPropertyCollection().stream().forEach(c -> cloneProperties.add(c.clone()));
        entity.setPropertyCollection(cloneProperties);

        return entity;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(WorkKanbanInfoEntity other) {
        boolean ret = false;

        if (Objects.equals(getKanbanName(), other.getKanbanName())
                && Objects.equals(getWorkName(), other.getWorkName())
                && Objects.equals(getSkipFlag(), other.getSkipFlag())
                && Objects.equals(getWorkflowName(), other.getWorkflowName())
                && Objects.equals(getStartDatetime(), other.getStartDatetime())
                && Objects.equals(getTaktTime(), other.getTaktTime())
                && Objects.equals(getEquipmentCollection(), other.getEquipmentCollection())
                && Objects.equals(getOrganizationCollection(), other.getOrganizationCollection())
                && propertyInfoListEquals(getPropertyCollection(), other.getPropertyCollection())) {
            ret = true;
        }

        return ret;
    }

    /**
     * 追加情報のリストが一致するか調べる
     *
     * @param left
     * @param right
     * @return 一致したらtrue
     */
    private boolean propertyInfoListEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        //順番が変わるときがあるのでソート
        List<WorkKanbanPropertyInfoEntity> left = new ArrayList<>(a);
        List<WorkKanbanPropertyInfoEntity> right = new ArrayList<>(b);
        left.sort(Comparator.comparing(p -> p.getWorkKanbanPropId(), Comparator.nullsFirst(Comparator.naturalOrder())));
        right.sort(Comparator.comparing(p -> p.getWorkKanbanPropId(), Comparator.nullsFirst(Comparator.naturalOrder())));

        java.util.Iterator<WorkKanbanPropertyInfoEntity> it1 = left.iterator();
        java.util.Iterator<WorkKanbanPropertyInfoEntity> it2 = right.iterator();

        while (it1.hasNext()) {
            WorkKanbanPropertyInfoEntity entity1 = it1.next();
            WorkKanbanPropertyInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }
}
