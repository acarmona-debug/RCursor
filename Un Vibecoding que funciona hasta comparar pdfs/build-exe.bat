@echo off
setlocal
cd /d "%~dp0"

echo [ProcureFlow] Building desktop executable...
py -3.12 -m venv .venv
if errorlevel 1 (
  echo [ProcureFlow] Could not create venv with Python 3.12.
  pause
  exit /b 1
)

call ".\.venv\Scripts\activate.bat"
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
if errorlevel 1 (
  echo [ProcureFlow] Dependency installation failed.
  pause
  exit /b 1
)

python -m PyInstaller --noconsole --onefile --name "ProcureFlow" main.py
if errorlevel 1 (
  echo [ProcureFlow] EXE build failed.
  pause
  exit /b 1
)

echo [ProcureFlow] Build completed: dist\ProcureFlow.exe
start "" explorer ".\dist"
