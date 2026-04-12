@echo off
setlocal EnableExtensions EnableDelayedExpansion
chcp 65001 >nul

set "BASE_DIR=%~dp0"
set "BACKEND_DIR=%BASE_DIR%backend"
set "FRONTEND_DIR=%BASE_DIR%frontend"
set "FRONTEND_POM=%FRONTEND_DIR%\pom.xml"
set "MVNW=%BACKEND_DIR%\mvnw.cmd"
set "GSON_JAR=%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Khong tim thay Java trong PATH.
    pause
    exit /b 1
)

echo Building frontend...
call "%MVNW%" -f "%FRONTEND_POM%" -DskipTests compile
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Frontend build failed.
    pause
    exit /b 1
)

if not exist "%GSON_JAR%" (
    echo [ERROR] Thieu dependency Gson: %GSON_JAR%
    pause
    exit /b 1
)

echo Launching frontend...
cd /d "%FRONTEND_DIR%\target\classes"
java -cp ".;%GSON_JAR%" com.foodorder.ui.LoginFrame
pause
