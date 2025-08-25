
--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1





-- バキュームとアナライズ


VACUUM  FULL    ANALYZE     mst_hierarchy   ;
VACUUM  FULL    ANALYZE     con_hierarchy   ;
VACUUM  FULL    ANALYZE     mst_reason  ;
VACUUM  FULL    ANALYZE     mst_work    ;
VACUUM  FULL    ANALYZE     mst_work_section    ;
VACUUM  FULL    ANALYZE     mst_workflow    ;
VACUUM  FULL    ANALYZE     con_workflow_work   ;
VACUUM  FULL    ANALYZE     con_work_equipment  ;
VACUUM  FULL    ANALYZE     con_work_organization   ;
VACUUM  FULL    ANALYZE     mst_equipment   ;
VACUUM  FULL    ANALYZE     mst_equipment_type  ;
VACUUM  FULL    ANALYZE     mst_object  ;
VACUUM  FULL    ANALYZE     mst_object_type ;
VACUUM  FULL    ANALYZE     mst_organization    ;
VACUUM  FULL    ANALYZE     con_organization_breaktime  ;
VACUUM  FULL    ANALYZE     con_organization_role   ;
VACUUM  FULL    ANALYZE     con_organization_work_category  ;
VACUUM  FULL    ANALYZE     mst_displayed_status    ;
VACUUM  FULL    ANALYZE     mst_role_authority  ;
VACUUM  FULL    ANALYZE     mst_holiday ;
VACUUM  FULL    ANALYZE     mst_schedule    ;
VACUUM  FULL    ANALYZE     mst_breaktime   ;
VACUUM  FULL    ANALYZE     mst_indirect_work   ;
VACUUM  FULL    ANALYZE     mst_authentication_info ;
VACUUM  FULL    ANALYZE     mst_work_category   ;
VACUUM  FULL    ANALYZE     trn_access_hierarchy    ;
VACUUM  FULL    ANALYZE     tre_kanban_hierarchy    ;
VACUUM  FULL    ANALYZE     mst_kanban_hierarchy    ;
VACUUM  FULL    ANALYZE     con_kanban_hierarchy    ;
VACUUM  FULL    ANALYZE     trn_kanban  ;
VACUUM  FULL    ANALYZE     con_workkanban_equipment    ;
VACUUM  FULL    ANALYZE     con_workkanban_organization ;
VACUUM  FULL    ANALYZE     trn_work_kanban ;
VACUUM  FULL    ANALYZE     trn_work_kanban_working ;
VACUUM  FULL    ANALYZE     trn_actual_result   ;
VACUUM  FULL    ANALYZE     trn_actual_adition  ;
VACUUM  FULL    ANALYZE     trn_indirect_actual ;
VACUUM  FULL    ANALYZE     tmp_warehouse_inventory_actual  ;


