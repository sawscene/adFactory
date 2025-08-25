/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.entity;

import adtekfuji.cash.CashManager;
import adtekfuji.locale.LocaleUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerFileInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.NameValueEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.commons.collections.map.HashedMap;

/**
 *
 * @author ta-ito
 */
public class LedgerFileListTableDataEntity {


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final LedgerFileInfoEntity ledgerFileInfoEntity;
    private final Map<String, String> nameValueMap;

    private final StringProperty ledgerFileCreator = new SimpleStringProperty();
    private final StringProperty ledgerFileCreateDate = new SimpleStringProperty();
    private final StringProperty worker = new SimpleStringProperty();
    private final StringProperty equipment = new SimpleStringProperty();
    private final StringProperty workStartDate = new SimpleStringProperty();
    private final StringProperty workEndDate = new SimpleStringProperty();

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * コンストラクタ
     *
     * @param entity 工程情報
     */
    public LedgerFileListTableDataEntity(LedgerFileInfoEntity entity) {
        this.ledgerFileInfoEntity = entity;
        CashManager cache = CashManager.getInstance();
        CacheUtils.createCacheEquipment(true);
        CacheUtils.createCacheOrganization(true);

        if (Objects.nonNull(entity.getOrganizationId())) {
            OrganizationInfoEntity creator = (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, entity.getOrganizationId());
            if (Objects.nonNull(creator)) {
                ledgerFileCreator.setValue(creator.getOrganizationName());
            }
        }

        List<Long> orgIds = JsonUtils.jsonToObjects(entity.getOrganizationIds(), Long[].class);
        if (Objects.nonNull(orgIds) && !orgIds.isEmpty()) {
            OrganizationInfoEntity organizationInfoEntity = (OrganizationInfoEntity) cache.getItem(OrganizationInfoEntity.class, orgIds.get(0));
            if (Objects.nonNull(organizationInfoEntity)) {
                String orgName = organizationInfoEntity.getOrganizationName();
                if (orgIds.size() >= 2) {
                    orgName += "(+" + (orgIds.size() - 1) + ")";
                }
                worker.setValue(orgName);
            }
        }

        List<Long> eqIds = JsonUtils.jsonToObjects(entity.getEquipmentIds(), Long[].class);
        if (Objects.nonNull(eqIds) && !eqIds.isEmpty()) {
            EquipmentInfoEntity equipmentInfoEntity = (EquipmentInfoEntity) cache.getItem(EquipmentInfoEntity.class, eqIds.get(0));
            if (Objects.nonNull(equipmentInfoEntity)) {
                String eqName = equipmentInfoEntity.getEquipmentName();
                if (orgIds.size() >= 2) {
                    eqName += "(+" + (eqIds.size()-1) + ")";
                }
                equipment.setValue(eqName);
            }
        }

        if (Objects.nonNull(entity.getUpdateDatetime())) {
            ledgerFileCreateDate.setValue(sdf.format(entity.getUpdateDatetime()));
        }

        if (Objects.nonNull(entity.getFromDate())) {
            workStartDate.setValue(sdf.format(entity.getFromDate()));
        }

        if (Objects.nonNull(entity.getToDate())) {
            workEndDate.setValue(sdf.format(entity.getToDate()));
        }

        if (StringUtils.nonEmpty(entity.getKeyword())) {
            this.nameValueMap
                    = JsonUtils.jsonToObjects(entity.getKeyword(), NameValueEntity[].class)
                    .stream()
                    .collect(toMap(NameValueEntity::getName, NameValueEntity::getValue));
        } else {
            this.nameValueMap = new HashMap<>();
        }

    }

    public LedgerFileInfoEntity getLedgerFileInfoEntity() {
        return ledgerFileInfoEntity;
    }

    public StringProperty ledgerFileCreatorProperty() { return ledgerFileCreator; }
    public StringProperty ledgerFileCreateDateProperty() { return ledgerFileCreateDate; }
    public StringProperty workerProperty() { return worker; }
    public StringProperty equipmentProperty() { return equipment; }
    public StringProperty workStartDateProperty() { return workStartDate; }
    public StringProperty workEndDateProperty() { return workEndDate; }
    public SimpleStringProperty getAddColumnProperty(Function<Map<String, String>, String> func) {
        return new SimpleStringProperty(func.apply(this.nameValueMap));
    }
}

