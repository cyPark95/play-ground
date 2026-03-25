---
name: spring-api-rules
description: Define controllers, services, repositories, entities, DTOs for Spring Boot REST API. Use when user mentions API, endpoint, controller, service, repository, entity, DTO, CRUD, domain, feature, function, or REST creation.
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, LSP
---

# Spring API Development Rules

Standard rules for Spring Boot REST API development in this project.

## Package Structure

```
pcy.study.sns
├── api/                         # REST API controllers and DTOs
│   ├── {resource}/              # Resource-specific DTOs
│   │   ├── {Domain}Request.java
│   │   └── {Domain}Response.java
│   ├── exception/
│   │   └── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── domain/                      # Domain packages
│   ├── base/                    # Shared domain base
│   │   ├── BaseEntity.java
│   │   ├── ErrorCode.java
│   │   └── DomainException.java
│   └── {domainName}/            # Per-domain package
│       ├── {Domain}.java
│       ├── {Domain}Repository.java
│       └── {Domain}Service.java
└── config/                      # Configuration classes
```

## Common Rules

- Constructor injection using `@RequiredArgsConstructor` where possible
- No field injection (`@Autowired` on fields)
- `@ConfigurationProperties` classes should be written as records
- Use Lombok `@Getter` actively for entities and classes (except DTOs which use records)

## Controller

- Use `@RestController`
- Do not use `@RequestMapping` at class level; write full endpoint path on each method
- Return type: `ResponseEntity<T>`
    - POST (create): `ResponseEntity.status(HttpStatus.CREATED).body(...)`
    - DELETE: `ResponseEntity.noContent().build()`
    - GET, PUT: `ResponseEntity.ok(...)`
- Naming: `*Controller`

## DTO

- Place in `api/{resource}/` package
- Use Java `record`
- Request DTO: `toEntity()` method (only if needed)
- Response DTO: `from(Entity)` static factory (only if needed)
- No business logic in DTOs; only data conversion methods allowed

## Domain

Each domain is organized under `domain/{domainName}/` package:

- `{Domain}.java` - Entity
- `{Domain}Repository.java` - Data access
- `{Domain}Service.java` - Business logic
- `{Domain}Exception.java` - Domain exception

### Entity

- `protected` default constructor
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Associations: `FetchType.LAZY` by default
- No FK constraints in database; use `@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))`
- Extend `BaseEntity` for `createdAt`, `modifiedAt`, `deletedAt` and soft-delete support

### Repository

- Extends `JpaRepository<Entity, ID>`
- Follow Spring Data JPA query method naming conventions

### Service

- Use `@Transactional` only when necessary:
    - Use when multiple write operations must be in a single transaction
    - Use when Dirty Checking is needed (entity modification without explicit save)
    - Do NOT use for single repository operations (they handle transactions automatically)
    - Do NOT use for simple read operations

## Exception Handling

### Structure

All error codes are defined in a single enum `domain/base/ErrorCode.java`.
Each domain has its own exception class directly in the domain package (no sub-package).

```
domain/base/ErrorCode.java       ← single enum for all error codes
domain/base/DomainException.java ← base exception class (throw directly in services)
api/exception/ErrorResponse.java ← API error response record
api/GlobalExceptionHandler.java  ← @RestControllerAdvice
```

### ErrorCode

```java

@RequiredArgsConstructor
public enum ErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_001", "Post not found"),
    POST_UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "POST_002", "..."),
    ...;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
```

- Code format: `{DOMAIN}_{sequence}` (e.g., `POST_001`, `USER_001`)
- HTTP status:
    - Not found → `404 NOT_FOUND`
    - Already exists / duplicate → `409 CONFLICT`
    - Unauthorized access → `403 FORBIDDEN`
    - Business rule violation → `400 BAD_REQUEST`

### DomainException

```java
public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
```

### Usage in Services

- Throw `DomainException` directly with an `ErrorCode`; never use `IllegalArgumentException` or
  `IllegalStateException`
- Domain context is encoded in the `ErrorCode` prefix (e.g., `POST_001`, `FOLLOW_001`) — no per-domain exception subclass needed

```java
throw new DomainException(ErrorCode.POST_NOT_FOUND);
```

### GlobalExceptionHandler

- `DomainException` → use `errorCode.getHttpStatus()` and return `ErrorResponse`
- `MethodArgumentNotValidException` → `400` with field validation message
- `Exception` → `500` with generic message

## API Shell Script

When creating a new API, create a shell script in `src/main/resources/http/`:

- File naming: lowercase with resource name (e.g., `post.sh`, `follow.sh`)
- Include curl commands for all endpoints (POST, GET, PUT, DELETE)
- Use `BASE_URL="http://localhost:8080"` variable
- Use `-b cookies.txt` for authenticated requests
- Add descriptive echo statements before each curl command
- Start with `#!/bin/bash` shebang
