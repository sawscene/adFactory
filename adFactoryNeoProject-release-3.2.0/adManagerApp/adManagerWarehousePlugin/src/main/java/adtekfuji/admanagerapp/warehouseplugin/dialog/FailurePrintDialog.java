/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author s-morita
 */
@FxComponent(id = "FailurePrintDialog", fxmlPath = "/fxml/warehouseplugin/failure_print_dialog.fxml")
public class FailurePrintDialog implements Initializable, ArgumentDelivery, DialogHandler {
    
    private Dialog dialog;
    private final Logger logger = LogManager.getLogger();
    
    @FXML
    private ListView failurePrintList;

    private ListProperty<String> listProperty = new SimpleListProperty<>();
    
    /**
     * コンストラクタ
     */
    public FailurePrintDialog(){
        
    }
    
    /**
     * 初期化
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("FailurePrintDialog start.");
    }
    
    /**
     * パラメータを設定する。
     *
     * @param argument パラメータ
     */
    @Override
    public void setArgument(Object argument) {
        List<String> failureSupplyNos = (List<String>) argument;
        
        this.failurePrintList.itemsProperty().bind(this.listProperty);
        this.listProperty.set(FXCollections.observableArrayList(failureSupplyNos));
    }
    
    /**
     * ダイアログ設定
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
     * OK処理
     * 
     * @param event アクションイベント
     */
    @FXML
    private void onOKButton(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     * 
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
