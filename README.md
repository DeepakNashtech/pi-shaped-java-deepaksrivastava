### Day 1

### 1. What are the differences between Mono and Flux, and where did you use each?
- **Mono** represents a stream that emits zero or one item (e.g., a single user or product).  
- **Flux** represents a stream that emits zero to many items (e.g., a list of products or cart items).  
- **Usage in this project:**  
  - **Mono** is used for single-entity operations (e.g., fetching a user by ID, creating a product).
  - **Flux** is used for collections (e.g., listing all products, streaming cart updates via SSE).
- [More details & examples](https://www.geeksforgeeks.org/difference-between-mono-and-flux-in-spring-webflux/)

---

### 2. How does R2DBC differ from traditional JDBC?
- **R2DBC** is a reactive, non-blocking API for SQL databases, designed for use with reactive frameworks like WebFlux.
- **JDBC** is blocking and synchronous, using a thread-per-connection model.
- **R2DBC** enables efficient resource usage and scalability in reactive applications, while JDBC can become a bottleneck under high concurrency.

---

### 3. How does Spring WebFlux routing differ from @RestController?
- **WebFlux functional routing** uses `RouterFunction` and `HandlerFunction` to define routes and handlers in a functional style (Java lambdas), rather than annotation-based controllers.
- **@RestController** is annotation-based and imperative, typical in Spring MVC.
- **WebFlux routing** is more flexible for composing and testing routes, and fits naturally with reactive programming.

---

### 4. What are the advantages of using @Transactional with R2DBC (or why not)?
- **@Transactional** with R2DBC provides declarative transaction management for reactive database operations.
- **Caveat:** Not all R2DBC drivers support all transaction features, and transaction boundaries must be managed reactively.
- **Advantage:** Ensures atomicity and consistency for multi-step operations, but must be used carefully to avoid blocking.

---

### 5. Explain the difference between optimistic vs pessimistic locking, and where would you use either?
- **Optimistic locking**: Assumes low conflict; checks for data changes before committing (e.g., using a version field). Use when conflicts are rare.
- **Pessimistic locking**: Locks data for the duration of a transaction to prevent conflicts. Use when conflicts are likely or data integrity is critical.

---

### 6. Describe how session management works using Redis in reactive apps.
- **Spring Session with Redis** stores session data in Redis, allowing stateless app instances to share session state.
- In reactive apps, session data is accessed and updated non-blockingly, ensuring scalability and performance.
- Used here to persist cart state across requests and instances.

---

### 7. How did you expose and verify SSE updates?
- **SSE (Server-Sent Events)** endpoint streams real-time cart updates to the browser.
- Implemented using `Flux` in a handler, emitting updates when the cart changes in Redis.
- Verified using integration tests with `WebTestClient` to assert real-time data delivery.

---

### 8. What is the role of Swagger/OpenAPI in CI/CD and API consumer tooling?
- **Swagger/OpenAPI** provides machine-readable API documentation.
- Enables automated API testing, client code generation, and contract verification in CI/CD pipelines.
- Improves collaboration and integration for API consumers.

---

### 9. How does WebTestClient differ from MockMvc in testing?
- **WebTestClient** is designed for testing reactive WebFlux applications, supporting non-blocking, asynchronous flows.
- **MockMvc** is for traditional, blocking Spring MVC apps.
- WebTestClient can test both server and client sides of reactive endpoints.

---

### 10. Compare WebSocket, SSE, and RSocket for real-time data needs.
- **WebSocket**: Full-duplex, bidirectional communication; suitable for chat, gaming, etc.
- **SSE**: One-way server-to-client streaming over HTTP; simple, good for real-time updates like notifications.
- **RSocket**: Binary, multiplexed, supports request-response, streaming, and bi-directional communication; advanced use cases.

