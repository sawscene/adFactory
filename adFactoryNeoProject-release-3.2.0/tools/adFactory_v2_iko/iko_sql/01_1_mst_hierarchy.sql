

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 01_0　移行用階層ID新旧対応表

-- 01_1　階層マスタ 工程 * 階層IDが0のレコードは移行しない（親ID=0の設定に必要なため、移行用に強制的に設定しているため）

insert
into mst_hierarchy
select 
a.hierarchy_id_new
,0
,a.hierarchy_name
-- 親階層ID
,(select c.hierarchy_id_new from iko_hierarchy c
where
a.parent_id_old = c.hierarchy_id_old and c.hierarchy_type = 0)
,1

from iko_hierarchy a
where
 a.hierarchy_type = 0
 and a.hierarchy_id_new <> 0
 ;

-- 01_1　階層マスタ 工程順　 * 階層IDが0のレコードは移行しない（親ID=0の設定に必要なため、移行用に強制的に設定しているため）

insert
into mst_hierarchy
select 
a.hierarchy_id_new
,1
,a.hierarchy_name
-- 親階層ID
,( 
select c.hierarchy_id_new from iko_hierarchy c
where
a.parent_id_old = c.hierarchy_id_old and c.hierarchy_type = 1
)
,1

from iko_hierarchy a
where
 a.hierarchy_type = 1
  and a.hierarchy_id_new <> 0;



--　親IDのNULLを0に更新
update mst_hierarchy
set
parent_hierarchy_id = 0
where
parent_hierarchy_id is null;








