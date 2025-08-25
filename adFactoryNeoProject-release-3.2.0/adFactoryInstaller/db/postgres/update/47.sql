-- 部品マスタ
ALTER TABLE mst_product
  ADD IF NOT EXISTS unit character varying(32);
comment on column mst_product.unit is '単位';

-- 入出庫実績
ALTER TABLE log_stock 
  ADD IF NOT EXISTS parts_no character varying(32),
  ADD IF NOT EXISTS note character varying(256);
comment on column log_stock.parts_no is '部品番号';
comment on column log_stock.note is 'コメント';

-- 資材情報
ALTER TABLE trn_material 
  ADD IF NOT EXISTS defect_num integer,
  ADD IF NOT EXISTS inspected_at timestamp with time zone;
comment on column trn_material.defect_num is '不適合数';
comment on column trn_material.inspected_at is '検査実施日時';

-- BOMマスタ
ALTER TABLE mst_bom ADD IF NOT EXISTS unit_no character varying(64);
comment on column mst_bom.unit_no is 'ユニット番号';

-- 出庫指示
ALTER TABLE trn_delivery
  DROP IF EXISTS unit_code,
  ADD IF NOT EXISTS model_name character varying(256),
  ADD IF NOT EXISTS unit_no character varying(64),
  ADD IF NOT EXISTS delivery_rule integer,
  ADD IF NOT EXISTS product_num integer;
comment on column trn_delivery.order_no is '製造番号';
comment on column trn_delivery.serial_start is '開始シリアル';
comment on column trn_delivery.serial_end is '終了シリアル';
comment on column trn_delivery.model_name is '機種名';
comment on column trn_delivery.unit_no is 'ユニット番号';
comment on column trn_delivery.delivery_rule is '出庫ルール';
comment on column trn_delivery.product_id is '製品ID';
comment on column trn_delivery.product_num is '製品数';

-- 出庫品目
ALTER TABLE trn_delivery_item
  ADD IF NOT EXISTS location_no character varying(32);
comment on column trn_delivery_item.location_no is '棚番号';


-- 在庫引当
--* BackupToTempTable
drop table if exists trn_reserve_material cascade;

--* RestoreFromTempTable
create table trn_reserve_material (
  delivery_no character varying(32) not null
  , item_no integer not null
  , material_no character varying(32) not null
  , reserved_num integer not null
  , reserved_at timestamp with time zone not null
  , person_no character varying(256) not null
  , delivery_num integer default 0
  , ver_info integer
  , constraint trn_reserve_material_pk primary key (delivery_no,item_no,material_no)
) ;

comment on table trn_reserve_material is '在庫引当';
comment on column trn_reserve_material.delivery_no is '出庫番号';
comment on column trn_reserve_material.item_no is '明細番号';
comment on column trn_reserve_material.material_no is '資材番号';
comment on column trn_reserve_material.reserved_num is '引当数';
comment on column trn_reserve_material.reserved_at is '引当日時';
comment on column trn_reserve_material.person_no is '社員番号';
comment on column trn_reserve_material.delivery_num is '出庫数';
comment on column trn_reserve_material.ver_info is '排他用バージョン';
