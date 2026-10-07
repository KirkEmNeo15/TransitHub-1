import { useMemo, useState } from 'react'

/** Splits a list that is already in the browser into pages. */
export function usePagination<T>(items: T[], pageSize: number) {
  const [page, setPage] = useState(0)

  const totalPages = Math.max(1, Math.ceil(items.length / pageSize))
  // if the list gets shorter (a filter or a delete), stay on a page that exists
  const currentPage = Math.min(page, totalPages - 1)

  const pageItems = useMemo(
    () => items.slice(currentPage * pageSize, currentPage * pageSize + pageSize),
    [items, currentPage, pageSize],
  )

  return { page: currentPage, setPage, totalPages, pageItems }
}
