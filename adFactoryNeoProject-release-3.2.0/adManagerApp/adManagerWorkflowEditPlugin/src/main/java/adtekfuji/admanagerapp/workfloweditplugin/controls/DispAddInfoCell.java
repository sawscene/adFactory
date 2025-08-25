/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.controls;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import jp.adtekfuji.adFactory.entity.work.DispAddInfoEntity;
import jp.adtekfuji.javafxcommon.controls.EditableListCell;

/**
 * 追加情報表示設定 セルコントロール
 *
 * @author y-harada
 */
public class DispAddInfoCell extends EditableListCell<DispAddInfoEntity>  {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final HBox itemHbox;
    private final Label targetTypeLabel;
    private final TextField nameTextField;
    private final Pane pane;

    /**
     * コンストラクタ
     *
     */
    public DispAddInfoCell() {
        super(true, true);

        this.itemHbox = new HBox();
        this.targetTypeLabel = new Label();
        this.nameTextField = new TextField();
        this.pane = new Pane();
        
        this.targetTypeLabel.setPrefWidth(110.0);
        this.nameTextField.setPrefWidth(180.0);

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
                long count = this.getListView().getItems().stream().filter(p -> p.getName().equals(newNameText) && !p.equals(this.getItem()) && p.getTarget().getDisplayName(rb).equals(this.targetTypeLabel.getText())).count();
                if (count == 0) {
                    // 入力された値をentityにセットする。
                    this.getItem().setName(newNameText);
                } else {
                    // 対象と項目名が重複した場合、警告して元の値に戻す。
                    String msg = String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), newNameText);
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), msg);

                    this.nameTextField.setText(oldNameText);
                    this.nameTextField.requestFocus();
               }
            }
        });

        this.itemHbox.setSpacing(0.0);
        this.itemHbox.setAlignment(Pos.CENTER_LEFT);
        this.itemHbox.getChildren().addAll(this.targetTypeLabel, this.nameTextField, this.pane);
        HBox.setHgrow(this.pane, Priority.ALWAYS);
                
        addItem(this.itemHbox);
    }

    /**
     * セルの内容を更新する。
     *
     * @param entity 更新エンティティ
     * @param isEmpty 空か
     */
    @Override
    public void updateItem(DispAddInfoEntity entity, boolean isEmpty) {
        super.updateItem(entity, isEmpty);

        if (entity != null && !isEmpty) {
            this.targetTypeLabel.setText(entity.getTarget().getDisplayName(rb));
            this.nameTextField.setText(entity.getName());

            this.targetTypeLabel.setVisible(true);
            this.nameTextField.setVisible(true);
            this.setGraphic(this.hbox);
        } else {
            this.targetTypeLabel.setText(null);
            this.nameTextField.setText(null);
            
            this.targetTypeLabel.setVisible(false);
            this.nameTextField.setVisible(false);
            this.setGraphic(null);
        }
    }
}
