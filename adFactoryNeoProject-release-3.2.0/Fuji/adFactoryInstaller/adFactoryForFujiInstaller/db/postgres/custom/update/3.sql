-- カンバン取得関数
DROP FUNCTION IF EXISTS get_kanban(bigint[]);
CREATE FUNCTION get_kanban (
  IN kanban_ids bigint[],
  OUT kanban_id bigint,
  OUT kanban_name character varying(256),
  OUT fk_workflow_id bigint,
  OUT workflow_name character varying(256),
  OUT start_datetime timestamp without time zone,
  OUT comp_datetime timestamp without time zone,
  OUT kanban_status character varying(128),
  OUT actual_start_datetime timestamp without time zone,
  OUT actual_comp_datetime timestamp without time zone
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  param text;
  conn_name text := 'kanban_conn';
  cur_name text := 'kanban_cur';
  query text := 'SELECT k.kanban_id, k.kanban_name, k.fk_workflow_id, w.workflow_name, k.start_datetime, k.comp_datetime, k.kanban_status, k.actual_start_datetime, k.actual_comp_datetime FROM trn_kanban k JOIN mst_workflow w ON w.workflow_id = k.fk_workflow_id WHERE kanban_id IN (';
  
BEGIN
  param := array_to_string(kanban_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB user=fujio password=fuji2017') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ')') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (kanban_id bigint, 
	    kanban_name character varying(256),
	    fk_workflow_id bigint,
	    workflow_name character varying(256),
	    start_datetime timestamp without time zone,
	    comp_datetime timestamp without time zone,
	    kanban_status character varying(128),
	    actual_start_datetime timestamp without time zone,
	    actual_comp_datetime timestamp without time zone);
    IF NOT FOUND THEN EXIT; END IF;
      kanban_id := rec.kanban_id;
      kanban_name := rec.kanban_name;
      fk_workflow_id := rec.fk_workflow_id;
      workflow_name := rec.workflow_name;
      start_datetime := rec.start_datetime;
      comp_datetime := rec.comp_datetime;
      kanban_status := rec.kanban_status;
      actual_start_datetime := rec.actual_start_datetime;
      actual_comp_datetime := rec.actual_comp_datetime;
    RETURN NEXT;
  END LOOP;
  
  SELECT dblink_close(conn_name, cur_name) INTO dummy;
  SELECT dblink_disconnect(conn_name) INTO dummy;

  EXCEPTION
    WHEN OTHERS THEN
      SELECT dblink_disconnect(conn_name) INTO dummy;
  RETURN;
END;
$$ LANGUAGE plpgsql;

-- 工程順取得関数
DROP FUNCTION IF EXISTS get_workflow(bigint[]);
CREATE FUNCTION get_workflow (
  IN workflow_ids bigint[],
  OUT workflow_id bigint,
  OUT workflow_name character varying(256)
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  param text;
  conn_name text := 'workflow_conn';
  cur_name text := 'workflow_cur';
  query text := 'SELECT workflow_id, workflow_name FROM mst_workflow WHERE workflow_id IN (';
  
BEGIN
  param := array_to_string(workflow_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB user=fujio password=fuji2017') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ')') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (workflow_id bigint, 
	    workflow_name character varying(256));
    IF NOT FOUND THEN EXIT; END IF;
      workflow_id := rec.workflow_id;
      workflow_name := rec.workflow_name;
    RETURN NEXT;
  END LOOP;
  
  SELECT dblink_close(conn_name, cur_name) INTO dummy;
  SELECT dblink_disconnect(conn_name) INTO dummy;

  EXCEPTION
    WHEN OTHERS THEN
      SELECT dblink_disconnect(conn_name) INTO dummy;
  RETURN;
END;
$$ LANGUAGE plpgsql;


-- カンバン階層取得関数
DROP FUNCTION IF EXISTS get_kanbanHierarchy(bigint[]);
CREATE FUNCTION get_kanbanHierarchy (
  IN kanban_hierarchy_ids bigint[],
  OUT kanban_hierarchy_id bigint,
  OUT hierarchy_name character varying(256)
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  param text;
  conn_name text := 'kanban_hierarchy_conn';
  cur_name text := 'kanban_hierarchy_cur';
  query text := 'SELECT kanban_hierarchy_id, hierarchy_name FROM mst_kanban_hierarchy WHERE kanban_hierarchy_id IN (';
  
BEGIN
  param := array_to_string(kanban_hierarchy_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB user=fujio password=fuji2017') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ')') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (kanban_hierarchy_id bigint, 
	    hierarchy_name character varying(256));
    IF NOT FOUND THEN EXIT; END IF;
      kanban_hierarchy_id := rec.kanban_hierarchy_id;
      hierarchy_name := rec.hierarchy_name;
    RETURN NEXT;
  END LOOP;
  
  SELECT dblink_close(conn_name, cur_name) INTO dummy;
  SELECT dblink_disconnect(conn_name) INTO dummy;

  EXCEPTION
    WHEN OTHERS THEN
      SELECT dblink_disconnect(conn_name) INTO dummy;
  RETURN;
END;
$$ LANGUAGE plpgsql;


-- t_verを更新
UPDATE t_ver SET verno='3' WHERE sid = 1;
INSERT INTO t_ver (sid, verno)
SELECT 1, '3'
WHERE NOT EXISTS (SELECT sid FROM t_ver WHERE sid = 1);
