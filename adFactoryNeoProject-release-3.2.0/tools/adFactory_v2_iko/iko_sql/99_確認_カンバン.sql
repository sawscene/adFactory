


--�@�f�[�^�m�F �@�J���o��

select * from trn_kanban
where kanban_id = 1261
;


--delete from mst_workflow
;

-- �� JSON


select x.key,* from trn_kanban,json_to_recordset(kanban_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by kanban_id
;






-- �� JSONB


select x.key,* from trn_kanban,jsonb_to_recordset(kanban_add_info) as x( key text ,type text ,val text ,disp text )
where
--kanban_id = 1261
x.key like '%�V%'
--and x.ky = 'WORK'

order by kanban_id
;



--�������@1


select x.key,* from trn_kanban,jsonb_to_recordset(kanban_add_info) as x( key text ,type text ,val text ,disp text )
where
kanban_id = 1261
--x.key like '%*%'
--and x.ky = 'WORK'

order by kanban_id
;



select * from mst_work;


--�������@�Q
select *
from trn_kanban cross join jsonb_array_elements(kanban_add_info) 
where value->>'key' like '%�V%'
--and workflow_id = '119'
;



-- �������@�R
select *
from (
select jsonb_array_elements(kanban_add_info) as kanban_add_info1 
from trn_kanban) as js
 where js.kanban_add_info1->>'key' like '%�V%' 

;



