ALTER TABLE trn_direct_actual ADD class_key character varying(256) NOT NULL default ''; -- 分類キー
comment on column trn_direct_actual.class_key is '分類キー';
