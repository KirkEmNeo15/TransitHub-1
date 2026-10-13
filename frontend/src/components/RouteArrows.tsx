import { useMemo, useState } from 'react'
import { divIcon } from 'leaflet'
import type { DivIcon, LatLngTuple } from 'leaflet'
import { Marker, useMap, useMapEvents } from 'react-leaflet'
import type { Pt } from '../utils/routeArrows'
import { arrowMarks, arrowSpacingForZoom } from '../utils/routeArrows'

// one arrow icon per angle (rounded to 5 degrees), created once and reused
const arrowIcons = new Map<number, DivIcon>()
function arrowIcon(angle: number): DivIcon {
  const rounded = Math.round(angle / 5) * 5
  let icon = arrowIcons.get(rounded)
  if (!icon) {
    icon = divIcon({
      className: '',
      // a chevron pointing right, turned to the direction of travel
      html:
        `<div class="th-route-arrow" style="transform:rotate(${rounded}deg)">` +
        '<svg viewBox="0 0 12 12" width="12" height="12" aria-hidden="true"><path d="M3 1.5 8.5 6 3 10.5" fill="none" ' +
        'stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg></div>',
      iconSize: [12, 12],
      iconAnchor: [6, 6],
    })
    arrowIcons.set(rounded, icon)
  }
  return icon
}

/**
 * Small arrows along a route line showing the driving direction. Level of detail: none when zoomed
 * far out, more of them as you zoom in (arrowSpacingForZoom). They never catch taps.
 */
export default function RouteArrows({ line }: { line: LatLngTuple[] }) {
  const map = useMap()
  const [zoom, setZoom] = useState(() => map.getZoom())
  useMapEvents({
    zoomend() {
      setZoom(map.getZoom())
    },
  })

  // The spacing is in screen pixels, so measure the line in pixels at this zoom, then turn the
  // arrow positions back into map coordinates. Done again only when the zoom or the line changes.
  const arrows = useMemo(() => {
    const spacing = arrowSpacingForZoom(zoom)
    if (spacing === null) return []
    const pixels: Pt[] = line.map((point) => map.project(point, zoom))
    return arrowMarks(pixels, spacing).map((mark) => {
      const latLng = map.unproject([mark.x, mark.y], zoom)
      return { position: [latLng.lat, latLng.lng] as LatLngTuple, angle: mark.angle }
    })
  }, [line, map, zoom])

  return (
    <>
      {arrows.map((arrow, index) => (
        <Marker key={index} position={arrow.position} icon={arrowIcon(arrow.angle)} interactive={false} keyboard={false} />
      ))}
    </>
  )
}