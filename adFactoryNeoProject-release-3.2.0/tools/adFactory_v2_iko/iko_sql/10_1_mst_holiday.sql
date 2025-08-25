

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★




-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 手順1:　休日情報を移行


insert
into mst_holiday

select

a_holiday_id
,a_holiday_name
,a_holiday_date
,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.holiday_id
,a.holiday_name
,a.holiday_date


from mst_holiday a

'
)
as LINK1
(
a_holiday_id int
,a_holiday_name text
,a_holiday_date timestamp

)
;


--　確認

--select * from mst_holiday
--;


