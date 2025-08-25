/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.controls;

import adtekfuji.admanagerapp.workfloweditplugin.entity.TraceCustomEntity;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * カスタム設定値セルコントロール
 *
 * @author nar-nakamura
 */
public class TraceCustomCell extends ListCell<TraceCustomEntity> {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final HBox hbox;
    private final TextField nameTextField;
    private final TextField valueTextField;
    private final Button deleteButton;
    private final Pane pane;

    /**
     * コンストラクタ
     *
     * ※．注意：表示項目追加等、行幅が変わる修正を行なったら、tabbed_work_compo.fxmlのtabPaneの幅を調整すること。
     */
    public TraceCustomCell() {
        super();

        this.hbox = new HBox();
        this.nameTextField = new TextField();
        this.valueTextField = new TextField();
        this.deleteButton = new Button("x");
        this.pane = new Pane();

        this.nameTextField.setPrefWidth(120.0);
        this.valueTextField.setPrefWidth(170.0);

        // 項目名
        this.nameTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && (oldValue != newValue)) {
                String oldNameText = this.getItem().getName();
                String newNameText = this.nameTextField.getText();

                // 入力確認
                if (Objects.isNull(newNameText) || newNameText.isEmpty()) {
                    this.nameTextField.setText(oldNameText);
                    return;
                }

                // 重複確認
                long count = this.getListView().getItems().stream().filter(p -> p.getName().equals(newNameText) && !p.equals(this.getItem())).count();
                if (count == 0) {
                    // 入力された値をentityにセットする。
                    this.getItem().setName(newNameText);
                } else {
                    // 重複する項目名が存在する場合、警告して元の値に戻す。
                    String msg = String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), newNameText);
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), msg);

                    this.nameTextField.setText(oldNameText);
                    this.nameTextField.requestFocus();
               }
            }
        });

        // 値
        this.valueTextField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue && (oldValue != newValue)) {
                String oldValueText = this.getItem().getValue();
                String newValueText = this.valueTextField.getText();

                // 入力確認
                if (Objects.isNull(newValueText) || newValueText.isEmpty()) {
                    this.valueTextField.setText(oldValueText);
                    return;
                }

                // 入力された値をentityにセットする。
                this.getItem().setValue(newValueText);
            }
        });

        // 削除ボタン
        this.deleteButton.getStyleClass().add("DeleteButton");
        this.deleteButton.setFocusTraversable(false);
        this.deleteButton.setOnAction((ActionEvent event) -> {
            // リストからアイテムを削除する
            this.getListView().getItems().remove(this.getItem());
        });

        this.hbox.setSpacing(5.0);
        this.hbox.setAlignment(Pos.CENTER_LEFT);
        this.hbox.getChildren().addAll(this.nameTextField, this.valueTextField, this.pane, this.deleteButton);
        HBox.setHgrow(this.pane, Priority.ALWAYS);
    }

    /**
     * セルの内容を更新する。
     *
     * @param entity
     * @param isEmpty 
     */
    @Override
    public void updateItem(TraceCustomEntity entity, boolean isEmpty) {
        super.updateItem(entity, isEmpty);

        if (entity != null && !isEmpty) {
            this.nameTextField.setText(entity.getName());
            this.valueTextField.setText(entity.getValue());

            this.nameTextField.setVisible(true);
            this.valueTextField.setVisible(true);
            this.deleteButton.setVisible(true);
            this.setGraphic(this.hbox);
        } else {
            this.nameTextField.setText(null);
            this.valueTextField.setText(null);

            this.nameTextField.setVisible(false);
            this.valueTextField.setVisible(false);
            this.deleteButton.setVisible(false);
            this.setGraphic(null);
        }
    }
}
