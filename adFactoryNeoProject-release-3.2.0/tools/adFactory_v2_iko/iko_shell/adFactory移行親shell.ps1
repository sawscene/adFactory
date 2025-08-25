

#********************************************
#* adFactory　DB移行 
#********************************************


#********************************************
#* adFactory　環境変数path追加　powershell用
#********************************************

## path追加　postgres環境
Set-Item Env:Path "$Env:Path;C:\adFactory\3rd\postgreSQL11\bin\"


## path追加　adFactory移行環境(shell)

Set-Item Env:Path "$Env:Path;C:\adFactory_v2_iko\iko_shell\"


## path追加　adFactory移行環境(sql)
Set-Item Env:Path "$Env:Path;C:\adFactory_v2_iko\iko_sql\"




#********************************************
#* adFactory　変数追加　powershell用
#********************************************

## 移行実施LOG配置場所(変数設定)
Set-Item Env:LOGPATH "C:\adFactory_v2_iko\data\log\"


## 移行実施バックアップデータ配置場所(変数設定)
Set-Item Env:BUCKUPPATH "C:\adFactory_v2_iko\data\backup\"


## 移行実施shell配置場所(変数設定)
Set-Item Env:SHELLPATH "C:\adFactory_v2_iko\iko_shell\"


## 移行実施SQL配置場所(変数設定)
Set-Item Env:SQLPATH "C:\adFactory_v2_iko\iko_sql\"


#********************************************
#* adFactory　DB移行 実施
#********************************************

echo " ★　★　★adFactory　リファクタリング移行開始（親）　START　★　★　★";date

## ログファイル名生成

$formatted_date = (Get-Date).ToString("yyyyMMdd")
$dmp_file_name = "adFactoryDB_"+$formatted_date+".log"
$fullpathlog = $ENV:LOGPATH+$dmp_file_name





##　ディレクトリをshell配置場所に移動

cd $Env:SHELLPATH




## 移行実施時のviewの削除や作成
adFactory移行事前処理.ps1 > $fullpathlog 


## 移行実施
adFactory移行実行.ps1 >>  $fullpathlog 


echo " ★　★　★adFactory　リファクタリング移行終了（親）　END　★　★　★";date

## 上記実行許可を行わないで起動する方法
##powershell -ExecutionPolicy RemoteSigned .\adFactory移行実行.ps1





