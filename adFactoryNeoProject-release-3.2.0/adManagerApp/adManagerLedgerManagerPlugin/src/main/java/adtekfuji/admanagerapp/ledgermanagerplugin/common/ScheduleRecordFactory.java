/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.common;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;
import jp.adtekfuji.adFactory.enumerate.WeekTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.property.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 * @author nar-nakamura
 */
public class ScheduleRecordFactory extends AbstractRecordFactory<ScheduleConditionInfoEntity> {
    private final Logger logger = LogManager.getLogger();
    private final Consumer<Boolean> blockUI;
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public ScheduleRecordFactory(Table<ScheduleConditionInfoEntity> table, LinkedList<ScheduleConditionInfoEntity> entities, Consumer<Boolean> blockUI) {
        super(table, entities);
        this.blockUI = blockUI;
    }

    SimpleDateFormat sf = new SimpleDateFormat(":yyyy/MM/dd HH:mm");

    /**
     * 計画表示
     *
     * @param scheduleConditionInfoEntity 計画情報
     * @return 表示
     */
    private String toMessage(ScheduleConditionInfoEntity scheduleConditionInfoEntity) {
        if (Objects.isNull(scheduleConditionInfoEntity) || scheduleConditionInfoEntity.isEmpty()) {
            return "";
        }

        switch (scheduleConditionInfoEntity.getSchedulePattern()) {
            case DAY:
                return scheduleConditionInfoEntity.getDayPeriod().toString()
                        + rb.getString("key.PerDay")
                        + scheduleConditionInfoEntity.getDateTime()
                        + "("
                        + LocaleUtils.getString("key.NextTime")
                        + scheduleConditionInfoEntity.getNextSchedule().map(sf::format).orElse("-")
                        + ")";

            case WEEK:
                return scheduleConditionInfoEntity.getWeekPeriod().toString()
                        + rb.getString("key.PerWeek")
                        + "["
                        + JsonUtils
                        .jsonToObjects(scheduleConditionInfoEntity.getWeeks(), WeekTypeEnum[].class)
                        .stream()
                        .map(item -> item.shortResourceKey)
                        .map(LocaleUtils::getString)
                        .collect(Collectors.joining(","))
                        + "]"
                        + scheduleConditionInfoEntity.getDateTime()
                        + "("
                        + LocaleUtils.getString("key.NextTime")
                        + scheduleConditionInfoEntity.getNextSchedule().map(sf::format).orElse("-")
                        + ")";
            case MONTH:
                switch (scheduleConditionInfoEntity.getMonthSchedulePattern()) {
                    case DAY:
                        return scheduleConditionInfoEntity.getMonthDayMonth()
                                + rb.getString("key.PerMonth")
                                + scheduleConditionInfoEntity.getMonthDayDay()
                                + rb.getString("key.Day")
                                + "("
                                + LocaleUtils.getString("key.NextTime")
                                + scheduleConditionInfoEntity.getNextSchedule().map(sf::format).orElse("-")
                                + ")";
                    case WEEK:
                        return scheduleConditionInfoEntity.getMonthWeekMonth()
                                + rb.getString("key.PerMonth")
                                + rb.getString(scheduleConditionInfoEntity.getMonthWeekWeek().resourceKey)
                                + rb.getString(scheduleConditionInfoEntity.getMonthWeekDay().resourceKey)
                                + "("
                                + LocaleUtils.getString("key.NextTime")
                                + scheduleConditionInfoEntity.getNextSchedule().map(sf::format).orElse("-")
                                + ")";
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    /**
     * レコードを追加する。
     *
     * @param value スケジュール情報エンティティ
     * @return レコード
     */
    @Override
    protected Record createRecord(ScheduleConditionInfoEntity value) {
        Record record = new Record(super.getTable(), true);

        LinkedList<AbstractCell> cells = new LinkedList<>();
        StringProperty stringProperty = new SimpleStringProperty(toMessage(value));
        CellTextField cellTextField = new CellTextField(record, stringProperty, true);
        cellTextField.setPrefWidth(250.0);
        cells.add(cellTextField);

        CellButton<ScheduleConditionInfoEntity> cellButton = new CellButton<>(record, new SimpleStringProperty(LocaleUtils.getString("key.SetProperty")), (event) -> {
            try {
                blockUI.accept(true);
                ScheduleConditionInfoEntity scheduleConditionInfoEntity = this.getEntity(record).clone();
                ButtonType ret = SceneContiner.getInstance().showComponentDialog(LocaleUtils.getString("key.OutputSchedule"), "ScheduleSelectionCompo", scheduleConditionInfoEntity, (Stage) ((Node) event.getSource()).getScene().getWindow());
                if (ButtonType.OK.equals(ret)) {
                    this.getEntity(record).apply(scheduleConditionInfoEntity);
                    stringProperty.setValue(toMessage(scheduleConditionInfoEntity));
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            } finally {
                blockUI.accept(false);
            }
        }, null);
        cellButton.addStyleClass("SimpleButton");
        cellButton.setPrefWidth(50.0);
        cells.add(cellButton);


        record.setCells(cells);

        return record;
    }

    /**
     * エンティティの型を取得する。
     *
     * @return エンティティの型
     */
    @Override
    public Class getEntityClass() {
        return ScheduleConditionInfoEntity.class;
    }
}
