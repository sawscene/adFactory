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
REM BpmnModel
REM ====================================================================
cd %CUR%\BpmnModel
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM ExcelReplacer
REM ====================================================================
cd %CUR%ExcelReplacer
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

REM ====================================================================
REM adUtility
REM ====================================================================
cd %CUR%
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%

exit /b %RESULT%
