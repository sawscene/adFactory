/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.dialog;

import adtekfuji.admanagerapp.warehouseplugin.entity.DeliveryInfo;
import adtekfuji.clientservice.WarehouseInfoFaced;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画変更ダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "WarehousePlanChangeDialog", fxmlPath = "/fxml/warehouseplugin/ChangePlansDialog.fxml")
public class ChangePalansDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WarehouseInfoFaced facad = new WarehouseInfoFaced();
    private final ObjectProperty<LocalDate> dateProperty;

    private Dialog dialog;
    private List<DeliveryInfo> deliveries;

    @FXML
    private DatePicker datePicker;

    /**
     * コンストラクタ
     */
    public ChangePalansDialog() {
        this.dateProperty = new SimpleObjectProperty();
    }

    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.dateProperty.set(DateUtils.toLocalDate(new Date()));
        this.datePicker.valueProperty().bindBidirectional(this.dateProperty);
    }

    /**
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof List) {
            this.deliveries = (List<DeliveryInfo>) argument;
            
            this.deliveries.stream()
                    .filter(o -> Objects.nonNull(o.getValue().getDueDate()))
                    .map(o -> o.getValue().getDueDate())
                    .min((o1, o2 ) -> o1.compareTo(o2))
                    .ifPresent(min -> dateProperty.set(DateUtils.toLocalDate(min)));
        }
    }

    /**
     * 
     * @param dialog 
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }

    /**
     * OKボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onOk(ActionEvent event) {
        try {
            
            List<String> deliveryNos = this.deliveries.stream()
                    .map(o -> o.getValue().getDeliveryNo())
                    .collect(Collectors.toList());

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    return  facad.updateDaliveryDate(deliveryNos, DateUtils.toDate(dateProperty.get()));
                }

                @Override
                protected void succeeded() {
                    super.succeeded();

                    ResponseEntity response = this.getValue();
                    if (response.isSuccess()) {
                        dialog.setResult(ButtonType.OK);
                        dialog.close();
                    } else {
                        DialogBox.alert(response.getErrorType());
                    }
                }

                @Override
                protected void failed() {
                    super.failed();

                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                        DialogBox.alert((Exception)this.getException());
                    }
                }
            };
            new Thread(task).start();

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
    private void onCancel(ActionEvent event) {
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
