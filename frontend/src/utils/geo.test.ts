import { describe, expect, it } from 'vitest'
import { distanceKm, formatCoordinates } from './geo'

describe('distanceKm', () => {
  it('is zero for the same point', () => {
    expect(distanceKm(13.94, 121.16, 13.94, 121.16)).toBe(0)
  })
  it('is the same in both directions', () => {
    expect(distanceKm(13.94, 121.16, 13.75, 121.05)).toBeCloseTo(distanceKm(13.75, 121.05, 13.94, 121.16), 9)
  })
  it('is about 111 km for one degree of latitude', () => {
    expect(distanceKm(13, 121, 14, 121)).toBeCloseTo(111.19, 1)
  })
})

describe('formatCoordinates', () => {
  it('shows 5 decimals', () => {
    expect(formatCoordinates(13.94112, 121.16312)).toBe('13.94112, 121.16312')
    expect(formatCoordinates(13.9, 121)).toBe('13.90000, 121.00000')
  })
})
