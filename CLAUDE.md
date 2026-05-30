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
 │    │                     #   Also: AuthenticatedUser, Role, pagination types
 │    │                     #   (Page, PageRequest, SortOrder), *SearchCriteria,
 │    │                     #   ChannelType, ClientChannel
 │    ├── usecase/          # Services/Use cases (Interfaces and Implementations)
 │    │                     #   Includes search/query use cases (paginated + filtered)
 │    ├── repository/       # Output Ports (Persistence interfaces, criteria-aware)
 │    ├── event/            # Output Ports (Messaging/event interfaces)
 │    └── exception/        # Pure business exceptions (e.g. AccessDeniedDomainException)
 ├── infrastructure/        # OUTPUT Adapters (Spring Data JPA, RabbitMQ, etc.)
 │    ├── entity/           # JPA Entities (@Entity, @Table)
 │    ├── repository/       # Spring Data JPA + Port Implementations (Specifications)
 │    ├── mapper/           # Explicit Mappers (Core Model <-> Entity)
 │    ├── security/         # Spring Security / JWT config; principal -> AuthenticatedUser
 │    ├── config/           # Spring Beans (@Configuration, manual UseCase instantiation)
 │    └── integration/      # OUTPUT Adapters (RabbitMQ/Kafka, Webhooks, SQS,
 │                          #   outbound bot messaging)
 └── api/                   # INPUT Adapters (HTTP — REST Controllers, bot webhooks)
      ├── cliente/
      │    ├── request/     # Input DTOs with Jakarta validations
      │    ├── response/    # Output DTOs
      │    └── ClienteController.kt
      ├── endereco/
      │    ├── request/
      │    ├── response/
      │    └── EnderecoController.kt
      └── bot/              # INPUT Adapter: inbound WhatsApp/Telegram webhooks
                            #   resolve AuthenticatedUser, then call core use cases
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

## API Design & Resource Access (User / Frontend-Oriented)

The goal of this section is a frontend that never forces a human to know an internal ID. IDs stay; we add discovery and identity around them.

- **Opaque UUIDs remain canonical in URLs.** The frontend obtains IDs from search/list endpoints; a human never types or memorizes them. Never use PII (CPF/CNPJ) as a URL path identifier — it leaks personal data into logs/history and breaks if the document changes.
- **Identity from the security context ("me" endpoints).** The authenticated client/technician never passes their own ID; it is derived from the token:
    - `GET /clientes/me`, `GET /clientes/me/enderecos`, `GET /clientes/me/chamados`
    - `GET /tecnicos/me`, `GET /tecnicos/me/chamados`
- **Search-first.** Every aggregate exposes a filtered, paginated list endpoint so resources are discoverable by human attributes (this is how an ADMIN obtains a target ID — search, then drill in by ID):
    - Cliente: `nome` (partial), `documento` (exact CPF/CNPJ), `email`, `ativo`, `cidade`, `uf`
    - Tecnico: `nome`, `categoria`, `disponivel`, `documento`
    - Chamado: `status`, `categoria`, `clienteId`, `tecnicoId`, `protocolo`, `criadoDe`, `criadoAte`
    - All list endpoints accept `page`, `size`, `sort`.
- **Human-facing ticket identifier.** Each `Chamado` has a readable `protocolo` (e.g. `FF-2026-000123`) for support/UX, generated on creation and unique. It is a business identifier for lookup/communication — distinct from the internal UUID, which stays the canonical key.

---

## Authentication & Authorization

- **Authentication at the boundary only.** Spring Security / JWT lives in `infrastructure.security` and `api`. **Core NEVER imports Spring Security** (or any framework).
- **Pure principal into use cases.** Controllers extract the authenticated principal and pass a pure `AuthenticatedUser` (`core.model`) into the use cases: `{ userId, role, clienteId?, tecnicoId? }`. No framework types cross into `core`.
- **Roles:** `ADMIN`, `CLIENT`, `TECHNICIAN`.
- **Two-layer authorization:**
    - Coarse role guard at the boundary — can this role hit this endpoint at all.
    - **Ownership / scope invariants enforced in `core` use cases** (this is a domain invariant, not an HTTP concern):
        - `CLIENT`: queries are force-scoped to their own `clienteId`; cannot read others' data, even if they pass another ID.
        - `TECHNICIAN`: scoped to assigned tickets.
        - `ADMIN`: full filters allowed.
- Ownership violations throw `AccessDeniedDomainException` from `core`, mapped to HTTP 403 by the `@RestControllerAdvice`.

---

## Pagination & Filtering Abstractions (Core, framework-free)

- **Do NOT leak Spring Data `Pageable`/`Page` into `core`.** Define pure types in `core.model`:
    - `PageRequest(page: Int, size: Int, sort: List<SortOrder>)`
    - `Page<T>(content: List<T>, totalElements: Long, page: Int, size: Int)`
    - One `XxxSearchCriteria` value object per searchable aggregate (`ClienteSearchCriteria`, `TecnicoSearchCriteria`, `ChamadoSearchCriteria`).
- Repository output ports accept `criteria` + `PageRequest` and return `Page<DomainModel>`.
- Infrastructure adapters translate core criteria/`PageRequest` to **Spring Data JPA Specifications** (default; QueryDSL acceptable if criteria grow complex) and map entities back to domain models.

---

## Multi-Channel Identity (Bots: WhatsApp / Telegram)

- **Identification ≠ authentication.** CPF/CNPJ identifies a client but does NOT prove identity (widely known/leaked in Brazil → LGPD risk). CPF is only a lookup key during channel linking — never a credential, and never sufficient on its own to return ticket data.
- **A bot is just another INPUT adapter.** It resolves an `AuthenticatedUser` from the channel identity and calls the SAME core use cases as the REST API (e.g. `BuscarChamadosUseCase`). Core never knows the request came from WhatsApp/Telegram.
- **Channel binding (recommended pattern).** Persist a `ClientChannel` link (`clienteId`, `channelType`, `externalId`, `verifiedAt`). After linking, messages from that chat are authenticated automatically — no CPF prompts on every interaction.
    - WhatsApp: the platform-verified sender phone is a possession factor; match it to the client's phone on file.
    - Telegram / unknown phone: OTP flow — send a one-time code to the contact ON FILE (email/SMS) or a deep link from the logged-in app; bind on success.
- **Read-only fallback (no link):** `protocolo` + a second factor (last 4 digits of phone, or CEP) may reveal ONLY minimal status, never full PII.
- **Adapter placement:** inbound bot webhooks are INPUT adapters (live under `api/bot`); outbound message sending is an OUTPUT adapter in `infrastructure/integration`. Bot SDKs/tokens never touch `core`.

---

## Strict Business Rules (Domain Invariants)

### Clients and Addresses
- **Soft delete:** Clients are never physically deleted from the database. There must be an `active: Boolean` or `deletedAt: LocalDateTime?` flag. Inactive clients cannot open new tickets.
- **Address binding:** An address belongs to exactly one client. A client can have multiple addresses.

### Visibility (see Authentication & Authorization)
- Resource visibility per role is a domain invariant enforced in `core` use cases. CLIENT/TECHNICIAN queries are force-scoped; ADMIN is unrestricted. This is the single source of truth for access scoping — do not re-implement it in controllers.

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
3. **No framework pollution in Core:** If you suggest a framework annotation (Spring, JPA, Jackson, Jakarta, Spring Security) or a framework type (`Pageable`, `Page`, `Authentication`) inside the `core` package, the response is wrong.
4. **Respect business rules:** Always validate that state transitions, deletions, or access scoping strictly respect the **Strict Business Rules** and **Authentication & Authorization** sections above.
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
- [x] Task 3.1: Core — Model (`Chamado`), Status Enum with internal transition validation, and Persistence Ports.
- [x] Task 3.2: Core — Automatic Categorization engine (keyword-based) and `PENDING_CATEGORIZATION` handling.
- [x] Task 3.3: Core — UseCases (Open, Assign Technician, Change Status, Cancel, Complete) + State transition Unit Tests.
- [x] Task 3.4: Infra — Database modeling, Mappers, DTOs, and REST Controllers for the Ticket flow.

### Phase 4: Search & Discovery (User/Frontend-Oriented)
- [ ] Task 4.1: Core — Pagination abstractions (`Page<T>`, `PageRequest`, `SortOrder`) — framework-free.
- [ ] Task 4.2: Core — `ClienteSearchCriteria` + `BuscarClientesUseCase` (filtered, paginated) + Unit Tests.
- [ ] Task 4.3: Core — `TecnicoSearchCriteria` + search use case + Unit Tests.
- [ ] Task 4.4: Core — `ChamadoSearchCriteria` + search use case (with ownership scoping) + Unit Tests.
- [ ] Task 4.5: Core — `Chamado` `protocolo` (human-readable identifier) generation rule + Unit Tests.
- [ ] Task 4.6: Infra — Extend repository ports/adapters with criteria queries (Spring Data Specifications) + pagination; list/search REST controllers and response DTOs.

### Phase 5: Authentication & Authorization
- [ ] Task 5.1: Core — `AuthenticatedUser` model, `Role` enum, `AccessDeniedDomainException`; thread the principal through query/command use cases; ownership-scoping invariants + Unit Tests.
- [ ] Task 5.2: Infra — Spring Security (JWT) config in `infrastructure.security`; extract principal -> `AuthenticatedUser`; coarse role guards; map `AccessDeniedDomainException` -> HTTP 403.
- [ ] Task 5.3: Infra — `/clientes/me` and `/tecnicos/me` endpoints (identity from token, no ID in path).

### Phase 6: Events and Messaging (Event-Driven Architecture)
- [ ] Task 6.1: Core — Domain Event models and Messaging Output Ports (`EventPublisherPort`).
- [ ] Task 6.2: Core — Update Ticket UseCases to fire events on state changes.
- [ ] Task 6.3: Infra — Broker configuration (RabbitMQ/Kafka) and Publication Port implementation.

### Phase 7: Webhook System
- [ ] Task 7.1: Core — Model (`WebhookSubscription`) and UseCase for partner subscription management.
- [ ] Task 7.2: Infra — Async HTTP dispatch engine with Exponential Retry mechanism and failure logging.

### Phase 8: Observability and Differentiators (Polish)
- [ ] Task 8.1: Structured Logging and Request Tracing (MDC/Trace ID) configuration.
- [ ] Task 8.2: Idempotency for event consumption and Rate Limiting on REST APIs.

### Phase 9: Multi-Channel Bot Identity (Future)
- [ ] Task 9.1: Core — `ChannelType`, `ClientChannel` model, `ClientChannelRepositoryPort`, `ChannelVerificationPort` (OTP issue/validate).
- [ ] Task 9.2: Core — `ResolveClientByChannelUseCase`, `LinkClientChannelUseCase`, `VerifyClientChannelUseCase` + Unit Tests. Reuse `BuscarChamadosUseCase` scoped by the resolved `AuthenticatedUser`.
- [ ] Task 9.3: Infra — Inbound webhook adapter (WhatsApp/Telegram) under `api/bot` -> resolves principal -> core; `OutboundMessagePort` implementation; OTP store with TTL (reuse Redis from Phase 8 if present, else DB row with expiry).

---

## Useful Commands
- Compile: `./gradlew compileKotlin`
- Run tests: `./gradlew test`
- Run locally: `./gradlew bootRun`