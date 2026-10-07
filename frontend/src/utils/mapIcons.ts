import { divIcon } from 'leaflet'

// Markers made of plain HTML, so no image files are needed (Leaflet's default marker images
// do not load correctly with Vite).
function labelIcon(label: string, color: string) {
  return divIcon({
    className: '',
    html: `<div style="width:30px;height:30px;border-radius:50%;background:${color};color:white;
      font:700 14px system-ui,sans-serif;display:flex;align-items:center;justify-content:center;
      border:3px solid white;box-shadow:0 1px 4px rgba(0,0,0,.5)">${label}</div>`,
    iconSize: [30, 30],
    iconAnchor: [15, 15],
  })
}

// created once, so React does not rebuild them on every render
export const originIcon = labelIcon('A', '#16a34a')
export const destinationIcon = labelIcon('B', '#dc2626')
