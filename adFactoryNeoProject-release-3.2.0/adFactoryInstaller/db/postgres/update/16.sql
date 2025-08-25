-- リソース
create table t_resource (
  resource_id bigserial not null
  , resource_type character varying(32) not null
  , resource_key character varying(256)
  , resource_string text
  , resource_bin bytea
  , constraint t_resource_pk primary key (resource_id)
) ;

comment on table t_resource is 'リソース';
comment on column t_resource.resource_id is 'リソースID';
comment on column t_resource.resource_type is 'リソースタイプ:LOCALE, IMAGE';
comment on column t_resource.resource_key is 'リソースキー';
comment on column t_resource.resource_string is 'テキスト';
comment on column t_resource.resource_bin is 'バイナリ';


-- 組織
-- ALTER TABLE mst_organization DROP COLUMN language_type;
ALTER TABLE mst_organization ADD lang_ids jsonb;
comment on column mst_organization.lang_ids is '言語';

-- 設備
ALTER TABLE mst_equipment ADD lang_ids jsonb;
comment on column mst_equipment.lang_ids is '言語';

-- 作業者操作実績
create table trn_operation (
  operation_id bigserial not null
  , operate_datetime timestamp without time zone not null
  , equipment_id bigint
  , organization_id bigint
  , operate_app character varying(256)
  , operation_type character varying(64) not null
  , add_info jsonb
  , constraint trn_operation_pk primary key (operation_id)
) ;

comment on table trn_operation is '作業者操作実績';
comment on column trn_operation.operation_id is '操作ID';
comment on column trn_operation.operate_datetime is '操作時間';
comment on column trn_operation.equipment_id is '設備ID';
comment on column trn_operation.organization_id is '組織ID';
comment on column trn_operation.operate_app is '操作アプリ';
comment on column trn_operation.operation_type is '操作タイプ';
comment on column trn_operation.add_info is '追加情報';

