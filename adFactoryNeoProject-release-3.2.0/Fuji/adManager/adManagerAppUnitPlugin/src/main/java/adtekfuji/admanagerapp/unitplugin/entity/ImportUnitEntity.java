/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.entity;

/**
 * ユニット インポート用データ
 *
 * @author s-maeda
 */
public class ImportUnitEntity {

    private String unit_hierarchy_name;
    private String unit_name;
    private String unit_template_name;
    private String start_datetime;
    private String delivery_datetime;

    /**
     * ユニット インポート用データ
     */
    public ImportUnitEntity() {

    }

    /**
     * ユニット インポート用データ
     *
     * @param kanban_hierarchy_name ユニット階層名
     * @param kanban_name ユニット名
     * @param unit_template_name ユニットテンプレート名
     * @param start_datetime 着手日
     * @param delivery_datetime 納品日
     */
    public ImportUnitEntity(String kanban_hierarchy_name, String kanban_name, String unit_template_name, String start_datetime, String delivery_datetime) {
        this.unit_hierarchy_name = kanban_hierarchy_name;
        this.unit_name = kanban_name;
        this.unit_template_name = unit_template_name;
        this.start_datetime = start_datetime;
        this.delivery_datetime = delivery_datetime;
    }

    /**
     * ユニット階層名を取得する。
     *
     * @return
     */
    public String getUnitHierarchyName() {
        return this.unit_hierarchy_name;
    }

    /**
     * ユニット階層名を設定する。
     *
     * @param value
     */
    public void setUnitHierarchyName(String value) {
        this.unit_hierarchy_name = value;
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
     * ユニットテンプレート名を取得する。
     *
     * @return
     */
    public String getUnitTemplateName() {
        return this.unit_template_name;
    }

    /**
     * ユニットテンプレート名を設定する。
     *
     * @param value
     */
    public void setUnitTemplateName(String value) {
        this.unit_template_name = value;
    }
    
    /**
     * 着手日を取得する。
     * 
     * @return 
     */
    public String getStartDatetime(){
        return this.start_datetime;
    }
    
    /**
     * 着手日を設定する。
     * 
     * @param value 
     */
    public void setStartDatetime(String value){
        this.start_datetime = value;
    }
    
    /**
     * 納品日を取得する。
     * 
     * @return 
     */
    public String getDeliveryDatetime(){
        return this.delivery_datetime;
    }
    
    /**
     * 納品日を設定する。
     * 
     * @param value 
     */
    public void setDeliveryDatetime(String value){
        this.delivery_datetime = value;
    }
    
    @Override
    public String toString() {
        return "ImportUnitEntity{" +
                "unit_hierarchy_name=" + this.unit_hierarchy_name +
                ", unit_name=" + this.unit_name +
                ", unit_template_name=" + this.unit_template_name +
                ", start_datetime=" + this.start_datetime +
                ", delivery_datetime=" + this.delivery_datetime +
                "}";
    }
}
