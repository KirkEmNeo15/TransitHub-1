import { useState } from 'react'
import type { FormEvent } from 'react'
import { createAlert, updateAlert } from '../../services/alertService'
import type { AlertRequest } from '../../types/Admin'
import type { Alert, AlertSeverity } from '../../types/Alert'
import type { Route } from '../../types/Route'
import { parseApiError } from '../../utils/apiError'
import FormField from '../FormField'
import { CheckboxField, FormButtons, FormError, SelectField, TextAreaField } from './FormControls'

interface AlertFormProps {
  alert: Alert | null // null = create a new alert
  routes: Route[] // for the "which route" choice
  onSaved: (saved: Alert) => void
  onCancel: () => void
}

const SEVERITY_OPTIONS = [
  { value: 'INFO', label: 'Info' },
  { value: 'WARNING', label: 'Warning' },
  { value: 'CRITICAL', label: 'Critical' },
]

/** Add or edit a service alert. */
export default function AlertForm({ alert, routes, onSaved, onCancel }: AlertFormProps) {
  const [title, setTitle] = useState(alert?.title ?? '')
  const [message, setMessage] = useState(alert?.message ?? '')
  const [severity, setSeverity] = useState<AlertSeverity>(alert?.severity ?? 'INFO')
  const [routeId, setRouteId] = useState(alert?.routeId != null ? String(alert.routeId) : '')
  const [active, setActive] = useState(alert?.active ?? true)
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [serverMessage, setServerMessage] = useState('')
  const [busy, setBusy] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    const found: Record<string, string> = {}
    if (title.trim() === '') found.title = 'Title cannot be empty'
    if (message.trim() === '') found.message = 'Message cannot be empty'
    setErrors(found)
    setServerMessage('')
    if (Object.keys(found).length > 0) return

    const request: AlertRequest = {
      title: title.trim(),
      message: message.trim(),
      severity,
      routeId: routeId === '' ? null : Number(routeId),
      active,
    }
    setBusy(true)
    try {
      const saved = alert ? await updateAlert(alert.id, request) : await createAlert(request)
      onSaved(saved)
    } catch (caught) {
      const parsed = parseApiError(caught)
      setErrors(parsed.fieldErrors)
      setServerMessage(parsed.message)
      setBusy(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-4">
      <FormError message={serverMessage} />
      <FormField id="alert-title" label="Title" value={title} onChange={setTitle} error={errors.title} />
      <TextAreaField id="alert-message" label="Message" value={message} onChange={setMessage} error={errors.message} rows={4} />
      <div className="grid gap-4 sm:grid-cols-2">
        <SelectField id="alert-severity" label="Severity" value={severity} onChange={(v) => setSeverity(v as AlertSeverity)} options={SEVERITY_OPTIONS} />
        <SelectField
          id="alert-route"
          label="Route"
          value={routeId}
          onChange={setRouteId}
          placeholder="All routes (general alert)"
          options={routes.map((route) => ({ value: String(route.id), label: `${route.routeCode} - ${route.routeName}` }))}
        />
      </div>
      <CheckboxField id="alert-active" label="Active (visible to commuters)" checked={active} onChange={setActive} />
      <FormButtons busy={busy} onCancel={onCancel} />
    </form>
  )
}
