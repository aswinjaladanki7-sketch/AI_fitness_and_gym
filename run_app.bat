@echo off
title AI GYM & FITNESS ASSISTANT - SRM IST 21CSC203P
echo ========================================================
echo   AI GYM & FITNESS ASSISTANT - Launching Application
echo   SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
echo   School of Computer Science and Engineering
echo ========================================================
echo.

where python >nul 2>nul
if %errorlevel% equ 0 (
    echo Python detected. Launching Flask backend on port 5000...
    start "AI Gym Flask Backend" cmd /k "cd AI-GYM-FITNESS\backend && python app.py"
) else (
    echo [Notice] Python not in system PATH. Running in zero-dependency web mode.
)

echo Starting local web server on http://localhost:8080...
start powershell -ExecutionPolicy Bypass -File "%~dp0serve.ps1" -Port 8080

timeout /t 2 >nul
start http://localhost:8080/index.html
echo.
echo Application opened in default web browser!
pause
