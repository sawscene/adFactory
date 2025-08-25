/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;

/**
 * 工程プロパティ情報
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class WorkPropertyInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workPropIdProperty;
    private LongProperty fkMasterIdProperty;
    private StringProperty workPropNameProperty;
    private ObjectProperty<CustomPropertyTypeEnum> workPropTypeProperty;
    private StringProperty workPropValueProperty;
    private IntegerProperty workPropOrderProperty;
    private ObjectProperty<WorkPropertyCategoryEnum> workPropCategoryProperty;
    private StringProperty workPropOptionProperty;
    private DoubleProperty workPropLowerToleranceProperty;
    private DoubleProperty workPropUpperToleranceProperty;
    private StringProperty workPropTagProperty;
    private StringProperty workPropValidationRuleProperty;
    private StringProperty workPropCheckpointProperty;

    @XmlElement(required = true)
    @JsonIgnore
    private Long workPropId;// 工程プロパティID

    @XmlElement()
    @JsonIgnore
    private Long fkMasterId;// 工程ID

    @XmlElement()
    @JsonProperty("key")
    private String workPropName;// 工程プロパティ名

    @XmlElement()
    @JsonProperty("type")
    private CustomPropertyTypeEnum workPropType;// 工程プロパティ種別

    @XmlElement()
    @JsonProperty("val")
    private String workPropValue;// 工程プロパティ値

    @XmlElement()
    @JsonProperty("disp")
    private Integer workPropOrder;// 工程プロパティ表示順

    @XmlElement()
    @JsonProperty("cat")
    private WorkPropertyCategoryEnum workPropCategory;// プロパティ種別

    @XmlElement()
    @JsonProperty("opt")
    private String workPropOption;// 付加情報

    @XmlElement()
    @JsonProperty("min")
    private Double workPropLowerTolerance;// 基準値下限

    @XmlElement()
    @JsonProperty("max")
    private Double workPropUpperTolerance;// 基準値上限

    @XmlElement()
    @JsonProperty("tag")
    private String workPropTag;// タグ

    @XmlElement()
    @JsonProperty("rules")
    private String workPropValidationRule;// 入力規則

    @XmlElement
    @JsonProperty("page")
    private Integer workSectionOrder;// 工程セクション表示順

    @XmlElement
    @JsonProperty("cp")
    private Integer workPropCheckpoint;// 進捗チェックポイント

    @XmlElement
    @JsonProperty("pg")
    private Boolean pageBreakEnabled;// ページ分割

    /**
     * コンストラクタ
     */
    public WorkPropertyInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workPropId 工程プロパティID
     * @param fkMasterId 工程ID
     * @param workPropName 工程プロパティ名
     * @param workPropType 工程プロパティ種別
     * @param workPropValue 工程プロパティ値
     * @param workPropOrder 工程プロパティ表示順
     */
    public WorkPropertyInfoEntity(Long workPropId, Long fkMasterId, String workPropName, CustomPropertyTypeEnum workPropType, String workPropValue, Integer workPropOrder) {
        //WorkPropertyCategoryEnum workPropCategory, String workPropOption, Double workPropLowerTolerance, Double workPropUpperTolerance) {
        this.workPropId = workPropId;
        this.fkMasterId = fkMasterId;
        this.workPropName = workPropName;
        this.workPropType = workPropType;
        this.workPropValue = workPropValue;
        this.workPropOrder = workPropOrder;
        //this.workPropCategory = workPropCategory;
        //this.workPropOption = workPropOption;
        //this.workPropLowerTolerance = workPropLowerTolerance;
        //this.workPropUpperTolerance = workPropUpperTolerance;
    }

    /**
     * 工程プロパティIDプロパティを取得する。
     *
     * @return 工程プロパティID
     */
    public LongProperty workPropIdProperty() {
        if (Objects.isNull(this.workPropIdProperty)) {
            this.workPropIdProperty = new SimpleLongProperty(this.workPropId);
        }
        return this.workPropIdProperty;
    }

    /**
     * 工程IDプロパティを取得する。
     *
     * @return 工程ID
     */
    public LongProperty fkMasterIdProperty() {
        if (Objects.isNull(this.fkMasterIdProperty)) {
            this.fkMasterIdProperty = new SimpleLongProperty(this.fkMasterId);
        }
        return this.fkMasterIdProperty;
    }

    /**
     * 工程プロパティ名プロパティを取得する。
     *
     * @return 工程プロパティ名
     */
    public StringProperty workPropNameProperty() {
        if (Objects.isNull(this.workPropNameProperty)) {
            this.workPropNameProperty = new SimpleStringProperty(this.workPropName);
        }
        return this.workPropNameProperty;
    }

    /**
     * 工程プロパティ種別プロパティを取得する。
     *
     * @return 工程プロパティ種別
     */
    public ObjectProperty<CustomPropertyTypeEnum> workPropTypeProperty() {
        if (Objects.isNull(this.workPropTypeProperty)) {
            this.workPropTypeProperty = new SimpleObjectProperty<>(this.workPropType);
        }
        return this.workPropTypeProperty;
    }

    /**
     * 工程プロパティ値プロパティを取得する。
     *
     * @return 工程プロパティ値
     */
    public StringProperty workPropValueProperty() {
        if (Objects.isNull(this.workPropValueProperty)) {
            this.workPropValueProperty = new SimpleStringProperty(this.workPropValue);
        }
        return this.workPropValueProperty;
    }

    /**
     * 工程プロパティ表示順プロパティを取得する。
     *
     * @return 工程プロパティ表示順
     */
    public IntegerProperty workPropOrderProperty() {
        if (Objects.isNull(this.workPropOrderProperty)) {
            this.workPropOrderProperty = new SimpleIntegerProperty(this.workPropOrder);
        }
        return this.workPropOrderProperty;
    }

    /**
     * プロパティ種別プロパティを取得する。
     *
     * @return プロパティ種別
     */
    public ObjectProperty<WorkPropertyCategoryEnum> workPropCategoryProperty() {
        if (Objects.isNull(this.workPropCategoryProperty)) {
            this.workPropCategoryProperty = new SimpleObjectProperty<>(this.getWorkPropCategory());
        }
        return this.workPropCategoryProperty;
    }

    /**
     * 付加情報プロパティを取得する。
     *
     * @return 付加情報
     */
    public StringProperty workPropOptionProperty() {
        if (Objects.isNull(this.workPropOptionProperty)) {
            this.workPropOptionProperty = new SimpleStringProperty(this.workPropOption);
        }
        return this.workPropOptionProperty;
    }

    /**
     * 基準値下限プロパティを取得する。
     *
     * @return 基準値下限
     */
    public DoubleProperty workPropLowerToleranceProperty() {
        if (Objects.isNull(this.workPropLowerToleranceProperty)) {
            this.workPropLowerToleranceProperty = new SimpleDoubleProperty(this.getWorkPropLowerTolerance());
        }
        return this.workPropLowerToleranceProperty;
    }

    /**
     * 基準値上限プロパティを取得する。
     *
     * @return 基準値上限
     */
    public DoubleProperty workPropUpperToleranceProperty() {
        if (Objects.isNull(this.workPropUpperToleranceProperty)) {
            this.workPropUpperToleranceProperty = new SimpleDoubleProperty(this.getWorkPropUpperTolerance());
        }
        return this.workPropUpperToleranceProperty;
    }

    /**
     * タグプロパティを取得する。
     *
     * @return タグ
     */
    public StringProperty workPropTagProperty() {
        if (Objects.isNull(this.workPropTagProperty)) {
            this.workPropTagProperty = new SimpleStringProperty(this.workPropTag);
        }
        return this.workPropTagProperty;
    }

    /**
     * 入力規則プロパティを取得する。
     *
     * @return 入力規則
     */
    public StringProperty workPropValidationRuleProperty() {
        if (Objects.isNull(this.workPropValidationRuleProperty)) {
            this.workPropValidationRuleProperty = new SimpleStringProperty(this.workPropValidationRule);
        }
        return this.workPropValidationRuleProperty;
    }

    /**
     * 進捗チェックポイントプロパティを取得する。
     *
     * @return 進捗チェックポイント
     */
    public StringProperty workPropCheckpointProperty() {
        if (Objects.isNull(this.workPropCheckpointProperty)) {
            if (Objects.nonNull(this.workPropCheckpoint)) {
                this.workPropCheckpointProperty = new SimpleStringProperty(StringTime.convertMillisToStringTime(this.workPropCheckpoint));
            } else {
                this.workPropCheckpointProperty = new SimpleStringProperty();
            }
        }
        return this.workPropCheckpointProperty;
    }

    /**
     * 工程プロパティIDを取得する。
     *
     * @return 工程プロパティID
     */
    public Long getWorkPropId() {
        if (Objects.nonNull(this.workPropIdProperty)) {
            return this.workPropIdProperty.get();
        }
        return this.workPropId;
    }

    /**
     * 工程プロパティIDを設定する。
     *
     * @param workPropId 工程プロパティID
     */
    public void setWorkPropId(Long workPropId) {
        if (Objects.nonNull(this.workPropIdProperty)) {
            this.workPropIdProperty.set(workPropId);
        } else {
            this.workPropId = workPropId;
        }
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getFkMasterId() {
        if (Objects.nonNull(this.fkMasterIdProperty)) {
            return this.fkMasterIdProperty.get();
        }
        return this.fkMasterId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param fkMasterId 工程ID
     */
    public void setFkMasterId(Long fkMasterId) {
        if (Objects.nonNull(this.fkMasterIdProperty)) {
            this.fkMasterIdProperty.set(fkMasterId);
        } else {
            this.fkMasterId = fkMasterId;
        }
    }

    /**
     * 工程プロパティ名を取得する。
     *
     * @return 工程プロパティ名
     */
    public String getWorkPropName() {
        if (Objects.nonNull(this.workPropNameProperty)) {
            return this.workPropNameProperty.get();
        }
        return this.workPropName;
    }

    /**
     * 工程プロパティ名を設定する。
     *
     * @param workPropName 工程プロパティ名
     */
    public void setWorkPropName(String workPropName) {
        if (Objects.nonNull(this.workPropNameProperty)) {
            this.workPropNameProperty.set(workPropName);
        } else {
            this.workPropName = workPropName;
        }
    }

    /**
     * 工程プロパティ種別を取得する。
     *
     * @return 工程プロパティ種別
     */
    public CustomPropertyTypeEnum getWorkPropType() {
        if (Objects.nonNull(this.workPropTypeProperty)) {
            return this.workPropTypeProperty.get();
        }
        return this.workPropType;
    }

    /**
     * 工程プロパティ種別を設定する。
     *
     * @param workPropType 工程プロパティ種別
     */
    public void setWorkPropType(CustomPropertyTypeEnum workPropType) {
        if (Objects.nonNull(this.workPropTypeProperty)) {
            this.workPropTypeProperty.set(workPropType);
        } else {
            this.workPropType = workPropType;
        }
    }

    /**
     * 工程プロパティ値を取得する。
     *
     * @return 工程プロパティ値
     */
    public String getWorkPropValue() {
        if (Objects.nonNull(this.workPropValueProperty)) {
            return this.workPropValueProperty.get();
        }
        return this.workPropValue;
    }

    /**
     * 工程プロパティ値を設定する。
     *
     * @param workPropValue 工程プロパティ値
     */
    public void setWorkPropValue(String workPropValue) {
        if (Objects.nonNull(this.workPropValueProperty)) {
            this.workPropValueProperty.set(workPropValue);
        } else {
            this.workPropValue = workPropValue;
        }
    }

    /**
     * 工程プロパティ表示順を取得する。
     *
     * @return 工程プロパティ表示順
     */
    public Integer getWorkPropOrder() {
        if (Objects.nonNull(this.workPropOrderProperty)) {
            return this.workPropOrderProperty.get();
        }
        return this.workPropOrder;
    }

    /**
     * 工程プロパティ表示順を設定する。
     *
     * @param workPropOrder 工程プロパティ表示順
     */
    public void setWorkPropOrder(Integer workPropOrder) {
        if (Objects.nonNull(this.workPropOrderProperty)) {
            this.workPropOrderProperty.set(workPropOrder);
        } else {
            this.workPropOrder = workPropOrder;
        }
    }

    /**
     * プロパティ種別を取得する。
     *
     * @return プロパティ種別
     */
    public WorkPropertyCategoryEnum getWorkPropCategory() {
        if (Objects.nonNull(this.workPropCategoryProperty)) {
            return this.workPropCategoryProperty.get();
        }
        return this.workPropCategory;
    }

    /**
     * プロパティ種別を設定する。
     *
     * @param workPropCategory プロパティ種別
     */
    public void setWorkPropCategory(WorkPropertyCategoryEnum workPropCategory) {
        if (Objects.nonNull(this.workPropCategoryProperty)) {
            this.workPropCategoryProperty.set(workPropCategory);
        } else {
            this.workPropCategory = workPropCategory;
        }
    }

    /**
     * 付加情報を取得する。
     *
     * @return 付加情報
     */
    public String getWorkPropOption() {
        if (Objects.nonNull(this.workPropOptionProperty)) {
            return this.workPropOptionProperty.get();
        }
        return this.workPropOption;
    }

    /**
     * 付加情報を設定する。
     *
     * @param workPropOption 付加情報
     */
    public void setWorkPropOption(String workPropOption) {
        if (Objects.nonNull(this.workPropOptionProperty)) {
            this.workPropOptionProperty.set(workPropOption);
        } else {
            this.workPropOption = workPropOption;
        }
    }

    /**
     * 基準値下限を取得する。
     *
     * @return 基準値下限
     */
    public Double getWorkPropLowerTolerance() {
        if (Objects.nonNull(workPropLowerToleranceProperty)) {
            return workPropLowerToleranceProperty.get();
        }
        if (Objects.isNull(this.workPropLowerTolerance)) {
            this.workPropLowerTolerance = Double.NaN;
        }
        return this.workPropLowerTolerance;
    }

    /**
     * 基準値下限を設定する。
     *
     * @param workPropLowerTolerance 基準値下限
     */
    public void setWorkPropLowerTolerance(Double workPropLowerTolerance) {
        if (Objects.nonNull(this.workPropLowerToleranceProperty)) {
            this.workPropLowerToleranceProperty.set(workPropLowerTolerance);
        } else {
            this.workPropLowerTolerance = workPropLowerTolerance;
        }
    }

    /**
     * 基準値上限を取得する。
     *
     * @return 基準値上限
     */
    public Double getWorkPropUpperTolerance() {
        if (Objects.nonNull(workPropUpperToleranceProperty)) {
            return workPropUpperToleranceProperty.get();
        }
        if (Objects.isNull(this.workPropUpperTolerance)) {
            this.workPropUpperTolerance = Double.NaN;
        }
        return this.workPropUpperTolerance;
    }

    /**
     * 基準値上限を設定する。
     *
     * @param workPropUpperTolerance 基準値上限
     */
    public void setWorkPropUpperTolerance(Double workPropUpperTolerance) {
        if (Objects.nonNull(this.workPropUpperToleranceProperty)) {
            this.workPropUpperToleranceProperty.set(workPropUpperTolerance);
        } else {
            this.workPropUpperTolerance = workPropUpperTolerance;
        }
    }

    /**
     * タグを取得する。
     *
     * @return タグ
     */
    public String getWorkPropTag() {
        if (Objects.nonNull(this.workPropTagProperty)) {
            return this.workPropTagProperty.get();
        }
        return this.workPropTag;
    }

    /**
     * タグを設定する。
     *
     * @param workPropTag タグ
     */
    public void setWorkPropTag(String workPropTag) {
        if (Objects.nonNull(this.workPropTagProperty)) {
            this.workPropTagProperty.set(workPropTag);
        } else {
            this.workPropTag = workPropTag;
        }
    }

    /**
     * 入力規則を取得する。
     *
     * @return 入力規則
     */
    public String getWorkPropValidationRule() {
        if (Objects.nonNull(this.workPropValidationRuleProperty)) {
            return this.workPropValidationRuleProperty.get();
        }
        return workPropValidationRule;
    }

    /**
     * 入力規則を設定する。
     *
     * @param workPropValidationRule 入力規則
     */
    public void setWorkPropValidationRule(String workPropValidationRule) {
        if (Objects.nonNull(this.workPropValidationRuleProperty)) {
            this.workPropValidationRuleProperty.set(workPropValidationRule);
        } else {
            this.workPropValidationRule = workPropValidationRule;
        }
    }

    /**
     * 工程セクション表示順を取得する。
     *
     * @return 工程セクション表示順
     */
    public Integer getWorkSectionOrder() {
        return this.workSectionOrder;
    }

    /**
     * 工程セクション表示順を設定する。
     *
     * @param workSectionOrder 工程セクション表示順
     */
    public void setWorkSectionOrder(Integer workSectionOrder) {
        this.workSectionOrder = workSectionOrder;
    }

    /**
     * 進捗チェックポイント
     * @return 進捗チェックポイント
     */
    public Integer getWorkPropCheckpoint() {
        if (Objects.nonNull(this.workPropCheckpointProperty)) {
            if (StringUtils.isEmpty(this.workPropCheckpointProperty.get())) {
                return null;
            }
            long time = StringTime.convertStringTimeToMillis(this.workPropCheckpointProperty.get());
            return (int) time;
        }
        return this.workPropCheckpoint;
    }

    /**
     * 進捗チェックポイントを設定する。
     *
     * @param workPropCheckpoint 進捗チェックポイント
     */
    public void setWorkPropCheckpoint(Integer workPropCheckpoint) {
        if (Objects.nonNull(this.workPropCheckpointProperty)) {
            this.workPropCheckpointProperty.set(StringTime.convertMillisToStringTime(workPropCheckpoint));
        } else {
            this.workPropCheckpoint = workPropCheckpoint;
        }
    }

    /**
     * ページ分割が有効かどうかを取得する。
     *
     * @return ページ分割
     */
    public Boolean getPageBreakEnabled() {
        return Boolean.TRUE.equals(this.pageBreakEnabled);
    }

    /**
     * ページ分割の有効・無効を設定する。
     *
     * @param pageBreakEnabled ページ分割
     */
    public void setPageBreakEnabled(Boolean pageBreakEnabled) {
        this.pageBreakEnabled = pageBreakEnabled;
    }

    /**
     * 基準値の範囲表示用文字列を取得する。
     *
     * @return 基準値の範囲表示用文字列
     */
    @JsonIgnore
    public String getDisplayTolerance() {
        String displayText = "";
        if (!Double.isNaN(this.getWorkPropLowerTolerance())) {
            if (!Double.isNaN(this.getWorkPropUpperTolerance())) {
                displayText = String.format("%s - %s", getWorkPropLowerTolerance(), getWorkPropUpperTolerance());
            } else {
                displayText = String.format("%s -", getWorkPropLowerTolerance());
            }
        } else if (!Double.isNaN(this.getWorkPropUpperTolerance())) {
            displayText = String.format("  - %s", getWorkPropUpperTolerance());
        }
        return displayText;
    }

    /**
     * 内部変数を更新する。
     */
    public void updateMember() {
        this.workPropId = this.getWorkPropId();
        this.fkMasterId = this.getFkMasterId();
        this.workPropName = this.getWorkPropName();
        this.workPropType = this.getWorkPropType();
        this.workPropValue = this.getWorkPropValue();
        this.workPropOrder = this.getWorkPropOrder();
        this.workPropCategory = this.getWorkPropCategory();
        this.workPropOption = this.getWorkPropOption();
        this.workPropLowerTolerance = this.getWorkPropLowerTolerance();
        this.workPropUpperTolerance = this.getWorkPropUpperTolerance();
        this.workPropTag = this.getWorkPropTag();
        this.workPropValidationRule = this.getWorkPropValidationRule();
        this.workPropCheckpoint = this.getWorkPropCheckpoint();
        this.pageBreakEnabled = this.getPageBreakEnabled();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.workPropId ^ (this.workPropId >>> 32));
        hash = 83 * hash + (int) (this.fkMasterId ^ (this.fkMasterId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.workPropName);
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
        final WorkPropertyInfoEntity other = (WorkPropertyInfoEntity) obj;
        if (!Objects.equals(this.getWorkPropId(), other.getWorkPropId())) {
            return false;
        }
        if (!Objects.equals(this.getFkMasterId(), other.getFkMasterId())) {
            return false;
        }
        return Objects.equals(this.getWorkPropName(), other.getWorkPropName());
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WorkPropertyInfoEntity{")
                .append("workPropId=").append(this.workPropId)
                .append(", fkMasterId=").append(this.fkMasterId)
                .append(", workPropName=").append(this.workPropName)
                .append(", workPropType=").append(this.workPropType)
                .append(", workPropValue=").append(this.workPropValue)
                .append(", workPropOrder=").append(this.workPropOrder)
                .append(", workPropCategory=").append(this.workPropCategory)
                .append(", workPropOption=").append(this.workPropOption)
                .append(", workPropLowerTolerance=").append(this.workPropLowerTolerance)
                .append(", workPropUpperTolerance=").append(this.workPropUpperTolerance)
                .append(", workPropTag=").append(this.workPropTag)
                .append(", workPropValidationRule=").append(this.workPropValidationRule)
                .append(", workSectionOrder=").append(this.workSectionOrder)
                .append(", workPropCheckpoint=").append(this.workPropCheckpoint)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public WorkPropertyInfoEntity clone() {
        WorkPropertyInfoEntity entity = new WorkPropertyInfoEntity();

        entity.setWorkPropName(this.getWorkPropName());
        entity.setWorkPropCategory(this.getWorkPropCategory());
        entity.setWorkPropType(this.getWorkPropType());
        entity.setWorkPropValue(this.getWorkPropValue());
        entity.setWorkPropOrder(this.getWorkPropOrder());
        entity.setWorkPropOption(this.getWorkPropOption());
        entity.setWorkPropTag(this.getWorkPropTag());
        entity.setWorkPropUpperTolerance(this.getWorkPropUpperTolerance());
        entity.setWorkPropLowerTolerance(this.getWorkPropLowerTolerance());
        entity.setWorkPropCheckpoint(this.getWorkPropCheckpoint());
        entity.setWorkPropValidationRule(this.getWorkPropValidationRule());
        entity.setWorkSectionOrder(this.getWorkSectionOrder());
        entity.setPageBreakEnabled(this.getPageBreakEnabled());
        return entity;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(WorkPropertyInfoEntity other) {
        if (Objects.equals(getWorkPropName(), other.getWorkPropName())
                && Objects.equals(this.getWorkPropCategory(), other.getWorkPropCategory())
                && Objects.equals(this.getWorkPropType(), other.getWorkPropType())
                && Objects.equals(this.getWorkPropValue(), other.getWorkPropValue())
                && Objects.equals(this.getWorkPropOption(), other.getWorkPropOption())
                && Objects.equals(this.getWorkPropTag(), other.getWorkPropTag())
                && Objects.equals(this.getWorkPropUpperTolerance(), other.getWorkPropUpperTolerance())
                && Objects.equals(this.getWorkPropLowerTolerance(), other.getWorkPropLowerTolerance())
                && Objects.equals(this.getWorkPropCheckpoint(), other.getWorkPropCheckpoint())
                && Objects.equals(this.getWorkPropValidationRule(), other.getWorkPropValidationRule())
                && Objects.equals(this.getWorkSectionOrder(), other.getWorkSectionOrder())) {
            return true;
        }
        return false;
    }
}
