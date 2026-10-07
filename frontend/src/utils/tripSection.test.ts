import { describe, expect, it } from 'vitest'
import { makeRoute } from '../test/fixtures'
import { findTripSection } from './tripSection'

describe('findTripSection', () => {
  const route = makeRoute()

  it('finds where to get on and off, and the riding time', () => {
    expect(findTripSection(route, 'alpha', 'gamma')).toEqual({ boardIndex: 0, alightIndex: 2, minutes: 25 })
    expect(findTripSection(route, 'Beta', 'Gamma')).toEqual({ boardIndex: 1, alightIndex: 2, minutes: 15 })
  })
  it('ignores upper and lower case and extra spaces', () => {
    expect(findTripSection(route, '  ALPHA ', 'beta')?.minutes).toBe(10)
  })
  it('finds nothing when the route goes the other way', () => {
    expect(findTripSection(route, 'Gamma', 'Alpha')).toBeNull()
  })
  it('finds nothing for unknown places or empty text', () => {
    expect(findTripSection(route, 'Nowhere', 'Gamma')).toBeNull()
    expect(findTripSection(route, '', 'Gamma')).toBeNull()
    expect(findTripSection(route, 'Alpha', '  ')).toBeNull()
  })
})
