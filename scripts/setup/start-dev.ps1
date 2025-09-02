# CitaSmart Development Environment
# This script starts all required services for development

Write-Host "🚀 Starting CitaSmart Development Environment" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# Change to project root
$projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
Set-Location $projectRoot

Write-Host "📍 Project root: $projectRoot" -ForegroundColor Gray

# Start infrastructure services
Write-Host "🐳 Starting infrastructure services..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Infrastructure services started successfully" -ForegroundColor Green
    
    # Wait for services to be ready
    Write-Host "⏳ Waiting for services to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
    
    # Check service health
    Write-Host "🔍 Checking service health..." -ForegroundColor Yellow
    
    # PostgreSQL
    $pgHealth = docker-compose ps postgres | Select-String "healthy"
    if ($pgHealth) {
        Write-Host "✅ PostgreSQL is healthy" -ForegroundColor Green
    } else {
        Write-Host "⚠️  PostgreSQL is starting..." -ForegroundColor Yellow
    }
    
    # Redis
    $redisHealth = docker-compose ps redis | Select-String "Up"
    if ($redisHealth) {
        Write-Host "✅ Redis is running" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Redis is starting..." -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "🌐 Service URLs:" -ForegroundColor Cyan
    Write-Host "📊 pgAdmin: http://localhost:5050" -ForegroundColor White
    Write-Host "   Email: admin@citasmart.com" -ForegroundColor Gray
    Write-Host "   Password: admin123" -ForegroundColor Gray
    Write-Host ""
    Write-Host "📋 Database Connection:" -ForegroundColor Cyan
    Write-Host "   Host: localhost:5432" -ForegroundColor White
    Write-Host "   Database: citasmart_db" -ForegroundColor White
    Write-Host "   Username: citasmart_user" -ForegroundColor White
    Write-Host "   Password: citasmart_pass" -ForegroundColor White
    Write-Host ""
    Write-Host "🔴 Redis:" -ForegroundColor Cyan
    Write-Host "   Host: localhost:6379" -ForegroundColor White
    Write-Host ""
    
    # Build backend services
    Write-Host "🔨 Building backend services..." -ForegroundColor Yellow
    
    # API Gateway
    if (Test-Path "backend/api-gateway/build.gradle") {
        Set-Location "backend/api-gateway"
        Write-Host "📦 Building API Gateway..." -ForegroundColor White
        .\gradlew clean compileJava
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ API Gateway built successfully" -ForegroundColor Green
        } else {
            Write-Host "❌ API Gateway build failed" -ForegroundColor Red
        }
        Set-Location $projectRoot
    }
    
    # User Service
    if (Test-Path "backend/user-service/build.gradle") {
        Set-Location "backend/user-service"
        Write-Host "📦 Building User Service..." -ForegroundColor White
        .\gradlew clean compileJava
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ User Service built successfully" -ForegroundColor Green
        } else {
            Write-Host "❌ User Service build failed" -ForegroundColor Red
        }
        Set-Location $projectRoot
    }
    
    Write-Host ""
    Write-Host "🎉 Development environment is ready!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "1. Start API Gateway: cd backend/api-gateway && .\gradlew bootRun" -ForegroundColor White
    Write-Host "2. Start User Service: cd backend/user-service && .\gradlew bootRun" -ForegroundColor White
    Write-Host "3. Start Frontend: cd frontend && ng serve" -ForegroundColor White
    Write-Host ""
    Write-Host "To stop services: docker-compose down" -ForegroundColor Gray
    
} else {
    Write-Host "❌ Failed to start infrastructure services" -ForegroundColor Red
    Write-Host "Please check Docker and docker-compose configuration" -ForegroundColor Red
    exit 1
}
