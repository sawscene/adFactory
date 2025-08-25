/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels;

import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanEditConfig;
import adtekfuji.admanagerapp.kanbaneditpluginels.common.KanbanEditPermanenceData;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
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
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
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

    @Override
    public void onSelectMenuAction() {
        // 機能一覧から開いたときツリーを初期化する
        KanbanEditPermanenceData.getInstance().setKanbanHierarchyRootItem(null);
        KanbanEditPermanenceData.getInstance().setSelectedWorkHierarchy(null);

        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("KanbanEditSceneELS")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");

//        // カンバン編集メニューを使用するか
//        if (KanbanEditConfig.getKanbanEditMenuEnabled()) {
//            sc.setComponent("SideNaviPane", "KanbanEditMenuCompoELS");// カンバン編集メニュー
//        } else {
//            sc.setComponent("ContentNaviPane", "KanbanListCompoELS_base");// カンバン編集画面 (メニューは使用しない)
//        }

        // ELS版は、adManeApp.properties の設定に関係なく KanbanCreateCompoELS を呼び出す。
        sc.setComponent("ContentNaviPane", "KanbanListCompoELS");// カンバン編集画面 (ELS)
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.NotRequireLicense;
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
