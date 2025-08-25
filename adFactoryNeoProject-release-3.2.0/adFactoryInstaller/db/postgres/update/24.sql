ALTER TABLE mst_equipment ADD config jsonb; -- 設定
comment on column mst_equipment.config is '設定';

-- 「間接工数実績」テーブルにカラムを追加
ALTER TABLE trn_work_kanban
    ADD need_actual_output_flag boolean;

ALTER TABLE trn_work_kanban
    ADD actual_output_datetime timestamp;

ALTER TABLE trn_actual_result
    ADD rework_num integer;

comment on column trn_work_kanban.need_actual_output_flag is '要実績出力フラグ';
comment on column trn_work_kanban.actual_output_datetime is '実績出力日時';
comment on column trn_actual_result.rework_num is '作業やり直し回数';
