
--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 05_2�@�ݔ���ʃ}�X�^�̈ڍs

delete from mst_equipment_type;

insert
into mst_equipment_type
select 

a_equipment_type_id
,a_name
,1


from dblink(
'DBLINK_adFactoryDB',
'select  
a.equipment_type_id
,a.name


from 
mst_equipment_type a'
) 
as aa( 
a_equipment_type_id int
,a_name text

 )

;

select pg_catalog.setval('mst_equipment_type_equipment_type_id_seq', (select max(equipment_type_id) + 1 from mst_equipment_type), false);


-- �m�F

--select * from mst_equipment_type
--order by equipment_type_id desc
--;

