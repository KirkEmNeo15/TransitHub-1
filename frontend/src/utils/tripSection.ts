import type { Route } from '../types/Route'

export interface TripSection {
  boardIndex: number // position in route.stops where the passenger gets on
  alightIndex: number // position where the passenger gets off
  minutes: number // riding time between the two stops
}

/**
 * Finds which part of a route matches a search: the first stop whose name contains the origin text,
 * followed (later on the same route) by a stop whose name contains the destination text.
 * This is the same rule the backend uses to find the routes, so a found route always has a section.
 */
export function findTripSection(route: Route, origin: string, destination: string): TripSection | null {
  const from = origin.trim().toLowerCase()
  const to = destination.trim().toLowerCase()
  if (from === '' || to === '') return null

  const names = route.stops.map((routeStop) => routeStop.stop.name.toLowerCase())
  for (let board = 0; board < names.length; board++) {
    if (!names[board].includes(from)) continue
    for (let alight = board + 1; alight < names.length; alight++) {
      if (names[alight].includes(to)) {
        return {
          boardIndex: board,
          alightIndex: alight,
          minutes: route.stops[alight].minutesFromStart - route.stops[board].minutesFromStart,
        }
      }
    }
  }
  return null
}
