

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������


-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@�H���J���o���E�ݔ��֘A�t�����ڍs


insert
into con_workkanban_equipment

select

a_fk_workkanban_id
,a_fk_equipment_id





from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.fk_workkanban_id
,a.fk_equipment_id


from con_workkanban_equipment a

'
)
as LINK1
(
a_fk_workkanban_id int
,a_fk_equipment_id int


)
;


--�@�m�F

--select * from con_workkanban_equipment
--;


