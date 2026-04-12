@echo off
chcp 65001 >nul
set "BASE_DIR=%~dp0..\"

echo Building frontend...
call "%BASE_DIR%backend\mvnw.cmd" -f "%BASE_DIR%frontend\pom.xml" -DskipTests compile
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Frontend build failed.
    pause
    exit /b 1
)

echo Running frontend...
call "%BASE_DIR%backend\mvnw.cmd" -f "%BASE_DIR%frontend\pom.xml" -Dexec.mainClass=com.foodorder.ui.LoginFrame -Dexec.classpathScope=runtime org.codehaus.mojo:exec-maven-plugin:3.3.0:java
pause
