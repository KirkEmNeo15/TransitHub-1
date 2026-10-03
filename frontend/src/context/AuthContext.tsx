import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import * as authService from '../services/authService'
import type { User } from '../types/User'
import { tokenStorage } from '../utils/tokenStorage'
import { AuthContext } from './authContextValue'
import type { AuthContextValue } from './authContextValue'

/** Keeps track of who is logged in and shares it with the whole app. */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  // If a token is saved, we are "loading" until the backend confirms it is still valid.
  const [loading, setLoading] = useState<boolean>(() => tokenStorage.get() !== null)

  // After a page refresh: ask the backend who the saved token belongs to.
  useEffect(() => {
    if (tokenStorage.get() === null) return
    let cancelled = false
    authService
      .fetchCurrentUser()
      .then((currentUser) => {
        if (!cancelled) setUser(currentUser)
      })
      .catch(() => {
        // An expired token (401) is cleared by the Axios interceptor. If the backend is simply
        // unreachable we keep the token, so the user is not logged out by a temporary problem.
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  // The Axios response interceptor announces expired tokens with this event.
  useEffect(() => {
    const handleExpired = () => setUser(null)
    window.addEventListener('auth:expired', handleExpired)
    return () => window.removeEventListener('auth:expired', handleExpired)
  }, [])

  const login = useCallback(async (email: string, password: string) => {
    const response = await authService.login({ email, password })
    tokenStorage.set(response.token)
    setUser(response.user)
  }, [])

  const register = useCallback(async (fullName: string, email: string, password: string) => {
    const response = await authService.register({ fullName, email, password })
    tokenStorage.set(response.token)
    setUser(response.user)
  }, [])

  const logout = useCallback(() => {
    tokenStorage.clear()
    setUser(null)
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: user !== null,
      isAdmin: user?.role === 'ADMIN',
      loading,
      login,
      register,
      logout,
    }),
    [user, loading, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
