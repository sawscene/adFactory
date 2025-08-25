package adtekfuji.admanagerapp.component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author e-mori
 */
@FxComponent(id = "AppBarCompo", fxmlPath = "/fxml/compo/app_bar_compo.fxml")
public class AppBarCompoFxController implements Initializable, ArgumentDelivery {

    private final SceneContiner sc = SceneContiner.getInstance();

    @FXML
    private Button menuButton;
    
    /**
     * アプリケーションバーを初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * パラメータを設定する。
     * 
     * @param argument パラメータ 
     */
    @Override
    public void setArgument(Object argument) {
        this.menuButton.setVisible(true);
        
        if (argument instanceof Properties) {
            Properties arguments = (Properties) argument;
            if (Boolean.parseBoolean(arguments.getProperty("hideMenu", Boolean.FALSE.toString()))) {
                this.menuButton.setVisible(false);
            }
        }
    }

    @FXML
    private void onMenuButton(ActionEvent event) {
        sc.visibleArea("MenuPane", true);
        sc.visibleArea("MenuPaneUnderlay", true);
    }
}
