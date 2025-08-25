@echo off
PATH=%PATH%;C:\Windows\Microsoft.NET\Framework\v4.0.30319

@echo on
msbuild DashboardCustomizeTool_JPC\DashboardCustomizeTool_JPC.sln /m /t:Rebuild /p:Configuration=Release /verbosity:normal
msbuild DashboardCustomizeTool_SCC\DashboardCustomizeTool_SCC.sln /m /t:Rebuild /p:Configuration=Release /verbosity:normal
msbuild DashboardCustomizeTool_TCC\DashboardCustomizeTool_TCC.sln /m /t:Rebuild /p:Configuration=Release /verbosity:normal
msbuild DashboardCustomizeTool_USC\DashboardCustomizeTool_USC.sln /m /t:Rebuild /p:Configuration=Release /verbosity:normal
msbuild DashboardCustomizeTool\DashboardCustomizeTool.sln /m /t:Rebuild /p:Configuration=Release /verbosity:normal

@echo off
if NOT "%ADFACTORY_OUTPUT%" EQU "" (
  @echo on
  copy /Y .\CmdSatellite\*.dll %ADFACTORY_OUTPUT%\CmdSatellite\
  copy /Y .\Release\bin\DashboardCustomizeTool.exe %ADFACTORY_OUTPUT%\bin\
  copy /Y .\Release\bin\Dockingcontrollayout.dll %ADFACTORY_OUTPUT%\bin\
  copy /Y .\Release\conf\*.* %ADFACTORY_OUTPUT%\conf\*.*
)
