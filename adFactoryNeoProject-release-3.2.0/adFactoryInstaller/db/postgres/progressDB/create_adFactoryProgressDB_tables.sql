\encoding UTF8;

-- カンバン進捗情報 (日付基準)
drop table if exists trn_kanban_date_progress;
create table trn_kanban_date_progress (
  progress_no text not null
  , kanban_name text not null
  , kanban_status text
  , model_name text
  , info_1 text
  , info_2 text
  , info_3 text
  , info_4 text
  , info_5 text
  , work_name_1 text
  , status_1 text
  , work_name_2 text
  , status_2 text
  , work_name_3 text
  , status_3 text
  , work_name_4 text
  , status_4 text
  , work_name_5 text
  , status_5 text
  , work_name_6 text
  , status_6 text
  , work_name_7 text
  , status_7 text
  , work_name_8 text
  , status_8 text
  , work_name_9 text
  , status_9 text
  , work_name_10 text
  , status_10 text
  , work_name_11 text
  , status_11 text
  , work_name_12 text
  , status_12 text
  , work_name_13 text
  , status_13 text
  , work_name_14 text
  , status_14 text
  , work_name_15 text
  , status_15 text
  , work_name_16 text
  , status_16 text
  , work_name_17 text
  , status_17 text
  , work_name_18 text
  , status_18 text
  , work_name_19 text
  , status_19 text
  , work_name_20 text
  , status_20 text
  , constraint trn_kanban_date_progress_PKC primary key (progress_no)
) ;

comment on table trn_kanban_date_progress is 'カンバン進捗情報 (日付基準)';
comment on column trn_kanban_date_progress.progress_no is 'プログレスNo:グループNo + 連番';
comment on column trn_kanban_date_progress.kanban_name is 'カンバン名';
comment on column trn_kanban_date_progress.kanban_status is 'カンバンステータス';
comment on column trn_kanban_date_progress.model_name is '機種';
comment on column trn_kanban_date_progress.info_1 is '追加情報1:プロジェクトNo';
comment on column trn_kanban_date_progress.info_2 is '追加情報2:ユーザー名';
comment on column trn_kanban_date_progress.info_3 is '追加情報3';
comment on column trn_kanban_date_progress.info_4 is '追加情報4';
comment on column trn_kanban_date_progress.info_5 is '追加情報5';
comment on column trn_kanban_date_progress.work_name_1 is '工程1_名称';
comment on column trn_kanban_date_progress.status_1 is '工程1_ステータス';
comment on column trn_kanban_date_progress.work_name_2 is '工程2_名称';
comment on column trn_kanban_date_progress.status_2 is '工程2_ステータス';
comment on column trn_kanban_date_progress.work_name_3 is '工程3_名称';
comment on column trn_kanban_date_progress.status_3 is '工程3_ステータス';
comment on column trn_kanban_date_progress.work_name_4 is '工程4_名称';
comment on column trn_kanban_date_progress.status_4 is '工程4_ステータス';
comment on column trn_kanban_date_progress.work_name_5 is '工程5_名称';
comment on column trn_kanban_date_progress.status_5 is '工程5_ステータス';
comment on column trn_kanban_date_progress.work_name_6 is '工程6_名称';
comment on column trn_kanban_date_progress.status_6 is '工程6_ステータス';
comment on column trn_kanban_date_progress.work_name_7 is '工程7_名称';
comment on column trn_kanban_date_progress.status_7 is '工程7_ステータス';
comment on column trn_kanban_date_progress.work_name_8 is '工程8_名称';
comment on column trn_kanban_date_progress.status_8 is '工程8_ステータス';
comment on column trn_kanban_date_progress.work_name_9 is '工程9_名称';
comment on column trn_kanban_date_progress.status_9 is '工程9_ステータス';
comment on column trn_kanban_date_progress.work_name_10 is '工程10_名称';
comment on column trn_kanban_date_progress.status_10 is '工程10_ステータス';
comment on column trn_kanban_date_progress.work_name_11 is '工程11_名称';
comment on column trn_kanban_date_progress.status_11 is '工程11_ステータス';
comment on column trn_kanban_date_progress.work_name_12 is '工程12_名称';
comment on column trn_kanban_date_progress.status_12 is '工程12_ステータス';
comment on column trn_kanban_date_progress.work_name_13 is '工程13_名称';
comment on column trn_kanban_date_progress.status_13 is '工程13_ステータス';
comment on column trn_kanban_date_progress.work_name_14 is '工程14_名称';
comment on column trn_kanban_date_progress.status_14 is '工程14_ステータス';
comment on column trn_kanban_date_progress.work_name_15 is '工程15_名称';
comment on column trn_kanban_date_progress.status_15 is '工程15_ステータス';
comment on column trn_kanban_date_progress.work_name_16 is '工程16_名称';
comment on column trn_kanban_date_progress.status_16 is '工程16_ステータス';
comment on column trn_kanban_date_progress.work_name_17 is '工程17_名称';
comment on column trn_kanban_date_progress.status_17 is '工程17_ステータス';
comment on column trn_kanban_date_progress.work_name_18 is '工程18_名称';
comment on column trn_kanban_date_progress.status_18 is '工程18_ステータス';
comment on column trn_kanban_date_progress.work_name_19 is '工程19_名称';
comment on column trn_kanban_date_progress.status_19 is '工程19_ステータス';
comment on column trn_kanban_date_progress.work_name_20 is '工程20_名称';
comment on column trn_kanban_date_progress.status_20 is '工程20_ステータス';

-- カンバン進捗情報 (工程基準)
drop table if exists trn_kanban_work_progress;
create table trn_kanban_work_progress (
  progress_no text not null
  , group_no integer default 0
  , seq_no integer not null
  , kanban_id bigint not null
  , kanban_name text not null
  , kanban_status text
  , model_name text
  , info_1 text
  , info_2 text
  , info_3 text
  , info_4 text
  , info_5 text
  , work_name_1 text
  , start_date_1 text
  , status_1 text
  , today_flg_1 text
  , work_name_2 text
  , start_date_2 text
  , status_2 text
  , today_flg_2 text
  , work_name_3 text
  , start_date_3 text
  , status_3 text
  , today_flg_3 text
  , work_name_4 text
  , start_date_4 text
  , status_4 text
  , today_flg_4 text
  , work_name_5 text
  , start_date_5 text
  , status_5 text
  , today_flg_5 text
  , work_name_6 text
  , start_date_6 text
  , status_6 text
  , today_flg_6 text
  , work_name_7 text
  , start_date_7 text
  , status_7 text
  , today_flg_7 text
  , work_name_8 text
  , start_date_8 text
  , status_8 text
  , today_flg_8 text
  , work_name_9 text
  , start_date_9 text
  , status_9 text
  , today_flg_9 text
  , work_name_10 text
  , start_date_10 text
  , status_10 text
  , today_flg_10 text
  , work_name_11 text
  , start_date_11 text
  , status_11 text
  , today_flg_11 text
  , work_name_12 text
  , start_date_12 text
  , status_12 text
  , today_flg_12 text
  , work_name_13 text
  , start_date_13 text
  , status_13 text
  , today_flg_13 text
  , work_name_14 text
  , start_date_14 text
  , status_14 text
  , today_flg_14 text
  , work_name_15 text
  , start_date_15 text
  , status_15 text
  , today_flg_15 text
  , work_name_16 text
  , start_date_16 text
  , status_16 text
  , today_flg_16 text
  , work_name_17 text
  , start_date_17 text
  , status_17 text
  , today_flg_17 text
  , work_name_18 text
  , start_date_18 text
  , status_18 text
  , today_flg_18 text
  , work_name_19 text
  , start_date_19 text
  , status_19 text
  , today_flg_19 text
  , work_name_20 text
  , start_date_20 text
  , status_20 text
  , today_flg_20 text
  , constraint trn_kanban_work_progress_PKC primary key (progress_no)
) ;

comment on table trn_kanban_work_progress is 'カンバン進捗情報 (工程基準)';
comment on column trn_kanban_work_progress.progress_no is 'プログレスNo:グループNo + 連番';
comment on column trn_kanban_work_progress.group_no is 'グループNo';
comment on column trn_kanban_work_progress.seq_no is '連番:グループNoで一意な値(1 ～ 999)';
comment on column trn_kanban_work_progress.kanban_id is 'カンバンID';
comment on column trn_kanban_work_progress.kanban_name is 'カンバン名';
comment on column trn_kanban_work_progress.kanban_status is 'カンバンステータス';
comment on column trn_kanban_work_progress.model_name is '機種';
comment on column trn_kanban_work_progress.info_1 is '追加情報1:プロジェクトNo';
comment on column trn_kanban_work_progress.info_2 is '追加情報2:ユーザー名';
comment on column trn_kanban_work_progress.info_3 is '追加情報3';
comment on column trn_kanban_work_progress.info_4 is '追加情報4';
comment on column trn_kanban_work_progress.info_5 is '追加情報5';
comment on column trn_kanban_work_progress.work_name_1 is '工程1_名称';
comment on column trn_kanban_work_progress.start_date_1 is '工程1_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_1 is '工程1_ステータス';
comment on column trn_kanban_work_progress.today_flg_1 is '工程1_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_2 is '工程2_名称';
comment on column trn_kanban_work_progress.start_date_2 is '工程2_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_2 is '工程2_ステータス';
comment on column trn_kanban_work_progress.today_flg_2 is '工程2_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_3 is '工程3_名称';
comment on column trn_kanban_work_progress.start_date_3 is '工程3_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_3 is '工程3_ステータス';
comment on column trn_kanban_work_progress.today_flg_3 is '工程3_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_4 is '工程4_名称';
comment on column trn_kanban_work_progress.start_date_4 is '工程4_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_4 is '工程4_ステータス';
comment on column trn_kanban_work_progress.today_flg_4 is '工程4_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_5 is '工程5_名称';
comment on column trn_kanban_work_progress.start_date_5 is '工程5_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_5 is '工程5_ステータス';
comment on column trn_kanban_work_progress.today_flg_5 is '工程5_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_6 is '工程6_名称';
comment on column trn_kanban_work_progress.start_date_6 is '工程6_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_6 is '工程6_ステータス';
comment on column trn_kanban_work_progress.today_flg_6 is '工程6_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_7 is '工程7_名称';
comment on column trn_kanban_work_progress.start_date_7 is '工程7_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_7 is '工程7_ステータス';
comment on column trn_kanban_work_progress.today_flg_7 is '工程7_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_8 is '工程8_名称';
comment on column trn_kanban_work_progress.start_date_8 is '工程8_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_8 is '工程8_ステータス';
comment on column trn_kanban_work_progress.today_flg_8 is '工程8_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_9 is '工程9_名称';
comment on column trn_kanban_work_progress.start_date_9 is '工程9_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_9 is '工程9_ステータス';
comment on column trn_kanban_work_progress.today_flg_9 is '工程9_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_10 is '工程10_名称';
comment on column trn_kanban_work_progress.start_date_10 is '工程10_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_10 is '工程10_ステータス';
comment on column trn_kanban_work_progress.today_flg_10 is '工程10_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_11 is '工程11_名称';
comment on column trn_kanban_work_progress.start_date_11 is '工程11_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_11 is '工程11_ステータス';
comment on column trn_kanban_work_progress.today_flg_11 is '工程11_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_12 is '工程12_名称';
comment on column trn_kanban_work_progress.start_date_12 is '工程12_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_12 is '工程12_ステータス';
comment on column trn_kanban_work_progress.today_flg_12 is '工程12_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_13 is '工程13_名称';
comment on column trn_kanban_work_progress.start_date_13 is '工程13_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_13 is '工程13_ステータス';
comment on column trn_kanban_work_progress.today_flg_13 is '工程13_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_14 is '工程14_名称';
comment on column trn_kanban_work_progress.start_date_14 is '工程14_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_14 is '工程14_ステータス';
comment on column trn_kanban_work_progress.today_flg_14 is '工程14_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_15 is '工程15_名称';
comment on column trn_kanban_work_progress.start_date_15 is '工程15_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_15 is '工程15_ステータス';
comment on column trn_kanban_work_progress.today_flg_15 is '工程15_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_16 is '工程16_名称';
comment on column trn_kanban_work_progress.start_date_16 is '工程16_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_16 is '工程16_ステータス';
comment on column trn_kanban_work_progress.today_flg_16 is '工程16_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_17 is '工程17_名称';
comment on column trn_kanban_work_progress.start_date_17 is '工程17_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_17 is '工程17_ステータス';
comment on column trn_kanban_work_progress.today_flg_17 is '工程17_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_18 is '工程18_名称';
comment on column trn_kanban_work_progress.start_date_18 is '工程18_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_18 is '工程18_ステータス';
comment on column trn_kanban_work_progress.today_flg_18 is '工程18_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_19 is '工程19_名称';
comment on column trn_kanban_work_progress.start_date_19 is '工程19_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_19 is '工程19_ステータス';
comment on column trn_kanban_work_progress.today_flg_19 is '工程19_本日フラグ:0:違う, 1:本日';
comment on column trn_kanban_work_progress.work_name_20 is '工程20_名称';
comment on column trn_kanban_work_progress.start_date_20 is '工程20_計画開始日付:yyyy/MM/dd';
comment on column trn_kanban_work_progress.status_20 is '工程20_ステータス';
comment on column trn_kanban_work_progress.today_flg_20 is '工程20_本日フラグ:0:違う, 1:本日';

-- 個別進捗情報
drop table if exists trn_work_progress;
create table trn_work_progress (
  progress_no text not null
  , progress_type text not null
  , progress_order text not null
  , progress_date text not null
  , start_time text not null
  , comp_time text not null
  , work_name text not null
  , constraint trn_work_progress_PKC primary key (progress_no,progress_type,progress_order)
) ;

comment on table trn_work_progress is '個別進捗情報';
comment on column trn_work_progress.progress_no is 'プログレスNo:グループNo + 連番';
comment on column trn_work_progress.progress_type is '種別:1:計画, 2:実績';
comment on column trn_work_progress.progress_order is '順:No,種別ごとに 1～';
comment on column trn_work_progress.progress_date is '日付:yyyy/MM/dd';
comment on column trn_work_progress.start_time is '開始時間:HH:mm';
comment on column trn_work_progress.comp_time is '終了時間:HH:mm';
comment on column trn_work_progress.work_name is '工程名';

-- 権限付与
GRANT SELECT ON trn_kanban_date_progress, trn_kanban_work_progress, trn_work_progress TO bi_user;
