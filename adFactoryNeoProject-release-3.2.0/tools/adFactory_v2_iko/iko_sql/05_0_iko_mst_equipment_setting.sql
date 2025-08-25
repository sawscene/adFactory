



--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �ݔ��}�X�^�֘A
-- �菇1:�@�ڍs�p�ݔ��}�X�^�ݒ荀�� �ݔ��}�X�^�ݒ荀�ڂ��������ɂ���


insert
into iko_mst_equipment_setting

select
a_fk_master_id
,a_IPv4_address
,((case when a_work_progress_flag='false' then 0  when a_work_progress_flag='true' then 1 else 0 end)::int)
,a_plugin_name 

from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.fk_master_id
,max(case a.equipment_setting_type when ''TYPE_IP4_ADDRESS'' then a.equipment_setting_value else null end) as IPv4_address
,max(case a.equipment_setting_type when ''TYPE_BOOLEAN'' then a.equipment_setting_value else null end) as work_progress_flag
,max(case a.equipment_setting_type when ''TYPE_PLUGIN'' then a.equipment_setting_value else null end) as plugin_name

from mst_equipment_setting a
group by a.fk_master_id
order by a.fk_master_id
'
)
as LINK1
(
a_fk_master_id int
,a_IPv4_address text
,a_work_progress_flag text
,a_plugin_name text 

)
;





--�@�m�F

--select * from iko_mst_equipment_setting

--;




