
--　データ確認　工程カンバン

select * from trn_work_kanban
where kanban_id = 1261
;


--delete from trn_work_kanban
;

-- ■ JSON


select x.key,* from trn_work_kanban,json_to_recordset(kanban_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by kanban_id
;






-- ■ JSONB


select x.key,* from trn_work_kanban,jsonb_to_recordset(work_kanban_add_info) as x( key text ,type text ,val text ,disp text )
where
--kanban_id = 1261
x.key like '%シ%'
--and x.ky = 'WORK'

order by kanban_id
;



--検索方法1


select x.key,* from trn_work_kanban,jsonb_to_recordset(work_kanban_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by kanban_id
;



select * from mst_work;


--検索方法２
select *
from trn_work_kanban cross join jsonb_array_elements(kanban_add_info) 
where value->>'key' like '%シ%'
--and workflow_id = '119'
;



-- 検索方法３
select *
from (
select jsonb_array_elements(kanban_add_info) as kanban_add_info1 
from trn_work_kanban) as js
 where js.kanban_add_info1->>'key' like '%シ%' 

;



