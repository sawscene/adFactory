-- カンバン
ALTER TABLE trn_kanban
  ADD IF NOT EXISTS cycle_time integer;
comment on column trn_kanban.cycle_time is '標準サイクルタイム';
