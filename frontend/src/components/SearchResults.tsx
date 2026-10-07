import { useCallback, useMemo, useState } from 'react'
import { Link } from 'react-router'
import { useApiData } from '../hooks/useApiData'
import { searchRoutes } from '../services/routeService'
import type { Route } from '../types/Route'
import ErrorMessage from './ErrorMessage'
import LoadingSpinner from './LoadingSpinner'
import SearchResultCard from './SearchResultCard'

type SortOrder = 'fastest' | 'cheapest'

const NO_ROUTES: Route[] = []

interface SearchResultsProps {
  origin: string
  destination: string
}

/** Searches the backend for routes between two places and lists the results. */
export default function SearchResults({ origin, destination }: SearchResultsProps) {
  const load = useCallback(() => searchRoutes(origin, destination), [origin, destination])
  const { state, reload } = useApiData(load)
  const [sort, setSort] = useState<SortOrder>('fastest')

  const routes = state.status === 'success' ? state.data : NO_ROUTES
  const sorted = useMemo(() => {
    const copy = [...routes]
    if (sort === 'fastest') {
      copy.sort((a, b) => a.estimatedMinutes - b.estimatedMinutes)
    } else {
      // routes without a fare go last
      copy.sort((a, b) => (a.estimatedFare ?? Infinity) - (b.estimatedFare ?? Infinity))
    }
    return copy
  }, [routes, sort])

  if (state.status === 'loading') return <LoadingSpinner label="Searching routes..." />
  if (state.status === 'error') return <ErrorMessage message={state.message} onRetry={reload} />

  const mapLink = `/map?${new URLSearchParams({ origin, destination }).toString()}`

  if (state.data.length === 0) {
    return (
      <div className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-600">
        <p className="font-semibold">No direct routes found from {origin} to {destination}.</p>
        <p className="mt-1 text-sm">
          Try another stop name, or swap the two places. Routes that need a transfer are not searched yet.
        </p>
        <Link to="/map" className="mt-3 inline-block font-semibold text-primary underline">
          Look at the map
        </Link>
      </div>
    )
  }

  return (
    <div>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <p className="text-slate-700">
          <strong>{state.data.length}</strong> route(s) from <strong>{origin}</strong> to{' '}
          <strong>{destination}</strong>
        </p>
        <div className="flex items-center gap-4">
          <label className="flex items-center gap-2 text-sm">
            Sort by
            <select
              value={sort}
              onChange={(event) => setSort(event.target.value as SortOrder)}
              className="rounded-lg border border-slate-300 bg-white px-2 py-1"
            >
              <option value="fastest">Fastest</option>
              <option value="cheapest">Cheapest</option>
            </select>
          </label>
          <Link to={mapLink} className="text-sm font-semibold text-primary hover:underline">
            Show on map
          </Link>
        </div>
      </div>

      <div className="mt-4 grid gap-4 lg:grid-cols-2">
        {sorted.map((route) => (
          <SearchResultCard key={route.id} route={route} origin={origin} destination={destination} />
        ))}
      </div>
    </div>
  )
}
