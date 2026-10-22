@echo off
title Push AI Gym and Fitness Assistant to GitHub
echo ========================================================
echo   AI GYM and FITNESS ASSISTANT - GitHub Push
echo   Repository: https://github.com/aswinjaladanki7-sketch/AI_fitness_and_gym.git
echo ========================================================
echo.

set "PATH=%LOCALAPPDATA%\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\cmd;%LOCALAPPDATA%\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\ucrt64\bin;%PATH%"

echo Staging any recent changes...
git add .

echo Committing changes...
git commit -m "Update project files" 2>nul

echo.
echo Pushing branch 'main' to GitHub origin...
echo (If prompted, please sign in with GitHub in your browser)
echo.
git push -u origin main

echo.
if %ERRORLEVEL% equ 0 (
    echo ========================================================
    echo   SUCCESS! All files pushed to GitHub successfully!
    echo   URL: https://github.com/aswinjaladanki7-sketch/AI_fitness_and_gym
    echo ========================================================
) else (
    echo ========================================================
    echo   Push encountered an error or needs authentication.
    echo ========================================================
)
echo.
pause
