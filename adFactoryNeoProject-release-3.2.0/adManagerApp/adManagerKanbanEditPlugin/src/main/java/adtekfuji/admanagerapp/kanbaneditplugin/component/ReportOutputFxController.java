/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component;

import adtekfuji.admanagerapp.kanbaneditplugin.entity.ReportOutputCondition;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 帳票出力ダイアログ
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ReportOutputDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/report_output_dialog.fxml")
public class ReportOutputFxController implements ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();

    private ReportOutputCondition condition;

    private Dialog dialog;

    @FXML
    private TextField outputFolderText; // 出力先
    @FXML
    private CheckBox leaveTagsCheck; // 置換されなかったタグを残す

    /**
     * コンストラクタ
     */
    public ReportOutputFxController() {
    }

    /**
     * 帳票出力条件を設定する。
     *
     * @param argument 帳票出力条件
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof ReportOutputCondition) {
            // 帳票出力条件
            this.condition = (ReportOutputCondition) argument;

            // 出力先
            File outputFolder = this.existFolder(this.condition.getOutputFolder());
            this.outputFolderText.setText(outputFolder.getPath());

            // 置換されなかったタグを残す
            this.leaveTagsCheck.setSelected(this.condition.isLeaveTags());
        }
    }

    /**
     * ダイアログを設定する。
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * 選択ボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onChoiceFolder(ActionEvent event) {
        try {
            Node node = (Node) event.getSource();

            File outputFolder = this.existFolder(this.outputFolderText.getText());

            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(outputFolder);

            File folder = dirChooser.showDialog(node.getScene().getWindow());
            if (Objects.nonNull(folder)) {
                this.outputFolderText.setText(folder.getAbsolutePath());
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * OKボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        try {
            // 帳票出力条件を設定する。
            if (!this.setCondition()) {
                return;
            }

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
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

    /**
     * フォルダパスが存在するか確認してFileを取得する。
     *
     * @param folderPath フォルダパス
     * @return フォルダパスのFile(存在しない場合はデスクトップ)
     */
    private File existFolder(String folderPath) {
        File desktop = Paths.get(System.getProperty("user.home"), "Desktop").toFile();

        if (Objects.isNull(folderPath)) {
            return desktop;
        }

        File folder = new File(folderPath);
        if (!folder.exists()) {
            return desktop;
        }

        if (folder.isFile()) {
            folder = folder.getParentFile();
        }

        return folder;
    }

    /**
     * 帳票出力条件を設定する。
     *
     * @return 設定結果(true:設定, false:エラー項目あり)
     */
    private boolean setCondition() {
        String outputFolder = this.outputFolderText.getText();
        boolean leaveTags = this.leaveTagsCheck.isSelected();

        File folder = new File(this.outputFolderText.getText());
        if (!folder.exists()) {
            return false;
        }

        this.condition.setOutputFolder(outputFolder);
        this.condition.setLeaveTags(leaveTags);

        return true;
    }
}
