import type { RouteStatus } from '../types/Route'

const styles: Record<RouteStatus, string> = {
  ACTIVE: 'bg-green-100 text-green-800',
  INACTIVE: 'bg-slate-200 text-slate-700',
  SUSPENDED: 'bg-amber-100 text-amber-800',
}

const labels: Record<RouteStatus, string> = {
  ACTIVE: 'Active',
  INACTIVE: 'Inactive',
  SUSPENDED: 'Suspended',
}

export default function StatusBadge({ status }: { status: RouteStatus }) {
  return (
    <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-medium ${styles[status]}`}>
      {labels[status]}
    </span>
  )
}
