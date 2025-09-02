# CitaSmart Development Setup Script
# PowerShell script to initialize development environmWrite-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Run: docker-compose up -d" -ForegroundColor White
Write-Host "2. Build backend: .\gradlew clean build (in each service directory)" -ForegroundColor White
Write-Host "3. Start development servers" -ForegroundColor White
Write-Host ""
Write-Host "For more information, see README.md" -ForegroundColor White
Write-Host "🚀 CitaSmart Development Environment Setup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Verificar Docker
Write-Host "🐳 Checking Docker..." -ForegroundColor Yellow
if (Get-Command docker -ErrorAction SilentlyContinue) {
    $dockerVersion = docker --version
    Write-Host "✅ Docker found: $dockerVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Docker not found. Please install Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar Docker Compose
Write-Host "🔧 Checking Docker Compose..." -ForegroundColor Yellow
if (Get-Command docker-compose -ErrorAction SilentlyContinue) {
    $composeVersion = docker-compose --version
    Write-Host "✅ Docker Compose found: $composeVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Docker Compose not found. Please install Docker Compose." -ForegroundColor Red
    exit 1
}

# Verificar Java
Write-Host "☕ Checking Java..." -ForegroundColor Yellow
if (Get-Command java -ErrorAction SilentlyContinue) {
    $javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString().Split('"')[1] }
    Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
    
    # Verificar si es Java 17 o superior
    $majorVersion = [int]($javaVersion.Split('.')[0])
    if ($majorVersion -lt 17) {
        Write-Host "⚠️  Warning: Java 17 or higher is recommended. Current version: $javaVersion" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Java not found. Please install Java 17 JDK." -ForegroundColor Red
    exit 1
}

# Verificar Gradle
Write-Host "🔨 Checking Gradle..." -ForegroundColor Yellow
if (Get-Command gradle -ErrorAction SilentlyContinue) {
    $gradleVersion = gradle --version | Select-String "Gradle" | ForEach-Object { $_.ToString().Split(' ')[1] }
    Write-Host "✅ Gradle found: $gradleVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Gradle not found. Please install Gradle or use Gradle Wrapper." -ForegroundColor Red
    Write-Host "ℹ️  You can use the Gradle Wrapper (gradlew) included in the projects." -ForegroundColor Yellow
}

# Verificar Node.js
Write-Host "🟢 Checking Node.js..." -ForegroundColor Yellow
if (Get-Command node -ErrorAction SilentlyContinue) {
    $nodeVersion = node --version
    Write-Host "✅ Node.js found: $nodeVersion" -ForegroundColor Green
    
    # Verificar versión mínima (Node 18+)
    $majorVersion = [int]($nodeVersion.Substring(1).Split('.')[0])
    if ($majorVersion -lt 18) {
        Write-Host "⚠️  Warning: Node.js 18 or higher is recommended. Current version: $nodeVersion" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Node.js not found. Please install Node.js 18 or higher." -ForegroundColor Red
    exit 1
}

# Verificar npm
Write-Host "📦 Checking npm..." -ForegroundColor Yellow
if (Get-Command npm -ErrorAction SilentlyContinue) {
    $npmVersion = npm --version
    Write-Host "✅ npm found: $npmVersion" -ForegroundColor Green
} else {
    Write-Host "❌ npm not found. Please install npm." -ForegroundColor Red
    exit 1
}

# Verificar Angular CLI
Write-Host "🅰️  Checking Angular CLI..." -ForegroundColor Yellow
if (Get-Command ng -ErrorAction SilentlyContinue) {
    $ngVersion = ng version --skip-git 2>$null | Select-String "Angular CLI:" | ForEach-Object { $_.ToString().Split(' ')[-1] }
    Write-Host "✅ Angular CLI found: $ngVersion" -ForegroundColor Green
} else {
    Write-Host "⚠️  Angular CLI not found. Installing..." -ForegroundColor Yellow
    npm install -g @angular/cli@17
    Write-Host "✅ Angular CLI installed successfully" -ForegroundColor Green
}

Write-Host ""
Write-Host "🎉 Environment validation completed!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Run: docker-compose up -d" -ForegroundColor White
Write-Host "2. Build backend: mvn clean install" -ForegroundColor White
Write-Host "3. Start development servers" -ForegroundColor White
Write-Host ""
Write-Host "For more information, see README.md" -ForegroundColor Gray
