/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.List;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author ke.yokoi
 */
public class CellComboBox<T> extends AbstractCell {

    private final List<T> selectionData;
    private final ListCell<T> buttonCellFactory;
    private final Callback<ListView<T>, ListCell<T>> comboCellFactory;
    private final ObjectProperty<T> objectData;
    private final ComboBox<T> comboBox = new ComboBox<>();
    private ChangeListener<T> actionListner = null;

    /**
     * 項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isDisabled;

    /**
     * コンストラクタ
     * 
     * @param abstractCellInterface セルのインターフェイス
     * @param selectionData 選択データ
     * @param buttonCellFactory ボタンセルファクトリ
     * @param comboCellFactory コンボセルファクトリ
     * @param objectData オブジェクトプロパティデータ
     */
    public CellComboBox(CellInterface abstractCellInterface, List<T> selectionData, ListCell<T> buttonCellFactory, Callback<ListView<T>, ListCell<T>> comboCellFactory, ObjectProperty<T> objectData) {
        this(abstractCellInterface, selectionData, buttonCellFactory, comboCellFactory, objectData, false);
    }

    /**
     * コンストラクタ
     * 
     * @param abstractCellInterface セルのインターフェイス
     * @param selectionData 選択データ
     * @param buttonCellFactory ボタンセルファクトリ
     * @param comboCellFactory コンボセルファクトリ
     * @param objectData オブジェクトプロパティデータ
     * @param isDisabled 項目の有効/無効状態(true：無効、false：有効)
     */
    public CellComboBox(CellInterface abstractCellInterface, List<T> selectionData, ListCell<T> buttonCellFactory, Callback<ListView<T>, ListCell<T>> comboCellFactory, ObjectProperty<T> objectData, boolean isDisabled) {
        super(abstractCellInterface);
        this.selectionData = selectionData;
        this.buttonCellFactory = buttonCellFactory;
        this.comboCellFactory = comboCellFactory;
        this.objectData = objectData;
        this.isDisabled = isDisabled;
    }

    public CellComboBox actionListner(ChangeListener<T> actionListner) {
        this.actionListner = actionListner;
        return this;
    }

    /**
     * ノードを生成する
     */
    @Override
    public void createNode() {
        this.comboBox.setMinWidth(120.0);
        this.comboBox.setItems(FXCollections.observableArrayList(this.selectionData));
        this.comboBox.getSelectionModel().select(this.objectData.get());
        this.objectData.bind(this.comboBox.valueProperty());
        this.comboBox.setButtonCell(this.buttonCellFactory);
        this.comboBox.setCellFactory(this.comboCellFactory);
        if (Objects.nonNull(this.actionListner)) {
            this.comboBox.valueProperty().addListener(this.actionListner);
        }
        this.comboBox.setDisable(this.isDisabled);
        super.setNode(this.comboBox);
    }

}
