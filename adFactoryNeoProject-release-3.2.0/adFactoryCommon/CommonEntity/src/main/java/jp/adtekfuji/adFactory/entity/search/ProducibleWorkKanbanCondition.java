package jp.adtekfuji.adFactory.entity.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ProducibleWorkKanbanCondition")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ProducibleWorkKanbanCondition implements Serializable {

    public ProducibleWorkKanbanCondition(){}

    @XmlElementWrapper(name = "equipmentIds")
    @XmlElement(name = "equipmentId")
    private List<Long> equipmentCollection = null;// 設備ID一覧

    @XmlElementWrapper(name = "organizationIds")
    @XmlElement(name = "organizationId")
    private List<Long> organizationCollection = null;// 組織ID一覧

    @XmlElementWrapper(name = "addInfoSearchConditions")
    @XmlElement(name = "addInfoSearchCondition")
    private List<AddInfoSearchCondition> addInfoSearchConditions; // 追加項目

    public List<Long> getEquipmentCollection() {
        return equipmentCollection;
    }

    public void setEquipmentCollection(List<Long> equipmentCollection) {
        this.equipmentCollection = equipmentCollection;
    }

    public List<Long> getOrganizationCollection() {
        return organizationCollection;
    }

    public void setOrganizationCollection(List<Long> organizationCollection) {
        this.organizationCollection = organizationCollection;
    }

    public List<AddInfoSearchCondition> getAddInfoSearchConditions() {
        return addInfoSearchConditions;
    }

    public void setAddInfoSearchConditions(List<AddInfoSearchCondition> addInfoSearchConditions) {
        this.addInfoSearchConditions = addInfoSearchConditions;
    }
}
