/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.controls.TraceCustomCell;
import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceCustomEntity;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import static javafx.scene.control.Alert.AlertType.WARNING;

/**
 * カスタム設定値リスト設定ダイアログ
 *
 * @author nar-nakamura
 */
@FxComponent(id = "TraceCustomListCompo", fxmlPath = "/fxml/admanagerworkfloweditplugin/trace_custom_list_compo.fxml")
public class TraceCustomListCompoController implements Initializable, ArgumentDelivery {

    final int MaxRegistNumber = 99;

    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private TextField nameField;
    @FXML
    private TextField valueField;
    @FXML
    private ListView<TraceCustomEntity> listView;

    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty valueProperty = new SimpleStringProperty();

    private ObservableList<TraceCustomEntity> traceCustomList = FXCollections.observableArrayList();

    /**
     * カスタム設定値リスト設定ダイアログを初期化する。
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
        this.traceCustomList = (ObservableList<TraceCustomEntity>) argument;

        // 項目名
        this.nameField.textProperty().bindBidirectional(this.nameProperty);
        this.nameField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.valueField.requestFocus();
                event.consume();
            }
        });

        // 値
        this.valueField.textProperty().bindBidirectional(this.valueProperty);
        this.valueField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.onAdd(null);
                event.consume();
            }
        });

        this.listView.setItems(this.traceCustomList);
        this.listView.fixedCellSizeProperty().set(30.0);
        this.listView.setCellFactory(new Callback<ListView<TraceCustomEntity>, ListCell<TraceCustomEntity>>() {
            @Override
            public ListCell<TraceCustomEntity> call(ListView<TraceCustomEntity> listView) {
                return new TraceCustomCell();
            }
        });
        this.listView.setEditable(true);
    }

    /**
     * 追加ボタンのアクション
     *
     * @param event 
     */
    @FXML
    private void onAdd(ActionEvent event) {
        Platform.runLater(() -> {
            // 項目名が未入力の場合、項目名入力欄にフォーカス移動
            if (StringUtils.isEmpty(this.nameProperty.get())) {
                this.nameField.requestFocus();
                return;
            }

            // 値が未入力の場合、値入力欄にフォーカス移動
            if (StringUtils.isEmpty(this.valueProperty.get())) {
                this.valueField.requestFocus();
                return;
            }

            // 最大件数を超えた場合に警告表示を出す
            if(traceCustomList.size()>=MaxRegistNumber
                    && ButtonType.CANCEL==sc.showOkCanselDialog(
                            WARNING,
                            LocaleUtils.getString("key.Warning"),
                            String.format(LocaleUtils.getString("key.warn.RecordLimitOver"),MaxRegistNumber),
                            LocaleUtils.getString("key.warn.RecordLimitOverDetail")))
            {
                return;
            };


            // 項目名が登録済の場合は警告表示し、未登録の場合はリストに追加する。
            TraceCustomEntity traceCustom;
            Optional<TraceCustomEntity> item = this.traceCustomList.stream().filter(p -> p.getName().equals(this.nameProperty.get())).findFirst();
            if (item.isPresent()) {
                traceCustom = item.get();

                // 重複する行が表示されるようスクロールして選択状態にする。
                this.listView.scrollTo(traceCustom);
                this.listView.getSelectionModel().clearSelection();
                this.listView.getSelectionModel().select(traceCustom);

                // 登録済の項目名の場合は警告ダイアログを表示する。
                String msg = String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), this.nameProperty.get());
                sc.showAlert(WARNING, LocaleUtils.getString("key.Warning"), msg);
            } else {
                traceCustom = new TraceCustomEntity();
                traceCustom.setName(this.nameProperty.get());
                traceCustom.setValue(this.valueProperty.get());
                this.traceCustomList.add(traceCustom);

                // 追加した行が表示されるようスクロールして選択状態にする。
                this.listView.scrollTo(traceCustom);
                this.listView.getSelectionModel().clearSelection();
                this.listView.getSelectionModel().select(traceCustom);

                // 入力欄をクリアする。
                this.nameField.clear();
                this.valueField.clear();
            }

            // 項目名入力欄にフォーカスを移動する。
            this.nameField.requestFocus();
        });
    }
}
