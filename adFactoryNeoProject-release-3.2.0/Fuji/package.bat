@echo off

rem ��1���� �o�[�W����
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 1.4.0) >"
)

rem ��2���� �r���h�ԍ�
if NOT "%2" EQU "" (
  set BUILD_NUM=%2
) else (
  set /p BUILD_NUM="Input Build Number (ex: 0) >"
)

adFactoryForFujiRelease.bat %VERSION% %BUILD_NUM% package

pause
