
--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- カンバン
-- 手順1:　工程カンバンの移行


insert 
into trn_work_kanban
select
a_work_kanban_id
,a_fk_kanban_id
,a_fk_workflow_id
,a_fk_work_id
,a_separate_work_flag
,a_implement_flag
,a_skip_flag
,a_start_datetime
,a_comp_datetime
,a_takt_time
,a_sum_times
,a_fk_update_person_id
,a_update_datetime
,a_work_status
,a_fk_interrupt_reason_id
,( select reason_id_new from iko_reason where reason_type = 2 and reason_id_old = a_fk_delay_reason_id )
,a_work_kanban_order
,a_serial_number
,a_sync_work
,a_actual_start_datetime
,a_actual_comp_datetime
,a_actual_num1
,a_actual_num2
,a_actual_num3
,a_rework_num



---  ■　追加情報
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp
    from
	    (select 
				fk_work_kanban_id
				,kanban_property_name as key
				,kanban_property_type as type
				,kanban_property_value as val
				,kanban_property_order as disp 
				
				from iko_trn_work_kanban_property

    		where
      		fk_work_kanban_id = LINK1.a_work_kanban_id
						
		) ZZ

  ) JSON_REC
)


 ---  ■　サービス情報　★　仮の設定
,null


from dblink
(
'DBLINK_adFactoryDB',
'select
a.work_kanban_id
,a.fk_kanban_id
,a.fk_workflow_id
,a.fk_work_id
,a.separate_work_flag
,a.implement_flag
,a.skip_flag
,a.start_datetime
,a.comp_datetime
,a.takt_time
,a.sum_times
,a.fk_update_person_id
,a.update_datetime
,a.work_status
,a.fk_interrupt_reason_id
,a.fk_delay_reason_id
,a.work_kanban_order
,a.serial_number
,a.sync_work
,a.actual_start_datetime
,a.actual_comp_datetime
,a.actual_num1
,a.actual_num2
,a.actual_num3
,a.rework_num

from 
trn_work_kanban a 
'
) 
as LINK1
(
a_work_kanban_id int
,a_fk_kanban_id int
,a_fk_workflow_id int
,a_fk_work_id int
,a_separate_work_flag boolean
,a_implement_flag boolean
,a_skip_flag boolean
,a_start_datetime timestamp
,a_comp_datetime timestamp
,a_takt_time int
,a_sum_times int
,a_fk_update_person_id int
,a_update_datetime timestamp
,a_work_status text
,a_fk_interrupt_reason_id int
,a_fk_delay_reason_id int
,a_work_kanban_order int
,a_serial_number int
,a_sync_work boolean
,a_actual_start_datetime timestamp
,a_actual_comp_datetime timestamp
,a_actual_num1 int
,a_actual_num2 int
,a_actual_num3 int
,a_rework_num int
)

;


