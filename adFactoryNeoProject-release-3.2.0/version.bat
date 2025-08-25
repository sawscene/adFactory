@echo off
set RESULT=0

rem ��1���� �o�[�W����
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 1.4.0)>"
)

set CUR=%~dp0


REM ====================================================================
REM ���ʃ��C�u����
REM ====================================================================
cd %CUR%adFactoryCommon\CommonEntity
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adFactoryCommon\ClientService
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adFactoryCommon\JavafxCommon
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adInterfaceService\adInterfaceServiceCommon
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

cd %CUR%adAndon\adAndonAppCommon
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


REM ====================================================================
REM adFactoryServer
REM ====================================================================
cd %CUR%adFactoryServer\adFactoryServer
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


REM ====================================================================
REM adFactoryLocale
REM ====================================================================
cd %CUR%adFactoryLocale\StandardLocalePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


REM ====================================================================
REM adManagerApp
REM ====================================================================
REM adManager ���Y�Ǘ��A�v���P�[�V����
cd %CUR%adManagerApp\adManagerApp
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �i�����j�^�ݒ�v���O�C��
cd %CUR%adManagerApp\adManagerAndonSettingPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �ݔ��ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerEquipmentEditPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �J���o���ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerKanbanEditPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���C���^�C�}�[����v���O�C��
cd %CUR%adManagerApp\adManagerLineTimerPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �g�D�ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerOrganizationEditPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���яo�̓v���O�C��
cd %CUR%adManagerApp\adManagerReportOutPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �V�X�e���ݒ�v���O�C��
cd %CUR%adManagerApp\adManagerSystemSettingPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �H���E�H�����ҏW�v���O�C��
cd %CUR%adManagerApp\adManagerWorkflowEditPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ��Ɠ���v���O�C�� (v2���Ή�)
cd %CUR%adManagerApp\adManagerWorkReportPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ELS�l�����i�����j�^�ݒ�v���O�C�� (v2���Ή�)
cd %CUR%adManagerApp\adManaMonitorSettingPluginELS
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �g�v�R���l�����i�����j�^�ݒ�v���O�C�� (v2���Ή�)
REM cd %CUR%adManagerApp\adManaMonitorSettingPluginTP
REM call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ��ƕ��̓v���O�C��
cd %CUR%adManagerApp\adManagerChartPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���Y�Ǘ��v���O�C��
cd %CUR%adManagerApp\adManaProductionNaviPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �q�Ɉē��v���O�C��
cd %CUR%adManagerApp\adManagerWarehousePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adInterfaceService
REM ====================================================================
REM adInterface �Ǘ��T�[�r�X
cd %CUR%adInterfaceService\adInterfaceService
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �q�Ɉē��T�[�r�X
cd %CUR%adInterfaceService\WarehouseServicePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM DbImportService �v���O�C��(TIPS�l����)
cd %CUR%adInterfaceService\DbImportService
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM DbOutputService�v���O�C��(TIPS�l����)
cd %CUR%adInterfaceService\DbOutputService
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �����C���|�[�g�T�[�r�X�v���O�C��
cd %CUR%adInterfaceService\ImportService
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �f�[�^�o�̓T�[�r�X�v���O�C��(FUJI�l����)
cd %CUR%adInterfaceService\ExportServicePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �f�C���[���|�[�g
cd %CUR%adInterfaceService\SummaryReportPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


REM ====================================================================
REM adMonitor
REM ====================================================================
REM adMonitor �A�v���P�[�V����
cd %CUR%adAndon\adAndonApp
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���C�A�E�g�c�[��
cd %CUR%adAndon\adAndonCustomizeTool
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���t�����t���[��
cd %CUR%adAndon\adAndonClockPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �x�����R�J�E���g�t���[��
cd %CUR%adAndon\adAndonDailyDelayNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���f���R�J�E���g�t���[��
cd %CUR%adAndon\adAndonDailyInterruptNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �������ѐi�����t���[��
cd %CUR%adAndon\adAndonDailyPlanDeviatedNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �������ѐi�����ԃt���[��
cd %CUR%adAndon\adAndonDailyPlanDeviatedTimePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �����v����ѐ��t���[��
cd %CUR%adAndon\adAndonDailyPlanNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �ݔ��v����ѐ��t���[��
cd %CUR%adAndon\adMonitorEquipmentPlanNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �H���ݔ��X�e�[�^�X�t���[��
cd %CUR%adAndon\adMonitorEquipmentStatusPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���C���J�E���g�_�E���t���[��
cd %CUR%adAndon\adAndonLineCountDownPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���C���S�̃X�e�[�^�X�t���[��
cd %CUR%adAndon\adMonitorLineStatusPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���C���^�N�g�^�C���t���[��
cd %CUR%adAndon\adAndonLineTaktTimePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �������ѐi�����t���[��
cd %CUR%adAndon\adAndonMonthlyPlanDeviatedNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �����v����ѐ��t���[��
cd %CUR%adAndon\adAndonMonthlyPlanNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �^�C�g���t���[��
cd %CUR%adAndon\adAndonTitlePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �����H���v����ѐ��t���[��
cd %CUR%adAndon\adAndonDailyWorkPlanNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �\�����j�^ �A�v���P�[�V����
cd %CUR%adAndon\AgendaAndonPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �H���i���t���[��
cd %CUR%adAndon\adMonitorWorkStatusPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �G�f�b�N�����Z�C�V�X�e���l�����������ѐi���t���[��
cd %CUR%adAndon\adMonitorDailyLineTimePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �]���t���[��
cd %CUR%adAndon\adMonitorBlankSpacePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �O���[�v�i���t���[��
cd %CUR%adAndon\adMonitorWorkDeliveryPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �H�����ѐi���t���[��
cd %CUR%adAndon\adMonitorEquipmentDeliveryPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���f�������t���[��
cd %CUR%adAndon\adMonitorSuspendedRatePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ��ƏI���\�z���ԃt���[��
cd %CUR%adAndon\adMonitorEstimatedTimePlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �H���ʌv����ѐ��t���[��
cd %CUR%adAndon\adAndonWorkPlanNumPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���Ճ��j�^�[�t���[��
cd %CUR%adAndon\adMonitorFloorPlugin
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adFactorySettingTool
REM ====================================================================
cd %CUR%adFactorySettingTool
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%


REM ====================================================================
REM tools
REM ====================================================================
REM �H���A�g�f�[�^�o�� (v2���Ή�)
REM cd %CUR%tools\adFactoryFujiActualDataOutput
REM call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �f�[�^�x�[�X�����e�i���X�c�[��
cd %CUR%adDatabaseApp
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �ݒ�c�[��
cd %CUR%tools\adSetupTool
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ���C�A�E�g�G�f�B�^
cd %CUR%tools\adFloorLayoutEditor
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM �i�����o��
cd %CUR%tools\adBridgeBI
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

exit /b %RESULT%
