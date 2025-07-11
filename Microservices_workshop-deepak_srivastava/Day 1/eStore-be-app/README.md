# Spring WebFlux E-commerce Cart Application

A reactive e-commerce cart application built with Spring WebFlux, R2DBC, Redis, and Server-Sent Events (SSE) for real-time cart updates.

## Features

- **Reactive REST APIs** using Spring WebFlux functional routing
- **Real-time cart updates** via Server-Sent Events (SSE) from Redis
- **R2DBC** for reactive database operations with H2
- **Redis pub/sub** for cart event streaming
- **WebSocket** support for chat functionality
- **gRPC** service for chat
- **RSocket** for reactive messaging
- **Structured JSON logging** with Logback
- **Comprehensive testing** with WebTestClient and Testcontainers

## SSE Implementation: Real-time Cart Events

### Overview

The application implements Server-Sent Events (SSE) to stream cart changes in real-time from embedded Redis. When cart operations occur (add item, remove item, update cart, etc.), events are published to Redis and streamed to connected clients via SSE.

### Architecture

```
Cart Operations → CartEventService → Redis Pub/Sub → SSE Controller → Browser
```

### Components

#### 1. CartEvent Model
```java
@Data
public class CartEvent {
    private String eventType; // ADD_ITEM, REMOVE_ITEM, UPDATE_CART, CLEAR_CART
    private Long cartId;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String cartStatus;
    private Instant timestamp;
    private String message;
}
```

#### 2. CartEventService
- Publishes cart events to Redis channel `cart:events`
- Subscribes to Redis events for SSE streaming
- Provides convenience methods for different event types

#### 3. SSE Controller
- `/cart-events` - Streams all cart events
- `/cart-events/{userId}` - Streams events for specific user
- Returns events in SSE format: `data: {JSON}\n\n`

### API Endpoints

#### SSE Endpoints
- `GET /cart-events` - Stream all cart events
- `GET /cart-events/{userId}` - Stream events for specific user

#### Cart Operations (trigger events)
- `POST /carts/user/{userId}` - Create cart
- `PUT /carts/{id}` - Update cart status
- `DELETE /carts/{id}` - Delete cart
- `POST /carts/{cartId}/items` - Add item to cart
- `DELETE /carts/items/{itemId}` - Remove item from cart

### Event Types

1. **ADD_ITEM** - When item is added to cart
2. **REMOVE_ITEM** - When item is removed from cart
3. **UPDATE_CART** - When cart status is updated
4. **CLEAR_CART** - When cart is deleted

### Testing

#### Browser Test
1. Start the application
2. Navigate to `http://localhost:8080/sse-test.html`
3. Connect to SSE stream
4. Trigger cart operations
5. Watch real-time events appear

#### Automated Tests
```bash
# Run SSE tests
mvn test -Dtest=SseCartEventsTest
```

### Example SSE Event
```json
{
  "eventType": "ADD_ITEM",
  "cartId": 1,
  "userId": 1,
  "productId": 101,
  "quantity": 2,
  "timestamp": "2025-07-10T12:30:45.123Z",
  "message": "Item added to cart: productId=101, quantity=2"
}
```

## Core Concept Questions & Answers

### 1. What are the differences between Mono and Flux, and where did you use each?

**Mono** represents a stream that emits zero or one item, while **Flux** represents a stream that emits zero to many items.

**Usage in this project:**
- **Mono**: Single-entity operations (get user by ID, create cart, save cart item)
- **Flux**: Collections and streams (get all users, list cart items, SSE event stream)

**Example:**
```java
// Mono - single result
public Mono<ServerResponse> getById(ServerRequest req) {
    return repository.findById(id).flatMap(user -> ServerResponse.ok().bodyValue(user));
}

// Flux - multiple results
public Flux<CartEvent> subscribeToCartEvents() {
    return redisTemplate.listenTo(ChannelTopic.of("cart:events"));
}
```

### 2. How does R2DBC differ from traditional JDBC?

**R2DBC** is reactive and non-blocking, while **JDBC** is blocking and synchronous.

**Key Differences:**
- **R2DBC**: Returns `Mono<T>` or `Flux<T>`, non-blocking I/O
- **JDBC**: Returns direct objects, blocking I/O
- **R2DBC**: Better for high-concurrency, reactive applications
- **JDBC**: Simpler for traditional request-response patterns

### 3. How does Spring WebFlux routing differ from @RestController?

**WebFlux functional routing** uses `RouterFunction` and `HandlerFunction`, while **@RestController** uses annotations.

**Functional Routing:**
```java
@Bean
public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
    return RouterFunctions
        .route(RequestPredicates.GET("/users"), handler::getAll)
        .andRoute(RequestPredicates.POST("/users"), handler::create);
}
```

**@RestController:**
```java
@RestController
public class UserController {
    @GetMapping("/users")
    public Flux<User> getAll() { ... }
}
```

### 4. What are the advantages of using @Transactional with R2DBC?

**Advantages:**
- **Atomic operations**: Multiple database operations succeed or fail together
- **Consistency**: Ensures data integrity across operations
- **Rollback support**: Automatic rollback on exceptions

**Example:**
```java
@Transactional
public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(User.class)
        .flatMap(repository::save)
        .flatMap(saved -> ServerResponse.ok().bodyValue(saved));
}
```

### 5. Explain the difference between optimistic vs pessimistic locking

**Optimistic Locking:**
- Assumes conflicts are rare
- Uses version fields or timestamps
- Throws exception on conflict
- Better performance, less database contention

**Pessimistic Locking:**
- Assumes conflicts are common
- Uses database locks (SELECT FOR UPDATE)
- Blocks other transactions
- Higher performance cost, more contention

**Usage:**
- **Optimistic**: High-read, low-write scenarios
- **Pessimistic**: High-write, critical data scenarios

### 6. Describe how session management works using Redis in reactive apps

**Redis Session Management:**
- Sessions stored in Redis instead of application memory
- Enables horizontal scaling across multiple instances
- Reactive Redis operations with `ReactiveRedisTemplate`
- Session data serialized as JSON in Redis

**Configuration:**
```yaml
spring:
  session:
    store-type: redis
  redis:
    host: localhost
    port: 6379
```

### 7. How did you expose and verify SSE updates?

**SSE Implementation:**
1. **Redis pub/sub** for event distribution
2. **SSE controller** with `MediaType.TEXT_EVENT_STREAM_VALUE`
3. **EventSource** in browser for real-time updates
4. **WebTestClient** for automated testing

**Verification:**
- Browser test page (`/sse-test.html`)
- Automated tests with `StepVerifier`
- Real-time event monitoring

### 8. What is the role of Swagger/OpenAPI in CI/CD and API consumer tooling?

**Swagger/OpenAPI Benefits:**
- **API Documentation**: Auto-generated, always up-to-date
- **Testing**: Interactive API testing interface
- **Code Generation**: Client SDKs for multiple languages
- **CI/CD Integration**: API contract validation
- **Consumer Tooling**: IDE plugins, API clients

### 9. How does WebTestClient differ from MockMvc in testing?

**WebTestClient:**
- **Reactive**: Designed for WebFlux applications
- **Non-blocking**: Returns `Flux`/`Mono` for testing
- **SSE Support**: Can test Server-Sent Events
- **WebSocket Support**: Can test WebSocket endpoints

**MockMvc:**
- **Traditional**: Designed for Spring MVC
- **Blocking**: Synchronous testing
- **Servlet-based**: Uses Servlet API

### 10. Compare WebSocket, SSE, and RSocket for real-time data needs

**WebSocket:**
- **Bidirectional**: Full-duplex communication
- **Complex**: Requires connection management
- **Use case**: Chat, gaming, real-time collaboration

**SSE (Server-Sent Events):**
- **Unidirectional**: Server to client only
- **Simple**: Built on HTTP, automatic reconnection
- **Use case**: Notifications, live feeds, status updates

**RSocket:**
- **Bidirectional**: Full-duplex with multiple interaction models
- **Reactive**: Built for reactive streams
- **Use case**: Microservices communication, high-performance scenarios

## Running the Application

### Prerequisites
- Java 21
- Maven 3.6+
- Redis (embedded for tests)

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/springbootapp-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

### Test the SSE Functionality
1. Start the application
2. Open `http://localhost:8080/sse-test.html`
3. Connect to SSE stream
4. Trigger cart operations
5. Watch real-time events

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=SseCartEventsTest
```

## Project Structure

```
src/
├── main/
│   ├── java/com/evolve/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers (SSE)
│   │   ├── handler/         # Functional route handlers
│   │   ├── model/           # Entity models
│   │   ├── repository/      # R2DBC repositories
│   │   ├── route/           # Functional routing
│   │   ├── service/         # Business logic (CartEventService)
│   │   └── websocket/       # WebSocket handlers
│   └── resources/
│       ├── static/          # Static files (sse-test.html)
│       └── schema.sql       # Database schema
└── test/
    └── java/com/evolve/springbootapp/
        ├── SseCartEventsTest.java
        ├── UserRouterTest.java
        ├── WebSocketHandlerTest.java
        └── GrpcChatServiceTest.java
```

## Technologies Used

- **Spring Boot 3.3.0** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Data R2DBC** - Reactive database access
- **H2 Database** - In-memory database
- **Redis** - Session storage and pub/sub
- **Embedded Redis** - For testing
- **WebSocket** - Real-time communication
- **gRPC** - High-performance RPC
- **RSocket** - Reactive messaging
- **Logback** - Logging framework
- **WebTestClient** - Reactive testing
- **Testcontainers** - Integration testing
