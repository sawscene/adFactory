-- 「ロットトレース」テーブルにカラムを追加する。
ALTER TABLE trn_lot_trace ADD kanban_name character varying(256); -- カンバン名
ALTER TABLE trn_lot_trace ADD model_name character varying(256); -- モデル名
ALTER TABLE trn_lot_trace ADD work_name character varying(256); -- 工程名
ALTER TABLE trn_lot_trace ADD person_name character varying(256); -- 作業者
ALTER TABLE trn_lot_trace ADD assembly_datetime timestamp without time zone; -- 組付け日時
ALTER TABLE trn_lot_trace ADD disabled boolean default false;

comment on column trn_lot_trace.kanban_name is 'カンバン名';
comment on column trn_lot_trace.model_name is '機種名';
comment on column trn_lot_trace.work_name is '工程名';
comment on column trn_lot_trace.person_name is '作業者';
comment on column trn_lot_trace.assembly_datetime is '組付け日時';
comment on column trn_lot_trace.disabled is '追跡無効';
