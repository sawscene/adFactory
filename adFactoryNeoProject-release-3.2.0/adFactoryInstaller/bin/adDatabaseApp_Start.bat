@echo off
start adDatabaseApp.exe -update >running.1

@echo off
del running.1
if exist running.1 (echo é¿çsíÜ) else echo èIóπçœ

@echo off
:wait
del running.1
if exist running.1 ping.exe localhost -n 2 1 & goto :wait

