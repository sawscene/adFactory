-- adFactoryDB から adFactoryDB2 への移行用テーブル作成

-- データベース操作ログ
drop table if exists database_operation_log;
create table database_operation_log (
  audit_log_id bigint not null
  , authentication_id bigint not null
  , target_table_name character varying(256) not null
  , method_name character varying(256) not null
  , insert_datetime timestamp without time zone not null
  , constraint database_operation_log_pk primary key (audit_log_id)
) ;

comment on table database_operation_log is 'データベース操作ログ:各データベースのアクセス情報を蓄積する';
comment on column database_operation_log.audit_log_id is '監査ログID';
comment on column database_operation_log.authentication_id is '認証id';
comment on column database_operation_log.target_table_name is '対象テーブル名';
comment on column database_operation_log.method_name is '処理メソッド名';
comment on column database_operation_log.insert_datetime is '追加日時';

-- 移行用階層ID新旧対応表
drop table if exists iko_hierarchy;
create table iko_hierarchy (
  hierarchy_type integer not null
  , hierarchy_id_new bigint not null
  , hierarchy_id_old bigint not null
  , parent_id_old bigint
  , hierarchy_name character varying(256)
  , constraint iko_hierarchy_pk primary key (hierarchy_type,hierarchy_id_new,hierarchy_id_old)
) ;

comment on table iko_hierarchy is '移行用階層ID新旧対応表:階層マスタと階層関連付けの移行用で、階層IDの新旧対応を記憶する';
comment on column iko_hierarchy.hierarchy_type is '階層種別';
comment on column iko_hierarchy.hierarchy_id_new is '新階層ID';
comment on column iko_hierarchy.hierarchy_id_old is '旧階層ID';
comment on column iko_hierarchy.parent_id_old is '旧親ID';
comment on column iko_hierarchy.hierarchy_name is '名前';

-- 移行用ライセンスID
drop table if exists iko_license;
create table iko_license (
  icense_id bigint
) ;

comment on table iko_license is '移行用ライセンスID';
comment on column iko_license.icense_id is 'ライセンスID';

-- 移行用設備マスタ設定項目
create table iko_mst_equipment_setting (
  equipment_id bigint
  , ipv4_address character varying(256)
  , work_progress_flag integer
  , plugin_name character varying(256)
) ;

comment on table iko_mst_equipment_setting is '移行用設備マスタ設定項目';
comment on column iko_mst_equipment_setting.equipment_id is '設備ID';
comment on column iko_mst_equipment_setting.ipv4_address is 'IPv4アドレス';
comment on column iko_mst_equipment_setting.work_progress_flag is '工程進捗フラグ';
comment on column iko_mst_equipment_setting.plugin_name is 'プラグイン名';

-- 移行用役割権限マスタ
drop table if exists iko_mst_role_authority;
create table iko_mst_role_authority (
  role_id bigint not null
  , authority_actual_del boolean
  , authority_resource_edit boolean
  , authority_kanban_create boolean
  , authority_line_manage boolean
  , authority_actual_output boolean
  , authority_kanban_reference boolean
  , authority_resource_reference boolean
  , authority_access_edit boolean
  , constraint iko_mst_role_authority_pk primary key (role_id)
) ;

comment on table iko_mst_role_authority is '移行用役割権限マスタ';
comment on column iko_mst_role_authority.role_id is '役割ID';
comment on column iko_mst_role_authority.authority_actual_del is '実績削除権限';
comment on column iko_mst_role_authority.authority_resource_edit is 'リソース編集権限';
comment on column iko_mst_role_authority.authority_kanban_create is 'カンバン作成権限';
comment on column iko_mst_role_authority.authority_line_manage is 'ライン管理権限';
comment on column iko_mst_role_authority.authority_actual_output is '実績出力権限';
comment on column iko_mst_role_authority.authority_kanban_reference is 'カンバン参照権限';
comment on column iko_mst_role_authority.authority_resource_reference is 'リソース参照権限';
comment on column iko_mst_role_authority.authority_access_edit is 'アクセス権編集権限';

-- 移行用工程マスタプロパティ
drop table if exists iko_mst_work_property;
create table iko_mst_work_property (
  work_prop_id bigserial not null
  , fk_master_id bigint not null
  , work_prop_name character varying(256) not null
  , work_prop_type character varying(128) not null
  , work_prop_value text
  , work_prop_order integer
  , work_prop_category character varying(128)
  , work_prop_option text
  , work_prop_lower_tolerance double precision
  , work_prop_upper_tolerance double precision
  , work_prop_tag character varying(128)
  , work_prop_validation_rule character varying(128)
  , work_section_order integer
  , work_prop_checkpoint integer
  , constraint iko_mst_work_property_pk primary key (work_prop_id)
) ;

alter table iko_mst_work_property add constraint mst_work_property_idx1
  unique (work_prop_id) ;

create index idx_work_property_fk_master_id
  on iko_mst_work_property(fk_master_id);

comment on table iko_mst_work_property is '移行用工程マスタプロパティ';
comment on column iko_mst_work_property.work_prop_id is 'ID';
comment on column iko_mst_work_property.fk_master_id is 'マスタID';
comment on column iko_mst_work_property.work_prop_name is 'プロパティ名';
comment on column iko_mst_work_property.work_prop_type is '型';
comment on column iko_mst_work_property.work_prop_value is '値';
comment on column iko_mst_work_property.work_prop_order is '表示順';
comment on column iko_mst_work_property.work_prop_category is 'プロパティ種別';
comment on column iko_mst_work_property.work_prop_option is '付加情報';
comment on column iko_mst_work_property.work_prop_lower_tolerance is '基準値下限';
comment on column iko_mst_work_property.work_prop_upper_tolerance is '基準値上限';
comment on column iko_mst_work_property.work_prop_tag is 'タグ';
comment on column iko_mst_work_property.work_prop_validation_rule is '入力規則';
comment on column iko_mst_work_property.work_section_order is '工程セクション表示順';
comment on column iko_mst_work_property.work_prop_checkpoint is '進捗チェックポイント';

-- 移行用理由ID新旧対応表
drop table if exists iko_reason;
create table iko_reason (
  reason_type bigint not null
  , reason_id_new bigint not null
  , reason_id_old bigint not null
  , reason character varying(256)
  , font_color character varying(128)
  , back_color character varying(128)
  , light_pattern character varying(128)
  , reason_order bigserial
  , constraint iko_reason_pk primary key (reason_type,reason_id_new,reason_id_old)
) ;

comment on table iko_reason is '移行用理由ID新旧対応表:階層マスタと階層関連付けの移行用で、階層IDの新旧対応を記憶する';
comment on column iko_reason.reason_type is '種別';
comment on column iko_reason.reason_id_new is '新理由ID';
comment on column iko_reason.reason_id_old is '旧理由ID';
comment on column iko_reason.reason is '理由';
comment on column iko_reason.font_color is '文字色';
comment on column iko_reason.back_color is '背景色';
comment on column iko_reason.light_pattern is '点灯パターン';
comment on column iko_reason.reason_order is '理由オーダー';

-- 移行用実績プロパティ
drop table if exists iko_trn_actual_property;
create table iko_trn_actual_property (
  actual_prop_id bigserial not null
  , fk_actual_id bigserial not null
  , actual_prop_name character varying(256) not null
  , actual_prop_type character varying(128) not null
  , actual_prop_value text
  , actual_prop_order integer
  , constraint iko_trn_actual_property_pk primary key (actual_prop_id)
) ;

create index idx_actual_property_fk_actual_id
  on iko_trn_actual_property(fk_actual_id);

create index idx_actual_property_name
  on iko_trn_actual_property(actual_prop_name);

comment on table iko_trn_actual_property is '移行用実績プロパティ:廃止　　*追加情報として親に持つ品質トレーサビリティの情報';
comment on column iko_trn_actual_property.actual_prop_id is '実績プロパティID';
comment on column iko_trn_actual_property.fk_actual_id is '実績ID';
comment on column iko_trn_actual_property.actual_prop_name is 'プロパティ名';
comment on column iko_trn_actual_property.actual_prop_type is '型';
comment on column iko_trn_actual_property.actual_prop_value is '値';
comment on column iko_trn_actual_property.actual_prop_order is '表示順';

-- 移行用カンバンプロパティ
drop table if exists iko_trn_kanban_property;
create table iko_trn_kanban_property (
  kanban_property_id bigserial not null
  , fk_kanban_id bigint not null
  , kanban_property_name character varying(256) not null
  , kanban_property_type character varying(128) not null
  , kanban_property_value text
  , kanban_property_order integer
  , constraint iko_trn_kanban_property_pk primary key (kanban_property_id)
) ;

alter table iko_trn_kanban_property add constraint trn_kanban_property_idx1
  unique (kanban_property_id) ;

create index idx_kanban_fk_kanban_id
  on iko_trn_kanban_property(fk_kanban_id);

create index idx_kanban_property_name
  on iko_trn_kanban_property(kanban_property_name);

comment on table iko_trn_kanban_property is '移行用カンバンプロパティ';
comment on column iko_trn_kanban_property.kanban_property_id is 'ID';
comment on column iko_trn_kanban_property.fk_kanban_id is 'カンバンID';
comment on column iko_trn_kanban_property.kanban_property_name is 'プロパティ名';
comment on column iko_trn_kanban_property.kanban_property_type is '型';
comment on column iko_trn_kanban_property.kanban_property_value is '値';
comment on column iko_trn_kanban_property.kanban_property_order is '表示順';

-- 移行用工程カンバンプロパティ
drop table if exists iko_trn_work_kanban_property;
create table iko_trn_work_kanban_property (
  work_kanban_property_id bigserial not null
  , fk_work_kanban_id bigint not null
  , kanban_property_name character varying(256) not null
  , kanban_property_type character varying(128) not null
  , kanban_property_value text
  , kanban_property_order integer
  , constraint iko_trn_work_kanban_property_pk primary key (work_kanban_property_id)
) ;

alter table iko_trn_work_kanban_property add constraint trn_work_kanban_property_idx1
  unique (work_kanban_property_id) ;

create index idx_work_kanban_property_fk_work_kanban_id
  on iko_trn_work_kanban_property(fk_work_kanban_id);

create index idx_work_kanban_property_name
  on iko_trn_work_kanban_property(kanban_property_name);

comment on table iko_trn_work_kanban_property is '移行用工程カンバンプロパティ';
comment on column iko_trn_work_kanban_property.work_kanban_property_id is 'ID';
comment on column iko_trn_work_kanban_property.fk_work_kanban_id is '工程カンバンID';
comment on column iko_trn_work_kanban_property.kanban_property_name is 'プロパティ名';
comment on column iko_trn_work_kanban_property.kanban_property_type is '型';
comment on column iko_trn_work_kanban_property.kanban_property_value is '値';
comment on column iko_trn_work_kanban_property.kanban_property_order is '表示順';
