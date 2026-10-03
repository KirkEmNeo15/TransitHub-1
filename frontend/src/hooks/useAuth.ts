import { useContext } from 'react'
import { AuthContext } from '../context/authContextValue'
import type { AuthContextValue } from '../context/authContextValue'

/** Gives any component the current user and the login/logout functions. */
export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used inside <AuthProvider>')
  }
  return context
}
