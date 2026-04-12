@echo off
setlocal EnableExtensions EnableDelayedExpansion
chcp 65001 >nul

set "BASE_DIR=%~dp0"
set "BACKEND_DIR=%BASE_DIR%backend"
set "FRONTEND_DIR=%BASE_DIR%frontend"
set "BACKEND_POM=%BACKEND_DIR%\pom.xml"
set "FRONTEND_POM=%FRONTEND_DIR%\pom.xml"
set "MVNW=%BACKEND_DIR%\mvnw.cmd"
set "GSON_JAR=%USERPROFILE%\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"
set "FRONTEND_CP=%FRONTEND_DIR%\target\classes;%GSON_JAR%"

if not exist "%MVNW%" (
    echo [ERROR] Khong tim thay Maven Wrapper: %MVNW%
    pause
    exit /b 1
)

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Khong tim thay Java trong PATH.
    pause
    exit /b 1
)

echo [1/4] Building backend...
call "%MVNW%" -f "%BACKEND_POM%" -DskipTests compile
if %ERRORLEVEL% neq 0 goto :error

echo [2/4] Building frontend...
call "%MVNW%" -f "%FRONTEND_POM%" -DskipTests compile
if %ERRORLEVEL% neq 0 goto :error

if not exist "%GSON_JAR%" (
    echo [ERROR] Thieu dependency Gson: %GSON_JAR%
    pause
    exit /b 1
)

echo [3/4] Starting backend...
set "BACKEND_PID="
for /f %%p in ('powershell -NoProfile -Command "(Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue ^| Select-Object -First 1 -ExpandProperty OwningProcess)"') do set "BACKEND_PID=%%p"
if defined BACKEND_PID taskkill /PID !BACKEND_PID! /F >nul 2>&1

start "FoodOrder Backend" cmd /min /c "cd /d ""%BACKEND_DIR%"" && call mvnw.cmd spring-boot:run"

echo Doi backend san sang...
set "BACKEND_READY="
for /L %%i in (1,1,60) do (
    powershell -NoProfile -Command "try { $r = Invoke-WebRequest -UseBasicParsing 'http://localhost:8080/api/foods' -TimeoutSec 2; if ($r.StatusCode -ge 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        set "BACKEND_READY=1"
        goto :backend_ready
    )
    timeout /t 1 /nobreak >nul
)

:backend_ready
if not defined BACKEND_READY (
    echo [ERROR] Backend khong len duoc. Kiem tra MySQL va backend.
    pause
    exit /b 1
)

echo [4/4] Launching app...
start "FoodOrder Frontend" cmd /min /c "cd /d ""%FRONTEND_DIR%\target\classes"" && java -cp ".;%GSON_JAR%" com.foodorder.ui.LoginFrame"

echo.
echo App da mo.
echo Admin mac dinh: admin@foodorder.com / admin123
exit /b 0

:error
echo.
echo [ERROR] Build that bai.
pause
exit /b 1
