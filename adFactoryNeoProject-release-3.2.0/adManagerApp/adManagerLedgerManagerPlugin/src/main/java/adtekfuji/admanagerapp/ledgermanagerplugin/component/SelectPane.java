/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.component;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author nar-nakamura
 */
public class SelectPane implements Initializable {

    private static final String SEPARATOR = ",";

    @FXML
    private AnchorPane mainPane;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Button choiceButton;
    @FXML
    private TextField choiceTextField;

    private Map<Long, String> choiceDatas = new LinkedHashMap();

    private EventHandler<ActionEvent> onCheckListener = (ActionEvent event) -> {};
    private EventHandler<ActionEvent> onClickButtonListener = (ActionEvent event) -> {};

    /**
     * 初期化
     *
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.checkBox.setSelected(false);
        this.choiceButton.setDisable(true);
        this.choiceTextField.setDisable(true);
    }

    /**
     * チェックボックスのイベントを設定する。
     *
     * @param onCheckListener 
     */
    public void setOnCheckListener(EventHandler<ActionEvent> onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    /**
     * 選択ボタンのイベントを設定する。
     *
     * @param onClickButtonListener 
     */
    public void setOnClickButtonListener(EventHandler<ActionEvent> onClickButtonListener) {
        this.onClickButtonListener = onClickButtonListener;
    }

    /**
     * チェック状態を取得する。
     *
     * @return true: ON, false: OFF
     */
    public boolean isSelected() {
        return this.checkBox.isSelected();
    }

    /**
     * チェック状態を設定する。
     *
     * @param isSelected true: ON, false: OFF
     */
    public void setSelected(boolean isSelected) {
        this.checkBox.setSelected(isSelected);
        this.onCheck(null);
    }

    /**
     * ラベルテキストを取得する。
     *
     * @return ラベルテキスト
     */
    public String getLabelText() {
        return this.checkBox.getText();
    }

    /**
     * ラベルテキストを設定する。
     *
     * @param text ラベルテキスト
     */
    public void setLabelText(String text) {
        this.checkBox.setText(text);
    }

    /**
     * 選択データの表示テキストを取得する。
     *
     * @return 選択データの表示テキスト
     */
    public String getChoiceText() {
        return this.choiceTextField.getText();
    }

    /**
     * 選択データのキーをカンマ区切りで繋げた文字列を取得する。
     *
     * @return 
     */
    public String getChoiceIdsString() {
        String result = "";
        if (Objects.nonNull(this.choiceDatas) && !this.choiceDatas.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Long id : this.choiceDatas.keySet()) {
                sb.append(String.valueOf(id)).append(SEPARATOR);
            }
            result = sb.substring(0, sb.length() - 1);
        }
        return result;
    }

    /**
     * 選択データ一覧を取得する。
     *
     * @return 選択データ一覧
     */
    public Map<Long, String> getChoiceDatas() {
        return this.choiceDatas;
    }

    /**
     * 
     * @param choiceDatas 
     */
    public void setChoiceDatas(Map<Long, String> choiceDatas) {
        this.choiceDatas = choiceDatas;
        if (Objects.isNull(choiceDatas) || choiceDatas.isEmpty()) {
            this.choiceTextField.setText("");
        } else {
            this.choiceTextField.setText(String.join(SEPARATOR, choiceDatas.values()));
        }
    }

    /**
     * チェックイベント
     *
     * @param event 
     */
    @FXML
    public void onCheck(ActionEvent event) {
        boolean isDisable = !this.checkBox.isSelected();
        this.choiceButton.setDisable(isDisable);
        this.choiceTextField.setDisable(isDisable);

        this.onCheckListener.handle(event);
    }

    /**
     * 選択ボタンイベント
     *
     * @param event 
     */
    @FXML
    public void onClickButton(ActionEvent event) {
        this.onClickButtonListener.handle(event);
    }

    /**
     * 無効状態を設定する。
     *
     * @param value true: 無効, false: 有効
     */
    public void setDisable(boolean value) {
        this.mainPane.setDisable(value);
    }
}
