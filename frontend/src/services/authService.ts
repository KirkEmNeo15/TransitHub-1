import type { AuthResponse, LoginRequest, RegisterRequest, User } from '../types/User'
import api from './api'

export async function login(request: LoginRequest): Promise<AuthResponse> {
  const response = await api.post<AuthResponse>('/api/auth/login', request)
  return response.data
}

export async function register(request: RegisterRequest): Promise<AuthResponse> {
  const response = await api.post<AuthResponse>('/api/auth/register', request)
  return response.data
}

export async function fetchCurrentUser(): Promise<User> {
  const response = await api.get<User>('/api/auth/me')
  return response.data
}
