/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.tablecell;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;

/**
 * 数値編集セル
 *
 * @author s-heya
 */
public class TableNumberCell<T> extends TableCell<T, String> {

    private final RestrictedTextField restrictedTextField;
    private final Double minLimit;
    private final Double maxLimit;

    private TablePosition<T, ?> tablePos = null;


    /**
     * コンストラクタ
     */
    public TableNumberCell() {
        this.restrictedTextField = new RestrictedTextField();
        this.minLimit = Double.MIN_VALUE;
        this.maxLimit = Double.MAX_VALUE;

        this.initialize();
    }

    /**
     * コンストラクタ
     * 
     * @param minLimit
     * @param maxLimit
     */
    public TableNumberCell(Double minLimit, Double maxLimit) {
        this.restrictedTextField = new RestrictedTextField();
        this.minLimit = minLimit;
        this.maxLimit = maxLimit;
        
        this.initialize();
    }

    /**
     * 数値編集セルを初期化する。
     */
    private void initialize() {
        if (Objects.nonNull(this.minLimit)) {
            this.restrictedTextField.setMinLimit(this.minLimit);
        }
        
        if (Objects.nonNull(this.maxLimit)) {
            this.restrictedTextField.setMaxLimit(this.maxLimit);
        }

        this.restrictedTextField.setAlignment(Pos.CENTER_RIGHT);
        this.restrictedTextField.setVerifier(Verifier.NUMBER_ONLY);

        this.restrictedTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String text = restrictedTextField.getText();
                if (org.apache.commons.lang3.StringUtils.isEmpty(text)) {
                    text = "0";
                }

                double value = Double.parseDouble(text);
                
                if (value >= this.minLimit && value <= this.maxLimit) {
                    commitEdit(text);
                } else {
                    commitEdit(restrictedTextField.getOldText());
                }
                
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                super.cancelEdit();
                this.setText(this.getString());
                this.setGraphic(null);
            }
        });

        this.restrictedTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && this.isEditing()){
                commitEdit(restrictedTextField.getText());
            }
        });

        this.restrictedTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // 数値以外入力不可
            if (!this.verifyNumeric(newValue)) {
                this.restrictedTextField.setText(oldValue);
            }
        });
    }

    /**
     * 編集開始
     */
    @Override
    public void startEdit() {
        if (this.isEmpty()) {
            return;
        }
        
        super.startEdit();

        // 編集中のセル
        final TableView<T> table = this.getTableView();
        this.tablePos = table.getEditingCell();

        this.restrictedTextField.setText(this.getString());
        this.restrictedTextField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

        this.setText(null);
        this.setGraphic(this.restrictedTextField);
        this.restrictedTextField.selectAll();
        this.restrictedTextField.requestFocus();
    }

    /**
     * 編集キャンセル
     */
    @Override
    public void cancelEdit() {
        if (this.isEditing()) {
            return;
        }

        // EditCommitイベントを発生
        final TableView<T> table = this.getTableView();
        TableColumn.CellEditEvent editEvent = new TableColumn.CellEditEvent(table, this.tablePos, TableColumn.editCommitEvent(), restrictedTextField.getText());
        Event.fireEvent(getTableColumn(), editEvent);

        super.cancelEdit();

        this.setText(this.restrictedTextField.getText());
        this.setGraphic(null);

        table.edit(-1, null);
    }

    /**
     * 編集完了
     *
     * @param newValue
     */
    @Override
    public void commitEdit(String newValue) {
        this.setText(newValue);
        super.commitEdit(newValue);
    }

    /**
     * セル更新
     */
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            this.setText(null);
            this.setGraphic(null);
        } else {
            if (this.isEditing()) {
                if (this.restrictedTextField != null) {
                    this.restrictedTextField.setText(this.getString());
                }
                this.setText(null);
                this.setGraphic(this.restrictedTextField);
            } else {
                this.setText(this.getString());
                this.setGraphic(null);
            }
        }
    }

    private String getString() {
        return this.getItem() == null ? "" : this.getItem();
    }

    /**
     * 入力データが数値か検証する。
     *
     * @param value
     * @return
     */
    private boolean verifyNumeric(String value) {
        return StringUtils.isEmpty(value) || Pattern.matches("^[0-9]*$", value);
    }
}
