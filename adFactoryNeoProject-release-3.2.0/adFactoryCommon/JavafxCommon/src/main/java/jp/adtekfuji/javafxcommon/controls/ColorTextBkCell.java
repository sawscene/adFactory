/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import adtekfuji.utility.StringUtils;
import java.util.ArrayList;
import java.util.List;
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
import jp.adtekfuji.javafxcommon.utils.TextUtils;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 * 文字色、背景色指定可能なテキストセルコントロール
 *
 */
public class ColorTextBkCell extends ListCell<InputValueColor> {
    
    private final HBox hbox = new HBox();
    private final Label label = new Label();
    private final TextField textField = new TextField();
    private final Pane pane = new Pane();
    private final Button deleteButton = new Button("x");
    private final Button upButton = new Button("▲");
    private final Button downButton = new Button("▼");
    private String text;
    private Color textColor = Color.BLACK;
    private Color textBkColor = Color.WHITE;
    private final ListView<InputValueColor> listView;
    
    public ColorPicker textColorParam = new ColorPicker();
    public ColorPicker textBkColorParam = new ColorPicker();

    private InputValueColor data;
    
    private final Label labelTitleValue = new Label();
    private final Label labelTitleTextColor = new Label();
    private final Label labelTitleTextBkColor = new Label();
    
    /**
     * コンストラクタ
     *
     * @param listView
     */
    public ColorTextBkCell(ListView<InputValueColor> listView) {
        this(listView, true, false);
    }
    
    /**
     * コンストラクタ
     *
     * @param listView
     */
    public ColorTextBkCell(ListView<InputValueColor> listView, boolean enableDelete, boolean enableOrder) {
        super();

        this.listView = listView;
        this.textField.setVisible(false);
        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // 編集を完了する
                ColorTextBkCell.this.commitEdit(new InputValueColor(ColorTextBkCell.this.textField.getText(), StringUtils.colorToRGBCode(this.textColor), StringUtils.colorToRGBCode(this.textBkColor)));
            }
        });

        this.textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && ColorTextBkCell.this.isEditing()){
                // 編集をキャンセルする
                ColorTextBkCell.this.cancelEdit();
            }
        });

        this.textColorParam.setValue(textColor);
        this.textColorParam.getStyleClass().add("textColor");
        this.textColorParam.setOnAction((ActionEvent event) -> {
            // 文字色を記憶する.
            this.textColor = this.textColorParam.getValue();
            if (Objects.nonNull(this.data)) {
                this.data.setTextColor(StringUtils.colorToRGBCode(textColor));
            }
        });

        this.textBkColorParam.setValue(textBkColor);
        this.textBkColorParam.getStyleClass().add("textBkColor");
        this.textBkColorParam.setOnAction((ActionEvent event) -> {
            // 背景色を記憶する.
            this.textBkColor = textBkColorParam.getValue();
            if (Objects.nonNull(this.data)) {
                this.data.setTextBkColor(StringUtils.colorToRGBCode(this.textBkColor));
            }
        });

        this.upButton.getStyleClass().add("UpButton");
        this.upButton.setOnAction((ActionEvent event) -> {
            // 順序を一つ上げる
            boolean isSelected = ColorTextBkCell.this.isSelected();
            int thisIndex = ColorTextBkCell.this.getIndex();
            InputValueColor thisItem = ColorTextBkCell.this.getItem();
            if (0 < thisIndex) {
                ColorTextBkCell.this.listView.getItems().remove(thisIndex);
                ColorTextBkCell.this.listView.getItems().add(thisIndex - 1, thisItem);
                if (isSelected) {
                    ColorTextBkCell.this.listView.getSelectionModel().select(thisIndex - 1);
                }
            }
        });

        this.downButton.getStyleClass().add("DownButton");
        this.downButton.setOnAction((ActionEvent event) -> {
            // 順序を一つ下げる
            boolean isSelected = ColorTextBkCell.this.isSelected();
            int thisIndex = ColorTextBkCell.this.getIndex();
            InputValueColor thisItem = ColorTextBkCell.this.getItem();
            if ((thisIndex + 1) < ColorTextBkCell.this.listView.getItems().size()) {
                ColorTextBkCell.this.listView.getItems().remove(thisIndex);
                ColorTextBkCell.this.listView.getItems().add(thisIndex + 1, thisItem);
                if (isSelected) {
                    ColorTextBkCell.this.listView.getSelectionModel().select(thisIndex + 1);
                }
            }
        });

        this.deleteButton.getStyleClass().add("DeleteButton");
        this.deleteButton.setOnAction((ActionEvent event) -> {
            // リストからアイテムを削除する
            Optional<InputValueColor> target = ColorTextBkCell.this.listView.getItems().stream().filter(p -> text.equals(p.getText())).findFirst();

            if (target.isPresent()) {
                ColorTextBkCell.this.listView.getItems().remove(target.get());
            }
        });

        List<Label> listTitle = new ArrayList<Label>();
        labelTitleValue.setText("値");
        labelTitleTextColor.setText("文字色");;
        labelTitleTextBkColor.setText("背景色");

        listTitle.add(labelTitleValue);
        listTitle.add(labelTitleTextColor);
        listTitle.add(labelTitleTextBkColor);
        
        this.hbox.setAlignment(Pos.CENTER_LEFT);
        int listRow = this.hbox.getChildren().size();
        if(listRow > 0)
        {
            this.hbox.setSpacing(5.0);
            this.hbox.getChildren().addAll(label, pane, textColorParam, textBkColorParam, deleteButton, upButton, downButton);
        }
        else
        {
            this.hbox.setSpacing(5.0);
            this.hbox.getChildren().addAll(label, pane, textColorParam, textBkColorParam, deleteButton, upButton, downButton);
        }

        HBox.setHgrow(this.pane, Priority.ALWAYS);
        setGraphic(this.hbox);
    }

    /**
     * セルの内容を更新する
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(InputValueColor item, boolean empty) {
        super.updateItem(item, empty);
        this.setText(null);
        if (empty) {
            this.data = null;
            this.text = null;
            this.textColor = null;
            this.textBkColor = null;
            this.setGraphic(null);
        } else {
            this.data = item;
            this.text = item.getText();
            this.textColor = Color.valueOf(item.getTextColor());
            this.textBkColor = Color.valueOf(item.getTextBkColor());
            
            this.label.setText(Objects.nonNull(item.getText()) ? item.getText() : "<null>");
            this.textColorParam.setValue(Objects.nonNull(Color.valueOf(item.getTextColor())) ? Color.valueOf(item.getTextColor()) : Color.BLACK);
            this.textBkColorParam.setValue(Objects.nonNull(Color.valueOf(item.getTextBkColor())) ? Color.valueOf(item.getTextBkColor()) : Color.WHITE);
            
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
    public void commitEdit(InputValueColor newValue) {
        if (this.isEditing()) {
            if (StringUtils.isEmpty(newValue.getText())) {
                super.cancelEdit();
                this.hbox.getChildren().remove(this.textField);
                this.hbox.getChildren().add(0, this.label);
                this.hbox.getChildren().add(0, this.textColorParam);
                this.hbox.getChildren().add(0, this.textBkColorParam);
                // リストからアイテムを削除する
                Optional<InputValueColor> target = ColorTextBkCell.this.listView.getItems().stream().filter(p -> text.equals(p.getText())).findFirst();
                if (target.isPresent()) {
                    ColorTextBkCell.this.listView.getItems().remove(target.get());
                }
                return;
            }

            this.text = newValue.getText();
            this.textColor = Color.valueOf(newValue.getTextColor());
            this.textBkColor = Color.valueOf(newValue.getTextBkColor());
            
            this.label.setText(this.text);
            this.textColorParam.setValue(this.textColor);
            this.textBkColorParam.setValue(this.textBkColor);
            
            this.hbox.getChildren().remove(this.textField);
            this.hbox.getChildren().add(0, this.label);
            super.commitEdit(newValue);
        }
    } 
}
