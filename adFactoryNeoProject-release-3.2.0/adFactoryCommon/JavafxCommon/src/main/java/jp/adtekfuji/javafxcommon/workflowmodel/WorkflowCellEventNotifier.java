/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import javax.swing.event.EventListenerList;
import jp.adtekfuji.javafxcommon.enumeration.WorkflowCellEventTypeEnum;

/**
 *
 * @author s-maeda
 */
public class WorkflowCellEventNotifier {

    private static final EventListenerList listenerList = new EventListenerList();

    /**
     * セルが左マウスボタンでダブルクリックされた事を通知する。
     *
     * @param cell
     */
    public static void raiseLeftMouseButtonDoubleClicked(CellBase cell) {
        WorkflowCellEvent event = new WorkflowCellEvent(
                WorkflowCellEventTypeEnum.EVENT_LEFT_MOUSE_BUTTON_DOUBLE_CLICKED, cell);
        for (WorkflowCellEventListener listener : listenerList.getListeners(WorkflowCellEventListener.class)) {
            listener.onWorkflowCellDoubleClicked(event);
        }
    }

    /**
     * WorkflowCellEventListenerを追加する。
     *
     * @param listener
     */
    public static void addWorkflowCellEventListener(WorkflowCellEventListener listener) {
        listenerList.add(WorkflowCellEventListener.class, listener);
    }

    /**
     * WorkflowCellEventListenerを削除する。
     *
     * @param listener
     */
    public static void removeWorkflowCellEventListener(WorkflowCellEventListener listener) {
        listenerList.remove(WorkflowCellEventListener.class, listener);
    }
}
