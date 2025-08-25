/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import java.io.Serializable;
import jp.adtekfuji.adappentity.enumerate.CustomPropertyTypeEnum;

/**
 * 生産実績プロパティ
 *
 * @author nar-nakamura
 */
public class ActualPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long actualPropId;
    private long fkActualId;
    private String actualPropName;
    private CustomPropertyTypeEnum actualPropType;
    private String actualPropValue;
    private Integer actualPropOrder;

    public ActualPropertyEntity() {
    }

    public Long getActualPropId() {
        return this.actualPropId;
    }

    public void setActualPropId(Long actualPropId) {
        this.actualPropId = actualPropId;
    }

    public long getFkActualId() {
        return this.fkActualId;
    }

    public void setFkActualId(long fkActualId) {
        this.fkActualId = fkActualId;
    }

    public String getActualPropName() {
        return this.actualPropName;
    }

    public void setActualPropName(String actualPropName) {
        this.actualPropName = actualPropName;
    }

    public CustomPropertyTypeEnum getActualPropType() {
        return this.actualPropType;
    }

    public void setActualPropType(CustomPropertyTypeEnum actualPropType) {
        this.actualPropType = actualPropType;
    }

    public String getActualPropValue() {
        return this.actualPropValue;
    }

    public void setActualPropValue(String actualPropValue) {
        this.actualPropValue = actualPropValue;
    }

    public Integer getActualPropOrder() {
        return this.actualPropOrder;
    }

    public void setActualPropOrder(Integer actualPropOrder) {
        this.actualPropOrder = actualPropOrder;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.actualPropId != null ? this.actualPropId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ActualPropertyEntity)) {
            return false;
        }
        ActualPropertyEntity other = (ActualPropertyEntity) object;
        if ((this.actualPropId == null && other.actualPropId != null) || (this.actualPropId != null && !this.actualPropId.equals(other.actualPropId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActualPropertyEntity{" + "actualPropId=" + actualPropId + ", fkActualId=" + fkActualId + ", actualPropName=" + actualPropName + ", actualPropType=" + actualPropType + ", actualPropValue=" + actualPropValue + ", actualPropOrder=" + actualPropOrder + '}';
    }
}
