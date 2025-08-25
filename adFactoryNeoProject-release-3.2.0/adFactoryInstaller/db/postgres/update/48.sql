-- 出庫指示
ALTER TABLE trn_delivery
  ADD IF NOT EXISTS stockout_num integer;
comment on column trn_delivery.stockout_num is '欠品数';

-- 出庫品目
ALTER TABLE trn_delivery_item
  ADD IF NOT EXISTS arrange integer,
  ADD IF NOT EXISTS usage_num integer;
comment on column trn_delivery_item.arrange is '手配区分';
comment on column trn_delivery_item.usage_num is '使用数';


ALTER TABLE trn_lot_trace DROP CONSTRAINT trn_lot_trace_pk;
ALTER TABLE trn_lot_trace ADD CONSTRAINT trn_lot_trace_pk PRIMARY KEY (delivery_no,item_no,material_no,work_kanban_id);
