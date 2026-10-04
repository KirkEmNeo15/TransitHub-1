interface ErrorMessageProps {
  message: string
  onRetry?: () => void
}

export default function ErrorMessage({ message, onRetry }: ErrorMessageProps) {
  return (
    <div role="alert" className="rounded-lg border border-danger/30 bg-red-50 px-4 py-3 text-danger">
      <p>{message}</p>
      {onRetry && (
        <button type="button" onClick={onRetry} className="mt-2 text-sm font-semibold underline">
          Try again
        </button>
      )}
    </div>
  )
}
