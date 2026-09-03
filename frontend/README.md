# TechCup Fútbol — Frontend

React 18 + TypeScript + Vite single-page application for the TechCup Fútbol tournament platform.
The REST contract it consumes is defined in `../docs/ARCHITECTURE.md` (section 3.5).

## Stack

- Vite 8, React 18, TypeScript (strict)
- Tailwind CSS v4 (`@tailwindcss/vite`)
- React Router 7 (`react-router`)
- Zustand 5 (session + UI state, persisted session in `localStorage`)
- Vitest + Testing Library (jsdom)
- oxlint (linter shipped by the Vite template)

## Requirements

- Node 22+
- pnpm 10+
- Backend running on `http://localhost:8080` (see `../backend`) for real data

## Run

```bash
pnpm install
pnpm dev          # http://localhost:5173, proxies /api -> http://localhost:8080
```

Copy `.env.example` to `.env` to override defaults:

| Variable                | Purpose                                                                 |
| ----------------------- | ----------------------------------------------------------------------- |
| `VITE_API_URL`          | API base URL used by the browser. Empty = relative `/api` (dev proxy). |
| `VITE_API_PROXY_TARGET` | Backend origin the Vite dev proxy forwards `/api` to.                   |

## Quality gates

```bash
pnpm lint         # oxlint
pnpm build        # tsc -b && vite build  -> dist/
pnpm test         # vitest run
pnpm preview      # serve the production build locally
```

## Project structure

```
src/
  app/          router, guards (RequireAuth / RequireRole), AppLayout, providers, App
  lib/          api client (fetch + bearer + ApiError), files (useFileUrl), formatters, labels
  store/        auth.store.ts (session), ui.store.ts (toasts)
  types/        api.ts — TypeScript mirror of every DTO / enum in the contract
  components/   atomic design: atoms / molecules / organisms / templates (presentational only)
  features/     one folder per backend module: api.ts, hooks/, components/, pages/
    auth/       LoginPage, RegisterPage
    home/       HomePage
    players/    ProfilePage, FreeAgentsPage, MyJoinRequestsPage
    teams/      TeamsPage, TeamDetailPage, MyTeamPage
    admin/      UsersAdminPage, AuditPage
```

Conventions:

- Presentational components receive data through props only; pages own data fetching via feature hooks
  (`useQuery` / `useMutation` in `src/lib/useQuery.ts`).
- UI copy is neutral Spanish; code, identifiers and comments are English.
- Enum values from the API are translated through `src/lib/labels.ts`.
- Binary files (`GET /api/files/{id}`) are fetched with the bearer token via `useFileUrl`.

## Routes

| Path               | Access                 |
| ------------------ | ---------------------- |
| `/login`           | public                 |
| `/register`        | public                 |
| `/`                | authenticated          |
| `/profile`         | authenticated          |
| `/players`         | CAPTAIN, ORGANIZER     |
| `/my-requests`     | PLAYER                 |
| `/teams`           | authenticated          |
| `/teams/:id`       | authenticated          |
| `/my-team`         | CAPTAIN                |
| `/tournaments`     | authenticated (phase 2)|
| `/referee/matches` | REFEREE (phase 2)      |
| `/admin/users`     | ADMIN, ORGANIZER       |
| `/admin/audit`     | ADMIN                  |

`ADMIN` implies every role.
