package jp.adtekfuji.adreporter;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import jp.adtekfuji.adreporter.common.ReporterConfig;
import jp.adtekfuji.adreporter.rmi.RmiServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author nar-nakamura
 */
public class ReporterFxController implements Initializable {

    private final Logger logger = LogManager.getLogger();

    private Stage stage;

    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int port = Integer.valueOf(ReporterConfig.getReporterPort());
        RmiServer.getInstance().setRmiPort(port);
    }

    /**
     * 
     * @param stage 
     */
    public void setStage(Stage stage) {
        this.stage = stage;

        // 開始・完了時のイベント
        this.stage.showingProperty().addListener(this.showingListener);
    }

    /**
     * 
     */
    private final ChangeListener<Boolean> showingListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        logger.info("showingListener: oldValue={}, newValue={}", oldValue, newValue);
        if (!oldValue && newValue) {
            // 開始時
            RmiServer.getInstance().start();
        } else if (oldValue && !newValue) {
            // 終了時
            RmiServer.getInstance().stop();
        }
    };
}
