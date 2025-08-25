-- 仕様
ALTER TABLE trn_product
ADD COLUMN product_spec1 character varying(256),
ADD COLUMN product_spec2 character varying(256),
ADD COLUMN product_spec3 character varying(256),
ADD COLUMN product_spec4 character varying(256),
ADD COLUMN product_spec5 character varying(256),
ADD COLUMN product_spec6 character varying(256),
ADD COLUMN product_spec7 character varying(256),
ADD COLUMN product_spec8 character varying(256),
ADD COLUMN product_spec9 character varying(256),
ADD COLUMN product_spec10 character varying(256);

comment on column trn_product.product_spec1 is '仕様1';
comment on column trn_product.product_spec2 is '仕様2';
comment on column trn_product.product_spec3 is '仕様3';
comment on column trn_product.product_spec4 is '仕様4';
comment on column trn_product.product_spec5 is '仕様5';
comment on column trn_product.product_spec6 is '仕様6';
comment on column trn_product.product_spec7 is '仕様7';
comment on column trn_product.product_spec8 is '仕様8';
comment on column trn_product.product_spec9 is '仕様9';
comment on column trn_product.product_spec10 is '仕様10';

-- 製品ID
ALTER TABLE trn_prod_result ADD COLUMN fk_product_id bigint;
comment on column trn_prod_result.fk_product_id is '製品ID';

-- 「工程実績」テーブルに「不良理由」を追加する。
ALTER TABLE trn_actual_result ADD defect_reason character varying(256); -- 不良理由
comment on column trn_actual_result.defect_reason is '不良理由';

-- 「工程実績」テーブルに「不良数」を追加する。
ALTER TABLE trn_actual_result ADD defect_num integer; -- 不良数
comment on column trn_actual_result.defect_num is '不良数';

-- 完成品
create table trn_parts (
  parts_id character varying(256) not null
  , serial_no_info jsonb
  , work_kanban_id bigint
  , comp_datetime timestamp without time zone
  , dest_work_kanban_id bigint
  , remove_flag boolean
  , constraint trn_parts_pk primary key (parts_id, work_kanban_id)
) ;

comment on table trn_parts is '完成品';
comment on column trn_parts.parts_id is 'パーツID';
comment on column trn_parts.serial_no_info is 'シリアル番号情報';
comment on column trn_parts.work_kanban_id is '製造元カンバンID';
comment on column trn_parts.comp_datetime is '製造日';
comment on column trn_parts.dest_work_kanban_id is '供給先カンバンID';
comment on column trn_parts.remove_flag is '論理削除フラグ';


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
    1 AS work_type_order

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
    work_date

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
    SUM(((act.work_time * ((job->'LVOL')::int + (job->'DEFECT')::int)) / kan_vol.lvol_sum)) work_time,
    -- 工程順ID
    act.workflow_id,
    -- カンバン名
    kan.kanban_name,
    -- モデル名
    kan.model_name,
    -- 実績数
    SUM(((act.comp_num * ((job->'LVOL')::int + (job->'DEFECT')::int)) / kan_vol.lvol_sum)) actual_num,
    -- 作業種別の順
    1 AS work_type_order

  FROM trn_actual_result AS act
  LEFT JOIN mst_work AS wk ON wk.work_id = act.work_id
  LEFT JOIN mst_organization AS org ON org.organization_id = act.organization_id
  LEFT JOIN trn_kanban AS kan ON kan.kanban_id = act.kanban_id

  JOIN jsonb_array_elements(kan.service_info) items(item) ON item->>'service' = 'els'
  JOIN jsonb_array_elements(item::jsonb->'job') job ON job->>'SN' IS NULL
  JOIN (
    SELECT 
      kanban_id,
      SUM((job->'LVOL')::int) lvol_sum
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
    work_date

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
    3 AS work_type_order

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
    2 AS work_type_order

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


-- 実績出力ビューの更新
DROP VIEW IF EXISTS view_report_out;
CREATE VIEW view_report_out AS
  SELECT
    -- 工程実績ID
    act.actual_id,
    -- カンバン階層名
    kanh.hierarchy_name kanban_hierarchy_name,
    -- カンバンID
    act.kanban_id AS fk_kanban_id,
    -- カンバン名
    kan.kanban_name,
    -- サブカンバン名
    kan.kanban_subname,
    -- 工程順階層名
    wfh.hierarchy_name workflow_hierarchy_name,
    -- 工程順ID
    act.workflow_id AS fk_workflow_id,
    -- 工程順名
    wf.workflow_name,
    -- 工程順の版数
    wf.workflow_rev,
    -- 工程階層名
    wkh.hierarchy_name work_hierarchy_name,
    -- 工程ID
    act.work_id AS fk_work_id,
    -- 工程名
    wk.work_name,
    -- 工程カンバンID
    act.work_kanban_id AS fk_work_kanban_id,
    -- 追加工程フラグ
    wkan.separate_work_flag,
    -- スキップフラグ
    wkan.skip_flag,
    -- 親組織名
    p_org.organization_name AS parent_organization_name,
    -- 親組織識別名
    p_org.organization_identify AS parent_organization_identify,
    -- 組織ID
    act.organization_id AS fk_organization_id,
    -- 組織名
    org.organization_name,
    -- 組織識別名
    org.organization_identify,
    -- 親設備名
    p_eq.equipment_name AS parent_equipment_name,
    -- 親設備識別名
    p_eq.equipment_identify AS parent_equipment_identify,
    -- 設備ID
    act.equipment_id AS fk_equipment_id,
    -- 設備名
    eq.equipment_name,
    -- 設備識別名
    eq.equipment_identify,
    -- ステータス
    act.actual_status,
    -- 中断理由
    act.interrupt_reason,
    -- 遅延理由
    act.delay_reason,
    -- 実施時刻
    act.implement_datetime,
    -- タクトタイム
    wkan.takt_time,
    -- 作業時間
    act.work_time,
    -- モデル名
    kan.model_name,
    -- 完成数
    act.comp_num,
    -- 不良理由
    act.defect_reason,
    -- 不良数
    act.defect_num,
    -- 追加情報
    act.actual_add_info

  FROM trn_actual_result act

  -- カンバン情報を結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
  -- カンバン階層情報を結合
  LEFT JOIN con_kanban_hierarchy con_kanh ON con_kanh.kanban_id = kan.kanban_id
  LEFT JOIN mst_kanban_hierarchy kanh ON kanh.kanban_hierarchy_id = con_kanh.kanban_hierarchy_id
  -- 工程カンバン情報を結合
  LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = act.work_kanban_id
  -- 工程順情報を結合
  LEFT JOIN mst_workflow wf ON wf.workflow_id = act.workflow_id
  -- 工程順階層情報を結合
  LEFT JOIN con_hierarchy con_wfh ON con_wfh.hierarchy_type = 1 AND con_wfh.work_workflow_id = act.workflow_id
  LEFT JOIN mst_hierarchy wfh ON wfh.hierarchy_type = 1 AND wfh.hierarchy_id = con_wfh.hierarchy_id
  -- 工程情報を結合
  LEFT JOIN mst_work wk ON wk.work_id = act.work_id
  -- 工程階層情報を結合
  LEFT JOIN con_hierarchy con_wkh ON con_wkh.hierarchy_type = 0 AND con_wkh.work_workflow_id = act.workflow_id
  LEFT JOIN mst_hierarchy wkh ON wkh.hierarchy_type = 0 AND wkh.hierarchy_id = con_wkh.hierarchy_id
  -- 設備情報を結合
  LEFT JOIN mst_equipment eq ON eq.equipment_id = act.equipment_id
  -- 親設備情報を結合
  LEFT JOIN mst_equipment p_eq ON p_eq.equipment_id = eq.parent_equipment_id
  -- 組織情報を結合
  LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
  -- 親組織情報を結合
  LEFT JOIN mst_organization p_org ON p_org.organization_id = org.parent_organization_id
;

comment on view view_report_out is '実績出力';
comment on column view_report_out.actual_id is '工程実績ID';
comment on column view_report_out.kanban_hierarchy_name is 'カンバン階層名';
comment on column view_report_out.fk_kanban_id is 'カンバンID';
comment on column view_report_out.kanban_name is 'カンバン名';
comment on column view_report_out.kanban_subname is 'サブカンバン名';
comment on column view_report_out.workflow_hierarchy_name is '工程順階層名';
comment on column view_report_out.fk_workflow_id is '工程順ID';
comment on column view_report_out.workflow_name is '工程順名';
comment on column view_report_out.workflow_rev is '工程順の版数';
comment on column view_report_out.work_hierarchy_name is '工程階層名';
comment on column view_report_out.fk_work_id is '工程ID';
comment on column view_report_out.work_name is '工程名';
comment on column view_report_out.fk_work_kanban_id is '工程カンバンID';
comment on column view_report_out.separate_work_flag is '追加工程フラグ';
comment on column view_report_out.skip_flag is 'スキップフラグ';
comment on column view_report_out.parent_organization_name is '親組織名';
comment on column view_report_out.parent_organization_identify is '親組織識別名';
comment on column view_report_out.fk_organization_id is '組織ID';
comment on column view_report_out.organization_name is '組織名';
comment on column view_report_out.organization_identify is '組織識別名';
comment on column view_report_out.parent_equipment_name is '親設備名';
comment on column view_report_out.parent_equipment_identify is '親設備識別名';
comment on column view_report_out.fk_equipment_id is '設備ID';
comment on column view_report_out.equipment_name is '設備名';
comment on column view_report_out.equipment_identify is '設備識別名';
comment on column view_report_out.actual_status is 'ステータス';
comment on column view_report_out.interrupt_reason is '中断理由';
comment on column view_report_out.delay_reason is '遅延理由';
comment on column view_report_out.implement_datetime is '実施時刻';
comment on column view_report_out.takt_time is 'タクトタイム';
comment on column view_report_out.work_time is '作業時間';
comment on column view_report_out.model_name is 'モデル名';
comment on column view_report_out.comp_num is '完成数';
comment on column view_report_out.defect_reason is '不良理由';
comment on column view_report_out.defect_num is '不良数';
comment on column view_report_out.actual_add_info is '追加情報';
