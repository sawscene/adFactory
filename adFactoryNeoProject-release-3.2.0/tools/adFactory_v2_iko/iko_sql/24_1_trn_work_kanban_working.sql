

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@�H���J���o����ƒ����X�g���ڍs


insert
into trn_work_kanban_working

select

a_work_kanban_working_id
,a_fk_work_kanban_id
,a_fk_equipment_id
,a_fk_organization_id




from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.work_kanban_working_id
,a.fk_work_kanban_id
,a.fk_equipment_id
,a.fk_organization_id


from trn_work_kanban_working a

'
)
as LINK1
(
a_work_kanban_working_id int
,a_fk_work_kanban_id int
,a_fk_equipment_id int
,a_fk_organization_id int



)
;


--�@�m�F

--select * from trn_work_kanban_working
--;


