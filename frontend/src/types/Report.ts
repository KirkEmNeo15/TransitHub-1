// Matches ReportRequest and ReportResponse in docs/API.md.
export type ReportStatus = 'OPEN' | 'REVIEWED' | 'RESOLVED'

export interface ReportRequest {
  routeId: number
  description: string
}

export interface Report {
  id: number
  routeId: number
  routeName: string
  reportedBy: string
  description: string
  status: ReportStatus
  createdAt: string
}
