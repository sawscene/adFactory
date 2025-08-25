-- 出庫指示
ALTER TABLE trn_delivery ADD serial_start character varying (32)
, ADD serial_end character varying (32)
, ADD unit_code character varying (256)
, ADD unit_name character varying (256)
, ADD dest_name character varying (256)
, ADD status character varying (32)
, ADD delivery_date timestamp with time zone;

comment on column trn_delivery.serial_start is 'シリアル番号(開始)';
comment on column trn_delivery.serial_end is 'シリアル番号(終了)';
comment on column trn_delivery.unit_code is 'ユニットコード';
comment on column trn_delivery.unit_name is 'ユニット名';
comment on column trn_delivery.dest_name is '出庫先';
comment on column trn_delivery.status is 'ステータス';
comment on column trn_delivery.delivery_date is '出庫日';

-- 部品マスタ
ALTER TABLE mst_product ADD classify character varying(2);
comment on column mst_product.classify is '管理区分';
