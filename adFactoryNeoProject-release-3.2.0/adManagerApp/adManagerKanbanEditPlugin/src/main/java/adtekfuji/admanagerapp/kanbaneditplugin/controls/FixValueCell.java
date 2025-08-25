/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.controls;

import adtekfuji.admanagerapp.kanbaneditplugin.entity.TracebilityItem;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import jp.adtekfuji.javafxcommon.controls.PropertySaveTableView;

/**
 * 品質データ 修正値セルコントロール
 *
 * @author y-harada
 */
public class FixValueCell extends TableCell<TracebilityItem, String> {

    private final HBox hbox = new HBox();
    private final Label label = new Label();
    private final TextField textField = new TextField();
    private final TextArea textArea = new TextArea();
    private final CheckBox checkBox = new CheckBox();
    private final ComboBox comboBox = new ComboBox();

    private static SimpleDateFormat sdf = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
    private final static String DATETIME_REGEX = "\\d|:|/|-|\\s";
    private PropertySaveTableView<TracebilityItem> tracebilityList;

    private String fixValue;

    /**
     * コンストラクタ
     *
     * @param tracebilityList リスト
     */
    public FixValueCell(PropertySaveTableView<TracebilityItem> tracebilityList) {
        super();

        this.tracebilityList = tracebilityList;

        this.textField.setVisible(false);
        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // 編集を完了する
                commitEdit(this.textField.getText());
            }
        });

        this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && FixValueCell.this.isEditing()) {
                // 編集をキャンセルする
                cancelEdit();
            }
        });

        this.textArea.setVisible(false);
        this.textArea.setOnKeyPressed((KeyEvent event) -> {
            if (!Objects.equals(event.getCode(), KeyCode.ENTER)) {
                return;
            }

            if (event.isAltDown()) {
                // Alt + Enter で改行する。
                this.textArea.insertText(this.textArea.getCaretPosition(), "\n");
            } else {
                // 編集を完了する。
                commitEdit(this.textArea.getText());
            }
        });

        this.textArea.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && FixValueCell.this.isEditing()) {
                // 編集をキャンセルする
                cancelEdit();
            }
        });

        this.hbox.setAlignment(Pos.TOP_LEFT);
        this.hbox.getChildren().addAll(label);

        this.checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                commitEdit(newValue == null || !newValue ? "0" : "1");
            }
        });

        this.comboBox.setEditable(true);
        this.comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                commitEdit(newValue == null ? "" : newValue);
            }
        });

        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setEditable(true);
    }

    /**
     * セルの内容を更新する
     *
     * @param fixValue 値
     * @param empty 空更新か
     */
    @Override
    protected void updateItem(String fixValue, boolean empty) {
        super.updateItem(fixValue, empty);
        this.setText(null);
        if (empty) {
            this.fixValue = null;
            this.setGraphic(null);
        } else {

            TracebilityItem item = tracebilityList.getItems().get(this.getIndex());
            this.setEditable(item.isEditable());

            switch (item.getInputType()) {
                // テキストボックス
                case TEXTFIELD:
                case TEXTFIELDTIME:
                case TEXTAREA:
                default:
                    this.fixValue = fixValue;
                    this.label.setText(Objects.nonNull(this.fixValue) ? this.fixValue : "<null>");
                    this.setGraphic(this.hbox);
                    break;
                // チェックボックス
                case CHECKBOX:
                    this.fixValue = fixValue;
                    this.checkBox.setSelected("1".equals(fixValue));
                    this.setGraphic(this.checkBox);
                    break;
                // コンボボックス
                case COMBOBOX:
                    this.fixValue = fixValue;
                    this.comboBox.getItems().clear();
                    this.comboBox.getItems().addAll(item.getInputList());
                    this.comboBox.setValue(fixValue);
                    this.comboBox.setPrefWidth(gettContentWidth(this.getWidth()));
                    this.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                        this.comboBox.setPrefWidth(gettContentWidth(newValue.doubleValue()));
                    });
                    this.setGraphic(this.comboBox);
                    break;
            }
        }
    }

    /**
     * 編集を開始する
     */
    @Override
    public void startEdit() {
        if (!this.isEditable()) {
            return;
        }

        super.startEdit();
        TracebilityItem item = tracebilityList.getItems().get(this.getIndex());
        switch (item.getInputType()) {
            // テキストボックス
            case TEXTFIELD:
            default:
                this.textField.setText(this.fixValue);
                this.textField.setVisible(true);
                this.textField.setPrefWidth(gettContentWidth(this.getWidth()));
                this.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                    this.textField.setPrefWidth(gettContentWidth(newValue.doubleValue()));
                });
                this.hbox.getChildren().remove(this.label);
                this.hbox.getChildren().add(0, this.textField);
                this.textField.requestFocus();
                break;
            case TEXTFIELDTIME:
                this.textField.setText(this.fixValue);
                this.textField.setVisible(true);
                this.textField.setPrefWidth(gettContentWidth(this.getWidth()));
                this.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                    this.textField.setPrefWidth(gettContentWidth(newValue.doubleValue()));
                });
                this.textField.addEventFilter(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
                    if (!event.getCharacter().matches(DATETIME_REGEX)) {
                        event.consume();
                    }
                });
                this.hbox.getChildren().remove(this.label);
                this.hbox.getChildren().add(0, this.textField);
                this.textField.requestFocus();
                break;
            case TEXTAREA:
                this.textArea.setText(this.fixValue);
                this.textArea.setVisible(true);
                this.textArea.setPrefWidth(gettContentWidth(this.getWidth()));
                this.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                    this.textArea.setPrefWidth(gettContentWidth(newValue.doubleValue()));
                });
                this.hbox.getChildren().remove(this.label);
                this.hbox.getChildren().add(0, this.textArea);
                this.textArea.requestFocus();
                break;
            // チェックボックス
            case CHECKBOX:
                this.checkBox.setDisable(false);
                this.checkBox.requestFocus();
                break;
            // コンボボックス
            case COMBOBOX:
                this.comboBox.setDisable(false);
                this.comboBox.requestFocus();
                break;
        }
    }

    /**
     * 編集をキャンセルする
     */
    @Override
    public void cancelEdit() {
        if (this.isEditing()) {
            if (this.label.equals(this.hbox.getChildren().get(0))) {
                super.cancelEdit();
                return;
            }
            this.hbox.getChildren().remove(this.textField);
            this.hbox.getChildren().remove(this.textArea);
            this.hbox.getChildren().add(0, this.label);
            super.cancelEdit();
        }
    }

    /**
     * 編集を完了する
     *
     * @param newValue 新しい値
     */
    @Override
    public void commitEdit(String newValue) {
        TracebilityItem item = tracebilityList.getItems().get(this.getIndex());
        switch (item.getInputType()) {
            // テキストボックス
            case TEXTFIELD:
            default:
                if (StringUtils.isEmpty(newValue)) {
                    super.cancelEdit();
                    this.hbox.getChildren().remove(this.textField);
                    this.hbox.getChildren().add(0, this.label);
                    return;
                }
                this.fixValue = newValue;
                this.label.setText(this.fixValue);
                this.hbox.getChildren().remove(this.textField);
                this.hbox.getChildren().add(0, this.label);
                break;
            case TEXTFIELDTIME:
                if (StringUtils.isEmpty(newValue)) {
                    super.cancelEdit();
                    this.hbox.getChildren().remove(this.textField);
                    this.hbox.getChildren().add(0, this.label);
                    return;
                }
                try {
                    sdf.setLenient(false);
                    Date data = sdf.parse(newValue);
                    this.fixValue = sdf.format(data);
                    this.label.setText(this.fixValue);
                    this.hbox.getChildren().remove(this.textField);
                    this.hbox.getChildren().add(0, this.label);
                } catch (ParseException ex) {
                    super.cancelEdit();
                    this.hbox.getChildren().remove(this.textField);
                    this.hbox.getChildren().add(0, this.label);
                    return;
                }
                break;
            case TEXTAREA:
                if (StringUtils.isEmpty(newValue)) {
                    super.cancelEdit();
                    this.hbox.getChildren().remove(this.textArea);
                    this.hbox.getChildren().add(0, this.label);
                    return;
                }
                this.fixValue = newValue;
                this.label.setText(this.fixValue);
                this.hbox.getChildren().remove(this.textArea);
                this.hbox.getChildren().add(0, this.label);
                break;
            // チェックボックス, コンボボックス
            case CHECKBOX:
            case COMBOBOX:
                if (StringUtils.isEmpty(newValue)) {
                    super.cancelEdit();
                    return;
                }
                this.fixValue = newValue;
                item.setInputValue(this.fixValue);
                break;
        }

        super.commitEdit(newValue);
    }

    /**
     * コンテンツの幅を取得する。
     * 
     * @param width 列幅
     * @return コンテンツの幅
     */
    private double gettContentWidth(double width) {
        return Math.max(200.0, width - 20);
    }
}
