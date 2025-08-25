

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLIMK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=postgres');



-- 手順1:　棚卸実績参照用テンポラリを移行


insert
into tmp_warehouse_inventory_actual

select

a_equipment_id
,a_actual_id
,a_equipment_name
,a_equipment_identify
,a_fk_equipment_type_id
,a_fk_update_person_id
,a_update_datetime
,a_remove_flag
,a_storage_name
,a_inventory
,a_reserve_inventory
,a_in_process_inventory
,a_inventory_stock
,a_inventory_temp
,a_management
,a_product
,a_standard
,a_material
,a_manufacturer
,a_fk_workflow_id
,a_implement_datetime
,a_fk_organization_id
,a_actual_status
,a_organization_identify
,a_organization_name
,a_actual_storage_name
,a_actual_inventory_stock
,a_actual_difference
,a_actual_affiliation_name
,a_actual_affiliation_code
,a_actual_stktake_label_no



from dblink 
(
'DBLIMK_adFactoryDB',
'select 

a.equipment_id
,a.actual_id
,a.equipment_name
,a.equipment_identify
,a.fk_equipment_type_id
,a.fk_update_person_id
,a.update_datetime
,a.remove_flag
,a.storage_name
,a.inventory
,a.reserve_inventory
,a.in_process_inventory
,a.inventory_stock
,a.inventory_temp
,a.management
,a.product
,a.standard
,a.material
,a.manufacturer
,a.fk_workflow_id
,a.implement_datetime
,a.fk_organization_id
,a.actual_status
,a.organization_identify
,a.organization_name
,a.actual_storage_name
,a.actual_inventory_stock
,a.actual_difference
,a.actual_affiliation_name
,a.actual_affiliation_code
,a.actual_stktake_label_no


from tmp_warehouse_inventory_actual a

'
)
as LINK1
(
a_equipment_id int
,a_actual_id int
,a_equipment_name text
,a_equipment_identify text
,a_fk_equipment_type_id int
,a_fk_update_person_id int
,a_update_datetime timestamp
,a_remove_flag boolean
,a_storage_name text
,a_inventory text
,a_reserve_inventory text
,a_in_process_inventory text
,a_inventory_stock text
,a_inventory_temp text
,a_management text
,a_product text
,a_standard text
,a_material text
,a_manufacturer text
,a_fk_workflow_id int 
,a_implement_datetime timestamp
,a_fk_organization_id int 
,a_actual_status text
,a_organization_identify text
,a_organization_name text
,a_actual_storage_name text
,a_actual_inventory_stock text
,a_actual_difference text
,a_actual_affiliation_name text
,a_actual_affiliation_code text
,a_actual_stktake_label_no text


)
;


--　確認

--select * from tmp_warehouse_inventory_actual
--;


