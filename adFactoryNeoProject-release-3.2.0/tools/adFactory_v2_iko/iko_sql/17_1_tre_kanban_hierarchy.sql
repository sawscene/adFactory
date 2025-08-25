

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　カンバン階層を移行


insert
into tre_kanban_hierarchy

select

a_parent_id
,a_child_id




from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.parent_id
,a.child_id



from tre_kanban_hierarchy a

'
)
as LINK1
(
a_parent_id int
,a_child_id int




)
;


--　確認

--select * from tre_kanban_hierarchy
--;


