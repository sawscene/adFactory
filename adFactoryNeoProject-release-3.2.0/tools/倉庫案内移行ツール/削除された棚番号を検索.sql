SELECT * FROM trn_material m
WHERE m.location_id IS NOT NULL AND m.in_stock_num > 0 
    AND NOT EXISTS (SELECT 1 FROM mst_location l WHERE l.location_id = m.location_id);
