/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.dsitemeditplugin.controller;

import adtekfuji.fxscene.ComponentArea;
import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

/**
 * 品番一覧画面
 *
 * @author s-heya
 */
@FxScene(id = "DsItemEditScene", fxmlPath = "/adtekfuji/admanagerapp/dsitemeditplugin/dsitem_edit_scene.fxml")
public class DsItemEditSceneController implements Initializable {

    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    @ComponentArea
    private AnchorPane AppBarPane;
    @FXML
    @ComponentArea
    private AnchorPane DsItemEditPane;
    @FXML
    @ComponentArea
    private AnchorPane MenuPane;
    @FXML
    @ComponentArea
    private AnchorPane MenuPaneUnderlay;
    @FXML
    @ComponentArea
    private SplitPane WorkflowEditPane;
    @FXML
    @ComponentArea
    private AnchorPane SideNaviPane;
    @FXML
    @ComponentArea
    private AnchorPane ContentNaviPane;

    /**
     * 品番一覧画面を初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sc.setComponent(MenuPane, "MainMenuCompo");
    }

}
