/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin;

import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditConfig;
import adtekfuji.admanagerapp.kanbaneditplugin.component.KanbanEditMenuCompoFxController;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;

/**
 *
 * @author ke.yokoi
 */
public class AdManagerAppKanbanPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();

    @Override
    public String getDisplayName() {
        return LocaleUtils.getString("key.EditKanbanTitle");
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.MANAGEMENT_FUNCTION;
    }

    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.MIDDLE_PRIORITY.getOrder() + 1;
    }
    
//    @Override
//    public MenuType getMenuType() {
//        return MenuType.TREE;
//    }
    
    @Override
    public List<String> getSubMenuDisplayName() {       
        List<String> list = new ArrayList<>();
        boolean isKanbanEditor = ClientServiceProperty.isLicensed(LicenseOptionType.KanbanEditor.getName());
        boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());     
        if (isKanbanEditor) {
            list.add(LocaleUtils.getString("key.SubMenuTitle.Kanban"));
        }
        if (KanbanEditConfig.isUseKanbanImport()) {
            list.add(LocaleUtils.getString("key.SubMenuTitle.PlanLoading"));
        }
        if (isLiteOption) {
            list.add(LocaleUtils.getString("key.SubMenuTitle.LiteKanbanTitle"));
        }
        return list;
//        return List.of(LocaleUtils.getString("key.SubMenuTitle.Kanban"),
//                LocaleUtils.getString("key.SubMenuTitle.LiteKanbanTitle"),
//                LocaleUtils.getString("key.SubMenuTitle.PlanLoading")
//            );
    }
    
    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("KanbanEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");

        boolean isKanbanEditMenuEnabled = KanbanEditConfig.isKanbanEditMenuEnabled();
        boolean isKanbanEditor = ClientServiceProperty.isLicensed(LicenseOptionType.KanbanEditor.getName());
        boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());

        // カンバン編集メニューを使用するか
        if ((isKanbanEditMenuEnabled && isKanbanEditor) || (isKanbanEditor && isLiteOption)) {
            sc.setComponent("SideNaviPane", "KanbanEditMenuCompo");// カンバン編集メニュー
        } else {
            // メニューは使用しない
            if (isKanbanEditor) {
                sc.setComponent("ContentNaviPane", "KanbanListCompo");// カンバン編集画面
            } else if (isLiteOption) {
                sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");// Liteカンバン編集画面
            }
        }
    }

    @Override
    public void onSelectSubMenuAction(String displayName) {
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("KanbanEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");

        String editName = LocaleUtils.getString("key.SubMenuTitle.Kanban");
        String importName = LocaleUtils.getString("key.SubMenuTitle.PlanLoading");
        String liteName = LocaleUtils.getString("key.SubMenuTitle.LiteKanbanTitle");

        if (editName.equals(displayName)) {
            sc.setComponent("ContentNaviPane", "KanbanListCompo");
        } else if (importName.equals(displayName)) {
            sc.setComponent("ContentNaviPane", "KanbanImportCompo");
        } else if (liteName.equals(displayName)) {
            sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");
        }
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.KanbanEditor;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.REFERENCE_KANBAN, (RoleAuthorityType) RoleAuthorityTypeEnum.MAKED_KANBAN);
    }

    /**
     * プロパティを設定する
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        if (Objects.nonNull(properties)) {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
                String propertyName = (String) e.nextElement();
                String propertyValue = properties.getProperty(propertyName);
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
    }
}
