# Trivia Game - Multiple Client Launcher (PowerShell)
# This script launches multiple client instances for scalability testing

param(
    [int]$ClientCount = 15,
    [string]$ClientDir = "TriviaClient"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Trivia Game - Client Launcher" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Launching $ClientCount client instances..." -ForegroundColor Yellow
Write-Host ""

$jobs = @()

for ($i = 1; $i -le $ClientCount; $i++) {
    Write-Host "Starting Client $i..." -ForegroundColor Green
    
    $job = Start-Process -FilePath "powershell.exe" `
        -ArgumentList "-NoExit", "-Command", "cd '$ClientDir'; .\gradlew.bat run" `
        -WindowStyle Normal `
        -PassThru
    
    $jobs += $job
    Start-Sleep -Milliseconds 2000
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All $ClientCount clients launched!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Instructions:" -ForegroundColor Yellow
Write-Host "1. Wait for all client windows to load"
Write-Host "2. In each client, enter a unique username (User1, User2, etc.)"
Write-Host "3. Set Host: localhost"
Write-Host "4. Set Port: 12345"
Write-Host "5. Click Login"
Write-Host ""
Write-Host "Check the server console to see active connections!" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
