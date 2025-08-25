/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.object;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * モノ情報エンティティクラス：作業する工程で使用する、モノの情報
 *
 * @author e-mori
 * @version 設計・製造ソリューション展(2016)
 * @since 2016.06.06.Mon
 */
@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "mst_object")
@NamedQueries({
    //モノ情報を取得するクエリ
    @NamedQuery(name = "ObjectEntity.findAll", query = "SELECT m FROM ObjectEntity m WHERE m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByPK", query = "SELECT m FROM ObjectEntity m WHERE m.objectId = :objectId AND m.fkObjectTypeId = :fkObjectTypeId AND m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByObjectId", query = "SELECT m FROM ObjectEntity m WHERE m.objectId = :objectId AND m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByFkObjectTypeId", query = "SELECT m FROM ObjectEntity m WHERE m.fkObjectTypeId = :fkObjectTypeId AND m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByObjectName", query = "SELECT m FROM ObjectEntity m WHERE m.objectName = :objectName AND m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByFkUpdatePersonId", query = "SELECT m FROM ObjectEntity m WHERE m.fkUpdatePersonId = :fkUpdatePersonId AND m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByUpdateDatetime", query = "SELECT m FROM ObjectEntity m WHERE m.updateDatetime = :updateDatetime AND m.removeFlag = false"),
    @NamedQuery(name = "ObjectEntity.findByRemoveFlag", query = "SELECT m FROM ObjectEntity m WHERE m.removeFlag = :removeFlag"),

    //モノ情報の存在を確認するクエリ
    @NamedQuery(name = "ObjectEntity.checkByPK", query = "SELECT COUNT(m.objectId) FROM ObjectEntity m WHERE m.objectId = :objectId AND m.fkObjectTypeId = :fkObjectTypeId"),
    @NamedQuery(name = "ObjectEntity.checkAddByObjectName", query = "SELECT COUNT(m.objectId) FROM ObjectEntity m WHERE m.fkObjectTypeId = :fkObjectTypeId AND m.objectName = :objectName"),
    @NamedQuery(name = "ObjectEntity.checkUpdateByObjectName", query = "SELECT COUNT(m.objectId) FROM ObjectEntity m WHERE m.objectId != :objectId AND m.fkObjectTypeId = :fkObjectTypeId AND m.objectName = :objectName")})
public class ObjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "object_id")
    private String objectId;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_object_type_id")
    private long fkObjectTypeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "object_name")
    private String objectName;
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @Column(name = "remove_flag")
    private Boolean removeFlag;

    public ObjectEntity() {
    }

    public ObjectEntity(String objectId, long objectTypeId) {
        this.objectId = objectId;
        this.fkObjectTypeId = objectTypeId;
    }

    public ObjectEntity(ObjectEntity in) {
        this.objectId = in.objectId;
        this.fkObjectTypeId = in.fkObjectTypeId;
        this.objectName = in.objectName;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
    }

    public ObjectEntity(String objectId, long fkObjectTypeId, String objectName) {
        this.objectId = objectId;
        this.fkObjectTypeId = fkObjectTypeId;
        this.objectName = objectName;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public long getFkObjectTypeId() {
        return this.fkObjectTypeId;
    }

    public void setFkObjectTypeId(long fkObjectTypeId) {
        this.fkObjectTypeId = fkObjectTypeId;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Long getFkUpdatePersonId() {
        return this.fkUpdatePersonId;
    }

    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        this.fkUpdatePersonId = fkUpdatePersonId;
    }

    public Date getUpdateDatetime() {
        return this.updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.objectId);
        hash = 89 * hash + Objects.hashCode(this.fkObjectTypeId);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectEntity)) {
            return false;
        }
        ObjectEntity other = (ObjectEntity) object;
        return Objects.equals(this.objectId, other.objectId) && Objects.equals(this.fkObjectTypeId, other.fkObjectTypeId);
    }

    @Override
    public String toString() {
        return "ObjectEntity{" + " objectId=" + this.objectId + ", fkObjectTypeId=" + this.fkObjectTypeId + ", objectName=" + this.objectName + ", fkUpdatePersonId=" + this.fkUpdatePersonId + ", updateDatetime=" + this.updateDatetime + ", removeFlag=" + this.removeFlag + " }";
    }
}
