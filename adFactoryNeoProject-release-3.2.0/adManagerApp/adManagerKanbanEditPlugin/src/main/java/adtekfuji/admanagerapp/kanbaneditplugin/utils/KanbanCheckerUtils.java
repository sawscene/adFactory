/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.utils;

import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanPropertyInfoEntity;

/**
 *
 * @author e-mori
 */
public class KanbanCheckerUtils {

    /**
     * カンバン情報が空欄になっていないか確認
     *
     * @param kanbanName
     * @param kanbanPropertyInfoEntitys
     * @return
     */
    public static Boolean checkEmptyKanban(String kanbanName, List<KanbanPropertyInfoEntity> kanbanPropertyInfoEntitys) {

        if (Objects.isNull(kanbanName) || "".equals(kanbanName)) {
            return true;
        }
        for (KanbanPropertyInfoEntity entity : kanbanPropertyInfoEntitys) {
            entity.updateMember();
            if (Objects.isNull(entity.getKanbanPropertyName())
                    || entity.getKanbanPropertyName().isEmpty()
                    || Objects.isNull(entity.getKanbanPropertyType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 工程カンバンの情報が空になっていないか確認
     *
     * @param entitys
     * @return
     */
    public static Boolean checkEmptyWorkKanbanProp(List<WorkKanbanPropertyInfoEntity> entitys) {
        for (WorkKanbanPropertyInfoEntity entity : entitys) {
            entity.updateMember();
            if (StringUtils.isEmpty(entity.getWorkKanbanPropName())
                    || Objects.isNull(entity.getWorkKanbanPropType())) {
                return true;
            }
        }
        return false;
    }

    public enum KanbanValidEnum {

        TIME_COMP_ERR("key.TimeFormatErrMessage"),
        DATE_COMP_ERR("key.DateCompErrMessage"),
        SUCCSESS("");

        private final String resourceKey;

        private KanbanValidEnum(String resourceKey) {
            this.resourceKey = resourceKey;
        }

        public String getResourceKey() {
            return resourceKey;
        }
    }

    public static KanbanValidEnum validItems(KanbanInfoEntity kanbanInfoEntity) {
        //タクトタイムフォーマット判定
        if (kanbanInfoEntity.getWorkKanbanCollection().stream().anyMatch(workKanban -> (!StringTime.validStringTime(workKanban.taktTimeProperty().get())
                || StringTime.convertStringTimeToMillis(workKanban.taktTimeProperty().get()) > Integer.MAX_VALUE))
                || kanbanInfoEntity.getSeparateworkKanbanCollection().stream().anyMatch(separatework -> (!StringTime.validStringTime(separatework.taktTimeProperty().get())
                        || StringTime.convertStringTimeToMillis(separatework.taktTimeProperty().get()) > Integer.MAX_VALUE))) {
//            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
            return KanbanValidEnum.TIME_COMP_ERR;
        }

        //開始時間、終了時間フォーマット判定
        if (kanbanInfoEntity.getWorkKanbanCollection().stream().anyMatch(workKanban -> (Objects.isNull(workKanban.getStartDatetime()) || Objects.isNull(workKanban.getCompDatetime())))
                || kanbanInfoEntity.getSeparateworkKanbanCollection().stream().anyMatch(separatework -> (Objects.isNull(separatework.getStartDatetime()) || Objects.isNull(separatework.getCompDatetime())))) {
//            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.TimeFormatErrMessage"));
            return KanbanValidEnum.TIME_COMP_ERR;
        }

        //開始時間<終了時間判定
        if (kanbanInfoEntity.getWorkKanbanCollection().stream().anyMatch(workKanban -> (workKanban.getStartDatetime().getTime() > workKanban.getCompDatetime().getTime()))
                || kanbanInfoEntity.getSeparateworkKanbanCollection().stream().anyMatch(separatework -> (separatework.getStartDatetime().getTime() > separatework.getCompDatetime().getTime()))) {
//            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.DateCompErrMessage"));
            return KanbanValidEnum.DATE_COMP_ERR;
        }

        return KanbanValidEnum.SUCCSESS;
    }

}
