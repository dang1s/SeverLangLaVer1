@echo off
cd /d "%~dp0"
java -server -cp "target\LangLa-1.0-SNAPSHOT.jar;target\LangLa-1.0-SNAPSHOT\*" -Dfile.encoding=UTF-8 com.sg188.server.Main
pause
