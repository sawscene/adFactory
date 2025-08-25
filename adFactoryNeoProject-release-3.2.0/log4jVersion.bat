@echo off

if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 2.17.0)>"
)

set CUR=%~dp0


REM ====================================================================
REM 共通ライブラリ
REM ====================================================================
cd %CUR%adFactoryCommon\CommonEntity
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adFactoryCommon\ClientService
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adFactoryCommon\JavafxCommon
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adInterfaceService\adInterfaceServiceCommon
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

cd %CUR%adAndon\adAndonAppCommon
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false


REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryServer
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
cd %CUR%adFactoryLocale\StandardLocalePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM cd %CUR%adFactoryLocale\WarehouseLocalePlugin
REM call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
REM call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false


REM ====================================================================
REM adManagerApp
REM ====================================================================
REM adManager 生産管理アプリケーション
cd %CUR%adManagerApp\adManagerApp
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 進捗モニタ設定プラグイン
cd %CUR%adManagerApp\adManagerAndonSettingPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 
cd %CUR%adManagerApp\adManagerAppKanbanRegistPreprocessPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 設備編集プラグイン
cd %CUR%adManagerApp\adManagerEquipmentEditPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM カンバン編集プラグイン
cd %CUR%adManagerApp\adManagerKanbanEditPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ラインタイマー制御プラグイン
cd %CUR%adManagerApp\adManagerLineTimerPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 組織編集プラグイン
cd %CUR%adManagerApp\adManagerOrganizationEditPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 実績出力プラグイン
cd %CUR%adManagerApp\adManagerReportOutPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM システム設定プラグイン
cd %CUR%adManagerApp\adManagerSystemSettingPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 工程・工程順編集プラグイン
cd %CUR%adManagerApp\adManagerWorkflowEditPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 作業日報プラグイン
cd %CUR%adManagerApp\adManagerWorkReportPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ELS様向け進捗モニタ設定プラグイン
cd %CUR%adManagerApp\adManaMonitorSettingPluginELS
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM トプコン様向け進捗モニタ設定プラグイン (v2未対応)
REM cd %CUR%adManagerApp\adManaMonitorSettingPluginTP
REM call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
REM call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 作業分析プラグイン
cd %CUR%adManagerApp\adManagerChartPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 生産管理プラグイン
cd %CUR%adManagerApp\adManaProductionNaviPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 倉庫案内プラグイン
cd %CUR%adManagerApp\adManagerWarehousePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ====================================================================
REM adInterfaceService
REM ====================================================================
REM adInterface 管理サービス
cd %CUR%adInterfaceService\adInterfaceService
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 倉庫案内サービス
cd %CUR%adInterfaceService\WarehouseServicePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM DbImportService プラグイン(TIPS様向け)
cd %CUR%adInterfaceService\DbImportService
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM DbOutputServiceプラグイン(TIPS様向け)
cd %CUR%adInterfaceService\DbOutputService
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 自動インポートサービスプラグイン
cd %CUR%adInterfaceService\ImportService
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM データ出力サービスプラグイン(FUJI様向け)
cd %CUR%adInterfaceService\ExportServicePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM デイリーレポート
cd %CUR%adInterfaceService\SummaryReportPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false


REM ====================================================================
REM adMonitor
REM ====================================================================
REM adMonitor アプリケーション
cd %CUR%adAndon\adAndonApp
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM レイアウトツール
cd %CUR%adAndon\adAndonCustomizeTool
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 日付時刻フレーム
cd %CUR%adAndon\adAndonClockPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 遅延理由カウントフレーム
cd %CUR%adAndon\adAndonDailyDelayNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 中断理由カウントフレーム
cd %CUR%adAndon\adAndonDailyInterruptNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 当日実績進捗数フレーム
cd %CUR%adAndon\adAndonDailyPlanDeviatedNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 当日実績進捗時間フレーム
cd %CUR%adAndon\adAndonDailyPlanDeviatedTimePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 当日計画実績数フレーム
cd %CUR%adAndon\adAndonDailyPlanNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 設備計画実績数フレーム
cd %CUR%adAndon\adMonitorEquipmentPlanNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 工程設備ステータスフレーム
cd %CUR%adAndon\adMonitorEquipmentStatusPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ラインカウントダウンフレーム
cd %CUR%adAndon\adAndonLineCountDownPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ライン全体ステータスフレーム
cd %CUR%adAndon\adMonitorLineStatusPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ラインタクトタイムフレーム
cd %CUR%adAndon\adAndonLineTaktTimePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 当月実績進捗数フレーム
cd %CUR%adAndon\adAndonMonthlyPlanDeviatedNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 当月計画実績数フレーム
cd %CUR%adAndon\adAndonMonthlyPlanNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM タイトルフレーム
cd %CUR%adAndon\adAndonTitlePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 当日工程計画実績数フレーム
cd %CUR%adAndon\adAndonDailyWorkPlanNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 予実モニタ アプリケーション
cd %CUR%adAndon\AgendaAndonPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 工程進捗フレーム
cd %CUR%adAndon\adMonitorWorkStatusPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM エデックリンセイシステム様向け当日実績進捗フレーム
cd %CUR%adAndon\adMonitorDailyLineTimePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 余白フレーム
cd %CUR%adAndon\adMonitorBlankSpacePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM グループ進捗フレーム
cd %CUR%adAndon\adMonitorWorkDeliveryPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 工程実績進捗フレーム
cd %CUR%adAndon\adMonitorEquipmentDeliveryPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 中断発生率フレーム
cd %CUR%adAndon\adMonitorSuspendedRatePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 作業終了予想時間フレーム
cd %CUR%adAndon\adMonitorEstimatedTimePlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 工程別計画実績数フレーム
cd %CUR%adAndon\adAndonWorkPlanNumPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 俯瞰モニターフレーム
cd %CUR%adAndon\adMonitorFloorPlugin
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM ====================================================================
REM adFactorySettingTool
REM ====================================================================
cd %CUR%adFactorySettingTool
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false


REM ====================================================================
REM tools
REM ====================================================================
REM 工数連携データ出力
cd %CUR%tools\adFactoryFujiActualDataOutput
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM データベースメンテナンスツール
cd %CUR%adDatabaseApp
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 設定ツール
cd %CUR%tools\adSetupTool
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM レイアウトエディタ
cd %CUR%tools\adFloorLayoutEditor
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

REM 進捗情報出力
cd %CUR%tools\adBridgeBI
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false

