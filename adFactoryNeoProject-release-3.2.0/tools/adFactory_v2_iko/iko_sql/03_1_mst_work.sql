

--　★★★★★★★★★★★★★★★★
--　★　　　移行先DBで実施　
--　★★★★★★★★★★★★★★★★




-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 工程マスタ
-- 手順1:　工程マスタの移行

insert 
into mst_work

select
a_work_id
,a_work_name
,a_takt_time
,a_content
,a_content_type
,a_fk_update_person_id
,a_update_datetime
,a_remove_flag
,a_font_color
,a_back_color
,a_use_parts
,a_work_number
---  ■　検査情報
,(select  json_agg(JSON_REC.*)::json from ( select  key , type , val , disp , cat , opt , max , min , tag , rules , page , cp 
    from
	  (select  work_prop_name as key
			,work_prop_type as type
			,work_prop_value as val
			,work_prop_order as disp
	   	    ,work_prop_category as cat
			,work_prop_option as opt
			,work_prop_lower_tolerance as min
			,work_prop_upper_tolerance as max
			,work_prop_tag as tag
			,work_prop_validation_rule as rules
			,work_section_order as page
			,work_prop_checkpoint cp
	  
      from iko_mst_work_property 
    where
      fk_master_id = a_work_id
	  and work_prop_category is not null
	order by disp ) ZZ
  ) JSON_REC
)
---  ■　追加情報
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp 
    from
	  (select work_prop_name as key
			,work_prop_type as type
			,work_prop_value as val
			,work_prop_order as disp
	  
      from iko_mst_work_property 
    where
      fk_master_id = a_work_id
	  and work_prop_category is null
	order by disp ) ZZ
  ) JSON_REC
)
 ---  ■　サービス情報　★　仮の設定
,null
,1


from dblink
(
'DBLINK_adFactoryDB',
'select
a.work_id
,a.work_name
,a.takt_time
,a.content
,a.content_type
,a.fk_update_person_id
,a.update_datetime
,a.remove_flag
,a.font_color
,a.back_color
,a.use_parts
,a.work_number
from 
mst_work a 
'
) 
as LINK1
(
a_work_id int
,a_work_name text
,a_takt_time int
,a_content text
,a_content_type text 
,a_fk_update_person_id int
,a_update_datetime TIMESTAMP
,a_remove_flag boolean
,a_font_color text 
,a_back_color text
,a_use_parts text
,a_work_number text

)

;

