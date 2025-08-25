

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 03_3　工程セクションの移行

insert
into mst_work_section
select 

a_work_section_id
,a_fk_work_id
,a_document_title
,a_page_num
,a_file_name
,a_file_update_datetime
,a_work_section_order
,a_physical_file_name
,1


from dblink(
'DBLINK_adFactoryDB',
'select  
a.work_section_id
,a.fk_work_id
,a.document_title
,a.page_num
,a.file_name
,a.file_update_datetime
,a.work_section_order
,a.physical_file_name

from 
mst_work_section a'
) 
as aa( 
a_work_section_id int
,a_fk_work_id int
,a_document_title text
,a_page_num int
,a_file_name text
,a_file_update_datetime TIMESTAMP
,a_work_section_order int
,a_physical_file_name text

 )

;


-- 確認

--select * from mst_work_section
--order by work_section_id,work_id
--;

