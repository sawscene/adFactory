ALTER TABLE trn_kanban ADD defect_num integer DEFAULT 0; -- 不良数
comment on column trn_kanban.defect_num is '不良数';

-- ライン生産情報ビューに不良数を追加
DROP VIEW IF EXISTS view_line_product;
CREATE VIEW view_line_product AS
  SELECT
    -- カンバンID
    kan.kanban_id,
    -- 設備ID
    act_info.equipment_id,
    -- 完了日時
    act_info.actual_end_time,
    -- モデル名
    kan.model_name,
    -- ロット数量
    COALESCE(kan.lot_quantity, 1) - kan.defect_num AS lot_quantity,
    -- 不良数
    kan.defect_num

  FROM trn_kanban kan

  -- 実績情報を結合
  LEFT JOIN (
    SELECT
      -- カンバンID
      last_act.kanban_id last_actual_kanban_id,
      -- 設備ID
      last_act.equipment_id,
      -- 完了日時
      last_act.implement_datetime actual_end_time

    -- カンバン毎の最後の実績
    FROM trn_actual_result last_act
    INNER JOIN (
      SELECT kanban_id AS kanban_id, MAX(implement_datetime) last_actual_datetime
        FROM trn_actual_result
        GROUP BY kanban_id
    ) su ON su.kanban_id = last_act.kanban_id AND su.last_actual_datetime = last_act.implement_datetime

) act_info ON act_info.last_actual_kanban_id = kan.kanban_id
WHERE kanban_status = 'COMPLETION';

comment on view view_line_product is 'ライン生産情報';
comment on column view_line_product.kanban_id is 'カンバンID';
comment on column view_line_product.equipment_id is '設備ID';
comment on column view_line_product.actual_end_time is '完了日時';
comment on column view_line_product.model_name is 'モデル名';
comment on column view_line_product.lot_quantity is 'ロット数量';
comment on column view_line_product.defect_num is '不良数';

INSERT INTO mst_displayed_status (status_name, font_color, back_color, light_pattern, notation_name, melody_path,melody_repeat)
 VALUES ('DEFECT', '#000000', '#FFFFFF', 'LIGHTING', '', '', False);

