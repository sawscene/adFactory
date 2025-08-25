
--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★

-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');

-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- 移行用階層ID新旧対応表への移行

-- 手順1:　工程の移行

insert 
into iko_hierarchy
select
 0
,a_work_hierarchy_id
,a_work_hierarchy_id
,b_parent_id
,a_hierarchy_name


from dblink('DBLINK_adFactoryDB',
'select  a.work_hierarchy_id,a.hierarchy_name,b.parent_id 
from mst_work_hierarchy a LEFT JOIN tre_work_hierarchy b on  a.work_hierarchy_id = b.child_id order by a.work_hierarchy_id') 
as aa( a_work_hierarchy_id int ,a_hierarchy_name text ,b_parent_id int );


-- 親IDの0が原本DBに存在しない為、0を作成する。 * 階層IDが0のレコードは移行しない（親ID=0の設定に必要なため、移行用に強制的に設定しているため）

insert
into iko_hierarchy
VALUES (
 0
,0
,0
,0
,'iko only'
);



-- 手順1-1.　シーケンスの作成  →　前準備へ

-- drop SEQUENCE mst_hierarchy_hierarchy_id_seq CASCADE;

--CREATE SEQUENCE mst_hierarchy_hierarchy_id_seq INCREMENT BY 1;

-- 手順1-2.　シーケンスの初期値設定

select setval('mst_hierarchy_hierarchy_id_seq',(select max(hierarchy_id_new) from iko_hierarchy))
;




-- 手順2:　工程順の移行

insert 
into iko_hierarchy
select
 1
,( nextval('mst_hierarchy_hierarchy_id_seq'))
,a_workflow_hierarchy_id
,b_parent_id
,a_hierarchy_name



from dblink(
'DBLINK_adFactoryDB',
'select  a.workflow_hierarchy_id,a.hierarchy_name,b.parent_id 
from mst_workflow_hierarchy a LEFT JOIN tre_workflow_hierarchy b on a.workflow_hierarchy_id = b.child_id order by a.workflow_hierarchy_id') 
as t1( a_workflow_hierarchy_id int ,a_hierarchy_name text ,b_parent_id int )
;


-- 親IDの0が原本DBに存在しない為、0を作成する。  * 階層IDが0のレコードは移行しない（親ID=0の設定に必要なため、移行用に強制的に設定しているため）

insert
into iko_hierarchy
VALUES (
 1
,0
,0
,0
,'iko only'
);




--truncate table iko_hierarchy;


