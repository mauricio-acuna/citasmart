#!/bin/bash
# CitaSmart - Stop Development Environment

echo "🛑 Stopping CitaSmart Development Environment"
echo "============================================="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m'

# Change to project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$PROJECT_ROOT"

echo -e "${GRAY}📍 Project root: $PROJECT_ROOT${NC}"

# Stop Docker services
echo -e "${YELLOW}🐳 Stopping Docker services...${NC}"
docker-compose down

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Docker services stopped successfully${NC}"
else
    echo -e "${YELLOW}⚠️  Some services may still be running${NC}"
fi

# Optional: Clean up Docker resources
echo -n "Do you want to clean up Docker volumes and networks? (y/N): "
read cleanup
if [ "$cleanup" = "y" ] || [ "$cleanup" = "Y" ]; then
    echo -e "${YELLOW}🧹 Cleaning up Docker resources...${NC}"
    docker-compose down -v --remove-orphans
    docker system prune -f
    echo -e "${GREEN}✅ Docker cleanup completed${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Development environment stopped successfully!${NC}"
echo ""
echo -e "${GRAY}To restart: ./scripts/setup/start-dev.sh${NC}"
