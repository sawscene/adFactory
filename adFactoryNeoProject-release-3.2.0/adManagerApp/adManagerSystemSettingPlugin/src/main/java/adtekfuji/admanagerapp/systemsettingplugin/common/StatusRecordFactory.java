/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.Arrays;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.CellCheckBox;
import jp.adtekfuji.javafxcommon.property.CellColorPicker;
import jp.adtekfuji.javafxcommon.property.CellComboBox;
import jp.adtekfuji.javafxcommon.property.CellFileChooser;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;

/**
 *
 * @author e.mori
 */
public class StatusRecordFactory extends AbstractRecordFactory<DisplayedStatusInfoEntity> {

    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * プロパティ情報型表示用セルクラス
     *
     */
    class LightPatternTypeComboBoxCellFactory extends ListCell<LightPatternEnum> {

        @Override
        protected void updateItem(LightPatternEnum item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString(item.getResourceKey()));
            }
        }
    }

    public StatusRecordFactory(Table table, LinkedList<DisplayedStatusInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();
        //タイトルに該当するカラムを追加する
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.Status"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StatusMonitorNotation"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.FontColor"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.BackColor"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.LightPattern"))).addStyleClass("ContentTitleLabel"));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StatusMelodyPath"))).addStyleClass("ContentTitleLabel").setPrefWidth(150.0));
        //
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(LocaleUtils.getString("key.StatusMelodyRepeart"))).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    @Override
    protected Record createRecord(DisplayedStatusInfoEntity entity) {
        Record record = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList<>();
        Callback<ListView<LightPatternEnum>, ListCell<LightPatternEnum>> comboCellFactory = (ListView<LightPatternEnum> param) -> new LightPatternTypeComboBoxCellFactory();

        //ステータス
        cells.add(new CellLabel(record, new SimpleStringProperty(getStatusName(entity.getStatusName()))).addStyleClass("ContentTextBox"));
        //表記
        cells.add(new CellTextField(record, entity.notationNameProperty()));
        //文字色
        cells.add(new CellColorPicker(record, entity.fontColorProperty()));
        //背景色
        cells.add(new CellColorPicker(record, entity.backColorProperty()));
        //点灯パターン
        cells.add(new CellComboBox<>(record, Arrays.asList(LightPatternEnum.values()), new LightPatternTypeComboBoxCellFactory(), comboCellFactory, entity.lightPatternProperty()));
        //メロディパス
        cells.add(new CellFileChooser(record, entity.melodyPathProperty(), "ContentTextBox-small", "DeleteButton",""));
        //メロディ繰り返し
        cells.add(new CellCheckBox(record, "", entity.melodyRepeatProperty()));

        record.setCells(cells);

        return record;
    }

    @Override
    public Class getEntityClass() {
        return DisplayedStatusInfoEntity.class;
    }

    private String getStatusName(StatusPatternEnum status) {
        if (Objects.isNull(status)) {
            return "key.UnkownStatus";
        }

        String statusName = LocaleUtils.getString(status.getResourceKey());
        switch (status) {
            case PLAN_NORMAL:
            case PLAN_DELAYSTART:
                return LocaleUtils.getString(KanbanStatusEnum.PLANNED.getResourceKey()) + "(" + statusName + ")";
            case WORK_NORMAL:
            case WORK_DELAYSTART:
            case WORK_DELAYCOMP:
                return LocaleUtils.getString(KanbanStatusEnum.WORKING.getResourceKey()) + "(" + statusName + ")";
            case INTERRUPT_NORMAL:
                return LocaleUtils.getString(KanbanStatusEnum.INTERRUPT.getResourceKey());
            case SUSPEND_NORMAL:
                return LocaleUtils.getString(KanbanStatusEnum.SUSPEND.getResourceKey());
            case COMP_NORMAL:
            case COMP_DELAYCOMP:
                return LocaleUtils.getString(KanbanStatusEnum.COMPLETION.getResourceKey()) + "(" + statusName + ")";
            case BREAK_TIME:
            case CALLING:
            case DEFECT:
                return LocaleUtils.getString(status.getResourceKey());
            default:
                return LocaleUtils.getString("key.UnkownStatus");
        }
    }
}
