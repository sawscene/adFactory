/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.controls;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jp.adtekfuji.adFactory.entity.warehouse.TrnMaterialInfo;

/**
 * (資材情報ツリーテーブル) 数値(Integer)編集セル
 *
 * @author nar-nakamura
 */
public class MaterialIntegerCell extends TreeTableCell<TrnMaterialInfo, Number> {

    private TextField textField;
    private TreeTablePosition<TrnMaterialInfo, ?> tablePos = null;

    /**
     * コンストラクタ
     */
    public MaterialIntegerCell() {
        this.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
        this.setAlignment(Pos.CENTER_RIGHT);
        this.setPadding(new Insets(0, 6, 0, 0));
    }

    /**
     * 編集開始
     */
    @Override
    public void startEdit() {
        if (this.isEmpty()) {
            return;
        }
        
        if (Objects.isNull(this.getTreeTableRow().getTreeItem()) || !this.getTreeTableRow().getTreeItem().isLeaf()) {
            // 親アイテムは編集不可
            return;
        }
        
        super.startEdit();

        // 編集中のセル
        final TreeTableView<TrnMaterialInfo> table = this.getTreeTableView();
        this.tablePos = table.getEditingCell();

        this.textField = new TextField(this.getString());
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        this.textField.setAlignment(Pos.CENTER_RIGHT);

        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                commitEdit(this.convertNumber(textField.getText()));
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                super.cancelEdit();
                this.setText(this.getString());
                this.setGraphic(null);
            }
        });

        this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && this.isEditing()) {
                commitEdit(this.convertNumber(textField.getText()));
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
    }

    /**
     * 編集キャンセル
     */
    @Override
    public void cancelEdit() {
        // EditCommitイベントを発生
        final TreeTableView<TrnMaterialInfo> table = this.getTreeTableView();
        TreeTableColumn.CellEditEvent editEvent = new TreeTableColumn.CellEditEvent(table, this.tablePos, TreeTableColumn.editCommitEvent(), this.convertNumber(textField.getText()));
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
    public void commitEdit(Number newValue) {
        super.commitEdit(newValue);
        this.setText(this.convertString(newValue));
    }

    /**
     * セル更新
     */
    @Override
    public void updateItem(Number item, boolean empty) {
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

    /**
     *
     * @return
     */
    private String getString() {
        return this.convertString(this.getItem());
    }

    /**
     *
     * @param value
     * @return
     */
    private String convertString(Number value) {
        return Objects.isNull(value) ? "0" : String.valueOf(value);
    }

    /**
     *
     * @param value
     * @return
     */
    private Integer convertNumber(String value) {
        return StringUtils.isEmpty(value) ? 0 : Integer.parseInt(value);
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
