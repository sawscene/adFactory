

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 手順1:　カンバン階層関連付けを移行


insert
into con_kanban_hierarchy

select

a_fk_kanban_hierarchy_id
,a_fk_kanban_id



from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.fk_kanban_hierarchy_id
,a.fk_kanban_id


from con_kanban_hierarchy a

'
)
as LINK1
(
a_fk_kanban_hierarchy_id int
,a_fk_kanban_id int


)
;


--　確認

--select * from con_kanban_hierarchy
--;


