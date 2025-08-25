/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;
import adtekfuji.utility.StringUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.NumberTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SchedulePatternEnum;
import jp.adtekfuji.adFactory.enumerate.WeekTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * 工程選択クラス
 *D
 * @author yu.nara
 */
@FxComponent(id = "ScheduleSelectionCompo", fxmlPath = "/fxml/compo/schedule_selection_compo.fxml")
public class ScheduleSelectionCompoFxController implements Initializable, ArgumentDelivery {
    @FXML
    DatePicker startDatePicker;
    @FXML
    private TextField startTime;
    @FXML
    private RadioButton daySchedule;
    @FXML
    private RadioButton weekSchedule;
    @FXML
    private RadioButton monthSchedule;
    @FXML
    private ToggleGroup schedulePeriod;

    @FXML
    private RadioButton monthDay;
    @FXML
    private RadioButton monthWeek;
    @FXML
    private ToggleGroup monthSchedulePeriod;
    @FXML
    private Pane monthDayPane;
    @FXML
    private Pane monthWeekPane;

    @FXML
    private Pane daySchedulePane;
    @FXML
    private Pane weekSchedulePane;
    @FXML
    private Pane monthSchedulePane;
    @FXML
    private TextField dayPeriod;
    @FXML
    private TextField weekPeriod;
    @FXML
    private TextField monthDayMonth;
    @FXML
    private TextField monthDayDay;
    @FXML
    private TextField monthWeekMonth;
    @FXML
    private ComboBox<NumberTypeEnum> monthWeekWeek;
    @FXML
    private ComboBox<WeekTypeEnum> monthWeekDay;

    @FXML
    CheckBox checkSunday;
    @FXML
    CheckBox checkMonday;
    @FXML
    CheckBox checkTuesday;
    @FXML
    CheckBox checkWednesday;
    @FXML
    CheckBox checkThursday;
    @FXML
    CheckBox checkFriday;
    @FXML
    CheckBox checkSaturday;

    ScheduleConditionInfoEntity scheduleConditionInfoEntity;

    /**
     * 週タイプのコンボボック表示
     */
    static class WeekTypeEnumComboBoxCellFactory extends ListCell<WeekTypeEnum> {
        @Override
        protected void updateItem(WeekTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.resourceKey));
            }
        }
    }

    /**
     * 週数タイプのコンボボック表示
     */
    static class NumberTypeEnumComboBoxCellFactory extends ListCell<NumberTypeEnum> {
        @Override
        protected void updateItem(NumberTypeEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.resourceKey));
            }
        }
    }

    /**
     * ダイアログを初期化する。
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 日・週・月のラジオボタンによる項目の表示切替
        schedulePeriod.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            RadioButton selectedRb = (RadioButton) newValue;
            boolean isDaySchedule = Objects.equals(selectedRb.getId(), daySchedule.getId());
            boolean isWeekSchedule = Objects.equals(selectedRb.getId(), weekSchedule.getId());
            boolean isMonthSchedule = Objects.equals(selectedRb.getId(), monthSchedule.getId());
            daySchedulePane.setManaged(isDaySchedule);
            daySchedulePane.setVisible(isDaySchedule);
            weekSchedulePane.setManaged(isWeekSchedule);
            weekSchedulePane.setVisible(isWeekSchedule);
            monthSchedulePane.setManaged(isMonthSchedule);
            monthSchedulePane.setVisible(isMonthSchedule);
        });

        // 月表示にて 日・週のラジオボタンによる項目の表示切替
        monthSchedulePeriod.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            RadioButton selectedRb = (RadioButton) newValue;
            boolean isDaySchedule = Objects.equals(selectedRb.getId(), monthDay.getId());
            boolean isWeekSchedule = Objects.equals(selectedRb.getId(), monthWeek.getId());
            monthDayPane.setDisable(!isDaySchedule);
            monthWeekPane.setDisable(!isWeekSchedule);
        });

        // 数字入力制限 (1 ～ 99)
        UnaryOperator<TextFormatter.Change> numericFormatter = (change) -> {
            final Pattern timePattern = Pattern.compile("^[1-9]|[1-9][0-9]$");
            if (!timePattern.matcher(change.getControlNewText()).matches()) {
                return null;
            }
            return change;
        };
        dayPeriod.setTextFormatter(new TextFormatter<String>(numericFormatter));
        weekPeriod.setTextFormatter(new TextFormatter<String>(numericFormatter));
        monthDayMonth.setTextFormatter(new TextFormatter<String>(numericFormatter));
        monthWeekMonth.setTextFormatter(new TextFormatter<String>(numericFormatter));

        // 日の入力制限 (1日～31日)
        UnaryOperator<TextFormatter.Change> dayFormatter = (change) -> {
            final Pattern timePattern = Pattern.compile("^[1-9]|[12][0-9]|3[10]$");
            if (!timePattern.matcher(change.getControlNewText()).matches()) {
                return null;
            }
            return change;
        };
        monthDayDay.setTextFormatter(new TextFormatter<String>(dayFormatter));

        // コンボボックス設定 (日曜日～月曜日)
        monthWeekDay.setButtonCell(new WeekTypeEnumComboBoxCellFactory());
        monthWeekDay.setCellFactory((param)-> new WeekTypeEnumComboBoxCellFactory());
        monthWeekDay.setItems(FXCollections.observableArrayList(WeekTypeEnum.values()));
        monthWeekDay.setValue(WeekTypeEnum.values()[0]);

        // コンボボックス設定 (第1 ～ 最終)
        monthWeekWeek.setButtonCell(new NumberTypeEnumComboBoxCellFactory());
        monthWeekWeek.setCellFactory((param)-> new NumberTypeEnumComboBoxCellFactory());
        monthWeekWeek.setItems(FXCollections.observableArrayList(NumberTypeEnum.values()));
        monthWeekWeek.setValue(NumberTypeEnum.values()[0]);
    }

    /**
     * チェックが入っている曜日一覧を取得
     * @return 曜日の一覧
     */
    List<WeekTypeEnum> getWeekTypeSelectedList() {
        List<WeekTypeEnum> weekTypeEnums = new ArrayList<>();
        if (checkSunday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.SUNDAY);
        }
        if (checkMonday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.MONDAY);
        }
        if (checkTuesday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.TUESDAY);
        }
        if (checkWednesday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.WEDNESDAY);
        }
        if (checkThursday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.THURSDAY);
        }
        if (checkFriday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.FRIDAY);
        }
        if (checkSaturday.isSelected()) {
            weekTypeEnums.add(WeekTypeEnum.SATURDAY);
        }
        return weekTypeEnums;
    }

    /**
     * 引数設定
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (!(argument instanceof ScheduleConditionInfoEntity)) {
            return;
        }
        this.scheduleConditionInfoEntity = (ScheduleConditionInfoEntity) argument;

        // 開始日
        if (Objects.isNull(this.scheduleConditionInfoEntity.getStartDate())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.scheduleConditionInfoEntity.setStartDate(calendar.getTime());
        }
        this.startDatePicker.setValue(DateUtils.toLocalDate(this.scheduleConditionInfoEntity.getStartDate()));
        this.startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setStartDate(DateUtils.getBeginningOfDate(newValue)));

        // 実施時間
        if (StringUtils.isEmpty(this.scheduleConditionInfoEntity.getDateTime())) {
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            this.scheduleConditionInfoEntity.setDateTime(sf.format(new Date()));
        }
        this.startTime.setText(scheduleConditionInfoEntity.getDateTime());
        this.startTime.textProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setDateTime(newValue));

        // 設定パターン
        if (Objects.isNull(this.scheduleConditionInfoEntity.getSchedulePattern())) {
            this.scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.DAY);
        }
        switch (this.scheduleConditionInfoEntity.getSchedulePattern()) {
            case WEEK:
                this.schedulePeriod.selectToggle(this.weekSchedule);
                break;
            case MONTH:
                this.schedulePeriod.selectToggle(this.monthSchedule);
                break;
            case DAY:
            default:
                this.schedulePeriod.selectToggle(this.daySchedule);
                break;
        }
        this.schedulePeriod.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selectedRb = (RadioButton) newValue;
            if (Objects.equals(selectedRb.getId(), weekSchedule.getId())) {
                this.scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.WEEK);
            } else if (Objects.equals(selectedRb.getId(), monthSchedule.getId())) {
                this.scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.MONTH);
            } else {
                this.scheduleConditionInfoEntity.setSchedulePattern(SchedulePatternEnum.DAY);
            }
        });

        // 日 - 実施間隔
        if (Objects.isNull(scheduleConditionInfoEntity.getDayPeriod())) {
            this.scheduleConditionInfoEntity.setDayPeriod(1);
        }
        this.dayPeriod.setText(scheduleConditionInfoEntity.getDayPeriod().toString());
        this.dayPeriod.textProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setDayPeriod(Integer.parseInt(newValue)));

        // 週 - 実施間隔
        if (Objects.isNull(scheduleConditionInfoEntity.getWeekPeriod())) {
            this.scheduleConditionInfoEntity.setWeekPeriod(1);
        }
        this.weekPeriod.setText(scheduleConditionInfoEntity.getWeekPeriod().toString());
        this.weekPeriod.textProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeekPeriod(Integer.parseInt(newValue)));

        // 週 - 実施曜日
        if (StringUtils.isEmpty(scheduleConditionInfoEntity.getWeeks())) {
            this.scheduleConditionInfoEntity.setWeeks("[]");
        }
        List<WeekTypeEnum> weeks = JsonUtils.jsonToObjects(scheduleConditionInfoEntity.getWeeks(), WeekTypeEnum[].class);
        weeks.forEach(week -> {
            switch (week) {
                case SUNDAY:
                    this.checkSunday.setSelected(true);
                    break;
                case MONDAY:
                    this.checkMonday.setSelected(true);
                    break;
                case TUESDAY:
                    this.checkTuesday.setSelected(true);
                    break;
                case WEDNESDAY:
                    this.checkWednesday.setSelected(true);
                    break;
                case THURSDAY:
                    this.checkThursday.setSelected(true);
                    break;
                case FRIDAY:
                    this.checkFriday.setSelected(true);
                    break;
                case SATURDAY:
                    this.checkSaturday.setSelected(true);
                    break;
                default:
            }
        });
        this.checkSunday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));
        this.checkMonday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));
        this.checkTuesday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));
        this.checkWednesday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));
        this.checkThursday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));
        this.checkFriday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));
        this.checkSaturday.selectedProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setWeeks(JsonUtils.objectsToJson(getWeekTypeSelectedList())));

        // 月 - 実施パターン
        if (Objects.isNull(this.scheduleConditionInfoEntity.getMonthSchedulePattern())) {
            this.scheduleConditionInfoEntity.setMonthSchedulePattern(SchedulePatternEnum.DAY);
        }
        switch (this.scheduleConditionInfoEntity.getMonthSchedulePattern()) {
            case WEEK:
                this.monthSchedulePeriod.selectToggle(this.monthWeek);
                break;
            case DAY:
                this.monthSchedulePeriod.selectToggle(this.monthDay);
            default:
                break;
        }
        this.monthSchedulePeriod.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selectedRb = (RadioButton) newValue;
            if (Objects.equals(selectedRb.getId(), monthWeek.getId())) {
                this.scheduleConditionInfoEntity.setMonthSchedulePattern(SchedulePatternEnum.WEEK);
            } else {
                this.scheduleConditionInfoEntity.setMonthSchedulePattern(SchedulePatternEnum.DAY);
            }
        });

        // 月 - 日 - 実施月間隔
        if (Objects.isNull(this.scheduleConditionInfoEntity.getMonthDayMonth())) {
            this.scheduleConditionInfoEntity.setMonthDayMonth(1);
        }
        this.monthDayMonth.setText(this.scheduleConditionInfoEntity.getMonthDayMonth().toString());
        this.monthDayMonth.textProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setMonthDayMonth(Integer.parseInt(newValue)));

        // 月 - 日 - 実施日間隔
        if (Objects.isNull(this.scheduleConditionInfoEntity.getMonthDayDay())) {
            this.scheduleConditionInfoEntity.setMonthDayDay(1);
        }
        this.monthDayDay.setText(this.scheduleConditionInfoEntity.getMonthDayDay().toString());
        this.monthDayDay.textProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setMonthDayDay(Integer.parseInt(newValue)));

        // 月 - 週 - 実施月間隔
        if (Objects.isNull(this.scheduleConditionInfoEntity.getMonthWeekMonth())) {
            this.scheduleConditionInfoEntity.setMonthWeekMonth(1);
        }
        this.monthWeekMonth.setText(this.scheduleConditionInfoEntity.getMonthWeekMonth().toString());
        this.monthWeekMonth.textProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setMonthWeekMonth(Integer.parseInt(newValue)));

        // 月 - 週 - 実施週数
        if (Objects.isNull(this.scheduleConditionInfoEntity.getMonthWeekWeek())) {
            this.scheduleConditionInfoEntity.setMonthWeekWeek(NumberTypeEnum.FIRST);
        }
        this.monthWeekWeek.setValue(this.scheduleConditionInfoEntity.getMonthWeekWeek());
        this.monthWeekWeek.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setMonthWeekWeek(newValue));

        // 月 - 週 - 実施曜日
        if (Objects.isNull(this.scheduleConditionInfoEntity.getMonthWeekDay())) {
            this.scheduleConditionInfoEntity.setMonthWeekDay(WeekTypeEnum.SUNDAY);
        }
        this.monthWeekDay.setValue(this.scheduleConditionInfoEntity.getMonthWeekDay());
        this.monthWeekDay.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.scheduleConditionInfoEntity.setMonthWeekDay(newValue));
    }
}
