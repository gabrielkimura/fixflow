# CLAUDE.md

## Coding Principles

- **Think before coding.** State your assumptions out loud. If the request is ambiguous, ask. If a simpler approach exists, push back. Stop when you are confused, name what is unclear, do not just pick one interpretation and run.
- **Simplicity first.** Write the minimum code that solves the problem. No speculative abstractions. No flexibility nobody asked for. The test: would a senior engineer call this overcomplicated.
- **Surgical changes.** Touch only what the task requires. Do not improve neighboring code. Do not refactor what is not broken. Every changed line should trace back to the request.
- **Goal-driven execution.** Turn vague instructions into verifiable targets before writing a line. "Add validation" becomes "write tests for invalid inputs, then make them pass."
- **English only in code.** All code, variable names, function names, column names, comments, and docstrings must be in English. User-facing labels (UI text, seed data) may be in Portuguese.

## Code Quality Checklist (run mentally before committing)

- **No duplicate style/logic blocks.** If the same inline style object, conditional pattern, or data transform appears twice, extract it (helper function, shared constant, or component). Two is a signal; three is a bug.
- **One source of truth for derived data.** If value B is mathematically guaranteed to equal value A, do not compute B separately — use A. Redundant derivations mislead readers into thinking the values can differ.
- **Backend returns only what the frontend consumes.** Do not query or include fields in API responses that no client reads. Dead data is dead code with an extra DB round-trip.
- **Self-review before commit.** After finishing a feature, re-read the full diff as if reviewing someone else's PR. Look for: repeated blocks, unused variables, stale imports, response fields nobody reads.

---

## FixFlow Backend — Project Context

FixFlow is an event-driven backend platform for managing residential and commercial technical service tickets, built with **Hexagonal Architecture**, **Kotlin 17+**, **Spring Boot 3.x**, and **Gradle (Kotlin DSL)**.

---

## Architectural Constraints (Strict Hexagonal)

Code must be strictly divided into `core` (pure, isolated, no frameworks) and `infrastructure` (Spring, JPA, etc).

### Required Folder Structure
```text
src/main/kotlin/com/periclao/fixflow/
 ├── core/                  # Pure Domain (No Spring, JPA, Jackson, or Jakarta annotations)
 │    ├── model/            # Domain data classes, Enums, Pure business rules
 │    ├── usecase/          # Services/Use cases (Interfaces and Implementations)
 │    ├── repository/       # Output Ports (Persistence interfaces)
 │    ├── event/            # Output Ports (Messaging/event interfaces)
 │    └── exception/        # Pure business exceptions
 ├── infrastructure/        # OUTPUT Adapters (Spring Data JPA, RabbitMQ, etc.)
 │    ├── entity/           # JPA Entities (@Entity, @Table)
 │    ├── repository/       # Spring Data JPA + Port Implementations
 │    ├── mapper/           # Explicit Mappers (Core Model <-> Entity)
 │    ├── config/           # Spring Beans (@Configuration, manual UseCase instantiation)
 │    └── integration/      # Output Adapters (RabbitMQ/Kafka, Webhooks, SQS)
 └── api/                   # INPUT Adapters (HTTP — REST Controllers)
      ├── cliente/
      │    ├── request/     # Input DTOs with Jakarta validations
      │    ├── response/    # Output DTOs
      │    └── ClienteController.kt
      └── endereco/
           ├── request/
           ├── response/
           └── EnderecoController.kt
```

---

## Clean Code & Best Practices (Kotlin + Spring)

- **Meaningful names:** Functions should be descriptive verbs. Classes should be nouns. Avoid abbreviations.
- **Small functions:** Each function should do only one thing (SRP) with few lines.
- **Immutability by default:** Always use `val`. Use `var` only when strictly necessary.
- **Native null safety:** Use Kotlin nullable types (`?`) and operators like `?:` (Elvis). Never use `Optional<T>`.
- **Dependency injection:** Via classic Kotlin constructor. Use of `@Autowired` is strictly prohibited.
- **Clean error handling:** `core` throws specific business exceptions. `infrastructure` catches them globally via `@RestControllerAdvice`.
- **Validation at the boundary:** Validate HTTP payloads with Jakarta Validation annotations on `infrastructure` DTOs, never on `core` models.
- **Core instantiation:** Register UseCases manually in `infrastructure.config.UseCaseConfig` using `@Bean`.

---

## Strict Business Rules (Domain Invariants)

### Clients and Addresses
- **Soft delete:** Clients are never physically deleted from the database. There must be an `active: Boolean` or `deletedAt: LocalDateTime?` flag. Inactive clients cannot open new tickets.
- **Address binding:** An address belongs to exactly one client. A client can have multiple addresses.

### Ticket Lifecycle and State Transitions
Tickets follow a strict state machine based on the `TicketStatus` enum:
- **OPEN:** Initial state when created and categorized.
- **UNDER_REVIEW:** When support is evaluating the issue or searching for eligible technicians.
- **TECHNICIAN_ASSIGNED:** When an available technician accepts or receives the ticket.
- **IN_PROGRESS:** When the technician starts the on-site service.
- **COMPLETED:** Final success state. Requires a technical closing description.
- **CANCELLED:** Final interruption state. Can occur from `OPEN`, `UNDER_REVIEW`, or `TECHNICIAN_ASSIGNED`. Cannot be cancelled if `IN_PROGRESS` or `COMPLETED`.

### Automatic Categorization
- On creation, the system analyzes the ticket description using a simple keyword engine from Core (e.g.: "vazamento", "cano", "infiltração" -> Category: *HYDRAULIC*; "curto", "tomada", "fio" -> Category: *ELECTRICAL*).
- If no mapped keyword is found, the ticket must be flagged with `PENDING_CATEGORIZATION`.

### Webhook Mechanism
- HTTP notification dispatch to partners must be **asynchronous** (must not block the main user request).
- On network failure (non-2xx HTTP status or timeout), the system must schedule a **Retry with Exponential Backoff** (e.g.: retry in 5m, 15m, 30m) up to 5 attempts. On definitive failure, log the failure in the audit entity.

---

## Development Strategy (Task-Based)

Develop in focused micro-tasks to save tokens and avoid hallucinations.
- **Inside-Out approach:** Always implement `core` first, then `infrastructure`.
- **Task flow:** Suggest code for the current task, ask for approval, and after user feedback, **update this file changing `[ ]` to `[x]`** before moving to the next.

---

## Claude Response Rules (System Prompts)

1. **Focus and slicing:** Never try to implement the entire feature at once. Look at the Backlog below, execute the first pending `[ ]` task, and ask for authorization to proceed.
2. **Extreme token economy:** Do not rewrite entire files for small changes. Show only the modified section using `// ...` comments to omit unchanged code.
3. **No framework pollution in Core:** If you suggest a framework annotation (Spring, JPA, Jackson) inside the `core` package, the response is wrong.
4. **Respect business rules:** Always validate that state transitions or deletions strictly respect the **Strict Business Rules** section above.
5. **Be direct:** Go straight to the point and code. No long introductions or conclusions.

---

## Development Backlog (Progress Checklist)

### Phase 0: Initial Setup
- [x] Task 0.1: `build.gradle.kts` configuration (Kotlin, Spring Boot, JPA, Postgres, Tests dependencies).
- [x] Task 0.2: `Dockerfile`, `docker-compose.yml` (Postgres + Message Broker), and `application.yml`.

### Phase 1: Client and Address Registration
- [x] Task 1.1: Core — Models (`Cliente`, `Endereco`), Enums, and business exceptions (with soft delete support).
- [x] Task 1.2: Core — Persistence Ports (`ClienteRepositoryPort`, `EnderecoRepositoryPort`).
- [x] Task 1.3: Core — UseCases (Create, Update, Query, Soft Delete) + Pure Unit Tests.
- [x] Task 1.4: Infra — JPA Entities, Mappers, and Spring Data Repositories.
- [x] Task 1.5: Infra — Input/Output DTOs, Jakarta Validations, and REST Controllers.
- [x] Task 1.6: Infra — Bean Configuration (`UseCaseConfig`) and Global Exception Handling.

### Phase 2: Technician Management
- [x] Task 2.1: Core — Model (`Tecnico`) and Persistence Port.
- [x] Task 2.2: Core — UseCases (Register, Query, List Technician Tickets) + Unit Tests.
- [x] Task 2.3: Infra — JPA Mapping, Repositories, DTOs, and REST Controller.

### Phase 3: Ticket Management (Core Business)
- [ ] Task 3.1: Core — Model (`Chamado`), Status Enum with internal transition validation, and Persistence Ports.
- [ ] Task 3.2: Core — Automatic Categorization engine (keyword-based) and `PENDING_CATEGORIZATION` handling.
- [ ] Task 3.3: Core — UseCases (Open, Assign Technician, Change Status, Cancel, Complete) + State transition Unit Tests.
- [ ] Task 3.4: Infra — Database modeling, Mappers, DTOs, and REST Controllers for the Ticket flow.

### Phase 4: Events and Messaging (Event-Driven Architecture)
- [ ] Task 4.1: Core — Domain Event models and Messaging Output Ports (`EventPublisherPort`).
- [ ] Task 4.2: Core — Update Ticket UseCases to fire events on state changes.
- [ ] Task 4.3: Infra — Broker configuration (RabbitMQ/Kafka) and Publication Port implementation.

### Phase 5: Webhook System
- [ ] Task 5.1: Core — Model (`WebhookSubscription`) and UseCase for partner subscription management.
- [ ] Task 5.2: Infra — Async HTTP dispatch engine with Exponential Retry mechanism and failure logging.

### Phase 6: Observability and Differentiators (Polish)
- [ ] Task 6.1: Structured Logging and Request Tracing (MDC/Trace ID) configuration.
- [ ] Task 6.2: Idempotency for event consumption and Rate Limiting on REST APIs.

---

## Useful Commands
- Compile: `./gradlew compileKotlin`
- Run tests: `./gradlew test`
- Run locally: `./gradlew bootRun`