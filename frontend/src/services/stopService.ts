import type { StopRequest } from '../types/Admin'
import type { Route } from '../types/Route'
import type { NearbyStop, Stop } from '../types/Stop'
import api from './api'

export async function getStops(search?: string): Promise<Stop[]> {
  const response = await api.get<Stop[]>('/api/stops', { params: { search } })
  return response.data
}

export async function getNearbyStops(
  latitude: number,
  longitude: number,
  radiusKm = 2,
): Promise<NearbyStop[]> {
  const response = await api.get<NearbyStop[]>('/api/stops/nearby', {
    params: { lat: latitude, lng: longitude, radiusKm },
  })
  return response.data
}

export async function getRoutesThroughStop(stopId: number): Promise<Route[]> {
  const response = await api.get<Route[]>(`/api/stops/${stopId}/routes`)
  return response.data
}

// ---------- admin: create, update, delete ----------

export async function createStop(request: StopRequest): Promise<Stop> {
  const response = await api.post<Stop>('/api/stops', request)
  return response.data
}

export async function updateStop(id: number, request: StopRequest): Promise<Stop> {
  const response = await api.put<Stop>(`/api/stops/${id}`, request)
  return response.data
}

export async function deleteStop(id: number): Promise<void> {
  await api.delete(`/api/stops/${id}`)
}
