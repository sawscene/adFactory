/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;

/**
 * リソース
 *
 * @author
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resource")
public class ResourceInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private NotifySetStringProperty resourceKeyProperty;

    @JsonProperty("id")
    @XmlElement()
    private Long resourceId;// リソースID

    @XmlElement()
    private ResourceTypeEnum resourceType;// リソース種別

    @JsonProperty("key")
    @XmlElement()
    private String resourceKey;// リソースキー

    @XmlElement()
    private String resourceString;// テキスト

    @XmlTransient
    private byte[] resourceBin;// バイナリ

    /**
     * コンストラクタ
     */
    public ResourceInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param resourceType リソース種別
     */
    public ResourceInfoEntity(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * コンストラクタ
     *
     * @param other リソース
     */
    public ResourceInfoEntity(ResourceInfoEntity other) {
        this.resourceId = other.resourceId;
        this.resourceType = other.resourceType;
        this.resourceKey = other.resourceKey;
        this.resourceString = other.resourceString;
        this.resourceBin =
                Objects.isNull(other.resourceBin)
                ? null
                : Arrays.copyOf(other.resourceBin, other.resourceBin.length);

    }

    /**
     * リソースキープロパティを取得する。
     *
     * @return リソースキー
     */
    public NotifySetStringProperty resourceKeyProperty() {
        if (Objects.isNull(this.resourceKeyProperty)) {
            this.resourceKeyProperty = new NotifySetStringProperty(this.resourceKey);
        }
        return this.resourceKeyProperty;
    }

    /**
     * リソースIDを取得する。
     *
     * @return リソースID
     */
    public Long getResourceId() {
        return this.resourceId;
    }

    /**
     * リソースIDを設定する。
     *
     * @param organizationId リソースID
     */
    public void setResourceId(Long organizationId) {
        this.resourceId = organizationId;
    }

    /**
     * リソース種別を取得する。
     *
     * @return リソース種別
     */
    public ResourceTypeEnum getResourceType() {
        return this.resourceType;
    }

    /**
     * リソース種別を設定する。
     *
     * @param resourceType リソース種別
     */
    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * リソースキーを取得する。
     *
     * @return リソースキー
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * リソースキーを設定する。
     *
     * @param resourceKey リソースキー
     */
    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * テキストを取得する。
     *
     * @return テキスト
     */
    public String getResourceString() {
        return this.resourceString;
    }

    /**
     * テキストを設定する。
     *
     * @param resourceString テキスト
     */
    public void setResourceString(String resourceString) {
        this.resourceString = resourceString;
    }

    /**
     * バイナリを取得する。
     *
     * @return バイナリ
     */
    public byte[] getResourceBin() {
        return this.resourceBin;
    }

    /**
     * バイナリを設定する。
     *
     * @param resourceBin バイナリ
     */
    public void setResourceBin(byte[] resourceBin) {
        this.resourceBin = Objects.isNull(resourceBin) ? null : Arrays.copyOf(resourceBin, resourceBin.length);
    }

     /**
     * オブジェクトを比較する。
     *
     * @param object オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceInfoEntity other = (ResourceInfoEntity) obj;
        if (!Objects.equals(this.resourceId, other.resourceId)
                ||!Objects.equals(this.resourceType, other.resourceType)
                ||!Objects.equals(this.resourceKey, other.resourceKey)
                ||!Objects.equals(this.resourceString, other.resourceString)
                ||!Arrays.equals(this.resourceBin, other.resourceBin)) {
            return false;
        }
        return true;
    }

     /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.resourceId);
        hash = 53 * hash + Objects.hashCode(this.resourceType);
        hash = 53 * hash + Objects.hashCode(this.resourceKey);
        hash = 53 * hash + Objects.hashCode(this.resourceString);
        hash = 53 * hash + Arrays.hashCode(this.resourceBin);
        return hash;
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("ResourceInfoEntity{")
                .append("resourceId=").append(this.resourceId)
                .append(", ")
                .append("resourceType=").append(this.resourceType)
                .append(", ")
                .append("resourceKey=").append(this.resourceKey)
                .append(", ")
                .append("resourceString=").append(this.resourceString)
                .append("}")
                .toString();
    }
}
