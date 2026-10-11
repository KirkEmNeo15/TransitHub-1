// Helpers for the Batangas City boundary (public/data/batangas-city-boundary.geojson).
// Pure functions without Leaflet, so they are easy to test.

/** [latitude, longitude], the order Leaflet uses. GeoJSON files store [longitude, latitude]. */
export type LatLng = [number, number]

/** polygons -> rings -> points. The first ring of a polygon is its outline, the others are holes. */
export type PolygonRings = LatLng[][]

export interface CityBoundary {
  name: string
  source: string
  polygons: PolygonRings[]
  bounds: [LatLng, LatLng] // [south-west, north-east]
  labelPoint: LatLng // where the city name is written
}

interface RawGeometry {
  type?: unknown
  coordinates?: unknown
}

function isPosition(value: unknown): value is [number, number] {
  return Array.isArray(value) && value.length >= 2 && typeof value[0] === 'number' && typeof value[1] === 'number'
}

function toRing(raw: unknown): LatLng[] {
  if (!Array.isArray(raw) || raw.length < 4 || !raw.every(isPosition)) {
    throw new Error('A boundary ring must have at least 4 [longitude, latitude] points')
  }
  return raw.map(([longitude, latitude]) => [latitude, longitude])
}

function toPolygon(raw: unknown): PolygonRings {
  if (!Array.isArray(raw) || raw.length === 0) throw new Error('A boundary polygon has no rings')
  return raw.map(toRing)
}

/** Checks a GeoJSON FeatureCollection (one Polygon or MultiPolygon feature) and converts it. */
export function parseBoundary(json: unknown): CityBoundary {
  const collection = json as { type?: unknown; features?: unknown }
  if (collection?.type !== 'FeatureCollection' || !Array.isArray(collection.features) || collection.features.length === 0) {
    throw new Error('The boundary file is not a GeoJSON FeatureCollection')
  }
  const feature = collection.features[0] as { properties?: Record<string, unknown>; geometry?: RawGeometry }
  const geometry = feature.geometry
  let polygons: PolygonRings[]
  if (geometry?.type === 'Polygon') polygons = [toPolygon(geometry.coordinates)]
  else if (geometry?.type === 'MultiPolygon' && Array.isArray(geometry.coordinates)) {
    polygons = geometry.coordinates.map(toPolygon)
  } else {
    throw new Error('The boundary must be a Polygon or MultiPolygon')
  }

  const points = polygons.flatMap((polygon) => polygon[0])
  const latitudes = points.map((point) => point[0])
  const longitudes = points.map((point) => point[1])
  const properties = feature.properties ?? {}

  return {
    name: typeof properties.name === 'string' ? properties.name : 'City',
    source: typeof properties.source === 'string' ? properties.source : '',
    polygons,
    bounds: [
      [Math.min(...latitudes), Math.min(...longitudes)],
      [Math.max(...latitudes), Math.max(...longitudes)],
    ],
    labelPoint: labelPointOf(largestPolygon(polygons)),
  }
}

/** Ray casting: is the point inside the ring? (points exactly on the edge may go either way) */
export function pointInRing(point: LatLng, ring: LatLng[]): boolean {
  const [y, x] = point
  let inside = false
  for (let i = 0, j = ring.length - 1; i < ring.length; j = i++) {
    const [yi, xi] = ring[i]
    const [yj, xj] = ring[j]
    if (yi > y !== yj > y && x < ((xj - xi) * (y - yi)) / (yj - yi) + xi) inside = !inside
  }
  return inside
}

export function pointInPolygon(point: LatLng, polygon: PolygonRings): boolean {
  const [outline, ...holes] = polygon
  return pointInRing(point, outline) && !holes.some((hole) => pointInRing(point, hole))
}

/** True when the point is inside the city (inside any of its polygons, for example an island). */
export function isInsideBoundary(latitude: number, longitude: number, boundary: CityBoundary): boolean {
  return boundary.polygons.some((polygon) => pointInPolygon([latitude, longitude], polygon))
}

/** Area in "square degrees" (shoelace formula); only used to compare polygons with each other. */
function ringArea(ring: LatLng[]): number {
  let sum = 0
  for (let i = 0, j = ring.length - 1; i < ring.length; j = i++) {
    sum += ring[j][1] * ring[i][0] - ring[i][1] * ring[j][0]
  }
  return Math.abs(sum) / 2
}

function largestPolygon(polygons: PolygonRings[]): PolygonRings {
  return polygons.reduce((best, polygon) => (ringArea(polygon[0]) > ringArea(best[0]) ? polygon : best))
}

/**
 * A good place for the city name: the center of the main area. If that center falls outside an
 * irregular outline, use the middle of the widest inside stretch on the same latitude instead.
 */
export function labelPointOf(polygon: PolygonRings): LatLng {
  const ring = polygon[0]
  let area = 0
  let cx = 0
  let cy = 0
  for (let i = 0, j = ring.length - 1; i < ring.length; j = i++) {
    const cross = ring[j][1] * ring[i][0] - ring[i][1] * ring[j][0]
    area += cross
    cx += (ring[j][1] + ring[i][1]) * cross
    cy += (ring[j][0] + ring[i][0]) * cross
  }
  const centroid: LatLng = area === 0 ? ring[0] : [cy / (3 * area), cx / (3 * area)]
  if (pointInPolygon(centroid, polygon)) return centroid

  const latitude = centroid[0]
  const crossings: number[] = []
  for (let i = 0, j = ring.length - 1; i < ring.length; j = i++) {
    const [yi, xi] = ring[i]
    const [yj, xj] = ring[j]
    if (yi > latitude !== yj > latitude) crossings.push(((xj - xi) * (latitude - yi)) / (yj - yi) + xi)
  }
  crossings.sort((a, b) => a - b)
  let best: LatLng = centroid
  let widest = -1
  for (let k = 0; k + 1 < crossings.length; k += 2) {
    const width = crossings[k + 1] - crossings[k]
    if (width > widest) {
      widest = width
      best = [latitude, (crossings[k] + crossings[k + 1]) / 2]
    }
  }
  return best
}

/** Font size (px) of the city name for a zoom level; 0 means "do not show it" (zoomed far out). */
export function cityLabelSize(zoom: number): number {
  if (zoom < 10) return 0
  if (zoom === 10) return 11
  if (zoom === 11) return 14
  if (zoom === 12) return 18
  return 22
}