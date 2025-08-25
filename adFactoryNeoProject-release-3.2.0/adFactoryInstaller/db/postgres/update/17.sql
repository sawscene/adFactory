-- 「工程マスタ」テーブルにカラムを追加する
ALTER TABLE mst_work ADD work_rev integer default 1 not null; -- 版数
ALTER TABLE mst_work ADD approval_id bigint; -- 申請ID
ALTER TABLE mst_work ADD approval_state integer default 4; -- 承認状態

alter table mst_work drop constraint mst_work_idx2;

alter table mst_work add constraint mst_work_name_rev
  unique (work_name,work_rev) ;

create index idx_work_name
  on mst_work(work_name);

comment on column mst_work.work_rev is '版数';
comment on column mst_work.approval_id is '申請ID';
comment on column mst_work.approval_state is '承認状態';

-- 「工程順マスタ」テーブルにカラムを追加する
ALTER TABLE mst_workflow ADD approval_id bigint; -- 申請ID
ALTER TABLE mst_workflow ADD approval_state integer default 4; -- 承認状態

comment on column mst_workflow.approval_id is '申請ID';
comment on column mst_workflow.approval_state is '承認状態';

-- 承認ルート
create table mst_approval_route (
  route_id bigserial not null
  , route_name character varying(256) not null
  , ver_info integer default 1 not null
  , constraint mst_approval_route_pk primary key (route_id)
) ;

comment on table mst_approval_route is '承認ルート';
comment on column mst_approval_route.route_id is 'ルートID';
comment on column mst_approval_route.route_name is 'ルート名';
comment on column mst_approval_route.ver_info is '排他用バージョン';

-- 承認順
create table mst_approval_order (
  route_id bigint not null
  , approval_order integer not null
  , organization_id bigint not null
  , approval_final boolean default false not null
  , constraint mst_approval_order_pk primary key (route_id,approval_order)
) ;

create index idx_approval_order_route_id
  on mst_approval_order(route_id);

comment on table mst_approval_order is '承認順';
comment on column mst_approval_order.route_id is 'ルートID';
comment on column mst_approval_order.approval_order is '承認順';
comment on column mst_approval_order.organization_id is '組織ID';
comment on column mst_approval_order.approval_final is '最終承認者';

-- 申請
create table trn_approval (
  approval_id bigserial not null
  , route_id bigint not null
  , requestor_id bigint not null
  , request_datetime timestamp without time zone not null
  , approval_state integer not null
  , data_type integer not null
  , new_data bigint not null
  , old_data bigint
  , approval_history jsonb
  , comment text
  , constraint trn_approval_pk primary key (approval_id)
) ;

comment on table trn_approval is '申請';
comment on column trn_approval.approval_id is '申請ID';
comment on column trn_approval.route_id is 'ルートID';
comment on column trn_approval.requestor_id is '申請者';
comment on column trn_approval.request_datetime is '申請日';
comment on column trn_approval.approval_state is '承認状態:0:未承認, 1:申請中, 2:取消, 3:却下, 4:最終承認済';
comment on column trn_approval.data_type is 'データ種別:0:工程, 1:工程順, 2:カンバン';
comment on column trn_approval.new_data is '新しいデータ';
comment on column trn_approval.old_data is '古いデータ:新規データの承認ではnull';
comment on column trn_approval.approval_history is '承認履歴';
comment on column trn_approval.comment is '申請コメント';

-- 承認フロー
create table trn_approval_flow (
  approval_id bigint not null
  , approval_order integer not null
  , approval_final boolean default false not null
  , approver_id bigint not null
  , approval_datetime timestamp without time zone
  , approval_state integer not null
  , comment text
  , constraint trn_approval_flow_pk primary key (approval_id,approval_order)
) ;

create index idx_approval_flow_approval_id
  on trn_approval_flow(approval_id);

comment on table trn_approval_flow is '承認フロー';
comment on column trn_approval_flow.approval_id is '申請ID';
comment on column trn_approval_flow.approval_order is '承認順';
comment on column trn_approval_flow.approval_final is '最終承認';
comment on column trn_approval_flow.approver_id is '承認者';
comment on column trn_approval_flow.approval_datetime is '操作日時';
comment on column trn_approval_flow.approval_state is '承認状態:0:未承認, 1:申請中, 2:取消, 3:却下, 4:最終承認済';
comment on column trn_approval_flow.comment is 'コメント';


-- 使用部品
create table trn_assembly_parts (
  parent_flag character varying(1)
  , parts_id character varying(10)
  , product_name character varying(256)
  , product_number character varying(256)
  , revision bigint
  , ano character varying(30)
  , pno character varying(9)
  , serial_number character varying(128)
  , kanban character varying(128)
  , kanban_parts_id character varying(128)
  , delivered_at timestamp with time zone
  , fixed_date timestamp with time zone
  , delivered_request_id bigint
  , bracket_flag character varying(1)
  , qauntity bigint
  , assembled_flag character varying(1)
  , fixed_flag character varying(1)
  , control_no character varying(36)
  , parent_no character varying(30)
  , person_no character varying(256)
  , update_date timestamp with time zone
  , ver_info integer default 1
  , constraint trn_assembly_parts_pk primary key (parts_id)
) ;

create index idx_assembly_parts_kanban
  on trn_assembly_parts(kanban);

comment on table trn_assembly_parts is '使用部品';
comment on column trn_assembly_parts.parent_flag is '親品目:Y: 親品目
N: 親品目以外';
comment on column trn_assembly_parts.parts_id is 'PID';
comment on column trn_assembly_parts.product_name is '品名';
comment on column trn_assembly_parts.product_number is '品目コード';
comment on column trn_assembly_parts.revision is 'Rev:最大10桁';
comment on column trn_assembly_parts.ano is 'ANO';
comment on column trn_assembly_parts.pno is 'PNO';
comment on column trn_assembly_parts.serial_number is 'シリアル番号';
comment on column trn_assembly_parts.kanban is 'カンバン名';
comment on column trn_assembly_parts.kanban_parts_id is 'カンバン親PID';
comment on column trn_assembly_parts.delivered_at is '払出日';
comment on column trn_assembly_parts.fixed_date is '使用確定日時:部品使用確定ボタンを押下した日時';
comment on column trn_assembly_parts.delivered_request_id is '出庫依頼番号:最大10桁';
comment on column trn_assembly_parts.bracket_flag is 'ブランケット品:Y: ブランケット品
N: ブランケット品以外';
comment on column trn_assembly_parts.qauntity is '数量:最大10桁';
comment on column trn_assembly_parts.assembled_flag is '使用／未使用:Y: 使用
N: 未使用';
comment on column trn_assembly_parts.fixed_flag is '使用確定フラグ:Y: 確定
N: 未確定';
comment on column trn_assembly_parts.control_no is '製番';
comment on column trn_assembly_parts.parent_no is '親ANO';
comment on column trn_assembly_parts.person_no is '社員番号';
comment on column trn_assembly_parts.update_date is '更新日時';
comment on column trn_assembly_parts.ver_info is '排他用バージョン';


-- 「工程実績ビュー」に「工程の版数」カラムを追加する。
CREATE OR REPLACE VIEW view_actual_result AS
  SELECT
    -- 実績ID
    act.actual_id,
    -- カンバンID
    act.kanban_id AS fk_kanban_id,
    -- 工程順ID
    act.workflow_id AS fk_workflow_id,
    -- 工程カンバンID
    act.work_kanban_id AS fk_work_kanban_id,
    -- 工程ID
    act.work_id AS fk_work_id,
    -- 組織ID
    act.organization_id AS fk_organization_id,
    -- 設備ID
    act.equipment_id AS fk_equipment_id,
    -- カンバン名
    kan.kanban_name,
    -- 工程名
    wk.work_name,
    -- 組織名
    org.organization_name,
    -- 設備名
    eq.equipment_name,
    -- 実施日時
    act.implement_datetime,
    -- ステータス
    act.actual_status,
    -- 作業時間
    act.work_time,
    -- 中断理由
    act.interrupt_reason,
    -- 遅延理由
    act.delay_reason,
    -- モデル名
    kan.model_name,
    -- 工程の版数
    wk.work_rev

  FROM trn_actual_result act

  -- カンバンを結合
  LEFT JOIN trn_kanban kan ON kan.kanban_id = act.kanban_id
  -- 工程を結合
  LEFT JOIN mst_work wk ON wk.work_id = act.work_id
  -- 組織を結合
  LEFT JOIN mst_organization org ON org.organization_id = act.organization_id
  -- 設備を結合
  LEFT JOIN mst_equipment eq ON eq.equipment_id = act.equipment_id

  WHERE act.actual_status <> 'OTHER'
;

comment on view view_actual_result is '工程実績ビュー';
comment on column view_actual_result.actual_id is '実績ID';
comment on column view_actual_result.fk_kanban_id is 'カンバンID';
comment on column view_actual_result.fk_workflow_id is '工程順ID';
comment on column view_actual_result.fk_work_kanban_id is '工程カンバンID';
comment on column view_actual_result.fk_work_id is '工程ID';
comment on column view_actual_result.fk_organization_id is '組織ID';
comment on column view_actual_result.fk_equipment_id is '設備ID';
comment on column view_actual_result.kanban_name is 'カンバン名';
comment on column view_actual_result.work_name is '工程名';
comment on column view_actual_result.organization_name is '組織名';
comment on column view_actual_result.equipment_name is '設備名';
comment on column view_actual_result.implement_datetime is '実施日時';
comment on column view_actual_result.actual_status is 'ステータス';
comment on column view_actual_result.work_time is '作業時間';
comment on column view_actual_result.interrupt_reason is '中断理由';
comment on column view_actual_result.delay_reason is '遅延理由';
comment on column view_actual_result.model_name is 'モデル名';
comment on column view_actual_result.work_rev is '工程の版数';
