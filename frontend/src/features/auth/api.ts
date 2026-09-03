import { api } from '@/lib/api'
import type { RegisterRequest, UserResponse } from '@/types/api'

export const authApi = {
  register: (payload: RegisterRequest) => api.post<UserResponse>('/auth/register', payload, { skipAuthRedirect: true }),
  me: () => api.get<UserResponse>('/auth/me'),
}
