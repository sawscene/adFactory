SELECT
    k2.kanban_id kanban_id
    , k2.production_number production_number
    , k2.start_datetime start_datetime
    , k2.comp_datetime comp_datetime
    , k2.actual_start_datetime actual_start_datetime
    , k2.actual_comp_datetime actual_comp_datetime
    , k2.kanban_status kanban_status
    , k2.work_num work_num
    , k2.comp_work_num comp_work_num
    , tk2.kanban_add_info kanban_add_info
    , tar.interrupt_reason interrupt_reason
    , mr.font_color font_color
    , mr.back_color back_color
    , mr.light_pattern light_pattern
    , tar2.defect_reason defect_reason
    , k2.comp_num comp_num
    , k2.lot_quantity lot_quantity
FROM
    ( 
        SELECT
            k.production_number
            , min(k.start_datetime) start_datetime
            , max(k.comp_datetime) comp_datetime
            , CASE min( 
                    CASE 
                        WHEN twk.work_status = 'DEFECT'  THEN 0 
                        WHEN twk.work_status = 'SUSPEND' THEN 1 
                        WHEN twk.work_status = 'WORKING' AND ?2 > twk.comp_datetime THEN 2 
                        WHEN twk.work_status = 'WORKING' AND twk.start_datetime < twk.actual_start_datetime THEN 3 
                        WHEN twk.work_status = 'WORKING' THEN 4 
                        WHEN twk.work_status = 'COMPLETION' AND twk.comp_datetime < twk.actual_comp_datetime THEN 5 
                        WHEN twk.work_status = 'COMPLETION' THEN 6 
                        WHEN twk.work_status = 'PLANNED' AND ?2 > twk.start_datetime THEN 7 
                        ELSE 8 
                        END
                ) 
                WHEN 0 THEN 'DEFECT' 
                WHEN 1 THEN 'SUSPEND_NORMAL' 
                WHEN 2 THEN 'WORK_DELAYCOMP' 
                WHEN 3 THEN 'WORK_DELAYSTART' 
                WHEN 4 THEN 'WORK_NORMAL' 
                WHEN 5 THEN 'COMP_DELAYCOMP' 
                WHEN 6 THEN 'COMP_NORMAL' 
                WHEN 7 THEN 'PLAN_DELAYSTART' 
                ELSE 'PLAN_NORMAL' 
                END kanban_status
            , count(twk.kanban_id) work_num
            , sum( 
                CASE 
                    WHEN twk.work_status = 'COMPLETION' THEN 1 
                    ELSE 0 
                    END
            ) comp_work_num
            , max(k.kanban_id) kanban_id
            , max( 
                CASE 
                    WHEN twk.work_status = 'SUSPEND' THEN twk.last_actual_id 
                    ELSE null 
                    END
            ) suspend_last_actual_id
            , max( 
                CASE 
                    WHEN twk.work_status = 'DEFECT' THEN twk.last_actual_id 
                    ELSE null 
                    END
            ) defect_last_actual_id
            , min(k.actual_start_datetime) actual_start_datetime
            , max(k.actual_comp_datetime) actual_comp_datetime
            , max(k.comp_num) comp_num
            , max(k.lot_quantity) lot_quantity
        FROM
            ( 
                with recursive hierarchY_list AS ( 
                    SELECT
                        tkh.child_id 
                    FROM
                        tre_kanban_hierarchy tkh 
                    WHERE
                        tkh.child_id = ANY (?3)
                    UNION ALL 
                    SELECT
                        tkh1.child_id 
                    FROM
                        tre_kanban_hierarchy tkh1
                        , hierarchy_list hl 
                    WHERE
                        hl.child_id = tkh1.parent_id
                ) 
                SELECT distinct
                    (tk.kanban_id) kanban_id
                    , tk.production_number
                    , tk.start_datetime
                    , tk.comp_datetime
                    , tk.actual_start_datetime
                    , tk.actual_comp_datetime
                    , tk.comp_num
                    , tk.lot_quantity
                FROM
                    con_kanban_hierarchy ckh
                JOIN hierarchy_list hl ON ckh.kanban_hierarchy_id = hl.child_id 
                JOIN trn_kanban tk ON ckh.kanban_id = tk.kanban_id 
                    AND tk.production_number NOTNULL 
                    AND (tk.start_datetime < ?4 OR tk.kanban_status <> 'PLANNED') 
                    AND (tk.kanban_status <> 'PLANNING' AND tk.kanban_status <> 'INTERRUPT')
            ) as k 
            JOIN trn_work_kanban twk ON twk.skip_flag = false AND k.kanban_id = twk.kanban_id 
                AND (twk.work_status <> 'PLANNING' AND twk.work_status <> 'INTERRUPT') 
        GROUP BY k.production_number
    ) k2 
    JOIN trn_kanban tk2 ON (k2.work_num <> k2.comp_work_num OR k2.actual_comp_datetime > ?1) AND k2.kanban_id = tk2.kanban_id 
    LEFT JOIN trn_actual_result tar ON tar.actual_id = k2.suspend_last_actual_id 
    LEFT JOIN trn_actual_result tar2 ON tar2.actual_id = k2.defect_last_actual_id 
    LEFT JOIN mst_reason mr ON mr.reason_type = 1 AND mr.reason = tar.interrupt_reason 
WHERE
    tar2.actual_id IS NULL OR tar2.implement_datetime > ?1
