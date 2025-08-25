/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.record.factory;

import jp.adtekfuji.forfujiapp.utils.WorkKanbanTimeReplaceData;
import jp.adtekfuji.forfujiapp.utils.WorkKanbanTimeReplaceUtils;
import jp.adtekfuji.forfujiapp.utils.WorkflowProcess;
import adtekfuji.cash.CashManager;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellChackBoxLisner;
import jp.adtekfuji.javafxcommon.property.CellCheckBox;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellDateAndTimeStampField;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTimeStampField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程カンバンレコード生成用クラス
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.17.Tsu
 */
public class WorkKanbanRecordFactory extends AbstractRecordFactory<WorkKanbanInfoEntity> implements CellChackBoxLisner {

    private final Properties properties = AdProperty.getProperties();

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class WorkStatusTypeComboBoxCellFactory extends ListCell<KanbanStatusEnum> {

        @Override
        protected void updateItem(KanbanStatusEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
                if (item != KanbanStatusEnum.PLANNED) {
                    setTextFill(Color.GRAY);
                    setDisable(true);
                }
            }
        }
    }

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final static CashManager cashManager = CashManager.getInstance();
    private final Logger logger = LogManager.getLogger();
    private final KanbanInfoEntity kanban;
    private final WorkflowProcess workflowProcess;
    private WorkKanbanTimeReplaceData timeReplaceData;

    public WorkKanbanRecordFactory(Table table, LinkedList<WorkKanbanInfoEntity> entitys, KanbanInfoEntity kanban, WorkflowProcess workflowProcess) {
        super(table, entitys);
        this.kanban = kanban;
        this.workflowProcess = workflowProcess;
    }

    public WorkKanbanRecordFactory timeReplaceData(WorkKanbanTimeReplaceData timeReplaceData) {
        this.timeReplaceData = timeReplaceData;
        return this;
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        // スキップ
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Skip"))).addStyleClass("ContentTitleLabel"));
        // 工程名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.ProcessName"))).addStyleClass("ContentTitleLabel"));
        // ステータス
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Status"))).addStyleClass("ContentTitleLabel"));
        // 設備
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Equipment"))).addStyleClass("ContentTitleLabel"));
        // 組織
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Organization"))).addStyleClass("ContentTitleLabel"));
        // タクトタイム
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.TactTimeTitle"))).addStyleClass("ContentTitleLabel"));
        // 開始日時
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StartDateAndTime"))).addStyleClass("ContentTitleLabel"));
        // 終了日時
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.EndDateAndTime"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(WorkKanbanInfoEntity entity) {
        Record record = new Record(super.getTable(), false).IsSelectCheckRecord(true);
        List<AbstractCell> cells = new ArrayList();
        Callback<ListView<KanbanStatusEnum>, ListCell<KanbanStatusEnum>> comboCellFactory = (ListView<KanbanStatusEnum> param) -> new WorkStatusTypeComboBoxCellFactory();

        boolean isScheduling = Boolean.valueOf(this.properties.getProperty("@Scheduling"));

        //スキップ確認
        CellCheckBox skipCell = new CellCheckBox(record, null, entity.skipFlagProperty()).actionListner(this);
        GridPane.setHalignment(skipCell.getNode(), HPos.CENTER);
        cells.add(skipCell);
        //工程順名
        if (!Objects.nonNull(entity.getWorkName())) {
            entity.setWorkName(LocaleUtils.getString("key.NoWorkName"));
        }
        cells.add(new CellLabel(record, entity.workNameProperty()));
        //ステータス切り替え
        cells.add(new CellComboBox<>(record, Arrays.asList(KanbanStatusEnum.values()), new WorkStatusTypeComboBoxCellFactory(), comboCellFactory, entity.workStatusProperty()));
        //設備切り替え
        cells.add(new CellLabel(record, getEquipmentNames(entity.getEquipmentCollection())));
        //組織切り替え
        cells.add(new CellLabel(record, getOrganizationNames(entity.getOrganizationCollection())));
        //タクトタイム
        cells.add(new CellTimeStampField(record, entity.taktTimeProperty()));
        //開始日時
        cells.add(new CellDateAndTimeStampField(record, entity.startDatetimeProperty(), !isScheduling));
        //終了日時
        cells.add(new CellDateAndTimeStampField(record, entity.compDatetimeProperty(), !isScheduling));

        record.setCells(cells);
        record.setRecordItem(entity);

        // スキップ行はステータス以降の項目を使用不可にする。
        this.setRowDisabled(record, entity.getSkipFlag());

        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkKanbanInfoEntity.class;
    }

    /**
     * スキップのアクション
     *
     * @param box
     */
    @Override
    public void chackAction(CellCheckBox box) {
        Record record = (Record) box.getCellInterface();
        WorkKanbanInfoEntity entity = (WorkKanbanInfoEntity) record.getRecordItem();
        if (!entity.getSeparateWorkFlag()) {
            List<BreakTimeInfoEntity> breaktimeList = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
            this.workflowProcess.updateTimetable(kanban, entity, breaktimeList);
        }

        // スキップ行はステータス以降の項目を使用不可にする。
        this.setRowDisabled(record, entity.getSkipFlag());
    }

    /**
     * 表示用設備名生成
     *
     * @param entitys 対象の工程の設備リスト
     * @return 設備が列挙された文字列
     */
    public StringProperty getEquipmentNames(List<Long> entitys) {
        if (!Objects.nonNull(entitys)) {
            return new SimpleStringProperty("");
        }
        if (entitys.isEmpty()) {
            return new SimpleStringProperty("");
        }

        StringBuilder sb = new StringBuilder();

        entitys.stream().map((equipmentId) -> {
            EquipmentInfoEntity cashData = (EquipmentInfoEntity) cashManager.getItem(EquipmentInfoEntity.class, equipmentId);
            sb.append(cashData.getEquipmentName());
            return equipmentId;
        }).forEach((_item) -> {
            sb.append(",");
        });

        return new SimpleStringProperty(sb.toString());
    }

    /**
     * 表示用組織名生成
     *
     * @param entitys 対象の工程の組織リスト
     * @return 組織が列挙された文字列
     */
    public StringProperty getOrganizationNames(List<Long> entitys) {
        if (!Objects.nonNull(entitys)) {
            return new SimpleStringProperty("");
        }
        if (entitys.isEmpty()) {
            return new SimpleStringProperty("");
        }
        StringBuilder sb = new StringBuilder();

        entitys.stream().map((organizationId) -> {
            OrganizationInfoEntity cashData = (OrganizationInfoEntity) cashManager.getItem(OrganizationInfoEntity.class, organizationId);
            sb.append(cashData.getOrganizationName());
            return organizationId;
        }).forEach((_item) -> {
            sb.append(",");
        });

        return new SimpleStringProperty(sb.toString());
    }

    /**
     * 行の選択・スキップ以外の項目を使用不可にする。
     *
     * @param record 行データ
     * @param isDisabled (true: 使用不可にする, false: 使用可能にする)
     */
    private void setRowDisabled(Record record, boolean isDisabled) {
        for (int i = 2; i < record.getCells().size(); i++) {
            record.getCells().get(i).setDisable(isDisabled);
        }
    }
}
