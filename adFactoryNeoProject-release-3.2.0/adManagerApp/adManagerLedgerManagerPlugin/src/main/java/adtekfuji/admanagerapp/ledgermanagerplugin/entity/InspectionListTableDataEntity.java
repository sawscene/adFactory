/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.entity;

import adtekfuji.admanagerapp.ledgermanagerplugin.utils.LedgerTagUtils;
import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.stream.Collectors.*;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.actual.ActualPropertyEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.NameValueEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;


/**
 *
 * @author ta-ito
 */
public class InspectionListTableDataEntity {

    private final List<ActualResultEntity> actualResultEntity;
    Map<String, String> tagMap;
    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final StringProperty dateTime = new SimpleStringProperty();
    private final StringProperty workflowName = new SimpleStringProperty();
    private final StringProperty workName = new SimpleStringProperty();
    private final StringProperty equipmentName = new SimpleStringProperty();
    private final StringProperty workerName = new SimpleStringProperty();
    private static final SimpleDateFormat SDFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final LedgerTagUtils ledgerTagUtils = LedgerTagUtils.getInstance();

    /**
     * コンストラクタ
     *
     * @param entity 工程情報
     */
    public InspectionListTableDataEntity(List<ActualResultEntity> actualResult, Map<Long, EquipmentInfoEntity> equipmentMap) {
        this.actualResultEntity = actualResult;
        this.dateTime.setValue(SDFORMAT.format(actualResult.get(0).getImplementDatetime()));

        this.workflowName.setValue(actualResult.get(0).getWorkflowName());
        if (actualResult.size() == 1) {
            this.workName.setValue(actualResult.get(0).getWorkName());
        } else {
            this.workName.setValue(String.format("%s(+%d)", actualResult.get(0).getWorkName(), actualResult.size()-1));
        }
        this.equipmentName.setValue(actualResult.get(0).getEquipmentName());
        this.workerName.setValue(actualResult.get(0).getOrganizationName());

        this.tagMap
                = actualResultEntity
                .stream()
                .map(ActualResultEntity::getActualAddInfo)
                .map(actualAddInfo -> JsonUtils.jsonToObjects(actualAddInfo, ActualPropertyEntity[].class))
                .flatMap(Collection::stream)
                .filter(item -> CustomPropertyTypeEnum.TYPE_TRACE.equals(item.getActualPropType()))
                .filter(item -> Objects.nonNull(item.getActualPropValue()))
                .collect(toMap(ActualPropertyEntity::getActualPropName, ActualPropertyEntity::getActualPropValue, (a,b)->a));

        this.tagMap.put("TAG_WORK_NAME", actualResultEntity.get(0).getWorkflowName());
        this.tagMap.put("TAG_WORK_ORGANIZATION", actualResultEntity.get(0).getOrganizationName());
        this.tagMap.put("TAG_WORK_ACTUAL_END", new SimpleDateFormat().format(actualResultEntity.get(0).getImplementDatetime()));
        this.tagMap.put("TAG_WORK_EQUIPMENT", actualResultEntity.get(0).getEquipmentName());

        Long eqid = actualResultEntity.get(0).getFkEquipmentId();
        if (equipmentMap.containsKey(eqid)) {
            EquipmentInfoEntity equipmentInfoEntity = equipmentMap.get(eqid);
            this.tagMap.put("TAG_WORK_EQUIPMENT_IDENTIFY", equipmentInfoEntity.getEquipmentIdentify());
        }
    }

    public List<ActualResultEntity> getActualResultEntity() { return this.actualResultEntity; }

    public BooleanProperty selectedProperty() {return this.selected;}
    public StringProperty dateTimeProperty() {return this.dateTime;}
    public StringProperty workflowNameProperty() {return workflowName;}
    public StringProperty workNameProperty() {return workName;}
    public StringProperty equipmentNameProperty() { return equipmentName; }
    public StringProperty workerNameProperty() {return workerName;}

    public StringProperty addColumnProperty(NameValueEntity entity) {
        return new SimpleStringProperty(tagMap.putIfAbsent(entity.getValue(), ledgerTagUtils.getVale(actualResultEntity, entity.getValue())));
    }

}

