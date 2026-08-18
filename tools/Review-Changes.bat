@echo off
setlocal

set "TOOLS_DIR=%~dp0"
set "REPO_ROOT=%~dp0.."
set "SCRIPT=%TOOLS_DIR%Review-Changes.ps1"

if not exist "%SCRIPT%" (
    echo ERROR: Could not find:
    echo %SCRIPT%
    echo.
    pause
    exit /b 1
)

cd /d "%REPO_ROOT%"
if errorlevel 1 (
    echo ERROR: Could not switch to repository root:
    echo %REPO_ROOT%
    echo.
    pause
    exit /b 1
)

echo.
echo === ANTARCHY - ASCENSION COMPANION QUICK REVIEW ===
echo.
echo 1. Review changes only
echo 2. Review changes and run Gradle build
echo.

set "MODE="
set /p "MODE=Select [1-2] (default 1): "

if not defined MODE set "MODE=1"

if "%MODE%"=="2" (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%" -Build
) else (
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%"
)

set "EXITCODE=%ERRORLEVEL%"

echo.
pause
exit /b %EXITCODE%
