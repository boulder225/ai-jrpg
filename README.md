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
## Tech Guide: ai-rpg-events

`ai-rpg-events` is a JVM-based platform that combines event sourcing with AI-driven gameplay. The project consists of a backend written in Java (Gradle build) and a React frontend. Key packages include event sourcing infrastructure, RPG domain models, AI integration, a pluggable game system layer, and a REST API server.

---

### 1. Architectural Layers

1. **Event Sourcing Core**  
   - `core.domain` defines the generic `Command`, `DomainEvent`, and `CommandResult` types.  
   - `core.infrastructure` provides the `EventStore` interface with implementations such as `InMemoryEventStore` and helpers like `ExpectedVersion` and `EventSourcing` for state reconstruction.  
     Example interface methods show append and read operations with optimistic concurrency control.

2. **RPG Domain Layer**  
   - `rpg` package declares domain commands (`RPGCommand`) and events (`RPGEvent`) capturing player actions, relationships, NPC evolution, and world events.  
   - `RPGBusinessLogic` processes commands and emits events, e.g., `movePlayer`, `performAction`, `startQuest`, and other domain rules.

3. **Command Handling**  
   - `RPGCommandHandler` rebuilds state from event streams and applies functional command processors. It contains methods like `executePlayerCommand` and `executeNPCCommand` for invoking domain logic on reconstructed state.

4. **AI Integration**  
   - `ai` package handles the connection to Claude AI. `ClaudeAIService` composes prompts and calls the Anthropic API, using `AICache` and `RateLimiter` to manage requests.  
   - `AIConfig` loads environment-based settings like model, token limits, and API keys.

5. **Game System Plugins**  
   - The `gameSystem` module defines a `GameSystem` interface for pluggable rule sets. The provided `DnDGameSystem` implements D&D-style logic and loads adventure data via `DnDAdventureLoader`.  
   - Generic structures like `GameContext`, `GameResponse`, and `AdventureData` allow the AI and API to operate independently of a specific ruleset.

6. **API Layer**  
   - `api.RPGApiServer` exposes REST endpoints for creating sessions, processing game actions, retrieving world state, AI prompts, and metrics. Initialization sets up the event store, AI service, and selected game system.

7. **Frontend**  
   - React app in `frontend/` interacts with the backend via fetch calls. It provides components for session creation, command execution, and a simple adventure map.

---

### 2. Code Flow and Interaction

1. **Session Creation**  
   - The client issues a POST to `/api/session/create` (listed in the README under API Endpoints).  
   - `RPGApiServer` creates an in-memory event store, builds an initial player context, and uses `ClaudeAIService` to generate a welcome message.

2. **Command Processing**  
   - When the player executes an action (`/api/game/action`), the API server retrieves the player’s stream of events, reconstructs state via `RPGCommandHandler`, and applies domain logic.  
   - The resulting events are appended to the store. AI responses are generated from full context (prompt built in `generateGameContextForAI`). Metrics about AI usage and latency are tracked with `AIMetrics` and `RPGMetrics`.

3. **State Queries**  
   - `/api/game/status` returns a contextual summary of the player’s current state including location, health, and quest progress, built by methods such as `buildContextSummary`.  
   - `/api/ai/prompt` exposes the exact AI prompt for transparency.

4. **Game System**  
   - The active game system (e.g., D&D) supplies adventure data, quick commands, and rules context. Locations, NPCs, and encounters are loaded from JSON resources such as `tsr_basic_adventure.json`. `DnDGameSystem` processes standard commands like movement, attacks, or conversations.

5. **Frontend Flow**  
   - React components call the API service methods (`createSession`, `executeAction`, `getStatus`, `getMetadata`, etc.), display AI responses, and update the map. Example call for session creation in `App.js` shows asynchronous `fetch` usage to `/api/session/create`.

---

### 3. Interaction Summary

1. **Server Startup**  
   - `Main` → creates `RPGApiServer` → loads `.env`, configures AI and game system, and starts HTTP server.

2. **Player Journey**  
   - Client begins by calling `/api/session/create` with player name.  
   - Server stores a `PlayerCreated` event and returns a message from Claude or a fallback.

3. **Actions**  
   - Player actions are posted to `/api/game/action`.  
   - Server reconstructs player state, calls `RPGBusinessLogic` to produce events (e.g., `PlayerMovedToLocation`, `ActionPerformed`), and uses `ClaudeAIService` for contextual narrative.

4. **World State**  
   - Queries to `/api/game/status` or `/api/ai/prompt` display the evolving state derived from events.  
   - The React UI provides visual feedback and allows quick commands defined by the game system.

---

### 4. Key Features

- Persistent event streams enable time-travel queries and detailed state models as highlighted in the README architecture section.
- Functional command processing supports optimistic concurrency and immutable events.
- The AI layer optionally calls the Claude API to generate immersive text, with local simulation as fallback.
- Game system plugins decouple rules and content from the core engine, facilitating support for different RPG systems.

Overall, the codebase offers a layered design:

1. **Infrastructure** – event store and concurrency handling.  
2. **Domain** – commands, events, state models.  
3. **Business Logic** – command processing and world rules.  
4. **AI Integration** – prompts, caching, metrics.  
5. **Game System Plugins** – rulesets and adventure data.  
6. **API Server** – HTTP endpoints wiring everything together.  
7. **Frontend** – minimal React interface for players.

This separation of concerns allows developers to extend the system with new game systems or AI behaviors while retaining the benefits of event sourcing for persistent world state.

