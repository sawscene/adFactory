

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');



-- 手順1:　工程カンバン・設備関連付けを移行


insert
into con_workkanban_equipment

select

a_fk_workkanban_id
,a_fk_equipment_id





from dblink 
(
'DBLINK_adFactoryDB',
'select 
a.fk_workkanban_id
,a.fk_equipment_id


from con_workkanban_equipment a

'
)
as LINK1
(
a_fk_workkanban_id int
,a_fk_equipment_id int


)
;


--　確認

--select * from con_workkanban_equipment
--;


