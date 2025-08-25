/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.controls;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import jp.adtekfuji.javafxcommon.utils.TextUtils;

/**
 * 色指定可能なテキストセルコントロール
 *
 * @author nar-nakamura
 */
public class ColorTextCell extends ListCell<ColorTextCellData> {
    private final HBox hbox = new HBox();
    private final Label label = new Label();
    private final TextField textField = new TextField();
    private final Pane pane = new Pane();
    private final Button deleteButton = new Button("x");
    private String text;
    private Color textColor;
    private final ListView<ColorTextCellData> listView;

    /**
     * コンストラクタ
     *
     * @param listView
     */
    public ColorTextCell(ListView<ColorTextCellData> listView) {
        super();

        this.listView = listView;

        this.textField.setVisible(false);
        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // 編集を完了する
                ColorTextCell.this.commitEdit(new ColorTextCellData(ColorTextCell.this.textField.getText(), this.textColor));
            }
        });

        this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && ColorTextCell.this.isEditing()){
                // 編集をキャンセルする
                ColorTextCell.this.cancelEdit();
            }
        });

        this.deleteButton.getStyleClass().add("DeleteButton");
        this.deleteButton.setOnAction((ActionEvent event) -> {
            // リストからアイテムを削除する
            Optional<ColorTextCellData> target = ColorTextCell.this.listView.getItems().stream().filter(p -> text.equals(p.getText())).findFirst();
            if (target.isPresent()) {
                ColorTextCell.this.listView.getItems().remove(target.get());
            }
        });

        this.hbox.setAlignment(Pos.CENTER_LEFT);
        this.hbox.getChildren().addAll(label, pane, deleteButton);
        HBox.setHgrow(this.pane, Priority.ALWAYS);
    }

    /**
     * セルの内容を更新する
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(ColorTextCellData item, boolean empty) {
        super.updateItem(item, empty);
        this.setText(null);
        if (empty) {
            this.text = null;
            this.textColor = null;
            this.setGraphic(null);
        } else {
            this.text = item.getText();
            this.textColor = item.getTextColor();
            this.label.setText(Objects.nonNull(item.getText()) ? item.getText() : "<null>");
            this.label.setTextFill(Objects.nonNull(item.getTextColor()) ? item.getTextColor() : Color.BLACK);
            this.setGraphic(this.hbox);
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

        if (!this.getListView().isEditable()) {
            return;
        }

        this.textField.setText(this.text);
        double width = TextUtils.computeTextWidth(this.textField.getFont(), this.text, 300.0D) + 40.0D;
        this.textField.setPrefWidth(width);
        this.textField.setVisible(true);
        this.hbox.getChildren().remove(this.label);
        this.hbox.getChildren().add(0, this.textField);
        super.startEdit();

        this.textField.requestFocus();
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
            this.hbox.getChildren().add(0, this.label);
            super.cancelEdit();
        }
    }

    /**
     * 編集を完了する
     *
     * @param newValue
     */
    @Override
    public void commitEdit(ColorTextCellData newValue) {
        if (this.isEditing()) {
            if (StringUtils.isEmpty(newValue.getText())) {
                super.cancelEdit();
                this.hbox.getChildren().remove(this.textField);
                this.hbox.getChildren().add(0, this.label);
                // リストからアイテムを削除する
                Optional<ColorTextCellData> target = ColorTextCell.this.listView.getItems().stream().filter(p -> text.equals(p.getText())).findFirst();
                if (target.isPresent()) {
                    ColorTextCell.this.listView.getItems().remove(target.get());
                }
                return;
            }

            this.text = newValue.getText();
            this.textColor = newValue.getTextColor();
            this.label.setText(this.text);
            this.label.setTextFill(this.textColor);
            this.hbox.getChildren().remove(this.textField);
            this.hbox.getChildren().add(0, this.label);
            super.commitEdit(newValue);
        }
    }
}
