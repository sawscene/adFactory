/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
import org.apache.commons.lang3.StringUtils;

/**
 * 工程セクションエンティティクラス
 *
 * @author s-heya
 */
@Entity
@Table(name = "mst_work_section")
@XmlRootElement(name = "workSection")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "WorkSectionEntity.findAll", query = "SELECT w FROM WorkSectionEntity w"),
    @NamedQuery(name = "WorkSectionEntity.findByWorkSectionId", query = "SELECT w FROM WorkSectionEntity w WHERE w.workSectionId = :workSectionId"),
    @NamedQuery(name = "WorkSectionEntity.findByFkWorkId", query = "SELECT w FROM WorkSectionEntity w WHERE w.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "WorkSectionEntity.removeByFkWorkId", query = "DELETE FROM WorkSectionEntity w WHERE w.fkWorkId = :fkWorkId")
})
public class WorkSectionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_section_id")
    private Long workSectionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fk_work_id")
    private long fkWorkId;
    @Size(max = 256)
    @Column(name = "document_title")
    private String documentTitle;
    @Column(name = "page_num")
    private Integer pageNum;
    @Size(max = 256)
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fileUpdated;
    @Basic(optional = false)
    @NotNull
    @Column(name = "work_section_order")
    private int workSectionOrder;

    public WorkSectionEntity() {
    }

    public WorkSectionEntity(WorkSectionEntity in) {
        this.workSectionId = in.workSectionId;
        this.fkWorkId = in.fkWorkId;
        this.documentTitle = in.documentTitle;
        this.pageNum = in.pageNum;
        this.fileName = in.fileName;
        this.fileUpdated = in.fileUpdated;
        this.workSectionOrder = in.workSectionOrder;
    }

    public WorkSectionEntity(Long workSectionId, long fkWorkId, int workSectionOrder) {
        this.workSectionId = workSectionId;
        this.fkWorkId = fkWorkId;
        this.workSectionOrder = workSectionOrder;
    }

    public Long getWorkSectionId() {
        return workSectionId;
    }

    public void setWorkSectionId(Long workSectionId) {
        this.workSectionId = workSectionId;
    }

    public long getFkWorkId() {
        return fkWorkId;
    }

    public void setFkWorkId(long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getFileUpdated() {
        return fileUpdated;
    }

    public void setFileUpdated(Date fileUpdated) {
        this.fileUpdated = fileUpdated;
    }

    public int getWorkSectionOrder() {
        return workSectionOrder;
    }

    public void setWorkSectionOrder(int workSectionOrder) {
        this.workSectionOrder = workSectionOrder;
    }

    public boolean hasDocument() {
        return !StringUtils.isEmpty(this.fileName);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workSectionId != null ? workSectionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkSectionEntity)) {
            return false;
        }
        WorkSectionEntity other = (WorkSectionEntity) object;
        return !((this.workSectionId == null && other.workSectionId != null) || (this.workSectionId != null && !this.workSectionId.equals(other.workSectionId)));
    }

    @Override
    public String toString() {
        return "WorkSectionEntity{" + "workSectionId=" + workSectionId + ", fkWorkId=" + fkWorkId + ", documentTitle=" + documentTitle
                + ", pageNum=" + pageNum + ", fileName=" + fileName + ", fileUpdated=" + fileUpdated + ", workSectionOrder=" + workSectionOrder + '}';
    }
}
