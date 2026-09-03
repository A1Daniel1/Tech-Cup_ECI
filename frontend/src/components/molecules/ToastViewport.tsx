import { useEffect } from 'react'
import { useUiStore, type Toast } from '@/store/ui.store'
import { Alert } from './Alert'

function ToastItem({ toast }: { toast: Toast }) {
  const dismiss = useUiStore((state) => state.dismissToast)

  useEffect(() => {
    if (toast.duration <= 0) return
    const timer = setTimeout(() => dismiss(toast.id), toast.duration)
    return () => clearTimeout(timer)
  }, [toast.id, toast.duration, dismiss])

  return (
    <Alert kind={toast.kind} title={toast.title} onClose={() => dismiss(toast.id)} className="shadow-lg">
      {toast.message}
    </Alert>
  )
}

/** Renders the toast queue from `ui.store`. Mount once near the app root. */
export function ToastViewport() {
  const toasts = useUiStore((state) => state.toasts)
  if (toasts.length === 0) return null
  return (
    <div
      aria-live="polite"
      className="pointer-events-none fixed inset-x-0 bottom-4 z-[60] flex flex-col items-center gap-2 px-4 sm:items-end"
    >
      {toasts.map((toast) => (
        <div key={toast.id} className="pointer-events-auto w-full max-w-sm">
          <ToastItem toast={toast} />
        </div>
      ))}
    </div>
  )
}
