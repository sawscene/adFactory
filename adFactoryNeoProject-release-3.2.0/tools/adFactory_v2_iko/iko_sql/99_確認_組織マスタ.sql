




--　データ確認    組織マスタ

select * from mst_organization

;


--delete from mst_workflow
;

-- ■ JSON


select x.key,* from mst_organization,json_to_recordset(organization_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%社員番%'
--and x.ky = 'WORK'

order by workflow_id
;






-- ■ JSONB


select x.key,* from mst_organization,jsonb_to_recordset(organization_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%社員番%'
--and x.ky = 'WORK'

order by organization_id
;



--検索方法1


select x.key,* from mst_organization,jsonb_to_recordset(organization_add_info) as x( key text ,type text ,val text ,disp text )
where
--work_id = '119'
x.key like '%社員番%'
--and x.ky = 'WORK'

order by organization_id
;




select * from mst_organization;


--検索方法２
select *
from mst_organization cross join jsonb_array_elements(organization_add_info) 
where value->>'key' like'%社員番%'
--and workflow_id = '119'
;



-- 検索方法３
select *
from (
select jsonb_array_elements(organization_add_info) as organization_add_info 
from mst_organization) as js
 where js.organization_add_info->>'key' like'%社員番%' 

;



