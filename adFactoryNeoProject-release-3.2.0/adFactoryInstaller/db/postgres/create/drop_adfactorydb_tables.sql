-- adFactoryDB2 テーブル削除

-- 階層関連付け
DROP TABLE IF EXISTS con_hierarchy CASCADE;
-- カンバン階層関連付け
DROP TABLE IF EXISTS con_kanban_hierarchy CASCADE;
-- 組織・休憩関連付け
DROP TABLE IF EXISTS con_organization_breaktime CASCADE;
-- 組織・役割関連付け
DROP TABLE IF EXISTS con_organization_role CASCADE;
-- 組織・作業区分関連付け
DROP TABLE IF EXISTS con_organization_work_category CASCADE;
-- 工程・設備関連付け
DROP TABLE IF EXISTS con_work_equipment CASCADE;
-- 工程・組織関連付け
DROP TABLE IF EXISTS con_work_organization CASCADE;
-- 工程順工程関連付け
DROP TABLE IF EXISTS con_workflow_work CASCADE;
-- 工程カンバン・設備関連付け
DROP TABLE IF EXISTS con_workkanban_equipment CASCADE;
-- 工程カンバン・組織関連付け
DROP TABLE IF EXISTS con_workkanban_organization CASCADE;
-- 認証情報
DROP TABLE IF EXISTS mst_authentication_info CASCADE;
-- 休憩マスタ設定項目
DROP TABLE IF EXISTS mst_breaktime CASCADE;
-- ステータス表示マスタ
DROP TABLE IF EXISTS mst_displayed_status CASCADE;
-- 設備マスタ
DROP TABLE IF EXISTS mst_equipment CASCADE;
-- 設備種別マスタ
DROP TABLE IF EXISTS mst_equipment_type CASCADE;
-- 階層マスタ
DROP TABLE IF EXISTS mst_hierarchy CASCADE;
-- 休日情報
DROP TABLE IF EXISTS mst_holiday CASCADE;
-- 間接作業マスタ
DROP TABLE IF EXISTS mst_indirect_work CASCADE;
-- カンバン階層マスタ
DROP TABLE IF EXISTS mst_kanban_hierarchy CASCADE;
-- モノマスタ
DROP TABLE IF EXISTS mst_object CASCADE;
-- モノ種別マスタ
DROP TABLE IF EXISTS mst_object_type CASCADE;
-- 組織マスタ
DROP TABLE IF EXISTS mst_organization CASCADE;
-- 理由マスタ
DROP TABLE IF EXISTS mst_reason CASCADE;
-- 役割権限マスタ
DROP TABLE IF EXISTS mst_role_authority CASCADE;
-- 予定情報
DROP TABLE IF EXISTS mst_schedule CASCADE;
-- 工程マスタ
DROP TABLE IF EXISTS mst_work CASCADE;
-- 作業区分マスタ
DROP TABLE IF EXISTS mst_work_category CASCADE;
-- 工程セクション
DROP TABLE IF EXISTS mst_work_section CASCADE;
-- 工程順マスタ
DROP TABLE IF EXISTS mst_workflow CASCADE;
-- DBバージョン
DROP TABLE IF EXISTS t_ver CASCADE;
-- カンバン階層
DROP TABLE IF EXISTS tre_kanban_hierarchy CASCADE;
-- 階層アクセス権
DROP TABLE IF EXISTS trn_access_hierarchy CASCADE;
-- 工程実績付加情報
DROP TABLE IF EXISTS trn_actual_adition CASCADE;
-- 工程実績
DROP TABLE IF EXISTS trn_actual_result CASCADE;
-- 間接工数実績
DROP TABLE IF EXISTS trn_indirect_actual CASCADE;
-- カンバン
DROP TABLE IF EXISTS trn_kanban CASCADE;
-- 生産実績
DROP TABLE IF EXISTS trn_prod_result CASCADE;
-- 製品
DROP TABLE IF EXISTS trn_product CASCADE;
-- 工程カンバン
DROP TABLE IF EXISTS trn_work_kanban CASCADE;
-- 工程カンバン作業中リスト
DROP TABLE IF EXISTS trn_work_kanban_working CASCADE;
-- 完成品
DROP TABLE IF EXISTS trn_parts CASCADE;

-- 月毎の trn_actual_result 子テーブルを作成する関数
DROP FUNCTION IF EXISTS create_actual_result;
-- 新しいレコードを trn_actual_result 子テーブルに振り分ける関数
DROP FUNCTION IF EXISTS insert_actual_result;
-- 月毎の trn_actual_result 子テーブルを作成する関数
DROP FUNCTION IF EXISTS create_actual_result;
-- 月毎の trn_prod_result 子テーブルを作成する関数
DROP FUNCTION IF EXISTS create_prod_result;
-- 新しいレコードを trn_prod_result 子テーブルに振り分ける関数
DROP FUNCTION IF EXISTS insert_prod_result;
