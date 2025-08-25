/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.equipment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adFactory.enumerate.TermUnitEnum;

/**
 * 設備インポート
 *
 * @author HN)y-harada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipment")
@JsonIgnoreProperties(ignoreUnknown=true)
public class EquipmentImportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private StringProperty equipmentIdentifyProperty;
    private StringProperty equipmentNameProperty;
    private StringProperty parentNameProperty;
    private LongProperty equipmentTypeIdProperty;
    
    @XmlElement()
    private String equipmentIdentify;// 設備識別名
    @XmlElement()
    private String equipmentName;// 設備名
    @XmlElement()
    private String parentName;// 親設備識別名
    @XmlElement()
    private Long equipmentTypeId;// 設備種別ID
    @XmlElement()
    private boolean removeFlg;// 論理削除フラグ
    @XmlElement()
    private boolean workProgressFlg;// 論理削除フラグ
    @XmlElement()
    private boolean calFlg;// 機器校正有無
    @XmlElement()
    private Date calNextDate;// 次回校正日
    @XmlElement()
    private Integer calTerm;// 校正間隔
    @XmlElement()
    private String calTermUnit;// 校正間隔単位
    @XmlElement()
    private Integer calWarnigDays;// 警告表示日数
    @XmlElement()
    private String pluginName;// プラグイン名
    @XmlElement()
    private Date calLastDate;// 最終校正日
    @XmlElement()
    private String calperson;// 最終校正実施者
    @XmlElement()
    private List<addInfoJson> addInfo;// 追加情報

    /**
     * コンストラクタ
     */
    public EquipmentImportEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param equipmentIdentify 設備識別名
     * @param equipmentName 設備名
     * @param parentName 親設備名
     * @param equipmentType 設備種別ID
     */
    public EquipmentImportEntity(String equipmentIdentify, String equipmentName, String parentName, Long equipmentType) {
        this.equipmentIdentify = equipmentIdentify;
        this.equipmentName = equipmentName;
        this.parentName = parentName;
        this.equipmentTypeId = equipmentType;
        
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
     * 親設備名プロパティを取得する。
     *
     * @return 親設備名
     */
    public StringProperty parentNameProperty() {
        if (Objects.isNull(this.parentNameProperty)) {
            this.parentNameProperty = new SimpleStringProperty(this.parentName);
        }
        return parentNameProperty;
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
     * 親設備名を取得する。
     *
     * @return 親設備ID
     */
    public String getParentName() {
        if (Objects.nonNull(this.parentNameProperty)) {
            return this.parentNameProperty.get();
        }
        return this.parentName;
    }

    /**
     * 親設備名を設定する。
     *
     * @param parentName 親設備名
     */
    public void setParentName(String parentName) {
        if (Objects.nonNull(this.parentNameProperty)) {
            this.parentNameProperty.set(parentName);
        } else {
            this.parentName = parentName;
        }
    }

    /**
     * 設備種別IDを取得する。
     *
     * @return 設備種別ID
     */
    public Long getEquipmentTypeId() {
        if (Objects.nonNull(this.equipmentTypeIdProperty)) {
            return this.equipmentTypeIdProperty.get();
        }
        return this.equipmentTypeId;
    }

    /**
     * 設備種別IDを設定する。
     *
     * @param equipmentTypeId 設備種別ID
     */
    public void setEquipmentTypeId(Long equipmentTypeId) {
        if (Objects.nonNull(this.equipmentTypeIdProperty)) {
            this.equipmentTypeIdProperty.set(equipmentTypeId);
        } else {
            this.equipmentTypeId = equipmentTypeId;
        }
    }
    
    /**
     * @return the removeFlg
     */
    public boolean isRemoveFlg() {
        return removeFlg;
    }

    /**
     * @param rmoveFlg the removeFlg to set
     */
    public void setRemoveFlg(boolean removeFlg) {
        this.removeFlg = removeFlg;
    }

    /**
     * @return the calFlg
     */
    public boolean getCalFlg() {
        return calFlg;
    }

    /**
     * @param calFlg the calFlg to set
     */
    public void setCalFlg(boolean calFlg) {
        this.calFlg = calFlg;
    }

    /**
     * @return the calNextDate
     */
    public Date getCalNextDate() {
        return calNextDate;
    }

    /**
     * @param calNextDate the calNextDate to set
     */
    public void setCalNextDate(Date calNextDate) {
        this.calNextDate = calNextDate;
    }

    /**
     * @return the calTerm
     */
    public Integer getCalTerm() {
        return calTerm;
    }

    /**
     * @param calTerm the calTerm to set
     */
    public void setCalTerm(Integer calTerm) {
        this.calTerm = calTerm;
    }

    /**
     * @return the calTermUnit
     */
    public String getCalTermUnit() {
        return calTermUnit;
    }

    /**
     * @param calTermUnit the calTermUnit to set
     */
    public void setCalTermUnit(String calTermUnit) {
        this.calTermUnit = calTermUnit;
    }

    /**
     * @return the calWarnigDays
     */
    public Integer getCalWarnigDays() {
        return calWarnigDays;
    }

    /**
     * @param calWarnigDays the calWarnigDays to set
     */
    public void setCalWarnigDays(Integer calWarnigDays) {
        this.calWarnigDays = calWarnigDays;
    }

    /**
     * @return the pluginName
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * @param pluginName the pluginName to set
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * @return the calLastDate
     */
    public Date getCalLastDate() {
        return calLastDate;
    }

    /**
     * @param calLastDate the calLastDate to set
     */
    public void setCalLastDate(Date calLastDate) {
        this.calLastDate = calLastDate;
    }

    /**
     * @return the addInfo
     */
    public List<addInfoJson> getAddInfo() {
        return addInfo;
    }

    /**
     * @param addInfo the addInfo to set
     */
    public void setAddInfo(List<addInfoJson> addInfo) {
        this.addInfo = addInfo;
    }

    /**
     * @return the workProgressFlg
     */
    public boolean isWorkProgressFlg() {
        return workProgressFlg;
    }

    /**
     * @param workProgressFlg the workProgressFlg to set
     */
    public void setWorkProgressFlg(boolean workProgressFlg) {
        this.workProgressFlg = workProgressFlg;
    }

    /**
     * @return the calperson
     */
    public String getCalperson() {
        return calperson;
    }

    /**
     * @param calperson the calperson to set
     */
    public void setCalperson(String calperson) {
        this.calperson = calperson;
    }
    
     /**
     * 追加情報クラス
     * 
     */
    public static class addInfoJson {
        
        private String key;
        private String val;
        private int disp;
        private String type;     
        
        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key the key to set
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * @return the val
         */
        public String getVal() {
            return val;
        }

        /**
         * @param val the val to set
         */
        public void setVal(String val) {
            this.val = val;
        }

        /**
         * @return the disp
         */
        public int getDisp() {
            return disp;
        }

        /**
         * @param disp the disp to set
         */
        public void setDisp(int disp) {
            this.disp = disp;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }  
    }
    
    /**
     * インポート可能かチェック
     *
     * @param pluginNames プラグイン名リスト
     * @return errorMeesage　エラーメッセージ　nullならインポート可能
     */
    @XmlTransient
    @JsonIgnore
    public String importableCheck(List<String> pluginNames) {
        
        String errorMeesage = "";
        
        // 必須チェック
        if(Objects.isNull(this.equipmentIdentify) || this.equipmentIdentify.isEmpty() 
                 || Objects.isNull(this.equipmentName) || this.equipmentName.isEmpty()){
            errorMeesage = "key.RequiredItemNotExist";
            return errorMeesage;
        }
        
        // プラグイン名チェック
        if (Objects.nonNull(this.pluginName) && checkPluginName(this.pluginName, pluginNames) == false) {           
            errorMeesage = "key.WrongPluginName";
            return errorMeesage;
        }
        
        // 校正間隔単位チェック
        if (Objects.nonNull(this.calTermUnit) && checkCalTermUnit(this.calTermUnit) == false) {
            errorMeesage = "key.WrongCalTermUnit";
            return errorMeesage;
        }
        
        String regex256 = "^.{0,256}$";
        String regex1024 = "^.{0,1024}$";

        // 追加項目の文字数、固定値チェック
        if (Objects.nonNull(this.addInfo)) {
            for (addInfoJson addInfoPerDisp : this.addInfo) {
                // 追加項目タイプのチェック
                if (checkAddInfoType(addInfoPerDisp.getType()) == false) {
                    errorMeesage = "key.WrongAddInfoType";
                    return errorMeesage;
                }
                // 追加項目名文字数チェック
                if (isMatcheRegex(addInfoPerDisp.getKey(), regex256) == false) {
                    errorMeesage = "key.AddInfoItemNameCountOver";
                    return errorMeesage;
                }
                // 追加項目現在値文字数チェック
                if (isMatcheRegex(addInfoPerDisp.getVal(), regex1024) == false) {
                    errorMeesage = "key.AddInfoItemValueCountOver";
                    return errorMeesage;
                }
            }
        }

        // 設備名文字数チェック
        if (Objects.nonNull(this.equipmentName) && isMatcheRegex(this.equipmentName, regex256) == false) {
            errorMeesage = "key.EquipmentNameCountOver";
            return errorMeesage;
        }
        
        // 親設備識別名文字数チェック
        if (Objects.nonNull(this.parentName) && isMatcheRegex(this.parentName, regex256) == false) {
            errorMeesage = "key.ParentNameCountOver";
            return errorMeesage;
        }
       
        // 設備識別名文字数チェック
        if (Objects.nonNull(this.equipmentIdentify) && isMatcheRegex(this.equipmentIdentify, regex256) == false){
            errorMeesage = "key.EquipmentIdentifyCountOver";
            return errorMeesage;
        }

        return errorMeesage;
    }
    
    /**
     * 対象文字列が正規表現にあっているかどうか
     * @param text 対象文字列
     * @param regex 正規表現
     * @return boolean
     */
    private boolean isMatcheRegex(String text, String regex) {

        //戻り値
        boolean isOk = false;

        if (text.matches(regex)) {
            isOk = true;
        }
        return isOk;
    }
    
    /**
     * プラグイン名をチェックする
     * @param pluginName プラグイン名
     * @return boolean
     */
    private boolean checkPluginName(String pluginName, List<String> pluginNames) {

        //戻り値
        boolean isOk = false;
        
        for (String plugin : pluginNames) {
            if (pluginName.equals(plugin)) {
                isOk = true;
            }
        }

        return isOk;
    }
    
     /**
     * 校正間隔単位をチェックする
     * @param pluginName 校正間隔
     * @return boolean
     */
    private boolean checkCalTermUnit(String calTermUnit) {

        //戻り値
        boolean isOk = false;

        if (Objects.nonNull(TermUnitEnum.fromString(calTermUnit))){
            isOk = true;
        }
            
        return isOk;
    }
    
     /**
     * 追加情報のタイプをチェックする
     * @param AddInfoType 追加情報のタイプ
     * @return boolean
     */
    private boolean checkAddInfoType(String AddInfoType) {

        //戻り値
        boolean isOk = false;

        if (Objects.nonNull(CustomPropertyTypeEnum.toEnum(AddInfoType))){
            isOk = true;
        }
            
        return isOk;
    }
    
    /**
     * 追加情報をjsonで返す
     * @param addInfo 追加情報
     * @return String
     */
    public String getAddInfoToString() {
       String addInfo = JsonUtils.objectsToJson(this.addInfo);
       return addInfo;
    }        
    
    @Override
    public int hashCode() {
        int hash = 7;
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
        if (!Objects.equals(this.getEquipmentName(), other.getEquipmentName())) {
            return false;
        }
        if (!Objects.equals(this.getEquipmentIdentify(), other.getEquipmentIdentify())) {
            return false;
        }
        if (!Objects.equals(this.getEquipmentTypeId(), other.getEquipmentType())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("EquipmentInfoEntity{")
                .append("parentId=").append(this.parentName)
                .append(", ")
                .append("equipmentIdentify=").append(this.equipmentIdentify)
                .append(", ")
                .append("equipmentName=").append(this.equipmentName)
                .append(", ")
                .append("equipmentTypeId=").append(this.equipmentTypeId)
                .append(", ")
                .toString();
    }
}