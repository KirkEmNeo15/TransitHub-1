import { useCallback, useState } from 'react'
import AdminPageHeader from '../../components/admin/AdminPageHeader'
import DataTable from '../../components/admin/DataTable'
import type { Column } from '../../components/admin/DataTable'
import Notice from '../../components/admin/Notice'
import Pagination from '../../components/admin/Pagination'
import ErrorMessage from '../../components/ErrorMessage'
import LoadingSpinner from '../../components/LoadingSpinner'
import { useApiData } from '../../hooks/useApiData'
import { useAuth } from '../../hooks/useAuth'
import { useDebouncedValue } from '../../hooks/useDebouncedValue'
import { changeUserRole, getAdminReports, getAdminUsers, setUserActive, updateReportStatus } from '../../services/adminService'
import type { Report, ReportStatus } from '../../types/Report'
import type { Role, User } from '../../types/User'
import { parseApiError } from '../../utils/apiError'
import { formatDateTime } from '../../utils/format'

const PAGE_SIZE = 10
const REPORT_STATUSES: ReportStatus[] = ['OPEN', 'REVIEWED', 'RESOLVED']
const loadReports = () => getAdminReports()

export default function UsersManagement() {
  const { user: currentUser } = useAuth()
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [notice, setNotice] = useState<{ kind: 'success' | 'error'; message: string } | null>(null)
  const debouncedSearch = useDebouncedValue(search)

  const fetchUsers = useCallback(() => getAdminUsers(debouncedSearch, page, PAGE_SIZE), [debouncedSearch, page])
  const users = useApiData(fetchUsers)
  const reports = useApiData(loadReports)

  const run = async (work: () => Promise<unknown>, success: string, after: () => void) => {
    try {
      await work()
      setNotice({ kind: 'success', message: success })
      after()
    } catch (caught) {
      setNotice({ kind: 'error', message: parseApiError(caught).message })
      after() // reload so the dropdown shows the real value again
    }
  }

  const userColumns: Column<User>[] = [
    { header: 'Name', cell: (user) => user.fullName },
    { header: 'Email', cell: (user) => user.email },
    {
      header: 'Role',
      cell: (user) => (
        <select
          aria-label={`Role of ${user.email}`}
          value={user.role}
          disabled={user.id === currentUser?.id}
          onChange={(event) =>
            run(() => changeUserRole(user.id, event.target.value as Role), `${user.email} is now ${event.target.value}.`, users.reload)
          }
          className="rounded border border-slate-300 bg-white px-2 py-1 text-sm disabled:bg-slate-100"
        >
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
        </select>
      ),
    },
    { header: 'Status', cell: (user) => (user.active ? 'Active' : 'Disabled') },
    { header: 'Joined', cell: (user) => formatDateTime(user.createdAt) },
    {
      header: 'Actions',
      cell: (user) => (
        <button
          type="button"
          disabled={user.id === currentUser?.id}
          onClick={() =>
            run(() => setUserActive(user.id, !user.active), `${user.email} ${user.active ? 'disabled' : 'enabled'}.`, users.reload)
          }
          className="text-sm font-semibold text-primary hover:underline disabled:cursor-not-allowed disabled:text-slate-400 disabled:no-underline"
        >
          {user.active ? 'Disable' : 'Enable'}
        </button>
      ),
    },
  ]

  const reportColumns: Column<Report>[] = [
    { header: 'Route', cell: (report) => report.routeName },
    { header: 'Description', cell: (report) => report.description },
    { header: 'Reported by', cell: (report) => report.reportedBy },
    { header: 'Date', cell: (report) => formatDateTime(report.createdAt) },
    {
      header: 'Status',
      cell: (report) => (
        <select
          aria-label={`Status of report ${report.id}`}
          value={report.status}
          onChange={(event) =>
            run(() => updateReportStatus(report.id, event.target.value as ReportStatus), 'Report updated.', reports.reload)
          }
          className="rounded border border-slate-300 bg-white px-2 py-1 text-sm"
        >
          {REPORT_STATUSES.map((status) => (
            <option key={status} value={status}>{status.charAt(0) + status.slice(1).toLowerCase()}</option>
          ))}
        </select>
      ),
    },
  ]

  return (
    <section className="space-y-10">
      <div>
        <AdminPageHeader
          title="Users"
          description="Change roles or disable accounts. You cannot change your own account here."
          search={search}
          onSearchChange={(value) => {
            setSearch(value)
            setPage(0)
          }}
          searchPlaceholder="Search by name or email"
        />
        {notice && <Notice kind={notice.kind} message={notice.message} onDismiss={() => setNotice(null)} />}
        {users.state.status === 'loading' && <LoadingSpinner />}
        {users.state.status === 'error' && <ErrorMessage message={users.state.message} onRetry={users.reload} />}
        {users.state.status === 'success' && (
          <>
            <DataTable columns={userColumns} rows={users.state.data.items} rowKey={(user) => user.id} emptyMessage="No users found." />
            <Pagination page={users.state.data.page} totalPages={users.state.data.totalPages} totalItems={users.state.data.totalItems} onPageChange={setPage} />
          </>
        )}
      </div>

      <div>
        <h2 className="text-xl font-bold">Reports from commuters</h2>
        <p className="mb-4 mt-1 text-slate-600">Wrong or outdated information reported by users.</p>
        {reports.state.status === 'loading' && <LoadingSpinner />}
        {reports.state.status === 'error' && <ErrorMessage message={reports.state.message} onRetry={reports.reload} />}
        {reports.state.status === 'success' && (
          <DataTable columns={reportColumns} rows={reports.state.data} rowKey={(report) => report.id} emptyMessage="No reports yet." />
        )}
      </div>
    </section>
  )
}
