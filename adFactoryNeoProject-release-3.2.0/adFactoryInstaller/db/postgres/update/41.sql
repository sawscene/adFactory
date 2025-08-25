-- 帳票マスタ
ALTER TABLE con_workflow_work ADD schedule_info JSONB;

comment on column con_workflow_work.schedule_info is 'スケジュール';


