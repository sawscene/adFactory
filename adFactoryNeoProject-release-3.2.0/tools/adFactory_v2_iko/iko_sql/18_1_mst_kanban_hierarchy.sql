

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������



-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- �菇1:�@�J���o���K�w�}�X�^���ڍs


insert
into mst_kanban_hierarchy

select

a_kanban_hierarchy_id
,a_hierarchy_name
,a_partition_flag




from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.kanban_hierarchy_id
,a.hierarchy_name
,a.partition_flag



from mst_kanban_hierarchy a

'
)
as LINK1
(
a_kanban_hierarchy_id int
,a_hierarchy_name text
,a_partition_flag boolean



)
;


--�@�m�F

--select * from mst_kanban_hierarchy
--;


