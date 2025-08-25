

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★




-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 手順1:　間接作業マスタを移行


insert
into mst_indirect_work

select

a_indirect_work_id
,a_class_number
,a_work_number
,a_work_name
,a_fk_work_category_id

,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.indirect_work_id
,a.class_number
,a.work_number
,a.work_name
,a.fk_work_category_id

from mst_indirect_work a

'
)
as LINK1
(
a_indirect_work_id int
,a_class_number text
,a_work_number text
,a_work_name text 
,a_fk_work_category_id int


)
;


--　確認

--select * from mst_indirect_work
--;


