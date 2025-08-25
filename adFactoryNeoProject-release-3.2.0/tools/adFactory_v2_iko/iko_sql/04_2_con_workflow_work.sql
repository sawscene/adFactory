
--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 04_1　工程順工程関連付けの移行

-- 04_1_1 　工程順工程関連付けの移行　工程順工程関連付け
insert
into con_workflow_work
select 

0
,a_fk_workflow_id
,a_fk_work_id
,a_skip_flag
,a_workflow_order
,a_standard_start_time
,a_standard_end_time



from dblink(
'DBLINK_adFactoryDB',
'select  
a.association_id
,a.fk_workflow_id
,a.fk_work_id
,a.skip_flag
,a.workflow_order
,a.standard_start_time
,a.standard_end_time

from 
con_workflow_work a'
) 
as aa( 
a_association_id int
,a_fk_workflow_id int
,a_fk_work_id int
,a_skip_flag boolean
,a_workflow_order int
,a_standard_start_time timestamp
,a_standard_end_time timestamp


 )

;


-- 04_1_2  　工程順工程関連付けの移行　工程順バラ工程関連付け
insert
into con_workflow_work
select 

1
,a_fk_workflow_id
,a_fk_work_id
,a_skip_flag
,a_workflow_order
,a_standard_start_time
,a_standard_end_time


from dblink(
'DBLINK_adFactoryDB',
'select  
a.association_id
,a.fk_workflow_id
,a.fk_work_id
,a.skip_flag
,a.workflow_order
,a.standard_start_time
,a.standard_end_time

from 
con_workflow_separatework a'
) 
as aa( 
a_association_id int
,a_fk_workflow_id int
,a_fk_work_id int
,a_skip_flag boolean
,a_workflow_order int
,a_standard_start_time timestamp
,a_standard_end_time timestamp


 )

;




-- 確認

--select * from con_workflow_work
--order by work_kbn,workflow_id
--;

