import { Link } from 'react-router'
import { TRANSPORT_COLORS } from '../utils/transportColors'

const transportTypes = [
  { name: 'Bus', description: 'Long-distance and express routes between towns.' },
  { name: 'Jeepney', description: 'The classic local route, with stops along the way.' },
  { name: 'Van', description: 'Fast point-to-point vans between terminals.' },
  { name: 'Shuttle', description: 'Short trips to malls, campuses and business areas.' },
  { name: 'Train', description: 'Rail lines, where available.' },
]

// The numbers are filled in with real data in Phase 13.
const statLabels = ['Active routes', 'Transportation stops', 'Available vehicles', 'Active alerts']

export default function Home() {
  return (
    <div className="space-y-12">
      {/* hero */}
      <section className="rounded-2xl bg-linear-to-br from-secondary to-primary px-6 py-14 text-center text-white sm:px-12">
        <h1 className="text-3xl font-bold sm:text-5xl">Navigate Your City With Confidence</h1>
        <p className="mx-auto mt-4 max-w-xl text-lg text-blue-100">
          Find buses, jeepneys, vans, and other transportation routes in one place.
        </p>
        <div className="mt-8 flex flex-col justify-center gap-3 sm:flex-row">
          <Link
            to="/map"
            className="rounded-lg bg-white px-6 py-3 font-semibold text-primary hover:bg-blue-50"
          >
            Explore Map
          </Link>
          <Link
            to="/routes"
            className="rounded-lg border border-white/60 px-6 py-3 font-semibold hover:bg-white/10"
          >
            Find a Route
          </Link>
        </div>
      </section>

      {/* statistics */}
      <section aria-label="Statistics" className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {statLabels.map((label) => (
          <div key={label} className="rounded-xl border border-slate-200 bg-white p-5 text-center shadow-sm">
            <p className="text-3xl font-bold text-primary">&mdash;</p>
            <p className="mt-1 text-sm text-slate-600">{label}</p>
          </div>
        ))}
      </section>

      {/* transportation types */}
      <section>
        <h2 className="text-2xl font-bold">Transportation Types</h2>
        <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
          {transportTypes.map((type) => (
            <div
              key={type.name}
              className="rounded-xl border border-slate-200 border-t-4 bg-white p-4 shadow-sm"
              style={{ borderTopColor: TRANSPORT_COLORS[type.name] }}
            >
              <h3 className="font-semibold">{type.name}</h3>
              <p className="mt-1 text-sm text-slate-600">{type.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* popular routes and latest alerts */}
      <section className="grid gap-6 lg:grid-cols-2">
        <div>
          <h2 className="text-2xl font-bold">Popular Routes</h2>
          <div className="mt-4 rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
            Popular routes appear here in Phase 13.
          </div>
        </div>
        <div>
          <h2 className="text-2xl font-bold">Latest Alerts</h2>
          <div className="mt-4 rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
            Latest alerts appear here in Phase 13.
          </div>
        </div>
      </section>
    </div>
  )
}
