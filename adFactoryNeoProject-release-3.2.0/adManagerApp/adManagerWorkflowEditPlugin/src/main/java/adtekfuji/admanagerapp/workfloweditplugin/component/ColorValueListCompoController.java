/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import jp.adtekfuji.javafxcommon.controls.ColorTextBkCell;
import jp.adtekfuji.javafxcommon.controls.InputValueColor;
/**
 * 値リスト設定ダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "ColorValueListCompo", fxmlPath = "/fxml/admanagerworkfloweditplugin/color_value_list_compo.fxml")
public class ColorValueListCompoController implements Initializable, ArgumentDelivery, ListChangeListener {

    private final String NUMBER_ONLY = "^[-|+]?[0-9]+(\\.[0-9]+)?$";

    @FXML
    private TextField textField;
    
    @FXML
    private ListView<InputValueColor> listView;

    private final StringProperty valueProperty = new SimpleStringProperty();
    private ObservableList<InputValueColor> valueList;

    /**
     * 値リスト設定ダイアログを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        final Label InputValueTitleLabel = new Label(LocaleUtils.getString("key.EditInputValueTitle"));
        InputValueTitleLabel.setAlignment(Pos.CENTER);
        
        final Label InputValueFontColorLabel = new Label(LocaleUtils.getString("key.InputValueFontColor"));
        InputValueFontColorLabel.setAlignment(Pos.CENTER);
        
        final Label InputValueBackColorLabel = new Label(LocaleUtils.getString("key.InputValueBackColor"));
        InputValueBackColorLabel.setAlignment(Pos.CENTER);
    }

    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {

        this.valueList = (ObservableList<InputValueColor>) argument;
        this.listView.itemsProperty().set(this.valueList);

        this.textField.textProperty().bindBidirectional(this.valueProperty);
        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.onAdd(null);
                event.consume();
            }
        });
        
        this.listView.setItems(this.valueList);

        ObservableList<InputValueColor> valueListTest;
        valueListTest = this.listView.getItems();
        
        this.listView.fixedCellSizeProperty().set(30.0);
        this.listView.setCellFactory(new Callback<ListView<InputValueColor>, ListCell<InputValueColor>>() {
            @Override
            public ListCell<InputValueColor> call(ListView<InputValueColor> param) {
                return new ColorTextBkCell(param, true, true);
            }
        });
        
        this.listView.setEditable(true);
        this.valueList.removeListener(this);
        this.valueList.addListener(this);
    }

    /**
     * 値リストが変更された。
     *
     * @param change
     */
    @Override
    public void onChanged(ListChangeListener.Change change) {

    }

    /**
     * 追加ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onAdd(ActionEvent event) {
        Platform.runLater(() -> {
            if (StringUtils.isEmpty(this.valueProperty.get())) {
                textField.requestFocus();
                return;
            }

            int ii = this.valueList.indexOf(this.valueProperty.get());
            if (ii < 0) {
                this.valueList.add(new InputValueColor(this.valueProperty.get()));
                ii = valueList.size() - 1;
                this.listView.scrollTo(valueList.size() - 1);
                this.listView.getSelectionModel().select(ii);
                this.listView.getFocusModel().focus(ii);
                this.textField.requestFocus();
                this.valueProperty.set("");
            } else {
                this.listView.scrollTo(ii);
                this.listView.getSelectionModel().select(ii);
                this.listView.getFocusModel().focus(ii);
            }
        });
    }
}
