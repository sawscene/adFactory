-- 階層アクセス権
create table tre_access_hierarchy_fuji (
  type_id smallint not null
  , fk_hierarchy_id bigint not null
  , fk_organization_id bigint not null
  , constraint tm_access_hierarchy_fuji_PKC primary key (type_id,fk_hierarchy_id,fk_organization_id)
) ;

comment on table tre_access_hierarchy_fuji is '階層アクセス権:各階層にアクセス権を許可する組織グループ（組織ID）を管理する。';
comment on column tre_access_hierarchy_fuji.type_id is '階層種別ID:階層種別ID
　０：ユニット階層
　１：ユニットテンプレート階層';
comment on column tre_access_hierarchy_fuji.fk_hierarchy_id is 'ID:各階層ID';
comment on column tre_access_hierarchy_fuji.fk_organization_id is '組織グループ:アクセス権を許可する組織ID';

-- t_verを更新
UPDATE t_ver SET verno='4' WHERE sid = 1;
INSERT INTO t_ver (sid, verno)
SELECT 1, '4'
WHERE NOT EXISTS (SELECT sid FROM t_ver WHERE sid = 1);
