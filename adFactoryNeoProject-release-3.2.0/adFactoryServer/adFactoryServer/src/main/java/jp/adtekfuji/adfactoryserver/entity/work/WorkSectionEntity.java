/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.work;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

/**
 * 工程セクション情報
 *
 * @author s-heya
 */
@Entity
@Table(name = "mst_work_section")
@XmlRootElement(name = "workSection")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 工程IDを指定して、工程セクション情報を取得する。
    @NamedQuery(name = "WorkSectionEntity.findByWorkId", query = "SELECT w FROM WorkSectionEntity w WHERE w.workId = :workId"),
    // 工程IDを指定して、工程セクション情報を削除する。
    @NamedQuery(name = "WorkSectionEntity.removeByWorkId", query = "DELETE FROM WorkSectionEntity w WHERE w.workId = :workId"),
})
public class WorkSectionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "work_section_id")
    private Long workSectionId;// 工程セクションID

    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_id")
    @XmlElement(name = "fkWorkId")
    private long workId;// 工程ID

    @Size(max = 256)
    @Column(name = "document_title")
    private String documentTitle;// ドキュメント名

    @Column(name = "page_num")
    private Integer pageNum;// ページ番号

    @Size(max = 256)
    @Column(name = "file_name")
    private String fileName;// 表示ファイル名

    @Column(name = "file_update_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fileUpdated;// ファイル更新日時

    @Basic(optional = false)
    //@NotNull
    @Column(name = "work_section_order")
    private int workSectionOrder;// 表示順

    @Column(name = "physical_file_name")
    private String physicalName;// 物理ファイル名

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public WorkSectionEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 
     */
    public WorkSectionEntity(WorkSectionEntity in) {
        this.workSectionId = in.workSectionId;
        this.workId = in.workId;
        this.documentTitle = in.documentTitle;
        this.pageNum = in.pageNum;
        this.fileName = in.fileName;
        this.fileUpdated = in.fileUpdated;
        this.workSectionOrder = in.workSectionOrder;
        this.physicalName = in.physicalName;
    }

    /**
     * コンストラクタ
     *
     * @param workSectionId 工程セクションID
     * @param workId 工程ID
     * @param workSectionOrder 表示順
     */
    public WorkSectionEntity(Long workSectionId, long workId, int workSectionOrder) {
        this.workSectionId = workSectionId;
        this.workId = workId;
        this.workSectionOrder = workSectionOrder;
    }

    /**
     * 工程セクションIDを取得する。
     *
     * @return 工程セクションID
     */
    public Long getWorkSectionId() {
        return workSectionId;
    }

    /**
     * 工程セクションIDを設定する。
     *
     * @param workSectionId 工程セクションID
     */
    public void setWorkSectionId(Long workSectionId) {
        this.workSectionId = workSectionId;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public long getWorkId() {
        return workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(long workId) {
        this.workId = workId;
    }

    /**
     * ドキュメント名を取得する。
     *
     * @return ドキュメント名
     */
    public String getDocumentTitle() {
        return documentTitle;
    }

    /**
     * ドキュメント名を設定する。
     *
     * @param documentTitle ドキュメント名
     */
    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    /**
     * ページ番号を取得する。
     *
     * @return ページ番号
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * ページ番号を設定する。
     *
     * @param pageNum ページ番号
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 表示ファイル名を取得する。
     *
     * @return 表示ファイル名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 表示ファイル名を設定する。
     *
     * @param fileName 表示ファイル名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * ファイル更新日時を取得する。
     *
     * @return ファイル更新日時
     */
    public Date getFileUpdated() {
        return fileUpdated;
    }

    /**
     * ファイル更新日時を設定する。
     *
     * @param fileUpdated ファイル更新日時
     */
    public void setFileUpdated(Date fileUpdated) {
        this.fileUpdated = fileUpdated;
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public int getWorkSectionOrder() {
        return workSectionOrder;
    }

    /**
     * 表示順を設定する。
     *
     * @param workSectionOrder 表示順
     */
    public void setWorkSectionOrder(int workSectionOrder) {
        this.workSectionOrder = workSectionOrder;
    }

    /**
     * 物理ファイル名を取得する。
     * 
     * @return 
     */
    public String getPhysicalName() {
        return this.physicalName;
    }

    /**
     * 物理ファイル名を設定する。
     * 
     * @param physicalName 
     */
    public void setPhysicalName(String physicalName) {
        this.physicalName = physicalName;
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

    /***
     * ドキュメントが設定されているかどうかを取得する。
     *
     * @return ドキュメントが設定されているかどうか (true:設定されている, false:設定されていない)
     */
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
        return new StringBuilder("WorkSectionEntity{")
                .append("workSectionId=").append(this.workSectionId)
                .append(", ")
                .append("workId=").append(this.workId)
                .append(", ")
                .append("documentTitle=").append(this.documentTitle)
                .append(", ")
                .append("pageNum=").append(this.pageNum)
                .append(", ")
                .append("fileName=").append(this.fileName)
                .append(", ")
                .append("fileUpdated=").append(this.fileUpdated)
                .append(", ")
                .append("workSectionOrder=").append(this.workSectionOrder)
                .append(", ")
                .append("physicalName=").append(this.physicalName)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
