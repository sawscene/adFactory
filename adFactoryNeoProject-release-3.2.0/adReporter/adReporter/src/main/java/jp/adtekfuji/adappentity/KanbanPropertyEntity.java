/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adappentity;

import java.io.Serializable;
import jp.adtekfuji.adappentity.enumerate.CustomPropertyTypeEnum;

/**
 * カンバンプロパティ情報
 *
 * @author nar-nakamura
 */
public class KanbanPropertyEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kanbannPropertyId;
    private Long fkKanbanId;
    private String kanbanPropertyName;
    private CustomPropertyTypeEnum kanbanPropertyType;
    private String kanbanPropertyValue;
    private long kanbanPropertyOrder;

    public KanbanPropertyEntity() {
    }

    public long getKanbanPropId() {
        return this.kanbannPropertyId;
    }

    public long getFkKanbanId() {
        return this.fkKanbanId;
    }

    public String getKanbanPropName() {
        return this.kanbanPropertyName;
    }

    public CustomPropertyTypeEnum getKanbanPropType() {
        return this.kanbanPropertyType;
    }

    public String getKanbanPropValue() {
        return this.kanbanPropertyValue;
    }

    public void setKanbanPropValue(String kanbanPropertyValue) {
        this.kanbanPropertyValue = kanbanPropertyValue;
    }

    public long getKanbanPropOrder() {
        return this.kanbanPropertyOrder;
    }

    @Override
    public String toString() {
        return "KanbanPropertyEntity { kanbannPropertyId=" + this.getKanbanPropId() + ", fkKanbanId=" + this.getFkKanbanId() + ", kanbanPropertyName=" + this.getKanbanPropName() + ", kanbanPropertyType=" + this.getKanbanPropType() + ", kanbanPropertyValue=" + this.getKanbanPropValue() + ", kanbanPropertyOrder=" + this.getKanbanPropOrder() + " }";
    }
}
