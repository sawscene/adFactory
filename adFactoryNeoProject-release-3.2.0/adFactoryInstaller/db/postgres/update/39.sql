-- 帳票階層マスタ
create table mst_ledger_hierarchy
(
    hierarchy_id        bigserial              not null,
    hierarchy_name      character varying(256) not null,
    parent_hierarchy_id bigserial,
    ver_info            integer default 1      not null
);

comment on table mst_ledger_hierarchy is '帳票階層マスタ';
comment on column mst_ledger_hierarchy.hierarchy_id is '階層ID';
comment on column mst_ledger_hierarchy.hierarchy_name is '階層名';
comment on column mst_ledger_hierarchy.parent_hierarchy_id is '親階層ID';
comment on column mst_ledger_hierarchy.ver_info is '排他用バージョン';

-- 帳票マスタ
create table mst_ledger
(
    ledger_id           bigserial              not null,
    parent_hierarchy_id bigserial              not null,
    ledger_name         character varying(256) not null,
    ledger_file_name         text,
    ledger_physical_file_name text,
    ledger_target       jsonb,
    ledger_condition    jsonb,
    update_datetime     timestamp without time zone,
    update_person_id    bigint,
    ver_info            integer default 1      not null
);

comment on table mst_ledger is '帳票マスタ';
comment on column mst_ledger.ledger_id is '帳票ID';
comment on column mst_ledger.parent_hierarchy_id is '親階層ID';
comment on column mst_ledger.ledger_name is '帳票名';
comment on column mst_ledger.ledger_file_name is 'ファイル名';
comment on column mst_ledger.ledger_physical_file_name is '物理ファイル名';
comment on column mst_ledger.ledger_target is '帳票ターゲット';
comment on column mst_ledger.ledger_condition is '帳票出力条件';
comment on column mst_ledger.update_datetime is '更新日時';
comment on column mst_ledger.update_person_id is '更新者';
comment on column mst_ledger.ver_info is '排他用バージョン';

-- 帳票ファイル
create table trn_ledger_file
(
    ledger_file_id bigserial not null,
    ledger_id bigserial not null,
    creator_id bigint,
    key_word text,
    create_datetime timestamp without time zone,
    from_date timestamp without time zone,
    to_date timestamp without time zone,
    organization_ids text,
    equipment_ids text,
    file_path text
);

comment on table trn_ledger_file is '帳票ファイル';
comment on column trn_ledger_file.ledger_file_id is '帳票ファイルID';
comment on column trn_ledger_file.ledger_id is '帳票ID';
comment on column trn_ledger_file.creator_id is '作成者';
comment on column trn_ledger_file.key_word is 'キーワード';
comment on column trn_ledger_file.from_date is '開始日時';
comment on column trn_ledger_file.to_date is '終了日時';
comment on column trn_ledger_file.organization_ids is '作業組織ID';
comment on column trn_ledger_file.equipment_ids is '作業設備ID';
comment on column trn_ledger_file.file_path is '帳票ファイル名';
