

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������


-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 01_0�@�ڍs�p�K�wID�V���Ή��\

-- 01_1�@�K�w�}�X�^ �H�� * �K�wID��0�̃��R�[�h�͈ڍs���Ȃ��i�eID=0�̐ݒ�ɕK�v�Ȃ��߁A�ڍs�p�ɋ����I�ɐݒ肵�Ă��邽�߁j

insert
into mst_hierarchy
select 
a.hierarchy_id_new
,0
,a.hierarchy_name
-- �e�K�wID
,(select c.hierarchy_id_new from iko_hierarchy c
where
a.parent_id_old = c.hierarchy_id_old and c.hierarchy_type = 0)
,1

from iko_hierarchy a
where
 a.hierarchy_type = 0
 and a.hierarchy_id_new <> 0
 ;

-- 01_1�@�K�w�}�X�^ �H�����@ * �K�wID��0�̃��R�[�h�͈ڍs���Ȃ��i�eID=0�̐ݒ�ɕK�v�Ȃ��߁A�ڍs�p�ɋ����I�ɐݒ肵�Ă��邽�߁j

insert
into mst_hierarchy
select 
a.hierarchy_id_new
,1
,a.hierarchy_name
-- �e�K�wID
,( 
select c.hierarchy_id_new from iko_hierarchy c
where
a.parent_id_old = c.hierarchy_id_old and c.hierarchy_type = 1
)
,1

from iko_hierarchy a
where
 a.hierarchy_type = 1
  and a.hierarchy_id_new <> 0;



--�@�eID��NULL��0�ɍX�V
update mst_hierarchy
set
parent_hierarchy_id = 0
where
parent_hierarchy_id is null;








