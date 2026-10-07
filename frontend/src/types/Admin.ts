// Types for the admin area. They match docs/API.md.
import type { AlertSeverity } from './Alert'
import type { Coordinate, RouteStatus } from './Route'

export interface PageResponse<T> {
  items: T[]
  page: number
  size: number
  totalItems: number
  totalPages: number
}

export interface AdminStats {
  totalRoutes: number
  activeRoutes: number
  totalStops: number
  totalVehicles: number
  availableVehicles: number
  totalUsers: number
  activeAlerts: number
  openReports: number
}

export interface FareRequest {
  baseFare: number
  perKmRate: number
}

export interface RouteStopRequest {
  stopId: number
  minutesFromStart: number
}

export interface ScheduleRequest {
  firstTrip: string // "05:00"
  lastTrip: string
  frequencyMinutes: number
  daysOperating: string
}

export interface RouteRequest {
  routeCode: string
  routeName: string
  origin: string
  destination: string
  status: RouteStatus
  estimatedMinutes: number
  distanceKm: number
  transportationId: number
  fare: FareRequest
  stops: RouteStopRequest[]
  path: Coordinate[] // empty = the line is drawn through the stops
  schedules: ScheduleRequest[]
}

export interface StopRequest {
  name: string
  description: string | null
  latitude: number
  longitude: number
}

export type TransportTypeCode = 'BUS' | 'JEEPNEY' | 'VAN'

export interface TransportationRequest {
  type: TransportTypeCode
  name: string
  code: string
  description: string | null
  airConditioned?: boolean
  modernized?: boolean
  seatingCapacity?: number
}

export interface AlertRequest {
  title: string
  message: string
  severity: AlertSeverity
  routeId: number | null
  active: boolean
}
