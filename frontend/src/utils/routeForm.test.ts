import { describe, expect, it } from 'vitest'
import { makeRoute } from '../test/fixtures'
import { buildRouteRequest, emptyRouteForm, routeToForm, stopsChanged } from './routeForm'

describe('empty form', () => {
  it('reports every missing required field', () => {
    const result = buildRouteRequest(emptyRouteForm(), null)
    expect(result.request).toBeNull()
    for (const field of ['routeCode', 'routeName', 'origin', 'destination', 'estimatedMinutes', 'distanceKm', 'transportationId', 'baseFare', 'perKmRate', 'stops']) {
      expect(result.errors[field]).toBeTruthy()
    }
  })
})

describe('editing an existing route', () => {
  it('turns a route into a form and back into the same request values', () => {
    const route = makeRoute()
    const result = buildRouteRequest(routeToForm(route), route)
    expect(result.errors).toEqual({})
    expect(result.request?.routeCode).toBe('T-01')
    expect(result.request?.transportationId).toBe(3)
    expect(result.request?.fare).toEqual({ baseFare: 13, perKmRate: 2 })
    expect(result.request?.stops).toEqual([
      { stopId: 1, minutesFromStart: 0 },
      { stopId: 2, minutesFromStart: 10 },
      { stopId: 3, minutesFromStart: 25 },
    ])
    expect(result.request?.schedules).toEqual([{ firstTrip: '05:00', lastTrip: '21:30', frequencyMinutes: 15, daysOperating: 'MON-SUN' }])
  })

  it('keeps the stored map line when the stops did not change', () => {
    const route = makeRoute()
    const result = buildRouteRequest(routeToForm(route), route)
    expect(result.request?.path).toEqual(route.path)
    expect(result.lineWillBeRedrawn).toBe(false)
    expect(stopsChanged(routeToForm(route), route)).toBe(false)
  })

  it('sends no line when the stops changed, so the backend redraws it', () => {
    const route = makeRoute()
    const form = routeToForm(route)
    form.stops[1].stopId = '9'
    const result = buildRouteRequest(form, route)
    expect(result.request?.path).toEqual([])
    expect(result.lineWillBeRedrawn).toBe(true)
    expect(stopsChanged(form, route)).toBe(true)
  })

  it('counts a different stop order as a change', () => {
    const route = makeRoute()
    const form = routeToForm(route)
    form.stops = [form.stops[1], form.stops[0], form.stops[2]]
    expect(stopsChanged(form, route)).toBe(true)
  })
})

describe('validation rules', () => {
  const valid = () => routeToForm(makeRoute())

  it('rejects the same origin and destination', () => {
    const form = valid()
    form.destination = ' alpha '
    expect(buildRouteRequest(form, null).errors.destination).toBeTruthy()
  })
  it('rejects fewer than two stops and repeated stops', () => {
    const one = valid()
    one.stops = one.stops.slice(0, 1)
    expect(buildRouteRequest(one, null).errors.stops).toContain('at least 2')

    const repeated = valid()
    repeated.stops[1].stopId = repeated.stops[0].stopId
    expect(buildRouteRequest(repeated, null).errors.stops).toContain('twice')
  })
  it('rejects a stop row that was left empty', () => {
    const form = valid()
    form.stops[2].stopId = ''
    expect(buildRouteRequest(form, null).errors.stops).toContain('Choose a stop')
  })
  it('rejects bad numbers', () => {
    const form = valid()
    form.estimatedMinutes = '0'
    form.distanceKm = '-3'
    form.baseFare = '12.345'
    form.perKmRate = 'abc'
    const errors = buildRouteRequest(form, null).errors
    expect(errors.estimatedMinutes).toBeTruthy()
    expect(errors.distanceKm).toBeTruthy()
    expect(errors.baseFare).toBeTruthy()
    expect(errors.perKmRate).toBeTruthy()
  })
  it('rejects a schedule whose first trip is not before the last trip', () => {
    const form = valid()
    form.schedules[0].firstTrip = '22:00'
    expect(buildRouteRequest(form, null).errors.schedules).toBeTruthy()
  })
  it('accepts a route without schedules', () => {
    const form = valid()
    form.schedules = []
    expect(buildRouteRequest(form, null).request?.schedules).toEqual([])
  })
})
