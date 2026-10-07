# Testing TransitHub

All sample data is fictional. Tests use this fictional data (backend) or small made-up data (frontend).

## 1. Backend tests (JUnit 5, 130 tests in 16 classes)

Run them from the `backend/` folder:

```bash
./mvnw clean test          # Windows CMD: mvnw clean test
```

`clean` removes old compiled files first. The **database must be running** (`docker compose up -d`) and
`.env` must exist, because most tests start the whole application (`@SpringBootTest`) and use the demo data.
Each test runs inside a transaction that is rolled back, so tests do not change your data.

If you loaded `sample-data.sql` twice, some tests fail with "expected 1 but was 2". Reset the database:
`docker compose down -v` then `docker compose up -d`.

### Unit tests (no database, 29 tests)
| Class | Tests | What it proves |
|---|---|---|
| `RouteEncapsulationTest` | 6 | A route's stops can only be changed through the route itself; invalid values are rejected (encapsulation) |
| `TransportationPolymorphismTest` | 7 | The same `calculateFare` call gives a different fare for Bus, Jeepney and Van (polymorphism) |
| `JwtServiceTest` | 7 | Tokens carry the user id; tampered, expired, foreign and garbage tokens are rejected; weak secrets refused |
| `DtoValidationTest` | 5 | Request objects reject blank names, bad coordinates, negative fares, routes without stops |
| `GeoUtilsTest` | 4 | Distance between two points; kilometers to degrees |

### Integration tests (need the database, 101 tests)
| Class | Tests | What it proves |
|---|---|---|
| `EntityMappingTest` | 3 | Tables and classes match; each transportation loads as its own subclass (inheritance) |
| `RepositoryTest` | 10 | Route search (direction, case, inactive routes), stops in an area, saving a route with its children |
| `RouteServiceTest` | 8 | Search fastest first with fares, create/update/delete, duplicate codes, status change |
| `StopServiceTest` | 6 | Nearby stops sorted by distance, unique names, a stop used by a route cannot be deleted |
| `TransportationServiceTest` | 7 | Each type is created as its subclass, type details required, a used transportation cannot be deleted |
| `AlertFavoriteReportStatsServiceTest` | 7 | Alerts, favorites, reports and statistics |
| `RouteControllerTest` | 10 | Route REST endpoints, status codes and JSON |
| `StopControllerTest` | 5 | Stop REST endpoints |
| `OtherControllersTest` | 12 | Transportation, alert, stats and admin table endpoints |
| `ErrorHandlingTest` | 18 | Every error comes back in the same JSON format (400, 404, 409, field errors) |
| `AuthSecurityTest` | 15 | Register, login, roles (USER vs ADMIN), 401/403, deactivated users, favorites belong to their owner |

## 2. Frontend tests (Vitest, 9 test files)

Vitest is a test runner made for Vite projects. React Testing Library draws a component in a pretend browser (jsdom)
so a test can click and read it.

### One-time setup (in the `frontend/` folder)

```bash
npm install -D vitest jsdom @testing-library/react
```

Then make two small edits:

1. In `frontend/package.json`, inside `"scripts"`, add: `"test": "vitest run"` and `"test:watch": "vitest"`.
2. In `frontend/tsconfig.app.json`, add a line next to `"include"` so the normal build does not check test files:
   `"exclude": ["src/**/*.test.ts", "src/**/*.test.tsx", "src/test"]`

Run:

```bash
npm test
```

### What is tested

| File | What it checks |
|---|---|
| `utils/format.test.ts` | Times, durations, distances, pesos, schedules and type details are shown correctly |
| `utils/geo.test.ts` | Distance formula and coordinate text |
| `utils/validation.test.ts` | The same email and password rules as the backend |
| `utils/tripSection.test.ts` | Where to get on and off for a search, and the riding time |
| `utils/routeForm.test.ts` | The admin route form: required fields, repeated stops, bad numbers, schedules, and keeping or redrawing the map line |
| `utils/transportColors.test.ts` | Each type has its own color |
| `utils/tokenStorage.test.ts` | The login token is saved, read and forgotten |
| `utils/apiError.test.ts` | Server errors become readable messages and field errors |
| `components/ui.test.tsx` | StatusBadge, ErrorMessage, FormField, DataTable, Pagination, BarChart |

`test/fixtures.ts` builds a small made-up route used by several tests. `test/setup.ts` cleans up after each test.

## 3. Manual test checklist (before the demo)

Start the database, backend (`./mvnw spring-boot:run`) and frontend (`npm run dev`), then check:

- [ ] Home page shows statistics and the demo data notice
- [ ] Routes page lists routes; filter by type; search origin and destination
- [ ] Route details show stops, fare, schedule; the Report form works when logged in
- [ ] Map shows colored route lines, stops, filters; clicking a stop or route shows details
- [ ] Register a new account; log in; log out; a wrong password shows an error
- [ ] Favorites: add and remove a route (logged in)
- [ ] Alerts page shows active alerts only
- [ ] A normal user opening `/admin` is sent away; the admin can open it
- [ ] Admin: add, edit, delete a stop; try deleting a stop used by a route (must be refused)
- [ ] Admin: add and edit a route with 3 stops; check it on the map
- [ ] Admin: add a Van, Jeepney and Bus; the extra fields change with the type
- [ ] Admin: create an alert, make it inactive, check it disappears from the Alerts page
- [ ] Admin: disable a user; that user can no longer use the site
- [ ] Stop the backend: pages show "Cannot reach the server" with a Try again button

## 4. What was and was not verified while writing this

- The pure frontend logic tests (format, geo, validation, trip section, route form, colors: 38 tests) were run
  with a stand-in test runner and passed.
- The backend tests, the component tests, and the tests that need Axios or localStorage were **not**
  run by the author; they must be run on your machine (`./mvnw clean test` and `npm test`). If one fails, send the output.
