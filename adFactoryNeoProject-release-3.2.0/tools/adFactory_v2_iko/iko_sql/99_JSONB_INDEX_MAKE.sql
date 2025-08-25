

--　JSONB　　インデックス作成


drop index mst_work_idx_jsn_work_check_info_key;
CREATE INDEX mst_work_idx_jsn_work_check_info_key ON mst_work USING  gin (work_check_info);


drop index mst_work_idx_jsn_work_add_info_key;
CREATE INDEX mst_work_idx_jsn_work_add_info_key ON mst_work USING  gin (work_check_info);




