/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin;

import adtekfuji.admanagerapp.chartplugin.controller.MainSceneController;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;

/**
 * 作業分析プラグインメニュー
 *
 * @author s-heya
 */
public class AdManagerChartPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();
    
    
    @Override
    public String getDisplayName() {
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        return LocaleUtils.getString("key.WorkAnalysis");
        //return "作業分析";
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.REFERENCE_FUNCTION;
    }

    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.MIDDLE_PRIORITY.getOrder();
    }
    
    
    @Override
    public Map<MainMenuCategory, List<MenuNode>> getMultilevelMenuNodes() {
        Map<MainMenuCategory, List<MenuNode>> nodeMap = new HashMap<>();
        List<MenuNode> resultNodes = new ArrayList<>();
        
        List<MenuNode> analysisChildren = new ArrayList<>();
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.TimeLine"), null)); // Timeline
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.KanbanTotalWorkTime"), null)); // Total Work Time
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.ProcessAverageWorkTime"), null)); // Average Work Time: Process
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.WorkerAverageWorkTime"), null)); // Average Work Time: Worker
        resultNodes.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.AnalysisTitle"), analysisChildren)); // Analysis with children
        nodeMap.put(MainMenuCategory.RESULT, resultNodes);

        return nodeMap;
    }
    
    @Override
    public void onSelectSubMenuAction(String subMenuDisplayName) {
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("ChartMainScene");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        
        final String timeLine = LocaleUtils.getString("key.SubMenuTitle.TimeLine");
        final String totalWorkTime = LocaleUtils.getString("key.SubMenuTitle.KanbanTotalWorkTime");
        final String averageWorkTimeProcess = LocaleUtils.getString("key.SubMenuTitle.ProcessAverageWorkTime");
        final String averageWorkTimeWorker = LocaleUtils.getString("key.SubMenuTitle.WorkerAverageWorkTime");
        
        
        if (timeLine.equals(subMenuDisplayName)) {
            //TODO  
        } else if (totalWorkTime.equals(subMenuDisplayName)) {
            //TODO
        } else if (averageWorkTimeProcess.equals(subMenuDisplayName)) {
           //TODO
        } else if (averageWorkTimeWorker.equals(subMenuDisplayName)) {
            //TODO
        }
    }

    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("ChartMainScene");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.CsvReportOut;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.OUTPUT_ACTUAL);
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
