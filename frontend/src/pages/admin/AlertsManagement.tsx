import { useState } from 'react'
import AdminPageHeader from '../../components/admin/AdminPageHeader'
import AlertForm from '../../components/admin/AlertForm'
import ConfirmDialog from '../../components/admin/ConfirmDialog'
import DataTable from '../../components/admin/DataTable'
import type { Column } from '../../components/admin/DataTable'
import Modal from '../../components/admin/Modal'
import Notice from '../../components/admin/Notice'
import Pagination from '../../components/admin/Pagination'
import ErrorMessage from '../../components/ErrorMessage'
import LoadingSpinner from '../../components/LoadingSpinner'
import { useApiData } from '../../hooks/useApiData'
import { usePagination } from '../../hooks/usePagination'
import { deleteAlert, getAllAlerts } from '../../services/alertService'
import { getRoutes } from '../../services/routeService'
import type { Alert } from '../../types/Alert'
import { formatDateTime } from '../../utils/format'

const PAGE_SIZE = 10
const loadAlerts = () => getAllAlerts()
const loadRoutes = () => getRoutes()

type Dialog = { kind: 'form'; alert: Alert | null } | { kind: 'delete'; alert: Alert } | null

const severityStyle: Record<Alert['severity'], string> = {
  INFO: 'bg-blue-100 text-blue-800',
  WARNING: 'bg-amber-100 text-amber-800',
  CRITICAL: 'bg-red-100 text-red-800',
}

export default function AlertsManagement() {
  const [dialog, setDialog] = useState<Dialog>(null)
  const [notice, setNotice] = useState<{ kind: 'success' | 'error'; message: string } | null>(null)
  const { state, reload } = useApiData(loadAlerts)
  const routes = useApiData(loadRoutes)
  const all = state.status === 'success' ? state.data : []
  const { page, setPage, totalPages, pageItems } = usePagination(all, PAGE_SIZE)

  const columns: Column<Alert>[] = [
    { header: 'Title', cell: (alert) => alert.title },
    { header: 'Severity', cell: (alert) => <span className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${severityStyle[alert.severity]}`}>{alert.severity}</span> },
    { header: 'Route', cell: (alert) => alert.routeName ?? 'All routes' },
    { header: 'Visible', cell: (alert) => (alert.active ? 'Yes' : 'No') },
    { header: 'Created', cell: (alert) => formatDateTime(alert.createdAt) },
    {
      header: 'Actions',
      cell: (alert) => (
        <div className="flex gap-3 text-sm font-semibold">
          <button type="button" className="text-primary hover:underline" onClick={() => setDialog({ kind: 'form', alert })}>Edit</button>
          <button type="button" className="text-danger hover:underline" onClick={() => setDialog({ kind: 'delete', alert })}>Delete</button>
        </div>
      ),
    },
  ]

  const close = () => setDialog(null)

  return (
    <section>
      <AdminPageHeader
        title="Alerts"
        description="Service notices shown to commuters. Inactive alerts are hidden from them."
        addLabel="Add alert"
        onAdd={() => setDialog({ kind: 'form', alert: null })}
      />
      {notice && <Notice kind={notice.kind} message={notice.message} onDismiss={() => setNotice(null)} />}

      {state.status === 'loading' && <LoadingSpinner />}
      {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
      {state.status === 'success' && (
        <>
          <DataTable columns={columns} rows={pageItems} rowKey={(alert) => alert.id} emptyMessage="No alerts yet." />
          <Pagination page={page} totalPages={totalPages} totalItems={all.length} onPageChange={setPage} />
        </>
      )}

      {dialog?.kind === 'form' && (
        <Modal title={dialog.alert ? 'Edit alert' : 'Add alert'} onClose={close}>
          {routes.state.status === 'loading' && <LoadingSpinner />}
          {routes.state.status === 'error' && <ErrorMessage message={routes.state.message} onRetry={routes.reload} />}
          {routes.state.status === 'success' && (
            <AlertForm
              alert={dialog.alert}
              routes={routes.state.data}
              onCancel={close}
              onSaved={(saved) => {
                close()
                setNotice({ kind: 'success', message: `Alert "${saved.title}" saved.` })
                reload()
              }}
            />
          )}
        </Modal>
      )}

      {dialog?.kind === 'delete' && (
        <ConfirmDialog
          title="Delete alert"
          message={`Delete the alert "${dialog.alert.title}"?`}
          onClose={close}
          onConfirm={async () => {
            await deleteAlert(dialog.alert.id)
            close()
            setNotice({ kind: 'success', message: `Alert "${dialog.alert.title}" deleted.` })
            reload()
          }}
        />
      )}
    </section>
  )
}
