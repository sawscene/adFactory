@echo off
chcp 65001 >nul 2>&1

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "c:\adFactory\bin\xlsx2pdf.ps1" -from "%~1" -to "%~2"

echo exit xlsx2pdf.bat

set EXITCODE=%ERRORLEVEL%
exit /b %EXITCODE%

