




--�@�f�[�^�m�F    �g�D�}�X�^

select * from mst_organization

;


--delete from mst_workflow
;

-- �� JSON


select x.key,* from mst_organization,json_to_recordset(organization_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%�Ј���%'
--and x.ky = 'WORK'

order by workflow_id
;






-- �� JSONB


select x.key,* from mst_organization,jsonb_to_recordset(organization_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%�Ј���%'
--and x.ky = 'WORK'

order by organization_id
;



--�������@1


select x.key,* from mst_organization,jsonb_to_recordset(organization_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%�Ј���%'
--and x.ky = 'WORK'

order by organization_id
;




select * from mst_organization;


--�������@�Q
select *
from mst_organization cross join jsonb_array_elements(organization_add_info) 
where value->>'key' like'%�Ј���%'
--and workflow_id = '119'
;



-- �������@�R
select *
from (
select jsonb_array_elements(organization_add_info) as organization_add_info 
from mst_organization) as js
 where js.organization_add_info->>'key' like'%�Ј���%' 

;



