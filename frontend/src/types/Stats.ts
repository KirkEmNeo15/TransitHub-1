// Matches PublicStatsResponse in docs/API.md. (The admin numbers are added in Phase 16.)
export interface PublicStats {
  activeRoutes: number
  stops: number
  availableVehicles: number
  activeAlerts: number
}
