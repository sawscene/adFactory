@echo off
set PGPASSWORD=@dtek1977
@echo on
start /wait %ADFACTORY_HOME%\3rd\postgreSQL11\bin\psql.exe -p 15432 -U postgres -f create_adFactoryTraceDB.sql
start /wait %ADFACTORY_HOME%\3rd\postgreSQL11\bin\psql.exe -p 15432 -U postgres -d adFactoryTraceDB -f create_adFactoryTraceDB_tables.sql
