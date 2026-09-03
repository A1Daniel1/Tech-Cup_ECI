import { api } from '@/lib/api'
import type { HomeResponse } from '@/types/api'

export const homeApi = {
  get: (signal?: AbortSignal) => api.get<HomeResponse>('/home', undefined, signal),
}
