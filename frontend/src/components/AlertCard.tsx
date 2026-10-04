import { Link } from 'react-router'
import type { Alert, AlertSeverity } from '../types/Alert'
import { formatDateTime } from '../utils/format'

const styles: Record<AlertSeverity, { box: string; badge: string; label: string }> = {
  INFO: { box: 'border-blue-200 bg-blue-50', badge: 'bg-blue-100 text-blue-800', label: 'Info' },
  WARNING: { box: 'border-amber-200 bg-amber-50', badge: 'bg-amber-100 text-amber-800', label: 'Warning' },
  CRITICAL: { box: 'border-red-200 bg-red-50', badge: 'bg-red-100 text-red-800', label: 'Critical' },
}

export default function AlertCard({ alert }: { alert: Alert }) {
  const style = styles[alert.severity]
  return (
    <article className={`rounded-xl border p-4 ${style.box}`}>
      <div className="flex flex-wrap items-center gap-2">
        <span className={`rounded-full px-2.5 py-0.5 text-xs font-semibold ${style.badge}`}>{style.label}</span>
        <h3 className="font-semibold">{alert.title}</h3>
      </div>
      <p className="mt-2 text-sm">{alert.message}</p>
      <p className="mt-2 text-xs text-slate-600">
        {formatDateTime(alert.createdAt)}
        {alert.routeId !== null && alert.routeName && (
          <>
            {' '}
            &middot;{' '}
            <Link to={`/routes/${alert.routeId}`} className="font-medium text-primary hover:underline">
              {alert.routeName}
            </Link>
          </>
        )}
      </p>
    </article>
  )
}
