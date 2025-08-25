/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowSeparateworkInfoEntity;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellCheckBox;
import jp.adtekfuji.javafxcommon.property.CellDateAndTimeStampField;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 *
 * @author ta.ito
 */
public class SeparateworkRecordFactory extends AbstractRecordFactory<ConWorkflowSeparateworkInfoEntity> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public SeparateworkRecordFactory(Table table, LinkedList<ConWorkflowSeparateworkInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Skip"))).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.ProcessName"))).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Equipment"))).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Organization"))).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StartDateAndTime"))).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EndDateAndTime"))).addStyleClass("ContentTitleLabel"));
        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(ConWorkflowSeparateworkInfoEntity entity) {
        Record record = new Record(super.getTable(), false).IsSelectCheckRecord(true);
        List<AbstractCell> cells = new ArrayList();

        if (!Objects.nonNull(entity.getWorkName())) {
            entity.setWorkName(LocaleUtils.getString("key.NoWorkName"));
        }
        //スキップ確認
        CellCheckBox skipCheckBox = new CellCheckBox(record, null, entity.skipFlagProperty());
        GridPane.setHalignment(skipCheckBox.getNode(), HPos.CENTER);
        cells.add(skipCheckBox);
        //工程順名
        cells.add(new CellLabel(record, entity.workNameProperty()));
        //設備切り替え getEquipmentNames(entity.getEquipmentCollection()
        cells.add(new CellLabel(record, getEquipmentNames(entity.getEquipmentCollection())));
        //組織切り替え getOrganizationNames(entity.getOrganizationCollection()
        cells.add(new CellLabel(record, getOrganizationNames(entity.getOrganizationCollection())));
        //開始日時
        cells.add(new CellDateAndTimeStampField(record, entity.standardStartTimeProperty()));
        //終了日時
        cells.add(new CellDateAndTimeStampField(record, entity.standardEndTimeProperty()));

        record.setCells(cells);
        record.setRecordItem(entity);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return ConWorkflowSeparateworkInfoEntity.class;
    }

    /**
     * 表示用設備名生成
     *
     * @param equipmentIds 対象の工程の設備リスト
     * @return 設備が列挙された文字列
     */
    public StringProperty getEquipmentNames(List<Long> equipmentIds) {
        StringBuilder sb = new StringBuilder();
        equipmentIds.stream().forEach(equipmentId -> {
            EquipmentInfoEntity cashData = CacheUtils.getCacheEquipment(equipmentId);
            if (Objects.nonNull(cashData)) {
                sb.append(cashData.getEquipmentName());
                sb.append(",");
            }
        });

        if (sb.length() > 0) {
            sb.replace(sb.length() - 1, sb.length(), " ");
        }

        return new SimpleStringProperty(sb.toString());
    }

    /**
     * 表示用組織名生成
     *
     * @param organizationIds 対象の工程の組織リスト
     * @return 組織が列挙された文字列
     */
    public StringProperty getOrganizationNames(List<Long> organizationIds) {
        StringBuilder sb = new StringBuilder();
        organizationIds.stream().forEach(organizationId -> {
            OrganizationInfoEntity cashData = CacheUtils.getCacheOrganization(organizationId);
            if (Objects.nonNull(cashData)) {
                sb.append(cashData.getOrganizationName());
                sb.append(",");
            }
        });

        if (sb.length() > 0) {
            sb.replace(sb.length() - 1, sb.length(), " ");
        }

        return new SimpleStringProperty(sb.toString());
    }
}
