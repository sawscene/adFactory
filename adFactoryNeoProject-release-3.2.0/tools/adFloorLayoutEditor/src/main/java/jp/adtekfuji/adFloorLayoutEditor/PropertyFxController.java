package jp.adtekfuji.adFloorLayoutEditor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author itage
 */
@FxComponent(id = "Property", fxmlPath = "/fxml/property.fxml")
public class PropertyFxController implements Initializable, ArgumentDelivery {

    @FXML
    private Label nameLabel;
    @FXML
    private TextField xPosField;
    @FXML
    private TextField yPosField;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof IconObject) {
            IconObject icon = (IconObject) argument;
            nameLabel.setText(icon.getLabel().getText());
            xPosField.setText(Double.toString(icon.getStack().getTranslateX()));
            yPosField.setText(Double.toString(icon.getStack().getTranslateY()));
            widthField.setText(Double.toString(icon.getStack().getPrefWidth()));
            heightField.setText(Double.toString(icon.getStack().getPrefHeight()));

            xPosField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                icon.getStack().setTranslateX(Double.valueOf(newValue));
            });
            yPosField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                icon.getStack().setTranslateY(Double.valueOf(newValue));
            });
            widthField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                icon.getStack().setPrefWidth(Double.valueOf(newValue));
            });
            heightField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                icon.getStack().setPrefHeight(Double.valueOf(newValue));
            });
        }
    }
}
