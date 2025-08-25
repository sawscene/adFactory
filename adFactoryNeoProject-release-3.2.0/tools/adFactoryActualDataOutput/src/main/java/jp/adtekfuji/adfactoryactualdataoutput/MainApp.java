package jp.adtekfuji.adfactoryactualdataoutput;

import adtekfuji.locale.LocaleUtils;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"), LocaleUtils.load("locale"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("adFactory Actual Output");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        logger.info("args:{}", args);
        launch(args);
    }

}
