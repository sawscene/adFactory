/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.object;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * モノ種別情報：モノをタイプ分けするための情報。モノ情報に付随して使用する
 *
 * @author e-mori
 * @version 設計・製造ソリューション展(2016)
 * @since 2016.06.06.Mon
 */
@Entity
@Table(name = "mst_object_type")
@XmlRootElement(name = "objectType")
@NamedQueries({
    // モノ種別IDを指定して、モノ種別情報を取得する。
    @NamedQuery(name = "ObjectTypeEntity.findByObjectTypeId", query = "SELECT m FROM ObjectTypeEntity m WHERE m.objectTypeId = :objectTypeId"),
    // モノ種別名を指定して、モノ種別情報を取得する。
    @NamedQuery(name = "ObjectTypeEntity.findByObjectTypeName", query = "SELECT m FROM ObjectTypeEntity m WHERE m.objectTypeName = :objectTypeName"),

    // モノ種別IDを指定して、モノ種別情報の件数を取得する。(存在確認)
    @NamedQuery(name = "ObjectTypeEntity.checkAddByObjectTypeId", query = "SELECT COUNT(m.objectTypeId) FROM ObjectTypeEntity m WHERE m.objectTypeId = :objectTypeId"),
    // モノ種別名の重複確認 (追加時)
    @NamedQuery(name = "ObjectTypeEntity.checkAddByObjectTypeName", query = "SELECT COUNT(m.objectTypeId) FROM ObjectTypeEntity m WHERE m.objectTypeName = :objectTypeName"),
    // モノ種別名の重複確認 (更新時)
    @NamedQuery(name = "ObjectTypeEntity.checkUpdateByObjectTypeName", query = "SELECT COUNT(m.objectTypeId) FROM ObjectTypeEntity m WHERE m.objectTypeId != :objectTypeId AND m.objectTypeName = :objectTypeName"),
})
public class ObjectTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "object_type_id")
    private Long objectTypeId;// モノ種別ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "object_type_name")
    private String objectTypeName;// モノ種別名

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public ObjectTypeEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param objectTypeName モノ種別名
     */
    public ObjectTypeEntity(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    /**
     * モノ種別IDを取得する。
     *
     * @return モノ種別ID
     */
    public Long getObjectTypeId() {
        return this.objectTypeId;
    }

    /**
     * モノ種別IDを設定する。
     *
     * @param objectTypeId モノ種別ID
     */
    public void setObjectTypeId(Long objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    /**
     * モノ種別名を取得する。
     *
     * @return モノ種別名
     */
    public String getObjectTypeName() {
        return this.objectTypeName;
    }

    /**
     * モノ種別名を設定する。
     *
     * @param objectTypeName モノ種別名
     */
    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.objectTypeId);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectTypeEntity)) {
            return false;
        }
        ObjectTypeEntity other = (ObjectTypeEntity) object;
        return Objects.equals(this.objectTypeId, other.objectTypeId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ObjectTypeEntity{")
                .append("objectTypeId=").append(this.objectTypeId)
                .append(", ")
                .append("objectTypeName=").append(this.objectTypeName)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
