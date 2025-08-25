-- 「入出庫実績」テーブルにカラムを追加する。
ALTER TABLE log_stock ADD IF NOT EXISTS request_num integer; -- 要求数
comment on column log_stock.request_num is '要求数';
