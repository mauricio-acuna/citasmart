#!/bin/bash
# CitaSmart Development Environment
# This script starts all required services for development

echo "🚀 Starting CitaSmart Development Environment"
echo "============================================="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
GRAY='\033[0;37m'
NC='\033[0m'

# Change to project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$PROJECT_ROOT"

echo -e "${GRAY}📍 Project root: $PROJECT_ROOT${NC}"

# Start infrastructure services
echo -e "${YELLOW}🐳 Starting infrastructure services...${NC}"
docker-compose up -d

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Infrastructure services started successfully${NC}"
    
    # Wait for services to be ready
    echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
    sleep 10
    
    # Check service health
    echo -e "${YELLOW}🔍 Checking service health...${NC}"
    
    # PostgreSQL
    if docker-compose ps postgres | grep -q "healthy"; then
        echo -e "${GREEN}✅ PostgreSQL is healthy${NC}"
    else
        echo -e "${YELLOW}⚠️  PostgreSQL is starting...${NC}"
    fi
    
    # Redis
    if docker-compose ps redis | grep -q "Up"; then
        echo -e "${GREEN}✅ Redis is running${NC}"
    else
        echo -e "${YELLOW}⚠️  Redis is starting...${NC}"
    fi
    
    echo ""
    echo -e "${CYAN}🌐 Service URLs:${NC}"
    echo -e "${WHITE}📊 pgAdmin: http://localhost:5050${NC}"
    echo -e "${GRAY}   Email: admin@citasmart.com${NC}"
    echo -e "${GRAY}   Password: admin123${NC}"
    echo ""
    echo -e "${CYAN}📋 Database Connection:${NC}"
    echo -e "${WHITE}   Host: localhost:5432${NC}"
    echo -e "${WHITE}   Database: citasmart_db${NC}"
    echo -e "${WHITE}   Username: citasmart_user${NC}"
    echo -e "${WHITE}   Password: citasmart_pass${NC}"
    echo ""
    echo -e "${CYAN}🔴 Redis:${NC}"
    echo -e "${WHITE}   Host: localhost:6379${NC}"
    echo ""
    
    # Build backend services
    echo -e "${YELLOW}🔨 Building backend services...${NC}"
    
    # API Gateway
    if [ -f "backend/api-gateway/build.gradle" ]; then
        cd "backend/api-gateway"
        echo -e "${WHITE}📦 Building API Gateway...${NC}"
        ./gradlew clean compileJava
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ API Gateway built successfully${NC}"
        else
            echo -e "${RED}❌ API Gateway build failed${NC}"
        fi
        cd "$PROJECT_ROOT"
    fi
    
    # User Service
    if [ -f "backend/user-service/build.gradle" ]; then
        cd "backend/user-service"
        echo -e "${WHITE}📦 Building User Service...${NC}"
        ./gradlew clean compileJava
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✅ User Service built successfully${NC}"
        else
            echo -e "${RED}❌ User Service build failed${NC}"
        fi
        cd "$PROJECT_ROOT"
    fi
    
    echo ""
    echo -e "${GREEN}🎉 Development environment is ready!${NC}"
    echo ""
    echo -e "${CYAN}Next steps:${NC}"
    echo -e "${WHITE}1. Start API Gateway: cd backend/api-gateway && ./gradlew bootRun${NC}"
    echo -e "${WHITE}2. Start User Service: cd backend/user-service && ./gradlew bootRun${NC}"
    echo -e "${WHITE}3. Start Frontend: cd frontend && ng serve${NC}"
    echo ""
    echo -e "${GRAY}To stop services: docker-compose down${NC}"
    
else
    echo -e "${RED}❌ Failed to start infrastructure services${NC}"
    echo -e "${RED}Please check Docker and docker-compose configuration${NC}"
    exit 1
fi
