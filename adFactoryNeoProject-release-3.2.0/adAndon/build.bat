cd %CURRENT%%APPLICATION_NAME%
call mvn versions:set -DgenerateBackupPoms=false -DnewVersion=%VERSION%
call mvn clean package
copy target\%APPLICATION_NAME%-%VERSION%.jar %PLUGIN_PATH%%APPLICATION_NAME%.jar
cd %CURRENT%