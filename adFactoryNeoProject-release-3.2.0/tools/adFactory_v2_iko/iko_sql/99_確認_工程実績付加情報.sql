

--�@�f�[�^�m�F �@�H�����ѕt�����

select * from trn_actual_adition
where actual_id = 1261
;


--delete from trn_work_kanban
;

-- �� JSON


select x.key,* from trn_actual_adition,json_to_recordset(actual_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by actual_id
;






-- �� JSONB


select x.key,* from trn_actual_adition,jsonb_to_recordset(actual_add_info) as x( key text ,type text ,val text ,disp text )
where
--kanban_id = 1261
x.key like '%�s��%'
--and x.ky = 'WORK'

order by actual_id
;



--�������@1


select x.key,* from trn_actual_adition,jsonb_to_recordset(actual_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by actual_id
;



select * from mst_work;


--�������@�Q
select *
from trn_actual_adition cross join jsonb_array_elements(actual_add_info) 
where value->>'key' like '%�V%'
--and workflow_id = '119'
;



-- �������@�R
select *
from (
select jsonb_array_elements(actual_add_info) as actual_add_info1 
from trn_actual_adition) as js
 where js.actual_add_info1->>'key' like '%�V%' 

;



