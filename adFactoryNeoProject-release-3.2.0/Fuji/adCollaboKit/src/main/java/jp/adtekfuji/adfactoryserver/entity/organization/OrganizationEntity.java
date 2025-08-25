/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;

/**
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_organization")
@XmlRootElement(name = "organization")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "OrganizationEntity.countWorkflowWorkAssociation", query = "SELECT COUNT(c.conWorkOrganizationEntityPK.fkWorkId) FROM ConWorkOrganizationEntity c WHERE c.conWorkOrganizationEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "OrganizationEntity.countWorkflowSeparateWorkAssociation", query = "SELECT COUNT(c.conSeparateworkOrganizationEntityPK.fkWorkId) FROM ConSeparateworkOrganizationEntity c WHERE c.conSeparateworkOrganizationEntityPK.fkOrganizationId = :fkOrganizationId"),
    @NamedQuery(name = "OrganizationEntity.countKanbanAssociation", query = "SELECT COUNT(c.conWorkkanbanOrganizationEntityPK.fkWorkkanbanId) FROM ConWorkkanbanOrganizationEntity c WHERE c.conWorkkanbanOrganizationEntityPK.fkOrganizationId = :fkOrganizationId"),

    // 組織識別名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.checkAddByOrganizationIdentify", query = "SELECT COUNT(o.organizationId) FROM OrganizationEntity o WHERE o.organizationIdentify = :organizationIdentify"),
    // 組織識別名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "OrganizationEntity.checkUpdateByOrganizationIdentify", query = "SELECT COUNT(o.organizationId) FROM OrganizationEntity o WHERE o.organizationIdentify = :organizationIdentify AND o.organizationId != :organizationId"),

    @NamedQuery(name = "OrganizationEntity.findAll", query = "SELECT o FROM OrganizationEntity o WHERE o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByOrganizationId", query = "SELECT o FROM OrganizationEntity o WHERE o.organizationId = :organizationId AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByOrganizationName", query = "SELECT o FROM OrganizationEntity o WHERE o.organizationName = :organizationName AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByOrganizationIdentify", query = "SELECT o FROM OrganizationEntity o WHERE o.organizationIdentify = :organizationIdentify AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByAuthorityType", query = "SELECT o FROM OrganizationEntity o WHERE o.authorityType = :authorityType AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByLanguageType", query = "SELECT o FROM OrganizationEntity o WHERE o.languageType = :languageType AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByPassword", query = "SELECT o FROM OrganizationEntity o WHERE o.password = :password AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByMailAddress", query = "SELECT o FROM OrganizationEntity o WHERE o.mailAddress = :mailAddress AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByFkUpdatePersonId", query = "SELECT o FROM OrganizationEntity o WHERE o.fkUpdatePersonId = :fkUpdatePersonId AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByUpdateDatetime", query = "SELECT o FROM OrganizationEntity o WHERE o.updateDatetime = :updateDatetime AND o.removeFlag = false"),
    @NamedQuery(name = "OrganizationEntity.findByRemoveFlag", query = "SELECT o FROM OrganizationEntity o WHERE o.removeFlag = :removeFlag"),
    // 親組織に属する組織を問い合わせる
    @NamedQuery(name = "OrganizationEntity.findByParentId", query = "SELECT o FROM OrganizationEntity o, OrganizationHierarchyEntity pk WHERE pk.organizationHierarchyEntityPK.parentId = :parentId AND pk.organizationHierarchyEntityPK.childId = o.organizationId ORDER BY o.organizationName ASC")})
public class OrganizationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "organization_id")
    private Long organizationId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(max = 256)
    @Column(name = "organization_name")
    private String organizationName;
    @Size(max = 256)
    @Column(name = "organization_identify")
    private String organizationIdentify;
    @Enumerated(EnumType.STRING)
    @Column(name = "authority_type")
    private AuthorityEnum authorityType;
    @Size(max = 128)
    @Column(name = "language_type")
    private String languageType;
    @Size(max = 256)
    @Column(name = "pass_word")
    private String password;
    @Size(max = 256)
    @Column(name = "mail_address")
    private String mailAddress;
    @XmlElement(name = "updatePersonId")
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @XmlTransient
    @Column(name = "remove_flag")
    private Boolean removeFlag = false;
    @Transient
    private Long childCount = 0L;
    @XmlElementWrapper(name = "organizationPropertys")
    @XmlElement(name = "organizationProperty")
    @Transient
    private List<OrganizationPropertyEntity> propertyCollection = null;
    @XmlElementWrapper(name = "breaktimes")
    @XmlElement(name = "breaktime")
    @Transient
    private List<Long> breaktimeCollection = null;
    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    @Transient
    private List<Long> roleCollection = null;

    public OrganizationEntity() {
    }

    public OrganizationEntity(OrganizationEntity in) {
        this.parentId = in.parentId;
        this.organizationName = in.organizationName;
        this.organizationIdentify = in.organizationIdentify;
        this.languageType = in.languageType;
        //this.password = in.password;
        this.authorityType = in.authorityType;
        this.mailAddress = in.mailAddress;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.propertyCollection = new ArrayList<>();
        if (Objects.nonNull(in.getPropertyCollection())) {
            for (OrganizationPropertyEntity property : in.getPropertyCollection()) {
                this.propertyCollection.add(new OrganizationPropertyEntity(property));
            }
        }
        this.breaktimeCollection = new ArrayList<>();
        if (Objects.nonNull(in.getBreaktimeCollection())) {
            for (Long breaktime : in.getBreaktimeCollection()) {
                this.breaktimeCollection.add(breaktime);
            }
        }
        this.roleCollection = new ArrayList<>();
        if (Objects.nonNull(in.getRoleCollection())) {
            for (Long role : in.getRoleCollection()) {
                this.roleCollection.add(role);
            }
        }
    }

    public OrganizationEntity(Long parentId, String organizationName, String organizationIdentify, AuthorityEnum authorityType, String languageType, String password, String mailAddress, Long fkUpdatePersonId, Date updateDatetime) {
        this.parentId = parentId;
        this.organizationName = organizationName;
        this.organizationIdentify = organizationIdentify;
        this.authorityType = authorityType;
        this.languageType = languageType;
        this.password = password;
        this.mailAddress = mailAddress;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationIdentify() {
        return organizationIdentify;
    }

    public void setOrganizationIdentify(String organizationIdentify) {
        this.organizationIdentify = organizationIdentify;
    }

    public AuthorityEnum getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(AuthorityEnum authorityType) {
        this.authorityType = authorityType;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public Long getFkUpdatePersonId() {
        return fkUpdatePersonId;
    }

    public void setFkUpdatePersonId(Long fkUpdatePersonId) {
        this.fkUpdatePersonId = fkUpdatePersonId;
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public Boolean getRemoveFlag() {
        return removeFlag;
    }

    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    public Long getChildCount() {
        return childCount;
    }

    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    public List<OrganizationPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<OrganizationPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public List<Long> getBreaktimeCollection() {
        return breaktimeCollection;
    }

    public void setBreaktimeCollection(List<Long> breaktimeCollection) {
        this.breaktimeCollection = breaktimeCollection;
    }

    public List<Long> getRoleCollection() {
        return roleCollection;
    }

    public void setRoleCollection(List<Long> roleCollection) {
        this.roleCollection = roleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (organizationId != null ? organizationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrganizationEntity)) {
            return false;
        }
        OrganizationEntity other = (OrganizationEntity) object;
        if ((this.organizationId == null && other.organizationId != null) || (this.organizationId != null && !this.organizationId.equals(other.organizationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrganizationEntity{" + "organizationId=" + organizationId + ", parentId=" + parentId + ", organizationName=" + organizationName + ", organizationIdentify=" + organizationIdentify + ", authorityType=" + authorityType + ", languageType=" + languageType + ", mailAddress=" + mailAddress + ", fkUpdatePersonId=" + fkUpdatePersonId + ", updateDatetime=" + updateDatetime + ", removeFlag=" + removeFlag + '}';
    }

}
