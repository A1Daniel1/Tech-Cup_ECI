# TechCup Fútbol — Backend

Spring Boot 3.5 / Java 17 modular monolith. Architecture, data model and REST contract:
[`../docs/ARCHITECTURE.md`](../docs/ARCHITECTURE.md).

## Run locally

Requirements: JDK 17+, Docker.

```bash
# from the repository root: PostgreSQL 16 (host 5433) + MongoDB 7 (host 27018)
docker compose up -d

cd backend
./mvnw spring-boot:run          # http://localhost:8080
```

Flyway creates the schema (`V1__schema.sql`) and seeds the administrator (`V2__seed_admin.sql`)
on first start.

- Swagger UI: http://localhost:8080/swagger-ui.html (click *Authorize* and paste the JWT)
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

### Default administrator (development only)

| e-mail | password |
|---|---|
| `admin@escuelaing.edu.co` | `Admin123*` |

Change it in any real deployment (update `password_hash` in `users` with a new BCrypt hash).

## Configuration

Every setting in `src/main/resources/application.yml` has an environment-variable override:

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://127.0.0.1:5433/techcup` | PostgreSQL JDBC URL |
| `DB_USER` | `techcup` | PostgreSQL user |
| `DB_PASSWORD` | `techcup` | PostgreSQL password |
| `MONGO_URI` | `mongodb://127.0.0.1:27018/techcup` | MongoDB (GridFS) connection |
| `JWT_SECRET` | dev value (change it) | HS256 signing key, at least 32 bytes |
| `JWT_EXPIRATION_MINUTES` | `480` | Token lifetime |
| `CORS_ORIGINS` | `http://localhost:5173` | Comma-separated allowed origins |
| `APP_INSTITUTIONAL_DOMAINS` | `escuelaing.edu.co,mail.escuelaing.edu.co` | Domains that count as institutional e-mail |
| `PORT` | `8080` | HTTP port |

## Build and test

```bash
./mvnw -q -DskipTests compile   # compile
./mvnw -q test                  # unit tests (JUnit 5 + Mockito, no database needed)
./mvnw package                  # executable jar in target/
```

## Project layout

```
edu.escuelaing.techcup
  shared/        config, security (JWT), exception handling, audit, file storage (GridFS), home
  identity/      register, login, roles, referees, inactivation
  players/       user updates, sport profiles, free-agent search, join requests
  teams/         teams, members, eligibility
  tournaments/   (phase 2) tournaments, venues, rulebook, registrations
  competition/   (phase 2) matches, lineups, results, standings, bracket, stats
```

Each domain module has four layers: `api` (controllers + DTO records), `application`
(use cases, business rules, audit), `domain` (JPA entities, enums, pure rules) and
`infrastructure` (Spring Data repositories, adapters). Modules talk to each other only through
application services or ports (e.g. `teams.application.TeamLockPort`,
`players.application.TeamGateway`).

## Quick smoke test with curl

```bash
# admin login
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"admin@escuelaing.edu.co","password":"Admin123*"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -s localhost:8080/api/auth/me -H "Authorization: Bearer $TOKEN"
```
