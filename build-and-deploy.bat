@echo off
REM ============================================
REM Attendance System Build and Deploy Script
REM ============================================

REM Maven 경로 자동 감지
set MVN_CMD=mvn
set MAVEN_VSCODE=%USERPROFILE%\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\maven\latest\bin\mvn.cmd

REM VS Code Maven 사용
if exist "%MAVEN_VSCODE%" (
    set MVN_CMD=%MAVEN_VSCODE%
    echo Found Maven: VS Code Extension
) else (
    where mvn >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Maven not found! Please install Maven.
        echo Download from: https://maven.apache.org/download.cgi
        pause
        exit /b 1
    )
    echo Found Maven: System PATH
)

echo [1/3] Building WAR file with Maven...
echo Using Maven: %MVN_CMD%

REM target 폴더가 잠겨있는 경우 대비하여 수동 삭제 시도
if exist "target" (
    echo Cleaning target directory...
    rmdir /s /q "target" 2>nul
    if exist "target" (
        echo [WARNING] Could not delete target folder. Trying package without clean...
        call "%MVN_CMD%" package -DskipTests
    ) else (
        call "%MVN_CMD%" clean package -DskipTests
    )
) else (
    call "%MVN_CMD%" clean package -DskipTests
)

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Checking build output...
if not exist "target\attendance.war" (
    echo [ERROR] WAR file not found in target directory!
    pause
    exit /b 1
)

echo [SUCCESS] WAR file built: target\attendance.war
echo.

set TOMCAT_WEBAPPS=C:\Program Files\apache-tomcat-10.1.48\webapps
set WAR_NAME=attendance.war

echo [3/3] Deploying to Tomcat...
echo Target: %TOMCAT_WEBAPPS%

REM Stop Tomcat if running (optional)
REM echo Stopping Tomcat...
REM net stop Tomcat10

REM Remove old deployment
if exist "%TOMCAT_WEBAPPS%\attendance" (
    echo Removing old deployment directory...
    rmdir /s /q "%TOMCAT_WEBAPPS%\attendance"
)

if exist "%TOMCAT_WEBAPPS%\%WAR_NAME%" (
    echo Removing old WAR file...
    del /f /q "%TOMCAT_WEBAPPS%\%WAR_NAME%"
)

REM Copy new WAR file
echo Copying WAR file to Tomcat webapps...
copy /y "target\attendance.war" "%TOMCAT_WEBAPPS%\%WAR_NAME%"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to copy WAR file! Check permissions.
    pause
    exit /b 1
)

echo.
echo ============================================
echo [SUCCESS] Deployment completed!
============================================
echo WAR file location: %TOMCAT_WEBAPPS%\%WAR_NAME%
echo.

REM Tomcat 시작
echo.
echo [4/4] Starting Tomcat server...

set "TOMCAT_STARTUP=C:\Program Files\apache-tomcat-10.1.48\bin\startup.bat"

if exist "%TOMCAT_STARTUP%" (
    echo Starting Tomcat from: %TOMCAT_STARTUP%
    cd /d "C:\Program Files\apache-tomcat-10.1.48\bin"
    call startup.bat
    cd /d "%~dp0"
    echo.
    echo ============================================
    echo [SUCCESS] Deployment and startup complete!
    echo ============================================
    echo Tomcat is starting...
    echo Access your application at:
    echo http://localhost:8080/attendance
    echo ============================================
) else (
    echo [WARNING] Tomcat startup script not found at:
    echo %TOMCAT_STARTUP%
    echo Please start Tomcat manually.
)

echo.
pause
