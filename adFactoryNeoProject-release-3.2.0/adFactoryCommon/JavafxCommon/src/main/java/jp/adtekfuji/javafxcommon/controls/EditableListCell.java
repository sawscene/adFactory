/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * リストのセルの削除・上移動・下移動を可能とするセルコントロール
 *
 * @param <T> オブジェクトの型情報
 * @author fu-kato
 */
public class EditableListCell<T> extends ListCell<T> {

    protected final HBox hbox = new HBox();
    private final Pane pane = new Pane();
    private final Button deleteButton = new Button("x");
    private final Button upButton = new Button("▲");
    private final Button downButton = new Button("▼");

    /**
     * コンストラクタ
     * 
     * @param enableDelete trueの場合、×ボタンを追加しその押下によりリストから項目を削除できる
     * @param enableOrder trueの場合、上下ボタンを追加しその押下によりリスト間で移動が可能となる
     */
    public EditableListCell(boolean enableDelete, boolean enableOrder) {

        createDeleteButton();
        createOrderButton();

        updateButtonAlignment(enableDelete, enableOrder);
    }

    /**
     * ボタンの左に追加で表示するアイテムを追加する。
     *
     * @param item
     */
    protected void addItem(Node item) {
        this.hbox.getChildren().add(0, item);
    }

    private void createDeleteButton() {

        this.deleteButton.getStyleClass().add("DeleteButton");
        this.deleteButton.setOnAction((ActionEvent event) -> {
            getListView().getItems().remove(getItem());
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(hbox);
        }
    }

    private void createOrderButton() {

        this.upButton.getStyleClass().add("DeleteButton");
        this.upButton.setOnAction((ActionEvent event) -> {
            // 順序を一つ上げる
            boolean isSelected = this.isSelected();
            int thisIndex = this.getIndex();
            T thisItem = this.getItem();
            if (0 < thisIndex) {
                getListView().getItems().remove(thisIndex);
                getListView().getItems().add(thisIndex - 1, thisItem);
                if (isSelected) {
                    getListView().getSelectionModel().select(thisIndex - 1);
                }
            }
        });

        this.downButton.getStyleClass().add("DeleteButton");
        this.downButton.setOnAction((ActionEvent event) -> {
            // 順序を一つ下げる
            boolean isSelected = this.isSelected();
            int thisIndex = this.getIndex();
            T thisItem = this.getItem();
            if ((thisIndex + 1) < getListView().getItems().size()) {
                getListView().getItems().remove(thisIndex);
                getListView().getItems().add(thisIndex + 1, thisItem);
                if (isSelected) {
                    getListView().getSelectionModel().select(thisIndex + 1);
                }
            }
        });
    }

    private void updateButtonAlignment(boolean enableDelete, boolean enableOrder) {
        this.hbox.getChildren().clear();

        if (enableDelete) {
            this.hbox.getChildren().addAll(this.deleteButton);
        }

        if (enableOrder) {
            this.hbox.getChildren().addAll(this.upButton, this.downButton);
        }

        if (this.hbox.getChildren().size() > 0) {
            this.hbox.getChildren().add(0, pane);
            this.hbox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(this.pane, Priority.ALWAYS);
            setGraphic(this.hbox);
            setContentDisplay(ContentDisplay.RIGHT);
        } else {
            setGraphic(null);
        }
    }
}
