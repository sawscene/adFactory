@echo off
rem PATH=%PATH%;C:\Program Files\NetBeans 8.0.1\java\maven\bin;C:\Program Files (x86)\Launch4j;C:\Program Files (x86)\Inno Setup 5

rem 第1引数 バージョン
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 1.4.0)>"
)

rem 第2引数 ビルド番号
if NOT "%2" EQU "" (
  set BUILD_NUM=%2
) else (
  set BUILD_NUM=%HISTORY_NUM%
)

set ADFACTORY_INTERNAL_VERSION=%VERSION%
set ADFACTORY_OUTERNAL_VERSION=%VERSION%

REM ====================================================================
REM 
REM gitコマンドで、コミット数、ブランチ名、コミットIDを取得し、
REM バージョン情報に残す.
REM 
REM ====================================================================
set TERM=msys
git log --oneline | wc -l | sed -e "s/[ \t]*//g">tmp.txt
set /p HISTORY_NUM=<tmp.txt
git rev-parse --abbrev-ref HEAD>tmp.txt
set /p BRANCH_NAME=<tmp.txt
git log -n 1 --format=%%H>tmp.txt
set /p COMMIT_ID=<tmp.txt
del /q tmp.txt

set CUR=%~dp0
set ADFACTORY_FOR_FUJI_OUTPUT=%~dp0adFactoryInstaller\adFactoryForFujiInstaller
echo [Version]>%ADFACTORY_FOR_FUJI_OUTPUT%\version.ini
echo Ver=%ADFACTORY_OUTERNAL_VERSION%.%BUILD_NUM%>>%ADFACTORY_FOR_FUJI_OUTPUT%\version.ini
echo InternalVer=%ADFACTORY_INTERNAL_VERSION%.%BUILD_NUM%>>%ADFACTORY_FOR_FUJI_OUTPUT%\version.ini
echo Branch=%BRANCH_NAME%>>%ADFACTORY_FOR_FUJI_OUTPUT%\version.ini
echo CommitId=%COMMIT_ID%>>%ADFACTORY_FOR_FUJI_OUTPUT%\version.ini


REM ====================================================================
REM 
REM フォルダ再構築
REM 
REM ====================================================================

del /q %ADFACTORY_FOR_FUJI_OUTPUT%\bin\adCellProductionMonitor.exe

rd /s /q %ADFACTORY_FOR_FUJI_OUTPUT%\plugin
rd /s /q %ADFACTORY_FOR_FUJI_OUTPUT%\war

mkdir %ADFACTORY_FOR_FUJI_OUTPUT%\bin > NUL 2>&1
mkdir %ADFACTORY_FOR_FUJI_OUTPUT%\logs > NUL 2>&1
mkdir %ADFACTORY_FOR_FUJI_OUTPUT%\plugin > NUL 2>&1
mkdir %ADFACTORY_FOR_FUJI_OUTPUT%\war > NUL 2>&1

REM ====================================================================

REM 
REM それぞれのプロジェクトをビルドする.
REM 
REM ====================================================================

REM ====================================================================
REM 共通ライブラリ
REM ====================================================================
cd %CUR%..\adFactoryCommon\CommonEntity
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%..\adFactoryCommon\ClientService
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%..\adFactoryCommon\JavafxCommon
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%..\adAndon\adAndonAppCommon
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adFactoryCommon\adFactoryForFujiCommon\ForFujiAppCommon
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adCollaboKit
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryForFujiServer
call release.bat %ADFACTORY_INTERNAL_VERSION%


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
cd %CUR%
cd ..
cd adFactoryLocale\ForFujiLocalePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adManagerApp
REM ====================================================================
cd %CUR%adManager\adManagerAppAnalysisPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adManager\adManagerAppSchedulePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adManager\adManagerAppUnitPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adManager\adManagerAppUnitTemplatePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adManager\adManaMonitorSettingPluginFuji
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adManager\adManaProductionNaviPluginFuji
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adAndon
REM ====================================================================
cd %CUR%adMonitor\adCellProductionMonitor
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adMonitor\adMonitorCycleTaktTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adFactoryFujiActualDataOutput
REM ====================================================================
cd %CUR%..\tools\adFactoryFujiActualDataOutput
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM 
REM インストーラーを作る.
REM 
REM ====================================================================

cd %CUR%
ISCC.exe %CUR%adFactoryInstaller\adFactoryForFujiInstaller\install.iss
ISCC.exe %CUR%adFactoryInstaller\adFactoryForFujiInstaller\install_clientApps.iss
