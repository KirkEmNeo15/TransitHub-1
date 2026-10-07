interface NoticeProps {
  kind: 'success' | 'error'
  message: string
  onDismiss: () => void
}

/** A green or red message bar at the top of an admin page. */
export default function Notice({ kind, message, onDismiss }: NoticeProps) {
  const style = kind === 'success' ? 'border-green-200 bg-green-50 text-green-800' : 'border-danger/30 bg-red-50 text-danger'
  return (
    <div role={kind === 'error' ? 'alert' : 'status'} className={`mb-4 flex items-start justify-between gap-3 rounded-lg border px-4 py-3 ${style}`}>
      <p>{message}</p>
      <button type="button" onClick={onDismiss} aria-label="Dismiss" className="text-lg leading-none">
        &times;
      </button>
    </div>
  )
}
