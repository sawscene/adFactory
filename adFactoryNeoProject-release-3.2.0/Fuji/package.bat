@echo off

rem 第1引数 バージョン
if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 1.4.0) >"
)

rem 第2引数 ビルド番号
if NOT "%2" EQU "" (
  set BUILD_NUM=%2
) else (
  set /p BUILD_NUM="Input Build Number (ex: 0) >"
)

adFactoryForFujiRelease.bat %VERSION% %BUILD_NUM% package

pause
