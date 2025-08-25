

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　カンバン階層マスタを移行


insert
into mst_kanban_hierarchy

select

a_kanban_hierarchy_id
,a_hierarchy_name
,a_partition_flag




from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.kanban_hierarchy_id
,a.hierarchy_name
,a.partition_flag



from mst_kanban_hierarchy a

'
)
as LINK1
(
a_kanban_hierarchy_id int
,a_hierarchy_name text
,a_partition_flag boolean



)
;


--　確認

--select * from mst_kanban_hierarchy
--;


