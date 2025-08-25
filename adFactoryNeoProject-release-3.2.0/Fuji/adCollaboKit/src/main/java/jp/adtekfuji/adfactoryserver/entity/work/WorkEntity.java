/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;

/**
 * 工程情報
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_work")
@XmlRootElement(name = "work")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkEntity.countWorkflowWorkAssociation", query = "SELECT COUNT(c.associationId) FROM ConWorkflowWorkEntity c WHERE c.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "WorkEntity.countWorkflowSeparateWorkAssociation", query = "SELECT COUNT(c.associationId) FROM ConWorkflowSeparateworkEntity c WHERE c.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "WorkEntity.countKanbanAssociation", query = "SELECT COUNT(w.workKanbanId) FROM WorkKanbanEntity w WHERE w.fkWorkId = :fkWorkId"),

    // 工程名の重複確認 (追加時) ※.削除済も対象
    @NamedQuery(name = "WorkEntity.checkAddByWorkName", query = "SELECT COUNT(w.workId) FROM WorkEntity w WHERE w.workName = :workName"),
    // 工程名の重複確認 (更新時) ※.削除済も対象
    @NamedQuery(name = "WorkEntity.checkUpdateByWorkName", query = "SELECT COUNT(w.workId) FROM WorkEntity w WHERE w.workName = :workName AND w.workId != :workId"),

    @NamedQuery(name = "WorkEntity.findAll", query = "SELECT w FROM WorkEntity w WHERE w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByWorkId", query = "SELECT w FROM WorkEntity w WHERE w.workId = :workId AND w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByWorkName", query = "SELECT w FROM WorkEntity w WHERE w.workName = :workName AND w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByTaktTime", query = "SELECT w FROM WorkEntity w WHERE w.taktTime = :taktTime AND w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByContent", query = "SELECT w FROM WorkEntity w WHERE w.content = :content AND w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByFkUpdatePersonId", query = "SELECT w FROM WorkEntity w WHERE w.fkUpdatePersonId = :fkUpdatePersonId AND w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByUpdateDatetime", query = "SELECT w FROM WorkEntity w WHERE w.updateDatetime = :updateDatetime AND w.removeFlag = false"),
    @NamedQuery(name = "WorkEntity.findByRemoveFlag", query = "SELECT w FROM WorkEntity w WHERE w.removeFlag = :removeFlag"),
    // 工程階層に属する工程を問い合わせる
    @NamedQuery(name = "WorkEntity.findByFkWorkHierarchyId", query = "SELECT w FROM ConWorkHierarchyEntity c JOIN WorkEntity w ON c.conWorkHierarchyEntityPK.fkWorkId = w.workId WHERE c.conWorkHierarchyEntityPK.fkWorkHierarchyId = :fkHierarchyId ORDER BY w.workName"),
    // 工程順に属する工程を問い合わせる ※.削除済も対象
    @NamedQuery(name = "WorkEntity.countByWorkflowId", query = "SELECT COUNT(w.workId) FROM ConWorkflowWorkEntity c JOIN WorkEntity w ON c.fkWorkId = w.workId WHERE c.fkWorkflowId = :fkWorkflowId"),
    @NamedQuery(name = "WorkEntity.findByWorkflowId", query = "SELECT w FROM ConWorkflowWorkEntity c JOIN WorkEntity w ON c.fkWorkId = w.workId WHERE c.fkWorkflowId = :fkWorkflowId"),
    // 工程IDで工程名を取得する ※.削除済も対象
    @NamedQuery(name = "WorkEntity.findWorkName", query = "SELECT w.workName FROM WorkEntity w WHERE w.workId = :workId")
})
public class WorkEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_id")
    private Long workId;
    @Transient
    private Long parentId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "work_name")
    private String workName;
    @Column(name = "takt_time")
    private Integer taktTime;
    @Size(max = 2147483647)
    @Column(name = "content")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentTypeEnum contentType;
    @XmlElement(name = "updatePersonId")
    @Column(name = "fk_update_person_id")
    private Long fkUpdatePersonId;
    @Column(name = "update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;
    @XmlTransient
    @Column(name = "remove_flag")
    private Boolean removeFlag = false;
    @Size(max = 128)
    @Column(name = "font_color")
    private String fontColor;
    @Size(max = 128)
    @Column(name = "back_color")
    private String backColor;
    @Size(max = 2147483647)
    @Column(name = "use_parts")
    private String useParts;
    @Size(max = 64)
    @Column(name = "work_number")
    private String workNumber;
    @XmlElementWrapper(name = "workSections")
    @XmlElement(name = "workSection")
    @Transient
    private List<WorkSectionEntity> workSectionCollection;
    @XmlElementWrapper(name = "workPropertys")
    @XmlElement(name = "workProperty")
    @Transient
    private List<WorkPropertyEntity> propertyCollection = null;

    public WorkEntity() {
    }

    public WorkEntity(WorkEntity in) {
        this.parentId = in.parentId;
        this.workName = in.workName;
        this.taktTime = in.taktTime;
        this.content = in.content;
        this.contentType = in.contentType;
        this.fkUpdatePersonId = in.fkUpdatePersonId;
        this.updateDatetime = in.updateDatetime;
        this.removeFlag = in.removeFlag;
        this.fontColor = in.fontColor;
        this.backColor = in.backColor;
        this.propertyCollection = new ArrayList<>();
        for (WorkPropertyEntity property : in.getPropertyCollection()) {
            this.propertyCollection.add(new WorkPropertyEntity(property));
        }
        this.workSectionCollection = new ArrayList<>();
        for (WorkSectionEntity section : in.getWorkSectionCollection()) {
            this.workSectionCollection.add(new WorkSectionEntity(section));
        }
    }

    public WorkEntity(Long parentId, String workName, Integer taktTime, String content, ContentTypeEnum contentType, Long fkUpdatePersonId, Date updateDatetime, String fontColor, String backColor) {
        this.parentId = parentId;
        this.workName = workName;
        this.taktTime = taktTime;
        this.content = content;
        this.contentType = contentType;
        this.fkUpdatePersonId = fkUpdatePersonId;
        this.updateDatetime = updateDatetime;
        this.removeFlag = false;
        this.fontColor = fontColor;
        this.backColor = backColor;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Integer getTaktTime() {
        return taktTime;
    }

    public void setTaktTime(Integer taktTime) {
        this.taktTime = taktTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ContentTypeEnum getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
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

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getUseParts() {
        return this.useParts;
    }

    public void setUseParts(String useParts) {
        this.useParts = useParts;
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public List<WorkPropertyEntity> getPropertyCollection() {
        return propertyCollection;
    }

    public void setPropertyCollection(List<WorkPropertyEntity> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    public List<WorkSectionEntity> getWorkSectionCollection() {
        return workSectionCollection;
    }

    public void setWorkSectionCollection(List<WorkSectionEntity> workSectionCollection) {
        this.workSectionCollection = workSectionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workId != null ? workId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkEntity)) {
            return false;
        }
        WorkEntity other = (WorkEntity) object;
        return !((this.workId == null && other.workId != null) || (this.workId != null && !this.workId.equals(other.workId)));
    }

    @Override
    public String toString() {
        return "WorkEntity{"
                + "workId=" + this.workId
                + ", parentId=" + this.parentId
                + ", workName=" + this.workName
                + ", taktTime=" + this.taktTime
                + ", content=" + this.content
                + ", contentType=" + this.contentType
                + ", fkUpdatePersonId=" + this.fkUpdatePersonId
                + ", updateDatetime=" + this.updateDatetime
                + ", removeFlag=" + this.removeFlag
                + ", fontColor=" + this.fontColor
                + ", backColor=" + this.backColor
                + ", useParts=" + this.useParts
                + ", workNumber=" + this.workNumber
                + '}';
    }
}
