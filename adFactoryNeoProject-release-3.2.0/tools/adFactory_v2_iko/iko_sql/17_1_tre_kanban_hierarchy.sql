

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@�J���o���K�w���ڍs


insert
into tre_kanban_hierarchy

select

a_parent_id
,a_child_id




from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.parent_id
,a.child_id



from tre_kanban_hierarchy a

'
)
as LINK1
(
a_parent_id int
,a_child_id int




)
;


--�@�m�F

--select * from tre_kanban_hierarchy
--;


