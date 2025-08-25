

--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　休憩マスタを移行


insert
into mst_breaktime

select

a_breaktime_id
,a_name
,a_starttime
,a_endtime
,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.breaktime_id
,a.name
,a.starttime
,a.endtime


from mst_breaktime a

'
)
as LINK1
(
a_breaktime_id int
,a_name text
,a_starttime timestamp
,a_endtime timestamp



)
;


--　確認

--select * from mst_breaktime
--;


