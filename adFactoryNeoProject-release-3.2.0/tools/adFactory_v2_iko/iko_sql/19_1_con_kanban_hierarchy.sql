

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- �菇1:�@�J���o���K�w�֘A�t�����ڍs


insert
into con_kanban_hierarchy

select

a_fk_kanban_hierarchy_id
,a_fk_kanban_id



from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.fk_kanban_hierarchy_id
,a.fk_kanban_id


from con_kanban_hierarchy a

'
)
as LINK1
(
a_fk_kanban_hierarchy_id int
,a_fk_kanban_id int


)
;


--�@�m�F

--select * from con_kanban_hierarchy
--;


