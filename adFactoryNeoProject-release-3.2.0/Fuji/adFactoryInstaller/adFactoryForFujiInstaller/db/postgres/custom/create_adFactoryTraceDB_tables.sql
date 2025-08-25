\encoding UTF8;

-- DBバージョン
create table t_ver (
  sid numeric(1,0) not null
  , verno character varying(20)
  , constraint t_ver_pk primary key (sid)
) ;

comment on table t_ver is 'DBバージョン';
comment on column t_ver.sid is 'ID';
comment on column t_ver.verno is 'DBバージョン';

-- トレーサビリティ
create table trn_traceability (
  kanban_id bigint not null
  , kanban_name character varying(256)
  , model_name character varying(256)
  , workflow_name character varying(256)
  , workflow_rev integer
  , work_kanban_id bigint not null
  , actual_id bigint not null
  , trace_name character varying(256)
  , trace_order integer
  , lower_limit double precision
  , upper_limit double precision
  , trace_value text
  , trace_confirm boolean
  , equipment_name character varying(256)
  , organization_name character varying(256)
  , implement_datetime timestamp without time zone
  , trace_tag character varying(256)
  , trace_props json
  , latest_flag boolean
) ;

create index idx_traceability_kanban_id
  on trn_traceability using btree (kanban_id);

create index idx_traceability_work_kanban_id
  on trn_traceability using btree (work_kanban_id);

comment on table trn_traceability is 'トレーサビリティ';
comment on column trn_traceability.kanban_id is 'カンバンID';
comment on column trn_traceability.kanban_name is 'カンバン名';
comment on column trn_traceability.model_name is 'モデル名';
comment on column trn_traceability.workflow_name is '工程順名';
comment on column trn_traceability.workflow_rev is '版数';
comment on column trn_traceability.work_kanban_id is '工程カンバンID';
comment on column trn_traceability.actual_id is '工程実績ID';
comment on column trn_traceability.trace_name is '項目名';
comment on column trn_traceability.trace_order is '順';
comment on column trn_traceability.lower_limit is '規格下限';
comment on column trn_traceability.upper_limit is '規格上限';
comment on column trn_traceability.trace_value is '値';
comment on column trn_traceability.trace_confirm is '確認';
comment on column trn_traceability.equipment_name is '設備名';
comment on column trn_traceability.organization_name is '組織名';
comment on column trn_traceability.implement_datetime is '作業日時';
comment on column trn_traceability.trace_tag is 'タグ';
comment on column trn_traceability.trace_props is '追加トレーサビリティ';
comment on column trn_traceability.latest_flag is '最終フラグ';

-- t_verにデータを追加
INSERT INTO t_ver (sid, verno)
SELECT 1, '0'
WHERE NOT EXISTS (SELECT sid FROM t_ver WHERE sid = 1);
