import { describe, expect, it } from 'vitest'
import { colorForType, DEFAULT_ROUTE_COLOR, TRANSPORT_COLORS } from './transportColors'

describe('colorForType', () => {
  it('gives each type its own color', () => {
    const colors = [colorForType('Bus'), colorForType('Jeepney'), colorForType('Van')]
    expect(new Set(colors).size).toBe(3)
    expect(colorForType('Bus')).toBe(TRANSPORT_COLORS.Bus)
  })
  it('uses the default color for an unknown type', () => {
    expect(colorForType('Ferry')).toBe(DEFAULT_ROUTE_COLOR)
  })
})
