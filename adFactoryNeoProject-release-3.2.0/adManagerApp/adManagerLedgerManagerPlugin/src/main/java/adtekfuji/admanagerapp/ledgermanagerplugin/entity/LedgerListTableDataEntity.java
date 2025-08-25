/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.entity;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.ledger.LedgerConditionEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleConditionInfoEntity;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 * @author yu.nara
 */
public class LedgerListTableDataEntity {
    SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    private final LedgerInfoEntity ledgerInfoEntity;
    private final StringProperty ledgerName = new SimpleStringProperty();
    private final StringProperty ledgerType = new SimpleStringProperty();
    private final StringProperty updateDatetime = new SimpleStringProperty();
    private final StringProperty lastDatetime = new SimpleStringProperty();
    private final StringProperty nextDatetime = new SimpleStringProperty();


    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * コンストラクタ
     *
     * @param entity 工程情報
     */
    public LedgerListTableDataEntity(LedgerInfoEntity entity) {
        this.ledgerInfoEntity = entity;
        LedgerConditionEntity ledgerConditionEntity = entity.getLedgerCondition();
        this.ledgerName.setValue(entity.getLedgerName());
        this.ledgerType.setValue(rb.getString(ledgerConditionEntity.getLedgerType().resourceKey));
        this.updateDatetime.setValue(StringTime.convertDateToString(entity.getUpdateDatetime(), LocaleUtils.getString("key.DateTimeFormat")));
        this.lastDatetime.setValue(StringTime.convertDateToString(entity.getLastImplementDatetime(), LocaleUtils.getString("key.DateTimeFormat")));

        ledgerConditionEntity
                .getScheduleConditionInfoEntity()
                .stream()
                .map(ScheduleConditionInfoEntity::getNextSchedule)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted()
                .findFirst()
                .ifPresent(date -> this.nextDatetime.setValue(sf.format(date)));
    }

    /**
     * 反映
     * @param entity 反映元
     */
    public void apply(LedgerInfoEntity entity) {
        this.ledgerInfoEntity.apply(entity);
        LedgerConditionEntity ledgerConditionEntity = entity.getLedgerCondition();
        this.ledgerName.setValue(entity.getLedgerName());
        this.ledgerType.setValue(rb.getString(ledgerConditionEntity.getLedgerType().resourceKey));
        this.updateDatetime.setValue(StringTime.convertDateToString(entity.getUpdateDatetime(), LocaleUtils.getString("key.DateTimeFormat")));

        ledgerConditionEntity
                .getScheduleConditionInfoEntity()
                .stream()
                .map(ScheduleConditionInfoEntity::getNextSchedule)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted()
                .findFirst()
                .ifPresent(date -> this.nextDatetime.setValue(sf.format(date)));
    }

    public LedgerInfoEntity getLedgerInfoEntity() {
        return ledgerInfoEntity;
    }
    public StringProperty ledgerNameProperty() {
        return ledgerName;
    }
    public StringProperty ledgerTypeProperty() {
        return ledgerType;
    }
    public StringProperty updateDatetimeProperty() {
        return updateDatetime;
    }
    public StringProperty nextDatetimeProperty() { return nextDatetime; }
    public StringProperty lastDatetimeProperty() { return lastDatetime; }
}

