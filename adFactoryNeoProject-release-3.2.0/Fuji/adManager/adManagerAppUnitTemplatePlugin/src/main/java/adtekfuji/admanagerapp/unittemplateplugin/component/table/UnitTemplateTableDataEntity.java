/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component.table;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.util.Objects;
import java.util.ResourceBundle;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;

/**
 * ユニットテンプレートテーブル用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.27.Tha
 */
public class UnitTemplateTableDataEntity {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private UnitTemplateHierarchyInfoEntity hierarchyInfoEntity;
    private UnitTemplateInfoEntity infoEntity;
    private String name;
    private String updatePersonName;
    private String updateDatetime;
    private Boolean isHierarchy;

    public UnitTemplateTableDataEntity(UnitTemplateHierarchyInfoEntity hierarchyInfoEntity) {
        this.hierarchyInfoEntity = hierarchyInfoEntity;
        this.infoEntity = null;
        this.name = hierarchyInfoEntity.getHierarchyName();
        this.updatePersonName = null;
        this.updateDatetime = null;
        this.isHierarchy = Boolean.TRUE;
    }

    public UnitTemplateTableDataEntity(UnitTemplateInfoEntity infoEntity) {
        this.hierarchyInfoEntity = null;
        this.infoEntity = infoEntity;
        this.name = infoEntity.getUnitTemplateName();
        this.updateDatetime = Objects.isNull(infoEntity.getUpdateDatetime()) ? "" : StringTime.convertDateToString(infoEntity.getUpdateDatetime(), LocaleUtils.getString("key.DateTimeFormat"));;
        this.isHierarchy = Boolean.FALSE;
    }

    public static ResourceBundle getRb() {
        return rb;
    }

    public UnitTemplateHierarchyInfoEntity getUnitTemplateHierarchyInfoEntity() {
        return hierarchyInfoEntity;
    }

    public UnitTemplateInfoEntity getUnitTemplateInfoEntity() {
        return infoEntity;
    }

    public String getName() {
        return name;
    }

    public String getUpdatePersonName() {
        return updatePersonName;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public Boolean getIsHierarchy() {
        return isHierarchy;
    }

    public void setUnitTemplateHierarchyInfoEntity(UnitTemplateHierarchyInfoEntity hierarchyInfoEntity) {
        this.hierarchyInfoEntity = hierarchyInfoEntity;
    }

    public void setUnitTemplateInfoEntity(UnitTemplateInfoEntity infoEntity) {
        this.infoEntity = infoEntity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUpdatePersonName(String updatePersonName) {
        this.updatePersonName = updatePersonName;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public void setIsHierarchy(Boolean isHierarchy) {
        this.isHierarchy = isHierarchy;
    }
    
    

}
