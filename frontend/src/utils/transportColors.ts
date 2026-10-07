// One color per transportation type, used for the route lines on the map.
// The keys are the values the backend sends in "transportation.type".
export const TRANSPORT_COLORS: Record<string, string> = {
  Bus: '#2563eb',
  Jeepney: '#f59e0b',
  Van: '#16a34a',
}

export const DEFAULT_ROUTE_COLOR = '#64748b'

export function colorForType(type: string): string {
  return TRANSPORT_COLORS[type] ?? DEFAULT_ROUTE_COLOR
}
