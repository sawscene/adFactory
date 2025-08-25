
--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★




-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 06_1　組織・役割関連付けの移行


insert
into con_organization_role
select 

a_fk_organization_id
,a_fk_role_id



from dblink(
'DBLINK_adFactoryDB',
'select  
a.fk_organization_id
,a.fk_role_id

from 
con_organization_role a'
) 
as aa( 
a_fk_organization_id int
,a_fk_role_id int


 )

;

-- 確認

--select * from con_organization_role
--order by organization_id,role_id
--;

