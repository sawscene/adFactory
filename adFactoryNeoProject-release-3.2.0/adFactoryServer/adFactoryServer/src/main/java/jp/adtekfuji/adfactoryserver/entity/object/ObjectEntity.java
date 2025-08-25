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
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * モノ情報：作業する工程で使用する、モノの情報
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
    // モノ種別ID・モノIDを指定して、モノ情報を取得する。(削除済のモノは対象外)
    @NamedQuery(name = "ObjectEntity.findByPK", query = "SELECT m FROM ObjectEntity m WHERE m.objectId = :objectId AND m.objectTypeId = :objectTypeId AND m.removeFlag = false"),
    // モノ種別IDを指定して、モノ情報一覧を取得する。(削除済のモノは対象外)
    @NamedQuery(name = "ObjectEntity.findByObjectTypeId", query = "SELECT m FROM ObjectEntity m WHERE m.objectTypeId = :objectTypeId AND m.removeFlag = false"),
    // モノ名を指定して、モノ情報を取得する。(削除済のモノは対象外)
    @NamedQuery(name = "ObjectEntity.findByObjectName", query = "SELECT m FROM ObjectEntity m WHERE m.objectName = :objectName AND m.removeFlag = false"),

    // モノ種別ID・モノIDを指定して、モノ情報の件数を取得する。(存在確認) ※.削除済も対象
    @NamedQuery(name = "ObjectEntity.checkByPK", query = "SELECT COUNT(m.objectId) FROM ObjectEntity m WHERE m.objectId = :objectId AND m.objectTypeId = :objectTypeId"),
    // モノ名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "ObjectEntity.checkAddByObjectName", query = "SELECT COUNT(m.objectId) FROM ObjectEntity m WHERE m.objectTypeId = :objectTypeId AND m.objectName = :objectName"),
    // モノ名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "ObjectEntity.checkUpdateByObjectName", query = "SELECT COUNT(m.objectId) FROM ObjectEntity m WHERE m.objectId != :objectId AND m.objectTypeId = :objectTypeId AND m.objectName = :objectName"),
})
public class ObjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "object_id")
    private String objectId;// モノID

    @Id
    @Basic(optional = false)
    //@NotNull
    @Column(name = "object_type_id")
    @XmlElement(name = "fkObjectTypeId")
    private long objectTypeId;// モノ種別ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "object_name")
    private String objectName;// モノ名

    @Column(name = "remove_flag")
    private Boolean removeFlag;// 削除フラグ

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public ObjectEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param objectId モノID
     * @param objectTypeId モノ種別ID
     */
    public ObjectEntity(String objectId, long objectTypeId) {
        this.objectId = objectId;
        this.objectTypeId = objectTypeId;
    }

    /**
     * コンストラクタ
     *
     * @param in モノ情報
     */
    public ObjectEntity(ObjectEntity in) {
        this.objectId = in.objectId;
        this.objectTypeId = in.objectTypeId;
        this.objectName = in.objectName;
        this.removeFlag = in.removeFlag;
    }

    /**
     * コンストラクタ
     *
     * @param objectId モノID
     * @param objectTypeId モノ種別ID
     * @param objectName モノ名
     */
    public ObjectEntity(String objectId, long objectTypeId, String objectName) {
        this.objectId = objectId;
        this.objectTypeId = objectTypeId;
        this.objectName = objectName;
    }

    /**
     * モノIDを取得する。
     *
     * @return モノID
     */
    public String getObjectId() {
        return this.objectId;
    }

    /**
     * モノIDを設定する。
     *
     * @param objectId モノID
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * モノ種別IDを取得する。
     *
     * @return モノ種別ID
     */
    public long getObjectTypeId() {
        return this.objectTypeId;
    }

    /**
     * モノ種別IDを設定する。
     *
     * @param objectTypeId モノ種別ID
     */
    public void setObjectTypeId(long objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    /**
     * モノ名を取得する。
     *
     * @return モノ名
     */
    public String getObjectName() {
        return this.objectName;
    }

    /**
     * モノ名を設定する。
     *
     * @param objectName モノ名
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * 削除フラグを取得する。
     *
     * @return 削除フラグ
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
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
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.objectId);
        hash = 89 * hash + Objects.hashCode(this.objectTypeId);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ObjectEntity)) {
            return false;
        }
        ObjectEntity other = (ObjectEntity) object;
        return Objects.equals(this.objectId, other.objectId) && Objects.equals(this.objectTypeId, other.objectTypeId);
    }

    @Override
    public String toString() {
        return new StringBuilder("ObjectEntity{")
                .append("objectId=").append(this.objectId)
                .append(", ")
                .append("objectTypeId=").append(this.objectTypeId)
                .append(", ")
                .append("objectName=").append(this.objectName)
                .append(", ")
                .append("removeFlag=").append(this.removeFlag)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
