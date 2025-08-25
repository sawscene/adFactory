-- 出庫品目
ALTER TABLE trn_delivery_item
  ADD IF NOT EXISTS unit_no character varying(64),
  ADD IF NOT EXISTS arrange_no character varying(32),
  ADD IF NOT EXISTS reserve integer default 0;
comment on column trn_delivery_item.unit_no is 'ユニット番号';
comment on column trn_delivery_item.arrange_no is '先行手配番号';
comment on column trn_delivery_item.reserve is '在庫引当状況';

