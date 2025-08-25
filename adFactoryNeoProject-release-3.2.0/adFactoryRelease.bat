@echo off
set RESULT=0

set JAVA_HOME=C:\adFactory\3rd\jdk21.0.4_7
set MAVEN_HOME=C:\tools\apache-maven-3.9.9
set GIT_HOME=C:\Program Files\Git
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%GIT_HOME%\cmd;%PATH%


REM ====================================================================
REM 
REM git�R�}���h�ŁA�R�~�b�g���A�u�����`���A�R�~�b�gID���擾���A
REM �o�[�W�������Ɏc��.
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

rem ��1���� �o�[�W����
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 1.4.0)>"
)

rem ��2���� �r���h�ԍ�
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
REM �t�H���_�č\�z
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
REM ���ꂼ��̃v���W�F�N�g���r���h����.
REM 
REM ====================================================================

REM ====================================================================
REM ���ʃ��C�u����
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
REM adManager ���Y�Ǘ��A�v���P�[�V����
cd %CUR%adManagerApp\adManagerApp
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �i�����j�^�ݒ�v���O�C��
cd %CUR%adManagerApp\adManagerAndonSettingPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �ݔ��ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerEquipmentEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �J���o���ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerKanbanEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���C���^�C�}�[����v���O�C��
cd %CUR%adManagerApp\adManagerLineTimerPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �g�D�ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerOrganizationEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���яo�̓v���O�C��
cd %CUR%adManagerApp\adManagerReportOutPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �V�X�e���ݒ�v���O�C��
cd %CUR%adManagerApp\adManagerSystemSettingPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

 REM �H���E�H�����ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerWorkflowEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ��Ɠ���v���O�C��
cd %CUR%adManagerApp\adManagerWorkReportPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ELS�l�����i�����j�^�ݒ�v���O�C��
cd %CUR%adManagerApp\adManaMonitorSettingPluginELS
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �g�v�R���l�����i�����j�^�ݒ�v���O�C��
cd %CUR%adManagerApp\adManaMonitorSettingPluginTP
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ��ƕ��̓v���O�C��
cd %CUR%adManagerApp\adManagerChartPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���Y�Ǘ��v���O�C��
cd %CUR%adManagerApp\adManaProductionNaviPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �q�Ɉē��v���O�C��
cd %CUR%adManagerApp\adManagerWarehousePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �T�}���[���|�[�g�v���O�C��
cd %CUR%adManagerApp\adManagerSummaryReportEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �T�}���[���|�[�g�v���O�C��
cd %CUR%adManagerApp\adManagerLedgerManagerPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �i�ԕҏW�v���O�C��
cd %CUR%adManagerApp\adManagerDsItemEditPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adInterfaceService
REM ====================================================================
REM adInterface �Ǘ��T�[�r�X
cd %CUR%adInterfaceService\adInterfaceService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �q�Ɉē��T�[�r�X
cd %CUR%adInterfaceService\WarehouseServicePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM DbImportService �v���O�C��(TIPS�l����)
cd %CUR%adInterfaceService\DbImportService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM DbOutputService�v���O�C��(TIPS�l����)
cd %CUR%adInterfaceService\DbOutputService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �����C���|�[�g�T�[�r�X�v���O�C��
cd %CUR%adInterfaceService\ImportService
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �f�[�^�o�̓T�[�r�X�v���O�C��(FUJI�l����)
cd %CUR%adInterfaceService\ExportServicePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �f�C���[���|�[�g
cd %CUR%adInterfaceService\SummaryReportPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���u�A�g
cd %CUR%adInterfaceService\DeviceConnectionServicePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ====================================================================
REM adMonitor
REM ====================================================================
REM adMonitor �A�v���P�[�V����
cd %CUR%adAndon\adAndonApp
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���C�A�E�g�c�[��
cd %CUR%adAndon\adAndonCustomizeTool
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���t�����t���[��
cd %CUR%adAndon\adAndonClockPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �x�����R�J�E���g�t���[��
cd %CUR%adAndon\adAndonDailyDelayNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���f���R�J�E���g�t���[��
cd %CUR%adAndon\adAndonDailyInterruptNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �������ѐi�����t���[��
cd %CUR%adAndon\adAndonDailyPlanDeviatedNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �������ѐi�����ԃt���[��
cd %CUR%adAndon\adAndonDailyPlanDeviatedTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �����v����ѐ��t���[��
cd %CUR%adAndon\adAndonDailyPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �ݔ��v����ѐ��t���[��
cd %CUR%adAndon\adMonitorEquipmentPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �H���ݔ��X�e�[�^�X�t���[��
cd %CUR%adAndon\adMonitorEquipmentStatusPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���C���J�E���g�_�E���t���[��
cd %CUR%adAndon\adAndonLineCountDownPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���C���S�̃X�e�[�^�X�t���[��
cd %CUR%adAndon\adMonitorLineStatusPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���C���^�N�g�^�C���t���[��
cd %CUR%adAndon\adAndonLineTaktTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �������ѐi�����t���[��
cd %CUR%adAndon\adAndonMonthlyPlanDeviatedNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �����v����ѐ��t���[��
cd %CUR%adAndon\adAndonMonthlyPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �^�C�g���t���[��
cd %CUR%adAndon\adAndonTitlePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �����H���v����ѐ��t���[��
cd %CUR%adAndon\adAndonDailyWorkPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �\�����j�^ �A�v���P�[�V����
cd %CUR%adAndon\AgendaAndonPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �H���i���t���[��
cd %CUR%adAndon\adMonitorWorkStatusPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �G�f�b�N�����Z�C�V�X�e���l�����������ѐi���t���[��
cd %CUR%adAndon\adMonitorDailyLineTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �]���t���[��
cd %CUR%adAndon\adMonitorBlankSpacePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �O���[�v�i���t���[��
cd %CUR%adAndon\adMonitorWorkDeliveryPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �H�����ѐi���t���[��
cd %CUR%adAndon\adMonitorEquipmentDeliveryPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���f�������t���[��
cd %CUR%adAndon\adMonitorSuspendedRatePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ��ƏI���\�z���ԃt���[��
cd %CUR%adAndon\adMonitorEstimatedTimePlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �H���ʌv����ѐ��t���[��
cd %CUR%adAndon\adAndonWorkPlanNumPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���Ճ��j�^�[�t���[��
cd %CUR%adAndon\adMonitorFloorPlugin
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �o�������كt���[��
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
REM �H���A�g�f�[�^�o��
REM cd %CUR%tools\adFactoryFujiActualDataOutput
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �H���A�g�f�[�^�o��(FUJI�L�c�p) 
REM cd %CUR%tools\adFactoryFujiToyotaActualDataOutput
REM call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �f�[�^�x�[�X�����e�i���X�c�[��
cd %CUR%adDatabaseApp
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �ݒ�c�[��
cd %CUR%tools\adSetupTool
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM ���C�A�E�g�G�f�B�^
cd %CUR%tools\adFloorLayoutEditor
call release.bat %ADFACTORY_INTERNAL_VERSION%

REM �i�����o��
cd %CUR%tools\adBridgeBI
call release.bat %ADFACTORY_INTERNAL_VERSION%


REM ====================================================================
REM adLinkService
REM ====================================================================
REM adLinkService�{��
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
REM �C���X�g�[���[�����.
REM 
REM ====================================================================
cd %CUR%

REM �W���C���X�g�[��
ISCC.exe %CUR%adFactoryInstaller\install.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM �W���C���X�g�[��(�N���C�A���g�̂�)
ISCC.exe %CUR%adFactoryInstaller\install_clientApps.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM �W���C���X�g�[��(adManager�̂�)
ISCC.exe %CUR%adFactoryInstaller\install_adManagerApps.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM �A�W�F���_���j�^�C���X�g�[��
ISCC.exe %CUR%adFactoryInstaller\install_AgendaMonitor.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

REM adLinkService �C���X�g�[��
ISCC.exe %CUR%adFactoryInstaller\install_adLinkService.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

exit /b %RESULT%
