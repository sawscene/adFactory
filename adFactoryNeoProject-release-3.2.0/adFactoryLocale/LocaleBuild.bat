powershell -ExecutionPolicy RemoteSigned -File ./localeBuilder.ps1 -localeType �W��_���{�� -outfile ./locale.properties

if NOT "%ADFACTORY_OUTPUT%" EQU "" (
  @echo on
  copy /Y locale.properties %ADFACTORY_OUTPUT%\bin\
)
