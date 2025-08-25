package adtekfuji.andon.agenda;

import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.model.AgendaModel;
import adtekfuji.andon.agenda.model.data.ConfigData;
import adtekfuji.fxscene.ComponentArea;
import adtekfuji.fxscene.FxScene;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import jp.adtekfuji.andon.common.AndonCampus;
import jp.adtekfuji.andon.common.MainSceneFxInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画実績画面のコントローラー
 *
 * @author s-heya
 */
@FxScene(id = "AgendaMain", fxmlPath = "/fxml/agenda_andon.fxml")
public class AgendaMainController implements Initializable, MainSceneFxInterface {

    private static final Logger logger = LogManager.getLogger();
    private final AndonCampus andonCampus = new AndonCampus();

    @FXML
    @ComponentArea
    private AnchorPane MainScenePane;
    @FXML
    @ComponentArea
    private AnchorPane MainSceneContentPane;
    @FXML
    @ComponentArea
    private AnchorPane AppBarPane;

    /**
     * 計画実績画面を初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            AgendaModel.getInstance().initialize();

            SceneContiner sc = SceneContiner.getInstance();
            sc.setComponent(this.AppBarPane, "HeaderCompo");
            sc.getStage().getScene().setFill(Color.BLACK);

            if (KanbanStatusConfig.getEnableView()) {
                // 画面表示あり
                sc.getStage().setFullScreen(ConfigData.getInstance().isFullScreen());
            } else {
                // 画面表示なし
                sc.getStage().setIconified(true);// 最小化
            }
        });
    }

    /**
     * 接続できないダイアログに移動する
     */
    @Override
    public void updateDisplay() {
        Platform.runLater(() -> {
            SceneContiner sc = SceneContiner.getInstance();
            sc.trans("AgendaMain");
            sc.setComponent(this.AppBarPane, "HeaderCompo");
        });
    }
}
