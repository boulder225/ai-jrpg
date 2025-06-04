# AI-JRPG

An AI-powered Japanese Role-Playing Game (JRPG) project that combines traditional JRPG elements with modern AI technology.

## Overview

This project aims to create an engaging JRPG experience where AI plays a crucial role in:
- Dynamic story generation
- Character development
- Combat mechanics
- World building

## Architecture

The project follows a multi-module architecture:

1. **Core Module** (`app/core`)
   - Domain models and business logic
   - Pure Java 21 records for immutable data
   - Type-safe enums and validation

2. **Persistence Module** (`app/persistence`)
   - JPA entities and repositories
   - PostgreSQL with JSON support
   - MapStruct for clean mapping

3. **Context Module** (`app/context`)
   - Player context management
   - Event-driven architecture
   - Redis caching

4. **Repository Module** (`app/repository`)
   - REST API endpoints
   - Spring Boot application
   - H2 database for development
   - JPA repositories for data access

## Getting Started

### Prerequisites
- Java 21
- Gradle 8.x
- Docker (for Redis and PostgreSQL)
- Claude API Key
- OpenAI API Key

### Environment Setup
1. Set required environment variables:
   ```bash
   export CLAUDE_API_KEY=your_claude_api_key
   export OPENAI_API_KEY=your_openai_api_key
   export REDIS_HOST=localhost
   export REDIS_PORT=6379
   ```

2. Start required services:
   ```bash
   docker-compose up -d
   ```

3. Run the application:
   ```bash
   ./gradlew :app:repository:run
   ```

### Development

The project uses a multi-module Gradle setup:

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Run specific module
./gradlew :app:repository:run
```

### API Documentation

The API is available at:
- Repository Service: http://localhost:8081
- H2 Console: http://localhost:8081/h2-console

## Features

### Core Features
- ✅ Multi-module architecture
- ✅ Java 21 records and pattern matching
- ✅ Type-safe domain model
- ✅ Event-driven design

### Context Management
- ✅ Player session tracking
- ✅ Action history
- ✅ NPC relationships
- ✅ Location tracking

### AI Integration
- ✅ Claude API integration
- ✅ OpenAI API integration
- ✅ Prompt generation
- ✅ Response processing

### Data Persistence
- ✅ JPA repositories
- ✅ JSON support
- ✅ Caching with Redis
- ✅ Database migrations
- ✅ Entity mapping with MapStruct
- ✅ Lombok for clean code

## Project Structure

```
app/
├── core/                 # Core domain models and business logic
├── persistence/         # JPA entities and repositories
├── context/            # Player context and event management
└── repository/         # REST API and Spring Boot application
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── ai/rpg/
    │   │   │       ├── persistence/    # JPA entities
    │   │   │       ├── repository/     # Repository interfaces
    │   │   │       └── config/         # Spring configuration
    │   │   └── resources/
    │   └── test/
    │       └── java/
    │           └── ai/rpg/
    │               └── repository/     # Repository tests
    └── build.gradle
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 