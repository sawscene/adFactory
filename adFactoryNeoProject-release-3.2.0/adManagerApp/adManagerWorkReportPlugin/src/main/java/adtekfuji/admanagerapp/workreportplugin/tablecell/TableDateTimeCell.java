/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workreportplugin.tablecell;

import adtekfuji.admanagerapp.workreportplugin.common.WorkReportConfig;
import adtekfuji.admanagerapp.workreportplugin.entity.WorkReportRowInfo;
import adtekfuji.admanagerapp.workreportplugin.enumerate.WorkReportWorkTypeEnum;
import adtekfuji.utility.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 時刻編集セル {@literal <mm>:<ss>}の形で保持する
 *
 * @author fu-kato
 */
public class TableDateTimeCell extends TableCell<WorkReportRowInfo, String> {

    private static final List<String> UNEDITABLE = Arrays.asList("stopTimeColumn");

    private TextField textField;
    private TablePosition<WorkReportRowInfo, ?> tablePos = null;
    private boolean isCommit;

    /**
     * コンストラクタ
     */
    public TableDateTimeCell() {
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

        // 間接作業、または、直接作業(直接工数が編集可設定)の場合のみ編集可能
        WorkReportRowInfo row = (WorkReportRowInfo) this.getTableRow().getItem();
        if (Objects.isNull(row)
                || row.getWorkType() == WorkReportWorkTypeEnum.NON_WORK_TIME.getValue()
                || (row.getWorkType() == WorkReportWorkTypeEnum.DIRECT_WORK.getValue() && !WorkReportConfig.getWorkReportDirectWorkEditable())) {
            return;
        }

        switch (WorkReportWorkTypeEnum.valueOf(row.getWorkType())) {
            case INDIRECT_WORK:
                if (UNEDITABLE.contains(this.getId())) {
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
        this.textField.setAlignment(Pos.CENTER_RIGHT);

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
            if (!newValue && this.isEditing()) {
                commitEdit(textField.getText());
            }
        });

        this.textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!this.verifyValue(newValue)) {
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
        String formatted = getFormatString(newValue);
        super.commitEdit(formatted);
        this.setText(formatted);
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
            this.getTableRow().setStyle(null);
        } else {
            WorkReportRowInfo row = (WorkReportRowInfo) this.getTableRow().getItem();
            String rowStyle = null;
            if (Objects.nonNull(row)) {
                if (row.getWorkType() == WorkReportWorkTypeEnum.DIRECT_WORK.getValue()
                        || row.getWorkType() == WorkReportWorkTypeEnum.REWORK.getValue() ) {
                    // 直接作業、後戻り作業・赤作業
                    rowStyle = new StringBuilder("-fx-background-color: ")
                            .append(WorkReportConfig.getWorkReportRowColorDirectWork())
                            .append("; -fx-background-insets: 1 1 1 1; -fx-text-background-color: black;")
                            .toString();
                } else if (row.getWorkType() == WorkReportWorkTypeEnum.NON_WORK_TIME.getValue()) {
                    // 中断時間
                    rowStyle = new StringBuilder("-fx-background-color: ")
                            .append(WorkReportConfig.getWorkReportRowColorNonWork())
                            .append("; -fx-background-insets: 1 1 1 1; -fx-text-background-color: black;")
                            .toString();
                }
            }
            this.getTableRow().setStyle(rowStyle);

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
     * セルの文字列を取得する。
     *
     * @return セルの文字列
     */
    private String getString() {
        return this.getItem() == null ? "" : this.getItem();
    }

    /**
     * フォーマットに合わせた文字列を取得する。
     *
     * @param value
     * @return
     */
    private String getFormatString(String value) {
        if (Objects.isNull(value)) {
            return value;
        }

        List<String> separated = Arrays.asList(value.split(":"));

        final Integer minIn = Integer.valueOf(separated.size() > 0 && !StringUtils.isEmpty(separated.get(0)) ? separated.get(0) : "00");
        final Integer secIn = Integer.valueOf(separated.size() > 1 ? separated.get(1) : "00");
        final Integer min = minIn + secIn / 60;
        final Integer sec = secIn % 60;

        return min.toString() + ":" + String.format("%02d", sec);
    }

    /**
     * 入力データが数値か検証する。
     *
     * @param value
     * @return
     */
    private boolean verifyValue(String value) {
        return StringUtils.isEmpty(value) || Pattern.matches("^[0-9]*:?[0-9]{0,2}$", value);
    }
}
