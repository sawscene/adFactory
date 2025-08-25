ALTER TABLE trn_kanban ADD comp_num integer default 0; -- 完成数
comment on column trn_kanban.comp_num is '完成数';

