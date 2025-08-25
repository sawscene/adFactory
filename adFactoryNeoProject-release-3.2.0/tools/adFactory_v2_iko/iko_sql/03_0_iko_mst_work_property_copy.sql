

--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　工程マスタプロパティのコピー


insert
into iko_mst_work_property

select
a_work_prop_id
,a_fk_master_id
,a_work_prop_name
,a_work_prop_type
,a_work_prop_value
,a_work_prop_order
,a_work_prop_category
,a_work_prop_option
,a_work_prop_lower_tolerance
,a_work_prop_upper_tolerance
,a_work_prop_tag
,a_work_prop_validation_rule
,a_work_section_order
,a_work_prop_checkpoint



from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.work_prop_id
,a.fk_master_id
,a.work_prop_name
,a.work_prop_type
,a.work_prop_value
,a.work_prop_order
,a.work_prop_category
,a.work_prop_option
,a.work_prop_lower_tolerance
,a.work_prop_upper_tolerance
,a.work_prop_tag
,a.work_prop_validation_rule
,a.work_section_order
,a.work_prop_checkpoint


from mst_work_property a

'
)
as LINK1
(
a_work_prop_id int
,a_fk_master_id int
,a_work_prop_name text
,a_work_prop_type  text
,a_work_prop_value text
,a_work_prop_order int
,a_work_prop_category text
,a_work_prop_option text
,a_work_prop_lower_tolerance double precision
,a_work_prop_upper_tolerance double precision
,a_work_prop_tag  text
,a_work_prop_validation_rule text
,a_work_section_order int
,a_work_prop_checkpoint int


)
;



-- アナライズとりインデックス

--ANALYZE VERBOSE iko_trn_actual_property;

--REINDEX table iko_trn_actual_property;

VACUUM  FULL    ANALYZE     iko_mst_work_property   ;



--　確認

--select * from iko_trn_actual_property
--;

