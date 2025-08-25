

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLIMK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 役割権限
-- 手順1:　役割権限マスタを移行


insert
into mst_role_authority

select
a_role_id
,a_role_name
,(select authority_actual_del from iko_mst_role_authority where role_id = a_role_id )
,(select authority_resource_edit from iko_mst_role_authority where role_id = a_role_id )
,(select authority_kanban_create from iko_mst_role_authority where role_id = a_role_id )
,(select authority_line_manage from iko_mst_role_authority where role_id = a_role_id )
,(select authority_actual_output from iko_mst_role_authority where role_id = a_role_id )
,(select authority_kanban_reference from iko_mst_role_authority where role_id = a_role_id )
,(select authority_resource_reference from iko_mst_role_authority where role_id = a_role_id )
,(select authority_access_edit from iko_mst_role_authority where role_id = a_role_id )
,1


from dblink 
(
'DBLIMK_adFactoryDB',
'select 
a.role_id
,a.role_name

from mst_role a

'
)
as LINK1
(
a_role_id int
,a_role_name text


)
;


--　確認

--select * from mst_role_authority
--;

