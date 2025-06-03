# AI-JRPG

An AI-powered Japanese Role-Playing Game (JRPG) project that combines traditional JRPG elements with modern AI technology.

## Overview

This project aims to create an engaging JRPG experience where AI plays a crucial role in:
- Dynamic story generation
- Character development
- Combat mechanics
- World building

## Migration Strategy

### Phase-Based Approach

## Phase 1: Foundation Setup (Week 1-2) ✅
**Goal**: Establish core Java infrastructure and dependencies

### Milestones:
1. **Project Structure & Build System** ✅
   - Update Gradle configuration for multi-module project ✅
   - Configure Java 21 features (records, pattern matching, virtual threads) ✅
   - Set up Spring Boot modules for different concerns ✅
   - Configure testing framework (JUnit 5, TestContainers) ✅

2. **Core Dependencies Setup** ✅
   - Database: Spring Data JPA + PostgreSQL ✅
   - Caching: Spring Cache + Redis ✅
   - Web: Spring WebMVC + WebFlux for reactive endpoints ✅
   - AI Integration: HTTP clients for Claude/OpenAI APIs ✅
   - Configuration: Spring Configuration Properties ✅
     - Implemented AppConfig class for centralized configuration
     - Updated services to use configuration properties
     - Removed @Value annotations in favor of type-safe configuration
   - Monitoring: Spring Actuator + Micrometer ✅

3. **Basic Domain Model** ✅
   - Create Java records for core entities (PlayerContext, ActionEvent, NPCRelationship) ✅
   - Implement basic repository interfaces ✅
     - Created JPA repositories for all domain entities
     - Added custom query methods for common operations
     - Implemented type-safe repository interfaces
   - Set up JPA entities and relationships

### Deliverables:
- Multi-module Gradle project structure ✅
- Spring Boot application with health checks ✅
- Basic domain model and repositories (In Progress)
- Database schema migration scripts (In Progress)

---

## Phase 2: Core Context Management (Week 3-4)  
**Goal**: Port context tracking and storage functionality

### Milestones:
1. **Context Storage Layer**
   - Port ContextStorage interface and implementations
   - Implement JPA repositories for PostgreSQL
   - Add Redis caching with Spring Cache
   - Create database migration scripts

2. **Context Manager Service**
   - Port ContextManager to Spring Service
   - Implement event-driven architecture with Spring Events
   - Add background processing with @Async and CompletableFuture
   - Session management and cleanup

3. **Domain Services**
   - Player session management
   - NPC relationship tracking
   - Location and movement tracking
   - Action recording and history

### Key Java Features:
- **Records**: Immutable data structures for contexts
- **Virtual Threads**: High-concurrency event processing
- **Pattern Matching**: Clean event handling logic

### Deliverables:
- Context management services
- PostgreSQL integration with connection pooling
- Redis caching layer
- Event processing pipeline

---

## Phase 3: AI Integration Services (Week 5-6)
**Goal**: Port AI services and prompt generation

### Milestones:
1. **AI Provider Framework**
   - Create AI provider abstraction with Strategy pattern
   - Implement Claude and OpenAI providers
   - Add circuit breaker and retry logic with Resilience4j
   - Rate limiting and request throttling

2. **Prompt Generation Engine**
   - Port prompt generation logic
   - Template engine integration (Thymeleaf/Freemarker)
   - Context summarization services
   - Caching and optimization

3. **AI Response Processing**
   - Response parsing and validation
   - Content filtering and safety checks
   - Response caching and metrics

### Java Advantages:
- **Spring Cloud Circuit Breaker**: Fault tolerance
- **WebClient**: Non-blocking HTTP clients  
- **Template Engines**: Professional prompt templating
- **Validation API**: Input/output validation

### Deliverables:
- AI provider services with failover
- Prompt generation engine
- Response processing pipeline
- API rate limiting and monitoring

---

## Phase 4: Web API Layer (Week 7-8)
**Goal**: Create RESTful APIs and web interface

### Milestones:
1. **REST API Development**
   - Spring MVC controllers for game actions
   - Input validation with Bean Validation
   - Error handling and custom exceptions
   - API documentation with OpenAPI/Swagger

2. **WebSocket Integration**
   - Real-time game events with Spring WebSocket
   - STOMP messaging for player actions
   - Session management for connected players

3. **Security & Authentication**
   - Spring Security configuration
   - JWT token management for sessions
   - Rate limiting and DDoS protection

### Spring Boot Features:
- **Spring MVC**: Robust REST API framework
- **Spring WebSocket**: Real-time communication
- **Spring Security**: Enterprise authentication
- **Spring Validation**: Comprehensive input validation

### Deliverables:
- REST API endpoints matching Go functionality
- WebSocket real-time updates
- Security and authentication layer
- Interactive web interface

---

## Phase 5: Enterprise Features (Week 9-10)
**Goal**: Add monitoring, deployment, and production features

### Milestones:
1. **Observability Stack**
   - Spring Actuator health checks and metrics
   - Distributed tracing with Micrometer Tracing
   - Structured logging with Logback
   - Custom business metrics

2. **Production Deployment**
   - Docker containerization with layered builds
   - Kubernetes deployment manifests
   - Environment-specific configuration
   - Database migration strategies

3. **Performance Optimization**
   - Connection pooling (HikariCP)
   - Redis optimization and clustering
   - JVM tuning for Java 21
   - Load testing and profiling

### Enterprise Java Benefits:
- **Spring Actuator**: Production-ready monitoring
- **Micrometer**: Metrics integration with Prometheus/Grafana
- **Spring Profiles**: Environment management
- **JVM Ecosystem**: Profiling and optimization tools

### Deliverables:
- Production monitoring and alerting
- Container deployment strategy
- Performance benchmarks vs Go implementation
- Operations runbooks

---

## Phase 6: Advanced AI Features (Week 11-12)
**Goal**: Implement next-generation autonomous agents

### Milestones:
1. **Autonomous Agent Framework**
   - AI agent lifecycle management
   - Decision-making algorithms with behavior trees
   - Knowledge graph integration for world consistency
   - Agent-to-agent communication protocols

2. **Web3 Integration Foundation**
   - Wallet connection services (MetaMask integration)
   - Smart contract interaction framework
   - Stablecoin payment processing
   - NFT asset management for equipment/items

3. **Visual Content Pipeline**
   - Midjourney API integration
   - Image generation request queuing
   - Asset storage and CDN integration
   - Real-time visual updates via WebSocket

### Advanced Features:
- **Virtual Threads**: Massive agent concurrency
- **Project Loom**: Simplified async programming
- **GraalVM Native**: Fast startup for edge deployment
- **Web3j**: Ethereum blockchain integration

### Deliverables:
- Autonomous AI agent system
- Web3 integration layer
- Visual content generation pipeline
- Real-time multiplayer foundation

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
   ```

2. Start required services:
   ```bash
   docker-compose up -d
   ```

3. Run the application:
   ```bash
   ./gradlew :app:repository:run
   ```

## Features

- AI-driven narrative
- Dynamic character interactions
- Traditional JRPG combat system
- Procedurally generated content

## License

This project is licensed under the MIT License - see the LICENSE file for details. 