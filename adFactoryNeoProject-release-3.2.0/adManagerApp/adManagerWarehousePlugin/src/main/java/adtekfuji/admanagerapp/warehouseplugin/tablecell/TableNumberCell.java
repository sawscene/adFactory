/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.tablecell;

import adtekfuji.utility.StringUtils;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;

/**
 * 数値編集セル
 *
 * @author s-heya
 */
public class TableNumberCell<T> extends TableCell<T, String> {

    private RestrictedTextField textField;
    private TablePosition<T, ?> tablePos = null;
    private boolean isCommit;

    /**
     * コンストラクタ
     */
    public TableNumberCell() {
    }

    /**
     * 編集開始
     */
    @Override
    public void startEdit() {
        if (this.isEmpty()) {
            return;
        }
        this.isCommit = false;

        
        super.startEdit();

        // 編集中のセル
        final TableView<T> table = this.getTableView();
        this.tablePos = table.getEditingCell();

        this.textField = new RestrictedTextField(this.getString());
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        this.textField.setAlignment(Pos.CENTER_RIGHT);
        this.textField.setVerifier(Verifier.NUMBER_ONLY);

        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                commitEdit(textField.getText());
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                super.cancelEdit();
                this.setText(this.getString());
                this.setGraphic(null);
            }
        });

        this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && this.isEditing()){
                commitEdit(textField.getText());
            }
        });

        this.textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            // 数値以外入力不可
            if (!this.verifyNumeric(newValue)) {
                this.textField.setText(oldValue);
            }
        });

        this.setText(null);
        this.setGraphic(this.textField);
        this.textField.selectAll();
        this.textField.requestFocus();
    }

    /**
     * 編集キャンセル
     */
    @Override
    public void cancelEdit() {
        // EditCommitイベントを発生
        final TableView<T> table = this.getTableView();
        TableColumn.CellEditEvent editEvent = new TableColumn.CellEditEvent(table, this.tablePos, TableColumn.editCommitEvent(), textField.getText());
        Event.fireEvent(getTableColumn(), editEvent);

        super.cancelEdit();

        this.setText(this.textField.getText());
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
        if (this.isCommit) {
            return;
        }
        this.isCommit = true;
        super.commitEdit(newValue);
        this.setText(newValue);
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
                if (this.textField != null) {
                    this.textField.setText(this.getString());
                }
                this.setText(null);
                this.setGraphic(this.textField);
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
