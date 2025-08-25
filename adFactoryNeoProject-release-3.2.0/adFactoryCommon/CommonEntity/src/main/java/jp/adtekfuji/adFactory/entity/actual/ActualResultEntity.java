/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.actual;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 工程実績情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "actualResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActualResultEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty actualIdProperty;// 工程実績IDプロパティ
    @XmlTransient
    private LongProperty fkKanbanIdProperty;// カンバンIDプロパティ
    @XmlTransient
    private StringProperty kanbanParentNameProperty;// カンバン階層名プロパティ
    @XmlTransient
    private StringProperty kanbanNameProperty;// カンバン名プロパティ
    @XmlTransient
    private StringProperty kanbanSubnameProperty;// サブカンバン名プロパティ
    @XmlTransient
    private LongProperty fkWorkKanbanIdProperty;// 工程カンバンIDプロパティ
    @XmlTransient
    private ObjectProperty<Date> implementDatetimeProperty;// 実施日時プロパティ
    @XmlTransient
    private LongProperty transactionIdProperty;// トランザクションIDプロパティ
    @XmlTransient
    private LongProperty fkEquipmentIdProperty;// 設備IDプロパティ
    @XmlTransient
    private StringProperty equipmentParentNameProperty;// 親設備の設備名プロパティ
    @XmlTransient
    private StringProperty equipmentParentIdentNameProperty;// 親設備の設備識別名プロパティ
    @XmlTransient
    private StringProperty equipmentNameProperty;// 設備名プロパティ
    @XmlTransient
    private StringProperty equipmentIdentNameProperty;// 設備識別名プロパティ
    @XmlTransient
    private LongProperty fkOrganizationIdProperty;// 組織IDプロパティ
    @XmlTransient
    private StringProperty organizationParentNameProperty;// 親組織の組織名プロパティ
    @XmlTransient
    private StringProperty organizationParentIdentNameProperty;// 親組織の組織識別名プロパティ
    @XmlTransient
    private StringProperty organizationNameProperty;// 組織名プロパティ
    @XmlTransient
    private StringProperty organizationIdentNameProperty;// 組織識別名プロパティ
    @XmlTransient
    private LongProperty fkWorkflowIdIdProperty;// 工程順IDプロパティ
    @XmlTransient
    private StringProperty workflowParentNameProperty;// 工程順階層名プロパティ
    @XmlTransient
    private StringProperty workflowNameProperty;// 工程順名プロパティ
    @XmlTransient
    private StringProperty workflowRevisionProperty;// 工程順版数プロパティ
    @XmlTransient
    private LongProperty fkWorkIdProperty;// 工程IDプロパティ
    @XmlTransient
    private StringProperty workParentNameProperty;// 工程階層名プロパティ
    @XmlTransient
    private StringProperty workNameProperty;// 工程名プロパティ
    @XmlTransient
    private ObjectProperty<KanbanStatusEnum> actualStatusProperty;// 工程実績ステータスプロパティ
    @XmlTransient
    private IntegerProperty taktTimeProperty;// タクトタイムプロパティ
    @XmlTransient
    private IntegerProperty workingTimeProperty;// 作業時間[ms]プロパティ
    @XmlTransient
    private BooleanProperty isSeparateWorkProperty;// 追加工程フラグプロパティ
    @XmlTransient
    private StringProperty interruptReasonProperty;// 中断理由プロパティ
    @XmlTransient
    private StringProperty delayReasonProperty;// 遅延理由プロパティ
    @XmlTransient
    private IntegerProperty compNumProperty;// 完成数プロパティ
    @XmlTransient
    private LongProperty pairIdProperty;// ペアIDプロパティ
    @XmlTransient
    private IntegerProperty nonWorkTimeProperty;// 中断時間プロパティ

    private Long actualId;// 工程実績ID
    private Long fkKanbanId;// カンバンID
    private String kanbanParentName;// カンバン階層名
    private String kanbanName;// カンバン名
    private String kanbanSubname;// サブカンバン名
    private Long fkWorkKanbanId;// 工程カンバンID
    private Date implementDatetime;// 実施日時
    private Long transactionId;// トランザクションID
    private Long fkEquipmentId;// 設備ID
    private String equipmentParentName;// 親設備の設備名
    private String equipmentParentIdentName;// 親設備の設備識別名
    private String equipmentName;// 設備名
    private String equipmentIdentName;// 設備識別名
    private Long fkOrganizationId;// 組織ID
    private String organizationName;// 組織名
    private String organizationIdentName;// 組織識別名
    private String organizationParentName;// 親組織の組織名
    private String organizationParentIdentName;// 親組織の組織識別名
    private Long fkWorkflowId;// 工程順ID
    private String workflowParentName;// 工程順階層名
    private String workflowName;// 工程順名
    private String workflowRevision;// 工程順版数
    private Long fkWorkId;// 工程ID
    private String workParentName;// 工程階層名
    private String workName;// 工程名
    private KanbanStatusEnum actualStatus;// 工程実績ステータス
    private Integer taktTime;// タクトタイム
    private Integer workingTime;// 作業時間[ms]
    private Boolean isSeparateWork;// 追加工程フラグ
    private String interruptReason;// 中断理由
    private String delayReason;// 遅延理由
    private Integer compNum;// 完成数
    private Long pairId;// ペアID
    private Integer nonWorkTime;// 中断時間[ms]
    private String actualAddInfo;// 追加情報(JSON)
    private String serviceInfo;// サービス情報(JSON)

    private Integer verInfo;// 排他用バーション

    private String defectReason;// 不良理由
    private Integer defectNum;// 不良数

    private Integer reworkNum;//作業やり直し回数

    @XmlTransient
    private List<ActualPropertyEntity> propertyCollection = null;// 実績プロパティ情報一覧

    @XmlTransient
    private Long sumTime;

    /**
     * コンストラクタ
     */
    public ActualResultEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param fkKanbanId カンバンID
     * @param fkWorkKanbanId 工程カンバンID
     * @param implementDatetime 実施日時
     * @param transactionId トランザクションID
     * @param fkEquipmentId 設備ID
     * @param fkOrganizationId 組織ID
     * @param fkWorkflowId 工程順ID
     * @param fkWorkId 工程ID
     * @param actualStatus 工程実績ステータス
     * @param interruptReason 中断理由
     * @param delayReason 遅延理由
     */
    public ActualResultEntity(Long fkKanbanId, Long fkWorkKanbanId, Date implementDatetime, Long transactionId, Long fkEquipmentId, Long fkOrganizationId, Long fkWorkflowId, Long fkWorkId, KanbanStatusEnum actualStatus, String interruptReason, String delayReason) {
        this.fkKanbanId = fkKanbanId;
        this.fkWorkKanbanId = fkWorkKanbanId;
        this.implementDatetime = implementDatetime;
        this.transactionId = transactionId;
        this.fkEquipmentId = fkEquipmentId;
        this.fkOrganizationId = fkOrganizationId;
        this.fkWorkflowId = fkWorkflowId;
        this.fkWorkId = fkWorkId;
        this.actualStatus = actualStatus;
        this.interruptReason = interruptReason;
        this.delayReason = delayReason;
        this.sumTime = 0L;
    }

    /**
     * 工程実績IDプロパティを取得する。
     *
     * @return 工程実績ID
     */
    public LongProperty actualIdProperty() {
        if (Objects.isNull(this.actualIdProperty)) {
            this.actualIdProperty = new SimpleLongProperty(this.actualId);
        }
        return this.actualIdProperty;
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
     * カンバン階層名プロパティを取得する。
     *
     * @return カンバン階層名
     */
    public StringProperty kanbanParentNameProperty() {
        if (Objects.isNull(this.kanbanParentNameProperty)) {
            this.kanbanParentNameProperty = new SimpleStringProperty(this.kanbanParentName);
        }
        return this.kanbanParentNameProperty;
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
     * サブカンバン名プロパティを取得する。
     *
     * @return サブカンバン名
     */
    public StringProperty kanbanSubnameProperty() {
        if (Objects.isNull(this.kanbanSubnameProperty)) {
            this.kanbanSubnameProperty = new SimpleStringProperty(this.kanbanSubname);
        }
        return this.kanbanSubnameProperty;
    }

    /**
     * 工程カンバンIDプロパティを取得する。
     *
     * @return 工程カンバンID
     */
    public LongProperty fkWorkKanbanIdProperty() {
        if (Objects.isNull(this.fkWorkKanbanIdProperty)) {
            this.fkWorkKanbanIdProperty = new SimpleLongProperty(this.fkWorkKanbanId);
        }
        return this.fkWorkKanbanIdProperty;
    }

    /**
     * 実施日時プロパティを取得する。
     *
     * @return 実施日時
     */
    public ObjectProperty<Date> implementDatetimeProperty() {
        if (Objects.isNull(this.implementDatetimeProperty)) {
            this.implementDatetimeProperty = new SimpleObjectProperty<>(this.implementDatetime);
        }
        return this.implementDatetimeProperty;
    }

    /**
     * トランザクションIDプロパティを取得する。
     *
     * @return トランザクションID
     */
    public LongProperty transactionIdProperty() {
        if (Objects.isNull(this.transactionIdProperty)) {
            this.transactionIdProperty = new SimpleLongProperty(this.transactionId);
        }
        return this.transactionIdProperty;
    }

    /**
     * 設備IDプロパティを取得する。
     *
     * @return 設備ID
     */
    public LongProperty fkEquipmentIdProperty() {
        if (Objects.isNull(this.fkEquipmentIdProperty)) {
            this.fkEquipmentIdProperty = new SimpleLongProperty(this.fkEquipmentId);
        }
        return this.fkEquipmentIdProperty;
    }

    /**
     * 親設備の設備名プロパティを取得する。
     *
     * @return 親設備の設備名
     */
    public StringProperty equipmentParentNameProperty() {
        if (Objects.isNull(this.equipmentParentNameProperty)) {
            this.equipmentParentNameProperty = new SimpleStringProperty(this.equipmentParentName);
        }
        return this.equipmentParentNameProperty;
    }

    /**
     * 親設備の設備識別名プロパティを取得する。
     *
     * @return 親設備の設備識別名
     */
    public StringProperty equipmentParentIdentNameProperty() {
        if (Objects.isNull(this.equipmentParentIdentNameProperty)) {
            this.equipmentParentIdentNameProperty = new SimpleStringProperty(this.equipmentParentIdentName);
        }
        return this.equipmentParentIdentNameProperty;
    }

    /**
     * 設備名プロパティを取得する。
     *
     * @return 設備名
     */
    public StringProperty equipmentNameProperty() {
        if (Objects.isNull(this.equipmentNameProperty)) {
            this.equipmentNameProperty = new SimpleStringProperty(this.equipmentName);
        }
        return this.equipmentNameProperty;
    }

    /**
     * 設備識別名プロパティを取得する。
     *
     * @return 設備識別名
     */
    public StringProperty equipmentIdentNameProperty() {
        if (Objects.isNull(this.equipmentIdentNameProperty)) {
            this.equipmentIdentNameProperty = new SimpleStringProperty(this.equipmentIdentName);
        }
        return this.equipmentIdentNameProperty;
    }

    /**
     * 組織IDプロパティを取得する。
     *
     * @return 組織ID
     */
    public LongProperty fkOrganizationIdProperty() {
        if (Objects.isNull(this.fkOrganizationIdProperty)) {
            this.fkOrganizationIdProperty = new SimpleLongProperty(this.fkOrganizationId);
        }
        return this.fkOrganizationIdProperty;
    }

    /**
     * 親組織の組織名プロパティを取得する。
     *
     * @return 親組織の組織名
     */
    public StringProperty organizationParentNameProperty() {
        if (Objects.isNull(this.organizationParentNameProperty)) {
            this.organizationParentNameProperty = new SimpleStringProperty(this.organizationParentName);
        }
        return this.organizationParentNameProperty;
    }

    /**
     * 親組織の組織識別名プロパティを取得する。
     *
     * @return 親組織の組織識別名
     */
    public StringProperty organizationParentIdentNameProperty() {
        if (Objects.isNull(this.organizationParentIdentNameProperty)) {
            this.organizationParentIdentNameProperty = new SimpleStringProperty(this.organizationParentIdentName);
        }
        return this.organizationParentIdentNameProperty;
    }

    /**
     * 組織名プロパティを取得する。
     *
     * @return 組織名
     */
    public StringProperty organizationNameProperty() {
        if (Objects.isNull(this.organizationNameProperty)) {
            this.organizationNameProperty = new SimpleStringProperty(this.organizationName);
        }
        return this.organizationNameProperty;
    }

    /**
     * 組織識別名プロパティを取得する。
     *
     * @return 組織識別名
     */
    public StringProperty organizationIdentNameProperty() {
        if (Objects.isNull(this.organizationIdentNameProperty)) {
            this.organizationIdentNameProperty = new SimpleStringProperty(this.organizationIdentName);
        }
        return this.organizationIdentNameProperty;
    }

    /**
     * 工程順IDプロパティを取得する。
     *
     * @return 工程順ID
     */
    public LongProperty fkWorkflowIdIdProperty() {
        if (Objects.isNull(this.fkWorkflowIdIdProperty)) {
            this.fkWorkflowIdIdProperty = new SimpleLongProperty(this.fkWorkflowId);
        }
        return this.fkWorkflowIdIdProperty;
    }

    /**
     * 工程順階層名プロパティを取得する。
     *
     * @return 工程順階層名
     */
    public StringProperty workflowParentNameProperty() {
        if (Objects.isNull(this.workflowParentNameProperty)) {
            this.workflowParentNameProperty = new SimpleStringProperty(this.workflowParentName);
        }
        return this.workflowParentNameProperty;
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
     * 工程順版数プロパティを取得する。
     *
     * @return 工程順版数
     */
    public StringProperty workflowRevisionProperty() {
        if (Objects.isNull(this.workflowRevisionProperty)) {
            this.workflowRevisionProperty = new SimpleStringProperty(this.workflowRevision);
        }
        return this.workflowRevisionProperty;
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
     * 工程階層名プロパティを取得する。
     *
     * @return 工程階層名
     */
    public StringProperty workParentNameProperty() {
        if (Objects.isNull(this.workParentNameProperty)) {
            this.workParentNameProperty = new SimpleStringProperty(this.workParentName);
        }
        return this.workParentNameProperty;
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
     * 工程実績ステータスプロパティを取得する。
     *
     * @return 工程実績ステータス
     */
    public ObjectProperty<KanbanStatusEnum> actualStatusProperty() {
        if (Objects.isNull(this.actualStatusProperty)) {
            this.actualStatusProperty = new SimpleObjectProperty<>(this.actualStatus);
        }
        return this.actualStatusProperty;
    }

    /**
     * タクトタイムプロパティを取得する。
     *
     * @return タクトタイム
     */
    public IntegerProperty taktTimeProperty() {
        if (Objects.isNull(this.taktTimeProperty)) {
            this.taktTimeProperty = new SimpleIntegerProperty(this.taktTime);
        }
        return this.taktTimeProperty;
    }

    /**
     * 作業時間[ms]プロパティを取得する。
     *
     * @return 作業時間[ms]
     */
    public IntegerProperty workingTimeProperty() {
        if (Objects.isNull(this.workingTimeProperty)) {
            this.workingTimeProperty = new SimpleIntegerProperty(this.workingTime);
        }
        return this.workingTimeProperty;
    }

    /**
     * 追加工程フラグプロパティを取得する。
     *
     * @return 追加工程フラグ (true: 追加工程, false: 工程順の工程)
     */
    public BooleanProperty isSeparateWorkProperty() {
        if (Objects.isNull(this.isSeparateWorkProperty)) {
            this.isSeparateWorkProperty = new SimpleBooleanProperty(this.isSeparateWork);
        }
        return this.isSeparateWorkProperty;
    }

    /**
     * 中断理由プロパティを取得する。
     *
     * @return 中断理由
     */
    public StringProperty interruptReasonProperty() {
        if (Objects.isNull(this.interruptReasonProperty)) {
            this.interruptReasonProperty = new SimpleStringProperty(this.interruptReason);
        }
        return this.interruptReasonProperty;
    }

    /**
     * 遅延理由プロパティを取得する。
     *
     * @return 遅延理由
     */
    public StringProperty delayReasonProperty() {
        if (Objects.isNull(this.delayReasonProperty)) {
            this.delayReasonProperty = new SimpleStringProperty(this.delayReason);
        }
        return this.delayReasonProperty;
    }

    /**
     * 完成数プロパティを取得する。
     *
     * @return 完成数
     */
    public IntegerProperty compNumProperty() {
        if (Objects.isNull(this.compNumProperty)) {
            this.compNumProperty = new SimpleIntegerProperty(this.compNum);
        }
        return this.compNumProperty;
    }

    /**
     * ペアIDプロパティを取得する。
     *
     * @return ペアID
     */
    public LongProperty pairIdProperty() {
        if (Objects.isNull(this.pairIdProperty)) {
            this.pairIdProperty = new SimpleLongProperty(this.pairId);
        }
        return this.pairIdProperty;
    }

    /**
     * 中断時間[ms]プロパティを取得する。
     *
     * @return 中断時間[ms]
     */
    public IntegerProperty nonWorkTimeProperty() {
        if (Objects.isNull(this.nonWorkTimeProperty)) {
            this.nonWorkTimeProperty = new SimpleIntegerProperty(this.nonWorkTime);
        }
        return this.nonWorkTimeProperty;
    }

    /**
     * 工程実績IDを取得する。
     *
     * @return 工程実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 工程実績IDを設定する。
     *
     * @param actualId 工程実績ID
     */
    public void setActualId(Long actualId) {
        this.actualId = actualId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getFkKanbanId() {
        return this.fkKanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param fkKanbanId カンバンID
     */
    public void setFkKanbanId(Long fkKanbanId) {
        this.fkKanbanId = fkKanbanId;
    }

    /**
     * カンバン階層名を取得する。
     *
     * @return カンバン階層名
     */
    public String getKanbanParentName() {
        return this.kanbanParentName;
    }

    /**
     * カンバン階層名を設定する。
     *
     * @param kanbanParentName カンバン階層名
     */
    public void setKanbanParentName(String kanbanParentName) {
        this.kanbanParentName = kanbanParentName;
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
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        return this.kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getFkWorkKanbanId() {
        return this.fkWorkKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param fkWorkKanbanId 工程カンバンID
     */
    public void setFkWorkKanbanId(Long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
        return this.implementDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param implementDatetime 実施日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        this.implementDatetime = implementDatetime;
    }

    /**
     * トランザクションIDを取得する。
     *
     * @return トランザクションID
     */
    public Long getTransactionId() {
        return this.transactionId;
    }

    /**
     * トランザクションIDを設定する。
     *
     * @param transactionId トランザクションID
     */
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getFkEquipmentId() {
        return this.fkEquipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param fkEquipmentId 設備ID
     */
    public void setFkEquipmentId(Long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
    }

    /**
     * 親設備の設備名を取得する。
     *
     * @return 親設備の設備名
     */
    public String getEquipmentParentName() {
        return this.equipmentParentName;
    }

    /**
     * 親設備の設備名を設定する。
     *
     * @param equipmentParentName 親設備の設備名
     */
    public void setEquipmentParentName(String equipmentParentName) {
        this.equipmentParentName = equipmentParentName;
    }

    /**
     * 親設備の設備識別名を取得する。
     *
     * @return 親設備の設備識別名
     */
    public String getEquipmentParentIdentName() {
        return this.equipmentParentIdentName;
    }

    /**
     * 親設備の設備識別名を設定する。
     *
     * @param equipmentParentIdentName 親設備の設備識別名
     */
    public void setEquipmentParentIdentName(String equipmentParentIdentName) {
        this.equipmentParentIdentName = equipmentParentIdentName;
    }

    /**
     * 設備名を取得する。
     *
     * @return 設備名
     */
    public String getEquipmentName() {
        return this.equipmentName;
    }

    /**
     * 設備名を設定する。
     *
     * @param equipmentName 設備名
     */
    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentName() {
        return this.equipmentIdentName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentName 設備識別名
     */
    public void setEquipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getFkOrganizationId() {
        return this.fkOrganizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param fkOrganizationId 組織ID
     */
    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationName;
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
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentName() {
        return this.organizationIdentName;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentName 組織識別名
     */
    public void setOrganizationIdentName(String organizationIdentName) {
        this.organizationIdentName = organizationIdentName;
    }

    /**
     * 親組織の組織名を取得する。
     *
     * @return 親組織の組織名
     */
    public String getOrganizationParentName() {
        return this.organizationParentName;
    }

    /**
     * 親組織の組織名を設定する。
     *
     * @param organizationParentName 親組織の組織名
     */
    public void setOrganizationParentName(String organizationParentName) {
        this.organizationParentName = organizationParentName;
    }

    /**
     * 親組織の組織識別名を取得する。
     *
     * @return 親組織の組織識別名
     */
    public String getOrganizationParentIdentName() {
        return this.organizationParentIdentName;
    }

    /**
     * 親組織の組織識別名を設定する。
     *
     * @param organizationParentIdentName 親組織の組織識別名
     */
    public void setOrganizationParentIdentName(String organizationParentIdentName) {
        this.organizationParentIdentName = organizationParentIdentName;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getFkWorkflowId() {
        return this.fkWorkflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param fkWorkflowId 工程順ID
     */
    public void setFkWorkflowId(Long fkWorkflowId) {
        this.fkWorkflowId = fkWorkflowId;
    }

    /**
     * 工程順階層名を取得する。
     *
     * @return 工程順階層名
     */
    public String getWorkflowParentName() {
        return this.workflowParentName;
    }

    /**
     * 工程順階層名を設定する。
     *
     * @param workflowParentName 工程順階層名
     */
    public void setWorkflowParentName(String workflowParentName) {
        this.workflowParentName = workflowParentName;
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
     * 工程順版数を取得する。
     *
     * @return 工程順版数
     */
    public String getWorkflowRevision() {
        return this.workflowRevision;
    }

    /**
     * 工程順版数を設定する。
     *
     * @param workflowRevision 工程順版数
     */
    public void setWorkflowRevision(String workflowRevision) {
        this.workflowRevision = workflowRevision;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getFkWorkId() {
        return this.fkWorkId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param fkWorkId 工程ID
     */
    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    /**
     * 工程階層名を取得する。
     *
     * @return 工程階層名
     */
    public String getWorkParentName() {
        return this.workParentName;
    }

    /**
     * 工程階層名を設定する。
     *
     * @param workParentName 工程階層名
     */
    public void setWorkParentName(String workParentName) {
        this.workParentName = workParentName;
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
     * 工程実績ステータスを取得する。
     *
     * @return 工程実績ステータス
     */
    public KanbanStatusEnum getActualStatus() {
        return this.actualStatus;
    }

    /**
     * 工程実績ステータスを設定する。
     *
     * @param actualStatus 工程実績ステータス
     */
    public void setActualStatus(KanbanStatusEnum actualStatus) {
        this.actualStatus = actualStatus;
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Integer getTaktTime() {
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Integer getWorkingTime() {
        return this.workingTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workingTime 作業時間[ms]
     */
    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    /**
     * 追加工程フラグを取得する。
     *
     * @return 追加工程フラグ (true: 追加工程, false: 工程順の工程)
     */
    public Boolean getIsSeparateWork() {
        return this.isSeparateWork;
    }

    /**
     * 追加工程フラグを設定する。
     *
     * @param isSeparateWork 追加工程フラグ (true: 追加工程, false: 工程順の工程)
     */
    public void setIsSeparateWork(Boolean isSeparateWork) {
        this.isSeparateWork = isSeparateWork;
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        this.interruptReason = interruptReason;
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }

    /**
     * 完成数を取得する。
     *
     * @return 完成数
     */
    public Integer getCompNum() {
        return this.compNum;
    }

    /**
     * 完成数を設定する。
     *
     * @param compNum 完成数
     */
    public void setCompNum(Integer compNum) {
        this.compNum = compNum;
    }

    /**
     * ペアIDを取得する。
     *
     * @return ペアID
     */
    public Long getPairId() {
        return this.pairId;
    }

    /**
     * ペアIDを設定する。
     *
     * @param pairId ペアID
     */
    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    /**
     * 中断時間[ms]を取得する。
     *
     * @return 中断時間[ms]
     */
    public Integer getNonWorkTime() {
        return this.nonWorkTime;
    }

    /**
     * 中断時間[ms]を設定する。
     *
     * @param nonWorkTime 中断時間[ms]
     */
    public void setNonWorkTime(Integer nonWorkTime) {
        this.nonWorkTime = nonWorkTime;
    }
    
    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getActualAddInfo() {
        return this.actualAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param actualAddInfo 追加情報(JSON)
     */
    public void setActualAddInfo(String actualAddInfo) {
        this.actualAddInfo = actualAddInfo;
    }

    /**
     * サービス情報(JSON)を取得する。
     *
     * @return サービス情報(JSON)
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報(JSON)を設定する。
     *
     * @param serviceInfo サービス情報(JSON)
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 不良理由を取得する。
     *
     * @return 不良理由
     */
    public String getDefectReason() {
        return this.defectReason;
    }

    /**
     * 不良理由を設定する。
     *
     * @param defectReason 不良理由
     */
    public void setDefectReason(String defectReason) {
        this.defectReason = defectReason;
    }

    /**
     * 不良数を取得する。
     *
     * @return 不良数
     */
    public Integer getDefectNum() {
        return this.defectNum;
    }

    /**
     * 不良数を設定する。
     *
     * @param defectNum 不良数
     */
    public void setDefectNum(Integer defectNum) {
        this.defectNum = defectNum;
    }

    /**
     * 作業やり直し回数
     * @return 作業やり直し回数
     */
    public Integer getReworkNum() { return this.reworkNum; }

    /**
     * 実績プロパティ情報一覧を取得する。
     *
     * @return 実績プロパティ情報一覧
     */
    public List<ActualPropertyEntity> getPropertyCollection() {
        
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if(Objects.isNull(this.propertyCollection)){
            // 変換した結果をエンティティにセットする
            this.setPropertyCollection(JsonUtils.jsonToObjects(this.getActualAddInfo(), ActualPropertyEntity[].class));
        }
        
        return this.propertyCollection;
    }

    /**
     * 実績プロパティ情報一覧を設定する。
     *
     * @param propertyCollection 実績プロパティ情報一覧
     */
    public void setPropertyCollection(List<ActualPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    /**
     * 実績プロパティの値を取得する。
     *
     * @param name 実績プロパティ名
     * @return 実績プロパティの値
     */
    public StringProperty getPropertyValue(String name) {
        if (Objects.nonNull(this.propertyCollection)) {
            for (ActualPropertyEntity setting : this.propertyCollection) {
                if (setting.getActualPropName().equals(name)) {
                    return setting.actualPropValueProperty();
                }
            }
        }
        return new SimpleStringProperty();
    }

    /**
     * 実績プロパティの値を取得する。
     *
     * @param name 実績プロパティ名
     * @return 実績プロパティの値一覧
     */
    public List<StringProperty> getPropertyValues(String name) {
        List<StringProperty> propertys = new ArrayList<>();
        if (Objects.nonNull(this.propertyCollection)) {
            this.propertyCollection.stream().filter(actualProperty -> (actualProperty.getActualPropName().equals(name))).forEach((actualProperty) -> {
                propertys.add(actualProperty.actualPropValueProperty());
            });
        }
        return propertys;
    }
    
    /**
     * 実績プロパティの値を設定する。
     *
     * @param name 実績プロパティ名
     * @param value 実績プロパティの値
     */
    public void setPropertyValue(String name, String value) {
        if (Objects.nonNull(this.propertyCollection)) {
            for (ActualPropertyEntity setting : this.propertyCollection) {
                if (setting.getActualPropName().equals(name)) {
                    setting.setActualPropValue(value);
                }
            }
        }
    }

    /**
     * 作業累積時間取得
     * @return 累積時間
     */
    public Long getSumTime() {
        return sumTime;
    }

    /**
     * 累積時間設定
     * @param sumTime 累積時間
     */
    public void setSumTime(Long sumTime) {
        this.sumTime = sumTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actualId != null ? actualId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ActualResultEntity)) {
            return false;
        }
        ActualResultEntity other = (ActualResultEntity) object;
        return !((this.actualId == null && other.actualId != null) || (this.actualId != null && !this.actualId.equals(other.actualId)));
    }

    @Override
    public String toString() {
        return new StringBuilder("ActualResultEntity{")
                .append("actualId=").append(this.actualId)
                .append(", fkKanbanId=").append(this.fkKanbanId)
                .append(", fkWorkKanbanId=").append(this.fkWorkKanbanId)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", workingTime=").append(this.workingTime)
                .append(", transactionId=").append(this.transactionId)
                .append(", fkEquipmentId=").append(this.fkEquipmentId)
                .append(", fkOrganizationId=").append(this.fkOrganizationId)
                .append(", fkWorkflowId=").append(this.fkWorkflowId)
                .append(", fkWorkId=").append(this.fkWorkId)
                .append(", actualStatus=").append(this.actualStatus)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", compNum=").append(this.compNum)
                .append(", pairId=").append(this.pairId)
                .append(", reworkNum=").append(this.reworkNum)
                .append(", nonWorkTime=").append(this.nonWorkTime)
                .append(", verInfo=").append(this.verInfo)
                .append(", defectReason=").append(this.defectReason)
                .append(", defectNum=").append(this.defectNum)
                .append("}")
                .toString();
    }
}
