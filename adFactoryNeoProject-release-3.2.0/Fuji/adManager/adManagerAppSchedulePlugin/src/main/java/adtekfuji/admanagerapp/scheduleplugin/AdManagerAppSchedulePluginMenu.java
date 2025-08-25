/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;
import jp.adtekfuji.forfujiapp.common.ClientPropertyConstants;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author ke.yokoi
 */
public class AdManagerAppSchedulePluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();

    @Override
    public String getDisplayName() {
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        return LocaleUtils.getString("key.ScheduleTitle");
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.MANAGEMENT_FUNCTION;
    }

    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("Scene");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");
        sc.setComponent("SideNaviPane", "UnitScheduleSubMeneCompo");
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.NotRequireLicense;
    }

    @Override
    public void pluginInitialize() {
        try {
            AdManagerAppMainMenuInterface.super.pluginInitialize(); //To change body of generated methods, choose Tools | Templates.
            AdProperty.load(ClientPropertyConstants.ADPROPERTY_FOR_FUJI_TAG,
                    ClientPropertyConstants.ADFACTORY_FOR_FUJI_CLIENT_PROPERTY_FILE);
        } catch (IOException ex) {
            LogManager.getLogger().fatal(ex, ex);
        }
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList(RoleAuthorityTypeEnum.values());
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
