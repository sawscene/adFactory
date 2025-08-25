

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 手順1:　認証情報を移行


insert
into mst_authentication_info

select

a_authentication_id
,a_fk_mastger_id
,a_authentication_type
,a_authentication_data
,a_validity_period
,a_use_lock

,1


from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.authentication_id
,a.fk_mastger_id
,a.authentication_type
,a.authentication_data
,a.validity_period
,a.use_lock


from mst_authentication_info a

'
)
as LINK1
(
a_authentication_id int
,a_fk_mastger_id int
,a_authentication_type text
,a_authentication_data text
,a_validity_period timestamp
,a_use_lock boolean


)
;


--　確認

--select * from mst_authentication_info
--;


