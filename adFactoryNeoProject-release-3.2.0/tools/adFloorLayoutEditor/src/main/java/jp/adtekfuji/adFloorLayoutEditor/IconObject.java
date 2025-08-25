/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFloorLayoutEditor;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import lombok.Getter;

/**
 *
 * @author ke.yokoi
 */
public class IconObject {

    @Getter
    private final int id;
    @Getter
    private final MovableNode body = new MovableNode();
    @Getter
    private final MovableNode grip = new MovableNode();

    @Getter
    private final StackPane stack = new StackPane();
    private final Rectangle bodyRect = new Rectangle();
    private final Rectangle gripRect = new Rectangle();
    @Getter
    private final Label label;

    private boolean isInit = true;

    /**
     * RectObject
     *
     * @param id
     * @param text
     * @param foreColor
     * @param backColor
     * @param posX
     * @param posY
     * @param width
     * @param height
     */
    public IconObject(final int id, final String text, final Color foreColor, final Color backColor, double posX, double posY, double width, double height) {

        this.id = id;
        //形状作成.
        stack.translateXProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Setting.SetIconPosX(id, newValue.doubleValue());
            if (!this.isInit) {
                Changed.setChanged(true);
            }
        });
        stack.translateYProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Setting.SetIconPosY(id, newValue.doubleValue());
            if (!this.isInit) {
                Changed.setChanged(true);
            }
        });
        stack.prefWidthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Setting.SetIconWidth(id, newValue.doubleValue());
            if (!this.isInit) {
                Changed.setChanged(true);
            }
        });
        stack.prefHeightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            Setting.SetIconHeight(id, newValue.doubleValue());
            if (!this.isInit) {
                Changed.setChanged(true);
            }
        });
        stack.translateXProperty().set(posX);
        stack.translateYProperty().set(posY);
        stack.prefWidthProperty().set(width);
        stack.prefHeightProperty().set(height);
        bodyRect.widthProperty().bind(stack.prefWidthProperty());
        bodyRect.heightProperty().bind(stack.prefHeightProperty());
        bodyRect.setFill(backColor);
        bodyRect.setId(String.valueOf(id));
        bodyRect.setStrokeWidth(1);
        bodyRect.setStroke(Color.BLACK);
        label = new Label(text);
        label.setTextFill(foreColor);
        label.setFont(new Font("メイリオ", 20));
        label.setContentDisplay(ContentDisplay.CENTER);
        label.setAlignment(Pos.CENTER);
        label.prefWidthProperty().bind(stack.prefWidthProperty());
        label.prefHeightProperty().bind(stack.prefHeightProperty());
        label.setMouseTransparent(true);
        //形状登録.
        stack.getChildren().addAll(bodyRect, label);
        stack.setAlignment(Pos.TOP_LEFT);
        body.setNode(stack);

        //サイズ変更用の形状作成.
        gripRect.translateXProperty().bindBidirectional(stack.prefWidthProperty());
        gripRect.translateYProperty().bindBidirectional(stack.prefHeightProperty());
        gripRect.translateXProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.doubleValue() < Setting.GetIconMinWidth()) {
                gripRect.translateXProperty().set(Setting.GetIconMinWidth());
            }
        });
        gripRect.translateYProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (newValue.doubleValue() < Setting.GetIconMinHeight()) {
                gripRect.translateYProperty().set(Setting.GetIconMinHeight());
            }
        });
        gripRect.setWidth(10);
        gripRect.setHeight(10);
        gripRect.setFill(Color.BLUE);
        gripRect.setStrokeWidth(1);
        gripRect.setStroke(Color.BLACK);
        grip.setNode(gripRect);

        //選択されたときのデザイン変更
        body.getSelectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                bodyRect.setStrokeWidth(2);
                bodyRect.setStroke(Color.BLUE);
                bodyRect.getStrokeDashArray().addAll(5.0, 5.0);
                stack.getChildren().add(gripRect);
            } else {
                bodyRect.setStrokeWidth(1);
                bodyRect.setStroke(Color.BLACK);
                bodyRect.getStrokeDashArray().clear();
                stack.getChildren().remove(gripRect);
            }
        });

        this.isInit = false;
    }

    public void moveNode(double x, double y) {
        stack.translateXProperty().set(stack.getTranslateX() + x);
        stack.translateYProperty().set(stack.getTranslateY() + y);
    }

    public Node getNode() {
        return stack;
    }

    public Boolean hasNode(Node node) {
        return stack.getChildren().contains(node);
    }

    public void setSelected(Node selectNode) {
        body.getSelectedProperty().set(true);
        if (selectNode.equals(bodyRect) || selectNode.equals(label)) {
            body.Movable(true);
            //grip.Movable(false);
        }
        if (selectNode.equals(gripRect)) {
            body.Movable(false);
            //grip.Movable(true);
        }
    }

    public void setUnselected() {
        body.getSelectedProperty().set(false);
    }

    public IconObjectBackup getBackup() {
        IconObjectBackup backup = new IconObjectBackup();
        backup.setXPos(this.stack.getTranslateX());
        backup.setYPos(this.stack.getTranslateY());
        backup.setWidth(this.stack.getPrefWidth());
        backup.setHeight(this.stack.getPrefHeight());
        return backup;
    }

    public void setRestore(IconObjectBackup backup) {
        this.stack.setTranslateX(backup.getXPos());
        this.stack.setTranslateY(backup.getYPos());
        this.stack.setPrefWidth(backup.getWidth());
        this.stack.setPrefHeight(backup.getHeight());
    }
}
