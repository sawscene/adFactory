/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.ledger;

import adtekfuji.utility.StringUtils;
import jp.adtekfuji.adFactory.enumerate.LedgerTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.*;
/**
 * カンバン情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledgerFile")
public class LedgerFileInfoEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long ledgerFileId;                        // 帳票ファイルID

    @XmlElement()
    private Long ledgerId;               // 帳票ID

    @XmlElement()                  // 作成者ID
    private Long organizationId;

    @XmlElement()
    private String keyword;                        // キーワード

    @XmlElement()
    private Date updateDatetime;                // 作成日時

    @XmlElement()
    private Date fromDate;                      // 開始日時

    @XmlElement()
    private Date toDate;                        // 終了日時

    @XmlElement()
    private String organizationIds;            // 作業組織ID

    @XmlElement()
    private String equipmentIds;               // 作業設備ID

    @XmlElement()
    private String filePath;                  // 帳票ファイル名

    public LedgerFileInfoEntity() {
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

    public void setOrganization_id(Long organizationId) {
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
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.ledgerFileId);
        return hash;
    }

    @Override
    public String toString() {
        return "LedgerFileInfoEntity{" +
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
