import { describe, expect, it } from 'vitest'
import { arrowMarks, arrowSpacingForZoom, routeLineWidth } from './routeArrows'

// screen coordinates: x to the right, y DOWN
describe('arrowSpacingForZoom (level of detail)', () => {
  it('shows no arrows when zoomed far out', () => {
    expect(arrowSpacingForZoom(10)).toBeNull()
    expect(arrowSpacingForZoom(12)).toBeNull()
  })
  it('puts arrows closer together as you zoom in', () => {
    expect(arrowSpacingForZoom(13)).toBe(240)
    expect(arrowSpacingForZoom(15)).toBe(140)
    expect(arrowSpacingForZoom(18)).toBe(110)
  })
})
describe('routeLineWidth (level of detail)', () => {
  it('is thin zoomed out and a little wider up close', () => {
    expect(routeLineWidth(11)).toBe(3)
    expect(routeLineWidth(14)).toBe(4)
    expect(routeLineWidth(17)).toBe(5)
  })
})

describe('arrowMarks', () => {
  it('places arrows at even spacing, pointing along the line', () => {
    const marks = arrowMarks([{ x: 0, y: 0 }, { x: 300, y: 0 }], 100)
    expect(marks.map((m) => m.x)).toEqual([50, 150, 250])
    expect(marks.every((m) => m.angle === 0)).toBe(true)
  })

  it('points down (90 degrees) on a line going down the screen', () => {
    expect(arrowMarks([{ x: 0, y: 0 }, { x: 0, y: 200 }], 100)[0].angle).toBe(90)
  })

  it('continues the spacing around corners', () => {
    const marks = arrowMarks([{ x: 0, y: 0 }, { x: 80, y: 0 }, { x: 80, y: 200 }], 100)
    expect(marks.map((m) => [m.x, m.y])).toEqual([
      [50, 0],
      [80, 70],
      [80, 170],
    ])
  })
})