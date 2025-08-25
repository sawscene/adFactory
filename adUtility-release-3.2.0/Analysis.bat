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
call mvn sonar:sonar

cd %CUR%BpmnModel
call mvn sonar:sonar

cd %CUR%ExcelReplacer
call mvn sonar:sonar

cd %CUR%
goto :EOF


:SUB
rem コードを分析します
mvn sonar:sonar
goto :EOF
