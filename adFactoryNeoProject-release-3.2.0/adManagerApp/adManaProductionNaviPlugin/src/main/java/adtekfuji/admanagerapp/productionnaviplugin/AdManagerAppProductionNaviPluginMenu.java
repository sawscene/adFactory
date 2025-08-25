/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
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
 * 生産管理ナビのメニュー
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class AdManagerAppProductionNaviPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();

    @Override
    public String getDisplayName() {
        return LocaleUtils.getString("key.ProductionNavi.Title");
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
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("PuroductionNaviScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");

        // 生産管理ナビのメニュー
        sc.setComponent("SideNaviPane", "ProductionNaviMenuCompo");
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.ProductionNavi;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.REFERENCE_RESOOURCE, (RoleAuthorityType) RoleAuthorityTypeEnum.EDITED_RESOOURCE);
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
