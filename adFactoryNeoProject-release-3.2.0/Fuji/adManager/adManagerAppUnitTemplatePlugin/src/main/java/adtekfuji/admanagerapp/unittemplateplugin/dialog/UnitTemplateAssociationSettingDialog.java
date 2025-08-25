/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.converter.DateTimeStringConverter;

/**
 * ユニットテンプレートの時間設定ダイアログ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.4.Fri
 */
@FxComponent(id = "UnitTemplateAssociationSettingDialog", fxmlPath = "/fxml/compo/dialog/unittemplateAssociationSettingDialog.fxml")
public class UnitTemplateAssociationSettingDialog implements Initializable, ArgumentDelivery {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final static String DATETIME_REGEX = "\\d|:|/|-|\\s";
    private final static String OFFSET_REGEX = "\\d|:|-";
    private final static String OFFSET_DEFAULT = "00:00:00";

    @FXML
    private TextField OffsetTimeTextField;
    @FXML
    private TextField EndTimeTextField;
    @FXML
    private TextField StartTimeTextField;
    @FXML
    private Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);
    }

    /**
     * 初期化処理
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof UnitTemplateAssociationSettingDialogEntity) {
            UnitTemplateAssociationSettingDialogEntity settingDialogEntity = (UnitTemplateAssociationSettingDialogEntity) argument;

            if (settingDialogEntity.isEditSingle()) {
                OffsetTimeTextField.setDisable(true);

                //新規作成時の時間を設定
                if (Objects.isNull(settingDialogEntity.getStartTime())) {
                    settingDialogEntity.setStartTime(new Date(0));
                }
                if (Objects.isNull(settingDialogEntity.getEndTime())) {
                    settingDialogEntity.setEndTime(new Date(0));
                }

                //日付フォーマット生成
                SimpleDateFormat toConvertDatePattern = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

                //開始時間
                StartTimeTextField.textProperty().bindBidirectional(settingDialogEntity.startTimeProperty(), new DateTimeStringConverter(LocaleUtils.getString("key.DateTimeFormat")));
                StartTimeTextField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                    if (!event.getCharacter().matches(DATETIME_REGEX)) {
                        event.consume();
                    }
                });
                StartTimeTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    //終了時間の自動計算
                    Date startDate = StringTime.convertStringToDate(newValue, LocaleUtils.getString("key.DateTimeFormat"));
                    if (Objects.nonNull(startDate)) {
                        Calendar endDate = new Calendar.Builder().setInstant(startDate.getTime() + settingDialogEntity.getTaktTime()).build();
                        settingDialogEntity.setEndTime(endDate.getTime());
                    }
                });

                //終了時間
                EndTimeTextField.textProperty().bindBidirectional(settingDialogEntity.endTimeProperty(), toConvertDatePattern);
                EndTimeTextField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                    if (!event.getCharacter().matches(DATETIME_REGEX)) {
                        event.consume();
                    }
                });
                EndTimeTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    //終了時間の判定
                    Date endDate = StringTime.convertStringToDate(newValue, LocaleUtils.getString("key.DateTimeFormat"));
                    if (Objects.nonNull(endDate)) {
                        Calendar minDate = new Calendar.Builder().setInstant(settingDialogEntity.getStartTime().getTime() + settingDialogEntity.getTaktTime()).build();
                        if (minDate.getTime().compareTo(endDate) > 0) {
                            settingDialogEntity.setEndTime(minDate.getTime());
                        } else {
                            settingDialogEntity.setEndTime(endDate);
                        }
                    }
                });
            } else {
                StartTimeTextField.setDisable(true);
                EndTimeTextField.setDisable(true);

                //オフセット時間
                OffsetTimeTextField.setText(OFFSET_DEFAULT);
                settingDialogEntity.offsetTimeProperty().bind(OffsetTimeTextField.textProperty());
                OffsetTimeTextField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                    if (!event.getCharacter().matches(OFFSET_REGEX)) {
                        event.consume();
                    }
                });
            }
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        progressPane.setVisible(flg);
    }

}
