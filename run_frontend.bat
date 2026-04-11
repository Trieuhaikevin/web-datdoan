@echo off
cd /d "%~dp0frontend\src\main\java"
echo.
echo ============================================
echo   Dat Do An Online - Food Order App
echo ============================================
echo.
echo Compiling files...
echo.
javac com/foodorder/ui/*.java
if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    echo.
    pause
    exit /b 1
)
echo [OK] Compilation successful!
echo.
echo.
echo Launching application...
echo.
java com.foodorder.ui.LoginFrame
pause
