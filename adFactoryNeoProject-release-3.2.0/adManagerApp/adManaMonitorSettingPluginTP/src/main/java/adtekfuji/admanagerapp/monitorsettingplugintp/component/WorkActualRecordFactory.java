/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.monitorsettingplugintp.component;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellAutoNumberLabel;
import jp.adtekfuji.javafxcommon.property.CellButton;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellNumericField;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.CellTimeStampField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 * 工程実績情報テーブルファクトリ
 *
 * @author s-heya
 */
public class WorkActualRecordFactory extends AbstractRecordFactory<WorkEquipmentSetting> {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public WorkActualRecordFactory(Table table, LinkedList<WorkEquipmentSetting> workEquips) {
        super(table, workEquips);
    }

    private String getEquipmentsName(List<Long> equipIds) {
        StringBuilder sb = new StringBuilder();
        if (equipIds.isEmpty()) {
            sb.append(LocaleUtils.getString("key.AndonLineSettingNotAllocate"));
        } else {
            List<EquipmentInfoEntity> equipments = CacheUtils.getCacheEquipment(equipIds);

            List<String> names = equipments.stream().map(p -> p.getEquipmentName()).collect(Collectors.toList());
            sb.append(String.join(", ", names));
        }
        return sb.toString();
    }

    private final EventHandler onActionEvent = (EventHandler) (Event event) -> {
        //
        Button cellButton = (Button) event.getSource();
        WorkEquipmentSetting workEquip = (WorkEquipmentSetting) cellButton.getUserData();
        List<EquipmentInfoEntity> selectEquipments = CacheUtils.getCacheEquipment(workEquip.getEquipmentIds());

        //
        SelectDialogEntity<EquipmentInfoEntity> selectDialogEntity = new SelectDialogEntity().equipments(selectEquipments);
        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity);
        if (ret.equals(ButtonType.OK)) {
            List<Long> equipIds = new ArrayList<>();
            for (EquipmentInfoEntity equip : selectDialogEntity.getEquipments()) {
                equipIds.add(equip.getEquipmentId());
            }
            workEquip.setEquipmentIds(equipIds);
            cellButton.setText(getEquipmentsName(equipIds));
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
        // 分類名
        cells.add(new CellLabel(record, LocaleUtils.getString("key.ClassName")).addStyleClass("ContentTitleLabel"));
        // 工程名
        cells.add(new CellLabel(record, LocaleUtils.getString("key.ProcessName")).addStyleClass("ContentTitleLabel"));
        // 当日計画数
        cells.add(new CellLabel(record, LocaleUtils.getString("key.AndonLineSettingDailyPlan")).addStyleClass("ContentTitleLabel"));
        // 開始時間
        cells.add(new CellLabel(record, LocaleUtils.getString("key.AndonLineSettingStartTime")).addStyleClass("ContentTitleLabel"));
        // 終了時間
        cells.add(new CellLabel(record, LocaleUtils.getString("key.AndonLineSettingEndTime")).addStyleClass("ContentTitleLabel"));
        // 対象設備
        cells.add(new CellLabel(record, LocaleUtils.getString("key.AndonLineSettingSelectEquipment")).addStyleClass("ContentTitleLabel"));

        record.setTitleCells(cells);

        return record;
    }

    /**
     * レコードを作成する。
     *
     * @param setting
     * @return
     */
    @Override
    protected Record createRecord(WorkEquipmentSetting setting) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);
        LinkedList<AbstractCell> cells = new LinkedList<>();
        cells.add(new CellAutoNumberLabel(record, "", super.getRecodeNum() + 1).addStyleClass("ContentTitleLabel"));
        // 分類名
        cells.add(new CellTextField(record, setting.categoryNameProperty()).setPrefWidth(180.0).addStyleClass("ContentTextBox"));
        // 工程名
        cells.add(new CellTextField(record, setting.titleProperty()).setPrefWidth(180.0).addStyleClass("ContentTextBox"));
        // 目標
        cells.add(new CellNumericField(record, setting.planNumProperty()).setPrefWidth(120.0).addStyleClass("ContentTextBox"));
        // 開始時間
        cells.add(new CellTimeStampField(record, setting.startWorkTimeProperty()).setPrefWidth(120.0).addStyleClass("ContentTextBox"));
        // 終了時間
        cells.add(new CellTimeStampField(record, setting.endWorkTimeProperty()).setPrefWidth(120.0).addStyleClass("ContentTextBox"));
        // 設備
        cells.add(new CellButton(record, new SimpleStringProperty(getEquipmentsName(setting.getEquipmentIds())), onActionEvent, setting).addStyleClass("ContentTextBox"));
        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkEquipmentSetting.class;
    }
}
