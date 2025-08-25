/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.assemblyparts;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

/**
 * 使用部品情報テーブル
 *
 * @author HN)y-harada
 */
@Entity
@Table(name = "trn_assembly_parts")
@XmlRootElement(name = "assemblyParts")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // カンバン親PIDを指定して、使用部品情報の件数を取得する。
    @NamedQuery(name = "AssemblyPartsEntity.countByKanbanPartsId", query = "SELECT COUNT(a.partsId) FROM AssemblyPartsEntity a WHERE a.kanbanPartsId = :kanbanPartsId"),
    // カンバン親PIDを指定して、使用部品情報一覧を取得する。
    @NamedQuery(name = "AssemblyPartsEntity.findByKanbanPartsId", query = "SELECT a FROM AssemblyPartsEntity a WHERE a.kanbanPartsId = :kanbanPartsId ORDER BY a.ano, a.pno, a.partsId"),
    // カンバン名を指定して、使用部品情報の件数を取得する。
    @NamedQuery(name = "AssemblyPartsEntity.countByKanbanName", query = "SELECT COUNT(a.partsId) FROM AssemblyPartsEntity a WHERE a.kanbanName = :kanbanName"),
    // カンバン名を指定して、使用部品情報一覧を取得する。
    @NamedQuery(name = "AssemblyPartsEntity.findByKanbanName", query = "SELECT a FROM AssemblyPartsEntity a WHERE a.kanbanName = :kanbanName ORDER BY a.ano, a.pno, a.partsId"),
})
public class AssemblyPartsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PID
     */
    @Id
    @Basic(optional = false)
    @Size(max = 10)
    @Column(name = "parts_id")
    private String partsId;

    /**
     * 親品目(Y:親品目, N:その他)
     */
    @Size(max = 1)
    @Column(name = "parent_flag")
    private String parentFlag;

    /**
     * 品名(パーツ名)
     */
    @Size(max = 256)
    @Column(name = "product_name")
    private String productName;

    /**
     * 品目コード
     */
    @Size(max = 256)
    @Column(name = "product_number")
    private String productNumber;

    /**
     * Rev
     */
    @Column(name = "revision")
    private Long revision;

    /**
     * ANO
     */
    @Size(max = 30)
    @Column(name = "ano")
    private String ano;

    /**
     * PNO
     */
    @Size(max = 9)
    @Column(name = "pno")
    private String pno;

    /**
     * シリアル番号
     */
    @Size(max = 128)
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * カンバン名
     */
    @Size(max = 128)
    @Column(name = "kanban")
    private String kanbanName;

    /**
     * カンバン親PID
     */
    @Size(max = 128)
    @Column(name = "kanban_parts_id")
    private String kanbanPartsId;

    /**
     * 払出日
     */
    @Column(name = "delivered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredAt;

    /**
     * 使用確定日時
     */
    @Column(name = "fixed_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fixedDate;

    /**
     * 出庫依頼番号
     */
    @Column(name = "delivered_request_id")
    private Long deliveredRequestId;

    /**
     * ブランケット品(Y:ブランケット品, N:その他)
     */
    @Size(max = 1)
    @Column(name = "bracket_flag")
    private String bracketFlag;

    /**
     * 数量
     */
    @Column(name = "qauntity")
    private Long qauntity;

    /**
     * 使用/未使用(Y:使用)
     */
    @Size(max = 1)
    @Column(name = "assembled_flag")
    private String assembledFlag;

    /**
     * 使用確定フラグ(Y:確定)
     */
    @Size(max = 1)
    @Column(name = "fixed_flag")
    private String fixedFlag;

    /**
     * 製番
     */
    @Size(max = 36)
    @Column(name = "control_no")
    private String controlNo;

    /**
     * 親ANO
     */
    @Size(max = 30)
    @Column(name = "parent_no")
    private String parentNo;

    /**
     * 社員番号
     */
    @Size(max = 64)
    @Column(name = "person_no")
    private String personNo;

    /**
     * 更新日時
     */
    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    /**
     * 排他用バージョン
     */
    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;

    /**
     * コンストラクタ
     */
    public AssemblyPartsEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param in 使用部品CSV情報
     */
    public AssemblyPartsEntity(AssemblyPartsCsv in) {
        this.partsId = in.getPartsId();
        this.parentFlag = in.getParentFlag();
        this.productName = in.getProductName();
        this.productNumber = in.getProductNumber();
        this.revision = in.getRevision();
        this.ano = in.getAno();
        this.pno = in.getPno();
        this.serialNumber = in.getSerialNumber();
        this.kanbanName = in.getKanbanName();
        this.kanbanPartsId = in.getKanbanPartsId();
        this.deliveredAt = in.getDeliveredAt();
        this.fixedDate = in.getFixedDate();
        this.deliveredRequestId = in.getDeliveredRequestId();
        this.bracketFlag = in.getBracketFlag();
        this.qauntity = in.getQauntity();
        this.assembledFlag = in.getAssembledFlag();

        if (StringUtils.equals(in.getParentFlag(), "Y")) {
            this.fixedFlag = "Y";
        } else {
            this.fixedFlag = in.getFixedFlag();
        }

        this.controlNo = in.getControlNo();
        this.parentNo = in.getParentNo();
        this.personNo = null;
        this.updateDate = null;
    }

    /**
     * PIDを取得する。
     *
     * @return PID
     */
    public String getPartsId() {
        return this.partsId;
    }

    /**
     * PIDを設定する。
     *
     * @param partsId PID
     */
    public void setPartsId(String partsId) {
        this.partsId = partsId;
    }

    /**
     * 親品目を取得する。
     *
     * @return 親品目(Y:親品目, N:その他)
     */
    public String getParentFlag() {
        return this.parentFlag;
    }

    /**
     * 親品目を設定する。
     *
     * @param parentFlag 親品目(Y:親品目, N:その他)
     */
    public void setParentFlag(String parentFlag) {
        this.parentFlag = parentFlag;
    }

    /**
     * 品名を取得する。
     *
     * @return 品名(パーツ名)
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * 品名を設定する。
     *
     * @param productName 品名(パーツ名)
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 品目コードを取得する。
     *
     * @return 品目コード
     */
    public String getProductNumber() {
        return this.productNumber;
    }

    /**
     * 品目コードを設定する。
     *
     * @param productNumber 品目コード
     */
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    /**
     * Revを取得する。
     *
     * @return Rev
     */
    public Long getRevision() {
        return this.revision;
    }

    /**
     * Revを設定する。
     *
     * @param revision Rev
     */
    public void setRevision(Long revision) {
        this.revision = revision;
    }

    /**
     * ANOを取得する。
     *
     * @return ANO
     */
    public String getAno() {
        return this.ano;
    }

    /**
     * ANOを設定する。
     *
     * @param ano ANO
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * PNOを取得する。
     *
     * @return PNO
     */
    public String getPno() {
        return this.pno;
    }

    /**
     * PNOを設定する。
     *
     * @param pno PNO
     */
    public void setPno(String pno) {
        this.pno = pno;
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public String getSerialNumber() {
        return this.serialNumber;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param serialNumber シリアル番号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }

    /**
     * カンバン親PIDを取得する。
     *
     * @return カンバン親PID
     */
    public String getKanbanPartsId() {
        return this.kanbanPartsId;
    }

    /**
     * カンバン親PIDを設定する。
     *
     * @param kanbanPartsId カンバン親PID
     */
    public void setKanbanPartsId(String kanbanPartsId) {
        this.kanbanPartsId = kanbanPartsId;
    }

    /**
     * 払出日を取得する。
     *
     * @return 払出日
     */
    public Date getDeliveredAt() {
        return this.deliveredAt;
    }

    /**
     * 払出日を設定する。
     *
     * @param deliveredAt 払出日
     */
    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    /**
     * 使用確定日時を取得する。
     *
     * @return 使用確定日時
     */
    public Date getFixedDate() {
        return this.fixedDate;
    }

    /**
     * 使用確定日時を設定する。
     *
     * @param fixedDate 使用確定日時
     */
    public void setFixedDate(Date fixedDate) {
        this.fixedDate = fixedDate;
    }

    /**
     * 出庫依頼番号を取得する。
     *
     * @return 出庫依頼番号
     */
    public Long getDeliveredRequestId() {
        return this.deliveredRequestId;
    }

    /**
     * 出庫依頼番号を設定する。
     *
     * @param deliveredRequestId 出庫依頼番号
     */
    public void setDeliveredRequestId(Long deliveredRequestId) {
        this.deliveredRequestId = deliveredRequestId;
    }

    /**
     * ブランケット品を取得する。
     *
     * @return ブランケット品(Y:ブランケット品, N:その他)
     */
    public String getBracketFlag() {
        return this.bracketFlag;
    }

    /**
     * ブランケット品を設定する。
     *
     * @param bracketFlag ブランケット品(Y:ブランケット品, N:その他)
     */
    public void setBracketFlag(String bracketFlag) {
        this.bracketFlag = bracketFlag;
    }

    /**
     * 数量を取得する。
     *
     * @return 数量
     */
    public Long getQauntity() {
        return this.qauntity;
    }

    /**
     * 数量を設定する。
     *
     * @param qauntity 数量
     */
    public void setQauntity(Long qauntity) {
        this.qauntity = qauntity;
    }

    /**
     * 使用/未使用を取得する。
     *
     * @return 使用/未使用(Y:使用)
     */
    public String getAssembledFlag() {
        return this.assembledFlag;
    }

    /**
     * 使用/未使用を設定する。
     *
     * @param assembledFlag 使用/未使用(Y:使用)
     */
    public void setAssembledFlag(String assembledFlag) {
        this.assembledFlag = assembledFlag;
    }

    /**
     * 使用確定フラグを取得する。
     *
     * @return 使用確定フラグ(Y:確定)
     */
    public String getFixedFlag() {
        return this.fixedFlag;
    }

    /**
     * 使用確定フラグを設定する。
     *
     * @param fixedFlag 使用確定フラグ(Y:確定)
     */
    public void setFixedFlag(String fixedFlag) {
        this.fixedFlag = fixedFlag;
    }

    /**
     * 製番を取得する。
     * 
     * @return 製番
     */
    public String getControlNo() {
        return this.controlNo;
    }

    /**
     * 製番を設定する。
     *
     * @param controlNo 製番
     */
    public void setControlNo(String controlNo) {
        this.controlNo = controlNo;
    }

    /**
     * 親ANOを取得する。
     * 
     * @return 親ANO
     */
    public String getParentNo() {
        return this.parentNo;
    }

    /**
     * 親ANOを設定する。
     *
     * @param parentNo 親ANO
     */
    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }

    /**
     * 社員番号を取得する。
     *
     * @return 社員番号
     */
    public String getPersonNo() {
        return this.personNo;
    }

    /**
     * 社員番号を設定する。
     *
     * @param personNo 社員番号
     */
    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDate 更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 排他用バージョンを取得する。
     *
     * @return 排他用バージョン
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バージョンを設定する。
     *
     * @param verInfo 排他用バージョン
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.partsId);
        return hash;
    }

    /**
     * イコール
     */
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
        final AssemblyPartsEntity other = (AssemblyPartsEntity) obj;
        if (!Objects.equals(this.partsId, other.partsId)) {
            return false;
        }
        return true;
    }

    /**
     * String化
     */
    @Override
    public String toString() {
        return new StringBuilder("AssemblyPartsEntity{")
                .append("partsId=").append(this.partsId)
                .append(", parentFlag=").append(this.parentFlag)
                .append(", productName=").append(this.productName)
                .append(", productNumber=").append(this.productNumber)
                .append(", revision=").append(this.revision)
                .append(", ano=").append(this.ano)
                .append(", pno=").append(this.pno)
                .append(", serialNumber=").append(this.serialNumber)
                .append(", kanbanName=").append(this.kanbanName)
                .append(", kanbanPartsId=").append(this.kanbanPartsId)
                .append(", deliveredAt=").append(this.deliveredAt)
                .append(", fixedDate=").append(this.fixedDate)
                .append(", deliveredRequestId=").append(this.deliveredRequestId)
                .append(", bracketFlag=").append(this.bracketFlag)
                .append(", qauntity=").append(this.qauntity)
                .append(", assembledFlag=").append(this.assembledFlag)
                .append(", fixedFlag=").append(this.fixedFlag)
                .append(", controlNo=").append(this.controlNo)
                .append(", parentNo=").append(this.parentNo)
                .append(", personNo=").append(this.personNo)
                .append(", updateDate=").append(this.updateDate)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
