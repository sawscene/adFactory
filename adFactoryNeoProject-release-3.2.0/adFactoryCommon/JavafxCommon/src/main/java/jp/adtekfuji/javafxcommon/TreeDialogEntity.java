/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon;

import javafx.scene.control.TreeItem;

/**
 *
 * @author ta.ito
 */
public class TreeDialogEntity {

    private TreeItem<?> treeRootItem;
    private String elementName;
    private TreeItem<?> treeSelectedItem;
    private Object listSelectedItem;
    private Boolean isUseHierarchy = false;
    private TreeItem<?> sourceItem;

    public TreeDialogEntity(TreeItem<?> treeRootItem, String elementName) {
        this.treeRootItem = treeRootItem;
        this.elementName = elementName;
    }

    public TreeDialogEntity(TreeItem<?> treeRootItem, String elementName, TreeItem<?> sourceItem) {
        this.treeRootItem = treeRootItem;
        this.elementName = elementName;
        this.sourceItem = sourceItem;
    }

    public TreeItem<?> getTreeRootItem() {
        return treeRootItem;
    }

    public void setTreeRootItem(TreeItem<?> treeRootItem) {
        this.treeRootItem = treeRootItem;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public TreeItem<?> getTreeSelectedItem() {
        return treeSelectedItem;
    }

    public void setTreeSelectedItem(TreeItem<?> treeSelectedItem) {
        this.treeSelectedItem = treeSelectedItem;
    }

    public Object getListSelectedItem() {
        return listSelectedItem;
    }

    public void setListSelectedItem(Object listSelectedItem) {
        this.listSelectedItem = listSelectedItem;
    }

    public Boolean getIsUseHierarchy() {
        return isUseHierarchy;
    }

    public void setIsUseHierarchy(Boolean isHierarchy) {
        this.isUseHierarchy = isHierarchy;
    }

    public TreeItem<?> getSourceItem() {
        return sourceItem;
    }

}
