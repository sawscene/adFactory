
--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 06_1　モノマスタの移行


insert
into mst_object
select 

a_object_id
,a_fk_object_type_id
,a_object_name
,a_remove_flag
,1


from dblink(
'DBLINK_adFactoryDB',
'select  
a.object_id
,a.fk_object_type_id
,a.object_name
,a.remove_flag

from 
mst_object a'
) 
as aa( 
a_object_id text
,a_fk_object_type_id bigint
,a_object_name text 
,a_remove_flag boolean


 )

;




-- 確認

