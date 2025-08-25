package jp.adtekfuji.admonitorpluginformat;

import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * メインシーンクラス
 * 
 * @author e.mori
 * @version 1.6.2
 * @since 2017.02.08.Wen
 */
@FxScene(id = "MonitorTemplateMain", fxmlPath = "/fxml/main.fxml")
public class MainFxController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SceneContiner sc = SceneContiner.getInstance();
        // モニター表示
        sc.setComponent(mainPane, "MonitorTemplate");
    }

}
