



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



--�y DBLINK VIEW�쐬�z
--  �� �ǉ����

--drop View VW_DBLIK_adFactoryDB_workflow_add_info;

create or replace View VW_DBLIK_adFactoryDB_workflow_add_info as
select * from dblink
('DBLINK_adFactoryDB',
'select 
fk_master_id
,property_name as key
, property_type as type
, property_initial_value as val
, property_order as odr 
from mst_kanban_property_template'
)
as 
json_rec(
fk_master_id int
,key text
,type text
,val text
,disp text)
;


