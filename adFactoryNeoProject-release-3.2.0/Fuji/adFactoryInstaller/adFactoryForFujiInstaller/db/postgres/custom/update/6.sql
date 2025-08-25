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
  query text := 'SELECT k.kanban_id, k.kanban_name, k.workflow_id, w.workflow_name, k.start_datetime, k.comp_datetime, k.kanban_status, k.actual_start_datetime, k.actual_comp_datetime FROM trn_kanban k JOIN mst_workflow w ON w.workflow_id = k.workflow_id WHERE kanban_id IN (';
  
BEGIN
  param := array_to_string(kanban_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ')') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (kanban_id bigint, 
	    kanban_name character varying(256),
	    workflow_id bigint,
	    workflow_name character varying(256),
	    start_datetime timestamp without time zone,
	    comp_datetime timestamp without time zone,
	    kanban_status character varying(128),
	    actual_start_datetime timestamp without time zone,
	    actual_comp_datetime timestamp without time zone);
    IF NOT FOUND THEN EXIT; END IF;
      kanban_id := rec.kanban_id;
      kanban_name := rec.kanban_name;
      fk_workflow_id := rec.workflow_id;
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


-- 工程カンバン取得関数
DROP FUNCTION IF EXISTS get_work_kanban(bigint[]);
CREATE FUNCTION get_work_kanban (
  IN kanban_ids bigint[],
  OUT work_kanban_id bigint,
  OUT fk_kanban_id bigint,
  OUT fk_work_id bigint,
  OUT work_name character varying(128),
  OUT start_datetime timestamp without time zone,
  OUT comp_datetime timestamp without time zone,
  OUT takt_time integer,
  OUT sum_times integer,
  OUT work_status character varying(128),
  OUT actual_start_datetime timestamp without time zone,
  OUT actual_comp_datetime timestamp without time zone
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  param text;
  conn_name text := 'work_kanban_conn';
  cur_name text := 'work_kanban_cur';
  query text := 'SELECT wk.work_kanban_id, wk.kanban_id, wk.work_id, w.work_name, wk.start_datetime, wk.comp_datetime, wk.takt_time, wk.sum_times, wk.work_status, wk.actual_start_datetime, wk.actual_comp_datetime FROM trn_work_kanban wk LEFT JOIN mst_work w ON w.work_id = wk.work_id WHERE wk.kanban_id IN (';
  
BEGIN
  param := array_to_string(kanban_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ') AND skip_flag = false ORDER BY wk.actual_start_datetime') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (work_kanban_id bigint,
            kanban_id bigint,
            work_id bigint,
            work_name character varying(128),
            start_datetime timestamp without time zone,
            comp_datetime timestamp without time zone,
            takt_time integer,
            sum_times integer,
            work_status character varying(128),
            actual_start_datetime timestamp without time zone,
            actual_comp_datetime timestamp without time zone);
    IF NOT FOUND THEN EXIT; END IF;
      work_kanban_id := rec.work_kanban_id;
      fk_kanban_id := rec.kanban_id;
      fk_work_id := rec.work_id;
      work_name := rec.work_name;
      start_datetime := rec.start_datetime;
      comp_datetime := rec.comp_datetime;
      takt_time := rec.takt_time;
      sum_times := rec.sum_times;
      work_status := rec.work_status;
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


-- 工程カンバン取得関数(WORKING、COMPLETION)
DROP FUNCTION IF EXISTS get_work_kanban_in_work(bigint[]);
CREATE FUNCTION get_work_kanban_in_work(
  IN kanban_ids bigint[],
  OUT work_kanban_id bigint,
  OUT fk_kanban_id bigint,
  OUT fk_work_id bigint,
  OUT work_name character varying(128),
  OUT start_datetime timestamp without time zone,
  OUT comp_datetime timestamp without time zone,
  OUT takt_time integer,
  OUT sum_times integer,
  OUT work_status character varying(128),
  OUT actual_start_datetime timestamp without time zone,
  OUT actual_comp_datetime timestamp without time zone
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  param text;
  conn_name text := 'work_kanban_conn';
  cur_name text := 'work_kanban_cur';
  query text := 'SELECT wk.work_kanban_id, wk.kanban_id, wk.work_id, w.work_name, wk.start_datetime, wk.comp_datetime, wk.takt_time, wk.sum_times, wk.work_status, wk.actual_start_datetime, wk.actual_comp_datetime FROM trn_work_kanban wk LEFT JOIN mst_work w ON w.work_id = wk.work_id WHERE wk.kanban_id IN (';
  
BEGIN
  param := array_to_string(kanban_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ') AND ((wk.work_status = ''WORKING'') OR (wk.work_status = ''COMPLETION'')) ORDER BY wk.actual_start_datetime') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (work_kanban_id bigint,
            kanban_id bigint,
            work_id bigint,
            work_name character varying(128),
            start_datetime timestamp without time zone,
            comp_datetime timestamp without time zone,
            takt_time integer,
            sum_times integer,
            work_status character varying(128),
            actual_start_datetime timestamp without time zone,
            actual_comp_datetime timestamp without time zone);
    IF NOT FOUND THEN EXIT; END IF;
      work_kanban_id := rec.work_kanban_id;
      fk_kanban_id := rec.kanban_id;
      fk_work_id := rec.work_id;
      work_name := rec.work_name;
      start_datetime := rec.start_datetime;
      comp_datetime := rec.comp_datetime;
      takt_time := rec.takt_time;
      sum_times := rec.sum_times;
      work_status := rec.work_status;
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


-- 進捗率取得関数
DROP FUNCTION IF EXISTS get_unit_progress_rate(bigint[]);
CREATE FUNCTION get_unit_progress_rate(kanban_ids bigint[])
RETURNS double precision AS $$
DECLARE
  query text := 'SELECT COUNT(wk.work_status = ''COMPLETION'' or null) / COUNT(wk.work_kanban_id)::float progress_rate FROM trn_work_kanban wk WHERE wk.kanban_id IN (';
  
BEGIN
  query := query || array_to_string(kanban_ids, ',') || ') AND wk.skip_flag = false';
  RETURN (SELECT * FROM dblink('dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432', query) AS (progress_rate double precision));
END;
$$ LANGUAGE plpgsql;


-- 作業実績取得関数
DROP FUNCTION IF EXISTS get_actual_result(bigint);
CREATE FUNCTION get_actual_result (
  IN work_kanban_id bigint,
  OUT fk_kanban_id bigint,
  OUT fk_work_kanban_id bigint,
  OUT actual_status character varying(256),
  OUT implement_datetime timestamp without time zone,
  OUT organization_name character varying(256)
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  conn_name text := 'actual_result_conn';
  cur_name text := 'actual_result_cur';
  query text := 'SELECT a.kanban_id, a.work_kanban_id, a.actual_status, a.implement_datetime, o.organization_name FROM trn_actual_result a LEFT JOIN mst_organization o ON o.organization_id = a.organization_id WHERE a.work_kanban_id = ';
  
BEGIN
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || work_kanban_id || 'AND (a.actual_status = ''WORKING'' OR a.actual_status = ''COMPLETION'')') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (kanban_id bigint,
            work_kanban_id bigint,
            actual_status character varying(256),
            implement_datetime timestamp without time zone,
            organization_name character varying(256));
    IF NOT FOUND THEN EXIT; END IF;
      fk_kanban_id := rec.kanban_id;
      fk_work_kanban_id := rec.work_kanban_id;
      actual_status := rec.actual_status;
      implement_datetime := rec.implement_datetime;
      organization_name := rec.organization_name;
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


-- 作業実績取得関数 (by カンバンID)
DROP FUNCTION IF EXISTS get_actual_result_by_kanbanId(bigint[]);
CREATE FUNCTION get_actual_result_by_kanbanId (
  IN kanban_ids bigint[],
  OUT actual_id bigint,
  OUT fk_kanban_id bigint,
  OUT fk_work_kanban_id bigint,
  OUT actual_status character varying(256),
  OUT implement_datetime timestamp without time zone,
  OUT organization_name character varying(256)
)
RETURNS SETOF RECORD AS $$
DECLARE
  rec record;
  dummy  text;
  param text;
  conn_name text := 'actual_result_conn';
  cur_name text := 'actual_result_cur';
  query text := 'SELECT a.actual_id, a.kanban_id, a.work_kanban_id, a.actual_status, a.implement_datetime, o.organization_name FROM trn_actual_result a LEFT JOIN mst_organization o ON o.organization_id = a.organization_id WHERE a.kanban_id IN (';
  
BEGIN
  param := array_to_string(kanban_ids, ',');
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
  SELECT dblink_open(conn_name, cur_name, query || param || ') AND (a.actual_status = ''WORKING'' OR a.actual_status = ''COMPLETION'') ORDER BY a.actual_id') INTO dummy;

  LOOP
    SELECT *
      INTO rec
        FROM dblink_fetch(conn_name, cur_name, 1)
          AS (actual_id bigint,
            kanban_id bigint,
            work_kanban_id bigint,
            actual_status character varying(256),
            implement_datetime timestamp without time zone,
            organization_name character varying(256));
    IF NOT FOUND THEN EXIT; END IF;
      actual_id := rec.actual_id;
      fk_kanban_id := rec.kanban_id;
      fk_work_kanban_id := rec.work_kanban_id;
      actual_status := rec.actual_status;
      implement_datetime := rec.implement_datetime;
      organization_name := rec.organization_name;
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
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
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
  SELECT dblink_connect_u(conn_name, 'dbname=adFactoryDB2 user=fujio password=fuji2017 port=15432') INTO dummy;
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
UPDATE t_ver SET verno='6' WHERE sid = 1;
INSERT INTO t_ver (sid, verno)
SELECT 1, '6'
WHERE NOT EXISTS (SELECT sid FROM t_ver WHERE sid = 1);
