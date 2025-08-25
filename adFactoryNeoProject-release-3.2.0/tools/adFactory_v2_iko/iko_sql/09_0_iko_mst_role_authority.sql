

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 役割権限
-- 手順1:　移行用役割権限マスタを横持ちにする


insert
into iko_mst_role_authority

select
a_role_id
,a_authority_actual_del
,a_authority_resource_edit
,a_authority_kanban_create
,a_authority_line_manage
,a_authority_actual_output
,a_authority_kanban_reference
,a_authority_resource_reference
,a_authority_access_edit


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.fk_role_id
,max(case a.authority_type when ''DELETE_ACTUAL'' then a.authority_enable::int else 0 end) as authority_actual_del
,max(case a.authority_type when ''EDITED_RESOOURCE'' then a.authority_enable::int else 0 end) as authority_resource_edit
,max(case a.authority_type when ''MAKED_KANBAN'' then a.authority_enable::int else 0 end) as authority_kanban_create
,max(case a.authority_type when ''MANAGED_LINE'' then a.authority_enable::int else 0 end) as authority_line_manage
,max(case a.authority_type when ''OUTPUT_ACTUAL'' then a.authority_enable::int else 0 end) as authority_actual_output
,max(case a.authority_type when ''REFERENCE_KANBAN'' then a.authority_enable::int else 0 end) as authority_kanban_reference
,max(case a.authority_type when ''REFERENCE_RESOOURCE'' then a.authority_enable::int else 0 end) as authority_resource_reference
,max(case a.authority_type when ''RIGHT_ACCESS'' then a.authority_enable::int else 0 end) as authority_access_edit


from mst_role_authority a
group by a.fk_role_id
order by a.fk_role_id
'
)
as LINK1
(
a_role_id int
,a_authority_actual_del boolean
,a_authority_resource_edit boolean
,a_authority_kanban_create boolean
,a_authority_line_manage boolean
,a_authority_actual_output boolean
,a_authority_kanban_reference boolean
,a_authority_resource_reference boolean
,a_authority_access_edit boolean

)
;


--　確認

--select * from iko_mst_role_authority
--;




