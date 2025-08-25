DROP VIEW IF EXISTS view_work_report;

-- 作業日報ビュー
CREATE 
OR REPLACE VIEW view_work_report AS 
SELECT
    0 AS work_type
    , to_char(act.implement_datetime, 'yyyymmdd' ::text) AS work_date
    , act.organization_id
    , org.organization_identify
    , org.organization_name
    , NULL ::bigint AS indirect_actual_id
    , act.work_id
    , NULL ::text AS class_number
    , wk.work_number
    , wk.work_name
    , COALESCE(kan.kanban_subname, '' ::character varying) AS order_number
    , sum(act.work_time) AS work_time
    , act.workflow_id
    , kan.kanban_name
    , kan.model_name
    , sum(act.comp_num) AS actual_num
    , 1 AS work_type_order
    , kan.production_number
    , ARRAY_TO_STRING(ARRAY_AGG(act.serial_no), '|') AS serial_no
FROM
    trn_actual_result act 
    LEFT JOIN mst_work wk 
        ON wk.work_id = act.work_id 
    LEFT JOIN mst_organization org 
        ON org.organization_id = act.organization_id 
    LEFT JOIN trn_kanban kan 
        ON kan.kanban_id = act.kanban_id 
WHERE
    act.organization_id IS NOT NULL 
    AND act.actual_status ::text <> 'INTERRUPT' ::text 
    AND ( 
        kan.production_type <> 2 
        OR kan.production_type = 2 
        AND kan.kanban_subname IS NOT NULL
    ) 
GROUP BY
    act.workflow_id
    , kan.kanban_name
    , kan.kanban_subname
    , kan.model_name
    , act.work_id
    , act.organization_id
    , wk.work_id
    , org.organization_id
    , ( 
        to_char(act.implement_datetime, 'yyyymmdd' ::text)
    ) 
    , kan.production_number
UNION ALL 
SELECT
    0 AS work_type
    , to_char(act.implement_datetime, 'yyyymmdd' ::text) AS work_date
    , act.organization_id
    , org.organization_identify
    , org.organization_name
    , NULL ::bigint AS indirect_actual_id
    , act.work_id
    , NULL ::text AS class_number
    , wk.work_number
    , wk.work_name
    , job.value ->> 'PORDER' ::text AS order_number
    , sum( 
        ( 
            act.work_time * ( 
                (job.value -> 'LVOL' ::text) ::bigint + (job.value -> 'DEFECT' ::text) ::bigint
            )
        ) ::numeric / kan.lvol_sum
    ) AS work_time
    , act.workflow_id
    , kan.kanban_name
    , kan.model_name
    , sum( 
        ( 
            act.comp_num * ( 
                (job.value -> 'LVOL' ::text) ::bigint + (job.value -> 'DEFECT' ::text) ::bigint
            )
        ) ::numeric / kan.lvol_sum
    ) AS actual_num
    , 1 AS work_type_order
    , kan.production_number
    , ARRAY_TO_STRING(ARRAY_AGG(act.serial_no), '|') AS serial_no 
FROM
    trn_actual_result act 
    LEFT JOIN mst_work wk 
        ON wk.work_id = act.work_id 
    LEFT JOIN mst_organization org 
        ON org.organization_id = act.organization_id 
    LEFT JOIN ( 
        SELECT
            trn_kanban.kanban_id
            , trn_kanban.kanban_name
            , trn_kanban.model_name
            , trn_kanban.production_type
            , trn_kanban.production_number
            , trn_kanban.kanban_subname
            , trn_kanban.service_info
            , CASE 
                WHEN sum( 
                    (job_1.value -> 'LVOL' ::text) ::bigint + (job_1.value -> 'DEFECT' ::text) ::bigint
                ) = 0 ::numeric 
                    THEN 1 ::numeric 
                ELSE sum( 
                    (job_1.value -> 'LVOL' ::text) ::bigint + (job_1.value -> 'DEFECT' ::text) ::bigint
                ) 
                END AS lvol_sum 
        FROM
            trn_kanban JOIN LATERAL jsonb_array_elements(trn_kanban.service_info) items_1(item) 
                ON (items_1.item ->> 'service' ::text) = 'els' ::text JOIN LATERAL jsonb_array_elements(items_1.item -> 'job' ::text)
                 job_1(value) 
                ON (job_1.value ->> 'SN' ::text) IS NULL 
        GROUP BY
            trn_kanban.kanban_id
            , trn_kanban.kanban_name
            , trn_kanban.model_name
            , trn_kanban.production_type
            , trn_kanban.production_number
            , trn_kanban.kanban_subname
            , trn_kanban.service_info
    ) kan 
        ON kan.kanban_id = act.kanban_id JOIN LATERAL jsonb_array_elements(kan.service_info) items(item) 
            ON (items.item ->> 'service' ::text) = 'els' ::text JOIN LATERAL jsonb_array_elements(items.item -> 'job' ::text)
             job(value) 
                ON (job.value ->> 'SN' ::text) IS NULL 
WHERE
    act.organization_id IS NOT NULL 
    AND act.actual_status ::text <> 'INTERRUPT' ::text 
    AND kan.production_type = 2 
    AND kan.kanban_subname IS NULL 
GROUP BY
    act.workflow_id
    , kan.kanban_name
    , (job.value ->> 'PORDER' ::text)
    , kan.model_name
    , act.work_id
    , act.organization_id
    , wk.work_id
    , org.organization_id
    , ( 
        to_char(act.implement_datetime, 'yyyymmdd' ::text)
    ) 
    , kan.production_number 
UNION ALL 
SELECT
    1 AS work_type
    , to_char(act.implement_datetime, 'yyyymmdd' ::text) AS work_date
    , act.organization_id
    , org.organization_identify
    , org.organization_name
    , act.indirect_actual_id
    , act.indirect_work_id AS work_id
    , wk.class_number
    , wk.work_number
    , wk.work_name
    , '' ::character varying AS order_number
    , sum(act.work_time) AS work_time
    , '-1' ::integer AS workflow_id
    , '' ::character varying AS kanban_name
    , '' ::character varying AS model_name
    , 0 AS actual_num
    , 3 AS work_type_order
    , act.production_number AS production_number
    , '' ::character varying AS serial_no 
FROM
    trn_indirect_actual act 
    LEFT JOIN mst_indirect_work wk 
        ON wk.indirect_work_id = act.indirect_work_id 
    LEFT JOIN mst_organization org 
        ON org.organization_id = act.organization_id 
WHERE
    act.organization_id IS NOT NULL 
GROUP BY
    act.indirect_actual_id
    , act.production_number
    , wk.indirect_work_id
    , org.organization_id
    , ( 
        to_char(act.implement_datetime, 'yyyymmdd' ::text)
    ) 
UNION ALL 
SELECT
    2 AS work_type
    , to_char(act.implement_datetime, 'yyyymmdd' ::text) AS work_date
    , act.organization_id
    , org.organization_identify
    , org.organization_name
    , NULL ::bigint AS indirect_actual_id
    , NULL ::bigint AS work_id
    , NULL ::text AS class_number
    , '' ::character varying AS work_number
    , act.interrupt_reason AS work_name
    , '' ::character varying AS order_number
    , sum(act.non_work_time) AS work_time
    , NULL ::bigint AS workflow_id
    , '' ::character varying AS kanban_name
    , '' ::character varying AS model_name
    , count(act.actual_id) AS actual_num
    , 2 AS work_type_order
    , '' ::character varying AS production_number
    , '' ::character varying AS serial_no 
FROM
    trn_actual_result act 
    LEFT JOIN mst_organization org 
        ON org.organization_id = act.organization_id 
WHERE
    act.organization_id IS NOT NULL 
    AND act.non_work_time IS NOT NULL 
GROUP BY
    act.interrupt_reason
    , act.organization_id
    , org.organization_id
    , ( 
        to_char(act.implement_datetime, 'yyyymmdd' ::text)
    );

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
comment on column view_work_report.serial_no is 'シリアル番号';

