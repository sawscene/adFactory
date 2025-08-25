/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.object;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * モノタイプ情報エンティティクラス：モノをタイプ分けするための情報。モノ情報に付随して使用する
 *
 * @author e-mori
 * @version 設計・製造ソリューション展(2016)
 * @since 2016.06.06.Mon
 */
@Entity
@Table(name = "mst_object_type")
@XmlRootElement(name = "objectType")
@NamedQueries({
    //モノタイプ情報を取得するクエリ
    @NamedQuery(name = "ObjectTypeEntity.findAll", query = "SELECT m FROM ObjectTypeEntity m"),
    @NamedQuery(name = "ObjectTypeEntity.findByObjectTypeId", query = "SELECT m FROM ObjectTypeEntity m WHERE m.objectTypeId = :objectTypeId"),
    @NamedQuery(name = "ObjectTypeEntity.findByObjectTypeName", query = "SELECT m FROM ObjectTypeEntity m WHERE m.objectTypeName = :objectTypeName"),

    //モノタイプ情報の存在を確認するクエリ
    @NamedQuery(name = "ObjectTypeEntity.checkAddByObjectTypeId", query = "SELECT COUNT(m.objectTypeId) FROM ObjectTypeEntity m WHERE m.objectTypeId = :objectTypeId"),
    @NamedQuery(name = "ObjectTypeEntity.checkAddByObjectTypeName", query = "SELECT COUNT(m.objectTypeId) FROM ObjectTypeEntity m WHERE m.objectTypeName = :objectTypeName"),
    @NamedQuery(name = "ObjectTypeEntity.checkUpdateByObjectTypeName", query = "SELECT COUNT(m.objectTypeId) FROM ObjectTypeEntity m WHERE m.objectTypeId != :objectTypeId AND m.objectTypeName = :objectTypeName")})
public class ObjectTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "object_type_id")
    private Long objectTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "object_type_name")
    private String objectTypeName;

    public ObjectTypeEntity() {
    }

    public ObjectTypeEntity(String objectTypeName) {
        this.objectTypeName = objectTypeName;
    }

    public Long getObjectTypeId() {
        return this.objectTypeId;
    }

    public void setObjectTypeId(Long objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public String getObjectTypeName() {
        return this.objectTypeName;
    }

    public void setObjectTypeName(String objectTypeName) {
        this.objectTypeName = objectTypeName;
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
        return "ObjectTypeEntity{" + " objectTypeId=" + this.objectTypeId + ", objectTypeName=" + this.objectTypeName + " }";
    }
}
