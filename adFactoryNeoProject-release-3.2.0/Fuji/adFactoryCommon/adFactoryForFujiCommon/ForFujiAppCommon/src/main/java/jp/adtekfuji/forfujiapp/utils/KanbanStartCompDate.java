/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author yu.kikukawa
 */
public class KanbanStartCompDate {

    /**
     * 工程カンバン(追加工程)の一覧から一番遅い完了時間を取得する
     *
     * @param entities
     * @return
     */
    public static Date getWorkKanbanCompDateTime(List<WorkKanbanInfoEntity> entities) {
        
        if(Objects.isNull(entities))
        {
            return null;
        }
        
        // 工程カンバンの一覧からスキップ工程を抜く.
        entities.removeIf(entitie -> {
            return entitie.getSkipFlag() == true;
        });

        Optional<WorkKanbanInfoEntity> start = entities.stream().max(Comparator.comparing(entity -> entity.getCompDatetime()));
        if (start.isPresent()) {
            return start.get().getCompDatetime();
        }

        return null;
    }

    /**
     * 工程カンバン(追加工程)の一覧から一番速い開始時間を取得する
     *
     * @param entities
     * @return
     */
    public static Date getWorkKanbanStartDateTime(List<WorkKanbanInfoEntity> entities) {
        
        if(Objects.isNull(entities))
        {
            return null;
        }
        // 工程カンバンの一覧からスキップ工程を抜く.
        entities.removeIf(entitie -> {
            return entitie.getSkipFlag() == true;
        });

        Optional<WorkKanbanInfoEntity> start = entities.stream().min(Comparator.comparing(entity -> entity.getStartDatetime()));
        if (start.isPresent()) {
            return start.get().getStartDatetime();
        }

        return null;
    }
}
