--�@��������������������������������
--�@���@�@�@�ڍs��DB�Ŏ��{�@
--�@��������������������������������

-- �G���[�����������ꍇ�ɏ����𒆒f�����܂��B
\set ON_ERROR_STOP 1

-- �f�[�^�x�[�X�@�ڑ��J�n
select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');

-- �菇1:�@�K�w�A�N�Z�X�����ڍs

-- 16_1�@�K�w�A�N�Z�X���@�g�D 0
insert
into trn_access_hierarchy 

select
  a.a_type_id,
  a.a_fk_hierarchy_id,
  a.a_fk_organization_id
from (
  select
    a_type_id,
    a_fk_hierarchy_id,
    a_fk_organization_id
  from dblink ('DBLINK_adFactoryDB','select a.type_id, a.fk_hierarchy_id, a.fk_organization_id from tm_access_hierarchy a where a.type_id = 0')
  as LINK1 (a_type_id int, a_fk_hierarchy_id int, a_fk_organization_id int)
) a
;

-- 16_1�@�K�w�A�N�Z�X���@�ݔ� 1
insert
into trn_access_hierarchy 

select
  a.a_type_id,
  a.a_fk_hierarchy_id,
  a.a_fk_organization_id
from (
  select
    a_type_id,
    a_fk_hierarchy_id,
    a_fk_organization_id
  from dblink ('DBLINK_adFactoryDB','select a.type_id, a.fk_hierarchy_id, a.fk_organization_id from tm_access_hierarchy a where a.type_id = 1')
  as LINK1 (a_type_id int, a_fk_hierarchy_id int, a_fk_organization_id int)
) a
;

-- 16_1�@�K�w�A�N�Z�X���@�H�� 2
insert
into trn_access_hierarchy

select
  a.a_type_id,
  h.hierarchy_id_new,
  a.a_fk_organization_id
from (
  select
    a_type_id,
    a_fk_hierarchy_id,
    a_fk_organization_id
  from dblink ('DBLINK_adFactoryDB','select a.type_id, a.fk_hierarchy_id, a.fk_organization_id from tm_access_hierarchy a where a.type_id = 2')
  as LINK1 (a_type_id int, a_fk_hierarchy_id int, a_fk_organization_id int)
) a
left join iko_hierarchy h on h.hierarchy_id_old = a.a_fk_hierarchy_id and h.hierarchy_type = 0
;

-- 16_1�@�K�w�A�N�Z�X���@�H���� 3
insert
into trn_access_hierarchy

select
  a.a_type_id,
  h.hierarchy_id_new,
  a.a_fk_organization_id
from (
  select
    a_type_id,
    a_fk_hierarchy_id,
    a_fk_organization_id
  from dblink ('DBLINK_adFactoryDB','select a.type_id, a.fk_hierarchy_id, a.fk_organization_id from tm_access_hierarchy a where a.type_id = 3')
  as LINK1 (a_type_id int, a_fk_hierarchy_id int, a_fk_organization_id int)
) a
left join iko_hierarchy h on h.hierarchy_id_old = a.a_fk_hierarchy_id and h.hierarchy_type = 1
;

-- 16_1�@�K�w�A�N�Z�X���@�J���o�� 4
insert
into trn_access_hierarchy

select
  a.a_type_id,
  a.a_fk_hierarchy_id,
  a.a_fk_organization_id
from (
  select
    a_type_id,
    a_fk_hierarchy_id,
    a_fk_organization_id
  from dblink ('DBLINK_adFactoryDB','select a.type_id, a.fk_hierarchy_id, a.fk_organization_id from tm_access_hierarchy a where a.type_id = 4')
  as LINK1 (a_type_id int, a_fk_hierarchy_id int, a_fk_organization_id int)
) a
;

--�@�m�F

--select * from trn_access_hierarchy
--;
