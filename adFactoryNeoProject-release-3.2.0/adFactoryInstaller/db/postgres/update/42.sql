-- 理由区分テーブルの制約を変更
ALTER TABLE mst_reason_category 
DROP CONSTRAINT mst_reason_category_idx2;

ALTER TABLE mst_reason_category 
ADD CONSTRAINT mst_reason_category_idx UNIQUE (reason_type, reason_category_name);
