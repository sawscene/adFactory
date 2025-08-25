
-- 役割テーブルに管理者を追加
INSERT 
INTO public.mst_role_authority( 
    role_id                                     -- 役割ID
    , role_name                                 -- 役割名
    , actual_del                                -- 実績削除権限
    , resource_edit                             -- リソース編集権限
    , kanban_create                             -- カンバン作成権限
    , line_manage                               -- ライン管理権限
    , actual_output                             -- 実績出力権限
    , kanban_reference                          -- カンバン参照権限
    , resource_reference                        -- リソース参照権限
    , access_edit                               -- アクセス権編集権限
    , ver_info                                  -- 排他用バージョン
    , approve                                   -- 承認権限
) 
SELECT
    nextval('mst_role_authority_role_id_seq')   -- 役割ID
    , '管理者 - Managers'                        -- 役割名
    , true                                      -- 実績削除権限
    , true                                      -- リソース編集権限
    , true                                      -- カンバン作成権限
    , true                                      -- ライン管理権限
    , true                                      -- 実績出力権限
    , true                                      -- カンバン参照権限
    , true                                      -- リソース参照権限
    , true                                      -- アクセス権編集権限
    , 1                                         -- 排他用バージョン
    , true                                      -- 承認権限
WHERE
    NOT EXISTS (SELECT role_id FROM mst_role_authority WHERE role_name = '管理者 - Managers');

-- 役割テーブルに作業者を追加
INSERT 
INTO public.mst_role_authority( 
    role_id                                     -- 役割ID
    , role_name                                 -- 役割名
    , actual_del                                -- 実績削除権限
    , resource_edit                             -- リソース編集権限
    , kanban_create                             -- カンバン作成権限
    , line_manage                               -- ライン管理権限
    , actual_output                             -- 実績出力権限
    , kanban_reference                          -- カンバン参照権限
    , resource_reference                        -- リソース参照権限
    , access_edit                               -- アクセス権編集権限
    , ver_info                                  -- 排他用バージョン
    , approve                                   -- 承認権限
) 
SELECT
    nextval('mst_role_authority_role_id_seq')   -- 役割ID
    , '作業者 - Workers'                         -- 役割名
    , false                                     -- 実績削除権限
    , false                                     -- リソース編集権限
    , true                                      -- カンバン作成権限
    , false                                     -- ライン管理権限
    , false                                     -- 実績出力権限
    , true                                      -- カンバン参照権限
    , true                                      -- リソース参照権限
    , false                                     -- アクセス権編集権限
    , 1                                         -- 排他用バージョン
    , false                                     -- 承認権限
WHERE
    NOT EXISTS (SELECT role_id FROM mst_role_authority WHERE role_name = '作業者 - Workers');

