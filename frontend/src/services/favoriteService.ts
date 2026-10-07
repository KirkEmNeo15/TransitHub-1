import type { Route } from '../types/Route'
import api from './api'

// These calls need a logged-in user: the token is added by the Axios interceptor.

export async function getFavorites(): Promise<Route[]> {
  const response = await api.get<Route[]>('/api/favorites')
  return response.data
}

export async function addFavorite(routeId: number): Promise<void> {
  await api.post(`/api/favorites/${routeId}`)
}

export async function removeFavorite(routeId: number): Promise<void> {
  await api.delete(`/api/favorites/${routeId}`)
}
