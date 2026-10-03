import axios from 'axios'
import { useEffect, useState } from 'react'
import { CircleMarker, MapContainer, Popup, TileLayer } from 'react-leaflet'
import { useLocation } from 'react-router'
import 'leaflet/dist/leaflet.css'
import api from '../services/api'
import type { HealthResponse } from '../types/Api'

// TEMPORARY page: proves that Tailwind, the router, Axios + the backend, and Leaflet all work.
// It is removed in Phase 12/14.

type ApiCheck =
  | { status: 'loading' }
  | { status: 'ok'; health: HealthResponse }
  | { status: 'error'; message: string }

function describeError(error: unknown): string {
  if (axios.isAxiosError(error)) {
    if (error.response) {
      return `The backend answered with status ${error.response.status}.`
    }
    return 'Cannot reach the backend. Is it running on port 8080? (A blocked CORS request looks the same.)'
  }
  return 'Unknown error.'
}

const LIPA_CENTER: [number, number] = [13.9411, 121.1631]

export default function SetupCheck() {
  const location = useLocation()
  const [apiCheck, setApiCheck] = useState<ApiCheck>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false
    api
      .get<HealthResponse>('/api/health')
      .then((response) => {
        if (!cancelled) setApiCheck({ status: 'ok', health: response.data })
      })
      .catch((error: unknown) => {
        if (!cancelled) setApiCheck({ status: 'error', message: describeError(error) })
      })
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <section className="space-y-6">
      <h1 className="text-2xl font-bold">Setup check</h1>

      <div className="grid gap-4 sm:grid-cols-3">
        <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
          <h2 className="font-semibold">Tailwind CSS</h2>
          <p className="mt-2 flex gap-2">
            <span className="rounded bg-primary px-2 py-1 text-sm text-white">primary</span>
            <span className="rounded bg-success px-2 py-1 text-sm text-white">success</span>
            <span className="rounded bg-warning px-2 py-1 text-sm text-white">warning</span>
            <span className="rounded bg-danger px-2 py-1 text-sm text-white">danger</span>
          </p>
          <p className="mt-2 text-sm text-slate-600">You should see four colored badges.</p>
        </div>

        <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
          <h2 className="font-semibold">React Router</h2>
          <p className="mt-2 text-sm">
            Current path: <code className="rounded bg-slate-100 px-1">{location.pathname}</code>
          </p>
        </div>

        <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
          <h2 className="font-semibold">Backend (Axios)</h2>
          {apiCheck.status === 'loading' && <p className="mt-2 text-sm">Checking...</p>}
          {apiCheck.status === 'ok' && (
            <p className="mt-2 text-sm text-success">
              Database {apiCheck.health.database}, {apiCheck.health.routesInDatabase ?? 'no'} routes found.
            </p>
          )}
          {apiCheck.status === 'error' && (
            <p className="mt-2 text-sm text-danger">{apiCheck.message}</p>
          )}
        </div>
      </div>

      <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <h2 className="font-semibold">Leaflet + OpenStreetMap</h2>
        <p className="mb-3 text-sm text-slate-600">
          You should see a street map of Lipa City with one blue dot (demo marker).
        </p>
        <MapContainer center={LIPA_CENTER} zoom={13} className="h-80 w-full rounded-lg">
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <CircleMarker center={LIPA_CENTER} radius={10} pathOptions={{ color: '#2563eb' }}>
            <Popup>Lipa City (demo data)</Popup>
          </CircleMarker>
        </MapContainer>
      </div>
    </section>
  )
}
