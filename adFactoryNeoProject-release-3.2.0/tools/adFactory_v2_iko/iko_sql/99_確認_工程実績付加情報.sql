

--　データ確認 　工程実績付加情報

select * from trn_actual_adition
where actual_id = 1261
;


--delete from trn_work_kanban
;

-- ■ JSON


select x.key,* from trn_actual_adition,json_to_recordset(actual_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by actual_id
;






-- ■ JSONB


select x.key,* from trn_actual_adition,jsonb_to_recordset(actual_add_info) as x( key text ,type text ,val text ,disp text )
where
--kanban_id = 1261
x.key like '%不具%'
--and x.ky = 'WORK'

order by actual_id
;



--検索方法1


select x.key,* from trn_actual_adition,jsonb_to_recordset(actual_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by actual_id
;



select * from mst_work;


--検索方法２
select *
from trn_actual_adition cross join jsonb_array_elements(actual_add_info) 
where value->>'key' like '%シ%'
--and workflow_id = '119'
;



-- 検索方法３
select *
from (
select jsonb_array_elements(actual_add_info) as actual_add_info1 
from trn_actual_adition) as js
 where js.actual_add_info1->>'key' like '%シ%' 

;



