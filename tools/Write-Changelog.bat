@echo off
setlocal

set "TOOLS_DIR=%~dp0"
set "REPO_ROOT=%~dp0.."
set "SCRIPT=%TOOLS_DIR%Write-Changelog.ps1"

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

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%SCRIPT%"
set "EXITCODE=%ERRORLEVEL%"

if not "%EXITCODE%"=="0" (
    echo.
    echo Write-Changelog failed with exit code %EXITCODE%.
    pause
    exit /b %EXITCODE%
)

echo.
pause
exit /b 0
