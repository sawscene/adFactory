
--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★




-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 移行用理由ID新旧対応表への移行

-- 手順1:　中断理由の移行

insert 
into iko_reason
select
1
,a_interrupt_id
,a_interrupt_id
,a_interrupt_reason
,a_font_color
,a_back_color
,a_light_pattern
,0


from dblink(
'DBLINK_adFactoryDB',
'select  a.interrupt_id,a.interrupt_reason,a.font_color,a.back_color,a.light_pattern
from 
mst_interrupt_reason a'
) 
as aa( a_interrupt_id int ,a_interrupt_reason text ,a_font_color text ,a_back_color text ,a_light_pattern text )

;

-- 手順1-1.　シーケンスの作成


--CREATE SEQUENCE mst_reason_reason_id_seq INCREMENT BY 1;

-- 手順1-2.　シーケンスの初期値設定

select setval('mst_reason_reason_id_seq',(select max(reason_id_new) from iko_reason));




-- 手順1:　遅延理由の移行


-- 移行用理由ID新旧対応表への移行

-- 手順2:　中断理由の移行

insert 
into iko_reason
select
2
,( nextval('mst_reason_reason_id_seq'))
,a_delay_id
,a_delay_reason
,a_font_color
,a_back_color
,a_light_pattern
,0


from dblink(
'DBLINK_adFactoryDB',
'select  a.delay_id,a.delay_reason,a.font_color,a.back_color,a.light_pattern
from 
mst_delay_reason a'
) 
as aa( a_delay_id int ,a_delay_reason text ,a_font_color text ,a_back_color text ,a_light_pattern text )

;




-- 手順3:　理由（呼出理由）の移行　■　呼出理由のみが設定されている

insert 
into iko_reason
select
0
,( nextval('mst_reason_reason_id_seq'))
,0
,a_reason
,'#000000'
,'#FFFFFF'
,'LIGHTING'
,a_reason_order


from dblink(
'DBLINK_adFactoryDB',
'select  a.reason , a.reason_order
from 
mst_reason a'
) 
as aa( a_reason text ,a_reason_order int )

;


