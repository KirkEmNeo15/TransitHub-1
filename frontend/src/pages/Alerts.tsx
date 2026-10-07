import AlertCard from '../components/AlertCard'
import ErrorMessage from '../components/ErrorMessage'
import LoadingSpinner from '../components/LoadingSpinner'
import { useApiData } from '../hooks/useApiData'
import { getActiveAlerts } from '../services/alertService'

export default function AlertsPage() {
  const { state, reload } = useApiData(getActiveAlerts)

  return (
    <section>
      <h1 className="text-2xl font-bold">Alerts and announcements</h1>
      <p className="mt-1 text-slate-600">Current service alerts, newest first.</p>

      <div className="mt-6 space-y-3">
        {state.status === 'loading' && <LoadingSpinner label="Loading alerts..." />}
        {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
        {state.status === 'success' && state.data.length === 0 && (
          <p className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
            There are no active alerts right now.
          </p>
        )}
        {state.status === 'success' && state.data.map((alert) => <AlertCard key={alert.id} alert={alert} />)}
      </div>
    </section>
  )
}
