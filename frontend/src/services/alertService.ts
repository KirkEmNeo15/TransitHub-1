import type { Alert } from '../types/Alert'
import api from './api'

/** Active alerts, newest first. */
export async function getActiveAlerts(): Promise<Alert[]> {
  const response = await api.get<Alert[]>('/api/alerts')
  return response.data
}
