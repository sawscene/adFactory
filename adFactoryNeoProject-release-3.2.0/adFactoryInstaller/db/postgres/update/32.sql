DROP VIEW IF EXISTS view_report_out;
DROP VIEW IF EXISTS view_work_report;

-- 製造番号の桁数を変更
ALTER TABLE trn_kanban ALTER COLUMN production_number TYPE character varying(256);

-- 実績出力ビュー
CREATE OR REPLACE VIEW view_report_out AS
SELECT act.actual_id,
       kanh.hierarchy_name         AS kanban_hierarchy_name,
       act.kanban_id               AS fk_kanban_id,
       kan.kanban_name,
       kan.kanban_subname,
       wfh.hierarchy_name          AS workflow_hierarchy_name,
       act.workflow_id             AS fk_workflow_id,
       wf.workflow_name,
       wf.workflow_rev,
       wkh.hierarchy_name          AS work_hierarchy_name,
       act.work_id                 AS fk_work_id,
       wk.work_name,
       act.work_kanban_id          AS fk_work_kanban_id,
       wkan.separate_work_flag,
       wkan.skip_flag,
       p_org.organization_name     AS parent_organization_name,
       p_org.organization_identify AS parent_organization_identify,
       act.organization_id         AS fk_organization_id,
       org.organization_name,
       org.organization_identify,
       p_eq.equipment_name         AS parent_equipment_name,
       p_eq.equipment_identify     AS parent_equipment_identify,
       act.equipment_id            AS fk_equipment_id,
       eq.equipment_name,
       eq.equipment_identify,
       act.actual_status,
       act.interrupt_reason,
       act.delay_reason,
       act.implement_datetime,
       wkan.takt_time,
       act.work_time,
       kan.model_name,
       act.comp_num,
       act.defect_reason,
       act.defect_num,
       act.actual_add_info,
       kan.production_number,
       act.serial_no,
       act.non_work_time
FROM trn_actual_result act
         LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
         LEFT JOIN con_kanban_hierarchy con_kanh ON con_kanh.kanban_id = kan.kanban_id
         LEFT JOIN mst_kanban_hierarchy kanh ON kanh.kanban_hierarchy_id = con_kanh.kanban_hierarchy_id
         LEFT JOIN trn_work_kanban wkan ON wkan.work_kanban_id = act.work_kanban_id
         LEFT JOIN mst_workflow wf ON wf.workflow_id = act.workflow_id
         LEFT JOIN con_hierarchy con_wfh ON con_wfh.hierarchy_type = 1 AND con_wfh.work_workflow_id = act.workflow_id
         LEFT JOIN mst_hierarchy wfh ON wfh.hierarchy_type = 1 AND wfh.hierarchy_id = con_wfh.hierarchy_id
         LEFT JOIN mst_work wk ON wk.work_id = act.work_id
         LEFT JOIN con_hierarchy con_wkh ON con_wkh.hierarchy_type = 0 AND con_wkh.work_workflow_id = act.workflow_id
         LEFT JOIN mst_hierarchy wkh ON wkh.hierarchy_type = 0 AND wkh.hierarchy_id = con_wkh.hierarchy_id
         LEFT JOIN mst_equipment eq ON eq.equipment_id = act.equipment_id
         LEFT JOIN mst_equipment p_eq ON p_eq.equipment_id = eq.parent_equipment_id
         LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
         LEFT JOIN mst_organization p_org ON p_org.organization_id = org.parent_organization_id;

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
comment on column view_report_out.production_number is '製品番号';
comment on column view_report_out.serial_no is 'シリアル番号';
comment on column view_report_out.non_work_time is '中断時間';

-- 作業日報ビュー
CREATE OR REPLACE VIEW view_work_report AS
SELECT 0                                                   AS work_type,
       to_char(act.implement_datetime, 'yyyymmdd'::text)   AS work_date,
       act.organization_id,
       org.organization_identify,
       org.organization_name,
       NULL::bigint                                        AS indirect_actual_id,
       act.work_id,
       NULL::text                                          AS class_number,
       wk.work_number,
       wk.work_name,
       COALESCE(kan.kanban_subname, ''::character varying) AS order_number,
       sum(act.work_time)                                  AS work_time,
       act.workflow_id,
       kan.kanban_name,
       kan.model_name,
       sum(act.comp_num)                                   AS actual_num,
       1                                                   AS work_type_order,
       kan.production_number
FROM trn_actual_result act
         LEFT JOIN mst_work wk ON wk.work_id = act.work_id
         LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
         LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
WHERE act.organization_id IS NOT NULL
  AND act.actual_status::text <> 'INTERRUPT'::text
  AND (kan.production_type <> 2 OR kan.production_type = 2 AND kan.kanban_subname IS NOT NULL)
GROUP BY act.workflow_id, kan.kanban_name, kan.kanban_subname, kan.model_name, act.work_id, act.organization_id,
    wk.work_id, org.organization_id, (to_char(act.implement_datetime, 'yyyymmdd'::text)), kan.production_number
UNION ALL
SELECT 0                                                 AS work_type,
       to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date,
       act.organization_id,
       org.organization_identify,
       org.organization_name,
       NULL::bigint                                      AS indirect_actual_id,
       act.work_id,
       NULL::text                                        AS class_number,
       wk.work_number,
       wk.work_name,
       job.value ->> 'PORDER'::text                      AS order_number,
    sum((act.work_time * ((job.value -> 'LVOL'::text)::bigint + (job.value -> 'DEFECT'::text)::bigint))::numeric /
    kan.lvol_sum)                                 AS work_time,
    act.workflow_id,
    kan.kanban_name,
    kan.model_name,
    sum((act.comp_num * ((job.value -> 'LVOL'::text)::bigint + (job.value -> 'DEFECT'::text)::bigint))::numeric /
    kan.lvol_sum)                                 AS actual_num,
    1                                                 AS work_type_order,
    kan.production_number
FROM trn_actual_result act
    LEFT JOIN mst_work wk ON wk.work_id = act.work_id
    LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
    LEFT JOIN (SELECT trn_kanban.kanban_id,
    trn_kanban.kanban_name,
    trn_kanban.model_name,
    trn_kanban.production_type,
    trn_kanban.production_number,
    trn_kanban.kanban_subname,
    trn_kanban.service_info,
    CASE
    WHEN sum((job_1.value -> 'LVOL'::text)::bigint +
    (job_1.value -> 'DEFECT'::text)::bigint) = 0::numeric THEN 1::numeric
    ELSE sum((job_1.value -> 'LVOL'::text)::bigint + (job_1.value -> 'DEFECT'::text)::bigint)
    END AS lvol_sum
    FROM trn_kanban
    JOIN LATERAL jsonb_array_elements(trn_kanban.service_info) items_1(item)
    ON (items_1.item ->> 'service'::text) = 'els'::text
    JOIN LATERAL jsonb_array_elements(items_1.item -> 'job'::text) job_1(value)
    ON (job_1.value ->> 'SN'::text) IS NULL
    GROUP BY trn_kanban.kanban_id, trn_kanban.kanban_name, trn_kanban.model_name,
    trn_kanban.production_type, trn_kanban.production_number, trn_kanban.kanban_subname,
    trn_kanban.service_info) kan ON kan.kanban_id = act.kanban_id
    JOIN LATERAL jsonb_array_elements(kan.service_info) items(item) ON (items.item ->> 'service'::text) = 'els'::text
    JOIN LATERAL jsonb_array_elements(items.item -> 'job'::text) job(value) ON (job.value ->> 'SN'::text) IS NULL
WHERE act.organization_id IS NOT NULL
  AND act.actual_status::text <> 'INTERRUPT'::text
  AND kan.production_type = 2
  AND kan.kanban_subname IS NULL
GROUP BY act.workflow_id, kan.kanban_name, (job.value ->> 'PORDER'::text), kan.model_name, act.work_id,
    act.organization_id, wk.work_id, org.organization_id, (to_char(act.implement_datetime, 'yyyymmdd'::text)),
    kan.production_number
UNION ALL
SELECT 1                                                 AS work_type,
       to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date,
       act.organization_id,
       org.organization_identify,
       org.organization_name,
       act.indirect_actual_id,
       act.indirect_work_id                              AS work_id,
       wk.class_number,
       wk.work_number,
       wk.work_name,
       ''::character varying                             AS order_number,
       sum(act.work_time)                                AS work_time,
       '-1'::integer                                     AS workflow_id,
       ''::character varying                             AS kanban_name,
       ''::character varying                             AS model_name,
       0                                                 AS actual_num,
       3                                                 AS work_type_order,
       act.production_number                             AS production_number
FROM trn_indirect_actual act
    LEFT JOIN mst_indirect_work wk ON wk.indirect_work_id = act.indirect_work_id
    LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
WHERE act.organization_id IS NOT NULL
GROUP BY act.indirect_actual_id, act.production_number, wk.indirect_work_id, org.organization_id,
    (to_char(act.implement_datetime, 'yyyymmdd'::text))
UNION ALL
SELECT 2                                                 AS work_type,
       to_char(act.implement_datetime, 'yyyymmdd'::text) AS work_date,
       act.organization_id,
       org.organization_identify,
       org.organization_name,
       NULL::bigint                                      AS indirect_actual_id,
       NULL::bigint                                      AS work_id,
       NULL::text                                        AS class_number,
       ''::character varying                             AS work_number,
       act.interrupt_reason                              AS work_name,
       ''::character varying                             AS order_number,
       sum(act.non_work_time)                            AS work_time,
       NULL::bigint                                      AS workflow_id,
       ''::character varying                             AS kanban_name,
       ''::character varying                             AS model_name,
       count(act.actual_id)                              AS actual_num,
       2                                                 AS work_type_order,
       ''::character varying                             AS production_number
FROM trn_actual_result act
    LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
WHERE act.organization_id IS NOT NULL
  AND act.non_work_time IS NOT NULL
GROUP BY act.interrupt_reason, act.organization_id, org.organization_id,
    (to_char(act.implement_datetime, 'yyyymmdd'::text));

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


