/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controller;

import adtekfuji.fxscene.ComponentArea;
import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * 作業分析画面のコントローラー
 *
 * @author s-heya
 */
@FxScene(id = "ChartMainScene", fxmlPath = "/fxml/chartplugin/MainScene.fxml")
public class MainSceneController implements Initializable {

    @FXML
    @ComponentArea
    private AnchorPane AppBarPane;

    @FXML
    @ComponentArea
    private AnchorPane SideNaviPane;

    @FXML
    @ComponentArea
    private AnchorPane ContentNaviPane;

    @FXML
    @ComponentArea
    private AnchorPane MenuPane;

    @FXML
    @ComponentArea
    private AnchorPane MenuPaneUnderlay;

    @FXML
    private BorderPane mainSceneBorderPane;

    /**
     * 作業分析画面の初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SceneContiner sc = SceneContiner.getInstance();
        sc.setComponent(MenuPane, "MainMenuCompo");
        sc.setComponent(AppBarPane, "AppBarCompo");
        sc.setComponent(SideNaviPane, "ChartNaviCompo", this);
    }

    /**
     * 全画面表示にする
     *
     * @param enable trueのとき全画面
     */
    public void setFullScreen(boolean enable) {
        if (enable) {
            ContentNaviPane.setPadding(Insets.EMPTY);
        } else {
            ContentNaviPane.setPadding(new Insets(8.0));
        }
        SceneContiner.getInstance().getStage().setFullScreen(enable);
    }

    /**
     * メニューバー、サイドメニューを隠す
     *
     * @param enable trueのとき隠す
     */
    public void hideMenu(boolean enable) {
        //メニューバーの位置でサイトバーの頭の位置が変わるため調整
        if (enable) {
            AnchorPane.setTopAnchor(mainSceneBorderPane, 0.0);
        } else {
            AnchorPane.setTopAnchor(mainSceneBorderPane, 64.0);
        }

        AppBarPane.setManaged(!enable);
        AppBarPane.setVisible(!enable);

        //メニューはボタンにより表示させるためここでは常にfalse
        MenuPane.setManaged(!enable);
        MenuPane.setVisible(false);

        SideNaviPane.setManaged(!enable);
        SideNaviPane.setVisible(!enable);
    }
}
