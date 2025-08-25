@echo off

if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 2.17.0)>"
)

set CUR=%~dp0


REM ====================================================================
REM ‹¤’Êƒ‰ƒCƒuƒ‰ƒŠ
REM ====================================================================
cd %CUR%..\adFactoryCommon\CommonEntity
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%..\adFactoryCommon\ClientService
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%..\adFactoryCommon\JavafxCommon
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%..\adAndon\adAndonAppCommon
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adFactoryCommon\adFactoryForFujiCommon\ForFujiAppCommon
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adCollaboKit
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryForFujiServer
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
cd %CUR%
cd ..
cd adFactoryLocale\ForFujiLocalePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ====================================================================
REM adManagerApp
REM ====================================================================
cd %CUR%adManager\adManagerAppAnalysisPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adManager\adManagerAppSchedulePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adManager\adManagerAppUnitPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adManager\adManagerAppUnitTemplatePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adManager\adManaMonitorSettingPluginFuji
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adManager\adManaProductionNaviPluginFuji
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ====================================================================
REM adAndon
REM ====================================================================
cd %CUR%adMonitor\adCellProductionMonitor
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adMonitor\adMonitorCycleTaktTimePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ====================================================================
REM adFactoryFujiActualDataOutput
REM ====================================================================
cd %CUR%..\tools\adFactoryFujiActualDataOutput
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
