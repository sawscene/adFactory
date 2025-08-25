/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingpluginels.common;

import adtekfuji.clientservice.WorkInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.andon.property.WorkSetting;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellAutoNumberLabel;
import jp.adtekfuji.javafxcommon.property.CellButton;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;

/**
 * 中断発生率情報テーブルファクトリ
 *
 * @author s-heya
 */
public class SuspendedRecordFactory extends AbstractRecordFactory<WorkSetting> {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final WorkInfoFacade workFacade = new WorkInfoFacade();

    public SuspendedRecordFactory(Table table, LinkedList<WorkSetting> works) {
        super(table, works);
    }

    private String getWorksName(List<Long> workIds) {
        StringBuilder sb = new StringBuilder();
        if (workIds.isEmpty()) {
            sb.append(LocaleUtils.getString("key.AndonLineSettingNotAllocate"));
        } else {
            for (Long id : workIds) {
                WorkInfoEntity work = workFacade.find(id);
                sb.append(sb.length() == 0 ? "" : ", ");
                sb.append(work.getWorkName());
            }
        }
        return sb.toString();
    }

    private final EventHandler onActionEvent = (EventHandler) (Event event) -> {
        //
        Button cellButton = (Button) event.getSource();
        WorkSetting work = (WorkSetting) cellButton.getUserData();
        List<WorkInfoEntity> selectWorks = new ArrayList<>();
        for (Long id : work.getWorkIds()) {
            selectWorks.add(workFacade.find(id));
        }
        //
        SelectDialogEntity<WorkInfoEntity> selectDialogEntity = new SelectDialogEntity().works(selectWorks);
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Process"), "WorksSelectionCompo", selectDialogEntity);
        if (ret.equals(ButtonType.OK)) {
            List<Long> workIds = new ArrayList<>();
            for (WorkInfoEntity workEnt : selectDialogEntity.getWorks()) {
                workIds.add(workEnt.getWorkId());
            }
            work.setWorkIds(workIds);
            cellButton.setText(getWorksName(workIds));
        }
    };

    /**
     * タイトルを生成する
     *
     * @return
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record record = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        cells.add(new CellLabel(record, ""));
        // 工程名
        cells.add(new CellLabel(record, LocaleUtils.getString("key.ProcessName")).addStyleClass("ContentTitleLabel"));
        // 対象工程
        cells.add(new CellLabel(record, LocaleUtils.getString("key.AndonLineSettingSelectWork")).addStyleClass("ContentTitleLabel"));

        record.setTitleCells(cells);

        return record;
    }

    /**
     * レコードを作成する。
     *
     * @param work
     * @return
     */
    @Override
    protected Record createRecord(WorkSetting work) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);
        LinkedList<AbstractCell> cells = new LinkedList<>();
        cells.add(new CellAutoNumberLabel(record, "", super.getRecodeNum() + 1).addStyleClass("ContentTitleLabel"));
        cells.add(new CellTextField(record, work.titleProperty()).addStyleClass("ContentTextBox"));
        //cells.add(new CellNumericField(record, work.planNumProperty()).addStyleClass("ContentTextBox"));
        cells.add(new CellButton(record, new SimpleStringProperty(getWorksName(work.getWorkIds())), onActionEvent, work).addStyleClass("ContentTextBox"));
        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkSetting.class;
    }
}
