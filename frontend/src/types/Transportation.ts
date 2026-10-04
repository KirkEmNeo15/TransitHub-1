// Matches TransportationResponse in docs/API.md.
export type TransportTypeName = 'Bus' | 'Jeepney' | 'Van'

export interface Transportation {
  id: number
  name: string
  code: string
  type: TransportTypeName
  description: string | null
  // type-specific values, for example { airConditioned: true } for a Bus
  details: Record<string, unknown>
}
