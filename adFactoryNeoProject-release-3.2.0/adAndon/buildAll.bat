@echo off
rem PATH=%PATH%;C:\Program Files\NetBeans 8.0.1\java\maven\bin;C:\Program Files (x86)\Launch4j;C:\Program Files (x86)\Inno Setup 5

set VERSION=1.5.0
set CURRENT=%~dp0
set BIN_PATH=C:\adFactory\bin\
set PLUGIN_PATH=C:\adFactory\plugin\
::set OUTPUT=%~dp0Output\

::IF EXIST %OUTPUT% (
:: %OUTPUT%が存在した場合
::rd /S /Q %OUTPUT%
::)

::md %OUTPUT%

cd %CURRENT%adAndonAppCommon
call release.bat %VERSION%

set APPLICATION_NAME=adAndonApp
cd %CURRENT%%APPLICATION_NAME%
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%
call mvn clean package
copy target\%APPLICATION_NAME%-%VERSION%.jar target\%APPLICATION_NAME%.jar
call launch4jc launch4j.xml
copy target\%APPLICATION_NAME%.exe %BIN_PATH%%APPLICATION_NAME%.exe
cd %CURRENT%

set APPLICATION_NAME=adAndonClockPlugin
call build.bat

set APPLICATION_NAME=adAndonDailyDelayNumPlugin
call build.bat

set APPLICATION_NAME=adAndonDailyInterruptNumPlugin
call build.bat

set APPLICATION_NAME=adAndonDailyPlanDeviatedNumPlugin
call build.bat

set APPLICATION_NAME=adAndonDailyPlanDeviatedTimePlugin
call build.bat

set APPLICATION_NAME=adAndonDailyPlanNumPlugin
call build.bat

set APPLICATION_NAME=adAndonEquipmentPlanNumPlugin
call build.bat

set APPLICATION_NAME=adAndonEquipmentStatusPlugin
call build.bat

set APPLICATION_NAME=adAndonLineCountDownPlugin
call build.bat

set APPLICATION_NAME=adAndonLineStatusPlugin
call build.bat

set APPLICATION_NAME=adAndonLineTaktTimePlugin
call build.bat

set APPLICATION_NAME=adAndonMonthlyPlanDeviatedNumPlugin
call build.bat

set APPLICATION_NAME=adAndonMonthlyPlanNumPlugin
call build.bat

set APPLICATION_NAME=adAndonTitlePlugin
call build.bat

set APPLICATION_NAME=AgendaAndonPlugin
cd %CURRENT%%APPLICATION_NAME%
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%
call mvn clean package
copy target\%APPLICATION_NAME%-%VERSION%.jar target\%APPLICATION_NAME%.jar
call launch4jc launch4j.xml
copy target\adAgendaMonitor.exe %BIN_PATH%adAgendaMonitor.exe
cd %CURRENT%

