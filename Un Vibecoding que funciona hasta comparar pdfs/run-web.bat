@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo [ProcureFlow] Starting local web app...

if not exist package.json (
  echo [ProcureFlow] package.json not found in this folder.
  echo [ProcureFlow] Open run-web.bat from the project folder.
  pause
  exit /b 1
)

where npm >nul 2>&1
if errorlevel 1 (
  echo [ProcureFlow] npm not found.
  echo [ProcureFlow] Install Node.js LTS and try again.
  pause
  exit /b 1
)

set PORT_READY=
for /f "tokens=1" %%A in ('netstat -ano ^| findstr ":3000" ^| findstr /I /C:"LISTENING" /C:"ESCUCHANDO"') do set PORT_READY=1
if defined PORT_READY (
  echo [ProcureFlow] Server already running on port 3000.
  start "" "http://localhost:3000"
  exit /b 0
)

if not exist node_modules (
  echo [ProcureFlow] Installing npm dependencies first run...
  call npm install
  if errorlevel 1 (
    echo [ProcureFlow] npm install failed.
    pause
    exit /b 1
  )
)

start "ProcureFlow Server" cmd /k "cd /d ""%~dp0"" && npm run dev"

set /a ATTEMPTS=0
:WAIT_PORT
set /a ATTEMPTS+=1
set PORT_READY=
for /f "tokens=1" %%A in ('netstat -ano ^| findstr ":3000" ^| findstr /I /C:"LISTENING" /C:"ESCUCHANDO"') do set PORT_READY=1
if defined PORT_READY goto OPEN_BROWSER
if !ATTEMPTS! GEQ 45 goto OPEN_BROWSER
timeout /t 1 /nobreak >nul
goto WAIT_PORT

:OPEN_BROWSER
start "" "http://localhost:3000"
echo [ProcureFlow] Browser opened at http://localhost:3000
echo [ProcureFlow] Keep the "ProcureFlow Server" window open while working.
