import { useState } from 'react'
import type { FormEvent } from 'react'
import { createRoute, updateRoute } from '../../services/routeService'
import type { Route, RouteStatus } from '../../types/Route'
import type { Stop } from '../../types/Stop'
import type { Transportation } from '../../types/Transportation'
import { parseApiError } from '../../utils/apiError'
import { buildRouteRequest, emptyRouteForm, newKey, routeToForm, stopsChanged } from '../../utils/routeForm'
import type { RouteFormState, ScheduleRow, StopRow } from '../../utils/routeForm'
import FormField from '../FormField'
import { FormButtons, FormError, SelectField } from './FormControls'

interface RouteFormProps {
  route: Route | null // null = create a new route
  allStops: Stop[]
  transportations: Transportation[]
  onSaved: (saved: Route) => void
  onCancel: () => void
}

const STATUS_OPTIONS = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
  { value: 'SUSPENDED', label: 'Suspended' },
]

const smallButton = 'rounded border border-slate-300 px-2 py-1 text-sm hover:bg-slate-50 disabled:opacity-40'

/** Add or edit a route: basic data, fare, the ordered stops and the schedules. */
export default function RouteForm({ route, allStops, transportations, onSaved, onCancel }: RouteFormProps) {
  const [state, setState] = useState<RouteFormState>(() => (route ? routeToForm(route) : emptyRouteForm()))
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [serverMessage, setServerMessage] = useState('')
  const [busy, setBusy] = useState(false)

  const setField = <K extends keyof RouteFormState>(field: K, value: RouteFormState[K]) =>
    setState((current) => ({ ...current, [field]: value }))

  // ----- stops editor -----
  const updateStopRow = (key: number, changes: Partial<StopRow>) =>
    setField('stops', state.stops.map((row) => (row.key === key ? { ...row, ...changes } : row)))
  const removeStopRow = (key: number) => setField('stops', state.stops.filter((row) => row.key !== key))
  const addStopRow = () => setField('stops', [...state.stops, { key: newKey(), stopId: '', minutes: '' }])
  const moveStopRow = (index: number, direction: -1 | 1) => {
    const target = index + direction
    if (target < 0 || target >= state.stops.length) return
    const copy = [...state.stops]
    ;[copy[index], copy[target]] = [copy[target], copy[index]]
    setField('stops', copy)
  }

  // ----- schedules editor -----
  const updateScheduleRow = (key: number, changes: Partial<ScheduleRow>) =>
    setField('schedules', state.schedules.map((row) => (row.key === key ? { ...row, ...changes } : row)))
  const addScheduleRow = () =>
    setField('schedules', [...state.schedules, { key: newKey(), firstTrip: '05:00', lastTrip: '21:00', frequency: '15', days: 'MON-SUN' }])
  const removeScheduleRow = (key: number) => setField('schedules', state.schedules.filter((row) => row.key !== key))

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setServerMessage('')
    const result = buildRouteRequest(state, route)
    setErrors(result.errors)
    if (result.request === null) return

    setBusy(true)
    try {
      const saved = route ? await updateRoute(route.id, result.request) : await createRoute(result.request)
      onSaved(saved)
    } catch (caught) {
      const parsed = parseApiError(caught)
      setErrors(parsed.fieldErrors)
      setServerMessage(parsed.message)
      setBusy(false)
    }
  }

  const stopOptions = allStops.map((stop) => ({ value: String(stop.id), label: stop.name }))
  const transportOptions = transportations.map((item) => ({ value: String(item.id), label: `${item.name} (${item.type})` }))
  const redrawWarning = route !== null && stopsChanged(state, route)

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-5">
      <FormError message={serverMessage} />

      <div className="grid gap-4 sm:grid-cols-2">
        <FormField id="route-code" label="Route code" value={state.routeCode} onChange={(v) => setField('routeCode', v)} error={errors.routeCode} />
        <FormField id="route-name" label="Route name" value={state.routeName} onChange={(v) => setField('routeName', v)} error={errors.routeName} />
        <FormField id="route-origin" label="Origin" value={state.origin} onChange={(v) => setField('origin', v)} error={errors.origin} />
        <FormField id="route-destination" label="Destination" value={state.destination} onChange={(v) => setField('destination', v)} error={errors.destination} />
        <FormField id="route-minutes" label="Travel time (minutes)" value={state.estimatedMinutes} onChange={(v) => setField('estimatedMinutes', v)} error={errors.estimatedMinutes} />
        <FormField id="route-distance" label="Distance (km)" value={state.distanceKm} onChange={(v) => setField('distanceKm', v)} error={errors.distanceKm} />
        <SelectField
          id="route-transport"
          label="Transportation"
          value={state.transportationId}
          onChange={(v) => setField('transportationId', v)}
          options={transportOptions}
          placeholder="Choose..."
          error={errors.transportationId}
        />
        <SelectField id="route-status" label="Status" value={state.status} onChange={(v) => setField('status', v as RouteStatus)} options={STATUS_OPTIONS} />
        <FormField id="route-base-fare" label="Base fare (PHP)" value={state.baseFare} onChange={(v) => setField('baseFare', v)} error={errors.baseFare} />
        <FormField id="route-per-km" label="Rate per km (PHP)" value={state.perKmRate} onChange={(v) => setField('perKmRate', v)} error={errors.perKmRate} />
      </div>

      <fieldset className="rounded-lg border border-slate-200 p-4">
        <legend className="px-2 font-semibold">Stops (in order)</legend>
        {errors.stops && <p role="alert" className="mb-2 text-sm text-danger">{errors.stops}</p>}
        {redrawWarning && (
          <p className="mb-2 rounded bg-amber-50 px-3 py-2 text-sm text-amber-800">
            The stops changed, so the map line will be drawn again straight through the stops. (Run the road tool again to make it follow roads.)
          </p>
        )}
        <ol className="space-y-2">
          {state.stops.map((row, index) => (
            <li key={row.key} className="flex flex-wrap items-end gap-2">
              <span className="w-6 pb-2 text-sm text-slate-500">{index + 1}.</span>
              <div className="min-w-48 flex-1">
                <SelectField id={`stop-${row.key}`} label="Stop" value={row.stopId} onChange={(v) => updateStopRow(row.key, { stopId: v })} options={stopOptions} placeholder="Choose a stop..." />
              </div>
              <div className="w-32">
                <FormField id={`minutes-${row.key}`} label="Min. from start" value={row.minutes} onChange={(v) => updateStopRow(row.key, { minutes: v })} />
              </div>
              <div className="flex gap-1 pb-1">
                <button type="button" className={smallButton} disabled={index === 0} onClick={() => moveStopRow(index, -1)} aria-label="Move stop up">&uarr;</button>
                <button type="button" className={smallButton} disabled={index === state.stops.length - 1} onClick={() => moveStopRow(index, 1)} aria-label="Move stop down">&darr;</button>
                <button type="button" className={smallButton} disabled={state.stops.length <= 2} onClick={() => removeStopRow(row.key)} aria-label="Remove stop">Remove</button>
              </div>
            </li>
          ))}
        </ol>
        <button type="button" onClick={addStopRow} className="mt-3 text-sm font-semibold text-primary hover:underline">+ Add a stop</button>
      </fieldset>

      <fieldset className="rounded-lg border border-slate-200 p-4">
        <legend className="px-2 font-semibold">Schedules (optional)</legend>
        {errors.schedules && <p role="alert" className="mb-2 text-sm text-danger">{errors.schedules}</p>}
        <ul className="space-y-3">
          {state.schedules.map((row) => (
            <li key={row.key} className="grid items-end gap-2 sm:grid-cols-[1fr_1fr_1fr_1.2fr_auto]">
              <div>
                <label htmlFor={`first-${row.key}`} className="mb-1 block text-sm font-medium">First trip</label>
                <input id={`first-${row.key}`} type="time" value={row.firstTrip} onChange={(e) => updateScheduleRow(row.key, { firstTrip: e.target.value })} className="w-full rounded-lg border border-slate-300 px-3 py-2" />
              </div>
              <div>
                <label htmlFor={`last-${row.key}`} className="mb-1 block text-sm font-medium">Last trip</label>
                <input id={`last-${row.key}`} type="time" value={row.lastTrip} onChange={(e) => updateScheduleRow(row.key, { lastTrip: e.target.value })} className="w-full rounded-lg border border-slate-300 px-3 py-2" />
              </div>
              <FormField id={`freq-${row.key}`} label="Every (min)" value={row.frequency} onChange={(v) => updateScheduleRow(row.key, { frequency: v })} />
              <FormField id={`days-${row.key}`} label="Days" value={row.days} onChange={(v) => updateScheduleRow(row.key, { days: v })} />
              <button type="button" className={`${smallButton} mb-1`} onClick={() => removeScheduleRow(row.key)}>Remove</button>
            </li>
          ))}
        </ul>
        <button type="button" onClick={addScheduleRow} className="mt-3 text-sm font-semibold text-primary hover:underline">+ Add a schedule</button>
      </fieldset>

      <FormButtons busy={busy} onCancel={onCancel} />
    </form>
  )
}
