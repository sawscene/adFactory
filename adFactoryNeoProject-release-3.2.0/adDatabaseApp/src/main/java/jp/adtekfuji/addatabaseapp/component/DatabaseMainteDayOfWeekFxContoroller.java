/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.addatabaseapp.component;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

/**
 * 定期実行タイプ「毎週」選択時の実行日選択ペインのコントローラ
 *
 * @author s-maeda
 */
public class DatabaseMainteDayOfWeekFxContoroller implements DatabaseMainteDayInfoPaneFxController, Initializable {

    @FXML
    private CheckBox sundayCheckBox;
    @FXML
    private CheckBox mondayCheckBox;
    @FXML
    private CheckBox tuesdayCheckBox;
    @FXML
    private CheckBox wednesdayCheckBox;
    @FXML
    private CheckBox thursdayCheckBox;
    @FXML
    private CheckBox fridayCheckBox;
    @FXML
    private CheckBox saturdayCheckBox;
    @FXML
    private GridPane weekPane;

    private String selectedDays;
    private final BooleanProperty selectExistProp = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDaysOfWeek("");

        // 各曜日チェックボックスを切り替えるたびに、選択有無チェックを行う
        List<Node> weekPaneChildren = this.weekPane.getChildren();
        weekPaneChildren.stream().filter((child) -> (child instanceof CheckBox)).forEach((child) -> {
            ((CheckBox) child).selectedProperty().addListener((ObservableValue<? extends Boolean> observal, Boolean oldValue, Boolean newValue) -> {
                if (!newValue.equals(oldValue)) {
                    updateSelectionExistance();
                }
            });
        });
    }

    /**
     * 引数の文字列通りに、実行日指定を切り替える
     *
     * @param selectedDays
     */
    public void setDaysOfWeek(String selectedDays) {
        this.selectedDays = selectedDays;

        List<String> selectedDaysList = Arrays.asList(this.selectedDays.split(", "));

        this.sundayCheckBox.setSelected(selectedDaysList.contains("SUN"));
        this.mondayCheckBox.setSelected(selectedDaysList.contains("MON"));
        this.tuesdayCheckBox.setSelected(selectedDaysList.contains("TUE"));
        this.wednesdayCheckBox.setSelected(selectedDaysList.contains("WED"));
        this.thursdayCheckBox.setSelected(selectedDaysList.contains("THU"));
        this.fridayCheckBox.setSelected(selectedDaysList.contains("FRI"));
        this.saturdayCheckBox.setSelected(selectedDaysList.contains("SAT"));

        updateSelectionExistance();
    }

    /**
     * 曜日選択有無を更新
     */
    private void updateSelectionExistance() {
        Boolean validState = false;
        validState = validState || this.sundayCheckBox.selectedProperty().get();
        validState = validState || this.mondayCheckBox.selectedProperty().get();
        validState = validState || this.tuesdayCheckBox.selectedProperty().get();
        validState = validState || this.wednesdayCheckBox.selectedProperty().get();
        validState = validState || this.thursdayCheckBox.selectedProperty().get();
        validState = validState || this.fridayCheckBox.selectedProperty().get();
        validState = validState || this.saturdayCheckBox.selectedProperty().get();

        selectExistProp.set(validState);
    }

    /**
     * 実行日データ取得
     */
    @Override
    public String get() {
        if (selectExistProp.get()) {
            StringBuilder sb = new StringBuilder();
            sb.append((sundayCheckBox.selectedProperty().get()) ? "SUN, " : "");
            sb.append((mondayCheckBox.selectedProperty().get()) ? "MON, " : "");
            sb.append((tuesdayCheckBox.selectedProperty().get()) ? "TUE, " : "");
            sb.append((wednesdayCheckBox.selectedProperty().get()) ? "WED, " : "");
            sb.append((thursdayCheckBox.selectedProperty().get()) ? "THU, " : "");
            sb.append((fridayCheckBox.selectedProperty().get()) ? "FRI, " : "");
            sb.append((saturdayCheckBox.selectedProperty().get()) ? "SAT, " : "");
            this.selectedDays = sb.substring(0, sb.lastIndexOf(", "));
        } else {
            this.selectedDays = "";
        }

        return this.selectedDays;
    }

    /**
     * 曜日選択有無プロパティを取得する
     *
     * @return
     */
    public BooleanProperty getSelectExistProperty() {
        return selectExistProp;
    }

    /**
     * 曜日が選択されているか
     *
     * @return
     */
    public Boolean isSelctionExist() {
        return selectExistProp.get();
    }
}
