import { useState } from 'react'
import type { FormEvent } from 'react'
import { createStop, updateStop } from '../../services/stopService'
import type { Stop } from '../../types/Stop'
import { parseApiError } from '../../utils/apiError'
import FormField from '../FormField'
import { FormButtons, FormError, TextAreaField } from './FormControls'

interface StopFormProps {
  stop: Stop | null // null = create a new stop
  onSaved: (stop: Stop) => void
  onCancel: () => void
}

const DECIMAL = /^-?\d+(\.\d+)?$/

/** Add or edit one stop. */
export default function StopForm({ stop, onSaved, onCancel }: StopFormProps) {
  const [name, setName] = useState(stop?.name ?? '')
  const [description, setDescription] = useState(stop?.description ?? '')
  const [latitude, setLatitude] = useState(stop ? String(stop.latitude) : '')
  const [longitude, setLongitude] = useState(stop ? String(stop.longitude) : '')
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [serverMessage, setServerMessage] = useState('')
  const [busy, setBusy] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    const found: Record<string, string> = {}
    if (name.trim() === '') found.name = 'Name cannot be empty'

    const lat = DECIMAL.test(latitude.trim()) ? Number(latitude) : NaN
    if (Number.isNaN(lat) || lat < -90 || lat > 90) found.latitude = 'Latitude must be a number from -90 to 90'
    const lng = DECIMAL.test(longitude.trim()) ? Number(longitude) : NaN
    if (Number.isNaN(lng) || lng < -180 || lng > 180) found.longitude = 'Longitude must be a number from -180 to 180'

    setErrors(found)
    setServerMessage('')
    if (Object.keys(found).length > 0) return

    const request = {
      name: name.trim(),
      description: description.trim() === '' ? null : description.trim(),
      latitude: lat,
      longitude: lng,
    }
    setBusy(true)
    try {
      const saved = stop ? await updateStop(stop.id, request) : await createStop(request)
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
      <FormField id="stop-name" label="Name" value={name} onChange={setName} error={errors.name} />
      <TextAreaField id="stop-description" label="Description (optional)" value={description} onChange={setDescription} error={errors.description} />
      <div className="grid gap-4 sm:grid-cols-2">
        <FormField id="stop-lat" label="Latitude" value={latitude} onChange={setLatitude} error={errors.latitude} placeholder="13.9411" />
        <FormField id="stop-lng" label="Longitude" value={longitude} onChange={setLongitude} error={errors.longitude} placeholder="121.1631" />
      </div>
      <FormButtons busy={busy} onCancel={onCancel} />
    </form>
  )
}
