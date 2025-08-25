-- 倉庫案内のデータをエクスポートする

-- log_stock
COPY (SELECT * FROM log_stock ORDER BY event_id) TO 'C:\temp\log_stock.csv' WITH CSV HEADER;

-- mst_bom
COPY (SELECT * FROM mst_bom ORDER BY bom_id) TO 'C:\temp\mst_bom.csv' WITH CSV HEADER;

-- mst_location
COPY (SELECT * FROM mst_location ORDER BY location_id) TO 'C:\temp\mst_location.csv' WITH CSV HEADER;

-- mst_product
COPY (SELECT * FROM mst_product ORDER BY product_id) TO 'C:\temp\mst_product.csv' WITH CSV HEADER;

-- mst_stock
COPY (SELECT * FROM mst_stock ORDER BY stock_id) TO 'C:\temp\mst_stock.csv' WITH CSV HEADER;

-- trn_delivery
COPY (SELECT * FROM trn_delivery) TO 'C:\temp\trn_delivery.csv' WITH CSV HEADER;

-- trn_delivery_item
COPY (SELECT * FROM trn_delivery_item) TO 'C:\temp\trn_delivery_item.csv' WITH CSV HEADER;

-- trn_lot_trace
-- 縺ｪ縺・

-- trn_material
COPY (SELECT * FROM trn_material) TO 'C:\temp\trn_material.csv' WITH CSV HEADER;
