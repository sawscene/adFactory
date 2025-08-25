

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- カンバン
-- 手順1:　カンバンの移行

insert 
into trn_kanban

select
a_kanban_id
,a_kanban_name
,a_kanban_subname
,a_fk_workflow_id
,a_start_datetime
,a_comp_datetime
,a_fk_update_person_id
,a_update_datetime
,a_kanban_status
,a_fk_interrupt_reason_id
,( select reason_id_new from iko_reason where reason_type = 2 and reason_id_old = a_fk_delay_reason_id )
,a_lot_quantity
,a_actual_start_datetime
,a_actual_comp_datetime
,a_model_name
,a_repair_num
,a_production_type

---  ■　追加情報
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp 
    from
		  (select kanban_property_name as key
			,kanban_property_type as type
			,kanban_property_value as val
			,kanban_property_order as disp
      	  from iko_trn_kanban_property
    	  where
      		fk_kanban_id = a_kanban_id
          order by disp
	  	)ZZ
  ) JSON_REC
)
 ---  ■　サービス情報　★　仮の設定
,null
,1


from dblink
(
'DBLINK_adFactoryDB',
'select
a.kanban_id
,a.kanban_name
,a.kanban_subname
,a.fk_workflow_id
,a.start_datetime
,a.comp_datetime
,a.fk_update_person_id
,a.update_datetime
,a.kanban_status
,a.fk_interrupt_reason_id
,a.fk_delay_reason_id
,a.lot_quantity
,a.actual_start_datetime
,a.actual_comp_datetime
,a.model_name
,a.repair_num
,a.production_type
from 
trn_kanban a 
'
) 
as LINK1
(
a_kanban_id int
,a_kanban_name text
,a_kanban_subname text
,a_fk_workflow_id int
,a_start_datetime timestamp
,a_comp_datetime timestamp
,a_fk_update_person_id int
,a_update_datetime timestamp
,a_kanban_status text
,a_fk_interrupt_reason_id int
,a_fk_delay_reason_id int
,a_lot_quantity int
,a_actual_start_datetime timestamp
,a_actual_comp_datetime timestamp
,a_model_name text
,a_repair_num int
,a_production_type int

)

;

