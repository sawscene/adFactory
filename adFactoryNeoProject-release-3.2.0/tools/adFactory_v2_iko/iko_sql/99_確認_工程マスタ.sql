






--�@�H���}�X�^�@���e�m�F







--�@�f�[�^�m�F

select * from mst_work
where work_id = 417
;


-- �� JSON


select x.key,* from mst_work,json_to_recordset(work_check_info) as x(key text ,type text ,val text ,disp text , cat text, opt text , max text, min text, tag text, rules text, page text, cp text)
where
--work_id = '119'
x.ky = 'WORK'
--and x.ky = 'WORK'

order by work_id
;



-- �� JSONB


select x.key,* from mst_work,jsonb_to_recordset(work_check_info) as x(key text ,type text ,val text ,disp text , cat text, opt text , max text, min text, tag text, rules text, page text, cp text)
where
work_id in ('119', '2816')
--and x.ky = 'INSPECTION'
--and x.ky = 'INSPECTION'
--x.ky = 'WORK'

order by work_id
;

--�������@1

select x.key,* from mst_work,jsonb_to_recordset(work_check_info) as x(key text ,type text ,val text ,disp text , cat text, opt text , max text, min text, tag text, rules text, page text, cp text)
where
--work_id = '119'
 x.key = 'INSPECTION'
--and x.ky = 'INSPECTION'
--x.ky = 'WORK'
order by work_id
;

select * from mst_work
where
work_id = '2816'
;



select * from mst_work;


--�������@�Q
select *
from mst_work cross join jsonb_array_elements(work_check_info) 
where value->>'key' = 'INSPECTION'
and work_id = '119'
;



-- �������@�R
select *
from (
select jsonb_array_elements(work_check_info) as work_check_info_1 
from mst_work) as js
 where js.work_check_info_1->>'key' = 'WORK' 

;



EXPLAIN (FORMAT JSON) SELECT * FROM mst_work;




CREATE EXTENSION jsquery; 




