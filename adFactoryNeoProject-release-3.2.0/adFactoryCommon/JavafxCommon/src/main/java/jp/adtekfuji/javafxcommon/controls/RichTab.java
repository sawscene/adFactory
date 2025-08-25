/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;

/**
 * 名前の編集が可能なタブコントロール
 *
 * @author s-heya
 */
public class RichTab extends Tab {

    private final Label label;
    private final TextField textField;
    private boolean editable = false;
    private boolean dragging = false;
    private boolean deleting = false;

    /**
     * コンストラクタ
     */
    public RichTab() {
        this("New Tab", null);
    }

    /**
     * コンストラクタ
     *
     * @param text
     */
    public RichTab(String text) {
        this(text, null);
    }

    /**
     * コンストラクタ
     *
     * @param text
     * @param content
     */
    public RichTab(String text, Node content) {
        super();
        this.label = new Label(text);
        this.label.setMinWidth(28.0);
        this.label.setAlignment(Pos.CENTER);
        this.label.setOnMouseClicked((mouseEvent) -> {
            if (!mouseEvent.isShiftDown() && !mouseEvent.isControlDown() && this.editable && mouseEvent.getClickCount() == 2) {
                this.rename();
            }
        });

        this.setGraphic(label);
        this.setContent(content);

        this.textField = new TextField(text);
        this.textField.setStyle("-fx-background-color: transparent");
        this.textField.setOnAction(event -> this.setGraphic(this.label));
        this.textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (this.textField.getText().isEmpty()) {
                    this.textField.setText(this.label.getText());
                } else {
                    this.label.setText(this.textField.getText());
                }
                this.setGraphic(this.label);
                
                ObservableList<Tab> tabs = this.getTabPane().getTabs();
                if(Objects.nonNull(tabs) && tabs.contains(this)){
                    tabs.set(tabs.indexOf(this), this);
                }
            }
        });
    }

    /**
     * 名前を取得する。
     *
     * @return
     */
    public String getName() {
        return this.label.getText();
    }

    /**
     * 名前を設定する。
     *
     * @param name
     */
    public void setName(String name) {
        this.label.setText(name);
        this.textField.setText(name);
    }

    /**
     * 名前を変更する。
     */
    public void rename() {
        this.setGraphic(this.textField);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {

                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        textField.selectAll();
                        textField.requestFocus();
                    }
                });
            }
        }.start();
    }

    /**
     * 名前を編集可能にする。
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * 名前が編集可能か?
     * @return true:編集可能/false:編集不可
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * ラベルを取得する。
     *
     * @return
     */
    public Label getLabel() {
        return this.label;
    }

    /**
     * ドラッグ中かどうか
     *
     * @return
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * ドラッグ中かどうかを設定する。
     *
     * @param dragging
     */
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    /**
     * 削除中か?
     * @return
     */
    public boolean isDeleting() {
        return deleting;
    }

    /**
     * 削除中か?
     * @param deleting
     */
    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }
}
