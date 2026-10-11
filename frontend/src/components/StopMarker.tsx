import { CircleMarker, Tooltip } from 'react-leaflet'
import type { Stop } from '../types/Stop'

interface StopMarkerProps {
  stop: Stop
  selected: boolean
  onTap: (stop: Stop) => void
}

/** A stop whose name contains "terminal" (any case) is drawn as a terminal. */
function isTerminal(stop: Stop): boolean {
  return stop.name.toLowerCase().includes('terminal')
}

/**
 * A transportation stop or terminal on the map. Tap it to see the routes that leave from it.
 * Terminals are bigger and green, and their name is always shown next to them.
 */
export default function StopMarker({ stop, selected, onTap }: StopMarkerProps) {
  const terminal = isTerminal(stop)
  const normalFill = terminal ? '#16a34a' : '#ffffff'

  return (
    <CircleMarker
      center={[stop.latitude, stop.longitude]}
      radius={terminal ? (selected ? 13 : 11) : selected ? 10 : 7}
      // bubblingMouseEvents: false = this click is not ALSO treated as a tap on the road below
      pathOptions={{
        color: terminal ? '#ffffff' : '#0f172a',
        weight: terminal ? 3 : 2,
        fillColor: selected ? '#2563eb' : normalFill,
        fillOpacity: 1,
        bubblingMouseEvents: false,
      }}
      eventHandlers={{ click: () => onTap(stop) }}
    >
      {terminal ? (
        // "permanent" = always visible, not only on hover
        <Tooltip permanent direction="right" offset={[12, 0]} className="th-terminal-label">
          {stop.name}
        </Tooltip>
      ) : (
        <Tooltip>{stop.name}</Tooltip>
      )}
    </CircleMarker>
  )
}