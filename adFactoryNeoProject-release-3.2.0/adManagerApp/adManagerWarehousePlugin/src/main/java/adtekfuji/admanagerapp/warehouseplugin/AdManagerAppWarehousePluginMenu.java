/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin;

import adtekfuji.admanagerapp.warehouseplugin.component.DataSyncInfo;
import adtekfuji.admanagerapp.warehouseplugin.socket.WarehouseClientService;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内メニュー
 * 
 * @author ke.yokoi
 */
public class AdManagerAppWarehousePluginMenu implements AdManagerAppMainMenuInterface {

    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();
    private WarehouseClientService service = null;

    /**
     * プラグインを初期化する。
     */
    @Override
    public void pluginInitialize() {
    }

    /**
     * プラグインを破棄する。
     */
    @Override
    public void pluginDestructor() {
        DataSyncInfo.getInstance().stopTimer();
    }

    /**
     * サービスを開始する。
     */
    @Override
    public void pluginServiceStart() {
        //WarehouseClientService.createInstance();
        //service = WarehouseClientService.getInstance();
        //service.startService();
    }

    /**
     * サービスを停止する。
     */
    @Override
    public void pluginServiceStop() {
        //if (Objects.nonNull(service)) {
        //    service.stopService();
        //}
    }

    /**
     * 表示名を取得する。
     * 
     * @return 表示名
     */
    @Override
    public String getDisplayName() {
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        return LocaleUtils.getString("key.WarehouseGuide");
    }

    /**
     * 表示カテゴリーを取得する。
     * 
     * @return 表示カテゴリー
     */
    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.MANAGEMENT_FUNCTION;
    }

    /**
     * 表示順を取得する。
     * 
     * @return 表示順 
     */
    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.HIGHEST_PRIORITY.getOrder();
    }

    /**
     * メニュー選択処理を実行する。
     */
    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("WarehouseScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");
        sc.setComponent("SideNaviPane", "WarehouseMenuCompo");
    }

    /**
     * オプション名を取得する。
     * 
     * @return オプション名
     */
    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.Warehouse;
    }

    /**
     * 機能権限名一覧を取得する。
     * 
     * @return 機能権限名一覧
     */
    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        // return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.REFERENCE_KANBAN, (RoleAuthorityType) RoleAuthorityTypeEnum.MAKED_KANBAN);
        return null;
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
