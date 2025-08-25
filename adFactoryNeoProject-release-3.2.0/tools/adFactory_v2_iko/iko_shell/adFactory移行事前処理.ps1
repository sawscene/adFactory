


#********************************************
#* adFactory　DB移行 実行のためのCD
#********************************************


## sqlファイルのdirへ移動
## ★　実施する環境に変更する事！


echo "★　実行dirへcd"
##$SQLPATH="C:\Users\seo\Desktop\SVN_adFactory\trunk\070_移行\SQL\"
$SQLPATH="C:\adFactory_v2_iko\iko_sql\"

echo $ENV:SQLPATH

cd $ENV:SQLPATH

#********************************************
#* adFactory　DB移行 環境整備 & 実行
#********************************************
## ログイン　psql -h localhost -p 15432 -U postgres -d adFactoryDB2

## ■　■　postgres　環境変数設定　■　■


##　データベース名
Set-Item Env:PGDATABASE "adFactoryDB2"

##　ホスト名
Set-Item Env:PGHOST "localhost"

##　ポート
Set-Item Env:PGPORT "15432"

##　ユーザー
Set-Item Env:PGUSER "postgres"

##　パスワード
Set-Item Env:PGPASSWORD "@dtek1977"

##　エラー報告
Set-Item Env:VERBOSITY "verbose"


$ENV:PGDATABASE
$ENV:PGHOST
$ENV:PGPORT
$ENV:PGUSER
$ENV:PGPASSWORD




#********************************************
#* adFactory　DB移行 実行 SEQの作成
#********************************************




## sqlファイルの実行

## 例　psql -d adFactoryDB_V2 -U postgres -f END_VACUUM.sql

echo " ★　★　adFactory　リファクタリング事前処理　START　★　★";date




echo "■　移行用テーブル作成" ; date
psql -f 00_create_iko_tables.sql




echo "■　DBLINK 作成" ; date
## 初回のみ
psql -f 00_0_DBLINK_create.sql 


echo "■　1/2　■　SEQ削除" ; date
## 不要
##psql -f 00_0_BEFOR_seq_drop.sql 


##echo "■　1/2　■　SEQ作成" ; date
## 不要
##psql -f 00_0_BEFOR_seq.sql 




echo "■　view Drop" ; date
## 初回は不要
##psql -f 00_0_BEFOR_view_drop.sql


##　各種view作成　★　2回目以降は上記viewの削除を実施する事

## 工程順　JSON用　カンバンプロパティテンプレート　mst_kanban_property_template
echo "■04_0_mst_workflow_json_view" ; date
psql -f 04_0_mst_workflow_json_view.sql

##　設備　JSON用　*設備マスタプロパティ　mst_equipment_property
echo "■05_0_mst_equipment_json_view" ; date
psql -f 05_0_mst_equipment_json_view.sql

##　組織　JSON用　*組織マスタプロパティ　mst_organization_property
echo "■07_0_mst_organization_json_view" ; date
psql -f 07_0_mst_organization_json_view.sql




echo " ★　★　adFactory　リファクタリング事前処理　END　★　★"
date





