@echo off
set PGPASSWORD=@dtek1977
@echo on
start /wait %ADFACTORY_HOME%\3rd\postgreSQL11\bin\psql.exe -p 15432 -U postgres -c "CREATE ROLE fujio LOGIN ENCRYPTED PASSWORD 'fuji2017'; GRANT SELECT ON ALL TABLES IN SCHEMA public TO fujio;"
