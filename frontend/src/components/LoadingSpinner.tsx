interface LoadingSpinnerProps {
  label?: string
}

export default function LoadingSpinner({ label = 'Loading...' }: LoadingSpinnerProps) {
  return (
    <div className="flex items-center justify-center gap-3 py-16 text-slate-600" role="status">
      <span className="h-6 w-6 animate-spin rounded-full border-2 border-slate-300 border-t-primary" />
      <span>{label}</span>
    </div>
  )
}
