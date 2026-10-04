import { CircleMarker, Popup, Tooltip } from 'react-leaflet'
import { Link } from 'react-router'
import type { Route } from '../types/Route'
import type { Stop } from '../types/Stop'
import type { PickMode } from '../types/Trip'
import { formatCoordinates } from '../utils/geo'
import StatusBadge from './StatusBadge'
import TypeBadge from './TypeBadge'

interface StopMarkerProps {
  stop: Stop
  // routes passing through this stop
  routes: Route[]
  onUseAsTripPoint: (mode: PickMode, stop: Stop) => void
}

/** A transportation stop on the map. Click it to see its location and the routes that pass through it. */
export default function StopMarker({ stop, routes, onUseAsTripPoint }: StopMarkerProps) {
  return (
    <CircleMarker
      center={[stop.latitude, stop.longitude]}
      radius={7}
      pathOptions={{ color: '#0f172a', weight: 2, fillColor: '#ffffff', fillOpacity: 1 }}
    >
      <Tooltip>{stop.name}</Tooltip>
      <Popup>
        <div className="min-w-[220px] space-y-2 text-sm">
          <p className="text-base font-semibold">{stop.name}</p>
          <p className="text-slate-600">
            Location: {formatCoordinates(stop.latitude, stop.longitude)}
            {stop.description && <span className="block">{stop.description}</span>}
          </p>

          <div>
            <p className="font-medium">Routes passing through this stop</p>
            {routes.length === 0 ? (
              <p className="text-slate-500">None</p>
            ) : (
              <ul className="mt-1 space-y-1">
                {routes.map((route) => (
                  <li key={route.id} className="flex flex-wrap items-center gap-2">
                    <TypeBadge type={route.transportation.type} />
                    <Link to={`/routes/${route.id}`} className="text-primary hover:underline">
                      {route.routeName}
                    </Link>
                    <StatusBadge status={route.status} />
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="flex gap-2 pt-1">
            <button
              type="button"
              onClick={() => onUseAsTripPoint('origin', stop)}
              className="rounded bg-green-600 px-2 py-1 text-xs font-semibold text-white hover:bg-green-700"
            >
              Set as origin
            </button>
            <button
              type="button"
              onClick={() => onUseAsTripPoint('destination', stop)}
              className="rounded bg-red-600 px-2 py-1 text-xs font-semibold text-white hover:bg-red-700"
            >
              Set as destination
            </button>
          </div>
        </div>
      </Popup>
    </CircleMarker>
  )
}
