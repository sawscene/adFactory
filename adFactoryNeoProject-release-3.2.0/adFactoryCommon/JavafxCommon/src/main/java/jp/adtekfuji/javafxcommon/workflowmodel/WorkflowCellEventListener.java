/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import java.util.EventListener;

/**
 *
 * @author s-maeda
 */
public interface WorkflowCellEventListener extends EventListener{

    public void onWorkflowCellDoubleClicked (WorkflowCellEvent event);
}
