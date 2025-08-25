-- 不要なインデックスを削除
DROP INDEX IF EXISTS t_ver_ix2;

-- 不要なユニーク制約を削除
ALTER TABLE IF EXISTS con_unit_hierarchy DROP CONSTRAINT IF EXISTS con_unit_hierarchy_ix1;
ALTER TABLE IF EXISTS con_unit_template_hierarchy DROP CONSTRAINT IF EXISTS con_unit_template_hierarchy_ix1;
ALTER TABLE IF EXISTS mst_unit_hierarchy DROP CONSTRAINT IF EXISTS mst_unit_hierarchy_ix1;
ALTER TABLE IF EXISTS mst_unit_template DROP CONSTRAINT IF EXISTS mst_unit_template_ix1;
ALTER TABLE IF EXISTS mst_unit_template_hierarchy DROP CONSTRAINT IF EXISTS mst_unit_template_hierarchy_ix1;
ALTER TABLE IF EXISTS mst_unit_template_property DROP CONSTRAINT IF EXISTS mst_unit_template_property_ix1;
ALTER TABLE IF EXISTS t_ver DROP CONSTRAINT IF EXISTS t_ver_ix1;
ALTER TABLE IF EXISTS tre_unit_hierarchy DROP CONSTRAINT IF EXISTS tre_unit_hierarchy_ix1;
ALTER TABLE IF EXISTS tre_unit_template_hierarchy DROP CONSTRAINT IF EXISTS tre_unit_template_hierarchy_ix1;
ALTER TABLE IF EXISTS trn_unit DROP CONSTRAINT IF EXISTS trn_unit_ix1;
ALTER TABLE IF EXISTS trn_unit_property DROP CONSTRAINT IF EXISTS trn_unit_property_ix1;

-- t_verを更新
UPDATE t_ver SET verno='5' WHERE sid = 1;
INSERT INTO t_ver (sid, verno)
SELECT 1, '5'
WHERE NOT EXISTS (SELECT sid FROM t_ver WHERE sid = 1);
