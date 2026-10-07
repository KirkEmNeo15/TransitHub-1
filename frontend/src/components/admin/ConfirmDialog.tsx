import { useState } from 'react'
import { parseApiError } from '../../utils/apiError'
import Modal from './Modal'

interface ConfirmDialogProps {
  title: string
  message: string
  confirmLabel?: string
  // does the work; if it throws, the error message is shown in the dialog
  onConfirm: () => Promise<void>
  onClose: () => void
}

/** "Are you sure?" before something is deleted. */
export default function ConfirmDialog({ title, message, confirmLabel = 'Delete', onConfirm, onClose }: ConfirmDialogProps) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  const handleConfirm = async () => {
    setBusy(true)
    setError('')
    try {
      await onConfirm()
    } catch (caught) {
      // for example 409: "This stop is used by one or more routes"
      setError(parseApiError(caught).message)
      setBusy(false)
    }
  }

  return (
    <Modal title={title} onClose={onClose}>
      <p>{message}</p>
      {error && (
        <p role="alert" className="mt-3 rounded-lg border border-danger/30 bg-red-50 px-3 py-2 text-sm text-danger">
          {error}
        </p>
      )}
      <div className="mt-6 flex justify-end gap-3">
        <button type="button" onClick={onClose} className="rounded-lg border border-slate-300 px-4 py-2 font-medium hover:bg-slate-50">
          Cancel
        </button>
        <button
          type="button"
          onClick={handleConfirm}
          disabled={busy}
          className="rounded-lg bg-danger px-4 py-2 font-semibold text-white hover:bg-red-700 disabled:opacity-60"
        >
          {busy ? 'Working...' : confirmLabel}
        </button>
      </div>
    </Modal>
  )
}
