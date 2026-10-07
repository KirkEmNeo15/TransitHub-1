import { useCallback, useState } from 'react'
import AdminPageHeader from '../../components/admin/AdminPageHeader'
import ConfirmDialog from '../../components/admin/ConfirmDialog'
import DataTable from '../../components/admin/DataTable'
import type { Column } from '../../components/admin/DataTable'
import Modal from '../../components/admin/Modal'
import Notice from '../../components/admin/Notice'
import Pagination from '../../components/admin/Pagination'
import RouteForm from '../../components/admin/RouteForm'
import ErrorMessage from '../../components/ErrorMessage'
import LoadingSpinner from '../../components/LoadingSpinner'
import TypeBadge from '../../components/TypeBadge'
import { useApiData } from '../../hooks/useApiData'
import { useDebouncedValue } from '../../hooks/useDebouncedValue'
import { getAdminRoutes } from '../../services/adminService'
import { changeRouteStatus, deleteRoute } from '../../services/routeService'
import { getStops } from '../../services/stopService'
import { getTransportations } from '../../services/transportationService'
import type { Route, RouteStatus } from '../../types/Route'
import { parseApiError } from '../../utils/apiError'

const PAGE_SIZE = 10
const STATUSES: RouteStatus[] = ['ACTIVE', 'INACTIVE', 'SUSPENDED']

// loaded when the form is opened (module-level = same identity every render)
const loadFormData = () => Promise.all([getStops(), getTransportations()])

type Dialog = { kind: 'form'; route: Route | null } | { kind: 'delete'; route: Route } | null

export default function RoutesManagement() {
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [dialog, setDialog] = useState<Dialog>(null)
  const [notice, setNotice] = useState<{ kind: 'success' | 'error'; message: string } | null>(null)
  const debouncedSearch = useDebouncedValue(search)

  const fetchRoutes = useCallback(() => getAdminRoutes(debouncedSearch, page, PAGE_SIZE), [debouncedSearch, page])
  const { state, reload } = useApiData(fetchRoutes)
  const formData = useApiData(loadFormData)

  const handleSearch = (value: string) => {
    setSearch(value)
    setPage(0)
  }

  const handleStatus = async (route: Route, status: RouteStatus) => {
    try {
      await changeRouteStatus(route.id, status)
      setNotice({ kind: 'success', message: `${route.routeCode} is now ${status.toLowerCase()}.` })
      reload()
    } catch (caught) {
      setNotice({ kind: 'error', message: parseApiError(caught).message })
    }
  }

  const columns: Column<Route>[] = [
    { header: 'Code', cell: (route) => <span className="font-mono">{route.routeCode}</span> },
    { header: 'Name', cell: (route) => route.routeName },
    { header: 'Type', cell: (route) => <TypeBadge type={route.transportation.type} /> },
    { header: 'Stops', cell: (route) => route.stops.length },
    {
      header: 'Status',
      cell: (route) => (
        <select
          aria-label={`Status of ${route.routeCode}`}
          value={route.status}
          onChange={(event) => handleStatus(route, event.target.value as RouteStatus)}
          className="rounded border border-slate-300 bg-white px-2 py-1 text-sm"
        >
          {STATUSES.map((status) => (
            <option key={status} value={status}>{status.charAt(0) + status.slice(1).toLowerCase()}</option>
          ))}
        </select>
      ),
    },
    {
      header: 'Actions',
      cell: (route) => (
        <div className="flex gap-3 text-sm font-semibold">
          <button type="button" className="text-primary hover:underline" onClick={() => setDialog({ kind: 'form', route })}>Edit</button>
          <button type="button" className="text-danger hover:underline" onClick={() => setDialog({ kind: 'delete', route })}>Delete</button>
        </div>
      ),
    },
  ]

  const close = () => setDialog(null)

  return (
    <section>
      <AdminPageHeader
        title="Routes"
        description="Add, edit, suspend or delete routes. (Demo data: fictional routes.)"
        search={search}
        onSearchChange={handleSearch}
        searchPlaceholder="Search by code, name, origin or destination"
        addLabel="Add route"
        onAdd={() => setDialog({ kind: 'form', route: null })}
      />
      {notice && <Notice kind={notice.kind} message={notice.message} onDismiss={() => setNotice(null)} />}

      {state.status === 'loading' && <LoadingSpinner />}
      {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
      {state.status === 'success' && (
        <>
          <DataTable columns={columns} rows={state.data.items} rowKey={(route) => route.id} emptyMessage="No routes found." />
          <Pagination page={state.data.page} totalPages={state.data.totalPages} totalItems={state.data.totalItems} onPageChange={setPage} />
        </>
      )}

      {dialog?.kind === 'form' && (
        <Modal title={dialog.route ? `Edit ${dialog.route.routeCode}` : 'Add route'} onClose={close} wide>
          {formData.state.status === 'loading' && <LoadingSpinner />}
          {formData.state.status === 'error' && <ErrorMessage message={formData.state.message} onRetry={formData.reload} />}
          {formData.state.status === 'success' && (
            <RouteForm
              route={dialog.route}
              allStops={formData.state.data[0]}
              transportations={formData.state.data[1]}
              onCancel={close}
              onSaved={(saved) => {
                close()
                setNotice({ kind: 'success', message: `Route ${saved.routeCode} saved.` })
                reload()
              }}
            />
          )}
        </Modal>
      )}

      {dialog?.kind === 'delete' && (
        <ConfirmDialog
          title="Delete route"
          message={`Delete ${dialog.route.routeCode} - ${dialog.route.routeName}? This also removes its schedules and alerts. This cannot be undone.`}
          onClose={close}
          onConfirm={async () => {
            await deleteRoute(dialog.route.id)
            close()
            setNotice({ kind: 'success', message: `Route ${dialog.route.routeCode} deleted.` })
            reload()
          }}
        />
      )}
    </section>
  )
}
