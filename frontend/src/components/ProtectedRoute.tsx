import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from '../hooks/useAuth'
import LoadingSpinner from './LoadingSpinner'

interface ProtectedRouteProps {
  requireAdmin?: boolean
}

/**
 * Wraps pages that need a login (and optionally the ADMIN role).
 * This only decides what the user SEES. The backend enforces the real security
 * on every API call, so hiding a page here is never the only protection.
 */
export default function ProtectedRoute({ requireAdmin = false }: ProtectedRouteProps) {
  const { isAuthenticated, isAdmin, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <LoadingSpinner label="Checking your login..." />
  }
  if (!isAuthenticated) {
    // remember where the user wanted to go, so we can send them back after login
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }
  if (requireAdmin && !isAdmin) {
    return (
      <section className="mx-auto max-w-md px-4 py-16 text-center">
        <h1 className="text-2xl font-bold">Access denied</h1>
        <p className="mt-2 text-slate-600">This area is only for administrators.</p>
      </section>
    )
  }
  return <Outlet />
}
