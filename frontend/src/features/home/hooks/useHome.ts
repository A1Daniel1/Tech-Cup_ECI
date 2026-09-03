import { useQuery } from '@/lib/useQuery'
import { homeApi } from '../api'

export function useHome() {
  return useQuery((signal) => homeApi.get(signal), [])
}
