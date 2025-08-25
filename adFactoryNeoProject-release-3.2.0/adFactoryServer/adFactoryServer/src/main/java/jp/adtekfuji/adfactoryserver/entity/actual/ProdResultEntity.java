/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 生産実績エンティティクラス
 * 
 * @author s-heya
 */
@Entity
@Table(name = "trn_prod_result")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProdResultEntity.find", query = "SELECT p FROM ProdResultEntity p WHERE p.pk.fkKanbanId = :fkKanbanId AND p.pk.fkWorkId = :fkWorkId AND p.pk.uniqueId = :uniqueId"),
    @NamedQuery(name = "ProdResultEntity.findByKanbanId", query = "SELECT p FROM ProdResultEntity p WHERE p.pk.fkKanbanId = :fkKanbanId"),
    @NamedQuery(name = "ProdResultEntity.findByKanbanIdAndWorkId", query = "SELECT p FROM ProdResultEntity p WHERE p.pk.fkKanbanId = :fkKanbanId AND p.pk.fkWorkId = :fkWorkId"),
    @NamedQuery(name = "ProdResultEntity.removeByKanbanId", query = "DELETE FROM ProdResultEntity p WHERE p.pk.fkKanbanId = :fkKanbanId"),
})
public class ProdResultEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    protected ProdResultEntityPK pk;

    //@NotNull
    @Column(name = "fk_work_kanban_id")
    private Long fkWorkKanbanId;
    //@NotNull
    @Column(name = "order_num")
    private Integer orderNum;
    @Size(max = 256)
    @Column(name = "product_spec1")
    private String productSpec1;
    @Size(max = 256)
    @Column(name = "product_spec2")
    private String productSpec2;
    @Size(max = 256)
    @Column(name = "product_spec3")
    private String productSpec3;
    @Size(max = 256)
    @Column(name = "product_spec4")
    private String productSpec4;
    @Size(max = 64)
    @Column(name = "status")
    private String status;
    @Size(max = 256)
    @Column(name = "defect_type")
    private String defectType;
    //@NotNull
    @Column(name = "fk_equipment_id")
    private Long fkEquipmentId;
    @Column(name = "fk_organization_id")
    //@NotNull
    private Long fkOrganizationId;
    @Column(name = "cycle_time")
    @Temporal(TemporalType.TIME)
    private Date cycleTime;
    @Column(name = "comp_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date compDatetime;
    @Column(name = "cycle_sec")
    private Integer cycleSec;

    public ProdResultEntity() {
    }

    public ProdResultEntity(Long fkKanbanId, Long fkWorkKanbanId, Long fkWorkId, Integer orderNum, String uniqueId, Long fkEquipmentId, Long fkOrganizationId) {
        this.pk = new ProdResultEntityPK(fkKanbanId, fkWorkId, uniqueId);
        this.fkWorkKanbanId = fkWorkKanbanId;
        this.orderNum = orderNum;
        this.fkEquipmentId = fkEquipmentId;
        this.fkOrganizationId = fkOrganizationId;
    }

    public ProdResultEntityPK getPK() {
        return pk;
    }

    public void setProdResultId(ProdResultEntityPK pk) {
        this.pk = pk;
    }

    public Long getFkWorkKanbanId() {
        return fkWorkKanbanId;
    }

    public void setFkWorkKanbanId(Long fkWorkKanbanId) {
        this.fkWorkKanbanId = fkWorkKanbanId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getProductSpec1() {
        return productSpec1;
    }

    public void setProductSpec1(String productSpec1) {
        this.productSpec1 = productSpec1;
    }

    public String getProductSpec2() {
        return productSpec2;
    }

    public void setProductSpec2(String productSpec2) {
        this.productSpec2 = productSpec2;
    }

    public String getProductSpec3() {
        return productSpec3;
    }

    public void setProductSpec3(String productSpec3) {
        this.productSpec3 = productSpec3;
    }

    public String getProductSpec4() {
        return productSpec4;
    }

    public void setProductSpec4(String productSpec4) {
        this.productSpec4 = productSpec4;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public Long getFkEquipmentId() {
        return fkEquipmentId;
    }

    public void setFkEquipmentId(Long fkEquipmentId) {
        this.fkEquipmentId = fkEquipmentId;
    }

    public Long getFkOrganizationId() {
        return fkOrganizationId;
    }

    public void setFkOrganizationId(Long fkOrganizationId) {
        this.fkOrganizationId = fkOrganizationId;
    }

    public Date getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(Date cycleTime) {
        this.cycleTime = cycleTime;
    }

    public Date getCompDatetime() {
        return compDatetime;
    }

    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    public Integer getCycleSec() {
        return cycleSec;
    }

    public void setCycleSec(Integer cycleSec) {
        this.cycleSec = cycleSec;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.pk);
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
        final ProdResultEntity other = (ProdResultEntity) obj;
        return Objects.equals(this.pk, other.pk);
    }

    @Override
    public String toString() {
        return "ProdResultEntity{" + "pk=" + pk + 
                ",fkWorkKanbanId=" + fkWorkKanbanId +
                ", orderNum=" + orderNum + ", productSpec1=" + productSpec1 + 
                ", productSpec2=" + productSpec2 + ", productSpec3=" + productSpec3 + ", productSpec4=" + productSpec4 + ", status=" + status + 
                ", defectType=" + defectType + ", fkEquipmentId=" + fkEquipmentId + ", fkOrganizationId=" + fkOrganizationId + 
                ", cycleTime=" + cycleTime + ", compDatetime=" + compDatetime + ", cycleSec=" + cycleSec + '}';
    }

}
