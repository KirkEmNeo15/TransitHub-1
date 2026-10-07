// Shape of the answer from GET /api/health (see docs/API.md).
// The other API types (Route, Stop, ...) are added in Phase 13.
export interface HealthResponse {
  application: string
  status: string
  database: string
  routesInDatabase: number | null
}
