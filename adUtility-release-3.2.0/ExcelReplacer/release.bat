@echo off
rem PATH=%PATH%;C:\Program Files\NetBeans 8.0.1\java\maven\bin

rem 第1引数 バージョン
if NOT "%1" EQU "" (
	call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%1
)

rem 共通ライブラリのためローカルリポジトリに配置
call mvn clean install

call mvn dependency:tree -DoutputFile=target/dependencylist.txt

