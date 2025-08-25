-- 「工程実績付加情報」テーブルから「画像データ」を削除。
ALTER TABLE trn_actual_adition DROP image_data;

-- 「工程実績付加情報」テーブルに「データ名」を追加。
ALTER TABLE trn_actual_adition ADD data_name character varying(256); -- データ名
comment on column trn_actual_adition.data_name is 'データ名';

-- 「工程実績付加情報」テーブルに「タグ」を追加。
ALTER TABLE trn_actual_adition ADD tag character varying(256); -- タグ
comment on column trn_actual_adition.tag is 'タグ';

-- 「工程実績付加情報」テーブルに「RAWデータ」を追加。
ALTER TABLE trn_actual_adition ADD raw_data bytea; -- RAWデータ
comment on column trn_actual_adition.raw_data is 'RAWデータ';

-- 「カンバン」テーブルに「製造番号」を追加する。
ALTER TABLE trn_kanban ADD production_number character varying(32); -- 製造番号
comment on column trn_kanban.production_number is '製造番号';

-- 作業日報ビュー (直接作業・間接作業・中断時間) の更新
DROP VIEW IF EXISTS view_work_report;
CREATE VIEW view_work_report AS 
  -- 日毎の作業者の直接工数 (工程・注番毎): ロット流しカンバン以外と、ロット流しカンバン (シリアルあり)
  SELECT
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    0 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    null::bigint AS indirect_actual_id,
    -- 作業ID (工程ID)
    act.work_id,
    -- 分類番号
    null::text AS class_number,
    -- 作業No
    wk.work_number,
    -- 作業内容
    wk.work_name AS work_name,
    -- 注文番号 (サブカンバン名)
    COALESCE (kan.kanban_subname, '') AS order_number,
    -- 工数(ms)
    SUM(act.work_time) AS work_time,
    -- 工程順ID
    act.workflow_id,
    -- カンバン名
    kan.kanban_name,
    -- モデル名
    kan.model_name,
    -- 実績数
    SUM(act.comp_num) AS actual_num,
    -- 作業種別の順
    1 AS work_type_order,
    -- 製造番号
    kan.production_number

  FROM trn_actual_result AS act
  LEFT JOIN mst_work AS wk ON wk.work_id = act.work_id
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  LEFT JOIN trn_kanban AS kan ON kan.kanban_id = act.kanban_id
  WHERE act.organization_id IS NOT NULL
  AND act.actual_status <> 'INTERRUPT'
  AND (kan.production_type <> 2 OR (kan.production_type = 2 AND kan.kanban_subname IS NOT NULL))
  GROUP BY
    act.workflow_id,
    kan.kanban_name,
    kan.kanban_subname,
    kan.model_name,
    act.work_id,
    act.organization_id,
    wk.work_id,
    org.organization_id,
    work_date,
    kan.production_number

  -- 日毎の作業者の直接工数 (工程・注番毎): ロット流し生産 (シリアルなし)
  UNION ALL
  SELECT
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    0 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    null::bigint AS indirect_actual_id,
    -- 作業ID (工程ID)
    act.work_id,
    -- 分類番号
    null::text AS class_number,
    -- 作業No
    wk.work_number,
    -- 作業内容
    wk.work_name AS work_name,
    -- 注文番号 (サービス情報に含まれる)
    job->>'PORDER' AS order_number,
    -- 工数(ms)
    SUM(((act.work_time * (job->'LVOL')::int) / kan_vol.lvol_sum)) work_time,
    -- 工程順ID
    act.workflow_id,
    -- カンバン名
    kan.kanban_name,
    -- モデル名
    kan.model_name,
    -- 実績数
    SUM(((act.comp_num * (job->'LVOL')::int) / kan_vol.lvol_sum)) actual_num,
    -- 作業種別の順
    1 AS work_type_order,
    -- 製造番号
    kan.production_number

  FROM trn_actual_result AS act
  LEFT JOIN mst_work AS wk ON wk.work_id = act.work_id
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  LEFT JOIN trn_kanban AS kan ON kan.kanban_id = act.kanban_id

  JOIN jsonb_array_elements(kan.service_info) items(item) ON item->>'service' = 'els'
  JOIN jsonb_array_elements(item::jsonb->'job') job ON job->>'SN' IS NULL
  JOIN (
    SELECT 
      kanban_id,
      SUM((job->'LVOL')::int + (job->'DEFECT')::int) lvol_sum
    FROM trn_kanban
    JOIN jsonb_array_elements(service_info) items(item) ON item->>'service' = 'els'
    JOIN jsonb_array_elements(item::jsonb->'job') job ON job->>'SN' IS NULL
    GROUP BY kanban_id
  ) kan_vol ON kan_vol.kanban_id = kan.kanban_id

  WHERE act.organization_id IS NOT NULL
  AND act.actual_status <> 'INTERRUPT'
  AND kan.production_type = 2
  AND kan.kanban_subname IS NULL
  GROUP BY
    act.workflow_id,
    kan.kanban_name,
    order_number,
    kan.model_name,
    act.work_id,
    act.organization_id,
    wk.work_id,
    org.organization_id,
    work_date,
    kan.production_number

  -- 日毎の作業者の間接工数 (間接作業毎) を結合
  UNION ALL
  SELECT 
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    1 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    act.indirect_actual_id,
    -- 作業ID
    act.indirect_work_id AS work_id,
    -- 分類番号
    wk.class_number,
    -- 作業No
    wk.work_number,
    -- 作業内容
    wk.work_name,
    -- 注文番号
    '' AS order_number,
    -- 工数(ms)
    SUM(act.work_time) AS work_time,
    -- 工程順ID
    -1 AS workflow_id,
    -- カンバン名
    '' AS kanban_name,
    -- モデル名
    '' AS model_name,
    -- 実績数
    0 AS actual_num,
    -- 作業種別の順
    3 AS work_type_order,
    -- 製造番号
    '' AS production_number

  FROM trn_indirect_actual AS act
  LEFT JOIN mst_indirect_work AS wk ON wk.indirect_work_id = act.indirect_work_id
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  WHERE act.organization_id IS NOT NULL
  GROUP BY
    act.indirect_actual_id,
    wk.indirect_work_id,
    org.organization_id,
    work_date

  -- 日毎の作業者の中断時間 (中断理由毎) を結合
  UNION ALL
  SELECT
    -- 作業種別 (0:直接作業, 1:間接作業, 2:中断時間)
    2 AS work_type,
    -- 作業日 ('yyyyMMdd')
    to_char(act.implement_datetime, 'yyyymmdd') AS work_date,
    -- 組織ID
    act.organization_id,
    -- 組織識別名
    org.organization_identify,
    -- 組織名
    org.organization_name,
    -- 間接工数実績ID
    null::bigint AS indirect_actual_id,
    -- 作業ID (工程ID)
    null::bigint AS work_id,
    -- 分類番号
    null::text AS class_number,
    -- 作業No
    '' AS work_number,
    -- 作業内容 (中断理由)
    act.interrupt_reason AS work_name,
    -- 注文番号
    '' AS order_number,
    -- 工数(ms) (中断時間)
    SUM(act.non_work_time) AS work_time,
    -- 工程順ID
    null::bigint AS workflow_id,
    -- カンバン名
    '' AS kanban_name,
    -- モデル名
    '' AS model_name,
    -- 実績数 (中断回数)
    COUNT(act.actual_id) AS actual_num,
    -- 作業種別の順
    2 AS work_type_order,
    -- 製造番号
    '' AS production_number

  FROM trn_actual_result AS act
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  WHERE act.organization_id IS NOT NULL
  AND act.non_work_time IS NOT NULL
  GROUP BY
    act.interrupt_reason,
    act.organization_id,
    org.organization_id,
    work_date
;

comment on view view_work_report is '作業日報';
comment on column view_work_report.work_type is '作業種別';
comment on column view_work_report.work_date is '作業日';
comment on column view_work_report.organization_id is '組織ID';
comment on column view_work_report.organization_identify is '組織識別名';
comment on column view_work_report.organization_name is '組織名';
comment on column view_work_report.indirect_actual_id is '間接工数実績ID';
comment on column view_work_report.work_id is '作業ID';
comment on column view_work_report.class_number is '分類番号';
comment on column view_work_report.work_number is '作業No';
comment on column view_work_report.work_name is '作業内容';
comment on column view_work_report.order_number is '注文番号';
comment on column view_work_report.work_time is '工数(ms)';
comment on column view_work_report.workflow_id is '工程順ID';
comment on column view_work_report.kanban_name is 'カンバン名';
comment on column view_work_report.model_name is 'モデル名';
comment on column view_work_report.actual_num is '実績数';
comment on column view_work_report.work_type_order is '作業種別の順';
comment on column view_work_report.production_number is '製造番号';

-- 「製品」テーブルの「ID」のデフォルト値を変更。(自動採番)
CREATE SEQUENCE trn_product_product_id_seq;
ALTER TABLE trn_product ALTER COLUMN product_id SET DEFAULT nextval('trn_product_product_id_seq'::regclass);
