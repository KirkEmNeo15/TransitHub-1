// @vitest-environment node
import { readFileSync } from 'node:fs'
import { describe, expect, it } from 'vitest'
import type { LatLng, PolygonRings } from './boundary'
import { cityLabelSize, isInsideBoundary, parseBoundary, pointInPolygon } from './boundary'

// the real file the map loads
const raw: unknown = JSON.parse(
  readFileSync(new URL('../../public/data/batangas-city-boundary.geojson', import.meta.url), 'utf8'),
)
const city = parseBoundary(raw)

describe('the Batangas City boundary file', () => {
  it('is the city outline from PSGC data (main area plus an island)', () => {
    expect(city.name).toBe('Batangas City')
    expect(city.polygons.length).toBe(2)
    expect(city.source).toContain('PSGC')
  })

  it('has bounds around Batangas City', () => {
    const [[south, west], [north, east]] = city.bounds
    expect(south).toBeCloseTo(13.526, 2)
    expect(north).toBeCloseTo(13.843, 2)
    expect(west).toBeCloseTo(121.029, 2)
    expect(east).toBeCloseTo(121.18, 2)
  })

  it('contains the verified Batangas Grand Terminal location', () => {
    expect(isInsideBoundary(13.790168, 121.061538, city)).toBe(true)
  })

  it('does not contain places in other towns', () => {
    expect(isInsideBoundary(13.938, 121.162, city)).toBe(false) // Lipa City
    expect(isInsideBoundary(13.8186, 121.1318, city)).toBe(false) // Ibaan
    expect(isInsideBoundary(14.0859, 121.1498, city)).toBe(false) // Tanauan
  })

  it('places the city name inside the city', () => {
    expect(isInsideBoundary(city.labelPoint[0], city.labelPoint[1], city)).toBe(true)
  })
})

describe('pointInPolygon', () => {
  const square: LatLng[] = [
    [0, 0],
    [0, 10],
    [10, 10],
    [10, 0],
    [0, 0],
  ]
  const hole: LatLng[] = [
    [4, 4],
    [4, 6],
    [6, 6],
    [6, 4],
    [4, 4],
  ]
  const withHole: PolygonRings = [square, hole]

  it('finds points inside and outside the outline', () => {
    expect(pointInPolygon([2, 2], [square])).toBe(true)
    expect(pointInPolygon([12, 2], [square])).toBe(false)
  })

  it('treats a hole as outside', () => {
    expect(pointInPolygon([5, 5], withHole)).toBe(false)
    expect(pointInPolygon([2, 2], withHole)).toBe(true)
  })
})

describe('parseBoundary', () => {
  it('rejects files that are not a polygon boundary', () => {
    expect(() => parseBoundary(null)).toThrow()
    expect(() => parseBoundary({ type: 'FeatureCollection', features: [] })).toThrow()
    expect(() =>
      parseBoundary({ type: 'FeatureCollection', features: [{ geometry: { type: 'Point', coordinates: [121, 13] } }] }),
    ).toThrow()
  })

  it('swaps GeoJSON [longitude, latitude] into [latitude, longitude]', () => {
    const parsed = parseBoundary({
      type: 'FeatureCollection',
      features: [
        {
          properties: { name: 'Test' },
          geometry: { type: 'Polygon', coordinates: [[[121, 13], [122, 13], [122, 14], [121, 14], [121, 13]]] },
        },
      ],
    })
    expect(parsed.polygons[0][0][1]).toEqual([13, 122])
    expect(parsed.bounds).toEqual([
      [13, 121],
      [14, 122],
    ])
  })
})

describe('cityLabelSize', () => {
  it('hides the name far out and grows it when zooming in', () => {
    expect(cityLabelSize(9)).toBe(0)
    expect(cityLabelSize(11)).toBeLessThan(cityLabelSize(12))
    expect(cityLabelSize(16)).toBe(22)
  })
})