set modelname=%1
set username=%2
set password=%3

cd %~dp0
powershell.exe -ExecutionPolicy RemoteSigned .\SummaryReport.ps1 %modelname% %username% %password%

