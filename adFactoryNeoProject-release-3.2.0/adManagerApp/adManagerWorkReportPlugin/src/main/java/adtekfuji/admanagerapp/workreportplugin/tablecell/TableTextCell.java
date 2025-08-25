/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.tablecell;

import adtekfuji.admanagerapp.workreportplugin.common.WorkReportConfig;
import adtekfuji.admanagerapp.workreportplugin.entity.WorkReportRowInfo;
import adtekfuji.admanagerapp.workreportplugin.enumerate.WorkReportWorkTypeEnum;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
 * @author nar-nakamura
 */
public class TableTextCell extends TableCell<WorkReportRowInfo, String> {

    private static final List<String> UNEDITABLE = Arrays.asList("resourcesColumn");

    private TextField textField;
    private TablePosition<WorkReportRowInfo, ?> tablePos = null;
    private boolean isCommit;
    
    private List<TableColumn> directWorkEditableColumns;// 直接作業の編集可能列一覧

    /**
     * コンストラクタ
     */
    public TableTextCell() {
    }

    /**
     * コンストラクタ
     * 
     * @param directWorkEditableColumns 直接作業の編集可能列一覧
     */
    public TableTextCell(List<TableColumn> directWorkEditableColumns) {
        this.directWorkEditableColumns = directWorkEditableColumns;
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

        // 間接作業、または、直接作業(直接工数が編集可設定で、編集許可列に含まれる列)の場合のみ編集可能
        WorkReportRowInfo row = (WorkReportRowInfo) this.getTableRow().getItem();
        if (Objects.isNull(row)) {
            return;
        }

        switch (WorkReportWorkTypeEnum.valueOf(row.getWorkType())) {
            case INDIRECT_WORK:
                if (UNEDITABLE.contains(this.getId())) {
                    return;
                }
                break;

            case NON_WORK_TIME:
                // 編集禁止
                return;
            case DIRECT_WORK:
            case REWORK:
                if (!WorkReportConfig.getWorkReportDirectWorkEditable()) {
                    // 編集禁止
                    return;
                }
                if (!this.directWorkEditableColumns.contains(this.getTableColumn())) {
                    // 編集禁止
                    return;
                }
                break;
            default:
                break;
        }
        
        super.startEdit();

        // 編集中のセル
        final TableView<WorkReportRowInfo> table = this.getTableView();
        this.tablePos = table.getEditingCell();

        this.textField = new TextField(this.getString());
        this.textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

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
        final TableView<WorkReportRowInfo> table = this.getTableView();
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
}
