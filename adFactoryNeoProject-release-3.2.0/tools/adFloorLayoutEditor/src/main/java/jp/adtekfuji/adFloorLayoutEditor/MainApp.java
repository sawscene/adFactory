package jp.adtekfuji.adFloorLayoutEditor;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle rb = LocaleUtils.load("locale");
        SceneProperties sp = new SceneProperties(stage, AdProperty.getProperties());
        SceneContiner.createInstance(sp);
        SceneContiner sc = SceneContiner.getInstance();
        sp.setAppTitle("adFloorLayoutEditor");
        sp.setAppIcon(new Image(getClass().getClassLoader().getResourceAsStream("image/icon.png")));
        sc.trans("Canvas");
        stage.setMaximized(true);
        stage.setOnCloseRequest((WindowEvent we) -> {
            if (Changed.isChanged()) {
                ButtonType buttonType = sc.showMessageBox(Alert.AlertType.INFORMATION, "", rb.getString("key.confirmLayout"), new ButtonType[]{ButtonType.OK, ButtonType.CANCEL}, ButtonType.CANCEL);
                if (buttonType != ButtonType.OK) {
                    we.consume();
                }
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "conf");
            AdProperty.load("adFloorLayoutEditor.properties");
        }
        catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        launch(args);
    }

}
