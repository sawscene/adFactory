


--�@�f�[�^�m�F   �H�����}�X�^

select * from mst_workflow
where workflow_id = 1
;


--delete from mst_workflow
;

-- �� JSON


select x.key,* from mst_workflow,json_to_recordset(workflow_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%*%'
--and x.ky = 'WORK'

order by workflow_id
;






-- �� JSONB


select x.key,* from mst_workflow,jsonb_to_recordset(workflow_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%*%'
--and x.ky = 'WORK'

order by workflow_id
;



--�������@1


select x.key,* from mst_workflow,jsonb_to_recordset(workflow_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%*%'
--and x.ky = 'WORK'

order by workflow_id
;



select * from mst_work;


--�������@�Q
select *
from mst_workflow cross join jsonb_array_elements(workflow_add_info) 
where value->>'key' like '%*%'
--and workflow_id = '119'
;



-- �������@�R
select *
from (
select jsonb_array_elements(workflow_add_info) as workflow_add_info_1 
from mst_workflow) as js
 where js.workflow_add_info_1->>'key' like '%*%' 

;



