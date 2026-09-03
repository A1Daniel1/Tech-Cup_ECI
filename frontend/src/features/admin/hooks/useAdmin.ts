import { useEffect, useState } from 'react'
import { useQuery } from '@/lib/useQuery'
import { adminApi } from '../api'

/** Debounces a value; used for the user search box. */
export function useDebouncedValue<T>(value: T, delayMs = 350): T {
  const [debounced, setDebounced] = useState(value)
  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delayMs)
    return () => clearTimeout(timer)
  }, [value, delayMs])
  return debounced
}

export function useUsers(search: string) {
  return useQuery((signal) => adminApi.searchUsers(search, signal), [search])
}

export function useAudit(action: string, limit: number) {
  return useQuery((signal) => adminApi.audit({ action, limit }, signal), [action, limit])
}
