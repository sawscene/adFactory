CREATE TABLE trn_product_plan
(
    product_plan_id    bigserial,
    comp_datetime      timestamp,
    item_name          varchar(128),
    item_code          varchar(128),
    nick_name          varchar(128),
    work_num           varchar(128),
    work_code          varchar(128),
    equipment_identify varchar(256),
    assert_num         varchar(128),
    production_num     integer,
    segment            varchar(128)
);

comment on table trn_product_plan is '生産計画情報';
comment on column trn_product_plan.product_plan_id is '生産計画情報ID';
comment on column trn_product_plan.comp_datetime is '完了予定日時';
comment on column trn_product_plan.item_name is '品名';
comment on column trn_product_plan.item_code is '品目';
comment on column trn_product_plan.nick_name is 'ニックネーム';
comment on column trn_product_plan.work_num is '工程番号';
comment on column trn_product_plan.work_code is '工程コード';
comment on column trn_product_plan.equipment_identify is '設備識別名';
comment on column trn_product_plan.assert_num is '資産番号';
comment on column trn_product_plan.production_num is '数量';
comment on column trn_product_plan.segment is 'セグメント';

-- 「生産計画情報」テーブルの「ID」のデフォルト値を変更。(自動採番)
CREATE SEQUENCE trn_product_plan_product_plan_id_seq;
ALTER TABLE trn_product_plan ALTER COLUMN product_plan_id SET DEFAULT nextval('trn_product_plan_product_plan_id_seq'::regclass);

ALTER TABLE trn_actual_result ADD remove_flag boolean default false; -- 論理削除フラグ
