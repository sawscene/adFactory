/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.comparator;

import java.util.Comparator;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 *
 * @author e-mori
 */
public class WorkKanbanStartDateComparator implements Comparator<WorkKanbanInfoEntity> {

    @Override
    public int compare(WorkKanbanInfoEntity o1, WorkKanbanInfoEntity o2) {
        return o1.getStartDatetime().before(o2.getStartDatetime()) ? -1 : 1;
    }

}
