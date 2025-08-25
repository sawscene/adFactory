--- 入出庫実績
ALTER TABLE log_stock ADD product_name character varying(64); -- 品名
comment on column log_stock.product_name is '品名';
