
--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★



-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 理由マスタへの移行

-- 手順1:　理由マスタの移行

insert 
into mst_reason
select
reason_id_new
,0
,reason_type
,reason
,font_color
,back_color
,light_pattern
,reason_order
,1


from iko_reason

;



-- 確認

--select * from iko_reason;

--select reason,count(*) from iko_reason
--group by reason
--having count(*) > 1
--;

