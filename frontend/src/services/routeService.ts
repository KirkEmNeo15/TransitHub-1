import type { RouteRequest } from '../types/Admin'
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

/** Active, direct routes from origin to destination, fastest first. */
export async function searchRoutes(origin: string, destination: string): Promise<Route[]> {
  const response = await api.get<Route[]>('/api/routes/search', { params: { origin, destination } })
  return response.data
}

// ---------- admin: create, update, delete (the backend allows these for ADMIN only) ----------

export async function createRoute(request: RouteRequest): Promise<Route> {
  const response = await api.post<Route>('/api/routes', request)
  return response.data
}

export async function updateRoute(id: number, request: RouteRequest): Promise<Route> {
  const response = await api.put<Route>(`/api/routes/${id}`, request)
  return response.data
}

export async function changeRouteStatus(id: number, status: RouteStatus): Promise<Route> {
  const response = await api.patch<Route>(`/api/routes/${id}/status`, { status })
  return response.data
}

export async function deleteRoute(id: number): Promise<void> {
  await api.delete(`/api/routes/${id}`)
}
