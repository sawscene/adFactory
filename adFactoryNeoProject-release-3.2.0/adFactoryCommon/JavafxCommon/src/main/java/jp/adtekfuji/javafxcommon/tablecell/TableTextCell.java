/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.tablecell;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * テキスト編集セル
 *
 * @author s-heya
 */
public class TableTextCell<T> extends TableCell<T, String> {

    private final TextField textField;
    private TablePosition<T, ?> tablePos = null;
    
    /**
     * コンストラクタ
     */
    public TableTextCell() {
        this.textField = new TextField();

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

        this.textField.setText(this.getString());
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

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
        if (this.isEditing()) {
            return;
        }

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
}
