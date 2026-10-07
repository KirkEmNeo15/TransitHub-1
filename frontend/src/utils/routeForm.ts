import type { RouteRequest } from '../types/Admin'
import type { Route, RouteStatus } from '../types/Route'

// The route form keeps every value as TEXT (what the user typed), then this file checks it
// and turns it into the request the backend expects. Pure functions: easy to test.

export interface StopRow {
  key: number // only used by React to tell the rows apart
  stopId: string
  minutes: string
}

export interface ScheduleRow {
  key: number
  firstTrip: string // "05:00"
  lastTrip: string
  frequency: string
  days: string
}

export interface RouteFormState {
  routeCode: string
  routeName: string
  origin: string
  destination: string
  status: RouteStatus
  estimatedMinutes: string
  distanceKm: string
  transportationId: string
  baseFare: string
  perKmRate: string
  stops: StopRow[]
  schedules: ScheduleRow[]
}

let nextKey = 1
export function newKey(): number {
  return nextKey++
}

export function emptyRouteForm(): RouteFormState {
  return {
    routeCode: '',
    routeName: '',
    origin: '',
    destination: '',
    status: 'ACTIVE',
    estimatedMinutes: '',
    distanceKm: '',
    transportationId: '',
    baseFare: '',
    perKmRate: '',
    stops: [
      { key: newKey(), stopId: '', minutes: '0' },
      { key: newKey(), stopId: '', minutes: '' },
    ],
    schedules: [],
  }
}

export function routeToForm(route: Route): RouteFormState {
  return {
    routeCode: route.routeCode,
    routeName: route.routeName,
    origin: route.origin,
    destination: route.destination,
    status: route.status,
    estimatedMinutes: String(route.estimatedMinutes),
    distanceKm: String(route.distanceKm),
    transportationId: String(route.transportation.id),
    baseFare: route.fareRule ? String(route.fareRule.baseFare) : '',
    perKmRate: route.fareRule ? String(route.fareRule.perKmRate) : '',
    stops: route.stops.map((routeStop) => ({
      key: newKey(),
      stopId: String(routeStop.stop.id),
      minutes: String(routeStop.minutesFromStart),
    })),
    schedules: route.schedules.map((schedule) => ({
      key: newKey(),
      firstTrip: schedule.firstTrip.slice(0, 5), // "05:00:00" -> "05:00"
      lastTrip: schedule.lastTrip.slice(0, 5),
      frequency: String(schedule.frequencyMinutes),
      days: schedule.daysOperating,
    })),
  }
}

export interface RouteFormResult {
  // field name -> message. Keys: routeCode, routeName, origin, destination, estimatedMinutes,
  // distanceKm, transportationId, baseFare, perKmRate, stops, schedules
  errors: Record<string, string>
  request: RouteRequest | null
  // true when the stops changed on an existing route, so the map line is drawn again through the stops
  lineWillBeRedrawn: boolean
}

const WHOLE_NUMBER = /^\d+$/
const MONEY = /^\d{1,6}(\.\d{1,2})?$/
const DECIMAL = /^\d+(\.\d+)?$/

function sameOrder(a: number[], b: number[]): boolean {
  return a.length === b.length && a.every((value, index) => value === b[index])
}

/** Checks the form (the backend checks everything again) and builds the request. */
export function buildRouteRequest(state: RouteFormState, existing: Route | null): RouteFormResult {
  const errors: Record<string, string> = {}

  const text = (value: string, field: string, label: string) => {
    if (value.trim() === '') errors[field] = `${label} cannot be empty`
  }
  text(state.routeCode, 'routeCode', 'Route code')
  text(state.routeName, 'routeName', 'Route name')
  text(state.origin, 'origin', 'Origin')
  text(state.destination, 'destination', 'Destination')
  if (
    state.origin.trim() !== '' &&
    state.origin.trim().toLowerCase() === state.destination.trim().toLowerCase()
  ) {
    errors.destination = 'Origin and destination must be different'
  }

  const minutes = WHOLE_NUMBER.test(state.estimatedMinutes.trim()) ? Number(state.estimatedMinutes) : NaN
  if (!(minutes >= 1)) errors.estimatedMinutes = 'Enter the travel time as a whole number of minutes (1 or more)'

  const distance = DECIMAL.test(state.distanceKm.trim()) ? Number(state.distanceKm) : NaN
  if (!(distance > 0)) errors.distanceKm = 'Enter the distance in km (more than 0)'

  const transportationId = WHOLE_NUMBER.test(state.transportationId) ? Number(state.transportationId) : NaN
  if (Number.isNaN(transportationId)) errors.transportationId = 'Choose the transportation'

  const baseFare = MONEY.test(state.baseFare.trim()) ? Number(state.baseFare) : NaN
  if (Number.isNaN(baseFare)) errors.baseFare = 'Enter the base fare (0 or more, at most 2 decimals)'
  const perKmRate = MONEY.test(state.perKmRate.trim()) ? Number(state.perKmRate) : NaN
  if (Number.isNaN(perKmRate)) errors.perKmRate = 'Enter the rate per km (0 or more, at most 2 decimals)'

  // ----- stops -----
  const stopIds = state.stops.map((row) => (WHOLE_NUMBER.test(row.stopId) ? Number(row.stopId) : NaN))
  if (state.stops.length < 2) {
    errors.stops = 'A route needs at least 2 stops'
  } else if (stopIds.some((id) => Number.isNaN(id))) {
    errors.stops = 'Choose a stop in every row'
  } else if (new Set(stopIds).size !== stopIds.length) {
    errors.stops = 'The same stop cannot appear twice in one route'
  } else if (state.stops.some((row) => !WHOLE_NUMBER.test(row.minutes.trim()))) {
    errors.stops = 'Minutes from start must be a whole number (0 or more) in every row'
  }

  // ----- schedules (optional) -----
  for (const row of state.schedules) {
    if (row.firstTrip === '' || row.lastTrip === '') {
      errors.schedules = 'Fill in the first and last trip times of every schedule'
    } else if (row.firstTrip >= row.lastTrip) {
      errors.schedules = 'In every schedule the first trip must be earlier than the last trip'
    } else if (!WHOLE_NUMBER.test(row.frequency.trim()) || Number(row.frequency) < 1) {
      errors.schedules = 'The time between trips must be a whole number of minutes (1 or more)'
    } else if (row.days.trim() === '') {
      errors.schedules = 'Fill in the days of every schedule (for example MON-SUN)'
    }
  }

  if (Object.keys(errors).length > 0) {
    return { errors, request: null, lineWillBeRedrawn: false }
  }

  // If the stops are the same as before, keep the stored map line (it may follow real roads).
  // If the stops changed, the old line no longer fits: send no line and the backend draws it through the stops.
  const keepsLine = existing !== null && sameOrder(existing.stops.map((routeStop) => routeStop.stop.id), stopIds)
  const lineWillBeRedrawn = existing !== null && !keepsLine

  return {
    errors,
    lineWillBeRedrawn,
    request: {
      routeCode: state.routeCode.trim(),
      routeName: state.routeName.trim(),
      origin: state.origin.trim(),
      destination: state.destination.trim(),
      status: state.status,
      estimatedMinutes: minutes,
      distanceKm: distance,
      transportationId,
      fare: { baseFare, perKmRate },
      stops: state.stops.map((row, index) => ({ stopId: stopIds[index], minutesFromStart: Number(row.minutes) })),
      path: keepsLine && existing ? existing.path : [],
      schedules: state.schedules.map((row) => ({
        firstTrip: row.firstTrip,
        lastTrip: row.lastTrip,
        frequencyMinutes: Number(row.frequency),
        daysOperating: row.days.trim(),
      })),
    },
  }
}

/**
 * True when the stops of an EXISTING route were changed in the form (a stop added, removed,
 * replaced or moved). The route form uses it to warn that the map line will be drawn again.
 * Rows that are still empty are ignored.
 */
export function stopsChanged(state: RouteFormState, existing: Route): boolean {
  const chosen = state.stops.filter((row) => row.stopId !== '').map((row) => Number(row.stopId))
  return !sameOrder(existing.stops.map((routeStop) => routeStop.stop.id), chosen)
}
