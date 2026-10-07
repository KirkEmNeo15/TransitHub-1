import { useState } from 'react'
import AdminPageHeader from '../../components/admin/AdminPageHeader'
import ConfirmDialog from '../../components/admin/ConfirmDialog'
import DataTable from '../../components/admin/DataTable'
import type { Column } from '../../components/admin/DataTable'
import Modal from '../../components/admin/Modal'
import Notice from '../../components/admin/Notice'
import Pagination from '../../components/admin/Pagination'
import TransportationForm from '../../components/admin/TransportationForm'
import ErrorMessage from '../../components/ErrorMessage'
import LoadingSpinner from '../../components/LoadingSpinner'
import TypeBadge from '../../components/TypeBadge'
import { useApiData } from '../../hooks/useApiData'
import { usePagination } from '../../hooks/usePagination'
import { deleteTransportation, getTransportations } from '../../services/transportationService'
import type { Transportation } from '../../types/Transportation'
import { describeDetails } from '../../utils/format'

const PAGE_SIZE = 10
const loadAll = () => getTransportations()

type Dialog = { kind: 'form'; item: Transportation | null } | { kind: 'delete'; item: Transportation } | null

export default function TransportationManagement() {
  const [dialog, setDialog] = useState<Dialog>(null)
  const [notice, setNotice] = useState<{ kind: 'success' | 'error'; message: string } | null>(null)
  const { state, reload } = useApiData(loadAll)
  const all = state.status === 'success' ? state.data : []
  const { page, setPage, totalPages, pageItems } = usePagination(all, PAGE_SIZE)

  const columns: Column<Transportation>[] = [
    { header: 'Name', cell: (item) => item.name },
    { header: 'Code', cell: (item) => <span className="font-mono">{item.code}</span> },
    { header: 'Type', cell: (item) => <TypeBadge type={item.type} /> },
    { header: 'Details', cell: (item) => describeDetails(item).join(', ') || '-' },
    {
      header: 'Actions',
      cell: (item) => (
        <div className="flex gap-3 text-sm font-semibold">
          <button type="button" className="text-primary hover:underline" onClick={() => setDialog({ kind: 'form', item })}>Edit</button>
          <button type="button" className="text-danger hover:underline" onClick={() => setDialog({ kind: 'delete', item })}>Delete</button>
        </div>
      ),
    },
  ]

  const close = () => setDialog(null)

  return (
    <section>
      <AdminPageHeader
        title="Transportation"
        description="Buses, jeepneys and vans. Each type has its own extra details."
        addLabel="Add transportation"
        onAdd={() => setDialog({ kind: 'form', item: null })}
      />
      {notice && <Notice kind={notice.kind} message={notice.message} onDismiss={() => setNotice(null)} />}

      {state.status === 'loading' && <LoadingSpinner />}
      {state.status === 'error' && <ErrorMessage message={state.message} onRetry={reload} />}
      {state.status === 'success' && (
        <>
          <DataTable columns={columns} rows={pageItems} rowKey={(item) => item.id} emptyMessage="No transportation yet." />
          <Pagination page={page} totalPages={totalPages} totalItems={all.length} onPageChange={setPage} />
        </>
      )}

      {dialog?.kind === 'form' && (
        <Modal title={dialog.item ? 'Edit transportation' : 'Add transportation'} onClose={close}>
          <TransportationForm
            transportation={dialog.item}
            onCancel={close}
            onSaved={(saved) => {
              close()
              setNotice({ kind: 'success', message: `"${saved.name}" saved.` })
              reload()
            }}
          />
        </Modal>
      )}

      {dialog?.kind === 'delete' && (
        <ConfirmDialog
          title="Delete transportation"
          message={`Delete "${dialog.item.name}"? It cannot be deleted while a route uses it.`}
          onClose={close}
          onConfirm={async () => {
            await deleteTransportation(dialog.item.id)
            close()
            setNotice({ kind: 'success', message: `"${dialog.item.name}" deleted.` })
            reload()
          }}
        />
      )}
    </section>
  )
}
