/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;

/**
 * テキストセル(TextField・TextArea 切替対応)
 *
 * @author nar-nakamura
 */
public class CellText extends AbstractCell {

    /**
     * テキストセル表示種別
     */
    public enum CellTextType {
        TextField,
        TextArea;
    }

    private final StringProperty valueProperty;
    private final int maxLength;

    private final RestrictedTextField textField;
    private final TextArea textArea;

    private CellTextType cellTextType = CellTextType.TextField;

    /**
     * コンストラクタ
     *
     * @param cell CellInterface
     * @param valueProperty 値
     */
    public CellText(CellInterface cell, StringProperty valueProperty) {
        this(cell, valueProperty, 1024);
    }

    /**
     * コンストラクタ
     *
     * @param cell CellInterface
     * @param valueProperty 値
     * @param maxLength TextFieldの最大文字数
     */
    public CellText(CellInterface cell, StringProperty valueProperty, int maxLength) {
        super(cell);

        // TextFieldで改行コードが削除されるため、値を記憶しておいてバインド後にセットする。
        String value = valueProperty.get();

        this.valueProperty = valueProperty;
        this.maxLength = maxLength;

        this.textField = new RestrictedTextField();
        this.textField.setMaxLength(this.maxLength);
        this.valueProperty.bindBidirectional(this.textField.textProperty());

        this.textArea = new TextArea();
        this.valueProperty.bindBidirectional(this.textArea.textProperty());

        this.valueProperty.set(value);
    }

    /**
     * ノードを作成する。
     */
    @Override
    public void createNode() {
        AnchorPane pane = new AnchorPane();

        pane.getChildren().add(this.textField);
        pane.getChildren().add(this.textArea);

        this.textField.setMinSize(0.0, 0.0);
        this.textArea.setMinSize(0.0, 0.0);

        AnchorPane.setTopAnchor(this.textField, 0.0);
        AnchorPane.setBottomAnchor(this.textField, 0.0);
        AnchorPane.setLeftAnchor(this.textField, 0.0);
        AnchorPane.setRightAnchor(this.textField, 0.0);

        AnchorPane.setTopAnchor(this.textArea, 0.0);
        AnchorPane.setBottomAnchor(this.textArea, 0.0);
        AnchorPane.setLeftAnchor(this.textArea, 0.0);
        AnchorPane.setRightAnchor(this.textArea, 0.0);

        super.setNode(pane);
    }

    /**
     * テキストセル表示種別を取得する。
     *
     * @return テキストセル表示種別
     */
    public CellTextType getCellTextType() {
        return this.cellTextType;
    }

    /**
     * テキストセル表示種別を設定して、表示を切り替える。
     *
     * @param cellTextType テキストセル表示種別
     */
    public void setCellTextType(CellTextType cellTextType) {
        this.cellTextType = cellTextType;
        this.switchType();
    }

    /**
     * 設定されているテキストセル表示種別に表示を切り替える。
     */
    private void switchType() {
        if (Objects.equals(this.cellTextType, CellTextType.TextArea)) {
            this.textField.setVisible(false);
            this.textArea.setVisible(true);
        } else {
            this.textField.setVisible(true);
            this.textArea.setVisible(false);
        }
    }
}
