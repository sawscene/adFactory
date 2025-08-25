/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditpluginels.common;

import adtekfuji.admanagerapp.kanbaneditpluginels.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.kanbaneditpluginels.utils.WorkflowProcess;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

/**
 * 工程カンバン表示生成クラス
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
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

    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");
    private final static CashManager CASH_MANAGER = CashManager.getInstance();
    private final KanbanInfoEntity kanban;
    private final WorkflowProcess workflowProcess;

    public WorkKanbanRecordFactory(Table table, LinkedList<WorkKanbanInfoEntity> entitys, KanbanInfoEntity kanban, WorkflowProcess workflowProcess) {
        super(table, entitys);
        this.kanban = kanban;
        this.workflowProcess = workflowProcess;
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.ProcessName"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Skip"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Status"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Equipment"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Organization"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.TactTimeTitle"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StartDateAndTime"))).addStyleClass("ContentTitleLabel"));
        //
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

        //工程順名
        StringProperty name = new SimpleStringProperty();
        if (!Objects.nonNull(entity.getWorkName())) {
            name.setValue(LocaleUtils.getString("key.NoWorkName"));
        } else if (Objects.nonNull(entity.getSerialNumber())) {
            name.setValue(entity.getWorkName() + " #" + entity.getSerialNumber());
        } else {
            name.setValue(entity.getWorkName());
        }
        cells.add(new CellLabel(record, name));
        //スキップ確認
        cells.add(new CellCheckBox(record, null, entity.skipFlagProperty()).actionListner(this));
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
            EquipmentInfoEntity cashData = (EquipmentInfoEntity) CASH_MANAGER.getItem(EquipmentInfoEntity.class, equipmentId);
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
            OrganizationInfoEntity cashData = (OrganizationInfoEntity) CASH_MANAGER.getItem(OrganizationInfoEntity.class, organizationId);
            sb.append(cashData.getOrganizationName());
            return organizationId;
        }).forEach((_item) -> {
            sb.append(",");
        });

        return new SimpleStringProperty(sb.toString());
    }

}
