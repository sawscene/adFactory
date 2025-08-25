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

/**
 * 組織情報の検索条件
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "organizationSearchCondition")
public class OrganizationSearchCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String organizationName = null;     // 組織名

    @XmlElement()
    private String organizationIdentify = null; // 組織識別名

    @XmlElement()
    private Boolean removeFlag = null;          // 削除フラグ

    @XmlElement()
    private Boolean isMatch = false;            // 完全一致検索

    @XmlElement()
    private Boolean withChildCount;             // 子階層数を取得する
    
    /**
     * コンストラクタ
     */
    public OrganizationSearchCondition() {
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     * @return 検索条件
     */
    public OrganizationSearchCondition organizationName(String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentify 組織識別名
     * @return 検索条件
     */
    public OrganizationSearchCondition organizationIdentName(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
        return this;
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
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentify() {
        return organizationIdentify;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentName 組織識別名
     */
    public void setgetOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
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
        return new StringBuilder("OrganizationSearchCondition{")
                .append("organizationName=").append(this.organizationName)
                .append(", organizationIdentify=").append(this.organizationIdentify)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", isMatch=").append(this.isMatch)
                .append(", withChildCount=").append(this.withChildCount)
                .append("}")
                .toString();
    }
}
