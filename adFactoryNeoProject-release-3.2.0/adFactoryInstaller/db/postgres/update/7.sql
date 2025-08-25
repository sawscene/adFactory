-- 「役割権限マスタ」テーブルに「承認権限」を追加する。
ALTER TABLE mst_role_authority ADD approve boolean default false; -- 承認権限
comment on column mst_role_authority.approve is '承認権限';

-- 「カンバン」テーブルに「承認」を追加する。
ALTER TABLE trn_kanban ADD approval jsonb; -- 承認
comment on column trn_kanban.approval is '承認';

-- 「カンバン帳票」テーブルを追加する。
CREATE TABLE trn_kanban_report (
  kanban_report_id bigserial not null
  , kanban_id bigint not null
  , template_name character varying(256)
  , output_datetime timestamp without time zone
  , file_path text
  , report_type integer
  , constraint trn_kanban_report_pk primary key (kanban_report_id)
) ;

CREATE INDEX idx_kanban_report_kanban_id
  ON trn_kanban_report(kanban_id);

comment on table trn_kanban_report is 'カンバン帳票';
comment on column trn_kanban_report.kanban_report_id is 'カンバン帳票ID';
comment on column trn_kanban_report.kanban_id is 'カンバンID';
comment on column trn_kanban_report.template_name is 'テンプレートファイル名';
comment on column trn_kanban_report.output_datetime is '出力日時';
comment on column trn_kanban_report.file_path is 'ファイルパス';
comment on column trn_kanban_report.report_type is '帳票種別';
