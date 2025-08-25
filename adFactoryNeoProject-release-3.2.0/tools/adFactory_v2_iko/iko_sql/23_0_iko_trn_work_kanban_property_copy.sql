

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　工程カンバンプロパティのコピー


insert
into iko_trn_work_kanban_property

select

a_work_kanbann_property_id
,a_fk_work_kanban_id
,a_kanban_property_name
,a_kanban_property_type
,a_kanban_property_value
,a_kanban_property_order


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.work_kanbann_property_id
,a.fk_work_kanban_id
,a.kanban_property_name
,a.kanban_property_type
,a.kanban_property_value
,a.kanban_property_order


from trn_work_kanban_property a

'
)
as LINK1
(
a_work_kanbann_property_id int
,a_fk_work_kanban_id int
,a_kanban_property_name text
,a_kanban_property_type text
,a_kanban_property_value text
,a_kanban_property_order int


)
;



-- アナライズとりインデックス

--ANALYZE VERBOSE iko_trn_work_kanban_property;

--REINDEX table iko_trn_work_kanban_property;

VACUUM  FULL    ANALYZE     iko_trn_work_kanban_property   ;





--　確認

--select * from iko_trn_work_kanban_property;
--;

--delete from iko_trn_work_kanban_property;

