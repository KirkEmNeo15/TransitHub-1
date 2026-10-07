import type { Report, ReportRequest } from '../types/Report'
import api from './api'

export async function submitReport(request: ReportRequest): Promise<Report> {
  const response = await api.post<Report>('/api/reports', request)
  return response.data
}

export async function getMyReports(): Promise<Report[]> {
  const response = await api.get<Report[]>('/api/reports/mine')
  return response.data
}
