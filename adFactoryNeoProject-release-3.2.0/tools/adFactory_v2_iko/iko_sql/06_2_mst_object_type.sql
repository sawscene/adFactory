
--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 06_1　モノ種別マスタの移行


insert
into mst_object_type
select 

a_object_type_id
,a_object_type_name
,1


from dblink(
'DBLINK_adFactoryDB',
'select  
a.object_type_id
,a.object_type_name


from 
mst_object_type a'
) 
as aa( 
a_object_type_id bigint
,a_object_type_name text


 )

;




-- 確認

--select * from mst_object_type
--order by object_type_id desc
--;

