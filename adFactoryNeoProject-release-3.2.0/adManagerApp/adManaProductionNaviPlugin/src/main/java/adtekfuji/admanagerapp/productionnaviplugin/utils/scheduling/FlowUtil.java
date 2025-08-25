/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling;

import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.bpmn.model.entity.BpmnEndEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnSequenceFlow;
import jp.adtekfuji.bpmn.model.entity.BpmnStartEvent;
import jp.adtekfuji.bpmn.model.entity.BpmnTask;

/**
 *
 * @since 2018/10/31
 * @author j.min
 */
public class FlowUtil {

    private final KanbanInfoEntity kanban;
    private final WorkflowInfoEntity workflow;

    public FlowUtil(KanbanInfoEntity kanban, WorkflowInfoEntity workflow) {
        this.kanban = kanban;
        this.workflow = workflow;
    }
    
    public List<BpmnSequenceFlow> getSeparateFlows() {
        List<BpmnSequenceFlow> result = new ArrayList<>();
                
        boolean isStart = true;
        String beforeId = "";
        for (WorkKanbanInfoEntity workKanban : kanban.getSeparateworkKanbanCollection()) {
            if(!isStart) {
                result.add(getSequence(beforeId, beforeId = String.valueOf(workKanban.getFkWorkId())));
            } else {
                result.add(getStartSequence(beforeId = String.valueOf(workKanban.getFkWorkId())));
                isStart = false;
            }
        }
        result.add(getEndSequence(beforeId));
        
        return result;
    }

    private BpmnSequenceFlow getStartSequence(String startId) {
        BpmnSequenceFlow startSeq = new BpmnSequenceFlow();
        startSeq.setSourceRef("start_id");
        startSeq.setTargetRef(startId);
        
        BpmnStartEvent start = new BpmnStartEvent("start_id", "start");
        startSeq.setSourceNode(start);
        BpmnTask startTask = new BpmnTask(startId, "");
        startSeq.setTargetNode(startTask);
        
        return startSeq;
    }
    
    private BpmnSequenceFlow getSequence(String sourceId, String targetId) {
        BpmnSequenceFlow seq = new BpmnSequenceFlow();
        seq.setSourceRef(sourceId);
        seq.setTargetRef(targetId);
        
        BpmnTask source = new BpmnTask(sourceId, "");
        seq.setSourceNode(source);
        BpmnTask target = new BpmnTask(targetId, "");
        seq.setTargetNode(target);
        return seq;
    }
    
    private BpmnSequenceFlow getEndSequence(String endId) {
        BpmnSequenceFlow endSeq = new BpmnSequenceFlow();
        endSeq.setSourceRef(endId);
        endSeq.setTargetRef("end_id");
        
        BpmnTask source = new BpmnTask(endId, "");
        endSeq.setSourceNode(source);
        BpmnEndEvent end = new BpmnEndEvent("end_id","end");
        endSeq.setTargetNode(end);
        
        return endSeq;
    }
}

