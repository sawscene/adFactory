-- Project Name : adFactoryForFujiDB_v1.4.2_20160926
-- Date/Time    : 2016/10/14 13:29:18
-- Author       : ek.mori
-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2

-- DBバージョン
create table T_VER (
  SID numeric(1,0) not null
  , VERNO character varying(20) not null
  , constraint T_VER_PKC primary key (SID)
) ;

alter table T_VER add constraint T_VER_IX1
  unique (SID) ;

create unique index T_VER_IX2
  on T_VER(VERNO);

-- ユニットテンプレート階層
create table tre_unit_template_hierarchy (
  parent_id bigint not null
  , child_id bigint not null
  , constraint tre_unit_template_hierarchy_PKC primary key (parent_id,child_id)
) ;

alter table tre_unit_template_hierarchy add constraint tre_unit_template_hierarchy_IX1
  unique (parent_id,child_id) ;

-- ユニットテンプレート階層マスタ
create table mst_unit_template_hierarchy (
  unit_template_hierarchy_id bigserial not null
  , hierarchy_name character varying(256) not null
  , constraint mst_unit_template_hierarchy_PKC primary key (unit_template_hierarchy_id)
) ;

alter table mst_unit_template_hierarchy add constraint mst_unit_template_hierarchy_IX1
  unique (unit_template_hierarchy_id) ;

-- ユニットテンプレート階層関連付け
create table con_unit_template_hierarchy (
  fk_unit_template_hierarchy_id bigint not null
  , fk_unit_template_id bigint not null
  , constraint con_unit_template_hierarchy_PKC primary key (fk_unit_template_hierarchy_id,fk_unit_template_id)
) ;

alter table con_unit_template_hierarchy add constraint con_unit_template_hierarchy_IX1
  unique (fk_unit_template_hierarchy_id,fk_unit_template_id) ;

-- 生産ユニット階層
create table tre_unit_hierarchy (
  parent_id bigint not null
  , child_id bigint not null
  , constraint tre_unit_hierarchy_PKC primary key (parent_id,child_id)
) ;

alter table tre_unit_hierarchy add constraint tre_unit_hierarchy_IX1
  unique (parent_id,child_id) ;

-- 生産ユニット階層マスタ
create table mst_unit_hierarchy (
  unit_hierarchy_id bigserial not null
  , hierarchy_name character varying(256) not null
  , constraint mst_unit_hierarchy_PKC primary key (unit_hierarchy_id)
) ;

alter table mst_unit_hierarchy add constraint mst_unit_hierarchy_IX1
  unique (unit_hierarchy_id) ;

-- 生産ユニット階層関連付け
create table con_unit_hierarchy (
  fk_unit_hierarchy_id bigint not null
  , fk_unit_id bigint not null
  , constraint con_unit_hierarchy_PKC primary key (fk_unit_hierarchy_id,fk_unit_id)
) ;

alter table con_unit_hierarchy add constraint con_unit_hierarchy_IX1
  unique (fk_unit_hierarchy_id,fk_unit_id) ;

-- 生産ユニット関連付け
create table con_unit_associate (
  unit_associate_id bigserial not null
  , fk_parent_unit_id bigint not null
  , fk_kanban_id bigint
  , fk_unit_id bigint
  , unit_associate_order integer
  , constraint con_unit_associate_PKC primary key (unit_associate_id)
) ;

alter table con_unit_associate add constraint con_unit_associate_IX1
  unique (fk_parent_unit_id,fk_kanban_id) ;

alter table con_unit_associate add constraint con_unit_associate_IX2
  unique (fk_parent_unit_id,fk_unit_id) ;

-- 生産ユニット
create table trn_unit (
  unit_id bigserial not null
  , unit_name character varying(256) not null
  , fk_unit_template_id bigint not null
  , workflow_diaglam text not null
  , start_datetime timestamp without time zone
  , comp_datetime timestamp without time zone
  , fk_update_person_id bigint
  , update_datetime timestamp without time zone
  , constraint trn_unit_PKC primary key (unit_id)
) ;

alter table trn_unit add constraint trn_unit_IX1
  unique (unit_id) ;

create index trn_unit_IX2
  on trn_unit(fk_unit_template_id);

create index trn_unit_IX3
  on trn_unit(unit_name);

create index trn_unit_IX4
  on trn_unit(start_datetime);

create index trn_unit_IX5
  on trn_unit(comp_datetime);

-- 生産ユニットプロパティ
create table trn_unit_property (
  unit_property_id bigserial not null
  , fk_unit_id bigint not null
  , unit_property_name character varying(256) not null
  , unit_property_type character varying(128) not null
  , unit_property_value text
  , unit_property_order integer
  , constraint trn_unit_property_PKC primary key (unit_property_id)
) ;

alter table trn_unit_property add constraint trn_unit_property_IX1
  unique (unit_property_id) ;

create index trn_unit_property_IX2
  on trn_unit_property(unit_property_name);

create index trn_unit_property_IX3
  on trn_unit_property(unit_property_value);

-- ユニットテンプレート関連付け
create table con_unit_template_associate (
  unit_template_associate_id bigserial not null
  , fk_parent_unit_template_id bigint not null
  , fk_workflow_id bigint
  , fk_unit_template_id bigint
  , unit_template_associate_order integer
  , standard_start_time timestamp without time zone
  , standard_end_time timestamp without time zone
  , constraint con_unit_template_associate_PKC primary key (unit_template_associate_id)
) ;

alter table con_unit_template_associate add constraint con_unit_template_associate_IX1
  unique (fk_parent_unit_template_id,fk_workflow_id) ;

alter table con_unit_template_associate add constraint con_unit_template_associate_IX2
  unique (fk_parent_unit_template_id,fk_unit_template_id) ;

-- ユニットテンプレートプロパティ
create table mst_unit_template_property (
  unit_template_property_id bigserial not null
  , fk_unit_template_id bigint not null
  , unit_template_property_name character varying(256) not null
  , unit_template_property_type character varying(128) not null
  , unit_template_property_value text
  , unit_template_property_order integer
  , constraint mst_unit_template_property_PKC primary key (unit_template_property_id)
) ;

alter table mst_unit_template_property add constraint mst_unit_template_property_IX1
  unique (unit_template_property_id) ;

create index mst_unit_template_property_IX2
  on mst_unit_template_property(unit_template_property_name);

create index mst_unit_template_property_IX3
  on mst_unit_template_property(unit_template_property_value);

-- ユニットテンプレート
create table mst_unit_template (
  unit_template_id bigserial not null
  , unit_template_name character varying(256) not null
  , workflow_diaglam text not null
  , fk_output_kanban_hierarchy_id bigint
  , fk_update_person_id bigint
  , update_datetime timestamp without time zone
  , remove_flag boolean
  , constraint mst_unit_template_PKC primary key (unit_template_id)
) ;

alter table mst_unit_template add constraint mst_unit_template_IX1
  unique (unit_template_id) ;

create index mst_unit_template_IX2
  on mst_unit_template(unit_template_name);

comment on table T_VER is 'DBバージョン	 adFactoryForFujiDB Version Definition';
comment on column T_VER.SID is 'ID';
comment on column T_VER.VERNO is 'DBバージョン';

comment on table tre_unit_template_hierarchy is 'ユニットテンプレート階層	 ユニットテンプレート階層の親子関連付けテーブル';
comment on column tre_unit_template_hierarchy.parent_id is '階層親ID	 階層の親IDの管理';
comment on column tre_unit_template_hierarchy.child_id is '階層子ID	 階層の子IDの管理.階層親ID,子IDは重複してはいけない';

comment on table mst_unit_template_hierarchy is 'ユニットテンプレート階層マスタ	 ユニットテンプレートを管理するための階層のマスターデータ';
comment on column mst_unit_template_hierarchy.unit_template_hierarchy_id is 'ユニットテンプレート階層ID	 階層情報の主キー';
comment on column mst_unit_template_hierarchy.hierarchy_name is 'ユニットテンプレート階層名	 階層名。ユニークにはしないがシステム内で同じ階層に同じ名前の階層が存在しないようにチェックすること';

comment on table con_unit_template_hierarchy is 'ユニットテンプレート階層関連付け	 ユニットテンプレートと階層テーブルを関連付けるテーブル';
comment on column con_unit_template_hierarchy.fk_unit_template_hierarchy_id is 'ユニットテンプレート階層ID	 ユニットテンプレート階層IDを管理する';
comment on column con_unit_template_hierarchy.fk_unit_template_id is 'ユニットテンプレートID	 ユニットテンプレートIDを管理する';

comment on table tre_unit_hierarchy is '生産ユニット階層	 生産ユニット階層の親子関連付けテーブル';
comment on column tre_unit_hierarchy.parent_id is '階層親ID	 階層の親IDの管理';
comment on column tre_unit_hierarchy.child_id is '階層子ID	 階層の子IDの管理.階層親ID,子IDは重複してはいけない';

comment on table mst_unit_hierarchy is '生産ユニット階層マスタ	 生産ユニットを管理するための階層のマスターデータ';
comment on column mst_unit_hierarchy.unit_hierarchy_id is '生産ユニット階層ID	 階層情報の主キー';
comment on column mst_unit_hierarchy.hierarchy_name is '生産ユニット階層名	 階層名。ユニークにはしないがシステム内で同じ階層に同じ名前の階層が存在しないようにチェックすること';

comment on table con_unit_hierarchy is '生産ユニット階層関連付け	 ユニットテンプレートと階層テーブルを関連付けるテーブル';
comment on column con_unit_hierarchy.fk_unit_hierarchy_id is '生産ユニット階層ID	 ユニットテンプレート階層IDを管理する';
comment on column con_unit_hierarchy.fk_unit_id is '生産ユニットID	 ユニットテンプレートIDを管理する';

comment on table con_unit_associate is '生産ユニット関連付け	 生産ユニットとカンバンの紐付を行います。このテーブルはカスタムテーブルと標準テーブルの関連付けになるためリレーションはシステム内で手動で行ってください。';
comment on column con_unit_associate.unit_associate_id is '生産ユニット関連付けID';
comment on column con_unit_associate.fk_parent_unit_id is '親生産ユニットID	 親階層のユニットID';
comment on column con_unit_associate.fk_kanban_id is '子カンバンID	 子階層のカンバンID';
comment on column con_unit_associate.fk_unit_id is '子生産ユニットID	 子階層の生産ユニットID';
comment on column con_unit_associate.unit_associate_order is '表示順	 表示順番。ユニットテンプレートに依存。';

comment on table trn_unit is '生産ユニット	 ユニットテンプレートを元に作成された製造予定情報';
comment on column trn_unit.unit_id is '生産ユニットID	 生産ユニット固有のユニークID';
comment on column trn_unit.unit_name is '生産ユニット名	 生産ユニット固有の名前(シリアルやオーダといった生産番号)';
comment on column trn_unit.fk_unit_template_id is 'ユニット種ID	 ユニット種からIDを取得';
comment on column trn_unit.workflow_diaglam is 'ワークフロー	 生産ユニットのワークフロー';
comment on column trn_unit.start_datetime is '着手予定日	 生産に着手する予定日';
comment on column trn_unit.comp_datetime is '納品予定日	 納品の予定日';
comment on column trn_unit.fk_update_person_id is '更新者	 生産ユニットを更新した人のID';
comment on column trn_unit.update_datetime is '更新日	 生産ユニットを更新した日時';

comment on table trn_unit_property is '生産ユニットプロパティ	 生産ユニットが持っているカスタムフィールド。生産ユニット新規生成時はユニットテンプレートにあるカスタムフィールドをコピーして一緒に登録する。';
comment on column trn_unit_property.unit_property_id is '生産ユニットプロパティID	 生産ユニットのプロパティ固有のID';
comment on column trn_unit_property.fk_unit_id is '生産ユニットID	 生産ユニットのIDを取得';
comment on column trn_unit_property.unit_property_name is 'プロパティ名	 生産ユニットプロパティの名前';
comment on column trn_unit_property.unit_property_type is '型	 生産ユニットプロパティの型';
comment on column trn_unit_property.unit_property_value is '値	 生産ユニットプロパティの内容';
comment on column trn_unit_property.unit_property_order is '表示値	 生産ユニットプロパティの表示順';

comment on table con_unit_template_associate is 'ユニットテンプレート関連付け	 ユニットテンプレートと工程順の紐付を行います。このテーブルはカスタムテーブルと標準テーブルの関連付けになるためリレーションはシステム内で手動で行ってください。';
comment on column con_unit_template_associate.unit_template_associate_id is 'ユニットテンプレート関連付けID';
comment on column con_unit_template_associate.fk_parent_unit_template_id is '親ユニットテンプレートID	 親階層のユニットテンプレートID';
comment on column con_unit_template_associate.fk_workflow_id is '子工程順ID	 子階層の工程順ID';
comment on column con_unit_template_associate.fk_unit_template_id is '子ユニットテンプレートID	 子階層のユニットテンプレートID';
comment on column con_unit_template_associate.unit_template_associate_order is '表示順	 表示順番。ワークフロー生成時に指定すること。';
comment on column con_unit_template_associate.standard_start_time is '基準開始時間';
comment on column con_unit_template_associate.standard_end_time is '基準終了時間';

comment on table mst_unit_template_property is 'ユニットテンプレートプロパティ	 ユニットテンプレートが持っているカスタムフィールド';
comment on column mst_unit_template_property.unit_template_property_id is 'ユニットテンプレートプロパティID	 ユニットテンプレートのプロパティ固有のID';
comment on column mst_unit_template_property.fk_unit_template_id is 'ユニットテンプレートID	 ユニットテンプレートのIDを取得';
comment on column mst_unit_template_property.unit_template_property_name is 'プロパティ名	 ユニットテンプレートプロパティの名前';
comment on column mst_unit_template_property.unit_template_property_type is '型	 ユニットテンプレートプロパティの型';
comment on column mst_unit_template_property.unit_template_property_value is '値	 ユニットテンプレートプロパティの内容';
comment on column mst_unit_template_property.unit_template_property_order is '表示値	 ユニットテンプレートプロパティの表示順';

comment on table mst_unit_template is 'ユニットテンプレート	 生産するユニット(機種)の作業テンプレートデータ';
comment on column mst_unit_template.unit_template_id is 'ユニットテンプレートID	 ユニットテンプレート固有のユニークID';
comment on column mst_unit_template.unit_template_name is 'ユニットテンプレート名	 ユニットテンプレート固有の名前(AIMEX,NXTといった製品名
や本機組付といった作業をくくる情報)';
comment on column mst_unit_template.workflow_diaglam is 'ワークフロー図	 ユニットテンプレートを更新した人のID';
comment on column mst_unit_template.fk_update_person_id is '更新者	 ユニットテンプレートを更新した人のID';
comment on column mst_unit_template.fk_output_kanban_hierarchy_id is 'カンバン出力先階層	 工程順をカンバンにする際のカンバンの保存先';
comment on column mst_unit_template.update_datetime is '更新日	 ユニットテンプレートを更新した日時';
comment on column mst_unit_template.remove_flag is '論理削除フラグ	 削除した際に表示上から消すためのフラグ';

