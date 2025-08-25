-- 「役割権限マスタ」テーブルに「工程・工程順編集権限」を追加する。
ALTER TABLE mst_role_authority ADD workflow_edit boolean default false; --工程・工程順編集権限
comment on column mst_role_authority.workflow_edit is '工程・工程順編集権限';

-- 「役割権限マスタ」テーブルに「工程・工程順参照権限」を追加する。
ALTER TABLE mst_role_authority ADD workflow_reference boolean default false; --工程・工程順参照権限
comment on column mst_role_authority.workflow_reference is '工程・工程順参照権限';
