/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.warehouseplugin.controls;

import adtekfuji.locale.LocaleUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 有効・無効切り替え可能なテキストフィールド
 *
 * @author fu-kato
 */
public class CheckTextField extends VBox {

    protected final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField textField;

    public CheckTextField() {
        URL url = getClass().getResource("/fxml/controls/check_text_field.fxml");
        FXMLLoader loader = new FXMLLoader(url, rb);
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();

            setSpacing(8.0);
            setPadding(new Insets(8.0, 8.0, 8.0, 8.0));

            this.textField.disableProperty().bind(checkBox.selectedProperty().not());
        } catch (IOException ex) {
            Logger logger = LogManager.getLogger();
            logger.fatal(ex, ex);
        }
    }

    /**
     * テキストフィールドに入力された値を取得する<br>
     * チェックボックスで選択されていない場合は常に空白文字を返す
     *
     * @return
     */
    public String getText() {
        return isSelected() ? this.textField.getText() : "";
    }
    
    public void setText(String value) {
        this.textField.setText(value);
    }

    public void setLabel(String label) {
        this.checkBox.textProperty().setValue(label);
    }

    public String getLabel() {
        return this.checkBox.textProperty().get();
    }

    public StringProperty labelProperty() {
        return this.checkBox.textProperty();
    }

    public boolean isSelected() {
        return this.checkBox.isSelected();
    }

    public void setSelected(boolean value) {
        this.checkBox.setSelected(value);
    }

    public BooleanProperty selectedProperty() {
        return checkBox.selectedProperty();
    }

}
