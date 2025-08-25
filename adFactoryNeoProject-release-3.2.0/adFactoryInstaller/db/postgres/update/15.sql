--- 理由区分マスタ
create table mst_reason_category (
  reason_category_id bigserial not null
  , reason_type integer not null
  , reason_category_name character varying(256) not null
  , ver_info integer default 1 not null
  , constraint mst_reason_category_pk primary key (reason_category_id)
) ;

alter table mst_reason_category add constraint mst_reason_category_idx2
  unique (reason_category_name) ;

comment on table mst_reason_category is '理由区分マスタ';
comment on column mst_reason_category.reason_category_id is '理由区分ID';
comment on column mst_reason_category.reason_type is '理由種別:0: 呼び出し理由
1: 中断理由
2: 遅延理由
3: 不良理由';
comment on column mst_reason_category.reason_category_name is '理由区分名';
comment on column mst_reason_category.ver_info is '排他用バージョン';

-- 理由マスタ
ALTER TABLE mst_reason ADD reason_category_id bigint; -- 理由区分ID
comment on column mst_reason.reason_category_id is '理由区分ID';

-- 作業履歴ビュー
-- 親組織IDを追加
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
      a1.assist
    FROM trn_actual_result a1
    WHERE a1.actual_status = 'WORKING'
  ) act

  LEFT JOIN (
    SELECT
      a2.actual_id,
      a2.work_kanban_id,
      a2.implement_datetime,
      a2.organization_id
    FROM trn_actual_result a2
  ) comp_act ON comp_act.work_kanban_id = act.work_kanban_id
    AND comp_act.implement_datetime >= act.implement_datetime
    AND comp_act.actual_id <> act.actual_id
    AND comp_act.organization_id = act.organization_id

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

-- 「工程カンバン」テーブルに「最終実績ID」を追加する。
ALTER TABLE trn_work_kanban ADD last_actual_id bigint;
comment on column trn_work_kanban.last_actual_id is '最終実績ID';

-- 工程カンバン別計画実績
DROP VIEW IF EXISTS view_work_kanban_topic;
CREATE VIEW view_work_kanban_topic AS
  SELECT
    -- カンバンID
    kan.kanban_id,
    -- 工程カンバンID
    wkan.work_kanban_id,
    -- 組織ID
    COALESCE(org.organization_id, 0) organization_id,
    -- カンバン名
    kan.kanban_name,
    -- カンバンステータス
    kan.kanban_status,
    -- 工程順名
    wf.workflow_name,
    -- モデル名
    kan.model_name,
    -- 工程名
    wk.work_name,
    -- 工程カンバンステータス
    wkan.work_status work_kanban_status,
    -- 組織名
    org.organization_name,
    -- 工程カンバンの開始予定日時
    wkan.start_datetime plan_start_time,
    -- 工程カンバンの完了予定日時
    wkan.comp_datetime plan_end_time,
    -- 工程カンバンの最初の実績日時
    wkan.actual_start_datetime actual_start_time,
    -- 工程カンバンの最後の実績日時
    CASE wkan.work_status WHEN 'SUSPEND' THEN act_info.implement_datetime ELSE wkan.actual_comp_datetime END actual_end_time,
    -- 設備名
    NULL AS equipment_name,
    -- 工程の文字色
    wk.font_color,
    -- 工程の背景色
    wk.back_color,
    -- 作業累計時間
    wkan.sum_times,
    -- タクトタイム
    wkan.takt_time takt_time,
    -- 工程ID
    wk.work_id,
    -- 工程順の版数
    wf.workflow_rev,
    -- カンバンの開始予定日時
    kan.start_datetime kanban_plan_start_time,
    -- カンバンの完了予定日時
    kan.comp_datetime kanban_plan_end_time,
    -- カンバンの開始実績日時
    kan.actual_start_datetime kanban_actual_start_time,
    -- カンバンの完了実績日時
    kan.actual_comp_datetime kanban_actual_end_time,
    -- 工程カンバンの表示順
    wkan.work_kanban_order,
    -- 追加工程
    wkan.separate_work_flag,
    -- 作業者名
    act_info.organization_name worker_name

  FROM trn_work_kanban wkan

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = wkan.kanban_id
  -- 工程順を結合
  LEFT JOIN mst_workflow wf ON wf.workflow_id = wkan.workflow_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = wkan.work_id
  -- 組織を結合
  LEFT JOIN con_workkanban_organization con_org ON con_org.workkanban_id = wkan.work_kanban_id

  LEFT JOIN mst_organization org ON org.organization_id = con_org.organization_id

  -- 実績情報を結合
  LEFT JOIN trn_actual_result act_info ON act_info.actual_id = wkan.last_actual_id

  WHERE wkan.skip_flag = false;

comment on view view_work_kanban_topic is '工程カンバン別計画実績';
comment on column view_work_kanban_topic.kanban_id is 'カンバンID';
comment on column view_work_kanban_topic.work_kanban_id is '工程カンバンID';
comment on column view_work_kanban_topic.organization_id is '組織ID';
comment on column view_work_kanban_topic.kanban_name is 'カンバン名';
comment on column view_work_kanban_topic.kanban_status is 'カンバンステータス';
comment on column view_work_kanban_topic.workflow_name is '工程順名';
comment on column view_work_kanban_topic.model_name is 'モデル名';
comment on column view_work_kanban_topic.work_name is '工程名';
comment on column view_work_kanban_topic.work_kanban_status is '工程カンバンステータス';
comment on column view_work_kanban_topic.organization_name is '組織名';
comment on column view_work_kanban_topic.plan_start_time is '工程カンバンの開始予定日時';
comment on column view_work_kanban_topic.plan_end_time is '工程カンバンの完了予定日時';
comment on column view_work_kanban_topic.actual_start_time is '工程カンバンの最初の実績日時';
comment on column view_work_kanban_topic.actual_end_time is '工程カンバンの最後の実績日時';
comment on column view_work_kanban_topic.equipment_name is '設備名';
comment on column view_work_kanban_topic.font_color is '工程の文字色';
comment on column view_work_kanban_topic.back_color is '工程の背景色';
comment on column view_work_kanban_topic.sum_times is '作業累計時間';
comment on column view_work_kanban_topic.takt_time is 'タクトタイム';
comment on column view_work_kanban_topic.work_id is '工程ID';
comment on column view_work_kanban_topic.work_kanban_order is '工程カンバンの表示順';
comment on column view_work_kanban_topic.workflow_rev is '工程順の版数';
comment on column view_work_kanban_topic.kanban_plan_start_time is 'カンバンの開始予定日時';
comment on column view_work_kanban_topic.kanban_plan_end_time is 'カンバンの完了予定日時';
comment on column view_work_kanban_topic.kanban_actual_start_time is 'カンバンの開始実績日時';
comment on column view_work_kanban_topic.kanban_actual_end_time is 'カンバンの完了実績日時';
comment on column view_work_kanban_topic.separate_work_flag is '追加工程';
comment on column view_work_kanban_topic.worker_name is '作業者名';
