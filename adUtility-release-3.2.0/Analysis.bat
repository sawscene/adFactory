@echo off

set CUR=%~dp0


REM ====================================================================
REM
REM ���ꂼ��̃v���W�F�N�g�𕪐͂���.
REM
REM ====================================================================

REM ====================================================================
REM ���ʃ��C�u����
REM ====================================================================
call mvn sonar:sonar

cd %CUR%BpmnModel
call mvn sonar:sonar

cd %CUR%ExcelReplacer
call mvn sonar:sonar

cd %CUR%
goto :EOF


:SUB
rem �R�[�h�𕪐͂��܂�
mvn sonar:sonar
goto :EOF
