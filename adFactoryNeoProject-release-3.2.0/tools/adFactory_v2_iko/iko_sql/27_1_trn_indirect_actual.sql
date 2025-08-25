

--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　間接工数実績を移行

insert
into trn_indirect_actual

select

a_indirect_actual_id
,a_fk_indirect_work_id
,a_implement_datetime
,a_transaction_id
,a_fk_organization_id
,a_work_time



from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.indirect_actual_id
,a.fk_indirect_work_id
,a.implement_datetime
,a.transaction_id
,a.fk_organization_id
,a.work_time


from trn_indirect_actual a

'
)
as LINK1
(
a_indirect_actual_id int
,a_fk_indirect_work_id int
,a_implement_datetime timestamp
,a_transaction_id int
,a_fk_organization_id int
,a_work_time int


)
;


--　確認

--select * from trn_indirect_actual
--;


