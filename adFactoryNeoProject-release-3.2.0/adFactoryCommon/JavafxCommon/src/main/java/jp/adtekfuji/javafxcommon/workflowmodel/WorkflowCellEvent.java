/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import jp.adtekfuji.javafxcommon.enumeration.WorkflowCellEventTypeEnum;

/**
 *
 * @author s-maeda
 */
public class WorkflowCellEvent {
    private final WorkflowCellEventTypeEnum type;
    private final CellBase souce;

    public WorkflowCellEvent(WorkflowCellEventTypeEnum type, CellBase souce) {
        this.type = type;
        this.souce = souce;
    }

    public CellBase getSouce() {
        return souce;
    }

    public WorkflowCellEventTypeEnum getType() {
        return type;
    }
}
