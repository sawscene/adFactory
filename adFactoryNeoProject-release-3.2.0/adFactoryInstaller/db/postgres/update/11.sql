-- ラベルマスタ
create table mst_label (
  label_id bigserial not null
  , label_name character varying(128) not null
  , font_color character varying(8)
  , back_color character varying(8)
  , label_priority integer
  , ver_info integer default 1 not null
  , constraint mst_label_pk primary key (label_id)
) ;

alter table mst_label add constraint mst_label_idx2
  unique (label_name) ;

comment on table mst_label is 'ラベルマスタ';
comment on column mst_label.label_id is 'ラベルID';
comment on column mst_label.label_name is 'ラベル名';
comment on column mst_label.font_color is '文字色:HTMLカラーコード(16進数)';
comment on column mst_label.back_color is '背景色:HTMLカラーコード(16進数)';
comment on column mst_label.label_priority is '優先度';
comment on column mst_label.ver_info is '排他用バージョン';

-- カンバン
ALTER TABLE trn_kanban ADD kanban_label jsonb; -- ラベル
comment on column trn_kanban.kanban_label is 'ラベル';

ALTER TABLE trn_kanban ADD customer_name character varying(256); -- 顧客名
comment on column trn_kanban.customer_name is '顧客名';

-- 工程マスタ
ALTER TABLE mst_work ADD display_items jsonb; -- 表示項目
comment on column mst_work.display_items is '表示項目';
