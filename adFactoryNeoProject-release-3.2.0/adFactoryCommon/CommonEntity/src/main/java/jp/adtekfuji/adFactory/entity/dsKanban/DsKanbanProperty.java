/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.dsKanban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * カンバン更新情報
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dsKanbanProperty")
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsKanbanProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private Integer category;

    @XmlElement()
    private Long kanbanId;

    @XmlElement()
    private String propertyName;

    @XmlElement()
    private String propertyValue;

    /**
     * コンストラクタ
     */
    public DsKanbanProperty() {
    }

    /**
     * コンストラクタ
     * 
     * @param category
     * @param kanbanId
     * @param propertyName
     * @param propertyValue 
     */
    public DsKanbanProperty(Integer category, Long kanbanId, String propertyName, String propertyValue) {
        this.category = category;
        this.kanbanId = kanbanId;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Long getKanbanId() {
        return kanbanId;
    }

    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
 
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("MstDsItem{")
                .append(", category=").append(this.category)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", propertyName=").append(this.propertyName)
                .append(", propertyValue=").append(this.propertyValue)
                .append("}")
                .toString();
    }
    
}
