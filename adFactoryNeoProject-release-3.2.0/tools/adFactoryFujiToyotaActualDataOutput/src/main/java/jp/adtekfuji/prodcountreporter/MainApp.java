package jp.adtekfuji.prodcountreporter;

import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.application.Application;
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
        System.setProperty("prism.lcdtext", "false");

        InputStreamReader fileReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("version.ini"));
        String ver;
        try (BufferedReader br = new BufferedReader(fileReader)) {
            ver = "v" + br.readLine();
        }

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("adFactory Fuji Toyota Actual Data Output " + ver);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        logger.info("Starting the applications: {}, {}", args.length, args);

        try {
            AdProperty.load("actual_output.properties");
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }

        if (args.length == 1 && "-silent".equals(args[0])) {

            try {
                logger.info("Silent execute...");

                OutputActualInfo info = new OutputActualInfo();
                info.load();
                OutputActualFacade outputActualFacade = new OutputActualFacade(info);
                outputActualFacade.output(OutputActualFacade.SEARCH_TYPE.LAST_UPDATE);

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                logger.info("Shutdown the application.");

                // Java VMを終了する
                System.exit(0);
            }

        } else {
            launch(args);

            logger.info("Shutdown the application.");

            // Java VMを終了する
            System.exit(0);
        }
    }
}
