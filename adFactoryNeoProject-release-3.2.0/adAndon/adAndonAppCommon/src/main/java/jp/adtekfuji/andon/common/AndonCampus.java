/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.common;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import jp.adtekfuji.andon.entity.PluginInfoEntity;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 * 進捗モニターのレイアウトパネル
 *
 * @author dungtn
 */
public class AndonCampus {

    Logger logger = LogManager.getLogger();

    private static final String COMPONENT_STYLE = "-fx-background-color: black";
    private static final String IS_DISPLAY = "1";
    private static final String DISPLAY_KEY = "Disp";
    private static final String TOP_KEY = "Top";
    private static final String LEFT_KEY = "Left";
    private static final String BOTTOM_KEY = "Bottom";
    private static final String RIGHT_KEY = "Right";
    private static final String MIN_HEIGHT_KEY = "MinHeight";
    private static final String MIN_WIDTH_KEY = "MinWidth";
    private static final String PREF_HEIGHT_KEY = "PrefHeight";
    private static final String PREF_WIDTH_KEY = "PrefWidth";
    private static final String SEPARATOR = "/";
    //private static final double INIT_WIDTH = 1920;
    //private static final double INIT_HEIGHT = 1080;
    private final String dashBoardIniFile = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "Dashboard.ini";
    private final String dashBoardIniFile2 = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "Dashboard2.ini";
    private String dashBoardIniForEquipment = "";

    public AndonCampus() {
    }

    /**
     * プラグインをロードして、指定位置に表示する。
     *
     * @param rootPane
     * @param components
     * @param setting 進捗モニタ設定
     */
    public void loadComponents(Pane rootPane, List<PluginInfoEntity> components, AndonMonitorLineProductSetting setting) {

        SceneContiner sc = SceneContiner.getInstance();
        String equipmentIdName = AdProperty.getProperties().getProperty("equipmentIdName");
        if (Objects.nonNull(equipmentIdName) && !equipmentIdName.isEmpty()) {
            dashBoardIniForEquipment = System.getenv("ADFACTORY_HOME") + File.separator + "conf"
                    + File.separator + "Dashboard2_" + equipmentIdName + ".ini";
        }

        try {
            int iniFileNo = 0;
            File file = null;

            if (!dashBoardIniForEquipment.isEmpty()) {
                file = new File(dashBoardIniForEquipment);
                if (file.exists()) {
                    iniFileNo = 2;
                }
            }

            if (iniFileNo == 0) {
                // レイアウト設定は、dashBoardIniFileよりdashBoardIniFile2を優先して読み込む。
                file = new File(dashBoardIniFile2);
                if (file.exists()) {
                    iniFileNo = 2;
                } else {
                    file = new File(dashBoardIniFile);
                    if (file.exists()) {
                        iniFileNo = 1;
                    }
                }
            }

            // サーバーからレイアウトを取得する場合レイアウト設定2を用いる
            iniFileNo = Objects.nonNull(setting.getRemoteLayout()) && setting.getRemoteLayout() ? 2 : iniFileNo;

            InputStream is = setting.getRemoteLayout() && Objects.nonNull(setting.getLayout())
                    ? new ByteArrayInputStream(setting.getLayout().getBytes())
                    : new FileInputStream(file);

            Wini ini = new Wini(new InputStreamReader(is, "Shift_JIS"));
            Iterator<Map.Entry<String, Section>> ite = ini.entrySet().iterator();

            while (ite.hasNext()) {
                try {
                    Map.Entry<String, Section> next = ite.next();
                    String sectionName = next.getKey();// セクション名 (設定1: "日別工程計画実績数フレーム.1", 設定2: "adAndonDailyWorkPlanNumPlugin.1")
                    if (iniFileNo == 2) {
                        sectionName = sectionName.toLowerCase();
                    }

                    String targetName;// 設定1: 表示名 (例: "日別工程計画実績数フレーム"), 設定2: プラグイン名(例: "adAndonDailyWorkPlanNumPlugin")
                    String pluginNo;// １つのプラグインが複数設定できる場合の番号 (".1", ".2", ...)
                    int pos = sectionName.indexOf('.');
                    if (pos > 0) {
                        targetName = sectionName.substring(0, pos);
                        pluginNo = sectionName.substring(pos);
                    } else {
                        targetName = sectionName;
                        pluginNo = "";
                    }
                    Section section = next.getValue();

                    if (IS_DISPLAY.equals(section.get(DISPLAY_KEY))) {
                        AnchorPane pane = new AnchorPane();
                        pane.setStyle(COMPONENT_STYLE);
                        pane.setVisible(true);
                        rootPane.getChildren().add(pane);

                        Optional<PluginInfoEntity> opt;
                        if (iniFileNo == 1) {
                            opt = components.stream().filter(p -> p.getDispName().equals(targetName)).findFirst();
                        } else {
                            String pluginName = targetName.toLowerCase();
                            opt = components.stream().filter(p -> p.getPluginName().equals(pluginName)).findFirst();
                        }

                        if (!opt.isPresent()) {
                            continue;
                        }

                        PluginInfoEntity pluginInfo = opt.get();
                        String dispName = pluginInfo.getDispName();// 表示名 (例: "日別工程計画実績数フレーム")
                        String frameName = dispName + pluginNo;// フレーム名 (例: "日別工程計画実績数フレーム.1")

                        String componentId = pluginInfo.getComponentName();
                        logger.info("Create Pane title:{}, id:{}", dispName, componentId);
                        if (Objects.nonNull(componentId)) {
                            // 指定位置にプラグインを表示
                            sc.setComponent(pane, componentId, frameName, true);
                            this.resizeLayoutComponent(pane, section, componentId, sc.getSizeComponent(componentId), sc.getFxComponents().containsKey(componentId), iniFileNo);
                        }
                    } else {
                        logger.info("Not display {}", sectionName);
                    }

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * Fix and resize Layout for Component
     *
     * @param pane
     * @param sec
     * @param compoName
     * @param compoSize
     * @param isExist
     * @param iniFileNo
     */
    private void resizeLayoutComponent(Pane pane, Section sec, String compoName, Map<String, Double> compoSize, boolean isExist, int iniFileNo) {

        pane.setMaxHeight(Pane.USE_PREF_SIZE);
        pane.setMinHeight(Pane.USE_PREF_SIZE);
        pane.setMaxWidth(Pane.USE_PREF_SIZE);
        pane.setMinWidth(Pane.USE_PREF_SIZE);

        String[] itemsTop = sec.get(TOP_KEY).split(SEPARATOR, 2);
        String[] itemsLeft = sec.get(LEFT_KEY).split(SEPARATOR, 2);
        String[] itemsBottom = sec.get(BOTTOM_KEY).split(SEPARATOR, 2);
        String[] itemsRight = sec.get(RIGHT_KEY).split(SEPARATOR, 2);

        double layoutX = Double.valueOf(itemsLeft[0]);
        double layoutY = Double.valueOf(itemsTop[0]);
        double layoutX2 = Double.valueOf(itemsRight[0]);
        double layoutY2 = Double.valueOf(itemsBottom[0]);

        double newWidth;
        double newHeight;
        if (iniFileNo == 2) {
            // Dashboard2.iniの場合、値がそのまま座標となる。

            // プラグインの表示サイズ
            newWidth = layoutX2 - layoutX;
            newHeight = layoutY2 - layoutY;
        } else {
            // Dashboard.iniの場合、値は割合。
            // ウィンドウ中央が表示されているディスプレイの解像度を全体の解像度として、座標を計算する。

            // Dashboard.ini の場合、レイアウトツールの計算誤差で右下座標が全体のサイズを超えている場合がある。
            double winWidth = Double.valueOf(itemsLeft[1]);
            double winHeight = Double.valueOf(itemsTop[1]);
            if (layoutX2 > winWidth) {
                layoutX2 = winWidth;
            }
            if (layoutY2 > winHeight) {
                layoutY2 = winHeight;
            }

            // プラグインの表示サイズ
            newWidth = layoutX2 - layoutX;
            newHeight = layoutY2 - layoutY;

            // ウィンドウの表示位置
            SceneContiner sc = SceneContiner.getInstance();
            double winX = sc.getWindow().getX() + (sc.getWindow().getWidth() / 2.0);
            double winY = sc.getWindow().getY() + (sc.getWindow().getHeight() / 2.0);
            //logger.info("***** winX={}, winY={}", winX, winY);

            // ウィンドウ中央が表示されているスクリーンを取得する。
            List<Screen> screens = Screen.getScreens();
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            for (int screenId = 0; screenId < screens.size(); screenId++) {
                visualBounds = screens.get(screenId).getBounds();
                //logger.info("***** visualBounds minX={}, maxX={}, minY={}, maxY={}", visualBounds.getMinX(), visualBounds.getMaxX(), visualBounds.getMinY(), visualBounds.getMaxY());
                if (winX >= visualBounds.getMinX() && winX < visualBounds.getMaxX() && winY >= visualBounds.getMinY() && winY < visualBounds.getMaxY()) {
                    //logger.info("***** screenId={}", screenId);
                    break;
                }
            }

            layoutX = (visualBounds.getWidth() / winWidth) * layoutX;
            layoutY = (visualBounds.getHeight() / winHeight) * layoutY;
            newWidth = (visualBounds.getWidth() / winWidth) * newWidth;
            newHeight = (visualBounds.getHeight() / winHeight) * newHeight;
        }

        pane.setLayoutX(layoutX);
        pane.setLayoutY(layoutY);

        if (isExist) {
            double oldWidth = Double.max(compoSize.get(MIN_WIDTH_KEY), compoSize.get(PREF_WIDTH_KEY));
            double oldHeight = Double.max(compoSize.get(MIN_HEIGHT_KEY), compoSize.get(PREF_HEIGHT_KEY));
            //pane.setPrefWidth(oldWidth);
            //pane.setPrefHeight(oldHeight);
            //this.transforms(pane, oldWidth, oldHeight, newWidth, newHeight);
            pane.setPrefWidth(newWidth);
            pane.setPrefHeight(newHeight);
            logger.info("resizeLayoutComponent: {}, layoutX:{}, layoutY:{}, oldWidth:{}, oldHeight:{}, newWidth:{}, newHeight:{}", compoName, layoutX, layoutY, oldWidth, oldHeight, newWidth, newHeight);
        } else {
            pane.setPrefWidth(newWidth);
            pane.setPrefHeight(newHeight);
            Label label = new Label();
            label.setText("'" + compoName + "' is not found.");
            label.getStyleClass().add("warn_message");
            label.setLayoutX(16);
            label.setLayoutY((newHeight / 2) - 8);
            pane.getChildren().add(label);
        }
    }

    /**
     * transforms for frameName
     *
     * @see adtekfuji.utility.ComponentsUtils.transforms(Node, double, double,
     * double, double)
     *
     * @param node
     * @param newWidth
     * @param newHeight
     */
    public void transforms(Node node, double newWidth, double newHeight) {
        Rectangle2D visualBounds = Screen.getPrimary().getBounds();
        this.transforms(node, visualBounds.getWidth(), visualBounds.getHeight(), newWidth, newHeight);
    }

    /**
     * コンポーネントを拡大縮小
     *
     * @param node
     * @param oldWidth
     * @param oldHeight
     * @param newWidth
     * @param newHeight
     */
    private void transforms(Node node, double oldWidth, double oldHeight, double newWidth, double newHeight) {
        // 表示倍率設定
        double ratio;
        if ((newHeight / newWidth) > (oldHeight / oldWidth)) {
            ratio = newWidth / oldWidth;
        } else {
            ratio = newHeight / oldHeight;
        }
        Scale scale = new Scale(ratio, ratio, 0, 0);
        node.getTransforms().add(scale);
    }
}
