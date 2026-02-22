@echo off
setlocal
cd /d "%~dp0"

echo [ProcureFlow] Starting local web app...
if not exist node_modules (
  echo [ProcureFlow] Installing npm dependencies (first run)...
  call npm install
  if errorlevel 1 (
    echo [ProcureFlow] npm install failed.
    pause
    exit /b 1
  )
)

start "" "http://localhost:3000"
call npm run dev
