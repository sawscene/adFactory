

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 設備マスタ
-- 手順1:　設備マスタの移行

insert 
into mst_equipment

select
a_equipment_id
,a_equipment_name
,a_equipment_identify
,a_fk_equipment_type_id
,a_fk_update_person_id
,a_update_datetime
,a_remove_flag
,a_cal_flag
,a_cal_next_date
,a_cal_term
,a_cal_term_unit
,a_cal_warning_days
,a_cal_last_date
,a_cal_person_id
,b_parent_id            --親設備ID
,(select IPv4_address from iko_mst_equipment_setting where equipment_id =  a_equipment_id )          --IPv4アドレス
,(select work_progress_flag::boolean from iko_mst_equipment_setting where equipment_id = a_equipment_id ) as work_progress_flag_bool   --工程進捗フラグ
,(select plugin_name from iko_mst_equipment_setting where equipment_id =  a_equipment_id )         --プラグイン名




---  ■　追加情報
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp 
    from
      VW_DBLIK_adFactoryDB_equipment_add_info 
    where
      fk_master_id = a_equipment_id
    order by disp
  ) JSON_REC
)
 ---  ■　サービス情報　★　仮の設定
,null
,1


from dblink
(
'DBLINK_adFactoryDB',
'select

a.equipment_id
,a.equipment_name
,a.equipment_identify
,a.fk_equipment_type_id
,a.fk_update_person_id
,a.update_datetime
,a.remove_flag
,a.cal_flag
,a.cal_next_date
,a.cal_term
,a.cal_term_unit
,a.cal_warning_days
,a.cal_last_date
,a.cal_person_id
,b.parent_id
,b.child_id



from 
mst_equipment a LEFT JOIN tre_equipment_hierarchy b on a.equipment_id = b.child_id



'
) 
as LINK1
(

a_equipment_id int
,a_equipment_name text
,a_equipment_identify text
,a_fk_equipment_type_id int
,a_fk_update_person_id int
,a_update_datetime TIMESTAMP
,a_remove_flag boolean
,a_cal_flag boolean 
,a_cal_next_date TIMESTAMP
,a_cal_term int
,a_cal_term_unit text 
,a_cal_warning_days int
,a_cal_last_date TIMESTAMP
,a_cal_person_id int
,b_parent_id int
,b_child_id int


)

;

--- 設備マスタ　工程進捗フラグのNULLをfalseに更新

UPDATE mst_equipment
set
work_progress_flag= false
where
work_progress_flag is null
;



--- 設備マスタ　論理削除フラグがOFFで親設備IDのNULLのデータを0に更新　*論理削除フラグがONのデータはNULLのままとする

UPDATE mst_equipment
set
parent_equipment_id=0

where
parent_equipment_id is null
and remove_flag = false
;






