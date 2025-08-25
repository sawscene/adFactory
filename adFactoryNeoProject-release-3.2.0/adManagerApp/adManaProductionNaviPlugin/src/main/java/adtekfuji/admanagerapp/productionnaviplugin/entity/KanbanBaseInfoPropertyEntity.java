/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity;

import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;

/**
 *　カンバン基本情報のプロパティ
 *
 * @author nar-nakamura
 */
public class KanbanBaseInfoPropertyEntity {

    private String kanbanPropertyName;
    private CustomPropertyTypeEnum kanbanPropertyType;
    private String kanbanPropertyValue;
    private Integer kanbanPropertyOrder;

    /**
     * カンバン基本情報のプロパティ
     *
     */
    public KanbanBaseInfoPropertyEntity() {

    }

    /**
     * カンバン基本情報のプロパティ
     *
     * @param propertyName
     * @param propertyType
     * @param propertyValue
     * @param propertyOrder 
     */
    public KanbanBaseInfoPropertyEntity(String propertyName, CustomPropertyTypeEnum propertyType, String propertyValue, Integer propertyOrder) {
        this.kanbanPropertyName = propertyName;
        this.kanbanPropertyType = propertyType;
        this.kanbanPropertyValue = propertyValue;
        this.kanbanPropertyOrder = propertyOrder;
    }

    /**
     * プロパティ名を取得する。
     *
     * @return
     */
    public String getKanbanPropertyName() {
        return this.kanbanPropertyName;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyName(String value) {
        this.kanbanPropertyName = value;
    }

    /**
     * プロパティ型を取得する。
     *
     * @return
     */
    public CustomPropertyTypeEnum getKanbanPropertyType() {
        return this.kanbanPropertyType;
    }

    /**
     * プロパティ型を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyType(CustomPropertyTypeEnum value) {
        this.kanbanPropertyType = value;
    }

    /**
     * プロパティ値を取得する。
     *
     * @return
     */
    public String getKanbanPropertyValue() {
        return this.kanbanPropertyValue;
    }

    /**
     * プロパティ値を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyValue(String value) {
        this.kanbanPropertyValue = value;
    }

    /**
     * オーダー順を取得する。
     *
     * @return
     */
    public Integer getKanbanPropertyOrder() {
        return this.kanbanPropertyOrder;
    }

    /**
     * オーダー順を設定する。
     *
     * @param value
     */
    public void setKanbanPropertyOrder(Integer value) {
        this.kanbanPropertyOrder = value;
    }
    
    @Override
    public String toString() {
        return "KanbanBaseInfoPropertyEntity{" +
                ", kanbanPropertyName=" + this.kanbanPropertyName +
                ", kanbanPropertyType=" + this.kanbanPropertyType +
                ", kanbanPropertyValue=" + this.kanbanPropertyValue +
                ", kanbanPropertyOrder=" + this.kanbanPropertyOrder +
                "}";
    }
}
