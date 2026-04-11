@echo off
cd /d "%~dp0src\main\java"
echo Compiling files...
javac com/foodorder/ui/*.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!
echo Running application...
java -cp ".;..\resources" com.foodorder.ui.LoginFrame
pause
