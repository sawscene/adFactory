
--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');


-- 06_1　ステータス表示マスタの移行


insert
into mst_displayed_status

select 
a_status_id
,null
,a_status_name
,a_font_color
,a_back_color
,a_light_pattern
,a_notation_name
,a_melody_path
,a_melody_repeat
,1


from dblink(
'DBLINK_adFactoryDB',
'select  
a.status_id
,a.status_name
,a.font_color
,a.back_color
,a.light_pattern
,a.notation_name
,a.melody_path
,a.melody_repeat
from 
mst_displayed_status a'
) 
as aa( 
a_status_id int
,a_status_name text
,a_font_color text
,a_back_color text
,a_light_pattern text
,a_notation_name text
,a_melody_path text
,a_melody_repeat boolean


 )

;

-- 確認

--select * from mst_displayed_status
--order by status_id,organization_id,status_name
--;

