

#********************************************
#* adFactory　DB移行 実行のためのCD
#********************************************


## sqlファイルのdirへ移動
## ★　実施する環境に変更する事！

cd C:\Users\seo\Desktop\SVN_adFactory\trunk\070_移行\SQL

#********************************************
#* adFactory　DB移行 一次的な環境整備 & 実行
#********************************************
## ログイン　psql -h localhost -p 5432 -U postgres -d adFactoryDB_V2

## ■　■　postgres　環境変数設定　■　■


##　データベース名
Set-Item Env:PGDATABASE "postgres"
##Set-Item Env:PGDATABASE "adFactoryDB_ORG"

##　ホスト名
Set-Item Env:PGHOST "localhost"

##　ポート
Set-Item Env:PGPORT "5432"

##　ユーザー
Set-Item Env:PGUSER "postgres"

##　パスワード
Set-Item Env:PGPASSWORD "postgres"

##　エラー報告
Set-Item Env:VERBOSITY "verbose"





#********************************************
#* adFactory　DB移行 実行
#********************************************



echo "■　50/51　■　99_DB_NAME_CHAING.sql.sql : rename" ; date


## DB名称変更　adFactoryDB　⇒　adFactoryDB_ORG
psql -f     99_DB_NAME_CHAING_ORG.sql



##　DB名称戻し　adFactoryDB_ORG　⇒　adFactoryDB
##psql -f     99_DB_NAME_CHAING_ORG_RERUN.sql



 if ($? -ne $True)
    {
        echo "***** ERROR *****" $?
        exit
    }
    else
    {
        echo "***** OK *****"
    }






echo " ★　★　adFactory　リファクタリング移行　ENDT　★　★"
date





