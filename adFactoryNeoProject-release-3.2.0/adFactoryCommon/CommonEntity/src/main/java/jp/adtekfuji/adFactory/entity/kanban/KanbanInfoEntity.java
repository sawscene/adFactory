/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * カンバン情報
 *
 * @author e-mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "kanban")
public class KanbanInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty kanbanIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty kanbanNameProperty;
    private StringProperty kanbanSubnameProperty;
    private LongProperty fkWorkflowIdProperty;
    private StringProperty workflowNameProperty;
    private IntegerProperty workflowRevProperty;
    private ObjectProperty<Date> startDatetimeProperty;
    private ObjectProperty<Date> compDatetimeProperty;
    private LongProperty fkUpdatePersonIdProperty;
    private ObjectProperty<Date> updateDateTimeProperty;
    private ObjectProperty<KanbanStatusEnum> kanbanStatusProperty;
    private LongProperty fkInterruptReasonIdProperty;
    private LongProperty fkDelayReasonIdProperty;
    private StringProperty modelNameProperty;

    @XmlElement(required = true)
    private Long kanbanId;                          // カンバンID
    @XmlElement()
    private Long parentId;                          // カンバン階層ID
    @XmlElement()
    private String parentName;                      // カンバン階層名
    @XmlElement()
    private String kanbanName;                      // カンバン名
    @XmlElement()
    private String kanbanSubname;                   // サブカンバン名
    @XmlElement()
    private Long fkWorkflowId;                      // 工程順ID
    @XmlElement()
    private String workflowName;                    // 工程順名
    @XmlElement()
    private Integer workflowRev;                    // 工程順版数
    @XmlElement()
    private Date startDatetime;                     // 先頭工程開始予定日時
    @XmlElement()
    private Date compDatetime;                      // 最終工程完了予定日時
    @XmlElement()
    private Long fkUpdatePersonId;                  // 更新者(組織ID)
    @XmlElement()
    private Date updateDatetime;                    // 更新日時
    @XmlElement()
    private KanbanStatusEnum kanbanStatus;          // カンバンステータス
    @XmlElement()
    private Long fkInterruptReasonId;               // 中断理由ID
    @XmlElement()
    private Long fkDelayReasonId;                   // 遅延理由ID

    @XmlTransient
    private List<KanbanPropertyInfoEntity> propertyCollection = null;// カンバンプロパティ一覧

    @XmlElementWrapper(name = "workKanbans")
    @XmlElement(name = "workKanban")
    private List<WorkKanbanInfoEntity> workKanbanCollection;// 通常工程の工程カンバン情報一覧
    @XmlElementWrapper(name = "separateworkKanbans")
    @XmlElement(name = "separateworkKanban")
    private List<WorkKanbanInfoEntity> separateworkKanbanCollection;// 追加工程の工程カンバン情報一覧
    @XmlElementWrapper(name = "actualResults")
    @XmlElement(name = "actualResult")
    private List<ActualResultEntity> actualResultCollection;// 工程実績情報一覧
    @XmlElement()
    private Integer lotQuantity;                    // ロット数量
    @XmlElement()
    private String modelName;                       // モデル名
    @XmlElementWrapper(name = "products")
    @XmlElement(name = "product")
    private List<ProductInfoEntity> products;       // 製品情報一覧
    @XmlElement()
    private Integer repairNum;                      // 補修数
    @XmlElement()
    private Integer productionType = 0;             // 生産タイプ

    @XmlElement()
    private String kanbanAddInfo;                   // 追加情報(JSON)
    @XmlElement()
    private String serviceInfo;                     // サービス情報(JSON)

    @XmlElement()
    private Integer verInfo;                        // 排他用バーション

    @XmlElement()
    private String productionNumber;                // 製造番号

    @XmlElement()
    private String approval;                        // 承認(JSON)

    @XmlElement()
    private String kanbanLabel;                     // ラベル(JSON)

    @XmlTransient
    private String updatePerson;                    // 更新者

    @XmlElement()
    private String ledgerPath;                      // 帳票テンプレートパス(JSON)
    @XmlElement()
    private Date actualStartTime;                   // 実績開始日時
    @XmlElement()
    private Date actualCompTime;                    // 実績完了日時
    @XmlElement()
    private Integer cycleTime;                      // 標準サイクルタイム

    /**
     * コンストラクタ
     */
    public KanbanInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanId カンバンID
     * @param parentId カンバン階層ID
     * @param kanbanName カンバン名
     * @param kanbanSubname サブカンバン名
     */
    public KanbanInfoEntity(Long kanbanId, Long parentId, String kanbanName, String kanbanSubname) {
        this.kanbanId = kanbanId;
        this.parentId = parentId;
        this.kanbanName = kanbanName;
        this.kanbanSubname = kanbanSubname;
    }

    /**
     * カンバンIDプロパティを取得する。
     *
     * @return カンバンIDプロパティ
     */
    public LongProperty kanbanIdProperty() {
        if (Objects.isNull(this.kanbanIdProperty)) {
            this.kanbanIdProperty = new SimpleLongProperty(this.kanbanId);
        }
        return this.kanbanIdProperty;
    }

    /**
     * カンバン階層IDプロパティを取得する。
     *
     * @return カンバン階層IDプロパティ
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentIdProperty;
    }

    /**
     * カンバン名プロパティを取得する。
     *
     * @return カンバン名プロパティ
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
     * @return サブカンバン名プロパティ
     */
    public StringProperty kanbanSubnameProperty() {
        if (Objects.isNull(this.kanbanSubnameProperty)) {
            this.kanbanSubnameProperty = new SimpleStringProperty(this.kanbanSubname);
        }
        return this.kanbanSubnameProperty;
    }

    /**
     * 工程順IDプロパティを取得する。
     *
     * @return 工程順IDプロパティ
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
     * @return 工程順名プロパティ
     */
    public StringProperty workflowNameProperty() {
        if (Objects.isNull(this.workflowNameProperty)) {
            this.workflowNameProperty = new SimpleStringProperty(this.workflowName);
        }
        return this.workflowNameProperty;
    }

    /**
     * 版数プロパティを取得する。
     *
     * @return 版数プロパティ
     */
    public IntegerProperty workflowRevProperty() {
        if (Objects.isNull(this.workflowRevProperty)) {
            this.workflowRevProperty = new SimpleIntegerProperty(this.workflowRev);
        }
        return this.workflowRevProperty;
    }

    /**
     * 先頭工程開始予定日時プロパティを取得する。
     *
     * @return 先頭工程開始予定日時プロパティ
     */
    public ObjectProperty<Date> startDatetimeProperty() {
        if (Objects.isNull(this.startDatetimeProperty)) {
            this.startDatetimeProperty = new SimpleObjectProperty(this.startDatetime);
        }
        return this.startDatetimeProperty;
    }

    /**
     * 最終工程完了予定日時プロパティを取得する。
     *
     * @return 最終工程完了予定日時プロパティ
     */
    public ObjectProperty<Date> compDatetimeProperty() {
        if (Objects.isNull(this.compDatetimeProperty)) {
            this.compDatetimeProperty = new SimpleObjectProperty(this.compDatetime);
        }
        return this.compDatetimeProperty;
    }

    /**
     * 更新者の組織IDプロパティを取得する。
     *
     * @return 更新者の組織IDプロパティ
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
     * @return 更新日時プロパティ
     */
    public ObjectProperty<Date> updateDateTimeProperty() {
        if (Objects.isNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty = new SimpleObjectProperty(this.updateDatetime);
        }
        return this.updateDateTimeProperty;
    }

    /**
     * カンバンステータスプロパティを取得する。
     *
     * @return カンバンステータスプロパティ
     */
    public ObjectProperty<KanbanStatusEnum> kanbanStatusProperty() {
        if (Objects.isNull(this.kanbanStatusProperty)) {
            this.kanbanStatusProperty = new SimpleObjectProperty(this.kanbanStatus);
        }
        return this.kanbanStatusProperty;
    }

    /**
     * 中断理由IDプロパティを取得する。
     *
     * @return 中断理由IDプロパティ
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
     * @return 遅延理由IDプロパティ
     */
    public LongProperty fkDelayReasonIdProperty() {
        if (Objects.isNull(this.fkDelayReasonIdProperty)) {
            this.fkDelayReasonIdProperty = new SimpleLongProperty(this.fkDelayReasonId);
        }
        return this.fkDelayReasonIdProperty;
    }

    /**
     * モデル名プロパティを取得する。
     *
     * @return モデル名プロパティ
     */
    public StringProperty modelNameProperty() {
        if (Objects.isNull(this.modelNameProperty)) {
            this.modelNameProperty = new SimpleStringProperty(this.modelName);
        }
        return this.modelNameProperty;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        if (Objects.nonNull(this.kanbanIdProperty)) {
            return this.kanbanIdProperty.get();
        }
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        if (Objects.nonNull(this.kanbanIdProperty)) {
            this.kanbanIdProperty.set(kanbanId);
        } else {
            this.kanbanId = kanbanId;
        }
    }

    /**
     * カンバン階層IDを取得する。
     *
     * @return カンバン階層ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * カンバン階層IDを設定する。
     *
     * @param parentId カンバン階層ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * カンバン階層名を取得
     * @return カンバン階層名
     */
    public String getParentName() {
        return this.parentName;
    }

    /**
     * カンバン階層名を設定する
     * @param parentName カンバン階層名
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
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
     * 先頭工程開始予定日時を取得する。
     *
     * @return 先頭工程開始予定日時
     */
    public Date getStartDatetime() {
        if (Objects.nonNull(this.startDatetimeProperty)) {
            return this.startDatetimeProperty.get();
        }
        return this.startDatetime;
    }

    /**
     * 先頭工程開始予定日時を設定する。
     *
     * @param startDatetime 先頭工程開始予定日時
     */
    public void setStartDatetime(Date startDatetime) {
        if (Objects.nonNull(this.startDatetimeProperty)) {
            this.startDatetimeProperty.set(startDatetime);
        } else {
            this.startDatetime = startDatetime;
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
     * 最終工程完了予定日時を取得する。
     *
     * @return 最終工程完了予定日時
     */
    public Date getCompDatetime() {
        if (Objects.nonNull(this.compDatetimeProperty)) {
            return this.compDatetimeProperty.get();
        }
        return this.compDatetime;
    }

    /**
     * 最終工程完了予定日時を設定する。
     *
     * @param compDatetime 最終工程完了予定日時
     */
    public void setCompDatetime(Date compDatetime) {
        if (Objects.nonNull(this.compDatetimeProperty)) {
            this.compDatetimeProperty.set(compDatetime);
        } else {
            this.compDatetime = compDatetime;
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
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            return this.updateDateTimeProperty.get();
        }
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty.set(updateDatetime);
        } else {
            this.updateDatetime = updateDatetime;
        }
    }

    /**
     * カンバンステータスを取得する。
     *
     * @return カンバンステータス
     */
    public KanbanStatusEnum getKanbanStatus() {
        if (Objects.nonNull(this.kanbanStatusProperty)) {
            return this.kanbanStatusProperty.get();
        }
        return this.kanbanStatus;
    }

    /**
     * カンバンステータスを設定する。
     *
     * @param kanbanStatus カンバンステータス
     */
    public void setKanbanStatus(KanbanStatusEnum kanbanStatus) {
        if (Objects.nonNull(this.kanbanStatusProperty)) {
            this.kanbanStatusProperty.set(kanbanStatus);
        } else {
            this.kanbanStatus = kanbanStatus;
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
     * カンバンプロパティ情報一覧を取得する。
     *
     * @return カンバンプロパティ情報一覧
     */
    public List<KanbanPropertyInfoEntity> getPropertyCollection() {
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if (Objects.isNull(this.propertyCollection)) {
            // 変換した結果をエンティティにセットする
            this.setPropertyCollection(JsonUtils.jsonToObjects(this.getKanbanAddInfo(), KanbanPropertyInfoEntity[].class));
        }
        return this.propertyCollection;
    }

    /**
     * カンバンプロパティ情報一覧を設定する。
     *
     * @param propertyCollection カンバンプロパティ情報一覧
     */
    public void setPropertyCollection(List<KanbanPropertyInfoEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public void addProperty(KanbanPropertyInfoEntity entity) {
        if (Objects.isNull(this.propertyCollection)) {
            this.propertyCollection = new ArrayList<>();
        }
        this.propertyCollection.add(entity);
    }

    /**
     * 通常工程の工程カンバン情報一覧を取得する。
     *
     * @return 通常工程の工程カンバン情報一覧
     */
    public List<WorkKanbanInfoEntity> getWorkKanbanCollection() {
        return this.workKanbanCollection;
    }

    /**
     * 通常工程の工程カンバン情報一覧を設定する。
     *
     * @param workKanbanCollection 通常工程の工程カンバン情報一覧
     */
    public void setWorkKanbanCollection(List<WorkKanbanInfoEntity> workKanbanCollection) {
        this.workKanbanCollection = workKanbanCollection;
    }

    /**
     * 追加工程の工程カンバン情報一覧を取得する。
     *
     * @return 追加工程の工程カンバン情報一覧
     */
    public List<WorkKanbanInfoEntity> getSeparateworkKanbanCollection() {
        return this.separateworkKanbanCollection;
    }

    /**
     * 追加工程の工程カンバン情報一覧を設定する。
     *
     * @param separateworkKanbanCollection 追加工程の工程カンバン情報一覧
     */
    public void setSeparateworkKanbanCollection(List<WorkKanbanInfoEntity> separateworkKanbanCollection) {
        this.separateworkKanbanCollection = separateworkKanbanCollection;
    }

    /**
     * 工程実績情報一覧を取得する。
     *
     * @return 工程実績情報一覧
     */
    public List<ActualResultEntity> getActualResultCollection() {
        return this.actualResultCollection;
    }

    /**
     * 工程実績情報一覧を設定する。
     *
     * @param actualResultCollection 工程実績情報一覧
     */
    public void setActualResultCollection(List<ActualResultEntity> actualResultCollection) {
        this.actualResultCollection = actualResultCollection;
    }

    /**
     * カンバンプロパティのプロパティ名を指定して値を取得する。
     *
     * @param name カンバンプロパティ名
     * @return カンバンプロパティ値
     */
    public StringProperty getPropertyValue(String name) {
        if (Objects.nonNull(this.propertyCollection)) {
            for (KanbanPropertyInfoEntity prop : this.propertyCollection) {
                if (prop.getKanbanPropertyName().equals(name)) {
                    return prop.kanbanPropValueProperty();
                }
            }
        }
        return new SimpleStringProperty();
    }

    /**
     * ロット数量を取得する。
     *
     * @return ロット数量
     */
    public Integer getLotQuantity() {
        return this.lotQuantity;
    }

    /**
     * ロット数量を設定する。
     *
     * @param lotQuantity ロット数量
     */
    public void setLotQuantity(Integer lotQuantity) {
        this.lotQuantity = lotQuantity;
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
     * 製品情報一覧を取得する。
     *
     * @return 製品情報一覧
     */
    public List<ProductInfoEntity> getProducts() {
        return this.products;
    }

    /**
     * 製品情報一覧を設定する。
     *
     * @param products 製品情報一覧
     */
    public void setProducts(List<ProductInfoEntity> products) {
        this.products = products;
    }

    /**
     * 補修数を取得する。
     *
     * @return 補修数
     */
    public Integer getRepairNum() {
        return this.repairNum;
    }

    /**
     * 補修数を設定する。
     *
     * @param repairNum 補修数
     */
    public void setRepairNum(Integer repairNum) {
        this.repairNum = repairNum;
    }

    /**
     * 生産タイプを取得する
     *
     * @return 生産タイプ
     */
    public Integer getProductionType() {
        return this.productionType;
    }

    /**
     * 生産タイプを設定する
     *
     * @param productionType 生産タイプ
     */
    public void setProductionType(Integer productionType) {
        this.productionType = productionType;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getKanbanAddInfo() {
        return this.kanbanAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param kanbanAddInfo 追加情報(JSON)
     */
    public void setKanbanAddInfo(String kanbanAddInfo) {
        this.kanbanAddInfo = kanbanAddInfo;
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
     * 製造番号を取得する。
     *
     * @return 製造番号
     */
    public String getProductionNumber() {
        return this.productionNumber;
    }

    /**
     * 製造番号を設定する。
     *
     * @param productionNumber 製造番号
     */
    public void setProductionNumber(String productionNumber) {
        this.productionNumber = productionNumber;
    }

    /**
     * 承認(JSON)を取得する。
     *
     * @return 承認(JSON)
     */
    public String getApproval() {
        return this.approval;
    }

    /**
     * 承認(JSON)を設定する。
     *
     * @param approval 承認(JSON)
     */
    public void setApproval(String approval) {
        this.approval = approval;
    }

    /**
     * ラベル(JSON)を取得する。
     * 
     * @return ラベル(JSON)
     */
    public String getKanbanLabel() {
        return this.kanbanLabel;
    }

    /**
     * ラベル(JSON)を設定する。
     * 
     * @param kanbanLabel ラベル(JSON) 
     */
    public void setKanbanLabel(String kanbanLabel) {
        this.kanbanLabel = kanbanLabel;
    }

    /**
     * 更新者を取得する。
     *
     * @return 更新者
     */
    public String getUpdatePerson() {
        return this.updatePerson;
    }

    /**
     * 更新者を設定する。
     *
     * @param updatePerson 更新者
     */
    public void setUpdatePerson(String updatePerson) {
        this.updatePerson = updatePerson;
    }

    /**
     * 帳票テンプレートパス(JSON)を取得する。
     *
     * @return 帳票テンプレートパス(JSON)
     */
    public String getLedgerPath() {
        return this.ledgerPath;
    }

    /**
     * 帳票テンプレートパス(JSON)を設定する。
     *
     * @param ledgerPath 帳票テンプレートパス(JSON)
     */
    public void setLedgerPath(String ledgerPath) {
        this.ledgerPath = ledgerPath;
    }

    /**
     * 実績開始日時を取得する。
     * 
     * @return 実績開始日時
     */
    public Date getActualStartTime() {
        return actualStartTime;
    }

    /**
     * 実績完了日時を取得する。
     * 
     * @return 実績完了日時
     */
    public Date getActualCompTime() {
        return actualCompTime;
    }

    /**
     * 標準サイクルタイムを取得する。
     * 
     * @return 標準サイクルタイム
     */
    public Integer getCycleTime() {
        return cycleTime;
    }

    /**
     * 標準サイクルタイムを設定する。
     * 
     * @param cycleTime 標準サイクルタイム
     */
    public void setCycleTime(Integer cycleTime) {
        this.cycleTime = cycleTime;
    }
    
    /**
     * 内部変数を更新する。
     */
    public void updateMember() {
        this.kanbanId = this.getKanbanId();
        this.kanbanName = this.getKanbanName();
        this.kanbanSubname = this.getKanbanSubname();
        this.fkWorkflowId = this.getFkWorkflowId();
        this.startDatetime = this.getStartDatetime();
        this.compDatetime = this.getCompDatetime();
        this.fkUpdatePersonId = this.getFkUpdatePersonId();
        this.updateDatetime = this.getUpdateDatetime();
        this.kanbanStatus = this.getKanbanStatus();
        this.fkInterruptReasonId = this.getFkInterruptReasonId();
        this.fkDelayReasonId = this.getFkDelayReasonId();
        this.modelName = this.getModelName();
        this.productionNumber = this.getProductionNumber();
        this.kanbanLabel = this.getKanbanLabel();

        this.updatePerson = this.getUpdatePerson();
    }

    /**
     * ハッシュコードを返す。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.kanbanId ^ (this.kanbanId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.kanbanName);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * 
     * @param obj オブジェクト
     * @return true: 等しい(同値)、false: 異なる
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KanbanInfoEntity other = (KanbanInfoEntity) obj;
        if (!Objects.equals(this.getKanbanId(), other.getKanbanId())) {
            return false;
        }
        return Objects.equals(this.getKanbanName(), other.getKanbanName());
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("KanbanInfoEntity{")
                .append("kanbanId=").append(this.kanbanId)
                .append(", parentId=").append(this.parentId)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanSubname=").append(this.kanbanSubname)
                .append(", fkWorkflowId=").append(this.fkWorkflowId)
                .append(", workflowName=").append(this.workflowName)
                .append(", workflowRev=").append(this.workflowRev)
                .append(", startDatetime=").append(this.startDatetime)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", fkUpdatePersonId=").append(this.fkUpdatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", kanbanStatus=").append(this.kanbanStatus)
                .append(", fkInterruptReasonId=").append(this.fkInterruptReasonId)
                .append(", fkDelayReasonId=").append(this.fkDelayReasonId)
                .append(", lotQuantity=").append(this.lotQuantity)
                .append(", modelName=").append(this.modelName)
                .append(", repairNum=").append(this.repairNum)
                .append(", productionType=").append(this.productionType)
                .append(", verInfo=").append(this.verInfo)
                .append(", productionNumber=").append(this.productionNumber)
                .append(", kanbanLabel=").append(this.kanbanLabel)
                .append(", updatePerson=").append(this.updatePerson)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報をコピーする
     *
     * @return KanbanInfoEntity オブジェクト
     */
    @Override
    public KanbanInfoEntity clone() {
        KanbanInfoEntity entity = new KanbanInfoEntity();

        entity.setKanbanName(this.getKanbanName());
        entity.setWorkflowName(this.getWorkflowName());
        entity.setFkWorkflowId(this.getFkWorkflowId());
        entity.setKanbanStatus(this.getKanbanStatus());

        // 追加情報のコピー
        List<KanbanPropertyInfoEntity> cloneProperties = new java.util.LinkedList<>();
        if (Objects.nonNull(getPropertyCollection())) {
            getPropertyCollection().stream().forEach(c -> cloneProperties.add(c.clone()));
        }
        entity.setPropertyCollection(cloneProperties);

        // 工程順のコピー
        List<WorkKanbanInfoEntity> cloneWorkKanbans = new java.util.LinkedList<>();
        if (Objects.nonNull(getWorkKanbanCollection())) {
            getWorkKanbanCollection().stream().forEach(c -> cloneWorkKanbans.add(c.clone()));
        }
        entity.setWorkKanbanCollection(cloneWorkKanbans);

        // 追加工程のコピー
        List<WorkKanbanInfoEntity> cloneSeparatedWorkKanbans = new java.util.LinkedList<>();
        if (Objects.nonNull(getSeparateworkKanbanCollection())) {
            getSeparateworkKanbanCollection().stream().forEach(c -> cloneSeparatedWorkKanbans.add(c.clone()));
        }
        entity.setSeparateworkKanbanCollection(cloneSeparatedWorkKanbans);

        entity.setModelName(this.getModelName());
        entity.setProductionNumber(this.getProductionNumber());
        entity.setKanbanLabel(this.getKanbanLabel());
        entity.setCycleTime(this.getCycleTime());

        entity.setVerInfo(this.getVerInfo());

        return entity;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(KanbanInfoEntity other) {
        return equalsDisplayInfo(other, true);
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @param isCheckStatus ステータスをチェックするか(true = チェックする)  // 2019/12/25 カンバン操作対応 引数の追加
     * @return
     */
    public boolean equalsDisplayInfo(KanbanInfoEntity other, boolean isCheckStatus) {
        boolean ret = false;

        if (Objects.equals(getKanbanName(), other.getKanbanName())
                && Objects.equals(this.getWorkflowName(), other.getWorkflowName())
                && Objects.equals(this.getFkWorkflowId(), other.getFkWorkflowId())
                && (!isCheckStatus // チェックしない場合ステータスが変更されていても常にTrue
                        || Objects.equals(this.getKanbanStatus(), other.getKanbanStatus()))
                && this.propertyInfoListEquals(this.getPropertyCollection(), other.getPropertyCollection())
                && this.workKanbanListEquals(this.getWorkKanbanCollection(), other.getWorkKanbanCollection())
                && this.workKanbanListEquals(this.getSeparateworkKanbanCollection(), other.getSeparateworkKanbanCollection())
                && Objects.equals(this.getModelName(), other.getModelName())
                && Objects.equals(this.getProductionNumber(), other.getProductionNumber())) {
            return true;
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
    private boolean propertyInfoListEquals(List left, List right) {
        if (left.size() != right.size()) {
            return false;
        }

        //順番が変わるときがあるのでソート
        List<KanbanPropertyInfoEntity> a = new ArrayList<>(left);
        List<KanbanPropertyInfoEntity> b = new ArrayList<>(right);
        a.sort(Comparator.comparing(p -> p.getKanbanPropId(), Comparator.nullsFirst(Comparator.naturalOrder())));
        b.sort(Comparator.comparing(p -> p.getKanbanPropId(), Comparator.nullsFirst(Comparator.naturalOrder())));

        Iterator<KanbanPropertyInfoEntity> it1 = a.iterator();
        Iterator<KanbanPropertyInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            KanbanPropertyInfoEntity entity1 = it1.next();
            KanbanPropertyInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 工程順のリストが一致するか調べる
     *
     * @param a
     * @param b
     * @return 一致したらtrue
     */
    private boolean workKanbanListEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        java.util.Iterator<WorkKanbanInfoEntity> it1 = a.iterator();
        java.util.Iterator<WorkKanbanInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            WorkKanbanInfoEntity entity1 = it1.next();
            WorkKanbanInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }
}
