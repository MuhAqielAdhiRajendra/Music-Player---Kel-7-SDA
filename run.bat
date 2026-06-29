@echo off
echo Hai....
if not exist "bin" mkdir bin
javac -cp "lib/*" -d bin src/*.java
if %ERRORLEVEL% equ 0 (
    echo Aplikasi dah jalan ya silahkan dicek
    java -cp "bin;lib/*" App
) else (
    echo aplikasi ndak jalan SKILL ISSU
)
pause
