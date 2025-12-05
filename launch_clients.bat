@echo off
REM Trivia Game - Multiple Client Launcher
REM This script launches multiple client instances for scalability testing

set CLIENT_COUNT=15
set CLIENT_DIR=TriviaClient

echo ========================================
echo Trivia Game - Client Launcher
echo ========================================
echo.
echo Launching %CLIENT_COUNT% client instances...
echo.

for /L %%i in (1,1,%CLIENT_COUNT%) do (
    echo Starting Client %%i...
    start "Trivia Client %%i" cmd /k "cd %CLIENT_DIR% && gradlew run"
    timeout /t 2 /nobreak >nul
)

echo.
echo ========================================
echo All %CLIENT_COUNT% clients launched!
echo ========================================
echo.
echo Instructions:
echo 1. Wait for all client windows to load
echo 2. In each client, enter a unique username (User1, User2, etc.)
echo 3. Set Host: localhost
echo 4. Set Port: 12345
echo 5. Click Login
echo.
echo Check the server console to see active connections!
echo.
pause
