package jp.adtekfuji.addatabaseapp.component;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import jp.adtekfuji.addatabaseapp.controller.PostgresManager;

/**
 * データベースメンテナンス画面コントローラー
 *
 * @author e-mori
 */
public class UpdateFxController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private void onUpdate(ActionEvent event) {
        PostgresManager updateManager = new PostgresManager();
        updateManager.updateTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
