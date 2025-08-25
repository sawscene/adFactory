

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　作業区分マスタを移行


insert
into mst_work_category

select

a_work_category_id
,a_work_category_name

,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.work_category_id
,a.work_category_name


from mst_work_category a

'
)
as LINK1
(
a_work_category_id int
,a_work_category_name text



)
;


--　確認

--select * from mst_work_category
--;


