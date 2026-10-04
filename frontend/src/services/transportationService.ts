import type { Transportation } from '../types/Transportation'
import api from './api'

export async function getTransportations(type?: string): Promise<Transportation[]> {
  const response = await api.get<Transportation[]>('/api/transportations', { params: { type } })
  return response.data
}
