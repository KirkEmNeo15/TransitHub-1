interface PaginationProps {
  page: number // starts at 0
  totalPages: number
  totalItems: number
  onPageChange: (page: number) => void
}

export default function Pagination({ page, totalPages, totalItems, onPageChange }: PaginationProps) {
  const buttonClass =
    'rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50'
  return (
    <nav aria-label="Pages" className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm">
      <p className="text-slate-600">{totalItems} item(s)</p>
      <div className="flex items-center gap-2">
        <button type="button" className={buttonClass} disabled={page <= 0} onClick={() => onPageChange(page - 1)}>
          Previous
        </button>
        <span>
          Page {page + 1} of {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          className={buttonClass}
          disabled={page + 1 >= totalPages}
          onClick={() => onPageChange(page + 1)}
        >
          Next
        </button>
      </div>
    </nav>
  )
}
