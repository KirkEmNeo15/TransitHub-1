import { useCallback, useState } from 'react'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'
import RouteCard from '../components/RouteCard'
import { useApiData } from '../hooks/useApiData'
import { getRoutes } from '../services/routeService'
import type { RouteStatus } from '../types/Route'

const typeOptions = [
  { value: '', label: 'All types' },
  { value: 'BUS', label: 'Bus' },
  { value: 'JEEPNEY', label: 'Jeepney' },
  { value: 'VAN', label: 'Van' },
]

const statusOptions: { value: '' | RouteStatus; label: string }[] = [
  { value: '', label: 'All statuses' },
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
  { value: 'SUSPENDED', label: 'Suspended' },
]

// The route search by origin and destination is added at the top of this page in Phase 15.
export default function RoutesPage() {
  const [type, setType] = useState('')
  const [status, setStatus] = useState<'' | RouteStatus>('')

  // a new function (and so a new request) is created only when a filter changes
  const loadRoutes = useCallback(
    () => getRoutes({ type: type || undefined, status: status || undefined }),
    [type, status],
  )
  const { state, reload } = useApiData(loadRoutes)

  return (
    <section>
      <h1 className="text-2xl font-bold">Routes</h1>
      <p className="mt-1 text-slate-600">All transportation routes. Choose a route to see its stops, fare and schedule.</p>

      <div className="mt-6 flex flex-wrap gap-3">
        <div>
          <label htmlFor="type-filter" className="mb-1 block text-sm font-medium">
            Type
          </label>
          <select
            id="type-filter"
            value={type}
            onChange={(event) => setType(event.target.value)}
            className="rounded-lg border border-slate-300 bg-white px-3 py-2"
          >
            {typeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="status-filter" className="mb-1 block text-sm font-medium">
            Status
          </label>
          <select
            id="status-filter"
            value={status}
            onChange={(event) => setStatus(event.target.value as '' | RouteStatus)}
            className="rounded-lg border border-slate-300 bg-white px-3 py-2"
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="mt-6">
        {state.status === 'loading' && <LoadingSpinner label="Loading routes..." />}
        {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
        {state.status === 'success' && state.data.length === 0 && (
          <p className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
            No routes match these filters.
          </p>
        )}
        {state.status === 'success' && state.data.length > 0 && (
          <>
            <p className="mb-3 text-sm text-slate-600">{state.data.length} route(s)</p>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {state.data.map((route) => (
                <RouteCard key={route.id} route={route} />
              ))}
            </div>
          </>
        )}
      </div>
    </section>
  )
}
