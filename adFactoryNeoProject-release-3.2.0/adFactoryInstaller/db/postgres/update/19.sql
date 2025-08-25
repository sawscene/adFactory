-- 「棚マスタ」テーブルにインデックスを追加する。
create index mst_location_area_name_idx
  on mst_location(area_name);

-- 「在庫マスタ」テーブルにインデックスを追加する。
create index mst_stock_location_product_idx
  on mst_stock(location_id,product_id);

-- 「資材情報」テーブルにインデックスを追加する。
create index trn_material_location_idx
  on trn_material(location_id);
