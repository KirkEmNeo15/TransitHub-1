import type { TransportationRequest } from '../types/Admin'
import type { Transportation } from '../types/Transportation'
import api from './api'

export async function getTransportations(type?: string): Promise<Transportation[]> {
  const response = await api.get<Transportation[]>('/api/transportations', { params: { type } })
  return response.data
}

// ---------- admin: create, update, delete ----------

export async function createTransportation(request: TransportationRequest): Promise<Transportation> {
  const response = await api.post<Transportation>('/api/transportations', request)
  return response.data
}

export async function updateTransportation(id: number, request: TransportationRequest): Promise<Transportation> {
  const response = await api.put<Transportation>(`/api/transportations/${id}`, request)
  return response.data
}

export async function deleteTransportation(id: number): Promise<void> {
  await api.delete(`/api/transportations/${id}`)
}
