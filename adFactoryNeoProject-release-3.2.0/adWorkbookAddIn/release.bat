@echo off
PATH=%PATH%;C:\Program Files (x86)\Microsoft Visual Studio\2019\Professional\\MSBuild\Current\Bin;C:\Windows\Microsoft.NET\Framework\v4.0.30319;


rem 第1引数 バージョン
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Enter Version (ex: 1.0.0)>"
)

rem 第2引数 ビルド番号
if NOT "%2" EQU "" (
  set BUILD_NUM=%2
) else (
  set /p BUILD_NUM="Enter Build Number (ex: 1)>"
)

set INTERNAL_VERSION=%VERSION%
set OUTERNAL_VERSION=%VERSION%

set CUR=%~dp0
set INSTALLER=%~dp0Installer

echo [Version]>%INSTALLER%\version.ini
echo Ver=%OUTERNAL_VERSION%.%BUILD_NUM%>>%INSTALLER%\version.ini
echo InternalVer=%INTERNAL_VERSION%.%BUILD_NUM%>>%INSTALLER%\version.ini

@echo on
msbuild %CUR%\adWorkbookAddIn.sln /m /t:Rebuild /p:Configuration=Release /verbosity:normal

@echo off
ISCC.exe %INSTALLER%\adWorkbookSetup.iss
if %ERRORLEVEL% NEQ 0 set RESULT=1

if "%1" EQU "" (
pause
)

exit /b %RESULT%
