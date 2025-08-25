/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adcollabokit;

import javax.ejb.Remote;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;

/**
 *
 * @author s-heya
 */
@Remote
public interface KanbanBean {
    public KanbanEntity getKanban(Long kanbanId);
    public KanbanEntity getKanban(String kanbanName, String workflowName);
}
