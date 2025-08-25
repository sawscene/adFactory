
--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★


--  ★　★　テーブルの用途を変更　将来的なテーブルの為移行不要とする　★　★




-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLIMK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=postgres');


-- カンバン
-- 手順1:　工程実績付加情報の移行


insert 
into trn_actual_adition
select

a_actual_id
---  ■　検査結果
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp
    from
	    (select 
				fk_actual_id
				,actual_prop_name as key
				,actual_prop_type as type
				,actual_prop_value as val
				,actual_prop_order as disp 
				
				from iko_trn_actual_property

    		where
      		fk_actual_id = LINK1.a_actual_id
						
		) ZZ

  ) JSON_REC
)

,null    --  サービス情報　★　仮の設定
,null    --  画像データ


from dblink
(
'DBLIMK_adFactoryDB',
'select
a.actual_id

from 
trn_actual_result a 
'
) 
as LINK1
(
a_actual_id int
)

;


