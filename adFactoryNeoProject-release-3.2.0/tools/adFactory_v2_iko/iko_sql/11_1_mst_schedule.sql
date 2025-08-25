

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　予定情報を移行


insert
into mst_schedule

select

a_schedule_id
,a_schedule_name
,a_schedule_from_date
,a_schedule_to_date
,a_fk_organization_id
,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.schedule_id
,a.schedule_name
,a.schedule_from_date
,a.schedule_to_date
,a.fk_organization_id

from mst_schedule a

'
)
as LINK1
(
a_schedule_id int
,a_schedule_name text
,a_schedule_from_date date
,a_schedule_to_date date
,a_fk_organization_id int


)
;


--　確認

--select * from mst_schedule
--;


