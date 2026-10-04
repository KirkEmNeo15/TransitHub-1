// A point the user chose on the map, as the start or the end of a trip.
export type PickMode = 'origin' | 'destination'

export interface TripPoint {
  latitude: number
  longitude: number
  label: string // a stop name, "My location", or the coordinates
}
