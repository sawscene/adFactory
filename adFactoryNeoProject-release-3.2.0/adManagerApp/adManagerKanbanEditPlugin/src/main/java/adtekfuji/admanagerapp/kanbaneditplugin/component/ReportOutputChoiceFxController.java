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
import java.util.List;
import java.util.Objects;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.controlsfx.control.CheckListView;

/**
 * 帳票出力ダイアログ（帳票選択あり）
 *
 * @author nar-nakamura
 */
@FxComponent(id = "ReportOutputChoiceDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/report_output_choice_dialog.fxml")
public class ReportOutputChoiceFxController implements ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();

    private ReportOutputCondition condition;

    private Dialog dialog;

    @FXML
    private TextField outputFolderText; // 出力先
    @FXML
    private CheckListView<String> reportListView; // 帳票リスト 
    @FXML
    private CheckBox leaveTagsCheck; // 置換されなかったタグを残す
    @FXML
    private CheckBox exportAsPdf; // pdfにて出力する

    public static boolean isExcelInstalled() {
        String officeKeyPath = "Software\\Microsoft\\Office";
        try {
            // レジストリキーが存在する場合、インストールされている可能性がある
            return Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, officeKeyPath)
                    || Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, officeKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * コンストラクタ
     */
    public ReportOutputChoiceFxController() {
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
            
            // 帳票テンプレートのファイル名を設定する。
            ObservableList<String> templateList = FXCollections.observableArrayList(this.condition.getTemplateNames());
            this.reportListView.setItems(templateList);

            // 置換されなかったタグを残す
            this.leaveTagsCheck.setSelected(this.condition.isLeaveTags());

            // PDFで出力する
            this.exportAsPdf.setDisable(!isExcelInstalled());
            this.exportAsPdf.setSelected(this.condition.isExportAsPdf());

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
        boolean exportAsPdf = this.exportAsPdf.isSelected();

        // チェック対象の帳票テンプレートファイル
        List<String> checkTenmplate = this.reportListView.getCheckModel().getCheckedItems();
        // 帳票テンプレートのチェックリストに一つもチェックを入れていない場合はOKボタンを無効扱いにする。
        if (!this.reportListView.getItems().isEmpty()) {
            if (checkTenmplate.isEmpty()) {
                return false;
            }
        }

        File folder = new File(this.outputFolderText.getText());
        if (!folder.exists()) {
            return false;
        }

        this.condition.setOutputFolder(outputFolder);
        this.condition.setLeaveTags(leaveTags);
        this.condition.setExportAsPdf(exportAsPdf);
        this.condition.setTemplateNames(checkTenmplate);

        return true;
    }
}
