import { Link } from 'react-router'

// Placeholder. The real home page (hero, statistics, popular routes) is built in Phase 12.
export default function Home() {
  return (
    <section>
      <h1 className="text-3xl font-bold">Navigate Your City With Confidence</h1>
      <p className="mt-2 text-slate-600">
        Find buses, jeepneys, vans, and other transportation routes in one place.
      </p>
      <Link
        to="/setup-check"
        className="mt-6 inline-block rounded-lg bg-primary px-4 py-2 font-semibold text-white hover:bg-blue-700"
      >
        Run the setup check
      </Link>
    </section>
  )
}
