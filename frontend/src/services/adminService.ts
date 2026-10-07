import type { AdminStats, PageResponse } from '../types/Admin'
import type { Report, ReportStatus } from '../types/Report'
import type { Route } from '../types/Route'
import type { Stop } from '../types/Stop'
import type { Role, User } from '../types/User'
import api from './api'

// Everything under /api/admin needs an ADMIN login.

export async function getAdminStats(): Promise<AdminStats> {
  const response = await api.get<AdminStats>('/api/admin/stats')
  return response.data
}

export async function getAdminRoutes(search: string, page: number, size: number): Promise<PageResponse<Route>> {
  const response = await api.get<PageResponse<Route>>('/api/admin/routes', { params: { search, page, size } })
  return response.data
}

export async function getAdminStops(search: string, page: number, size: number): Promise<PageResponse<Stop>> {
  const response = await api.get<PageResponse<Stop>>('/api/admin/stops', { params: { search, page, size } })
  return response.data
}

export async function getAdminUsers(search: string, page: number, size: number): Promise<PageResponse<User>> {
  const response = await api.get<PageResponse<User>>('/api/admin/users', { params: { search, page, size } })
  return response.data
}

export async function setUserActive(id: number, active: boolean): Promise<User> {
  const response = await api.patch<User>(`/api/admin/users/${id}/active`, { active })
  return response.data
}

export async function changeUserRole(id: number, role: Role): Promise<User> {
  const response = await api.patch<User>(`/api/admin/users/${id}/role`, { role })
  return response.data
}

export async function getAdminReports(status?: ReportStatus): Promise<Report[]> {
  const response = await api.get<Report[]>('/api/admin/reports', { params: { status } })
  return response.data
}

export async function updateReportStatus(id: number, status: ReportStatus): Promise<Report> {
  const response = await api.patch<Report>(`/api/admin/reports/${id}/status`, { status })
  return response.data
}
