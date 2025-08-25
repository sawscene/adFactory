/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.tablecell;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.enumeration.Verifier;

/**
 * テキスト編集セル
 *
 * @author s-heya
 */
public class TableRestrictedTextCell<T> extends TableCell<T, String> {

    private final RestrictedTextField restrictedTextField;
    private TablePosition<T, ?> tablePos = null;
    
    /**
     * コンストラクタ
     * 
     * @param verifier
     * @param maxLength
     */
    public TableRestrictedTextCell(Verifier verifier, Integer maxLength) {
        this.restrictedTextField = new RestrictedTextField();
        this.initialize(verifier, maxLength, null);
    }

    /**
     * コンストラクタ
     * 
     * @param verifier
     * @param maxLength
     * @param textFormat 
     */
    public TableRestrictedTextCell(Verifier verifier, Integer maxLength, String textFormat) {
        this.restrictedTextField = new RestrictedTextField();
        this.initialize(verifier, maxLength, textFormat);
    }
    
    /**
     * テキスト編集セルを初期化する。
     */
    private void initialize(Verifier verifier, Integer maxLength, String textFormat) {
        if (Objects.nonNull(verifier)) {
            this.restrictedTextField.setVerifier(verifier);
        }
        
        if (Objects.nonNull(maxLength)) {
            this.restrictedTextField.setMaxLength(maxLength);
        }

        if (!StringUtils.isEmpty(textFormat)) {
            this.restrictedTextField.setTextFormat(textFormat);
        }

        this.restrictedTextField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                commitEdit(restrictedTextField.getText());
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
}
