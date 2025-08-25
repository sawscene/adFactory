

--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- 既存で使用しているシーケンスの設定
-- データ移行後シーケンスを設定する

select setval('mst_authentication_info_authentication_id_seq',(select (case when max(authentication_id) > 0 then max(authentication_id) else 1 end) from mst_authentication_info));
select setval('mst_breaktime_breaktime_id_seq',(select (case when max(breaktime_id) > 0 then max(breaktime_id) else 1 end) from mst_breaktime));
select setval('mst_displayed_status_status_id_seq',(select (case when max(status_id) > 0 then max(status_id) else 1 end) from mst_displayed_status));
select setval('mst_equipment_equipment_id_seq',(select (case when max(equipment_id) > 0 then max(equipment_id) else 1 end) from mst_equipment));
select setval('mst_equipment_type_equipment_type_id_seq',(select (case when max(equipment_type_id) > 0 then max(equipment_type_id) else 1 end) from mst_equipment_type));
select setval('mst_holiday_holiday_id_seq',(select (case when max(holiday_id) > 0 then max(holiday_id) else 1 end) from mst_holiday));
select setval('mst_indirect_work_indirect_work_id_seq',(select (case when max(indirect_work_id) > 0 then max(indirect_work_id) else 1 end) from mst_indirect_work));
select setval('mst_kanban_hierarchy_kanban_hierarchy_id_seq',(select (case when max(kanban_hierarchy_id) > 0 then max(kanban_hierarchy_id) else 1 end) from mst_kanban_hierarchy));
select setval('mst_organization_organization_id_seq',(select (case when max(organization_id) > 0 then max(organization_id) else 1 end) from mst_organization));
select setval('mst_role_authority_role_id_seq',(select (case when max(role_id) > 0 then max(role_id) else 1 end) from mst_role_authority));
select setval('mst_schedule_schedule_id_seq',(select (case when max(schedule_id) > 0 then max(schedule_id) else 1 end) from mst_schedule));
select setval('mst_work_section_work_section_id_seq',(select (case when max(work_section_id) > 0 then max(work_section_id) else 1 end) from mst_work_section));
select setval('mst_work_work_id_seq',(select (case when max(work_id) > 0 then max(work_id) else 1 end) from mst_work));
select setval('mst_workflow_workflow_id_seq',(select (case when max(workflow_id) > 0 then max(workflow_id) else 1 end) from mst_workflow));
select setval('trn_actual_result_actual_id_seq',(select (case when max(actual_id) > 0 then max(actual_id) else 1 end) from trn_actual_result));
select setval('trn_indirect_actual_indirect_actual_id_seq',(select (case when max(indirect_actual_id) > 0 then max(indirect_actual_id) else 1 end) from trn_indirect_actual));
select setval('trn_kanban_kanban_id_seq',(select (case when max(kanban_id) > 0 then max(kanban_id) else 1 end) from trn_kanban));
select setval('trn_work_kanban_work_kanban_id_seq',(select (case when max(work_kanban_id) > 0 then max(work_kanban_id) else 1 end) from trn_work_kanban));
select setval('trn_work_kanban_working_work_kanban_working_id_seq',(select (case when max(work_kanban_working_id) > 0 then max(work_kanban_working_id) else 1 end) from trn_work_kanban_working));
select setval('mst_work_category_work_category_id_seq',(select (case when max(work_category_id) > 0 then max(work_category_id) else 1 end) from mst_work_category));






select setval('mst_object_type_object_type_id_seq',(select (case when max(object_type_id) > 0 then max(object_type_id) else 1 end) from mst_object_type));

