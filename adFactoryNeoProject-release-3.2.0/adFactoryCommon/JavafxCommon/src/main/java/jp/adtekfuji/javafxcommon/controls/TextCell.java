/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jp.adtekfuji.javafxcommon.utils.TextUtils;

/**
 * テキストセルコントロール
 *
 * @author s-heya
 */
public class TextCell extends EditableListCell<String> {

    private final Label label = new Label();
    private final TextField textField = new TextField();
    private String text;

    /**
     * コンストラクタ
     *
     * @param listView
     */
    public TextCell(ListView<String> listView) {
        this(listView, true, false);
    }

    /**
     * テキストのみを持つリストセルコンストラクタ
     *
     * @param listView
     * @param enableDelete trueの場合、削除可能な×ボタンをもつセルを作成する
     * @param enableOrder trueの場合、上下移動が可能なボタンを持つセルを作成する
     */
    public TextCell(ListView<String> listView, boolean enableDelete, boolean enableOrder) {
        super(enableDelete, enableOrder);

        this.textField.setVisible(false);
        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // 編集を完了する
                TextCell.this.commitEdit(TextCell.this.textField.getText());
            }
        });

        this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && TextCell.this.isEditing()) {
                // 編集をキャンセルする
                TextCell.this.cancelEdit();
            }
        });

        addItem(label);
    }

    /**
     * セルの内容を更新する
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        this.setText(null);
        if (empty) {
            this.text = null;
            this.setGraphic(null);
        } else {
            this.text = item;
            this.label.setText(Objects.nonNull(item) ? item : "<null>");
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
    public void commitEdit(String newValue) {
        if (this.isEditing()) {
            if (StringUtils.isEmpty(newValue)) {
                super.cancelEdit();
                this.hbox.getChildren().remove(this.textField);
                this.hbox.getChildren().add(0, this.label);
                getListView().getItems().remove(text);
                return;
            }

            this.text = newValue;
            this.label.setText(this.text);
            this.hbox.getChildren().remove(this.textField);
            this.hbox.getChildren().add(0, this.label);
            super.commitEdit(newValue);
        }
    }
}
