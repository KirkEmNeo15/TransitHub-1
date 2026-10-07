import type { AlertRequest } from '../types/Admin'
import type { Alert } from '../types/Alert'
import api from './api'

/** Active alerts, newest first. */
export async function getActiveAlerts(): Promise<Alert[]> {
  const response = await api.get<Alert[]>('/api/alerts')
  return response.data
}

// ---------- admin ----------

/** Every alert, including inactive ones. */
export async function getAllAlerts(): Promise<Alert[]> {
  const response = await api.get<Alert[]>('/api/admin/alerts')
  return response.data
}

export async function createAlert(request: AlertRequest): Promise<Alert> {
  const response = await api.post<Alert>('/api/alerts', request)
  return response.data
}

export async function updateAlert(id: number, request: AlertRequest): Promise<Alert> {
  const response = await api.put<Alert>(`/api/alerts/${id}`, request)
  return response.data
}

export async function deleteAlert(id: number): Promise<void> {
  await api.delete(`/api/alerts/${id}`)
}
