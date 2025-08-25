@echo off
echo %~dp0
:set CUR_PATH=%~dp0
set CUR_PATH=%USERPROFILE%\Desktop\update_table
set PGPASSWORD=@dtek1977
set path=%Path%;C:\adFactory\3rd\postgreSQL11\bin

echo update start.
psql -U postgres -d adFactoryDB2 -p 15432 -f %CUR_PATH%\update_table.sql
if %ERRORLEVEL% equ 0 goto SUCCESS
echo;
echo update failed.
pause
exit
:SUCCESS
echo;

echo update completed.
pause
