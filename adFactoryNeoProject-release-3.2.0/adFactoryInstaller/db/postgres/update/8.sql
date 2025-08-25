-- Project Name : adWarehouseDB
-- Date/Time    : 2020/07/23 17:38:01
-- Author       : ADTEK FUJI CO.,LTD.
-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2

/*
  BackupToTempTable, RestoreFromTempTable疑似命令が付加されています。
  これにより、drop table, create table 後もデータが残ります。
  この機能は一時的に $$TableName のような一時テーブルを作成します。
*/

-- 入出庫実績
--* BackupToTempTable
drop table if exists log_stock cascade;

--* RestoreFromTempTable
create table log_stock (
  event_id bigserial not null
  , event_kind smallint
  , material_no character varying(32)
  , supply_no character varying(32)
  , delivery_no character varying(32)
  , item_no integer
  , order_no character varying(32)
  , product_no character varying(32) not null
  , area_name character varying(32)
  , location_no character varying(32)
  , event_num integer not null
  , person_no character varying(256) not null
  , event_date timestamp with time zone not null
  , serial_no character varying(32)
  , category smallint
  , synced boolean default false
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint log_stock_pk primary key (event_id)
) ;

-- 部品構成マスタ
--* BackupToTempTable
drop table if exists mst_bom cascade;

--* RestoreFromTempTable
create table mst_bom (
  bom_id bigserial not null
  , parent_id bigint not null
  , child_id bigint not null
  , required_num integer default 1
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint mst_bom_pk primary key (bom_id)
) ;

create index mst_bom_IDX01
  on mst_bom(parent_id);

create index mst_bom_IDX02
  on mst_bom(child_id);

-- 棚マスタ
--* BackupToTempTable
drop table if exists mst_location cascade;

--* RestoreFromTempTable
create table mst_location (
  location_id bigserial not null
  , area_name character varying(32) not null
  , location_no character varying(32) not null
  , location_type integer
  , guide_order integer
  , location_spec character varying(256)
  , ver_info integer
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint mst_location_pk primary key (location_id)
) ;

alter table mst_location add constraint mst_location_KEY01
  unique (area_name,location_no) ;

-- 部品マスタ
--* BackupToTempTable
drop table if exists mst_product cascade;

--* RestoreFromTempTable
create table mst_product (
  product_id bigserial
  , product_no character varying(32) not null
  , product_name character varying(64)
  , important_rank smallint default 0
  , location jsonb
  , property jsonb
  , ver_info integer
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint mst_product_pk primary key (product_id)
) ;

create unique index mst_product_IDX01
  on mst_product(product_no);

-- 在庫マスタ
--* BackupToTempTable
drop table if exists mst_stock cascade;

--* RestoreFromTempTable
create table mst_stock (
  stock_id bigserial not null
  , location_id bigint not null
  , product_id bigint not null
  , stock_num integer default 0
  , stock_date timestamp with time zone
  , inventory_num integer
  , inventory_date timestamp with time zone
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint mst_stock_pk primary key (stock_id)
) ;

create index mst_stock_IDX01
  on mst_stock(location_id);

create index mst_stock_IDX02
  on mst_stock(product_id);

-- 出庫指示
--* BackupToTempTable
drop table if exists trn_delivery cascade;

--* RestoreFromTempTable
create table trn_delivery (
  delivery_no character varying(32) not null
  , order_no character varying(32)
  , serial_no character varying(32)
  , product_id bigint
  , due_date timestamp with time zone
  , ver_info integer
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint trn_delivery_pk primary key (delivery_no)
) ;

-- 出庫アイテム
--* BackupToTempTable
drop table if exists trn_delivery_item cascade;

--* RestoreFromTempTable
create table trn_delivery_item (
  delivery_no character varying(32) not null
  , item_no integer not null
  , material_no character varying(32)
  , order_no character varying(32)
  , serial_no character varying(32)
  , product_id bigint not null
  , required_num integer default 0 not null
  , due_date timestamp with time zone
  , delivery_num integer default 0
  , property jsonb
  , ver_info integer
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint trn_delivery_item_pk primary key (delivery_no,item_no)
) ;

create index trn_delivery_item_IDX01
  on trn_delivery_item(delivery_no);

-- 資材情報
--* BackupToTempTable
drop table if exists trn_material cascade;

--* RestoreFromTempTable
create table trn_material (
  material_no character varying(32) not null
  , supply_no character varying(32)
  , item_no integer
  , order_no character varying(32)
  , product_id bigint not null
  , location_id bigint
  , arrival_plan timestamp with time zone
  , arrival_num integer default 0
  , arrival_date timestamp with time zone
  , stock_num integer default 0
  , delivery_num integer default 0
  , in_stock_num integer default 0
  , stock_date timestamp with time zone
  , property jsonb
  , ver_info integer
  , serial_no character varying(32)
  , category smallint
  , branch_no integer default 0
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , constraint trn_material_pk primary key (material_no)
) ;

create unique index trn_material_IDX01
  on trn_material(supply_no);

comment on table log_stock is '入出庫実績';
comment on column log_stock.event_id is 'イベント番号';
comment on column log_stock.event_kind is 'イベント種別:10：受入、入庫
20:出庫
30:棚卸';
comment on column log_stock.material_no is '資材番号';
comment on column log_stock.supply_no is '納入番号:発注番号/注文番号';
comment on column log_stock.delivery_no is '出庫番号';
comment on column log_stock.item_no is '明細番号';
comment on column log_stock.order_no is '製造番号';
comment on column log_stock.product_no is '品目';
comment on column log_stock.area_name is '区画名';
comment on column log_stock.location_no is '棚番号';
comment on column log_stock.event_num is '数量';
comment on column log_stock.person_no is '社員番号';
comment on column log_stock.event_date is 'イベント日時';
comment on column log_stock.serial_no is 'シリアル番号:製品番号';
comment on column log_stock.category is '手配区分:1:支給品、2:購入品、3:加工品、9:不明';
comment on column log_stock.synced is '同期フラグ:true:同期済、false:未同期';
comment on column log_stock.create_date is '作成日時';
comment on column log_stock.update_date is '更新日時';

comment on table mst_bom is '部品構成マスタ';
comment on column mst_bom.bom_id is '部品構成ID';
comment on column mst_bom.parent_id is '親部品ID';
comment on column mst_bom.child_id is '子部品ID';
comment on column mst_bom.required_num is '要求数';
comment on column mst_bom.create_date is '作成日時';
comment on column mst_bom.update_date is '更新日時';

comment on table mst_location is '棚マスタ';
comment on column mst_location.location_id is '棚ID';
comment on column mst_location.area_name is '区画名';
comment on column mst_location.location_no is '棚番号';
comment on column mst_location.location_type is '棚種類:0： 通常保管棚
1： キット箱';
comment on column mst_location.guide_order is '案内順';
comment on column mst_location.location_spec is '指定部品:品番(カンマ区切り)';
comment on column mst_location.ver_info is '排他用バージョン';
comment on column mst_location.create_date is '作成日時';
comment on column mst_location.update_date is '更新日時';

comment on table mst_product is '部品マスタ';
comment on column mst_product.product_id is '部品ID';
comment on column mst_product.product_no is '品目';
comment on column mst_product.product_name is '品名';
comment on column mst_product.important_rank is '重要度ランク:0: 警告なし
1: 警告あり';
comment on column mst_product.location is '指定棚';
comment on column mst_product.property is 'プロパティ:メーカー名
仕様・規格
材質';
comment on column mst_product.ver_info is '排他用バージョン';
comment on column mst_product.create_date is '作成日時';
comment on column mst_product.update_date is '更新日時';

comment on table mst_stock is '在庫マスタ';
comment on column mst_stock.stock_id is '在庫ID';
comment on column mst_stock.location_id is '棚ID';
comment on column mst_stock.product_id is '部品ID';
comment on column mst_stock.stock_num is '在庫数';
comment on column mst_stock.stock_date is '最終入庫日時';
comment on column mst_stock.inventory_num is '棚卸在庫数';
comment on column mst_stock.inventory_date is '棚卸実施日';
comment on column mst_stock.create_date is '作成日時';
comment on column mst_stock.update_date is '更新日時';

comment on table trn_delivery is '出庫指示';
comment on column trn_delivery.delivery_no is '出庫番号';
comment on column trn_delivery.order_no is '製造番号:工事No';
comment on column trn_delivery.serial_no is 'シリアル番号';
comment on column trn_delivery.product_id is '部品ID:製品';
comment on column trn_delivery.due_date is '納期';
comment on column trn_delivery.ver_info is '排他用バージョン';
comment on column trn_delivery.create_date is '作成日時';
comment on column trn_delivery.update_date is '更新日時';

comment on table trn_delivery_item is '出庫アイテム';
comment on column trn_delivery_item.delivery_no is '出庫番号';
comment on column trn_delivery_item.item_no is '明細番号';
comment on column trn_delivery_item.material_no is '資材番号';
comment on column trn_delivery_item.order_no is '製造番号';
comment on column trn_delivery_item.serial_no is 'シリアル番号';
comment on column trn_delivery_item.product_id is '部品ID';
comment on column trn_delivery_item.required_num is '要求数';
comment on column trn_delivery_item.due_date is '納期';
comment on column trn_delivery_item.delivery_num is '出庫数:出庫済数';
comment on column trn_delivery_item.property is 'プロパティ';
comment on column trn_delivery_item.ver_info is '排他用バージョン';
comment on column trn_delivery_item.create_date is '作成日時';
comment on column trn_delivery_item.update_date is '更新日時';

comment on table trn_material is '資材情報';
comment on column trn_material.material_no is '資材番号:QRコード';
comment on column trn_material.supply_no is '納入番号:発注番号/注文番号';
comment on column trn_material.item_no is '明細番号';
comment on column trn_material.order_no is '製造番号:製番';
comment on column trn_material.product_id is '部品ID';
comment on column trn_material.location_id is '保管棚:棚ID';
comment on column trn_material.arrival_plan is '納入予定日';
comment on column trn_material.arrival_num is '納入予定数';
comment on column trn_material.arrival_date is '納入日';
comment on column trn_material.stock_num is '入庫数';
comment on column trn_material.delivery_num is '出庫数';
comment on column trn_material.in_stock_num is '在庫数';
comment on column trn_material.stock_date is '最終入庫日時';
comment on column trn_material.property is 'プロパティ';
comment on column trn_material.ver_info is '排他用バージョン';
comment on column trn_material.serial_no is 'シリアル番号';
comment on column trn_material.category is '手配区分:1:支給品、2:購入品、3:加工品、9:不明';
comment on column trn_material.branch_no is '末尾番号:資材を分割した時に採番される番号';
comment on column trn_material.create_date is '作成日時';
comment on column trn_material.update_date is '更新日時';

/* 
 * trn_material パーティションテーブル
 */

-- 月毎の trn_material 子テーブルを作成する関数
CREATE OR REPLACE FUNCTION create_material(timestamp with time zone) RETURNS VOID AS
  $$
    DECLARE
      begin_time TIMESTAMP; -- time の開始時刻
      expire_time TIMESTAMP; -- time の終了時刻
    BEGIN
      begin_time := date_trunc('month', $1);
      expire_time := begin_time + '1 month'::INTERVAL;
      EXECUTE 'CREATE TABLE IF NOT EXISTS '
              || 'trn_material_'
              || to_char($1, 'YYYY"_"MM')
              || '('
              || 'LIKE trn_material INCLUDING DEFAULTS INCLUDING INDEXES, '
              || 'CHECK('''
              || begin_time
              || ''' <= create_date AND create_date < '''
              || expire_time
              || ''')'
              || ') INHERITS (trn_material)';
    END;
  $$
  LANGUAGE plpgsql
;


-- 新しいレコードを trn_material 子テーブルに振り分ける関数
CREATE OR REPLACE FUNCTION insert_material() RETURNS TRIGGER AS
  $$
    BEGIN
      LOOP
        BEGIN
          -- trn_material 子テーブルに振り分ける
          EXECUTE 'INSERT INTO '
                  || 'trn_material_'
                  || to_char(new.create_date, 'YYYY"_"MM')
                  || ' VALUES(($1).*)' USING new;
          RETURN NULL;
        EXCEPTION WHEN undefined_table THEN
          -- trn_material 子テーブルを作成
          PERFORM create_material(new.create_date);
        END;
      END LOOP;
    END;
  $$
  LANGUAGE plpgsql
;


-- trn_material への insert 時に 子テーブルへの振り分けを行うためのトリガー
DROP TRIGGER IF EXISTS insert_material_trigger ON trn_material;
CREATE TRIGGER insert_material_trigger
    BEFORE INSERT ON trn_material
    FOR EACH ROW EXECUTE PROCEDURE insert_material();


/* 
 * trn_delivery パーティションテーブル
 */


-- 月毎の trn_delivery 子テーブルを作成する関数
CREATE OR REPLACE FUNCTION create_delivery(timestamp with time zone) RETURNS VOID AS
  $$
    DECLARE
      begin_time TIMESTAMP; -- time の開始時刻
      expire_time TIMESTAMP; -- time の終了時刻
    BEGIN
      begin_time := date_trunc('month', $1);
      expire_time := begin_time + '1 month'::INTERVAL;
      EXECUTE 'CREATE TABLE IF NOT EXISTS '
              || 'trn_delivery_'
              || to_char($1, 'YYYY"_"MM')
              || '('
              || 'LIKE trn_delivery INCLUDING DEFAULTS INCLUDING INDEXES, '
              || 'CHECK('''
              || begin_time
              || ''' <= create_date AND create_date < '''
              || expire_time
              || ''')'
              || ') INHERITS (trn_delivery)';
    END;
  $$
  LANGUAGE plpgsql
;


-- 新しいレコードを trn_delivery 子テーブルに振り分ける関数
CREATE OR REPLACE FUNCTION insert_delivery() RETURNS TRIGGER AS
  $$
    BEGIN
      LOOP
        BEGIN
          -- trn_delivery 子テーブルに振り分ける
          EXECUTE 'INSERT INTO '
                  || 'trn_delivery_'
                  || to_char(new.create_date, 'YYYY"_"MM')
                  || ' VALUES(($1).*)' USING new;
          RETURN NULL;
        EXCEPTION WHEN undefined_table THEN
          -- trn_delivery 子テーブルを作成
          PERFORM create_delivery(new.create_date);
        END;
      END LOOP;
    END;
  $$
  LANGUAGE plpgsql
;


-- trn_delivery への insert 時に 子テーブルへの振り分けを行うためのトリガー
DROP TRIGGER IF EXISTS insert_delivery_trigger ON trn_delivery;
CREATE TRIGGER insert_delivery_trigger
    BEFORE INSERT ON trn_delivery
    FOR EACH ROW EXECUTE PROCEDURE insert_delivery();


/* 
 * trn_delivery_item パーティションテーブル
 */


-- 月毎の trn_delivery_item 子テーブルを作成する関数
CREATE OR REPLACE FUNCTION create_delivery_item(timestamp with time zone) RETURNS VOID AS
  $$
    DECLARE
      begin_time TIMESTAMP; -- time の開始時刻
      expire_time TIMESTAMP; -- time の終了時刻
    BEGIN
      begin_time := date_trunc('month', $1);
      expire_time := begin_time + '1 month'::INTERVAL;
      EXECUTE 'CREATE TABLE IF NOT EXISTS '
              || 'trn_delivery_item_'
              || to_char($1, 'YYYY"_"MM')
              || '('
              || 'LIKE trn_delivery_item INCLUDING DEFAULTS INCLUDING INDEXES, '
              || 'CHECK('''
              || begin_time
              || ''' <= create_date AND create_date < '''
              || expire_time
              || ''')'
              || ') INHERITS (trn_delivery_item)';
    END;
  $$
  LANGUAGE plpgsql
;


-- 新しいレコードを trn_delivery_item 子テーブルに振り分ける関数
CREATE OR REPLACE FUNCTION insert_delivery_item() RETURNS TRIGGER AS
  $$
    BEGIN
      LOOP
        BEGIN
          -- trn_delivery_item 子テーブルに振り分ける
          EXECUTE 'INSERT INTO '
                  || 'trn_delivery_item_'
                  || to_char(new.create_date, 'YYYY"_"MM')
                  || ' VALUES(($1).*)' USING new;
          RETURN NULL;
        EXCEPTION WHEN undefined_table THEN
          -- trn_delivery_item 子テーブルを作成
          PERFORM create_delivery_item(new.create_date);
        END;
      END LOOP;
    END;
  $$
  LANGUAGE plpgsql
;


-- trn_delivery_item への insert 時に 子テーブルへの振り分けを行うためのトリガー
DROP TRIGGER IF EXISTS insert_delivery_item_trigger ON trn_delivery_item;
CREATE TRIGGER insert_delivery_item_trigger
    BEFORE INSERT ON trn_delivery_item
    FOR EACH ROW EXECUTE PROCEDURE insert_delivery_item();


/* 
 * log_stock パーティションテーブル
 */


-- 月毎の log_stock 子テーブルを作成する関数
CREATE OR REPLACE FUNCTION create_log_stock(timestamp with time zone) RETURNS VOID AS
  $$
    DECLARE
      begin_time TIMESTAMP; -- time の開始時刻
      expire_time TIMESTAMP; -- time の終了時刻
    BEGIN
      begin_time := date_trunc('month', $1);
      expire_time := begin_time + '1 month'::INTERVAL;
      EXECUTE 'CREATE TABLE IF NOT EXISTS '
              || 'log_stock_'
              || to_char($1, 'YYYY"_"MM')
              || '('
              || 'LIKE log_stock INCLUDING DEFAULTS INCLUDING INDEXES, '
              || 'CHECK('''
              || begin_time
              || ''' <= create_date AND create_date < '''
              || expire_time
              || ''')'
              || ') INHERITS (log_stock)';
    END;
  $$
  LANGUAGE plpgsql
;


-- 新しいレコードを log_stock 子テーブルに振り分ける関数
CREATE OR REPLACE FUNCTION insert_log_stock() RETURNS TRIGGER AS
  $$
    BEGIN
      LOOP
        BEGIN
          -- log_stock 子テーブルに振り分ける
          EXECUTE 'INSERT INTO '
                  || 'log_stock_'
                  || to_char(new.create_date, 'YYYY"_"MM')
                  || ' VALUES(($1).*)' USING new;
          RETURN NULL;
        EXCEPTION WHEN undefined_table THEN
          -- log_stock 子テーブルを作成
          PERFORM create_log_stock(new.create_date);
        END;
      END LOOP;
    END;
  $$
  LANGUAGE plpgsql
;


-- log_stock への insert 時に 子テーブルへの振り分けを行うためのトリガー
DROP TRIGGER IF EXISTS insert_log_stock_trigger ON log_stock;
CREATE TRIGGER insert_log_stock_trigger
    BEFORE INSERT ON log_stock
    FOR EACH ROW EXECUTE PROCEDURE insert_log_stock();
