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

## The map page (Phase 14)
`/map` shows every route stored in the database as a line, and every stop as a small circle.

| You do this | What happens |
|---|---|
| Click a route line (or a route in the left list) | The line gets thicker, the map zooms to it, its stops are numbered, and a card shows name, type, code, start, destination, stops, fare, operating hours, travel time and status |
| Click a stop | A popup shows its name, location and the routes passing through it |
| Tick or untick Bus / Jeepney / Van, Active, Inactive | Routes are shown or hidden. Inactive and suspended routes are dashed |
| Type in the search bar | Only routes matching the name, code, place, operator or a stop name stay on the map |
| "Set origin (A)" / "Set destination (B)", then click the map | Pins A and B appear. The nearest stop to each is found, and "Find routes between these stops" lists the direct routes, fastest first |

Notes
- Route lines are drawn from the points stored in the database (`route_points`). The demo routes are straight
  lines between stops, because real road geometry is not stored. This is **not** live GPS tracking.
- Map tiles come from OpenStreetMap. Their usage policy allows light use such as a school project.
- The pins use plain HTML icons, so Leaflet's default marker image files are not needed.
- `/map?route=3` opens the map with route 3 selected (the "View on map" button on a route page uses this).

## Files
| File | Job |
|---|---|
| `pages/Map.tsx` | Holds the state (filters, selected route, chosen points) and arranges the page |
| `components/MapView.tsx` | The Leaflet map: tiles, lines, stops, pins, zoom-to-route |
| `components/StopMarker.tsx` | One stop and its popup |
| `components/MapFilters.tsx` | The filter checkboxes |
| `components/RouteInfoCard.tsx` | The selected-route card |
| `components/MapPickPanel.tsx` | Choosing A and B, nearest stops, route results |
| `hooks/useNearestStop.ts` | Finds the closest stop to a clicked point (uses `/api/stops/nearby`) |
| `utils/mapHelpers.ts` | Leaflet helpers: positions, pin icons, text matching |

## The map (Phase 14)
Page `/map` (`pages/Map.tsx`) loads every route and stop once, then filters them in the browser.

| Piece | Job |
|---|---|
| `components/MapView.tsx` | The Leaflet map: OpenStreetMap tiles, one colored line per route, stop markers, origin (A) and destination (B) markers, zoom to the selected route |
| `components/StopMarker.tsx` | A stop. Its popup shows the name, location and the routes passing through it |
| `components/RouteSummary.tsx` | Route name, type, code, starting point, destination, stops, fare, hours, travel time, status. Used in the line popup and the side panel |
| `components/MapFilters.tsx` | Bus / Jeepney / Van and Active / Inactive check boxes |
| `utils/mapIcons.ts` | The A and B markers, drawn with HTML so no image files are needed |

How it behaves:
- Line colors come from `utils/transportColors.ts`. Inactive and suspended routes are dashed.
- "Inactive routes" in the filter means every route that is not ACTIVE.
- Click a line: a popup opens and the route is selected (thicker line, details under the map).
- Click a stop: its popup lists the routes through it, with "Set as origin / destination" buttons.
- "Pick origin / destination" then a click on the map places the A / B marker. "Use my location"
  asks the browser for your position (only when you click it).
- When an origin is set, the stops within 2 km are listed, using `GET /api/stops/nearby`.
- `/map?route=3` opens the map with route 3 selected (the "View on map" button on a route page).
- The straight-line distance between A and B is only an approximation, not a travel distance.
  Finding transportation between A and B comes with the route search (Phase 15).
