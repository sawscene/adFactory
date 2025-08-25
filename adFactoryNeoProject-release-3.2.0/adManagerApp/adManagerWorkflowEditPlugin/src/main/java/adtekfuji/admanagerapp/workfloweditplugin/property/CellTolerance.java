/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.property;

import adtekfuji.utility.StringUtils;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;
import jp.adtekfuji.javafxcommon.dialog.DialogBox;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 * 基準値セルコントロール
 *
 * @author s-heya
 */
public class CellTolerance extends AbstractCell {

    /**
     * String、Number コンバーター
     */
    StringConverter<Number> converter = new StringConverter<Number>() {
        @Override
        public Number fromString(String str) {
            try {
                if (StringUtils.isEmpty(str)) {
                    return Double.NaN;
                }
                double value = Double.parseDouble(str);
                return value;
            }
            catch (Exception ex) {
                return Double.NaN;
            }
        }

        @Override
        public String toString(Number value) {
            try {
                if (Objects.isNull(value) || Double.isNaN((Double) value)) {
                    return "";
                }
                return String.valueOf(value);
            }
            catch (Exception ex) {
                return "";
            }
        }
    };

    private final WorkPropertyInfoEntity workProperty;
    private MenuButton popupButton;
    private final DoubleProperty lowerToleranceProperty =  new SimpleDoubleProperty();
    private final DoubleProperty upperToleranceProperty =  new SimpleDoubleProperty();

    /**
     * 項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isDisabled;

    /**
     * コンストラクタ
     *
     * @param record
     * @param workProperty
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellTolerance(Record record, WorkPropertyInfoEntity workProperty, boolean isDisabled) {
        super(record);
        this.workProperty = workProperty;
        this.isDisabled = isDisabled;
    }

    /**
     * ノードを生成する
     */
    @Override
    public void createNode() {
        this.popupButton = new MenuButton(workProperty.getDisplayTolerance());
        this.popupButton.setPrefWidth(200.0);
        this.popupButton.getStyleClass().add("ContentTextBox");
        this.popupButton.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // ポップアップが閉じられたら変更内容をボタンに反映する
                if (newValue) {
                    lowerToleranceProperty.set(workProperty.getWorkPropLowerTolerance());
                    upperToleranceProperty.set(workProperty.getWorkPropUpperTolerance());
                } else {
                    if (lowerToleranceProperty.get() > upperToleranceProperty.get()) {
                        // 入力エラー
                        lowerToleranceProperty.set(workProperty.getWorkPropLowerTolerance());
                        upperToleranceProperty.set(workProperty.getWorkPropUpperTolerance());
                        DialogBox.simpleAlert("key.alert.inputValidation", "key.alert.inputValidation.details");
                        return;
                    }

                    workProperty.setWorkPropLowerTolerance(lowerToleranceProperty.get());
                    workProperty.setWorkPropUpperTolerance(upperToleranceProperty.get());
                    workProperty.updateMember();

                    popupButton.setText(workProperty.getDisplayTolerance());
                }
            }
        });

        if (WorkPropertyCategoryEnum.MEASURE.equals(workProperty.getWorkPropCategory())
                || WorkPropertyCategoryEnum.TIMER.equals(workProperty.getWorkPropCategory())) {
            popupButton.setDisable(this.isDisabled);
        } else {
            popupButton.setDisable(true);
        }

        workProperty.workPropCategoryProperty().addListener(new ChangeListener<WorkPropertyCategoryEnum>() {
            @Override
            public void changed(ObservableValue<? extends WorkPropertyCategoryEnum> observable, WorkPropertyCategoryEnum oldValue, WorkPropertyCategoryEnum newValue) {
                // プロパティ種別によって変更を許可する
                if (WorkPropertyCategoryEnum.MEASURE.equals(newValue)
                        || WorkPropertyCategoryEnum.TIMER.equals(newValue)) {
                    popupButton.setDisable(false);
                } else {
                    workProperty.setWorkPropLowerTolerance(Double.NaN);
                    workProperty.setWorkPropUpperTolerance(Double.NaN);
                    workProperty.updateMember();

                    popupButton.setText(workProperty.getDisplayTolerance());
                    popupButton.setDisable(true);
                }
            }
        });

        MenuItem popup = new MenuItem();
        popup.getStyleClass().add("popup-box");
        popup.setGraphic(createContent());

        this.popupButton.getItems().setAll(popup);

        // commonのsetNodeのdisable設定が自分自身であるため、初期設定内容を自分自身に反映
        this.setDisable(popupButton.isDisable());
        super.setNode(popupButton);
    }

    /**
     * コンテンツを生成する
     *
     * @return
     */
    private Node createContent() {
        HBox hBox = new HBox(5);
        hBox.setAlignment(Pos.CENTER);

        // 基準値下限
        TextField textField1 = new TextField();
        textField1.setPrefWidth(120.0);
        textField1.setAlignment(Pos.CENTER_RIGHT);
        textField1.getStyleClass().add("text-normal-bold");
        Bindings.bindBidirectional(textField1.textProperty(), lowerToleranceProperty, converter);

        Label label = new Label(" - ");
        label.getStyleClass().add("text-normal-bold");

        // 基準値上限
        TextField textField2 = new TextField();
        textField2.setPrefWidth(120.0);
        textField2.setAlignment(Pos.CENTER_RIGHT);
        textField2.getStyleClass().add("text-normal-bold");
        Bindings.bindBidirectional(textField2.textProperty(), upperToleranceProperty, converter);

        hBox.getChildren().addAll(textField1, label, textField2);

        return hBox;
    }
}
