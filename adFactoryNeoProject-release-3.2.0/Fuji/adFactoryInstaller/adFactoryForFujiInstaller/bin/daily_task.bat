@echo off

rem Apache�̃��O�t�@�C�����A30�����c���č폜����B
set APACHE_LOG_PATH=%ADFACTORY_HOME%\3rd\Apache-2.2.34\logs\
for /f "skip=30" %%F in ('dir /b /o-n %APACHE_LOG_PATH%access_????????.log') do del %APACHE_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %APACHE_LOG_PATH%error_????????.log') do del %APACHE_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %APACHE_LOG_PATH%ssl_request_????????.log') do del %APACHE_LOG_PATH%%%F

rem TomEE�̃��O�t�@�C�����A30�����c���č폜����B
set TOMEE_LOG_PATH=%ADFACTORY_HOME%\3rd\apache-tomee-plume-7.1.1\logs\
for /f "skip=30" %%F in ('dir /b /o-n %TOMEE_LOG_PATH%localhost.????-??-??.log') do del %TOMEE_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %TOMEE_LOG_PATH%TomEE.????-??-??.log') do del %TOMEE_LOG_PATH%%%F

rem PostgreSQL�̃��O�t�@�C�����A30�����c���č폜����B
set PSQL_LOG_PATH=%ADFACTORY_HOME%\3rd\postgreSQL11\data\adFactorylogs\
for /f "skip=30" %%F in ('dir /b /o-n %PSQL_LOG_PATH%postgresql_????????.log') do del %PSQL_LOG_PATH%%%F

set PSQL_LOG_PATH=%ADFACTORY_HOME%\3rd\postgreSQL\data\adFactorylogs\
for /f "skip=30" %%F in ('dir /b /o-n %PSQL_LOG_PATH%postgresql_????????.log') do del %PSQL_LOG_PATH%%%F

rem adFactory�̃��O�t�@�C�����A30���c���č폜����B
set ADFACTORY_LOG_PATH=%ADFACTORY_HOME%\logs\
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adAndonApp*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adDatabaseApp*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adFactoryGuiTest*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adFactoryServer*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adInterfaceService*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adManagerApp*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adUpdaterApp*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adSetupTool*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adCellProductionMonitorApp*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
for /f "skip=30" %%F in ('dir /b /o-n %ADFACTORY_LOG_PATH%adFactoryForFujiServer*.log.zip') do del %ADFACTORY_LOG_PATH%%%F
