/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.ledger;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;


/**
 * 帳票ファイル情報
 *
 * @author yu.nara
 */
@Entity
@Table(name = "trn_ledger_file")
@XmlRootElement(name = "ledgerFile")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = "LedgerFileEntity.findByLedgerIds", query = "SELECT l FROM LedgerFileEntity l WHERE l.ledgerId IN :ledgerIds"),
})
public class LedgerFileEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ledger_file_id")
    private Long ledgerFileId;                        // 帳票ファイルID

    @Column(name = "ledger_id")
    private Long ledgerId;               // 帳票ID

    @Column(name = "creator_id")                  // 作成者ID
    private Long organizationId;

    @Column(name = "key_word")
    private String keyword;                        // キーワード

    @Column(name = "create_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDatetime;                // 作成日時

    @Column(name = "from_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fromDate;                      // 開始日時

    @Column(name = "to_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date toDate;                        // 終了日時

    @Column(name = "organization_ids")
    private String organizationIds;            // 作業組織ID

    @Column(name = "equipment_ids")
    private String equipmentIds;               // 作業設備ID

    @Column(name = "file_path")
    private String filePath;                  // 帳票ファイル名

    public LedgerFileEntity() {
    }

    public LedgerFileEntity(Long ledgerId, Long organizationId, String keyword, Date updateDatetime, Date fromDate, Date toDate, String organizationIds, String equipmentIds, String filePath) {
        this.ledgerId = ledgerId;
        this.organizationId = organizationId;
        this.keyword = keyword;
        this.updateDatetime = updateDatetime;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.organizationIds = organizationIds;
        this.equipmentIds = equipmentIds;
        this.filePath = filePath;
    }

    public Long getLedgerFileId() {
        return ledgerFileId;
    }

    public void setLedgerFileId(Long ledgerFileId) {
        this.ledgerFileId = ledgerFileId;
    }

    public Long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(Long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(String organizationIds) {
        this.organizationIds = organizationIds;
    }

    public String getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(String equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "LedgerFileEntity{" +
                "ledgerFileId=" + ledgerFileId +
                ", ledgerId=" + ledgerId +
                ", organizationId=" + organizationId +
                ", keyword='" + keyword + '\'' +
                ", updateDatetime=" + updateDatetime +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", organizationIds='" + organizationIds + '\'' +
                ", equipmentIds='" + equipmentIds + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
