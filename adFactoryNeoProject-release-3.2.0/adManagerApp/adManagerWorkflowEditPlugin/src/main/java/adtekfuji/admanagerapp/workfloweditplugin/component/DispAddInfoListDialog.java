/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.controls.DispAddInfoCell;
import jp.adtekfuji.adFactory.entity.work.DispAddInfoEntity;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * 表示項目リスト設定ダイアログ
 *
 * @author y-harada
 */
@FxComponent(id = "DispAddInfoListDialog", fxmlPath = "/fxml/admanagerworkfloweditplugin/disp_addInfo_list_dialog.fxml")
public class DispAddInfoListDialog implements Initializable, ArgumentDelivery {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private ComboBox<DispAddInfoEntity.TargetTypeEnum> targetCombo;
    @FXML
    private TextField nameField;
    @FXML
    private ListView<DispAddInfoEntity> listView;

    private final StringProperty nameProperty = new SimpleStringProperty();

    private ObservableList<DispAddInfoEntity> dispAddInfoList = FXCollections.observableArrayList();

    /**
     * 表示項目リスト設定ダイアログを初期化する。
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * パラメータを設定する。
     *
     * @param argument 表示項目リスト
     */
    @Override
    public void setArgument(Object argument) {
        this.dispAddInfoList = (ObservableList<DispAddInfoEntity>) argument;
        
        // 対象種別
        this.targetCombo.setItems(FXCollections.observableArrayList(DispAddInfoEntity.TargetTypeEnum.values()));
        Callback<ListView<DispAddInfoEntity.TargetTypeEnum>, ListCell<DispAddInfoEntity.TargetTypeEnum>> comboCellFactory = (ListView<DispAddInfoEntity.TargetTypeEnum> param) -> new TargetTypeComboBoxCellFactory();
        this.targetCombo.setButtonCell(new TargetTypeComboBoxCellFactory());
        this.targetCombo.setCellFactory(comboCellFactory);
        this.targetCombo.getSelectionModel().select(0);

        // 項目名
        this.nameField.textProperty().bindBidirectional(this.nameProperty);
        this.nameField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.onAdd(null);
                event.consume();
            }
        });

        // リスト設定
        this.listView.setItems(this.dispAddInfoList);
        this.listView.fixedCellSizeProperty().set(30.0);
        this.listView.setCellFactory(new Callback<ListView<DispAddInfoEntity>, ListCell<DispAddInfoEntity>>() {
            @Override
            public ListCell<DispAddInfoEntity> call(ListView<DispAddInfoEntity> listView) {
                return new DispAddInfoCell();
            }
        });
        this.listView.setEditable(true);
    }

    /**
     * 追加ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onAdd(ActionEvent event) {
        Platform.runLater(() -> {
            // 項目名が未入力の場合、項目名入力欄にフォーカス移動
            if (StringUtils.isEmpty(this.nameProperty.get())) {
                this.nameField.requestFocus();
                return;
            }

            // 項目が登録済の場合は警告表示し、未登録の場合はリストに追加する。
            DispAddInfoEntity dispAddInfo;
            Optional<DispAddInfoEntity> item = this.dispAddInfoList.stream().filter(p -> p.getTarget().equals(this.targetCombo.getValue())).filter(p -> p.getName().equals(this.nameProperty.get())).findFirst();
            if (item.isPresent()) {
                dispAddInfo = item.get();

                // 重複する行が表示されるようスクロールして選択状態にする。
                this.listView.scrollTo(dispAddInfo);
                this.listView.getSelectionModel().clearSelection();
                this.listView.getSelectionModel().select(dispAddInfo);

                // 登録済の項目名の場合は警告ダイアログを表示する。
                String msg = String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), this.nameProperty.get());
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), msg);
            } else {
                dispAddInfo = new DispAddInfoEntity();
                dispAddInfo.setTarget(this.targetCombo.getValue());
                dispAddInfo.setName(this.nameProperty.get());
                dispAddInfo.setOrder(this.dispAddInfoList.size() + 1);
                this.dispAddInfoList.add(dispAddInfo);

                // 追加した行が表示されるようスクロールして選択状態にする。
                this.listView.scrollTo(dispAddInfo);
                this.listView.getSelectionModel().clearSelection();
                this.listView.getSelectionModel().select(dispAddInfo);

                // 入力欄をクリアする。
                this.nameField.clear();
            }

            // 項目名入力欄にフォーカスを移動する。
            this.nameField.requestFocus();
        });
    }
    
    /**
     * コンボボックスセルファクトリー
     *
     */
    class TargetTypeComboBoxCellFactory extends ListCell<DispAddInfoEntity.TargetTypeEnum> {

        @Override
        protected void updateItem(DispAddInfoEntity.TargetTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(item.getDisplayName(rb));
            }
        }
    }
    
}
