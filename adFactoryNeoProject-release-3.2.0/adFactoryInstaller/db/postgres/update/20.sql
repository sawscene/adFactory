-- 「入出庫実績」テーブルにカラムを追加する。
ALTER TABLE log_stock ADD adjustment integer; -- 在庫調整

comment on column log_stock.adjustment is '在庫調整';
