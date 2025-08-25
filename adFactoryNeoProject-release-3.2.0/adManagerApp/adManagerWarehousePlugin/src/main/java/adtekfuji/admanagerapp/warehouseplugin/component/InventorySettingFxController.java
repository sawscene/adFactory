/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.component;

import adtekfuji.admanagerapp.warehouseplugin.socket.WarehouseClientHandler;
import adtekfuji.admanagerapp.warehouseplugin.socket.WarehouseClientService;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.stage.WindowEvent;
import jp.adtekfuji.javafxcommon.controls.TimeTextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 棚卸設定ダイアログ
 * FXML Controller class
 *
 * @author nar-nakamura
 */
@FxComponent(id = "InventorySettingDialog", fxmlPath = "/fxml/warehouseplugin/inventory_setting_dialog.fxml")
public class InventorySettingFxController implements Initializable, ArgumentDelivery, DialogHandler {

    private final static Logger logger = LogManager.getLogger();
    private final WarehouseClientHandler clientHandler = WarehouseClientService.getInstance().getClient();
    private final String GET_CONFIG = "GET_CONFIG";
    private final String SET_CONFIG = "SET_CONFIG";
    private final String STKTAKE_START_DATE = "STKTAKE_START_DATE";

    private static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private TimeTextField fromTimeField;

    private Dialog dialog;

    private LocalDateTime startDateTime;
    /**
     * 
     */
    public InventorySettingFxController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.getWarehouseConfig();
    }

    @Override
    public void setArgument(Object argument) {
    }

    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * 
     * @param event 
     */
    @FXML
    private void onOkButton(ActionEvent event) {
        try {
            if (!this.setWarehouseConfig()) {
                return;
            }

            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
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
     * 
     */
    private void getWarehouseConfig() {
        try {
            String responce = clientHandler.send(this.GET_CONFIG, this.STKTAKE_START_DATE);
            String message[] = responce.split(",");

            if (this.GET_CONFIG.equals(message[0].trim())) {
                for (int i = 1; i < message.length; i++) {
                    String name = message[i++].trim();
                    String value = message[i].trim();

                    switch (name) {
                        case STKTAKE_START_DATE:// 棚卸開始日
                            this.startDateTime = LocalDateTime.parse(value, datetimeFormatter);
                            LocalDate fromDate = this.startDateTime.toLocalDate();
                            LocalTime fromTime = this.startDateTime.toLocalTime();
                            fromDatePicker.setValue(fromDate);
                            fromTimeField.setText(fromTime.format(timeFormatter));
                            break;
                        default:
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 
     */
    private boolean setWarehouseConfig() {
        try {
            LocalDate fromDate = fromDatePicker.getValue();
            LocalTime fromTime = LocalTime.parse(fromTimeField.getText());
            LocalDateTime fromDateTime = LocalDateTime.of(fromDate, fromTime);

            if (!fromDateTime.equals(this.startDateTime) && fromDateTime.isBefore(LocalDateTime.now())) {
                // 現在日時より前の日時は指定できない。(現在の棚卸開始日時を除く)
                return false;
            }

            this.startDateTime = LocalDateTime.of(fromDate, fromTime);
            String startDateString = datetimeFormatter.format(this.startDateTime);
            clientHandler.send(this.SET_CONFIG, this.STKTAKE_START_DATE + "," + startDateString);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return true;
    }
}
