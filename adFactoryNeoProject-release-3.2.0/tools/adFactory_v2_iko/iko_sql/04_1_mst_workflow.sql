

--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������




-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1


-- �f�[�^�x�[�X�@�ڑ��J�n

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- �H�����}�X�^
-- �菇1:�@�H�����}�X�^�̈ڍs

insert 
into mst_workflow
select
a_workflow_id
,a_workflow_name
,a_workflow_revision
,a_workflow_diaglam 
,a_fk_update_person_id
,a_update_datetime
,a_ledger_path
,a_remove_flag
,a_workflow_number
,a_workflow_rev
,a_model_name
,a_open_time
,a_close_time
,a_schedule_policy

---  ���@�ǉ����
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp 
    from
      VW_DBLIK_adFactoryDB_workflow_add_info 
    where
      fk_master_id = a_workflow_id
    order by disp
  ) JSON_REC
)
 ---  ���@�T�[�r�X���@���@���̐ݒ�
,null
,1


from dblink
(
'DBLINK_adFactoryDB',
'select
a.workflow_id
,a.workflow_name
,a.workflow_revision
,a.workflow_diaglam
,a.fk_update_person_id
,a.update_datetime
,a.ledger_path
,a.remove_flag
,a.workflow_number
,a.workflow_rev
,a.model_name
,a.open_time
,a.close_time
,a.schedule_policy

from 
mst_workflow a 
'
) 
as LINK1
(
a_workflow_id int
,a_workflow_name text
,a_workflow_revision text
,a_workflow_diaglam text
,a_fk_update_person_id int
,a_update_datetime TIMESTAMP
,a_ledger_path text
,a_remove_flag boolean
,a_workflow_number text
,a_workflow_rev int
,a_model_name text
,a_open_time time
,a_close_time time
,a_schedule_policy int

)

;

