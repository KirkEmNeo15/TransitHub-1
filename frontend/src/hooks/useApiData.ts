import { useCallback, useEffect, useState } from 'react'
import { parseApiError } from '../utils/apiError'

export type AsyncState<T> =
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; message: string }

/**
 * Loads data from the backend and tells the component what to show:
 * a spinner (loading), the data (success) or an error message (error).
 *
 * "fetcher" must keep the same identity between renders, otherwise the data is loaded again
 * on every render. Use a function defined outside the component, or wrap it in useCallback
 * with the values it depends on (for example the selected filters).
 */
export function useApiData<T>(fetcher: () => Promise<T>) {
  const [state, setState] = useState<AsyncState<T>>({ status: 'loading' })
  const [reloadCount, setReloadCount] = useState(0)

  useEffect(() => {
    let cancelled = false // ignore the answer if the component left or the fetcher changed
    fetcher()
      .then((data) => {
        if (!cancelled) setState({ status: 'success', data })
      })
      .catch((error: unknown) => {
        if (!cancelled) setState({ status: 'error', message: parseApiError(error).message })
      })
    return () => {
      cancelled = true
    }
  }, [fetcher, reloadCount])

  /** Shows the spinner again and loads the data once more. */
  const reload = useCallback(() => {
    setState({ status: 'loading' })
    setReloadCount((count) => count + 1)
  }, [])

  return { state, reload }
}
