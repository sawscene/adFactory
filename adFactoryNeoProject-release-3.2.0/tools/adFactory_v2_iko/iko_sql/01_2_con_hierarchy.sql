

--�@��������������������������������

--�@���@�@�@�ڍs��DB�Ŏ��{�@

--�@��������������������������������




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 01_2�@�K�w�֘A�t�� �H��

insert
into con_hierarchy
select 
a.hierarchy_id_new
,z.b_fk_work_id
,a.hierarchy_type


from 
   iko_hierarchy a , 
   (select b_fk_work_hierarchy_id,b_fk_work_id from dblink('DBLINK_adFactoryDB','select fk_work_hierarchy_id,fk_work_id from con_work_hierarchy') 
   as b(b_fk_work_hierarchy_id int ,b_fk_work_id int)
   ) z
where
 a.hierarchy_type = 0
 and a.hierarchy_id_old = z.b_fk_work_hierarchy_id

;



-- 01_2�@�K�w�֘A�t���@�H����


insert
into con_hierarchy
select 
a.hierarchy_id_new
,z.b_fk_workflow_id
,a.hierarchy_type


from 
   iko_hierarchy a , 
   (select b_fk_workflow_hierarchy_id,b_fk_workflow_id from dblink('DBLINK_adFactoryDB','select fk_workflow_hierarchy_id,fk_workflow_id from con_workflow_hierarchy') 
   as b(b_fk_workflow_hierarchy_id int ,b_fk_workflow_id int)
   ) z
where
 a.hierarchy_type = 1
 and a.hierarchy_id_old = z.b_fk_workflow_hierarchy_id

;

