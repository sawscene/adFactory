-- Project Name : afFactoryER
-- Date/Time    : 2019/10/21 15:52:54
-- Author       : adtek fuji
-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2


-- 移行用工程マスタプロパティ

drop table iko_mst_work_property cascade;

-- 移行用カンバンプロパティ

drop table iko_trn_kanban_property cascade;

-- 移行用実績プロパティ

drop table iko_trn_actual_property cascade;

-- 移行用工程カンバンプロパティ

drop table iko_trn_work_kanban_property cascade;

-- 移行用役割権限マスタ
--* BackupToTempTable
drop table iko_mst_role_authority cascade;

-- 移行用設備マスタ設定項目

drop table iko_mst_equipment_setting cascade;

-- 移行用ライセンスID

drop table iko_license cascade;

-- データベース操作ログ

drop table database_operation_log cascade;

-- 移行用理由ID新旧対応表

drop table iko_reason cascade;

-- 移行用階層ID新旧対応表

drop table iko_hierarchy cascade;

