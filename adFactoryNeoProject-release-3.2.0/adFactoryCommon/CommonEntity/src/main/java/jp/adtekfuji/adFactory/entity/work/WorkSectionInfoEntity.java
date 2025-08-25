/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import adtekfuji.utility.StringUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * 工程セクションエンティティクラス
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workSection")
public class WorkSectionInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long workSectionId;// 工程セクションID
    @XmlElement(required = true)
    private Long fkWorkId;// 工程ID
    @XmlElement()
    private String documentTitle;// ドキュメント名
    @XmlElement()
    private Integer pageNum;// ページ番号
    @XmlElement()
    private String fileName;// 表示ファイル名
    @XmlElement()
    private Date fileUpdated;// ファイル更新日時
    @XmlElement(required = true)
    private Integer workSectionOrder;// 表示順
    @XmlElement()
    private String physicalName;// 物理ファイル名
    @XmlTransient
    private LinkedList<WorkPropertyInfoEntity> traceabilityCollection;// トレーサビリティ情報一覧
    @XmlTransient
    private String sourcePath;// ソースパス
    @XmlTransient
    private boolean isChenged;// ドキュメントが更新されかどうか

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    /**
     * コンストラクタ
     */
    public WorkSectionInfoEntity() {
    }

    /**
     * 工程セクションIDを取得する。
     *
     * @return 工程セクションID
     */
    public Long getWorkSectionId() {
        return this.workSectionId;
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
    public Long getFkWorkId() {
        return this.fkWorkId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param fkWorkId 工程ID
     */
    public void setFkWorkId(Long fkWorkId) {
        this.fkWorkId = fkWorkId;
    }

    /**
     * ドキュメント名を取得する。
     *
     * @return ドキュメント名
     */
    public String getDocumentTitle() {
        return this.documentTitle;
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
        return this.pageNum;
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
        return this.fileName;
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
        return this.fileUpdated;
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
    public Integer getWorkSectionOrder() {
        return this.workSectionOrder;
    }

    /**
     * 表示順を設定する。
     *
     * @param workSectionOrder 表示順
     */
    public void setWorkSectionOrder(Integer workSectionOrder) {
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
     * トレーサビリティ一覧を取得する。
     *
     * @return トレーサビリティ情報一覧
     */
    public LinkedList<WorkPropertyInfoEntity> getTraceabilityCollection() {
        if (Objects.isNull(this.traceabilityCollection)) {
            this.traceabilityCollection = new LinkedList<>();
        }
        return this.traceabilityCollection;
    }

    /**
     * トレーサビリティ情報一覧を設定する。
     *
     * @param traceabilityCollection トレーサビリティ一覧
     */
    public void setTraceabilityCollection(LinkedList<WorkPropertyInfoEntity> traceabilityCollection) {
        this.traceabilityCollection = traceabilityCollection;
    }

    /**
     * ソースパスを取得する。
     *
     * @return ソースパス
     */
    public String getSourcePath() {
        return this.sourcePath;
    }

    /**
     * ソースパスを設定する。
     *
     * @param sourcePath ソースパス
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * ドキュメントが更新されたかどうかを取得する。
     *
     * @return ドキュメントが更新されたかどうか (true:更新されている, false:更新されていない)
     */
    public boolean isChenged() {
        return this.isChenged;
    }

    /**
     * ドキュメントが更新されかどうかを設定する。
     *
     * @param isChenged ドキュメントが更新されかどうか (true:更新されている, false:更新されていない)
     */
    public void setChenged(boolean isChenged) {
        this.isChenged = isChenged;
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

    /**
     * ドキュメントが設定されているかどうかを取得する。
     *
     * @return ドキュメントが設定されているかどうか (true:設定されている, false:設定されていない)
     */
    public boolean hasDocument() {
        return !StringUtils.isEmpty(this.fileName);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.workSectionId);
        hash = 47 * hash + Objects.hashCode(this.fkWorkId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkSectionInfoEntity other = (WorkSectionInfoEntity) obj;
        if (!Objects.equals(this.workSectionId, other.workSectionId)) {
            return false;
        }
        return Objects.equals(this.fkWorkId, other.fkWorkId);
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("WorkSectionInfoEntity{")
                .append("workSectionId=").append(this.workSectionId)
                .append(", fkWorkId=").append(this.fkWorkId)
                .append(", documentTitle=").append(this.documentTitle)
                .append(", pageNum=").append(this.pageNum)
                .append(", fileName=").append(this.fileName)
                .append(", fileUpdated=").append(this.fileUpdated)
                .append(", workSectionOrder=").append(this.workSectionOrder)
                .append(", physicalName=").append(this.physicalName)
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    /**
     * 表示される情報をコピーする
     *
     * @return
     */
    public WorkSectionInfoEntity clone() {
        WorkSectionInfoEntity entity = new WorkSectionInfoEntity();

        entity.setFkWorkId(this.getFkWorkId());
        entity.setWorkSectionId(this.getWorkSectionId());
        entity.setWorkSectionOrder(this.getWorkSectionOrder());
        entity.setDocumentTitle(this.getDocumentTitle());
        entity.setFileName(this.getFileName());
        entity.setSourcePath(this.getSourcePath());
        entity.setFileUpdated(this.getFileUpdated());
        entity.setPageNum(this.getPageNum());
        entity.setPhysicalName(this.getPhysicalName());

        //トレーサビリティのコピー
        LinkedList<WorkPropertyInfoEntity> traces = new LinkedList();
        this.getTraceabilityCollection().stream().forEach(c -> traces.add(c.clone()));
        entity.setTraceabilityCollection(traces);

        return entity;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean equalsDisplayInfo(WorkSectionInfoEntity other) {
        if (Objects.equals(getDocumentTitle(), other.getDocumentTitle())
                && Objects.equals(getFileName(), other.getFileName())
                && Objects.equals(this.getWorkSectionOrder(), other.getWorkSectionOrder())) {
            //&& traceablitiesEquals(getTraceabilityCollection(), other.getTraceabilityCollection())) {
            return true;
        }
        return false;
    }

    /**
     * トレーサビリティの内容が一致するか調べる
     *
     * @param a
     * @param b
     * @return
     */
    private boolean traceablitiesEquals(LinkedList a, LinkedList b) {
        if (Objects.isNull(a) || Objects.isNull(b)) {
            return false;
        }

        if (a.size() != b.size()) {
            return false;
        }

        java.util.Iterator<WorkPropertyInfoEntity> ita = a.iterator();
        java.util.Iterator<WorkPropertyInfoEntity> itb = b.iterator();

        while (ita.hasNext()) {
            WorkPropertyInfoEntity ena = ita.next();
            WorkPropertyInfoEntity enb = itb.next();
            if (!ena.equalsDisplayInfo(enb)) {
                return false;
            }
        }

        return true;
    }
}
