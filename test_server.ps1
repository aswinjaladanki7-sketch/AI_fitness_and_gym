$pages = @(
    'index.html',
    'login.html',
    'register.html',
    'dashboard.html',
    'fitness.html',
    'nutrition.html',
    'workouts.html',
    'ai-trainer.html',
    'progress.html',
    'profile.html',
    'css/style.css',
    'js/api.js',
    'js/ai-trainer.js'
)

Write-Host "Validating all web pages and static assets on http://localhost:8080/..." -ForegroundColor Cyan

foreach ($page in $pages) {
    try {
        $url = "http://localhost:8080/$page"
        $resp = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 3
        Write-Host " [PASS] $page -> HTTP $($resp.StatusCode) ($($resp.Content.Length) bytes)" -ForegroundColor Green
    } catch {
        Write-Host " [FAIL] $page -> Error: $_" -ForegroundColor Red
    }
}
