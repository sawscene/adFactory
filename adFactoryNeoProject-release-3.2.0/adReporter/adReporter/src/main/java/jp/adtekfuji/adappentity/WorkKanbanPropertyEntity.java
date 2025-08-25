/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import java.io.Serializable;
import jp.adtekfuji.adappentity.enumerate.CustomPropertyTypeEnum;

/**
 * 工程カンバンプロパティ
 *
 * @author nar-nakamura
 */
public class WorkKanbanPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long workKanbannPropertyId;
    private Long fkWorkKanbanId;
    private String workKanbanPropName;
    private CustomPropertyTypeEnum workKanbanPropType;
    private String workKanbanPropValue;
    private Long workKanbanPropOrder;

    public WorkKanbanPropertyEntity() {
    }

    public long getWorkKanbanPropId() {
        return this.workKanbannPropertyId;
    }

    public long getFkMasterId() {
        return this.fkWorkKanbanId;
    }

    public String getWorkKanbanPropName() {
        return this.workKanbanPropName;
    }

    public CustomPropertyTypeEnum getWorkKanbanPropType() {
        return this.workKanbanPropType;
    }

    public String getWorkKanbanPropValue() {
        return this.workKanbanPropValue;
    }

    public long getWorkKanbanPropOrder() {
        return this.workKanbanPropOrder;
    }

    @Override
    public String toString() {
        return "WorkKanbanPropertyEntity { workKanbannPropertyId=" + this.getWorkKanbanPropId() + ", fkWorkKanbanId=" + this.getFkMasterId() + ", workKanbanPropertyName=" + this.getWorkKanbanPropName() + ", workKanbanPropertyType=" + this.getWorkKanbanPropType() + ", workKanbanPropertyValue=" + this.getWorkKanbanPropValue() + ", workKanbanPropertyOrder=" + this.getWorkKanbanPropOrder() + " }";
    }
}
