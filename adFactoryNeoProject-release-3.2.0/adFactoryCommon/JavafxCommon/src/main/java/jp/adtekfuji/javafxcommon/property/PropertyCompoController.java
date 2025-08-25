/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * プロパティーダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "PropertyCompo", fxmlPath = "/fxml/javafxcommon/property_compo.fxml")
public class PropertyCompoController implements Initializable, ArgumentDelivery {

    @FXML
    private Label titleLabel;
    @FXML
    private TextArea textArea;

    /**
     * プロパティーダイアログを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof String[]) {
            String[] array = (String[]) argument;
            this.titleLabel.setText(array[0]);
            this.textArea.setText(array[1]);
        }
    }
}
