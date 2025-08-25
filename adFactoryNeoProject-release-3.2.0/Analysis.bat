@echo off

set CUR=%~dp0


REM ====================================================================
REM
REM それぞれのプロジェクトを分析する.
REM
REM ====================================================================

REM ====================================================================
REM 共通ライブラリ
REM ====================================================================
cd %CUR%adFactoryCommon\CommonEntity
call mvn sonar:sonar

cd %CUR%adFactoryCommon\JavafxCommon
call mvn sonar:sonar

cd %CUR%adFactoryCommon\ClientService
call mvn sonar:sonar

cd %CUR%adInterfaceService\adInterfaceServiceCommon
call mvn sonar:sonar

cd %CUR%adAndon\adAndonAppCommon
call mvn sonar:sonar


REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryServer
call mvn sonar:sonar


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
cd %CUR%adFactoryLocale\StandardLocalePlugin
call mvn sonar:sonar


REM ====================================================================
REM adManagerApp
REM ====================================================================
cd %CUR%adManagerApp\adManagerApp
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerAndonSettingPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerEquipmentEditPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerKanbanEditPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerLedgerOutPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerLineTimerPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerOrganizationEditPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerReportOutPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerSystemSettingPlugin
call mvn sonar:sonar

cd %CUR%adManagerApp\adManagerWorkflowEditPlugin
call mvn sonar:sonar


REM ====================================================================
REM adInterfaceService
REM ====================================================================
cd %CUR%adInterfaceService\adInterfaceService
call mvn sonar:sonar


REM ====================================================================
REM adAndon
REM ====================================================================
cd %CUR%adAndon\adAndonApp
call mvn sonar:sonar

cd %CUR%adAndon\adAndonCustomizeTool
call mvn sonar:sonar

cd %CUR%adAndon\adAndonClockPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonDailyDelayNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonDailyInterruptNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonDailyPlanDeviatedNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonDailyPlanDeviatedTimePlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonDailyPlanNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonEquipmentPlanNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonEquipmentStatusPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonLineCountDownPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonLineStatusPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonLineTaktTimePlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonMonthlyPlanDeviatedNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonMonthlyPlanNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonTitlePlugin
call mvn sonar:sonar

cd %CUR%adAndon\adAndonDailyWorkPlanNumPlugin
call mvn sonar:sonar

cd %CUR%adAndon\AgendaAndonPlugin
call mvn sonar:sonar


REM ====================================================================
REM adFactorySettingTool
REM ====================================================================
cd %CUR%adFactorySettingTool
call mvn sonar:sonar

cd %CUR%adDatabaseApp
call mvn sonar:sonar


REM ====================================================================
REM tools
REM ====================================================================
REM cd %CUR%tools\adFactoryFujiActualDataOutput
REM call mvn sonar:sonar

REM cd %CUR%tools\GuiTestTool
REM call mvn sonar:sonar

cd %CUR%
goto :EOF


:SUB
rem コードを分析します
mvn sonar:sonar
goto :EOF
