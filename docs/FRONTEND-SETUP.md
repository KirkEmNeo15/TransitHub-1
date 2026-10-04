# Frontend setup (React + TypeScript + Vite)

Needs Node.js 22 LTS (or at least 20.19). Check with `node -v`.

## First time (one developer creates the project, everyone else just runs npm install)
```bash
# from the project root
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
npm install tailwindcss @tailwindcss/vite react-router axios leaflet react-leaflet
npm install -D @types/leaflet
```
Then add the Tailwind plugin to `frontend/vite.config.ts`:
```ts
import tailwindcss from '@tailwindcss/vite'
// ...
plugins: [react(), tailwindcss()],
```

## Every other developer
```bash
cd frontend
npm install
```

## Run
```bash
cd frontend
npm run dev      # http://localhost:5173
npm run build    # type-checks and builds
npm run lint
```
The backend must be running on http://localhost:8080. To use another address, copy
`frontend/.env.example` to `frontend/.env` and change `VITE_API_BASE_URL`.

## Folder structure (`frontend/src`)
| Folder | What goes there |
|---|---|
| `components/` | Reusable pieces: Navbar, MapView, RouteCard... |
| `pages/` and `pages/admin/` | One file per screen |
| `services/` | Axios calls to the backend (`api.ts` is the shared client) |
| `context/` | Shared state, for example the logged-in user |
| `hooks/` | Reusable React hooks |
| `types/` | TypeScript interfaces for the API data |
| `utils/` | Small helper functions and constants |

## Pages and who can open them
| URL | Page | Who |
|---|---|---|
| `/` | Home | everyone |
| `/map` | Map | everyone |
| `/routes`, `/routes/:id` | Route list and details | everyone |
| `/alerts` | Alerts | everyone |
| `/login`, `/register` | Log in / sign up | everyone |
| `/favorites`, `/profile` | My favorites, my profile | logged-in users |
| `/admin` and `/admin/routes`, `/stops`, `/transportation`, `/alerts`, `/users` | Admin area | ADMIN only |
| `/setup-check` | Developer diagnostics (Tailwind, router, backend, map) | everyone |

The frontend hides pages the user may not open, but that is only for convenience.
The backend checks the login and the role again on every API call.

## How login works in the frontend
1. `Login.tsx` calls `AuthContext.login(...)`, which calls `POST /api/auth/login`.
2. The token is saved in `localStorage` (`utils/tokenStorage.ts`) and the user is kept in React state.
3. `services/api.ts` adds `Authorization: Bearer <token>` to every request.
4. After a page refresh, `AuthProvider` asks `GET /api/auth/me` to restore the user.
5. If the backend answers 401 (expired token), the token is removed and the user is logged out.

## How the frontend gets data (Phase 13)
```
Page  ->  useApiData(fetcher)  ->  service function  ->  api.ts (Axios)  ->  backend
```
| Piece | Job |
|---|---|
| `types/` | TypeScript shapes of every API answer (`Route`, `Stop`, `Alert`, ...), copied from `docs/API.md` |
| `services/` | One small function per endpoint, for example `getRoutes()` or `addFavorite(id)` |
| `hooks/useApiData.ts` | Calls a service and returns `loading`, `success` (with the data) or `error` (with a message) |
| `components/` | `RouteCard`, `RouteDetails`, `AlertCard`, `StatusBadge`, `TypeBadge`, `ErrorMessage`, `ReportForm` |
| `utils/format.ts` | Pesos, times (`5:00 AM`), durations, distances |

Every page shows a spinner while loading, a red message with "Try again" when the backend fails,
and an empty-state message when there is nothing to show.

Note: "Popular Routes" on the home page is simply the first three active routes by name. The system
has no usage statistics yet, so a real popularity ranking is a future improvement.
