


--　データ確認 設備マスタ

select * from mst_equipment
order by equipment_id
;


--delete from mst_equipment
;

-- ■ JSON


select x.key,* from mst_equipment,json_to_recordset(equipment_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%adFac%'
--and x.ky = 'WORK'

order by equipment_id
;






-- ■ JSONB


select x.key,* from mst_equipment,jsonb_to_recordset(equipment_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%adFac%'
--and x.ky = 'WORK'

order by equipment_id
;



--検索方法1


select x.key,* from mst_equipment,jsonb_to_recordset(equipment_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%adFac%'
--and x.ky = 'WORK'

order by equipment_id
;



select * from equipment_id;


--検索方法２
select *
from mst_equipment cross join jsonb_array_elements(equipment_add_info) 
where value->>'key' like '%adFac%'
--and workflow_id = '119'
;



-- 検索方法３
select *
from (
select jsonb_array_elements(equipment_add_info) as equipment_add_info1 
from mst_equipment) as js
 where js.equipment_add_info1->>'key' like '%adFac%' 

;



