@echo off
set RESULT=0

set JAVA_HOME=C:\adFactory\3rd\jdk21.0.4_7
set MAVEN_HOME=C:\tools\apache-maven-3.9.9
set GIT_HOME=C:\Program Files\Git
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%GIT_HOME%\cmd;%PATH%


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

set CUR=%~dp0
set ADFACTORY_OUTPUT=%~dp0adFactoryInstaller
echo [Version]>%ADFACTORY_OUTPUT%\version.ini
echo Ver=%ADFACTORY_OUTERNAL_VERSION%.%BUILD_NUM%>>%ADFACTORY_OUTPUT%\version.ini
echo InternalVer=%ADFACTORY_INTERNAL_VERSION%.%BUILD_NUM%>>%ADFACTORY_OUTPUT%\version.ini
echo Branch=%BRANCH_NAME%>>%ADFACTORY_OUTPUT%\version.ini
echo CommitId=%COMMIT_ID%>>%ADFACTORY_OUTPUT%\version.ini


REM ====================================================================
REM 
REM フォルダ再構築
REM 
REM ====================================================================

del /q %ADFACTORY_OUTPUT%\bin\adAgendaMonitor.exe
del /q %ADFACTORY_OUTPUT%\bin\adAndonApp.exe
del /q %ADFACTORY_OUTPUT%\bin\adDatabaseApp.exe
del /q %ADFACTORY_OUTPUT%\bin\adFactorySettingTool.exe
del /q %ADFACTORY_OUTPUT%\bin\adInterfaceService.exe
del /q %ADFACTORY_OUTPUT%\bin\adManagerApp.exe
del /q %ADFACTORY_OUTPUT%\bin\DashboardCustomizeTool.exe
del /q %ADFACTORY_OUTPUT%\bin\Dockingcontrollayout.dll
del /q %ADFACTORY_OUTPUT%\bin\GuiTestTool.exe
del /q %ADFACTORY_OUTPUT%\bin\adFloorLayoutEditor.exe

rd /s /q %ADFACTORY_OUTPUT%\CmdSatellite
rd /s /q %ADFACTORY_OUTPUT%\plugin
rd /s /q %ADFACTORY_OUTPUT%\war
rd /s /q %ADFACTORY_OUTPUT%adLinkService

mkdir %ADFACTORY_OUTPUT%\CmdSatellite > NUL 2>&1
mkdir %ADFACTORY_OUTPUT%\logs > NUL 2>&1
mkdir %ADFACTORY_OUTPUT%\plugin > NUL 2>&1
mkdir %ADFACTORY_OUTPUT%\war > NUL 2>&1

mkdir %ADFACTORY_OUTPUT%\adLinkService > NUL 2>&1
mkdir %ADFACTORY_OUTPUT%\adLinkService\bin > NUL 2>&1
mkdir %ADFACTORY_OUTPUT%\adLinkService\plugin > NUL 2>&1


REM ====================================================================
REM 
REM それぞれのプロジェクトをビルドする.
REM 
REM ====================================================================

REM ====================================================================
REM 共通ライブラリ
REM ====================================================================
cd %CUR%adFactoryCommon\CommonEntity
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adFactoryCommon\ClientService
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adFactoryCommon\JavafxCommon
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adInterfaceService\adInterfaceServiceCommon
call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adAndon\adAndonAppCommon
call release.bat %ADFACTORY_INTERNAL_VERSION%


REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryServer
call release.bat %ADFACTORY_INTERNAL_VERSION%


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
REM cd %CUR%adFactoryLocale\StandardLocalePlugin
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

REM cd %CUR%adFactoryLocale\WarehouseLocalePlugin
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

cd %CUR%adFactoryLocale
call LocaleBuild.bat

REM ====================================================================
REM adManagerApp
REM ====================================================================
REM adManager 生産管理アプリケーション
cd %CUR%adManagerApp\adManagerApp
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 進捗モニタ設定プラグイン
cd %CUR%adManagerApp\adManagerAndonSettingPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 設備編集プラグイン
cd %CUR%adManagerApp\adManagerEquipmentEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM カンバン編集プラグイン
cd %CUR%adManagerApp\adManagerKanbanEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ラインタイマー制御プラグイン
cd %CUR%adManagerApp\adManagerLineTimerPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 組織編集プラグイン
cd %CUR%adManagerApp\adManagerOrganizationEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 実績出力プラグイン
cd %CUR%adManagerApp\adManagerReportOutPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM システム設定プラグイン
cd %CUR%adManagerApp\adManagerSystemSettingPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

 REM 工程・工程順編集プラグイン
cd %CUR%adManagerApp\adManagerWorkflowEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 作業日報プラグイン
cd %CUR%adManagerApp\adManagerWorkReportPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ELS様向け進捗モニタ設定プラグイン
cd %CUR%adManagerApp\adManaMonitorSettingPluginELS
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM トプコン様向け進捗モニタ設定プラグイン
cd %CUR%adManagerApp\adManaMonitorSettingPluginTP
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 作業分析プラグイン
cd %CUR%adManagerApp\adManagerChartPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 生産管理プラグイン
cd %CUR%adManagerApp\adManaProductionNaviPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 倉庫案内プラグイン
cd %CUR%adManagerApp\adManagerWarehousePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM サマリーレポートプラグイン
cd %CUR%adManagerApp\adManagerSummaryReportEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM サマリーレポートプラグイン
cd %CUR%adManagerApp\adManagerLedgerManagerPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 品番編集プラグイン
cd %CUR%adManagerApp\adManagerDsItemEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adInterfaceService
REM ====================================================================
REM adInterface 管理サービス
cd %CUR%adInterfaceService\adInterfaceService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 倉庫案内サービス
cd %CUR%adInterfaceService\WarehouseServicePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM DbImportService プラグイン(TIPS様向け)
cd %CUR%adInterfaceService\DbImportService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM DbOutputServiceプラグイン(TIPS様向け)
cd %CUR%adInterfaceService\DbOutputService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 自動インポートサービスプラグイン
cd %CUR%adInterfaceService\ImportService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM データ出力サービスプラグイン(FUJI様向け)
cd %CUR%adInterfaceService\ExportServicePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM デイリーレポート
cd %CUR%adInterfaceService\SummaryReportPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 装置連携
cd %CUR%adInterfaceService\DeviceConnectionServicePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adMonitor
REM ====================================================================
REM adMonitor アプリケーション
cd %CUR%adAndon\adAndonApp
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM レイアウトツール
cd %CUR%adAndon\adAndonCustomizeTool
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 日付時刻フレーム
cd %CUR%adAndon\adAndonClockPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 遅延理由カウントフレーム
cd %CUR%adAndon\adAndonDailyDelayNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 中断理由カウントフレーム
cd %CUR%adAndon\adAndonDailyInterruptNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 当日実績進捗数フレーム
cd %CUR%adAndon\adAndonDailyPlanDeviatedNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 当日実績進捗時間フレーム
cd %CUR%adAndon\adAndonDailyPlanDeviatedTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 当日計画実績数フレーム
cd %CUR%adAndon\adAndonDailyPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 設備計画実績数フレーム
cd %CUR%adAndon\adMonitorEquipmentPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 工程設備ステータスフレーム
cd %CUR%adAndon\adMonitorEquipmentStatusPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ラインカウントダウンフレーム
cd %CUR%adAndon\adAndonLineCountDownPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ライン全体ステータスフレーム
cd %CUR%adAndon\adMonitorLineStatusPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ラインタクトタイムフレーム
cd %CUR%adAndon\adAndonLineTaktTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 当月実績進捗数フレーム
cd %CUR%adAndon\adAndonMonthlyPlanDeviatedNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 当月計画実績数フレーム
cd %CUR%adAndon\adAndonMonthlyPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM タイトルフレーム
cd %CUR%adAndon\adAndonTitlePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 当日工程計画実績数フレーム
cd %CUR%adAndon\adAndonDailyWorkPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 予実モニタ アプリケーション
cd %CUR%adAndon\AgendaAndonPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 工程進捗フレーム
cd %CUR%adAndon\adMonitorWorkStatusPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM エデックリンセイシステム様向け当日実績進捗フレーム
cd %CUR%adAndon\adMonitorDailyLineTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 余白フレーム
cd %CUR%adAndon\adMonitorBlankSpacePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM グループ進捗フレーム
cd %CUR%adAndon\adMonitorWorkDeliveryPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 工程実績進捗フレーム
cd %CUR%adAndon\adMonitorEquipmentDeliveryPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 中断発生率フレーム
cd %CUR%adAndon\adMonitorSuspendedRatePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 作業終了予想時間フレーム
cd %CUR%adAndon\adMonitorEstimatedTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 工程別計画実績数フレーム
cd %CUR%adAndon\adAndonWorkPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 俯瞰モニターフレーム
cd %CUR%adAndon\adMonitorFloorPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 出来高差異フレーム
cd %CUR%adAndon\adMonitorYieldDiffPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adFactorySettingTool
REM ====================================================================
cd %CUR%adFactorySettingTool
call release.bat %ADFACTORY_INTERNAL_VERSION%


REM ====================================================================
REM tools
REM ====================================================================
REM 工数連携データ出力
REM cd %CUR%tools\adFactoryFujiActualDataOutput
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 工数連携データ出力(FUJI豊田用) 
REM cd %CUR%tools\adFactoryFujiToyotaActualDataOutput
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

REM データベースメンテナンスツール
cd %CUR%adDatabaseApp
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 設定ツール
cd %CUR%tools\adSetupTool
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM レイアウトエディタ
cd %CUR%tools\adFloorLayoutEditor
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM 進捗情報出力
cd %CUR%tools\adBridgeBI
call release.bat %ADFACTORY_INTERNAL_VERSION%


REM ====================================================================
REM adLinkService
REM ====================================================================
REM adLinkService本体
cd %CUR%adLinkService\adLinkService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM adLinkBarcode
cd %CUR%adLinkService\adLinkBarcode
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM adLinkBarcode
cd %CUR%adLinkService\adLinkFPInspection
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM adLinkServerURLReceiver
cd %CUR%adLinkService\adLinkServerURLReceiver
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adWorkbook
REM ====================================================================
REM cd %CUR%adWorkbookAddIn
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM 
REM インストーラーを作る.
REM 
REM ====================================================================
cd %CUR%

REM 標準インストーラ
ISCC.exe %CUR%adFactoryInstaller\install.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM 標準インストーラ(クライアントのみ)
ISCC.exe %CUR%adFactoryInstaller\install_clientApps.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM 標準インストーラ(adManagerのみ)
ISCC.exe %CUR%adFactoryInstaller\install_adManagerApps.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM アジェンダモニタインストーラ
ISCC.exe %CUR%adFactoryInstaller\install_AgendaMonitor.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM adLinkService インストーラ
ISCC.exe %CUR%adFactoryInstaller\install_adLinkService.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

exit /b %RESULT%
