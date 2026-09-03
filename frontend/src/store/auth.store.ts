import { create } from 'zustand'
import { createJSONStorage, persist } from 'zustand/middleware'
import { api, setApiSessionHandlers } from '@/lib/api'
import type { LoginResponse, Role, UserResponse } from '@/types/api'

export interface AuthState {
  token: string | null
  expiresAt: string | null
  user: UserResponse | null
  /** True while a login request is in flight. */
  loading: boolean

  login: (email: string, password: string) => Promise<UserResponse>
  /** Calls `POST /auth/logout` (best effort) and clears the local session. */
  logout: () => Promise<void>
  /** Reloads `GET /auth/me` into the store. Returns null when there is no session. */
  refreshMe: () => Promise<UserResponse | null>
  /** Drops the local session without contacting the server. */
  clearSession: () => void
  /** True when the user holds any of the given roles. ADMIN implies every role. */
  hasRole: (...roles: Role[]) => boolean
  isAuthenticated: () => boolean
}

export function userHasRole(user: UserResponse | null, roles: Role[]): boolean {
  if (!user) return false
  if (roles.length === 0) return true
  if (user.roles.includes('ADMIN')) return true
  return roles.some((role) => user.roles.includes(role))
}

export const AUTH_STORAGE_KEY = 'techcup.auth'

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      expiresAt: null,
      user: null,
      loading: false,

      login: async (email, password) => {
        set({ loading: true })
        try {
          const response = await api.post<LoginResponse>(
            '/auth/login',
            { email, password },
            { skipAuthRedirect: true },
          )
          set({ token: response.token, expiresAt: response.expiresAt, user: response.user, loading: false })
          return response.user
        } catch (cause) {
          set({ loading: false })
          throw cause
        }
      },

      logout: async () => {
        if (get().token) {
          try {
            await api.post<void>('/auth/logout', undefined, { skipAuthRedirect: true })
          } catch {
            // The token is client-discarded; a failed logout call must not keep the session alive.
          }
        }
        get().clearSession()
      },

      refreshMe: async () => {
        if (!get().token) return null
        const user = await api.get<UserResponse>('/auth/me')
        set({ user })
        return user
      },

      clearSession: () => set({ token: null, expiresAt: null, user: null, loading: false }),

      hasRole: (...roles) => userHasRole(get().user, roles),

      isAuthenticated: () => !!get().token && !!get().user,
    }),
    {
      name: AUTH_STORAGE_KEY,
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({ token: state.token, expiresAt: state.expiresAt, user: state.user }),
    },
  ),
)

// Wire the API client to the store: bearer token + 401 handling.
setApiSessionHandlers({
  getToken: () => useAuthStore.getState().token,
  onUnauthorized: () => {
    const { token, clearSession } = useAuthStore.getState()
    if (!token) return
    clearSession()
    if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
      window.location.assign('/login')
    }
  },
})
