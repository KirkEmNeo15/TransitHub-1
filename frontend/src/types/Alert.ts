// Matches AlertResponse in docs/API.md.
export type AlertSeverity = 'INFO' | 'WARNING' | 'CRITICAL'

export interface Alert {
  id: number
  title: string
  message: string
  severity: AlertSeverity
  active: boolean
  createdAt: string
  routeId: number | null
  routeName: string | null
}
