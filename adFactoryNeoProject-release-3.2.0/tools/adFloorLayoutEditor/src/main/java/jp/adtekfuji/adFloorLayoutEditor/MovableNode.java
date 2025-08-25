/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFloorLayoutEditor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

/**
 *
 * @author ke.yokoi
 */
public class MovableNode {

    @Getter
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty(false);

    private Boolean isMovable = true;
    private Node node;
    private Point2D dragAnchor;
    private double posX = 0;
    private double posY = 0;

    public MovableNode() {
    }

    public void setNode(final Node node) {
        this.node = node;
        //change a cursor when it is over circle
        node.setCursor(Cursor.HAND);
        //フォーカスを有効にする
        node.setFocusTraversable(true);
        //add a mouse listeners
        node.setOnMouseClicked((MouseEvent me) -> {
            //the event will be passed only to the circle which is on front
            me.consume();
        });
        node.setOnMouseDragged((MouseEvent me) -> {
            if (isMovable && dragAnchor != null) {
                double dragX = me.getSceneX() - dragAnchor.getX();
                double dragY = me.getSceneY() - dragAnchor.getY();
                node.setTranslateX(posX + dragX);
                node.setTranslateY(posY + dragY);
            }
        });
        node.setOnMouseEntered((MouseEvent me) -> {
            //change the z-coordinate of the circle
            node.toFront();
        });
        node.setOnMouseExited((MouseEvent me) -> {
        });
        node.setOnMousePressed((MouseEvent me) -> {
            if (isMovable) {
                //when mouse is pressed, store initial position
                posX = node.getTranslateX();
                posY = node.getTranslateY();
                dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
                //フォーカスを要求.
                node.requestFocus();
            }
        });
    }

    public void Movable(Boolean flg) {
        isMovable = flg;
        if (!isMovable) {
            dragAnchor = null;
        }
    }

    public Node getNode() {
        return node;
    }

    public void remove() {
        node.setVisible(false);
    }

    public void update(Boolean visible, double posX, double posY) {
        node.setVisible(visible);
        node.setTranslateX(posX);
        node.setTranslateY(posY);
    }
}
