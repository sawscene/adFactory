CREATE TABLE trn_direct_actual (
    direct_actual_id   BIGSERIAL,
    work_type          INTEGER           NOT NULL,
    implement_datetime TIMESTAMP         NOT NULL,
    organization_id    BIGINT,
    work_id            BIGINT,
    work_name          varchar(256),
    order_number       varchar(256),
    work_time          INTEGER DEFAULT 0 NOT NULL,
    workflow_id        BIGINT,
    kanban_name        varchar(256),
    model_name         varchar(256),
    actual_number      INTEGER,
    work_type_order    INTEGER,
    product_number     varchar(256),
    remove_flag        BOOLEAN DEFAULT FALSE NOT NULL,
    update_person_id   BIGINT,
    actual_add_info    JSONB,
    ver_info           INTEGER DEFAULT 1 NOT NULL
);

COMMENT ON TABLE trn_direct_actual IS '直接工数実績';
COMMENT ON COLUMN trn_direct_actual.direct_actual_id IS '直接工数実績ID';
COMMENT ON COLUMN trn_direct_actual.work_type IS '作業種別';
COMMENT ON COLUMN trn_direct_actual.implement_datetime IS '実施日時';
COMMENT ON COLUMN trn_direct_actual.organization_id IS '組織ID';
COMMENT ON COLUMN trn_direct_actual.work_id IS '工程ID';
COMMENT ON COLUMN trn_direct_actual.work_name IS '工程名';
COMMENT ON COLUMN trn_direct_actual.order_number IS '注文番号';
COMMENT ON COLUMN trn_direct_actual.work_time IS '作業時間';
COMMENT ON COLUMN trn_direct_actual.workflow_id IS '工程順ID';
COMMENT ON COLUMN trn_direct_actual.kanban_name IS 'カンバン名';
COMMENT ON COLUMN trn_direct_actual.model_name IS 'モデル名';
COMMENT ON COLUMN trn_direct_actual.actual_number IS '実績数';
COMMENT ON COLUMN trn_direct_actual.work_type_order IS '作業種別順';
COMMENT ON COLUMN trn_direct_actual.product_number IS '製造番号';
COMMENT ON COLUMN trn_direct_actual.remove_flag IS '論理削除フラグ';
COMMENT ON COLUMN trn_direct_actual.update_person_id IS '更新者';
COMMENT ON COLUMN trn_direct_actual.actual_add_info IS '追加情報';
COMMENT ON COLUMN trn_direct_actual.ver_info IS '排他用バージョン';


