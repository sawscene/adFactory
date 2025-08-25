echo off
setlocal enabledelayedexpansion

rem �Q�Ƃ���o�[�W�����t�@�C��
set UPDATER_VER=%ADFACTORY_HOME%\adUpdaterApp\version.ini
rem ���s�t�@�C���p�X
set UPDATER_EXE=%ADFACTORY_HOME%\adUpdaterApp\bin\adUpdaterApp.exe
set APPLICATION_EXE=%ADFACTORY_HOME%\bin\adAndonApp.exe

rem ���L�o�[�W����(1.0.4.58)�ȏ�͋N�����@2�ŋN������B
set /a EXEC2_V1=1
set /a EXEC2_V2=0
set /a EXEC2_V3=4
set /a EXEC2_V4=58

for /f "delims== tokens=1,2" %%a in (!UPDATER_VER!) do (
  if %%a equ Ver (
    echo %%b

    for /f "delims=. tokens=1,2,3,4" %%c in ("%%b") do (
      set /a v1=%%c
      set /a v2=%%d
      set /a v3=%%e
      set /a v4=%%f

      if !v1! gtr !EXEC2_V1! (
        goto EXEC2_LINE
      )
      if !v1! lss !EXEC2_V1! (
        goto EXEC1_LINE
      )

      if !v2! gtr !EXEC2_V2! (
        goto EXEC2_LINE
      )
      if !v2! lss !EXEC2_V2! (
        goto EXEC1_LINE
      )

      if !v3! gtr !EXEC2_V3! (
        goto EXEC2_LINE
      )
      if !v3! lss !EXEC2_V3! (
        goto EXEC1_LINE
      )

      if !v4! gtr !EXEC2_V4! (
        goto EXEC2_LINE
      )
      if !v4! lss !EXEC2_V4! (
        goto EXEC1_LINE
      )
    )
    goto EXEC2_LINE
  )
)

rem adUpdater�̃o�[�W�������m�F�ł��Ȃ��ꍇ�̓A�v���P�[�V�����̂݋N������B
start /w %APPLICATION_EXE% %1

goto EXIT_LINE

:EXEC2_LINE
rem �N�����@2�FadUpdater����A�v���P�[�V�������N������B
start /w %UPDATER_EXE% %APPLICATION_EXE% %1

goto EXIT_LINE

:EXEC1_LINE
rem �N�����@1�FadUpdater�ƃA�v���P�[�V������ʁX�ɋN������B
start /w %UPDATER_EXE% 
start /w %APPLICATION_EXE% %1

:EXIT_LINE
endlocal
