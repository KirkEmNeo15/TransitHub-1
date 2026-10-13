import type { Stop } from '../types/Stop'

/** A stop whose name contains "terminal" (any case) is a terminal. Only terminals get a pin on the map. */
export function isTerminal(stop: Pick<Stop, 'name'>): boolean {
  return stop.name.toLowerCase().includes('terminal')
}