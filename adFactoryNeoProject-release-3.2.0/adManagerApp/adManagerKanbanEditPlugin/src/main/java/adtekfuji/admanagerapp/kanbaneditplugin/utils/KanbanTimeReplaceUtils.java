/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.utils;

import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author e-mori
 */
public class KanbanTimeReplaceUtils {

    /**
     * カンバン予定開始時間更新処理
     *
     * @param kanbanInfoEntity
     * @param entity
     */
    public static void batchKanbanStartAndCompTime(KanbanInfoEntity kanbanInfoEntity, WorkKanbanInfoEntity entity) {
        //スキップだったら開始終了時間に変更はない.
        if(entity.getSkipFlag())
        {
            return;
        }
        
        if (Objects.nonNull(kanbanInfoEntity.getStartDatetime())) {
            if (kanbanInfoEntity.getStartDatetime().after(entity.getStartDatetime())) {
                kanbanInfoEntity.setStartDatetime(entity.getStartDatetime());
            }
        } else {
            kanbanInfoEntity.setStartDatetime(entity.getStartDatetime());
        }
        if (Objects.nonNull(kanbanInfoEntity.getCompDatetime())) {
            if (kanbanInfoEntity.getCompDatetime().before(entity.getCompDatetime())) {
                kanbanInfoEntity.setCompDatetime(entity.getCompDatetime());
            }
        } else {
            kanbanInfoEntity.setCompDatetime(entity.getCompDatetime());
        }

    }
}
