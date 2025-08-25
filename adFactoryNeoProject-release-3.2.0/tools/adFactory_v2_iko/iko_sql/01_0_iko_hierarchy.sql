
--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������

-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');

-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �ڍs�p�K�wID�V���Ή��\�ւ̈ڍs

-- �菇1:�@�H���̈ڍs

insert 
into iko_hierarchy
select
 0
,a_work_hierarchy_id
,a_work_hierarchy_id
,b_parent_id
,a_hierarchy_name


from dblink('DBLINK_adFactoryDB',
'select  a.work_hierarchy_id,a.hierarchy_name,b.parent_id 
from mst_work_hierarchy a LEFT JOIN tre_work_hierarchy b on  a.work_hierarchy_id = b.child_id order by a.work_hierarchy_id') 
as aa( a_work_hierarchy_id int ,a_hierarchy_name text ,b_parent_id int );


-- �eID��0�����{DB�ɑ��݂��Ȃ��ׁA0���쐬����B * �K�wID��0�̃��R�[�h�͈ڍs���Ȃ��i�eID=0�̐ݒ�ɕK�v�Ȃ��߁A�ڍs�p�ɋ����I�ɐݒ肵�Ă��邽�߁j

insert
into iko_hierarchy
VALUES (
 0
,0
,0
,0
,'iko only'
);



-- �菇1-1.�@�V�[�P���X�̍쐬  ���@�O������

-- drop SEQUENCE mst_hierarchy_hierarchy_id_seq CASCADE;

--CREATE SEQUENCE mst_hierarchy_hierarchy_id_seq INCREMENT BY 1;

-- �菇1-2.�@�V�[�P���X�̏����l�ݒ�

select setval('mst_hierarchy_hierarchy_id_seq',(select max(hierarchy_id_new) from iko_hierarchy))
;




-- �菇2:�@�H�����̈ڍs

insert 
into iko_hierarchy
select
 1
,( nextval('mst_hierarchy_hierarchy_id_seq'))
,a_workflow_hierarchy_id
,b_parent_id
,a_hierarchy_name



from dblink(
'DBLINK_adFactoryDB',
'select  a.workflow_hierarchy_id,a.hierarchy_name,b.parent_id 
from mst_workflow_hierarchy a LEFT JOIN tre_workflow_hierarchy b on a.workflow_hierarchy_id = b.child_id order by a.workflow_hierarchy_id') 
as t1( a_workflow_hierarchy_id int ,a_hierarchy_name text ,b_parent_id int )
;


-- �eID��0�����{DB�ɑ��݂��Ȃ��ׁA0���쐬����B  * �K�wID��0�̃��R�[�h�͈ڍs���Ȃ��i�eID=0�̐ݒ�ɕK�v�Ȃ��߁A�ڍs�p�ɋ����I�ɐݒ肵�Ă��邽�߁j

insert
into iko_hierarchy
VALUES (
 1
,0
,0
,0
,'iko only'
);




--truncate table iko_hierarchy;


