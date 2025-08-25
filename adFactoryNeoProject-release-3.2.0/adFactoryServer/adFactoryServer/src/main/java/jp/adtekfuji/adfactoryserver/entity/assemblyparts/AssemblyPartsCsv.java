/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.assemblyparts;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import java.io.Serializable;
import java.util.Date;

/**
 * 使用部品CSV情報
 *
 * @author nar-nakamura
 */
public class AssemblyPartsCsv implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 親品目(Y:親品目, N:その他)
     */
    @CsvBindByPosition(position = 0)
    private String parentFlag;

    /**
     * PID
     */
    @CsvBindByPosition(position = 1)
    private String partsId;

    /**
     * 品名(パーツ名)
     */
    @CsvBindByPosition(position = 2)
    private String productName;

    /**
     * 品目コード
     */
    @CsvBindByPosition(position = 3)
    private String productNumber;

    /**
     * Rev
     */
    @CsvBindByPosition(position = 4)
    private Long revision;

    /**
     * ANO
     */
    @CsvBindByPosition(position = 5)
    private String ano;

    /**
     * PNO
     */
    @CsvBindByPosition(position = 6)
    private String pno;

    /**
     * シリアル番号
     */
    @CsvBindByPosition(position = 7)
    private String serialNumber;

    /**
     * カンバン名
     */
    @CsvBindByPosition(position = 8)
    private String kanbanName;

    /**
     * カンバン親PID
     */
    @CsvBindByPosition(position = 9)
    private String kanbanPartsId;

    /**
     * 払出日
     */
    @CsvBindByPosition(position = 10)
    @CsvDate(value = "yyyy/MM/dd HH:mm:ss")
    private Date deliveredAt;

    /**
     * 使用確定日時　※.インポート対象外
     */
    //@CsvBindByPosition(position = 11)
    //@CsvDate(value = "yyyy/MM/dd HH:mm:ss")
    private Date fixedDate = null;

    /**
     * 出庫依頼番号
     */
    @CsvBindByPosition(position = 12)
    private Long deliveredRequestId;

    /**
     * ブランケット品(Y:ブランケット品, N:その他)
     */
    @CsvBindByPosition(position = 13)
    private String bracketFlag;

    /**
     * 数量
     */
    @CsvBindByPosition(position = 14)
    private Long qauntity;

    /**
     * 使用/未使用(Y:使用)　※.インポート対象外
     */
    //@CsvBindByPosition(position = 15)
    private String assembledFlag = null;

    /**
     * 使用確定フラグ(Y:確定)　※.インポート対象外
     */
    //@CsvBindByPosition(position = 16)
    private String fixedFlag = null;

    /**
     * 製番
     */
    @CsvBindByPosition(position = 17)
    private String controlNo;

    /**
     * 親ANO
     */
    @CsvBindByPosition(position = 18)
    private String parentNo;

    /**
     * コンストラクタ
     */
    public AssemblyPartsCsv() {
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

    @Override
    public String toString() {
        return new StringBuilder("AssemblyPartsCsv{")
                .append("parentFlag=").append(this.parentFlag)
                .append(", partsId=").append(this.partsId)
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
                .append("}")
                .toString();
    }
}
