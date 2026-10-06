import { useEffect } from 'react'
import type { LatLngTuple } from 'leaflet'
import {
  CircleMarker,
  MapContainer,
  Marker,
  Polyline,
  Popup,
  TileLayer,
  Tooltip,
  useMap,
  useMapEvents,
} from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import type { Route } from '../types/Route'
import type { Stop } from '../types/Stop'
import type { PickMode, TripPoint } from '../types/Trip'
import { destinationIcon, originIcon } from '../utils/mapIcons'
import { colorForType } from '../utils/transportColors'
import RouteSummary from './RouteSummary'
import StopMarker from './StopMarker'

export interface FlyTarget {
  latitude: number
  longitude: number
}

interface MapViewProps {
  routes: Route[] // the routes to draw
  allRoutes: Route[] // every route, used by the stop popups
  stops: Stop[] // the stops to draw
  selectedRoute: Route | null
  // routes found by a search (the others are dimmed); null when no search is active
  highlightedRouteIds: Set<number> | null
  onSelectRoute: (routeId: number) => void
  focusBounds: LatLngTuple[] | null // the map zooms to show these points
  flyTarget: FlyTarget | null // the map moves to this point
  origin: TripPoint | null
  destination: TripPoint | null
  pickMode: PickMode | null
  onPick: (mode: PickMode, latitude: number, longitude: number) => void
  onStopAsTripPoint: (mode: PickMode, stop: Stop) => void
}

const DEFAULT_CENTER: LatLngTuple = [13.94, 121.16] // Lipa City
const DEFAULT_ZOOM = 11

/** Zooms the map to show a set of points whenever that set changes. */
function FitBounds({ bounds }: { bounds: LatLngTuple[] | null }) {
  const map = useMap()
  useEffect(() => {
    if (bounds && bounds.length > 0) {
      map.fitBounds(bounds, { padding: [40, 40] })
    }
  }, [map, bounds])
  return null
}

/** Moves the map to a point (used when the user clicks a nearby stop in the side panel). */
function FlyTo({ target }: { target: FlyTarget | null }) {
  const map = useMap()
  useEffect(() => {
    if (target) {
      map.flyTo([target.latitude, target.longitude], 16)
    }
  }, [map, target])
  return null
}

/** While the user is choosing an origin or destination, a click on the map picks that point. */
function PickHandler({ mode, onPick }: { mode: PickMode | null; onPick: MapViewProps['onPick'] }) {
  useMapEvents({
    click(event) {
      if (mode) {
        onPick(mode, event.latlng.lat, event.latlng.lng)
      }
    },
  })
  return null
}

export default function MapView({
  routes,
  allRoutes,
  stops,
  selectedRoute,
  highlightedRouteIds,
  onSelectRoute,
  focusBounds,
  flyTarget,
  origin,
  destination,
  pickMode,
  onPick,
  onStopAsTripPoint,
}: MapViewProps) {
  // Drawing order: dimmed routes first, then search results, and the selected route last (on top).
  const importance = (route: Route) =>
    route.id === selectedRoute?.id ? 2 : highlightedRouteIds === null || highlightedRouteIds.has(route.id) ? 1 : 0
  const drawOrder = [...routes].sort((a, b) => importance(a) - importance(b))

  return (
    <MapContainer center={DEFAULT_CENTER} zoom={DEFAULT_ZOOM} scrollWheelZoom className="h-full w-full">
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <FitBounds bounds={focusBounds} />
      <FlyTo target={flyTarget} />
      <PickHandler mode={pickMode} onPick={onPick} />

      {/* route lines */}
      {drawOrder
        .filter((route) => route.path.length >= 2)
        .map((route) => {
          const selected = route.id === selectedRoute?.id
          const dimmed = !selected && highlightedRouteIds !== null && !highlightedRouteIds.has(route.id)
          return (
            <Polyline
              key={route.id}
              positions={route.path.map((point): LatLngTuple => [point.latitude, point.longitude])}
              pathOptions={{
                color: colorForType(route.transportation.type),
                weight: selected ? 8 : dimmed ? 3 : 5,
                opacity: selected ? 1 : dimmed ? 0.2 : 0.8,
                // inactive and suspended routes are dashed
                dashArray: route.status === 'ACTIVE' ? undefined : '8 10',
              }}
              eventHandlers={{ click: () => onSelectRoute(route.id) }}
            >
              <Tooltip sticky>{route.routeName}</Tooltip>
              <Popup maxWidth={340}>
                <RouteSummary route={route} />
              </Popup>
            </Polyline>
          )
        })}

      {/* start and end of the selected route */}
      {selectedRoute && selectedRoute.stops.length >= 2 && (
        <>
          <CircleMarker
            center={[selectedRoute.stops[0].stop.latitude, selectedRoute.stops[0].stop.longitude]}
            radius={11}
            pathOptions={{ color: '#16a34a', weight: 4, fill: false }}
            interactive={false}
          />
          <CircleMarker
            center={[
              selectedRoute.stops[selectedRoute.stops.length - 1].stop.latitude,
              selectedRoute.stops[selectedRoute.stops.length - 1].stop.longitude,
            ]}
            radius={11}
            pathOptions={{ color: '#dc2626', weight: 4, fill: false }}
            interactive={false}
          />
        </>
      )}

      {/* stops */}
      {stops.map((stop) => (
        <StopMarker
          key={stop.id}
          stop={stop}
          routes={allRoutes.filter((route) => route.stops.some((routeStop) => routeStop.stop.id === stop.id))}
          onUseAsTripPoint={onStopAsTripPoint}
        />
      ))}

      {/* the user's origin and destination */}
      {origin && (
        <Marker position={[origin.latitude, origin.longitude]} icon={originIcon}>
          <Tooltip>Origin: {origin.label}</Tooltip>
        </Marker>
      )}
      {destination && (
        <Marker position={[destination.latitude, destination.longitude]} icon={destinationIcon}>
          <Tooltip>Destination: {destination.label}</Tooltip>
        </Marker>
      )}
    </MapContainer>
  )
}
