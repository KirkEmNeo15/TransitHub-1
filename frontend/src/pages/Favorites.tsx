import { useState } from 'react'
import { Link } from 'react-router'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'
import RouteCard from '../components/RouteCard'
import { useApiData } from '../hooks/useApiData'
import { getFavorites, removeFavorite } from '../services/favoriteService'
import { parseApiError } from '../utils/apiError'

export default function FavoritesPage() {
  const { state, reload } = useApiData(getFavorites)
  const [removeError, setRemoveError] = useState('')

  const handleRemove = async (routeId: number) => {
    setRemoveError('')
    try {
      await removeFavorite(routeId)
      reload()
    } catch (error) {
      setRemoveError(parseApiError(error).message)
    }
  }

  return (
    <section>
      <h1 className="text-2xl font-bold">My favorite routes</h1>
      <p className="mt-1 text-slate-600">The routes you saved.</p>

      {removeError && <div className="mt-4"><ErrorMessage message={removeError} /></div>}

      <div className="mt-6">
        {state.status === 'loading' && <LoadingSpinner label="Loading your favorites..." />}
        {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
        {state.status === 'success' && state.data.length === 0 && (
          <div className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-600">
            <p>You have not saved any routes yet.</p>
            <Link to="/routes" className="mt-2 inline-block font-semibold text-primary underline">
              Browse routes
            </Link>
          </div>
        )}
        {state.status === 'success' && state.data.length > 0 && (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {state.data.map((route) => (
              <RouteCard
                key={route.id}
                route={route}
                actions={
                  <button
                    type="button"
                    onClick={() => handleRemove(route.id)}
                    className="text-sm font-semibold text-danger hover:underline"
                  >
                    Remove
                  </button>
                }
              />
            ))}
          </div>
        )}
      </div>
    </section>
  )
}
