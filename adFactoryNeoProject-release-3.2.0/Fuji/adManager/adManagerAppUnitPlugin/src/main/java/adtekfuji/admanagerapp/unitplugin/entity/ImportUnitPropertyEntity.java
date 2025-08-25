/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.entity;

/**
 * ユニットプロパティ インポート用データ
 * @author s-maeda
 */
public class ImportUnitPropertyEntity {

    private String unit_name;
    private String unit_property_name;
    private String unit_property_type;
    private String unit_property_value;

    /**
     * ユニットプロパティ インポート用データ
     *
     */
    public ImportUnitPropertyEntity() {

    }

    /**
     * ユニットプロパティ インポート用データ
     *
     * @param unit_name ユニット名
     * @param unit_property_name プロパティ名
     * @param unit_property_type プロパティ型
     * @param unit_property_value プロパティ値
     */
    public ImportUnitPropertyEntity(String unit_name, String unit_property_name, String unit_property_type, String unit_property_value) {
        this.unit_name = unit_name;
        this.unit_property_name = unit_property_name;
        this.unit_property_type = unit_property_type;
        this.unit_property_value = unit_property_value;
    }

    /**
     * ユニット名を取得する。
     *
     * @return
     */
    public String getUnitName() {
        return this.unit_name;
    }

    /**
     * ユニット名を設定する。
     *
     * @param value
     */
    public void setUnitName(String value) {
        this.unit_name = value;
    }

    /**
     * プロパティ名を取得する。
     *
     * @return
     */
    public String getUnitPropertyName() {
        return this.unit_property_name;
    }

    /**
     * プロパティ名を設定する。
     *
     * @param value
     */
    public void setUnitPropertyName(String value) {
        this.unit_property_name = value;
    }

    /**
     * プロパティ型を取得する。
     *
     * @return
     */
    public String getUnitPropertyType() {
        return this.unit_property_type;
    }

    /**
     * プロパティ型を設定する。
     *
     * @param value
     */
    public void setUnitPropertyType(String value) {
        this.unit_property_type = value;
    }

    /**
     * プロパティ値を取得する。
     *
     * @return
     */
    public String getUnitPropertyValue() {
        return this.unit_property_value;
    }

    /**
     * プロパティ値を設定する。
     *
     * @param value
     */
    public void setUnitPropertyValue(String value) {
        this.unit_property_value = value;
    }
    
    @Override
    public String toString() {
        return "ImportUnitPropertyEntity{" +
                "unit_name=" + this.unit_name +
                ", unit_property_name=" + this.unit_property_name +
                ", unit_property_type=" + this.unit_property_type +
                ", unit_property_value=" + this.unit_property_value +
                "}";
    }
}
