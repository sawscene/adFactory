/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.dsitemeditplugin;

import adtekfuji.fxscene.SceneContiner;
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
 * 品番編集プラグイン
 * 
 * @author s-heya
 */
public class AdManagerAppDsItemEditPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();

    @Override
    public String getDisplayName() {
        //return LocaleUtils.getString("品番編集");
        return "品番編集";
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.MANAGEMENT_FUNCTION;
    }

    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.LOW_PRIORITY.getOrder() + 1;
    }

    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("DsItemEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");
        sc.visibleArea("WorkflowEditPane", false); // 工程・工程順編集画面
        sc.setComponent("DsItemEditPane", "DsItemListCompo");
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.NotRequireLicense;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList(
                (RoleAuthorityType) RoleAuthorityTypeEnum.REFERENCE_RESOOURCE, 
                (RoleAuthorityType) RoleAuthorityTypeEnum.EDITED_RESOOURCE);
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
}
