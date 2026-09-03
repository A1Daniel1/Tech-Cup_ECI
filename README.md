# TechCup Fútbol

Web platform to manage the semester football tournament of the Systems, AI, Cybersecurity and
Statistics engineering programs at Escuela Colombiana de Ingeniería Julio Garavito (DOSW final project).

It replaces WhatsApp groups, Google Forms and spreadsheets with one system where players register,
captains build teams, organizers run the tournament (registrations, payments review, fixtures,
results) and everyone can see standings, brackets and statistics.

## Architecture in one paragraph

A **modular monolith**: one Spring Boot application split into the five functional domains of the
specification (`identity`, `players`, `teams`, `tournaments`, `competition`), each with its own
layers (controllers, application services, domain, data adapters). The "orchestrator" of the spec is
the monolith's JWT filter, global error handler and home endpoint. PostgreSQL stores relational data;
MongoDB (GridFS) stores binaries only (profile photos, venue images, payment receipts, rulebook PDF).
The frontend is a React + TypeScript SPA. Full details, data model and REST contract:
[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md). Original requirements: [docs/spec-extracted.md](docs/spec-extracted.md).

## Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, Flyway, Maven |
| Databases | PostgreSQL 16 (data), MongoDB 7 GridFS (files) |
| Frontend | React 18, TypeScript, Vite, Tailwind CSS v4, Zustand, React Router, pnpm |
| Local infra | Docker Compose |

## Run locally

Start the databases. PostgreSQL is published on host port **5433** and MongoDB on **27018**,
on purpose: many machines already run those services on their default ports, and the local
one would silently win the connection.

```bash
docker compose up -d
```

```bash
cd backend && ./mvnw spring-boot:run
```

```bash
cd frontend && pnpm install && pnpm dev
```

- Web: http://localhost:5173
- API: http://localhost:8080, Swagger UI at http://localhost:8080/swagger-ui.html
- Default admin (development only): `admin@escuelaing.edu.co` / `Admin123*`

### Demo data

With the databases and the backend running, this loads a complete tournament (4 teams of 7
players, referees, venues, rulebook, approved registrations, fixtures and results) so the
application can be explored without filling in every form. It prints the accounts it created.

```bash
./scripts/seed-demo.sh
```

## Tests

```bash
cd backend && ./mvnw test
```

```bash
cd frontend && pnpm test
```

## Repository layout

```
backend/    Spring Boot monolith
frontend/   React SPA
scripts/    demo data seeding
docs/       architecture and requirements
```
