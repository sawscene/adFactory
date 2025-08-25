@echo off
set PGPASSWORD=@dtek1977
@echo on
%ADFACTORY_HOME%\3rd\postgreSQL\bin\psql.exe -U postgres -f create_bi_user.sql
%ADFACTORY_HOME%\3rd\postgreSQL\bin\psql.exe -U postgres -f create_adFactoryProgressDB.sql
%ADFACTORY_HOME%\3rd\postgreSQL\bin\psql.exe -U postgres -d adFactoryProgressDB -f create_adFactoryProgressDB_tables.sql
