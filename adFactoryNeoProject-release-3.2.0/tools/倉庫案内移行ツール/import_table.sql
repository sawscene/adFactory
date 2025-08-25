-- 倉庫案内のデータをインポートする

-- 対象テーブルのデータ削除
DELETE FROM log_stock;
DELETE FROM mst_bom;
DELETE FROM mst_location;
DELETE FROM mst_product;
DELETE FROM mst_stock;
DELETE FROM trn_delivery;
DELETE FROM trn_delivery_item;
DELETE FROM trn_material;

-- log_stock
COPY log_stock FROM 'C:\temp\log_stock.csv' (FORMAT CSV, HEADER true);

-- mst_bom
COPY mst_bom FROM 'C:\temp\mst_bom.csv' (FORMAT CSV, HEADER true);

-- mst_location
COPY mst_location FROM 'C:\temp\mst_location.csv' (FORMAT CSV, HEADER true);

-- mst_product
COPY mst_product(product_id,product_no,product_name,important_rank,location,property,ver_info,create_date,update_date,figure_no) FROM 'C:\temp\mst_product.csv' (FORMAT CSV, HEADER true);

-- mst_stock
COPY mst_stock FROM 'C:\temp\mst_stock.csv' (FORMAT CSV, HEADER true);

-- trn_delivery
COPY trn_delivery(delivery_no,order_no,serial_no,product_id,due_date,ver_info,create_date,update_date) FROM 'C:\temp\trn_delivery.csv' (FORMAT CSV, HEADER true);

-- trn_delivery_item
COPY trn_delivery_item(delivery_no,item_no,material_no,order_no,serial_no,product_id,required_num,due_date,delivery_num,property,ver_info,create_date,update_date
) FROM 'C:\temp\trn_delivery_item.csv' (FORMAT CSV, HEADER true);

-- trn_lot_trace
-- 遘ｻ陦後↑縺・

-- trn_material
COPY trn_material FROM 'C:\temp\trn_material.csv' (FORMAT CSV, HEADER true);

