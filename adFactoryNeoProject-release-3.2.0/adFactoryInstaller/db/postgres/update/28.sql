-- 「実績出力」ビューに製造番号カラムを追加
CREATE OR REPLACE VIEW view_report_out AS
SELECT act.actual_id,
       kanh.hierarchy_name         AS kanban_hierarchy_name,
       act.kanban_id               AS fk_kanban_id,
       kan.kanban_name,
       kan.kanban_subname,
       wfh.hierarchy_name          AS workflow_hierarchy_name,
       act.workflow_id             AS fk_workflow_id,
       wf.workflow_name,
       wf.workflow_rev,
       wkh.hierarchy_name          AS work_hierarchy_name,
       act.work_id                 AS fk_work_id,
       wk.work_name,
       act.work_kanban_id          AS fk_work_kanban_id,
       wkan.separate_work_flag,
       wkan.skip_flag,
       p_org.organization_name     AS parent_organization_name,
       p_org.organization_identify AS parent_organization_identify,
       act.organization_id         AS fk_organization_id,
       org.organization_name,
       org.organization_identify,
       p_eq.equipment_name         AS parent_equipment_name,
       p_eq.equipment_identify     AS parent_equipment_identify,
       act.equipment_id            AS fk_equipment_id,
       eq.equipment_name,
       eq.equipment_identify,
       act.actual_status,
       act.interrupt_reason,
       act.delay_reason,
       act.implement_datetime,
       wkan.takt_time,
       act.work_time,
       kan.model_name,
       act.comp_num,
       act.defect_reason,
       act.defect_num,
       act.actual_add_info,
       kan.production_number,
       act.serial_no,
       act.non_work_time
FROM trn_actual_result act
         LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
         LEFT JOIN con_kanban_hierarchy con_kanh ON con_kanh.kanban_id = kan.kanban_id
         LEFT JOIN mst_kanban_hierarchy kanh ON kanh.kanban_hierarchy_id = con_kanh.kanban_hierarchy_id
         LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = act.work_kanban_id
         LEFT JOIN mst_workflow wf ON wf.workflow_id = act.workflow_id
         LEFT JOIN con_hierarchy con_wfh ON con_wfh.hierarchy_type = 1 AND con_wfh.work_workflow_id = act.workflow_id
         LEFT JOIN mst_hierarchy wfh ON wfh.hierarchy_type = 1 AND wfh.hierarchy_id = con_wfh.hierarchy_id
         LEFT JOIN mst_work wk ON wk.work_id = act.work_id
         LEFT JOIN con_hierarchy con_wkh ON con_wkh.hierarchy_type = 0 AND con_wkh.work_workflow_id = act.workflow_id
         LEFT JOIN mst_hierarchy wkh ON wkh.hierarchy_type = 0 AND wkh.hierarchy_id = con_wkh.hierarchy_id
         LEFT JOIN mst_equipment eq ON eq.equipment_id = act.equipment_id
         LEFT JOIN mst_equipment p_eq ON p_eq.equipment_id = eq.parent_equipment_id
         LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
         LEFT JOIN mst_organization p_org ON p_org.organization_id = org.parent_organization_id;

comment on column view_report_out.non_work_time is '中断時間';
