@echo off
rem PATH=%PATH%;C:\Program Files\NetBeans 8.0.1\java\maven\bin;C:\Program Files (x86)\Launch4j
set APPLICATION_NAME=WTResultCopier

rem 第1引数 バージョン
if NOT "%1" EQU "" (
  set ADFACTORY_VERSION=%1
) else (
  set /p ADFACTORY_VERSION="input version>"
)

rem 第2引数 デプロイするか
IF "%2" EQU "deploy" (
  SET DEPLOY_FLG="1"
) ELSE (
  SET DEPLOY_FLG="0"
)

call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%ADFACTORY_VERSION%
IF %ERRORLEVEL% NEQ 0 GOTO FAILURE

IF %DEPLOY_FLG% EQU "1" (
  call mvn clean deploy
) ELSE (
  call mvn clean package
)
IF %ERRORLEVEL% NEQ 0 GOTO FAILURE

call mvn dependency:tree -DoutputFile=target/dependencylist.txt
IF %ERRORLEVEL% NEQ 0 GOTO FAILURE

GOTO SUCCESS

rem ビルド失敗時の処理
:FAILURE
SET errNo=%ERRORLEVEL%
color 0C
echo errorNo=%errNo%
echo .
echo .
echo .
echo BUILD ERROR
echo .
echo .
echo .

cd \
GOTO EOF

rem ビルド成功時の処理
:SUCCESS
SET errNo=%ERRORLEVEL%
echo errorNo=%errNo%
echo .
echo 正常終了
echo .

copy target\%APPLICATION_NAME%-%ADFACTORY_VERSION%.jar target\%APPLICATION_NAME%.jar
call launch4jc launch4j.xml
if NOT "%ADFACTORY_OUTPUT%" EQU "" (
  @echo on
  copy /Y target\%APPLICATION_NAME%.exe %ADFACTORY_OUTPUT%\bin\
)

:EOF
