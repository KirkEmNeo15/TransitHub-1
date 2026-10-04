import type { Route, RouteStatus } from '../types/Route'
import api from './api'

export interface RouteFilters {
  type?: string // "BUS", "JEEPNEY" or "VAN"
  status?: RouteStatus
}

export async function getRoutes(filters: RouteFilters = {}): Promise<Route[]> {
  // undefined values are left out of the URL by Axios
  const response = await api.get<Route[]>('/api/routes', { params: filters })
  return response.data
}

export async function getRoute(id: number): Promise<Route> {
  const response = await api.get<Route>(`/api/routes/${id}`)
  return response.data
}

/** Active, direct routes from origin to destination, fastest first. Used by the search in Phase 15. */
export async function searchRoutes(origin: string, destination: string): Promise<Route[]> {
  const response = await api.get<Route[]>('/api/routes/search', { params: { origin, destination } })
  return response.data
}
