/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.dialog;

import jp.adtekfuji.adFactory.entity.kanban.KanbanHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplatePropertyInfoEntity;

/**
 * プロパティ用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
public class UnitTemplateSettingDialogEntity {

    private KanbanHierarchyInfoEntity outputKanbanHierarchy;
    private PropertySettingDialogEntity<UnitTemplatePropertyInfoEntity> propertyEntity;

    public UnitTemplateSettingDialogEntity(KanbanHierarchyInfoEntity outputKanbanHierarchy, PropertySettingDialogEntity<UnitTemplatePropertyInfoEntity> propertyEntity) {
        this.outputKanbanHierarchy = outputKanbanHierarchy;
        this.propertyEntity = propertyEntity;
    }

    public KanbanHierarchyInfoEntity getOutputKanbanHierarchy() {
        return outputKanbanHierarchy;
    }

    public PropertySettingDialogEntity<UnitTemplatePropertyInfoEntity> getPropertyEntity() {
        return propertyEntity;
    }

    public void setOutputKanbanHierarchy(KanbanHierarchyInfoEntity outputKanbanHierarchy) {
        this.outputKanbanHierarchy = outputKanbanHierarchy;
    }

    public void setPropertyEntity(PropertySettingDialogEntity<UnitTemplatePropertyInfoEntity> propertyEntity) {
        this.propertyEntity = propertyEntity;
    }
}
