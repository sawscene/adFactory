/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateValidator;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import jp.adtekfuji.forfujiapp.common.AdFactoryForFujiClientAppConfig;
import jp.adtekfuji.forfujiapp.dialog.entity.SelectDialogEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitPropertyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplatePropertyInfoEntity;
import jp.adtekfuji.forfujiapp.javafx.record.factory.UnitPropertyRecordFactory;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.validator.StringValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット情報編集画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
@FxComponent(id = "UnitDetailDialog", fxmlPath = "/fxml/dialog/unitDetailDialog.fxml")
public class UnitDetailDialog implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final static AdFactoryForFujiClientAppConfig CONFIG = new AdFactoryForFujiClientAppConfig();
    //private final static String DATETIME_REGEX = "\\d|:|-|/|\\s";
    private static SimpleDateFormat TO_CONVERT_DATE_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private SelectDialogEntity<UnitTemplateInfoEntity> selectUnitTemplateData = null;
    private String beforeDate = "";
    private UnitInfoEntity unit = null;
    private final static String DEL = new String(new byte[]{0x7F});
    private final static String BS = "\b";
    private final static String NUM = "\\d";

    private final static int REGEX_NAME_NUMBER = 256;
    private final LinkedList<UnitPropertyInfoEntity> unitPropertyInfoEntities = new LinkedList<>();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label hierarchyNameLabel;
    @FXML
    private TextField unittemplateTextfield;
    @FXML
    private Button selectUnitTemplateButton;
    @FXML
    private TextField unitNameTextfield;
    @FXML
    private TextField outsetDateTextfiled;
    @FXML
    private TextField deliveryDateTextfiled;
    @FXML
    private VBox propertyFieldPane;
    @FXML
    private VBox progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TO_CONVERT_DATE_PATTERN = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        logger.info(UnitDetailDialog.class.getName() + ":initialize start");
        logger.info(UnitDetailDialog.class.getName() + ":initialize end");
    }

    @Override
    public void setArgument(Object argument) {
        logger.info(UnitDetailDialog.class.getName() + ":setArgument start");
        this.selectUnitTemplateButton.setVisible(false);
        if (argument instanceof UnitInfoEntity) {
            unit = (UnitInfoEntity) argument;

            // 詳細画面の表示
            this.createUnitView();
            // イベント登録.
            // createUnitViewの中にあると ユニットテンプレートを選んだ後も呼ばれて2重でイベント登録されるので外出し.
            this.setDateTextfieldEvents(outsetDateTextfiled);
            this.setDateTextfieldEvents(deliveryDateTextfiled);
            // バインド.
            // createUnitViewの中にあると ユニットテンプレートを選んだ後も呼ばれて2重でバインドされるので外出し.
            this.outsetDateTextfiled.textProperty().bindBidirectional(unit.startDatetimeProperty(), TO_CONVERT_DATE_PATTERN);
            this.deliveryDateTextfiled.textProperty().bindBidirectional(unit.compDatetimeProperty(), TO_CONVERT_DATE_PATTERN);
            // カスタムフィールド表示
            this.createCustamField();
        }
        logger.info(UnitDetailDialog.class.getName() + ":setArgument end");
    }

    /**
     * ユニットテンプレート選択ダイアログ表示処理
     *
     * @param event
     */
    @FXML
    public void onSelectUnitTemplate(ActionEvent event) {
        logger.info(UnitDetailDialog.class.getName() + ":onSelectUnitTemplat start");

        if (Objects.isNull(selectUnitTemplateData)) {
            selectUnitTemplateData = new SelectDialogEntity<>().uri(CONFIG.getAdFactoryForFujiServerAddress());
        }

        SelectDialogEntity<UnitTemplateInfoEntity> oldSelect = new SelectDialogEntity<>(selectUnitTemplateData);
        ButtonType type = sc.showComponentDialog(LocaleUtils.getString("key.UnitTemplateHierarchy"), "UnitTemplateSingleSelectionCompo", selectUnitTemplateData);

        if (type.equals(ButtonType.OK) && Objects.nonNull(selectUnitTemplateData.getItem())) {
            try {
                this.blockUI(true);

                long unitTemplateId = selectUnitTemplateData.getItem().getUnitTemplateId();

                UnitTemplateInfoEntity entity = RestAPI.getUnitTemplate(unitTemplateId);

                this.unit.setFkUnitTemplateId(entity.getUnitTemplateId());
                this.unit.setUnitTemplateName(entity.getUnitTemplateName());
                this.unit.setWorkflowDiaglam(entity.getWorkflowDiaglam());
                this.unit.setCompDatetime(new Date(unit.getStartDatetime().getTime() + entity.getTactTime()));

                this.unit.getUnitPropertyCollection().clear();
                for (UnitTemplatePropertyInfoEntity property : entity.getUnitTemplatePropertyCollection()) {
                    unit.getUnitPropertyCollection().add(new UnitPropertyInfoEntity(null, null, property.getUnitTemplatePropertyName(), property.getUnitTemplatePropertyType(), property.getUnitTemplatePropertyValue(), property.getUnitTemplatePropertyOrder()));
                }

                this.createUnitView();
                this.createCustamField();
            } finally {
                this.blockUI(false);
            }
        } else if (type.equals(ButtonType.CLOSE) || type.equals(ButtonType.CANCEL) || type.equals(ButtonType.NO)) {
            selectUnitTemplateData = oldSelect;
        }

        logger.info(UnitDetailDialog.class.getName() + ":onSelectUnitTemplat end");
    }

    /**
     * ユニット新規作成画面構築
     *
     */
    private void createUnitView() {
        // 編集の場合工程選択は無効
        if (Objects.isNull(unit.getUnitId())) {
            this.selectUnitTemplateButton.setVisible(true);
            this.unittemplateTextfield.setEditable(false);
        }
        this.hierarchyNameLabel.setText(unit.getParentName());
        // 生産ユニット名がない場合はから文字
        if (Objects.isNull(unit.getUnitName())) {
            this.unit.setUnitName("");
        }
        StringValidator.bindValidator(unitNameTextfield, unit.unitNameProperty()).setMaxChars(REGEX_NAME_NUMBER);
        this.unitNameTextfield.textProperty().bindBidirectional(unit.unitNameProperty());
        // ユニットテンプレートの選択がない場合はから文字
        if (Objects.isNull(unit.getUnitTemplateName())) {
            this.unit.setUnitTemplateName("");
        }
        this.unittemplateTextfield.textProperty().bindBidirectional(unit.unitTemplateNameProperty());
        // 開始日の設定がない場合は今現在の日付
        if (Objects.isNull(unit.getStartDatetime())) {
            this.unit.setStartDatetime(new Date());
        }
        this.outsetDateTextfiled.setText(TO_CONVERT_DATE_PATTERN.format(unit.getStartDatetime()));

        // 終了日の設定がない場合は今現在の日付
        if (Objects.isNull(unit.getCompDatetime())) {
            this.unit.setCompDatetime(new Date());
        }
        this.deliveryDateTextfiled.setText(TO_CONVERT_DATE_PATTERN.format(unit.getCompDatetime()));
    }

    /**
     * カスタムフィールドの生成処理
     *
     */
    private void createCustamField() {
        if (!Objects.nonNull(unit.getUnitPropertyCollection())) {
            unit.setUnitPropertyCollection(new LinkedList<>());
        }
        propertyFieldPane.getChildren().clear();
        unitPropertyInfoEntities.clear();
        unitPropertyInfoEntities.addAll(unit.getUnitPropertyCollection());
        unitPropertyInfoEntities.sort((Comparator.comparing(kanban -> kanban.getUnitPropertyOrder())));

        Table customPropertyTable = new Table(propertyFieldPane.getChildren()).isAddRecord(true)
                .isColumnTitleRecord(true).title(LocaleUtils.getString("key.CustomField")).styleClass("ContentTitleLabel");
        customPropertyTable.setAbstractRecordFactory(new UnitPropertyRecordFactory(customPropertyTable, unitPropertyInfoEntities));

        //追加情報Orderを更新
        scrollPane.setOnMouseExited((MouseEvent me) -> {
            unit.getUnitPropertyCollection().clear();
            int order = 0;
            for (UnitPropertyInfoEntity entity : unitPropertyInfoEntities) {
                entity.updateMember();
                entity.setUnitPropertyOrder(order);
                unit.getUnitPropertyCollection().add(entity);
                order = order + 1;
            }
        });
    }

    /**
     * 日付のテキストフィールドにバリデーション該当時のイベントを注入
     *
     * @param field 日付入力の必要なテキストフィールド
     */
    private void setDateTextfieldEvents(TextField field) {

        field.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            // delキーを押されたとき.カーソル位置の後ろが数字なら0にする.カーソルを後ろにずらす.
            if (event.getCharacter().matches(DEL)) {
                DelKeyEvent(field);
                return;
            }
            // bsキーを押されたとき.カーソル位置の前が数字なら0にする.カーソルを前にずらす.
            if (event.getCharacter().matches(BS)) {
                BSKeyEvent(field);
                return;
            }
            // 数字キーが押されたとき.カーソル位置の後ろの数字を押された数字にする.
            if (event.getCharacter().matches(NUM)) {
                NumKeyEvent(field, event.getCharacter().charAt(0));
            }
            // イベント消費.
            event.consume();
        });

        field.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                beforeDate = field.getText();
            } else if (!DateValidator.isValid(field.getText(), LocaleUtils.getString("key.DateTimeFormat"))) {
                field.setText(beforeDate);
            }
        });
    }

    /**
     * Delキーが押されたときの動作.
     *
     * @param field 日付入力の必要なテキストフィールド
     */
    private void DelKeyEvent(TextField field) {

        StringBuilder sb = new StringBuilder(field.getText());
        int cp = field.getCaretPosition();

        //  2 0 1 7 / 0 6 / 0 8   0 8 : 3 5 : 0 0
        // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 ← cpの位置によって動作を変える.cp(カレットポジション=カーソル位置)
        switch (cp) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
            case 17:
            case 18:
                sb.insert(cp, '0');
                field.setText(sb.toString());
                field.positionCaret(cp + 1);
                break;
            case 3:
            case 6:
            case 9:
            case 12:
            case 15:
                sb.insert(cp, '0');
                field.setText(sb.toString());
                field.positionCaret(cp + 2);
                break;
            case 4:
            case 7:
            case 10:
            case 13:
            case 16:
                sb.insert(cp - 1, '0');
                field.setText(sb.toString());
                field.positionCaret(cp + 1);
                break;
            case 19:
            default:
                break;
        }
    }

    /**
     * BSキーが押されたときの動作.
     *
     * @param field 日付入力の必要なテキストフィールド
     */
    private void BSKeyEvent(TextField field) {

        StringBuilder sb = new StringBuilder(field.getText());
        int cp = field.getCaretPosition();

        //  2 0 1 7 / 0 6 / 0 8   0 8 : 3 5 : 0 0
        // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 ← cpの位置によって動作を変える.cp(カレットポジション=カーソル位置)
        switch (cp) {
            case 0:
                if (19 == sb.length()) {
                    break;
                }
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 11:
            case 12:
            case 14:
            case 15:
            case 17:
            case 18:
                sb.insert(cp, '0');
                field.setText(sb.toString());
                field.positionCaret(cp);
                break;
            case 4:
            case 7:
                sb.insert(cp, '/');
                field.setText(sb.toString());
                field.positionCaret(cp);
                break;
            case 10:
                sb.insert(cp, ' ');
                field.setText(sb.toString());
                field.positionCaret(cp);
                break;
            case 13:
            case 16:
                sb.insert(cp, ':');
                field.setText(sb.toString());
                field.positionCaret(cp);
                break;
            default:
                sb.insert(cp, '0');
                field.setText(sb.toString());
                field.positionCaret(cp);
                break;
        }
    }

    /**
     * Numキーが押されたときの動作.
     *
     * @param field 日付入力の必要なテキストフィールド
     */
    private void NumKeyEvent(TextField field, char numKey) {

        StringBuilder sb = new StringBuilder(field.getText());
        int cp = field.getCaretPosition();

        //  2 0 1 7 / 0 6 / 0 8   0 8 : 3 5 : 0 0
        // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 ← cpの位置によって動作を変える.cp(カレットポジション=カーソル位置)
        switch (cp) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
            case 17:
            case 18:
                sb.setCharAt(cp, numKey);
                field.setText(sb.toString());
                field.positionCaret(cp + 1);
                break;
            case 3:
            case 6:
            case 9:
            case 12:
            case 15:
                sb.setCharAt(cp, numKey);
                field.setText(sb.toString());
                field.positionCaret(cp + 2);
                break;
            case 4:
            case 7:
            case 10:
            case 13:
            case 16:
            case 19:
                sb.setCharAt(cp - 1, numKey);
                field.setText(sb.toString());
                field.positionCaret(cp + 1);
                break;
            default:
                sb.setCharAt(18, numKey);
                field.setText(sb.toString());
                field.positionCaret(19);
                break;
        }
    }

    /**
     * ダイアログを待機状態にする。
     *
     * @param block
     */
    private void blockUI(boolean block) {
        this.scrollPane.setDisable(block);
        this.progressPane.setVisible(block);
    }
}
