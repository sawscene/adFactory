/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import adtekfuji.utility.StringUtils;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

/**
 * コンボボックスセルコントロール
 *
 * @author s-heya
 */
public class CellSimpleComboBox extends AbstractCell {

    private final List<String> items;
    private final StringProperty property;
    private final ComboBox<String> comboBox = new ComboBox<>();
    private ChangeListener<String> listener = null;

    /**
     * コンストラクタ
     *
     * @param cellInterface
     * @param items
     * @param property
     * @param isEditable
     */
    public CellSimpleComboBox(CellInterface cellInterface, List<String> items, StringProperty property, boolean isEditable) {
        super(cellInterface);
        this.items = items;
        this.property = property;
        this.comboBox.setEditable(isEditable);
    }

    /**
     * リスナーを設定する
     *
     * @param listener
     * @return
     */
    public CellSimpleComboBox setChangeListener(ChangeListener<String> listener) {
        this.listener = listener;
        return this;
    }

    /**
     * ノードを生成する
     */
    @Override
    public void createNode() {
        this.comboBox.setMinWidth(120.0);
        this.comboBox.setMaxWidth(200.0);
        this.comboBox.setItems(FXCollections.observableArrayList(this.items));
        this.comboBox.getSelectionModel().select(this.property.get());
        if (StringUtils.isEmpty(this.comboBox.getSelectionModel().getSelectedItem())) {
            this.comboBox.getSelectionModel().select(0);
        }

        this.property.bind(this.comboBox.valueProperty());

        if (Objects.nonNull(this.listener)) {
            this.comboBox.valueProperty().addListener(this.listener);
        }

        super.setNode(this.comboBox);
    }
}