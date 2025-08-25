package jp.adtekfuji.adfactoryserver.entity.kanban;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "trn_product_plan")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "productPlan")
@NamedNativeQueries({
        @NamedNativeQuery(name = "ProductPlanEntity.deleteAll", query = "DELETE FROM trn_product_plan"),
})
public class ProductPlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_plan_id")
    private Long productPlanId;

    @XmlElement(required = true)
    @Column(name = "item_name")
    private String itemName; // 品名

    @XmlElement()
    @Column(name = "item_code")
    private String itemCode; //品目

    @XmlElement()
    @Column(name = "nick_name")
    private String nickName; //ニックネーム

    @XmlElement()
    @Column(name = "work_num")
    private String workNumber; // 工程番号

    @XmlElement()
    @Column(name = "work_code")
    private String workCode; // 工程コード

    @XmlElement()
    @Column(name = "equipment_identify")
    private String equipmentIdentify; // 設備名

    @XmlElement()
    @Column(name = "assert_num")
    private String assertNum; // 資産番号

    @XmlElement()
    @Column(name = "comp_datetime")
    private Date compDatetime; // 完了日

    @XmlElement()
    @Column(name = "production_num")
    private Long productionNumber; // 数量

    @XmlElement()
    @Column(name = "segment")
    private String segment; // セグメント

    @XmlElement()
    @Transient
    private String workName; // 工程名


    public ProductPlanEntity() {
    }

    public ProductPlanEntity(String itemName, String itemCode, String nickName, String workNumber, String workCode, String workName, String equipmentIdentify, String assertNum, Date compDatetime, String segment, Long productionNumber) {
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.nickName = nickName;
        this.workNumber = workNumber;
        this.workCode = workCode;
        this.equipmentIdentify = equipmentIdentify;
        this.assertNum = assertNum;
        this.compDatetime = compDatetime;
        this.productionNumber = productionNumber;
        this.workName = workName;
        this.segment = segment;
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

    public String getWorkName() {
        return workName;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }
}
