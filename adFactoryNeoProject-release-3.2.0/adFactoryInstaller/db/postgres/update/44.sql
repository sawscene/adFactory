-- 理由区分マスタにデフォルト理由区分を追加
ALTER TABLE mst_reason_category ADD default_reason_category boolean DEFAULT false;
comment on column mst_reason_category.default_reason_category is 'デフォルト理由区分';


-- 理由区分マスタのデフォルト理由区分を更新
UPDATE mst_reason_category 
SET
    default_reason_category = true 
WHERE
    reason_category_name = 'default';


-- 理由区分テーブルにdefaultを挿入
INSERT INTO mst_reason_category(reason_type, reason_category_name, default_reason_category) 
SELECT 0, 'default', true WHERE NOT EXISTS (SELECT 1 FROM mst_reason_category WHERE reason_type = 0 AND default_reason_category = true);

INSERT INTO mst_reason_category(reason_type, reason_category_name, default_reason_category) 
SELECT 1, 'default', true WHERE NOT EXISTS (SELECT 1 FROM mst_reason_category WHERE reason_type = 1 AND default_reason_category = true);

INSERT INTO mst_reason_category(reason_type, reason_category_name, default_reason_category) 
SELECT 2, 'default', true WHERE NOT EXISTS (SELECT 1 FROM mst_reason_category WHERE reason_type = 2 AND default_reason_category = true);

INSERT INTO mst_reason_category(reason_type, reason_category_name, default_reason_category) 
SELECT 3, 'default', true WHERE NOT EXISTS (SELECT 1 FROM mst_reason_category WHERE reason_type = 3 AND default_reason_category = true);


-- 理由テーブルの理由区分を更新
UPDATE mst_reason 
SET
    reason_category_id = cat.reason_category_id 
FROM
    mst_reason_category cat 
WHERE
    mst_reason.reason_category_id IS NULL 
    AND mst_reason.reason_type = cat.reason_type 
    AND cat.reason_category_name = 'default';

