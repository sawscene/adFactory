

--　★★★★★★★★★★★★★★★★

--　★　　　移行先DBで実施　

--　★★★★★★★★★★★★★★★★


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始

select dblink_connect('DBLINK_adFactoryDB','host=localhost port=5432 dbname=adFactoryDB user=postgres password=@dtek1977');




-- 組織マスタ
-- 手順1:　組織マスタの移行

insert 
into mst_organization
select

a_organization_id
,a_organization_name
,a_organization_identify
,a_authority_type
,a_language_type
,a_pass_word
,a_mail_address
,a_fk_update_person_id
,a_update_datetime
,a_remove_flag
,a_work_skill
,b_parent_id

---  ■　追加情報
,(select  json_agg(JSON_REC.*)::json from ( select key , type , val , disp 
    from
      VW_DBLIK_adFactoryDB_organization_add_info 
    where
      fk_master_id = a_organization_id
    order by disp
  ) JSON_REC
)
 ---  ■　サービス情報　★　仮の設定
,null
,1


from dblink
(
'DBLINK_adFactoryDB',
'select
a.organization_id
,a.organization_name
,a.organization_identify
,a.authority_type
,a.language_type
,a.pass_word
,a.mail_address
,a.fk_update_person_id
,a.update_datetime
,a.remove_flag
,a.work_skill
,b.parent_id
from 
mst_organization a LEFT JOIN tre_organization_hierarchy b on  a.organization_id = b.child_id
'
) 
as LINK1
(
a_organization_id int
,a_organization_name text
,a_organization_identify text
,a_authority_type text
,a_language_type text
,a_pass_word text
,a_mail_address text
,a_fk_update_person_id int
,a_update_datetime timestamp
,a_remove_flag boolean
,a_work_skill text
,b_parent_id int
)

;




--- 組織マスタ　論理削除フラグがOFFで親組織IDのNULLのデータを0に更新　*論理削除フラグがONのデータはNULLのままとする

UPDATE mst_organization
set
parent_organization_id=0

where
parent_organization_id is null
and remove_flag = false
;










