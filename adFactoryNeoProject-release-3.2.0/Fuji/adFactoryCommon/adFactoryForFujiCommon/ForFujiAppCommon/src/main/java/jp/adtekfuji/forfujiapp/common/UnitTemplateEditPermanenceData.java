/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.common;

import java.util.Objects;
import javafx.scene.control.TreeItem;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateHierarchyInfoEntity;

/**
 *
 * @author s-maeda
 */
public class UnitTemplateEditPermanenceData {

    private static UnitTemplateEditPermanenceData instance = null;

    private TreeItem<UnitTemplateHierarchyInfoEntity> unitTemplateHierarchyRootItem;
    private TreeItem<UnitTemplateHierarchyInfoEntity> selectedUnitTemplateHierarchy;
    private Long loginUserId;

    private UnitTemplateEditPermanenceData() {
    }

    public static UnitTemplateEditPermanenceData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UnitTemplateEditPermanenceData();
        }
        return instance;
    }

    public void updateUnitTemplateHierarchy() {
    }

    public TreeItem<UnitTemplateHierarchyInfoEntity> getUnitTemplateHierarchyRootItem() {
        return unitTemplateHierarchyRootItem;
    }

    public void setUnitTemplateHierarchyRootItem(TreeItem<UnitTemplateHierarchyInfoEntity> unitTemplateRootItem) {
        this.unitTemplateHierarchyRootItem = unitTemplateRootItem;
    }

    public TreeItem<UnitTemplateHierarchyInfoEntity> getSelectedUnitTemplateHierarchy() {
        return selectedUnitTemplateHierarchy;
    }

    public void setSelectedUnitTemplateHierarchy(TreeItem<UnitTemplateHierarchyInfoEntity> selectedUnitTemplateHierarchy) {
        this.selectedUnitTemplateHierarchy = selectedUnitTemplateHierarchy;
    }

    public Long getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(Long loginUserId) {
        this.loginUserId = loginUserId;
    }
}
