/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.guitesttool;

import adtekfuji.fxscene.ComponentArea;
import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author ke.yokoi
 */
@FxScene(id = "MainScene", fxmlPath = "/fxml/main_scene.fxml")
public class MainSceneFxController implements Initializable {

    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    @ComponentArea
    private AnchorPane MainScenePane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sc.setComponent(MainScenePane, "KanbanListCompo");
    }

    @FXML
    private void onEqipmentAction(ActionEvent event) {
        sc.showModelessDialog("設備一覧", "EquipmentListCompo", null);
    }

    @FXML
    private void onOrganizationAction(ActionEvent event) {
        sc.showModelessDialog("組織一覧", "OrganizationListCompo", null);
    }
    
    @FXML
    private void onAutoTest(ActionEvent event) {
        sc.showModelessDialog("自動テスト", "AutoTestCompo", null);
    }

}
