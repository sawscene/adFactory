-- 作業履歴ビュー
-- pairId を使用して高速化
DROP VIEW IF EXISTS view_work_history;
CREATE OR REPLACE VIEW view_work_history AS
  SELECT
    -- 実績ID
    his.actual_id,
    -- カンバンID
    his.kanban_id,
    -- 工程順ID
    his.workflow_id,
    -- 工程カンバンID
    his.work_kanban_id,
    -- 工程ID
    his.work_id,
    -- 組織ID
    his.organization_id,
    -- カンバン名
    his.kanban_name,
    -- カンバンステータス
    kan.kanban_status,
    -- 工程順名
    his.workflow_name,
    -- モデル名
    kan.model_name,
    -- 工程名
    his.work_name,
    -- 工程カンバンステータス
    wkan.work_status AS work_kanban_status,
    -- 組織名
    his.organization_name,
    -- 計画開始日時
    wkan.start_datetime AS plan_start_time,
    -- 計画完了日時
    wkan.comp_datetime AS plan_end_time,
    -- 工程開始日時
    wkan.actual_start_datetime AS work_start_time,
    -- 工程完了日時
    wkan.actual_comp_datetime AS work_end_time,
    -- 実績開始日時
    his.implement_datetime AS actual_start_time,
    -- 実績完了日時
    his.actual_end_time,
    -- 工程の文字色
    wk.font_color,
    -- 工程の背景色
    wk.back_color,
    -- 作業累計時間
    wkan.sum_times,
    -- タクトタイム
    wkan.takt_time takt_time,
    -- 設備ID
    his.equipment_id AS fk_equipment_id,
    -- 表示順
    wkan.work_kanban_order,
    -- 追加工程
    wkan.separate_work_flag,
    -- 応援
    his.assist,
    -- 親組織ID
    org.parent_organization_id
FROM (
  SELECT
    act.actual_id,
    act.kanban_id,
    act.workflow_id,
    act.work_kanban_id,
    act.work_id,
    act.organization_id,
    act.equipment_id,
    act.implement_datetime,
    act.kanban_name,
    act.workflow_name,
    act.work_name,
    act.organization_name,
    MIN(comp_act.implement_datetime) AS actual_end_time,
    act.assist
  FROM (
    SELECT
      a1.actual_id,
      a1.kanban_id,
      a1.workflow_id,
      a1.work_kanban_id,
      a1.work_id,
      a1.organization_id,
      a1.equipment_id,
      a1.implement_datetime,
      a1.kanban_name,
      a1.workflow_name,
      a1.work_name,
      a1.organization_name,
      a1.assist,
      a1.pair_id
    FROM trn_actual_result a1
    WHERE a1.actual_status = 'WORKING'
  ) act

  LEFT JOIN (
    SELECT
      a2.actual_id,
      a2.work_kanban_id,
      a2.implement_datetime,
      a2.organization_id,
      a2.pair_id
    FROM trn_actual_result a2
  ) comp_act ON comp_act.pair_id = act.actual_id

  GROUP BY
    act.actual_id,
    act.kanban_id,
    act.workflow_id,
    act.work_kanban_id,
    act.work_id,
    act.organization_id,
    act.equipment_id,
    act.implement_datetime,
    act.kanban_name,
    act.workflow_name,
    act.work_name,
    act.organization_name,
    act.assist
  ) his
  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = his.kanban_id
  -- 工程カンバンを結合
  LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = his.work_kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = his.work_id
  -- 組織マスタを結合
  LEFT JOIN mst_organization org ON org.organization_id = his.organization_id
;

comment on view view_work_history is '作業履歴ビュー';
comment on column view_work_history.actual_id is '実績ID';
comment on column view_work_history.kanban_id is 'カンバンID';
comment on column view_work_history.workflow_id is '工程順ID';
comment on column view_work_history.work_kanban_id is '工程カンバンID';
comment on column view_work_history.work_id is '工程ID';
comment on column view_work_history.organization_id is '組織ID';
comment on column view_work_history.kanban_name is 'カンバン名';
comment on column view_work_history.kanban_status is 'カンバンステータス';
comment on column view_work_history.workflow_name is '工程順名';
comment on column view_work_history.model_name is 'モデル名';
comment on column view_work_history.work_name is '工程名';
comment on column view_work_history.work_kanban_status is '工程カンバンステータス';
comment on column view_work_history.organization_name is '組織名';
comment on column view_work_history.plan_start_time is '計画開始日時';
comment on column view_work_history.plan_end_time is '計画完了日時';
comment on column view_work_history.work_start_time is '工程開始日時';
comment on column view_work_history.work_end_time is '工程完了日時';
comment on column view_work_history.actual_start_time is '実績開始日時';
comment on column view_work_history.actual_end_time is '実績完了日時';
comment on column view_work_history.font_color is '工程の文字色';
comment on column view_work_history.back_color is '工程の背景色';
comment on column view_work_history.sum_times is '作業累計時間';
comment on column view_work_history.takt_time is 'タクトタイム';
comment on column view_work_history.fk_equipment_id is '設備ID';
comment on column view_work_history.work_kanban_order is '表示順';
comment on column view_work_history.separate_work_flag is '追加工程';
comment on column view_work_history.assist is '応援';
comment on column view_work_history.parent_organization_id is '親組織ID';
