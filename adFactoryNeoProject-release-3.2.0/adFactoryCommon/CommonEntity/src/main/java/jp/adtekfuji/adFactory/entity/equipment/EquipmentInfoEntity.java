/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.equipment;

import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import jp.adtekfuji.adFactory.entity.organization.ConfigInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.enumerate.TermUnitEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 設備情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipment")
public class EquipmentInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty equipmentIdProperty;
    @XmlTransient
    private LongProperty parentIdProperty;
    @XmlTransient
    private StringProperty equipmentIdentifyProperty;
    @XmlTransient
    private StringProperty equipmentNameProperty;
    @XmlTransient
    private StringProperty langIdsProperty;
    @XmlTransient
    private LongProperty equipmentTypeIdProperty;
    @XmlTransient
    private LongProperty updatePersonIdProperty;
    @XmlTransient
    private ObjectProperty<Date> updateDateTimeProperty;

    @XmlTransient
    private BooleanProperty calFlagProperty;
    @XmlTransient
    private ObjectProperty<LocalDate> calNextDateProperty;
    @XmlTransient
    private ObjectProperty<LocalDate> calLastDateProperty;
    @XmlTransient
    private IntegerProperty calWarningDaysProperty;
    @XmlTransient
    private IntegerProperty calTermProperty;
    @XmlTransient
    private ObjectProperty<TermUnitEnum> calTermUnitProperty;
    @XmlTransient
    private LongProperty calPersonIdProperty;
    @XmlTransient
    private StringProperty configProperty;

    @XmlTransient
    private Function<Long, String> licenseDisplay = count -> count.toString();
    @XmlTransient
    private Function<Long, String> liteDisplay = count -> count.toString();
    @XmlTransient
    private Function<Long, String> reporterDisplay = count -> count.toString();

    @XmlElement(required = true)
    private Long equipmentId;// 設備ID
    @XmlElement()
    private Long parentId;// 親設備ID (parentEquipmentId)
    @XmlElement()
    private String equipmentIdentify;// 設備識別名
    @XmlElement()
    private String equipmentName;// 設備名
    @XmlElement()
    private Long equipmentTypeId;// 設備種別ID
    @XmlElement()
    private String langIds;// 言語(JSON形式)
    @XmlElement()
    private Long updatePersonId;// 更新者(組織ID)
    @XmlElement()
    private Date updateDatetime;// 更新日時
    @XmlElement()
    private Boolean removeFlag;// 削除フラグ
    @XmlElement()
    private Long childCount = 0L;// 子設備数
    @XmlElement()
    private Long licenseCount = 0L;     // ライセンス数
    @XmlElement()
    private String config;// 設定

    @XmlTransient
    private List<EquipmentPropertyInfoEntity> propertyInfoCollection = null;// 設備プロパティ一覧

    @XmlTransient
    private List<EquipmentSettingInfoEntity> settingInfoCollection = new ArrayList<>();// 設備設定一覧

    @XmlElement()
    private Boolean calFlag;// 機器校正有無
    @XmlElement()
    //@XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    private Date calNextDate;// 次回校正日
    @XmlElement()
    //@XmlJavaTypeAdapter(LocalDateXmlAdapter.class)
    private Date calLastDate;// 最終校正日
    @XmlElement()
    private Integer calWarningDays;// 警告表示日数
    @XmlElement()
    private Integer calTerm;// 校正間隔
    @XmlElement()
    private TermUnitEnum calTermUnit;// 間隔単位
    @XmlElement()
    private Long calPersonId;// 校正実施者

    @XmlElement()
    private String ipv4Address = "";// IPv4アドレス
    @XmlElement()
    private Boolean workProgressFlag = false;// 工程進捗フラグ
    @XmlElement()
    private String pluginName = "";// プラグイン名

    @XmlElement()
    private String equipmentAddInfo;// 追加情報(JSON)
    @XmlElement()
    private String serviceInfo;// サービス情報(JSON)

    @XmlElementWrapper(name = "localeFileInfos")
    @XmlElement(name = "localeFileInfo")
    private List<LocaleFileInfoEntity> localeFileInfoCollection = new LinkedList<>(); // 言語ファイル

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    @XmlElement()
    private Long liteCount = 0L;        // Lite ライセンス数
    @XmlElement()
    private Long reporterCount = 0L;    // Reporter ライセンス数
    @XmlElement()
    private EquipmentTypeEntity equipmentType; // 設備種別

    /**
     * コンストラクタ
     */
    public EquipmentInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param equipmentId 設備ID
     * @param equipmentIdentify 設備識別名
     * @param equipmentName 設備名
     * @param equipmentTypeId 設備種別ID
     */
    public EquipmentInfoEntity(Long equipmentId, String equipmentIdentify, String equipmentName, Long equipmentTypeId) {
        this.equipmentId = equipmentId;
        this.equipmentIdentify = equipmentIdentify;
        this.equipmentName = equipmentName;
        this.equipmentTypeId = equipmentTypeId;
    }
    
    /**
     * 設備IDプロパティを取得する。
     *
     * @return 設備ID
     */
    public LongProperty equipmentIdProperty() {
        if (Objects.isNull(this.equipmentIdProperty)) {
            this.equipmentIdProperty = new SimpleLongProperty(this.equipmentId);
        }
        return this.equipmentIdProperty;
    }

    /**
     * 親設備IDプロパティを取得する。
     *
     * @return 親設備ID
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return parentIdProperty;
    }

    /**
     * 設備識別名プロパティを取得する。
     *
     * @return 設備識別名
     */
    public StringProperty equipmentIdentifyProperty() {
        if (Objects.isNull(this.equipmentIdentifyProperty)) {
            this.equipmentIdentifyProperty = new SimpleStringProperty(this.equipmentIdentify);
        }
        return this.equipmentIdentifyProperty;
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
     * 設備種別プロパティを取得する。
     *
     * @return 設備種別
     */
    public LongProperty equipmentTypeIdProperty() {
        if (Objects.isNull(this.equipmentTypeIdProperty)) {
            this.equipmentTypeIdProperty = new SimpleLongProperty(this.equipmentTypeId);
        }
        return this.equipmentTypeIdProperty;
    }

    /**
     * 言語プロパティを取得する。
     *
     * @return 言語
     */
    public StringProperty langIdsProperty() {
        if (Objects.isNull(this.langIdsProperty)) {
            this.langIdsProperty = new SimpleStringProperty(this.langIds);
        }
        return this.langIdsProperty;
    }


    /**
     * 更新者(組織ID)プロパティを取得する。
     *
     * @return 更新者(組織ID)
     */
    public LongProperty updatePersonIdProperty() {
        if (Objects.isNull(this.updatePersonIdProperty)) {
            this.updatePersonIdProperty = new SimpleLongProperty(this.updatePersonId);
        }
        return this.updatePersonIdProperty;
    }

    /**
     * 更新日時プロパティを取得する。
     *
     * @return 更新日時
     */
    public ObjectProperty<Date> updateDateTimeProperty() {
        if (Objects.isNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty = new SimpleObjectProperty(this.updateDatetime);
        }
        return this.updateDateTimeProperty;
    }

    /**
     * 機器校正有無プロパティを取得する。
     *
     * @return 機器校正有無
     */
    public BooleanProperty calFlagProperty() {
        if (Objects.isNull(this.calFlagProperty)) {
            this.calFlagProperty = new SimpleBooleanProperty(this.calFlag);
        }
        return this.calFlagProperty;
    }

    /**
     * 次回校正日プロパティを取得する。
     *
     * @return 次回校正日
     */
    public ObjectProperty<LocalDate> calNextDateProperty() {
        if (Objects.isNull(this.calNextDateProperty)) {
            this.calNextDateProperty = new SimpleObjectProperty(this.calNextDate);
        }
        return this.calNextDateProperty;
    }

    /**
     * 最終校正日プロパティを取得する。
     *
     * @return 最終校正日
     */
    public ObjectProperty<LocalDate> calLastDateProperty() {
        if (Objects.isNull(this.calLastDateProperty)) {
            this.calLastDateProperty = new SimpleObjectProperty(this.calLastDate);
        }
        return this.calLastDateProperty;
    }

    /**
     * 警告表示日数プロパティを取得する。
     *
     * @return 警告表示日数
     */
    public IntegerProperty calWarningDaysProperty() {
        if (Objects.isNull(this.calWarningDaysProperty)) {
            this.calWarningDaysProperty = new SimpleIntegerProperty(this.calWarningDays);
        }
        return this.calWarningDaysProperty;
    }

    /**
     * 校正間隔プロパティを取得する。
     *
     * @return 校正間隔
     */
    public IntegerProperty calTermProperty() {
        if (Objects.isNull(this.calTermProperty)) {
            this.calTermProperty = new SimpleIntegerProperty(this.calTerm);
        }
        return this.calTermProperty;
    }

    /**
     * 間隔単位プロパティを取得する。
     *
     * @return 間隔単位
     */
    public ObjectProperty calTermUnitProperty() {
        if (Objects.isNull(this.calTermUnitProperty)) {
            this.calTermUnitProperty = new SimpleObjectProperty(this.calTermUnit);
        }
        return this.calTermUnitProperty;
    }

    /**
     * 校正実施者(組織ID)プロパティを取得する。
     *
     * @return 校正実施者(組織ID)
     */
    public LongProperty calPersonIdProperty() {
        if (Objects.isNull(this.calPersonIdProperty)) {
            this.calPersonIdProperty = new SimpleLongProperty(this.calPersonId);
        }
        return this.calPersonIdProperty;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getEquipmentId() {
        if (Objects.nonNull(this.equipmentIdProperty)) {
            return this.equipmentIdProperty.get();
        }
        return this.equipmentId;
    }

    /**
     * 設備IDを設定する。
     *
     * @param equipmentId 設備ID
     */
    public void setEquipmentId(Long equipmentId) {
        if (Objects.nonNull(this.equipmentIdProperty)) {
            this.equipmentIdProperty.set(equipmentId);
        } else {
            this.equipmentId = equipmentId;
        }
    }

    /**
     * 親設備IDを取得する。
     *
     * @return 親設備ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * 親設備IDを設定する。
     *
     * @param parentId 親設備ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentify() {
        if (Objects.nonNull(this.equipmentIdentifyProperty)) {
            return this.equipmentIdentifyProperty.get();
        }
        return this.equipmentIdentify;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentify 設備識別名
     */
    public void setEquipmentIdentify(String equipmentIdentify) {
        if (Objects.nonNull(this.equipmentIdentifyProperty)) {
            this.equipmentIdentifyProperty.set(equipmentIdentify);
        } else {
            this.equipmentIdentify = equipmentIdentify;
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
     * 設備種別IDを取得する。
     *
     * @return 設備種別ID
     */
    public Long getEquipmentType() {
        if (Objects.nonNull(this.equipmentTypeIdProperty)) {
            return this.equipmentTypeIdProperty.get();
        }
        return this.equipmentTypeId;
    }

    /**
     * 設備種別IDを設定する。
     *
     * @param equipmentType 設備種別ID
     */
    public void setEquipmentType(Long equipmentType) {
        if (Objects.nonNull(this.equipmentTypeIdProperty)) {
            this.equipmentTypeIdProperty.set(equipmentType);
        } else {
            this.equipmentTypeId = equipmentType;
        }
    }

    /**
     * 言語を取得する。
     *
     * @return 言語
     */
    public String getLangIds() {
        if (Objects.nonNull(this.langIdsProperty)) {
            return this.langIdsProperty.get();
        }
        return this.langIds;
    }

    /**
     * 言語を設定する。
     *
     * @param langIds 言語
     */
    public void setLangIds(String langIds) {
        if (Objects.nonNull(this.langIdsProperty)) {
            this.langIdsProperty.set(langIds);
        } else {
            this.langIds = langIds;
        }
    }


    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            return this.updatePersonIdProperty.get();
        }
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
     */
    public void setUpdatePersonId(Long updatePersonId) {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            this.updatePersonIdProperty.set(updatePersonId);
        } else {
            this.updatePersonId = updatePersonId;
        }
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDateTime() {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            return this.updateDateTimeProperty.get();
        }
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDateTime 更新日時
     */
    public void setUpdateDateTime(Date updateDateTime) {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty.set(updateDateTime);
        } else {
            this.updateDatetime = updateDateTime;
        }
    }

    /**
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ (true:削除, false:未削除)
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    /**
     * 論理削除フラグを設定する。
     *
     * @param removeFlag 論理削除フラグ (true:削除, false:未削除)
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 子設備数を取得する。
     *
     * @return 子設備数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子設備数を設定する。
     *
     * @param childCount 子設備数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    /**
     * ライセンス数を取得する。
     *
     * @return ライセンス数
     */
    public Long getLicenseCount() {
        return this.licenseCount;
    }

    /**
     * ライセンス数を設定する。
     *
     * @param terminalCount ライセンス数
     */
    public void setLicenseCount(Long terminalCount) {
        this.licenseCount = terminalCount;
    }

    /**
     * Lite ライセンス数を取得する。
     * 
     * @return 
     */
    public Long getLiteCount() {
        return liteCount;
    }

    /**
     * Lite ライセンス数を設定する。
     * 
     * @param liteCount 
     */
    public void setLiteCount(Long liteCount) {
        this.liteCount = liteCount;
    }

    /**
     * Reporter ライセンス数を取得する。
     * 
     * @return 
     */
    public Long getReporterCount() {
        return reporterCount;
    }

    /**
     * Reporter ライセンス数を設定する。
     * 
     * @param reporterCount 
     */
    public void setReporterCount(Long reporterCount) {
        this.reporterCount = reporterCount;
    }    

    /**
     * ライセンス数を文字列表現を取得する。
     * 
     * @param isDisplayLicense
     * @param isLiteOption
     * @param isReporterOption
     * @return 
     */
    public String getString(boolean isDisplayLicense, boolean isLiteOption, boolean isReporterOption) {
        if(!isDisplayLicense) {
            return getEquipmentName();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(getEquipmentName());
        sb.append(" (");
        sb.append(licenseDisplay.apply(this.licenseCount));
        sb.append(", ");
        sb.append(isLiteOption ? liteDisplay.apply(this.liteCount) : 0);
        sb.append(", ");
        sb.append(isReporterOption ? reporterDisplay.apply(this.reporterCount) : 0);
        sb.append(")");

        return sb.toString();
    }

    /**
     * ライセンス数の表示文字列を返す関数を設定する。
     * 
     * @param licenseDisplay 
     */
    public void setLicenseDisplay(Function<Long, String> licenseDisplay) {
        this.licenseDisplay = licenseDisplay;
    }

    /**
     * Lite ライセンス数の表示文字列を返す関数を設定する。
     * 
     * @param liteDisplay 
     */
    public void setLiteDisplay(Function<Long, String> liteDisplay) {
        this.liteDisplay = liteDisplay;
    }

    /**
     * Reporter ライセンス数の表示文字列を返す関数を設定する。
     * 
     * @param reporterDisplay 
     */
    public void setReporterDisplay(Function<Long, String> reporterDisplay) {
        this.reporterDisplay = reporterDisplay;
    }

    /**
     * 設備プロパティ一覧を取得する。
     *
     * @return 設備プロパティ一覧
     */
    public List<EquipmentPropertyInfoEntity> getPropertyInfoCollection() {
        
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if(Objects.isNull(this.propertyInfoCollection)){
            // 変換した結果をエンティティにセットする
            this.setPropertyInfoCollection(JsonUtils.jsonToObjects(this.getEquipmentAddInfo(), EquipmentPropertyInfoEntity[].class));
        }
        
        return this.propertyInfoCollection;
    }

    /**
     * 設備プロパティ一覧を設定する。
     *
     * @param propertyInfoCollection 設備プロパティ一覧
     */
    public void setPropertyInfoCollection(List<EquipmentPropertyInfoEntity> propertyInfoCollection) {
        this.propertyInfoCollection = propertyInfoCollection;
    }

    /**
     * 設備設定一覧を取得する。
     *
     * @return 設備設定一覧
     */
    public List<EquipmentSettingInfoEntity> getSettingInfoCollection() {
        return this.settingInfoCollection;
    }

    /**
     * 設備設定一覧を設定する。
     *
     * @param settingInfoCollection 設備設定一覧
     */
    public void setSettingInfoCollection(List<EquipmentSettingInfoEntity> settingInfoCollection) {
        this.settingInfoCollection = settingInfoCollection;
    }

    /**
     * 設備プロパティを取得する。
     *
     * @param name 設備プロパティ名
     * @return 設備プロパティ
     */
    public StringProperty getPropertyValue(String name) {
        if (Objects.nonNull(this.propertyInfoCollection)) {
            for (EquipmentPropertyInfoEntity prop : this.propertyInfoCollection) {
                if (prop.getEquipmentPropName().equals(name)) {
                    return prop.equipmentPropValueProperty();
                }
            }
        }
        return new SimpleStringProperty();
    }

    /**
     * 設備設定を取得する。
     *
     * @param name 設備設定名
     * @return 設備設定
     */
    public StringProperty getSettingValue(String name) {
        if (Objects.nonNull(this.settingInfoCollection)) {
            for (EquipmentSettingInfoEntity setting : this.settingInfoCollection) {
                if (setting.getEquipmentSettingName().equals(name)) {
                    return setting.equipmentSettingValueProperty();
                }
            }
        }
        return new SimpleStringProperty();
    }

    /**
     * 機器校正有無を取得する。
     *
     * @return 機器校正有無
     */
    public Boolean getCalFlag() {
        if (Objects.nonNull(this.calFlagProperty)) {
            return this.calFlagProperty.get();
        }
        return this.calFlag;
    }

    /**
     * 機器校正有無を設定する。
     *
     * @param enableCalib 機器校正有無
     */
    public void setCalFlag(Boolean enableCalib) {
        if (Objects.nonNull(this.calFlagProperty)) {
            this.calFlagProperty.set(enableCalib);
        } else {
            this.calFlag = enableCalib;
        }
    }

    /**
     * 次回校正日を取得する。
     *
     * @return 次回校正日
     */
    public Date getCalNextDate() {
        if (Objects.nonNull(this.calNextDateProperty)) {
            return DateUtils.toDate(this.calNextDateProperty.get());
        }
        return this.calNextDate;
    }

    /**
     * 次回校正日を設定する。
     *
     * @param calibDate 次回校正日
     */
    public void setCalNextDate(Date calibDate) {
        if (Objects.nonNull(this.calNextDateProperty)) {
            this.calNextDateProperty.set(DateUtils.toLocalDate(calibDate));
        } else {
            this.calNextDate = calibDate;
        }
    }

    /**
     * 最終校正日を取得する。
     *
     * @return 最終校正日
     */
    public Date getCalLastDate() {
        if (Objects.nonNull(this.calLastDateProperty)) {
            return DateUtils.toDate(this.calLastDateProperty.get());
        }
        return this.calLastDate;
    }

    /**
     * 最終校正日を設定する。
     *
     * @param prevCalibDate 最終校正日
     */
    public void setCalLastDate(Date prevCalibDate) {
        if (Objects.nonNull(this.calLastDateProperty)) {
            this.calLastDateProperty.set(DateUtils.toLocalDate(prevCalibDate));
        } else {
            this.calLastDate = prevCalibDate;
        }
    }

    /**
     * 警告表示日数を取得する。
     *
     * @return 警告表示日数
     */
    public Integer getCalWarningDays() {
        if (Objects.nonNull(this.calWarningDaysProperty)) {
            return this.calWarningDaysProperty.get();
        }
        return this.calWarningDays;
    }

    /**
     * 警告表示日数を設定する。
     *
     * @param warningDays 警告表示日数
     */
    public void setCalWarningDays(Integer warningDays) {
        if (Objects.nonNull(this.calWarningDaysProperty)) {
            this.calWarningDaysProperty.set(warningDays);
        } else {
            this.calWarningDays = warningDays;
        }
    }

    /**
     * 校正間隔を取得する。
     *
     * @return 校正間隔
     */
    public Integer getCalTerm() {
        if (Objects.nonNull(this.calTermProperty)) {
            return this.calTermProperty.get();
        }
        return this.calTerm;
    }

    /**
     * 校正間隔を設定する。
     *
     * @param cycleValue 校正間隔
     */
    public void setCalTerm(Integer cycleValue) {
        if (Objects.nonNull(this.calTermProperty)) {
            this.calTermProperty.set(cycleValue);
        } else {
            this.calTerm = cycleValue;
        }
    }

    /**
     * 間隔単位を取得する。
     *
     * @return 間隔単位
     */
    public TermUnitEnum getCalTermUnit() {
        if (Objects.nonNull(this.calTermUnitProperty)) {
            return this.calTermUnitProperty.get();
        }
        return this.calTermUnit;
    }

    /**
     * 間隔単位を設定する。
     *
     * @param cycleType 間隔単位
     */
    public void setCalTermUnit(TermUnitEnum cycleType) {
        if (Objects.nonNull(this.calTermUnitProperty)) {
            this.calTermUnitProperty.set(cycleType);
        } else {
            this.calTermUnit = cycleType;
        }
    }

    /**
     * 校正実施者(組織ID)を取得する。
     *
     * @return 校正実施者(組織ID)
     */
    public Long getCalPersonId() {
        if (Objects.nonNull(this.calPersonIdProperty)) {
            return this.calPersonIdProperty.get();
        }
        return this.calPersonId;
    }

    /**
     * 校正実施者(組織ID)を設定する。
     *
     * @param calibInspector 校正実施者(組織ID)
     */
    public void setCalPersonId(Long calibInspector) {
        if (Objects.nonNull(this.calPersonIdProperty)) {
            this.calPersonIdProperty.set(calibInspector);
        } else {
            this.calPersonId = calibInspector;
        }
    }

    /**
     * IPv4アドレスを取得する。
     *
     * @return IPv4アドレス
     */
    public String getIpv4Address() {
        return ipv4Address;
    }

    /**
     * IPv4アドレスを設定する。
     *
     * @param ipv4Address IPv4アドレス
     */
    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    /**
     * 工程進捗フラグを取得する。
     *
     * @return 工程進捗フラグ
     */
    public Boolean getWorkProgressFlag() {
        return workProgressFlag;
    }

    /**
     * 工程進捗フラグを設定する。
     *
     * @param workProgressFlag 工程進捗フラグ
     */
    public void setWorkProgressFlag(Boolean workProgressFlag) {
        this.workProgressFlag = workProgressFlag;
    }

    /**
     * プラグイン名を取得する。
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * プラグイン名を設定する。
     *
     * @param pluginName プラグイン名
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getEquipmentAddInfo() {
        return this.equipmentAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param equipmentAddInfo 追加情報(JSON)
     */
    public void setEquipmentAddInfo(String equipmentAddInfo) {
        this.equipmentAddInfo = equipmentAddInfo;
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
     * 言語ファイルを取得する。
     *
     * @return 言語ファイル
     */
    public List<LocaleFileInfoEntity> getLocaleFileInfoCollection() {
        return this.localeFileInfoCollection;
    }

    /**
     * 言語ファイルを設定する。
     *
     * @param localeFileInfoCollection 言語ファイル
     */
    public void setLocaleFileInfoCollection(List<LocaleFileInfoEntity> localeFileInfoCollection) {
        this.localeFileInfoCollection = localeFileInfoCollection;
    }

    /**
     * 設定プロパティを取得する。
     *
     * @return 設定
     */
    public StringProperty configProperty() {
        if (Objects.isNull(this.configProperty)) {
            this.configProperty = new SimpleStringProperty(this.config);
        }
        return this.configProperty;
    }

    /**
     * 設定を設定する。
     *
     * @param config 設定
     */
    public void setConfig(String config) {
        if (Objects.nonNull(this.configProperty)) {
            this.configProperty.set(config);
        } else {
            this.config = config;
        }
    }

    /**
     * 背景色を取得する。
     *
     * @return 背景色
     */
    public String getConfig() {
        if (Objects.nonNull(this.configProperty)) {
            return this.configProperty.get();
        }
        return this.config;
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
     * 設備種別を取得する。
     * 
     * @return 設備種別
     */
    public EquipmentTypeEntity getEquipmentTypeEntity() {
        return this.equipmentType;
    }
    
    /**
     * 設備種別を設定する。
     * 
     * @param equipmentType 設備種別
     */
    public void setEquipmentTypeEntity(EquipmentTypeEntity equipmentType) {
        this.equipmentType = equipmentType;
    }
    
    /**
     * 設備種別情報を取得する
     *
     * @param equipment
     * @return
     */
    public static Map getSettingInfo(EquipmentInfoEntity equipment) {
        Map<String, EquipmentSettingInfoEntity> map = new HashMap<>();
        if (Objects.nonNull(equipment.getSettingInfoCollection())) {
            for (EquipmentSettingInfoEntity setting : equipment.getSettingInfoCollection()) {
                map.put(setting.getEquipmentSettingName(), setting);
            }
        }
        return map;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.equipmentId ^ (this.equipmentId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.equipmentIdentify);
        hash = 83 * hash + Objects.hashCode(this.equipmentTypeId);
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
        final EquipmentInfoEntity other = (EquipmentInfoEntity) obj;
        if (!Objects.equals(this.getEquipmentId(), other.getEquipmentId())) {
            return false;
        }
        if (!Objects.equals(this.getEquipmentIdentify(), other.getEquipmentIdentify())) {
            return false;
        }
        return Objects.equals(this.getEquipmentType(), other.getEquipmentType());
    }

    @Override
    public String toString() {
        return new StringBuilder("EquipmentInfoEntity{")
                 .append("equipmentId=").append(this.equipmentId)
                .append(", equipmentName=").append(this.equipmentName)
                .append(", equipmentIdentify=").append(this.equipmentIdentify)
                .append(", equipmentTypeId=").append(this.equipmentTypeId)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", calFlag=").append(this.calFlag)
                .append(", calTerm=").append(this.calTerm)
                .append(", calTermUnit=").append(this.calTermUnit)
                .append(", calWarningDays=").append(this.calWarningDays)
                .append(", calNextDate=").append(this.calNextDate)
                .append(", calPersonId=").append(this.calPersonId)
                .append(", parentId=").append(this.parentId)
                .append(", ipv4Address=").append(this.ipv4Address)
                .append(", workProgressFlag=").append(this.workProgressFlag)
                .append(", pluginName=").append(this.pluginName)
                .append(", langIds=").append(this.langIds)
                .append(", config=").append(this.config)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    /**
     * 内容が一致しているかどうかを調べる
     *
     * @param info
     * @return
     */
    public boolean displayInfoEquals(EquipmentInfoEntity info) {
        boolean ret = false;
        if (!(Objects.equals(getEquipmentName(), info.getEquipmentName())
                && Objects.equals(this.getEquipmentIdentify(), info.getEquipmentIdentify())
                && Objects.equals(this.getEquipmentType(), info.getEquipmentType())
                && equipmentPropertyInfoListEquals(this.getPropertyInfoCollection(), info.getPropertyInfoCollection())
                && settingInfoListEquals(this.getSettingInfoCollection(), info.getSettingInfoCollection())
                && Objects.equals(this.getCalFlag(), info.getCalFlag())
                && Objects.equals(this.getCalNextDate(), info.getCalNextDate())
                && Objects.equals(this.getCalTerm(), info.getCalTerm())
                && Objects.equals(this.getCalTermUnit(), info.getCalTermUnit())
                && Objects.equals(this.getCalWarningDays(), info.getCalWarningDays())
                && Objects.equals(this.getCalLastDate(), info.getCalLastDate())
                && Objects.equals(this.getCalPersonId(), info.getCalPersonId())
                && Objects.equals(this.getParentId(), info.getParentId())
                && Objects.equals(this.getIpv4Address(), info.getIpv4Address())
                && Objects.equals(this.getWorkProgressFlag(), info.getWorkProgressFlag())
                && Objects.equals(this.getPluginName(), info.getPluginName())
                && Objects.equals(this.getLangIds(), info.getLangIds())
                && Objects.equals(this.getLocaleFileInfoCollection(), info.getLocaleFileInfoCollection()))
        ) {
            return false;
        }

        ConfigInfoEntity infoConf = StringUtils.isEmpty(info.getConfig()) ? new ConfigInfoEntity() : JsonUtils.jsonToObject(info.getConfig(), ConfigInfoEntity.class);
        ConfigInfoEntity thisConf = StringUtils.isEmpty(this.getConfig()) ? new ConfigInfoEntity() : JsonUtils.jsonToObject(this.getConfig(), ConfigInfoEntity.class);
        return Objects.equals(infoConf, thisConf);
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    @Override
    public EquipmentInfoEntity clone() {
        EquipmentInfoEntity entity = new EquipmentInfoEntity();

        entity.setEquipmentName(this.getEquipmentName());
        entity.setEquipmentIdentify(this.getEquipmentIdentify());
        entity.setEquipmentType(this.getEquipmentType());

        //プロパティのコピー
        List<EquipmentPropertyInfoEntity> cloneProperties = new LinkedList<>();
        getPropertyInfoCollection().stream().forEach(c -> cloneProperties.add(c.clone()));
        entity.setPropertyInfoCollection(cloneProperties);

        //設備情報のコピー
        List<EquipmentSettingInfoEntity> cloneSettings = new LinkedList<>();
        getSettingInfoCollection().stream().forEach(c -> cloneSettings.add(c.clone()));
        entity.setSettingInfoCollection(cloneSettings);

        // 校正情報
        entity.setCalFlag(this.getCalFlag());
        entity.setCalNextDate(this.getCalNextDate());
        entity.setCalLastDate(this.getCalLastDate());
        entity.setCalPersonId(this.getCalPersonId());
        entity.setCalWarningDays(this.getCalWarningDays());
        entity.setCalTerm(this.getCalTerm());
        entity.setCalTermUnit(this.getCalTermUnit());

        entity.setParentId(this.getParentId());

        // 設備情報
        entity.setIpv4Address(this.getIpv4Address());
        entity.setWorkProgressFlag(this.getWorkProgressFlag());
        entity.setPluginName(this.getPluginName());

        // 追加情報
        entity.setEquipmentAddInfo(this.getEquipmentAddInfo());
        // サービス情報
        entity.setServiceInfo(this.getServiceInfo());

        // 言語(JSON形式)
        entity.setLangIds(this.getLangIds());

        // 言語
        entity.setLocaleFileInfoCollection(
                this.localeFileInfoCollection
                        .stream()
                        .map(LocaleFileInfoEntity::new)
                        .collect(Collectors.toList())
        );

        // 設定
        entity.setConfig(this.getConfig());

        // バージョン情報
        entity.setVerInfo(this.getVerInfo());

        return entity;
    }

    /**
     * List<EquipmentPropertyInfoEntity>の各項目が一致するか調べる
     *
     * @param a
     * @param b
     * @return
     */
    private boolean equipmentPropertyInfoListEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        Iterator<EquipmentPropertyInfoEntity> it1 = a.iterator();
        Iterator<EquipmentPropertyInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            EquipmentPropertyInfoEntity entity1 = it1.next();
            EquipmentPropertyInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * EquipmentSettingInfoEntityのリストの中身が一致するか調べる
     *
     * @param a
     * @param b
     * @return
     */
    private boolean settingInfoListEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        Iterator<EquipmentSettingInfoEntity> it1 = a.iterator();
        Iterator<EquipmentSettingInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            EquipmentSettingInfoEntity entity1 = it1.next();
            EquipmentSettingInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }
}
