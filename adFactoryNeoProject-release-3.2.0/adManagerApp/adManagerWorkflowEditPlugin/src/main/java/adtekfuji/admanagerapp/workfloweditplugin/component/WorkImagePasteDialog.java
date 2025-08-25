package adtekfuji.admanagerapp.workfloweditplugin.component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author k.watanabe
 */
@FxComponent(id = "WorkImagePasteDialog", fxmlPath = "/fxml/compo/work_image_paste_dialog.fxml")
public class WorkImagePasteDialog implements Initializable, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private Dialog dialog;

    /**
     * 初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * ダイアログの設定
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        DialogPane dialogPane =  dialog.getDialogPane();

        Region buttonBar = (Region) dialogPane.lookup(".button-bar");
        if (buttonBar != null) {
            // ボタンバーの非表示
            buttonBar.setStyle("-fx-pref-height: 0;");
        }
        // タイトルバーの非表示
        dialog.initStyle(StageStyle.UNDECORATED);

        dialogPane.getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
        this.dialog = dialog;
    }

    /**
     * 現在のシートに貼り付けボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onPasteToCurrentSheetAction(ActionEvent event) {
        try {
            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * すべてのシートに貼り付けボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onPasteToAllSheetsAction(ActionEvent event) {
        try {
            this.dialog.setResult(ButtonType.YES);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onCancelAction(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
