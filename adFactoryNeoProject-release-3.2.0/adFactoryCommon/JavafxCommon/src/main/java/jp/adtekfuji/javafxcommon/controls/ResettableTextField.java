/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;
 
import adtekfuji.utility.StringUtils;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
 
/**
 * リセットボタン付きのテキストフィールド
 * 
 * @author s-heya
 */
public class ResettableTextField extends GridPane {
 
    TextField textField = null;
    Group resetButton = null;
 
    /**
     * コンストラクタ
     */
    public ResettableTextField() {
        this.textField = new TextField();
        //this.textField.getStyleClass().add("resettable-text-field");
        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isEmpty(newValue)) {
                this.resetButton.setVisible(false);
            } else {
                this.resetButton.setVisible(true);
            }
        });
 
        this.resetButton = createResetButton();
        this.resetButton.setOnMouseClicked((MouseEvent t) -> {
            this.resetButton.setVisible(false);
            this.textField.clear();
        });
 
        // this.setLayoutX(1.0);
        // this.setLayoutY(1.0);
        this.setHgap(4.0);
        
        ColumnConstraints textFieldComponent = new ColumnConstraints();
        textFieldComponent.setHgrow(Priority.SOMETIMES);
        textFieldComponent.setMinWidth(10.0);
        this.getColumnConstraints().add(textFieldComponent);
        
        ColumnConstraints buttonComponent = new ColumnConstraints();
        this.getColumnConstraints().add(buttonComponent);
        textFieldComponent.setMinWidth(10.0);
        this.add(this.textField, 0, 0);
        this.add(this.resetButton, 1, 0);

    }
 
    /**
     * リセットボタンを生成する。
     * 
     * @return Group
     */
    private Group createResetButton() {
        Group group = new Group();
        
        final Circle circle = new Circle();
        circle.setRadius(7.0);
        
        final Rectangle rect1 = this.createRect();
        rect1.setRotate(-45);
        
        final Rectangle rect2 = this.createRect();
        rect2.setRotate(45);
        
        group.setOnMouseEntered((MouseEvent event) -> {
            circle.setFill(Color.web("#097dda"));
            rect1.setFill(Color.WHITE);
            rect2.setFill(Color.WHITE);
        });
        
        group.setOnMouseExited((MouseEvent event) -> {
            circle.setFill(Color.web("#d3d3d3"));
            rect1.setFill(Color.web("#868686"));
            rect2.setFill(Color.web("#868686"));
        });

        group.setVisible(false);
        circle.setFill(Color.web("#d3d3d3"));
        group.getChildren().addAll(circle, rect1, rect2);
        return group;
    }
 
    /**
     * 短形を生成する。
     * 
     * @return Rectangle
     */
    private Rectangle createRect() {
        Rectangle rect = new Rectangle(8.0, 2.0);
        rect.setX(-4.0);
        rect.setY(-1.0);
        rect.setStrokeWidth(1.0);
        rect.setFill(Color.web("#868686"));
        return rect;
    }

    /**
     * 入力されたテキストを取得する。
     * 
     * @return テキスト 
     */
    public String getText() {
        return this.textField.getText();
    }
    
    /**
     * テキストフィールドを取得する。
     * 
     * @return TextField
     */
    public TextField getTextField() {
        return this.textField;
    }
    
    /**
     * リセットボタンを取得する。
     * 
     * @return Group
     */
    public Group getReseyButton() {
        return this.resetButton;
    }
}