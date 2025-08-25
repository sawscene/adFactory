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
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;

/**
 *
 * @author e.mori
 */
public class WorkEquipmentRecordFactory extends AbstractRecordFactory<WorkEquipmentSetting> {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    public WorkEquipmentRecordFactory(Table table, LinkedList<WorkEquipmentSetting> workEquips) {
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
        cells.add(new CellLabel(record, LocaleUtils.getString("key.DisplayName")).addStyleClass("ContentTitleLabel"));
        cells.add(new CellLabel(record, LocaleUtils.getString("key.Equipment")).addStyleClass("ContentTitleLabel"));

        record.setTitleCells(cells);

        return record;
    }

    @Override
    protected Record createRecord(WorkEquipmentSetting workEquip) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);
        LinkedList<AbstractCell> cells = new LinkedList<>();
        cells.add(new CellAutoNumberLabel(record, "", super.getRecodeNum() + 1).addStyleClass("ContentTitleLabel"));
        cells.add(new CellTextField(record, workEquip.titleProperty()).addStyleClass("ContentTextBox"));
        cells.add(new CellButton(record, new SimpleStringProperty(getEquipmentsName(workEquip.getEquipmentIds())), onActionEvent, workEquip).addStyleClass("ContentTextBox"));
        record.setCells(cells);
        return record;
    }

    @Override
    public Class getEntityClass() {
        return WorkEquipmentSetting.class;
    }
}
