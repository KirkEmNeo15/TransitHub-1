import { useCallback, useMemo, useState } from 'react'
import type { LatLngTuple } from 'leaflet'
import { Link, useSearchParams } from 'react-router'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'
import MapFilters from '../components/MapFilters'
import type { MapFilterState } from '../components/MapFilters'
import MapView from '../components/MapView'
import type { FlyTarget } from '../components/MapView'
import RouteSearch from '../components/RouteSearch'
import RouteSummary from '../components/RouteSummary'
import { useApiData } from '../hooks/useApiData'
import { getRoutes, searchRoutes } from '../services/routeService'
import { getNearbyStops, getStops } from '../services/stopService'
import type { Route } from '../types/Route'
import type { NearbyStop, Stop } from '../types/Stop'
import type { PickMode, TripPoint } from '../types/Trip'
import { formatDuration, formatPeso } from '../utils/format'
import { distanceKm, formatCoordinates } from '../utils/geo'
import { colorForType } from '../utils/transportColors'

const NO_ROUTES: Route[] = []
const NO_STOPS: Stop[] = []

// defined outside the component so the identity stays the same (see useApiData)
const loadAllRoutes = () => getRoutes()
const loadAllStops = () => getStops()

const INITIAL_FILTERS: MapFilterState = {
  types: { Bus: true, Jeepney: true, Van: true },
  showActive: true,
  showInactive: true,
}

function routeMatchesQuery(route: Route, query: string): boolean {
  if (query === '') return true
  const text = [route.routeName, route.routeCode, route.origin, route.destination, route.transportation.name]
    .join(' ')
    .toLowerCase()
  return text.includes(query)
}

export default function MapPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const routesData = useApiData(loadAllRoutes)
  const stopsData = useApiData(loadAllStops)

  const [filters, setFilters] = useState<MapFilterState>(INITIAL_FILTERS)
  const [query, setQuery] = useState('')
  // "View on map" on the route page opens this page with ?route=<id>
  const [selectedRouteId, setSelectedRouteId] = useState<number | null>(() => {
    const fromUrl = Number(searchParams.get('route'))
    return Number.isInteger(fromUrl) && fromUrl > 0 ? fromUrl : null
  })

  const [pickMode, setPickMode] = useState<PickMode | null>(null)
  const [origin, setOrigin] = useState<TripPoint | null>(null)
  const [destination, setDestination] = useState<TripPoint | null>(null)
  const [flyTarget, setFlyTarget] = useState<FlyTarget | null>(null)
  const [locationError, setLocationError] = useState('')

  // ----- route search (the address looks like /map?origin=Lipa&destination=Batangas) -----
  const searchOrigin = searchParams.get('origin') ?? ''
  const searchDestination = searchParams.get('destination') ?? ''
  const searchActive = searchOrigin !== '' && searchDestination !== ''
  const loadSearch = useCallback(
    () => (searchActive ? searchRoutes(searchOrigin, searchDestination) : Promise.resolve<Route[]>([])),
    [searchActive, searchOrigin, searchDestination],
  )
  const search = useApiData(loadSearch)
  const searchResults = searchActive && search.state.status === 'success' ? search.state.data : NO_ROUTES
  const highlightedIds = useMemo(
    () => (searchActive && search.state.status === 'success' ? new Set(search.state.data.map((route) => route.id)) : null),
    [searchActive, search.state],
  )

  const allRoutes = routesData.state.status === 'success' ? routesData.state.data : NO_ROUTES
  const allStops = stopsData.state.status === 'success' ? stopsData.state.data : NO_STOPS

  // ----- what is shown, after the filters -----
  const visibleRoutes = useMemo(() => {
    const text = query.trim().toLowerCase()
    return allRoutes.filter((route) => {
      const typeAllowed = filters.types[route.transportation.type]
      const statusAllowed = route.status === 'ACTIVE' ? filters.showActive : filters.showInactive
      return typeAllowed && statusAllowed && routeMatchesQuery(route, text)
    })
  }, [allRoutes, filters, query])

  const visibleStops = useMemo(() => {
    // only stops that belong to at least one visible route
    const ids = new Set(visibleRoutes.flatMap((route) => route.stops.map((routeStop) => routeStop.stop.id)))
    return allStops.filter((stop) => ids.has(stop.id))
  }, [visibleRoutes, allStops])

  const selectedRoute = visibleRoutes.find((route) => route.id === selectedRouteId) ?? null
  const visibleIds = new Set(visibleRoutes.map((route) => route.id))

  // the map zooms to the selected route, else to the search results, else to all routes
  const focusBounds = useMemo<LatLngTuple[] | null>(() => {
    let source: Route[] = allRoutes
    if (selectedRoute) source = [selectedRoute]
    else if (searchResults.length > 0) source = searchResults
    const points = source.flatMap((route) =>
      route.path.map((point): LatLngTuple => [point.latitude, point.longitude]),
    )
    return points.length > 0 ? points : null
  }, [selectedRoute, searchResults, allRoutes])

  // ----- stops near the origin -----
  const loadNearby = useCallback(
    () =>
      origin
        ? getNearbyStops(origin.latitude, origin.longitude, 2)
        : Promise.resolve<NearbyStop[]>([]),
    [origin],
  )
  const nearby = useApiData(loadNearby)

  const loadNearbyDestination = useCallback(
    () =>
      destination
        ? getNearbyStops(destination.latitude, destination.longitude, 2)
        : Promise.resolve<NearbyStop[]>([]),
    [destination],
  )
  const nearbyDestination = useApiData(loadNearbyDestination)

  // The closest stop to A and to B. They are used to search for routes between the two points.
  const originStop = nearby.state.status === 'success' ? (nearby.state.data[0]?.stop ?? null) : null
  const destinationStop =
    nearbyDestination.state.status === 'success' ? (nearbyDestination.state.data[0]?.stop ?? null) : null

  const findRoutesBetweenPoints = () => {
    if (originStop && destinationStop) {
      setSearchParams({ origin: originStop.name, destination: destinationStop.name })
    }
  }

  // ----- choosing the origin and the destination -----
  const setTripPoint = (mode: PickMode, point: TripPoint) => {
    if (mode === 'origin') setOrigin(point)
    else setDestination(point)
    setPickMode(null)
  }

  const handlePick = (mode: PickMode, latitude: number, longitude: number) => {
    setTripPoint(mode, { latitude, longitude, label: formatCoordinates(latitude, longitude) })
  }

  const handleStopAsTripPoint = (mode: PickMode, stop: Stop) => {
    setTripPoint(mode, { latitude: stop.latitude, longitude: stop.longitude, label: stop.name })
  }

  const useMyLocation = () => {
    setLocationError('')
    if (!navigator.geolocation) {
      setLocationError('Your browser cannot share your location.')
      return
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const point = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
          label: 'My location',
        }
        setOrigin(point)
        setFlyTarget({ latitude: point.latitude, longitude: point.longitude })
      },
      () => setLocationError('Could not get your location. Allow location access, or pick a point on the map.'),
    )
  }

  const clearTrip = () => {
    setOrigin(null)
    setDestination(null)
    setPickMode(null)
  }

  const straightLine =
    origin && destination
      ? distanceKm(origin.latitude, origin.longitude, destination.latitude, destination.longitude)
      : null

  // ----- loading and errors -----
  if (routesData.state.status === 'loading' || stopsData.state.status === 'loading') {
    return <LoadingSpinner label="Loading the map data..." />
  }
  if (routesData.state.status === 'error') {
    return <ErrorMessage message={routesData.state.message} onRetry={routesData.reload} />
  }
  if (stopsData.state.status === 'error') {
    return <ErrorMessage message={stopsData.state.message} onRetry={stopsData.reload} />
  }

  return (
    <section className="space-y-4">
      <div>
        <h1 className="text-2xl font-bold">Map</h1>
        <p className="text-slate-600">
          Click a route line or a stop for details. All data is fictional demo data.
        </p>
      </div>

      <RouteSearch
        key={`${searchOrigin}|${searchDestination}`}
        initialOrigin={searchOrigin}
        initialDestination={searchDestination}
        suggestions={allStops.map((stop) => stop.name)}
        onSearch={(from, to) => setSearchParams({ origin: from, destination: to })}
        onClear={searchActive ? () => setSearchParams({}) : undefined}
      />

      <div>
        <label htmlFor="map-search" className="sr-only">
          Filter the routes on the map
        </label>
        <input
          id="map-search"
          type="search"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Filter the routes on the map (name, code, origin, destination, operator)"
          className="w-full rounded-lg border border-slate-300 bg-white px-4 py-2 outline-none focus:ring-2 focus:ring-primary/40"
        />
      </div>

      <div className="grid gap-4 lg:grid-cols-[320px_1fr]">
        {/* ---------- side panel ---------- */}
        <aside className="space-y-4">
          {searchActive && (
            <div className="rounded-xl border border-primary/40 bg-blue-50 p-4 shadow-sm">
              <h2 className="text-sm font-semibold">
                Routes from {searchOrigin} to {searchDestination}
              </h2>
              {search.state.status === 'loading' && <p className="mt-2 text-sm text-slate-600">Searching...</p>}
              {search.state.status === 'error' && (
                <p role="alert" className="mt-2 text-sm text-danger">
                  {search.state.message}
                </p>
              )}
              {search.state.status === 'success' && search.state.data.length === 0 && (
                <p className="mt-2 text-sm text-slate-700">
                  No direct routes found. Try other stop names. Routes that need a transfer are not searched yet.
                </p>
              )}
              {search.state.status === 'success' && search.state.data.length > 0 && (
                <>
                  <p className="mt-1 text-xs text-slate-600">The other routes are dimmed on the map.</p>
                  <ul className="mt-2 space-y-1">
                    {search.state.data.map((route) => (
                      <li key={route.id}>
                        <button
                          type="button"
                          onClick={() => setSelectedRouteId(route.id)}
                          className={`w-full rounded px-2 py-1.5 text-left text-sm hover:bg-white ${
                            route.id === selectedRoute?.id ? 'bg-white font-semibold' : ''
                          }`}
                        >
                          <span
                            className="mr-2 inline-block h-3 w-3 rounded-full align-middle"
                            style={{ backgroundColor: colorForType(route.transportation.type) }}
                          />
                          {route.routeName}
                          <span className="block pl-5 text-xs font-normal text-slate-600">
                            {formatPeso(route.estimatedFare)} &middot; {formatDuration(route.estimatedMinutes)}
                            {!visibleIds.has(route.id) && ' \u00b7 hidden by the filters'}
                          </span>
                        </button>
                      </li>
                    ))}
                  </ul>
                </>
              )}
            </div>
          )}

          <MapFilters filters={filters} onChange={setFilters} />

          <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
            <h2 className="text-sm font-semibold">Your trip</h2>
            <div className="mt-3 flex flex-wrap gap-2">
              <button
                type="button"
                onClick={() => setPickMode(pickMode === 'origin' ? null : 'origin')}
                className={`rounded-lg border px-3 py-1.5 text-sm font-medium ${
                  pickMode === 'origin' ? 'border-green-600 bg-green-50 text-green-700' : 'border-slate-300 hover:bg-slate-50'
                }`}
              >
                Pick origin
              </button>
              <button
                type="button"
                onClick={() => setPickMode(pickMode === 'destination' ? null : 'destination')}
                className={`rounded-lg border px-3 py-1.5 text-sm font-medium ${
                  pickMode === 'destination' ? 'border-red-600 bg-red-50 text-red-700' : 'border-slate-300 hover:bg-slate-50'
                }`}
              >
                Pick destination
              </button>
              <button
                type="button"
                onClick={useMyLocation}
                className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium hover:bg-slate-50"
              >
                Use my location
              </button>
            </div>

            {pickMode && (
              <p className="mt-2 rounded bg-blue-50 px-2 py-1 text-sm text-blue-800">
                Click the map to set the {pickMode}, or click a stop and choose &ldquo;Set as {pickMode}&rdquo;.
              </p>
            )}
            {locationError && <p className="mt-2 text-sm text-danger">{locationError}</p>}

            <dl className="mt-3 space-y-1 text-sm">
              <div className="flex gap-2">
                <dt className="font-semibold text-green-700">A Origin:</dt>
                <dd>{origin ? origin.label : <span className="text-slate-400">not set</span>}</dd>
              </div>
              <div className="flex gap-2">
                <dt className="font-semibold text-red-700">B Destination:</dt>
                <dd>{destination ? destination.label : <span className="text-slate-400">not set</span>}</dd>
              </div>
              {straightLine !== null && (
                <div className="text-slate-600">Straight-line distance: {straightLine.toFixed(1)} km</div>
              )}
            </dl>
            {origin && destination && (
              <div className="mt-3 rounded-lg bg-slate-50 p-3 text-sm">
                {originStop && destinationStop ? (
                  <>
                    <p className="text-slate-600">
                      Nearest stops: <strong>{originStop.name}</strong> and <strong>{destinationStop.name}</strong>
                    </p>
                    <button
                      type="button"
                      onClick={findRoutesBetweenPoints}
                      className="mt-2 rounded-lg bg-primary px-3 py-1.5 font-semibold text-white hover:bg-blue-700"
                    >
                      Find routes between A and B
                    </button>
                  </>
                ) : nearby.state.status === 'loading' || nearbyDestination.state.status === 'loading' ? (
                  <p className="text-slate-500">Looking for the nearest stops...</p>
                ) : (
                  <p className="text-slate-600">
                    There is no stop within 2 km of {originStop ? 'B' : 'A'}, so routes cannot be searched from these points.
                  </p>
                )}
              </div>
            )}
            {(origin || destination) && (
              <button type="button" onClick={clearTrip} className="mt-2 text-sm font-medium text-primary hover:underline">
                Clear origin and destination
              </button>
            )}

            {origin && (
              <div className="mt-4 border-t border-slate-200 pt-3">
                <h3 className="text-sm font-semibold">Stops within 2 km of the origin</h3>
                {nearby.state.status === 'loading' && <p className="mt-1 text-sm text-slate-500">Searching...</p>}
                {nearby.state.status === 'error' && <p className="mt-1 text-sm text-danger">{nearby.state.message}</p>}
                {nearby.state.status === 'success' && nearby.state.data.length === 0 && (
                  <p className="mt-1 text-sm text-slate-500">No stops nearby.</p>
                )}
                {nearby.state.status === 'success' && nearby.state.data.length > 0 && (
                  <ul className="mt-1 space-y-1">
                    {nearby.state.data.map((item) => (
                      <li key={item.stop.id}>
                        <button
                          type="button"
                          onClick={() => setFlyTarget({ latitude: item.stop.latitude, longitude: item.stop.longitude })}
                          className="flex w-full justify-between gap-2 rounded px-2 py-1 text-left text-sm hover:bg-slate-100"
                        >
                          <span>{item.stop.name}</span>
                          <span className="text-slate-500">{item.distanceKm.toFixed(2)} km</span>
                        </button>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            )}
          </div>

          <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
            <h2 className="text-sm font-semibold">Routes ({visibleRoutes.length})</h2>
            {visibleRoutes.length === 0 ? (
              <p className="mt-2 text-sm text-slate-500">No routes match the filters.</p>
            ) : (
              <ul className="mt-2 max-h-64 space-y-1 overflow-y-auto">
                {visibleRoutes.map((route) => (
                  <li key={route.id}>
                    <button
                      type="button"
                      onClick={() => setSelectedRouteId(route.id === selectedRouteId ? null : route.id)}
                      className={`flex w-full items-center gap-2 rounded px-2 py-1.5 text-left text-sm ${
                        route.id === selectedRoute?.id ? 'bg-primary/10 font-semibold' : 'hover:bg-slate-100'
                      }`}
                    >
                      <span
                        className="h-3 w-3 shrink-0 rounded-full"
                        style={{ backgroundColor: colorForType(route.transportation.type) }}
                      />
                      <span className="flex-1">{route.routeName}</span>
                      {route.status !== 'ACTIVE' && <span className="text-xs text-slate-500">{route.status.toLowerCase()}</span>}
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </aside>

        {/* ---------- map and selected route ---------- */}
        <div className="space-y-4">
          {/* "isolate" keeps the map's controls below the sticky navigation bar */}
          <div className="isolate h-[60vh] min-h-[420px] overflow-hidden rounded-xl border border-slate-200 shadow-sm">
            <MapView
              routes={visibleRoutes}
              allRoutes={allRoutes}
              stops={visibleStops}
              selectedRoute={selectedRoute}
              highlightedRouteIds={highlightedIds}
              onSelectRoute={setSelectedRouteId}
              focusBounds={focusBounds}
              flyTarget={flyTarget}
              origin={origin}
              destination={destination}
              pickMode={pickMode}
              onPick={handlePick}
              onStopAsTripPoint={handleStopAsTripPoint}
            />
          </div>

          {selectedRoute ? (
            <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
              <RouteSummary route={selectedRoute} />
              <button
                type="button"
                onClick={() => setSelectedRouteId(null)}
                className="mt-3 text-sm font-medium text-slate-600 hover:underline"
              >
                Clear selection
              </button>
            </div>
          ) : (
            <p className="text-sm text-slate-500">
              Select a route to see its details here.{' '}
              <Link to="/routes" className="text-primary underline">
                Browse the route list
              </Link>
            </p>
          )}
        </div>
      </div>
    </section>
  )
}
