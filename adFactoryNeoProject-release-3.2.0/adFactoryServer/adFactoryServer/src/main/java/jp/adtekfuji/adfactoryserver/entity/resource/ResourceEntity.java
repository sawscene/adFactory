/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;

/**
 * リソース
 *
 * @author
 */
@Entity
@Table(name = "t_resource")
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        // リソースIDを指定して、リソース情報を取得する。
        @NamedQuery(name = "ObjectEntity.findByPK", query = "SELECT m FROM ObjectEntity m WHERE m.objectId = :objectId AND m.objectTypeId = :objectTypeId AND m.removeFlag = false"),
        @NamedQuery(name = "ResourceEntity.findByTypeKey", query = "SELECT m FROM ResourceEntity m WHERE m.resourceType = :resourceType AND m.resourceKey = :resourceKey")
})
public class ResourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "resource_id")
    @JsonProperty("id")
    private Long resourceId;// ID

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(name = "resource_type")
    @JsonIgnore
    private ResourceTypeEnum resourceType;// リソース種別

    @Size(max = 256)
    @Basic(optional = false)
    //@NotNull
    @Column(name = "resource_key")
    @JsonProperty("key")
    private String resourceKey;// リソースキー

    @Basic(optional = false)
    @Column(name = "resource_string")
    @JsonIgnore
    private String resourceString;// テキスト

    @Basic(optional = false)
    @Column(name = "resource_bin")
    @JsonIgnore
    private byte[] resourceBin;//　バイナリ

    /**
     * コンストラクタ
     */
    public ResourceEntity() {
    }

    /**
     * コストラクタ
     * 
     * @param entity 
     */
    public ResourceEntity(ResourceEntity entity) {
        this.resourceId = entity.resourceId;
        this.resourceType = entity.resourceType;
        this.resourceKey = entity.resourceKey;
        this.resourceString = entity.resourceString;
        this.resourceBin =
                Objects.isNull(entity.resourceBin)
                        ? null
                        : Arrays.copyOf(entity.resourceBin, entity.resourceBin.length);
    }

    /**
     * コンストラクタ
     *
     * @param resourceId
     * @param resourceType
     * @param resourceString
     * @param resourceKey
     * @param resourceBin
     */
    public ResourceEntity(Long resourceId, ResourceTypeEnum resourceType, String resourceKey, String resourceString, byte[] resourceBin) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.resourceKey = resourceKey;
        this.resourceString = resourceString;
        this.resourceBin = resourceBin;
    }

    /**
     * リソースIDを取得する。
     *
     * @return リソースID
     */
    public Long getResourceId() {
        return resourceId;
    }

    /**
     * リソースIDを設定する。
     *
     * @param resourceId リソースID
     */
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * リソースタイプを取得する。
     *
     * @return リソースタイプ
     */
    public ResourceTypeEnum getResourceType() {
        return resourceType;
    }

    /**
     * リソースタイプを設定する。
     *
     * @param resourceType リソースタイプ
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
        return resourceKey;
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
     * リソース文字列を取得する。
     *
     * @return リソース文字列
     */
    public String getResourceString() {
        return resourceString;
    }

    /**
     * リソース文字列を設定する。
     *
     * @param resourceString リソース文字列
     */
    public void setResourceString(String resourceString) {
        this.resourceString = resourceString;
    }

    /**
     * リソースバイナリを取得する。
     *
     * @return リソースバイナリ
     */
    public byte[] getResourceBin() {
        return resourceBin;
    }

    /**
     * リソースバイナリを設定する。
     *
     * @param resourceBin リソースバイナリ
     */
    public void setResourceBin(byte[] resourceBin) {
        this.resourceBin = resourceBin;
    }

    /**
     * cast
     *
     * @return
     */
    public ResourceInfoEntity cast() {
        ResourceInfoEntity entity = new ResourceInfoEntity();
        entity.setResourceId(this.resourceId);
        entity.setResourceType(this.resourceType);
        entity.setResourceKey(this.resourceKey);
        entity.setResourceString(this.resourceString);
        entity.setResourceBin(this.resourceBin);
        return entity;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getResourceId() != null ? getResourceId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ResourceEntity)) {
            return false;
        }
        ResourceEntity other = (ResourceEntity) object;
        if ((this.getResourceId() == null && other.getResourceId() != null) || (this.getResourceId() != null && !this.resourceId.equals(other.resourceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ResourceEntity{")
                .append("resourceId=").append(this.getResourceId())
                .append(", resourceType=").append(this.getResourceType())
                .append(", resourceKey=").append(this.getResourceKey())
                .append("}")
                .toString();
    }
}
