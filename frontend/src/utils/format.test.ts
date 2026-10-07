import { describe, expect, it } from 'vitest'
import { makeRoute } from '../test/fixtures'
import { describeDetails, describeSchedule, formatDistance, formatDuration, formatPeso, formatTime, operatingHours } from './format'

describe('formatTime', () => {
  it('turns 24-hour times into 12-hour times', () => {
    expect(formatTime('05:00:00')).toBe('5:00 AM')
    expect(formatTime('13:05:00')).toBe('1:05 PM')
  })
  it('handles midnight and noon', () => {
    expect(formatTime('00:00:00')).toBe('12:00 AM')
    expect(formatTime('12:00:00')).toBe('12:00 PM')
  })
})

describe('formatDuration', () => {
  it('uses minutes below one hour', () => {
    expect(formatDuration(45)).toBe('45 min')
  })
  it('uses hours and minutes from one hour', () => {
    expect(formatDuration(60)).toBe('1 h')
    expect(formatDuration(90)).toBe('1 h 30 min')
  })
})

describe('formatDistance and formatPeso', () => {
  it('shows one decimal for kilometers', () => {
    expect(formatDistance(8.46)).toBe('8.5 km')
  })
  it('shows pesos with two decimals, and N/A when there is no value', () => {
    expect(formatPeso(35.1)).toContain('35.10')
    expect(formatPeso(null)).toBe('N/A')
    expect(formatPeso(undefined)).toBe('N/A')
  })
})

describe('schedules', () => {
  it('shows the operating hours of the first schedule', () => {
    expect(operatingHours(makeRoute().schedules)).toBe('5:00 AM - 9:30 PM')
  })
  it('shows N/A when there is no schedule', () => {
    expect(operatingHours([])).toBe('N/A')
  })
  it('describes a schedule in one line', () => {
    expect(describeSchedule(makeRoute().schedules[0])).toBe('5:00 AM - 9:30 PM, every 15 min, MON-SUN')
  })
})

describe('describeDetails', () => {
  it('describes each type with its own details', () => {
    const bus = makeRoute().transportation
    expect(describeDetails(bus)).toEqual(['Air-conditioned'])
    expect(describeDetails({ ...bus, type: 'Jeepney', details: { modernized: false } })).toEqual(['Traditional jeepney'])
    expect(describeDetails({ ...bus, type: 'Van', details: { seatingCapacity: 14 } })).toEqual(['14 seats'])
  })
  it('returns nothing when there are no details', () => {
    expect(describeDetails({ ...makeRoute().transportation, details: {} })).toEqual([])
  })
})
