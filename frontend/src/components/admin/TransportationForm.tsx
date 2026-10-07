import { useState } from 'react'
import type { FormEvent } from 'react'
import { createTransportation, updateTransportation } from '../../services/transportationService'
import type { TransportationRequest, TransportTypeCode } from '../../types/Admin'
import type { Transportation } from '../../types/Transportation'
import { parseApiError } from '../../utils/apiError'
import FormField from '../FormField'
import { CheckboxField, FormButtons, FormError, SelectField, TextAreaField } from './FormControls'

interface TransportationFormProps {
  transportation: Transportation | null // null = create a new one
  onSaved: (saved: Transportation) => void
  onCancel: () => void
}

const TYPE_OPTIONS = [
  { value: 'BUS', label: 'Bus' },
  { value: 'JEEPNEY', label: 'Jeepney' },
  { value: 'VAN', label: 'Van' },
]

function typeToCode(type: Transportation['type']): TransportTypeCode {
  return type === 'Bus' ? 'BUS' : type === 'Jeepney' ? 'JEEPNEY' : 'VAN'
}

/** Add or edit a transportation. The extra fields change with the type (inheritance in the backend). */
export default function TransportationForm({ transportation, onSaved, onCancel }: TransportationFormProps) {
  const editing = transportation !== null
  const details = transportation?.details ?? {}

  const [type, setType] = useState<TransportTypeCode>(transportation ? typeToCode(transportation.type) : 'BUS')
  const [name, setName] = useState(transportation?.name ?? '')
  const [code, setCode] = useState(transportation?.code ?? '')
  const [description, setDescription] = useState(transportation?.description ?? '')
  const [airConditioned, setAirConditioned] = useState(details.airConditioned === true)
  const [modernized, setModernized] = useState(details.modernized === true)
  const [seats, setSeats] = useState(typeof details.seatingCapacity === 'number' ? String(details.seatingCapacity) : '')
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [serverMessage, setServerMessage] = useState('')
  const [busy, setBusy] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    const found: Record<string, string> = {}
    if (name.trim() === '') found.name = 'Name cannot be empty'
    if (code.trim() === '') found.code = 'Code cannot be empty'
    let seatingCapacity: number | undefined
    if (type === 'VAN') {
      seatingCapacity = /^\d+$/.test(seats.trim()) ? Number(seats) : NaN
      if (Number.isNaN(seatingCapacity) || seatingCapacity < 1) found.seatingCapacity = 'Seats must be a whole number (1 or more)'
    }
    setErrors(found)
    setServerMessage('')
    if (Object.keys(found).length > 0) return

    const request: TransportationRequest = {
      type,
      name: name.trim(),
      code: code.trim(),
      description: description.trim() === '' ? null : description.trim(),
      ...(type === 'BUS' ? { airConditioned } : {}),
      ...(type === 'JEEPNEY' ? { modernized } : {}),
      ...(type === 'VAN' ? { seatingCapacity } : {}),
    }
    setBusy(true)
    try {
      const saved = transportation ? await updateTransportation(transportation.id, request) : await createTransportation(request)
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
      <SelectField
        id="transport-type"
        label={editing ? 'Type (cannot be changed)' : 'Type'}
        value={type}
        onChange={(value) => setType(value as TransportTypeCode)}
        options={TYPE_OPTIONS}
        disabled={editing}
      />
      <div className="grid gap-4 sm:grid-cols-2">
        <FormField id="transport-name" label="Name" value={name} onChange={setName} error={errors.name} />
        <FormField id="transport-code" label="Code" value={code} onChange={setCode} error={errors.code} />
      </div>
      <TextAreaField id="transport-description" label="Description (optional)" value={description} onChange={setDescription} error={errors.description} />
      {type === 'BUS' && <CheckboxField id="transport-ac" label="Air-conditioned" checked={airConditioned} onChange={setAirConditioned} />}
      {type === 'JEEPNEY' && <CheckboxField id="transport-modern" label="Modernized jeepney" checked={modernized} onChange={setModernized} />}
      {type === 'VAN' && <FormField id="transport-seats" label="Seating capacity" value={seats} onChange={setSeats} error={errors.seatingCapacity} />}
      <FormButtons busy={busy} onCancel={onCancel} />
    </form>
  )
}
