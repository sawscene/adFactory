/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ImportFormatFileUtil;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.importformat.HolidayFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 休日フォーマット変更画面
 *
 * @author nar-nakamura
 */
@FxComponent(id = "HolidayFormatChangeCompo", fxmlPath = "/fxml/admanaproductionnaviplugin/holiday_format_compo.fxml")
public class HolidayFormatChangeCompoController implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private static final int INPUT_MAX_SIZE = 3;

    @FXML
    private ComboBox<String> csvEncodeCombo;
    @FXML
    private TextField csvLineStartField;
    @FXML
    private TextField csvHolidayDateField;
    @FXML
    private TextField csvHolidayNameField;

    @FXML
    private TextField xlsSheetNameField;
    @FXML
    private TextField xlsLineStartField;
    @FXML
    private TextField xlsHolidayDateField;
    @FXML
    private TextField xlsHolidayNameField;

    @FXML
    private Pane progressPane;

    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initItem();
        this.loadSetting();
    }

    /**
     * 
     * @param block 
     */
    private void blockUI(Boolean block) {
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * 登録ボタンのアクション
     *
     * @param enent 
     */
    @FXML
    private void onEntryAction(ActionEvent enent) {
        logger.info("onEntryAction start.");
        boolean isCancel = false;
        try {
            blockUI(true);

            if (!this.isCheck()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.inputValidation"));
                isCancel = true;
                return;
            }

            final HolidayFormatInfo holidayFormatInfo = createHolidayFormatInfo();

            Task task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    // 現在のフォーマット設定を読み込む。
                    ImportFormatInfo importFormatInfo = ImportFormatFileUtil.load();
                    // 休日フォーマット設定を更新する。
                    importFormatInfo.setHolidayFormatInfo(holidayFormatInfo);
                    // 設定をファイルに保存する。
                    return ImportFormatFileUtil.save(importFormatInfo);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        if (this.getValue()) {
                            sc.setComponent("ContentNaviPane", "HolidayImportCompo");
                        } else {
                            logger.error("登録エラー");
                        }
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param enent 
     */
    @FXML
    private void onCancelAction(ActionEvent enent) {
        logger.info("onCancelAction start.");
        boolean chancelFlg = false;

        if (!chancelFlg) {
            sc.setComponent("ContentNaviPane", "HolidayImportCompo");
        }
    }

    /**
     * 設定情報読み込み処理
     */
    private void loadSetting() {
        logger.info("loadSetting");
        try {
            blockUI(true);

            Task task = new Task<ImportFormatInfo>() {
                @Override
                protected ImportFormatInfo call() throws Exception {
                    return ImportFormatFileUtil.load();
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        updateView(this.getValue());
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * アイテムを初期化する。
     */
    private void initItem() {
        try {
            String encodeDatas = properties.getProperty(ProductionNaviPropertyConstants.KEY_MASTER_CSV_FILE_ENCODE, ProductionNaviPropertyConstants.MASTER_CSV_FILE_ENCODE);
            if (!encodeDatas.isEmpty()) {
                String[] buff = encodeDatas.split(",");
                for (String encodeData : buff) {
                    this.csvEncodeCombo.getItems().add(encodeData);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面を更新する。
     *
     * @param importFormatInfo インポートフォーマット情報
     */
    private void updateView(ImportFormatInfo importFormatInfo) {
        this.dispHolidayFormatInfo(importFormatInfo.getHolidayFormatInfo());
    }

    /**
     * カンバン情報タブの表示を更新する。
     *
     * @param holidayFormatInfo カンバンのフォーマット情報
     */
    private void dispHolidayFormatInfo(HolidayFormatInfo holidayFormatInfo) {
        try {
            // エンコード
            this.csvEncodeCombo.setValue(holidayFormatInfo.getCsvFileEncode());
            // シート名
            this.xlsSheetNameField.setText(holidayFormatInfo.getXlsSheetName());

            // 読み込み開始行
            this.csvLineStartField.setText(holidayFormatInfo.getCsvStartRow());
            this.xlsLineStartField.setText(holidayFormatInfo.getXlsStartRow());
            // 休日
            this.csvHolidayDateField.setText(holidayFormatInfo.getCsvHolidayDate());
            this.xlsHolidayDateField.setText(holidayFormatInfo.getXlsHolidayDate());
            // 休日名
            this.csvHolidayNameField.setText(holidayFormatInfo.getCsvHolidayName());
            this.xlsHolidayNameField.setText(holidayFormatInfo.getXlsHolidayName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面の情報から休日インポートフォーマット設定を作成する。
     *
     * @return 休日インポートフォーマット設定
     */
    private HolidayFormatInfo createHolidayFormatInfo() {
        HolidayFormatInfo holidayFormatInfo = new HolidayFormatInfo();

        // エンコード
        holidayFormatInfo.setCsvFileEncode(this.csvEncodeCombo.getSelectionModel().getSelectedItem());
        // シート名
        holidayFormatInfo.setXlsSheetName(this.xlsSheetNameField.getText());

        // 読み込み開始行
        holidayFormatInfo.setCsvStartRow(this.csvLineStartField.getText());
        holidayFormatInfo.setXlsStartRow(this.xlsLineStartField.getText());
        // 休日
        holidayFormatInfo.setCsvHolidayDate(this.csvHolidayDateField.getText());
        holidayFormatInfo.setXlsHolidayDate(this.xlsHolidayDateField.getText());
        // 休日名
        holidayFormatInfo.setCsvHolidayName(this.csvHolidayNameField.getText());
        holidayFormatInfo.setXlsHolidayName(this.xlsHolidayNameField.getText());

        return holidayFormatInfo;
    }

    /**
     * 入力チェック
     *
     * @return 結果
     */
    private boolean isCheck() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.csvEncodeCombo)) {
            value = false;
        }

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.csvLineStartField)) {
            value = false;
        } else if (!this.withinRange(this.csvLineStartField.getText())) {
            ProductionNaviUtils.setFieldError(this.csvLineStartField);
            value = false;
        }

        // CSV形式：休日
        if (!ProductionNaviUtils.isNumber(this.csvHolidayDateField)) {
            value = false;
        } else if (!this.withinRange(this.csvHolidayDateField.getText())) {
            ProductionNaviUtils.setFieldError(this.csvHolidayDateField);
            value = false;
        }

        // CSV形式：休日名
        if (!ProductionNaviUtils.isNumber(this.csvHolidayNameField)) {
            value = false;
        } else if (!this.withinRange(this.csvHolidayNameField.getText())) {
            ProductionNaviUtils.setFieldError(this.csvHolidayNameField);
            value = false;
        }

        // Excel形式：シート名
        if (!ProductionNaviUtils.isNotNull(this.xlsSheetNameField)) {
            value = false;
        }

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.xlsLineStartField)) {
            value = false;
        } else if (!this.withinRange(this.xlsLineStartField.getText())) {
            ProductionNaviUtils.setFieldError(this.xlsLineStartField);
            value = false;
        }

        // Excel形式：休日
        if (!ProductionNaviUtils.isAlphabet(this.xlsHolidayDateField, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：休日名
        if (!ProductionNaviUtils.isAlphabet(this.xlsHolidayNameField, INPUT_MAX_SIZE)) {
            value = false;
        }

        return value;
    }

    /**
     * 全ての入力欄を通常表示にする。
     */
    private void setTextFieldNormal() {
        // エンコード
        ProductionNaviUtils.setFieldNormal(this.csvEncodeCombo);
        // シート名
        ProductionNaviUtils.setFieldNormal(this.xlsSheetNameField);

        // 読み込み開始行
        ProductionNaviUtils.setFieldNormal(this.csvLineStartField);
        ProductionNaviUtils.setFieldNormal(this.xlsLineStartField);
        // 休日
        ProductionNaviUtils.setFieldNormal(this.csvHolidayDateField);
        ProductionNaviUtils.setFieldNormal(this.xlsHolidayDateField);
        // 休日名
        ProductionNaviUtils.setFieldNormal(this.csvHolidayNameField);
        ProductionNaviUtils.setFieldNormal(this.xlsHolidayNameField);
    }

    /**
     * 範囲内か？
     *
     * @param value
     * @return 
     */
    private boolean withinRange(String value) {
        boolean result = false;
        try {
            Integer num = Integer.valueOf(value);
            if (num > 0 && num < 1000) {
                // 範囲内
                result = true;
            }
        } catch (NumberFormatException e) {
            // 数値以外
        }
        return result;
    }
}
