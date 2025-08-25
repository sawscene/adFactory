/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
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
import jp.adtekfuji.javafxcommon.controls.TextCell;

/**
 * 値リスト設定ダイアログ
 *
 * @author s-heya
 */
@FxComponent(id = "ValueListCompo", fxmlPath = "/fxml/compo/value_list_compo.fxml")
public class ValueListCompoController implements Initializable, ArgumentDelivery, ListChangeListener {

    private final String NUMBER_ONLY = "^[-|+]?[0-9]+(\\.[0-9]+)?$";

    @FXML
    private TextField textField;
    @FXML
    private ListView<String> listView;

    private final StringProperty valueProperty = new SimpleStringProperty();
    private ObservableList<String> valueList;

    /**
     * 値リスト設定ダイアログを初期化する。
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
        this.valueList = (ObservableList<String>) argument;

        this.textField.textProperty().bindBidirectional(this.valueProperty);
        this.textField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.onAdd(null);
                event.consume();
            }
        });

        // 値リスト
        this.listView.setItems(this.valueList);
        this.listView.fixedCellSizeProperty().set(30.0);
        this.listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new TextCell(param, true, true);
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
//        try {
//            this.valueList.removeListener(this);
//
//            this.sort();
//        }
//        finally {
//            this.valueList.addListener(this);
//        }
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
                valueList.add(this.valueProperty.get());
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

    /**
     * リストをソートする。
     *
     */
    private void sort() {
        // 数値かどうか調べる
        boolean isNumberOnly = true;
        for (String value : valueList) {
            if (!value.matches(NUMBER_ONLY)) {
                isNumberOnly = false;
                break;
            }
        }

        if (isNumberOnly) {
            // 数値でソート
            Collections.sort(valueList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return (Double.parseDouble(o1) >= Double.parseDouble(o2)) ? 1 : -1;
                }
            });
        } else {
            // 文字列でソート
            Collections.sort(valueList);
        }
    }
}
