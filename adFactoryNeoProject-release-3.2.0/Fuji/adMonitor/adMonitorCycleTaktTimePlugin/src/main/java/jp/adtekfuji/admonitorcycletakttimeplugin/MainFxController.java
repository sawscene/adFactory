package jp.adtekfuji.admonitorcycletakttimeplugin;

import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

@FxScene(id = "CycleTaktTimeMain", fxmlPath = "/fxml/admonitorcycletakttimeplugin/main.fxml")
public class MainFxController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SceneContiner sc = SceneContiner.getInstance();
        sc.setComponent(mainPane, "CycleTaktTimeCompo");
    }
}
