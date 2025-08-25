/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.andonsetting.dialog;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;

/**
 * ライン選択ダイアログ
 *
 * @author fu-kato
 */
public class LineSelectDialog {

    private static final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();

    public static List<EquipmentInfoEntity> getLines(List<Long> targets) {
        return targets.stream()
                .map(id -> equipmentInfoFacade.find(id))
                .collect(Collectors.toList());
    }

    /**
     * 設備IDのリストを受け取り選択した設備エンティティのリストを設定する
     *
     * @param event
     * @return
     */
    public static ButtonType showDialogLineEntity(Event event) {
        Button eventSrc = (Button) event.getSource();
        List<Long> targets = (List) eventSrc.getUserData();

        List<EquipmentInfoEntity> lines = getLines(targets);

        SelectDialogEntity<EquipmentInfoEntity> selectDialogEntity = new SelectDialogEntity();
        if (Objects.nonNull(lines)) {
            selectDialogEntity.equipments(lines);
        }

        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        SceneContiner sc = SceneContiner.getInstance();

        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity, (Stage) ((Node) event.getSource()).getScene().getWindow(), true);
        if (ret.equals(ButtonType.OK)) {
            lines.clear();

            if (!selectDialogEntity.getEquipments().isEmpty()) {
                lines.addAll(selectDialogEntity.getEquipments());
            }
            eventSrc.setUserData(lines);
        }

        return ret;
    }

    /**
     * 設備IDのリストを受け取り選択した設備IDのリストを設定する
     *
     * @param event
     * @return
     */
    public static ButtonType showDialog(Event event) {
        Button eventSrc = (Button) event.getSource();
        List<Long> targets = (List) eventSrc.getUserData();

        List<EquipmentInfoEntity> lines = getLines(targets);

        SelectDialogEntity<EquipmentInfoEntity> selectDialogEntity = new SelectDialogEntity();
        if (Objects.nonNull(lines)) {
            selectDialogEntity.equipments(lines);
        }

        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        SceneContiner sc = SceneContiner.getInstance();

        ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Equipment"), "EquipmentSelectionCompo", selectDialogEntity, (Stage) ((Node) event.getSource()).getScene().getWindow(), true);
        if (ret.equals(ButtonType.OK)) {
            lines.clear();

            if (!selectDialogEntity.getEquipments().isEmpty()) {
                lines.addAll(selectDialogEntity.getEquipments());
            }
            eventSrc.setUserData(lines);
        }

        return ret;
    }
}
