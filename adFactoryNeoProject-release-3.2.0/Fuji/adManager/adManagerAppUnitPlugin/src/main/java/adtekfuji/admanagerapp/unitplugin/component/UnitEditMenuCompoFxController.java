/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * ユニット編集メニュー
 *
 * @author s-maeda
 */
@FxComponent(id = "UnitEditMenuCompo", fxmlPath = "/fxml/compo/unitEditMenu.fxml")
public class UnitEditMenuCompoFxController implements Initializable, ArgumentDelivery {

    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     * ユニット編集
     *
     * @param event 
     */
    @FXML
    public void onViewUnitList(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "UnitListComp");
    }

    /**
     * 生産計画読み込み(ユニット)
     *
     * @param event 
     */
    @FXML
    public void onViewUnitImport(ActionEvent event) {
        sc.setComponent("ContentNaviPane", "UnitImportCompo");
    }

}
