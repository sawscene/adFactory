--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������

-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1

-- �f�[�^�x�[�X�@�ڑ��J�n
select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 04_3�@�H���E�g�D�֘A�t���̈ڍs

-- 04_3_1 �@�H�����H���֘A�t���̈ڍs�@�H���E�g�D�֘A�t��
insert into con_work_organization
select
  0,
  a_fk_workflow_id,
  a_fk_work_id,
  a_fk_organization_id

from dblink ('DBLINK_adFactoryDB', '
  select
    c.fk_workflow_id,
    c.fk_work_id,
    a.fk_organization_id
  from con_work_organization a
  join con_workflow_work c on c.association_id = a.fk_work_id
'
) as aa (
  a_fk_workflow_id int,
  a_fk_work_id int,
  a_fk_organization_id int
)
;


-- 04_3_2 �@�H�����H���֘A�t���̈ڍs�@�o���H���E�g�D�֘A�t��
insert into con_work_organization
select
  1,
  a_fk_workflow_id,
  a_fk_work_id,
  a_fk_organization_id

from dblink ('DBLINK_adFactoryDB','
  select
    c.fk_workflow_id,
    c.fk_work_id,
    a.fk_organization_id
  from con_separatework_organization a
  join con_workflow_separatework c on c.association_id = a.fk_work_id
'
) as aa (
  a_fk_workflow_id int,
  a_fk_work_id int,
  a_fk_organization_id int
)
;


-- �m�F

--select * from con_work_organization
--order by work_kbn desc
--;
