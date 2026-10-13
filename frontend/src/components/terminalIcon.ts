import { divIcon } from 'leaflet'

// A round, glowing terminal pin with a bus symbol, drawn with HTML and CSS
// (classes th-terminal-pin in index.css). No image files are needed.
const BUS_SYMBOL =
  '<svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true" fill="none" stroke="currentColor" ' +
  'stroke-width="2" stroke-linecap="round" stroke-linejoin="round">' +
  '<rect x="5" y="3" width="14" height="14" rx="3"/><path d="M5 11h14"/><path d="M8 20v-3M16 20v-3"/>' +
  '<circle cx="8.5" cy="14" r=".6" fill="currentColor"/><circle cx="15.5" cy="14" r=".6" fill="currentColor"/></svg>'

function terminalIcon(selected: boolean) {
  const size = selected ? 44 : 36
  return divIcon({
    className: '',
    html: `<div class="th-terminal-pin${selected ? ' th-terminal-pin--selected' : ''}" style="width:${size}px;height:${size}px">${BUS_SYMBOL}</div>`,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2], // the middle of the pin sits on the coordinate
    tooltipAnchor: [size / 2, 0], // the name label starts at the right edge of the pin
  })
}

// created once, so React does not rebuild them on every render
export const terminalPin = terminalIcon(false)
export const selectedTerminalPin = terminalIcon(true)