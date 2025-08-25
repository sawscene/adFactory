-- カンバン階層に「adFactoryLite」を追加
INSERT INTO mst_kanban_hierarchy (hierarchy_name)
  SELECT 'adFactoryLite'
  WHERE NOT EXISTS (SELECT kanban_hierarchy_id FROM mst_kanban_hierarchy WHERE hierarchy_name = 'adFactoryLite');

INSERT INTO tre_kanban_hierarchy (parent_id, child_id)
  SELECT 0, a.kanban_hierarchy_id FROM mst_kanban_hierarchy a WHERE a.hierarchy_name = 'adFactoryLite';

-- 工程階層と工程順階層に「adFactoryLite」を追加
INSERT INTO mst_hierarchy (hierarchy_type, hierarchy_name, parent_hierarchy_id)
  SELECT 0, 'adFactoryLite', 0
  WHERE NOT EXISTS (SELECT hierarchy_id FROM mst_hierarchy WHERE hierarchy_type = 0 AND hierarchy_name = 'adFactoryLite');

INSERT INTO mst_hierarchy (hierarchy_type, hierarchy_name, parent_hierarchy_id)
  SELECT 1, 'adFactoryLite', 0
  WHERE NOT EXISTS (SELECT hierarchy_id FROM mst_hierarchy WHERE hierarchy_type = 1 AND hierarchy_name = 'adFactoryLite');
