#!/bin/bash
# CitaSmart Development Setup Script for L# Check Gradle
echo -e "${YELLOW}📦 Checking Gradle...${NC}"
if command_exists gradle; then
    gradle_version=$(gradle --version | grep "Gradle" | head -1 | awk '{print $2}')
    echo -e "${GREEN}✅ Gradle found: $gradle_version${NC}"
else
    echo -e "${YELLOW}⚠️  Gradle not found. Using Gradle Wrapper instead...${NC}"
    if [ -f "./gradlew" ]; then
        echo -e "${GREEN}✅ Gradle Wrapper found${NC}"
    else
        echo -e "${RED}❌ Gradle Wrapper not found. Please ensure you're in the correct directory.${NC}"
        exit 1
    fi
fiS

echo "🚀 CitaSmart Development Environment Setup"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check Docker
echo -e "${YELLOW}🐳 Checking Docker...${NC}"
if command_exists docker; then
    docker_version=$(docker --version)
    echo -e "${GREEN}✅ Docker found: $docker_version${NC}"
else
    echo -e "${RED}❌ Docker not found. Please install Docker.${NC}"
    exit 1
fi

# Check Docker Compose
echo -e "${YELLOW}🔧 Checking Docker Compose...${NC}"
if command_exists docker-compose; then
    compose_version=$(docker-compose --version)
    echo -e "${GREEN}✅ Docker Compose found: $compose_version${NC}"
else
    echo -e "${RED}❌ Docker Compose not found. Please install Docker Compose.${NC}"
    exit 1
fi

# Check Java
echo -e "${YELLOW}☕ Checking Java...${NC}"
if command_exists java; then
    java_version=$(java -version 2>&1 | grep "version" | awk -F '"' '{print $2}')
    echo -e "${GREEN}✅ Java found: $java_version${NC}"
    
    # Check if Java 17 or higher
    major_version=$(echo $java_version | awk -F. '{print $1}')
    if [ "$major_version" -lt 17 ]; then
        echo -e "${YELLOW}⚠️  Warning: Java 17 or higher is recommended. Current version: $java_version${NC}"
    fi
else
    echo -e "${RED}❌ Java not found. Please install Java 17 JDK.${NC}"
    exit 1
fi

# Check Maven
echo -e "${YELLOW}🔨 Checking Maven...${NC}"
if command_exists mvn; then
    maven_version=$(mvn --version | grep "Apache Maven" | awk '{print $3}')
    echo -e "${GREEN}✅ Maven found: $maven_version${NC}"
else
    echo -e "${RED}❌ Maven not found. Please install Apache Maven.${NC}"
    exit 1
fi

# Check Node.js
echo -e "${YELLOW}🟢 Checking Node.js...${NC}"
if command_exists node; then
    node_version=$(node --version)
    echo -e "${GREEN}✅ Node.js found: $node_version${NC}"
    
    # Check minimum version (Node 18+)
    major_version=$(echo $node_version | sed 's/v//' | awk -F. '{print $1}')
    if [ "$major_version" -lt 18 ]; then
        echo -e "${YELLOW}⚠️  Warning: Node.js 18 or higher is recommended. Current version: $node_version${NC}"
    fi
else
    echo -e "${RED}❌ Node.js not found. Please install Node.js 18 or higher.${NC}"
    exit 1
fi

# Check npm
echo -e "${YELLOW}📦 Checking npm...${NC}"
if command_exists npm; then
    npm_version=$(npm --version)
    echo -e "${GREEN}✅ npm found: $npm_version${NC}"
else
    echo -e "${RED}❌ npm not found. Please install npm.${NC}"
    exit 1
fi

# Check Angular CLI
echo -e "${YELLOW}🅰️  Checking Angular CLI...${NC}"
if command_exists ng; then
    ng_version=$(ng version --skip-git 2>/dev/null | grep "Angular CLI:" | awk '{print $3}')
    echo -e "${GREEN}✅ Angular CLI found: $ng_version${NC}"
else
    echo -e "${YELLOW}⚠️  Angular CLI not found. Installing...${NC}"
    npm install -g @angular/cli@17
    echo -e "${GREEN}✅ Angular CLI installed successfully${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Environment validation completed!${NC}"
echo ""
echo -e "${CYAN}Next steps:${NC}"
echo -e "${NC}1. Run: docker-compose up -d${NC}"
echo -e "${NC}2. Build backend: ./gradlew clean build (in each service directory)${NC}"
echo -e "${NC}3. Start development servers${NC}"
echo ""
echo -e "${NC}For more information, see README.md${NC}"
