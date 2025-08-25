@echo off
reg add "HKEY_CURRENT_USER\SOFTWARE\Microsoft\Office\Excel\Addins\adWorkbookAddIn" /v "Description" /t "REG_SZ" /d "adWorkbookAddIn" /f
reg add "HKEY_CURRENT_USER\SOFTWARE\Microsoft\Office\Excel\Addins\adWorkbookAddIn" /v "FriendlyName" /t "REG_SZ" /d "adWorkbookAddIn" /f
reg add "HKEY_CURRENT_USER\SOFTWARE\Microsoft\Office\Excel\Addins\adWorkbookAddIn" /v "LoadBehavior" /t "REG_DWORD" /d "3" /f
reg add "HKEY_CURRENT_USER\SOFTWARE\Microsoft\Office\Excel\Addins\adWorkbookAddIn" /v "Manifest" /t "REG_SZ" /d "file:///C:/adFactory/adWorkbook/bin/adWorkbookAddIn.vsto|vstolocal" /f

powershell -ExecutionPolicy Bypass -File C:\adFactory\adWorkbook\installpfx.ps1

C:\adFactory\adWorkbook\bin\adWorkbookAddIn.vsto
