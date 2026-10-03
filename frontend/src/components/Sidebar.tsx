import { Link, NavLink } from 'react-router'

const adminLinks = [
  { to: '/admin', label: 'Dashboard', end: true },
  { to: '/admin/routes', label: 'Routes', end: false },
  { to: '/admin/stops', label: 'Stops', end: false },
  { to: '/admin/transportation', label: 'Transportation', end: false },
  { to: '/admin/alerts', label: 'Alerts', end: false },
  { to: '/admin/users', label: 'Users', end: false },
]

function itemClass({ isActive }: { isActive: boolean }): string {
  return `whitespace-nowrap rounded-md px-3 py-2 text-sm font-medium ${
    isActive ? 'bg-primary text-white' : 'text-slate-700 hover:bg-slate-100'
  }`
}

/** Admin navigation: a column on desktop, a scrollable row on phones. */
export default function Sidebar() {
  return (
    <aside className="border-b border-slate-200 bg-white md:w-56 md:shrink-0 md:border-b-0 md:border-r">
      <nav
        className="flex gap-1 overflow-x-auto p-2 md:sticky md:top-0 md:flex-col md:overflow-visible md:p-4"
        aria-label="Admin"
      >
        {adminLinks.map((link) => (
          <NavLink key={link.to} to={link.to} end={link.end} className={itemClass}>
            {link.label}
          </NavLink>
        ))}
        <Link to="/" className="whitespace-nowrap rounded-md px-3 py-2 text-sm text-slate-500 hover:bg-slate-100 md:mt-4">
          &larr; Back to the site
        </Link>
      </nav>
    </aside>
  )
}
