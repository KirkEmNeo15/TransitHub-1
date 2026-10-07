import { useCallback, useState } from 'react'
import AdminPageHeader from '../../components/admin/AdminPageHeader'
import ConfirmDialog from '../../components/admin/ConfirmDialog'
import DataTable from '../../components/admin/DataTable'
import type { Column } from '../../components/admin/DataTable'
import Modal from '../../components/admin/Modal'
import Notice from '../../components/admin/Notice'
import Pagination from '../../components/admin/Pagination'
import StopForm from '../../components/admin/StopForm'
import ErrorMessage from '../../components/ErrorMessage'
import LoadingSpinner from '../../components/LoadingSpinner'
import { useApiData } from '../../hooks/useApiData'
import { useDebouncedValue } from '../../hooks/useDebouncedValue'
import { getAdminStops } from '../../services/adminService'
import { deleteStop } from '../../services/stopService'
import type { Stop } from '../../types/Stop'

const PAGE_SIZE = 10

type Dialog = { kind: 'form'; stop: Stop | null } | { kind: 'delete'; stop: Stop } | null

export default function StopsManagement() {
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [dialog, setDialog] = useState<Dialog>(null)
  const [notice, setNotice] = useState<{ kind: 'success' | 'error'; message: string } | null>(null)
  const debouncedSearch = useDebouncedValue(search)

  const fetchStops = useCallback(() => getAdminStops(debouncedSearch, page, PAGE_SIZE), [debouncedSearch, page])
  const { state, reload } = useApiData(fetchStops)

  const columns: Column<Stop>[] = [
    { header: 'Name', cell: (stop) => stop.name },
    { header: 'Latitude', cell: (stop) => stop.latitude.toFixed(5) },
    { header: 'Longitude', cell: (stop) => stop.longitude.toFixed(5) },
    { header: 'Description', cell: (stop) => stop.description ?? '-' },
    {
      header: 'Actions',
      cell: (stop) => (
        <div className="flex gap-3 text-sm font-semibold">
          <button type="button" className="text-primary hover:underline" onClick={() => setDialog({ kind: 'form', stop })}>Edit</button>
          <button type="button" className="text-danger hover:underline" onClick={() => setDialog({ kind: 'delete', stop })}>Delete</button>
        </div>
      ),
    },
  ]

  const close = () => setDialog(null)

  return (
    <section>
      <AdminPageHeader
        title="Stops"
        description="Places where vehicles stop. A stop used by a route cannot be deleted."
        search={search}
        onSearchChange={(value) => {
          setSearch(value)
          setPage(0)
        }}
        searchPlaceholder="Search stops by name"
        addLabel="Add stop"
        onAdd={() => setDialog({ kind: 'form', stop: null })}
      />
      {notice && <Notice kind={notice.kind} message={notice.message} onDismiss={() => setNotice(null)} />}

      {state.status === 'loading' && <LoadingSpinner />}
      {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
      {state.status === 'success' && (
        <>
          <DataTable columns={columns} rows={state.data.items} rowKey={(stop) => stop.id} emptyMessage="No stops found." />
          <Pagination page={state.data.page} totalPages={state.data.totalPages} totalItems={state.data.totalItems} onPageChange={setPage} />
        </>
      )}

      {dialog?.kind === 'form' && (
        <Modal title={dialog.stop ? 'Edit stop' : 'Add stop'} onClose={close}>
          <StopForm
            stop={dialog.stop}
            onCancel={close}
            onSaved={(saved) => {
              close()
              setNotice({ kind: 'success', message: `Stop "${saved.name}" saved.` })
              reload()
            }}
          />
        </Modal>
      )}

      {dialog?.kind === 'delete' && (
        <ConfirmDialog
          title="Delete stop"
          message={`Delete the stop "${dialog.stop.name}"?`}
          onClose={close}
          onConfirm={async () => {
            await deleteStop(dialog.stop.id)
            close()
            setNotice({ kind: 'success', message: `Stop "${dialog.stop.name}" deleted.` })
            reload()
          }}
        />
      )}
    </section>
  )
}
