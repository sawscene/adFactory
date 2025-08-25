/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 実績出力情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "reportOut")
public class ReportOutInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty actualIdProperty;
    private StringProperty kanbanParentNameProperty;
    private LongProperty fkKanbanIdProperty;
    private StringProperty kanbanNameProperty;
    private StringProperty kanbanSubnameProperty;
    private StringProperty workflowParentNameProperty;
    private LongProperty fkWorkflowIdProperty;
    private StringProperty workflowNameProperty;
    private IntegerProperty workflowRevProperty;
    private StringProperty workParentNameProperty;
    private LongProperty fkWorkIdProperty;
    private StringProperty workNameProperty;
    private LongProperty fkWorkKanbanIdProperty;
    private BooleanProperty isSeparateWorkProperty;
    private BooleanProperty isSkipProperty;
    private StringProperty organizationParentNameProperty;
    private StringProperty organizationParentIdentNameProperty;
    private LongProperty fkOrganizationIdProperty;
    private StringProperty organizationNameProperty;
    private StringProperty organizationIdentNameProperty;
    private StringProperty equipmentParentNameProperty;
    private StringProperty equipmentParentIdentNameProperty;
    private LongProperty fkEquipmentIdProperty;
    private StringProperty equipmentNameProperty;
    private StringProperty equipmentIdentNameProperty;
    private ObjectProperty<KanbanStatusEnum> actualStatusProperty;
    private StringProperty interruptReasonProperty;
    private StringProperty delayReasonProperty;
    private ObjectProperty<Date> implementDatetimeProperty;
    private IntegerProperty taktTimeProperty;
    private IntegerProperty workingTimeProperty;
    private StringProperty modelNameProperty;
    private IntegerProperty compNumProperty;
    private StringProperty defectReasonProperty;// 不良理由
    private IntegerProperty defectNumProperty;// 不良数
    private StringProperty productionNumberProperty;// 製造番号
    private StringProperty serialNoProperty;
    
    private Long actualId;// 実績ID
    private String kanbanParentName;// カンバン階層名
    private Long fkKanbanId;// カンバンID
    private String kanbanName;// カンバン名
    private String kanbanSubname;// サブカンバン名
    private String workflowParentName;// 工程順階層名
    private Long fkWorkflowId;// 工程順ID
    private String workflowName;// 工程順名
    private Integer workflowRev;// 工程順版数
    private String workParentName;// 工程階層名
    private Long fkWorkId;// 工程ID
    private String workName;// 工程名
    private Long fkWorkKanbanId;// 工程カンバンID
    private Boolean isSeparateWork;// 追加工程フラグ
    private Boolean isSkip;// スキップフラグ
    private String organizationParentName;// 親組織の組織名
    private String organizationParentIdentName;// 親組織の組織識別名
    private Long fkOrganizationId;// 組織ID
    private String organizationName;// 組織名
    private String organizationIdentName;// 組織識別名
    private String equipmentParentName;// 親設備の設備名
    private String equipmentParentIdentName;// 親設備の設備識別名
    private Long fkEquipmentId;// 設備ID
    private String equipmentName;// 設備名
    private String equipmentIdentName;// 設備識別名
    private KanbanStatusEnum actualStatus;// 工程実績ステータス
    private String interruptReason;// 中断理由
    private String delayReason;// 遅延理由
    private Date implementDatetime;// 実施日時
    private Integer taktTime;// タクトタイム
    private Integer workingTime;// 作業時間[ms]
    private String modelName;// モデル名
    private Integer compNum;// 完成数
    private String defectReason;// 不良理由
    private Integer defectNum;// 不良数
    private String productionNumber;// 製造番号
    private String actualAddInfo;// 追加情報(JSON)
    private String serialNo; // シリアル番号
    private Integer nonWorkTime; // 中断時間

    @XmlElementWrapper(name = "actualPropertys")
    @XmlElement(name = "actualProperty")
    private List<ActualPropertyEntity> propertyCollection = null;

    /**
     * コンストラクタ
     */
    public ReportOutInfoEntity() {
    }

    /**
     * 実績IDプロパティを取得する。
     *
     * @return 実績ID
     */
    public LongProperty actualIdProperty() {
        if (Objects.isNull(this.actualIdProperty)) {
            this.actualIdProperty = new SimpleLongProperty(this.actualId);
        }
        return this.actualIdProperty;
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
     * カンバンIDプロパティを取得する。
     *
     * @return カンバンID
     */
    public LongProperty fkKanbanIdProperty() {
        if (Objects.isNull(this.fkKanbanIdProperty) && Objects.nonNull(this.fkKanbanId)) {
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
     * 工程順IDプロパティを取得する。
     *
     * @return 工程順ID
     */
    public LongProperty fkWorkflowIdProperty() {
        if (Objects.isNull(this.fkWorkflowIdProperty) && Objects.nonNull(this.fkWorkflowId)) {
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
     * 工程IDプロパティを取得する。
     *
     * @return 工程ID
     */
    public LongProperty fkWorkIdProperty() {
        if (Objects.isNull(this.fkWorkIdProperty) && Objects.nonNull(this.fkWorkId)) {
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
     * 工程カンバンIDプロパティを取得する。
     *
     * @return 工程カンバンID
     */
    public LongProperty fkWorkKanbanIdProperty() {
        if (Objects.isNull(this.fkWorkKanbanIdProperty) && Objects.nonNull(this.fkWorkKanbanId)) {
            this.fkWorkKanbanIdProperty = new SimpleLongProperty(this.fkWorkKanbanId);
        }
        return this.fkWorkKanbanIdProperty;
    }

    /**
     * 追加工程フラグプロパティを取得する。
     *
     * @return 追加工程フラグ
     */
    public BooleanProperty isSeparateWorkProperty() {
        if (Objects.isNull(this.isSeparateWorkProperty) && Objects.nonNull(this.isSeparateWork)) {
            this.isSeparateWorkProperty = new SimpleBooleanProperty(this.isSeparateWork);
        }
        return this.isSeparateWorkProperty;
    }

    /**
     * スキップフラグプロパティを取得する。
     *
     * @return スキップフラグ
     */
    public BooleanProperty isSkipProperty() {
        if (Objects.isNull(this.isSkipProperty) && Objects.nonNull(this.isSkip)) {
            this.isSkipProperty = new SimpleBooleanProperty(this.isSkip);
        }
        return this.isSkipProperty;
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
     * 組織IDプロパティを取得する。
     *
     * @return 組織ID
     */
    public LongProperty fkOrganizationIdProperty() {
        if (Objects.isNull(this.fkOrganizationIdProperty) && Objects.nonNull(this.fkOrganizationId)) {
            this.fkOrganizationIdProperty = new SimpleLongProperty(this.fkOrganizationId);
        }
        return this.fkOrganizationIdProperty;
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
     * 設備IDプロパティを取得する。
     *
     * @return 設備ID
     */
    public LongProperty fkEquipmentIdProperty() {
        if (Objects.isNull(this.fkEquipmentIdProperty) && Objects.nonNull(this.fkEquipmentId)) {
            this.fkEquipmentIdProperty = new SimpleLongProperty(this.fkEquipmentId);
        }
        return this.fkEquipmentIdProperty;
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
     * 工程実績ステータスプロパティを取得する。
     *
     * @return 工程実績ステータス
     */
    public ObjectProperty<KanbanStatusEnum> actualStatusProperty() {
        if (Objects.isNull(this.actualStatusProperty) && Objects.nonNull(this.actualStatus)) {
            this.actualStatusProperty = new SimpleObjectProperty<>(this.actualStatus);
        }
        return this.actualStatusProperty;
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
     * 実施日時プロパティを取得する。
     *
     * @return 実施日時
     */
    public ObjectProperty<Date> implementDatetimeProperty() {
        if (Objects.isNull(this.implementDatetimeProperty) && Objects.nonNull(this.implementDatetime)) {
            this.implementDatetimeProperty = new SimpleObjectProperty<>(this.implementDatetime);
        }
        return this.implementDatetimeProperty;
    }

    /**
     * タクトタイムプロパティを取得する。
     *
     * @return タクトタイム
     */
    public IntegerProperty taktTimeProperty() {
        if (Objects.isNull(this.taktTimeProperty) && Objects.nonNull(this.taktTime)) {
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
        if (Objects.isNull(this.workingTimeProperty) && Objects.nonNull(this.workingTime)) {
            this.workingTimeProperty = new SimpleIntegerProperty(this.workingTime);
        }
        return this.workingTimeProperty;
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
     * 完成数プロパティを取得する。
     *
     * @return 完成数
     */
    public IntegerProperty compNumProperty() {
        if (Objects.isNull(this.compNumProperty) && Objects.nonNull(this.compNum)) {
            this.compNumProperty = new SimpleIntegerProperty(this.compNum);
        }
        return this.compNumProperty;
    }

    /**
     * 不良理由プロパティを取得する。
     *
     * @return 不良理由
     */
    public StringProperty defectReasonProperty() {
        if (Objects.isNull(this.defectReasonProperty)) {
            this.defectReasonProperty = new SimpleStringProperty(this.defectReason);
        }
        return this.defectReasonProperty;
    }

    /**
     * 不良数プロパティを取得する。
     *
     * @return 不良数
     */
    public IntegerProperty defectNumProperty() {
        if (Objects.isNull(this.defectNumProperty) && Objects.nonNull(this.defectNum)) {
            this.defectNumProperty = new SimpleIntegerProperty(this.defectNum);
        }
        return this.defectNumProperty;
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
     * シリアル番号プロパティを取得する
     * 
     * @return シリアル番号プロパティ
     */
    public StringProperty serialNoProperty() {
        if (Objects.isNull(this.serialNoProperty) && Objects.nonNull(this.serialNo)) {
            this.serialNoProperty = new SimpleStringProperty(this.serialNo);
        }
        return this.serialNoProperty;
    }
    
    /**
     * 実績IDを取得する。
     *
     * @return 実績ID
     */
    public Long getActualId() {
        if (Objects.nonNull(this.actualIdProperty)) {
            return this.actualIdProperty.get();
        }
        return this.actualId;
    }

    /**
     * 実績IDを設定する。
     *
     * @param actualId 実績ID
     */
    public void setActualId(Long actualId) {
        if (Objects.nonNull(this.actualIdProperty)) {
            this.actualIdProperty.set(actualId);
        } else {
            this.actualId = actualId;
        }
    }

    /**
     * カンバン階層名を取得する。
     *
     * @return カンバン階層名
     */
    public String getKanbanParentName() {
        if (Objects.nonNull(this.kanbanParentNameProperty)) {
            return this.kanbanParentNameProperty.get();
        }
        return this.kanbanParentName;
    }

    /**
     * カンバン階層名を設定する。
     *
     * @param kanbanParentName カンバン階層名
     */
    public void setKanbanParentName(String kanbanParentName) {
        if (Objects.nonNull(this.kanbanParentNameProperty)) {
            this.kanbanParentNameProperty.set(kanbanParentName);
        } else {
            this.kanbanParentName = kanbanParentName;
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
     * サブカンバン名を取得する。
     *
     * @return サブカンバン名
     */
    public String getKanbanSubname() {
        if (Objects.nonNull(this.kanbanSubnameProperty)) {
            return this.kanbanSubnameProperty.get();
        }
        return this.kanbanSubname;
    }

    /**
     * サブカンバン名を設定する。
     *
     * @param kanbanSubname サブカンバン名
     */
    public void setKanbanSubname(String kanbanSubname) {
        if (Objects.nonNull(this.kanbanSubnameProperty)) {
            this.kanbanSubnameProperty.set(kanbanSubname);
        } else {
            this.kanbanSubname = kanbanSubname;
        }
    }

    /**
     * 工程順階層名を取得する。
     *
     * @return 工程順階層名
     */
    public String getWorkflowParentName() {
        if (Objects.nonNull(this.workflowParentNameProperty)) {
            return this.workflowParentNameProperty.get();
        }
        return this.workflowParentName;
    }

    /**
     * 工程順階層名を設定する。
     *
     * @param workflowParentName 工程順階層名
     */
    public void setWorkflowParentName(String workflowParentName) {
        if (Objects.nonNull(this.workflowParentNameProperty)) {
            this.workflowParentNameProperty.set(workflowParentName);
        } else {
            this.workflowParentName = workflowParentName;
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
     * 工程階層名を取得する。
     *
     * @return 工程階層名
     */
    public String getWorkParentName() {
        if (Objects.nonNull(this.workParentNameProperty)) {
            return this.workParentNameProperty.get();
        }
        return this.workParentName;
    }

    /**
     * 工程階層名を設定する。
     *
     * @param workParentName 工程階層名
     */
    public void setWorkParentName(String workParentName) {
        if (Objects.nonNull(this.workParentNameProperty)) {
            this.workParentNameProperty.set(workParentName);
        } else {
            this.workParentName = workParentName;
        }
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
        return this.fkWorkId;
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
        return this.workName;
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
     * 工程カンバンIDを取得する。
     *
     * @return 工程カンバンID
     */
    public Long getFkWorkKanbanId() {
        if (Objects.nonNull(this.fkWorkKanbanIdProperty)) {
            return this.fkWorkKanbanIdProperty.get();
        }
        return this.fkWorkKanbanId;
    }

    /**
     * 工程カンバンIDを設定する。
     *
     * @param fkWorkKanbanId 工程カンバンID
     */
    public void setFkWorkKanbanwId(Long fkWorkKanbanId) {
        if (Objects.nonNull(this.fkWorkKanbanIdProperty)) {
            this.fkWorkKanbanIdProperty.set(fkWorkKanbanId);
        } else {
            this.fkWorkKanbanId = fkWorkKanbanId;
        }
    }

    /**
     * 追加工程フラグを取得する。
     *
     * @return 追加工程フラグ
     */
    public Boolean getIsSeparateWork() {
        if (Objects.nonNull(this.isSeparateWorkProperty)) {
            return this.isSeparateWorkProperty.get();
        }
        return this.isSeparateWork;
    }

    /**
     * 追加工程フラグを設定する。
     *
     * @param isSeparateWork 追加工程フラグ
     */
    public void setIsSeparateWork(Boolean isSeparateWork) {
        if (Objects.nonNull(this.isSeparateWorkProperty)) {
            this.isSeparateWorkProperty.set(isSeparateWork);
        } else {
            this.isSeparateWork = isSeparateWork;
        }
    }

    /**
     * スキップフラグを取得する。
     *
     * @return スキップフラグ
     */
    public Boolean getIsSkip() {
        if (Objects.nonNull(this.isSkipProperty)) {
            return this.isSkipProperty.get();
        }
        return this.isSkip;
    }

    /**
     * スキップフラグを設定する。
     *
     * @param isSkip スキップフラグ
     */
    public void setIsSkip(Boolean isSkip) {
        if (Objects.nonNull(this.isSkipProperty)) {
            this.isSkipProperty.set(isSkip);
        } else {
            this.isSkip = isSkip;
        }
    }

    /**
     * 親組織の組織名を取得する。
     *
     * @return 親組織の組織名
     */
    public String getOrganizationParentName() {
        if (Objects.nonNull(this.organizationParentNameProperty)) {
            return this.organizationParentNameProperty.get();
        }
        return this.organizationParentName;
    }

    /**
     * 親組織の組織名を設定する。
     *
     * @param organizationParentName 親組織の組織名
     */
    public void setOrganizationParentName(String organizationParentName) {
        if (Objects.nonNull(this.organizationParentNameProperty)) {
            this.organizationParentNameProperty.set(organizationParentName);
        } else {
            this.organizationParentName = organizationParentName;
        }
    }

    /**
     * 親組織の組織識別名を取得する。
     *
     * @return 親組織の組織識別名
     */
    public String getOrganizationParentIdentName() {
        if (Objects.nonNull(this.organizationParentIdentNameProperty)) {
            return this.organizationParentIdentNameProperty.get();
        }
        return this.organizationParentIdentName;
    }

    /**
     * 親組織の組織識別名を設定する。
     *
     * @param organizationParentIdentName 親組織の組織識別名
     */
    public void setOrganizationParentIdentName(String organizationParentIdentName) {
        if (Objects.nonNull(this.organizationParentIdentNameProperty)) {
            this.organizationParentIdentNameProperty.set(organizationParentIdentName);
        } else {
            this.organizationParentIdentName = organizationParentIdentName;
        }
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getFkOrganizationId() {
        if (Objects.nonNull(this.fkOrganizationIdProperty)) {
            return this.fkOrganizationIdProperty.get();
        }
        return this.fkOrganizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param fkOrganizationId 組織ID
     */
    public void setFkOrganizationId(Long fkOrganizationId) {
        if (Objects.nonNull(this.fkOrganizationIdProperty)) {
            this.fkOrganizationIdProperty.set(fkOrganizationId);
        } else {
            this.fkOrganizationId = fkOrganizationId;
        }
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        if (Objects.nonNull(this.organizationNameProperty)) {
            return this.organizationNameProperty.get();
        }
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        if (Objects.nonNull(this.organizationNameProperty)) {
            this.organizationNameProperty.set(organizationName);
        } else {
            this.organizationName = organizationName;
        }
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentName() {
        if (Objects.nonNull(this.organizationIdentNameProperty)) {
            return this.organizationIdentNameProperty.get();
        }
        return this.organizationIdentName;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentName 組織識別名
     */
    public void setOrganizationIdentName(String organizationIdentName) {
        if (Objects.nonNull(this.organizationIdentNameProperty)) {
            this.organizationIdentNameProperty.set(organizationIdentName);
        } else {
            this.organizationIdentName = organizationIdentName;
        }
    }

    /**
     * 親設備の設備名を取得する。
     *
     * @return 親設備の設備名
     */
    public String getEquipmentParentName() {
        if (Objects.nonNull(this.equipmentParentNameProperty)) {
            return this.equipmentParentNameProperty.get();
        }
        return this.equipmentParentName;
    }

    /**
     * 親設備の設備名を設定する。
     *
     * @param equipmentParentName 親設備の設備名
     */
    public void setEquipmentParentName(String equipmentParentName) {
        if (Objects.nonNull(this.equipmentParentNameProperty)) {
            this.equipmentParentNameProperty.set(equipmentParentName);
        } else {
            this.equipmentParentName = equipmentParentName;
        }
    }

    /**
     * 親設備の設備識別名を取得する。
     *
     * @return 親設備の設備識別名
     */
    public String getEquipmentParentIdentName() {
        if (Objects.nonNull(this.equipmentParentIdentNameProperty)) {
            return this.equipmentParentIdentNameProperty.get();
        }
        return this.equipmentParentIdentName;
    }

    /**
     * 親設備の設備識別名を設定する。
     *
     * @param equipmentParentIdentName 親設備の設備識別名
     */
    public void setEquipmentParentIdentName(String equipmentParentIdentName) {
        if (Objects.nonNull(this.equipmentParentIdentNameProperty)) {
            this.equipmentParentIdentNameProperty.set(equipmentParentIdentName);
        } else {
            this.equipmentParentIdentName = equipmentParentIdentName;
        }
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getFkEquipmentId() {
        if (Objects.nonNull(this.fkEquipmentIdProperty)) {
            return this.fkEquipmentIdProperty.get();
        }
        return this.fkEquipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param fkEquipmentId 設備ID
     */
    public void setFkEquipmentId(Long fkEquipmentId) {
        if (Objects.nonNull(this.fkEquipmentIdProperty)) {
            this.fkEquipmentIdProperty.set(fkEquipmentId);
        } else {
            this.fkEquipmentId = fkEquipmentId;
        }
    }

    /**
     * 設備名を取得する。
     *
     * @return 設備名
     */
    public String getEquipmentName() {
        if (Objects.nonNull(this.equipmentNameProperty)) {
            return this.equipmentNameProperty.get();
        }
        return this.equipmentName;
    }

    /**
     * 設備名を設定する。
     *
     * @param equipmentName 設備名
     */
    public void setEquipmentName(String equipmentName) {
        if (Objects.nonNull(this.equipmentNameProperty)) {
            this.equipmentNameProperty.set(equipmentName);
        } else {
            this.equipmentName = equipmentName;
        }
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentName() {
        if (Objects.nonNull(this.equipmentIdentNameProperty)) {
            return this.equipmentIdentNameProperty.get();
        }
        return this.equipmentIdentName;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentName 設備識別名
     */
    public void setEquipmentIdentName(String equipmentIdentName) {
        if (Objects.nonNull(this.equipmentIdentNameProperty)) {
            this.equipmentIdentNameProperty.set(equipmentIdentName);
        } else {
            this.equipmentIdentName = equipmentIdentName;
        }
    }

    /**
     * 工程実績ステータスを取得する。
     *
     * @return 工程実績ステータス
     */
    public KanbanStatusEnum getActualStatus() {
        if (Objects.nonNull(this.actualStatusProperty)) {
            return this.actualStatusProperty.get();
        }
        return this.actualStatus;
    }

    /**
     * 工程実績ステータスを設定する。
     *
     * @param actualStatus 工程実績ステータス
     */
    public void setActualStatus(KanbanStatusEnum actualStatus) {
        if (Objects.nonNull(this.actualStatusProperty)) {
            this.actualStatusProperty.set(actualStatus);
        } else {
            this.actualStatus = actualStatus;
        }
    }

    /**
     * 中断理由を取得する。
     *
     * @return 中断理由
     */
    public String getInterruptReason() {
        if (Objects.nonNull(this.interruptReasonProperty)) {
            return this.interruptReasonProperty.get();
        }
        return this.interruptReason;
    }

    /**
     * 中断理由を設定する。
     *
     * @param interruptReason 中断理由
     */
    public void setInterruptReason(String interruptReason) {
        if (Objects.nonNull(this.interruptReasonProperty)) {
            this.interruptReasonProperty.set(interruptReason);
        } else {
            this.interruptReason = interruptReason;
        }
    }

    /**
     * 遅延理由を取得する。
     *
     * @return 遅延理由
     */
    public String getDelayReason() {
        if (Objects.nonNull(this.delayReasonProperty)) {
            return this.delayReasonProperty.get();
        }
        return this.delayReason;
    }

    /**
     * 遅延理由を設定する。
     *
     * @param delayReason 遅延理由
     */
    public void setDelayReason(String delayReason) {
        if (Objects.nonNull(this.delayReasonProperty)) {
            this.delayReasonProperty.set(delayReason);
        } else {
            this.delayReason = delayReason;
        }
    }

    /**
     * 実施日時を取得する。
     *
     * @return 実施日時
     */
    public Date getImplementDatetime() {
        if (Objects.nonNull(this.implementDatetimeProperty)) {
            return this.implementDatetimeProperty.get();
        }
        return this.implementDatetime;
    }

    /**
     * 実施日時を設定する。
     *
     * @param implementDatetime 実施日時
     */
    public void setImplementDatetime(Date implementDatetime) {
        if (Objects.nonNull(this.implementDatetimeProperty)) {
            this.implementDatetimeProperty.set(implementDatetime);
        } else {
            this.implementDatetime = implementDatetime;
        }
    }

    /**
     * タクトタイムを取得する。
     *
     * @return タクトタイム
     */
    public Integer getTaktTime() {
        if (Objects.nonNull(this.taktTimeProperty)) {
            return this.taktTimeProperty.get();
        }
        return this.taktTime;
    }

    /**
     * タクトタイムを設定する。
     *
     * @param taktTime タクトタイム
     */
    public void setTaktTime(Integer taktTime) {
        if (Objects.nonNull(this.taktTimeProperty)) {
            this.taktTimeProperty.set(taktTime);
        } else {
            this.taktTime = taktTime;
        }
    }

    /**
     * 作業時間[ms]を取得する。
     *
     * @return 作業時間[ms]
     */
    public Integer getWorkingTime() {
        if (Objects.nonNull(this.workingTimeProperty)) {
            return this.workingTimeProperty.get();
        }
        return this.workingTime;
    }

    /**
     * 作業時間[ms]を設定する。
     *
     * @param workingTime 作業時間[ms]
     */
    public void setWorkingTime(Integer workingTime) {
        if (Objects.nonNull(this.workingTimeProperty)) {
            this.workingTimeProperty.set(workingTime);
        } else {
            this.workingTime = workingTime;
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

    /**
     * 完成数を取得する。
     *
     * @return 完成数
     */
    public Integer getCompNum() {
        if (Objects.nonNull(this.compNumProperty)) {
            return this.compNumProperty.get();
        }
        return this.compNum;
    }

    /**
     * 完成数を設定する。
     *
     * @param compNum 完成数
     */
    public void setCompNum(Integer compNum) {
        if (Objects.nonNull(this.compNumProperty)) {
            this.compNumProperty.set(compNum);
        } else {
            this.compNum = compNum;
        }
    }

    /**
     * 不良理由を取得する。
     *
     * @return 不良理由
     */
    public String getDefectReason() {
        if (Objects.nonNull(this.defectReasonProperty)) {
            return this.defectReasonProperty.get();
        }
        return this.defectReason;
    }

    /**
     * 不良理由を設定する。
     *
     * @param defectReason 不良理由
     */
    public void setDefectReason(String defectReason) {
        if (Objects.nonNull(this.defectReasonProperty)) {
            this.defectReasonProperty.set(defectReason);
        } else {
            this.defectReason = defectReason;
        }
    }

    /**
     * 不良数を取得する。
     *
     * @return 不良数
     */
    public Integer getDefectNum() {
        if (Objects.nonNull(this.defectNumProperty)) {
            return this.defectNumProperty.get();
        }
        return this.defectNum;
    }

    /**
     * 不良数を設定する。
     *
     * @param defectNum 不良数
     */
    public void setDefectNum(Integer defectNum) {
        if (Objects.nonNull(this.defectNumProperty)) {
            this.defectNumProperty.set(defectNum);
        } else {
            this.defectNum = defectNum;
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
     * 追加情報一覧を取得する。
     *
     * @return 追加情報一覧
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
     * 追加情報一覧を設定する。
     *
     * @param propertyCollection 追加情報一覧
     */
    public void setPropertyCollection(List<ActualPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    /**
     * 項目名を指定して、追加情報の値を取得する。
     *
     * @param name 追加情報の項目名
     * @return 追加情報の値
     */
    public StringProperty getPropertyValue(String name) {
        if (Objects.nonNull(this.getPropertyCollection())) {
            Optional<ActualPropertyEntity> opt = this.getPropertyCollection().stream().filter(actualProperty -> (actualProperty.getActualPropName().equals(name))).findFirst();
            if (opt.isPresent()) {
                return opt.get().actualPropValueProperty();
            } else {
                return new SimpleStringProperty();
            }
        }
        return new SimpleStringProperty();
    }

    /**
     * 項目名を指定して、追加情報の値を取得する。
     *
     * @param name 追加情報の項目名
     * @return 追加情報の値
     */
    public List<StringProperty> getPropertyValues(String name) {
        List<StringProperty> properties = new ArrayList<>();
        if (Objects.nonNull(this.getPropertyCollection())) {
            this.getPropertyCollection().stream().filter(actualProperty -> (actualProperty.getActualPropName().equals(name))).forEach((actualProperty) -> {
                properties.add(actualProperty.actualPropValueProperty());
            });
        }
        return properties;
    }

    /**
     * シリア番号を取得する。
     * 
     * @return シリアル番号 
     */
    public String getSerialNo() {
        if (Objects.nonNull(this.serialNoProperty)) {
            return this.serialNoProperty.get();
        }
        return this.serialNo;
    }

    /**
     * 中断時間を取得する。
     * 
     * @return 中断時間 
     */
    public Integer getNonWorkTime() {
        return nonWorkTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.actualId);
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
        final ReportOutInfoEntity other = (ReportOutInfoEntity) obj;
        if (!Objects.equals(this.actualId, other.actualId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReportOutEntity{")
                .append("actualId=").append(this.actualId)
                .append(", kanbanParentName=").append(this.kanbanParentName)
                .append(", fkKanbanId=").append(this.fkKanbanId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", workflowParentName=").append(this.workflowParentName)
                .append(", fkWorkflowId=").append(this.fkWorkflowId)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", workParentName=").append(this.workParentName)
                .append(", fkWorkId=").append(this.fkWorkId)
                .append(", workName=").append(this.workName)
                .append(", fkWorkKanbanId=").append(this.fkWorkKanbanId)
                .append(", isSeparateWork=").append(this.isSeparateWork)
                .append(", isSkip=").append(this.isSkip)
                .append(", organizationParentName=").append(this.organizationParentName)
                .append(", organizationParentIdentName=").append(this.organizationParentIdentName)
                .append(", fkOrganizationId=").append(this.fkOrganizationId)
                .append(", organizationName=").append(this.organizationName)
                .append(", organizationIdentName=").append(this.organizationIdentName)
                .append(", equipmentParentName=").append(this.equipmentParentName)
                .append(", equipmentParentIdentName=").append(this.equipmentParentIdentName)
                .append(", fkEquipmentId=").append(this.fkEquipmentId)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", equipmentIdentName=").append(this.equipmentIdentName)
                .append(", actualStatus=").append(this.actualStatus)
                .append(", interruptReason=").append(this.interruptReason)
                .append(", delayReason=").append(this.delayReason)
                .append(", implementDatetime=").append(this.implementDatetime)
                .append(", taktTime=").append(this.taktTime)
                .append(", workingTime=").append(this.workingTime)
                .append(", modelName=").append(this.modelName)
                .append(", compNum=").append(this.compNum)
                .append(", defectReason=").append(this.defectReason)
                .append(", defectNum=").append(this.defectNum)
                .append("}")
                .toString();
    }
}
