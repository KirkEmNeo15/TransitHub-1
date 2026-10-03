// Matches UserResponse, AuthResponse, LoginRequest and RegisterRequest in docs/API.md.
export type Role = 'USER' | 'ADMIN'

export interface User {
  id: number
  fullName: string
  email: string
  role: Role
  active: boolean
  createdAt: string
}

export interface AuthResponse {
  token: string
  tokenType: string
  expiresInSeconds: number
  user: User
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  fullName: string
  email: string
  password: string
}
