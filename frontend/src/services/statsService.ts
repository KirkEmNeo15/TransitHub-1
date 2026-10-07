import type { PublicStats } from '../types/Stats'
import api from './api'

export async function getPublicStats(): Promise<PublicStats> {
  const response = await api.get<PublicStats>('/api/stats')
  return response.data
}
