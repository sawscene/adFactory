-- 出庫品目
ALTER TABLE trn_delivery_item
  ADD IF NOT EXISTS withdraw_num integer default 0,
  ALTER COLUMN reserve SET DEFAULT NULL;
comment on column trn_delivery_item.withdraw_num is '在庫払出数';
comment on column trn_delivery_item.reserve is '在庫引当状況';

