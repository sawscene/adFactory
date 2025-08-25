/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.utils;

import java.util.Comparator;
import java.util.Date;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author yu.kikukawa
 */
public class WorkKanbanComparator implements Comparator<WorkKanbanInfoEntity> {

    /**
     * workKanbanInfoEntityを比較する。
     *
     * @param workKanbanInfoEntity1
     * @param workKanbanInfoEntity2
     * @return
     */
    @Override
    public int compare(WorkKanbanInfoEntity workKanbanInfoEntity1, WorkKanbanInfoEntity workKanbanInfoEntity2) {
        Date startTime1 = workKanbanInfoEntity1.getStartDatetime();
        Date startTime2 = workKanbanInfoEntity2.getStartDatetime();
        int compare = startTime1.compareTo(startTime2);
        if (0 == compare) {
            int order1 = workKanbanInfoEntity1.getWorkKanbanOrder();
            int order2 = workKanbanInfoEntity2.getWorkKanbanOrder();
            return order1 <= order2 ? -1 : 1;
        }
        return compare;
    }
}
