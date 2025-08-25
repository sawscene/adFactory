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
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;
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
 * 日付編集セル
 *
 * @author nar-nakamura
 */
public class TableDateCell extends TableCell<WorkReportRowInfo, String> {

    private TextField textField;
    private TablePosition<WorkReportRowInfo, ?> tablePos = null;
    private boolean isCommit;

    /**
     * コンストラクタ
     */
    public TableDateCell() {
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

        // 間接作業のみ編集可能
        WorkReportRowInfo row = (WorkReportRowInfo) this.getTableRow().getItem();
        if (Objects.isNull(row) || row.getWorkType() != WorkReportWorkTypeEnum.INDIRECT_WORK.getValue()) {
            return;
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
        String monthDay = getFormatString(newValue);
        super.commitEdit(monthDay);
        this.setText(monthDay);
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
     *
     * @return
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

        String ret;
        String[] sepValue = value.split("/");
        int[] values = new int[sepValue.length];
        int year = LocalDate.now().getYear();

        for (int cnt = 0; cnt < sepValue.length; cnt++) {
            values[cnt] = StringUtils.parseInteger(sepValue[cnt]);
        }
        switch (sepValue.length) {
            case 2:
                ret = String.format("%02d/%02d", values[0], values[1]);
                break;
            case 3:
                if (values[0] < 1000) {
                    values[0] = year - (year % 1000) + values[0];
                }
                ret = String.format("%04d/%02d/%02d", values[0], values[1], values[2]);
                break;
            default:
                ret = value;
                break;
        }

        return ret;
    }

    /**
     * 入力文字を検証する。
     *
     * @param value
     * @return
     */
    private boolean verifyValue(String value) {
//        return StringUtils.isEmpty(value) || Pattern.matches("^[0-9]{1,2}[/]?[0-9]{0,2}$", value);
        return StringUtils.isEmpty(value) || Pattern.matches("^[0-9]{1,4}[/]?[0-9]{0,2}[/]?[0-9]{0,2}$", value);
    }
}
