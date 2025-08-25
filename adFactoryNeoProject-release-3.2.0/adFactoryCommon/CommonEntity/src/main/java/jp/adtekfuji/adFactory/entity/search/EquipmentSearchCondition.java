/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.search;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;

/**
 * 設備情報検索条件
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "equipmentSearchCondition")
public class EquipmentSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String equipmentName = null;// 設備名

    @XmlElement()
    private String equipmentIdentName = null;// 設備識別名

    @XmlElement()
    private String parentName = null;// 親設備の設備名

    @XmlElement()
    private String parentIdentName = null;// 親設備の設備識別名

    @XmlElement()
    private EquipmentTypeEnum equipmentType = null;// 設備種別

    @XmlElement()
    private Boolean removeFlag = null;// 削除フラグ

    @XmlElement()
    private Boolean isMatch = false;// 完全一致検索

    @XmlElement()
    private String ipv4Address = null;// IPv4アドレス

    @XmlElement()
    private Boolean withChildCount; // 子階層数を取得する
    
    /**
     * コンストラクタ
     */
    public EquipmentSearchCondition() {
    }

    /**
     * コンストラクタ
     *
     * @param equipmentName 設備名
     * @param equipmentIdentName 設備識別名
     * @param parentName 親設備の設備名
     * @param parentIdentName 親設備の設備識別名
     * @param equipmentType 設備種別
     */
    public EquipmentSearchCondition(String equipmentName, String equipmentIdentName, String parentName, String parentIdentName, EquipmentTypeEnum equipmentType) {
        this.equipmentName = equipmentName;
        this.equipmentIdentName = equipmentIdentName;
        this.parentName = parentName;
        this.parentIdentName = parentIdentName;
        this.equipmentType = equipmentType;
    }

    /**
     * 設備名を設定する。
     *
     * @param equipmentName 設備名
     * @return 設定を追加した検索条件
     */
    public EquipmentSearchCondition equipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
        return this;
    }

    /**
     * 設備識別名を設定する。
     *
     * @param equipmentIdentName 設備識別名
     * @return 設備情報検索条件
     */
    public EquipmentSearchCondition equipmentIdentName(String equipmentIdentName) {
        this.equipmentIdentName = equipmentIdentName;
        return this;
    }

    /**
     * 親設備の設備名を設定する。
     *
     * @param parentName 親設備の設備名
     * @return 設備情報検索条件
     */
    public EquipmentSearchCondition parentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    /**
     * 親設備の設備識別名を設定する。
     *
     * @param parentIdentName 親設備の設備識別名
     * @return 設備情報検索条件
     */
    public EquipmentSearchCondition parentIdentName(String parentIdentName) {
        this.parentIdentName = parentIdentName;
        return this;
    }

    /**
     * 設備種別を設定する。
     *
     * @param equipmentType 設備種別
     * @return 設備情報検索条件
     */
    public EquipmentSearchCondition equipmentType(EquipmentTypeEnum equipmentType) {
        this.equipmentType = equipmentType;
        return this;
    }

    /**
     * 検索条件にIPv4アドレスを設定する。
     *
     * @param ipv4Address IPv4アドレス
     * @return 設備情報検索条件
     */
    public EquipmentSearchCondition ipv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
        return this;
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

    /**
     * 設備識別名を取得する。
     *
     * @return 設備識別名
     */
    public String getEquipmentIdentName() {
        return equipmentIdentName;
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
     * 親設備の設備名を取得する。
     *
     * @return 親設備の設備名
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * 親設備の設備名を設定する。
     *
     * @param parentName 親設備の設備名
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 親設備の設備識別名を取得する。
     *
     * @return 親設備の設備識別名
     */
    public String getParentIdentName() {
        return parentIdentName;
    }

    /**
     * 親設備の設備識別名を設定する。
     *
     * @param parentIdentName 親設備の設備識別名
     */
    public void setParentIdentName(String parentIdentName) {
        this.parentIdentName = parentIdentName;
    }

    /**
     * 設備種別を取得する。
     *
     * @return 設備種別
     */
    public EquipmentTypeEnum getEquipmentType() {
        return equipmentType;
    }

    /**
     * 設備種別を設定する。
     *
     * @param equipmentType 設備種別
     */
    public void setEquipmentType(EquipmentTypeEnum equipmentType) {
        this.equipmentType = equipmentType;
    }

    /**
     * 削除フラグを取得する。
     *
     * @return 削除フラグ
     */
    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    /**
     * 削除フラグを設定する。
     *
     * @param removeFlag 削除フラグ
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 完全一致検索するかを取得する。
     *
     * @return 完全一致検索するか (true: 完全一致検索, false:あいまい検索)
     */
    public Boolean isMatch() {
        return this.isMatch;
    }

    /**
     * 完全一致検索するかを設定する。
     *
     * @param isMatch 完全一致検索するか (true: 完全一致検索, false:あいまい検索)
     */
    public void setMatch(Boolean isMatch) {
        this.isMatch = isMatch;
    }

    /**
     * IPv4アドレスを取得する。
     *
     * @return IPv4アドレス
     */
    public String getIpv4Address() {
        return this.ipv4Address;
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
     * 子階層数を取得するかどうかを返す。
     * 
     * @return true: 子階層数を取得する、false: 子階層数を取得しない
     */
    public Boolean isWithChildCount() {
        return withChildCount;
    }

    /**
     * 子階層数を取得するかどうか設定する。
     * 
     * @param withChildCount true: 子階層数を取得する、false: 子階層数を取得しない
     */
    public void setWithChildCount(Boolean withChildCount) {
        this.withChildCount = withChildCount;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("EquipmentSearchCondition{")
                .append("equipmentName=").append(this.equipmentName)
                .append(", equipmentIdentName=").append(this.equipmentIdentName)
                .append(", parentName=").append(this.parentName)
                .append(", parentIdentName=").append(this.parentIdentName)
                .append(", equipmentType=").append(this.equipmentType)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", isMatch=").append(this.isMatch)
                .append(", ipv4Address=").append(this.ipv4Address)
                .append(", withChildCount=").append(this.withChildCount)
                .append("}")
                .toString();
    }
}
