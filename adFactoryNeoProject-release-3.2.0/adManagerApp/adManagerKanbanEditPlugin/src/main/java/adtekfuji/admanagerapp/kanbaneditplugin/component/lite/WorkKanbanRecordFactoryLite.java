/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.component.lite;

import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkKanbanTimeReplaceUtils;
import adtekfuji.admanagerapp.kanbaneditplugin.utils.WorkflowProcess;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.master.BreakTimeInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellButton;
import jp.adtekfuji.javafxcommon.property.CellChackBoxLisner;
import jp.adtekfuji.javafxcommon.property.CellCheckBox;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 工程カンバン表示生成クラス
 *
 * @author kenji.yokoi
 */
public class WorkKanbanRecordFactoryLite extends AbstractRecordFactory<WorkKanbanInfoEntity> implements CellChackBoxLisner {

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
                setText(RB.getString(item.getResourceKey()));
                if (item != KanbanStatusEnum.PLANNED) {
                    setTextFill(Color.GRAY);
                    setDisable(true);
                }
            }
        }
    }

    private final Properties properties = AdProperty.getProperties();
    private final static ResourceBundle RB = LocaleUtils.getBundle("locale.locale");;
    private final KanbanInfoEntity kanban;
    private final WorkflowProcess workflowProcess;
    private final EventHandler<ActionEvent> onCompAction;

    private final static String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";
    private String pickLiteWorkNameRegex;

    public WorkKanbanRecordFactoryLite(Table table, LinkedList<WorkKanbanInfoEntity> entitys, KanbanInfoEntity kanban, WorkflowProcess workflowProcess, EventHandler<ActionEvent> onCompAction) {
        super(table, entitys);
        this.kanban = kanban;
        this.workflowProcess = workflowProcess;
        this.onCompAction = onCompAction;
        //プロパティから取得
        this.pickLiteWorkNameRegex = properties.getProperty(PICK_LITE_WORK_NAME_REGEX_KEY);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する

        // スキップ
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(RB.getString("key.Skip"))).addStyleClass("ContentTitleLabel"));
        // 工程名
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(RB.getString("key.ProcessName"))).addStyleClass("ContentTitleLabel"));
        // ステータス
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(RB.getString("key.Status"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);
        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(WorkKanbanInfoEntity entity) {
        Record record = new Record(super.getTable(), false).IsSelectCheckRecord(super.getTable().getIsSelectCheckRecord());
        List<AbstractCell> cells = new ArrayList();
        Callback<ListView<KanbanStatusEnum>, ListCell<KanbanStatusEnum>> comboCellFactory = (ListView<KanbanStatusEnum> param) -> new WorkStatusTypeComboBoxCellFactory();

        //スキップ確認
        CellCheckBox skipCell = new CellCheckBox(record, null, entity.skipFlagProperty()).actionListner(this);
        GridPane.setHalignment(skipCell.getNode(), HPos.CENTER);
        cells.add(skipCell.addStyleClass("ContentButton"));
        //工程名
        StringProperty name = new SimpleStringProperty();
        if (!Objects.nonNull(entity.getWorkName())) {
            name.setValue(RB.getString("key.NoWorkName"));
        } else if (Objects.nonNull(entity.getSerialNumber())) {
            name.setValue(entity.getWorkName() + " #" + entity.getSerialNumber());
        } else {
            name.setValue(entity.getWorkName());
        }
        //工程名だけ抽出
        try
        {
            Matcher m = Pattern.compile(this.pickLiteWorkNameRegex).matcher(name.getValue());
            if (m.find()) {
                name.setValue(m.group(1));
            }
        } catch(Exception ex) {
        }
        cells.add(new CellLabel(record, name).addStyleClass("ContentTextBox").setPrefWidth(300));
        //ステータス切り替え
        cells.add(new CellComboBox<>(record, Arrays.asList(KanbanStatusEnum.values()),
                new WorkStatusTypeComboBoxCellFactory(), comboCellFactory,
                entity.workStatusProperty()).addStyleClass("ContentComboBox"));
        
        record.setCells(cells);
        record.setRecordItem(entity);
        
        // スキップ行はステータス以降の項目を使用不可にする。
        this.setRowDisabled(record, 2, entity.getSkipFlag());
        // カンバンステータスが"計画済み"または"作業中"の場合、行を使用不可にする
        KanbanStatusEnum kanbanStatus = kanban.getKanbanStatus();
        if (kanbanStatus.equals(KanbanStatusEnum.PLANNED) || kanbanStatus.equals(KanbanStatusEnum.WORKING)){
            this.setRowDisabled(record, 0, true);
        }

        // 完了ボタン
        StringProperty label = new SimpleStringProperty(RB.getString("key.complete"));
        record.getCells().add(new CellButton<>(record, label, this.onCompAction, entity).addStyleClass("ContentButton"));
        // ステータスに応じて完了ボタンを使用不可にする。
        this.setCompButtomDisabled(record, entity);

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
        try {
            Record record = (Record) box.getCellInterface();

            WorkKanbanInfoEntity entity = (WorkKanbanInfoEntity) record.getRecordItem();
            if (!entity.getSeparateWorkFlag()) {
                List<BreakTimeInfoEntity> breaktimeList = WorkKanbanTimeReplaceUtils.getWorkKanbanBreakTimes(kanban.getWorkKanbanCollection());
                List<HolidayInfoEntity> holidays = WorkKanbanTimeReplaceUtils.getHolidays(kanban.getStartDatetime());
                this.workflowProcess.setBaseTime(kanban, breaktimeList, kanban.getStartDatetime(), holidays);
            }

            // スキップ行はステータス以降の項目を使用不可にする。
            this.setRowDisabled(record, 2, entity.getSkipFlag());
            // ステータスに応じて完了ボタンを使用不可にする。
            this.setCompButtomDisabled(record, entity);
        } catch (Exception ex) {
        }
    }

    /**
     * 行の選択・スキップ以外の項目を使用不可にする。
     *
     * @param record 行データ
     * @param isDisabled (true: 使用不可にする, false: 使用可能にする)
     */
    private void setRowDisabled(Record record, int pos, boolean isDisabled) {
        for (int i = pos; i < record.getCells().size(); i++) {
            record.getCells().get(i).setDisable(isDisabled);
        }
    }
    
    /**
     * 計画中・作業中・中止・作業完了の場合は完了ボタンを使用不可にする。
     * 
     * @param record 
     * @param entity 
     */
    private void setCompButtomDisabled(Record record, WorkKanbanInfoEntity entity) {
        KanbanStatusEnum kanbanStatus = kanban.getKanbanStatus();
        KanbanStatusEnum workStatus = entity.getWorkStatus();
        List<KanbanStatusEnum> checkKanbanStatus = new ArrayList<>(Arrays.asList(
            KanbanStatusEnum.PLANNING, KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.COMPLETION));
        List<KanbanStatusEnum> checkWorkStatus = new ArrayList<>(Arrays.asList(
            KanbanStatusEnum.PLANNING, KanbanStatusEnum.WORKING, KanbanStatusEnum.INTERRUPT, KanbanStatusEnum.COMPLETION));
        if (entity.getSkipFlag() || checkKanbanStatus.contains(kanbanStatus) || checkWorkStatus.contains(workStatus)) {
            record.getCells().get(3).setDisable(true);
        }
    }
}
