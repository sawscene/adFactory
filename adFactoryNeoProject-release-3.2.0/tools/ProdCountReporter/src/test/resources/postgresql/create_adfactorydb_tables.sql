-- adFactoryDB2 テーブル作成

-- 階層関連付け
create table con_hierarchy (
  hierarchy_id bigint not null
  , work_workflow_id bigint not null
  , hierarchy_type integer
  , constraint con_hierarchy_pk primary key (hierarchy_id,work_workflow_id)
) ;

alter table con_hierarchy add constraint con_hierarchy_idx1
  unique (hierarchy_id,work_workflow_id) ;

comment on table con_hierarchy is '階層関連付け:新規追加
階層を保持するテーブル
工程、工程順を一元管理する';
comment on column con_hierarchy.hierarchy_id is '階層ID';
comment on column con_hierarchy.work_workflow_id is '工程・工程順ID';
comment on column con_hierarchy.hierarchy_type is '階層種別:■階層種別 (hierarchy_type)
　0: 工程
　1: 工程順';

-- カンバン階層関連付け
create table con_kanban_hierarchy (
  kanban_hierarchy_id bigint not null
  , kanban_id bigint not null
  , constraint con_kanban_hierarchy_pk primary key (kanban_hierarchy_id,kanban_id)
) ;

alter table con_kanban_hierarchy add constraint con_kanban_hierarchy_idx1
  unique (kanban_hierarchy_id,kanban_id) ;

comment on table con_kanban_hierarchy is 'カンバン階層関連付け:
階層関連付けへ統合';
comment on column con_kanban_hierarchy.kanban_hierarchy_id is 'カンバン階層ID';
comment on column con_kanban_hierarchy.kanban_id is 'カンバンID';

-- 組織・休憩関連付け
create table con_organization_breaktime (
  organization_id bigint not null
  , breaktime_id bigint not null
  , constraint con_organization_breaktime_pk primary key (organization_id,breaktime_id)
) ;

comment on table con_organization_breaktime is '組織・休憩関連付け:組織単位に休憩のを設定';
comment on column con_organization_breaktime.organization_id is '組織ID';
comment on column con_organization_breaktime.breaktime_id is '休憩ID';

-- 組織・役割関連付け
create table con_organization_role (
  organization_id bigint not null
  , role_id bigint not null
  , constraint con_organization_role_pk primary key (organization_id,role_id)
) ;

comment on table con_organization_role is '組織・役割関連付け:組織単位に役割を設定したい
組織には人も設定しているので、人単位にも権限を付与している';
comment on column con_organization_role.organization_id is '組織ID';
comment on column con_organization_role.role_id is '役割ID';

-- 組織・作業区分関連付け
create table con_organization_work_category (
  organization_id bigint not null
  , work_category_id bigint not null
  , constraint con_organization_work_category_pk primary key (organization_id,work_category_id)
) ;

comment on table con_organization_work_category is '組織・作業区分関連付け:組織単位に作業区分を設定したい';
comment on column con_organization_work_category.organization_id is '組織ID';
comment on column con_organization_work_category.work_category_id is '作業区分ID';

-- 工程・設備関連付け
create table con_work_equipment (
  work_kbn integer not null
  , workflow_id bigint not null
  , work_id bigint not null
  , equipment_id bigint not null
  , constraint con_work_equipment_pk primary key (work_kbn,workflow_id,work_id,equipment_id)
) ;

comment on table con_work_equipment is '工程・設備関連付け:工程順の中に設定している工程に対する設備の割当て
※ここで言う設備は、作業者端末';
comment on column con_work_equipment.work_kbn is '工程区分:■工程区分 (work_kbn)
　0: 通常工程
　1: バラ工程';
comment on column con_work_equipment.workflow_id is '工程順ID';
comment on column con_work_equipment.work_id is '工程ID';
comment on column con_work_equipment.equipment_id is '設備ID';

-- 工程・組織関連付け
create table con_work_organization (
  work_kbn integer not null
  , workflow_id bigint not null
  , work_id bigint not null
  , organization_id bigint not null
  , constraint con_work_organization_pk primary key (work_kbn,workflow_id,work_id,organization_id)
) ;

comment on table con_work_organization is '工程・組織関連付け:工程順の中に設定している工程に対する組織の割当て
※誰が作業できるか';
comment on column con_work_organization.work_kbn is '工程区分:■工程区分 (work_kbn)
　0: 通常工程
　1: バラ工程';
comment on column con_work_organization.workflow_id is '工程順ID';
comment on column con_work_organization.work_id is '工程ID';
comment on column con_work_organization.organization_id is '組織ID';

-- 工程順工程関連付け
create table con_workflow_work (
  work_kbn integer not null
  , workflow_id bigint not null
  , work_id bigint not null
  , skip_flag boolean not null
  , workflow_order integer not null
  , standard_start_time timestamp without time zone
  , standard_end_time timestamp without time zone
  , constraint con_workflow_work_pk primary key (work_kbn,workflow_id,work_id)
) ;

create index idx_workflow_work_fk_workflow_id
  on con_workflow_work(workflow_id);

comment on table con_workflow_work is '工程順工程関連付け:工程順の中にどの工程があるのか管理しているテーブル';
comment on column con_workflow_work.work_kbn is '工程区分:■工程区分 (work_kbn)
　0: 通常工程
　1: バラ工程';
comment on column con_workflow_work.workflow_id is '工程順ID';
comment on column con_workflow_work.work_id is '工程ID';
comment on column con_workflow_work.skip_flag is 'スキップフラグ';
comment on column con_workflow_work.workflow_order is '表示順';
comment on column con_workflow_work.standard_start_time is '基準開始時間';
comment on column con_workflow_work.standard_end_time is '基準完了時間';

-- 工程カンバン・設備関連付け
create table con_workkanban_equipment (
  workkanban_id bigint not null
  , equipment_id bigint not null
  , constraint con_workkanban_equipment_pk primary key (workkanban_id,equipment_id)
) ;

alter table con_workkanban_equipment add constraint con_workkanban_equipment_idx1
  unique (workkanban_id,equipment_id) ;

comment on table con_workkanban_equipment is '工程カンバン・設備関連付け:
工程・設備関連付けに準ずる';
comment on column con_workkanban_equipment.workkanban_id is '工程カンバンID';
comment on column con_workkanban_equipment.equipment_id is '設備ID';

-- 工程カンバン・組織関連付け
create table con_workkanban_organization (
  workkanban_id bigint not null
  , organization_id bigint not null
  , constraint con_workkanban_organization_pk primary key (workkanban_id,organization_id)
) ;

alter table con_workkanban_organization add constraint con_workkanban_organization_idx1
  unique (workkanban_id,organization_id) ;

comment on table con_workkanban_organization is '工程カンバン・組織関連付け';
comment on column con_workkanban_organization.workkanban_id is '工程カンバンID';
comment on column con_workkanban_organization.organization_id is '組織ID';

-- 認証情報
create table mst_authentication_info (
  authentication_id bigserial not null
  , organization_id serial not null
  , authentication_type character varying(128) not null
  , authentication_data text not null
  , validity_period timestamp without time zone
  , use_lock boolean
  , ver_info integer default 1 not null
  , constraint mst_authentication_info_pk primary key (authentication_id)
) ;

comment on table mst_authentication_info is '認証情報';
comment on column mst_authentication_info.authentication_id is '認証id';
comment on column mst_authentication_info.organization_id is '組織ID:マスタidを変更';
comment on column mst_authentication_info.authentication_type is '種別';
comment on column mst_authentication_info.authentication_data is '認証情報';
comment on column mst_authentication_info.validity_period is '有効期限';
comment on column mst_authentication_info.use_lock is '使用ロック';
comment on column mst_authentication_info.ver_info is '排他用バージョン';

-- 休憩マスタ設定項目
create table mst_breaktime (
  breaktime_id bigserial not null
  , breaktime_name character varying(256) not null
  , starttime timestamp with time zone not null
  , endtime timestamp with time zone not null
  , ver_info integer default 1 not null
  , constraint mst_breaktime_pk primary key (breaktime_id)
) ;

alter table mst_breaktime add constraint mst_breaktime_idx2
  unique (breaktime_name) ;

comment on table mst_breaktime is '休憩マスタ設定項目:会社、部、課、チーム　等（最小は人単位）組織の階層単位に休憩を定義出来るようにしている';
comment on column mst_breaktime.breaktime_id is '休憩ID';
comment on column mst_breaktime.breaktime_name is '休憩名称';
comment on column mst_breaktime.starttime is '開始時間';
comment on column mst_breaktime.endtime is '終了時間';
comment on column mst_breaktime.ver_info is '排他用バージョン';

-- ステータス表示マスタ
create table mst_displayed_status (
  status_id bigserial not null
  , organization_id bigint
  , status_name character varying(256) not null
  , font_color character varying(128)
  , back_color character varying(128)
  , light_pattern character varying(128)
  , notation_name character varying(256)
  , melody_path text
  , melody_repeat boolean
  , ver_info integer default 1 not null
  , constraint mst_displayed_status_pk primary key (status_id)
) ;

alter table mst_displayed_status add constraint mst_displayed_status_idx2
  unique (status_name) ;

comment on table mst_displayed_status is 'ステータス表示マスタ:モニター、作業者端末の色、音などの設定を行う
*色、音制御は基本このマスタに設定して制御を行うルール

ステータスは固定で持っているため、削除、追加は出来ない。色などの修正のみ';
comment on column mst_displayed_status.status_id is 'ステータス表示ID';
comment on column mst_displayed_status.organization_id is '組織ID';
comment on column mst_displayed_status.status_name is 'ステータス名';
comment on column mst_displayed_status.font_color is '文字色';
comment on column mst_displayed_status.back_color is '背景色';
comment on column mst_displayed_status.light_pattern is '点灯パターン';
comment on column mst_displayed_status.notation_name is '表記';
comment on column mst_displayed_status.melody_path is 'メロディーパス';
comment on column mst_displayed_status.melody_repeat is 'メロディー繰り返し';
comment on column mst_displayed_status.ver_info is '排他用バージョン';

-- 設備マスタ
create table mst_equipment (
  equipment_id bigserial not null
  , equipment_name character varying(256) not null
  , equipment_identify character varying(256) not null
  , equipment_type_id bigint
  , update_person_id bigint
  , update_datetime timestamp without time zone
  , remove_flag boolean
  , cal_flag boolean
  , cal_next_date timestamp without time zone
  , cal_term integer
  , cal_term_unit character varying(8)
  , cal_warning_days integer
  , cal_last_date timestamp without time zone
  , cal_person_id bigint
  , parent_equipment_id bigint
  , IPv4_address character varying(256)
  , work_progress_flag boolean default false
  , plugin_name character varying(256)
  , equipment_add_info jsonb
  , service_info jsonb
  , ver_info integer default 1 not null
  , constraint mst_equipment_pk primary key (equipment_id)
) ;

alter table mst_equipment add constraint mst_equipment_idx2
  unique (equipment_identify) ;

create index idx_equipmen_name
  on mst_equipment(equipment_name);


comment on table mst_equipment is '設備マスタ:設備の情報
*モニタ、作業者端末、計測機器、製造設備';
comment on column mst_equipment.equipment_id is '設備ID';
comment on column mst_equipment.equipment_name is '設備名';
comment on column mst_equipment.equipment_identify is '設備識別名';
comment on column mst_equipment.equipment_type_id is '設備種別';
comment on column mst_equipment.update_person_id is '更新者';
comment on column mst_equipment.update_datetime is '更新日時';
comment on column mst_equipment.remove_flag is '論理削除フラグ';
comment on column mst_equipment.cal_flag is '機器校正有無';
comment on column mst_equipment.cal_next_date is '次回校正日';
comment on column mst_equipment.cal_term is '校正間隔';
comment on column mst_equipment.cal_term_unit is '間隔単位';
comment on column mst_equipment.cal_warning_days is '警告表示日数';
comment on column mst_equipment.cal_last_date is '最終校正日';
comment on column mst_equipment.cal_person_id is '校正実施者';
comment on column mst_equipment.parent_equipment_id is '親設備ID:1.親がある場合、親の設備IDを設定する。
2.最上位の場合0を設定';
comment on column mst_equipment.IPv4_address is 'IPv4アドレス';
comment on column mst_equipment.work_progress_flag is '工程進捗フラグ';
comment on column mst_equipment.plugin_name is 'プラグイン名';
comment on column mst_equipment.equipment_add_info is '追加情報';
comment on column mst_equipment.service_info is 'サービス情報';
comment on column mst_equipment.ver_info is '排他用バージョン';

-- 設備種別マスタ
create table mst_equipment_type (
  equipment_type_id bigserial not null
  , name character varying(256) not null
  , ver_info integer default 1 not null
  , constraint mst_equipment_type_pk primary key (equipment_type_id)
) ;

alter table mst_equipment_type add constraint mst_equipment_type_idx2
  unique (name) ;

comment on table mst_equipment_type is '設備種別マスタ:設備の種別（モニタ、作業者端末、計測機器、製造設備）';
comment on column mst_equipment_type.equipment_type_id is '設備種別ID';
comment on column mst_equipment_type.name is '名前';
comment on column mst_equipment_type.ver_info is '排他用バージョン';

-- 階層マスタ
create table mst_hierarchy (
  hierarchy_id bigserial not null
  , hierarchy_type integer
  , hierarchy_name character varying(256) not null
  , parent_hierarchy_id bigint
  , ver_info integer default 1 not null
  , constraint mst_hierarchy_pk primary key (hierarchy_id)
) ;

alter table mst_hierarchy add constraint mst_hierarchy_idx1
  unique (hierarchy_type,hierarchy_name) ;

comment on table mst_hierarchy is '階層マスタ:新規追加　
階層を保持するテーブル
工程、工程順を一元管理する';
comment on column mst_hierarchy.hierarchy_id is '階層ID';
comment on column mst_hierarchy.hierarchy_type is '階層種別:
■階層種別 (hierarchy_type)
　0: 工程
　1: 工程順';
comment on column mst_hierarchy.hierarchy_name is '名前';
comment on column mst_hierarchy.parent_hierarchy_id is '親階層ID';
comment on column mst_hierarchy.ver_info is '排他用バージョン';

-- 休日情報
create table mst_holiday (
  holiday_id bigserial not null
  , holiday_name character varying(256)
  , holiday_date timestamp without time zone
  , ver_info integer default 1 not null
  , constraint mst_holiday_pk primary key (holiday_id)
) ;

comment on table mst_holiday is '休日情報:システム全体の共通の休日
生産管理でexcelデータ読み込み処理で登録';
comment on column mst_holiday.holiday_id is '休日ID';
comment on column mst_holiday.holiday_name is '休日の名称';
comment on column mst_holiday.holiday_date is '休日の日付';
comment on column mst_holiday.ver_info is '排他用バージョン';

-- 間接作業マスタ
create table mst_indirect_work (
  indirect_work_id bigserial not null
  , class_number character varying(64) default 'NONE' not null
  , work_number character varying(64) not null
  , work_name character varying(256) not null
  , work_category_id bigint default 1 not null
  , ver_info integer default 1 not null
  , constraint mst_indirect_work_pk primary key (indirect_work_id)
) ;

alter table mst_indirect_work add constraint mst_indirect_work_idx1
  unique (class_number,work_number) ;

comment on table mst_indirect_work is '間接作業マスタ:日報機能で集計する際に必要となるマスタ
adProductで管理しない作業を登録するマスタ（たとえば、会議など）';
comment on column mst_indirect_work.indirect_work_id is '間接作業ID';
comment on column mst_indirect_work.class_number is '分類番号';
comment on column mst_indirect_work.work_number is '作業番号';
comment on column mst_indirect_work.work_name is '作業名';
comment on column mst_indirect_work.work_category_id is '作業区分ID';
comment on column mst_indirect_work.ver_info is '排他用バージョン';

-- カンバン階層マスタ
create table mst_kanban_hierarchy (
  kanban_hierarchy_id bigserial not null
  , hierarchy_name character varying(256) not null
  , partition_flag boolean default false
  , constraint mst_kanban_hierarchy_pk primary key (kanban_hierarchy_id)
) ;

alter table mst_kanban_hierarchy add constraint mst_kanban_hierarchy_idx1
  unique (kanban_hierarchy_id) ;

alter table mst_kanban_hierarchy add constraint mst_kanban_hierarchy_idx2
  unique (hierarchy_name) ;

comment on table mst_kanban_hierarchy is 'カンバン階層マスタ:
階層マスタに準ずる';
comment on column mst_kanban_hierarchy.kanban_hierarchy_id is 'カンバン階層ID';
comment on column mst_kanban_hierarchy.hierarchy_name is '名前';
comment on column mst_kanban_hierarchy.partition_flag is '完了カンバン自動移動フラグ';

-- モノマスタ
create table mst_object (
  object_id character varying(256) not null
  , object_type_id bigint not null
  , object_name character varying(256) not null
  , remove_flag boolean
  , ver_info integer default 1 not null
  , constraint mst_object_pk primary key (object_id,object_type_id)
) ;

create index idx_object_name
  on mst_object(object_name);

comment on table mst_object is 'モノマスタ:工程編集で部品選択で選択する対象になるデーテ
部品
*計測器とは電子天秤などは設備に登録';
comment on column mst_object.object_id is 'モノID';
comment on column mst_object.object_type_id is 'モノ種別ID';
comment on column mst_object.object_name is 'モノ名';
comment on column mst_object.remove_flag is '削除フラグ';
comment on column mst_object.ver_info is '排他用バージョン';

-- モノ種別マスタ
create table mst_object_type (
  object_type_id bigserial not null
  , object_type_name character varying(256) not null
  , ver_info integer default 1 not null
  , constraint mst_object_type_pk primary key (object_type_id)
) ;

alter table mst_object_type add constraint mst_object_type_idx2
  unique (object_type_name) ;

comment on table mst_object_type is 'モノ種別マスタ:モノの種類';
comment on column mst_object_type.object_type_id is 'モノ種別ID';
comment on column mst_object_type.object_type_name is 'モノ種別名';
comment on column mst_object_type.ver_info is '排他用バージョン';

-- 組織マスタ
create table mst_organization (
  organization_id bigserial not null
  , organization_name character varying(256) not null
  , organization_identify character varying(256) not null
  , authority_type character varying(128)
  , language_type character varying(128)
  , pass_word character varying(256)
  , mail_address character varying(256)
  , update_person_id bigint
  , update_datetime timestamp without time zone
  , remove_flag boolean
  , work_skill text
  , parent_organization_id bigint
  , organization_add_info jsonb
  , service_info jsonb
  , ver_info integer default 1 not null
  , constraint mst_organization_pk primary key (organization_id)
) ;

alter table mst_organization add constraint mst_organization_idx2
  unique (organization_identify) ;

create index idx_organization_name
  on mst_organization(organization_name);

comment on table mst_organization is '組織マスタ:組織と人の情報';
comment on column mst_organization.organization_id is '組織ID';
comment on column mst_organization.organization_name is '組織名';
comment on column mst_organization.organization_identify is '組織識別名:従業員コードのようにユニークになるキーを設定する';
comment on column mst_organization.authority_type is '権限';
comment on column mst_organization.language_type is '言語';
comment on column mst_organization.pass_word is 'パスワード';
comment on column mst_organization.mail_address is 'メールアドレス';
comment on column mst_organization.update_person_id is '更新者';
comment on column mst_organization.update_datetime is '更新日時';
comment on column mst_organization.remove_flag is '論理削除フラグ';
comment on column mst_organization.work_skill is '作業スキル';
comment on column mst_organization.parent_organization_id is '親組織ID';
comment on column mst_organization.organization_add_info is '追加情報';
comment on column mst_organization.service_info is 'サービス情報';
comment on column mst_organization.ver_info is '排他用バージョン';

-- 理由マスタ
create table mst_reason (
  reason_id bigserial not null
  , organization_id bigint
  , reason_type integer
  , reason character varying(256) not null
  , font_color character varying(128)
  , back_color character varying(128)
  , light_pattern character varying(128)
  , reason_order bigint
  , ver_info integer default 1 not null
  , constraint mst_reason_pk primary key (reason_id)
) ;

comment on table mst_reason is '理由マスタ:新規追加

理由を設定するマスタ';
comment on column mst_reason.reason_id is '理由ID';
comment on column mst_reason.organization_id is '組織ID:0:全ての組織に対応';
comment on column mst_reason.reason_type is '理由種別:■ 理由種別 (reason_type)
　0: 呼び出し理由
　1: 中断理由
　2: 遅延理由';
comment on column mst_reason.reason is '理由:';
comment on column mst_reason.font_color is '文字色';
comment on column mst_reason.back_color is '背景色';
comment on column mst_reason.light_pattern is '点灯パターン';
comment on column mst_reason.reason_order is '理由オーダー';
comment on column mst_reason.ver_info is '排他用バージョン';

-- 役割権限マスタ
create table mst_role_authority (
  role_id bigserial not null
  , role_name character varying(256)
  , actual_del boolean default false
  , resource_edit boolean default false
  , kanban_create boolean default false
  , line_manage boolean default false
  , actual_output boolean default false
  , kanban_reference boolean default false
  , resource_reference boolean default false
  , access_edit boolean default false
  , ver_info integer default 1 not null
  , constraint mst_role_authority_pk primary key (role_id)
) ;

comment on table mst_role_authority is '役割権限マスタ:役割の制限情報を設定
*機能制限する項目は固定で分類化さ（リソース編集、カンバン作成　など）れて制限されている。　*制限項目の増減は改修が必要
*役割は増減可能';
comment on column mst_role_authority.role_id is '役割ID';
comment on column mst_role_authority.role_name is '役割名';
comment on column mst_role_authority.actual_del is '実績削除権限';
comment on column mst_role_authority.resource_edit is 'リソース編集権限';
comment on column mst_role_authority.kanban_create is 'カンバン作成権限';
comment on column mst_role_authority.line_manage is 'ライン管理権限';
comment on column mst_role_authority.actual_output is '実績出力権限';
comment on column mst_role_authority.kanban_reference is 'カンバン参照権限';
comment on column mst_role_authority.resource_reference is 'リソース参照権限';
comment on column mst_role_authority.access_edit is 'アクセス権編集権限';
comment on column mst_role_authority.ver_info is '排他用バージョン';

-- 予定情報
create table mst_schedule (
  schedule_id bigserial not null
  , schedule_name character varying(256)
  , schedule_from_date timestamp without time zone
  , schedule_to_date timestamp without time zone
  , organization_id bigint
  , ver_info integer default 1 not null
  , constraint mst_schedule_pk primary key (schedule_id)
) ;

comment on table mst_schedule is '予定情報:予定する未来の休日やその他予定を設定し、自動スケジューリングから除外する目的

休日作業する場合は自動スケジューリングの対象外となっている。';
comment on column mst_schedule.schedule_id is '予定情報ID';
comment on column mst_schedule.schedule_name is '予定の名称';
comment on column mst_schedule.schedule_from_date is '予定日時の先頭';
comment on column mst_schedule.schedule_to_date is '予定日時の末尾';
comment on column mst_schedule.organization_id is '組織ID';
comment on column mst_schedule.ver_info is '排他用バージョン';

-- 工程マスタ
create table mst_work (
  work_id bigserial not null
  , work_name character varying(256) not null
  , takt_time integer
  , content text
  , content_type character varying(128)
  , update_person_id bigint
  , update_datetime timestamp without time zone
  , remove_flag boolean
  , font_color character varying(128)
  , back_color character varying(128)
  , use_parts text
  , work_number character varying(64)
  , work_check_info jsonb
  , work_add_info jsonb
  , service_info jsonb
  , ver_info integer default 1 not null
  , constraint mst_work_pk primary key (work_id)
) ;

alter table mst_work add constraint mst_work_idx2
  unique (work_name) ;

comment on table mst_work is '工程マスタ:工程の基本情報を登録';
comment on column mst_work.work_id is '工程ID';
comment on column mst_work.work_name is '工程名';
comment on column mst_work.takt_time is 'タクトタイム[ms]';
comment on column mst_work.content is 'コンテンツ';
comment on column mst_work.content_type is 'コンテンツタイプ';
comment on column mst_work.update_person_id is '更新者';
comment on column mst_work.update_datetime is '更新日時';
comment on column mst_work.remove_flag is '論理削除フラグ';
comment on column mst_work.font_color is '文字色';
comment on column mst_work.back_color is '背景色';
comment on column mst_work.use_parts is '使用部品';
comment on column mst_work.work_number is '作業番号';
comment on column mst_work.work_check_info is '検査情報';
comment on column mst_work.work_add_info is '追加情報';
comment on column mst_work.service_info is 'サービス情報';
comment on column mst_work.ver_info is '排他用バージョン';

-- 作業区分マスタ
create table mst_work_category (
  work_category_id bigserial not null
  , work_category_name character varying(256) not null
  , ver_info integer default 1 not null
  , constraint mst_work_category_pk primary key (work_category_id)
) ;

alter table mst_work_category add constraint mst_work_category_idx2
  unique (work_category_name) ;

comment on table mst_work_category is '作業区分マスタ:間接作業の分類';
comment on column mst_work_category.work_category_id is '作業区分ID';
comment on column mst_work_category.work_category_name is '作業区分名';
comment on column mst_work_category.ver_info is '排他用バージョン';

-- 工程セクション
create table mst_work_section (
  work_section_id bigserial not null
  , work_id bigint not null
  , document_title character varying(256)
  , page_num integer
  , file_name character varying(256)
  , file_update_datetime timestamp with time zone
  , work_section_order integer not null
  , physical_file_name character varying(256)
  , ver_info integer default 1 not null
  , constraint mst_work_section_pk primary key (work_section_id)
) ;

comment on table mst_work_section is '工程セクション:品質トレーサビリティの各ページの基本情報を格納している情報
工程マスタプロパティ－工程セクション表示順と結合して情報を取得する';
comment on column mst_work_section.work_section_id is '工程セクションID';
comment on column mst_work_section.work_id is '工程ID';
comment on column mst_work_section.document_title is 'ドキュメント名';
comment on column mst_work_section.page_num is 'ページ番号';
comment on column mst_work_section.file_name is '表示ファイル名';
comment on column mst_work_section.file_update_datetime is 'ファイル更新日時';
comment on column mst_work_section.work_section_order is '表示順';
comment on column mst_work_section.physical_file_name is '物理ファイル名';
comment on column mst_work_section.ver_info is '排他用バージョン';

-- 工程順マスタ
create table mst_workflow (
  workflow_id bigserial not null
  , workflow_name character varying(256) not null
  , workflow_revision character varying(256)
  , workflow_diaglam text
  , update_person_id bigint
  , update_datetime timestamp without time zone
  , ledger_path text
  , remove_flag boolean
  , workflow_number character varying(64)
  , workflow_rev integer default 1 not null
  , model_name character varying(256)
  , open_time time
  , close_time time
  , schedule_policy integer default 0 not null
  , workflow_add_info jsonb
  , service_info jsonb
  , ver_info integer default 1 not null
  , constraint mst_workflow_pk primary key (workflow_id)
) ;

alter table mst_workflow add constraint mst_workflow_idx2
  unique (workflow_name,workflow_rev) ;

create index idx_workflow_name
  on mst_workflow(workflow_name);

comment on table mst_workflow is '工程順マスタ:工程順の基本情報を登録';
comment on column mst_workflow.workflow_id is '工程順ID';
comment on column mst_workflow.workflow_name is '工程順名';
comment on column mst_workflow.workflow_revision is '版名:未使用';
comment on column mst_workflow.workflow_diaglam is 'ワークフロー図';
comment on column mst_workflow.update_person_id is '更新者';
comment on column mst_workflow.update_datetime is '更新日時';
comment on column mst_workflow.ledger_path is '帳票テンプレートパス';
comment on column mst_workflow.remove_flag is '論理削除フラグ';
comment on column mst_workflow.workflow_number is '作業番号';
comment on column mst_workflow.workflow_rev is '版数';
comment on column mst_workflow.model_name is '機種名';
comment on column mst_workflow.open_time is '始業時間';
comment on column mst_workflow.close_time is '終業時間';
comment on column mst_workflow.schedule_policy is '作業順序';
comment on column mst_workflow.workflow_add_info is '追加情報';
comment on column mst_workflow.service_info is 'サービス情報';
comment on column mst_workflow.ver_info is '排他用バージョン';

-- DBバージョン
create table t_ver (
  sid numeric(1,0) not null
  , verno character varying(20) not null
  , constraint t_ver_pk primary key (sid)
) ;

comment on table t_ver is 'DBバージョン:adFactoryDB Version Definition';
comment on column t_ver.sid is 'DBバージョンID';
comment on column t_ver.verno is 'DBバージョン';

-- カンバン階層
create table tre_kanban_hierarchy (
  parent_id bigint not null
  , child_id bigint
  , constraint tre_kanban_hierarchy_pk primary key (parent_id,child_id)
) ;

alter table tre_kanban_hierarchy add constraint tre_kanban_hierarchy_idx1
  unique (parent_id,child_id) ;

comment on table tre_kanban_hierarchy is 'カンバン階層';
comment on column tre_kanban_hierarchy.parent_id is '親ID';
comment on column tre_kanban_hierarchy.child_id is '子ID';

-- 階層アクセス権
create table trn_access_hierarchy (
  type_id smallint not null
  , hierarchy_id bigint not null
  , organization_id bigint not null
  , constraint trn_access_hierarchy_pk primary key (type_id,hierarchy_id,organization_id)
) ;

comment on table trn_access_hierarchy is '階層アクセス権:各階層に対して、どの組織が表示できるか制御している情報を保持する
※階層種別IDでどの階層（カンバン階層、工程階層など）を区別し、全階層のアクセス権を保持しているテーブル';
comment on column trn_access_hierarchy.type_id is '階層種別ID';
comment on column trn_access_hierarchy.hierarchy_id is '階層ID';
comment on column trn_access_hierarchy.organization_id is '組織ID';

-- 工程実績付加情報
create table trn_actual_adition (
  actual_adition_id bigserial
  , actual_id bigint
  , image_data bytea
  , constraint trn_actual_adition_pk primary key (actual_adition_id,actual_id)
) ;

comment on table trn_actual_adition is '工程実績付加情報';
comment on column trn_actual_adition.actual_adition_id is '付加情報id';
comment on column trn_actual_adition.actual_id is '実績ID';
comment on column trn_actual_adition.image_data is '画像データ';

-- 工程実績
create table trn_actual_result (
  actual_id bigserial not null
  , kanban_id bigint not null
  , work_kanban_id bigint not null
  , implement_datetime timestamp without time zone not null
  , transaction_id bigint not null
  , equipment_id bigint
  , organization_id bigint
  , workflow_id bigint not null
  , work_id bigint not null
  , actual_status character varying(256) not null
  , work_time integer
  , interrupt_reason character varying(256)
  , delay_reason character varying(256)
  , comp_num integer
  , pair_id bigint
  , non_work_time integer
  , interrupt_reason_id bigint
  , delay_reason_id bigint
  , kanban_name character varying(256)
  , equipment_name character varying(256)
  , organization_name character varying(256)
  , workflow_name character varying(256)
  , work_name character varying(256)
  , actual_add_info jsonb
  , service_info jsonb
  , ver_info integer default 1 not null
  , constraint trn_actual_result_pk primary key (actual_id)
) ;

create index idx_actual_result_fk_equipment_id
  on trn_actual_result(equipment_id);

create index idx_actual_result_fk_kanban_id
  on trn_actual_result(kanban_id);

create index idx_actual_result_fk_organization_id
  on trn_actual_result(organization_id);

create index idx_actual_result_fk_work_kanban_id
  on trn_actual_result(work_kanban_id);

comment on table trn_actual_result is '工程実績:adProduct で作業開始などアクションした際の履歴としてデータを登録する';
comment on column trn_actual_result.actual_id is '実績ID';
comment on column trn_actual_result.kanban_id is 'カンバンID';
comment on column trn_actual_result.work_kanban_id is '工程カンバンID';
comment on column trn_actual_result.implement_datetime is '実施時刻';
comment on column trn_actual_result.transaction_id is 'トランザクションID';
comment on column trn_actual_result.equipment_id is '設備ID';
comment on column trn_actual_result.organization_id is '組織ID';
comment on column trn_actual_result.workflow_id is '工程順ID';
comment on column trn_actual_result.work_id is '工程ID';
comment on column trn_actual_result.actual_status is 'ステータス';
comment on column trn_actual_result.work_time is '作業時間[ms]';
comment on column trn_actual_result.interrupt_reason is '中断理由';
comment on column trn_actual_result.delay_reason is '遅延理由';
comment on column trn_actual_result.comp_num is '完成数';
comment on column trn_actual_result.pair_id is 'ペアID';
comment on column trn_actual_result.non_work_time is '中断時間[ms]';
comment on column trn_actual_result.interrupt_reason_id is '中断理由ID';
comment on column trn_actual_result.delay_reason_id is '遅延理由ID';
comment on column trn_actual_result.kanban_name is 'カンバン名';
comment on column trn_actual_result.equipment_name is '設備名';
comment on column trn_actual_result.organization_name is '組織名';
comment on column trn_actual_result.workflow_name is '工程順名';
comment on column trn_actual_result.work_name is '工程名';
comment on column trn_actual_result.actual_add_info is '検査結果';
comment on column trn_actual_result.service_info is 'サービス情報';
comment on column trn_actual_result.ver_info is '排他用バージョン';

-- 間接工数実績
create table trn_indirect_actual (
  indirect_actual_id bigserial not null
  , indirect_work_id bigint not null
  , implement_datetime timestamp with time zone not null
  , transaction_id bigint not null
  , organization_id bigint
  , work_time integer default 0 not null
  , constraint trn_indirect_actual_pk primary key (indirect_actual_id)
) ;

comment on table trn_indirect_actual is '間接工数実績:間接作業の実績を登録
adManager、adProductで誰がいつ何をしたのかを手動で登録した内容が保持される。';
comment on column trn_indirect_actual.indirect_actual_id is '間接工数実績ID';
comment on column trn_indirect_actual.indirect_work_id is '間接作業ID';
comment on column trn_indirect_actual.implement_datetime is '実施日時';
comment on column trn_indirect_actual.transaction_id is 'トランザクションID';
comment on column trn_indirect_actual.organization_id is '組織ID';
comment on column trn_indirect_actual.work_time is '作業時間[ms]';

-- カンバン
create table trn_kanban (
  kanban_id bigserial not null
  , kanban_name character varying(256) not null
  , kanban_subname character varying(256)
  , workflow_id bigint not null
  , start_datetime timestamp without time zone
  , comp_datetime timestamp without time zone
  , update_person_id bigint
  , update_datetime timestamp without time zone
  , kanban_status character varying(128) not null
  , interrupt_reason_id bigint
  , delay_reason_id bigint
  , lot_quantity integer
  , actual_start_datetime timestamp without time zone
  , actual_comp_datetime timestamp without time zone
  , model_name character varying(256)
  , repair_num integer
  , production_type integer
  , kanban_add_info jsonb
  , service_info jsonb
  , ver_info integer default 1 not null
  , constraint trn_kanban_pk primary key (kanban_id)
) ;

alter table trn_kanban add constraint trn_kanban_idx2
  unique (kanban_name,kanban_subname,workflow_id) ;

create index idx_kanban_fk_workflow_id
  on trn_kanban(workflow_id);

create index idx_kanban_name
  on trn_kanban(kanban_name);

create index idx_kanban_subname
  on trn_kanban(kanban_subname);

comment on table trn_kanban is 'カンバン:カンバンの基本情報を保持するテーブル';
comment on column trn_kanban.kanban_id is 'カンバンID';
comment on column trn_kanban.kanban_name is 'カンバン名';
comment on column trn_kanban.kanban_subname is 'サブカンバン名';
comment on column trn_kanban.workflow_id is '工程順ID';
comment on column trn_kanban.start_datetime is '先頭工程開始予定日時';
comment on column trn_kanban.comp_datetime is '最終工程完了予定日時';
comment on column trn_kanban.update_person_id is '更新者';
comment on column trn_kanban.update_datetime is '更新日時';
comment on column trn_kanban.kanban_status is 'ステータス';
comment on column trn_kanban.interrupt_reason_id is '中断理由ID:実績で管理しているのでここでは不要';
comment on column trn_kanban.delay_reason_id is '遅延理由ID:実績で管理しているのでここでは不要';
comment on column trn_kanban.lot_quantity is 'ロット数量';
comment on column trn_kanban.actual_start_datetime is '開始日時';
comment on column trn_kanban.actual_comp_datetime is '完了日時';
comment on column trn_kanban.model_name is '機種名';
comment on column trn_kanban.repair_num is '補修数';
comment on column trn_kanban.production_type is '生産タイプ';
comment on column trn_kanban.kanban_add_info is '追加情報';
comment on column trn_kanban.service_info is 'サービス情報';
comment on column trn_kanban.ver_info is '排他用バージョン';

-- 生産実績
create table trn_prod_result (
  prod_result_id bigserial
  , fk_kanban_id bigint not null
  , fk_work_kanban_id bigint not null
  , fk_work_id bigint not null
  , order_num integer not null
  , unique_id character varying(256) not null
  , product_spec1 character varying(256)
  , product_spec2 character varying(256)
  , product_spec3 character varying(256)
  , product_spec4 character varying(256)
  , status character varying(64)
  , defect_type character varying(256)
  , fk_equipment_id bigint not null
  , fk_organization_id bigint not null
  , cycle_time time
  , cycle_sec integer
  , comp_datetime timestamp without time zone
  , shipping_id character varying(256)
  , tracking boolean default true not null
  , constraint trn_prod_result_pk primary key (prod_result_id)
) ;

create index idx_prod_result_fk_kanban_id
  on trn_prod_result(fk_kanban_id);

comment on table trn_prod_result is '生産実績:1つのカンバン/工程を複数人で作業した場合でも、カンバン/工程/UIDで1個のデータしか登録されません。
設備IDと組織IDは、最初に工程を開始した設備と組織で登録されます。';
comment on column trn_prod_result.prod_result_id is '生産実績ID';
comment on column trn_prod_result.fk_kanban_id is 'カンバンID';
comment on column trn_prod_result.fk_work_kanban_id is '工程カンバンID';
comment on column trn_prod_result.fk_work_id is '工程ID';
comment on column trn_prod_result.order_num is '副番';
comment on column trn_prod_result.unique_id is 'ユニークID';
comment on column trn_prod_result.product_spec1 is '仕様1';
comment on column trn_prod_result.product_spec2 is '仕様2';
comment on column trn_prod_result.product_spec3 is '仕様3';
comment on column trn_prod_result.product_spec4 is '仕様4';
comment on column trn_prod_result.status is 'ステータス';
comment on column trn_prod_result.defect_type is '不良理由';
comment on column trn_prod_result.fk_equipment_id is '設備ID';
comment on column trn_prod_result.fk_organization_id is '組織ID';
comment on column trn_prod_result.cycle_time is 'サイクルタイム';
comment on column trn_prod_result.cycle_sec is 'サイクルタイム(秒)';
comment on column trn_prod_result.comp_datetime is '完了日時';
comment on column trn_prod_result.shipping_id is '出荷タグ';
comment on column trn_prod_result.tracking is '追跡フラグ';

-- 製品
create table trn_product (
  product_id bigint not null
  , unique_id character varying(256) not null
  , fk_kanban_id bigint
  , comp_datetime timestamp with time zone
  , status character varying(64)
  , defect_type character varying(256)
  , order_num integer
  , defect_work_name character varying(256)
  , shipping_id character varying(256)
  , constraint trn_product_pk primary key (product_id)
) ;

alter table trn_product add constraint trn_product_id
  unique (product_id) ;

create index idx_product_fk_kanban_id
  on trn_product(fk_kanban_id);

create index idx_product_unique_id
  on trn_product(unique_id);

create index idx_product_shipping_id
  on trn_product(shipping_id);

comment on table trn_product is '製品';
comment on column trn_product.product_id is 'ID';
comment on column trn_product.unique_id is 'ユニークID';
comment on column trn_product.fk_kanban_id is 'カンバンID';
comment on column trn_product.comp_datetime is '完成日時';
comment on column trn_product.status is 'ステータス';
comment on column trn_product.defect_type is '不良種別';
comment on column trn_product.order_num is '副番:カンバン内登録順';
comment on column trn_product.defect_work_name is '不良工程';
comment on column trn_product.shipping_id is '出荷タグ';

-- 工程カンバン
create table trn_work_kanban (
  work_kanban_id bigserial not null
  , kanban_id bigint not null
  , workflow_id bigint not null
  , work_id bigint not null
  , separate_work_flag boolean not null
  , implement_flag boolean not null
  , skip_flag boolean not null
  , start_datetime timestamp without time zone
  , comp_datetime timestamp without time zone
  , takt_time integer
  , sum_times integer default 0
  , update_person_id bigint
  , update_datetime timestamp without time zone
  , work_status character varying(128) not null
  , interrupt_reason_id bigint
  , delay_reason_id bigint
  , work_kanban_order integer
  , serial_number integer
  , sync_work boolean
  , actual_start_datetime timestamp without time zone
  , actual_comp_datetime timestamp without time zone
  , actual_num1 integer
  , actual_num2 integer
  , actual_num3 integer
  , rework_num integer
  , work_kanban_add_info jsonb
  , service_info jsonb
  , constraint trn_work_kanban_pk primary key (work_kanban_id)
) ;

create index idx_work_kanban_fk_kanban_id
  on trn_work_kanban(kanban_id);

create index idx_work_kanban_fk_workflow_id
  on trn_work_kanban(workflow_id);

comment on table trn_work_kanban is '工程カンバン:工程順に対するカンバンと同じで工程に対するカンバンと同じ';
comment on column trn_work_kanban.work_kanban_id is '工程カンバンID';
comment on column trn_work_kanban.kanban_id is 'カンバンID';
comment on column trn_work_kanban.workflow_id is '工程順ID';
comment on column trn_work_kanban.work_id is '工程ID';
comment on column trn_work_kanban.separate_work_flag is 'バラ工程フラグ';
comment on column trn_work_kanban.implement_flag is '実施フラグ';
comment on column trn_work_kanban.skip_flag is 'スキップフラグ';
comment on column trn_work_kanban.start_datetime is '開始予定日時';
comment on column trn_work_kanban.comp_datetime is '完了予定日時';
comment on column trn_work_kanban.takt_time is 'タクトタイム[ms]';
comment on column trn_work_kanban.sum_times is '作業累積時間[ms]';
comment on column trn_work_kanban.update_person_id is '更新者';
comment on column trn_work_kanban.update_datetime is '更新日時';
comment on column trn_work_kanban.work_status is 'ステータス';
comment on column trn_work_kanban.interrupt_reason_id is '中断理由ID:実績で管理しているのでここでは不要';
comment on column trn_work_kanban.delay_reason_id is '遅延理由ID:実績で管理しているのでここでは不要';
comment on column trn_work_kanban.work_kanban_order is '表示順';
comment on column trn_work_kanban.serial_number is 'シリアル番号';
comment on column trn_work_kanban.sync_work is '同時作業';
comment on column trn_work_kanban.actual_start_datetime is '開始日時';
comment on column trn_work_kanban.actual_comp_datetime is '完了日時';
comment on column trn_work_kanban.actual_num1 is 'A品実績数';
comment on column trn_work_kanban.actual_num2 is 'B品実績数';
comment on column trn_work_kanban.actual_num3 is 'C品実績数';
comment on column trn_work_kanban.rework_num is '作業やり直し回数';
comment on column trn_work_kanban.work_kanban_add_info is '追加情報';
comment on column trn_work_kanban.service_info is 'サービス情報';

-- 工程カンバン作業中リスト
create table trn_work_kanban_working (
  work_kanban_working_id bigserial not null
  , work_kanban_id bigint
  , equipment_id bigint
  , organization_id bigint
  , constraint trn_work_kanban_working_pk primary key (work_kanban_working_id)
) ;

comment on table trn_work_kanban_working is '工程カンバン作業中リスト:どのカンバンがだれがどこでを管理している。
作業中のみを管理している。';
comment on column trn_work_kanban_working.work_kanban_working_id is '工程カンバン作業中リストID';
comment on column trn_work_kanban_working.work_kanban_id is '工程カンバンID';
comment on column trn_work_kanban_working.equipment_id is '設備ID';
comment on column trn_work_kanban_working.organization_id is '組織ID';


-- 月毎の trn_actual_result 子テーブルを作成する関数
CREATE OR REPLACE FUNCTION create_actual_result(IN TIMESTAMP) RETURNS VOID AS
  $$
    DECLARE
      begin_time TIMESTAMP; -- time の開始時刻
      expire_time TIMESTAMP; -- time の終了時刻
    BEGIN
      begin_time := date_trunc('month', $1);
      expire_time := begin_time + '1 month'::INTERVAL;
      EXECUTE 'CREATE TABLE IF NOT EXISTS '
              || 'trn_actual_result_'
              || to_char($1, 'YYYY"_"MM')
              || '('
              || 'LIKE trn_actual_result INCLUDING DEFAULTS INCLUDING INDEXES, '
              || 'CHECK('''
              || begin_time
              || ''' <= implement_datetime AND implement_datetime < '''
              || expire_time
              || ''')'
              || ') INHERITS (trn_actual_result)';
    END;
  $$
  LANGUAGE plpgsql
;


-- 新しいレコードを trn_actual_result 子テーブルに振り分ける関数
CREATE OR REPLACE FUNCTION insert_actual_result() RETURNS TRIGGER AS
  $$
    BEGIN
      LOOP
        BEGIN
          -- trn_actual_result 子テーブルに振り分ける
          EXECUTE 'INSERT INTO '
                  || 'trn_actual_result_'
                  || to_char(new.implement_datetime, 'YYYY"_"MM')
                  || ' VALUES(($1).*)' USING new;
          RETURN NULL;
        EXCEPTION WHEN undefined_table THEN
          -- trn_actual_result 子テーブルを作成
          PERFORM create_actual_result(new.implement_datetime);
        END;
      END LOOP;
    END;
  $$
  LANGUAGE plpgsql
;


-- trn_actual_result への insert 時に 子テーブルへの振り分けを行うためのトリガー
CREATE TRIGGER insert_actual_result_trigger
    BEFORE INSERT ON trn_actual_result
    FOR EACH ROW EXECUTE PROCEDURE insert_actual_result();


-- 月毎の trn_prod_result 子テーブルを作成する関数
CREATE OR REPLACE FUNCTION create_prod_result(IN TIMESTAMP) RETURNS VOID AS
  $$
    DECLARE
      begin_time TIMESTAMP; -- time の開始時刻
      expire_time TIMESTAMP; -- time の終了時刻
    BEGIN
      begin_time := date_trunc('month', $1);
      expire_time := begin_time + '1 month'::INTERVAL;
      EXECUTE 'CREATE TABLE IF NOT EXISTS '
              || 'trn_prod_result_'
              || to_char($1, 'YYYY"_"MM')
              || '('
              || 'LIKE trn_prod_result INCLUDING DEFAULTS INCLUDING INDEXES, '
              || 'CHECK('''
              || begin_time
              || ''' <= comp_datetime AND comp_datetime < '''
              || expire_time
              || ''')'
              || ') INHERITS (trn_prod_result)';
    END;
  $$
  LANGUAGE plpgsql
;


-- 新しいレコードを trn_prod_result 子テーブルに振り分ける関数
CREATE OR REPLACE FUNCTION insert_prod_result() RETURNS TRIGGER AS
  $$
    BEGIN
      LOOP
        BEGIN
          -- trn_prod_result 子テーブルに振り分ける
          EXECUTE 'INSERT INTO '
                  || 'trn_prod_result_'
                  || to_char(new.comp_datetime, 'YYYY"_"MM')
                  || ' VALUES(($1).*)' USING new;
          RETURN NULL;
        EXCEPTION WHEN undefined_table THEN
          -- trn_prod_result 子テーブルを作成
          PERFORM create_prod_result(new.comp_datetime);
        END;
      END LOOP;
    END;
  $$
  LANGUAGE plpgsql
;


-- trn_prod_result への insert 時に 子テーブルへの振り分けを行うためのトリガー
CREATE TRIGGER insert_prod_result_trigger
    BEFORE INSERT ON trn_prod_result
    FOR EACH ROW EXECUTE PROCEDURE insert_prod_result();


-- 工程実績ビュー
CREATE OR REPLACE VIEW view_actual_result AS
  SELECT
    -- 実績ID
    act.actual_id,
    -- カンバンID
    act.kanban_id AS fk_kanban_id,
    -- 工程順ID
    act.workflow_id AS fk_workflow_id,
    -- 工程カンバンID
    act.work_kanban_id AS fk_work_kanban_id,
    -- 工程ID
    act.work_id AS fk_work_id,
    -- 組織ID
    act.organization_id AS fk_organization_id,
    -- 設備ID
    act.equipment_id AS fk_equipment_id,
    -- カンバン名
    kan.kanban_name,
    -- 工程名
    wk.work_name,
    -- 組織名
    org.organization_name,
    -- 設備名
    eq.equipment_name,
    -- 実施日時
    act.implement_datetime,
    -- ステータス
    act.actual_status,
    -- 作業時間
    act.work_time,
    -- 中断理由
    act.interrupt_reason,
    -- 遅延理由
    act.delay_reason,
    -- モデル名
    kan.model_name

  FROM trn_actual_result act

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = act.work_id
  -- 組織を結合
  LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
  -- 設備を結合
  LEFT JOIN mst_equipment eq ON eq.equipment_id = act.equipment_id

  WHERE act.actual_status <> 'OTHER'
;

comment on view view_actual_result is '工程実績ビュー';
comment on column view_actual_result.actual_id is '実績ID';
comment on column view_actual_result.fk_kanban_id is 'カンバンID';
comment on column view_actual_result.fk_workflow_id is '工程順ID';
comment on column view_actual_result.fk_work_kanban_id is '工程カンバンID';
comment on column view_actual_result.fk_work_id is '工程ID';
comment on column view_actual_result.fk_organization_id is '組織ID';
comment on column view_actual_result.fk_equipment_id is '設備ID';
comment on column view_actual_result.kanban_name is 'カンバン名';
comment on column view_actual_result.work_name is '工程名';
comment on column view_actual_result.organization_name is '組織名';
comment on column view_actual_result.equipment_name is '設備名';
comment on column view_actual_result.implement_datetime is '実施日時';
comment on column view_actual_result.actual_status is 'ステータス';
comment on column view_actual_result.work_time is '作業時間';
comment on column view_actual_result.interrupt_reason is '中断理由';
comment on column view_actual_result.delay_reason is '遅延理由';
comment on column view_actual_result.model_name is 'モデル名';


-- カンバン別計画実績
CREATE VIEW view_kanban_topic AS
  SELECT
    -- カンバンID
    kan.kanban_id,
    -- 工程カンバンID
    COALESCE(act_info.work_kanban_id, 0) AS work_kanban_id,
    -- 組織ID
    COALESCE(act_info.last_actual_organization_id, 0) AS organization_id,
    -- カンバン名
    kan.kanban_name,
    -- カンバンステータス
    kan.kanban_status,
    -- 工程順名
    wf.workflow_name,
    -- モデル名
    kan.model_name,
    -- 工程名
    act_info.last_actual_work_name AS work_name,
    -- 工程カンバンステータス
    act_info.work_status AS work_kanban_status,
    -- 設備名
    act_info.last_actual_equipment_name AS equipment_name,
    -- 組織名
    act_info.last_actual_organization_name AS organization_name,
    -- カンバンの開始予定日時
    kan.start_datetime AS plan_start_time,
    -- カンバンの完了予定日時
    kan.comp_datetime AS plan_end_time,
    -- 工程カンバンの最初の実績日時
    act_info.actual_start_time AS actual_start_time,
    -- 工程カンバンの最後の実績日時
    act_info.actual_end_time AS actual_end_time,
    -- 工程の文字色
    act_info.font_color, 
    -- 工程の背景色
    act_info.back_color,
    -- 作業累計時間
    act_info.sum_times,
    -- タクトタイム
    act_info.takt_time,
    -- 工程ID
    act_info.work_id,
    -- 工程順の版数
    wf.workflow_rev,
    -- カンバンの開始予定日時
    kan.start_datetime AS kanban_plan_start_time,
    -- カンバンの完了予定日時
    kan.comp_datetime AS kanban_plan_end_time,
    -- カンバンの開始実績日時
    kan.actual_start_datetime AS kanban_actual_start_time,
    -- カンバンの完了実績日時
    kan.actual_comp_datetime AS kanban_actual_end_time,
    -- 工程カンバンの表示順
    NULL AS work_kanban_order

  FROM trn_kanban kan

  -- 工程順を結合
  LEFT JOIN mst_workflow wf ON wf.workflow_id = kan.workflow_id

  -- 実績情報を結合
  LEFT JOIN (
    SELECT
      -- 最後の実績のカンバンID
      last_act.kanban_id AS last_actual_kanban_id,
      -- 最後の実績の実績ID
      last_act.actual_id AS last_actual_id,
      -- 最後の実績のステータス
      last_act.actual_status AS last_actual_status,
      -- 最後の実績の工程名
      wk.work_name AS last_actual_work_name,
      -- 最後のの実績の組織ID
      org.organization_id AS last_actual_organization_id,
      -- 最後のの実績の組織名
      org.organization_name AS last_actual_organization_name,
      -- 最後のの実績の設備名
      eq.equipment_name AS last_actual_equipment_name,
      -- 最初の実績の日時
      first_act.first_actual_datetime AS actual_start_time,
      -- 最後の実績の日時
      last_act.implement_datetime AS actual_end_time,
      -- 工程カンバンID
      wkan.work_kanban_id AS work_kanban_id,
      -- 工程ステータス
      wkan.work_status,
      -- 工程カンバンの開始予定日時
      wkan.start_datetime work_kanban_start_datetime,
      -- 工程カンバンの終了予定日時
      wkan.comp_datetime work_kanban_comp_datetime,
      -- 工程の文字色 (最後(現在)の実績から)
      wk.font_color font_color, 
      -- 工程の背景色 (最後(現在)の実績から)
      wk.back_color back_color,
      -- 作業累計時間 (最後(現在)の実績から)
      wkan.sum_times sum_times,
      -- タクトタイム
      wkan.takt_time takt_time,
      -- 工程ID
      wk.work_id work_id

    -- カンバン毎の最後(現在)の実績
    FROM trn_actual_result last_act
    INNER JOIN (
      SELECT kanban_id, MAX(implement_datetime) last_actual_datetime
        FROM trn_actual_result
        GROUP BY kanban_id
    ) su ON su.kanban_id = last_act.kanban_id AND su.last_actual_datetime = last_act.implement_datetime

    -- カンバン毎の最初の実績時間を結合
    LEFT JOIN (
      SELECT kanban_id, MIN(implement_datetime) first_actual_datetime
        FROM trn_actual_result
        GROUP BY kanban_id
    ) first_act ON first_act.kanban_id = last_act.kanban_id

    -- 最後(現在)の実績の工程カンバンを結合
    LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = last_act.work_kanban_id
    -- 最後(現在)の実績の工程を結合
    LEFT JOIN mst_work wk ON wk.work_id = last_act.work_id
    -- 最後(現在)の実績の組織を結合
    LEFT JOIN mst_organization org ON org.organization_id = last_act.organization_id
    -- 最後(現在)の実績の設備を結合
    LEFT JOIN mst_equipment eq ON eq.equipment_id = last_act.equipment_id
) act_info ON act_info.last_actual_kanban_id = kan.kanban_id;

comment on view view_kanban_topic is 'カンバン別計画実績';
comment on column view_kanban_topic.kanban_id is 'カンバンID';
comment on column view_kanban_topic.work_kanban_id is '工程カンバンID';
comment on column view_kanban_topic.organization_id is '組織ID';
comment on column view_kanban_topic.kanban_name is 'カンバン名';
comment on column view_kanban_topic.kanban_status is 'カンバンステータス';
comment on column view_kanban_topic.workflow_name is '工程順名';
comment on column view_kanban_topic.model_name is 'モデル名';
comment on column view_kanban_topic.work_name is '工程名';
comment on column view_kanban_topic.work_kanban_status is '工程カンバンステータス';
comment on column view_kanban_topic.organization_name is '組織名';
comment on column view_kanban_topic.plan_start_time is 'カンバンの開始予定日時';
comment on column view_kanban_topic.plan_end_time is 'カンバンの完了予定日時';
comment on column view_kanban_topic.actual_start_time is '工程カンバンの最初の実績日時';
comment on column view_kanban_topic.actual_end_time is '工程カンバンの最後の実績日時';
comment on column view_kanban_topic.equipment_name is '設備名';
comment on column view_kanban_topic.font_color is '工程の文字色';
comment on column view_kanban_topic.back_color is '工程の背景色';
comment on column view_kanban_topic.sum_times is '作業累計時間';
comment on column view_kanban_topic.takt_time is 'タクトタイム';
comment on column view_kanban_topic.work_id is '工程ID';
comment on column view_kanban_topic.work_kanban_order is '工程カンバンの表示順';
comment on column view_kanban_topic.workflow_rev is '工程順の版数';
comment on column view_kanban_topic.kanban_plan_start_time is 'カンバンの開始予定日時';
comment on column view_kanban_topic.kanban_plan_end_time is 'カンバンの完了予定日時';
comment on column view_kanban_topic.kanban_actual_start_time is 'カンバンの開始実績日時';
comment on column view_kanban_topic.kanban_actual_end_time is 'カンバンの完了実績日時';


-- ライン生産情報ビュー
CREATE VIEW view_line_product AS
  SELECT
    -- カンバンID
    kan.kanban_id,
    -- 設備ID
    act_info.equipment_id,
    -- 完了日時
    act_info.actual_end_time,
    -- モデル名
    kan.model_name,
    -- ロット数量
    kan.lot_quantity

  FROM trn_kanban kan

  -- 実績情報を結合
  LEFT JOIN (
    SELECT
      -- カンバンID
      last_act.kanban_id last_actual_kanban_id,
      -- 設備ID
      last_act.equipment_id,
      -- 完了日時
      last_act.implement_datetime actual_end_time

    -- カンバン毎の最後の実績
    FROM trn_actual_result last_act
    INNER JOIN (
      SELECT kanban_id AS kanban_id, MAX(implement_datetime) last_actual_datetime
        FROM trn_actual_result
        GROUP BY kanban_id
    ) su ON su.kanban_id = last_act.kanban_id AND su.last_actual_datetime = last_act.implement_datetime

) act_info ON act_info.last_actual_kanban_id = kan.kanban_id
WHERE kanban_status = 'COMPLETION';

comment on view view_line_product is 'ライン生産情報';
comment on column view_line_product.kanban_id is 'カンバンID';
comment on column view_line_product.equipment_id is '設備ID';
comment on column view_line_product.actual_end_time is '完了日時';
comment on column view_line_product.model_name is 'モデル名';
comment on column view_line_product.lot_quantity is 'ロット数量';


-- 作業者別作業時間履歴ビュー (作業中の実績は含まれない)
CREATE OR REPLACE VIEW view_org_work_history AS
  SELECT
    -- 実績ID
    act.actual_id,
    -- カンバンID
    kan.kanban_id,
    -- 工程順ID
    wf.workflow_id,
    -- 工程カンバンID
    wkan.work_kanban_id,
    -- 工程ID
    wk.work_id,
    -- 組織ID
    org.organization_id,
    -- カンバン名
    kan.kanban_name,
    -- カンバンステータス
    kan.kanban_status,
    -- 工程順名
    wf.workflow_name,
    -- 工程名
    wk.work_name,
    -- 工程カンバンステータス
    wkan.work_status work_kanban_status,
    -- 組織名
    org.organization_name,
    -- 計画開始日時
    wkan.start_datetime plan_start_time,
    -- 計画完了日時
    wkan.comp_datetime plan_end_time,
    -- 工程開始日時
    wkan.actual_start_datetime work_start_time,
    -- 工程完了日時
    wkan.actual_comp_datetime work_end_time,
    -- 実績開始日時
    MAX(working_act.implement_datetime) actual_start_time,
    -- 実績完了日時
    act.implement_datetime actual_end_time,
    -- 工程の文字色
    wk.font_color,
    -- 工程の背景色
    wk.back_color,
    -- 作業累計時間
    wkan.sum_times,
    -- タクトタイム
    wkan.takt_time takt_time,
    -- 作業時間
    act.work_time

  FROM trn_actual_result act

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
  -- 工程順を結合
  LEFT JOIN mst_workflow wf ON wf.workflow_id = act.workflow_id
  -- 工程カンバンを結合
  LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = act.work_kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = act.work_id
  -- 組織を結合
  LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
  -- 実績完了日時
  LEFT JOIN trn_actual_result working_act ON (working_act.work_kanban_id = act.work_kanban_id AND working_act.implement_datetime < act.implement_datetime AND working_act.organization_id = act.organization_id)

  WHERE act.actual_status = 'COMPLETION' OR act.actual_status = 'SUSPEND'

  GROUP BY
    act.actual_id,
    kan.kanban_id,
    wf.workflow_id,
    wkan.work_kanban_id,
    wk.work_id,
    org.organization_id
;


-- 工程実績ビュー
CREATE OR REPLACE VIEW view_prod_result AS
  SELECT
    -- カンバン名
    kan.kanban_name,
    -- 工程名
    wk.work_name,
    -- 副番
    res.order_num,
    -- ユニークID
    res.unique_id,
    -- モデル名
    kan.model_name,
    -- 仕様1
    res.product_spec1,
    -- 仕様2
    res.product_spec2,
    -- 仕様3
    res.product_spec3,
    -- 処理区分
    res.status,
    -- 不良理由
    res.defect_type,
    -- 社員名
    org.organization_name,
    -- 社員コード
    org.organization_identify,
    -- サイクルタイム
    res.cycle_time,
    -- サイクルタイム(秒)
    res.cycle_sec,
    -- 完了日時
    res.comp_datetime

  FROM trn_prod_result res

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = res.fk_kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = res.fk_work_id
  -- 組織を結合
  LEFT JOIN mst_organization org ON org.organization_id = res.fk_organization_id
;

comment on view view_prod_result is '生産実績ビュー';
comment on column view_prod_result.kanban_name is 'カンバン名';
comment on column view_prod_result.work_name is '工程名';
comment on column view_prod_result.order_num is '副番';
comment on column view_prod_result.unique_id is 'ユニークID';
comment on column view_prod_result.model_name is 'モデル名';
comment on column view_prod_result.product_spec1 is '仕様1';
comment on column view_prod_result.product_spec2 is '仕様2';
comment on column view_prod_result.product_spec3 is '仕様3';
comment on column view_prod_result.status is '処理区分';
comment on column view_prod_result.defect_type is '不良理由';
comment on column view_prod_result.organization_name is '社員名';
comment on column view_prod_result.organization_identify is '社員コード';
comment on column view_prod_result.cycle_time is 'サイクルタイム';
comment on column view_prod_result.cycle_sec is 'サイクルタイム(秒)';
comment on column view_prod_result.comp_datetime is '完了日時';


-- 実績出力ビュー
CREATE VIEW view_report_out AS
  SELECT
    -- 工程実績ID
    act.actual_id,
    -- カンバン階層名
    kanh.hierarchy_name kanban_hierarchy_name,
    -- カンバンID
    act.kanban_id AS fk_kanban_id,
    -- カンバン名
    kan.kanban_name,
    -- サブカンバン名
    kan.kanban_subname,
    -- 工程順階層名
    wfh.hierarchy_name workflow_hierarchy_name,
    -- 工程順ID
    act.workflow_id AS fk_workflow_id,
    -- 工程順名
    wf.workflow_name,
    -- 工程順の版数
    wf.workflow_rev,
    -- 工程階層名
    wkh.hierarchy_name work_hierarchy_name,
    -- 工程ID
    act.work_id AS fk_work_id,
    -- 工程名
    wk.work_name,
    -- 工程カンバンID
    act.work_kanban_id AS fk_work_kanban_id,
    -- 追加工程フラグ
    wkan.separate_work_flag,
    -- スキップフラグ
    wkan.skip_flag,
    -- 親組織名
    p_org.organization_name AS parent_organization_name,
    -- 親組織識別名
    p_org.organization_identify AS parent_organization_identify,
    -- 組織ID
    act.organization_id AS fk_organization_id,
    -- 組織名
    org.organization_name,
    -- 組織識別名
    org.organization_identify,
    -- 親設備名
    p_eq.equipment_name AS parent_equipment_name,
    -- 親設備識別名
    p_eq.equipment_identify AS parent_equipment_identify,
    -- 設備ID
    act.equipment_id AS fk_equipment_id,
    -- 設備名
    eq.equipment_name,
    -- 設備識別名
    eq.equipment_identify,
    -- ステータス
    act.actual_status,
    -- 中断理由
    act.interrupt_reason,
    -- 遅延理由
    act.delay_reason,
    -- 実施時刻
    act.implement_datetime,
    -- タクトタイム
    wkan.takt_time,
    -- 作業時間
    act.work_time,
    -- モデル名
    kan.model_name,
    -- 完成数
    act.comp_num,
    -- 追加情報
    act.actual_add_info

  FROM trn_actual_result act

  -- カンバン情報を結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
  -- カンバン階層情報を結合
  LEFT JOIN con_kanban_hierarchy con_kanh ON con_kanh.kanban_id = kan.kanban_id
  LEFT JOIN mst_kanban_hierarchy kanh ON kanh.kanban_hierarchy_id = con_kanh.kanban_hierarchy_id
  -- 工程カンバン情報を結合
  LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = act.work_kanban_id
  -- 工程順情報を結合
  LEFT JOIN mst_workflow wf ON wf.workflow_id = act.workflow_id
  -- 工程順階層情報を結合
  LEFT JOIN con_hierarchy con_wfh ON con_wfh.hierarchy_type = 1 AND con_wfh.work_workflow_id = act.workflow_id
  LEFT JOIN mst_hierarchy wfh ON wfh.hierarchy_type = 1 AND wfh.hierarchy_id = con_wfh.hierarchy_id
  -- 工程情報を結合
  LEFT JOIN mst_work wk ON wk.work_id = act.work_id
  -- 工程階層情報を結合
  LEFT JOIN con_hierarchy con_wkh ON con_wkh.hierarchy_type = 0 AND con_wkh.work_workflow_id = act.workflow_id
  LEFT JOIN mst_hierarchy wkh ON wkh.hierarchy_type = 0 AND wkh.hierarchy_id = con_wkh.hierarchy_id
  -- 設備情報を結合
  LEFT JOIN mst_equipment eq ON eq.equipment_id = act.equipment_id
  -- 親設備情報を結合
  LEFT JOIN mst_equipment p_eq ON p_eq.equipment_id = eq.parent_equipment_id
  -- 組織情報を結合
  LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
  -- 親組織情報を結合
  LEFT JOIN mst_organization p_org ON p_org.organization_id = org.parent_organization_id
;

comment on view view_report_out is '実績出力';
comment on column view_report_out.actual_id is '工程実績ID';
comment on column view_report_out.kanban_hierarchy_name is 'カンバン階層名';
comment on column view_report_out.fk_kanban_id is 'カンバンID';
comment on column view_report_out.kanban_name is 'カンバン名';
comment on column view_report_out.kanban_subname is 'サブカンバン名';
comment on column view_report_out.workflow_hierarchy_name is '工程順階層名';
comment on column view_report_out.fk_workflow_id is '工程順ID';
comment on column view_report_out.workflow_name is '工程順名';
comment on column view_report_out.workflow_rev is '工程順の版数';
comment on column view_report_out.work_hierarchy_name is '工程階層名';
comment on column view_report_out.fk_work_id is '工程ID';
comment on column view_report_out.work_name is '工程名';
comment on column view_report_out.fk_work_kanban_id is '工程カンバンID';
comment on column view_report_out.separate_work_flag is '追加工程フラグ';
comment on column view_report_out.skip_flag is 'スキップフラグ';
comment on column view_report_out.parent_organization_name is '親組織名';
comment on column view_report_out.parent_organization_identify is '親組織識別名';
comment on column view_report_out.fk_organization_id is '組織ID';
comment on column view_report_out.organization_name is '組織名';
comment on column view_report_out.organization_identify is '組織識別名';
comment on column view_report_out.parent_equipment_name is '親設備名';
comment on column view_report_out.parent_equipment_identify is '親設備識別名';
comment on column view_report_out.fk_equipment_id is '設備ID';
comment on column view_report_out.equipment_name is '設備名';
comment on column view_report_out.equipment_identify is '設備識別名';
comment on column view_report_out.actual_status is 'ステータス';
comment on column view_report_out.interrupt_reason is '中断理由';
comment on column view_report_out.delay_reason is '遅延理由';
comment on column view_report_out.implement_datetime is '実施時刻';
comment on column view_report_out.takt_time is 'タクトタイム';
comment on column view_report_out.work_time is '作業時間';
comment on column view_report_out.model_name is 'モデル名';
comment on column view_report_out.comp_num is '完成数';


-- 作業履歴ビュー
CREATE OR REPLACE VIEW view_work_history AS
  SELECT
    -- 実績ID
    his.actual_id,
    -- カンバンID
    his.kanban_id,
    -- 工程順ID
    his.workflow_id,
    -- 工程カンバンID
    his.work_kanban_id,
    -- 工程ID
    his.work_id,
    -- 組織ID
    his.organization_id,
    -- カンバン名
    his.kanban_name,
    -- カンバンステータス
    kan.kanban_status,
    -- 工程順名
    his.workflow_name,
    -- モデル名
    kan.model_name,
    -- 工程名
    his.work_name,
    -- 工程カンバンステータス
    wkan.work_status AS work_kanban_status,
    -- 組織名
    his.organization_name,
    -- 計画開始日時
    wkan.start_datetime AS plan_start_time,
    -- 計画完了日時
    wkan.comp_datetime AS plan_end_time,
    -- 工程開始日時
    wkan.actual_start_datetime AS work_start_time,
    -- 工程完了日時
    wkan.actual_comp_datetime AS work_end_time,
    -- 実績開始日時
    his.implement_datetime AS actual_start_time,
    -- 実績完了日時
    his.actual_end_time,
    -- 工程の文字色
    wk.font_color,
    -- 工程の背景色
    wk.back_color,
    -- 作業累計時間
    wkan.sum_times,
    -- タクトタイム
    wkan.takt_time takt_time,
    -- 設備ID
    his.equipment_id AS fk_equipment_id

FROM (
  SELECT
    act.actual_id,
    act.kanban_id,
    act.workflow_id,
    act.work_kanban_id,
    act.work_id,
    act.organization_id,
    act.equipment_id,
    act.implement_datetime,
    act.kanban_name,
    act.workflow_name,
    act.work_name,
    act.organization_name,

    MIN(comp_act.implement_datetime) AS actual_end_time

  FROM (
    SELECT
      a1.actual_id,
      a1.kanban_id,
      a1.workflow_id,
      a1.work_kanban_id,
      a1.work_id,
      a1.organization_id,
      a1.equipment_id,
      a1.implement_datetime,
      a1.kanban_name,
      a1.workflow_name,
      a1.work_name,
      a1.organization_name
    FROM trn_actual_result a1
    WHERE a1.actual_status = 'WORKING'
  ) act

  LEFT JOIN (
    SELECT
      a2.actual_id,
      a2.work_kanban_id,
      a2.implement_datetime,
      a2.organization_id
    FROM trn_actual_result a2
  ) comp_act ON comp_act.work_kanban_id = act.work_kanban_id
    AND comp_act.implement_datetime >= act.implement_datetime
    AND comp_act.actual_id <> act.actual_id
    AND comp_act.organization_id = act.organization_id

  GROUP BY
    act.actual_id,
    act.kanban_id,
    act.workflow_id,
    act.work_kanban_id,
    act.work_id,
    act.organization_id,
    act.equipment_id,
    act.implement_datetime,
    act.kanban_name,
    act.workflow_name,
    act.work_name,
    act.organization_name
  ) his
  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = his.kanban_id
  -- 工程カンバンを結合
  LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = his.work_kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = his.work_id
;

comment on view view_work_history is '作業履歴ビュー';
comment on column view_work_history.actual_id is '実績ID';
comment on column view_work_history.kanban_id is 'カンバンID';
comment on column view_work_history.workflow_id is '工程順ID';
comment on column view_work_history.work_kanban_id is '工程カンバンID';
comment on column view_work_history.work_id is '工程ID';
comment on column view_work_history.organization_id is '組織ID';
comment on column view_work_history.kanban_name is 'カンバン名';
comment on column view_work_history.kanban_status is 'カンバンステータス';
comment on column view_work_history.workflow_name is '工程順名';
comment on column view_work_history.model_name is 'モデル名';
comment on column view_work_history.work_name is '工程名';
comment on column view_work_history.work_kanban_status is '工程カンバンステータス';
comment on column view_work_history.organization_name is '組織名';
comment on column view_work_history.plan_start_time is '計画開始日時';
comment on column view_work_history.plan_end_time is '計画完了日時';
comment on column view_work_history.work_start_time is '工程開始日時';
comment on column view_work_history.work_end_time is '工程完了日時';
comment on column view_work_history.actual_start_time is '実績開始日時';
comment on column view_work_history.actual_end_time is '実績完了日時';
comment on column view_work_history.font_color is '工程の文字色';
comment on column view_work_history.back_color is '工程の背景色';
comment on column view_work_history.sum_times is '作業累計時間';
comment on column view_work_history.takt_time is 'タクトタイム';
comment on column view_work_history.fk_equipment_id is '設備ID';


-- 工程カンバン別計画実績
CREATE VIEW view_work_kanban_topic AS
  SELECT
    -- カンバンID
    kan.kanban_id,
    -- 工程カンバンID
    wkan.work_kanban_id,
    -- 組織ID
    COALESCE(org.organization_id, 0) organization_id,
    -- カンバン名
    kan.kanban_name,
    -- カンバンステータス
    kan.kanban_status,
    -- 工程順名
    wf.workflow_name,
    -- モデル名
    kan.model_name,
    -- 工程名
    wk.work_name,
    -- 工程カンバンステータス
    wkan.work_status work_kanban_status,
    -- 組織名
    org.organization_name,
    -- 工程カンバンの開始予定日時
    wkan.start_datetime plan_start_time,
    -- 工程カンバンの完了予定日時
    wkan.comp_datetime plan_end_time,
    -- 工程カンバンの最初の実績日時
    wkan.actual_start_datetime actual_start_time,
    -- 工程カンバンの最後の実績日時
    wkan.actual_comp_datetime actual_end_time,
    -- 設備名
    NULL AS equipment_name,
    -- 工程の文字色
    wk.font_color,
    -- 工程の背景色
    wk.back_color,
    -- 作業累計時間
    wkan.sum_times,
    -- タクトタイム
    wkan.takt_time takt_time,
    -- 工程ID
    wk.work_id,
    -- 工程順の版数
    wf.workflow_rev,
    -- カンバンの開始予定日時
    kan.start_datetime kanban_plan_start_time,
    -- カンバンの完了予定日時
    kan.comp_datetime kanban_plan_end_time,
    -- カンバンの開始実績日時
    kan.actual_start_datetime kanban_actual_start_time,
    -- カンバンの完了実績日時
    kan.actual_comp_datetime kanban_actual_end_time,
    -- 工程カンバンの表示順
    wkan.work_kanban_order

  FROM trn_work_kanban wkan

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = wkan.kanban_id
  -- 工程順を結合
  LEFT JOIN mst_workflow wf ON wf.workflow_id = wkan.workflow_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = wkan.work_id
  -- 組織を結合
  LEFT JOIN con_workkanban_organization con_org ON con_org.workkanban_id = wkan.work_kanban_id

  LEFT JOIN mst_organization org ON org.organization_id = con_org.organization_id

  WHERE wkan.skip_flag = false;

comment on view view_work_kanban_topic is '工程カンバン別計画実績';
comment on column view_work_kanban_topic.kanban_id is 'カンバンID';
comment on column view_work_kanban_topic.work_kanban_id is '工程カンバンID';
comment on column view_work_kanban_topic.organization_id is '組織ID';
comment on column view_work_kanban_topic.kanban_name is 'カンバン名';
comment on column view_work_kanban_topic.kanban_status is 'カンバンステータス';
comment on column view_work_kanban_topic.workflow_name is '工程順名';
comment on column view_work_kanban_topic.model_name is 'モデル名';
comment on column view_work_kanban_topic.work_name is '工程名';
comment on column view_work_kanban_topic.work_kanban_status is '工程カンバンステータス';
comment on column view_work_kanban_topic.organization_name is '組織名';
comment on column view_work_kanban_topic.plan_start_time is '工程カンバンの開始予定日時';
comment on column view_work_kanban_topic.plan_end_time is '工程カンバンの完了予定日時';
comment on column view_work_kanban_topic.actual_start_time is '工程カンバンの最初の実績日時';
comment on column view_work_kanban_topic.actual_end_time is '工程カンバンの最後の実績日時';
comment on column view_work_kanban_topic.equipment_name is '設備名';
comment on column view_work_kanban_topic.font_color is '工程の文字色';
comment on column view_work_kanban_topic.back_color is '工程の背景色';
comment on column view_work_kanban_topic.sum_times is '作業累計時間';
comment on column view_work_kanban_topic.takt_time is 'タクトタイム';
comment on column view_work_kanban_topic.work_id is '工程ID';
comment on column view_work_kanban_topic.work_kanban_order is '工程カンバンの表示順';
comment on column view_work_kanban_topic.workflow_rev is '工程順の版数';
comment on column view_work_kanban_topic.kanban_plan_start_time is 'カンバンの開始予定日時';
comment on column view_work_kanban_topic.kanban_plan_end_time is 'カンバンの完了予定日時';
comment on column view_work_kanban_topic.kanban_actual_start_time is 'カンバンの開始実績日時';
comment on column view_work_kanban_topic.kanban_actual_end_time is 'カンバンの完了実績日時';


-- 作業日報ビュー (直接作業・間接作業・中断時間) の更新
CREATE OR REPLACE VIEW view_work_report AS 
  -- 日毎の作業者の直接工数 (工程・注番毎)
  SELECT
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    0 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    null AS indirect_actual_id,
    -- 作業ID (工程ID)
    act.work_id,
    -- 分類番号
    null AS class_number,
    -- 作業No
    wk.work_number,
    -- 作業内容
    wk.work_name AS work_name,
    -- 注文番号 (サブカンバン名)
    COALESCE (kan.kanban_subname, '') AS order_number,
    -- 工数(ms)
    SUM(act.work_time) AS work_time,
    -- 工程順ID
    act.workflow_id,
    -- カンバン名
    kan.kanban_name,
    -- モデル名
    kan.model_name,
    -- 実績数
    SUM(act.comp_num) AS actual_num,
    -- 作業種別の順
    1 AS work_type_order

  FROM trn_actual_result AS act
  LEFT JOIN mst_work AS wk ON wk.work_id = act.work_id
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  LEFT JOIN trn_kanban AS kan ON kan.kanban_id = act.kanban_id
  WHERE act.organization_id IS NOT NULL AND act.actual_status <> 'INTERRUPT'
  GROUP BY
    act.workflow_id,
    kan.kanban_name,
    kan.kanban_subname,
    kan.model_name,
    act.work_id,
    act.organization_id,
    wk.work_id,
    org.organization_id,
    work_date

  -- 日毎の作業者の間接工数 (間接作業毎) を結合
  UNION ALL
  SELECT 
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    1 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    act.indirect_actual_id,
    -- 作業ID
    act.indirect_work_id AS work_id,
    -- 分類番号
    wk.class_number,
    -- 作業No
    wk.work_number,
    -- 作業内容
    wk.work_name,
    -- 注文番号
    '' AS order_number,
    -- 工数(ms)
    SUM(act.work_time) AS work_time,
    -- 工程順ID
    -1 AS workflow_id,
    -- カンバン名
    '' AS kanban_name,
    -- モデル名
    '' AS model_name,
    -- 実績数
    0 AS actual_num,
    -- 作業種別の順
    3 AS work_type_order

  FROM trn_indirect_actual AS act
  LEFT JOIN mst_indirect_work AS wk ON wk.indirect_work_id = act.indirect_work_id
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  WHERE act.organization_id IS NOT NULL
  GROUP BY
    act.indirect_actual_id,
    wk.indirect_work_id,
    org.organization_id,
    work_date

  -- 日毎の作業者の中断時間 (中断理由毎) を結合
  UNION ALL
  SELECT
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    2 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    null AS indirect_actual_id,
    -- 作業ID (工程ID)
    null AS work_id,
    -- 分類番号
    null AS class_number,
    -- 作業No
    '' AS work_number,
    -- 作業内容 (中断理由)
    act.interrupt_reason AS work_name,
    -- 注文番号
    '' AS order_number,
    -- 工数(ms) (中断時間)
    SUM(act.non_work_time) AS work_time,
    -- 工程順ID
    null AS workflow_id,
    -- カンバン名
    '' AS kanban_name,
    -- モデル名
    '' AS model_name,
    -- 実績数 (中断回数)
    COUNT(act.actual_id) AS actual_num,
    -- 作業種別の順
    2 AS work_type_order

  FROM trn_actual_result AS act
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  WHERE act.organization_id IS NOT NULL
  AND act.non_work_time IS NOT NULL
  GROUP BY
    act.interrupt_reason,
    act.organization_id,
    org.organization_id,
    work_date
;

comment on view view_work_report is '作業日報';
comment on column view_work_report.work_type is '作業種別';
comment on column view_work_report.work_date is '作業日';
comment on column view_work_report.organization_id is '組織ID';
comment on column view_work_report.organization_identify is '組織識別名';
comment on column view_work_report.organization_name is '組織名';
comment on column view_work_report.indirect_actual_id is '間接工数実績ID';
comment on column view_work_report.work_id is '作業ID';
comment on column view_work_report.class_number is '分類番号';
comment on column view_work_report.work_number is '作業No';
comment on column view_work_report.work_name is '作業内容';
comment on column view_work_report.order_number is '注文番号';
comment on column view_work_report.work_time is '工数(ms)';
comment on column view_work_report.workflow_id is '工程順ID';
comment on column view_work_report.kanban_name is 'カンバン名';
comment on column view_work_report.model_name is 'モデル名';
comment on column view_work_report.actual_num is '実績数';
comment on column view_work_report.work_type_order is '作業種別の順';


-- 生産実績ビュー (カンバン/工程)
CREATE OR REPLACE VIEW view_work_result AS
  SELECT
    -- カンバン名
    kan.kanban_name,
    -- 工程名
    wk.work_name,
    -- モデル名
    kan.model_name,
    -- 仕様1
    pr.product_spec1,
    -- 仕様2
    pr.product_spec2,
    -- 仕様3
    pr.product_spec3,
    -- サイクルタイム(秒)
    gr.cycle_sec,
    -- 完了日時
    pr.comp_datetime

  FROM
  
  (SELECT
        r.fk_kanban_id, 
        r.fk_work_id,
        MIN(r.unique_id) unique_id,
	SUM(r.cycle_sec) cycle_sec
  FROM trn_prod_result r
  GROUP BY r.fk_kanban_id, r.fk_work_id
  ) gr

  -- 生産実績を結合
  LEFT JOIN trn_prod_result pr ON pr.fk_kanban_id = gr.fk_kanban_id AND pr.fk_work_id = gr.fk_work_id AND pr.unique_id = gr.unique_id
  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = gr.fk_kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = gr.fk_work_id
;

comment on view view_work_result is '生産実績ビュー (カンバン/工程)';
comment on column view_work_result.kanban_name is 'カンバン名';
comment on column view_work_result.work_name is '工程名';
comment on column view_work_result.product_spec1 is '仕様1';
comment on column view_work_result.product_spec2 is '仕様2';
comment on column view_work_result.product_spec3 is '仕様3';
comment on column view_work_result.cycle_sec is 'サイクルタイム(秒)';
comment on column view_work_result.comp_datetime is '完了日時';


-- 工程実績ビュー
CREATE OR REPLACE VIEW view_prod_result AS
  SELECT
    -- カンバン名
    kan.kanban_name,
    -- 工程名
    wk.work_name,
    -- 副番
    res.order_num,
    -- ユニークID
    res.unique_id,
    -- モデル名
    kan.model_name,
    -- 仕様1
    res.product_spec1,
    -- 仕様2
    res.product_spec2,
    -- 仕様3
    res.product_spec3,
    -- 処理区分
    res.status,
    -- 不良理由
    res.defect_type,
    -- 社員名
    org.organization_name,
    -- 社員コード
    org.organization_identify,
    -- サイクルタイム
    res.cycle_time,
    -- サイクルタイム(秒)
    res.cycle_sec,
    -- 完了日時
    res.comp_datetime

  FROM trn_prod_result res

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = res.fk_kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = res.fk_work_id
  -- 組織を結合
  LEFT JOIN mst_organization org ON org.organization_id = res.fk_organization_id
;

comment on view view_prod_result is '生産実績ビュー';
comment on column view_prod_result.kanban_name is 'カンバン名';
comment on column view_prod_result.work_name is '工程名';
comment on column view_prod_result.order_num is '副番';
comment on column view_prod_result.unique_id is 'ユニークID';
comment on column view_prod_result.model_name is 'モデル名';
comment on column view_prod_result.product_spec1 is '仕様1';
comment on column view_prod_result.product_spec2 is '仕様2';
comment on column view_prod_result.product_spec3 is '仕様3';
comment on column view_prod_result.status is '処理区分';
comment on column view_prod_result.defect_type is '不良理由';
comment on column view_prod_result.organization_name is '社員名';
comment on column view_prod_result.organization_identify is '社員コード';
comment on column view_prod_result.cycle_time is 'サイクルタイム';
comment on column view_prod_result.cycle_sec is 'サイクルタイム(秒)';
comment on column view_prod_result.comp_datetime is '完了日時';
