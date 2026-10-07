// Matches StopResponse and NearbyStopResponse in docs/API.md.
export interface Stop {
  id: number
  name: string
  description: string | null
  latitude: number
  longitude: number
  demoData: boolean
}

export interface NearbyStop {
  stop: Stop
  distanceKm: number
}
