@echo off
set PATH=%PATH%;%ADFACTORY_HOME%\3rd\PostgreSQL\bin
set NOW_DATE=%date:~0,4%%date:~5,2%%date:~8,2%
set time2=%time: =0%
set NOW_TIME=%time2:~0,2%%time2:~3,2%%time2:~6,2%

if Not Exist %ADFACTORY_HOME%\temp md %ADFACTORY_HOME%\temp

xcopy /E /Y /R /I %ADFACTORY_HOME%\logs\*.* %ADFACTORY_HOME%\temp\logs
xcopy /E /Y /R /I %ADFACTORY_HOME%\3rd\Apache2\logs\*.* %ADFACTORY_HOME%\temp\apache
xcopy /E /Y /R /I %ADFACTORY_HOME%\3rd\apache-tomee-plume-1.7.1\logs\*.* %ADFACTORY_HOME%\temp\tomee

pg_dump --username=postgres -Fc adFactoryDB > %ADFACTORY_HOME%\temp\adFactoryDB.backup
%ADFACTORY_HOME%\bin\7za.exe a -ssw %ADFACTORY_HOME%\temp\logs_%NOW_DATE%_%NOW_TIME%.zip %ADFACTORY_HOME%\temp\logs %ADFACTORY_HOME%\temp\apache %ADFACTORY_HOME%\temp\tomee %ADFACTORY_HOME%\temp\adFactoryDB.backup

del %ADFACTORY_HOME%\temp\adFactoryDB.backup
del /q %ADFACTORY_HOME%\logs\*.*
rd  /s /q %ADFACTORY_HOME%\temp\logs
rd  /s /q %ADFACTORY_HOME%\temp\apache
rd  /s /q %ADFACTORY_HOME%\temp\tomee
