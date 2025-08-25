/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | s
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.unit;

import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユニット関連情報
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.26.Wen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "conUnitAssociate")
public class ConUnitAssociateInfoEntity {

    private static final long serialVersionUID = 1L;

    private LongProperty unitAssociationIdProperty;
    private LongProperty fkParentUnitIdProperty;
    private LongProperty fkKanbanIdProperty;
    private StringProperty kanbanNameProperty;
    private LongProperty fkUnitIdProperty;
    private StringProperty unitNameProperty;
    private IntegerProperty unitAssociateOrderProperty;

    @XmlElement(required = true)
    private Long unitAssociationId;
    @XmlElement()
    private Long fkParentUnitId;
    @XmlElement()
    private Long fkKanbanId;
    @XmlElement()
    private String kanbanName;
    @XmlElement()
    private Long fkUnitId;
    @XmlElement()
    private String unitName;
    @XmlElement()
    private Integer unitAssociateOrder;

    public ConUnitAssociateInfoEntity() {
    }

    public ConUnitAssociateInfoEntity(Long unitAssociationId, Long fkParentUnitId, boolean skipFlag, Integer unitAssociateOrder) {
        this.unitAssociationId = unitAssociationId;
        this.fkParentUnitId = fkParentUnitId;
        this.unitAssociateOrder = unitAssociateOrder;
    }

    public ConUnitAssociateInfoEntity(ConUnitAssociateInfoEntity in) {
        this.fkParentUnitId = in.fkParentUnitId;
        this.fkKanbanId = in.fkKanbanId;
        this.fkUnitId = in.fkUnitId;
        this.unitAssociateOrder = in.unitAssociateOrder;
    }

    public LongProperty unitAssociationIdProperty() {
        if (Objects.isNull(unitAssociationIdProperty)) {
            unitAssociationIdProperty = new SimpleLongProperty(unitAssociationId);
        }
        return unitAssociationIdProperty;
    }

    public LongProperty fkParentUnitIdProperty() {
        if (Objects.isNull(fkParentUnitIdProperty)) {
            fkParentUnitIdProperty = new SimpleLongProperty(fkParentUnitId);
        }
        return fkParentUnitIdProperty;
    }

    public LongProperty fkKanbanIdProperty() {
        if (Objects.isNull(fkKanbanIdProperty)) {
            fkKanbanIdProperty = new SimpleLongProperty(fkKanbanId);
        }
        return fkKanbanIdProperty;
    }

    public StringProperty kanbanNameProperty() {
        if (Objects.isNull(kanbanNameProperty)) {
            kanbanNameProperty = new SimpleStringProperty(kanbanName);
        }
        return kanbanNameProperty;
    }

    public LongProperty fkUnitIdProperty() {
        if (Objects.isNull(fkUnitIdProperty)) {
            fkUnitIdProperty = new SimpleLongProperty(fkUnitId);
        }
        return fkUnitIdProperty;
    }

    public StringProperty unitNameProperty() {
        if (Objects.isNull(unitNameProperty)) {
            unitNameProperty = new SimpleStringProperty(unitName);
        }
        return unitNameProperty;
    }

    public IntegerProperty unitAssociateOrderProperty() {
        if (Objects.isNull(unitAssociateOrderProperty)) {
            unitAssociateOrderProperty = new SimpleIntegerProperty(unitAssociateOrder);
        }
        return unitAssociateOrderProperty;
    }

    public Long getUnitAssociationId() {
        if (Objects.nonNull(unitAssociationIdProperty)) {
            return unitAssociationIdProperty.get();
        }
        return unitAssociationId;
    }

    public void setUnitAssociationId(Long unitAssociationId) {
        if (Objects.nonNull(unitAssociationIdProperty)) {
            unitAssociationIdProperty.set(unitAssociationId);
        } else {
            this.unitAssociationId = unitAssociationId;
        }
    }

    public Long getFkParentUnitId() {
        if (Objects.nonNull(fkParentUnitIdProperty)) {
            return fkParentUnitIdProperty.get();
        }
        return fkParentUnitId;
    }

    public void setFkParentUnitId(Long fkParentUnitId) {
        if (Objects.nonNull(fkParentUnitIdProperty)) {
            fkParentUnitIdProperty.set(fkParentUnitId);
        } else {
            this.fkParentUnitId = fkParentUnitId;
        }
    }

    public Long getFkKanbanId() {
        if (Objects.nonNull(fkKanbanIdProperty)) {
            return fkKanbanIdProperty.get();
        }
        return fkKanbanId;
    }

    public void setFkKanbanId(Long fkKanbanId) {
        if (Objects.nonNull(fkKanbanIdProperty)) {
            fkKanbanIdProperty.set(fkKanbanId);
        } else {
            this.fkKanbanId = fkKanbanId;
        }
    }

    public String getKanbanName() {
        if (Objects.nonNull(kanbanNameProperty)) {
            return kanbanNameProperty.get();
        }
        return kanbanName;
    }

    public void setKanbanName(String kanbanName) {
        if (Objects.nonNull(kanbanNameProperty)) {
            kanbanNameProperty.set(kanbanName);
        } else {
            this.kanbanName = kanbanName;
        }
    }

    public Long getFkUnitId() {
        if (Objects.nonNull(fkUnitIdProperty)) {
            return fkUnitIdProperty.get();
        }
        return fkUnitId;
    }

    public void setFkUnitId(Long fkUnitId) {
        if (Objects.nonNull(fkUnitIdProperty)) {
            fkUnitIdProperty.set(fkUnitId);
        } else {
            this.fkUnitId = fkUnitId;
        }
    }

    public String getUnitName() {
        if (Objects.nonNull(unitNameProperty)) {
            return unitNameProperty.get();
        }
        return unitName;
    }

    public void setUnitName(String unitName) {
        if (Objects.nonNull(unitNameProperty)) {
            unitNameProperty.set(kanbanName);
        } else {
            this.unitName = unitName;
        }
    }

    public Integer getUnitAssociateOrder() {
        if (Objects.nonNull(unitAssociateOrderProperty)) {
            return unitAssociateOrderProperty.get();
        }
        return unitAssociateOrder;
    }

    public void setUnitAssociateOrder(Integer unitAssociateOrder) {
        if (Objects.nonNull(unitAssociateOrderProperty)) {
            unitAssociateOrderProperty.set(unitAssociateOrder);
        } else {
            this.unitAssociateOrder = unitAssociateOrder;
        }
    }

    public void updateMember() {
        this.unitAssociationId = getUnitAssociationId();
        this.fkParentUnitId = getFkParentUnitId();
        this.fkKanbanId = getFkKanbanId();
        this.unitAssociateOrder = getUnitAssociateOrder();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.unitAssociationId ^ (this.unitAssociationId >>> 32));
        hash = 83 * hash + (int) (this.fkParentUnitId ^ (this.fkParentUnitId >>> 32));
        hash = 83 * hash + (int) (this.fkKanbanId ^ (this.fkKanbanId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConUnitAssociateInfoEntity other = (ConUnitAssociateInfoEntity) obj;
        if (!Objects.equals(this.getUnitAssociationId(), other.getUnitAssociationId())) {
            return false;
        }
        if (!Objects.equals(this.getFkParentUnitId(), other.getFkParentUnitId())) {
            return false;
        }
        return Objects.equals(this.getFkKanbanId(), other.getFkKanbanId());
    }

    @Override
    public String toString() {
        return "ConUnitAssociateInfoEntity{" + "unitAssociationId=" + getUnitAssociationId() + ", fkParentUnitId=" + getFkParentUnitId() + ", fkKanbanId=" + getFkKanbanId() + ", fkUnitId=" + getFkUnitId() + ", unitAssociateOrder=" + getUnitAssociateOrder() + '}';
    }
}
