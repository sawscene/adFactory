@echo off
set RESULT=0

rem 第1引数 バージョン
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 1.4.0)>"
)

set CUR=%~dp0


REM ====================================================================
REM 共通ライブラリ
REM ====================================================================
cd %CUR%..\adFactoryCommon\CommonEntity
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%..\adFactoryCommon\ClientService
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%..\adFactoryCommon\JavafxCommon
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%..\adAndon\adAndonAppCommon
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adFactoryCommon\adFactoryForFujiCommon\ForFujiAppCommon
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adCollaboKit
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryForFujiServer
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
cd %CUR%
cd ..
cd adFactoryLocale\ForFujiLocalePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adManagerApp
REM ====================================================================
cd %CUR%adManager\adManagerAppAnalysisPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adManager\adManagerAppSchedulePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adManager\adManagerAppUnitPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adManager\adManagerAppUnitTemplatePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adManager\adManaMonitorSettingPluginFuji
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adManager\adManaProductionNaviPluginFuji
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adAndon
REM ====================================================================
cd %CUR%adMonitor\adCellProductionMonitor
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adMonitor\adMonitorCycleTaktTimePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adFactoryFujiActualDataOutput
REM ====================================================================
cd %CUR%..\tools\adFactoryFujiActualDataOutput
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


exit /b %RESULT%
