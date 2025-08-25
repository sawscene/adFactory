/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.common;

import java.util.Objects;
import javafx.scene.control.TreeItem;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;

/**
 *
 * @author s-maeda
 */
public class UnitEditPermanenceData {

    private static UnitEditPermanenceData instance = null;

    private TreeItem<UnitHierarchyInfoEntity> unitHierarchyRootItem;
    private TreeItem<UnitHierarchyInfoEntity> selectedUnitHierarchy;
    private Long loginUserId;

    private UnitEditPermanenceData() {
    }

    public static UnitEditPermanenceData getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UnitEditPermanenceData();
        }
        return instance;
    }

    public void updateUnitHierarchy() {
    }

    public TreeItem<UnitHierarchyInfoEntity> getUnitHierarchyRootItem() {
        return unitHierarchyRootItem;
    }

    public void setUnitHierarchyRootItem(TreeItem<UnitHierarchyInfoEntity> unitRootItem) {
        this.unitHierarchyRootItem = unitRootItem;
    }

    public TreeItem<UnitHierarchyInfoEntity> getSelectedUnitHierarchy() {
        return selectedUnitHierarchy;
    }

    public void setSelectedUnitHierarchy(TreeItem<UnitHierarchyInfoEntity> selectedUnitHierarchy) {
        this.selectedUnitHierarchy = selectedUnitHierarchy;
    }

    public Long getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(Long loginUserId) {
        this.loginUserId = loginUserId;
    }
}
