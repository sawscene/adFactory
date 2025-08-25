-- 品番マスタ
--* BackupToTempTable
drop table if exists mst_dsitem cascade;

--* RestoreFromTempTable
create table mst_dsitem (
  product_id bigserial
  , category integer not null
  , product_no character varying(2568) not null
  , product_name character varying(256)
  , spec character varying(256)
  , unit character varying(32)
  , location1 character varying(32)
  , location2 character varying(32)
  , bom jsonb
  , workflow1 bigint
  , workflow2 bigint
  , property jsonb
  , update_person_id bigint
  , update_datetime timestamp with time zone
  , ver_info integer default 1
  , constraint mst_dsitem_pk primary key (product_id)
) ;

create unique index mst_dsitem_IDX01
  on mst_dsitem(category,product_no);

comment on table mst_dsitem is '品番マスタ:デンソー高棚様';
comment on column mst_dsitem.product_id is '部品ID';
comment on column mst_dsitem.category is '区分:1: 補給生産、2:検査';
comment on column mst_dsitem.product_no is '品番';
comment on column mst_dsitem.product_name is '品名';
comment on column mst_dsitem.spec is '型式・仕様:車種・タイプ';
comment on column mst_dsitem.unit is '単位';
comment on column mst_dsitem.location1 is 'ロケーション1:組付治具台車';
comment on column mst_dsitem.location2 is 'ロケーション2:検査治具台車';
comment on column mst_dsitem.bom is '構成部品';
comment on column mst_dsitem.workflow1 is '工程順1:組付工程';
comment on column mst_dsitem.workflow2 is '工程順2:検査工程';
comment on column mst_dsitem.property is 'プロパティ';
comment on column mst_dsitem.update_person_id is '更新者';
comment on column mst_dsitem.update_datetime is '更新日時';
comment on column mst_dsitem.ver_info is '排他用バージョン';
