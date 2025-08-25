/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.property;

import adtekfuji.admanagerapp.workfloweditplugin.common.Constants;
import adtekfuji.admanagerapp.workfloweditplugin.common.XmlSerializer;
import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceCustomEntity;
import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceOptionEntity;
import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceSettingEntity;
import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AccessoryFieldTypeEnum;
import jp.adtekfuji.adFactory.enumerate.AlignmentType;
import jp.adtekfuji.adFactory.enumerate.BulkType;
import jp.adtekfuji.adFactory.enumerate.KeyboardType;
import jp.adtekfuji.adFactory.enumerate.TraceOptionTypeEnum;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.javafxcommon.controls.InputValueColor;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * オプションセルコントロール
 *
 * @author s-heya
 */
public class CellOption extends AbstractCell {

    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * コンバーター
     */
    StringConverter<String> converter = new StringConverter<String>() {
        @Override
        public String fromString(String str) {
            return null;
        }

        @Override
        public String toString(String str) {
            StringBuilder sb = new StringBuilder();

            String[] fields = new String[]{};
            if (!StringUtils.isEmpty(str)) {
                try {
                    TraceSettingEntity traceSetting = (TraceSettingEntity) XmlSerializer.deserialize(TraceSettingEntity.class, str);
                    if (Objects.nonNull(traceSetting)) {
                        
                        boolean exitKeyboardType = false;
                        
                        for (TraceOptionTypeEnum key : traceSetting.getKeys()) {
                            switch (key) {
                                case FIELDS:
                                    String fieldsValue = traceSetting.getValue(TraceOptionTypeEnum.FIELDS.toString());
                                    if (!StringUtils.isEmpty(fieldsValue)) {
                                        fields = fieldsValue.split("\\|", 0);
                                    }   
                                    break;
                                // 管理番号の各識別名はチェックの有無にかかわらず存在するがチェックがない場合はボタンに表示しない
                                case REFERENCE_NUMBER:
                                    break;
                                // 項目数はカスタムフィールドの子要素のため表示する必要はない
                                case FIELD_SIZE:
                                    break;
                                case TEN_KEYBOARD:
                                    if (WorkPropertyCategoryEnum.CUSTOM.equals(workProperty.workPropCategoryProperty().get())) {
                                        // カスタムの時はキーボード入力をボタンに表示
                                        sb.append(LocaleUtils.getString("key.InputKeyboard"));
                                    } else {
                                        // 測定の時はテンキー入力をボタンに表示
                                        sb.append(LocaleUtils.getString("key.InputTenKeyboard"));
                                    }   sb.append(",");
                                    break;
                                case KEYBOARD_TYPE:
                                    exitKeyboardType = true;
                                    break;
                                default:
                                    String name = LocaleUtils.getString(key.getResourceKey());
                                    if (!StringUtils.isEmpty(name)) {
                                        sb.append(name);
                                        sb.append(",");
                                    }   break;
                            }
                        }
                        
                        if (WorkPropertyCategoryEnum.CUSTOM.equals(workProperty.workPropCategoryProperty().get()) && !exitKeyboardType) {
                            sb.append(LocaleUtils.getString("key.InputKeyboard"));
                            sb.append(",");
                        }
                        
                        // カスタム設定値リスト
                        String traceCustomString = LocaleUtils.getString("key.TraceCustoms");
                        if (WorkPropertyCategoryEnum.PRODUCT.equals(workProperty.workPropCategoryProperty().get())) {
                            // 完成品種別の場合 「構成部品」という文字列をオプションボタンに追加
                            // (カスタム設定値リストと同じところに構成部品データを格納したため)
                            traceCustomString = LocaleUtils.getString("key.Component");
                        }
                        if (!traceSetting.getTraceCustoms().isEmpty()) {
                            sb.append(traceCustomString);
                            sb.append(",");
                        }
                    }
                } catch (Exception ex1) {
                    try {
                        fields = str.split("\\|", 0);
                    } catch (Exception ex2) {
                        logger.fatal(ex2, ex2);
                    }
                }
            } else {
                if (WorkPropertyCategoryEnum.CUSTOM.equals(workProperty.workPropCategoryProperty().get())) {
                    sb.append(LocaleUtils.getString("key.InputKeyboard"));
                    sb.append(",");
                }
            }

            for (String field : fields) {
                if (AccessoryFieldTypeEnum.LOT.equals(field)) {
                    sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.LOT.getResourceKey()));
                    sb.append(",");
                } else if (AccessoryFieldTypeEnum.SERIAL.equals(field)) {
                    sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.SERIAL.getResourceKey()));
                    sb.append(",");
                } else if (AccessoryFieldTypeEnum.QUANTITY.equals(field)) {
                    sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.QUANTITY.getResourceKey()));
                    sb.append(",");
                } else if (AccessoryFieldTypeEnum.EQUIPMENT.equals(field)) {
                    sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.EQUIPMENT.getResourceKey()));
                    sb.append(",");
                } else if (AccessoryFieldTypeEnum.CUSTOM.equals(field)) {
                    sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.CUSTOM.getResourceKey()));
                    sb.append(",");
                } else if (AccessoryFieldTypeEnum.PARTS_ID.equals(field)) {
                    sb.append(LocaleUtils.getString(AccessoryFieldTypeEnum.PARTS_ID.getResourceKey()));
                    sb.append(",");
                }
            }

            if (0 < sb.length()) {
                int index = sb.lastIndexOf(",");
                sb.deleteCharAt(index);
            }

            return sb.toString();
        }
    };

    /**
     * コンバーター(整数値・文字列)
     */
    StringConverter<Number> converterInt = new StringConverter<Number>() {
        @Override
        public Number fromString(String str) {
            try {
                int value = Integer.parseInt(str);
                return value;
            } catch (Exception ex) {
                return 0;
            }
        }

        @Override
        public String toString(Number value) {
            try {
                return String.valueOf(value);
            } catch (Exception ex) {
                return "0";
            }
        }
    };

    private final Logger logger = LogManager.getLogger();
    private final WorkPropertyInfoEntity workProperty;
    private MenuButton popupButton;
    private Popup popup;
    private final ResourceBundle rb;
    private final CheckBox lotNumberCheckBox;
    private final CheckBox serialNumberCheckBox;
    private final CheckBox quantityCheckBox;
    private final CheckBox referenceNumberCheckBox;
    private final StringProperty referenceNumberProperty = new SimpleStringProperty();
    private final CheckBox customCheckBox;
    private final CheckBox optionForbitEmptyDataCheckBox;
    private final CheckBox optionInputListOnly;
    private final CheckBox partsIDCheckBox;
    private final CheckBox tenKeyboardCheckBox;
    private final CheckBox optionBulkInputCheckBox; // 製品単位に入力する(一括検査入力)
    private final ComboBox<BulkType> bulkTypeComboBox;
    private final ObjectProperty<BulkType> bulkTypeProperty = new SimpleObjectProperty<>();

    // INSPECTION
    private final CheckBox optionAttachFileCheckBox; // ファイルを添付する
    private final CheckBox inputTextCheckBox;       // コメントを入力する
    private final CheckBox displayTextCheckBox;     // 後工程にコメントを表示する

    // CUSTOM
    private final CheckBox optionPluginCheckBox;
    private final StringProperty optionPluginProperty = new SimpleStringProperty();
    private final IntegerProperty customFieldSizeProperty = new SimpleIntegerProperty(1);
    private final CheckBox optionDelimiterCheckBox;
    private final StringProperty optionDelimiterProperty = new SimpleStringProperty(DEFAULT_DELIMITER);
    private final CheckBox optionHoldPrevDataCheckBox;
    private final CheckBox optionForbitRepeatedDataCheckBox;
    private final CheckBox optionCheckBarcodeCheckBox;
    private final CheckBox optionInputProductNumCheckBox;
    private final CheckBox optionInputKeyboardCheckBox;
    private final ObjectProperty<KeyboardType> keyboardTypeProperty = new SimpleObjectProperty<>();
    private final CheckBox optionWorkCheckBox;
    private final StringProperty optionWorkProperty = new SimpleStringProperty();
    private final CheckBox optionWorkPropCheckBox;
    private final StringProperty optionWorkPropProperty = new SimpleStringProperty();
    private final CheckBox readQRByCameraCheckBox;

    // LIST
    private final CheckBox optionCountCheckBox;
    private final IntegerProperty optionCountProperty = new SimpleIntegerProperty(0);

    // MEASURE
    private final CheckBox integerDigitsCheckBox;
    private final IntegerProperty integerDigitsProperty = new SimpleIntegerProperty(0);
    private final CheckBox decimalDigitsCheckBox;
    private final IntegerProperty decimalDigitsProperty = new SimpleIntegerProperty(0);
    private final CheckBox absoluteCheckBox;

    // 管理番号として選択した設備
    private final List<String> selectedEquipments = new ArrayList();

    // 入力値リスト
    private final Button valueListButton;
    private List<String> valueList = new LinkedList<>();
    
    private final ComboBox<AlignmentType> alignmentComboBox; // 入力値リストの文字レイアウト機能追加対応
    private final ObjectProperty<AlignmentType> alignmentTypeProperty = new SimpleObjectProperty<>(); // 入力値リストの文字レイアウト機能追加対応

    // 色付入力値リスト
    private final Button valueListButton2;
    private List<InputValueColor> colorValueList = new LinkedList<>();
    
    // カスタム設定値リスト (複数のカスタム値(基準値)を設定できるようにする)
    private final Button traceCustomsButton;
    private List<TraceCustomEntity> traceCustoms = new ArrayList();

    private final Button componentButton;

    private static final String CHARSET = "UTF-8";
    private static final String DEFAULT_DELIMITER = "\\r\\n";

    private boolean initialValidInteger;
    private boolean initialValidDecimal;

    /**
     * コンストラクタ
     *
     * @param record
     * @param workProperty
     * @param rb
     */
    public CellOption(Record record, WorkPropertyInfoEntity workProperty, ResourceBundle rb) {
        super(record);
        this.workProperty = workProperty;
        this.rb = rb;
        this.lotNumberCheckBox = new CheckBox(this.rb.getString(AccessoryFieldTypeEnum.LOT.getResourceKey()));
        this.lotNumberCheckBox.getStyleClass().add("text-normal-bold");
        this.serialNumberCheckBox = new CheckBox(this.rb.getString(AccessoryFieldTypeEnum.SERIAL.getResourceKey()));
        this.serialNumberCheckBox.getStyleClass().add("text-normal-bold");
        this.quantityCheckBox = new CheckBox(this.rb.getString(AccessoryFieldTypeEnum.QUANTITY.getResourceKey()));
        this.quantityCheckBox.getStyleClass().add("text-normal-bold");
        this.referenceNumberCheckBox = new CheckBox(this.rb.getString(AccessoryFieldTypeEnum.EQUIPMENT.getResourceKey()));
        this.referenceNumberCheckBox.getStyleClass().add("text-normal-bold");
        this.customCheckBox = new CheckBox(this.rb.getString(AccessoryFieldTypeEnum.CUSTOM.getResourceKey()));
        this.customCheckBox.getStyleClass().add("text-normal-bold");
        this.optionForbitEmptyDataCheckBox = new CheckBox(this.rb.getString("key.ForbitEmptyData"));
        this.optionForbitEmptyDataCheckBox.getStyleClass().add("text-normal-bold");
        this.optionInputListOnly = new CheckBox(this.rb.getString("key.InputListOnly"));
        this.optionInputListOnly.getStyleClass().add("text-normal-bold");
        this.partsIDCheckBox = new CheckBox(this.rb.getString(AccessoryFieldTypeEnum.PARTS_ID.getResourceKey()));
        this.partsIDCheckBox.getStyleClass().add("text-normal-bold");
        this.tenKeyboardCheckBox = new CheckBox(this.rb.getString("key.InputTenKeyboard"));
        this.tenKeyboardCheckBox.getStyleClass().add("text-normal-bold");
        this.optionAttachFileCheckBox = new CheckBox(this.rb.getString("key.AttachFile"));
        this.optionAttachFileCheckBox.getStyleClass().add("text-normal-bold");
        this.inputTextCheckBox = new CheckBox(this.rb.getString("inputText"));
        this.inputTextCheckBox.getStyleClass().add("text-normal-bold");
        this.displayTextCheckBox = new CheckBox(this.rb.getString("displayText"));
        this.displayTextCheckBox.getStyleClass().add("text-normal-bold");
        this.optionBulkInputCheckBox = new CheckBox(this.rb.getString("key.BulkInputOption"));
        this.optionBulkInputCheckBox.getStyleClass().add("text-normal-bold");
        this.bulkTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(BulkType.values()));
        this.bulkTypeComboBox.setPrefWidth(200.0);
        this.bulkTypeComboBox.getStyleClass().add("text-normal-bold");

        // CUSTOM
        this.optionPluginCheckBox = new CheckBox(this.rb.getString("key.PluginName"));// プラグイン名
        this.optionPluginCheckBox.getStyleClass().add("text-normal-bold");
        this.optionDelimiterCheckBox = new CheckBox(this.rb.getString("key.InputDelimiter"));
        this.optionDelimiterCheckBox.getStyleClass().add("text-normal-bold");
        this.optionHoldPrevDataCheckBox = new CheckBox(this.rb.getString("key.HoldPreviousData"));
        this.optionHoldPrevDataCheckBox.getStyleClass().add("text-normal-bold");
        this.optionForbitRepeatedDataCheckBox = new CheckBox(this.rb.getString("key.ForbitRepeatedData"));
        this.optionForbitRepeatedDataCheckBox.getStyleClass().add("text-normal-bold");
        this.optionCheckBarcodeCheckBox = new CheckBox(this.rb.getString("key.CheckBarcode"));
        this.optionCheckBarcodeCheckBox.getStyleClass().add("text-normal-bold");
        this.optionInputProductNumCheckBox = new CheckBox(this.rb.getString("key.InputProductNum"));
        this.optionInputProductNumCheckBox.getStyleClass().add("text-normal-bold");
        this.optionInputKeyboardCheckBox = new CheckBox(this.rb.getString("key.InputKeyboard")); // キーボード入力
        this.optionInputKeyboardCheckBox.setPrefWidth(200.0);
        this.optionInputKeyboardCheckBox.getStyleClass().add("text-normal-bold");
        this.readQRByCameraCheckBox = new CheckBox(this.rb.getString("key.ReadQRByCamera"));// カメラでQRコードを読み取る
        this.readQRByCameraCheckBox.getStyleClass().add("text-normal-bold");
        
        // TIMER
        this.optionWorkCheckBox = new CheckBox(this.rb.getString("key.Process"));// 工程
        this.optionWorkCheckBox.getStyleClass().add("text-normal-bold");
        this.optionWorkPropCheckBox = new CheckBox(this.rb.getString("key.Tag"));// 工程プロパティ(タグ)
        this.optionWorkPropCheckBox.getStyleClass().add("text-normal-bold");

        // LIST
        this.optionCountCheckBox = new CheckBox(this.rb.getString("key.RowCount"));// 行数
        this.optionCountCheckBox.getStyleClass().add("text-normal-bold");

        // MEASURE
        this.integerDigitsCheckBox = new CheckBox(this.rb.getString("key.IntegerDigits"));
        this.integerDigitsCheckBox.getStyleClass().add("text-normal-bold");
        this.decimalDigitsCheckBox = new CheckBox(this.rb.getString("key.DecimalDigits"));
        this.decimalDigitsCheckBox.getStyleClass().add("text-normal-bold");
        this.absoluteCheckBox = new CheckBox(this.rb.getString("key.AbsoluteDisplay"));
        this.absoluteCheckBox.getStyleClass().add("text-normal-bold");

        this.valueListButton = new Button(this.rb.getString("key.ValueList"));
        this.valueListButton.setOnAction((event) -> {
            // 値リストダイアログを表示
            ObservableList<String> list = FXCollections.observableArrayList(this.valueList);
            ButtonType result = sc.showComponentDialog(rb.getString("key.ValueList"), "ValueListCompo", list, new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
            if (ButtonType.OK == result) {
                this.valueList.clear();
                this.valueList.addAll(list);
                this.update();
            }
            //this.showPopup(popupButton, popup);
        });
        
        // 入力値リストの文字レイアウト機能追加対応
        this.alignmentComboBox = new ComboBox<>(FXCollections.observableArrayList(AlignmentType.values()));
        this.alignmentComboBox.setPrefWidth(200.0);
        this.alignmentComboBox.getStyleClass().add("text-normal-bold");

        // 色付入力値リスト追加対応
        this.valueListButton2 = new Button(this.rb.getString("key.ColorValueList"));
        this.valueListButton2.setOnAction((event) -> {
            // 値リストダイアログを表示
            ObservableList<InputValueColor> list = FXCollections.observableArrayList(this.colorValueList);
            ButtonType result = sc.showComponentDialog(rb.getString("key.ColorValueList"), "ColorValueListCompo", list, new ButtonType[]{ButtonType.OK, ButtonType.CLOSE});
            if (ButtonType.OK == result) {
                this.colorValueList.clear();
                this.colorValueList.addAll(list);
                this.update();
            }
        });  
        
        // カスタム設定値リストボタン
        this.traceCustomsButton = new Button(rb.getString("key.TraceCustoms"));
        this.traceCustomsButton.setOnAction((event) -> {
            ObservableList<TraceCustomEntity> list = FXCollections.observableArrayList(this.traceCustoms);
            ButtonType result = sc.showComponentDialog(rb.getString("key.TraceCustoms"), "TraceCustomListCompo", list, new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
            if (ButtonType.OK == result) {
                this.traceCustoms.clear();
                this.traceCustoms.addAll(list);
                this.update();
            }
        });

        // 構成部品ボタン
        this.componentButton = new Button(this.rb.getString("key.Component"));
        this.componentButton.getStyleClass().add("text-normal-bold");
        this.componentButton.setOnAction((event) -> {
            ObservableList<TraceCustomEntity> list = FXCollections.observableArrayList(this.traceCustoms);
            ButtonType result = sc.showComponentDialog(rb.getString("key.Component"), "ComponentCompo", list, new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
            if (ButtonType.OK == result) {
                this.traceCustoms.clear();
                this.traceCustoms.addAll(list);
                this.update();
            }
        });
    }

    /**
     * ノードを生成する
     */
    @Override
    public void createNode() {
        this.popupButton = new MenuButton();
        this.popupButton.setPrefWidth(200.0);
        this.popupButton.getStyleClass().add("ContentTextBox");
        Bindings.bindBidirectional(popupButton.textProperty(), this.workProperty.workPropOptionProperty(), converter);

        if (WorkPropertyCategoryEnum.JUDG.equals(this.workProperty.workPropCategoryProperty().get())) {
            this.popupButton.setDisable(true);
        } else {
            this.popupButton.setDisable(false);
        }

        workProperty.workPropCategoryProperty().addListener(new ChangeListener<WorkPropertyCategoryEnum>() {
            @Override
            public void changed(ObservableValue<? extends WorkPropertyCategoryEnum> observable, WorkPropertyCategoryEnum oldValue, WorkPropertyCategoryEnum newValue) {
                workProperty.setWorkPropOption(null);
                lotNumberCheckBox.setSelected(false);
                serialNumberCheckBox.setSelected(false);
                quantityCheckBox.setSelected(false);
                referenceNumberCheckBox.setSelected(false);
                customCheckBox.setSelected(false);
                optionForbitEmptyDataCheckBox.setSelected(false);
                optionInputListOnly.setSelected(false);
                partsIDCheckBox.setSelected(false);
                tenKeyboardCheckBox.setSelected(false);
                optionAttachFileCheckBox.setSelected(false);
                inputTextCheckBox.setSelected(false);
                displayTextCheckBox.setSelected(false);
                optionBulkInputCheckBox.setSelected(false);
                bulkTypeProperty.set(BulkType.BULK_TYPE_SEQUENTIAL);

                // CUSTOM
                optionPluginCheckBox.setSelected(false);
                optionPluginProperty.setValue(null);
                customFieldSizeProperty.setValue(1);
                optionDelimiterCheckBox.setSelected(false);
                optionDelimiterProperty.setValue(DEFAULT_DELIMITER);
                optionHoldPrevDataCheckBox.setSelected(false);
                optionForbitRepeatedDataCheckBox.setSelected(false);
                optionCheckBarcodeCheckBox.setSelected(false);
                optionInputProductNumCheckBox.setSelected(false);
                optionInputKeyboardCheckBox.setSelected(true); // Ver.2.1.15 以前の動作に合わせて、キーボード入力はONにする
                keyboardTypeProperty.set(KeyboardType.TEN_KEYBOARD);
                alignmentTypeProperty.set(AlignmentType.ALIGNMENT_CENTER); // 入力値リストの文字レイアウト機能追加対応
                readQRByCameraCheckBox.setSelected(false);
                // TIMER
                optionWorkCheckBox.setSelected(false);
                optionWorkProperty.setValue(null);
                optionWorkPropCheckBox.setSelected(false);
                optionWorkPropProperty.setValue(null);
                // LIST
                optionCountCheckBox.setSelected(false);
                optionCountProperty.setValue(null);
                // MEASURE
                integerDigitsCheckBox.setSelected(false);
                integerDigitsProperty.setValue(null);
                decimalDigitsCheckBox.setSelected(false);
                decimalDigitsProperty.setValue(null);
                absoluteCheckBox.setSelected(false);
                // 値リスト
                valueList.clear();

                // 色付き入力値リスト
                colorValueList.clear();

                // カスタム設定値リスト
                traceCustoms.clear();

                if (WorkPropertyCategoryEnum.JUDG.equals(newValue)) {
                    popupButton.setDisable(true);
                } else {
                    popupButton.setDisable(false);
                }

                workProperty.updateMember();
                
                update();
            }
        });

        popupButton.setOnMouseClicked(event -> {
            this.popup = new Popup();
            this.popup.setAutoHide(true);
            this.popup.setHideOnEscape(true);
            this.popup.setAutoFix(true);
            this.popup.getContent().addAll(createContent(workProperty.workPropCategoryProperty().get()));

            CellOption.this.showPopup(popupButton, popup);

            this.popup.setOnHidden((WindowEvent event1) -> {
                this.update();
            });
        });

        // commonのsetNodeのdisable設定が自分自身であるため、初期設定内容を自分自身に反映
        this.setDisable(popupButton.isDisable());
        super.setNode(this.popupButton);
    }

    /**
     * コンテンツを生成する
     *
     * @param category プロパティ種別
     * @return VBox
     */
    private VBox createContent(WorkPropertyCategoryEnum category) {
        VBox vBox = new VBox(5);
        vBox.getStyleClass().add("context-menu");
        vBox.setPadding(new Insets(10, 10, 10, 10));

        String optionString = this.workProperty.getWorkPropOption();

        TraceSettingEntity traceSetting = null;
        try {
            if (Objects.isNull(optionString) || optionString.isEmpty()) {
                traceSetting = new TraceSettingEntity();
            } else {
                traceSetting = (TraceSettingEntity) XmlSerializer.deserialize(TraceSettingEntity.class, optionString);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        boolean exitKeyboardType = false;

        if (Objects.nonNull(traceSetting)) {
            if (WorkPropertyCategoryEnum.CUSTOM.equals(category)
                    || WorkPropertyCategoryEnum.TIMER.equals(category)
                    || WorkPropertyCategoryEnum.LIST.equals(category)) {
                // チェックボックスとテキストボックス
                for (TraceOptionEntity traceOption : traceSetting.getTraceOptions()) {
                    if (TraceOptionTypeEnum.PLUGIN.equals(traceOption.getKey())) {
                        this.optionPluginCheckBox.setSelected(true);
                        this.optionPluginProperty.setValue(traceOption.getValue());
                    } else if (TraceOptionTypeEnum.WORK.equals(traceOption.getKey())) {
                        this.optionWorkCheckBox.setSelected(true);
                        this.optionWorkProperty.setValue(traceOption.getValue());
                    } else if (TraceOptionTypeEnum.PROPERTY.equals(traceOption.getKey())) {
                        this.optionWorkPropCheckBox.setSelected(true);
                        this.optionWorkPropProperty.setValue(traceOption.getValue());
                    } else if (TraceOptionTypeEnum.COUNT.equals(traceOption.getKey())) {
                        this.optionCountCheckBox.setSelected(true);
                        this.optionCountProperty.setValue(Integer.valueOf(traceOption.getValue()));
                    } else if (TraceOptionTypeEnum.FIELDS.equals(traceOption.getKey())) {
                        this.customCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.VALUE_LIST.equals(traceOption.getKey())) {
                        this.valueList.clear();
                        this.valueList.addAll(traceOption.getValues());
                    } else if (TraceOptionTypeEnum.ALIGNMENT.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応
                        this.alignmentTypeProperty.set(null);
                        this.alignmentComboBox.getSelectionModel().clearSelection();
                    } else if (TraceOptionTypeEnum.ALIGNMENT_LEFT.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応 左
                        this.alignmentTypeProperty.set(AlignmentType.ALIGNMENT_LEFT);
                        this.alignmentComboBox.getSelectionModel().select(AlignmentType.ALIGNMENT_LEFT);
                    } else if (TraceOptionTypeEnum.ALIGNMENT_CENTER.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応 中央
                        this.alignmentTypeProperty.set(AlignmentType.ALIGNMENT_CENTER);
                        this.alignmentComboBox.getSelectionModel().select(AlignmentType.ALIGNMENT_CENTER);
                    } else if (TraceOptionTypeEnum.ALIGNMENT_RIGHT.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応 右
                        this.alignmentTypeProperty.set(AlignmentType.ALIGNMENT_RIGHT);
                        this.alignmentComboBox.getSelectionModel().select(AlignmentType.ALIGNMENT_RIGHT);
                    } else if (TraceOptionTypeEnum.COLOR_VALUE_LIST.equals(traceOption.getKey())) {
                        this.colorValueList.clear();
                        this.colorValueList.addAll(traceOption.getColorTextBkValues());
                    } else if (TraceOptionTypeEnum.DELIMITER.equals(traceOption.getKey())) {
                        this.optionDelimiterCheckBox.setSelected(true);
                        this.optionDelimiterProperty.setValue(traceOption.getValue());
                    } else if (TraceOptionTypeEnum.FIELD_SIZE.equals(traceOption.getKey())) {
                        this.customFieldSizeProperty.setValue(Integer.valueOf(traceOption.getValue()));
                    } else if (TraceOptionTypeEnum.HOLD_PREV_DATA.equals(traceOption.getKey())) {
                        this.optionHoldPrevDataCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.CHECK_UNIQUE.equals(traceOption.getKey())) {
                        this.optionForbitRepeatedDataCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.CHECK_EMPTY.equals(traceOption.getKey())) {
                        this.optionForbitEmptyDataCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.INPUT_LIST_ONLY.equals(traceOption.getKey())) {
                        this.optionInputListOnly.setSelected(true);
                    } else if (TraceOptionTypeEnum.BULK_INPUT.equals(traceOption.getKey())) {
                        this.optionBulkInputCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.BULK_TYPE_SEQUENTIAL.equals(traceOption.getKey())) {
                        this.bulkTypeProperty.set(BulkType.BULK_TYPE_SEQUENTIAL);
                        this.bulkTypeComboBox.getSelectionModel().select(BulkType.BULK_TYPE_SEQUENTIAL);
                    } else if (TraceOptionTypeEnum.BULK_TYPE_GROUPING.equals(traceOption.getKey())) {
                        this.bulkTypeProperty.set(BulkType.BULK_TYPE_GROUPING);
                        this.bulkTypeComboBox.getSelectionModel().select(BulkType.BULK_TYPE_GROUPING);
                    } else if (TraceOptionTypeEnum.CHECK_BARCODE.equals(traceOption.getKey())) {
                        this.optionCheckBarcodeCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.INPUT_PRODUCT_NUM.equals(traceOption.getKey())) {
                        this.optionInputProductNumCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.TEN_KEYBOARD.equals(traceOption.getKey())) {
                        // キーボード入力
                        this.optionInputKeyboardCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.KEYBOARD_TYPE.equals(traceOption.getKey())) {
                        // キーボードタイプ
                        exitKeyboardType = true;
                        KeyboardType keyboardType = KeyboardType.valueOf(traceOption.getValue());
                        if (Objects.isNull(keyboardType)) {
                            keyboardType = KeyboardType.TEN_KEYBOARD;
                        }
                        
                        this.keyboardTypeProperty.set(keyboardType);
                    } else if (TraceOptionTypeEnum.QR_READ.equals(traceOption.getKey())) {
                        this.readQRByCameraCheckBox.setSelected(true);
                    }
                }
            } else {
                // トレーサビリティを追加して最初のオプション画面表示時にはTraceOptionが空のため値(設備名)が管理番号に表示されない。追加
                if (Objects.nonNull(workProperty.getWorkPropValue())) {
                    this.referenceNumberProperty.setValue(workProperty.getWorkPropValue());
                    this.selectedEquipments.add(workProperty.getWorkPropValue());
                }

                // チェックボックス
                for (TraceOptionEntity traceOption : traceSetting.getTraceOptions()) {
                    if (TraceOptionTypeEnum.FIELDS.equals(traceOption.getKey())) {
                        String[] fields = new String[]{};
                        if (!StringUtils.isEmpty(traceOption.getValue())) {
                            fields = traceOption.getValue().split("\\|", 0);
                        }

                        for (String field : fields) {
                            if (AccessoryFieldTypeEnum.LOT.equals(field)) {
                                this.lotNumberCheckBox.setSelected(true);
                            } else if (AccessoryFieldTypeEnum.SERIAL.equals(field)) {
                                this.serialNumberCheckBox.setSelected(true);
                            } else if (AccessoryFieldTypeEnum.QUANTITY.equals(field)) {
                                this.quantityCheckBox.setSelected(true);
                            } else if (AccessoryFieldTypeEnum.EQUIPMENT.equals(field)) {
                                this.referenceNumberCheckBox.setSelected(true);
                            } else if (AccessoryFieldTypeEnum.CUSTOM.equals(field)) {
                                this.customCheckBox.setSelected(true);
                            } else if (AccessoryFieldTypeEnum.PARTS_ID.equals(field)) {
                                this.partsIDCheckBox.setSelected(true);
                            }
                        }
                    } else if (TraceOptionTypeEnum.WORK.equals(traceOption.getKey())) {
                        this.optionWorkCheckBox.setSelected(true);
                        this.optionWorkProperty.setValue(traceOption.getValue());
                    } else if (TraceOptionTypeEnum.PROPERTY.equals(traceOption.getKey())) {
                        this.optionWorkPropCheckBox.setSelected(true);
                        this.optionWorkPropProperty.setValue(traceOption.getValue());
                    } else if (TraceOptionTypeEnum.INTEGER_DIGITS.equals(traceOption.getKey())) {
                        this.integerDigitsCheckBox.setSelected(true);
                        this.integerDigitsProperty.setValue(Integer.parseInt(traceOption.getValue()));
                    } else if (TraceOptionTypeEnum.DECIMAL_DIGITS.equals(traceOption.getKey())) {
                        this.decimalDigitsCheckBox.setSelected(true);
                        this.decimalDigitsProperty.setValue(Integer.parseInt(traceOption.getValue()));
                    } else if (TraceOptionTypeEnum.ABSOLUTE_DISPLAY.equals(traceOption.getKey())) {
                        this.absoluteCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.REFERENCE_NUMBER.equals(traceOption.getKey())) {
                        // 値として設定された設備は選択済みの設備として常に含める
                        final String propValue = workProperty.getWorkPropValue();
                        final List<String> optionValues = Optional.ofNullable(traceOption.getValues()).orElse(Collections.emptyList());

                        if (!optionValues.contains(propValue) && Objects.nonNull(propValue) && !propValue.isEmpty()) {
                            optionValues.add(propValue);
                        }

                        this.referenceNumberProperty.setValue(optionValues.stream().collect(Collectors.joining(", ")));

                        this.selectedEquipments.clear();
                        this.selectedEquipments.addAll(optionValues);
                    } else if (TraceOptionTypeEnum.CHECK_EMPTY.equals(traceOption.getKey())) {
                        this.optionForbitEmptyDataCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.INPUT_LIST_ONLY.equals(traceOption.getKey())) {
                        this.optionInputListOnly.setSelected(true);
                    } else if (TraceOptionTypeEnum.TEN_KEYBOARD.equals(traceOption.getKey())) {
                        
                        if (WorkPropertyCategoryEnum.MEASURE.equals(category)) {
                            this.tenKeyboardCheckBox.setSelected(true);
                        } else {
                            this.optionInputKeyboardCheckBox.setSelected(true);
                            if (Objects.isNull(keyboardTypeProperty.get())) {
                                this.keyboardTypeProperty.set(KeyboardType.TEN_KEYBOARD);
                            }
                        }

                    } else if (TraceOptionTypeEnum.ATTACH_FILE.equals(traceOption.getKey())) {
                        this.optionAttachFileCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.BULK_INPUT.equals(traceOption.getKey())) {
                        this.optionBulkInputCheckBox.setSelected(true);
                    } else if (TraceOptionTypeEnum.BULK_TYPE_SEQUENTIAL.equals(traceOption.getKey())) {
                        this.bulkTypeProperty.set(BulkType.BULK_TYPE_SEQUENTIAL);
                        this.bulkTypeComboBox.getSelectionModel().select(BulkType.BULK_TYPE_SEQUENTIAL);
                    } else if (TraceOptionTypeEnum.BULK_TYPE_GROUPING.equals(traceOption.getKey())) {
                        this.bulkTypeProperty.set(BulkType.BULK_TYPE_GROUPING);
                        this.bulkTypeComboBox.getSelectionModel().select(BulkType.BULK_TYPE_GROUPING);
                    }  else if (TraceOptionTypeEnum.VALUE_LIST.equals(traceOption.getKey())) {
                        this.valueList.clear();
                        this.valueList.addAll(traceOption.getValues());
                    } else if (TraceOptionTypeEnum.ALIGNMENT.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応
                        this.alignmentTypeProperty.set(null);
                        this.alignmentComboBox.getSelectionModel().clearSelection();
                    } else if (TraceOptionTypeEnum.ALIGNMENT_LEFT.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応 左
                        this.alignmentTypeProperty.set(AlignmentType.ALIGNMENT_LEFT);
                        this.alignmentComboBox.getSelectionModel().select(AlignmentType.ALIGNMENT_LEFT);
                    } else if (TraceOptionTypeEnum.ALIGNMENT_CENTER.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応 中央
                        this.alignmentTypeProperty.set(AlignmentType.ALIGNMENT_CENTER);
                        this.alignmentComboBox.getSelectionModel().select(AlignmentType.ALIGNMENT_CENTER);
                    } else if (TraceOptionTypeEnum.ALIGNMENT_RIGHT.equals(traceOption.getKey())) {
                        // 入力値リストの文字レイアウト機能追加対応 右
                        this.alignmentTypeProperty.set(AlignmentType.ALIGNMENT_RIGHT);
                        this.alignmentComboBox.getSelectionModel().select(AlignmentType.ALIGNMENT_RIGHT);
                    } else if (TraceOptionTypeEnum.COLOR_VALUE_LIST.equals(traceOption.getKey())) {
                        this.colorValueList.clear();
                        this.colorValueList.addAll(traceOption.getColorTextBkValues());

                    } else if (TraceOptionTypeEnum.TEN_KEYBOARD.equals(traceOption.getKey())) {
                        // キーボード入力
                        this.optionInputKeyboardCheckBox.setSelected(true);

                    } else if (TraceOptionTypeEnum.KEYBOARD_TYPE.equals(traceOption.getKey())) {
                        // キーボードタイプ
                        this.optionInputKeyboardCheckBox.setSelected(true);
                        KeyboardType keyboardType = KeyboardType.valueOf(traceOption.getValue());
                        if (Objects.isNull(keyboardType)) {
                            keyboardType = KeyboardType.TEN_KEYBOARD;
                        }
                        this.keyboardTypeProperty.set(keyboardType);

                    } else if (TraceOptionTypeEnum.INPUT_TEXT.equals(traceOption.getKey())) {
                        this.inputTextCheckBox.setSelected(true);

                    } else if (TraceOptionTypeEnum.DISPLAY_TEXT.equals(traceOption.getKey())) {
                        this.displayTextCheckBox.setSelected(true);
                    }
                }
            }

            // カスタム設定値リスト
            this.traceCustoms.clear();
            if (!traceSetting.getTraceCustoms().isEmpty()) {
                this.traceCustoms.addAll(traceSetting.getTraceCustoms());
            }
        } else {
            // オプションの保存形式が古い場合
            String[] fields = new String[]{};
            if (!StringUtils.isEmpty(optionString)) {
                fields = optionString.split("\\|", 0);
            }

            for (String field : fields) {
                if (AccessoryFieldTypeEnum.LOT.equals(field)) {
                    this.lotNumberCheckBox.setSelected(true);
                } else if (AccessoryFieldTypeEnum.SERIAL.equals(field)) {
                    this.serialNumberCheckBox.setSelected(true);
                } else if (AccessoryFieldTypeEnum.QUANTITY.equals(field)) {
                    this.quantityCheckBox.setSelected(true);
                } else if (AccessoryFieldTypeEnum.EQUIPMENT.equals(field)) {
                    this.referenceNumberCheckBox.setSelected(true);
                } else if (AccessoryFieldTypeEnum.CUSTOM.equals(field)) {
                    this.customCheckBox.setSelected(true);
                } else if (AccessoryFieldTypeEnum.PARTS_ID.equals(field)) {
                    this.partsIDCheckBox.setSelected(true);
                }
            }
        }

        if (Objects.nonNull(category)) {
            switch (category) {
                case PARTS:
                    vBox.getChildren().addAll(this.partsIDCheckBox, this.lotNumberCheckBox, this.serialNumberCheckBox, this.quantityCheckBox, this.optionForbitEmptyDataCheckBox, this.optionInputListOnly);
                    vBox.getChildren().add(this.traceCustomsButton);// カスタム設定値リスト
                    break;
                case WORK:
                    vBox.getChildren().add(this.createBulkTypeComboBox(this.optionBulkInputCheckBox, this.bulkTypeProperty));
                    vBox.getChildren().add(this.createOptionCheckAndEquipContent(this.referenceNumberCheckBox, this.referenceNumberProperty));
                    vBox.getChildren().add(this.traceCustomsButton);// カスタム設定値リスト
                    break;
                case INSPECTION:
                    vBox.getChildren().add(this.createBulkTypeComboBox(this.optionBulkInputCheckBox, this.bulkTypeProperty));
                    vBox.getChildren().add(this.createOptionCheckAndEquipContent(this.referenceNumberCheckBox, this.referenceNumberProperty));
                    vBox.getChildren().add(this.optionAttachFileCheckBox);  // ファイルを添付する
                    vBox.getChildren().add(this.inputTextCheckBox);         // コメントを入力する v2.1.18
                    vBox.getChildren().add(this.displayTextCheckBox);       // 後工程にコメントを表示する v2.1.18
                    vBox.getChildren().add(this.createKeyboardTypeComboBox(this.optionInputKeyboardCheckBox, this.keyboardTypeProperty)); // キーボード入力 v2.1.18
                    vBox.getChildren().add(this.createOptionButtonAndCheckAndComboContent(this.valueListButton, this.optionInputListOnly, this.alignmentTypeProperty)); // 入力値リストの文字レイアウト機能追加対応
                    vBox.getChildren().add(this.traceCustomsButton);        // カスタム設定値リスト
                    break;

                case MEASURE:
                    vBox.getChildren().add(this.createBulkTypeComboBox(this.optionBulkInputCheckBox, this.bulkTypeProperty));
                    vBox.getChildren().add(this.createOptionCheckAndButtonContent(this.optionWorkCheckBox, this.optionWorkProperty));
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.optionWorkPropCheckBox, this.optionWorkPropProperty));
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.integerDigitsCheckBox, this.integerDigitsProperty));
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.decimalDigitsCheckBox, this.decimalDigitsProperty));
                    vBox.getChildren().add(this.absoluteCheckBox);
                    vBox.getChildren().add(this.tenKeyboardCheckBox); // テンキーボード入力
                    vBox.getChildren().add(this.createOptionCheckAndEquipContent(this.referenceNumberCheckBox, this.referenceNumberProperty));
                    vBox.getChildren().add(this.createOptionButtonAndCheckAndComboContent(this.valueListButton, this.optionInputListOnly, this.alignmentTypeProperty)); // 入力値リストの文字レイアウト機能追加対応
                    vBox.getChildren().add(this.createOptionButtonAndCheckContent(this.valueListButton2, this.optionInputListOnly));
                    vBox.getChildren().add(this.traceCustomsButton);// カスタム設定値リスト
                    break;

                case CUSTOM:
                    if (!exitKeyboardType) {
                        this.optionInputKeyboardCheckBox.setSelected(true);
                        this.keyboardTypeProperty.set(KeyboardType.TEN_KEYBOARD);
                    }
                    
                    vBox.getChildren().add(this.createBulkTypeComboBox(this.optionBulkInputCheckBox, this.bulkTypeProperty));
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.optionPluginCheckBox, this.optionPluginProperty));
                    // 2019/12/04 カスタム入力フィールドの項目数追加対応 数値の最大値を5から10に変更 
                    vBox.getChildren().add(this.createNumberSelectionCombo(this.customCheckBox, this.customFieldSizeProperty, 1, 10));
                    vBox.getChildren().add(this.readQRByCameraCheckBox);
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.optionDelimiterCheckBox, this.optionDelimiterProperty));
                    vBox.getChildren().add(this.optionHoldPrevDataCheckBox);
                    vBox.getChildren().add(this.optionForbitEmptyDataCheckBox);
                    vBox.getChildren().add(this.optionForbitRepeatedDataCheckBox);
                    vBox.getChildren().add(this.createKeyboardTypeComboBox(this.optionInputKeyboardCheckBox, this.keyboardTypeProperty)); // キーボード入力
                    if (Boolean.valueOf(AdProperty.getProperties().getProperty(Constants.ENABLE_CHECK_BARCODE, Constants.ENABLE_CHECK_BARCODE_DEFAULT))) {
                        // バーコード照合プラグインを使用する場合、「制定ラベルと照合する」チェックボックスを追加
                        vBox.getChildren().add(this.optionCheckBarcodeCheckBox);
                    }
                    vBox.getChildren().add(this.optionInputProductNumCheckBox);
                    vBox.getChildren().add(this.createOptionButtonAndCheckAndComboContent(this.valueListButton, this.optionInputListOnly, this.alignmentTypeProperty)); // 入力値リストの文字レイアウト機能追加対応
                    vBox.getChildren().add(this.createOptionButtonAndCheckContent(this.valueListButton2, this.optionInputListOnly));
                    vBox.getChildren().add(this.traceCustomsButton);// カスタム設定値リスト
                    break;
                case TIMER:
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.optionWorkCheckBox, this.optionWorkProperty));
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.optionWorkPropCheckBox, this.optionWorkPropProperty));
                    vBox.getChildren().add(this.traceCustomsButton);// カスタム設定値リスト
                    break;
                case LIST:
                    vBox.getChildren().add(this.createOptionCheckAndTextContent(this.optionCountCheckBox, this.optionCountProperty));
                    vBox.getChildren().add(this.traceCustomsButton);// カスタム設定値リスト
                    break;
                case PRODUCT:
                    vBox.getChildren().addAll(this.optionForbitEmptyDataCheckBox);
                    vBox.getChildren().add(this.componentButton);// 構成部品
                    break;
                default: // INFO, JUDGE
                    break;
            }
        }

        // 正規表現を自動で入力するチェックボックスが無効に「変更された場合」のみクリアするため現在の状態を記録する
        initialValidDecimal = Objects.nonNull(this.decimalDigitsCheckBox) && this.decimalDigitsCheckBox.isSelected();
        initialValidInteger = Objects.nonNull(this.integerDigitsCheckBox) && this.integerDigitsCheckBox.isSelected();

        return vBox;
    }

    /**
     * 左にボタン、右にチェックボックスを表示するペインを作成する。
     *
     * @param button
     * @param checkBox
     * @return
     */
    private HBox createOptionButtonAndCheckContent(Button button, CheckBox checkBox) {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_LEFT);

        Pane buttonPane = new Pane(button);
        buttonPane.setPrefWidth(200.0);

        hBox.getChildren().addAll(buttonPane, checkBox);

        return hBox;
    }
    
    /**
     * 左にボタン、右にチェックボックスと文字列を選択可能なコンボボックスを表示するペインを作成する。
     *
     * @param button
     * @param checkBox
     * @param valueProperty
     * @return 
     */
    private HBox createOptionButtonAndCheckAndComboContent(Button button, CheckBox checkBox, ObjectProperty<AlignmentType> valueProperty) {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_LEFT);

        Pane buttonPane = new Pane(button);
        buttonPane.setPrefWidth(200.0);

        VBox vBox = new VBox(2);
        vBox.setAlignment(Pos.CENTER_LEFT);

        HBox hBox2 = new HBox(2);
        hBox2.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(LocaleUtils.getString("key.Alignment"));
        label.setPrefWidth(100.0);

        final ComboBox<AlignmentType> combo = new ComboBox(FXCollections.observableArrayList(AlignmentType.values()));
        combo.setItems(FXCollections.observableArrayList(AlignmentType.values()));
        combo.setPrefWidth(100.0);
        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                valueProperty.setValue(newValue);
            }
        });
        
        // リストセル
        combo.setCellFactory(cell -> new ListCell<AlignmentType>() {
            @Override
            protected void updateItem(AlignmentType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                    setGraphic(null);
                } else {
                    setGraphic(null);
                    setText(LocaleUtils.getString(item.getResourceKey()));
                }
            }
        });

        // コンバーター
        combo.setConverter(new StringConverter<AlignmentType>() {
            @Override
            public String toString(AlignmentType alignmentType) {
                return Objects.nonNull(alignmentType) ? LocaleUtils.getString(alignmentType.getResourceKey()) : "";
            }

            @Override
            public AlignmentType fromString(String string) {
                return null;
            }
        });

        combo.getSelectionModel().select(valueProperty.get());

        hBox2.getChildren().addAll(label, combo);
        vBox.getChildren().addAll(hBox2, checkBox);

        hBox.getChildren().addAll(buttonPane, vBox);

        return hBox;
    }

    /**
     * 設備選択ダイアログを表示する
     *
     * @param checkBox チェックボックス
     * @param prop プロパティ
     * @return HBox
     */
    private HBox createOptionCheckAndEquipContent(CheckBox checkBox, StringProperty prop) {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_LEFT);

        checkBox.setPrefWidth(200.0);

        Button equipButton = new Button();
        equipButton.setPrefWidth(200.0);
        equipButton.setPadding(new Insets(2, 4, 2, 4));
        equipButton.setAlignment(Pos.CENTER_LEFT);
        equipButton.getStyleClass().add("text-normal-bold");
        equipButton.setText(prop.getValue());
        equipButton.textProperty().bind(prop);

        equipButton.setOnAction((ActionEvent actionEvent) -> {
            try {
                final EquipmentInfoFacade facade = new EquipmentInfoFacade();
                final List<EquipmentInfoEntity> equips = new ArrayList<>();
                for (String equipName : selectedEquipments) {
                    if (Objects.isNull(equipName) || equipName.isEmpty()) {
                        continue;
                    }
                    EquipmentInfoEntity entity = facade.findName(equipName);
                    if (Objects.nonNull(entity) && Objects.nonNull(entity.getEquipmentIdentify())) {
                        equips.add(entity);
                    }
                }

                SelectDialogEntity selectDialogEntity = new SelectDialogEntity().equipments(equips);
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity, true);
                if (ret.equals(ButtonType.OK)) {
                    final List<EquipmentInfoEntity> selected = selectDialogEntity.getEquipments();

                    selectedEquipments.clear();
                    selectedEquipments.addAll(selected.stream().map(EquipmentInfoEntity::getEquipmentIdentify).collect(Collectors.toList()));

                    // すべての設備を削除することができるがトレサの値で設定した設備は必須のため常に保存する
                    if (Objects.nonNull(workProperty.getWorkPropValue())
                            && !workProperty.getWorkPropValue().isEmpty()
                            && !selectedEquipments.contains(workProperty.getWorkPropValue())) {
                        selectedEquipments.add(workProperty.getWorkPropValue());
                    }

                    // オプションポップアップが消えるときに更新されるが
                    // ダイアログを表示した時点でポップアップが消えるため再度更新
                    this.update();
                }

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        hBox.getChildren().addAll(checkBox, equipButton);
        return hBox;
    }

    /**
     * チェックボックスと数値を範囲選択可能なコンボボックスを生成する
     *
     * @param checkBox チェックボックス
     * @param prop プロパティ
     * @param minNum 数値の最小値
     * @param maxNum 数値の最大値
     * @return HBox
     */
    private HBox createNumberSelectionCombo(CheckBox checkBox, Property prop, int minNum, int maxNum) {
        final IntegerProperty integerProp = (IntegerProperty) prop;

        final HBox hBox = new HBox(2.0);
        hBox.setAlignment(Pos.CENTER_LEFT);

        checkBox.setPrefWidth(200.0);

        final Label label = new Label(LocaleUtils.getString("key.FieldSize"));
        label.setPrefWidth(50.0);
        final HBox child = new HBox(2.0);
        child.setAlignment(Pos.CENTER_LEFT);

        final ComboBox<Integer> combo = new ComboBox(FXCollections.observableArrayList(IntStream.rangeClosed(minNum, maxNum).boxed().collect(toList())));
        combo.setPrefWidth(100.0);
        // プロパティにコンボボックスをbindしてしまうと(双方向バインドはできない)再設定のためにプロパティへの値の再設定ができない。bindせずに直接設定する。
        if (integerProp.getValue() < minNum || maxNum < integerProp.getValue()) {
            combo.getSelectionModel().selectFirst();
        } else {
            combo.getSelectionModel().select(integerProp.getValue());
        }
        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                prop.setValue(newValue);
            }
        });

        child.getChildren().addAll(label, combo);

        hBox.getChildren().addAll(checkBox, child);

        return hBox;
    }
 
    /**
     * チェックボックスと工程選択ボタンを生成する
     *
     * @param checkBox チェックボックス
     * @param prop プロパティ
     * @return HBox
     */
    private HBox createOptionCheckAndButtonContent(CheckBox checkBox, StringProperty prop) {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_LEFT);

        checkBox.setPrefWidth(200.0);

        Button workButton = new Button();
        workButton.setPrefWidth(200.0);
        workButton.setPadding(new Insets(2, 4, 2, 4));
        workButton.setAlignment(Pos.CENTER_LEFT);
        workButton.getStyleClass().add("text-normal-bold");
        workButton.setUserData(prop);
        workButton.setText(prop.getValue());
        workButton.textProperty().bind(prop);

        workButton.setOnAction((ActionEvent actionEvent) -> {
            try {
                SelectDialogEntity selectDialogEntity = new SelectDialogEntity();
                ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.DialogWorkSettiong"), "WorkSingleSelectionCompo", selectDialogEntity);
                if (ret.equals(ButtonType.OK)
                        && Objects.nonNull(selectDialogEntity.getWorks())
                        && !selectDialogEntity.getWorks().isEmpty()) {
                    List<WorkInfoEntity> works = selectDialogEntity.getWorks();
                    WorkInfoEntity work = works.get(0);

                    Button button = (Button) actionEvent.getSource();
                    SimpleStringProperty ssp = (SimpleStringProperty) button.getUserData();
                    ssp.setValue(work.getWorkName());

                    // オプションポップアップが消えるときに更新されるが
                    // ダイアログを表示した時点でポップアップが消えるため再度更新
                    this.update();
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        });

        hBox.getChildren().addAll(checkBox, workButton);
        return hBox;
    }

    /**
     * 付加情報(チェックボックスとテキストボックス)を生成する
     *
     * @param checkBox チェックボックス
     * @param prop プロパティ
     * @return HBox
     */
    private HBox createOptionCheckAndTextContent(CheckBox checkBox, StringProperty prop) {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_LEFT);

        checkBox.setPrefWidth(200.0);

        TextField txtField = new TextField();
        txtField.setPrefWidth(200.0);
        txtField.setPadding(new Insets(2, 4, 2, 4));
        txtField.setAlignment(Pos.CENTER_LEFT);
        txtField.getStyleClass().add("text-normal-bold");
        Bindings.bindBidirectional(txtField.textProperty(), prop);

        hBox.getChildren().addAll(checkBox, txtField);
        return hBox;
    }

    /**
     * 付加情報(チェックボックスと整数値用テキストボックス)を生成する
     *
     * @param checkBox チェックボックス
     * @param prop プロパティ
     * @return HBox
     */
    private HBox createOptionCheckAndTextContent(CheckBox checkBox, IntegerProperty prop) {
        HBox hBox = new HBox(2);
        hBox.setAlignment(Pos.CENTER_LEFT);

        checkBox.setPrefWidth(200.0);

        TextField txtField = new TextField();
        txtField.setPrefWidth(100.0);
        txtField.setPadding(new Insets(2, 4, 2, 4));
        txtField.setAlignment(Pos.CENTER_RIGHT);
        txtField.getStyleClass().add("text-normal-bold");
        Bindings.bindBidirectional(txtField.textProperty(), prop, converterInt);

        hBox.getChildren().addAll(checkBox, txtField);
        return hBox;
    }

    /**
     * ポップアップを表示する
     *
     * @param node ノード
     * @param popup ポップアップ
     */
    private void showPopup(final Node node, final Popup popup) {
        final Window window = node.getScene().getWindow();
        double x = window.getX() + node.localToScene(0, 0).getX() + node.getScene().getX();
        double y = window.getY() + node.localToScene(0, 0).getY() + node.getScene().getY() + node.getBoundsInParent().getHeight();
        popup.show(window, x, y);

        // ポップアップの表示位置を調整
        if (!popup.getContent().isEmpty()) {
            final Node content = popup.getContent().get(0);
            x -= content.localToScene(0, 0).getX();
            y -= content.localToScene(0, 0).getY() + 3;
        }

        // ポップアップを再表示
        popup.hide();
        popup.show(window, x, y);
    }

    // 整数 入力制限あり、少数 入力制限あり
    private static final String pattern1 = "^-$|^-?[0-9]{1,%d}(\\.[0-9]{0,%d})?$";   // 小数部の入力を省略可
    // 整数 入力制限あり、少数 入力制限あり
    private static final String pattern2 = "^-$|^-?0(\\.[0-9]{0,%d})?$";             // 小数部の入力を省略可
    // 整数 入力制限あり、少数 入力不可
    private static final String pattern3 = "^-$|^-?[0-9]{1,%d}?$";
    // 整数 入力制限あり、少数 入力制限なし
    private static final String pattern4 = "^-$|^-?[0-9]{1,%d}(\\.[0-9]*)?$";        // 小数部の入力を省略可
    // 整数 入力制限なし、少数 入力制限あり
    private static final String pattern5 = "^-$|^-?[0-9]+(\\.[0-9]{0,%d})?$";        // 小数部の入力を省略可
    // 整数 入力制限なし、少数 入力不可
    private static final String pattern6 = "^-$|^-?[0-9]+$";

    /**
     * 入力規則を取得する。
     *
     * @return 入力規則の文字列
     */
    
    private String getValidationRule() {
        if (!this.integerDigitsCheckBox.isSelected() && !this.decimalDigitsCheckBox.isSelected()) {
            return null;
        }

        String validationRule = null;

        if (this.integerDigitsCheckBox.isSelected()) {
            if (this.decimalDigitsCheckBox.isSelected()) {
                if (0 < this.decimalDigitsProperty.get()) {
                    if (1 < this.integerDigitsProperty.get()) {
                        // 整数 入力制限あり、少数 入力制限あり
                        validationRule = String.format(pattern1, this.integerDigitsProperty.get(), this.decimalDigitsProperty.get());
                    } else if (1 == this.integerDigitsProperty.get()) {
                        validationRule = String.format("^-$|^-?[0-9](\\.[0-9]{0,%d})?$", this.decimalDigitsProperty.get());
                    } else {
                        validationRule = String.format(pattern2, this.decimalDigitsProperty.get());
                    }
                } else {
                    if (1 < this.integerDigitsProperty.get()) {
                        // 整数 入力制限あり、少数 入力不可
                        validationRule = String.format(pattern3, this.integerDigitsProperty.get());
                    } else if (1 == this.integerDigitsProperty.get()) {
                        // 整数 入力制限あり、少数 入力不可
                        validationRule = "^-$|^-?[0-9]?$";
                    } else {
                        // 0しか入力出来ない
                        validationRule = "^0$";
                    }
                }
            } else {
                // 整数 入力制限あり、少数 入力制限なし
                if (1 < this.integerDigitsProperty.get()) {
                    validationRule = String.format(pattern4, this.integerDigitsProperty.get());
                } else if (1 == this.integerDigitsProperty.get()) {
                    validationRule = "^-$|^-?[0-9](\\.[0-9]*)?$";
                } else {
                    validationRule = "^-$|^-?0(\\.[0-9]*)?$";
                }
            }
        } else if (this.decimalDigitsCheckBox.isSelected()) {
            if (0 < this.decimalDigitsProperty.get()) {
                validationRule = String.format(pattern5, this.decimalDigitsProperty.get());
            } else {
                validationRule = pattern6;
            }
        }

        return validationRule;
    }

    /**
     * 品質トレーサビリティ設定情報を更新する。
     */
    private void update() {
        TraceSettingEntity traceSetting = new TraceSettingEntity();
        String optionString = null;

        WorkPropertyCategoryEnum category = workProperty.workPropCategoryProperty().get();
        if (WorkPropertyCategoryEnum.CUSTOM.equals(category)
                || WorkPropertyCategoryEnum.TIMER.equals(category)
                || WorkPropertyCategoryEnum.LIST.equals(category)) {
            // プラグイン
            if (optionPluginCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.PLUGIN.toString());
                traceOption.setValue(optionPluginProperty.getValue());
                traceSetting.getTraceOptions().add(traceOption);
            }

            // 工程
            if (optionWorkCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.WORK.toString());
                traceOption.setValue(optionWorkProperty.getValue());
                traceSetting.getTraceOptions().add(traceOption);
            }

            // 工程プロパティ
            if (optionWorkPropCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.PROPERTY.toString());
                traceOption.setValue(optionWorkPropProperty.getValue());
                traceSetting.getTraceOptions().add(traceOption);
            }

            // 行数
            if (optionCountCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.COUNT.toString());
                traceOption.setValue(String.valueOf(optionCountProperty.getValue()));
                traceSetting.getTraceOptions().add(traceOption);
            }

            // カスタムフィールド
            if (customCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.FIELDS.toString());
                traceOption.setValue(AccessoryFieldTypeEnum.CUSTOM.toString());
                traceSetting.getTraceOptions().add(traceOption);
                // 項目数
                final TraceOptionEntity customFieldSizeOption = new TraceOptionEntity();
                customFieldSizeOption.setKey(TraceOptionTypeEnum.FIELD_SIZE.toString());
                customFieldSizeOption.setValue(String.valueOf(customFieldSizeProperty.getValue()));
                traceSetting.getTraceOptions().add(customFieldSizeOption);
            }

            // 区切り文字
            if (optionDelimiterCheckBox.isSelected()) {
                final TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.DELIMITER.toString());
                traceOption.setValue(String.valueOf(optionDelimiterProperty.getValue()));
                traceSetting.getTraceOptions().add(traceOption);
            }

            if (optionHoldPrevDataCheckBox.isSelected()) {
                final TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.HOLD_PREV_DATA.toString());
                traceOption.setValue(String.valueOf(optionHoldPrevDataCheckBox.isSelected()));
                traceSetting.getTraceOptions().add(traceOption);
            }

            if (optionForbitRepeatedDataCheckBox.isSelected()) {
                final TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.CHECK_UNIQUE.toString());
                traceOption.setValue(String.valueOf(optionForbitRepeatedDataCheckBox.isSelected()));
                traceSetting.getTraceOptions().add(traceOption);
            }
            // ラベルと照合する
            if (optionCheckBarcodeCheckBox.isSelected()) {
                final TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.CHECK_BARCODE.toString());
                traceOption.setValue(String.valueOf(optionCheckBarcodeCheckBox.isSelected()));
                traceSetting.getTraceOptions().add(traceOption);
            }
            // 製造番号を登録する
            if (optionInputProductNumCheckBox.isSelected()) {
                final TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.INPUT_PRODUCT_NUM.toString());
                traceOption.setValue(String.valueOf(optionInputProductNumCheckBox.isSelected()));
                traceSetting.getTraceOptions().add(traceOption);
            }
            // キーボード入力
            if (optionInputKeyboardCheckBox.isSelected()) {
                final TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.TEN_KEYBOARD.name());
                traceOption.setValue(String.valueOf(optionInputKeyboardCheckBox.isSelected()));
                traceSetting.getTraceOptions().add(traceOption);
            }
            
            if (WorkPropertyCategoryEnum.CUSTOM.equals(category)) {
                if (Objects.isNull(this.keyboardTypeProperty.get())) {
                    this.keyboardTypeProperty.set(KeyboardType.TEN_KEYBOARD);
                }

                TraceOptionEntity keyboardTypeOption = new TraceOptionEntity();
                keyboardTypeOption.setKey(TraceOptionTypeEnum.KEYBOARD_TYPE.name());
                keyboardTypeOption.setValue(this.keyboardTypeProperty.get().name());
                traceSetting.getTraceOptions().add(keyboardTypeOption);
            }

            if (this.readQRByCameraCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.QR_READ.toString());
                traceOption.setValue(Boolean.TRUE.toString());
                traceSetting.getTraceOptions().add(traceOption);
            }

        } else {
            // 工程
            if (optionWorkCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.WORK.toString());
                traceOption.setValue(optionWorkProperty.getValue());
                traceSetting.getTraceOptions().add(traceOption);
            }

            // 工程プロパティ
            if (optionWorkPropCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.PROPERTY.toString());
                traceOption.setValue(optionWorkPropProperty.getValue());
                traceSetting.getTraceOptions().add(traceOption);
            }

            if (this.integerDigitsCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.INTEGER_DIGITS.toString());
                traceOption.setValue(String.valueOf(this.integerDigitsProperty.getValue()));
                traceSetting.getTraceOptions().add(traceOption);
            }

            if (this.decimalDigitsCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.DECIMAL_DIGITS.toString());
                traceOption.setValue(String.valueOf(this.decimalDigitsProperty.getValue()));
                traceSetting.getTraceOptions().add(traceOption);
            }

            if (this.absoluteCheckBox.isSelected()) {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.ABSOLUTE_DISPLAY.toString());
                traceOption.setValue(Boolean.TRUE.toString());
                traceSetting.getTraceOptions().add(traceOption);
            }
            
            StringBuilder sb = new StringBuilder();

            if (lotNumberCheckBox.isSelected()) {
                sb.append(AccessoryFieldTypeEnum.LOT.toString());
                sb.append("|");
            }

            if (serialNumberCheckBox.isSelected()) {
                sb.append(AccessoryFieldTypeEnum.SERIAL.toString());
                sb.append("|");
            }

            if (quantityCheckBox.isSelected()) {
                sb.append(AccessoryFieldTypeEnum.QUANTITY.toString());
                sb.append("|");
            }

            if (referenceNumberCheckBox.isSelected()) {
                sb.append(AccessoryFieldTypeEnum.EQUIPMENT.toString());
                sb.append("|");
            }

            if (partsIDCheckBox.isSelected()) {
                sb.append(AccessoryFieldTypeEnum.PARTS_ID.toString());
                sb.append("|");
            }

            // 管理番号(設備)についてはチェックの有無にかかわらず設定を反映する
            {
                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.REFERENCE_NUMBER.toString());
                traceOption.setValues(selectedEquipments);
                traceSetting.getTraceOptions().add(traceOption);
            }

            {
                if (0 < sb.length()) {
                    int index = sb.lastIndexOf("|");
                    sb.deleteCharAt(index);
                }

                TraceOptionEntity traceOption = new TraceOptionEntity();
                traceOption.setKey(TraceOptionTypeEnum.FIELDS.toString());
                traceOption.setValue(sb.toString());
                traceSetting.getTraceOptions().add(traceOption);
            }
            
            if (WorkPropertyCategoryEnum.INSPECTION.equals(category)) {
                if (this.optionInputKeyboardCheckBox.isSelected()) {
                    final TraceOptionEntity traceOption = new TraceOptionEntity();
                    traceOption.setKey(TraceOptionTypeEnum.TEN_KEYBOARD.name());
                    traceOption.setValue(String.valueOf(optionInputKeyboardCheckBox.isSelected()));
                    traceSetting.getTraceOptions().add(traceOption);
                }

                if (Objects.isNull(this.keyboardTypeProperty.get())) {
                    this.keyboardTypeProperty.set(KeyboardType.TEN_KEYBOARD);
                }

                TraceOptionEntity keyboardTypeOption = new TraceOptionEntity();
                keyboardTypeOption.setKey(TraceOptionTypeEnum.KEYBOARD_TYPE.name());
                keyboardTypeOption.setValue(this.keyboardTypeProperty.get().name());
                traceSetting.getTraceOptions().add(keyboardTypeOption);

            } else {
                if (this.tenKeyboardCheckBox.isSelected()) {
                    TraceOptionEntity traceOption = new TraceOptionEntity();
                    traceOption.setKey(TraceOptionTypeEnum.TEN_KEYBOARD.toString());
                    traceOption.setValue(Boolean.TRUE.toString());
                    traceSetting.getTraceOptions().add(traceOption);
                }            
            }
        }

        if (this.optionForbitEmptyDataCheckBox.isSelected()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.CHECK_EMPTY.toString());
            traceOption.setValue(Boolean.TRUE.toString());
            traceSetting.getTraceOptions().add(traceOption);
        }

        if (this.optionInputListOnly.isSelected()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.INPUT_LIST_ONLY.toString());
            traceOption.setValue(Boolean.TRUE.toString());
            traceSetting.getTraceOptions().add(traceOption);
        }

        if (this.optionAttachFileCheckBox.isSelected()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.ATTACH_FILE.toString());
            traceOption.setValue(Boolean.TRUE.toString());
            traceSetting.getTraceOptions().add(traceOption);
        }

        if (this.optionBulkInputCheckBox.isSelected()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.BULK_INPUT.toString());
            traceOption.setValue(Boolean.TRUE.toString());
            traceSetting.getTraceOptions().add(traceOption);
            
            BulkType selectedBulkType = bulkTypeProperty.get();
            if (Objects.nonNull(selectedBulkType)) {
                TraceOptionEntity bulkTypeOption = new TraceOptionEntity();
                
                // 選択されたBulkTypeに基づいて適切なTraceOptionTypeEnumを設定
                switch (selectedBulkType) {
                    case BULK_TYPE_SEQUENTIAL:
                        bulkTypeOption.setKey(TraceOptionTypeEnum.BULK_TYPE_SEQUENTIAL.toString());
                        break;
                    case BULK_TYPE_GROUPING:
                        bulkTypeOption.setKey(TraceOptionTypeEnum.BULK_TYPE_GROUPING.toString());
                        break;
                }
                
                bulkTypeOption.setValue(Boolean.TRUE.toString());
                traceSetting.getTraceOptions().add(bulkTypeOption);
            }
        }

        // 入力値リスト
        if (!this.valueList.isEmpty()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.VALUE_LIST.toString());
            traceOption.setValues(valueList);
            traceSetting.getTraceOptions().add(traceOption);
        }
        

        if (WorkPropertyCategoryEnum.CUSTOM.equals(workProperty.workPropCategoryProperty().get()) || WorkPropertyCategoryEnum.INSPECTION.equals(workProperty.workPropCategoryProperty().get()) || WorkPropertyCategoryEnum.MEASURE.equals(workProperty.workPropCategoryProperty().get())) {
            AlignmentType selectedAlignment = alignmentTypeProperty.get();
            if (Objects.nonNull(selectedAlignment)) {
                // 選択されたAlignmentTypeを追加
                TraceOptionEntity alignmentTypeOption = new TraceOptionEntity();

                // 選択されたAlignmentTypeに基づいて適切なTraceOptionTypeEnumを設定
                switch (selectedAlignment) {
                    case ALIGNMENT_LEFT:
                        alignmentTypeOption.setKey(TraceOptionTypeEnum.ALIGNMENT_LEFT.toString());
                        break;
                    case ALIGNMENT_CENTER:
                        alignmentTypeOption.setKey(TraceOptionTypeEnum.ALIGNMENT_CENTER.toString());
                        break;
                    case ALIGNMENT_RIGHT:
                        alignmentTypeOption.setKey(TraceOptionTypeEnum.ALIGNMENT_RIGHT.toString());
                        break;
                }

                alignmentTypeOption.setValue(Boolean.TRUE.toString());
                traceSetting.getTraceOptions().add(alignmentTypeOption);
            }
        }

        // 色付入力値リスト
        if (!this.colorValueList.isEmpty()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.COLOR_VALUE_LIST.toString());
            traceOption.setColorTextBkValues(colorValueList);
            traceSetting.getTraceOptions().add(traceOption);
        }

        // カスタム設定値
        if (!this.traceCustoms.isEmpty()) {
            traceSetting.setTraceCustoms(this.traceCustoms);
        }
        
        // コメントを入力する
        if (this.inputTextCheckBox.isSelected()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.INPUT_TEXT.name());
            traceOption.setValue(String.valueOf(2));    // 入力フォームのカラムを拡張できるようにするため数値型
            traceSetting.getTraceOptions().add(traceOption);
        }

        // 後工程にコメントを表示する
        if (this.displayTextCheckBox.isSelected()) {
            TraceOptionEntity traceOption = new TraceOptionEntity();
            traceOption.setKey(TraceOptionTypeEnum.DISPLAY_TEXT.name());
            traceOption.setValue(String.valueOf(true));
            traceSetting.getTraceOptions().add(traceOption);
        }

        if (!traceSetting.getTraceOptions().isEmpty() || !traceSetting.getTraceCustoms().isEmpty()) {
            try {
                optionString = XmlSerializer.serialize(traceSetting);
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }

        // 整数有効桁数と少数有効桁数より入力規則を設定
        String validationRule = this.getValidationRule();
        if (isValidationRuleDisabled()) {
            workProperty.setWorkPropValidationRule("");
        } else if (!StringUtils.isEmpty(validationRule)) {
            workProperty.setWorkPropValidationRule(validationRule);
        }

        workProperty.setWorkPropOption(optionString);
        workProperty.updateMember();
    }

    /**
     * 入力規則が無効かどうかを取得する。
     *
     * @return (true: 無効, false: 有効)
     */
    private boolean isValidationRuleDisabled() {
        final boolean currentDecimal = Objects.nonNull(this.decimalDigitsCheckBox) && this.decimalDigitsCheckBox.isSelected();
        final boolean currentInteger = Objects.nonNull(this.integerDigitsCheckBox) && this.integerDigitsCheckBox.isSelected();

        // 入力規則をクリアするのは自動で入力規則を設定するチェックが入っていない場合。
        // 「チェックが入っていない場合」はそもそもチェックボックス(小数・桁数)が存在しない場合があるため「チェックが変更された」ことを確認する必要がある
        return (currentDecimal != initialValidDecimal || currentInteger != initialValidInteger)
                && !(currentDecimal || currentInteger);
    }

    /**
     * キーボードタイプコンボボックスを生成する。
     * 
     * @param checkBox キーボード入力チェックボックス
     * @param valueProperty キーボートタイプのプロパティ
     * @return 
     */
    private HBox createKeyboardTypeComboBox(CheckBox checkBox, ObjectProperty<KeyboardType> valueProperty) {

        final ComboBox<KeyboardType> combo = new ComboBox(FXCollections.observableArrayList(KeyboardType.values()));
        combo.setPrefWidth(200.0);
        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                valueProperty.setValue(newValue);
            }
        });
        
        // リストセル
        combo.setCellFactory(cell -> new ListCell<KeyboardType>(){
           @Override
           protected void updateItem(KeyboardType item, boolean empty) {
               super.updateItem(item, empty);
               if (empty) {
                 setText("");
                 setGraphic(null);
               } else {
                 setGraphic(null);
                 setText(LocaleUtils.getString(item.getResourceKey()));
               }
           }
        });
        
        // コンバーター
        combo.setConverter(new StringConverter<KeyboardType>() {
            @Override
            public String toString(KeyboardType keyboardType) {
                if (Objects.isNull(keyboardType)) {
                    return "";
                }
                return LocaleUtils.getString(keyboardType.getResourceKey());
            }
            @Override
            public KeyboardType fromString(String string) {
                return null;
            }
        });

        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            combo.setDisable(!newValue);
        });

        combo.getSelectionModel().select(valueProperty.get());
        combo.setDisable(!checkBox.selectedProperty().get());

        final HBox child = new HBox(2.0);
        child.setAlignment(Pos.CENTER_LEFT);
        child.getChildren().addAll(combo);

        final HBox hBox = new HBox(2.0);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(checkBox, child);

        return hBox;
    }

    /**
     * 製品単位に入力タイプコンボボックスを生成する。
     *
     * @param checkBox 製品単位に入力するチェックボックス
     * @param valueProperty 選択された製品単位に入力タイプの値を保持するプロパティ
     * @return HBox
     */
    private HBox createBulkTypeComboBox(CheckBox checkBox, ObjectProperty<BulkType> valueProperty) {
        final ComboBox<BulkType> combo = new ComboBox<>(FXCollections.observableArrayList(BulkType.values()));
        combo.setPrefWidth(200.0);
        
        if (checkBox.isSelected()) {
            combo.getSelectionModel().select(valueProperty.get());
        } else {
            valueProperty.set(BulkType.BULK_TYPE_SEQUENTIAL);
            combo.getSelectionModel().select(BulkType.BULK_TYPE_SEQUENTIAL);
            combo.setDisable(true);
        }

        // リストセル
        combo.setCellFactory(cell -> new ListCell<BulkType>() {
            @Override
            protected void updateItem(BulkType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || Objects.isNull(item)) {
                    setText(null);
                } else {
                    setText(LocaleUtils.getString(item.getResourceKey()));
                }
            }
        });
        
        // コンバーター
        combo.setConverter(new StringConverter<BulkType>() {
            @Override
            public String toString(BulkType bulkType) {
                return Objects.isNull(bulkType) ? "" : LocaleUtils.getString(bulkType.getResourceKey());
            }
            
            @Override
            public BulkType fromString(String string) {
                return null;
            }
        });

        // コンボボックスの選択が変更された際に、valuePropertyを更新する
        combo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                valueProperty.setValue(newValue);
            }
        });

        // チェックボックスの状態に応じて、コンボボックスを有効・無効にする
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            combo.setDisable(!newValue);
        });

        checkBox.setPrefWidth(200.0);
        final HBox child = new HBox(2.0);
        child.setAlignment(Pos.CENTER_LEFT);
        child.getChildren().addAll(combo);

        final HBox hBox = new HBox(2.0);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(checkBox, child);

        return hBox;
    }
}
