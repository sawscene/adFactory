-- adminの権限を更新
UPDATE mst_role_authority 
SET
    workflow_edit = true                        -- 工程・工程順編集権限
    , workflow_reference = true                 -- 工程・工程順参照権限
WHERE
    role_name = 'admin';

-- 管理者の権限を更新
UPDATE mst_role_authority 
SET
    workflow_edit = (                           -- 工程・工程順編集権限
        SELECT
            r.resource_edit 
        FROM
            mst_role_authority r 
        WHERE
            role_name = '管理者 - Managers'
    ) 
    , workflow_reference = (                    -- 工程・工程順参照権限
        SELECT
            r.resource_reference 
        FROM
            mst_role_authority r 
        WHERE
            role_name = '管理者 - Managers'
    ) 
WHERE
    role_name = '管理者 - Managers';

-- 作業者の権限を更新
UPDATE mst_role_authority 
SET
    workflow_edit = (                           -- 工程・工程順編集権限
        SELECT
            r.resource_edit 
        FROM
            mst_role_authority r 
        WHERE
            role_name = '作業者 - Workers'
    ) 
    , workflow_reference = (                    -- 工程・工程順参照権限
        SELECT
            r.resource_reference 
        FROM
            mst_role_authority r 
        WHERE
            role_name = '作業者 - Workers'
    ) 
WHERE
    role_name = '作業者 - Workers';
