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
