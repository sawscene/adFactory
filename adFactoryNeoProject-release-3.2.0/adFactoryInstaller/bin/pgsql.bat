@echo off
set PATH=%PATH%;C:\adFactory\3rd\postgreSQL11\bin
set PGCLIENTENCODING=UTF8

rem ��1���� �f�[�^�x�[�X��
if NOT "%1" EQU "" (
  set DB_NAME=%1
) else (
  set /p DB_NAME="Database Name:"
)

rem ��2���� �t�@�C����
if NOT "%2" EQU "" (
  set FILE_NAME=%2
) else (
  set /p FILE_NAME="File Name:"
)

psql.exe -p 15432 -U postgres -d %DB_NAME% -f %FILE_NAME%
