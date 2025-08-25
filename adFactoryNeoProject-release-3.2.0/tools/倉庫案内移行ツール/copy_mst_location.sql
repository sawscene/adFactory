drop table if exists mst_location_old cascade;

create table mst_location_old (
  location_id bigserial not null
  , area_name character varying(32) not null
  , location_no character varying(32) not null
  , location_type integer
  , guide_order integer
  , location_spec character varying(256)
  , ver_info integer
  , create_date timestamp with time zone not null
  , update_date timestamp with time zone
  , inventory_flag boolean default false
  , constraint mst_location_old_pk primary key (location_id)
) ;

alter table mst_location_old add constraint mst_location_old_KEY01
  unique (area_name,location_no) ;

create index mst_location_old_area_name_idx
  on mst_location_old(area_name);

comment on table mst_location_old is '棚マスタ';
comment on column mst_location_old.location_id is '棚ID';
comment on column mst_location_old.area_name is '区画名';
comment on column mst_location_old.location_no is '棚番号';
comment on column mst_location_old.location_type is '棚種類:0： 通常保管棚
1： キット箱';
comment on column mst_location_old.guide_order is '案内順';
comment on column mst_location_old.location_spec is '指定部品:品番(カンマ区切り)';
comment on column mst_location_old.ver_info is '排他用バージョン';
comment on column mst_location_old.create_date is '作成日時';
comment on column mst_location_old.update_date is '更新日時';
comment on column mst_location_old.inventory_flag is '棚卸実施:true：棚卸作業可，false：棚卸作業不可';

-- インポート
COPY mst_location_old FROM 'C:\temp\20240618\new\public.mst_location.csv' (FORMAT CSV, HEADER true);


