-- 帳票マスタ
ALTER TABLE mst_ledger ADD last_implement_datetime timestamp with time zone;

comment on column mst_ledger.last_implement_datetime is '最終実施日';

ALTER TABLE trn_ledger_file ALTER COLUMN key_word TYPE JSONB USING key_word::JSONB;

