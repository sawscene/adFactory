@echo off

schtasks /delete /tn "Daily Task" /f
schtasks /create /tn "Daily Task" /tr %ADFACTORY_HOME%\bin\daily_task.bat /sc daily /st 04:00:00 /f