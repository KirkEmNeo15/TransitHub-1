import { createContext } from 'react'
import type { User } from '../types/User'

// The shared login state that any component can read with useAuth().
export interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  isAdmin: boolean
  // true while we check an existing token after a page refresh
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  register: (fullName: string, email: string, password: string) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined)
