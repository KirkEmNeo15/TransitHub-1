import { useAuth } from '../hooks/useAuth'

export default function Profile() {
  const { user } = useAuth()
  if (!user) return null // ProtectedRoute makes sure this does not happen

  const rows = [
    { label: 'Full name', value: user.fullName },
    { label: 'Email', value: user.email },
    { label: 'Role', value: user.role === 'ADMIN' ? 'Administrator' : 'User' },
    { label: 'Member since', value: new Date(user.createdAt).toLocaleDateString() },
  ]

  return (
    <section className="mx-auto max-w-xl">
      <h1 className="text-2xl font-bold">My profile</h1>
      <dl className="mt-6 divide-y divide-slate-200 rounded-xl border border-slate-200 bg-white shadow-sm">
        {rows.map((row) => (
          <div key={row.label} className="flex justify-between gap-4 px-4 py-3">
            <dt className="text-slate-600">{row.label}</dt>
            <dd className="font-medium">{row.value}</dd>
          </div>
        ))}
      </dl>
    </section>
  )
}
