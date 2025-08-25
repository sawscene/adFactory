-- 「資材情報」テーブルにカラムを追加する。
ALTER TABLE trn_material ADD inventory_num integer; -- 棚卸在庫数
ALTER TABLE trn_material ADD inventory_location_id bigint; -- 棚番訂正
ALTER TABLE trn_material ADD inventory_date timestamp with time zone; -- 棚卸実施日
ALTER TABLE trn_material ADD inventory_person_no character varying(256); -- 棚卸実施者
ALTER TABLE trn_material ADD inventory_flag boolean default false; -- 棚卸実施
ALTER TABLE trn_material ADD inventory_confirm boolean default false; -- 棚卸確認

comment on column trn_material.inventory_num is '棚卸在庫数';
comment on column trn_material.inventory_location_id is '棚番訂正';
comment on column trn_material.inventory_date is '棚卸実施日';
comment on column trn_material.inventory_person_no is '棚卸実施者';
comment on column trn_material.inventory_flag is '棚卸実施';
comment on column trn_material.inventory_confirm is '棚卸確認';

-- 「棚マスタ」テーブルにカラムを追加する。
ALTER TABLE mst_location ADD inventory_flag boolean default false; -- 棚卸実施

comment on column mst_location.inventory_flag is '棚卸実施';

-- 「入出庫実績」テーブルにカラムを追加する。
ALTER TABLE log_stock ADD in_stock_num integer default 0; -- 在庫数

comment on column log_stock.in_stock_num is '在庫数';

