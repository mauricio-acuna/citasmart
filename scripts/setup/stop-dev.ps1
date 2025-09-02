# CitaSmart - Stop Development Environment

Write-Host "🛑 Stopping CitaSmart Development Environment" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# Change to project root
$projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Set-Location $projectRoot

Write-Host "📍 Project root: $projectRoot" -ForegroundColor Gray

# Stop Docker services
Write-Host "🐳 Stopping Docker services..." -ForegroundColor Yellow
docker-compose down

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Docker services stopped successfully" -ForegroundColor Green
} else {
    Write-Host "⚠️  Some services may still be running" -ForegroundColor Yellow
}

# Optional: Clean up Docker resources
$cleanup = Read-Host "Do you want to clean up Docker volumes and networks? (y/N)"
if ($cleanup -eq 'y' -or $cleanup -eq 'Y') {
    Write-Host "🧹 Cleaning up Docker resources..." -ForegroundColor Yellow
    docker-compose down -v --remove-orphans
    docker system prune -f
    Write-Host "✅ Docker cleanup completed" -ForegroundColor Green
}

Write-Host ""
Write-Host "🎉 Development environment stopped successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "To restart: .\scripts\setup\start-dev.ps1" -ForegroundColor Gray
