
--　移行用view削除



--  ■　VIEWの　DROP


-- エラーが発生した場合に処理を中断させます。
\set ON_ERROR_STOP 1


-- データベース　接続開始


--【 DBLINK VIEW作成】
--  ■ 追加情報


--　工程順　JSON用　*カンバンプロパティ　mst_kanban_property_template
drop View VW_DBLIK_adFactoryDB_workflow_add_info;



--　設備　JSON用　*設備マスタプロパティ　mst_equipment_property

drop View VW_DBLIK_adFactoryDB_equipment_add_info;




--　組織　JSON用　*組織マスタプロパティ　mst_organization_property
drop View VW_DBLIK_adFactoryDB_organization_add_info;



