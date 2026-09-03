import { create } from 'zustand'

export type ToastKind = 'success' | 'error' | 'info' | 'warning'

export interface Toast {
  id: number
  kind: ToastKind
  title?: string
  message: string
  /** Milliseconds before auto-dismiss. `0` keeps the toast until closed. */
  duration: number
}

export interface ToastInput {
  kind?: ToastKind
  title?: string
  message: string
  duration?: number
}

interface UiState {
  toasts: Toast[]
  pushToast: (toast: ToastInput) => number
  dismissToast: (id: number) => void
  clearToasts: () => void
}

let nextToastId = 1

export const useUiStore = create<UiState>()((set) => ({
  toasts: [],
  pushToast: ({ kind = 'info', title, message, duration = 4500 }) => {
    const id = nextToastId++
    set((state) => ({ toasts: [...state.toasts, { id, kind, title, message, duration }] }))
    return id
  },
  dismissToast: (id) => set((state) => ({ toasts: state.toasts.filter((toast) => toast.id !== id) })),
  clearToasts: () => set({ toasts: [] }),
}))

/** Imperative helpers usable outside React components. */
export const toast = {
  success: (message: string, title?: string) => useUiStore.getState().pushToast({ kind: 'success', message, title }),
  error: (message: string, title?: string) =>
    useUiStore.getState().pushToast({ kind: 'error', message, title, duration: 7000 }),
  info: (message: string, title?: string) => useUiStore.getState().pushToast({ kind: 'info', message, title }),
  warning: (message: string, title?: string) => useUiStore.getState().pushToast({ kind: 'warning', message, title }),
}
