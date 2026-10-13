// Direction arrows on the selected route, with "level of detail" (LOD): the closer you zoom in,
// the more arrows; zoomed far out, none at all. Plain math in screen pixels (x right, y DOWN).

export interface Pt {
  x: number
  y: number
}

/** A direction arrow: its position and the angle it points to, in degrees (0 = right, 90 = down). */
export interface ArrowMark extends Pt {
  angle: number
}

const MAX_ARROWS = 400 // safety limit when zoomed in very close on a long route

/**
 * Level of detail: how many pixels apart the arrows are at a zoom level, or null for no arrows.
 * Zoomed out (whole city) the arrows would only clutter the map, so they appear from zoom 13.
 */
export function arrowSpacingForZoom(zoom: number): number | null {
  if (zoom < 13) return null
  if (zoom < 14) return 240
  if (zoom < 15) return 180
  if (zoom < 16) return 140
  return 110
}
/**
 * Level of detail for the route line itself: thin when zoomed out, a little wider up close
 * (in pixels, without the white edge). Navigation maps use about 4 to 5 pixels for a route.
 */
export function routeLineWidth(zoom: number): number {
  if (zoom < 13) return 3
  if (zoom < 15) return 4
  return 5
}
/** Arrows every `spacing` pixels along the line, the first one half a spacing from the start. */
export function arrowMarks(points: Pt[], spacing: number): ArrowMark[] {
  const marks: ArrowMark[] = []
  if (spacing <= 0) return marks
  let next = spacing / 2 // distance along the line of the next arrow
  let travelled = 0
  for (let i = 1; i < points.length && marks.length < MAX_ARROWS; i++) {
    const a = points[i - 1]
    const b = points[i]
    const length = Math.hypot(b.x - a.x, b.y - a.y)
    if (length === 0) continue
    const angle = (Math.atan2(b.y - a.y, b.x - a.x) * 180) / Math.PI
    while (next <= travelled + length && marks.length < MAX_ARROWS) {
      const t = (next - travelled) / length
      marks.push({ x: a.x + (b.x - a.x) * t, y: a.y + (b.y - a.y) * t, angle })
      next += spacing
    }
    travelled += length
  }
  return marks
}