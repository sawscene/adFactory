package jp.adtekfuji.adFactory.entity.productplan;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "productPlan")
public class ProductPlanInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Long productPlanId; // id

    @XmlElement(required = true)
    private String itemName; // 品名

    @XmlElement()
    private String itemCode; //品目

    @XmlElement()
    private String nickName; //ニックネーム

    @XmlElement()
    private String workNumber; // 工程番号

    @XmlElement()
    private String workCode; // 工程コード

    @XmlElement()
    private String equipmentIdentify; // 設備名

    @XmlElement()
    private String assertNum; // 資産番号

    @XmlElement()
    private Date compDatetime; // 完了日

    @XmlElement()
    private Long productionNumber; // 数量

    @XmlElement()
    private String workName; // 工程コード

    @XmlElement()
    private String segment; // セグメント

    public ProductPlanInfoEntity() {
    }

    public ProductPlanInfoEntity(String itemName, String itemCode, String nickName, String workNumber, String workCode, String workName, String equipmentIdentify, String assertNum, Date compDatetime, String segment, Long productionNumber) {
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.nickName = nickName;
        this.workNumber = workNumber;
        this.workCode = workCode;
        this.equipmentIdentify = equipmentIdentify;
        this.assertNum = assertNum;
        this.compDatetime = compDatetime;
        this.productionNumber = productionNumber;
        this.segment = segment;
        this.workName = workName;
    }

    public Long getProductPlanId() {
        return productPlanId;
    }

    public void setProductPlanId(Long productPlanId) {
        this.productPlanId = productPlanId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getEquipmentIdentify() {
        return equipmentIdentify;
    }

    public void setEquipmentIdentify(String equipmentIdentify) {
        this.equipmentIdentify = equipmentIdentify;
    }

    public String getAssertNum() {
        return assertNum;
    }

    public void setAssertNum(String assertNum) {
        this.assertNum = assertNum;
    }

    public Date getCompDatetime() {
        return compDatetime;
    }

    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    public Long getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(Long productionNumber) {
        this.productionNumber = productionNumber;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }
}
