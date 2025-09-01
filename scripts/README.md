# CitaSmart Development Scripts

Collection of utility scripts for CitaSmart development and deployment.

## 📁 Available Scripts

```
scripts/
├── setup/              # Setup and installation scripts
│   ├── setup-dev.sh
│   ├── setup-prod.sh
│   └── install-deps.sh
├── build/              # Build and deployment scripts
│   ├── build-all.sh
│   ├── build-backend.sh
│   ├── build-frontend.sh
│   └── deploy.sh
├── database/           # Database management scripts
│   ├── backup.sh
│   ├── restore.sh
│   ├── migrate.sh
│   └── seed.sh
├── monitoring/         # Monitoring and health check scripts
│   ├── health-check.sh
│   ├── logs.sh
│   └── metrics.sh
└── utils/             # Utility scripts
    ├── clean.sh
    ├── reset.sh
    └── test-all.sh
```

## 🚀 Quick Commands

### Development Setup
```bash
# Complete development setup
./scripts/setup/setup-dev.sh

# Install all dependencies
./scripts/setup/install-deps.sh
```

### Build and Deploy
```bash
# Build all services
./scripts/build/build-all.sh

# Deploy to staging
./scripts/build/deploy.sh staging

# Deploy to production
./scripts/build/deploy.sh production
```

### Database Operations
```bash
# Create database backup
./scripts/database/backup.sh

# Restore from backup
./scripts/database/restore.sh backup-2025-09-01.sql

# Run migrations
./scripts/database/migrate.sh
```

### Monitoring
```bash
# Check health of all services
./scripts/monitoring/health-check.sh

# View logs
./scripts/monitoring/logs.sh api-gateway

# Get metrics
./scripts/monitoring/metrics.sh
```

## 📋 Usage Examples

### First Time Setup
```bash
# 1. Clone repository
git clone https://github.com/mauricio-acuna/citasmart.git
cd citasmart

# 2. Run development setup
chmod +x scripts/setup/setup-dev.sh
./scripts/setup/setup-dev.sh

# 3. Start development environment
docker-compose up -d postgres redis
./scripts/build/build-all.sh
```

### Daily Development
```bash
# Start all services for development
./scripts/utils/start-dev.sh

# Run tests
./scripts/utils/test-all.sh

# Clean and reset environment
./scripts/utils/reset.sh
```

### Deployment
```bash
# Build for production
./scripts/build/build-all.sh --env=production

# Deploy to staging
./scripts/build/deploy.sh staging

# Check deployment health
./scripts/monitoring/health-check.sh --env=staging
```

## 🔧 Script Configuration

Most scripts accept configuration through environment variables or command line arguments:

```bash
# Environment variables
export ENVIRONMENT=development
export DATABASE_URL=postgresql://localhost:5432/citasmart_dev
export API_BASE_URL=http://localhost:8080

# Command line arguments
./scripts/build/deploy.sh --env=staging --version=1.2.0 --no-migrate
```

## 📝 Adding New Scripts

1. Create your script in the appropriate category folder
2. Make it executable: `chmod +x your-script.sh`
3. Add proper header and documentation
4. Update this README.md

Example script template:
```bash
#!/bin/bash
# Description: What this script does
# Usage: ./script-name.sh [options]
# Author: Your Name

set -e  # Exit on error

# Script implementation here
```

## 🔒 Security Notes

- Scripts that handle sensitive data are in `.gitignore`
- Production scripts require additional authentication
- Always review scripts before execution in production
- Use environment variables for secrets, never hardcode

## 🤝 Contributing

When adding new scripts:
- Follow existing naming conventions
- Add proper error handling
- Include usage documentation
- Test thoroughly before committing
