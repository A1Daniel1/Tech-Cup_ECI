import { useCallback, useEffect, useRef, useState } from 'react'
import { errorMessage, isApiError } from '@/lib/api'

export interface QueryResult<T> {
  data: T | null
  loading: boolean
  error: string | null
  /** HTTP status of the last error, if any (useful to treat 404 as "empty"). */
  errorStatus: number | null
  refetch: () => Promise<void>
  /** Replaces the cached data locally (e.g. after a mutation returned the new entity). */
  setData: (updater: T | null | ((previous: T | null) => T | null)) => void
}

export interface QueryOptions {
  /** When false the query is not executed (e.g. missing id or unauthorised). */
  enabled?: boolean
  /** HTTP statuses that should resolve to `data = null` instead of an error (e.g. 404 for "mine"). */
  nullOnStatus?: number[]
}

/**
 * Minimal server-state hook: runs `fetcher` on mount and whenever `deps` change,
 * aborting in-flight requests on cleanup. Not a cache; pages own their data.
 */
export function useQuery<T>(
  fetcher: (signal: AbortSignal) => Promise<T>,
  deps: readonly unknown[],
  options: QueryOptions = {},
): QueryResult<T> {
  const { enabled = true, nullOnStatus = [] } = options
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState<boolean>(enabled)
  const [error, setError] = useState<string | null>(null)
  const [errorStatus, setErrorStatus] = useState<number | null>(null)
  const [version, setVersion] = useState(0)
  const fetcherRef = useRef(fetcher)
  useEffect(() => {
    fetcherRef.current = fetcher
  })
  const nullOnStatusKey = nullOnStatus.join(',')

  useEffect(() => {
    if (!enabled) {
      setLoading(false)
      return
    }
    const controller = new AbortController()
    setLoading(true)
    setError(null)
    setErrorStatus(null)
    const silentStatuses = nullOnStatusKey ? nullOnStatusKey.split(',').map(Number) : []

    fetcherRef
      .current(controller.signal)
      .then((result) => {
        if (controller.signal.aborted) return
        setData(result)
        setLoading(false)
      })
      .catch((cause: unknown) => {
        if (controller.signal.aborted) return
        if (isApiError(cause) && silentStatuses.includes(cause.status)) {
          setData(null)
          setLoading(false)
          return
        }
        setError(errorMessage(cause))
        setErrorStatus(isApiError(cause) ? cause.status : null)
        setLoading(false)
      })

    return () => controller.abort()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enabled, version, nullOnStatusKey, ...deps])

  const refetch = useCallback(async () => {
    setVersion((v) => v + 1)
  }, [])

  return { data, loading, error, errorStatus, refetch, setData }
}

export interface MutationResult<Args extends unknown[], T> {
  mutate: (...args: Args) => Promise<T>
  loading: boolean
  error: string | null
  /** Field-level errors from the last failed call (`details[]` of the API error). */
  fieldErrors: Record<string, string>
  reset: () => void
}

/** Wraps an async action with loading / error state. Rethrows so callers can react. */
export function useMutation<Args extends unknown[], T>(
  action: (...args: Args) => Promise<T>,
): MutationResult<Args, T> {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const actionRef = useRef(action)
  useEffect(() => {
    actionRef.current = action
  })

  const mutate = useCallback(async (...args: Args): Promise<T> => {
    setLoading(true)
    setError(null)
    setFieldErrors({})
    try {
      return await actionRef.current(...args)
    } catch (cause) {
      setError(errorMessage(cause))
      if (isApiError(cause)) setFieldErrors(cause.fieldErrors)
      throw cause
    } finally {
      setLoading(false)
    }
  }, [])

  const reset = useCallback(() => {
    setError(null)
    setFieldErrors({})
  }, [])

  return { mutate, loading, error, fieldErrors, reset }
}
