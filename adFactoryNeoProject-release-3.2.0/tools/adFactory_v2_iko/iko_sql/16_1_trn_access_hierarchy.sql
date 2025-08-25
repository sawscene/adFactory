--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★

-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1

-- データベース　接続開始
select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');

-- 手順1:　階層アクセス権を移行

-- 16_1　階層アクセス権　組織 0
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

-- 16_1　階層アクセス権　設備 1
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

-- 16_1　階層アクセス権　工程 2
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

-- 16_1　階層アクセス権　工程順 3
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

-- 16_1　階層アクセス権　カンバン 4
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

--　確認

--select * from trn_access_hierarchy
--;
