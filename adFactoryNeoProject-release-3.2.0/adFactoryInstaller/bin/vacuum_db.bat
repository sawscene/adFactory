@echo off
set path=%Path%;c:\adFactory\3rd\postgreSQL\bin

@echo on
rem C:\adFactory\3rd\postgreSQL\bin\vacuumlo.exe -p 15432 -U postgres adFactoryDB2
psql -p 15432 -U postgres -d adFactoryDB2 -c "VACUUM FULL"
