

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.controls.ComponentCell;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * 構成部品リスト設定ダイアログ
 *
 * @author itage
 */
@FxComponent(id = "ComponentCompo", fxmlPath = "/fxml/admanagerworkfloweditplugin/component_compo.fxml")
public class ComponentCompoController implements Initializable, ArgumentDelivery {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private TextField nameField;
    @FXML
    private TextField ruleField;
    @FXML
    private TextField valueField;
    @FXML
    private ListView<TraceCustomEntity> listView;

    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty ruleProperty = new SimpleStringProperty();
    private final StringProperty valueProperty = new SimpleStringProperty();

    private ObservableList<TraceCustomEntity> Component = FXCollections.observableArrayList();

    /**
     * 構成部品リスト設定ダイアログを初期化する。
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
        this.Component = (ObservableList<TraceCustomEntity>) argument;

        // 品名
        this.nameField.textProperty().bindBidirectional(this.nameProperty);
        this.nameField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.ruleField.requestFocus();
                event.consume();
            }
        });

        // 入力規則
        this.ruleField.textProperty().bindBidirectional(this.ruleProperty);
        this.ruleField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.valueField.requestFocus();
                event.consume();
            }
        });

        // タグ
        this.valueField.textProperty().bindBidirectional(this.valueProperty);
        this.valueField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.onAdd(null);
                event.consume();
            }
        });

        this.listView.setItems(this.Component);
        this.listView.fixedCellSizeProperty().set(30.0);
        this.listView.setCellFactory(new Callback<ListView<TraceCustomEntity>, ListCell<TraceCustomEntity>>() {
            @Override
            public ListCell<TraceCustomEntity> call(ListView<TraceCustomEntity> listView) {
                return new ComponentCell();
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

            // タグ名が登録済の場合は警告表示し、未登録の場合はリストに追加する。
            TraceCustomEntity Component;
            Optional<TraceCustomEntity> item = this.Component.stream().filter(p -> p.getValue().equals(this.valueProperty.get())).findFirst();
            if (item.isPresent()) {
                Component = item.get();
                // 重複する行が表示されるようスクロールして選択状態にする。
                this.listView.scrollTo(Component); 
                this.listView.getSelectionModel().clearSelection(); 
                this.listView.getSelectionModel().select(Component);
                // 登録済のタグ名の場合は警告ダイアログを表示する。
                String msg = String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), this.valueProperty.get());
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), msg);
            } else {
                Component = new TraceCustomEntity();
                Component.setName(this.nameProperty.get());
                Component.setRule(this.ruleProperty.get());
                Component.setValue(this.valueProperty.get());
                this.Component.add(Component);

                // 追加した行が表示されるようスクロールして選択状態にする。
                this.listView.scrollTo(Component);
                this.listView.getSelectionModel().clearSelection();
                this.listView.getSelectionModel().select(Component);

                // 入力欄をクリアする。
                this.nameField.clear();
                this.ruleField.clear();
                this.valueField.clear();
            }

            // 項目名入力欄にフォーカスを移動する。
            this.nameField.requestFocus();
        });
    }
}