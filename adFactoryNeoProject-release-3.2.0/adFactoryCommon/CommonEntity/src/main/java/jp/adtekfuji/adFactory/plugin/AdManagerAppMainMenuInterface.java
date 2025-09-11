/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin;

import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.MenuTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;
import jp.adtekfuji.javafxcommon.WorkflowEditPermanenceData;

/**
 *
 * @author ke.yokoi
 */
public class AdManagerAppWorkFlowEditPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();
    
    final String menuType = AdProperty.getProperties().getProperty("menuType");

    @Override
    public String getDisplayName() {
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        return LocaleUtils.getString("key.EditWorkflowTitle");
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.MANAGEMENT_FUNCTION;
    }

    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.MIDDLE_PRIORITY.getOrder();
    }
    
    @Override
    public void onSelectMenuAction(String subMenuDisplayName) {
        // 機能一覧から開いたときツリーを初期化する
        WorkflowEditPermanenceData.getInstance().getWorkHierarchyRootItem().getChildren().clear();
        WorkflowEditPermanenceData.getInstance().getWorkflowHierarchyRootItem().getChildren().clear();
        WorkflowEditPermanenceData.getInstance().setSelectedWorkHierarchy(null);
        WorkflowEditPermanenceData.getInstance().setSelectedWorkflowHierarchy(null);

        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("WorkflowEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");

        final boolean isWorkflowEditor = ClientServiceProperty.isLicensed(LicenseOptionType.WorkflowEditor.getName());
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        
        Map<String, String> componentMap = new HashMap<>();
        componentMap.put(LocaleUtils.getString("key.SubMenuTitle.Process"), "WorkEditCompo");
        componentMap.put(LocaleUtils.getString("key.SubMenuTitle.OrderProcesses"), "WorkflowEditCompo");
        componentMap.put(LocaleUtils.getString("key.SubMenuTitle.LiteOrderProcess"), "WorkflowEditLite");
        
        String component = componentMap.get(subMenuDisplayName);
        if (MenuTypeEnum.TREE.getValue().equals(menuType) || (Objects.nonNull(component))) {
                sc.setComponent("ContentNaviPane", component);
        } else {
            if (isWorkflowEditor) {
                sc.setComponent("SideNaviPane", "WorkflowNaviCompo");
            } else if (isLiteOption && !isWorkflowEditor) {
                sc.setComponent("ContentNaviPane", "WorkflowEditLite");
            }
        }
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.WorkflowEditor;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.REFERENCE_WORKFLOW, (RoleAuthorityType) RoleAuthorityTypeEnum.EDITED_WORKFLOW);
    }

    /**
     * プロパティを設定する
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        if (Objects.nonNull(properties))  {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
                String propertyName = (String) e.nextElement();
                String propertyValue = properties.getProperty(propertyName);
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
    }
    
    @Override
    public Map<MainMenuCategory, List<MenuNode>> getSubMenuDisplayNames() {
        Map<MainMenuCategory, List<MenuNode>> mapSubMenu = new HashMap<>();
        
        
        final boolean isWorkflowEditor = ClientServiceProperty.isLicensed(LicenseOptionType.WorkflowEditor.getName());
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        
        if(isWorkflowEditor) {
            List<MenuNode> workFlowSubMenu = List.of(
                new MenuNode(LocaleUtils.getString("key.SubMenuTitle.Process"), null),
                new MenuNode(LocaleUtils.getString("key.SubMenuTitle.OrderProcesses"), null)
            );
            mapSubMenu.put(MainMenuCategory.OPERATION, workFlowSubMenu); 
            mapSubMenu.put(MainMenuCategory.REPORTER, workFlowSubMenu); 
        }
        
        if(!isLiteOption) {
            mapSubMenu.put(MainMenuCategory.LITE, 
                    List.of(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.LiteOrderProcess"), null)
                ));
        }
        
        return mapSubMenu;
    }
    
    @Override
    public void onSelectSubMenuAction(String subMenuDisplayName) {
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("WorkflowEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");
        sc.setComponent("SideNaviPane", "WorkflowNaviCompo");
        
        Map<String, String> componentMap = new HashMap<>();
        componentMap.put(LocaleUtils.getString("key.SubMenuTitle.Process"), "WorkEditCompo");
        componentMap.put(LocaleUtils.getString("key.SubMenuTitle.OrderProcesses"), "WorkflowEditCompo");
        componentMap.put(LocaleUtils.getString("key.SubMenuTitle.LiteOrderProcess"), "WorkflowEditLite");
        
        String component = componentMap.get(subMenuDisplayName);
        if (Objects.nonNull(component)) {
            sc.setComponent("ContentNaviPane", component);
             
        }
    }
}
