@echo off
chcp 65001 >nul
cd /d "%~dp0frontend\src\main\java"
javac com/foodorder/ui/*.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
java -cp ".;..\resources" com.foodorder.ui.LoginFrame
