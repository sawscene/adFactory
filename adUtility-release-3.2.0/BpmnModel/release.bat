@echo off
rem PATH=%PATH%;C:\Program Files\NetBeans 8.0.1\java\maven\bin

rem ��1���� �o�[�W����
if NOT "%1" EQU "" (
	call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%1
)

rem ���ʃ��C�u�����̂��߃��[�J�����|�W�g���ɔz�u
call mvn clean install

call mvn dependency:tree -DoutputFile=target/dependencylist.txt

