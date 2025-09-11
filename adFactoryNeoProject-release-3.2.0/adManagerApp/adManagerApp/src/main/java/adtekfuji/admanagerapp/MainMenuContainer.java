/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp;

import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.MenuTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface.MainMenuCategory;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface.MenuNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class MainMenuContainer {

    private static MainMenuContainer instance = null;
    private static final Logger logger = LogManager.getLogger();
    private final List<AdManagerAppMainMenuInterface> plugins = new ArrayList<>();

    private final SystemResourceFacade systemResourceFacade = new SystemResourceFacade();
    
    /**
     * プラグインからオプション種別を取得します。
     * {@link AdManagerAppMainMenuInterface#getOptionType()} を使用してオプション種別を取得し、
     * 取得できない場合はデフォルト値として {@link LicenseOptionType#Unknown} を返します。
     *
     * @param plugin オプション種別を取得する対象のプラグイン
     * @return 取得したオプション種別。取得できない場合は {@link LicenseOptionType#Unknown}
     */
    private LicenseOptionType getOptionalType(AdManagerAppMainMenuInterface plugin) {
        try {
            return plugin.getOptionType();
        } catch (NoSuchFieldError ex) {
            logger.error("plugin:{} optionType is not found.", plugin.getClass().getName());
            return LicenseOptionType.Unknown;
        }
    }

    private MainMenuContainer() {
        try {
            // オプションライセンスを取得する。
            List<SystemOptionEntity> optionLicenses = systemResourceFacade.getLicenseOptions();

            // プラグインを読み込む
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            plugins.clear();
            plugins.addAll(PluginLoader.load(AdManagerAppMainMenuInterface.class));
            plugins.sort(new PluginComparator());
            logger.info("plugin:{}", plugins);

            for (AdManagerAppMainMenuInterface plugin : plugins) {
                plugin.pluginInitialize();

                // プラグインのライセンスが不要または有効な場合、プラグインのサービスを開始する。
                final LicenseOptionType optionalType = getOptionalType(plugin);
                boolean isLisenceEnabled = false;
                if (LicenseOptionType.NotRequireLicense.equals(optionalType)) {
                    isLisenceEnabled = true;
                } else {
                    Optional<SystemOptionEntity> opt = optionLicenses.stream().filter(p -> p.getOptionName().equals(optionalType.getName())).findFirst();
                    if (opt.isPresent()) {
                        isLisenceEnabled = opt.get().getEnable();
                    }
                }

                if (isLisenceEnabled) {
                    plugin.pluginServiceStart();
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public static void createInstance() {
        if (Objects.isNull(instance)) {
            instance = new MainMenuContainer();
        }
    }

    public static MainMenuContainer getInstance() {
        if (Objects.isNull(instance)) {
            logger.fatal("not create instance");
        }
        return instance;
    }

    public void pluginDestructor() {
        for (AdManagerAppMainMenuInterface plugin : plugins) {
            plugin.pluginServiceStop();// プラグインのサービスを停止する。
            plugin.pluginDestructor();
        }
    }

    /**
     * メニューに拡張機能(プラグイン)を表示する
     *
     * @param manupane
     * @param optionLicenses
     */
    public void makeMenuButton(final Pane manupane, List<SystemOptionEntity> optionLicenses) {
        try {
            logger.info("makeMenuButton start.");
            
            MenuTypeEnum menuType = MenuTypeEnum.from(AdProperty.getProperties()
                           .getProperty("menuType", MenuTypeEnum.DEFAULT.getValue()));

            switch (menuType) {
                case TREE:
                    makeTreeMenuButton(manupane, optionLicenses);
                    break;
                case DEFAULT:
                default:
                    makeDefaultMenuButton(manupane, optionLicenses);
                    break;
            }
        }
        finally {
            logger.info("makeMenuButton end.");
        }
    }

    private void makeDefaultMenuButton(final Pane manupane, List<SystemOptionEntity> optionLicenses) {
        
        Properties properties = new Properties();

        for (SystemOptionEntity optionLicence : optionLicenses) {
            logger.info("License: " + optionLicence.getOptionName() + " = " + optionLicence.getEnable());
            properties.setProperty(optionLicence.getOptionName(), optionLicence.getEnable().toString());
        }

        Optional<SystemOptionEntity> kanbanEditor = optionLicenses.stream().filter((o) -> LicenseOptionType.KanbanEditor.getName().equals(o.getOptionName())).findFirst();
        Optional<SystemOptionEntity> workflowEditor = optionLicenses.stream().filter((o) -> LicenseOptionType.WorkflowEditor.getName().equals(o.getOptionName())).findFirst();
        Optional<SystemOptionEntity> liteOption = optionLicenses.stream().filter((o) -> LicenseOptionType.LiteOption.getName().equals(o.getOptionName())).findFirst();
        Optional<SystemOptionEntity> reporterOption = optionLicenses.stream().filter((o) -> LicenseOptionType.ReporterOption.getName().equals(o.getOptionName())).findFirst();

        final boolean isKanbanEditor = kanbanEditor.isPresent() ? kanbanEditor.get().getEnable() : false;
        final boolean isWorkflowEditor = workflowEditor.isPresent() ? workflowEditor.get().getEnable() : false;
        final boolean isLiteOption = liteOption.isPresent() ? liteOption.get().getEnable() : false;
        final boolean isReporterOption = reporterOption.isPresent() ? reporterOption.get().getEnable() : false;

        // 作業日報
        final String dailyReportDisplayName = LocaleUtils.getString("key.WorkReportTitle");
        // 進捗モニタ
        final String progressMonitorDisplayName = LocaleUtils.getString("key.AndonSetting");
        // 作業分析メニューの表示名
        final String workAnalysisDisplayName = LocaleUtils.getString("key.WorkAnalysis");
        // 生産管理
        final String manufacturingManagementDisplayName = LocaleUtils.getString("key.ProductionNavi.Title");

        manupane.getChildren().clear();
        plugins.stream().map((plugin) -> {
            // 機能権限を確認する
            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            boolean isAllow = true;

            List<RoleAuthorityType> types = plugin.getRoleAuthorityType();
            if (Objects.nonNull(types)) {
                isAllow = false;
                for (RoleAuthorityType auth : types) {
                    RoleAuthorityTypeEnum.add(auth);
                    if (loginUser.checkRoleAuthority(auth)) {
                        isAllow = true;
                    }
                }
            }

            // オプションライセンスを確認する
            LicenseOptionType pluginLicenseType = getOptionalType(plugin);
            if (!LicenseOptionType.NotRequireLicense.equals(pluginLicenseType)) {
                boolean isLicensed = false;
                switch (pluginLicenseType) {
                    case KanbanEditor:
                        isLicensed = (isKanbanEditor || isLiteOption);
                        break;
                    case WorkflowEditor:
                        isLicensed = (isWorkflowEditor || isLiteOption);
                        break;
                    default:
                        if (workAnalysisDisplayName.equals(plugin.getDisplayName()) && isLiteOption && !isKanbanEditor) {
                            // Lite単体構成の場合、作業分析メニューは非表示
                            isLicensed = false;
                        } else {
                            String optionName = pluginLicenseType.getName();
                            Optional<SystemOptionEntity> find = optionLicenses.stream().filter((o) -> optionName.equals(o.getOptionName())).findFirst();
                            if (find.isPresent()) {
                                isLicensed = find.get().getEnable();
                            }
                        }
                        break;
                }

                if (isAllow && !isLicensed) {
                    isAllow = false;
                }
            }

            // レポータのライセンスのみの場合
            if (isReporterOption && !isKanbanEditor
                    && (workAnalysisDisplayName.equals(plugin.getDisplayName()) // 作業分析
                    || dailyReportDisplayName.equals(plugin.getDisplayName()) // 作業日報
                    || progressMonitorDisplayName.equals(plugin.getDisplayName()) // 進捗モニタ設定
                    || manufacturingManagementDisplayName.equals(plugin.getDisplayName()) // 生産管理
            )) {
                isAllow = false;
            }

            if (isAllow) {
                // プラグインの使用を許可
                plugin.setProperties(properties);

                    Button button = new Button(plugin.getDisplayName());
                    button.getStyleClass().add("MenuButton");
                    button.setOnAction((ActionEvent event) -> {
                        plugin.onSelectMenuAction(null);
                    });

                    return button;
            } else {
                logger.warn("plugin:{} is not allow.", plugin.getClass().getName());
            }
            return null;
        }).forEach((button) -> {
            if (Objects.nonNull(button)) {
                manupane.getChildren().add(button);
            }
        });
    }
    
    private String getCategoryDisplayName(MainMenuCategory category) {
        switch (category) {
            case OPERATION:
                return LocaleUtils.getString("key.MainMenuTitle.Operation");
            case LITE:
                return LocaleUtils.getString("key.MainMenuTitle.Lite");
            case REPORTER:
                return LocaleUtils.getString("key.MainMenuTitle.Reporter");
            case RESULT:
                return LocaleUtils.getString("key.MainMenuTitle.ActualOutput");
            case WAREHOUSE:
                return LocaleUtils.getString("key.MainMenuTitle.WareHouse");
            case SETTINGS:
                return LocaleUtils.getString("key.MainMenuTitle.Settings");
            default:
                return category.name();
        }
    }
    
    private void makeTreeMenuButton(final Pane manupane, List<SystemOptionEntity> optionLicenses) {
        logger.info("makeTreeMenuButton start.");
        Properties properties = new Properties();
        
        Map<MainMenuCategory, List<MenuNode>> treeStructureNode = new HashMap<>();

        for (SystemOptionEntity optionLicence : optionLicenses) {
            logger.info("License: " + optionLicence.getOptionName() + " = " + optionLicence.getEnable());
            properties.setProperty(optionLicence.getOptionName(), optionLicence.getEnable().toString());
        }

        Optional<SystemOptionEntity> kanbanEditor = optionLicenses.stream().filter((o) -> LicenseOptionType.KanbanEditor.getName().equals(o.getOptionName())).findFirst();
        Optional<SystemOptionEntity> workflowEditor = optionLicenses.stream().filter((o) -> LicenseOptionType.WorkflowEditor.getName().equals(o.getOptionName())).findFirst();
        Optional<SystemOptionEntity> liteOption = optionLicenses.stream().filter((o) -> LicenseOptionType.LiteOption.getName().equals(o.getOptionName())).findFirst();
        Optional<SystemOptionEntity> reporterOption = optionLicenses.stream().filter((o) -> LicenseOptionType.ReporterOption.getName().equals(o.getOptionName())).findFirst();

        final boolean isKanbanEditor = kanbanEditor.isPresent() ? kanbanEditor.get().getEnable() : false;
        final boolean isWorkflowEditor = workflowEditor.isPresent() ? workflowEditor.get().getEnable() : false;
        final boolean isLiteOption = liteOption.isPresent() ? liteOption.get().getEnable() : false;
        final boolean isReporterOption = reporterOption.isPresent() ? reporterOption.get().getEnable() : false;

        // 作業日報
        final String dailyReportDisplayName = LocaleUtils.getString("key.WorkReportTitle");
        // 進捗モニタ
        final String progressMonitorDisplayName = LocaleUtils.getString("key.AndonSetting");
        // 作業分析メニューの表示名
        final String workAnalysisDisplayName = LocaleUtils.getString("key.WorkAnalysis");
        // 生産管理
        final String manufacturingManagementDisplayName = LocaleUtils.getString("key.ProductionNavi.Title");
        
        manupane.getChildren().clear();
        
        // Process each plugin
        for (AdManagerAppMainMenuInterface plugin : plugins) {
            // 機能権限を確認する
            LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
            boolean isAllow = true;

            List<RoleAuthorityType> types = plugin.getRoleAuthorityType();
            if (Objects.nonNull(types)) {
                isAllow = false;
                for (RoleAuthorityType auth : types) {
                    RoleAuthorityTypeEnum.add(auth);
                    if (loginUser.checkRoleAuthority(auth)) {
                        isAllow = true;
                    }
                }
            }

            // オプションライセンスを確認する
            LicenseOptionType pluginLicenseType = getOptionalType(plugin);
            if (!LicenseOptionType.NotRequireLicense.equals(pluginLicenseType)) {
                boolean isLicensed = false;
                switch (pluginLicenseType) {
                    case KanbanEditor:
                        isLicensed = (isKanbanEditor || isLiteOption);
                        break;
                    case WorkflowEditor:
                        isLicensed = (isWorkflowEditor || isLiteOption);
                        break;
                    default:
                        if (workAnalysisDisplayName.equals(plugin.getDisplayName()) && isLiteOption && !isKanbanEditor) {
                            // Lite単体構成の場合、作業分析メニューは非表示
                            isLicensed = false;
                        } else {
                            String optionName = pluginLicenseType.getName();
                            Optional<SystemOptionEntity> find = optionLicenses.stream().filter((o) -> optionName.equals(o.getOptionName())).findFirst();
                            if (find.isPresent()) {
                                isLicensed = find.get().getEnable();
                            }
                        }
                        break;
                }

                if (isAllow && !isLicensed) {
                    isAllow = false;
                }
            }

            // レポータのライセンスのみの場合
            if (isReporterOption && !isKanbanEditor
                    && (workAnalysisDisplayName.equals(plugin.getDisplayName()) // 作業分析
                    || dailyReportDisplayName.equals(plugin.getDisplayName()) // 作業日報
                    || progressMonitorDisplayName.equals(plugin.getDisplayName()) // 進捗モニタ設定
                    || manufacturingManagementDisplayName.equals(plugin.getDisplayName()) // 生産管理
                    )) {
                isAllow = false;
            }

            if (!isAllow) {
                logger.warn("plugin:{} is not allow.", plugin.getClass().getName());
                continue;
            }

            plugin.setProperties(properties);
            
            treeStructureNode = plugin.getSubMenuDisplayNames();
            if(Objects.isNull(treeStructureNode) || treeStructureNode.isEmpty()) {
                continue;
            }
            
            for (Map.Entry<MainMenuCategory, List<MenuNode>> entry : treeStructureNode.entrySet()) {
                System.out.println( entry.getKey());
                for (MenuNode node : entry.getValue()) {
                    printNode(node, "  ");  // helper method to print hierarchy
                }
            }
            
            // Recursive helper method to print MenuNode and children
            
            
//            for (Map.Entry<AdManagerAppMainMenuInterface.MainMenuCategory, List<AdManagerAppMainMenuInterface.MenuNode>> entry : treeStructureNode.entrySet()) {
//                AdManagerAppMainMenuInterface.MainMenuCategory category = entry.getKey();
//                System.out.println(category);
//                if (category == AdManagerAppMainMenuInterface.MainMenuCategory.UNUSED_MENU_CATEGORY) {
//                    continue; // Skip unused category
//                }
//                
//                for (AdManagerAppMainMenuInterface.MenuNode rootNode : entry.getValue()) {
//                     System.out.println("RootNode" + rootNode);
           }
        
        logger.info("makeTreeMenuButton end.");
    }
    
    // Helper method to recursively build TreeItems from MenuNodes
    private TreeItem<String> buildTreeItem(MenuNode node, AdManagerAppMainMenuInterface plugin, 
                                          Map<TreeItem<String>, AdManagerAppMainMenuInterface> itemPluginMap) {
        TreeItem<String> item = new TreeItem<>(node.getDisplayName());
        if (node.getChildren().isEmpty()) {
            // Leaf node: Associate plugin for action
            itemPluginMap.put(item, plugin);
        } else {
            // Parent node: Recurse for children
            for (AdManagerAppMainMenuInterface.MenuNode child : node.getChildren()) {
                item.getChildren().add(buildTreeItem(child, plugin, itemPluginMap));
            }
            item.setExpanded(true); // Expand submenus by default
        }
        return item;
    }

    private class PluginComparator implements Comparator<AdManagerAppMainMenuInterface> {

        @Override
        public int compare(AdManagerAppMainMenuInterface o1, AdManagerAppMainMenuInterface o2) {
            if (o1.getDisplayCategory().ordinal() > o2.getDisplayCategory().ordinal()) {
                if (o1.getDisplayOrder() < o2.getDisplayOrder()) {
                    return 4;
                } else if (o1.getDisplayOrder() > o2.getDisplayOrder()) {
                    return 3;
                }
                return 2;
            } else if (o1.getDisplayCategory().ordinal() < o2.getDisplayCategory().ordinal()) {
                if (o1.getDisplayOrder() < o2.getDisplayOrder()) {
                    return -4;
                } else if (o1.getDisplayOrder() > o2.getDisplayOrder()) {
                    return -3;
                }
                return -2;
            }
            if (o1.getDisplayOrder() < o2.getDisplayOrder()) {
                return 1;
            } else if (o1.getDisplayOrder() > o2.getDisplayOrder()) {
                return -1;
            }
            return 0;
        }
    }

    private void printNode(MenuNode node, String indent) {
        System.out.println(indent + "- " + node.getDisplayName());
        for (MenuNode child : node.getChildren()) {
            printNode(child, indent + "  ");
        }
    }

}
