@echo off

if NOT "%1" EQU "" (
  set VERSION=%1
) else (
  set /p VERSION="Input Version (ex: 2.17.0)>"
)

set CUR=%~dp0


call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-api -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
call mvn versions:use-dep-version -Dincludes=org.apache.logging.log4j:log4j-core -DdepVersion=%VERSION% -DforceVersion=true -DgenerateBackupPoms=false
